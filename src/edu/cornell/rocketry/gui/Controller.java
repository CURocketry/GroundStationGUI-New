package edu.cornell.rocketry.gui;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.ImageIcon;

//import org.math.plot.Plot3DPanel; //FIXME







import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;

import edu.cornell.rocketry.comm.receive.RealReceiver;
import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.comm.receive.TestReceiver;
import edu.cornell.rocketry.comm.send.RealSender;
import edu.cornell.rocketry.comm.send.Sender;
import edu.cornell.rocketry.comm.send.TestSender;
import edu.cornell.rocketry.gui.Model;
import edu.cornell.rocketry.util.Command;
import edu.cornell.rocketry.util.CommandReceipt;
import edu.cornell.rocketry.util.CommandResponse;
import edu.cornell.rocketry.util.CommandTask;
import edu.cornell.rocketry.util.GPSResponse;
import edu.cornell.rocketry.util.Position;
import edu.cornell.rocketry.util.PayloadStatus;
import edu.cornell.rocketry.util.RunnableFactory;
import edu.cornell.rocketry.xbee.OutgoingPacket;
import edu.cornell.rocketry.xbee.OutgoingPacketType;
import edu.cornell.rocketry.xbee.XBeeListenerThread;
import edu.cornell.rocketry.xbee.XBeeSender;
import edu.cornell.rocketry.xbee.XBeeSenderException;
import gnu.io.CommPortIdentifier;

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
		realReceiver = new RealReceiver(this, realModel.xbeeListener);
		testSender = new TestSender(this);
		realSender = new RealSender(this);
		r = new RunnableFactory(this);
		//xbee = new XBee();
		
		testing = false;	//default for now	
	}
	
	/*------------------------- Getters & Setters ---------------------------*/
	
	public Sender getSender(boolean test) { 
		return test? testSender : realSender; 
	}
	
	public Receiver getReceiver (boolean test) {
		return test? testReceiver : realReceiver;
	}
	
	public void refreshAll () {
		//re-load markers on 
		Collection<Position> all_rocket_positions = 
			model(testing).getPastRocketPositions();
		updateRocketPositionFull(all_rocket_positions);
		
		mainWindow.setPayloadStatus(model(testing).payload());
		
		Position current = model(testing).position();
		if (testing) updateXBeeDisplayFields("--", "--", "--", "--");
		else updateXBeeDisplayFields(current.lat()+"", current.lon()+"", current.alt()+"", "--");
	}
	
	
	/*------------------ Control & Tracking Update Methods ------------------*/
	
	/*void updateRocketTrajectory(){
		LinkedList<Position> rocket_past_pos = model(testing).getPastRocketPositions();
		int nPositions = rocket_past_pos.size();
		if (nPositions> 1){
			double[] lat = {rocket_past_pos.get(nPositions-2).lat(), rocket_past_pos.get(nPositions-1).lat()};
			double[] lon = {rocket_past_pos.get(nPositions-2).lon(), rocket_past_pos.get(nPositions-1).lon()};
			double[] alt = {rocket_past_pos.get(nPositions-2).alt(), rocket_past_pos.get(nPositions-1).alt()};
	
			Plot3DPanel plot = mainWindow.getTrajectoryPlot();
			plot.addLinePlot(
                "Rocket Trajectory",
                Color.red,
               	lat,lon,alt);

//        //Adjust bounds to keep them constant...
//        plot.setFixedBounds(0,model.minLat,model.maxLat);
//        plot.setFixedBounds(1,model.minLng,model.maxLng);
//        plot.setFixedBounds(2,model.minAlt,model.maxAlt);
		}
		
	}*/ //FIXME

    void updateRocketPosition (Position p) {
    	mainWindow.addMapMarkerDot
    		(""+Position.millisToTime(p.time()), p.lat(), p.lon());
    	//updateRocketTrajectory(); //FIXME
    }
    
    void updateRocketPositionFull (Collection<Position> ps) {
    	clearMapMarkers();
    	for (Position p : ps) {
    		updateRocketPosition(p);
    	}
    }
    
    void updatePayloadStatus (PayloadStatus st) {
    	//System.out.println("Updating Payload Status: " + st.toString());
    	model(testing).setPayload(st);
    	mainWindow.setPayloadStatus(model(testing).payload());
    	//System.out.println("Updated Payload Status: " + model(testing).payload().toString());
    }
    
    public void clearMapMarkers () {
    	mainWindow.clearMapMarkers();
    }
    
    public void sendCommand (CommandTask task) {
    	Command c = new Command(task, System.currentTimeMillis());
    	sender().send(c);
    }
    
    
	
	
	public synchronized void acceptCommandReceipt (CommandReceipt r) {
		//display receipt
		String message = 
			r.success() ? 
				(r.task().toString() + " successfully sent.") :
				("COULD NOT SEND " + r.task().toString() + 
					". \n -> " + r.message());
		ilog("\nCommand Receipt Received:");
		ilog(message);
		
		//process receipt
		if (r.task() == CommandTask.EnablePayload
			|| r.task() == CommandTask.DisablePayload) {
			updatePayloadStatus(PayloadStatus.Busy);
		}
	}
	
	public synchronized void acceptCommandResponse (CommandResponse r, boolean test) {
		//display response
		ilog("\nCommand Response Received:");
		ilog(r.task().toString());
		ilog(r.successful() ? "Successful" : "Unsuccessful");
		ilog(r.message());
		ilog("elapsed time: " + r.time() + " ms");
		
		//process response
		if (r.task() == CommandTask.EnablePayload
			|| r.task() == CommandTask.DisablePayload) {
			if (r.successful()) {
				PayloadStatus ps = r.task() == CommandTask.EnablePayload ? PayloadStatus.Enabled : PayloadStatus.Disabled;
				updatePayloadStatus(ps);
			} else { //we failed to complete task
				//reset to what it was before failed attempt
				updatePayloadStatus(model(testing).prevPayload());
			}
		}
	}
	
	public synchronized void acceptGPSResponse (GPSResponse r, boolean test) {
		ilog("\nGPS Response Received:");
		if (gpsCheck(r)) {
			ilog("(" + r.lat() + ", " + r.lon() + ", " + r.alt() + ")");
			ilog("gps time: " + Position.millisToTime(r.time()) + " ms");
			// Update model
			model(test).updatePosition(r.lat(), r.lon(), r.alt(), r.time());
			if (test == testing) updateRocketPosition (model(test).position());
			if (!test) updateXBeeDisplayFields (
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
	
	public void resetTestSender (File f) {
		testSender = new TestSender(this, f);
	}
	
	/*------------------------ XBee Methods -------------------------*/
	
	
	
	public void updateXBeeDisplayFields (String lat, String lon, String alt, String flag) {
		mainWindow.updateXBeeData(lat, lon, alt, flag);
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
		mainWindow.serialPortsList.removeAllItems();
		for (String s : comboBoxList) {
			mainWindow.serialPortsList.addItem(s);
		}
	}

	public void updateSelectedAddress() {
		model(false).selectedAddress = GSGui.addr[mainWindow.addressesList.getSelectedIndex()]; //set active address
	}
	
	public void updateSelectedBaudRate() {
		model(false).selectedBaud = (int) mainWindow.baudList.getSelectedItem(); //set active rate
	}

    public void initXbee() throws XBeeException {
    	XBee xbee = model(false).xbee;
    	XBeeListenerThread xbeeListener = model(false).xbeeListener;

		// get selected serial port...
		String selSerial = (String) mainWindow.serialPortsList.getSelectedItem();

		if (xbee != null && xbee.isConnected()) {
			xbee.close();
			xbeeListener.stopListening();
		}
		

		System.out.println(selSerial);
		xbee.open(selSerial, model(false).selectedBaud); //open port
		xbeeListener = new XBeeListenerThread(receiver(false), xbee, mainWindow); //init a new listener thread
		xbeeListener.start();

		mainWindow.resetPacketCounters();
	}

	public boolean sendXBeePacket(String msg) {
    	XBee xbee = model(false).xbee;
    	
		OutgoingPacket payload = new OutgoingPacket(OutgoingPacketType.TEST);
		try {
			XBeeSender mailman = new XBeeSender(xbee, model(false).selectedAddress, payload);
			mailman.send();
			mainWindow.addToReceiveText("Sent (" + mainWindow.getNumSent() + "): " + msg);
			return true;
		}
		catch (XBeeSenderException e) {
			mainWindow.addToReceiveText("Error (" + mainWindow.getNumError() + "): " + e.getMessage());
			mainWindow.incNumError();
			return false;
		}
	}
}
