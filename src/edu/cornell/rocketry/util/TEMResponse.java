package edu.cornell.rocketry.util;

public class TEMResponse {
	
	private double latitude;
	private double longitude;
	private double altitude;
	private byte flag;
	
	private long time;
	private double rotation;
	private double acceleration;
	
	
	/**
	 * Create a TEM Response object with GPS time {@code t}
	 * @param lat latitude
	 * @param lon longitude
	 * @param alt altitude
	 * @param flag GPS flag
	 * @param t GPS time
	 * @param rot rotation
	 * @param acc acceleration
	 */
	public TEMResponse (double lat, double lon, double alt, byte flag, long t, double rot, double acc){
		latitude = lat;
		longitude = lon;
		altitude = alt;
		this.flag = flag;
		time = t;
		rotation = rot;
		acceleration = acc;
	}

	
	/**
	 * Create a TEM Response object with system time substituted for GPS  time
	 * @param lat latitude
	 * @param lon longitude
	 * @param alt altitude
	 * @param flag GPS flag
	 * @param rot rotation
	 * @param acc acceleration
	 */
	public TEMResponse (double lat, double lon, double alt, byte flag, double rot, double acc) {
		latitude = lat;
		longitude = lon;
		altitude = alt;
		this.flag = flag;
		time = System.currentTimeMillis();
		rotation = rot;
		acceleration = acc;
	}
	
	
	public double lat() { return latitude; }
	public double lon() { return longitude; }
	public double alt() { return altitude; }
	public byte flag() { return flag; }
	public long time() { return time; }
	public double rot() {return rotation;}
	public double acc() {return acceleration;}
	
	public Datum create_datum () {
		return new Datum (
			latitude, 
			longitude, 
			altitude, 
			time, 
			rotation, 
			acceleration);
	}
	
}
