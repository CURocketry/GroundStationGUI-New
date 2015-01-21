package edu.cornell.rocketry.gui;

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
			CommandResponse r = Handler.handler.runStartSequence();
			synchronized (Handler.handler) { 
				Handler.handler.acceptCommandResponse(r);
			}
		}
	}
	
	private class StopRunnable implements Runnable {
		public void run () {
			CommandResponse r = Handler.handler.runStopSequence();
			synchronized (Handler.handler) { 
				Handler.handler.acceptCommandResponse(r);
			}
		}
	}
	
	private class EnableRunnable implements Runnable {
		public void run() {
			CommandResponse r = Handler.handler.runEnablePayload();
			synchronized (Handler.handler) { 
				Handler.handler.acceptCommandResponse(r);
			}
		}
	}
	
	private class DisableRunnable implements Runnable {
		public void run() {
			CommandResponse r = Handler.handler.runDisablePayload();
			synchronized (Handler.handler) { 
				Handler.handler.acceptCommandResponse(r);
			}
		}
	}

}
