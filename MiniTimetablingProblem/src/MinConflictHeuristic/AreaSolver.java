package MinConflictHeuristic;

import java.util.*;

public class AreaSolver {

	private ArrayList<Employee> employees = new ArrayList<Employee>();
	private ArrayList<Location> locations = new ArrayList<Location>();
	private Area currentArea;

	public AreaSolver(Area currentArea) {
		employees = currentArea.getEmployees();
		locations = currentArea.getLocations();
		this.currentArea = currentArea;
	}

	public Area attemptSolve() {
		HeuristicMeasure hm = new HeuristicMeasure();
		int noChangeCount = 0;
		int heuristicScore = hm.heuristicScore(currentArea);
		while (heuristicScore != 0 && noChangeCount < 10000) {
			for (int i = 0; i < locations.size(); i++) {
				Location l = locations.get(i);
				Employee[][] timetable = l.getTimetable();
				// Needs to try many different locations for each violation positions
				int[] violationPosition = getRankOrQualViolationPosition(l);
				int rankOrQualAttemptCounter = 0;
				// 200 attempts at trying to solve each location's conflicts
				while (violationPosition != null) {
					if (Math.random() > 0.5) {
						// Switch with a random employee already in the timetable working another day
						int jPosition = violationPosition[1];
						// Make sure jPosition isn't same as "to-switch" j position because that would
						// switch with someone already working the same day so would not be a swap.
						while (jPosition == violationPosition[1]) {
							jPosition = (int) (Math.random() * timetable[0].length);
						}
						int[] positionToSwitch = { (int) (Math.random() * timetable.length), jPosition };
//						Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
//						Employee temp = copy[violationPosition[0]][violationPosition[1]];
//						copy[violationPosition[0]][violationPosition[1]] = copy[positionToSwitch[0]][positionToSwitch[1]];
//						copy[positionToSwitch[0]][positionToSwitch[1]] = temp;
//						Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(),
//								l.getrank2Req(), l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
						
						Location lCopy = switchEmployee(positionToSwitch[0],positionToSwitch[1], l);
						
//						lCopy.setTimetable(copy);
						if (hm.locationScore(lCopy) < hm.locationScore(l)) {
							l = lCopy;
							locations.set(i, l);
							noChangeCount = 0;
						} else {
							noChangeCount++;
						}
					} else {
						// Switch with an employee not working that day
						Employee[] freeOnDay = getEmployeesFreeEachDay().get(violationPosition[1]);
						// Try random employee
						int index = (int) (Math.random() * freeOnDay.length);
						Employee employeeToSwitch = freeOnDay[index];
						Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
						copy[violationPosition[0]][violationPosition[1]] = employeeToSwitch;
						Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(),
								l.getrank2Req(), l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
						lCopy.setTimetable(copy);
						
						if (hm.locationScore(lCopy) < hm.locationScore(l)) {
							l = lCopy;
							locations.set(i, l);
							noChangeCount = 0;
						} else {
							noChangeCount++;
						}
					}
					violationPosition = getRankOrQualViolationPosition(l);
					rankOrQualAttemptCounter++;
				}
			}
			// 2000 Attempts to minimise the area's conflicts per iteration
			int[] areaViolationPosition = getAreaConstraintViolationPosition();
			int doubleBookedAttemptCount = 0;
			while (areaViolationPosition != null) {
				if (Math.random() > 0.7) {
					// Switch with a random employee already in the timetable working another day
					int jPosition = areaViolationPosition[1];
					// Make sure jPosition isn't same as to switch position because that would
					// switch with someone already working the same day so would not be a swap.
					while (jPosition == areaViolationPosition[1]) {
						jPosition = (int) (Math.random() * 6);
					}

					// Create a copy of location and make a change from a violation position to
					// another position
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

					// If heuristic score of area is less in the copy then set copy as new area.
					if (hm.heuristicScore(areaCopy) < hm.heuristicScore(currentArea)) {
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
					
					// If heuristic score of area is less in the copy then set copy as new area.
					if (hm.heuristicScore(areaCopy) < hm.heuristicScore(currentArea)) {
						locationWithViolation = lCopy;
						locations.set(areaViolationPosition[2], lCopy);
						currentArea.setLocations(locations);
						noChangeCount = 0;
					} else {
						noChangeCount++;
					}
				}
				areaViolationPosition = getAreaConstraintViolationPosition();
				doubleBookedAttemptCount++;
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

	private Location switchEmployee(int i, int j, Location l) {
		Employee[][] timetable = l.getTimetable();
		// Creating a copy here as the original timetable may be better meaning a change should not occur.
		Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
		Employee temp = copy[i][j];
		copy[i][j] = copy[i][j];
		copy[i][j] = temp;
		Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(),
				l.getrank2Req(), l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
		lCopy.setTimetable(copy);
		return lCopy;
	}
	
	
	private static void sortEmployees(Employee[][] timetable) {
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
			System.out.println(
					"0th employee" + employeesCopy.get(0).getName() + " 1st emp" + employeesCopy.get(1).getName());
			Collections.shuffle(employeesCopy);
			System.out.println(
					"0th employee" + employeesCopy.get(0).getName() + "1st emp" + employeesCopy.get(1).getName());
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
		Map<Employee, Integer> timetable = new HashMap<>();
		int employeesPerDay = 6;
		int daysInWeek = 7;
		int randomStartPointI = (int) Math.random() * employeesPerDay;
		int randomStartPointJ = (int) Math.random() * daysInWeek;
		for (int i = 0; i < currentArea.getLocations().get(0).getTimetable().length; i++) {
			// Need location index to return which location the constraint violation is in,
			// along with the position of the violation within that location's timetable.
			int locationIndex = 0;
			for (Location l : currentArea.getLocations()) {
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					if (timetable.containsKey(
							table[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek])) {
						return new int[] { (j + randomStartPointJ) % employeesPerDay,
								(i + randomStartPointI) % daysInWeek, locationIndex };
					} else {
						timetable.put(
								table[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek],
								1);
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
		for (int i = 0; i < currentArea.getLocations().get(0).getTimetable().length; i++) {
			int locationIndex = 0;
			for (Location l : currentArea.getLocations()) {
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					if (workingDaysCount.containsKey(table[j][i])) {
						int value = workingDaysCount.get(table[j][i]);
						if (value == 5) {
							return new int[] { j, i, locationIndex };
						} else {
							workingDaysCount.put(table[j][i], value);
						}
					} else {
						workingDaysCount.put(table[j][i], 1);
					}
				}
				locationIndex++;
			}
		}
		return null;
	}

}
