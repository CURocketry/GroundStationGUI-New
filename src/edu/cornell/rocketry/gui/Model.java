package edu.cornell.rocketry.gui;

import java.util.LinkedList;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;

import edu.cornell.rocketry.util.Position;
import edu.cornell.rocketry.util.PayloadStatus;
import edu.cornell.rocketry.xbee.XBeeListenerThread;


public class Model {
	
	private PayloadStatus pl_status;
	private PayloadStatus prev_stable_pl_status;
	private Position rocket_pos;
	private LinkedList<Position> rocket_past_pos;
	
	//XBee tab variables
	public XBee xbee = new XBee(); //keep as public reference @see XBeeListenerThread.java
	protected XBeeListenerThread xbeeListener;
	protected XBeeAddress64 selectedAddress;				//selected address
	protected int selectedBaud = 57600; //serial comm rate
	
	
	public Model () {
		pl_status = PayloadStatus.Disabled;
		prev_stable_pl_status = PayloadStatus.Disabled;
		rocket_pos = new Position (0,0,0, 0); //there might be better stub values...
		rocket_past_pos = new LinkedList<Position>();
	}
	
	public PayloadStatus payload () {
		return pl_status;
	}
	
	public PayloadStatus prevPayload () {
		return prev_stable_pl_status;
	}
	
	public void setPayload (PayloadStatus st) {
		//make sure prev_stable_pl_status is never Busy - that's not a stable state!
		if (pl_status != PayloadStatus.Busy) {
			prev_stable_pl_status = pl_status;
		}
		pl_status = st;
	}
	
	public Position position () {
		return rocket_pos;
	}
	
	public LinkedList<Position> getPastRocketPositions(){
		return rocket_past_pos;
	}
	
	public void updatePosition(double x, double y, double z) {
		Position p = new Position(x,y,z, System.currentTimeMillis());
		rocket_past_pos.add(p);
		rocket_pos = p;
	}
	
	public void updatePosition (double x, double y, double z, long t) {
		Position p = new Position (x,y,z,t);
		rocket_past_pos.add(p);
		rocket_pos = p;
	}
	
	public XBee xbee() {
		return xbee;
	}
	
	public XBeeAddress64 address() {
		return selectedAddress;
	}
}