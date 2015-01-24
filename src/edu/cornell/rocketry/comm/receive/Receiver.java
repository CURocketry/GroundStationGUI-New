package edu.cornell.rocketry.comm.receive;

import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.GPSResponse;

public interface Receiver {
	
	public void acceptCommandResponse (CommandResponse cr);
	
	public void acceptGPSResponse (GPSResponse gr);
}
