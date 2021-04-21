package AlgorithmComparison;
import MinConflictHeuristic.Area;
import MinConflictHeuristic.AreaSolver;
import MinConflictHeuristic.Employee;
import MinConflictHeuristic.Location;
import MinConflictHeuristic.ReadData;


public class Comparator {

	public static void main(String[] args) {
		ReadData rd = new ReadData();
		Area a = rd.getAllAreas().get(0);
//		AreaSolver as = new AreaSolver(a);
//		as.populateTimetablesRandomly();
		
		MCHillClimb mchc = new MCHillClimb(a);
		long startTime  = System.nanoTime();
		Area mchcSolved = mchc.solve();
		long timeElapsed = System.nanoTime() - startTime;
		System.out.println(" Min conflicts heuristic runtime:" + timeElapsed);
		
		
	}
	
}
