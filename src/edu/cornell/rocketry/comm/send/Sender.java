package edu.cornell.rocketry.comm.send;

public interface Sender {
	
	public void send (Command c);
	
	public void send (String msg);
	
}
