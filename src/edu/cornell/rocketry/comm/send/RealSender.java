package edu.cornell.rocketry.comm.send;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.Command;
import edu.cornell.rocketry.util.CommandReceipt;

public class RealSender implements Sender{
	
	Controller controller;
	
	XBeeSender sender;
	
	public RealSender (Controller c, XBee x, XBeeAddress64 a) {
		controller = c;
		sender = new XBeeSender(x, a);
	}

	@Override
	public void send(Command c) {
		CommandReceipt r;
		
		CommandFlag f = new CommandFlag();
		f.set(c.type(), true);
		
		OutgoingPacket packet = new OutgoingCommandPacket(f);
		
		try {
			sender.send(packet);
			r = new CommandReceipt(c.type(), true, "");		
		} catch (XBeeSenderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			r = new CommandReceipt(c.type(), false, e.toString());
		}
		
		controller.acceptCommandReceipt(r);
	}
	
	public void send (String msg) {
		
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
