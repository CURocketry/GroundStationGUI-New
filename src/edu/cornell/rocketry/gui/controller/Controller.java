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

import com.rapplogic.xbee.api.XBeeException;

import edu.cornell.rocketry.comm.Command;
import edu.cornell.rocketry.comm.CommandReceipt;
import edu.cornell.rocketry.comm.CommandType;
import edu.cornell.rocketry.comm.TEMResponse;
import edu.cornell.rocketry.comm.TEMStatusFlag;
import edu.cornell.rocketry.comm.XBeeController;
import edu.cornell.rocketry.comm.TEMStatusFlag.Type;
import edu.cornell.rocketry.comm.receive.RealReceiver;
import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.comm.receive.TestReceiver;
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
	public Receiver receiver(boolean test) { return (test? testReceiver : realReceiver); }
	
	private Sender testSender;
	private Sender realSender;
	public Sender sender () { return (testing? testSender : realSender); }
	
	private RocketModel rocketModel;
	private ApplicationModel applicationModel;
	
	private XBeeController xbeeController;
	
	
	public Controller (View gui) {
		view = gui;
		model = new RocketModel();
		testReceiver = new TestReceiver(this);
		realReceiver = new RealReceiver(this);
		xbeeController = new XBeeController(this);
		testSender = new TestSender(this);
		realSender = new RealSender(this, xbeeController.xbee(), view.selectedAddress);
		System.out.println("selectedAddress = " + view.selectedAddress);
		dataLogger = new DataLogger();
		dataLogger.log("time,lat,lon,alt");
		
		System.out.println("Controller Initialized");
		
		testing = false;	//default for now	
	}
	
	/*------------------------- Getters & Setters ---------------------------*/
	
	public DataLogger logger() { return dataLogger; }
	
	public XBeeController commController() { return xbeeController; }
	
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
		Collection<Datum> all_rocket_data = model.pastRocketData();
		updateRocketPositionFull(all_rocket_data);
		
		view.setCameraStatus(model.cameraStatus());
		
	}
	
	
	/*------------------ Control & Tracking Update Methods ------------------*/
	
	void updateRocketTrajectory(){
		List<Datum> rocket_past_data = model.pastRocketData();
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
	
	/** 
	 * Ensures that the map (present in the minimap in the control tab 
	 * and the recovery tab) is properly set to include the tiles from
	 * the current directory.
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

    void updateRocketPosition (Datum d) {
    	MapMarkerDot toAdd = new MapMarkerDot(""+Position.millisToTime(d.time()), new Coordinate(d.lat(), d.lon()));
    	view.addMapMarkerDot
    		(toAdd);
    	if (prevDot != null) {
    		prevDot.setBackColor(Color.BLACK);
    		prevDot.setName("");
    	}
    	prevDot = toAdd;
    	updateRocketTrajectory(); //FIXME
    }
    
    void updateRocketPositionFull (Collection<Datum> data) {
    	clearMapMarkers();
    	for (Datum d : data) {
    		updateRocketPosition(d);
    	}
    }
    
    void updateCameraStatus (Status st) {
    	model.setCameraStatus(st);
    	view.setCameraStatus(model.cameraStatus());
    }
    
    void updateGPSStatus (Status st) {
    	model.setGPSStatus(st);
    	view.setGPSStatus(model.gpsStatus());
    }
    
    public void clearMapMarkers () {
    	view.clearMapMarkers();
    }
    
    public void sendCommand (CommandType type) {
    	Command c = new Command(type, System.currentTimeMillis());
    	sender().send(c);
    }
    
    
	
	
	public synchronized void acceptCommandReceipt (CommandReceipt r) {
		//display receipt
		String message = 
			r.success() ? 
				(r.type().toString() + " successfully sent.") :
				("COULD NOT SEND " + r.type().toString() + 
					". \n -> " + r.message());
		ilog("\nCommand Receipt Received:");
		ilog(message);
		
		//process receipt
		if (r.type() == CommandType.ENABLE_CAMERA
			|| r.type() == CommandType.DISABLE_CAMERA) {
			updateCameraStatus(Status.BUSY);
		}
	}
	
	public synchronized void acceptTEMResponse (TEMResponse r, boolean test) {
		ilog("\nTEM Response Received:");
		
		//process flag
		TEMStatusFlag flag = r.flag();
		
		if (flag.isSet(Type.camera_enabled)) {
			view.setCameraStatus(Status.ENABLED);
		} else {
			view.setCameraStatus(Status.DISABLED);
		}
		
		if (flag.isSet(Type.gps_fix)) {
			view.setGPSStatus(Status.ENABLED);
		} else {
			view.setGPSStatus(Status.DISABLED);
		}
		
		if (flag.isSet(Type.transmit_freq_max)) {
			//TODO: make transmission frequency indicator
		} else {
			//same as above 
		}
		
		if (gpsCheck(r)) {
			ilog("(" + r.lat() + ", " + r.lon() + ", " + r.alt() + ")");
			ilog("gps time: " + Position.millisToTime(r.time()) + " ms");
			// Update model
			model.update(r.create_datum());
			updateRocketPosition (model.currentDatum());
			String posn = "(" + r.lat() + ", " + r.lon() + ")";
			view.updateLatestPosition(posn);
			if (!test) updateXBeeDisplayFields (
				""+r.lat(),""+r.lon(),""+r.alt(),""+r.flag());
			if (!test) dataLogger.log(
				System.currentTimeMillis()+","+r.lat()+","+r.lon()+","
					+r.alt()+","+r.rot()+","+r.acc_x()+","+r.acc_y()+","+r.acc_z());
			
			MapMarker m = new MapMarkerDot(r.lat(), r.lon());
			Pair<Long, MapMarker> p = new Pair<Long, MapMarker>(r.time(), m);
			all_markers.add(p);
			view.map().addMapMarker(m);

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
		} else {
			ilog("inaccurate data received");
		}
		
		if (!test) dataLogger.log(
				""+System.currentTimeMillis()+","+r.lat()+","+r.lon()+","+r.alt());
	}
		
	
	/**
	 * Returns whether or not the given GPSResponse contains 
	 * reasonable coordinates (e.g. (0,0,0) will be rejected).
	 * @param r the GPSResponse to be evaluated
	 * @return
	 */
	private boolean gpsCheck (TEMResponse r) {
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
	 * @param s
	 */
	private void ilog (String s) {
		view.controlLog("- " + s);
		System.out.println("logged: " + s);
	}
	
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
	 * updated the Serial Port List (i.e. after a refresh)
	 * @void
	 */
	public void updateSerialPortsList() {
		ArrayList<String> comboBoxList = new ArrayList<String>();
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();// this line was false
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				comboBoxList.add(portId.getName());
				// System.out.println(portId.getName());
			} else {
				// System.out.println(portId.getName());
			}
		}

		// update list...
		view.serialPortsList.removeAllItems();
		for (String s : comboBoxList) {
			view.serialPortsList.addItem(s);
		}
	}

	public void setSelectedAddress(XBeeAddress64 addr) {
		view.selectedAddress = View.addr[view.addressesList.getSelectedIndex()]; //set active address
		applicationModel.setSelectedAddress(addr);
	}
	
	public void updateSelectedBaudRate() {
		view.selectedBaud = (int) view.baudList.getSelectedItem(); //set active rate
	}

    public void initXbee() throws XBeeException {

		// get selected serial port...
		String selSerial = (String) view.serialPortsList.getSelectedItem();
		
		
		System.out.println("Initializing XBee");
		

		System.out.println(selSerial);
		xbeeController.openXBee(selSerial, view.selectedBaud); //open port
		
		//don't just do this by default!
		//commController.startListening();
		
		view.resetPacketCounters();
	}

	public void sendXBeePacket(String msg) {
		
		sender().send(msg);
		
	}
	
    /**
     * Limits the MapMarkers that are visible on the screen. 
     * Any MapMarker that was received after start_time and before 
     * end_time will be displayed; the others will not.
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
     * Clears all data from the screen, functionally resetting the application
     */
    public void clearData () {
    	//clear map markers
    	view.map().setMapMarkerList(new ArrayList<MapMarker>());
    	//clear 3d plot
    	view.resetTrajectoryPlot();
    	//clear analytics tab
    	view.initializeAnalyticsTab();
    	
    }
}