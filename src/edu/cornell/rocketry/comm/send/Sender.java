package edu.cornell.rocketry.comm.send;

/** an object that handles a sending instructions to the TRACER */
public interface Sender {
	
	public void send (Command c);
	
	public void send (String msg);
	
}
