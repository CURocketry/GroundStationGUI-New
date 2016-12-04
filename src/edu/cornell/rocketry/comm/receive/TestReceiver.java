package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.controller.Controller;
import edu.cornell.rocketry.lora.LoRaPacket;


/** an implementation of Receiver that handles responses from a simulated XBee */
public class TestReceiver implements Receiver {
	
	private Controller controller;

	public TestReceiver (Controller h) {
		controller = h;
	}

	public synchronized void acceptLoRaPacket(LoRaPacket tr) {
		controller.acceptLoRaPacket(tr, true);
	}
	

}
