package MinConflictHeuristic;

import java.util.*;

public class AreaSolver {

	private ArrayList<Employee> employees;
	private ArrayList<Location> locations;
	private Area currentArea;
	private ArrayList<Stack<Employee>> employeesPreviousAreas;

	public AreaSolver(Area currentArea, ArrayList<Stack<Employee>> employeesPreviousAreas) {
		this.employees = currentArea.getEmployees();
		this.locations = currentArea.getLocations();
		this.currentArea = currentArea;
		this.employeesPreviousAreas = employeesPreviousAreas;
	}

	public Area attemptSolve() {
		HeuristicMeasure hm = new HeuristicMeasure();
		int noChangeCount = 0;
		int heuristicScore = hm.heuristicScore(currentArea);
		while (heuristicScore != 0 && noChangeCount < 1000) {
			for (int i = 0; i < locations.size(); i++) {
				Location l = locations.get(i);
				// Needs to try many different locations for each violation position
				int[] violationPosition = getRankOrQualViolationPosition(l);
				int rankOrQualAttemptCounter = 0;
				// 200 attempts at trying to solve each location's conflicts
				while (violationPosition != null && rankOrQualAttemptCounter < 200) {
					l = locations.get(i);
					Area areaCopy;
					if (Math.random() > 0.5) {
						areaCopy = switchEmployeeTT(violationPosition[0], violationPosition[1], l, i);
					} else {
						areaCopy = switchEmployeeFree(violationPosition[0], violationPosition[1], l, i);
					}
					if (noChangeCount > 800 && employeesPreviousAreas.size() != 0
							&& employeesPreviousAreas.get(violationPosition[1]).size() != 0) {
						areaCopy = switchEmployeeStack(violationPosition[0], violationPosition[1], l, i);
					}
					if (hm.heuristicScore(areaCopy) < hm.heuristicScore(currentArea)) {
//						System.out.println("Change made");
						currentArea = areaCopy;
						locations = currentArea.getLocations();
						// Peeks rather than pops in function so if employee used then must be removed:
						if (noChangeCount >= 800 && employeesPreviousAreas.get(violationPosition[1]).size() != 0) {
							employeesPreviousAreas.get(violationPosition[1]).pop();
							noChangeCount = 0;
						}
//						noChangeCount = 0;
					} else {
						noChangeCount++;
					}
					violationPosition = getRankOrQualViolationPosition(l);
					rankOrQualAttemptCounter++;
				}
			}

			// 2000 Attempts to minimise the area's conflicts per iteration
			int[] areaViolationPosition = getAreaConstraintViolationPosition();
			int areaViolationAttemptCount = 0;
			while (areaViolationPosition != null && areaViolationAttemptCount < 200) {
				Area areaCopy  = switchEmployeeFree(areaViolationPosition[0], areaViolationPosition[1],
							currentArea.getLocations().get(areaViolationPosition[2]), areaViolationPosition[2]);
				if (noChangeCount > 4500 && employeesPreviousAreas.size() != 0
						&& employeesPreviousAreas.get(areaViolationPosition[1]).size() != 0) {
					areaCopy = switchEmployeeStack(areaViolationPosition[0], areaViolationPosition[1],
							currentArea.getLocations().get(areaViolationPosition[2]), areaViolationPosition[2]);
				}
				if (hm.heuristicScore(areaCopy) < hm.heuristicScore(currentArea)) {
//					System.out.println("Change made1");
					currentArea = areaCopy;
					locations = currentArea.getLocations();
//					noChangeCount = 0;
				} else {
					noChangeCount++;
				}
				areaViolationPosition = getAreaConstraintViolationPosition();
				areaViolationAttemptCount++;
			}
			heuristicScore = hm.heuristicScore(currentArea);
			noChangeCount++;
		}
		for (int i = 0; i < locations.size(); i++) {
			locations.set(i, sortEmployees(locations.get(i)));
		}
		currentArea.setLocations(locations);
//		Main.violationCount = 0;
		return currentArea;
	}

