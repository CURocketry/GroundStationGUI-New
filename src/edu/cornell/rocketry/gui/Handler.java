package edu.cornell.rocketry.gui;

import javax.swing.JButton;

import edu.cornell.rocketry.gui.Model;
import edu.cornell.rocketry.gui.Model.Position;

public class Handler {
	
	private static Thread worker;
	
	
	
	private Signaller signaller;
	
	private static Model model = Model.model;
	
	
	public static Handler handler = new Handler();
	
	private Handler () {
		signaller = new Signaller();
	}
	
	
	public void hitSequence () {
		if (worker != null) worker.interrupt();
		if (model.sequence() == Model.Status.Disabled) {
			worker = new Thread(RunnableFactory.r.getStartRunnable());
		} else {
			worker = new Thread(RunnableFactory.r.getStopRunnable());
		}
		worker.start();
	}
	
	public void hitPayload () {
		if (worker != null) worker.interrupt();
		if (model.payload() == Model.Status.Disabled) {
			acceptCommandResponse(
					new CommandResponse (CommandResponse.Task.EnablePayload, false, 0L, "command sent"));
			worker = new Thread(RunnableFactory.r.getEnableRunnable());
		} else {
			acceptCommandResponse(
					new CommandResponse (CommandResponse.Task.DisablePayload, false, 0L, "command sent"));
			worker = new Thread(RunnableFactory.r.getDisableRunnable());
		}
		worker.start();
	}
	

	
	

	public CommandResponse runStartSequence () {
		long t1 = System.currentTimeMillis();
		handler.signaller.sendStart();		
		long t2 = System.currentTimeMillis();
		
		return new CommandResponse (CommandResponse.Task.StartSequence, false, t2 - t1, "src.gui.Handler#startSequence() unimplemented.");
	}
	
	public CommandResponse runStopSequence () {
		long t1 = System.currentTimeMillis();
		handler.signaller.sendStop();
		long t2 = System.currentTimeMillis();
		
		return new CommandResponse (CommandResponse.Task.StopSequence, false, t2 - t1, "src.gui.Handler#stopSequence() unimplemented.");
	}
	
	public CommandResponse runEnablePayload () {
		long t1 = System.currentTimeMillis();
		handler.signaller.sendEnable();
		long t2 = System.currentTimeMillis();
		
		return new CommandResponse (CommandResponse.Task.EnablePayload, false, t2 - t1, "src.gui.Handler#enablePayload() unimplemented.");		
	}
	
	public CommandResponse runDisablePayload () {
		long t1 = System.currentTimeMillis();
		handler.signaller.sendDisable();
		long t2 = System.currentTimeMillis();
		
		return new CommandResponse (CommandResponse.Task.DisablePayload, false, t2 - t1, "src.gui.Handler#disablePayload() unimplemented.");
	}
	
	public void acceptCommandResponse (CommandResponse r) {
		System.out.println("Command Response Received:");
		clog(r.task().toString());
		clog(r.successful() ? "Successful" : "Unsuccessful");
		clog(r.message());
		clog("elapsed time: " + r.time() + " ms");
		
	}
	
	public void acceptGPSResponse (GPSResponse r) {
		System.out.println("GPS Response Received:");
		if (gpsCheck(r)) {
			glog("(" + r.x() + ", " + r.y() + ", " + r.z() + ")");
			glog("gps time: " + r.time() + " ms");
			// Update model
			model.updatePosition(r.x(), r.y(), r.z());
		} else {
			glog("inaccurate data received");
		}
	}
	
	/**
	 * Returns whether or not the given GPSResponse contains 
	 * reasonable coordinates (e.g. (0,0,0) will be rejected).
	 * @param r the GPSResponse to be evaluated
	 * @return
	 */
	private boolean gpsCheck (GPSResponse r) {
		//TODO
		return true;
	}
	
	private static void clog (String s) {
		System.out.println("CMD_RESPONSE: " + s);
	}
	
	private static void glog (String s) {
		System.out.println("GPS_RESPONSE: " + s);
	}
	
}
