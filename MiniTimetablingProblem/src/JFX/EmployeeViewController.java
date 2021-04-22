package JFX;

import java.io.IOException;
import java.util.ArrayList;

import MinConflictHeuristic.Area;
import MinConflictHeuristic.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class EmployeeViewController {

	Area area;
	@FXML
	private TableView<Employee> employees; 
	@FXML
	private TableColumn<Employee, String> name;
	@FXML
	private TableColumn<Employee, Integer> rank;
	@FXML
	private TableColumn<Employee, Boolean> boatDriver;
	@FXML
	private TableColumn<Employee, Boolean> boatCrewman;
	@FXML
	private TableColumn<Employee, Boolean> jetSki;
	@FXML
	private TableColumn<Employee, Boolean> fullTime;
	
	public void initialise(Area area) {
		this.area = area;
		name.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
		rank.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("rank"));
		boatDriver.setCellValueFactory(new PropertyValueFactory<Employee, Boolean>("boatDriver"));
		boatCrewman.setCellValueFactory(new PropertyValueFactory<Employee, Boolean>("boatCrewman"));
		jetSki.setCellValueFactory(new PropertyValueFactory<Employee, Boolean>("jetSki"));
		fullTime.setCellValueFactory(new PropertyValueFactory<Employee, Boolean>("fullTime"));
		
		employees.setItems(getEmployees(area.getEmployees()));
	}
	
	public ObservableList<Employee> getEmployees(ArrayList<Employee> employees){
		sortEmployees(employees);
		ObservableList<Employee> employees1 = FXCollections.observableArrayList();
		for (Employee e : employees) {
			employees1.add(e);
		}
		
		return employees1;
	}

	private void sortEmployees(ArrayList<Employee> employees2) {
		for (int i = 0; i < employees2.size() -1; i++) {
			for (int j = i; j < employees2.size();  j++) {
				if (employees2.get(i).getName().charAt(4) > employees2.get(j).getName().charAt(4)) {
					Employee temp = employees2.get(i);
					employees2.set(i, employees2.get(j));
					employees2.set(j, temp);
				}
			}
		}
	}

	@FXML
	public void mainView(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("TimetablesView.fxml"));
		Parent mainViewParent = loader.load();
		Scene mainViewScene = new Scene(mainViewParent);

		ViewController controller = loader.getController();
		controller.populateSolved();
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(mainViewScene);
		window.show();
	}
	
	@FXML
	protected void createEmployeeView(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("CreateEmployee.fxml"));
		Parent createEmpViewParent = loader.load();
		Scene createEmpViewScene = new Scene(createEmpViewParent);
		
		CreateEmployeeController controller = loader.getController();
		controller.initialise();
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(createEmpViewScene);
		window.show();
	}
	
}















