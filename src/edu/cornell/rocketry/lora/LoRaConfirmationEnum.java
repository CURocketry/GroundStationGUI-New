package edu.cornell.rocketry.lora;

public enum LoRaConfirmationEnum {
	LAUNCH_CONFIRM,
	RASPBERRYPI_ERROR,
	CANCEL_CONFIRM,
	DONE_CONFIRM,
	UNKNOWN_CONFIRM // if we receive an unknown confirmation packet
}
