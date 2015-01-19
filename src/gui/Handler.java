package gui;

import javax.swing.JButton;

import gui.Model;

public class Handler {
	
	private static Thread worker;
	
	public static Handler handler = new Handler();
	
	public Signaller signaller;
	
	private Handler () {
		signaller = new Signaller();
	}
	
	
	public static void hitSequence (/*JButton jb*/) {
		if (worker != null) worker.interrupt();
		if (Model.model.sequence() == Model.Status.Disabled) {
			worker = new Thread(RunnableFactory.r.getStartRunnable());
		} else {
			//jb.setText("Enable Sequence");
			worker = new Thread(RunnableFactory.r.getStopRunnable());
		}
		worker.start();
	}
	
	public static void hitPayload () {
		if (worker != null) worker.interrupt();
		if (Model.model.payload() == Model.Status.Disabled) {
			worker = new Thread(RunnableFactory.r.getEnableRunnable());
		} else {
			worker = new Thread(RunnableFactory.r.getDisableRunnable());
		}
	}
	

	
	

	public static Response runStartSequence () {
		long t1 = System.currentTimeMillis();
		handler.signaller.sendStart();		
		long t2 = System.currentTimeMillis();
		
		return new Response ("Start Sequence", false, t2 - t1, "src.gui.Handler#startSequence() unimplemented.");
	}
	
	public static Response runStopSequence () {
		long t1 = System.currentTimeMillis();
		handler.signaller.sendStop();
		long t2 = System.currentTimeMillis();
		
		return new Response ("Stop Sequence", false, t2 - t1, "src.gui.Handler#stopSequence() unimplemented.");
	}
	
	public static Response runEnablePayload () {
		long t1 = System.currentTimeMillis();
		handler.signaller.sendEnable();
		long t2 = System.currentTimeMillis();
		
		return new Response ("Enable Payload", false, t2 - t1, "src.gui.Handler#enablePayload() unimplemented.");		
	}
	
	public static Response runDisablePayload () {
		long t1 = System.currentTimeMillis();
		handler.signaller.sendDisable();
		long t2 = System.currentTimeMillis();
		
		return new Response ("Disable Payload", false, t2 - t1, "src.gui.Handler#disablePayload() unimplemented.");
	}
	
	public static void acceptResponse (Response r) {
		System.out.println("Response Received:");
		System.out.println("RESPONSE: " + r.task());
		System.out.println("RESPONSE: " + (r.successful() ? "Successful" : "Unsuccessful"));
		System.out.println("RESPONSE: " + r.message());
		System.out.println("RESPONSE: " + r.time() + " ms");
	}
	
	
	
	
	
	
}
