package edu.cornell.rocketry.comm.shared;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;

import edu.cornell.rocketry.comm.receive.XBeeListenerThread;
import edu.cornell.rocketry.gui.Controller;

public class XBeeController {
	
	private XBee xbee;
	private XBeeListenerThread listener;
	//TODO: PUT THE XBEE ADDRESS HERE TOO
	private Controller controller;
	
	public XBeeController (Controller c) {
		xbee = new XBee();
		controller = c;
		listener = new XBeeListenerThread (c.getReceiver(false), xbee, c.view());
	}
	
	public void openXBee (String port, int baud) throws XBeeException { 
		/*if (xbee != null && xbee.isConnected()) {
			xbee.close();
		}*/
		xbee.open(port, baud);
	}
	
	public XBee xbee() {
		return xbee;
	}
	
	public void refresh () {
		System.out.println("comm.shared.CommState#refresh () unimplemented.");
	}
	
	public void startListening () {
		if (!xbee.isConnected())
			System.out.println ("CommController: Cannot start listening; xbee not connected!");
		else {
			listener = new XBeeListenerThread (controller.getReceiver(false), xbee, controller.view());
			listener.start();
		}
	}
	
	public void stopListening () {
		listener.stopListening();
	}

}
