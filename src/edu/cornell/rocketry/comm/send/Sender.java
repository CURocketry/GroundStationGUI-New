package edu.cornell.rocketry.comm.send;

import edu.cornell.rocketry.util.CommandTask;

public interface Sender {
	
	public void send (CommandTask t, String[] args);
	
}
