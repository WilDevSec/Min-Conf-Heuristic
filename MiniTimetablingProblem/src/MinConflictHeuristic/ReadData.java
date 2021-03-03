package MinConflictHeuristic;

import Dataset.CSVIO;
import java.util.ArrayList;

public class ReadData {

	public ReadData() {

	}

	// Name, rank, Boat Driver, Boat Crewman, Jet Ski, Full time
	private ArrayList<Employee> InitialiseEmployees() {
		ArrayList<String> employeesString = CSVIO.readCSV("/home/will/Studies/Diss/Dataset/employeeList.csv");
		ArrayList<Employee> employees = new ArrayList<Employee>();
		for (String s : employeesString) {
			String[] employeeAsArray = s.split(",");
			String name = employeeAsArray[0];
			int rank = Integer.parseInt(employeeAsArray[1]);
			boolean boatDriver = Boolean.parseBoolean(employeeAsArray[2]);
			boolean boatCrewman = Boolean.parseBoolean(employeeAsArray[3]);
			boolean jetSkiUser = Boolean.parseBoolean(employeeAsArray[4]);
			boolean fullTime = Boolean.parseBoolean(employeeAsArray[5]);
			Employee e = new Employee(name, rank, boatDriver, boatCrewman, jetSkiUser, fullTime);
			employees.add(e);
		}
		return employees;
	}

	// Employee ID -- Required rank 4 -- Required rank 3 -- Required rank 2 --
	// Required Boat drivers -- Required Crew mates -- Required Jet Ski users
	private ArrayList<Location> InitialiseLocations() {
		ArrayList<String> locationsString = CSVIO.readCSV("/home/will/Studies/Diss/Dataset/locationList.csv");
		ArrayList<Location> locations = new ArrayList<Location>();
		for (String s : locationsString) {
			String[] locationsAsArray = s.split(",");
			int employeeID = Integer.parseInt(locationsAsArray[0]);
			int rank4Req = Integer.parseInt(locationsAsArray[1]);
			int rank3Req = Integer.parseInt(locationsAsArray[2]);
			int rank2Req = Integer.parseInt(locationsAsArray[3]);
			int boatDriversReq = Integer.parseInt(locationsAsArray[4]);
			int crewmanReq = Integer.parseInt(locationsAsArray[5]);
			int jetSkiUsersReq = Integer.parseInt(locationsAsArray[6]);
			Location l = new Location(employeeID, rank4Req, rank3Req, rank2Req, boatDriversReq, crewmanReq,
					jetSkiUsersReq);
			locations.add(l);
		}
		return locations;
	}

	public ArrayList<Area> getAllAreas() {
		ArrayList<Area> allAreas = new ArrayList<Area>();
		ArrayList<Location> allLocations = InitialiseLocations();
		ArrayList<Employee> allEmployees = InitialiseEmployees();
		int locationsAddedMarker = 0;
		int employeesAddedMarker = 0;
		for (int i = 0; i < 60; i++) {
			ArrayList<Location> locationsInArea = new ArrayList<Location>();
			for (int j = 0; j < 5; j++) {
				locationsInArea.add(allLocations.get(locationsAddedMarker++));
			}
			ArrayList<Employee> employeesInArea = new ArrayList<Employee>();
			for (int j = 0; j < 60; j++) {
				employeesInArea.add(allEmployees.get(employeesAddedMarker++));
			}
			allAreas.add(new Area(locationsInArea, employeesInArea));
		}
		return allAreas;
	}

}
