package edu.cornell.rocketry.util;

import java.util.concurrent.TimeUnit;

public class Position {
	private double latitude;
	private double longitude;
	private double altitude;
	
	private long time;
	private double rotation;
	private double acceleration;
	
	public Position (double lat, double lon, double alt, long t) {
		latitude = lat;
		longitude = lon;
		altitude = alt;
		time = t;
		bounds_check();
	}
	
	public Position (double lat, double lon, double alt, long t, double rot, double acc) {
		latitude = lat;
		longitude = lon;
		altitude = alt;
		time = t;
		rotation = rot;
		acceleration = acc;
		bounds_check();
	}
	
	
	public double lat() { return latitude; }
	public double lon() { return longitude; }
	public double alt() { return altitude; }
	public long time() { return time; }
	public double rot() { return rotation; }
	public double acc() { return acceleration; }
	
	private void bounds_check () {
		/*
		if (true) //conditions: TODO
			System.err.println("Rocket placed at invalid position");
		*/
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
}