package edu.cornell.rocketry.comm.send;

import edu.cornell.rocketry.comm.Command;

public interface Sender {
	
	public void send (Command c);
	
	public void send (String msg);
	
}
