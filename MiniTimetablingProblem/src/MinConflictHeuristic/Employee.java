package MinConflictHeuristic;

import javafx.beans.property.SimpleBooleanProperty;

public class Employee {

	private String name;
	private int rank, fromArea;
	private boolean boatDriver, boatCrewman;
	private boolean jetSki, fullTime;

	public Employee(String name, int rank, boolean boatDriver, boolean boatCrewman, boolean jetSki, boolean fullTime, int fromArea) {
		this.name = name;
		this.rank = rank;
		this.boatDriver = boatDriver;
		this.boatCrewman = boatCrewman;
		this.jetSki = jetSki;
		this.fullTime = fullTime;
		this.fromArea = fromArea;
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
	
	public boolean getJetSki() {
		return jetSki;
	}
	
	public boolean getFullTime() {
		return fullTime;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getFromArea() {
		return fromArea;
		
	}

}

