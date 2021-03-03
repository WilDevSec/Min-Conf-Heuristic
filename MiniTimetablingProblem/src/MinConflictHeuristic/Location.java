package MinConflictHeuristic;
import java.util.ArrayList;
import java.util.List;

public class Location {

	// 7 days in a week and this is a weekly timetable
	private final int DAYS_IN_TABLE = 7;
	// Using 6 employees per day for every location - as explained in documentation.
	private final int EMPLOYEES_PER_DAY = 6;
	private Employee[][] timetable;
	
	
	public Location(int employeeID, int rank4Req, int rank3Req, int rank2Req, int boatDriversReq, int crewmanReq, int jetSkiUsersReq) {
		timetable = new Employee[EMPLOYEES_PER_DAY][DAYS_IN_TABLE];
	}
	
	public void editEmployeeInTable(Employee newEmployee, int employee, int day) {
		timetable[employee][day] = newEmployee;
	}
	
	public void setTimetable(Employee[][] ttable) {
		timetable = ttable;
	}
	
	public Employee[][] getTimetable(){
		return timetable;
	}
}
