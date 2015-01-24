package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.GPSResponse;
import edu.cornell.rocketry.xbee.XBeeListenerThread;

public class RealReceiver implements Receiver {
	
	private Controller handler;
	
	private XBeeListenerThread xbeeReceiver;
	
	public RealReceiver (Controller h, XBeeListenerThread t) {
		handler = h;
		xbeeReceiver = t;
	}

	public synchronized void acceptCommandResponse(CommandResponse cr) {
		// TODO Auto-generated method stub
		
	}

	public synchronized void acceptGPSResponse(GPSResponse gr) {
		handler.acceptGPSResponse(gr, false);
	}
	
}
