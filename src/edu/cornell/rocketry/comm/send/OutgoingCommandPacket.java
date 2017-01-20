package edu.cornell.rocketry.comm.send;


/** a class representing a packet that gives a command to the TRACER
 * in effect, this is a wrapper around a single byte which acts as the
 * command
 *
 */
public class OutgoingCommandPacket implements OutgoingPacket {	
	private int[] payload; // all commands are a single byte
	// the fact that it's an array is legacy code and should be factored out when cleaning this code up again
	
	public OutgoingCommandPacket (CommandType f) {
		payload = new int[1];
		switch(f) {
		case LAUNCH:
			payload[0] = 'L';
			break;
		case CANCEL:
			payload[0] = 'C';
			break;
		default:
			throw new UnsupportedOperationException("invalid CommandType");
			//should never reach here due to typechecking, anyway
		}
	}
	
	/** returns the first int in the payload
	 * note that the payload is only one int long anyway
	 */
	public int getPayload() {
		return payload[0];
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
