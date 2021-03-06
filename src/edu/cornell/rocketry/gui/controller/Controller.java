package edu.cornell.rocketry.gui.controller;

//import jTile.src.org.openstreetmap.gui.jmapviewer.JMapViewer;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.math.plot.Plot3DPanel;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;

import edu.cornell.rocketry.comm.XBeeController;
import edu.cornell.rocketry.comm.receive.RealReceiver;
import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.comm.receive.TEMResponse;
import edu.cornell.rocketry.comm.receive.TEMStatusFlag;
import edu.cornell.rocketry.comm.receive.TestReceiver;
import edu.cornell.rocketry.comm.receive.TEMStatusFlag.Type;
import edu.cornell.rocketry.comm.send.Command;
import edu.cornell.rocketry.comm.send.CommandReceipt;
import edu.cornell.rocketry.comm.send.CommandType;
import edu.cornell.rocketry.comm.send.RealSender;
import edu.cornell.rocketry.comm.send.Sender;
import edu.cornell.rocketry.comm.send.TestSender;
import edu.cornell.rocketry.gui.model.ApplicationModel;
import edu.cornell.rocketry.gui.model.Datum;
import edu.cornell.rocketry.gui.model.RocketModel;
import edu.cornell.rocketry.gui.model.Position;
import edu.cornell.rocketry.gui.view.View;
import edu.cornell.rocketry.util.DataLogger;
import edu.cornell.rocketry.util.Status;
import edu.cornell.rocketry.util.LocalLoader;
import edu.cornell.rocketry.util.ErrorLogger;
import edu.cornell.rocketry.util.Pair;
import gnu.io.CommPortIdentifier;

public class Controller {
	public List<Pair<Long, MapMarker>> all_markers = new ArrayList<Pair<Long, MapMarker>>();
	
	public boolean testing;
	
	private DataLogger dataLogger;
	
	//view
	private View view;
	
	//for removing text from previous dot
	private MapMarkerDot prevDot;
	
	private Receiver testReceiver;
	private Receiver realReceiver;
	public Receiver receiver (boolean test) { return (test? testReceiver : realReceiver); }
	
	private Sender testSender;
	private Sender realSender;
	public Sender sender () { return (testing? testSender : realSender); }
	
	private RocketModel rocketModel;
	private ApplicationModel applicationModel;
	
	private XBeeController xbeeController;
	
	
	public Controller (View gui) {
		view = gui;
		rocketModel = new RocketModel();
		applicationModel = new ApplicationModel();
		testReceiver = new TestReceiver(this);
		//realReceiver = new RealReceiver(this); initialized w/ xbee initialization
		xbeeController = new XBeeController(this);
		testSender = new TestSender(this);
		realSender = new RealSender(this, xbeeController.getXbee(), applicationModel.getXbeeAddress());
		System.out.println("selectedAddress = " + applicationModel.getXbeeAddress());
		dataLogger = new DataLogger();
		//time,lat,lon,alt,rot,acc_x,acc_y,acc_z,temp,flag
		dataLogger.logHeader("//time,lat,lon,alt,rot,acc_x,acc_y,acc_z,temp,flag");
		
		System.out.println("Controller Initialized");
		
		testing = false;	//default for now	
	}
	
	/*------------------------- Getters & Setters ---------------------------*/
	
	public DataLogger dataLogger() { return dataLogger; }
	
	public XBeeController getXbeeController () { return xbeeController; }
	
	public View view () {
		return view;
	}
	
	public Sender getSender(boolean test) { 
		return test? testSender : realSender; 
	}
	
	public Receiver getReceiver (boolean test) {
		return test? testReceiver : realReceiver;
	}
	
	public void refreshDisplay () {
		//re-load markers on map
		Collection<Datum> all_rocket_data = rocketModel.getPastRocketData();
		updateRocketPositionFull(all_rocket_data);
		
		//set statuses at top of Control panel
		view.setCameraStatus(rocketModel.getCameraStatus());
		view.setLaunchStatus(rocketModel.getLaunchStatus());
		view.setLandedStatus(rocketModel.getLandedStatus());
		view.setInitializationStatus(rocketModel.getInitStatus());
		view.setGPSStatus(rocketModel.getGPSStatus());
		
	}
	
	
	/*------------------ Control & Tracking Update Methods ------------------*/
	
