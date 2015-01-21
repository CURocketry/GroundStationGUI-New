package edu.cornell.rocketry.gui;

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
	private Task task;
	private String message;
	
	/** 
	 * Create a response object with a message.
	 * @param task the operation this response is from
	 * @param success success of the operation
	 * @param time (ms) taken for operation
	 * @param message error/info message
	 */
	public CommandResponse (Task task, boolean success, long time, String message) {
		this.task = task;
		successful = success;
		responseTime = time;
		this.message = message;
	}
	
	/**
	 * Create a response object without a message.
	 * @param task the operation this response is from
	 * @param success success of the operation
	 * @param time (ms) taken for operation
	 */
	public CommandResponse (Task task, boolean success, long time) {
		this.task = task;
		successful = success;
		responseTime = time;
		message = "no response message";
	}
	
	/**
	 * Returns the task from which this response originated.
	 */
	
	public Task task() { return task; }
	
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
	
	enum Task {
		SendTest,
		SendData,
		EnablePayload,
		DisablePayload,
		InitializeXBee,
		RefreshPortList,
		ChangeBaudRate,
		ChangeAddress,
		ChangePort,
		StartSequence,
		StopSequence;
		
		public String toString(Task t) {
			switch (t) {
			case SendTest:
				return "SendTest";
			case SendData:
				return "SendData";
			case EnablePayload:
				return "EnablePayload";
			case DisablePayload:
				return "DisablePayload";
			case InitializeXBee:
				return "InitializeXBee";
			case RefreshPortList:
				return "RefreshportList";
			case ChangeBaudRate:
				return "ChangeBaudRate";
			case ChangeAddress:
				return "ChangeAddress";
			case ChangePort:
				return "ChangePort";
			case StartSequence:
				return "StartSequence";
			case StopSequence:
				return "StartSequence";
			default:
				throw new IllegalArgumentException();
			}
		}
	}
	
}