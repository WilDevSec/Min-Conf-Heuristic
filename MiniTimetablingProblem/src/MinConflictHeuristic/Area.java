package MinConflictHeuristic;

import java.util.ArrayList;
import java.util.List;

public class Area {

	private List<Location> locations = new ArrayList<Location>();
	private List<Employee> employees = new ArrayList<Employee>();

	public Area(List<Location> locations, List<Employee> employees) {
		this.locations = locations;
		this.employees = employees;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public List<Employee> getEmployees() {
		return employees;
	}
	
}
