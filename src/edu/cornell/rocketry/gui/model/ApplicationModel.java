package edu.cornell.rocketry.gui.model;

import com.rapplogic.xbee.api.XBeeAddress64;

public class ApplicationModel {
	
	private XBeeAddress64 xbeeAddress64;
	private int baudRate;
	private String serialPort;

	
	public ApplicationModel () {
		
	}
	
	public void setSerialPort (String port) {
		serialPort = port;
	}
	
	public String getSerialPort () {
		return serialPort;
	}
	
	public void setBaudRate (int rate) {
		baudRate = rate;
	}
	
	public int getBaudRate () {
		return baudRate;
	}
	
	public void setXbeeAddress (XBeeAddress64 addr) {
		xbeeAddress64 = addr;
	}
	
	public XBeeAddress64 getXbeeAddress () {
		return xbeeAddress64;
	}
}
