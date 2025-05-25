package Infrastructure;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LoggerUtil {
	
	private static final String LOG_FILE = "TestResult/Result-log.txt";
	
	/**
	 * log function Appends a log message to the log file.
	 * <p>
	 * Opens the log file in append mode and writes the provided message followed by a new line.
	 * @param message The log message to be written to the file.
	 */
	
	public static void log(String message) {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE,true))){
			writer.write(message);
			writer.newLine();
		} catch(IOException e) {
			System.err.println("Failed to write log:" + e.getMessage());
		}
		
	}

}
