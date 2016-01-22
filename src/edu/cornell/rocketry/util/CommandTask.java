package edu.cornell.rocketry.util;

public enum CommandTask {
	SendTest,
	SendData,
	EnableCamera,
	DisableCamera,
	InitializeXBee,
	RefreshPortList,
	ChangeBaudRate,
	ChangeAddress,
	ChangePort,
	TRANSMIT_START,
	TRANSMIT_HALT,
	GPSFix;
	
	public String toString() {
		switch (this) {
		case SendTest:
			return "SendTest";
		case SendData:
			return "SendData";
		case EnableCamera:
			return "EnableCamera";
		case DisableCamera:
			return "DisableCamera";
		case InitializeXBee:
			return "InitializeXBee";
		case RefreshPortList:
			return "RefreshportList";
		case ChangeBaudRate:
			return "ChangeBaudRate";
		case ChangeAddress:
			return "ChangeAddress";
		case ChangePort:
			return "ChangePort";
		case TRANSMIT_START:
			return "TRANSMIT_START";
		case TRANSMIT_HALT:
			return "TRANSMIT_HALT";
		case GPSFix:
			return "GPSFix";
		default:
			throw new IllegalArgumentException();
		}
	}
}