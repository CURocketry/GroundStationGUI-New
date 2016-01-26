package edu.cornell.rocketry.comm.receive;

import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;

import edu.cornell.rocketry.comm.TEMStatusFlag;

public class IncomingPacket {
	
	//Incoming packet structure [len=17]
	
	//[flag x1] 
	//[lat x4 signed] [lon x4 signed] [alt x2 unsigned] 
	//[gyro x2 signed] 
	//[acc_z x1 signed] [acc_x x1 signed] [acc_y x1 signed]
	//[temp x1 unsigned]
	
	//FIXME: PARSE AS SIGNED EXCEPT FOR TEMPERATURE
	
	private final int FLAG_LEN = 1;
	private final int LAT_LEN  = 4;
	private final int LON_LEN  = 4;
	private final int ALT_LEN  = 2;
	private final int GYRO_LEN = 2;
	private final int ACC_LEN  = 1;
	private final int TEMP_LEN = 1;
	
	private final int PACKET_SIZE = 17;
	
	private int latitude;
	private int longitude;
	private int altitude;
	private byte flag;
	private int gyroscope;
	private int acceleration_z;
	private int acceleration_x;
	private int acceleration_y;
	private int temperature;
	
	int[] rawPacketData;
	
	public IncomingPacket (ZNetRxResponse ioSample) {
		int[] tmp;
		rawPacketData = ioSample.getData();
		
		int i = 0;
		
		tmp = new int[LAT_LEN];
		System.arraycopy(rawPacketData, i, tmp, 0, LAT_LEN);
		latitude = convertToDecimalInt(tmp);
		i += LAT_LEN;
		
		tmp = new int[LON_LEN];
		System.arraycopy(rawPacketData, i, tmp, 0, LON_LEN);
		longitude = convertToDecimalInt(tmp);
		i += LON_LEN;
		
		tmp = new int[ALT_LEN];
		System.arraycopy(rawPacketData, i, tmp, 0, ALT_LEN);
		altitude = convertToDecimalInt(tmp);
		i += ALT_LEN;
		
		tmp = new int[FLAG_LEN];
		System.arraycopy(rawPacketData, i, tmp, 0, FLAG_LEN);
		flag = (byte) tmp[0];
		i+= FLAG_LEN;
		
		tmp = new int[GYRO_LEN];
		System.arraycopy(rawPacketData, i, tmp, 0, GYRO_LEN);
		gyroscope = convertToDecimalInt(tmp);
		i += GYRO_LEN;
		
		tmp = new int[ACC_LEN];
		System.arraycopy(rawPacketData, i, tmp, 0, ACC_LEN);
		acceleration_z = convertToDecimalInt(tmp);
		i += ACC_LEN;
		
		tmp = new int[ACC_LEN];
		System.arraycopy(rawPacketData, i, tmp, 0, ACC_LEN);
		acceleration_x = convertToDecimalInt(tmp);
		i += ACC_LEN;
		
		tmp = new int[ACC_LEN];
		System.arraycopy(rawPacketData, i, tmp, 0, ACC_LEN);
		acceleration_y = convertToDecimalInt(tmp);
		i += ACC_LEN;
		
		tmp = new int[TEMP_LEN];
		System.arraycopy(rawPacketData, i, tmp, 0, TEMP_LEN);
		temperature = convertToDecimalInt(tmp);
		i += TEMP_LEN;
		
		assert(i == PACKET_SIZE);
	}
	
	private int convertToDecimalInt(int[] array){
		int result = 0;
		for (int i=array.length-1; i>=0; i--) {
			if (i>0)
				result = (result | array[i]) << 8;
			else 
				result = result | array[i];
		}
		return result;
	}
	
	public double latitude () {
		return ((double) latitude) / 10000;
	}
	
	public double longitude () {
		return ((double) longitude) / 10000 * -1;
	}
	
	public double altitude () {
		return (double) altitude;
	}
	
	public double acceleration_x () {
		return ((double) acceleration_x) / 100;
		//FIXME apply proper conversion
	}
	
	public double acceleration_y () {
		return ((double) acceleration_y) / 100;
		//FIXME apply proper conversion
	}
	
	public double acceleration_z () {
		return ((double) acceleration_z) / 100;
		//FIXME apply proper conversion
	}
	
	public double gyroscope () {
		return ((double) gyroscope) / 360;
		//FIXME apply proper conversion
	}
	
	public double temperature () {
		return (double) temperature;
		//FIXME apply proper conversion
	}
	
	public byte flag () {
		return flag;
	}
	
	public String toString () {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rawPacketData.length; i++) {
			String integer = Integer.toHexString(rawPacketData[i]);
			if (integer.length() == 1) {
				integer = "0" + integer;
			}
			sb.append(Integer.toHexString(rawPacketData[i]));
			if (i < rawPacketData.length - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	public String toVerboseString () {
		StringBuilder sb = new StringBuilder();
		sb.append("IncomingPacket[");
		sb.append("lat:").append(latitude()).append(",");
		sb.append("lon:").append(longitude()).append(",");
		sb.append("alt:").append(altitude()).append(",");
		sb.append("acc_x:").append(acceleration_x()).append(",");
		sb.append("acc_y:").append(acceleration_y()).append(",");
		sb.append("acc_z:").append(acceleration_z()).append(",");
		sb.append("gyro:").append(gyroscope()).append(",");
		sb.append("temp:").append(temperature()).append(",");
		sb.append("flag:").append((new TEMStatusFlag(flag)).toString());
		sb.append("]");
		return sb.toString();
	}

}
