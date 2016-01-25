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

public class OutgoingCommandPacket implements OutgoingPacket {
	
	private int[] payload;
	
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
