package JFX;

import MinConflictHeuristic.ReadData;
import MinConflictHeuristic.SoftHeuristic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import MinConflictHeuristic.Area;
import MinConflictHeuristic.AreaOptimiser;
import MinConflictHeuristic.AreaSolver;
import MinConflictHeuristic.Employee;
import MinConflictHeuristic.HeuristicMeasure;
import MinConflictHeuristic.Location;
import MinConflictHeuristic.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ViewController {

	@FXML
	private Label areaPointer;
	@FXML
	private Label hardViolationCount;
	@FXML
	private Label softViolationCount;
	@FXML
	private TableView<Row> location1;
	@FXML
	private TableView<Row> location2;
	@FXML
	private TableView<Row> location3;
	@FXML
	private TableView<Row> location4;
	@FXML
	private TableView<Row> location5;
	@FXML
	private TableColumn<Row, String> mon1;
	@FXML
	private TableColumn<Row, String> tue1;
	@FXML
	private TableColumn<Row, String> wed1;
	@FXML
	private TableColumn<Row, String> thu1;
	@FXML
	private TableColumn<Row, String> fri1;
	@FXML
	private TableColumn<Row, String> sat1;
	@FXML
	private TableColumn<Row, String> sun1;
	@FXML
	private TableColumn<Row, String> mon2;
	@FXML
	private TableColumn<Row, String> tue2;
	@FXML
	private TableColumn<Row, String> wed2;
	@FXML
	private TableColumn<Row, String> thu2;
	@FXML
	private TableColumn<Row, String> fri2;
	@FXML
	private TableColumn<Row, String> sat2;
	@FXML
	private TableColumn<Row, String> sun2;
	@FXML
	private TableColumn<Row, String> mon3;
	@FXML
	private TableColumn<Row, String> tue3;
	@FXML
	private TableColumn<Row, String> wed3;
	@FXML
	private TableColumn<Row, String> thu3;
	@FXML
	private TableColumn<Row, String> fri3;
	@FXML
	private TableColumn<Row, String> sat3;
	@FXML
	private TableColumn<Row, String> sun3;
	@FXML
	private TableColumn<Row, String> mon4;
	@FXML
	private TableColumn<Row, String> tue4;
	@FXML
	private TableColumn<Row, String> wed4;
	@FXML
	private TableColumn<Row, String> thu4;
	@FXML
	private TableColumn<Row, String> fri4;
	@FXML
	private TableColumn<Row, String> sat4;
	@FXML
	private TableColumn<Row, String> sun4;
	@FXML
	private TableColumn<Row, String> mon5;
	@FXML
	private TableColumn<Row, String> tue5;
	@FXML
	private TableColumn<Row, String> wed5;
	@FXML
	private TableColumn<Row, String> thu5;
	@FXML
	private TableColumn<Row, String> fri5;
	@FXML
	private TableColumn<Row, String> sat5;
	@FXML
	private TableColumn<Row, String> sun5;

	ReadData rd;

	private List<Area> solvedAreas = new ArrayList<>();
	ArrayList<Stack<Employee>> freeEmployees = new ArrayList<>();

	@FXML
	public void generateTT(Event e) {
		rd = new ReadData();
		HeuristicMeasure hs = new HeuristicMeasure();
		SoftHeuristic sh = new SoftHeuristic();
		ArrayList<Area> areas = rd.getAllAreas();
		for (int i = 0; i < 7; i++) {
			freeEmployees.add(new Stack<Employee>());
		}
		for (int i = 0; i < areas.size(); i++) {
			Area a = areas.get(i);
			AreaSolver as = new AreaSolver(a, freeEmployees);
			a = as.populateTimetablesRandomly();
			System.out.println("Area Randomly Populated");
			a = as.attemptSolve();
			System.out.println("Area Solved");
			solvedAreas.add(a);
			setHardViolationCount();
			setAreaPointer();
			populateTT(a);
			pushToStack(as.getEmployeesFreeEachDay());
		}
		freeEmployees = new ArrayList<Stack<Employee>>();
		for (int i = 0; i < 7; i++) {
			freeEmployees.add(new Stack<Employee>());
		}
		for (int i = solvedAreas.size() - 1; i >= 0; i--) {
			Area a = solvedAreas.get(i);
			AreaSolver as = new AreaSolver(a, freeEmployees);
			a = as.attemptSolve();
			System.out.println("Area Solved");
			Main.violationCount += hs.heuristicScore(a);
			Main.softViolationCount += sh.heuristicScore(a);
			setHardViolationCount();
			setAreaPointer();
			Area aSorted = sortEmployees(a);
			solvedAreas.set(i, aSorted);
			populateTT(aSorted);
			pushToStack(as.getEmployeesFreeEachDay());
		}
		Main.solvedAreasPublic = solvedAreas;
		Main.violationCount = 0;
		////
		setHardViolationCount();
		setSoftViolationCount();
		System.out.println("All areas solved");
	}

	@FXML
	public void optimiseTT(Event e) {
		SoftHeuristic sh = new SoftHeuristic();
		// Reset to 0 before counting again after optimisation
		Main.softViolationCount = 0;
		for (int i = 0; i < solvedAreas.size(); i++) {
			Area a = solvedAreas.get(i);
			AreaOptimiser ao = new AreaOptimiser(a);
			a = ao.attemptOptimise();
			populateTT(a);
			solvedAreas.set(i,  a);
			Main.softViolationCount += sh.heuristicScore(a);
			System.out.println("Area optimised");
		}
		Main.solvedAreasPublic = solvedAreas;
		setSoftViolationCount();
	}

	private Area sortEmployees(Area a) {
		ArrayList<Location> locations = a.getLocations();
		for (int j = 0; j < locations.size(); j++) {
			Location loc = locations.get(j);
			Employee[][] tt = loc.getTimetable();
			for (int k = 0; k < tt[0].length; k++) {
				for (int l = 0; l < tt.length - 1; l++) {
					for (int m = l; m < tt.length; m++) {
						//Char at 4 because all employees have 3 spaces infront of their name for aesthetics in timetable.
						if ((tt[m][k].getName().charAt(4) < (tt[l][k].getName().charAt(4)))) {
							Employee temp = tt[l][k];
							tt[l][k] = tt[m][k];
							tt[m][k] = temp;
						}
					}
				}
			}
			Location l = new Location(loc.getLocationID(), loc.getrank4Req(), loc.getrank3Req(), loc.getrank2Req(),
					loc.getBoatDriversReq(), loc.getCrewmenReq(), loc.getJetSkiUsersReq());
			l.setTimetable(tt);
			locations.set(j, l);
		}
		Area aSorted = new Area(locations, a.getEmployees());
		return aSorted;
	}

	private void pushToStack(ArrayList<Employee[]> employeesOneArea) {
		for (int i = 0; i < 7; i++) {
			Employee[] oneDay = employeesOneArea.get(i);
			Stack<Employee> dayStack = freeEmployees.get(i);
			for (Employee e : oneDay) {
				dayStack.push(e);
			}
			freeEmployees.set(i, dayStack);
		}
	}

	public void populateTT(Area area) {
		mon1.setCellValueFactory(new PropertyValueFactory<Row, String>("mon"));
		tue1.setCellValueFactory(new PropertyValueFactory<Row, String>("tue"));
		wed1.setCellValueFactory(new PropertyValueFactory<Row, String>("wed"));
		thu1.setCellValueFactory(new PropertyValueFactory<Row, String>("thu"));
		fri1.setCellValueFactory(new PropertyValueFactory<Row, String>("fri"));
		sat1.setCellValueFactory(new PropertyValueFactory<Row, String>("sat"));
		sun1.setCellValueFactory(new PropertyValueFactory<Row, String>("sun"));

		mon2.setCellValueFactory(new PropertyValueFactory<Row, String>("mon"));
		tue2.setCellValueFactory(new PropertyValueFactory<Row, String>("tue"));
		wed2.setCellValueFactory(new PropertyValueFactory<Row, String>("wed"));
		thu2.setCellValueFactory(new PropertyValueFactory<Row, String>("thu"));
		fri2.setCellValueFactory(new PropertyValueFactory<Row, String>("fri"));
		sat2.setCellValueFactory(new PropertyValueFactory<Row, String>("sat"));
		sun2.setCellValueFactory(new PropertyValueFactory<Row, String>("sun"));

		mon3.setCellValueFactory(new PropertyValueFactory<Row, String>("mon"));
		tue3.setCellValueFactory(new PropertyValueFactory<Row, String>("tue"));
		wed3.setCellValueFactory(new PropertyValueFactory<Row, String>("wed"));
		thu3.setCellValueFactory(new PropertyValueFactory<Row, String>("thu"));
		fri3.setCellValueFactory(new PropertyValueFactory<Row, String>("fri"));
		sat3.setCellValueFactory(new PropertyValueFactory<Row, String>("sat"));
		sun3.setCellValueFactory(new PropertyValueFactory<Row, String>("sun"));

		mon4.setCellValueFactory(new PropertyValueFactory<Row, String>("mon"));
		tue4.setCellValueFactory(new PropertyValueFactory<Row, String>("tue"));
		wed4.setCellValueFactory(new PropertyValueFactory<Row, String>("wed"));
		thu4.setCellValueFactory(new PropertyValueFactory<Row, String>("thu"));
		fri4.setCellValueFactory(new PropertyValueFactory<Row, String>("fri"));
		sat4.setCellValueFactory(new PropertyValueFactory<Row, String>("sat"));
		sun4.setCellValueFactory(new PropertyValueFactory<Row, String>("sun"));

		mon5.setCellValueFactory(new PropertyValueFactory<Row, String>("mon"));
		tue5.setCellValueFactory(new PropertyValueFactory<Row, String>("tue"));
		wed5.setCellValueFactory(new PropertyValueFactory<Row, String>("wed"));
		thu5.setCellValueFactory(new PropertyValueFactory<Row, String>("thu"));
		fri5.setCellValueFactory(new PropertyValueFactory<Row, String>("fri"));
		sat5.setCellValueFactory(new PropertyValueFactory<Row, String>("sat"));
		sun5.setCellValueFactory(new PropertyValueFactory<Row, String>("sun"));

		if (area.getLocations().size() > 4) {
			Location location01 = area.getLocations().get(0);
			Employee[][] timetable1 = location01.getTimetable();
			Location location03 = area.getLocations().get(2);
			Employee[][] timetable3 = location03.getTimetable();
			Location location04 = area.getLocations().get(3);
			Employee[][] timetable4 = location04.getTimetable();
			Location location05 = area.getLocations().get(4);
			Employee[][] timetable5 = location05.getTimetable();
			Location location02 = area.getLocations().get(1);
			Employee[][] timetable2 = location02.getTimetable();
			location1.setItems(getRows(timetable1));
			location2.setItems(getRows(timetable2));
			location3.setItems(getRows(timetable3));
			location4.setItems(getRows(timetable4));
			location5.setItems(getRows(timetable5));
		}
	}

	public ObservableList<Row> getRows(Employee[][] timetable) {
		ObservableList<Row> rows = FXCollections.observableArrayList();
		for (int i = 0; i < timetable.length; i++) {
			rows.add(new Row(timetable[i][0].getName(), timetable[i][1].getName(), timetable[i][2].getName(),
					timetable[i][3].getName(), timetable[i][4].getName(), timetable[i][5].getName(),
					timetable[i][6].getName()));
		}
		return rows;
	}

	@FXML
	public void employeesView(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Employees.fxml"));
		Parent employeeViewParent = loader.load();
		Scene employeeViewScene = new Scene(employeeViewParent);

		EmployeeViewController controller = loader.getController();
		controller.initialise(solvedAreas.get(Main.areaBookmark));
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(employeeViewScene);
		window.show();
	}

	@FXML
	public void locationsView(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Locations.fxml"));
		Parent descriptionViewParent = loader.load();
		Scene descriptionViewScene = new Scene(descriptionViewParent);
		
		LocationViewController controller = loader.getController();
		controller.initialise(solvedAreas.get(Main.areaBookmark));
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(descriptionViewScene);
		window.show();
	}
	
	@FXML
	public void createArea(Event e) {
		ArrayList<Employee> employees = new ArrayList<>();
		ArrayList<Location> locations = new ArrayList<>();
		Area a = new Area(locations, employees);
		solvedAreas.add(a);
		setAreaPointer();
	}

	public void setHardViolationCount() {
		hardViolationCount.setText(Integer.toString(Main.violationCount));
	}

	public void setSoftViolationCount() {
		softViolationCount.setText(Integer.toString(Main.softViolationCount));
	}

	public void setAreaPointer() {
		areaPointer.setText(Integer.toString(Main.areaBookmark));
	}
	
	@FXML
	public void nextArea(Event e) {
		if (Main.areaBookmark < solvedAreas.size() - 1) {
			Main.areaBookmark++;
			populateTT(solvedAreas.get(Main.areaBookmark));
		}
		setAreaPointer();
	}
	
	@FXML
	public void previousArea(Event e) {
		if (Main.areaBookmark > 0) {
			Main.areaBookmark--;
			populateTT(solvedAreas.get(Main.areaBookmark));
		}
		setAreaPointer();
	}

	public void populateSolved() {
		solvedAreas = Main.solvedAreasPublic;
		populateTT(solvedAreas.get(Main.areaBookmark));
		setHardViolationCount();
		setSoftViolationCount();
	}
	
}


