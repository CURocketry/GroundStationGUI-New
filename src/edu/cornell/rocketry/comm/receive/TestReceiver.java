package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.GPSResponse;
import edu.cornell.rocketry.util.RocketSimulator;



public class TestReceiver implements Receiver {
	
	private Controller controller;
	
	private RocketSimulator rsim;

	public TestReceiver (Controller h) {
		controller = h;
	}
	
	public synchronized void acceptCommandResponse(CommandResponse cr) {
		controller.acceptCommandResponse(cr, true);
	}

	public synchronized void acceptGPSResponse(GPSResponse gr) {
		controller.acceptGPSResponse(gr, true);
	}
	
	public void setGPSSpoofer (RocketSimulator gs) {
		rsim = gs;
	}
	

}
