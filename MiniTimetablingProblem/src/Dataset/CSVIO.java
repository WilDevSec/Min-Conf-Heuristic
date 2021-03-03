package Dataset;

import java.io.File;
import java.io.FileNotFoundException;
//import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVIO {

	/**
	 * Reads the CSV file, and turns each unit into a string, and adding to an array
	 * 
	 * @param fileLocation
	 * @return lineList
	 */
	public static ArrayList<String> readCSV(String fileLocation) {
		ArrayList<String> lineList = new ArrayList<>();
		Scanner sc = null;
		try {
			sc = new Scanner(new File(fileLocation));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (sc.hasNextLine()) {
			String nextLine = sc.nextLine();
			if (!(nextLine.equals(""))) {
				lineList.add(nextLine);
			}
		}
		sc.close();

		return lineList;

	}

	/**
	 * Takes the array of data and adds it to CSV file at given location.
	 * 
	 * @param data
	 * @param fileLocation
	 */
	public static void writeCSV(List<String> data, String fileLocation) {
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(fileLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (String line : data) {
			printWriter.println(line);
		}
		printWriter.close();
	}
}