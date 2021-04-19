package JFX;
import MinConflictHeuristic.ReadData;

import java.util.ArrayList;
import java.util.List;

import MinConflictHeuristic.Area;
import MinConflictHeuristic.AreaOptimiser;
import MinConflictHeuristic.AreaSolver;
import MinConflictHeuristic.HeuristicMeasure;
import MinConflictHeuristic.Main;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ViewController {
	ReadData rd;
	private int areaBookmark;
	@FXML
	public void generateTT(Event e) {
		rd = new ReadData();
		List<Area> solvedAreas = new ArrayList<>(); 
		HeuristicMeasure hs = new HeuristicMeasure();
		for (Area a : rd.getAllAreas()) {
			AreaSolver as = new AreaSolver(a);
			as.populateTimetablesRandomly();
			a = as.attemptSolve();
			System.out.println("Area Solved");
			solvedAreas.add(a);
			Main.violationCount += hs.heuristicScore(a);
		}
		
	}
	
	@FXML
	public void optimiseTT(Event e) {
		List<Area> optimisedAreaa = new ArrayList<>();
		for (Area a : rd.getAreasInitialised()) {
			AreaOptimiser ao = new AreaOptimiser(a);
			ao.attemptOptimise();
		}
	}
	
	@FXML
	public void nextArea(Event e) {
		
	}
	
	@FXML
	public void previousArea(Event e) {
		
	}
}
