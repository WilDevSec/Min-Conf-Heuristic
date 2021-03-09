package Dataset;

import java.util.ArrayList;
import java.util.List;

public class LocationsGenerator {

	// Employee ID -- Required rank 4 -- Required rank 3 -- Required rank 2 --
	// Required Boat drivers -- Required Crew mates -- Required Jet Ski users
	public void generateLocations() {
		List<String> locations = new ArrayList<String>();
		for (int i = 0; i <= 1500; i++) {
			// Location ID
			String location = String.format("%04d", i);
			// Required rank 4. Always 1 as every location needs just 1 senior employee/
			// rank 4 employee.
			location += "," + 1;
			// Required rank 3, always 0-2
			location += "," + (int) (Math.random() * 2 + 1);
			// Required rank 2, always 0-2
			location += "," + (int) (Math.random() * 2 + 1);
			// Required boat driver, never need more than one as beaches only have one boat
			location += "," + (Math.random() > 0.7 ? 1 : 0);
			// Required boat crew man, somewhere between 0 & 2. IRL depends on whether there
			// is a boat and how many employees are on rotation there
			location += "," + (int) (Math.random() * 2 + 1);
			// Required Jet Ski users, usually 0 but can be 1 or 2
			location += "," + (Math.random() > 0.7 ? (int) (Math.random() * 2 + 1) : 0);
			locations.add(location);
		}
		CSVIO.writeCSV(locations, "/home/will/Studies/Diss/Dataset/locationList.csv");
	}
}
