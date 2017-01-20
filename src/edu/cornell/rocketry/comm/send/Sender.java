package edu.cornell.rocketry.comm.send;

/** an object that handles a sending instructions to the TRACER */
public interface Sender {
	
	/** send the given CommandType up to the rocket (or simulated rocket) */
	public void send (CommandType c);
	
	/** DEBUG ONLY: send the given raw CommandPacket up to the rocket
	 * (use the CommandType version for the final version) */
	public void send (OutgoingCommandPacket msg);
	
}
