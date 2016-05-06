package edu.cornell.rocketry.sim;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.comm.receive.TEMResponse;
import edu.cornell.rocketry.comm.receive.TEMStatusFlag;
import edu.cornell.rocketry.util.ErrorLogger;

public class ContinuousTEMSimulator implements TEMSimulator {
	
	private static final long MAX_FREQUENCY_DELAY = 200;
	private static final long MIN_FREQUENCY_DELAY = 5000;
	private static final long WAIT_DELAY = 10000;
	
	private Thread sim_worker;
	
	private boolean temInitialized;
	private boolean gpsFix;
	private boolean camera_enabled;
	private boolean transmitMaxFreq;
	private boolean launch_ready;
	private boolean landed;
	private boolean transmit_freq_max;
	
	private boolean CONTINUE_TRANSMITTING;
	
	private String simFilePath;
	private File simFile;
	
	private long timestamp;
	private double latitude, longitude, start_acc_x, start_acc_y, start_acc_z, delta_acc_x, delta_acc_y, delta_acc_z, wind_vel_x, wind_vel_y, wind_vel_z, rotation;
	
	private long startTime;
	
	private double newLat, newLong;
	
	private int altitude = 0;
	
	private Receiver receiver;
	
	public ContinuousTEMSimulator (String  path, Receiver r) {
		receiver = r;
		this.simFilePath = path;
		this.simFile = new File(path);
		CONTINUE_TRANSMITTING = false;
		
		transmit_freq_max = true;
		temInitialized = false;
		gpsFix = false;
		camera_enabled = false;
		transmitMaxFreq = false;
		launch_ready = false;
		landed = false;
		
		loadSimFile();
		initWorker();
		
		//throw new UnsupportedOperationException("ContinuousTEMSimulator not implemented!");
		
	}
	
	public ContinuousTEMSimulator (File f, Receiver r){
		receiver = r;
		this.simFile = f;
		this.simFilePath = f.getPath();
		CONTINUE_TRANSMITTING = false;
		
		transmit_freq_max = true;
		temInitialized = false;
		gpsFix = false;
		camera_enabled = false;
		transmitMaxFreq = false;
		launch_ready = false;
		landed = false;
		
		loadSimFile();
		initWorker();
		
		//throw new UnsupportedOperationException("ContinuousTEMSimulator not implemented!");
	}
	
	private void initWorker () {
		sim_worker = new Thread(
			new Runnable () {
				public void run () {
					for (;;) {
						long delay = 
							transmitMaxFreq 
							? MAX_FREQUENCY_DELAY 
							: MIN_FREQUENCY_DELAY;
						if (Thread.interrupted()) {
							if (!CONTINUE_TRANSMITTING) {
								try {
									Thread.sleep(WAIT_DELAY);
									continue;
								} catch (InterruptedException e) {
									continue;
								}
							}
						}
						
						TEMStatusFlag flag = new TEMStatusFlag();
						
						flag.set(TEMStatusFlag.Type.sys_init, temInitialized);
						flag.set(TEMStatusFlag.Type.gps_fix, gpsFix);
						flag.set(TEMStatusFlag.Type.camera_enabled, camera_enabled);
						flag.set(TEMStatusFlag.Type.launch_ready, launch_ready);
						flag.set(TEMStatusFlag.Type.landed, landed);
						flag.set(TEMStatusFlag.Type.transmit_freq_max, transmit_freq_max);
						
						long currentTime = System.currentTimeMillis();
						long elapsedTime = (currentTime - startTime) / 1000; //this is measured in seconds
						
						double delta_x_pos = 1.0 / 6.0 * delta_acc_x * Math.pow(elapsedTime, 3) + 0.5 * start_acc_x * Math.pow(elapsedTime, 2) + wind_vel_x * elapsedTime;
						double delta_y_pos = 1.0 / 6.0 * delta_acc_y * Math.pow(elapsedTime, 3) + 0.5 * start_acc_y * Math.pow(elapsedTime, 2) + wind_vel_y * elapsedTime;
						
						double delta_z_pos;
						if (start_acc_z + elapsedTime * delta_acc_z > -9.8)
							delta_z_pos = 1.0 / 6.0 * delta_acc_z * Math.pow(elapsedTime, 3) + 0.5 * start_acc_z * Math.pow(elapsedTime, 2) + wind_vel_z * elapsedTime;
						else
							delta_z_pos = -4.9 * Math.pow(elapsedTime, 2) + wind_vel_z * elapsedTime;
						
						altitude = (int) ((double) altitude + delta_z_pos);
						
						newLat += delta_x_pos / 111000.0;
						
						double one_deg_longitude_at_lat = 6371000.0 * Math.cos(newLat * Math.PI / 180.0);
						
						newLong += delta_y_pos / one_deg_longitude_at_lat;
						
						
						TEMResponse r = 
							new TEMResponse(
								newLat, newLong, altitude,
								flag.byteValue(),
								timestamp + elapsedTime,
								rotation,
								start_acc_x + elapsedTime * delta_acc_x, start_acc_y + elapsedTime * delta_acc_y, start_acc_z + elapsedTime * delta_acc_z);
						
						synchronized (receiver) {
							receiver.acceptTEMResponse(r);
						}
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							if (!CONTINUE_TRANSMITTING) {
								try {
									Thread.sleep(WAIT_DELAY);
									continue;
								} catch (InterruptedException ee) {
									continue;
								}
							}
						}
					}
				}
			});
	}

	@Override
	public void startTransmitting() {
		System.out.println("BasicTEMSimulator.startTransmitting() called");
		startTime = System.currentTimeMillis();
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
		// TODO Auto-generated method stub

	}
	
	private void loadSimFile(){
		try {
			Scanner sc;
			if (simFile != null)
				sc = new Scanner(simFile);
			else
				sc = new Scanner(new File(simFilePath));
			
			@SuppressWarnings("unused")
			String headers = sc.nextLine(); // you don't need to use the headers, but keep them in the file to easily create another one
			String data = sc.nextLine();
			
			String[] dataArray = data.split(",");
			timestamp = Long.parseLong(dataArray[0]);
			latitude = Double.parseDouble(dataArray[1]);
			longitude = Double.parseDouble(dataArray[2]);
			start_acc_x = Double.parseDouble(dataArray[3]);
			start_acc_y = Double.parseDouble(dataArray[4]);
			start_acc_z = Double.parseDouble(dataArray[5]);
			delta_acc_x = Double.parseDouble(dataArray[6]);
			delta_acc_y = Double.parseDouble(dataArray[7]);
			delta_acc_z = Double.parseDouble(dataArray[8]);
			wind_vel_x = Double.parseDouble(dataArray[9]);
			wind_vel_y = Double.parseDouble(dataArray[10]);
			wind_vel_z = Double.parseDouble(dataArray[11]);
			rotation = Double.parseDouble(dataArray[12]);
			
			newLat = latitude;
			newLong = longitude;
			
			
			
			
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			ErrorLogger.err("File " + simFilePath + " not found in continuousTEMsimulator.");
		}
		
	}

}
