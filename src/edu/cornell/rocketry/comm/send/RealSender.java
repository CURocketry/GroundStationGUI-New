package edu.cornell.rocketry.comm.send;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.CommandReceipt;
import edu.cornell.rocketry.util.CommandTask;

public class RealSender implements Sender{
	
	Controller handler;
	
	public RealSender (Controller h) {
		handler = h;
	}

	@Override
	public void send(CommandTask t, String[] args) {
		switch (t) {
		default:
			CommandReceipt r = 
				new CommandReceipt (t, false, "edu.cornell.rocketry.comm.send.RealSender#send unimplemented");
			handler.acceptCommandReceipt(r);
		}
		
	}

	
	
	
}
