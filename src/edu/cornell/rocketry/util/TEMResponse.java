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
	 * @param flag status flags
	 * @param t GPS time
	 * @param rot rotation
	 * @param acc acceleration
	 */
	public TEMResponse (double lat, double lon, double alt, byte flag, long t, double rot, double acc){
		this.latitude = lat;
		this.longitude = lon;
		this.altitude = alt;
		this.flag = flag;
		this.time = t;
		this.rotation = rot;
		this.acceleration = acc;
	}

	
	/**
	 * Create a TEM Response object with system time substituted for GPS  time
	 * @param lat latitude
	 * @param lon longitude
	 * @param alt altitude
	 * @param flag status flags
	 * @param rot rotation
	 * @param acc acceleration
	 */
	public TEMResponse (double lat, double lon, double alt, byte flag, double rot, double acc) {
		this.latitude = lat;
		this.longitude = lon;
		this.altitude = alt;
		this.flag = flag;
		this.time = System.currentTimeMillis();
		this.rotation = rot;
		this.acceleration = acc;
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
