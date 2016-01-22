package edu.cornell.rocketry.util;

import java.util.concurrent.TimeUnit;

public class Datum {
	private Position position;
	
	private long time;
	private double rotation;
	private double acceleration;

	public Datum (Position p, long t, double rot, double acc) {
		position = p;
		time = t;
		rotation = rot;
		acceleration = acc;
	}
	
	public Datum (double lat, double lon, double alt, long t, double rot, double acc) {
		position = new Position(lat, lon, alt);
		time = t;
		rotation = rot;
		acceleration = acc;
	}
	
	
	public double lat() { return position.lat(); }
	public double lon() { return position.lon(); }
	public double alt() { return position.alt(); }
	public Position pos() { return position; }
	public long time() { return time; }
	public double rot() { return rotation; }
	public double acc() { return acceleration; }
	
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