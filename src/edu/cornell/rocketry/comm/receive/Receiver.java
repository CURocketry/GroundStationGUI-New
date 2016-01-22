package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.TEMResponse;

public interface Receiver {
	
	public void acceptCommandResponse (CommandResponse cr);
	
	public void acceptGPSResponse (TEMResponse gr);
	
}
