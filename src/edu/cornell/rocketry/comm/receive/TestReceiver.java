package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.GPSResponse;
import edu.cornell.rocketry.util.GPSSpoofer;



public class TestReceiver implements Receiver {
	
	private Controller handler;
	
	private GPSSpoofer spoofGPS;

	public TestReceiver (Controller h) {
		handler = h;
	}
	
	public synchronized void acceptCommandResponse(CommandResponse cr) {
		// TODO Auto-generated method stub
		
	}

	public synchronized void acceptGPSResponse(GPSResponse gr) {
		handler.acceptGPSResponse(gr, true);
	}
	
	public void setGPSSpoofer (GPSSpoofer gs) {
		spoofGPS = gs;
	}

}