	/**
	 * Takes the data in rocketModel and plots it on screen
	 * 
	 * TODO: this is something that takes the a model and displays it on screen;
	 * TODO: should it be in Model, then?
	 * */
	void updateRocketTrajectory(){
		List<Datum> rocket_past_data = rocketModel.getPastRocketData();
		int nPositions = rocket_past_data.size();
		if (nPositions> 1){
			double[] lat = {rocket_past_data.get(nPositions-2).lat(), rocket_past_data.get(nPositions-1).lat()};
			double[] lon = {rocket_past_data.get(nPositions-2).lon(), rocket_past_data.get(nPositions-1).lon()};
			double[] alt = {rocket_past_data.get(nPositions-2).alt(), rocket_past_data.get(nPositions-1).alt()};
	
			Plot3DPanel plot = view.getTrajectoryPlot();
			plot.addLinePlot(
                "Rocket Trajectory",
                Color.red,
               	lat,lon,alt);

//        //Adjust bounds to keep them constant...
//        plot.setFixedBounds(0,model.minLat,model.maxLat);
//        plot.setFixedBounds(1,model.minLng,model.maxLng);
//        plot.setFixedBounds(2,model.minAlt,model.maxAlt);
		}
		
	} //FIXME
	//What needs to be fixed? I'm not actually sure.
	
	/** 
	 * Ensures that the map (visible both in the minimap in the control tab 
	 * and the recovery tab) is properly set to include the tiles from the
	 * current directory.
	 */
	public void addTilesToMap(File f) {
		//reset map to one with all of the tiles in cache.
		MemoryTileCache cache = (MemoryTileCache) view.map().getTileCache();
		TileSource source = view.map().getTileController().getTileSource();
		
		addTilesToCacheFromFile(cache, source, f);
		
		view.map().getTileController().setTileCache(cache);
		
		//re-add map markers
		refreshDisplay();
		System.out.println("Added tiles to map");
	}
	
	/** 
	 * add the tiles from the file to the cache
	 * 
	 * @param cache  the MemoryTileCache to write to
	 * @param source  the TileSource that... does something
	 * @param f  the directory that the tiles on your machine are in
	 */
	private void addTilesToCacheFromFile(final MemoryTileCache cache, final TileSource source, final File f) {
		final LinkedList<Tile> acc = new LinkedList<Tile>();
		
		Thread worker = new Thread() {
			public void run () {
				synchronized(cache) {					
					LocalLoader.buildTileList(acc, f, source);
					
					for (Tile t : acc) {
						cache.addTile(t);
					}
					
				}
			}
		};
		
		worker.start();
	}
	
	/**
	 * Removes all tiles from the current map's cache.
	 */
	public void resetMapTiles() {
		view.map().getTileController().setTileCache(new MemoryTileCache());
	}

	/**
	 * Add the given datum to the model (view?) TODO: perhaps migrate this to Model
	 * @param d  the Datum giving the time, latitude, and longitude of the added point
	 */
    void updateRocketPosition (Datum d) {
    	MapMarkerDot toAdd = new MapMarkerDot(""+Position.millisToTime(d.time()), new Coordinate(d.lat(), d.lon()));
    	view.addMapMarkerDot(toAdd);
    	if (prevDot != null) {
    		prevDot.setBackColor(Color.BLACK);
    		prevDot.setName("");
    	}
    	prevDot = toAdd;
    	updateRocketTrajectory(); //FIXME (Why? I'm not sure what's wrong with this)
    }
    
    /**
     * clear the screen and plot all the new data
     * (TODO: is this another thing to be moved to Model?)
     * 
     * @param data  all data to be added
     */
    void updateRocketPositionFull (Collection<Datum> data) {
    	clearMapMarkers();
    	for (Datum d : data) {
    		updateRocketPosition(d);
    	}
    }
    
    /* several functions that all update the model to the given status */
    private void updateCameraStatus (Status st) {
    	rocketModel.setCameraStatus(st);
    	view.setCameraStatus(rocketModel.getCameraStatus());
    }
    
    private void updateGPSStatus (Status st) {
    	rocketModel.setGPSStatus(st);
    	view.setGPSStatus(rocketModel.getGPSStatus());
    }
    
    private void updateLaunchStatus (Status st) {
    	rocketModel.setLaunchStatus(st);
    	view.setLaunchStatus(st);
    }
    
    private void updateLandedStatus (Status st) {
    	rocketModel.setLandedStatus(st);
    	view.setLandedStatus(st);
    }
    
    private void updateInitializationStatus (Status st) {
    	rocketModel.setInitStatus(st);
    	view.setInitializationStatus(st);
    }
    
    /**
     * clear the view of all map markers
     */
    public void clearMapMarkers () {
    	view.clearMapMarkers();
    }
    
    public void sendCommand (CommandType type) {
    	Command c = new Command(type, System.currentTimeMillis());
    	sender().send(c);
    }
    
	
	
