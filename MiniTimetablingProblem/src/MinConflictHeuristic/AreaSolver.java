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
		while (heuristicScore != 0 && noChangeCount < 10000) {
			for (int i = 0; i < locations.size(); i++) {
				Location l = locations.get(i);
				Employee[][] timetable = l.getTimetable();
				// Needs to try many different locations for each violation position
				int[] violationPosition = getRankOrQualViolationPosition(l);
				int rankOrQualAttemptCounter = 0;
				// 200 attempts at trying to solve each location's conflicts
				while (violationPosition != null && rankOrQualAttemptCounter < 10000) {
					Location lCopy;
					if (Math.random() > 0.5) {
						lCopy = switchEmployeeTT(violationPosition[0], violationPosition[1], l);

					} else {
						lCopy = switchEmployeeFree(violationPosition[0], violationPosition[1], l);
					}
					if (noChangeCount > 10000 && employeesPreviousAreas.size() != 0
							&& employeesPreviousAreas.get(violationPosition[1]).size() != 0) {
						lCopy = switchEmployeeStack(violationPosition[0], violationPosition[1], l);
					}
					if (hm.locationScore(lCopy) < hm.locationScore(l)) {
						System.out.println("Change made");
						l = lCopy;
						locations.set(i, l);
						// Peeks rather than pops in function so if employee used then must be removed:
						if (noChangeCount >= 3000) {
							employeesPreviousAreas.get(violationPosition[1]).pop();
						}
						noChangeCount = 0;
					} else {
						noChangeCount++;
					}
					violationPosition = getRankOrQualViolationPosition(l);
					rankOrQualAttemptCounter++;
				}
			}
			
			// 2000 Attempts to minimise the area's conflicts per iteration
			int[] areaViolationPosition = getAreaConstraintViolationPosition();
			int doubleBookedAttemptCount = 0;
			while (areaViolationPosition != null && doubleBookedAttemptCount < 10000) {
				Location lCopy;
				if (Math.random() > 0.5) {
					lCopy = switchEmployeeTT(areaViolationPosition[0], areaViolationPosition[1],
							currentArea.getLocations().get(areaViolationPosition[2]));
				} else {
					lCopy = switchEmployeeFree(areaViolationPosition[0], areaViolationPosition[1],
							currentArea.getLocations().get(areaViolationPosition[2]));
				}
				if (noChangeCount > 3000 && employeesPreviousAreas.size() != 0
						&& employeesPreviousAreas.get(areaViolationPosition[1]).size() != 0) {
					lCopy = switchEmployeeStack(areaViolationPosition[0], areaViolationPosition[1],
							currentArea.getLocations().get(areaViolationPosition[2]));
				}
				ArrayList<Location> locationsCopy = new ArrayList<>();
				for (Location l : locations) {
					locationsCopy.add(l);
				}
				locationsCopy.set(areaViolationPosition[2], lCopy);
				Area areaCopy = new Area(locationsCopy, employees);
				if (hm.heuristicScore(areaCopy) < hm.heuristicScore(currentArea)) {
					System.out.println("Change made1");
					locations.set(areaViolationPosition[2], lCopy);
					currentArea.setLocations(locations);
					noChangeCount = 0;
				} else {
					noChangeCount++;
				}

				areaViolationPosition = getAreaConstraintViolationPosition();
				doubleBookedAttemptCount++;
			}
			heuristicScore = hm.heuristicScore(currentArea);
			noChangeCount++;
		}
		for (int i = 0; i < locations.size(); i++) {
			locations.set(i, sortEmployees(locations.get(i)));
		}
		Main.violationCount = 0;
		currentArea.setLocations(locations);
		return currentArea;
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
		int index = (int) (Math.random() * freeOnDay.length);
		Employee employeeToSwitch = freeOnDay[index];
		Employee[][] timetable = l.getTimetable();
		Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
		copy[i][j] = employeeToSwitch;
		Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(), l.getrank2Req(),
				l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
		lCopy.setTimetable(copy);
		return lCopy;
	}

	private Location switchEmployeeStack(int i, int j, Location l) {
		Employee employeeToSwitch = employeesPreviousAreas.get(j).peek();
		Employee[][] timetable = l.getTimetable();
		Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
		copy[i][j] = employeeToSwitch;
		Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(), l.getrank2Req(),
				l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
		lCopy.setTimetable(copy);
		return lCopy;
	}

	private static Location sortEmployees(Location location) {
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
				if (e.isBoatDriver())
					boatDriverCount++;
				if (e.isBoatCrewman())
					crewmenCount++;
				if (e.isjetSkiUser())
					jetSkiUsersCount++;
			}
			// Start at random point loop through whole array using Modulo

			/*
			 * If there are not enough employees of any rank 4,3,2 then return the position
			 * of a rank 1 firstly, as they will be least likely to decrease any score by
			 * switching out someone of another rank that is required.
			 */
			if (rank4Count < location.getrank4Req() || rank3Count < location.getrank3Req()
					|| rank2Count < location.getrank2Req()) {
				for (int j = 0; j < employeesPerDay; j++) {
					if (timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek]
							.getRank() == 1) {
						return new int[] { (j + randomStartPointJ) % employeesPerDay,
								(i + randomStartPointI) % daysInWeek };
					}
				}
			}
			/*
			 * Then return position of someone who is just not of the rank which is in
			 * deficit. May be cases where all employees of one rank are in the same
			 * location on the same day
			 */
			if (rank4Count < location.getrank4Req()) {
				for (int j = 0; j < employeesPerDay; j++) {
					if (timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek]
							.getRank() != 4) {
						return new int[] { (j + randomStartPointJ) % employeesPerDay,
								(i + randomStartPointI) % daysInWeek };
					}
				}
			}
			if (rank3Count < location.getrank3Req()) {
				for (int j = 0; j < employeesPerDay; j++) {
					if (timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek]
							.getRank() != 3) {
						return new int[] { (j + randomStartPointJ) % employeesPerDay,
								(i + randomStartPointI) % daysInWeek };
					}
				}
			}
			if (rank2Count < location.getrank2Req()) {
				for (int j = 0; j < employeesPerDay; j++) {
					if (timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek]
							.getRank() != 2) {
						return new int[] { (j + randomStartPointJ) % employeesPerDay,
								(i + randomStartPointI) % daysInWeek };
					}
				}
			}
			// If not enough each qualification then return position of someone who doesn't
			// have it.
			if (boatDriverCount < location.getBoatDriversReq()) {
				for (int j = 0; j < employeesPerDay; j++) {
					if (!timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek]
							.isBoatDriver()) {
						return new int[] { (j + randomStartPointJ) % employeesPerDay,
								(i + randomStartPointI) % daysInWeek };
					}
				}
			}
			if (crewmenCount < location.getCrewmenReq()) {
				for (int j = 0; j < employeesPerDay; j++) {
					if (!timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek]
							.isBoatCrewman()) {
						return new int[] { (j + randomStartPointJ) % employeesPerDay,
								(i + randomStartPointI) % daysInWeek };
					}
				}
			}
			if (jetSkiUsersCount < location.getJetSkiUsersReq()) {
				for (int j = 0; j < employeesPerDay; j++) {
					if (!timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek]
							.isjetSkiUser()) {
						return new int[] { (j + randomStartPointJ) % employeesPerDay,
								(i + randomStartPointI) % daysInWeek };
					}
				}
			}
		}
		return null;
	}

	// Can return either double booked employee or employee working too many days
	// randomly.
	private int[] getAreaConstraintViolationPosition() {
		if (Math.random() < 0.5) {
			return getDoubleBookedConstraintPosition();
		} else {
			return employeeWorkingMoreThan5DaysLocation();
		}
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
		for (int i = 0; i < daysInWeek; i++) {
			int locationIndex = 0;
			for (int k = 0; k < 5; k++) {
				Location l = currentArea.getLocations().get(k);
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					if (workingDaysCount.containsKey(table[j][i])) {
						int value = workingDaysCount.get(table[j][i]);
						if (value == 5) {
							return new int[] { j, i, k };
						} else {
							value += 1;
							workingDaysCount.put(table[j][i], value);
						}
					} else {
						workingDaysCount.put(table[j][i], 1);
					}
				}
			}
		}
		return null;
	}

}
