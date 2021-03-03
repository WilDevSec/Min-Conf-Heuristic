package MinConflictHeuristic;
import java.util.*;

public class AreaSolver {

	ArrayList<Employee> employees = new ArrayList<Employee>();
	ArrayList<Location> locations = new ArrayList<Location>();
	public AreaSolver(Area currentArea) {
		employees = currentArea.getEmployees();
		locations = currentArea.getLocations();
	}
	
	public int[] getConstraintLocation(Location location) {
		return new int[] {2, 3};
	}
	
	public boolean employeesDoubleBooked(Location timetable) {
		Employee[][] table = timetable.getTimetable();
		for (int i = 0; i < table[0].length; i++) {
			Map<Employee, Integer> employeeCount = new HashMap<>();
			for (int j = 0; j < table.length; j++) {
				if (employeeCount.containsKey(table[j][i])) {
					timetable.editEmployeeInTable(getFreeEmployee(timetable, i), j, i);
					return true;
				} else {
					employeeCount.put(table[j][i], 1);
				}
			}
		}
		return false;
	}

	private Employee getFreeEmployee(Location timetable, int day) {
		Employee ret = null;
		Employee[][] table = timetable.getTimetable();
		Collections.shuffle(employees);
		for (Employee e : employees) {
			boolean freeOnDay = true;
			for (int i = 0; i < table.length; i++) {
				if (e.equals(table[i][day])) {
					freeOnDay = false;
				}
			}
			if (freeOnDay)
				return e;
		}
		return ret;
	}

	public boolean employeesWorkingTooMuch(Location timetable) {
		Employee[][] table = timetable.getTimetable();
		Map<Employee, Integer> employeeCount = new HashMap<>();
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				Employee e = table[i][j];
				if (employeeCount.containsKey(e) && employeeCount.get(e) >= 5) {
					timetable.editEmployeeInTable(getFreeEmployee(timetable, j), i, j);
					System.out.println(e.getName());
					return true;
				} else if (employeeCount.containsKey(e)) {
					employeeCount.put(e, employeeCount.get(e) + 1);
				} else {
					employeeCount.put(e, 1);
				}
			}
		}
		
		return false;
	}

}
