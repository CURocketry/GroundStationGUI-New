package edu.cornell.rocketry.util;

public class TEMResponse {
	
	private double latitude;
	private double longitude;
	private double altitude;
	private StatusFlag flag;
	
	private long time;
	private double rotation;
	private double acceleration_x;
	private double acceleration_y;
	private double acceleration_z;
	private double temperature;

	
	/**
	 * Create a TEM Response object
	 * @param lat latitude
	 * @param lon longitude
	 * @param alt altitude
	 * @param flag status flags
	 * @param rot rotation
	 * @param acc acceleration
	 */
	public TEMResponse 
			(double lat, 
			double lon, 
			double alt, 
			byte flag, 
			double rot, 
			double acc_x,
			double acc_y,
			double acc_z,
			double temp) {
		this.latitude = lat;
		this.longitude = lon;
		this.altitude = alt;
		this.flag = new StatusFlag(flag);
		this.time = System.currentTimeMillis();
		this.rotation = rot;
		this.acceleration_x = acc_x;
		this.acceleration_y = acc_y;
		this.acceleration_z = acc_z;
		temperature = temp;
	}
	
	
	public double lat()      { return latitude; }
	public double lon()      { return longitude; }
	public double alt()      { return altitude; }
	public StatusFlag flag() { return flag; }
	public long time()       { return time; }
	public double rot()      { return rotation; }
	public double acc_x()    { return acceleration_x; }
	public double acc_y ()   { return acceleration_y; }
	public double acc_z ()   { return acceleration_z; }
	public double temp ()    { return temperature; } 
	
	public Datum create_datum () {
		return new Datum (
			latitude, 
			longitude, 
			altitude, 
			time, 
			rotation, 
			acceleration_x,
			acceleration_y,
			acceleration_z,
			temperature);
	}
	
}
