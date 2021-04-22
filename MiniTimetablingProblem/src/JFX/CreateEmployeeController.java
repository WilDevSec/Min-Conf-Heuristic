package JFX;

import java.io.IOException;

import MinConflictHeuristic.Employee;
import MinConflictHeuristic.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateEmployeeController {

	@FXML private TextField nameIn;
	@FXML private ChoiceBox<Integer> rankIn;
	@FXML private CheckBox boatDrivIn;
	@FXML private CheckBox boatCrewIn;
	@FXML private CheckBox jetSkiIn;
	@FXML private CheckBox fulltimeIn;
	
	
	public void initialise() {
		rankIn.getItems().addAll(1, 2, 3, 4);
	}
	
	@FXML
	public void employeesView(ActionEvent e) throws IOException {
		Main.solvedAreasPublic.get(Main.areaBookmark).addEmployee(makeEmployee());
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Employees.fxml"));
		Parent emlpoyeeViewParent = loader.load();
		Scene employeeViewScene = new Scene(emlpoyeeViewParent);

		EmployeeViewController controller = loader.getController();
		controller.initialise(Main.solvedAreasPublic.get(Main.areaBookmark));
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(employeeViewScene);
		window.show();
	}
	
	protected Employee makeEmployee() {
		String name = nameIn.getText();
		int rank = rankIn.getValue();
		boolean boatDriver = boatDrivIn.isSelected();
		boolean boatCrew = boatCrewIn.isSelected();
		boolean jetSki = jetSkiIn.isSelected();
		boolean fulltime = fulltimeIn.isSelected();
		Employee emp = new Employee(name, rank, boatDriver, boatCrew, jetSki, fulltime);
		return emp;
	}
	

}
