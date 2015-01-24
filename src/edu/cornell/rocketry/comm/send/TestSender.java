package edu.cornell.rocketry.comm.send;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.CommandReceipt;
import edu.cornell.rocketry.util.CommandTask;
import edu.cornell.rocketry.util.GPSSpoofer;

public class TestSender implements Sender {
	
	Controller controller;
	
	GPSSpoofer gspoof;
	
	public TestSender (Controller c) {
		controller = c;
		String path = "./assets/gps_spoof_west_campus.csv";
		gspoof = new GPSSpoofer(path, controller.getReceiver(true));
	}
	
	public void send (CommandTask task, String[] args) {
		switch (task) {
		case StartTestSequence:
			gspoof.start();
			controller.acceptCommandReceipt(
				new CommandReceipt(task, true, "<no message>"));
			break;
		case StopTestSequence:
			gspoof.stop();
			gspoof.reset();
			break;
		default:
			throw new UnsupportedOperationException(task.toString());
		}
	}
	
	

}
