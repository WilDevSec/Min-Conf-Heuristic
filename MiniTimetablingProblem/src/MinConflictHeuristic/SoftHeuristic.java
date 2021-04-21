package MinConflictHeuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoftHeuristic {
	
	public SoftHeuristic() {

	}

	public int heuristicScore(Area area) {
		return partTimeWorking2DaysCount(area) + fullTimeWithoutConsecutiveDaysOffCount(area);
	}

	private int partTimeWorking2DaysCount(Area area) {
		Map<Employee, Integer> partTimeworkingDaysCount = new HashMap<>();
		int violationCount = 0;
		for (int i = 0; i < area.getLocations().get(0).getTimetable().length; i++) {
			for (Location l : area.getLocations()) {
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					Employee e = table[j][i];
					if (e.getFullTime()) {
						if (partTimeworkingDaysCount.containsKey(e)) {
							int value = partTimeworkingDaysCount.get(e);
							if (value == 3) {
								violationCount++;
							} else {
								partTimeworkingDaysCount.put(e, value);
							}
						} else {
							partTimeworkingDaysCount.put(e, 1);
						}
					}
				}
			}
		}
		return violationCount;
	}

	private int fullTimeWithoutConsecutiveDaysOffCount(Area area) {
		ArrayList<ArrayList<Employee>> fullTimeWorkingDays = new ArrayList<>();
		int violationCount = 0;
		for (int i = 0; i < area.getLocations().get(0).getTimetable().length; i++) {
			ArrayList<Employee> ftEmployeesOnDay = new ArrayList<>();
			for (Location l : area.getLocations()) {
				Employee[][] table = l.getTimetable();
				for (int j = 0; j < table.length; j++) {
					Employee e = table[j][i];
					if (e.getFullTime()) {
						ftEmployeesOnDay.add(e);
					}
					if (i > 1) {
						if (!fullTimeWorkingDays.get(i - 1).contains(e) && fullTimeWorkingDays.get(i - 2).contains(e)) {
							violationCount++;
						}
					}
				}
			}
			fullTimeWorkingDays.add(ftEmployeesOnDay);
		}
		return violationCount;
	}
}
