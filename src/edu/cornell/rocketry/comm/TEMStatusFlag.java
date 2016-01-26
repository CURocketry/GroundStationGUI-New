package edu.cornell.rocketry.comm;

public class TEMStatusFlag {
	
	byte flag;
	
	public TEMStatusFlag (byte f) {
		flag = f;
	}
	
	public TEMStatusFlag () {
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
		int b = (int) (flag & elem.bitMask());
		return b != 0;
	}
	
	public byte byteValue() {
		return flag;
	}
	
	public enum Type {
		sys_init,
		gps_fix,
		camera_enabled,
		transmit_freq_max,
		launch_ready,
		landed;
		
		public int bitMask () {
			switch(this) {
			case sys_init:
				return 1;
			case gps_fix:
				return 1 << 1;
			case camera_enabled:
				return 1 << 2;
			case transmit_freq_max:
				return 1 << 3;
			case launch_ready:
				return 1 << 4;
			case landed:
				return 1 << 5;
			default:
				throw new IllegalStateException("invalid StatusFlag");
			}
		}
		
		public String toString () {
			switch (this) {
			case sys_init:
				return "sys_init";
			case gps_fix:
				return "gps_fix";
			case camera_enabled:
				return "camera_enabled";
			case transmit_freq_max:
				return "transmit_freq_max";
			case launch_ready:
				return "launch_ready";
			case landed:
				return "landed";
			default:
				throw new IllegalStateException("invalid StatusFlag");
			}
		}
	}
	
	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append("StatusFlag[");
		sb.append("sys_init:").append(isSet(Type.sys_init)).append(",");
		sb.append("gps_fix:").append(isSet(Type.gps_fix)).append(",");
		sb.append("camera_enabled:").append(isSet(Type.camera_enabled)).append(",");
		sb.append("transmit_freq_max:").append(isSet(Type.transmit_freq_max)).append(",");
		sb.append("launch_ready:").append(isSet(Type.launch_ready)).append(",");
		sb.append("landed:").append(isSet(Type.landed));
		sb.append("]");
		return sb.toString();
	}
}
