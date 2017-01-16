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
	public void send(Command c) {
		/*CommandReceipt r;
		
		CommandFlag f = new CommandFlag();
		f.set(c.type(), true);
		
		OutgoingPacket packet = new OutgoingCommandPacket(f);
		
		try {
			sender.send(packet);
			r = new CommandReceipt(c.type(), true, "");		
		} catch (XBeeSenderException e) {
			e.printStackTrace();
			r = new CommandReceipt(c.type(), false, e.toString());
		}
		
		controller.acceptCommandReceipt(r);*/
		System.err.println("send(Command c) is not implemented!");
	}
	
	/*
	public void send (Collection<Command> cs) {
		List<CommandReceipt> rs = new ArrayList<CommandReceipt>();
		
		CommandFlag f = new CommandFlag();
		for (Command c : cs) {
			f.set(c.type(), true);
		}
		
		OutgoingPacket packet = new OutgoingCommandPacket(f);
		
		try {
			sender.send(packet);
			for (Command c : cs) {
				rs.add(new CommandReceipt(c.type(), true, ""));
			}
		} catch (XBeeSenderException e) {
			e.printStackTrace();
			for (Command c : cs) {
				rs.add(new CommandReceipt(c.type(), false, e.toString()));
			}
		}
		
		for (CommandReceipt r : rs) {
			controller.acceptCommandReceipt(r);
		}
		
	}*/
	
	public void send (String msg) {
		OutgoingPacket packet = new OutgoingStringPacket(msg);
		
		try {
			System.out.println("edu.cornell.rocketry.comm.send.RealSender#send(String): sending string packet");
			lora.send(packet);
		} catch (LoRaException e) {
			System.err.println("edu.cornell.rocketry.comm.send.RealSender#send(String): failed to send String packet");
			e.printStackTrace();
		}
	}

	
}
