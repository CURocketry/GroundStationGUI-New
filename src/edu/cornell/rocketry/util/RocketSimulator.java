package edu.cornell.rocketry.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import edu.cornell.rocketry.comm.receive.Receiver;

public class RocketSimulator {
	
	private String simfilepath;
	private File simfile;
	
	private Thread gps_worker;
	
	private int frequency; //frequency, in Hz.
	
	
	
	private int index;
	private ArrayList<Datum> data;
	
	private Receiver receiver;
	
	private boolean gps_fix;
	private boolean camera_enabled;
	private boolean transmit_freq; //true -> max freq; false -> min freq
	
	public RocketSimulator (String path, Receiver r) {
		this.simfilepath = path;
		this.simfile = new File(path);
		receiver = r;
		frequency = 1;
		index = 0;
		data = new ArrayList<Datum>();
		gps_fix = true;
		camera_enabled = false;
		transmit_freq = false;
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
		transmit_freq = false;
		loadSimFile();
	}
	
	private void sim_go(long requestStartTime) {
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
						//TODO: set gps_fix, camera_enabled, transmit_freq here
						StatusFlag flag = new StatusFlag();
						
						flag.set(StatusFlag.Type.gps_fix, gps_fix);
						flag.set(StatusFlag.Type.camera_enabled, camera_enabled);
						flag.set(StatusFlag.Type.transmit_freq, transmit_freq);
						
						TEMResponse r = 
							new TEMResponse (d.lat(), d.lon(), d.alt(), flag.byteValue(), d.time(), d.rot(), d.acc());
						System.out.println("Receiver Object in gworker thread: " + receiver);
						synchronized (receiver) {
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
		gps_worker.start();
	}
	
	private void sim_halt(long requestStartTime) {
		if (gps_worker != null) gps_worker.interrupt();
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
