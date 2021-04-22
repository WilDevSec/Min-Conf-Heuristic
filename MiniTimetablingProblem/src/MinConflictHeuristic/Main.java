package MinConflictHeuristic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Dataset.LocationsGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

	public static int violationCount = 0;
	public static int softViolationCount = 0;
	public static List<Area> solvedAreasPublic;
	public static int areaBookmark = 0;
	
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/JFX/TimetablesView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}

//TimetableGenerator tg = new TimetableGenerator();
//Timetable table = tg.createTable();
//Employee[][] showEmployees = table.getTable();
//
//                                
//System.out.println("Initial permutation: ");
//System.out.println("Mon     Tue     Wed     Thu     Fri     Sat     Sun");
//System.out.println("--      --      --      --      --      --      -- ");
//for (int i = 0; i < showEmployees.length; i++) {
//	for (int j = 0; j < showEmployees[i].length; j++) {
//		System.out.print(showEmployees[i][j].getName() + "     ");
//	}
//	System.out.println();
//}
//System.out.println();
//
//Solver s = new Solver();
//while (s.employeesDoubleBooked(table)) {
//	System.out.println("Mon     Tue     Wed     Thu     Fri     Sat     Sun");
//	System.out.println("--      --      --      --      --      --      -- ");
//	for (int i = 0; i < showEmployees.length; i++) {
//		for (int j = 0; j < showEmployees[i].length; j++) {
//			System.out.print(showEmployees[i][j].getName() + "     ");
//		}
//		System.out.println();
//	}
//	System.out.println();
//}
//
//System.out.println("Changed double booked employees, now checking for people working more than 5 days");
//while (s.employeesWorkingTooMuch(table)) {
//	System.out.println("Mon     Tue     Wed     Thu     Fri     Sat     Sun");
//	System.out.println("--      --      --      --      --      --      -- ");
//	for (int i = 0; i < showEmployees.length; i++) {
//		for (int j = 0; j < showEmployees[i].length; j++) {
//			System.out.print(showEmployees[i][j].getName() + "     ");
//		}
//		System.out.println();
//	}
//	System.out.println();
//}
//System.out.println("Sorted: ");
//sortEmployees(showEmployees);
//System.out.println("Mon     Tue     Wed     Thu     Fri     Sat     Sun");
//System.out.println("--      --      --      --      --      --      -- ");
//for (int i = 0; i < showEmployees.length; i++) {
//	for (int j = 0; j < showEmployees[i].length; j++) {
//		System.out.print(showEmployees[i][j].getName() + "     ");
//	}
//	System.out.println();
//}
//System.out.println();
//System.out.println("Done");