package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.GPSResponse;
//import edu.cornell.rocketry.xbee.XBeeListenerThread;
import edu.cornell.rocketry.comm.receive.XBeeListenerThread;

public class RealReceiver implements Receiver {
	
	private Controller controller;
	
	//private XBeeListenerThread xbeeReceiver;
	
	public RealReceiver (Controller c) {
		controller = c;
		//xbeeReceiver = new XBeeListenerThread(this, controller.commController().xbee(), controller.view());
	}

	public synchronized void acceptCommandResponse(CommandResponse cr) {
		// TODO Auto-generated method stub
		
	}

	public synchronized void acceptGPSResponse(GPSResponse gr) {
		controller.acceptGPSResponse(gr, false);
	}
	
}
