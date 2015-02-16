package edu.cornell.rocketry.comm.send;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.Command;
import edu.cornell.rocketry.util.CommandReceipt;
import edu.cornell.rocketry.util.CommandTask;

public class RealSender implements Sender{
	
	Controller controller;
	
	public RealSender (Controller h) {
		controller = h;
	}

	@Override
	public void send(Command c) {
		switch (c.task()) {
		default:
			CommandReceipt r = 
				new CommandReceipt (c.task(), false, "edu.cornell.rocketry.comm.send.RealSender#send unimplemented");
			controller.acceptCommandReceipt(r);
		}
		
	}

	
	
	
}
