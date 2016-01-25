package edu.cornell.rocketry.comm.send;

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

}
