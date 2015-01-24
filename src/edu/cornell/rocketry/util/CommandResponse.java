package edu.cornell.rocketry.util;

/**
 * A class for creating immutable response objects that contain
 * a success flag, the amount of time taken to complete the request (ms), 
 * and an optional information or error message.
 * @author Gus
 *
 */
public class CommandResponse {
	
	private boolean successful;
	private long responseTime;
	private CommandTask task;
	private String message;
	
	/** 
	 * Create a response object with a message.
	 * @param CommandTask the operation this response is from
	 * @param success success of the operation
	 * @param time (ms) taken for operation
	 * @param message error/info message
	 */
	public CommandResponse (CommandTask CommandTask, boolean success, long time, String message) {
		this.task = CommandTask;
		successful = success;
		responseTime = time;
		this.message = message;
	}
	
	/**
	 * Create a response object without a message.
	 * @param CommandTask the operation this response is from
	 * @param success success of the operation
	 * @param time (ms) taken for operation
	 */
	public CommandResponse (CommandTask CommandTask, boolean success, long time) {
		this.task = CommandTask;
		successful = success;
		responseTime = time;
		message = "no response message";
	}
	
	/**
	 * Returns the CommandTask from which this response originated.
	 */
	
	public CommandTask task() { return task; }
	
	/**
	 * Returns whether or not the operation was successful.
	 */
	public boolean successful() { return successful; }
	
	/**
	 * Returns the amount of time taken to complete the request, in ms.
	 */
	public double time() { return responseTime; }
	
	/**
	 * Returns the optional response message.
	 */
	public String message() { return message; }
	
	
	
}