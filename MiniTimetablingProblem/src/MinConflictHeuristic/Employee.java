package MinConflictHeuristic;

public class Employee {

	private String name;
	private int rank;
	private boolean boatDriver, boatCrewman, jetSki, fullTime;

	public Employee(String name, int rank, boolean boatDriver, boolean boatCrewman, boolean jetSki, boolean fullTime) {
		this.name = name;
		this.rank = rank;
		this.boatDriver = boatDriver;
		this.boatCrewman = boatCrewman;
		this.jetSki = jetSki;
		this.fullTime = fullTime;
	}
	
	public int getRank() {
		return rank;
	}
	
	public boolean getBoatDriver() {
		return boatDriver;
	}
	
	public boolean getBoatCrewman() {
		return boatCrewman;
	}
	
	public boolean getjetSki() {
		return jetSki;
	}
	
	public boolean getfullTime() {
		return fullTime;
	}
	
	public String getName() {
		return name;
	}
}
