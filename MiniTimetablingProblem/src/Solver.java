import java.util.*;

public class Solver {

	public Solver() {

	}

	
	public boolean employeesDoubleBooked(Timetable timetable) {
		Employee[][] table = timetable.getTable();
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

	private Employee getFreeEmployee(Timetable timetable, int day) {
		Employee ret = null;
		Employee[][] table = timetable.getTable();
		List<Employee> employees = timetable.getEmployees();
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

	public boolean employeesWorkingTooMuch(Timetable timetable) {
		Employee[][] table = timetable.getTable();
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
