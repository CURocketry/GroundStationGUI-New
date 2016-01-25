package edu.cornell.rocketry.util;

public class StatusFlag {
	
	byte flag;
	
	public StatusFlag (byte f) {
		flag = f;
	}
	
	public StatusFlag () {
		flag = 0x0;
	}
	
	public void set (Type elem, boolean b) {
		if (b) {
			flag |= elem.bitMask();
		} else {
			flag &= ~elem.bitMask();
		}
	}
	
	public boolean isSet (Type elem) {
		byte b = (byte) (flag & elem.bitMask());
		return b != 0;
	}
	
	public byte byteValue() {
		return flag;
	}
	
	public enum Type {
		gps_fix,
		camera_enabled,
		transmit_freq_max;
		
		public byte bitMask () {
			switch(this) {
			case gps_fix:
				return (byte) 0b00000001;
			case camera_enabled:
				return (byte) 0b00000010;
			case transmit_freq_max:
				return (byte) 0b00000100;
			default: 
				throw new IllegalStateException("invalid StatusFlag");
			}
		}
		
		public String toString () {
			switch (this) {
			case gps_fix:
				return "gps_fix";
			case camera_enabled:
				return "camera_enabled";
			case transmit_freq_max:
				return "transmit_freq_max";
			default:
				throw new IllegalStateException("invalid StatusFlag");
			}
		}
	}

}
