package edu.cornell.rocketry.gui;

import java.util.LinkedList;
import java.util.List;

import edu.cornell.rocketry.util.GPSStatus;
import edu.cornell.rocketry.util.Position;
import edu.cornell.rocketry.util.CameraStatus;
import edu.cornell.rocketry.util.Datum;


public class Model {
	
	private CameraStatus cm_status;
	private CameraStatus prev_stable_cm_status;
	private GPSStatus gps_status;
	private Datum rocket_curr;
	private LinkedList<Datum> rocket_past;
	
	//XBee tab variables
	//public XBee xbee = new XBee(); //keep as public reference @see XBeeListenerThread.java
	//protected XBeeAddress64 selectedAddress;				//selected address
	//protected int selectedBaud = 57600; //serial comm rate
	
	
	public Model () {
		cm_status = CameraStatus.Disabled;
		prev_stable_cm_status = CameraStatus.Disabled;
		gps_status = GPSStatus.NoFix;
		rocket_curr = null;
		rocket_past = new LinkedList<Datum>();
	}
	
	public CameraStatus camera () {
		return cm_status;
	}
	
	public CameraStatus prevPayload () {
		return prev_stable_cm_status;
	}
	
	public void setPayload (CameraStatus st) {
		//make sure prev_stable_cm_status is never Busy - that's not a stable state!
		if (cm_status != CameraStatus.Busy) {
			prev_stable_cm_status = cm_status;
		}
		cm_status = st;
	}
	
	public GPSStatus gps () {
		return gps_status;
	}
	
	public void setGPS (GPSStatus st) {
		gps_status = st;
	}
	
	public Position current_position () {
		return rocket_curr.pos();
	}
	
	public Datum current_datum () {
		return rocket_curr;
	}
	
	public List<Datum> getPastRocketData(){
		return rocket_past;
	}
	
	public void update (double x, double y, double z, long t, long a, long r) {
		Position p = new Position(x,y,z);
		Datum d = new Datum(p,t,r,a);
		rocket_past.add(d);
		rocket_curr = d;
	}
	
	public void update (Datum d) {
		rocket_past.add(d);
		rocket_curr = d;
	}
	
}