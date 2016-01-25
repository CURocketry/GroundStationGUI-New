package edu.cornell.rocketry.comm.send;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;
import com.rapplogic.xbee.api.zigbee.ZNetTxStatusResponse;


public class XBeeSender {
	
	private XBeeAddress64 destination;
	private XBee xbee;
	
	public XBeeSender(XBee x, XBeeAddress64 a) {
		destination = a;
		xbee = x;
	}
	
	/**
	 * Send a packet to remote XBee
	 * @param r		packet to send
	 */
	public void send(OutgoingPacket packet) throws XBeeSenderException{
		
		try {
			// send a request and wait up to 10 seconds for the response
			
			//add condition for Send Data
	
			final ZNetTxRequest request = new ZNetTxRequest(destination, packet.payload());
			
			String payloadString = "";
			payloadString +=  "[";
			for (int i = 0; i < packet.payload().length; i++) {
				payloadString += packet.payload()[i];
				if (i < packet.payload().length - 1) 
					payloadString += ",";
			}
			payloadString += "]";
			
			System.out.println("payload = " + payloadString);
			
			ZNetTxStatusResponse response = (ZNetTxStatusResponse) xbee.sendSynchronous(request,1000);
			//System.out.println(response.isSuccess());
			
			if (response.isSuccess()) {
				// packet was delivered successfully
				System.out.println("success");
			} else {
				// packet was not delivered
				throw new XBeeSenderException("Packet not delivered");
			}
			
		} catch (XBeeTimeoutException e) {
			// timeout after 10 seconds
			throw new XBeeSenderException("Packet delivery timed out");
			
			// no response was received in the allotted time

		} catch (XBeeException e) {
			e.printStackTrace();
			throw new XBeeSenderException("Packet not delivered, XBee Exception: " + e.getMessage());
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new XBeeSenderException("Java Error. Make sure XBee is initialized: " + e.getMessage());
		}

	}
}