	/**
	 * receive and process an ACK from the rocket for a command that we sent
	 * 
	 * @param r  the CommandReceipt that we just received 
	 */
	public synchronized void acceptCommandReceipt (CommandReceipt r) {
		//display receipt
		String message;
		if(r.success()) {
			message = r.type().toString() + " successfully sent.";
		} else {
			message = "COULD NOT SEND " + r.type().toString() + 
					". \n -> " + r.message();
		}
		ilog("\nCommand Receipt Received:");
		ilog(message);
		
		//process receipt
		if (r.type() == CommandType.ENABLE_CAMERA
			|| r.type() == CommandType.DISABLE_CAMERA) {
			updateCameraStatus(Status.BUSY);
		} 
		
		if (r.type() == CommandType.BEGIN_LAUNCH
			|| r.type() == CommandType.CANCEL_LAUNCH) {
			updateLaunchStatus(Status.BUSY);
		}
	}
	

	/**
	 * receive and process a data packet from the TEM
	 * 
	 * @param r  the TEMResponse
	 * @param test  whether or not we're in testing mode
	 */
	public synchronized void acceptTEMResponse (TEMResponse r, boolean test) {
		ilog("\nTEM Response Received:");
		
		//process flag
		TEMStatusFlag flag = r.flag();
		
		if (flag.isSet(Type.camera_enabled)) {
			updateCameraStatus(Status.ENABLED);
		} else {
			updateCameraStatus(Status.DISABLED);
		}
		
		if (flag.isSet(Type.gps_fix)) {
			updateGPSStatus(Status.ENABLED);
		} else {
			updateGPSStatus(Status.DISABLED);
		}
		
		if (flag.isSet(Type.launch_ready)) {
			updateLaunchStatus(Status.ENABLED);
		} else {
			updateLaunchStatus(Status.DISABLED);
		}
		
		if (flag.isSet(Type.landed)) {
			updateLandedStatus(Status.ENABLED);
		} else {
			updateLandedStatus(Status.DISABLED);
		}
		
		if (flag.isSet(Type.sys_init)) {
			updateInitializationStatus(Status.ENABLED);
		} else {
			updateInitializationStatus(Status.DISABLED);
		}
		
		//log data to file
		//time,lat,lon,alt,rot,acc_x,acc_y,acc_z,temp,flag
		StringBuilder sb = new StringBuilder();
		sb
			.append(r.time()).append(",")
			.append(r.lat()).append(",")
			.append(r.lon()).append(",")
			.append(r.alt()).append(",")
			.append(r.rot()).append(",")
			.append(r.acc_x()).append(",")
			.append(r.acc_y()).append(",")
			.append(r.acc_z()).append(",")
			.append(r.temp()).append(",")
			.append(r.flag().toHexString());
		
		dataLogger.log(sb.toString());
		
		updateAnalyticsDisplayFields
			(r.lat(), 
			r.lon(), 
			r.alt(), 
			r.time(), 
			r.rot(), 
			r.acc_x(), 
			r.acc_y(), 
			r.acc_z(),
			r.temp());
		
		if (gpsCheck(r)) {
			ilog("(" + r.lat() + ", " + r.lon() + ", " + r.alt() + ")");
			ilog("gps time: " + Position.millisToTime(r.time()) + " ms");
			// Update model
			rocketModel.update(r.createDatum());
			updateRocketPosition (rocketModel.getCurrentDatum());
			String posn = "(" + r.lat() + ", " + r.lon() + ")";
			view.updateLatestPosition(posn);
			if (!test) updateXBeeDisplayFields (
				""+r.lat(),""+r.lon(),""+r.alt(),""+r.flag());
			
			MapMarker m = new MapMarkerDot(r.lat(), r.lon());
			Pair<Long, MapMarker> p = new Pair<Long, MapMarker>(r.time(), m);
			all_markers.add(p);
			view.map().addMapMarker(m);
		} else {
			ilog("inaccurate gps data received");
		}
	}
		
	
	/**
	 * Returns whether or not the given GPSResponse contains 
	 * reasonable coordinates (e.g. (0,0,0) will be rejected).
	 * @param r  the TEMResponse to be evaluated
	 * @return  whether or not the GPS is in a reasonable range 
	 */
	private boolean gpsCheck (TEMResponse r) {
		//TODO: don't hard-code numbers
		//return true;
		//NORTHEAST:
		return (
			r.lat() < 45 && r.lat() > 40 &&
			r.lon() > -80 && r.lon() < -70);
		//ALABAMA (Huntsville):
		// return (
		//  	r.lat() < 36 && r.lat() > 34 &&
		//  	r.lon() > -88 && r.lon() < -85);
	}
	
	
	
