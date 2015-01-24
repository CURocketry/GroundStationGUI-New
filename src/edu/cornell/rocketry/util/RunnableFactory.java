package edu.cornell.rocketry.util;

import edu.cornell.rocketry.gui.Controller;

public class RunnableFactory {
	
	Controller handler;
	
	
	public RunnableFactory (Controller h) {
		handler = h;
	}
	
	/*
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
	
	/*
	private class StartRunnable implements Runnable {
		public void run () {
			CommandResponse r = handler.runStartSequence();
			synchronized (handler) { 
				handler.acceptCommandResponse(r);
			}
		}
	}
	
	private class StopRunnable implements Runnable {
		public void run () {
			CommandResponse r = handler.runStopSequence();
			synchronized (handler) { 
				handler.acceptCommandResponse(r);
			}
		}
	}
	
	private class EnableRunnable implements Runnable {
		public void run() {
			CommandResponse r = handler.runEnablePayload();
			synchronized (handler) { 
				handler.acceptCommandResponse(r);
			}
		}
	}
	
	private class DisableRunnable implements Runnable {
		public void run() {
			CommandResponse r = handler.runDisablePayload();
			synchronized (handler) { 
				handler.acceptCommandResponse(r);
			}
		}
	}
	*/
}
