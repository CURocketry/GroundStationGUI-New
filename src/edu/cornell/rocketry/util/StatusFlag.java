package edu.cornell.rocketry.util;

import java.util.HashMap;
import java.util.Map;

public class StatusFlag {
	
	byte flag;
	Map<Type, Byte> m;
	
	public StatusFlag (byte f) {
		m = new HashMap<Type, Byte>();
		flag = f;
		m.put(Type.gps_fix,        (byte) 0b00000001);
		m.put(Type.camera_enabled, (byte) 0b00000010);
		m.put(Type.transmit_freq,  (byte) 0b00000100);
	}
	
	public StatusFlag () {
		m = new HashMap<Type, Byte>();
		flag = 0x0;
	}
	
	public void set (Type elem, boolean b) {
		if (!m.containsKey(elem)) {
			throw new IllegalArgumentException("no flag '" + elem + "' found");
		}
		if (b) {
			flag |= m.get(elem).byteValue();
		} else {
			flag &= ~m.get(elem).byteValue();
		}
	}
	
	public boolean is_set (Type elem) {
		if (!m.containsKey(elem)) {
			throw new IllegalArgumentException("no flag '" + elem + "' found");
		}
		byte b = (byte) (flag | m.get(elem).byteValue());
		System.out.println("hello!");
		return b != 0;
	}
	
	public byte byteValue() {
		return flag;
	}
	
	public enum Type {
		gps_fix,
		camera_enabled,
		transmit_freq
	}

}
