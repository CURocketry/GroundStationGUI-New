package edu.cornell.rocketry.comm.send;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.cornell.rocketry.gui.controller.Controller;
import edu.cornell.rocketry.lora.LoRa;
import edu.cornell.rocketry.lora.LoRaException;

/**
 * an implementation of Sender that connects to a real XBee
 *
 */
public class RealSender implements Sender{
	
	Controller controller;
	LoRa lora;
	
	public RealSender (Controller c, LoRa lr) {
		controller = c;
		lora = lr;
	}

	@Override
	public void send(CommandType c) {
		OutgoingCommandPacket msg = new OutgoingCommandPacket(c);
		send(msg);
	}
	
	@Override
	public void send (OutgoingCommandPacket msg) {
		try {
			System.out.println("edu.cornell.rocketry.comm.send.RealSender#send(String): sending packet containing: " + msg.getPayload());
			lora.send(msg);
		} catch (LoRaException e) {
			System.err.println("edu.cornell.rocketry.comm.send.RealSender#send(String): failed to send packet containing: " + msg.getPayload());
			e.printStackTrace();
		}
	}

	
}
