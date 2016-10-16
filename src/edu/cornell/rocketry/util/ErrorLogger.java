package edu.cornell.rocketry.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * a collection of functions that writes status updates (errors, warnings, debugs, etc.) 
 * to a log file at GroundStationGUI-New/log/error.log
 *
 */
public final class ErrorLogger {
	/** hide the constructor */
	private ErrorLogger (){
	}
	
	private static final String FILEPATH =
			System.getProperty("user.dir") + File.separator + "log" + File.separator + "errorlog.log";
	
	private static void log (String msg, LoggerLevel level) {
		String s;
		
		String timeStamp = 
			new SimpleDateFormat("MM/dd HH:mm:ss", Locale.US).format(new Date());
		
		switch (level) {
		case DEBUG:
			s = "[ DEBUG ] [ " + timeStamp + " ] " + msg;
			break;
		case INFO:
			s = "[ INFO  ] [ " + timeStamp + " ] " + msg;
			break;
		case WARNING:
			s = "[ WARN  ] [ " + timeStamp + " ] " + msg;
			break;
		case ERROR:
			s = "[ ERROR ] [ " + timeStamp + " ] " + msg;
			break;
		case FATAL:
			s = "[ FATAL ] [ " + timeStamp + " ] " + msg;
			break;
		default:
			s = "[ ????? ] [ " + timeStamp + " ] " + msg;
		}
		
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(FILEPATH), true));
			w.write(s + "\n");
			w.close();
		} catch (Exception e) {
			System.err.println("edu.cornell.rocketry.util.Logger#log(..) failed with exception: " 
				+ e.toString() + " when trying to write to file '" + FILEPATH + "'");
		}
	}
	
	public static void info (String msg) {
		log (msg, LoggerLevel.INFO);
	}
	
	public static void debug (String msg) {
		log (msg, LoggerLevel.DEBUG);
	}
	
	public static void warn (String msg) {
		log (msg, LoggerLevel.WARNING);
	}
	
	public static void err (String msg) {
		log (msg, LoggerLevel.ERROR);
	}
	
	public static void fatal (String msg) {
		log (msg, LoggerLevel.FATAL);
	}
	
	public static void main (String[] args) {
		String msg = "this is a test message";
		
		debug(msg);
		info(msg);
		warn(msg);
		err(msg);
		fatal(msg);
		
		
	}

}
