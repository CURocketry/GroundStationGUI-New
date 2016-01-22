package edu.cornell.rocketry.comm.send;

import java.io.File;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.Command;
import edu.cornell.rocketry.util.CommandReceipt;
import edu.cornell.rocketry.util.CommandTask;
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
	
	public void switchFile (String s) {}; //todo
	
	public void send (Command c) {
		try {
			switch (c.task()) {
			case TRANSMIT_START:
				rsim.restart(c.time());
				break;
			case TRANSMIT_HALT:
				rsim.reset(c.time());
				break;
			case EnableCamera:
				rsim.enableCamera(c.time());
				break;
			case DisableCamera:
				rsim.disableCamera(c.time());
				break;
			default:
				throw new UnsupportedOperationException(c.task().toString());
			}
			receipt(c.task(), true, "");
		} catch (Exception e) {
			receipt(c.task(), false, "Unrecoverable: " + e.toString());
		}
	}
	
	private void receipt (CommandTask t, boolean s, String m) {
		controller.acceptCommandReceipt(
			new CommandReceipt(t, s, m));
	}
	
	

}
