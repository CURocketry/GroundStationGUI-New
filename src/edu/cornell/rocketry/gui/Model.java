package edu.cornell.rocketry.gui;

import java.util.LinkedList;

public class Model {
	
	private Status pl_status;
	private Status sq_status;
	private Position rocket_pos;
	private LinkedList<Position> rocket_past_pos;
	
	public static Model model = new Model();
	
	private Model () {
		pl_status = Status.Disabled;
		sq_status = Status.Disabled;
		rocket_pos = new Position (0,0,0); //there might be better stub values...
		rocket_past_pos = new LinkedList<Position>();
	}
	
	public Status payload() {
		return pl_status;
	}
	
	public void payload(Status st) {
		pl_status = st;
	}
	
	public Status sequence() {
		return sq_status;
	}
	
	public void sequence(Status st) {
		sq_status = st;
	}
	
	public Position position() {
		return rocket_pos;
	}
	
	public void updatePosition(double x, double y, double z) {
		Position p = new Position(x,y,z);
		rocket_past_pos.add(p);
		rocket_pos = p;
	}
	
	
	
	/* ***** Supporting Datatypes ***** */
	
	enum Status {
		Enabled,
		Trouble,
		Error,
		Disabled
	}
	
	public class Position {
		private double x_pos;
		private double y_pos;
		private double z_pos;
		
		public Position (double x, double y) {
			x_pos = x;
			y_pos = y;
			bounds_check();
		}
		
		public Position (double x, double y, double z) {
			x_pos = x;
			y_pos = y;
			z_pos = z;
			bounds_check();
		}
		
		
		public double x() { return x_pos; }
		public double y() { return y_pos; }
		public double z() { return z_pos; }
		
		private void bounds_check () {
			/*
			if (true) //conditions: TODO
				System.err.println("Rocket placed at invalid position");
			*/
		}
	}
}
