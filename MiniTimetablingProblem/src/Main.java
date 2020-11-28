
public class Main {

	public static void main(String[] args) {
		TimetableGenerator tg = new TimetableGenerator();
		Timetable table = tg.createTable();
		Employee[][] showEmployees = table.getTable();

		
		System.out.println("Initial permutation: ");
		System.out.println("Mon     Tue     Wed     Thu     Fri     Sat     Sun");
		System.out.println("--      --      --      --      --      --      -- ");
		for (int i = 0; i < showEmployees.length; i++) {
			for (int j = 0; j < showEmployees[i].length; j++) {
				System.out.print(showEmployees[i][j].getName() + "     ");
			}
			System.out.println();
		}
		System.out.println();

		Solver s = new Solver();
		while (s.employeesDoubleBooked(table)) {
			System.out.println("Mon     Tue     Wed     Thu     Fri     Sat     Sun");
			System.out.println("--      --      --      --      --      --      -- ");
			for (int i = 0; i < showEmployees.length; i++) {
				for (int j = 0; j < showEmployees[i].length; j++) {
					System.out.print(showEmployees[i][j].getName() + "     ");
				}
				System.out.println();
			}
			System.out.println();
		}
		
		System.out.println("Changed double booked employees, now checking for people working more than 5 days");
		while (s.employeesWorkingTooMuch(table)) {
			System.out.println("Mon     Tue     Wed     Thu     Fri     Sat     Sun");
			System.out.println("--      --      --      --      --      --      -- ");
			for (int i = 0; i < showEmployees.length; i++) {
				for (int j = 0; j < showEmployees[i].length; j++) {
					System.out.print(showEmployees[i][j].getName() + "     ");
				}
				System.out.println();
			}
			System.out.println();
		}
		System.out.println("Sorted: ");
		sortEmployees(showEmployees);
		System.out.println("Mon     Tue     Wed     Thu     Fri     Sat     Sun");
		System.out.println("--      --      --      --      --      --      -- ");
		for (int i = 0; i < showEmployees.length; i++) {
			for (int j = 0; j < showEmployees[i].length; j++) {
				System.out.print(showEmployees[i][j].getName() + "     ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Done");

	}

	private static void sortEmployees(Employee[][] showEmployees) {
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int i = 0; i < showEmployees[0].length; i++) {
				for (int j = 0; j < showEmployees.length -1; j++) {
					if (showEmployees[j][i].getName().substring(0, 1).compareTo(showEmployees[j+1][i].getName().substring(0, 1)) > 0) {
						Employee temp = showEmployees[j][i];
						showEmployees[j][i] = showEmployees[j+1][i];
						showEmployees[j+1][i] = temp;
						sorted = false;
					}
				}
			}
		}
		
	}
}
