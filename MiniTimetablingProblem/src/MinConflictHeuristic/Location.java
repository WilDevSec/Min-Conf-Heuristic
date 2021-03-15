package MinConflictHeuristic;

public class Location {

	// 7 days in a week and this is a weekly timetable
	private final int DAYS_IN_TABLE = 7;
	// Using 6 employees per day for every location - as explained in documentation.
	private final int EMPLOYEES_PER_DAY = 6;
	private Employee[][] timetable;
	private int locationID, rank4Req, rank3Req, rank2Req, boatDriversReq, crewmenReq, jetSkiUsersReq;
	
	public Location(int locationID, int rank4Req, int rank3Req, int rank2Req, int boatDriversReq, int crewmenReq, int jetSkiUsersReq) {
		this.locationID = locationID;
		this.rank4Req = rank4Req;
		this.rank3Req = rank3Req;
		this.rank2Req = rank2Req;
		this.boatDriversReq = boatDriversReq;
		this.crewmenReq = crewmenReq;
		this.jetSkiUsersReq = jetSkiUsersReq;
	}
	
	public int getLocationID() {
		return locationID;
	}
	public int getrank4Req() {
		return rank4Req;
	}
	
	public int getrank3Req() {
		return rank3Req;
	}
	
	public int getrank2Req() {
		return rank2Req;
	}
	
	public int getBoatDriversReq() {
		return boatDriversReq;
	}
	
	public int getCrewmenReq() {
		return crewmenReq;
	}
	
	public int getJetSkiUsersReq() {
		return jetSkiUsersReq;
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