	// Switch with a random employee already in the timetable working another day
	private Area switchEmployeeTT(int i, int j, Location l, int locVioPosition) {
		Employee[][] timetable = l.getTimetable();
		int iPosition = (int) (Math.random() * timetable.length);
		int jPosition = (int) (Math.random() * timetable[0].length);
		int locPosition = (int) (Math.random() * 5);
		Location lToSwitch = locations.get(locPosition);
		Employee[][] timetable2 = lToSwitch.getTimetable();
		// Make sure jPosition isn't same as "to-switch" j position because that would
		// switch with someone already working the same day so would not be a swap.
		while (jPosition == j) {
			jPosition = (int) (Math.random() * timetable[0].length);
		}
		Employee temp = timetable[i][j];
		timetable[i][j] = timetable2[iPosition][jPosition];
		timetable2[iPosition][jPosition] = temp;
	
//		Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
		Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(), l.getrank2Req(),
				l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
		lCopy.setTimetable(timetable);
		Location lCopy2 = new Location(lToSwitch.getLocationID(), lToSwitch.getrank4Req(), lToSwitch.getrank3Req(), lToSwitch.getrank2Req(),
				lToSwitch.getBoatDriversReq(), lToSwitch.getCrewmenReq(), lToSwitch.getJetSkiUsersReq());
		lCopy2.setTimetable(timetable2);
		ArrayList<Location> locationsTemp = new ArrayList<>(locations);
		locationsTemp.set(locVioPosition, lCopy);
		locationsTemp.set(locPosition, lCopy2);
		Area areaCopy = new Area(locationsTemp, employees);
		return areaCopy;
	}

	// Switch employee in timetable with someone from list of Employees
	private Area switchEmployeeFree(int i, int j, Location l, int locVioPosition) {
		Employee[] freeOnDay = getEmployeesFreeEachDay().get(j);
		// Try random employee
		int index = (int) (Math.random() * freeOnDay.length -1);
		Employee employeeToSwitch = freeOnDay[index];
		Employee[][] timetable = l.getTimetable();
		timetable[i][j] = employeeToSwitch;
		Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(), l.getrank2Req(),
				l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
		lCopy.setTimetable(timetable);
		ArrayList<Location> locationsTemp = new ArrayList<>(locations);
		locationsTemp.set(locVioPosition, lCopy);
		Area areaCopy = new Area(locationsTemp, employees);
		return areaCopy;
	}

	private Area switchEmployeeStack(int i, int j, Location l, int locVioPos) {
		Employee employeeToSwitch = employeesPreviousAreas.get(j).peek();
		Employee[][] timetable = l.getTimetable();
		timetable[i][j] = employeeToSwitch;
		Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(), l.getrank2Req(),
				l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
		lCopy.setTimetable(timetable);
		ArrayList<Location> locationsTemp = new ArrayList<>(locations);
		locationsTemp.set(locVioPos, lCopy);
		Area areaCopy = new Area(locationsTemp, employees);
		return areaCopy;
	}

	private Location sortEmployees(Location location) {
		Employee[][] timetable = location.getTimetable();
		for (int i = 0; i < timetable[0].length; i++) {
			for (int j = 0; j < timetable.length - 1; j++) {
				for (int k = j; k < timetable.length; k++) {
					if (timetable[j][i].getName().substring(0, 1)
							.compareTo(timetable[k][i].getName().substring(0, 1)) > 0) {
						Employee temp = timetable[j][i];
						timetable[j][i] = timetable[k][i];
						timetable[k][i] = temp;
					}
				}
			}
		}
		Location l = new Location(location.getLocationID(), location.getrank4Req(), location.getrank3Req(),
				location.getrank2Req(), location.getBoatDriversReq(), location.getCrewmenReq(),
				location.getJetSkiUsersReq());
		l.setTimetable(timetable);
		return l;
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

	public Area populateTimetablesRandomly() {
		int employeesPerDay = 6;
		int daysInWeek = 7;
		List<Employee[][]> locationTimetables = new ArrayList<>();
		for (int i = 0; i < locations.size(); i++) {
			locationTimetables.add(new Employee[employeesPerDay][daysInWeek]);
		}
		for (int i = 0; i < daysInWeek; i++) {
			int indexEmployeesUsed = 0;
			List<Employee> employeesCopy = employees;
			employeesCopy = shuffleList(employeesCopy);
			for (int j = 0; j < locations.size(); j++) {
				Employee[][] e = locationTimetables.get(j);
				for (int k = 0; k < employeesPerDay; k++) {
					e[k][i] = employeesCopy.get(indexEmployeesUsed);
					indexEmployeesUsed += 1;
				}
				locationTimetables.set(j, e);
			}
		}
		int index = 0;
		for (int i = 0; i < locations.size(); i++) {
			Location l = locations.get(i);
			l.setTimetable(locationTimetables.get(index++));
			locations.set(i, l);
		}
		currentArea.setLocations(locations);
		return currentArea;

	}

	// Collections built in shuffle will not work even when entirely isolated so
	// have to create my own shuffle function :(
	private List<Employee> shuffleList(List<Employee> employeeList) {
		Random r = new Random();
		List<Employee> shuffledList = new ArrayList<Employee>(employeeList);
		for (int i = employeeList.size() - 1; i > 0; i--) {
			int j = r.nextInt(i);
			Employee temp = shuffledList.get(i);
			shuffledList.set(i, shuffledList.get(j));
			shuffledList.set(j, temp);
		}
		return shuffledList;
	}

	// Hard constraint
	// Finding the position of where the minimum number of employees with a rank is
	// not met
	private int[] getRankOrQualViolationPosition(Location location) {
		int employeesPerDay = 6;
		int daysInWeek = 7;
		int randomStartPointI = (int) (Math.random() * employeesPerDay);
		int randomStartPointJ = (int) (Math.random() * daysInWeek);
		int rank4Req = location.getrank4Req();
		int rank3Req = location.getrank3Req();
		int rank2Req = location.getrank2Req();
		int bDrReq = location.getBoatDriversReq();
		int bCrReq = location.getCrewmenReq();
		int jsReq = location.getJetSkiUsersReq();
		Employee[][] timetable = location.getTimetable();
		// Find which constraints are violated
		for (int i = 0; i < daysInWeek; i++) {
			int rank4Count = 0;
			int rank3Count = 0;
			int rank2Count = 0;
			int boatDriverCount = 0;
			int crewmenCount = 0;
			int jetSkiUsersCount = 0;
			for (int j = 0; j < employeesPerDay; j++) {
				Employee e = timetable[j][i];
				int rank = e.getRank();
				if (rank == 4)
					rank4Count++;
				else if (rank == 3)
					rank3Count++;
				else if (rank == 2)
					rank2Count++;
				if (e.getBoatDriver())
					boatDriverCount++;
				if (e.getBoatCrewman())
					crewmenCount++;
				if (e.getJetSki())
					jetSkiUsersCount++;
			}
			for (int j = 0; j < employeesPerDay; j++) {
				Employee e = timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek];
				int[] pos = new int[] { (j + randomStartPointJ) % employeesPerDay,
						(i + randomStartPointI) % daysInWeek };
				
				if (rank4Count < rank4Req) {
					if (rank3Count > rank3Req && e.getRank() == 3) {
						return pos;
					}
					else if (rank2Count > rank2Req && e.getRank() == 2) {
						return pos;
					}
					else if (e.getRank() == 1) {
						return pos;
					}
				}
				if (rank2Count + rank3Count < rank3Req) {
					if (rank4Count > rank4Req && e.getRank() == 2) {
						return pos;
					}
					else if (e.getRank() == 1) {
						return pos;
					}
				}
				if (boatDriverCount < bDrReq && e.getBoatDriver()) {
					return pos;
				}
				if (crewmenCount < bCrReq && e.getBoatCrewman()) {
					return pos;
				}
				if (jetSkiUsersCount < jsReq && e.getJetSki()) {
					return pos;
				}
			}
		}
		return null;
	}

