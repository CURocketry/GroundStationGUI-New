package edu.cornell.rocketry.gui;

import java.util.LinkedList;

import javax.swing.ImageIcon;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;

import edu.cornell.rocketry.comm.receive.RealReceiver;
import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.comm.receive.TestReceiver;
import edu.cornell.rocketry.comm.send.RealSender;
import edu.cornell.rocketry.comm.send.Sender;
import edu.cornell.rocketry.comm.send.TestSender;
import edu.cornell.rocketry.gui.Model;
import edu.cornell.rocketry.util.CommandReceipt;
import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.CommandTask;
import edu.cornell.rocketry.util.GPSResponse;
import edu.cornell.rocketry.util.Position;
import edu.cornell.rocketry.util.PayloadStatus;
import edu.cornell.rocketry.util.RunnableFactory;
import edu.cornell.rocketry.xbee.XBeeListenerThread;

public class Controller {
	
	private Thread worker;
	
	public boolean testing;
	
	//view
	private GSGui mainWindow;
	
	private RunnableFactory r;
	
	private Receiver testReceiver;
	private Receiver realReceiver;
	public Receiver receiver(boolean test) { return (test? testReceiver : realReceiver); }
	
	private Sender testSender;
	private Sender realSender;
	public Sender sender() { return (testing? testSender : realSender); }
	
	private Model testModel;
	private Model realModel;
	public Model model(boolean test) { return (test? testModel : realModel); }
	
	public Controller (GSGui gui) {
		mainWindow = gui;
		testModel = new Model();
		realModel = new Model();
		testReceiver = new TestReceiver(this);
		realReceiver = new RealReceiver(this, mainWindow.xbeeListener);
		testSender = new TestSender(this);
		realSender = new RealSender(this);
		r = new RunnableFactory(this);
		//xbee = new XBee();
		
		testing = true;	//default for now	
	}
	
	/*------------------------- Getters & Setters ---------------------------*/
	
	public Sender getSender(boolean test) { 
		return test? testSender : realSender; 
	}
	
	public Receiver getReceiver (boolean test) {
		return test? testReceiver : realReceiver;
	}
	
	
	/*------------------ Control & Tracking Update Methods ------------------*/

    void updateRocketPosition (Position p) {
    	mainWindow.addMapMarkerDot
    		(""+Position.millisToTime(p.time()), p.lat(), p.lon());
    }
    
    void updateRocketPositionFull (LinkedList<Position> ps) {
    	mainWindow.clearMapMarkers();
    	for (Position p : ps) {
    		updateRocketPosition(p);
    	}
    }
    
    void updatePayloadStatus (PayloadStatus st) {
    	ImageIcon i;
    	switch (st) {
    	case Enabled:
    		i = new ImageIcon("./assets/green_square_20_20");
    		break;
    	case Busy:
    		i = new ImageIcon("./assets/yellow_square_20_20");
    		break;
    	case Disabled:
    		i = new ImageIcon("./assets/red_square_20_20");
    		break;
    	default:
    		throw new IllegalArgumentException (st.toString());
    	}
    	mainWindow.payloadStatus.setIcon(i);
    }
    
    public void clearMapMarkers () {
    	mainWindow.clearMapMarkers();
    }
    
    public void sendCommand (CommandTask task) {
    	sender().send(task, null);
    }
	
	
	public synchronized void acceptCommandReceipt (CommandReceipt r) {
		String message = 
			r.success() ? 
				(r.task().toString() + " successfully sent.") :
				("COULD NOT SEND " + r.task().toString() + 
					". \n -> " + r.message());
		ilog("\nCommand Response Received:");
		ilog(message);
	}
	
	public synchronized void acceptCommandResponse (CommandResponse r, boolean test) {
		ilog("\nCommand Response Received:");
		ilog(r.task().toString());
		ilog(r.successful() ? "Successful" : "Unsuccessful");
		ilog(r.message());
		ilog("elapsed time: " + r.time() + " ms");
		
	}
	
	public synchronized void acceptGPSResponse (GPSResponse r, boolean test) {
		ilog("\nGPS Response Received:");
		if (gpsCheck(r)) {
			ilog("(" + r.lat() + ", " + r.lon() + ", " + r.alt() + ")");
			ilog("gps time: " + Position.millisToTime(r.time()) + " ms");
			// Update model
			model(test).updatePosition(r.lat(), r.lon(), r.alt(), r.time());
			updateRocketPosition (model(test).position());
			updateXBeeDisplayFields (
				""+r.lat(),""+r.lon(),""+r.alt(),""+r.flag());
		} else {
			ilog("inaccurate data received");
		}
	}
	
	
	/**
	 * Returns whether or not the given GPSResponse contains 
	 * reasonable coordinates (e.g. (0,0,0) will be rejected).
	 * @param r the GPSResponse to be evaluated
	 * @return
	 */
	private boolean gpsCheck (GPSResponse r) {
		//TODO
		return true;
	}
	
	/** Logs the given string to the text info panel in
	 * the Control tab of the main window.
	 * @param s
	 */
	private void ilog (String s) {
		mainWindow.controlLog("- " + s);
		System.out.println("logged: " + s);
	}
	
	/*------------------------ XBee Methods -------------------------*/
	
	
	
	public void updateXBeeDisplayFields (String lat, String lon, String alt, String flag) {
		mainWindow.updateXBeeData(lat, lon, alt, flag);
	}
	
}
