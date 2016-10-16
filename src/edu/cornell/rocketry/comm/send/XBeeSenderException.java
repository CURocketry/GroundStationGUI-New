package edu.cornell.rocketry.comm.send;

/**
 * an exception that can be thrown by XBeeSender
 * @author derrick
 *
 */
public class XBeeSenderException extends Exception {
	
	public XBeeSenderException() {
		super();
	}
	
	public XBeeSenderException(String message) {
		super(message);
	}
}
