import java.util.ArrayList;
import java.util.List;

public class Timetable {

	private int employeesNeeded, daysInTable;
	private Employee[][] table;
	private List<Employee> employees = new ArrayList<>();
	
	
	public Timetable(int employeesNeeded, int daysInTable) {
		this.employeesNeeded = employeesNeeded;
		this.daysInTable = daysInTable;
		table = new Employee[employeesNeeded][daysInTable];
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
