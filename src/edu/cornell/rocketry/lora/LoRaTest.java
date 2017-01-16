package edu.cornell.rocketry.lora;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import org.junit.Test;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class LoRaTest {
	
	//this no longer works now that startListening directly prints to the screen
	/*
	// this functionality must be tested concurrently with a Python program that simulates an arduino sending data
	@Test
	public void testLoRa() {
		LoRa a = new LoRa("COM6");
		assertEquals("COM6", a.getPortName());
		LoRaPacket a_packet = new LoRaPacket(1258, 10.5f, 20.5f, 30.5f, 40.5f, 50.5f, 60.5f);
		LoRaPacket a_packet_received = a.startListening();
		//sendBytes(portname, hexString);
		assertEquals(a_packet, a_packet_received);
		
		try {
			LoRa b = new LoRa("COM_Nonexistent");
		} catch (LoRaException e) {
			assertEquals("No port named \"COM_Nonexistent\" was found.", e.getMessage());
		}
	}
	*/

	@Test
	public void testLoRaPacket() {
		LoRaPacket a = new LoRaPacket(1258, 10.5f, 20.5f, 30.5f, 40.5f, 50.5f, 60.5f);
		byte[] b_string = hexStringToByteArray("000004ea01412800000141a400000141f40000014222000001424a00000142720000");
		
		LoRaPacket b = new LoRaPacket(b_string);
		assertEquals(1258, b.time, 0.0);
		assertEquals(10.5, b.lat, 0.0);
		assertEquals(20.5, b.lon, 0.0);
		assertEquals(30.5, b.alt, 0.0);
		assertEquals(40.5, b.x, 0.0);
		assertEquals(50.5, b.y, 0.0);
		assertEquals(60.5, b.z, 0.0);
		assertEquals(a, b);
	}
	
	/** http://stackoverflow.com/questions/11208479/how-do-i-initialize-a-byte-array-in-java */
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	/** pretend we're the arduino, sending data to the port */
	private void sendBytes(String portname, String hexString){
		// find the port
		Enumeration<CommPortIdentifier> enumComm = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId = null;
		while (enumComm.hasMoreElements()) {
	        CommPortIdentifier currPortId = enumComm.nextElement();
            if (currPortId.getName().equals(portname)) {
                portId = currPortId;
                break;
            }
		}
		if (portId == null) {
			throw new LoRaException(String.format("No port named \"%s\" was found.", portname));
		}
		
		// connect to the port that we found and send the data
		int baudrate = 19200;
		try {
			SerialPort serialPort = (SerialPort) portId.open(portname, LoRa.TIMEOUT);
			serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			
			OutputStream output = serialPort.getOutputStream();
			byte[] b_string = hexStringToByteArray(hexString);
			output.write(b_string);
			output.flush();
			output.close();
			
			serialPort.close();
		} catch (PortInUseException | UnsupportedCommOperationException | IOException e) {
			throw new LoRaException(String.format("Port \"%s\" is in use.", portname), e);
		}
	}
}
