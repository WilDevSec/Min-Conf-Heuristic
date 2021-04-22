package JFX;

import java.io.IOException;

import MinConflictHeuristic.Location;
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

public class CreateLocationController {

	@FXML private TextField ID;
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
		Main.solvedAreasPublic.get(Main.areaBookmark).addLocation(makeLocation());
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Locations.fxml"));
		Parent locViewParent = loader.load();
		Scene locViewScene = new Scene(locViewParent);

		LocationViewController controller = loader.getController();
		controller.initialise(Main.solvedAreasPublic.get(Main.areaBookmark));
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(locViewScene);
		window.show();
	}
	
	protected Location makeLocation() {
//		String name = nameIn.getText();
		int rank = rankIn.getValue();
		boolean boatDriver = boatDrivIn.isArmed();
		boolean boatCrew = boatCrewIn.isArmed();
		boolean jetSki = jetSkiIn.isArmed();
		boolean fulltime = fulltimeIn.isArmed();
//		Employee emp = new Employee(name, rank, boatDriver, boatCrew, jetSki, fulltime);
		return null;
	}
	

}