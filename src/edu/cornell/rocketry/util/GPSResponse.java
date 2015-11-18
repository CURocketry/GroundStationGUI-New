package edu.cornell.rocketry.util;

public class GPSResponse {
	
	private double latitude;
	private double longitude;
	private double altitude;
	private byte flag;
	
	private long time;
	private double rotation;
	private double acceleration;
	
	/** 
	 * Create a GPS Response object without GPS time; system time substituted
	 * @param x
	 * @param y
	 * @param z
	 */
	public GPSResponse (double lat, double lon, double alt, byte flag) {
		latitude = lat;
		longitude = lon;
		altitude = alt;
		this.flag = flag;
		time = System.currentTimeMillis();
	}
	
	/**
	 * Create a GPS Response object with GPS time
	 * @param x
	 * @param y
	 * @param z
	 * @param t
	 */
	public GPSResponse (double lat, double lon, double alt, byte flag, long t) {
		latitude = lat;
		longitude = lon;
		altitude = alt;
		this.flag = flag;
		time = t;
	}
	
	public GPSResponse (double lat, double lon, double alt, byte flag, long t, double rot, double acc){
		latitude = lat;
		longitude = lon;
		altitude = alt;
		this.flag = flag;
		time = t;
		rotation = rot;
		acceleration = acc;
	}
	
	
	public double lat() { return latitude; }
	public double lon() { return longitude; }
	public double alt() { return altitude; }
	public byte flag() { return flag; }
	public long time() { return time; }
	public double getRot() {return rotation;}
	public double getAcc() {return acceleration;}
	
}
