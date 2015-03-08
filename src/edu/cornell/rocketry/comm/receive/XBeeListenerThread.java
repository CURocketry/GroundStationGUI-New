// License: GPL. For details, see LICENSE file.
package edu.cornell.rocketry.comm.receive;
import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;

import edu.cornell.rocketry.comm.receive.RealReceiver;
import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.gui.GSGui;
import edu.cornell.rocketry.util.GPSResponse;

public class XBeeListenerThread extends Thread {

	private Receiver receiver;
	private GSGui mainWindow;
	private XBee xbee;
	
	public XBeeListenerThread (Receiver r, XBee xb, GSGui mw) {
		receiver = r;
		xbee = xb;
		mainWindow = mw;
	}
	
	//Changed implementations so that startListening() will 
	//actually start process w/o having to also call start();
//	public void stopListening() { keepListening = false; }
//	public void startListening() { keepListening = true; }
	
	public void startListening() { //if (!isAlive()) {
		System.out.println("LISTENER: STARTING"); 
		start(); //}
	}
	public void stopListening() { //if (isAlive()) {
		System.out.println("LISTENER: INTERRUPTING"); 
		interrupt(); //}
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
				//System.out.println("Listening 1");
				XBeeResponse response = xbee.getResponse(2000);
				//System.out.println("Listening 2");
				if (response.getApiId() == ApiId.ZNET_RX_RESPONSE) {
					//System.out.println("Listening 3");
					ZNetRxResponse ioSample = (ZNetRxResponse) response;
					IncomingPacket packet = new IncomingPacket(ioSample);
					//System.out.println("Listening 4");
					GPSResponse r = new GPSResponse (
						packet.latitude(), packet.longitude(), 
						packet.altitude(), packet.flag(), 
						System.currentTimeMillis());
					//System.out.println("Listening 5");
					synchronized(receiver) {
						//System.out.println("Listening 6");
						//System.out.println(receiver);
						receiver.acceptGPSResponse(r);
						//System.out.println("Listening 7");
					}
					//System.out.println("Listening 8");
					mainWindow.incNumRec();
					mainWindow.addToReceiveText("Received (" + mainWindow.getNumRec() + "): "
							+ packet.toString());
					//System.out.println("Listening 9");
				}
			} 
			catch (XBeeTimeoutException e) {
				//System.out.println("Listening Timeout Exception");
				System.out.println("timeout");
				// we timed out without a response
			} catch (XBeeException e) {
				//if (isInterrupted()) System.out.println("Interruption Failure");
				//System.out.println("Listening XBeeException");
				mainWindow.incNumError();
				mainWindow.addToReceiveText("Error (" + mainWindow.getNumError() + "): XBee Problem: "+ e.getMessage());
				e.printStackTrace();
				return; //stop polling
			} catch (Exception e) {
				//System.out.println("Listening Unknown Exception");
				e.printStackTrace();
			}
			//System.out.println("Listening 10");
		}
	}

}
