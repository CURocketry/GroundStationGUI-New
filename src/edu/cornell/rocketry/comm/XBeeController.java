package edu.cornell.rocketry.comm;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;

import edu.cornell.rocketry.comm.receive.XBeeListenerThread;
import edu.cornell.rocketry.gui.controller.Controller;


/**
 * Contains the state and functions necessary to connect to and interface with
 * the XBee radio.
 * 
 * @author Gus
 *
 */
public class XBeeController {
	
	private XBee xbee;
	private XBeeListenerThread listener;
	//TODO: PUT THE XBEE ADDRESS HERE TOO
	private Controller controller;
	
	/**
	 * Creates an XBeeController with parent {@link Controller} {@code c}.
	 * @param c parent {@code Controller}
	 */
	public XBeeController (Controller c) {
		xbee = new XBee();
		controller = c;
	}
	
	/**
	 * Opens a connection to the XBee with the given parameters.
	 * 
	 * @param port the port on which to connect to the XBee
	 * @param baud the baud rate at which to communicate with the XBee
	 * 
	 * @throws XBeeException if connection to XBee fails
	 */
	public void openXBee (String port, int baud) throws XBeeException { 
		/*if (xbee != null && xbee.isConnected()) {
			xbee.close();
		}*/
		xbee.open(port, baud);
	}
	
	public XBee getXbee() {
		return xbee;
	}
	
	/**
	 * Starts a new thread listening for incoming signals from the XBee radio.
	 */
	public void startListening () {
		if (!xbee.isConnected())
			System.out.println ("XBeeController: Cannot start listening; xbee not connected!");
		else {
			//FIXME: XBeeListenerThread should not see the view! Remove from constructor & refactor
			listener = new XBeeListenerThread (controller.getReceiver(false), xbee, controller.view());
			listener.start();
		}
	}
	
	/**
	 * Stops listening for incoming signals from the XBee radio.
	 */
	public void stopListening () {
		if (listener != null) 
			listener.stopListening();
	}

}
