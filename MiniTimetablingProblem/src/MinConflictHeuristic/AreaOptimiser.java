package MinConflictHeuristic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreaOptimiser {

	ArrayList<Employee> employees = new ArrayList<Employee>();
	ArrayList<Location> locations = new ArrayList<Location>();
	Area currentArea;

	public AreaOptimiser(Area currentArea) {
		employees = currentArea.getEmployees();
		locations = currentArea.getLocations();
		this.currentArea = currentArea;
	}

	public Area attemptOptimise() {
		HeuristicMeasure hm = new HeuristicMeasure();
		SoftHeuristic sh = new SoftHeuristic();
		int noChangeCount = 0;
		int heuristicScore = hm.heuristicScore(currentArea);
		while (heuristicScore != 0 && noChangeCount < 1000) {
			int[] areaViolationPosition = violationPosition();
			int attemptsAtViolation = 0;
			while (areaViolationPosition != null && attemptsAtViolation < 200) {
				if (Math.random() > 0.7) {
					// Switch with a random employee already in the timetable working another day
					int jPosition = areaViolationPosition[1];
					// Make sure jPosition isn't same as to switch position because that would
					// switch with someone already working the same day so would not be a swap.
					while (jPosition == areaViolationPosition[1]) {
						jPosition = (int) (Math.random() * 6);
					}
					int[] positionToSwitch = { (int) (Math.random() * 6), jPosition };
					Location locationWithViolation = currentArea.getLocations().get(areaViolationPosition[2]);
					Employee[][] timetable = locationWithViolation.getTimetable();
					Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
					Employee temp = copy[areaViolationPosition[0]][areaViolationPosition[1]];
					copy[areaViolationPosition[0]][areaViolationPosition[1]] = copy[positionToSwitch[0]][positionToSwitch[1]];
					copy[positionToSwitch[0]][positionToSwitch[1]] = temp;
					Location lCopy = new Location(locationWithViolation.getLocationID(),
							locationWithViolation.getrank4Req(), locationWithViolation.getrank3Req(),
							locationWithViolation.getrank2Req(), locationWithViolation.getBoatDriversReq(),
							locationWithViolation.getCrewmenReq(), locationWithViolation.getJetSkiUsersReq());
					lCopy.setTimetable(copy);
					
					// Create new area with the changed location to compare if a change brings a
					// better score
					ArrayList<Location> locationsCopy = new ArrayList<>();
					for (Location l : locations) {
						locationsCopy.add(l);
					}
					locationsCopy.set(areaViolationPosition[2], lCopy);
					Area areaCopy = new Area(locationsCopy, employees);
					
					if (hm.heuristicScore(areaCopy) <= hm.heuristicScore(currentArea) && sh.heuristicScore(areaCopy) < sh.heuristicScore(currentArea)) {
						locationWithViolation = lCopy;
						locations.set(areaViolationPosition[2], lCopy);
						currentArea.setLocations(locations);
						noChangeCount = 0;
					} else {
						noChangeCount++;
					}
				} else {
					// Switch with an employee from the list of all available employees
					// Similary to previous block but there may not be enough instances of an
					// employee in the timetable
					// So taking from list of all employees must occur
					Employee[] freeOnDay = getEmployeesFreeEachDay().get(areaViolationPosition[1]);
					// Try random employee
					int index = (int) (Math.random() * freeOnDay.length);
					Employee employeeToSwitch = freeOnDay[index];
					Location locationWithViolation = currentArea.getLocations().get(areaViolationPosition[2]);
					Employee[][] timetable = locationWithViolation.getTimetable();
					Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
					copy[areaViolationPosition[0]][areaViolationPosition[1]] = employeeToSwitch;
					Location lCopy = new Location(locationWithViolation.getLocationID(),
							locationWithViolation.getrank4Req(), locationWithViolation.getrank3Req(),
							locationWithViolation.getrank2Req(), locationWithViolation.getBoatDriversReq(),
							locationWithViolation.getCrewmenReq(), locationWithViolation.getJetSkiUsersReq());
					lCopy.setTimetable(copy);
					
					// Create new area with the changed location to compare if a change brings a
					// better score
					ArrayList<Location> locationsCopy = new ArrayList<>();
					for (Location l : locations) {
						locationsCopy.add(l);
					}
					locationsCopy.set(areaViolationPosition[2], lCopy);
					Area areaCopy = new Area(locationsCopy, employees);
					
					if (hm.heuristicScore(areaCopy) <= hm.heuristicScore(currentArea) && sh.heuristicScore(areaCopy) < sh.heuristicScore(currentArea)) {
						locationWithViolation = lCopy;
						locations.set(areaViolationPosition[2], lCopy);
						currentArea.setLocations(locations);
						noChangeCount = 0;
					} else {
						noChangeCount++;
					}
				}
				areaViolationPosition = violationPosition();
				attemptsAtViolation++;
			}
			heuristicScore = hm.heuristicScore(currentArea);
		}
		for (int i = 0; i < locations.size(); i++) {
			Location l = locations.get(i);
			sortEmployees(l.getTimetable());
		}
		Main.violationCount = 0;
		currentArea.setLocations(locations);
		return currentArea;
	}
	
	private int[] violationPosition() {
		return Math.random() > 0.5 ? fullTimeWithoutConsecutiveDaysOffPosition() : partTimeNotWorking2or3DaysPosition();
	}
	/*If an employee is working a day, not working a day, 
	 * and then working a day, then they do not have consecutive days off. 
	 * This function checks for consecutive days off by checking for this.*/
	private int[] fullTimeWithoutConsecutiveDaysOffPosition() {
		ArrayList<ArrayList<Employee>> ftEmployeesWorkingEachDay = new ArrayList<>();
		int employeesPerDay = 6;
		int daysInWeek = 7;
		for (int i = 0; i < currentArea.getLocations().get(0).getTimetable().length; i++) {
			int locationIndex = 0;
			ArrayList<Employee> ftEmployeesOnDay = new ArrayList<>();
			for (Location l : currentArea.getLocations()) {
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					Employee e = table[j][i];
					if(e.isfullTime()) {
						ftEmployeesOnDay.add(e);
					}
					if (i > 1) {
						if (!ftEmployeesWorkingEachDay.get(i-1).contains(e) && ftEmployeesWorkingEachDay.get(i-2).contains(e)) {
							return new int[] { j, i, locationIndex };
						}
					}
				}
				locationIndex++;
			}
			ftEmployeesWorkingEachDay.add(ftEmployeesOnDay);
		}
		return null;
	}
	
	private int[] partTimeNotWorking2or3DaysPosition() {
		Map<Employee, Integer> workingDaysCount = new HashMap<>();
		int employeesPerDay = 6;
		int daysInWeek = 7;
		for (int i = 0; i < currentArea.getLocations().get(0).getTimetable().length; i++) {
			int locationIndex = 0;
			for (Location l : currentArea.getLocations()) {
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					Employee e = table[j][i];
					if (e.isfullTime()) {
						if (workingDaysCount.containsKey(e)) {
							int value = workingDaysCount.get(e);
							if (value == 3) {
								return new int[] { j, i, locationIndex };
							} else {
								workingDaysCount.put(e, value);
							}
						} else {
							workingDaysCount.put(e, 1);
						}
					}
				}
				locationIndex++;
			}
		}
		return null;
	}
	
	public ArrayList<Employee[]> getEmployeesFreeEachDay() {
		ArrayList<Employee[]> employeesFree = new ArrayList<Employee[]>();
		int daysInWeek = 7;
		int employeesPerDay = 6;
		for (int i = 0; i < daysInWeek; i++) {
			List<Employee> employeesLocalPerDay = new ArrayList<>(employees);
			for (Location l : locations) {
				Employee[][] timetable = l.getTimetable();
				for (int j = 0; j < employeesPerDay; j++) {
					if (employeesLocalPerDay.contains(timetable[j][i])) {
						employeesLocalPerDay.remove(timetable[j][i]);
					}
				}
			}
			employeesFree.add(employeesLocalPerDay.toArray(new Employee[employeesLocalPerDay.size()]));
		}
		return employeesFree;
	}
	
	private static void sortEmployees(Employee[][] timetable) {
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int i = 0; i < timetable[0].length; i++) {
				for (int j = 0; j < timetable.length -1; j++) {
					if (timetable[j][i].getName().substring(0, 1).compareTo(timetable[j+1][i].getName().substring(0, 1)) > 0) {
						Employee temp = timetable[j][i];
						timetable[j][i] = timetable[j+1][i];
						timetable[j+1][i] = temp;
						sorted = false;
					}
				}
			}
		}
	}
	
}







