package edu.cornell.rocketry.comm.send;


/** a class representing a packet that gives a command to the TRACER
 * in effect, this is a wrapper around a single byte which acts as the
 * command
 *
 */
public class OutgoingCommandPacket implements OutgoingPacket {	
	private int[] payload; // all commands are a single byte
	
	public OutgoingCommandPacket (CommandType f) {
		payload = new int[1];
		switch(f) {
		case LAUNCH:
			payload[0] = 'L';
			break;
		case CANCEL:
			payload[0] = 'C';
		default:
			throw new UnsupportedOperationException("invalid CommandType");
			//should never reach here due to typechecking, anyway
		}
	}
	
	/** ONLY TO BE USED FOR DEBUGGING; use the version that accepts
	 * a CommandType to ensure data consistency */
	public OutgoingCommandPacket (int[] p) {
		payload = p;
	}
	
	public int[] payload () {
		return payload;
	}
}
