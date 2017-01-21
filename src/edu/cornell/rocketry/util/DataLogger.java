package edu.cornell.rocketry.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * a class that allows logging to a file at GroundStationGUI-New/log/tracer_log_XXX.csv
 * where XXX is the current time in milliseconds since Jan. 1 1970. 
 *
 */
public class DataLogger {
	
	private PrintWriter writer;
	private String filename;
	private boolean writtenTo = false;
	private boolean closed = false;
	
	public DataLogger () {
		try {
			Path thePath = Paths.get("log", "tracer_log_" + System.currentTimeMillis() + ".csv");
			filename = thePath.toString();
			writer = new PrintWriter(thePath.toFile(), "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			String msg = 
				"edu.cornell.rocketry.util.DataLogger#init() failed to "
					+ "create new log file, exception: " + e.toString();
			System.err.println(msg);
			e.printStackTrace();
			ErrorLogger.err(msg);
		}
	}
	
	public void logHeader (String s) {
		if (closed) return;
		writer.println(s);
		if (writer.checkError()) 
			ErrorLogger.err("edu.cornell.rocketry.util.DataLogger#logHeader(..) "
				+ "failed to write to log file.");
	}
	
	public void log (String s) {
		writtenTo = true;
		logHeader(s);
	}
	
	public void close () {
		if (closed) {
			ErrorLogger.warn("edu.cornell.rocketry.util.DataLogger#close: "
				+ "already closed");			
			return;
		}
		closed = true;
		writer.close();
		if (!writtenTo) {
			System.out.println("not written to, attempting to delete");
			boolean success = new File(filename).delete();
			if (!success) {
				ErrorLogger.err("edu.cornell.rocketry.util.DataLogger#close() "
					+ "failed to delete unused log file");
			} else {
				ErrorLogger.info("edu.cornell.rocketry.util.DataLogger#close() "
					+ "deleted unused log file '" + filename + "'");
			}
			System.out.println("deletion successful: " + success);
		}
	}

}
