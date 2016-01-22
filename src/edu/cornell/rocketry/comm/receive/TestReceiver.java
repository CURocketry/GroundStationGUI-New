package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.TEMResponse;



public class TestReceiver implements Receiver {
	
	private Controller controller;

	public TestReceiver (Controller h) {
		controller = h;
	}
	
	public synchronized void acceptCommandResponse(CommandResponse cr) {
		controller.acceptCommandResponse(cr, true);
	}

	public synchronized void acceptGPSResponse(TEMResponse tr) {
		controller.acceptTEMResponse(tr, true);
	}
	

}
