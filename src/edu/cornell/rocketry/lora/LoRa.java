package edu.cornell.rocketry.lora;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import edu.cornell.rocketry.comm.receive.Receiver;
import edu.cornell.rocketry.comm.send.OutgoingPacket;
import edu.cornell.rocketry.gui.view.View;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class LoRa implements Closeable {
	public static int TIMEOUT = 3000; //in milliseconds
	
	private boolean verbose = false;
	private int baudrate = 19200;
	
	private String portname;
	private CommPortIdentifier portId;
	private SerialPort serialPort;
	private boolean isConnected;
	
	// so that we can directly communicate with the view - should we communicate with the model instead?
	private Receiver receiver;
	private View mainWindow;

	/** initialize a LoRa without an active connection but with a connection to the
	 *  Receiver receiver and the View mainWindow */
	public LoRa(Receiver receiver, View mainWindow) throws LoRaException {
		this.receiver = receiver;
		this.mainWindow = mainWindow;
		isConnected = false;
	}
	
	/** initialize a LoRa connection, with a connection to the Receiver receiver and the View mainWindow */
	public LoRa(String portname, Receiver receiver, View mainWindow) throws LoRaException {
		this.receiver = receiver;
		this.mainWindow = mainWindow;
		this.portname = portname;
		isConnected = false;
		
		connectTo(portname);
	}
	
	public void changePortName(String portname) throws LoRaException {
		this.portname = portname;
		
		if(serialPort != null) {
			serialPort.close();
		}
		connectTo(portname);
	}
	
	/** set up the connection and set portId appropriately
	 * throws an error if it cannot connect 
	 * @throws LoRaException */
	private void connectTo(String portname) throws LoRaException {
		printIfVerbose(String.format("Looking for %s...", portname));

		// find the port
		Enumeration<CommPortIdentifier> enumComm = CommPortIdentifier.getPortIdentifiers();
		portId = null;
		while (enumComm.hasMoreElements()) {
	        CommPortIdentifier currPortId = enumComm.nextElement();
            if (currPortId.getName().equals(portname)) {
                portId = currPortId;
                break;
            }
		}
		if (portId == null) {
			printIfVerbose(String.format("%s was not found!", portname));
			throw new LoRaException(String.format("No port named \"%s\" was found.", portname));
		}
		
		// connect to the port that we found
		try {
			serialPort = (SerialPort) portId.open(portname, TIMEOUT);
		} catch (PortInUseException e) {
			printIfVerbose(String.format("%s cannot be used!", portname));
			throw new LoRaException(String.format("Port \"%s\" is in use.", portname), e);
		}
		
		printIfVerbose(String.format("%s was found!", portname));

		isConnected = true;
	}
	
	/** busy-wait until we receive a full message, and then return the parsed LoRaPacket
	 * 
	 * if the input is invalid, throws a LoRaException
	 * 
	 * TODO: make it wait more intelligently instead of just busy-waiting
	 * TODO: timeouts, maybe */
	public void startListening() throws LoRaException {
		(new Thread(){
			public void run() {
				try {
					serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					printIfVerbose("Ready to start receiving on " + serialPort);
					
					InputStream input = serialPort.getInputStream();
					input.skip(input.available()); //clear any previously existing buffer
					while(true){ //TODO: have a stop command
						// busy wait:
						while(input.available() < LoRaPacket.MESSAGE_LENGTH){}
						
						byte[] bytes = new byte[LoRaPacket.MESSAGE_LENGTH];
						input.read(bytes, 0, LoRaPacket.MESSAGE_LENGTH);
						
						
						LoRaPacket r = new LoRaPacket(bytes);
						
						mainWindow.incNumRec();
						mainWindow.addToReceiveText("Received (" + mainWindow.getNumRec() + "): "
								+ r.toString());
						
						/*
						mainWindow.addToReceiveText("bytes: ");
						for(byte b : bytes){
							mainWindow.addToReceiveText(String.format("%02x", b));
						}
						mainWindow.addToReceiveText("");
						*/
						
						synchronized (receiver) {
							receiver.acceptLoRaPacket(r);
						}
					}
				} catch (UnsupportedCommOperationException e) {
					throw new LoRaException(e);
				} catch (IOException e) {
					throw new LoRaException(e);
				}
			}
		}).start();
	}

	public void send(OutgoingPacket packet) throws LoRaException {
		// TODO Auto-generated method stub
		(new Thread(){
			public void run() {
				try {
					serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					printIfVerbose("Ready to start sending on " + serialPort);
					
					OutputStream input = serialPort.getOutputStream();
					
					//System.out.print
				} catch (UnsupportedCommOperationException e) {
					throw new LoRaException(e);
				} catch (IOException e) {
					throw new LoRaException(e);
				}
			}
		}).start();
	}
		
	/** return the port name */
	public String getPortName(){
		return portname;
	}
	
	@Override
	/** close the connection */
	public void close() throws IOException {
		// TODO Auto-generated method stub
		serialPort.close();
		isConnected = false;
		printIfVerbose("closing (TODO: actually do closing stuff)");
	}
	
	private void printIfVerbose(Object s) {
		if(!verbose) return;
		System.out.println(s.toString());
	}
}
