package MinConflictHeuristic;

import java.util.ArrayList;
import java.util.List;

public class Area {

	private ArrayList<Location> locations = new ArrayList<Location>();
	private ArrayList<Employee> employees = new ArrayList<Employee>();

	public Area(ArrayList<Location> locations, ArrayList<Employee> employees) {
		this.locations = locations;
		this.employees = employees;
	}

	
	public ArrayList<Location> getLocations() {
		return locations;
	}

	public ArrayList<Employee> getEmployees() {
		return employees;
	}
	
	public void setLocations(ArrayList<Location> locations) {
		this.locations = locations;
	}
	
	public void addEmployee(Employee e) {
		employees.add(e);
	}
	
}
