package edu.cornell.rocketry.util;

public class CommandReceipt {

	
	private CommandType type;
	private boolean success;
	private String message;
	
	public CommandReceipt  (CommandType type, boolean success, String message) {
		this.type = type;
		this.success = success;
		this.message = message;
	}
	
	public CommandType type() { return type; }
	public boolean success() { return success; }
	public String message() { return message; }
}
