package edu.cornell.rocketry.comm;

public class Command {
	
	private CommandType type;
	
	private long startTime;
	
	
	public Command (CommandType ct, long t) {
		type = ct;
		startTime = t;
	}
	
	public CommandType type () { return type; }
	
	public long time() { return startTime; }

}
