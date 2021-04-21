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
		int softHeuristicScore = sh.heuristicScore(currentArea);
		while (softHeuristicScore != 0 && noChangeCount < 100) {
			int[] areaViolationPosition = violationPosition();
			int attemptsAtViolation = 0;
			while (areaViolationPosition != null && attemptsAtViolation < 100) {
				Location lCopy;
				if (Math.random() > 0.5) {
					lCopy = switchEmployeeTT(areaViolationPosition[0], areaViolationPosition[1],
							currentArea.getLocations().get(areaViolationPosition[2]));
				} else {
					lCopy = switchEmployeeFree(areaViolationPosition[0], areaViolationPosition[1],
							currentArea.getLocations().get(areaViolationPosition[2]));
				}
				ArrayList<Location> locationsCopy = new ArrayList<>();
				for (Location l : locations) {
					locationsCopy.add(l);
				}
				locationsCopy.set(areaViolationPosition[2], lCopy);
				Area areaCopy = new Area(locationsCopy, employees);
				if (hm.heuristicScore(areaCopy) <= hm.heuristicScore(currentArea)
						&& sh.heuristicScore(areaCopy) < sh.heuristicScore(currentArea)) {
					locations.set(areaViolationPosition[2], lCopy);
					currentArea.setLocations(locations);
					noChangeCount = 0;
				} else {
					noChangeCount++;
				}
				areaViolationPosition = violationPosition();
				noChangeCount++;
			}
			softHeuristicScore = sh.heuristicScore(currentArea);
		}
		for (int i = 0; i < locations.size(); i++) {
			Location l = locations.get(i);
			sortEmployees(l.getTimetable());
		}
		currentArea.setLocations(locations);
		return currentArea;
	}

	private int[] violationPosition() {
		return Math.random() > 0.5 ? fullTimeWithoutConsecutiveDaysOffPosition() : partTimeNotWorking2or3DaysPosition();
	}

	/*
	 * If an employee is working a day, not working a day, and then working a day,
	 * then they do not have consecutive days off. This function checks for
	 * consecutive days off by checking for this.
	 */
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
					if (e.getFullTime()) {
						ftEmployeesOnDay.add(e);
					}
					if (i > 1) {
						if (!ftEmployeesWorkingEachDay.get(i - 1).contains(e)
								&& ftEmployeesWorkingEachDay.get(i - 2).contains(e)) {
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
					if (e.getFullTime()) {
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

	// Switch with a random employee already in the timetable working another day
		private Location switchEmployeeTT(int i, int j, Location l) {
			Employee[][] timetable = l.getTimetable();
			int iPosition = (int) (Math.random() * timetable.length);
			int jPosition = (int) (Math.random() * timetable[0].length);
			// Make sure jPosition isn't same as "to-switch" j position because that would
			// switch with someone already working the same day so would not be a swap.
			while (jPosition == j) {
				jPosition = (int) (Math.random() * timetable[0].length);
			}
			// Creating a copy here as the original timetable may be better meaning a change
			// should not occur.
			Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
			Employee temp = copy[i][j];
			copy[i][j] = copy[iPosition][jPosition];
			copy[iPosition][jPosition] = temp;
			Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(), l.getrank2Req(),
					l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
			lCopy.setTimetable(copy);
			return lCopy;
		}

		// Switch employee in timetable with someone from list of Employees
		private Location switchEmployeeFree(int i, int j, Location l) {
			Employee[] freeOnDay = getEmployeesFreeEachDay().get(j);
			// Try random employee
			int index = (int) (Math.random() * freeOnDay.length -1);
			Employee employeeToSwitch = freeOnDay[index];
			Employee[][] timetable = l.getTimetable();
			timetable[i][j] = employeeToSwitch;
			Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(), l.getrank2Req(),
					l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
			lCopy.setTimetable(timetable);
			return lCopy;
		}

	private static void sortEmployees(Employee[][] timetable) {
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int i = 0; i < timetable[0].length; i++) {
				for (int j = 0; j < timetable.length - 1; j++) {
					if (timetable[j][i].getName().substring(0, 1)
							.compareTo(timetable[j + 1][i].getName().substring(0, 1)) > 0) {
						Employee temp = timetable[j][i];
						timetable[j][i] = timetable[j + 1][i];
						timetable[j + 1][i] = temp;
						sorted = false;
					}
				}
			}
		}
	}

}
