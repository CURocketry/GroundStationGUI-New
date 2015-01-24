package edu.cornell.rocketry.gui;

import java.util.LinkedList;

import edu.cornell.rocketry.util.Position;
import edu.cornell.rocketry.util.PayloadStatus;


public class Model {
	
	private PayloadStatus pl_status;
	private PayloadStatus sq_status;
	private Position rocket_pos;
	private LinkedList<Position> rocket_past_pos;
	
	
	public Model () {
		pl_status = PayloadStatus.Disabled;
		sq_status = PayloadStatus.Disabled;
		rocket_pos = new Position (0,0,0, 0); //there might be better stub values...
		rocket_past_pos = new LinkedList<Position>();
	}
	
	public PayloadStatus payload() {
		return pl_status;
	}
	
	public void setPayload (PayloadStatus st) {
		pl_status = st;
	}
	
	public PayloadStatus sequence() {
		return sq_status;
	}
	
	public void setSequence(PayloadStatus st) {
		sq_status = st;
	}
	
	public Position position() {
		return rocket_pos;
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
}