	// Can return either double booked employee or employee working too many days
	// randomly.
	private int[] getAreaConstraintViolationPosition() {
		return employeeWorkingMoreThan5DaysLocation() != null ? employeeWorkingMoreThan5DaysLocation()
				: getDoubleBookedConstraintPosition();
	}

	private int[] getDoubleBookedConstraintPosition() {
		int employeesPerDay = 6;
		int daysInWeek = 7;
		int randomStartPointI = (int) Math.random() * employeesPerDay;
		int randomStartPointJ = (int) Math.random() * daysInWeek;
		for (int i = 0; i < daysInWeek; i++) {
			List<Employee> employees = new ArrayList<>();
			// Need location index to return which location the constraint violation is in,
			// along with the position of the violation within that location's timetable.
			int locationIndex = 0;
			for (Location l : currentArea.getLocations()) {
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					if (employees.contains(
							table[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek])) {
						return new int[] { (j + randomStartPointJ) % employeesPerDay,
								(i + randomStartPointI) % daysInWeek, locationIndex };
					} else {
						employees.add(
								table[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek]);
					}
				}
				locationIndex++;
			}
		}
		return null;
	}

	private int[] employeeWorkingMoreThan5DaysLocation() {
		Map<Employee, Integer> workingDaysCount = new HashMap<>();
		int employeesPerDay = 6;
		int daysInWeek = 7;
		int randomStartPointI = (int) Math.random() * employeesPerDay;
		int randomStartPointJ = (int) Math.random() * daysInWeek;
		for (int i = 0; i < daysInWeek; i++) {
			int iPoint = (i + randomStartPointI) % daysInWeek;
			for (int k = 0; k < 5; k++) {
				Location l = currentArea.getLocations().get(k);
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					int jPoint = (j + randomStartPointJ) % employeesPerDay;
					if (workingDaysCount.containsKey(table[jPoint][iPoint])) {
						int value = workingDaysCount.get(table[jPoint][iPoint]);
						if (value == 5) {
							return new int[] { jPoint, iPoint, k };
						} else {
							value += 1;
							workingDaysCount.put(table[jPoint][iPoint], value);
						}
					} else {
						workingDaysCount.put(table[jPoint][iPoint], 1);
					}
				}
			}
		}
		return null;
	}

}
