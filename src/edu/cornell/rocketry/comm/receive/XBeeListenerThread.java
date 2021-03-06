// License: GPL. For details, see LICENSE file.
package edu.cornell.rocketry.comm.receive;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;

import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.gui.view.View;

/**
 * a listener that waits for a signal from the XBee and handles this signal
 * with a Receiver, and then updating the View
 *
 */
public class XBeeListenerThread extends Thread {

	private Receiver receiver;
	private View mainWindow;
	private XBee xbee;
	
	private int timeout = 2000;
	
	/** constructor */
	public XBeeListenerThread (Receiver r, XBee xb, View mw) {
		receiver = r;
		xbee = xb;
		mainWindow = mw;
	}
	
	//Changed implementations so that startListening() will 
	//actually start process w/o having to also call start();
//	public void stopListening() { keepListening = false; }
//	public void startListening() { keepListening = true; }
	
	public void startListening() {
		System.out.println("LISTENER: STARTING"); 
		start();
	}
	public void stopListening() {
		System.out.println("LISTENER: INTERRUPTING"); 
		interrupt();
	}
	
	@Override
	public void run() {
		while (true) {
			//System.out.println("Listening");
			if (interrupted()) {
				System.out.println("LISTENER: INTERRUPTED");
				return;
			}
			try {
				XBeeResponse response = xbee.getResponse(timeout);

				if (response.getApiId() == ApiId.ZNET_RX_RESPONSE) {

					ZNetRxResponse ioSample = (ZNetRxResponse) response;
					IncomingPacket packet = new IncomingPacket(ioSample);

					TEMResponse r = 
						new TEMResponse (
							packet.latitude(), 
							packet.longitude(), 
							packet.altitude(), 
							packet.flag(), 
							packet.gyroscope(),
							packet.acceleration_x(),
							packet.acceleration_y(),
							packet.acceleration_z(),
							packet.temperature());
					
					System.out.println("Actual Latitude:" + packet.latitude());
					System.out.println("Actual Longitude:" + packet.longitude());
					mainWindow.incNumRec();
					mainWindow.addToReceiveText("Received (" + mainWindow.getNumRec() + "): "
							+ packet.toVerboseString());
					mainWindow.addToReceiveText("Received (" + mainWindow.getNumRec() + "): "
							+ packet.toString());
					
					synchronized (receiver) {
						receiver.acceptTEMResponse(r);
					}
				}
			} 
			catch (XBeeTimeoutException e) {
				System.out.println("timeout");
				// we timed out without a response
			} catch (XBeeException e) {
				mainWindow.incNumError();
				mainWindow.addToReceiveText("Error (" + mainWindow.getNumError() + "): XBee Problem: "+ e.getMessage());
				e.printStackTrace();
				return; //stop polling
			} catch (Exception e) {
				System.out.println("Listening Unknown Exception:");
				e.printStackTrace();
			}
		}
	}
}
