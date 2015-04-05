// License: GPL. For details, see LICENSE file.
package edu.cornell.rocketry.comm.receive;
import java.util.ArrayList;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;

import edu.cornell.rocketry.comm.receive.RealReceiver;
import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.gui.GSGui;
import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.CommandTask;
import edu.cornell.rocketry.util.GPSResponse;

public class XBeeListenerThread extends Thread {

	private Receiver receiver;
	private GSGui mainWindow;
	private XBee xbee;
	
	private int timeout = 2000;
	
	public XBeeListenerThread (Receiver r, XBee xb, GSGui mw) {
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
				//System.out.println("Listening 1");
				XBeeResponse response = xbee.getResponse(timeout);
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
					System.out.println("Actual Latitude:" + packet.latitude());
					System.out.println("Actual Longitude:" + packet.longitude());
					//System.out.println("Listening 5");
					synchronized(receiver) {
						//System.out.println("Listening 6");
						//System.out.println(receiver);
						receiver.acceptGPSResponse(r);
						
						for (CommandResponse cr : parseFlags(packet.flag())) {
							receiver.acceptCommandResponse(cr);
						}
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
				System.out.println("Listening Unknown Exception:");
				e.printStackTrace();
			}
			//System.out.println("Listening 10");
		}
	}

	public static ArrayList<CommandResponse> parseFlags(byte flag) {
		
		//GPS fix?
		CommandResponse r = 
			new CommandResponse(
				CommandTask.GPSFix, 
				!((flag & IncomingPacket.FLAG_GPS_FIX) == 0),
				0,"");
		//Payload enabled?
		CommandResponse s = 
			new CommandResponse(
				((flag & IncomingPacket.FLAG_PAYLOAD) == 0) ? 
					CommandTask.EnablePayload: 
					CommandTask.DisablePayload, 
				true,
				0, "");
		//other flags currently unused
		
		ArrayList<CommandResponse> updates = new ArrayList<CommandResponse>();
		updates.add(r);
		
		return updates;
	}
}