	/** Logs the given string to the text info panel in
	 * the Control tab of the main window.
	 * 
	 * @param s  the string to be logged
	 */
	private void ilog (String s) {
		view.controlLog("- " + s);
		System.out.println("logged: " + s);
	}
	
	/**
	 * create a new TestSender (throwing out any old ones)
	 * 
	 * @param f
	 */
	public void resetTestSender (File f) {
		testSender = new TestSender(this, f);
	}
	
	/*--------------------- Analytics Methods -----------------------*/
	public void updateAnalyticsDisplayFields 
			(double latitude, 
			double longitude, 
			double altitude, 
			long time, 
			double rotation, 
			double acceleration_x,
			double acceleration_y,
			double acceleration_z,
			double temp) {
		view.updateAnalytics
			(latitude, 
			longitude, 
			altitude, 
			time, 
			rotation, 
			acceleration_x,
			acceleration_y,
			acceleration_z,
			temp);
	}


	/*------------------------ XBee Methods -------------------------*/
	
	
	
	public void updateXBeeDisplayFields (String lat, String lon, String alt, String flag) {
		view.updateXBeeData(lat, lon, alt, flag);
	}
	

	/**
	 * update the Serial Port List (i.e. after a refresh)
	 * 
	 * Note: this directly updates the view, without referencing the Model
	 * TODO: add this to the Model
	 * @void
	 */
	public void updateSerialPortsList() {
		ArrayList<String> comboBoxList = new ArrayList<String>();
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();// this line was false
		//TODO: What does "This line was false" mean?
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				comboBoxList.add(portId.getName());
				// System.out.println(portId.getName());
			} else {
				// System.out.println(portId.getName());
			}
		}
		
		view.updateSerialPortsList(comboBoxList);
	}
	
	/*several functions that set variables in the model*/
	public void setSerialPort(String port) {
		applicationModel.setSerialPort(port);
	}

	public void setSelectedAddress(XBeeAddress64 addr) {
		realSender = new RealSender(this, xbeeController.getXbee(), addr);
		applicationModel.setXbeeAddress(addr);
	}
	
	public void updateSelectedBaudRate(int rate) {
		applicationModel.setBaudRate(rate);
	}

	/**
	 * setup anything that the XBee needs to run
	 * 
	 * @throws XBeeException
	 */
    public void initXbee() throws XBeeException {

		// get selected serial port...
//		String selSerial = (String) view.serialPortsList.getSelectedItem();
    	
    	realReceiver = new RealReceiver(this);
		
		int baudRate = applicationModel.getBaudRate();
		String selSerial = applicationModel.getSerialPort();
		
		System.out.println("Initializing XBee");
		
		xbeeController.openXBee(selSerial, baudRate);
		
		view.resetPacketCounters();
	}

    /**
     * send a message. Note that although we're sending a String, it's
     * actually a stream of bytes, not characters
     * 
     * @param msg
     */
	public void sendXBeePacket(String msg) {
		sender().send(msg);
	}
	
    /**
     * Limits the MapMarkers that are visible on the screen. 
     * Any MapMarker that was received after start_time and before 
     * end_time will be displayed; the others will not.
     * 
     * That is, take in a time interval and replot on the view
     * only the markers that fall within the time interval
     * 
     * @param start  the starting time
     * @param end  the ending time
     */
    public void limitMapMarkers (long start, long end) {
    	long start_time = System.currentTimeMillis() - start*1000;
    	long end_time = System.currentTimeMillis() - end*1000;
    	List<MapMarker> filtered_markers = new ArrayList<MapMarker>();
    	for (Pair<Long, MapMarker> p : all_markers) {
    		long time = p.left().longValue();
    		MapMarker marker = p.right();
    		if (time > start_time && time < end_time) {
    			filtered_markers.add(marker);
    		}
    	}
    	view.map().setMapMarkerList(filtered_markers);
    }
    
    /**
     * Resets all data in the model and clears the view
     */
    public void clearData () {
    	//clear model
    	rocketModel = new RocketModel();
    	//clear map markers
    	view.map().setMapMarkerList(new ArrayList<MapMarker>());
    	//clear 3d plot
    	view.resetTrajectoryPlot();
    	//clear analytics tab
    	view.initializeAnalyticsTab();
    	
    	refreshDisplay();
    	
    }

    /**
     * Cleanup and closing code to be run upon normal application exit.
     * 
     * In particular, this just closes the data logger
     */
    public void onClose () {
    	ErrorLogger.info("Application closing...");
    	dataLogger().close();
    	ErrorLogger.info("Data logger closed");
    	ErrorLogger.info("Application closed");
    }
}