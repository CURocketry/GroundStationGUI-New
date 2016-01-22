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
	
	private String simfilepath;
	private File simfile;
	
	private Thread gps_worker;
	private Thread cam_worker;
	
	private int frequency; //frequency, in Hz.
	
	private int index;
	private ArrayList<Datum> data;
	
	private Receiver receiver;
	
	private byte GPSflag;
	//TODO: what flag to spoof? (set in constructors)
	
	public RocketSimulator (String path, Receiver r) {
		this.simfilepath = path;
		this.simfile = new File(path);
		receiver = r;
		frequency = 1;
		index = 0;
		data = new ArrayList<Datum>();
		GPSflag = 0xf;
		loadSimFile();
	}
	
	public RocketSimulator (File f, Receiver r) {
		this.simfile = f;
		this.simfilepath = f.getPath();
		receiver = r;
		index = 0;
		data = new ArrayList<Datum>();
		GPSflag = 0xf;
		loadSimFile();
	}
	
	public void enableCamera(long requestStartTime) {
		final long st = requestStartTime;
		if (cam_worker != null) cam_worker.interrupt();
		cam_worker = new Thread(
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
						CommandResponse cre = new CommandResponse(CommandTask.EnableCamera, true, et, "");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else if (p < pr_comm_fail) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.EnableCamera, false, et, "failure: could not connect");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else if (p < pr_unknown_fail) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.EnableCamera, false, et, "failure: unknown");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}

					else throw new InternalError("edu.cornell.rocketry.util.RocketSimulator#enableCamera failed: no cases reached");
				}
			});
		cam_worker.start();		
	}
	
	public void disableCamera(long requestStartTime) {
		final long st = requestStartTime;
		if (cam_worker != null) cam_worker.interrupt();
		cam_worker = new Thread(
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
						CommandResponse cre = new CommandResponse(CommandTask.DisableCamera, true, et, "");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else if (p < pr_comm_fail) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.DisableCamera, false, et, "failure: could not connect");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else if (p < pr_unknown_fail) {
						long ft = System.currentTimeMillis();
						long et = ft - st;
						CommandResponse cre = new CommandResponse(CommandTask.DisableCamera, false, et, "failure: unknown");
						synchronized (receiver) { receiver.acceptCommandResponse(cre); }
					}
					
					else throw new InternalError("edu.cornell.rocketry.util.RocketSimulator#disableCamera failed: no cases reached");
				}
			});
		cam_worker.start();
		
	}
	
	private void sim_go(long requestStartTime) {
		final long st = requestStartTime;
		gps_worker = new Thread(
			new Runnable() {
				public void run() {
					long delay = (long) (1000.0 / getFrequency());
					Datum d;
					for ( ; index < data.size(); index++) {
						if (Thread.interrupted()) {
							return;
						}
						d = data.get(index);
						TEMResponse r = 
							new TEMResponse (d.lat(), d.lon(), d.alt(), GPSflag, d.time(), d.rot(), d.acc());
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
		gps_worker.start();
		
		long ft = System.currentTimeMillis();
		long et = ft - st;
		CommandResponse cre = new CommandResponse(CommandTask.TRANSMIT_START, true, et, "");
		synchronized (receiver) { receiver.acceptCommandResponse(cre); }
	}
	
	private void sim_halt(long requestStartTime) {
		final long st = requestStartTime;
		if (gps_worker != null) gps_worker.interrupt();
		
		long ft = System.currentTimeMillis();
		long et = ft - st;
		CommandResponse cre = new CommandResponse(CommandTask.TRANSMIT_HALT, true, et, "");
		synchronized (receiver) { receiver.acceptCommandResponse(cre); }
	}
	
	public void reset(long requestStartTime) {
		System.out.println("reset called");
		sim_halt(requestStartTime);
		index = 0;
	}
	
	public void restart(long requestStartTime) {
		System.out.println("restart called");
		reset(requestStartTime);
		sim_go(requestStartTime);
	}
	
	
	public int getFrequency() { 
		return frequency; 
	}
	
	public void setFrequency(int f) { 
		if (f > 0) 
		frequency = f; 
		else frequency = 1;
	}
	
	private void loadSimFile () {
		System.out.println("loading simulation file");
		try {
			Scanner sc;
			if (simfile == null)
				sc = new Scanner(new File (simfilepath));
			else sc = new Scanner(simfile);
			
			String line;
			String [] components;
			Datum d;
			while (sc.hasNextLine()) {
				
				line = sc.nextLine();
				components = line.split(",");
				
				if (components[0].trim().substring(0, 2).equals("//")) {
					continue;
				}
				
				d = new Datum (
					Double.parseDouble(components[1]),
					Double.parseDouble(components[2]),
					Integer.parseInt(components[3]),
					Long.parseLong(components[0]),
					Double.parseDouble(components[4]),
					Double.parseDouble(components[5])
				);
				data.add(d);
			}
			sc.close();
			Logger.debug("edu.cornell.rocketry.util.RocketSimulator#parseCSV: Loaded Data: " + data);
		} catch (Exception e) {
			Logger.err("edu.cornell.rocketry.util.RocketSimulator#parseCSV failed with exception: " + e.toString());
		}
	}
	
	public static void main (String[] args) {
		System.out.println(System.currentTimeMillis());
	}
}
