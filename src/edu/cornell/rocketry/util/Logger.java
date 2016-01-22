package edu.cornell.rocketry.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Logger {
	
	private static final String FILEPATH = System.getProperty("user.dir") + "/log/errorlog.log";
	
	private static void log (String msg, LoggerLevel level) {
		String s;
		
		switch (level) {
		case DEBUG:
			s = "[ DEBUG ] " + msg;
			break;
		case INFO:
			s = "[ INFO  ] " + msg;
			break;
		case WARNING:
			s = "[ WARN  ] " + msg;
			break;
		case ERROR:
			s = "[ ERROR ] " + msg;
			break;
		case FATAL:
			s = "[ FATAL ] " + msg;
			break;
		default:
			s = "[ ????? ] " + msg;
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
