package edu.cornell.rocketry.lora;

/**
 * A collection of exceptions that LoRa-related functions can throw  
 *
 * Based heavily on the XBeeException code for XBee-related exceptions
 * (no longer used in this code)
 *
 */
public class LoRaException extends RuntimeException {

	private static final long serialVersionUID = -8040939886615173229L;
	private Exception cause;
	
	public LoRaException(String message) {
		super(message);
	}

	public LoRaException(String message, Exception e) {
		super(message, e);
	}
	
	public LoRaException() {
		super();
	}
	
	public LoRaException(Exception cause) {
		super();
		this.setCause(cause);
	}

	public Exception getCause() {
		return cause;
	}

	public void setCause(Exception cause) {
		this.cause = cause;
	}
}
