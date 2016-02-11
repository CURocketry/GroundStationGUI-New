package edu.cornell.rocketry.sim;

public interface TEMSimulator {
	
	public void startTransmitting ();
	public void stopTransmitting ();
	
	public void transmitMaxFrequency ();
	public void transmitMinFrequency ();
	
	public void enableCamera ();
	public void disableCamera ();
	
	public void launchPrepare ();
	public void launchCancel ();
	
	public void reset ();

}
