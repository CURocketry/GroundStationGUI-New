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
	
	public RocketModel () {
		cameraStatus = Status.DISABLED;
		gpsStatus = Status.DISABLED;
		initStatus = Status.DISABLED;
		launchStatus = Status.DISABLED;
		landedStatus = Status.DISABLED;
		
		rocketCurrent = null;
		rocketPast = new LinkedList<Datum>();
	}
	
	/*
	 * Camera Status
	 */
	public Status getCameraStatus () {
		return cameraStatus;
	}
	
	public void setCameraStatus (Status st) {
		cameraStatus = st;
	}
	
	/*
	 * GPS Status
	 */
	public Status getGPSStatus () {
		return gpsStatus;
	}
	
	public void setGPSStatus (Status st) {
		gpsStatus = st;
	}
	
	/*
	 * Launch Status
	 */
	public Status getLaunchStatus () {
		return launchStatus;
	}
	
	public void setLaunchStatus (Status st) {
		launchStatus = st;
	}
	
	/*
	 * Initialization Status
	 */
	public Status getInitStatus () {
		return initStatus;
	}
	
	public void setInitStatus (Status st) {
		initStatus = st;
	}
	
	/*
	 * Landed Status
	 */
	public Status getLandedStatus () {
		return landedStatus;
	}
	
	public void setLandedStatus (Status st) {
		landedStatus = st;
	}
	
	/*
	 * Data Retrieval
	 */
	public Position getCurrentPosition () {
		return rocketCurrent.pos();
	}
	
	public Datum getCurrentDatum () {
		return rocketCurrent;
	}
	
	public List<Datum> getPastRocketData(){
		return rocketPast;
	}
	
	/*
	 * Update model
	 */
	/**
	 * Adds the given {@link Datum} to this {@link RocketModel}.
	 * @param d represents new information to be stored about the Rocket
	 */
	public void update (Datum d) {
		rocketPast.add(d);
		rocketCurrent = d;
	}
	
}