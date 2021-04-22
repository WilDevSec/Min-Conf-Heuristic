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
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class CreateLocationController {

	@FXML private ChoiceBox<Integer> rank4In;
	@FXML private ChoiceBox<Integer> rank3In;
	@FXML private ChoiceBox<Integer> rank2In;
	@FXML private ChoiceBox<Integer> boatDrIn;
	@FXML private ChoiceBox<Integer> boatCrIn;
	@FXML private ChoiceBox<Integer> jetSkIn;
	
	
	
	public void initialise() {
		rank4In.getItems().addAll(0, 1, 2, 3, 4, 5, 6);
		rank3In.getItems().addAll(0, 1, 2, 3, 4, 5, 6);
		rank2In.getItems().addAll(0, 1, 2, 3, 4, 5, 6);
		boatDrIn.getItems().addAll(0, 1, 2, 3, 4, 5, 6);
		boatCrIn.getItems().addAll(0, 1, 2, 3, 4, 5, 6);
		jetSkIn.getItems().addAll(0, 1, 2, 3, 4, 5, 6);
	}
	
	@FXML
	public void locationsView(ActionEvent e) throws IOException {
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
		int id = Main.solvedAreasPublic.get(Main.areaBookmark).getLocations().size();
		int rank4 = rank4In.getValue();
		int rank3 = rank3In.getValue();
		int rank2 = rank2In.getValue();
		int boatDr = boatDrIn.getValue();
		int boatCr = boatCrIn.getValue();
		int jetSk = jetSkIn.getValue();
		
		Location loc = new Location(id, rank4, rank3, rank2, boatDr, boatCr, jetSk);
		return loc;
	}
	

}