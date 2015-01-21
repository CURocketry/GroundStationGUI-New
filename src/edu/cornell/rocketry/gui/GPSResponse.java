package edu.cornell.rocketry.gui;

public class GPSResponse {
	
	private double x_pos;
	private double y_pos;
	private double z_pos;
	
	private double time;
	
	/** 
	 * Create a GPS Response object without GPS time; system time substituted
	 * @param x
	 * @param y
	 * @param z
	 */
	public GPSResponse (double x, double y, double z) {
		x_pos = x;
		y_pos = y;
		z_pos = z;
		time = System.currentTimeMillis();
	}
	
	/**
	 * Create a GPS Response object with GPS time
	 * @param x
	 * @param y
	 * @param z
	 * @param t
	 */
	public GPSResponse (double x, double y, double z, long t) {
		x_pos = x;
		y_pos = y;
		z_pos = z;
		time = t;
	}
	
	public double x() { return x_pos; }
	public double y() { return y_pos; }
	public double z() { return z_pos; }
	public double time() { return time; }
	
}
