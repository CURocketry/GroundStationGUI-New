package edu.cornell.rocketry.util;

public class GPSResponse {
	
	private double latitude;
	private double longitude;
	private double altitude;
	private byte flag;
	
	private long time;
	
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
	
	public double lat() { return latitude; }
	public double lon() { return longitude; }
	public double alt() { return altitude; }
	public byte flag() { return flag; }
	public long time() { return time; }
	
}
