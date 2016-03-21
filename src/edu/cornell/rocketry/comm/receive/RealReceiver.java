package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.controller.Controller;

public class RealReceiver implements Receiver {
	
	private Controller controller;
	
	//private XBeeListenerThread xbeeReceiver;
	
	public RealReceiver (Controller c) {
		controller = c;
		//xbeeReceiver = new XBeeListenerThread(this, controller.commController().xbee(), controller.view());
	}

	public synchronized void acceptTEMResponse(TEMResponse tr) {
		controller.acceptTEMResponse(tr, false);
	}
	
}
