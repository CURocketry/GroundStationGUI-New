package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.lora.LoRaPacket;

/** an object that handles a response from the TRACER */
public interface Receiver {
	
	public void acceptLoRaPacket (LoRaPacket r);
	
}
