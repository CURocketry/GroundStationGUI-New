package edu.cornell.rocketry.util;

public class CommandReceipt {

	
	private CommandTask task;
	private boolean success;
	private String message;
	
	public CommandReceipt  (CommandTask task, boolean success, String message) {
		this.task = task;
		this.success = success;
		this.message = message;
	}
	
	public CommandTask task() { return task; }
	public boolean success() { return success; }
	public String message() { return message; }
}
