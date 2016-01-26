package edu.cornell.rocketry.gui.model;

import java.util.LinkedList;
import java.util.List;

import edu.cornell.rocketry.util.Status;


public class RocketModel {
	
	private Status cameraStatus;
	private Status initStatus;
	private Status launchStatus;
	private Status landedStatus;
	private Status gpsStatus;
	private Datum rocketCurrent;
	private LinkedList<Datum> rocketPast;
	
	//XBee tab variables
	//public XBee xbee = new XBee(); //keep as public reference @see XBeeListenerThread.java
	//protected XBeeAddress64 selectedAddress;				//selected address
	//protected int selectedBaud = 57600; //serial comm rate
	
	
	public RocketModel () {
		cameraStatus = Status.DISABLED;
		gpsStatus = Status.DISABLED;
		rocketCurrent = null;
		rocketPast = new LinkedList<Datum>();
	}
	
	public Status cameraStatus () {
		return cameraStatus;
	}
	
	public void setCameraStatus (Status st) {
		cameraStatus = st;
	}
	
	public Status gpsStatus () {
		return gpsStatus;
	}
	
	public void setGPSStatus (Status st) {
		gpsStatus = st;
	}
	
	public Status launchStatus () {
		return launchStatus;
	}
	
	public void setLaunchStatus (Status st) {
		launchStatus = st;
	}
	
	public Status initStatus () {
		return initStatus;
	}
	
	public Status landedStatus () {
		return landedStatus;
	}
	
	public void setLandedStatus (Status st) {
		landedStatus = st;
	}
	
	public Position currentPosition () {
		return rocketCurrent.pos();
	}
	
	public Datum currentDatum () {
		return rocketCurrent;
	}
	
	public List<Datum> pastRocketData(){
		return rocketPast;
	}
	
	public void update (double x, double y, double z, long tm, double a_x, double a_y, double a_z, double r, double tp) {
		Position p = new Position(x,y,z);
		Datum d = new Datum(p,tm,r,a_x,a_y,a_z,tp);
		rocketPast.add(d);
		rocketCurrent = d;
	}
	
	public void update (Datum d) {
		rocketPast.add(d);
		rocketCurrent = d;
	}
	
}