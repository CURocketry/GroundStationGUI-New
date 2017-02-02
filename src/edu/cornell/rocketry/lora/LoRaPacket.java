package edu.cornell.rocketry.lora;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

/**
 * 
 * A packet from the LoRa, parsed into its components
 * 
 * all Float values can be null if it is invalid data
 * 
 * @author derrick
 *
 */
public class LoRaPacket {
	public static final int MESSAGE_LENGTH = 34; //in bytes
	
	public int time;
	public Float lat = null;
	public Float lon = null;
	public Float alt = null;
	public Float x = null;
	public Float y = null;
	public Float z = null;
	
	private Byte commandByte = null;

	/** constructs a packet from the byte array
	 * all values are assumed to be big-endian
	 * 
	 * throws LoRaExceptions if input is an invalid byte stream */
	public LoRaPacket(byte[] bytes) throws LoRaException {
		ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 0, 4));
		bb.order(ByteOrder.BIG_ENDIAN);
		time = bb.getInt();

		//maybe there's a better way to do this than copy-paste?
		int counter = 4;
		if (bytes[counter] != 0x00) {
			bb = ByteBuffer.wrap(Arrays.copyOfRange(bytes, counter+1, counter+5));
			lat = bb.getFloat();
		}

		counter += 5;
		if (bytes[counter] != 0x00) {
			bb = ByteBuffer.wrap(Arrays.copyOfRange(bytes, counter+1, counter+5));
			lon = bb.getFloat();
		}

		counter += 5;
		if (bytes[counter] != 0x00) {
			bb = ByteBuffer.wrap(Arrays.copyOfRange(bytes, counter+1, counter+5));
			alt = bb.getFloat();
		}
		
		counter += 5;
		if (bytes[counter] != 0x00) {
			bb = ByteBuffer.wrap(Arrays.copyOfRange(bytes, counter+1, counter+5));
			x = bb.getFloat();
		}

		counter += 5;
		if (bytes[counter] != 0x00) {
			bb = ByteBuffer.wrap(Arrays.copyOfRange(bytes, counter+1, counter+5));
			y = bb.getFloat();
		}

		counter += 5;
		if (bytes[counter] != 0x00) {
			bb = ByteBuffer.wrap(Arrays.copyOfRange(bytes, counter+1, counter+5));
			z = bb.getFloat();
		}
		
		commandByte = bytes[4];
	}
	
	/** constructs a packet from the given parameters */
	public LoRaPacket(int time, Float lat, Float lon, Float alt, Float x, Float y, Float z) {
		this.time = time;
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof LoRaPacket) {
			LoRaPacket oTyped = (LoRaPacket) o;
			return
				time == oTyped.time &&
				Objects.equals(lat, oTyped.lat)&&
				Objects.equals(lon, oTyped.lon)&&
				Objects.equals(alt, oTyped.alt)&&
				Objects.equals(x, oTyped.x)&&
				Objects.equals(y, oTyped.y)&&
				Objects.equals(z, oTyped.z);
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return String.format("PACKET: (time: %d, lat: %f, long: %f, alt: %f, x: %f, y: %f, z: %f)",
				time, lat, lon, alt, x, y, z);
	}
	
	public int time() {
		return time;
	}
	public float lat() {
		return lat == null ? Float.NaN : lat;
	}
	public float lon() {
		return lon == null ? Float.NaN : lon;
	}
	public float alt() {
		return alt == null ? Float.NaN : alt;
	}
	public float x() {
		return x == null ? Float.NaN : x;
	}
	public float y() {
		return y == null ? Float.NaN : y;
	}
	public float z() {
		return z == null ? Float.NaN : z;
	}
	
	public boolean isConfirmation(){
		return time == -1; //TODO: if unsigned, change to MAX_INT
	}
	public LoRaConfirmationEnum getConfirmation(){
		switch(commandByte) {
		case 'L':
			return LoRaConfirmationEnum.LAUNCH_CONFIRM;
		case 'C':
			return LoRaConfirmationEnum.CANCEL_CONFIRM;
		case 'D':
			return LoRaConfirmationEnum.DONE_CONFIRM;
		default:
			System.out.println(commandByte);
			return LoRaConfirmationEnum.UNKNOWN_CONFIRM;
		}
	}
}
