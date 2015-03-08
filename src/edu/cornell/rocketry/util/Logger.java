package edu.cornell.rocketry.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Logger {
	
	PrintWriter writer;
	
	
	public Logger () {
		try {
			writer = new PrintWriter("gps_log_file_" + String.valueOf(System.currentTimeMillis()) + "ms.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void log (String s) {
		System.out.println("Logging: " + s);
		writer.println(s);
		if (writer.checkError()) System.out.println("WRITER ERROR");
	}
	
	public void close () {
		writer.close();
	}
	
	

}
