package edu.cornell.rocketry.comm.send;

public enum CommandType {
	ENABLE_CAMERA,
	DISABLE_CAMERA,
	TRANSMIT_START,
	TRANSMIT_HALT,
	TRANSMIT_FREQ_MAX,
	TRANSMIT_FREQ_MIN,
	BEGIN_LAUNCH,
	CANCEL_LAUNCH;
	
	public byte bitMask () {
		switch (this) {
		case ENABLE_CAMERA:
			return (byte) 1;
		case DISABLE_CAMERA:
			return (byte) 1 << 1;
		case TRANSMIT_START:
			return (byte) 1 << 2;
		case TRANSMIT_HALT:
			return (byte) 1 << 3;
		case TRANSMIT_FREQ_MAX:
			return (byte) 1 << 4;
		case TRANSMIT_FREQ_MIN:
			return (byte) 1 << 5;
		case BEGIN_LAUNCH:
			return (byte) 1 << 6;
		case CANCEL_LAUNCH:
			return (byte) 0b10000000; //thanks Obama
		default:
			throw new IllegalStateException("invalid CommandType");
		}
	}
	
	public String toString () {
		switch (this) {
		case ENABLE_CAMERA:
			return "ENABLE_CAEMERA";
		case DISABLE_CAMERA:
			return "DISABLE_CAMERA";
		case TRANSMIT_START:
			return "TRANSMIT_START";
		case TRANSMIT_HALT:
			return "TRANSMIT_HALT";
		case TRANSMIT_FREQ_MAX:
			return "TRANSMIT_FREQ_MAX";
		case TRANSMIT_FREQ_MIN:
			return "TRANSMIT_FREQ_MIN";
		case BEGIN_LAUNCH:
			return "BEGIN_LAUNCH";
		case CANCEL_LAUNCH:
			return "CANCEL_LAUNCH";
		default:
			throw new IllegalStateException("invalid CommandType");
		}
	}
}

//public enum CommandTask {
//	ENABLE_CAMERA,
//	DISABLE_CAMERA,
//	TRANSMIT_START,
//	TRANSMIT_HALT,
//	TRANSMIT_FREQ_MAX, //5Hz
//	TRANSMIT_FREQ_MIN, //0.2Hz
//	BEGIN_LAUNCH,
//	CANCEL_LAUNCH;
	
	//gps altitude, longitude, latitude //2 4 4
	//acceleration (x,y,z) //1 1 1
	//rotation //2
	//temperature //1
	//flag //2
	
	
	//18 bytes
	
	//[flag x1] 
	//[lat x4 signed] [lon x4 signed] [alt x2 unsigned] 
	//[gyro x2 signed] 
	//[acc_z x1 signed] [acc_x x1 signed] [acc_y x1 signed]
	//[temp x1 unsigned]
	
	//flag = 
	// -> gps_fix
	// -> camera_enabled
	// -> transmit_freq
	
	//command byte structure
	// -> [enable_camera][disable_camera][transmit_start][transmit_halt][transmit_max][transmit_min][begin_launch][cancel_launch]
	
	
//	public String toString() {
//		switch (this) {
//		case ENABLE_CAMERA:
//			return "ENABLE_CAMERA";
//		case DISABLE_CAMERA:
//			return "DISABLE_CAMERA";
//		case TRANSMIT_START:
//			return "TRANSMIT_START";
//		case TRANSMIT_HALT:
//			return "TRANSMIT_HALT";
//		case TRANSMIT_FREQ_MAX:
//			return "TRANSMIT_FREQ_MAX";
//		case TRANSMIT_FREQ_MIN:
//			return "TRANSMIT_FREQ_MIN";
//		case BEGIN_LAUNCH:
//			return "BEGIN_LAUNCH";
//		case CANCEL_LAUNCH:
//			return "CANCEL_LAUNCH";
//		default:
//			throw new IllegalStateException("CommandTask could not be converted to a String");
//		}
//	}
//}