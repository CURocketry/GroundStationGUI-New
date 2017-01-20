package edu.cornell.rocketry.comm.send;

/** a class representing a packet that gives a byte array to the TRACER
 * in effect, this is a wrapper around a byte array, which is inputed
 * as a String. (This uses native Java Strings, so characters above 0xffff
 * are split into two surrogate pairs. But as long as we're not sending
 * up emoji to the TRACER, this should not generally be an issue.) 
 *
 */
public class OutgoingStringPacket implements OutgoingPacket {
	
	private int[] payload;

	public OutgoingStringPacket (String msg) {
		char[] tmp = msg.toCharArray();
		
		payload = new int[tmp.length];
		
		for (int i = 0; i < tmp.length; i++) {
			payload[i] = (int) tmp[i];
		}
	}
	
	@Override
	public int[] payload() {
		return payload;
	}

	@Override
	public int getPayload() {
		return payload[0];
	}

}
