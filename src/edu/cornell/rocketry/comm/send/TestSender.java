package edu.cornell.rocketry.comm.send;

import java.io.File;

import edu.cornell.rocketry.gui.controller.Controller;
import edu.cornell.rocketry.sim.BasicTEMSimulator;
import edu.cornell.rocketry.sim.ContinuousTEMSimulator;
import edu.cornell.rocketry.sim.TEMSimulator;

/**
 * an implementation of Sender that connects to a simulated XBee
 *
 */
public class TestSender implements Sender {
	
	Controller controller;
	
	TEMSimulator rsim;
	
	public TestSender (Controller c) {
		controller = c;
		String path = "./sim/campus_20_sim.temdata";

		for (String k: path.split("\\.")) {
			System.out.println("k is: \"" + k +"\"");
		}
		System.out.println(path.split("\\.").length - 1);
		
		if (path.split("\\.")[path.split("\\.").length - 1].equals("csim") == false)
		//String path = "./tem_log_file_1455937569571ms.temdata";
			rsim = new BasicTEMSimulator(path, controller.getReceiver(true));
		
		else
			rsim = new ContinuousTEMSimulator(path, controller.getReceiver(true));
	}
	
	public TestSender (Controller c, File f) {
		controller = c;
		rsim = new BasicTEMSimulator(f, controller.getReceiver(true));
	}
	
	public void switchFile (String s) {}; //TODO
	
	public void send (String msg) {
		throw new UnsupportedOperationException();
	}
	
	public void send (Command c) {
		try {
			switch (c.type()) {
			case ENABLE_CAMERA:
				rsim.enableCamera();
				break;
			case DISABLE_CAMERA:
				rsim.disableCamera();
				break;
			case TRANSMIT_START:
				rsim.startTransmitting();
				break;
			case TRANSMIT_HALT:
				rsim.stopTransmitting();
				break;
			case TRANSMIT_FREQ_MAX:
				rsim.transmitMaxFrequency();
				break;
			case TRANSMIT_FREQ_MIN:
				rsim.transmitMinFrequency();
				break;
			case BEGIN_LAUNCH:
				rsim.launchPrepare();
				break;
			case CANCEL_LAUNCH:
				rsim.launchCancel();
				break;
			default:
				throw new UnsupportedOperationException(c.type().toString());
			}
			receipt(c.type(), true, "");
		} catch (Exception e) {
			receipt(c.type(), false, "Unrecoverable: " + e.toString());
		}
	}
	
	private synchronized void receipt (CommandType t, boolean s, String m) {
		controller.acceptCommandReceipt(
			new CommandReceipt(t, s, m));
	}
	
	

}
