package edu.cornell.rocketry.sim;

import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.comm.receive.TEMResponse;
import edu.cornell.rocketry.comm.receive.TEMStatusFlag;
import edu.cornell.rocketry.gui.model.Datum;

public class ContinuousTEMSimulator implements TEMSimulator {
	
	private static final long MAX_FREQUENCY_DELAY = 200;
	private static final long MIN_FREQUENCY_DELAY = 5000;
	private static final long WAIT_DELAY = 10000;
	
	private Thread simWorker;
	
	private boolean temInitialized;
	private boolean gpsFix;
	private boolean cameraEnabled;
	private boolean transmitMaxFreq;
	private boolean launchReady;
	private boolean landed;
	
	private boolean CONTINUE_TRANSMITTING;
	
	private Receiver receiver;
	
	public ContinuousTEMSimulator (Receiver r) {
		receiver = r;
		CONTINUE_TRANSMITTING = false;
		
		temInitialized = false;
		gpsFix = false;
		cameraEnabled = false;
		transmitMaxFreq = false;
		launchReady = false;
		landed = false;
		
	}
	
	private void initWorker () {
		simWorker = new Thread(
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
						
						Datum d = null; //TODO
						TEMStatusFlag flag = new TEMStatusFlag();
						
						flag.set(TEMStatusFlag.Type.sys_init, temInitialized);
						flag.set(TEMStatusFlag.Type.gps_fix, gpsFix);
						flag.set(TEMStatusFlag.Type.camera_enabled, cameraEnabled);
						flag.set(TEMStatusFlag.Type.launch_ready, launchReady);
						flag.set(TEMStatusFlag.Type.landed, landed);
						flag.set(TEMStatusFlag.Type.transmit_freq_max, transmitMaxFreq);
						
						TEMResponse r = 
							new TEMResponse(
								d.lat(), d.lon(), d.alt(),
								flag.byteValue(),
								d.time(),
								d.rot(),
								d.acc_x(), d.acc_y(), d.acc_z());
						
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
		CONTINUE_TRANSMITTING = true;
		
	}

	@Override
	public void stopTransmitting() {
		CONTINUE_TRANSMITTING = false;
	}

	@Override
	public void transmitMaxFrequency() {
		transmitMaxFreq = true;
	}

	@Override
	public void transmitMinFrequency() {
		transmitMaxFreq = false;
	}

	@Override
	public void enableCamera() {
		cameraEnabled = true;
	}

	@Override
	public void disableCamera() {
		cameraEnabled = false;
	}

	@Override
	public void launchPrepare() {
		// TODO Auto-generated method stub

	}

	@Override
	public void launchCancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
