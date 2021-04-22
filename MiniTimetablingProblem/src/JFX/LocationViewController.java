package JFX;

import java.io.IOException;
import java.util.ArrayList;

import MinConflictHeuristic.Area;
import MinConflictHeuristic.Employee;
import MinConflictHeuristic.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class LocationViewController {

	
	@FXML
	private TableView<Location> locations; 
	@FXML
	private TableColumn<Location, String> ID;
	@FXML
	private TableColumn<Location, Integer> rank4;
	@FXML
	private TableColumn<Location, Integer> rank3;
	@FXML
	private TableColumn<Location, Integer> rank2;
	@FXML
	private TableColumn<Location, Boolean> crewmen;
	@FXML
	private TableColumn<Location, Boolean> boatDrivs;
	@FXML
	private TableColumn<Location, Boolean> jetSki;
	
	
	public void initialise(Area area) {
		ID.setCellValueFactory(new PropertyValueFactory<Location, String>("locationID"));
		rank4.setCellValueFactory(new PropertyValueFactory<Location, Integer>("rank4Req"));
		rank3.setCellValueFactory(new PropertyValueFactory<Location, Integer>("rank3Req"));
		rank2.setCellValueFactory(new PropertyValueFactory<Location, Integer>("rank2Req"));
		boatDrivs.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("boatDriversReq"));
		crewmen.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("crewmenReq"));
		jetSki.setCellValueFactory(new PropertyValueFactory<Location, Boolean>("jetSkiUsersReq"));
		
		locations.setItems(getLocations(area.getLocations()));
	}

	private ObservableList<Location> getLocations(ArrayList<Location> locations1) {
		ObservableList<Location> locations2 = FXCollections.observableArrayList();
		for (Location l : locations1) {
			locations2.add(l);
		}
		return locations2;
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
	protected void createLocationView(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("CreateLocation.fxml"));
		Parent createLocViewParent = loader.load();
		Scene createLocViewScene = new Scene(createLocViewParent);
		
		CreateEmployeeController controller = loader.getController();
		controller.initialise();
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(createLocViewScene);
		window.show();
	}
	
}
