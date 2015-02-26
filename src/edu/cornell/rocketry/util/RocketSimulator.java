package edu.cornell.rocketry.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.comm.receive.TestReceiver;
import edu.cornell.rocketry.gui.Controller;

public class RocketSimulator {
	
	private String GPSfilepath;
	private File file = null;
	
	private Thread gworker;
	private Thread pworker;
	
	private int frequency; //frequency, in Hz.
	
	private int index;
	private ArrayList<Position> positions;
	
	private Receiver receiver;
	
	private byte GPSflag;
	//TODO: what flag to spoof? (set in constructors)
	
	public RocketSimulator (String path, Receiver r) {
		this.GPSfilepath = path;
		receiver = r;
		frequency = 1;
		index = 0;
		positions = new ArrayList<Position>();
		GPSflag = 0xf;
		readGPSFile();
	}
	
	public RocketSimulator (File f, Receiver r) {
		this.file = f;
		receiver = r;
		index = 0;
		positions = new ArrayList<Position>();
		GPSflag = 0xf;
		readGPSFile();
	}
	
	public void enablePayload(long requestStartTime) {
		final long st = requestStartTime;
		if (pworker != null) pworker.interrupt();
		pworker = new Thread(
			new Runnable () {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						return;
					}
					
					Random r = new Random();
					r.setSeed(System.currentTimeMillis());
					
					//probabilities of success
					int pr_success = 8;
					int pr_comm_fail = 9;
					int pr_unknown_fail = 10;
					
					int p = r.nextInt() % 10;
					
					if (p < pr_success) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.EnablePayload, true, et, "");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else if (p < pr_comm_fail) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.EnablePayload, false, et, "failure: could not connect");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else if (p < pr_unknown_fail) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.EnablePayload, false, et, "failure: unknown");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else throw new InternalError("no cases reached");
				}
			});
		pworker.start();		
	}
	
	public void disablePayload(long requestStartTime) {
		final long st = requestStartTime;
		if (pworker != null) pworker.interrupt();
		pworker = new Thread(
			new Runnable () {
				public void run() {
					int sleep_time = (int) Math.random() * 1000;
					try {
						Thread.sleep(sleep_time);
					} catch (InterruptedException ie) {
						return;
					}
					
					Random r = new Random();
					r.setSeed(System.currentTimeMillis());
					
					//probabilities of success
					int pr_success = 8;
					int pr_comm_fail = 9;
					int pr_unknown_fail = 10;
					
					int p = r.nextInt() % 10;
					
					if (p < pr_success) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.DisablePayload, true, et, "");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else if (p < pr_comm_fail) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.DisablePayload, false, et, "failure: could not connect");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else if (p < pr_unknown_fail) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.DisablePayload, false, et, "failure: unknown");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else throw new InternalError("no cases reached");
				}
			});
		pworker.start();
		
		
	}
	
	public void startGPS() {
		gworker = new Thread(
			new Runnable() {
				public void run() {
					long delay = (long) (1000.0 / getFrequency());
					Position p;
					for ( ; index < positions.size(); index++) {
						if (Thread.interrupted()) {
							return;
						}
						p = positions.get(index);
						GPSResponse r = 
							new GPSResponse (p.lat(), p.lon(), p.alt(), GPSflag, p.time());
						System.out.println("Receiver Object in gworker thread: " + receiver);
						synchronized (receiver) {
							receiver.acceptGPSResponse (r);
						}
						try {
							Thread.sleep(delay);
						} catch (InterruptedException ie) {
							return;
						}
					}
				}
			});
		gworker.start();
	}
	
	public void stopGPS() {
		if (gworker != null) gworker.interrupt();
	}
	
	public void resetGPS() {
		stopGPS();
		index = 0;
	}
	
	public void restartGPS() {
		resetGPS();
		startGPS();
	}
	
	
	private void readGPSFile () {
		parseCSV();
	}
	
	
	public int getFrequency() { 
		return frequency; 
	}
	
	public void setFrequency(int f) { 
		if (f > 0) 
		frequency = f; 
		else frequency = 1;
	}
	
	private void parseCSV () {
		try {
			Scanner sc;
			if (file == null)
				sc = new Scanner(new File (GPSfilepath));
			else sc = new Scanner(file);
			String line;
			String [] components;
			Position p;
			while (sc.hasNextLine()) {
				
				line = sc.nextLine();
				components = line.split(",");
				p = new Position (
					Double.parseDouble(components[0]),
					Double.parseDouble(components[1]),
					Integer.parseInt(components[2]),
					Long.parseLong(components[3]));
				positions.add(p);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file: " + GPSfilepath);
		}
	}
}
