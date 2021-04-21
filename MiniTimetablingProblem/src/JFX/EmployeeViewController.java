package JFX;

import java.util.ArrayList;

import MinConflictHeuristic.Area;
import MinConflictHeuristic.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class EmployeeViewController {

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
		name.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
		rank.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("rank"));
		boatDriver.setCellValueFactory(new PropertyValueFactory<Employee, Boolean>("boatDriver"));
		boatCrewman.setCellValueFactory(new PropertyValueFactory<Employee, Boolean>("boatCrewman"));
		jetSki.setCellValueFactory(new PropertyValueFactory<Employee, Boolean>("jetSki"));
		fullTime.setCellValueFactory(new PropertyValueFactory<Employee, Boolean>("fullTime"));
		
		employees.setItems(getEmployees(area.getEmployees()));
	}
	
	public ObservableList<Employee> getEmployees(ArrayList<Employee> employees){
		ObservableList<Employee> employees1 = FXCollections.observableArrayList();
		for (Employee e : employees) {
			employees1.add(e);
		}
		return employees1;
	}

	
	
}















