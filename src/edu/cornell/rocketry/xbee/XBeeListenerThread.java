// License: GPL. For details, see LICENSE file.
package edu.cornell.rocketry.xbee;
import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;

import edu.cornell.rocketry.comm.receive.RealReceiver;
import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.gui.GSGui;
import edu.cornell.rocketry.util.TEMResponse;

public class XBeeListenerThread extends Thread {

	private boolean keepListening;
	private boolean receiving = false;
	
	private Receiver receiver;
	private GSGui mainWindow;
	private XBee xbee;
	
	public XBeeListenerThread (Receiver r, XBee xb) {
		receiver = r;
		xbee = xb;
		keepListening = true;
	}
	
	public void stopListening() { keepListening = false; }
	public void startListening() { keepListening = true; }
	
	@Override
	public void run() {
		while (keepListening) {
			try {
				System.out.println("Listening");
				XBeeResponse response = xbee.getResponse();
				
				if (response.getApiId() == ApiId.ZNET_RX_RESPONSE) {

					ZNetRxResponse ioSample = (ZNetRxResponse) response;
					IncomingPacket packet = new IncomingPacket(ioSample);
					
					TEMResponse r = new TEMResponse (
						packet.latitude(), packet.longitude(), 
						packet.altitude(), packet.flag(), 
						System.currentTimeMillis(),0,0);
					
					synchronized(receiver) {
						receiver.acceptGPSResponse(r);
					}
					
					mainWindow.incNumRec();
					mainWindow.addToReceiveText("Received (" + mainWindow.getNumRec() + "): "
							+ packet.toString());

				}
			} 
			catch (XBeeTimeoutException e) {
				System.out.println("timeout");
				// we timed out without a response
			} catch (XBeeException e) {
				mainWindow.incNumError();
				mainWindow.addToReceiveText("Error (" + mainWindow.getNumError() + "): XBee Problem: "+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
