package edu.cornell.rocketry.comm.send;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.Command;
import edu.cornell.rocketry.util.CommandReceipt;
import edu.cornell.rocketry.util.CommandTask;

public class RealSender implements Sender{
	
	Controller controller;
	
	XBeeSender sender;
	
	public RealSender (Controller h, XBee x, XBeeAddress64 a) {
		controller = h;
		sender = new XBeeSender(x, a);
	}

	@Override
	public void send(Command c) {
		switch (c.task()) {
		//for each type of command, create the proper OutgoingPacket of the proper OutgoingPacketType
		//Once you have the packet, do sender.send(), and catch the appropriate exceptions.
		default:
			CommandReceipt r = 
				new CommandReceipt (c.task(), false, "edu.cornell.rocketry.comm.send.RealSender#send unimplemented");
			controller.acceptCommandReceipt(r);
		}
		
	}
	
	/**
	 * Creates a new XBeeSender object from the given arguments.
	 * Sets this RealSender object to use the new XBeeSender.
	 * @param x
	 * @param a
	 */
	public void resetSender(XBee x, XBeeAddress64 a) {
		sender = new XBeeSender(x, a);
	}

	
	
	
}
