package edu.cornell.rocketry.comm.send;

/** a group of CommandTypes, represented as the physical bits to be sent */
public class CommandFlag {
	
	byte flag;
	
	public CommandFlag (byte f) {
		flag = f;
	}
	
	/** default constructor, defaulting to nothing set */
	public CommandFlag () {
		flag = 0x0;
	}
	
	/** turn on or off the CommandType elem
	 * 
	 * @param elem
	 * @param b  true to turn on elem, false to turn it off
	 * @return  the same CommandFlag, so that this command can be chained
	 */
	public CommandFlag set (CommandType elem, boolean b) {
		if (b) {
			flag |= elem.bitMask();
		} else {
			flag &= ~elem.bitMask();
		}
		
		return this;
	}
	
	/** checks if elem is set to true */
	public boolean isSet (CommandType elem) {
		byte b = (byte) (flag & elem.bitMask());
		return b != 0;
	}
	
	public byte byteValue () {
		return flag;
	}
	
	public String toString () {
		CommandType[] types = {
			CommandType.ENABLE_CAMERA,
			CommandType.DISABLE_CAMERA,
			CommandType.TRANSMIT_START,
			CommandType.TRANSMIT_HALT,
			CommandType.TRANSMIT_FREQ_MAX,
			CommandType.TRANSMIT_FREQ_MIN,
			CommandType.BEGIN_LAUNCH,
			CommandType.CANCEL_LAUNCH
			};
		
		String s = "";
		String b = "";
		for (int i = 0; i < types.length; i++) {
			if (isSet(types[i])) {
				b += "1";
				s += types[i].toString();
				if (i < types.length - 1) {
					s += ",";
				}
			} else {
				b += "0";
			}
		}
		
		return "<CommandFlag [0b" + b + "] {" + s + "}>";
	}

}
