package edu.cornell.rocketry.comm.send;

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
		String path = "./assets/gps_spoof_west_campus.csv";
		rsim = new RocketSimulator(path, controller.getReceiver(true));
	}
	
	public void send (Command c) {
		try {
			switch (c.task()) {
			case StartTestSequence:
				rsim.startGPS();
				break;
			case StopTestSequence:
				rsim.stopGPS();
				rsim.resetGPS();
				break;
			case EnablePayload:
				rsim.enablePayload(c.time());
				break;
			case DisablePayload:
				rsim.disablePayload(c.time());
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
