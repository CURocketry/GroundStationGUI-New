package edu.cornell.rocketry.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import edu.cornell.rocketry.comm.receive.Receiver;

public class RocketSimulator {
	
	private static final long MAX_FREQUENCY_DELAY = 200;
	private static final long MIN_FREQUENCY_DELAY = 5000;
	
	private String simfilepath;
	private File simfile;
	
	private Thread sim_worker;
	
	private int index;
	private ArrayList<Datum> data;
	
	private Receiver receiver;
	
	private static boolean gps_fix;
	private static boolean camera_enabled;
	private static boolean transmit_freq_max; //true -> max freq; false -> min freq
	
	private static boolean CONTINUE_TRANSMITTING = true;
	
	public RocketSimulator (String path, Receiver r) {
		this.simfilepath = path;
		this.simfile = new File(path);
		receiver = r;
		index = 0;
		data = new ArrayList<Datum>();
		gps_fix = true;
		camera_enabled = false;
		transmit_freq_max = false;
		loadSimFile();
	}
	
	public RocketSimulator (File f, Receiver r) {
		this.simfile = f;
		this.simfilepath = f.getPath();
		receiver = r;
		index = 0;
		data = new ArrayList<Datum>();
		gps_fix = true;
		camera_enabled = false;
		transmit_freq_max = false;
		loadSimFile();
	}
	
	private void sim_go(long requestStartTime) {
		System.out.println("calling sim_go");
		sim_worker = new Thread(
			new Runnable() {
				public void run() {
					System.out.println("sim_worker run called");
					long delay = transmit_freq_max ? MAX_FREQUENCY_DELAY : MIN_FREQUENCY_DELAY;
					Datum d;
					System.out.println("starting for loop");
					for ( ; index < data.size(); index++) {
						
						if (Thread.interrupted()) {
							if (!CONTINUE_TRANSMITTING) {
								return;
							}
							delay = transmit_freq_max ? MAX_FREQUENCY_DELAY : MIN_FREQUENCY_DELAY;
						}
						
						d = data.get(index);
						
						StatusFlag flag = new StatusFlag();
						
						flag.set(StatusFlag.Type.gps_fix, gps_fix);
						flag.set(StatusFlag.Type.camera_enabled, camera_enabled);
						flag.set(StatusFlag.Type.transmit_freq_max, transmit_freq_max);
						
						TEMResponse r = 
							new TEMResponse (d.lat(), d.lon(), d.alt(), flag.byteValue(), d.time(), d.rot(), d.acc_x(), d.acc_y(), d.acc_z());
						System.out.println("Receiver Object in gworker thread: " + receiver);
						synchronized (receiver) {
							System.out.println("sending response to receiver");
							receiver.acceptTEMResponse (r);
						}
						try {
							Thread.sleep(delay);
						} catch (InterruptedException ie) {
							return;
						}
					}
				}
			});
		System.out.println("starting sim_go");
		sim_worker.start();
	}
	
	private void sim_halt(long requestStartTime) {
		if (sim_worker != null) sim_worker.interrupt();
	}
	
	public void reset(long requestStartTime) {
		CONTINUE_TRANSMITTING = false;
		System.out.println("reset called");
		sim_halt(requestStartTime);
		index = 0;
	}
	
	public void restart(long requestStartTime) {
		CONTINUE_TRANSMITTING = true;
		System.out.println("restart called");
		reset(requestStartTime);
		sim_go(requestStartTime);
	}
	
	
	public void setMaxFrequency () {
		CONTINUE_TRANSMITTING = true;
		transmit_freq_max = true;
		if (sim_worker != null) sim_worker.interrupt();
	}
	
	public void setMinFrequency () {
		CONTINUE_TRANSMITTING = true;
		transmit_freq_max = false;
		if (sim_worker != null) sim_worker.interrupt();
	}
	
	public void enableCamera () {
		camera_enabled = true;
	}
	
	public void disableCamera () {
		camera_enabled = false;
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
				
				System.out.println("loading simulation file line");
				
				line = sc.nextLine();
				components = line.split(",");
				
				if (components[0].trim().substring(0, 2).equals("//")) {
					continue;
				}
				
				d = new Datum (
					Double.parseDouble(components[1]), //lat
					Double.parseDouble(components[2]), //lon
					Integer.parseInt(components[3]),   //alt
					Long.parseLong(components[0]),     //time
					Double.parseDouble(components[4]), //rotation
					Double.parseDouble(components[5]), //acc_x
					Double.parseDouble(components[6]), //acc_y
					Double.parseDouble(components[7]), //acc_z
					Double.parseDouble(components[8]) //temp
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
