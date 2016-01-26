package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.comm.TEMResponse;
import edu.cornell.rocketry.gui.controller.Controller;



public class TestReceiver implements Receiver {
	
	private Controller controller;

	public TestReceiver (Controller h) {
		controller = h;
	}

	public synchronized void acceptTEMResponse(TEMResponse tr) {
		controller.acceptTEMResponse(tr, true);
	}
	

}
