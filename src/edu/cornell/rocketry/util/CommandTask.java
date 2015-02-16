package edu.cornell.rocketry.util;

public enum CommandTask {
	SendTest,
	SendData,
	EnablePayload,
	DisablePayload,
	InitializeXBee,
	RefreshPortList,
	ChangeBaudRate,
	ChangeAddress,
	ChangePort,
	StartTestSequence,
	StopTestSequence;
	
	public String toString() {
		switch (this) {
		case SendTest:
			return "SendTest";
		case SendData:
			return "SendData";
		case EnablePayload:
			return "EnablePayload";
		case DisablePayload:
			return "DisablePayload";
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
		case StartTestSequence:
			return "StartTestSequence";
		case StopTestSequence:
			return "StopTestSequence";
		default:
			throw new IllegalArgumentException();
		}
	}
}