package MinConflictHeuristic;
import java.util.ArrayList;
import java.util.List;

public class Location {

	// 7 days in a week and this is a weekly timetable
	private final int DAYS_IN_TABLE = 7;
	// Using 6 employees per day for every location - as explained in documentation.
	private final int EMPLOYEES_PER_DAY = 6;
	private Employee[][] table;
	private List<Employee> employees = new ArrayList<>();
	
	
	public Location(int daysInTable) {
		table = new Employee[EMPLOYEES_PER_DAY][DAYS_IN_TABLE];
	}
	
	public void editEmployeeInTable(Employee newEmployee, int employee, int day) {
		table[employee][day] = newEmployee;
	}
	
	public Employee[][] getTable(){
		return table;
	}
	
	public void addEmployeeToList(Employee e) {
		employees.add(e);
	}

	public List<Employee> getEmployees() {
		return employees;
	}
}
