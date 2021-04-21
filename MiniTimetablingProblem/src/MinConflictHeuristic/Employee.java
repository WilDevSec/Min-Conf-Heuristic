package MinConflictHeuristic;

import javafx.beans.property.SimpleBooleanProperty;

public class Employee {

	private String name;
	private int rank;
	private boolean boatDriver, boatCrewman;
	private boolean jetSki, fullTime;
	private SimpleBooleanProperty boatDr, boatCr, jetS, fullT;

	public Employee(String name, int rank, boolean boatDriver, boolean boatCrewman, boolean jetSki, boolean fullTime) {
		this.name = name;
		this.rank = rank;
		this.boatDriver = boatDriver;
		this.boatCrewman = boatCrewman;
		this.jetSki = jetSki;
		this.fullTime = fullTime;
		this.boatDr = (new SimpleBooleanProperty(boatDriver));
		this.boatCr = new SimpleBooleanProperty(boatCrewman);
		this.jetS = (new SimpleBooleanProperty(jetSki));
		this.fullT = new SimpleBooleanProperty(fullTime);
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

}

