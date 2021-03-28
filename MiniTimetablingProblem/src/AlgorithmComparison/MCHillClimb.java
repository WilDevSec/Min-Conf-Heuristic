package AlgorithmComparison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MinConflictHeuristic.Employee;
import MinConflictHeuristic.Area;
import MinConflictHeuristic.HeuristicMeasure;
import MinConflictHeuristic.Location;
import MinConflictHeuristic.AreaSolver;

public class MCHillClimb {

	private Area area;
	private ArrayList<Location> locations;
	private ArrayList<Employee> employees;

	public MCHillClimb(Area area) {
		this.area = area;
		this.locations = area.getLocations();
		this.employees = area.getEmployees();
	}

	public void solveLocation() {
		Location location = locations.get(0);
		
	}
	
	public Area solve() {
		HeuristicMeasure hm = new HeuristicMeasure();
		AreaSolver as = new AreaSolver(area);
		int iterationCount = 0;
		int heuristicScore = hm.heuristicScore(area);
		while (heuristicScore != 0 ) {
			for (Location l : locations) {
				Employee[][] timetable = l.getTimetable();
				// Needs to try many different locations for each violation positions
				int[] violationPosition = getRankOrQualViolationPosition(l);
				int rankOrQualAttemptCounter = 0;
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
						Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
						Employee temp = copy[violationPosition[0]][violationPosition[1]];
						copy[violationPosition[0]][violationPosition[1]] = copy[positionToSwitch[0]][positionToSwitch[1]];
						copy[positionToSwitch[0]][positionToSwitch[1]] = temp;
						Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(),
								l.getrank2Req(), l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
						lCopy.setTimetable(copy);
						System.out.println("HScore of new location: " + hm.locationScore(lCopy));
						System.out.println("HScore of old location: " + hm.locationScore(l));
						if (hm.locationScore(lCopy) < hm.locationScore(l)) {
							l = lCopy;
							System.out.println("Employee switched = " + temp.getName());
							System.out.println("Heuristic score: " + hm.locationScore(l));
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
							System.out.println("Employee switched = " + employeeToSwitch.getName());
							System.out.println("Heuristic score: " + hm.locationScore(l));
						}
					}
					violationPosition = getRankOrQualViolationPosition(l);
					rankOrQualAttemptCounter++;
				}
			}
			// Then needs to try all the violation positions, maybe 50 times each limit.
			int[] areaViolationPosition = getDoubleBookedConstraintPosition();
			int doubleBookedAttemptCount = 0;
			while (areaViolationPosition != null) {
				if (Math.random() > 0.7) {
					// Switch with a random employee already in the timetable working another day
					int jPosition = areaViolationPosition[1];
					// Make sure jPosition isn't same as to switch position because that would
					// switch with someone already working the same day so would not be a swap.
					while (jPosition == areaViolationPosition[1]) {
						jPosition = (int) (Math.random() * 7);
					}
					int[] positionToSwitch = { (int) (Math.random() * 7), jPosition };
					Location locationWithViolation = area.getLocations().get(areaViolationPosition[2]);
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
					if (hm.locationScore(lCopy) < hm.locationScore(locationWithViolation)) {
						locationWithViolation = lCopy;
						System.out.println("Employee switched = " + temp.getName());
						System.out.println("Heuristic score: " + hm.locationScore(locationWithViolation));
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
					Location locationWithViolation = area.getLocations().get(areaViolationPosition[2]);
					Employee[][] timetable = locationWithViolation.getTimetable();
					Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
					copy[areaViolationPosition[0]][areaViolationPosition[1]] = employeeToSwitch;
					Location lCopy = new Location(locationWithViolation.getLocationID(),
							locationWithViolation.getrank4Req(), locationWithViolation.getrank3Req(),
							locationWithViolation.getrank2Req(), locationWithViolation.getBoatDriversReq(),
							locationWithViolation.getCrewmenReq(), locationWithViolation.getJetSkiUsersReq());
					lCopy.setTimetable(copy);
					if (hm.locationScore(locationWithViolation) > hm.locationScore(lCopy)) {
						locationWithViolation = lCopy;
						System.out.println("Employee switched = " + employeeToSwitch.getName());
						System.out.println("Heuristic score: " + hm.locationScore(locationWithViolation));
					}
				}
				areaViolationPosition = getDoubleBookedConstraintPosition();
			}
			doubleBookedAttemptCount++;
		}
		return area;
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
	
	private int[] getDoubleBookedConstraintPosition() {
		Map<Employee, Integer> timetable = new HashMap<>();
		int employeesPerDay = 6;
		int daysInWeek = 7;
		int randomStartPointI = (int) Math.random() * employeesPerDay;
		int randomStartPointJ = (int) Math.random() * daysInWeek;
		for (int i = 0; i < area.getLocations().get(0).getTimetable().length; i++) {
			// Need location index to return which location the constraint violation is in,
			// along with the position of the violation within that location's timetable.
			int locationIndex = 0;
			for (Location l : area.getLocations()) {
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

}
