package edu.cornell.rocketry.comm.send;

//public class OutgoingPacket {
//	public static final int DIR_TEST = 0xAB;
//	public static final int DIR_PAYLOAD = 0xAC;
//	
//	private OutgoingPacketType type;
//	private int[] payload;
//	
//	public OutgoingPacket(OutgoingPacketType t) {
//		type = t;
//		switch (type) {
//		case TEST:
//			payload = new int[1];
//			payload[0] = DIR_TEST;
//			break;
//		case PAYLOAD_LAUNCH:
//			payload = new int[1];
//			payload[0] = DIR_PAYLOAD;
//			break;
//		}
//	}
//	
//	public int[] getPayload() {
//		return payload;
//	}
//
//}

/** a class representing a packet that gives a command to the TRACER
 * in effect, this is a wrapper around a single byte which acts as the
 * command
 *
 */
public class OutgoingCommandPacket implements OutgoingPacket {
	
	private int[] payload; // all commands are a single byte
	
	public OutgoingCommandPacket (CommandFlag f) {
		payload = new int[1];
		payload[0] = (int) f.byteValue();
	}
	
	public OutgoingCommandPacket (int[] p) {
		payload = p;
	}
	
	public int[] payload () {
		return payload;
	}
}
