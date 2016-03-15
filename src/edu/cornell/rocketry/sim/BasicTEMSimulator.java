package edu.cornell.rocketry.sim;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import edu.cornell.rocketry.comm.TEMResponse;
import edu.cornell.rocketry.comm.TEMStatusFlag;
import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.gui.model.Datum;
import edu.cornell.rocketry.util.ErrorLogger;

public class BasicTEMSimulator implements TEMSimulator {
	
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
	private static boolean transmit_freq_max; //true -> max freq; false -> min freq3
	private static boolean launch_ready;
	
	private static boolean CONTINUE_TRANSMITTING = true;
	
	public BasicTEMSimulator (String path, Receiver r) {
		this.simfilepath = path;
		this.simfile = new File(path);
		receiver = r;
		index = 0;
		data = new ArrayList<Datum>();
		gps_fix = true;
		camera_enabled = false;
		transmit_freq_max = true;
		launch_ready = false;
		loadSimFile();
		initSimWorker();
	}
	
	public BasicTEMSimulator (File f, Receiver r) {
		this.simfile = f;
		this.simfilepath = f.getPath();
		receiver = r;
		index = 0;
		data = new ArrayList<Datum>();
		gps_fix = true;
		camera_enabled = false;
		transmit_freq_max = false;
		loadSimFile();
		initSimWorker();
	}
	
	@Override
	public void startTransmitting() {
		System.out.println("BasicTEMSimulator.startTransmitting() called");
		sim_worker.start();
	}
	
	@Override
	public void stopTransmitting () {
		CONTINUE_TRANSMITTING = false;
		sim_worker.interrupt();
	}
	
	
	public void setMaxFrequency () {
		CONTINUE_TRANSMITTING = true;
		transmit_freq_max = true;
		sim_worker.interrupt();
	}
	
	public void setMinFrequency () {
		CONTINUE_TRANSMITTING = true;
		transmit_freq_max = false;
		sim_worker.interrupt();
	}
	
	public void enableCamera () {
		camera_enabled = true;
		sim_worker.interrupt();
	}
	
	public void disableCamera () {
		camera_enabled = false;
		sim_worker.interrupt();
	}

	@Override
	public void transmitMaxFrequency() {
		transmit_freq_max = false;
		sim_worker.interrupt();
	}

	@Override
	public void transmitMinFrequency() {
		transmit_freq_max = false;
		sim_worker.interrupt();
	}

	@Override
	public void launchPrepare() {
		launch_ready = true;
		sim_worker.interrupt();
	}

	@Override
	public void launchCancel() {
		launch_ready = false;
		sim_worker.interrupt();
	}

	@Override
	public void reset() {
		stopTransmitting();
		index = 0;
		initSimWorker();
	}
	
	private void initSimWorker () {
		sim_worker = new Thread(
				new Runnable() {
					public void run() {
						System.out.println("sim_worker run called");
						long delay = transmit_freq_max ? MAX_FREQUENCY_DELAY : MIN_FREQUENCY_DELAY;
						Datum d;
						for ( ; index < data.size(); index++) {
							
							if (Thread.interrupted()) {
								System.out.println("Thread interrupted");
								if (!CONTINUE_TRANSMITTING) {
									return;
								}
								delay = transmit_freq_max ? MAX_FREQUENCY_DELAY : MIN_FREQUENCY_DELAY;
							}
							
							d = data.get(index);
							
							TEMStatusFlag flag = new TEMStatusFlag();
							
							flag.set(TEMStatusFlag.Type.sys_init, true);
							flag.set(TEMStatusFlag.Type.launch_ready, launch_ready);
							flag.set(TEMStatusFlag.Type.landed, false);
							flag.set(TEMStatusFlag.Type.gps_fix, gps_fix);
							flag.set(TEMStatusFlag.Type.camera_enabled, camera_enabled);
							flag.set(TEMStatusFlag.Type.transmit_freq_max, transmit_freq_max);
							
							TEMResponse r = 
								new TEMResponse (d.lat(), d.lon(), d.alt(), flag.byteValue(), d.time(), d.rot(), d.acc_x(), d.acc_y(), d.acc_z());
							synchronized (receiver) {
								receiver.acceptTEMResponse (r);
							}
							try {
								Thread.sleep(delay);
							} catch (InterruptedException ie) {
								Thread.currentThread().interrupt();
								System.out.println("Caught InterruptedException, calling Thread.currentThread.isInterrupted(): " + Thread.currentThread().isInterrupted());
								continue;
							}
						}
					}
				});
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
					Long.parseLong(components[0]),     //time
					Double.parseDouble(components[1]), //lat
					Double.parseDouble(components[2]), //lon
					Integer.parseInt(components[3]),   //alt
					Double.parseDouble(components[4]), //rotation
					Double.parseDouble(components[5]), //acc_x
					Double.parseDouble(components[6]), //acc_y
					Double.parseDouble(components[7]), //acc_z
					Double.parseDouble(components[8])  //temp
				);
				data.add(d);
			}
			sc.close();
		} catch (Exception e) {
			ErrorLogger.err("edu.cornell.rocketry.util.BasicTEMSimulator#loadSimFile failed with exception: " + e.toString());
		}
	}
}
