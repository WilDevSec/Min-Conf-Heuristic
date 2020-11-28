import java.util.UUID;

public class TimetableGenerator {

	public TimetableGenerator() {
		
	}
	
	public Timetable createTable(){
		Timetable timetable = new Timetable(5, 7);
		Employee[] employees = createEmployees();
		
		for (Employee e : employees) {
			timetable.addEmployeeToList(e);
		}
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 7; j++) {
				timetable.editEmployeeInTable(employees[(int) (Math.random() * employees.length)], i, j);
			}
		}
		return timetable;
	}
	
	private Employee[] createEmployees() {
		Employee[] employees = {new Employee("Sam"), new Employee("Tom"), new Employee("Dav"),
				new Employee("Pam"), new Employee("Sue"), new Employee("Rex"),
				new Employee("Joe"), new Employee("Xia"), new Employee("Lil")};
		/* Employee[] employees = new Employee[10];
		
		for (int i = 0; i < 10; i++) {
			employees[i] = new Employee(Integer.toString(i));
		}*/
		return employees;
	}
}
