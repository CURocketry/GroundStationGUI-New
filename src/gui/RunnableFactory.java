package gui;

public class RunnableFactory {
	
	public static RunnableFactory r = new RunnableFactory();
	
	private RunnableFactory () {
		
	}
	
	public Runnable getStartRunnable() {
		return new StartRunnable();
	}
	
	public Runnable getStopRunnable() {
		return new StopRunnable();
	}
	
	public Runnable getEnableRunnable() {
		return new EnableRunnable();
	}
	
	public Runnable getDisableRunnable() {
		return new DisableRunnable();
	}
	
/* Classes for running tasks in new threads */
	
	
	private class StartRunnable implements Runnable {
		public void run () {
			Response r = Handler.runStartSequence();
			synchronized (Handler.handler) { 
				Handler.acceptResponse(r);
			}
		}
	}
	
	private class StopRunnable implements Runnable {
		public void run () {
			Response r = Handler.runStopSequence();
			synchronized (Handler.handler) { 
				Handler.acceptResponse(r);
			}
		}
	}
	
	private class EnableRunnable implements Runnable {
		public void run() {
			Response r = Handler.runEnablePayload();
			synchronized (Handler.handler) { 
				Handler.acceptResponse(r);
			}
		}
	}
	
	private class DisableRunnable implements Runnable {
		public void run() {
			Response r = Handler.runDisablePayload();
			synchronized (Handler.handler) { 
				Handler.acceptResponse(r);
			}
		}
	}

}
