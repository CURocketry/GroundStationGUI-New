package edu.cornell.rocketry.gui.model;

import java.util.concurrent.TimeUnit;

import edu.cornell.rocketry.util.ErrorLogger;

public class Position {
	private double latitude;
	private double longitude;
	private int altitude;
	
	public Position (double lat, double lon, int alt) {
		latitude = lat;
		longitude = lon;
		altitude = alt;
		bounds_check();
	}
	
	public double lat() { return latitude; }
	public double lon() { return longitude; }
	public int alt() { return altitude; }
	
	private void bounds_check () {
		if (latitude > 90) {
			ErrorLogger.warn
				("edu.cornell.rocketry.util.Position#bounds_check failed: latitude = " 
					+ latitude + ", greater than 90");
			latitude = 90;
		}
		if (latitude < -90) {
			ErrorLogger.warn
			("edu.cornell.rocketry.util.Position#bounds_check failed: latitude = " 
				+ latitude + ", less than -90");
			latitude = -90;
		}
		if (longitude > 180) {
			ErrorLogger.warn
			("edu.cornell.rocketry.util.Position#bounds_check failed: longitude = " 
				+ longitude + ", greater than 180");
			longitude = 180;
		}
		if (longitude < -180) {
			ErrorLogger.warn("edu.cornell.rocketry.util.Position#bounds_check failed: longitude = " 
				+ longitude + ", less than -180");
			longitude = -180;
		}
	}
	
	public static String millisToTime(long millis) {
		return
			String.format("%02d:%02d:%02d", 
			    (TimeUnit.MILLISECONDS.toHours(millis) + 7) % 12,
			    TimeUnit.MILLISECONDS.toMinutes(millis) - 
			    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
			    TimeUnit.MILLISECONDS.toSeconds(millis) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		//http://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
		
	}
	
	public String toString () {
		return "Position[lat:" + lat() + ", lon:" + lon() + ", alt:" + alt() + "]";
	}
}