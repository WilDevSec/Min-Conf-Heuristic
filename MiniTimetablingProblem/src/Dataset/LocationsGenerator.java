package Dataset;

import java.util.ArrayList;
import java.util.List;

public class LocationsGenerator {

	// Employee ID -- Required rank 4 -- Required rank 3 --  Required rank 2 -- Required Boat driver --  Required Crew mate -- Required Jet Ski
	public void generateLocations() {
		List<String> locations = new ArrayList<String>();
		for (int i = 0; i <= 1500; i++) {
			String location = String.format("%04d", i);
			// Required rank 4
			location += "," + 1;
			// Required rank 3
			location += "," + (int) (Math.random() * 2 + 1);
			// Required rank 2
			location += "," + (int) (Math.random() * 2 + 1);
			// Required boat driver, never need more than one as beaches only have one boat
			location += "," + (Math.random() > 0.7 ? 1 : 0);
			// Required boat crew man
			location += "," + (int) (Math.random() * 2 + 1);
			// Required Jet Ski users, usually 0 but can be 1 or 2
			location += "," + (Math.random() > 0.7 ? (int) (Math.random() * 2 + 1) : 0);
			locations.add(location);
		}
		CSVIO.writeCSV(locations, "/home/will/Studies/Diss/Dataset/locationList.csv");
	}
}
