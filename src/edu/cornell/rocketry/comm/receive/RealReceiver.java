package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.TEMResponse;

public class RealReceiver implements Receiver {
	
	private Controller controller;
	
	//private XBeeListenerThread xbeeReceiver;
	
	public RealReceiver (Controller c) {
		controller = c;
		//xbeeReceiver = new XBeeListenerThread(this, controller.commController().xbee(), controller.view());
	}

	public synchronized void acceptCommandResponse(CommandResponse cr) {
		controller.acceptCommandResponse(cr, false);
	}

	public synchronized void acceptGPSResponse(TEMResponse tr) {
		controller.acceptTEMResponse(tr, false);
	}
	
}
