package MinConflictHeuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HeuristicMeasure {

	public HeuristicMeasure() {

	}

	public int heuristicScore(Area area) {
		int count = 0;
		for (Location l : area.getLocations()) {
			count += rankAndQualificationMissing(l);
			System.out.println("Rank and qual for a location:" + rankAndQualificationMissing(l));
		}
		count += employeesDoubleBookedCount(area);
		System.out.println("Empl double booked: " + employeesDoubleBookedCount(area));
		count += fullTimeEmployeesNotWorking5Days(area);
		System.out.println("Full time: " + fullTimeEmployeesNotWorking5Days(area));
		System.out.println("Over all count: " + count);
		return count;
	}
	
	public int locationScore(Location location) {
		return rankAndQualificationMissing(location); 
	}

	private int rankAndQualificationMissing(Location location) {
		Employee[][] employees = location.getTimetable();
		int score = 0;
		for (int i = 0; i < employees[0].length; i++) {
			int rank4Count = 0;
			int rank3Count = 0;
			int rank2Count = 0;
			int boatDriverCount = 0;
			int crewmenCount = 0;
			int jetSkiUsersCount = 0;
			for (int j = 0; j < employees.length; j++) {
				Employee e = employees[j][i];
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
			score += rank4Count < location.getrank4Req() ? location.getrank4Req() - rank4Count : 0;
			score += rank3Count < location.getrank3Req() ? location.getrank3Req() - rank3Count : 0;
			score += rank2Count < location.getrank2Req() ? location.getrank2Req() - rank2Count : 0;
			score += boatDriverCount < location.getBoatDriversReq() ? location.getBoatDriversReq() - boatDriverCount
					: 0;
			score += crewmenCount < location.getCrewmenReq() ? location.getCrewmenReq() - crewmenCount : 0;
			score += jetSkiUsersCount < location.getJetSkiUsersReq() ? location.getJetSkiUsersReq() - jetSkiUsersCount
					: 0;
		}
		return score;
	}
	

	private int employeesDoubleBookedCount(Area area) {
		int count = 0;
		for (int i = 0; i < area.getLocations().get(0).getTimetable()[0].length; i++) {
			List<Employee> employeeCount = new ArrayList<>();
			for (Location l : area.getLocations()) {
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					if (employeeCount.contains(table[j][i])) {
						count++;
					} else {
						employeeCount.add(table[j][i]);
					}
				}
			}
		}
		return count;
	}

	private int fullTimeEmployeesNotWorking5Days(Area area) {
		int count = 0;
		Map<Employee, Integer> employeeCount = new HashMap<>();
		for (int i = 0; i < area.getLocations().get(0).getTimetable()[0].length; i++) {
			// Need second hash to make sure employees aren't counted as working
			// multiple days when they are working twice on the same day
			List<Employee> employeePerDay = new ArrayList<>();
			for (int k = 0; k < 5; k++) {
				Location l = area.getLocations().get(k);
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					if (!employeePerDay.contains(table[j][i])) {
						if (employeeCount.containsKey(table[j][i])) {
							int value = employeeCount.get(table[j][i]);
							if (value == 5) {
								count++;
							} else {
								value += 1;
								employeeCount.put(table[j][i], value);
							}
						} else {
							employeeCount.put(table[j][i], 1);
						}
						employeePerDay.add(table[j][i]);
					}
				}
			}
		}
		return count;
	}

	private int ranksOfEmployeesMissing(Location location) {
		Employee[][] employees = location.getTimetable();
		int sum = 0;
		for (int i = 0; i < employees[0].length; i++) {
			Map<Integer, Integer> employeesRanksCount = new HashMap<>();
			for (int j = 0; j < employees.length; j++) {
				// J first because looping vertically first (through employees each day)
				Employee emp = employees[j][i];
				int employeeRank = emp.getRank();
				if (!employeesRanksCount.containsKey(employeeRank)) {
					employeesRanksCount.put(employeeRank, 1);
				} else {
					employeesRanksCount.put(employeeRank, employeesRanksCount.get(employeeRank) + 1);
				}
			}
			int rank4Count = employeesRanksCount.get(4);
			if (rank4Count < location.getrank4Req()) {
				sum += location.getrank4Req() - rank4Count;
			}
			int rank3Count = employeesRanksCount.get(3);
			if (rank3Count < location.getrank3Req()) {
				sum += location.getrank3Req() - rank3Count;
			}
			int rank2Count = employeesRanksCount.get(2);
			if (rank2Count < location.getrank2Req()) {
				sum += location.getrank2Req() - rank2Count;
			}
		}
		return sum;
	}

	private int qualificationsMissing(Location location) {
		Employee[][] employees = location.getTimetable();
		int sum = 0;
		for (int i = 0; i < employees[0].length; i++) {
			Map<String, Integer> employeeQualificationsCount = new HashMap<>();
			for (int j = 0; j < employees.length; j++) {
				// J first because looping vertically first (through employees each day)
				Employee emp = employees[j][i];
				if (emp.isBoatDriver()) {
					if (!employeeQualificationsCount.containsKey("boatDrivers")) {
						employeeQualificationsCount.put("boatDrivers", 1);
					} else {
						employeeQualificationsCount.put("boatDrivers",
								employeeQualificationsCount.get("boatDrivers") + 1);
					}
				}
				if (emp.isBoatCrewman()) {
					if (!employeeQualificationsCount.containsKey("boatCrewmen")) {
						employeeQualificationsCount.put("boatCrewmen", 1);
					} else {
						employeeQualificationsCount.put("boatCrewmen",
								employeeQualificationsCount.get("boatCrewmen") + 1);
					}
				}
				if (emp.isjetSkiUser()) {
					if (!employeeQualificationsCount.containsKey("jetSkiUsers")) {
						employeeQualificationsCount.put("jetSkiUsers", 1);
					} else {
						employeeQualificationsCount.put("jetSkiUsers",
								employeeQualificationsCount.get("jetSkiUsers") + 1);
					}
				}
			}
			int boatDriversCount = employeeQualificationsCount.get("boatDrivers");
			sum += boatDriversCount < location.getBoatDriversReq() ? location.getBoatDriversReq() - boatDriversCount
					: 0;

			int boatCrewmenCount = employeeQualificationsCount.get("boatCrewmen");
			sum += boatCrewmenCount < location.getCrewmenReq() ? location.getCrewmenReq() - boatCrewmenCount : 0;

			int jetSkiUsersCount = employeeQualificationsCount.get("jetSkiUsers");
			sum += jetSkiUsersCount < location.getJetSkiUsersReq() ? location.getJetSkiUsersReq() - jetSkiUsersCount
					: 0;
		}
		return sum;
	}

}
