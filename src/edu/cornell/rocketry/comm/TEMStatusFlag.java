package edu.cornell.rocketry.comm;

/**
 * Wrapper for a byte flag that contains status information from the TEM.
 * 
 * @author Gus
 *
 */
public class TEMStatusFlag {
	
	/**
	 * Byte flag received from the TEM.
	 */
	private byte flag;
	
	/**
	 * Wraps {@code f} in a new TEMStatusFlag.
	 * 
	 * @param f flag to be wrapped
	 */
	public TEMStatusFlag (byte f) {
		flag = f;
	}
	
	/**
	 * Creates a new TEMStatusFlag with dummy flag 0x00;
	 */
	public TEMStatusFlag () {
		flag = 0x0;
	}
	
	/**
	 * Sets (if {@code b} is {@code true}) 
	 * or resets (if {@code b} is {@code false})
	 * the bit corresponding to the given {@code Type}.
	 * 
	 * @param elem specifies which bit to set or reset
	 * @param b specifies whether to set or reset the specified bit
	 */
	public void set (Type elem, boolean b) {
		if (b) {
			flag |= elem.bitMask();
		} else {
			flag &= ~elem.bitMask();
		}
	}
	
	/**
	 * @param elem specifies which bit to check
	 * @return whether or not the bit corresponding with the given 
	 * {@code Type} is set
	 */
	public boolean isSet (Type elem) {
		int b = (flag & elem.bitMask());
		return b != 0;
	}
	
	/**
	 * @return underlying {@code byte} representation of this flag
	 */
	public byte byteValue() {
		return flag;
	}
	
	/**
	 * The types of information carried in a {@link TEMStatusFlag}.
	 * 
	 * Possible types:
	 * <ul>
	 * 	<li>sys_init - set iff the system was successfully initialized</li>
	 * 	<li>gps_fix - set iff the TEM's GPS has a fix</li>
	 * 	<li>camera_enabled - set iff the camera is filming</li>
	 * 	<li>transmit_freq_max - set iff the system is transmitting 
	 * 		at maximum frequency</li>
	 * 	<li>launch_ready - set iff the system is ready for and 
	 * 		expects an imminent launch</li>
	 * 	<li>landed - set iff the system detects that it has landed</li>
	 * </ul>
	 * @author Gus
	 *
	 */
	public enum Type {
		sys_init,
		gps_fix,
		camera_enabled,
		transmit_freq_max,
		launch_ready,
		landed;
		
		/**
		 * @return the bit representing this {@code Type}
		 */
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
		
		/**
		 * @return direct String transcription of this {@code Type}
		 */
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
	
	/**
	 * @return a hexadecimal {@code String} representing the 
	 * literal {@code byte} flag
	 */
	public String toHexString () {
		return Integer.toHexString(flag);
	}
	
	/**
	 * @return a human-readable {@code String} representing this flag, 
	 * broken down by each {@link Type}
	 */
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
