package edu.cornell.rocketry.comm.send;

/** the type of command to be sent to the rocket from the ground station */

public enum CommandType {
	LAUNCH, CANCEL;
	
	public String toString(){
		switch(this){
		case LAUNCH:
			return "LAUNCH";
		case CANCEL:
			return "CANCEL";
		}
		return "UNKNOWN COMMANDTYPE";
	}
}