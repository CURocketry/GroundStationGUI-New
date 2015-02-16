package edu.cornell.rocketry.util;

public class Command {
	
	private CommandTask commandTask;
	
	private long startTime;
	
	
	public Command (CommandTask ct, long t) {
		commandTask = ct;
		startTime = t;
	}
	
	public CommandTask task() { return commandTask; }
	
	public long time() { return startTime; }

}
