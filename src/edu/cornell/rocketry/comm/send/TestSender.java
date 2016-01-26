package edu.cornell.rocketry.comm.send;

import java.io.File;

import edu.cornell.rocketry.comm.Command;
import edu.cornell.rocketry.comm.CommandReceipt;
import edu.cornell.rocketry.comm.CommandType;
import edu.cornell.rocketry.gui.controller.Controller;
import edu.cornell.rocketry.sim.TEMSimulator;

public class TestSender implements Sender {
	
	Controller controller;
	
	TEMSimulator rsim;
	
	public TestSender (Controller c) {
		controller = c;
		String path = "./sim/campus_20.rsim";
		rsim = new TEMSimulator(path, controller.getReceiver(true));
	}
	
	public TestSender (Controller c, File f) {
		controller = c;
		rsim = new TEMSimulator(f, controller.getReceiver(true));
	}
	
	public void switchFile (String s) {}; //TODO
	
	public void send (String msg) {
		throw new UnsupportedOperationException();
	}
	
	public void send (Command c) {
		try {
			switch (c.type()) {
			case ENABLE_CAMERA:
				rsim.enableCamera();
				break;
			case DISABLE_CAMERA:
				rsim.disableCamera();
				break;
			case TRANSMIT_START:
				rsim.restart(c.time());
				break;
			case TRANSMIT_HALT:
				rsim.reset(c.time());
				break;
			case TRANSMIT_FREQ_MAX:
				rsim.setMaxFrequency();
				break;
			case TRANSMIT_FREQ_MIN:
				rsim.setMinFrequency();
				break;
			case BEGIN_LAUNCH:
				//TODO
				throw new UnsupportedOperationException(c.type().toString());
			case CANCEL_LAUNCH:
				//TODO
				throw new UnsupportedOperationException(c.type().toString());
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
