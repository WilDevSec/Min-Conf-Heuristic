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
		AreaSolver as = new AreaSolver(a);
		as.populateTimetablesRandomly();
		Location l = a.getLocations().get(0);
		for(Employee[] e: l.getTimetable()) {
			for (Employee ee : e) {
				System.out.println(ee.getName());
			}
		}
	}
	
}
