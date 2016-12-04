package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.controller.Controller;
import edu.cornell.rocketry.lora.LoRa;
import edu.cornell.rocketry.lora.LoRaPacket;

/** an implementation of Receiver that handles responses from a real XBee */
public class RealReceiver implements Receiver {
	
	private Controller controller;
	private LoRa lora;
	//private XBeeListenerThread xbeeReceiver;
	
	public RealReceiver (Controller c, LoRa lr) {
		controller = c;
		lora = lr;
		//xbeeReceiver = new XBeeListenerThread(this, controller.commController().xbee(), controller.view());
	}

	public synchronized void acceptLoRaPacket(LoRaPacket tr) {
		controller.acceptLoRaPacket(tr, false);
	}
	
}
