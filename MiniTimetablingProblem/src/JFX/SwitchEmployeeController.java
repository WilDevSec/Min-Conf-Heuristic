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
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class SwitchEmployeeController {
	@FXML
	private ChoiceBox<Integer> area1;
	@FXML
	private ChoiceBox<Integer> loc1;
	@FXML
	private ChoiceBox<Integer> row1;
	@FXML
	private ChoiceBox<String> day1;

	@FXML
	private ChoiceBox<Integer> area2;
	@FXML
	private ChoiceBox<Integer> loc2;
	@FXML
	private ChoiceBox<Integer> row2;
	@FXML
	private ChoiceBox<String> day2;

	public void initialise() {
		for (int i = 1; i <= Main.solvedAreasPublic.size(); i++) {
			area1.getItems().add(i);
			area2.getItems().add(i);
		}
		for (int i = 0; i < 5; i++) {
			loc1.getItems().add(i);
			loc2.getItems().add(i);
		}
		for (int i = 1; i < 7; i++) {
			row1.getItems().add(i);
			row2.getItems().add(i);
		}
		day1.getItems().addAll("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
		day2.getItems().addAll("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
	}

	@FXML
	public void mainView(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("TimetablesView.fxml"));
		Parent timetableViewParent = loader.load();
		Scene timetableViewScene = new Scene(timetableViewParent);

		TimetablesViewController controller = loader.getController();
		controller.switchEmployee(area1.getValue(), loc1.getValue(), row1.getValue(), getDayAsInt(day1.getValue()),
				area2.getValue(), loc2.getValue(), row2.getValue(), getDayAsInt(day2.getValue()));
		controller.populateSolved();
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(timetableViewScene);
		window.show();
	}
	
	private int getDayAsInt(String s) {
		int x = 0;
		switch (s) {
		case "Mon":
			x = 0;
			break;
		case "Tue":
			x = 1;
			break;
		case "Wed":
			x = 2;
			break;
		case "Thu":
			x = 3;
			break;
		case "Fri":
			x = 4;
			break;
		case "Sat":
			x = 5;
			break;
		case "Sun":
			x = 6;
			break;
		}
		return x;
	}

}
