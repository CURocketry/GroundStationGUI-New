package edu.cornell.rocketry.comm.send;

import edu.cornell.rocketry.comm.CommandType;

public class CommandFlag {
	
	byte flag;
	
	public CommandFlag (byte f) {
		flag = f;
	}
	
	public CommandFlag () {
		flag = 0x0;
	}
	
	public CommandFlag set (CommandType elem, boolean b) {
		if (b) {
			flag |= elem.bitMask();
		} else {
			flag &= ~elem.bitMask();
		}
		
		return this;
	}
	
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
