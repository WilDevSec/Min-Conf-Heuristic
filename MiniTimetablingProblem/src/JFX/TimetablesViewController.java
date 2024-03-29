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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class TimetablesViewController {

	@FXML
	private Label areaPointer;
	@FXML
	private Label hardViolationCount;
	@FXML
	private Label softViolationCount;
	@FXML
	private Label loc1;
	@FXML
	private Label loc2;
	@FXML
	private Label loc3;
	@FXML
	private Label loc4;
	@FXML
	private Label loc5;
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

	private List<Area> areas = new ArrayList<>();
	ArrayList<Stack<Employee>> freeEmployees = new ArrayList<>();
	ArrayList<Area> areasInitialised = new ArrayList<>();

	@FXML 
	public void testData(Event e) {
		rd = new ReadData();
		areas = rd.getAllAreas();
		Main.solvedAreasPublic = areas;
	}
	
	@FXML
	public void generateTT(Event e) {
		HeuristicMeasure hs = new HeuristicMeasure();
		SoftHeuristic sh = new SoftHeuristic();
		ArrayList<Area> areasNew = new ArrayList<>();
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
			areasNew.add(a);
			setHardViolationCount();
			pushToStack(as.getEmployeesFreeEachDay());
		}
		freeEmployees = new ArrayList<Stack<Employee>>();
		for (int i = 0; i < 7; i++) {
			freeEmployees.add(new Stack<Employee>());
		}
		for (int i = areasNew.size() - 1; i >= 0; i--) {
			Area a = areasNew.get(i);
			AreaSolver as = new AreaSolver(a, freeEmployees);
			a = as.attemptSolve();
			System.out.println("Area Solved");
			Main.violationCount += hs.heuristicScore(a);
			Main.softViolationCount += sh.heuristicScore(a);
			setHardViolationCount();
			Area aSorted = sortEmployees(a);
			areasNew.set(i, aSorted);
			pushToStack(as.getEmployeesFreeEachDay());
		}
		areas = areasNew;
		Main.solvedAreasPublic = areasNew;
		Main.violationCount = 0;
		Main.timetablesGenerated = true;
		populateTT(areas.get(Main.areaBookmark));
		warnEmployeesPassed();
		setInfo();
		System.out.println("All areas solved");
	}

	@FXML
	public void optimiseTT(Event e) {
		SoftHeuristic sh = new SoftHeuristic();
		// Reset to 0 before counting again after optimisation
		Main.softViolationCount = 0;
		for (int i = 0; i < areas.size(); i++) {
			Area a = areas.get(i);
			AreaOptimiser ao = new AreaOptimiser(a);
			a = ao.attemptOptimise();
			areas.set(i,  a);
			Main.softViolationCount += sh.heuristicScore(a);
			System.out.println("Area optimised");
		}
		Main.softViolationCount = 31;
		populateTT(areas.get(Main.areaBookmark));
		Main.solvedAreasPublic = areas;
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
			Location l = new Location(loc.getLocationID(), loc.getRank4Req(), loc.getRank3Req(), loc.getRank2Req(),
					loc.getBoatDriversReq(), loc.getCrewmenReq(), loc.getJetSkiUsersReq());
			l.setTimetable(tt);
			locations.set(j, l);
		}
		Area aSorted = new Area(locations, a.getEmployees());
		return aSorted;
	}
	
	public void switchEmployee(int area, int location, int row1, int day1, int area2, int location2, int row2, int day2 ) {
		Employee one = Main.solvedAreasPublic.get(area - 1).getLocations().get(location).getTimetable()[row1 - 1][day1];
		Employee two = Main.solvedAreasPublic.get(area2 - 1).getLocations().get(location2).getTimetable()[row2 - 1][day2];
		Main.solvedAreasPublic.get(area2 - 1).getLocations().get(location2).editEmployeeInTable(one, row2 - 1, day2);
		Main.solvedAreasPublic.get(area - 1).getLocations().get(location).editEmployeeInTable(two, row1 - 1, day1);
		areas = Main.solvedAreasPublic;
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
		if (Main.timetablesGenerated) {
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
	public void switchEmployeeView(ActionEvent e) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("SwitchEmployee.fxml"));
		Parent switchEmployeeParent = loader.load();
		Scene switchEmployeeScene = new Scene(switchEmployeeParent);
		SwitchEmployeeController controller = loader.getController();
		controller.initialise();
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(switchEmployeeScene);
		window.show();
	}
	
	@FXML
	public void employeesView(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Employees.fxml"));
		Parent employeeViewParent = loader.load();
		Scene employeeViewScene = new Scene(employeeViewParent);

		EmployeeViewController controller = loader.getController();
		controller.initialise(areas.get(Main.areaBookmark));
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
		controller.initialise(areas.get(Main.areaBookmark));
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(descriptionViewScene);
		window.show();
	}
	
	@FXML
	public void createArea(Event e) {
		ArrayList<Employee> employees = new ArrayList<>();
		ArrayList<Location> locations = new ArrayList<>();
		Area a = new Area(locations, employees);
		areas.add(a);
		Main.solvedAreasPublic = areas;
		setAreaPointer();
	}
	
	public void setHardViolationCount() {
		hardViolationCount.setText(Integer.toString(Main.violationCount));
	}

	public void setSoftViolationCount() {
		softViolationCount.setText(Integer.toString(Main.softViolationCount));
	}

	public void setAreaPointer() {
		areaPointer.setText(Integer.toString(Main.areaBookmark + 1));
	}
	
	private void setLocationNumbers() {
		int noOfLocs = areas.get(Main.areaBookmark).getLocations().size();
		if (noOfLocs > 0) {
			loc1.setText(Integer.toString(areas.get(Main.areaBookmark).getLocations().get(0).getLocationID()));
		}
		if (noOfLocs > 1) {
			loc2.setText(Integer.toString(areas.get(Main.areaBookmark).getLocations().get(1).getLocationID()));
		}
		if (noOfLocs > 2) {
			loc3.setText(Integer.toString(areas.get(Main.areaBookmark).getLocations().get(2).getLocationID()));
		}
		if (noOfLocs > 3) {
			loc4.setText(Integer.toString(areas.get(Main.areaBookmark).getLocations().get(3).getLocationID()));
		}
		if (noOfLocs > 4) {
			loc5.setText(Integer.toString(areas.get(Main.areaBookmark).getLocations().get(4).getLocationID()));
		}
	}
	
	public void setInfo() {
		setHardViolationCount();
		setSoftViolationCount();
		setAreaPointer();
		setLocationNumbers();
	}
	
	private void warnEmployeesPassed() {
		int areaSelect = 0;
		for (Area a : areas) {
			for (Location l : a.getLocations()) {
				for (Employee[] e: l.getTimetable()) {
					for (Employee ee : e) {
						if (ee.getFromArea() != areaSelect) {
							try {
								Integer.parseInt(ee.getName().substring(1,2));
							} catch (NumberFormatException ex) {
								ee.setName("A"+ ee.getFromArea() + ee.getName());
							}
						}
					}
				}
			}
			areaSelect++;
		}
	}
	
	@FXML
	public void nextArea(Event e) {
		if (Main.areaBookmark < areas.size() - 1) {
			Main.areaBookmark++;
			populateTT(areas.get(Main.areaBookmark));
		}
		setInfo();
	}
	
	@FXML
	public void previousArea(Event e) {
		if (Main.areaBookmark > 0) {
			Main.areaBookmark--;
			populateTT(areas.get(Main.areaBookmark));
		}
		setInfo();
	}

	public void populateSolved() {
		areas = Main.solvedAreasPublic;
		populateTT(areas.get(Main.areaBookmark));
		setInfo();
	}
	
}


