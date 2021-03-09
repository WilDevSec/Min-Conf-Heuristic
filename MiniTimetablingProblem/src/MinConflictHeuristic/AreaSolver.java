package MinConflictHeuristic;

import java.util.*;

public class AreaSolver {

	ArrayList<Employee> employees = new ArrayList<Employee>();
	ArrayList<Location> locations = new ArrayList<Location>();
	Area currentArea;

	public AreaSolver(Area currentArea) {
		employees = currentArea.getEmployees();
		locations = currentArea.getLocations();
		this.currentArea = currentArea;
	}

	
	public Area attemptSolve() {
		initialiseEmployeesRandomly();
		HeuristicMeasure hm = new HeuristicMeasure();
		int iterationCount = 0;
		int heuristicScore = hm.heuristicScore(currentArea);
		while (heuristicScore != 0 && iterationCount < 1000) {
			for (Location l : locations) {
				Employee[][] timetable = l.getTimetable();
				// Needs to try many different locations for each violation positions
				int[] violationPosition = getRankOrQualViolationPosition(l);
				//Then needs to try all the violation positions, maybe 50 times each limit.
				int[] positionToSwitch = {(int) (Math.random() * timetable.length), (int) (Math.random() * timetable[0].length)};
				Employee[][] copy = Arrays.stream(timetable).map(Employee[]::clone).toArray(Employee[][]::new);
				Employee temp = copy[violationPosition[0]][violationPosition[1]];
				copy[violationPosition[0]][violationPosition[1]] = copy[positionToSwitch[0]][positionToSwitch[1]];
				copy[positionToSwitch[0]][positionToSwitch[1]] = temp;
				Location lCopy = new Location(l.getLocationID(), l.getrank4Req(), l.getrank3Req(), l.getrank2Req(), l.getBoatDriversReq(), l.getCrewmenReq(), l.getJetSkiUsersReq());
				if (hm.locationScore(lCopy) < hm.locationScore(l)) {
					l = lCopy;
				}
			}
			
			iterationCount++;
		}
		return currentArea;
	}
	
	public Employee[][] getEmployeesFreeEachDay() {
		return null;
	}
	
	private void initialiseEmployeesRandomly() {
		int employeesPerDay = 6;
		int daysInWeek = 7;
		for (Location l : locations) {
			Collections.shuffle(employees);
			int index = 0;
			Employee[][] timetable = new Employee[employeesPerDay][daysInWeek];
			for (int i = 0; i <= timetable[0].length; i++) {
				for (int j = 0; j <= timetable.length; i++) {
					timetable[i][j] = employees.get(index++);
				}
			}
			l.setTimetable(timetable);
		}
	}

	
	//Hard constraint 
	// Finding the position of where the minimum number of employees with a rank is not met
	private int[] getRankOrQualViolationPosition(Location location) {
		//Start at random point loop through whole array using Modulo
		int employeesPerDay = 6;
		int daysInWeek = 7;
		int randomStartPointI = (int) Math.random() * employeesPerDay;
		int randomStartPointJ = (int) Math.random() * daysInWeek;
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
				Employee e = timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek];
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
			// If there are not enough employees of any rank 4,3,2 then return the position of a rank 1.
			if (rank4Count < location.getrank4Req() || rank3Count < location.getrank3Req() || rank2Count < location.getrank2Req()) {
				for (int j = 0;j<employeesPerDay;j++) {
					if (timetable[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek].getRank() == 1) {
						return new int[] {(j + randomStartPointJ) % employeesPerDay, (i + randomStartPointI) % daysInWeek};
					}
				}
			}
			// Need to return position to be switch to add boat drivers, crewmen and jet ski users
			if (boatDriverCount < location.getBoatDriversReq()) {
				
			}
			if (crewmenCount < location.getCrewmenReq()) {
				
			}
			if (jetSkiUsersCount < location.getJetSkiUsersReq()) {
				
			}
		}
		return null;
	}
	
	private int[] getDoubleBookedConstraintPosition(Area area) {
		Map<Employee, Integer> timetable = new HashMap<>();
		int employeesPerDay = 6;
		int daysInWeek = 7;
		int randomStartPointI = (int) Math.random() * employeesPerDay;
		int randomStartPointJ = (int) Math.random() * daysInWeek;
		for (int i = 0; i < area.getLocations().get(0).getTimetable().length; i++) {
			for (Location l : area.getLocations()) {
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					if (timetable.containsKey(table[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek])) {
						return new int[] {(j + randomStartPointJ) % employeesPerDay, (i + randomStartPointI) % daysInWeek};
					} else {
						timetable.put(table[(j + randomStartPointJ) % employeesPerDay][(i + randomStartPointI) % daysInWeek], 1);
					}
				}
			}
		}
		return null;
	}

//	private Employee getFreeEmployee(Location timetable, int day) {
//		Employee ret = null;
//		Employee[][] table = timetable.getTimetable();
//		Collections.shuffle(employees);
//		for (Employee e : employees) {
//			boolean freeOnDay = true;
//			for (int i = 0; i < table.length; i++) {
//				if (e.equals(table[i][day])) {
//					freeOnDay = false;
//				}
//			}
//			if (freeOnDay)
//				return e;
//		}
//		return ret;
//	}

//	public boolean employeesWorkingTooMuch(Location timetable) {
//		Employee[][] table = timetable.getTimetable();
//		Map<Employee, Integer> employeeCount = new HashMap<>();
//		for (int i = 0; i < table.length; i++) {
//			for (int j = 0; j < table[i].length; j++) {
//				Employee e = table[i][j];
//				if (employeeCount.containsKey(e) && employeeCount.get(e) >= 5) {
//					timetable.editEmployeeInTable(getFreeEmployee(timetable, j), i, j);
//					System.out.println(e.getName());
//					return true;
//				} else if (employeeCount.containsKey(e)) {
//					employeeCount.put(e, employeeCount.get(e) + 1);
//				} else {
//					employeeCount.put(e, 1);
//				}
//			}
//		}
//
//		return false;
//	}

}
