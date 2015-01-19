package gui;

/**
 * A class for creating immutable response objects that contain
 * a success flag, the amount of time taken to complete the request (ms), 
 * and an optional information or error message.
 * @author Gus
 *
 */
public class Response {
	
	private boolean successful;
	private long responseTime;
	private String task;
	private String message;
	
	/** 
	 * Create a response object with a message.
	 * @param task the operation this response is from
	 * @param success success of the operation
	 * @param time (ms) taken for operation
	 * @param message error/info message
	 */
	public Response (String task, boolean success, long time, String message) {
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
	public Response (String task, boolean success, long time) {
		this.task = task;
		successful = success;
		responseTime = time;
		message = "";
	}
	
	/**
	 * Returns the task from which this response originated.
	 */
	
	public String task() { return task; }
	
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