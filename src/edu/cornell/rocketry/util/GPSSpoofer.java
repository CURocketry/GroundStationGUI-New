package edu.cornell.rocketry.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.comm.receive.TestReceiver;
import edu.cornell.rocketry.gui.Controller;

public class GPSSpoofer {
	
	private String path;
	
	private Thread worker;
	
	private int frequency; //frequency, in Hz.
	
	private int index;
	private ArrayList<Position> positions;
	
	private Receiver receiver;
	
	private byte flag;
	//TODO: what flag to spoof? (set in constructors)
	
	public GPSSpoofer (String path, Receiver r) {
		this.path = path;
		receiver = r;
		frequency = 1;
		index = 0;
		positions = new ArrayList<Position>();
		flag = 0xf;
		readFile();
	}
	
	public GPSSpoofer (String path, Receiver r, int frequency) {
		this.path = path;
		receiver = r;
		this.frequency = frequency;
		index = 0;
		positions = new ArrayList<Position>();
		flag = 0xf;
		readFile();
	}
	
	
	public void start() {
		System.out.println("Receiver Object in main thread: " + receiver);
		worker = new Thread(
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
							new GPSResponse (p.lat(), p.lon(), p.alt(), flag, p.time());
						System.out.println("Receiver Object in worker thread: " + receiver);
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
		worker.start();
	}
	
	public void stop() {
		if (worker != null) worker.interrupt();
	}
	
	public void reset() {
		if (worker != null) worker.interrupt();
		index = 0;
	}
	
	public void restart() {
		reset();
		start();
	}
	
	
	private void readFile () {
		parseCSV ();
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
			Scanner sc = new Scanner (new File (path));
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
			System.out.println("Could not find file: " + path);
		}
	}
}
