package MinConflictHeuristic;

import Dataset.CSVIO;
import java.util.ArrayList;

public class ReadData {

	private ArrayList<Area> areasInitialised;
	
	public ReadData() {

	}
	
	// Puts all data from csv files into area, location and employee classes and return all as list of areas
	public ArrayList<Area> getAllAreas() {
		ArrayList<Area> allAreas = new ArrayList<Area>();
		ArrayList<Location> allLocations = InitialiseLocations();
		ArrayList<Employee> allEmployees = InitialiseEmployees();
		int locationsAddedMarker = 0;
		int employeesAddedMarker = 0;
		for (int i = 0; i < 50; i++) {
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
		areasInitialised = allAreas;
		return allAreas;
	}
	
	// Returns areas after they have been initialised. For use after getAllAreas() has been run (optimising for soft constraint).
	public ArrayList<Area> getAreasInitialised() {
		return areasInitialised;
	}
	
	// Name, rank, Boat Driver, Boat Crewman, Jet Ski, Full time
	private ArrayList<Employee> InitialiseEmployees() {
		ArrayList<String> employeesString = CSVIO.readCSV("/home/will/Studies/Diss/Dataset/employeeList.csv");
		ArrayList<Employee> employees = new ArrayList<Employee>();
		int employeeCount = 1;
		for (String s : employeesString) {
			String[] employeeAsArray = s.split(",");
			String name = employeeAsArray[0];
			int rank = Integer.parseInt(employeeAsArray[1]);
			boolean boatDriver = Boolean.parseBoolean(employeeAsArray[2]);
			boolean boatCrewman = Boolean.parseBoolean(employeeAsArray[3]);
			boolean jetSkiUser = Boolean.parseBoolean(employeeAsArray[4]);
			boolean fullTime = Boolean.parseBoolean(employeeAsArray[5]);
			int areaFrom = employeeCount / 60;
			Employee e = new Employee(name, rank, boatDriver, boatCrewman, jetSkiUser, fullTime, areaFrom);
			employees.add(e);
			employeeCount++;
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
			int locationID = Integer.parseInt(locationsAsArray[0]);
			int rank4Req = Integer.parseInt(locationsAsArray[1]);
			int rank3Req = Integer.parseInt(locationsAsArray[2]);
			int rank2Req = Integer.parseInt(locationsAsArray[3]);
			int boatDriversReq = Integer.parseInt(locationsAsArray[4]);
			int crewmenReq = Integer.parseInt(locationsAsArray[5]);
			int jetSkiUsersReq = Integer.parseInt(locationsAsArray[6]);
			Location l = new Location(locationID, rank4Req, rank3Req, rank2Req, boatDriversReq, crewmenReq,
					jetSkiUsersReq);
			locations.add(l);
		}
		return locations;
	}

}
