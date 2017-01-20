package edu.cornell.rocketry.comm.send;

/** an interface representing the data to be sent to the TRACER */
public interface OutgoingPacket {
	
	public int[] payload ();
	
	/** returns the first int in the payload
	 */
	public int getPayload();
	
}
