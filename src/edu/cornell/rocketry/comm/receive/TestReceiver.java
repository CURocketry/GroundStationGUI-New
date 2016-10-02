package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.controller.Controller;


/** an implementation of Receiver that handles responses from a simulated XBee */
public class TestReceiver implements Receiver {
	
	private Controller controller;

	public TestReceiver (Controller h) {
		controller = h;
	}

	public synchronized void acceptTEMResponse(TEMResponse tr) {
		controller.acceptTEMResponse(tr, true);
	}
	

}
