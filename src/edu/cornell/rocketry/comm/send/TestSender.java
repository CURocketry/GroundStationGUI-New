package edu.cornell.rocketry.comm.send;

import java.io.File;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.Command;
import edu.cornell.rocketry.util.CommandReceipt;
import edu.cornell.rocketry.util.CommandType;
import edu.cornell.rocketry.util.RocketSimulator;

public class TestSender implements Sender {
	
	Controller controller;
	
	RocketSimulator rsim;
	
	public TestSender (Controller c) {
		controller = c;
		String path = "./sim/campus_20.rsim";
		rsim = new RocketSimulator(path, controller.getReceiver(true));
	}
	
	public TestSender (Controller c, File f) {
		controller = c;
		rsim = new RocketSimulator(f, controller.getReceiver(true));
	}
	
	public void switchFile (String s) {}; //TODO
	
	public void send (String msg) {
		throw new UnsupportedOperationException();
	}
	
	public void send (Command c) {
		try {
			switch (c.type()) {
			case ENABLE_CAMERA:
				//TODO
				break;
			case DISABLE_CAMERA:
				//TODO
				break;
			case TRANSMIT_START:
				rsim.restart(c.time());
				break;
			case TRANSMIT_HALT:
				rsim.reset(c.time());
				break;
			case TRANSMIT_FREQ_MAX:
				//TODO
				break;
			case TRANSMIT_FREQ_MIN:
				//TODO
				break;
			case BEGIN_LAUNCH:
				//TODO
				break;
			case CANCEL_LAUNCH:
				//TODO
				break;
			default:
				throw new UnsupportedOperationException(c.type().toString());
			}
			receipt(c.type(), true, "");
		} catch (Exception e) {
			receipt(c.type(), false, "Unrecoverable: " + e.toString());
		}
	}
	
	private synchronized void receipt (CommandType t, boolean s, String m) {
		controller.acceptCommandReceipt(
			new CommandReceipt(t, s, m));
	}
	
	

}
