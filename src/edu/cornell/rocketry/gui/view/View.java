package edu.cornell.rocketry.gui.view;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

import jTile.src.org.openstreetmap.fma.jtiledownloader.views.main.JTileDownloaderMainViewPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.math.plot.Plot3DPanel; //FIXME
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import edu.cornell.rocketry.lora.LoRa;
import edu.cornell.rocketry.lora.LoRaException;
import edu.cornell.rocketry.comm.send.CommandType;
import edu.cornell.rocketry.gui.controller.Controller;
import edu.cornell.rocketry.util.Status;
import edu.cornell.rocketry.util.ImageFactory;


/**
 * Demonstrates the usage of {@link JMapViewer}
 *
 * @author Jan Peter Stotz
 *
 */
public class View extends JFrame implements JMapViewerEventListener {



	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ *
	 *                                                                       *
	 *                     .: GUI FIELD DECLARATION :.                       *
	 *                                                                       *
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

	private static final long serialVersionUID = 1L;

	final View view = this;

	private Controller controller;// = new Controller(this);

	//tab panel declarations
	JMapViewerTree treeMap = null;
	private JPanel controlPanel;
	private JPanel analyticsPanel;
	private JPanel downloadPanel;
	private JPanel radioPanel;
	private JPanel settingsPanel;

	JTabbedPane tabbedPane;

	/*------------------------ Control Tab Fields ---------------------------*/
	JPanel status;
	JPanel latestPositionPanel;
	JLabel latestPositionLabel;
	JLabel latestPosition;


	JPanel statusSection;
	JPanel controlsSection;
	JPanel trajpanel;

	JPanel cameraStatusContainer;
	JPanel gpsStatusContainer;
	JPanel initStatusContainer;
	JPanel launchStatusContainer;
	JPanel landedStatusContainer;

	JScrollPane infologscrollpane;
	JTextArea infolog;

	JLabel cameraStatusLabel;
	JLabel cameraStatus;

	JLabel gpsStatusLabel;
	JLabel gpsStatus;

	JLabel initStatusLabel;
	JLabel initStatus;

	JLabel launchStatusLabel;
	JLabel launchStatus;

	JLabel landedStatusLabel;
	JLabel landedStatus;

	JButton settings = new JButton ("Settings");

	JButton beginLaunchButton = new JButton("Prepare for Launch");
	JButton cancelLaunchButton = new JButton("Cancel Launch Preparations");

	private Plot3DPanel trajectoryplot = new Plot3DPanel(); //FIXME

	/*------------------------ Analytics Tab Fields --------------------------*/
	JLabel maxAscentSpeedLabel = new JLabel("Max Ascent Speed (m/s): ");
	JLabel maxDriftSpeedLabel = new JLabel("Max Drift Speed (m/s): ");
	JLabel currentSpeedLabel = new JLabel("Current Speed (m/s): ");
	JLabel currentAccelerationLabel = new JLabel("Current Acceleration (m/s^2): ");
	JLabel currentBearingLabel = new JLabel("Current Bearing: ");
	JLabel maxAltitudeLabel = new JLabel("Max Altitude (m): ");
	JLabel currentAltitudeLabel = new JLabel("Current Altitude (m): ");
	JLabel maxRotationLabel = new JLabel("Max Rotation (RPM): ");
	JLabel currentRotationLabel = new JLabel("Current Rotation (RPM): "); 
	JLabel averageRotationLabel = new JLabel("Average Rotation (RPM): "); 
	JLabel elapsedTimeSinceLaunchLabel = new JLabel("Elapsed Time Since Launch (sec): ");
	JLabel timeToApogeeSinceLaunchLabel = new JLabel("Time to Apogee Since Launch (sec): ");

	public Chart2D altitudeChart;
	public ITrace2D altitudeTrace;
	public Chart2D rotationChart;
	public ITrace2D rotationTrace;
	public Chart2D accelChart;
	public ITrace2D accelTrace;
	JLabel atimeLabel = new JLabel("Time: ");
	JTextField atimeInput = new JTextField();
	JButton alimitMapMarkersButton = new JButton("Limit Map Markers");

	private static JLabel maxAscentSpeed = new JLabel("0.0");
	private static JLabel maxDriftSpeed = new JLabel("0.0");
	private static JLabel currentSpeed = new JLabel("0.0");
	private static JLabel currentAcceleration = new JLabel("0.0");
	private static JLabel currentBearing = new JLabel("0.0");
	private static JLabel maxAltitude = new JLabel("0.0");
	private static JLabel currentAltitude = new JLabel("0.0");
	private static JLabel maxRotation = new JLabel("0.0");
	private static JLabel currentRotation = new JLabel("0.0");
	private static JLabel averageRotation = new JLabel("0.0"); 
	private static JLabel elapsedTimeSinceLaunch = new JLabel("0");
	private static JLabel timeToApogeeSinceLaunch = new JLabel("0");

	private static boolean hasLaunched = false;
	private static boolean hasApogeed = false;
	private static boolean hasApogeedFlag = false;
	private static long startTime;
	private static final double driftTol = 0.1;

	private static double prevLatitude;
	private static double prevLongitude;
	private static double prevSpeed;
	private static double averageRotationValue = 0.0;
	private static int numRotationDataPoints = 0;
	private static long prevTime;
	private static double maxAscentSpeedValue = 0.0;
	private static double maxDriftSpeedValue = 0.0;
	private static double maxAltitudeValue = 0.0;
	private static double maxRotationValue = 0.0;

	/*------------------------ Recovery Tab Fields --------------------------*/
	private JLabel zoomLabel=null;
	private JLabel zoomValue=null;

	private JLabel mperpLabelName=null;
	private JLabel mperpLabelValue = null;

	JPanel panel = new JPanel();
	JPanel panelTop = new JPanel();
	JPanel panelBottom = new JPanel();
	JPanel helpPanel = new JPanel();

	JTextField lonInput = new JTextField();
	JTextField latInput = new JTextField();
	JTextField timeInput = new JTextField();
	JLabel lonLabel = new JLabel("Longitude: ");
	JLabel latLabel = new JLabel("Latitude: ");
	JLabel timeLabel = new JLabel("Time: ");
	JButton manualInputButton = new JButton("Add Map Marker");
	JButton clearMapMarkersButton = new JButton("Clear Map Markers");
	JButton limitMapMarkersButton = new JButton("Limit Map Markers");

	JPanel manualInputPanel = new JPanel(new GridBagLayout());

	/*------------------------- Download Tab Fields -------------------------*/



	/*--------------------------- Radio Tab Fields --------------------------*/

	public static final Integer[] baudRates = {4800, 9600, 19200, 38400, 57600, 115200};

	private int numRec = 0; 	//number received packets
	private int numSent = 0;	//number sent packets
	private int numErr = 0; 	//number error packets


	private JTextArea receiveText;
	private JTextField sendEdit;
	private final static Font titleFont = new Font("Arial", Font.BOLD, 20);
	private final static Font textAreaFont = new Font("Arial", Font.PLAIN, 10);

	protected int selectedBaud = 57600; //serial comm rate

	protected JComboBox<String> serialPortsList;
	protected JComboBox<Integer> baudList;

	private JPanel dataPanel, tablePanel; //statusPanel
	private static JLabel lat,longi,alt,flag;




	/* ------------------------ Settings Tab Fields ------------------------ */

	/* TESTING */
	//container
	private JPanel testingSettingsPanel;
	//label
	private JLabel testingSettingsPanelLabel;
	//elements
	private JCheckBox testingCheckBox;
	private JCheckBox debugPrintoutsCheckBox;
	private JFileChooser gpsSimFileChooser;
	private JButton gpsSimFileChooserButton;

	/* MAP */
	//container
	private JPanel mapSettingsPanel;
	//label
	private JLabel mapSettingsPanelLabel;
	//elements
	private JButton tileLocationChooserButton;
	private JFileChooser tileLocationChooser;
	private JButton defaultLocationChooser;
	private JCheckBox manualPointEntryCheckBox;

	/* GENERAL */
	//container
	private JPanel generalSettingsPanel;
	//label
	private JLabel generalSettingsPanelLabel;
	//elements
	private JButton clearDataButton;

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ *
	 *                                                                       *
	 *                         .: CONSTRUCTOR :.                             *
	 *                                                                       *
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

	/**
	 * Constructs the Rocketry GS Gui.
	 */
	public View() {
		super("CURocketry Ground Station GUI");
		setSize(500, 500);

		ImageFactory.init();

		//FIXME ^^ implement so we have the ability to change this

		controller = new Controller(this);

		treeMap = new JMapViewerTree("Zones", false);

		// Listen to the map viewer for user operations so components will
		// receive events and update
		map().addJMVListener(this);

		// final JMapViewer map = new JMapViewer(new MemoryTileCache(),4);
		// map.setTileLoader(new OsmFileCacheTileLoader(map));
		// new DefaultMapController(map);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				controller.onClose();
				dispose();
				System.exit(0);
			}
		});

		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);


		initializeControlTab();

		initializeAnalyticsTab();

		initializeRecoveryTab();

		initializeDownloadTab();

		initializeRadioTab();

		initializeSettingsTab();


		/*------------------ Create Tabbed Pane & Add Tabs ------------------*/   

		tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Control", null, controlPanel, "GS Control Tab");
		tabbedPane.addTab("Analytics", null, analyticsPanel, "Analytics Tab");
		tabbedPane.addTab("Recovery", null, treeMap, "Recovery Tracking Tab");
		tabbedPane.addTab("Download", null, downloadPanel, "Map Downloading Tab");
		tabbedPane.addTab("Radio", null, radioPanel, "Radio Setup Tab");
		tabbedPane.addTab("Settings", null, settingsPanel, "Settings Tab");

		/* Activate the Tabbed Pane */
		setContentPane(tabbedPane);

		setVisible(true);        


		/*---------------------LISTEN FOR TAB CHANGE-------------------------*/
		final JMapViewer map = treeMap.getViewer();

		//change the parent of the map back and forth so that it is accessible 
		//in both the control and recovery tabs.
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				String tab = sourceTabbedPane.getTitleAt(index).toString();
				if (tab.equals("Control")) {
					//System.out.println("Giving to Control");
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.anchor = GridBagConstraints.CENTER;
					c.ipadx = 10;
					c.ipady = 10;
					c.insets = new Insets(10, 10, 10, 10);
					c.gridwidth = 2;
					c.weightx = 1.0;
					c.weighty = 0.9;
					c.gridx = 2;
					c.gridy = 3;
					controlPanel.add(treeMap.getViewer(), c);
				} else if (tab.equals("Recovery")) {
					treeMap.setViewer(map);
				}
			}
		};
		tabbedPane.addChangeListener(changeListener);


		/*---------------------------- Other --------------------------------*/
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultLookAndFeelDecorated(true);

	}

	/*------------------------------ Aliases --------------------------------*/

	public Plot3DPanel getTrajectoryPlot(){
		return trajectoryplot;
	} //FIXME

	public void resetTrajectoryPlot() {
		trajpanel.remove(trajectoryplot);
		trajectoryplot = new Plot3DPanel();
		trajpanel.add(trajectoryplot,BorderLayout.CENTER);
	}

	public JMapViewer map(){
		return treeMap.getViewer();
	}
	@SuppressWarnings("unused")
	private static Coordinate c (double lat, double lon){
		return new Coordinate(lat, lon);
	}


	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ *
	 *                                                                       *
	 *                 .: GUI INITIALIZATION FUNCTIONS :.                    *
	 *                                                                       *
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

	private void initializeControlTab() {
		//general
		controlPanel = new BackgroundJPanel("./assets/black_wood_background.jpg");

		controlPanel.setLayout(new GridBagLayout());

		//status indicators
		status = new JPanel(new FlowLayout());
		status.setOpaque(false);

		//display of last known position
		latestPositionPanel = new JPanel(new BorderLayout());
		latestPositionPanel.setOpaque(false);
		latestPosition = new JLabel("no data");
		latestPositionLabel = new JLabel("Last Known Position: ");
		latestPositionLabel.setForeground(Color.WHITE);
		latestPosition.setForeground(Color.WHITE);
		latestPositionPanel.add(latestPositionLabel, BorderLayout.WEST);
		latestPositionPanel.add(latestPosition, BorderLayout.EAST);

		//display whether we're ready for launch or not
		launchStatusContainer = new JPanel(new BorderLayout());
		launchStatusLabel = new JLabel("Launch Ready: ");
		launchStatusLabel.setOpaque(false);
		launchStatusLabel.setForeground(Color.WHITE);
		launchStatus = new JLabel();
		launchStatus.setIcon(ImageFactory.disabledImage());
		launchStatus.setOpaque(false);
		launchStatusContainer.add(launchStatusLabel, BorderLayout.WEST);
		launchStatusContainer.add(launchStatus, BorderLayout.EAST);
		launchStatusContainer.setOpaque(false);
		status.add(launchStatusContainer);


		//begin launch button
		beginLaunchButton.setVisible(true);
		beginLaunchButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.sendCommand(CommandType.LAUNCH);
				}
			}
		});

		//cancel launch button
		cancelLaunchButton.setVisible(true);
		cancelLaunchButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.sendCommand(CommandType.CANCEL);
				}
			}
		});

		//info log
		infolog = new JTextArea (); 
		infolog.setLineWrap(true);
		infolog.setEditable(false);
		infolog.setWrapStyleWord(true);
		infologscrollpane = new JScrollPane(infolog);

		//3D Plot
		trajpanel = new JPanel(new BorderLayout()); 
		trajpanel.add(trajectoryplot,BorderLayout.CENTER);


		//construct sections
		statusSection = new JPanel();

		statusSection.add(status);
		statusSection.add(latestPositionPanel);

		statusSection.setOpaque(false);
		infologscrollpane.setOpaque(false);

		infologscrollpane.setVisible(true); //by default, toggled in settings

		//add sections

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.ipadx = 10;
		c.ipady = 10;
		c.insets = new Insets(10, 10, 10, 10);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		c.weightx = 1.0;
		c.weighty = 0.0;
		controlPanel.add(statusSection, c);

		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 1.0;
		c.weighty = 0.9;
		controlPanel.add(trajpanel, c);

		c.gridx = 2;
		c.gridy = 3;
		controlPanel.add(treeMap.getViewer(), c);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.weightx = 1.0;
		c.weighty = 0.15;
		c.anchor = GridBagConstraints.PAGE_END;
		controlPanel.add(infologscrollpane, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.1;
		c.weighty = 0.0;

		c.gridx = 0;
		c.gridy = 1;
		controlPanel.add(beginLaunchButton, c);

		c.gridx = 2;
		c.gridy = 1;
		controlPanel.add(cancelLaunchButton, c);


		controlPanel.setVisible(true);
		controlPanel.validate();
	}

	public void initializeAnalyticsTab(){
		altitudeChart = new Chart2D();
		altitudeTrace = new Trace2DSimple();
		rotationChart = new Chart2D();
		rotationTrace = new Trace2DSimple();
		accelChart = new Chart2D();
		accelTrace = new Trace2DSimple();

		analyticsPanel = new JPanel();

		altitudeChart.addTrace(altitudeTrace);
		AxisTitle altaxisTitleX = new AxisTitle("Altitude vs. Time");
		IAxis altaxisX = altitudeChart.getAxisX();
		altaxisX.setAxisTitle(altaxisTitleX);

		rotationChart.addTrace(rotationTrace);
		AxisTitle rotaxisTitleX = new AxisTitle("Rotation vs. Time");
		IAxis rotaxisX = rotationChart.getAxisX();
		rotaxisX.setAxisTitle(rotaxisTitleX);

		accelChart.addTrace(accelTrace);
		AxisTitle accelaxisTitleX = new AxisTitle("Acceleration vs. Time");
		IAxis accelaxisX = accelChart.getAxisX();
		accelaxisX.setAxisTitle(accelaxisTitleX);

		JPanel top = new JPanel(new GridBagLayout()); 
		GridBagConstraints b = new GridBagConstraints();
		analyticsPanel.add(top);

		b.anchor = GridBagConstraints.WEST;
		b.fill = GridBagConstraints.HORIZONTAL;
		b.gridx = 0; b.gridy = 0;
		top.add(currentBearingLabel, b);
		b.gridx = 1; b.gridy = 0;
		b.insets = new Insets(0,0,0,130);
		top.add(currentBearing, b);

		b.insets = new Insets(0,0,0,0);
		b.ipadx = 0;
		b.gridx = 2; b.gridy = 0;
		b.anchor = GridBagConstraints.CENTER;
		top.add(atimeLabel, b);
		b.gridx = 3; b.gridy = 0;
		b.fill = GridBagConstraints.BOTH;
		b.ipadx = 200; 
		b.insets = new Insets(0,0,0,5);
		top.add(atimeInput, b);
		b.gridx = 4; b.gridy = 0;
		b.ipadx = 0;
		b.insets = new Insets(0,0,0,130);
		top.add(alimitMapMarkersButton, b);

		b.ipadx = 0;
		b.gridx = 5; b.gridy = 0;
		b.insets = new Insets(0,0,0,0);
		b.anchor = GridBagConstraints.EAST;
		top.add(elapsedTimeSinceLaunchLabel, b);
		b.gridx = 6; b.gridy = 0;
		top.add(elapsedTimeSinceLaunch, b);
		
		//c.gridx = 7; c.gridy = 1;
		//data.add(timeToApogeeSinceLaunchLabel, c);
		//c.gridx = 8; c.gridy = 1;
		//data.add(timeToApogeeSinceLaunch, c);
		
		JPanel data = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		analyticsPanel.add(data);
 		
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(15,0,0,0);
		c.gridx = 0; c.gridy = 2;
 		data.add(currentSpeedLabel, c);
		c.insets = new Insets(0,0,0,0);
 		c.gridx = 0; c.gridy = 3;
 		data.add(maxAscentSpeedLabel, c);
 		c.gridx = 0; c.gridy = 4;
 		data.add(maxDriftSpeedLabel, c);
		c.insets = new Insets(15,0,0,70);
 		c.gridx = 1; c.gridy = 2;
 		data.add(currentSpeed, c);
		c.insets = new Insets(0,0,0,70);
 		c.gridx = 1; c.gridy = 3;
 		data.add(maxAscentSpeed, c);
 		c.gridx = 1; c.gridy = 4;
 		data.add(maxDriftSpeed, c);
 
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(15,0,0,0);
 		c.gridx = 3; c.gridy = 2;
 		data.add(currentRotationLabel, c);
		c.insets = new Insets(0,0,0,0);
 		c.gridx = 3; c.gridy = 3;
 		data.add(averageRotationLabel, c);
 		c.gridx = 3; c.gridy = 4;
 		data.add(maxRotationLabel, c);
		c.insets = new Insets(15,0,0,70);
 		c.gridx = 4; c.gridy = 2;
 		data.add(currentRotation, c);
		c.insets = new Insets(0,0,0,0);
 		c.gridx = 4; c.gridy = 3;
 		data.add(averageRotation, c);
 		c.gridx = 4; c.gridy = 4;
 		data.add(maxRotation, c);
 
		c.insets = new Insets(15,0,0,0);
 		c.gridx = 5; c.gridy = 2;
 		data.add(currentAltitudeLabel, c);
		c.insets = new Insets(0,0,0,0);
 		c.gridx = 5; c.gridy = 3;
 		data.add(maxAltitudeLabel, c);
		c.insets = new Insets(15,0,0,70);
 		c.gridx = 6; c.gridy = 2;
 		data.add(currentAltitude, c);  
		c.insets = new Insets(0,0,0,70);
 		c.gridx = 6; c.gridy = 3;
 		data.add(maxAltitude, c);
 		
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(15,0,0,0);
 		c.gridx = 8; c.gridy = 2;
 		data.add(currentAccelerationLabel, c);
		c.insets = new Insets(15,0,0,0);
 		c.gridx = 9; c.gridy = 2;
 		data.add(currentAcceleration, c);


		alimitMapMarkersButton.setVisible(true);	
		alimitMapMarkersButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					String timeLimits = atimeInput.getText();
					String nums = timeLimits.replaceAll("[\\D]", " ");
					int firstBlank = nums.indexOf(" ");
					String limit1 = nums.substring(0, firstBlank);

					String b = nums.substring(firstBlank);
					String limit2 = b.replaceAll(" ", "");

					if(limit2.length() == 0)
						controller.limitMapMarkers(Long.parseLong(limit1), System.currentTimeMillis()*1000);
					else
						controller.limitMapMarkers(Long.parseLong(limit1), Long.parseLong(limit2));
				}
			}
		});
		
		JPanel graph = new JPanel(new GridBagLayout()); 
		GridBagConstraints d = new GridBagConstraints();
		analyticsPanel.add(graph);
		d.insets = new Insets(20,0,0,30);
		d.anchor = GridBagConstraints.WEST;
		d.fill = GridBagConstraints.BOTH;
		d.gridx = 0; d.gridy = 10;
		d.ipadx = 400;
		d.ipady = 400;
		graph.add(altitudeChart, d);

		d.anchor = GridBagConstraints.CENTER;
		d.gridx = 4; d.gridy = 10;
		graph.add(rotationChart, d);
		
		d.insets = new Insets(20,0,0,0);
		d.anchor = GridBagConstraints.EAST;
		d.gridx = 7; d.gridy = 10;
		graph.add(accelChart, d);
	}

	private void initializeRecoveryTab() {
		mperpLabelName=new JLabel("Meters/Pixels: ");
		mperpLabelValue=new JLabel(String.format("%.2f",map().getMeterPerPixel()));

		zoomLabel=new JLabel("Zoom: ");
		zoomValue=new JLabel(String.format("%s", map().getZoom()));

		/* ~~~~~~~~~ Manual Input Initialization ~~~~~~~~~ */
		manualInputButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					try {
						Double lat = Double.parseDouble(latInput.getText());
						Double lon = Double.parseDouble(lonInput.getText());
						MapMarkerDot m = new MapMarkerDot(Color.RED, lat, lon);
						map().addMapMarker(m);
					} catch (Exception ex) {
						System.out.println("Only number inputs allowed.");
					}
				}
			}
		});

		clearMapMarkersButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.clearMapMarkers();
				}
			}
		});


		/*
	    JTextField lonInput = new JTextField();
	    JTextField latInput = new JTextField();
	    JLabel lonLabel = new JLabel("Longitude: ");
	    JLabel latLabel = new JLabel("Latitude: ");
	    JButton manualInputButton = new JButton("Add Map Marker");
	    JButton clearMapMarkersButton = new JButton("Clear Map Markers");

	    JPanel manualInputPanel = new JPanel(new GridBagLayout());
		 */

		lonInput.setPreferredSize(new Dimension(100, 20));
		latInput.setPreferredSize(new Dimension(100, 20));
		timeInput.setPreferredSize(new Dimension(100, 20));

		GridBagConstraints c = new GridBagConstraints();

		c.ipadx = 5; c.ipady = 5;

		c.gridx = 0; c.gridy = 0;
		manualInputPanel.add(latLabel, c);

		c.gridx = 0; c.gridy = 1;
		manualInputPanel.add(latInput, c);

		c.gridx = 1; c.gridy = 0;
		manualInputPanel.add(lonLabel, c);

		c.gridx = 1; c.gridy = 1;
		manualInputPanel.add(lonInput, c);

		c.gridx = 2; c.gridy = 0;
		manualInputPanel.add(manualInputButton, c);

		c.gridx = 2; c.gridy = 1;
		manualInputPanel.add(clearMapMarkersButton, c);


		panelTop.add(manualInputPanel);
		manualInputPanel.setVisible(false); //by default, toggled in settings


		/* ~~~~~~~ End Manual Input Initialization ~~~~~~~ */


		//allow for constant horizontal scrolling (why not?)
		map().setScrollWrapEnabled(true);

		//use cyclemaps as default b/c contains street & topo information
		map().setTileSource(new OsmTileSource.CycleMap());

		treeMap.add(panel, BorderLayout.NORTH); //remove treeMap for this and one below
		treeMap.add(helpPanel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout());
		panel.add(panelTop, BorderLayout.NORTH);
		panel.add(panelBottom, BorderLayout.SOUTH);
		JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
				+ "left double click or mouse wheel to zoom.");
		helpPanel.add(helpLabel);
		JButton button = new JButton("Zoom to Markers");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				map().setDisplayToFitMapMarkers();
			}
		});
		/*JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[] { new OsmTileSource.Mapnik(),
                new OsmTileSource.CycleMap(), new BingAerialTileSource(), new MapQuestOsmTileSource(), new MapQuestOpenAerialTileSource() });
        tileSourceSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map().setTileSource((TileSource) e.getItem());
            }
        });
        panelTop.add(tileSourceSelector);*/
		JComboBox<TileLoader> tileLoaderSelector;
		try {
			tileLoaderSelector = new JComboBox<>(new TileLoader[] { new OsmFileCacheTileLoader(map()), new OsmTileLoader(map()) });
		} catch (IOException e) {
			tileLoaderSelector = new JComboBox<>(new TileLoader[] { new OsmTileLoader(map()) });
		}
		tileLoaderSelector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				map().setTileLoader((TileLoader) e.getItem());
			}
		});
		map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());

		//panelTop.add(tileLoaderSelector); do not display for now. Use whichever one is possible (pref. Cache Loader)

		final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
		showMapMarker.setSelected(map().getMapMarkersVisible());
		showMapMarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map().setMapMarkerVisible(showMapMarker.isSelected());
			}
		});
		panelBottom.add(showMapMarker);
		///
		final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
		showTileGrid.setSelected(map().isTileGridVisible());
		showTileGrid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map().setTileGridVisible(showTileGrid.isSelected());
			}
		});
		panelBottom.add(showTileGrid);
		final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
		showZoomControls.setSelected(map().getZoomContolsVisible());
		showZoomControls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map().setZoomContolsVisible(showZoomControls.isSelected());
			}
		});
		panelBottom.add(showZoomControls);
		panelBottom.add(button);

		panelTop.add(zoomLabel);
		panelTop.add(zoomValue);
		panelTop.add(mperpLabelName);
		panelTop.add(mperpLabelValue);
		panelTop.add(timeLabel);
		panelTop.add(timeInput);
		panelTop.add(limitMapMarkersButton);



		limitMapMarkersButton.setVisible(true);	
		limitMapMarkersButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					String timeLimits = timeInput.getText();
					String nums = timeLimits.replaceAll("[\\D]", " ");
					int firstBlank = nums.indexOf(" ");
					String limit1 = nums.substring(0, firstBlank);

					String b = nums.substring(firstBlank);
					String limit2 = b.replaceAll(" ", "");

					if(limit2.length() == 0)
						controller.limitMapMarkers(Long.parseLong(limit1), System.currentTimeMillis()*1000);
					else
						controller.limitMapMarkers(Long.parseLong(limit1), Long.parseLong(limit2));
				}
			}
		});


		map().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					map().getAttribution().handleAttribution(e.getPoint(), true);
				}
			}
		});

		map().addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
				if (cursorHand) {
					map().setCursor(new Cursor(Cursor.HAND_CURSOR));
				} else {
					map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});

	}

	private void initializeDownloadTab() {
		downloadPanel = new JTileDownloaderMainViewPanel(this);
	}

	private void initializeRadioTab() {
		controlLog("Remember to go to the Radio Tab to set up the connection to the Arduino/LoRa first!\n");

		// Layout GUI
		radioPanel = new JPanel(new BorderLayout());

		/*-- Setup Radio Panel --*/

		JPanel radioInitPanel = new JPanel(new BorderLayout());
		JLabel radioInitLabel = new JLabel("Setup Arduino/LoRa Connection", JLabel.CENTER);
		radioInitLabel.setFont(titleFont);
		radioInitPanel.add(radioInitLabel, BorderLayout.NORTH);
		JPanel radioInitGrid = new JPanel(new GridLayout(5, 2));

		//Radio Serial Port Label
		JPanel serialPortPanel = new JPanel(new BorderLayout());
		serialPortPanel.add(new JLabel("Arduino Serial Port: "), BorderLayout.WEST);

		//Serial port dropdown
		serialPortsList = new JComboBox<String>(); //initialize empty dropdown
		controller.updateSerialPortsList();
		serialPortsList.setSelectedIndex(serialPortsList.getItemCount() - 1);
		controller.setSerialPort((String) serialPortsList.getSelectedItem()); //initialize model
		serialPortsList.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				controller.setSerialPort((String) serialPortsList.getSelectedItem());
			}
		});

		//Refresh serial ports button
		serialPortPanel.add(serialPortsList, BorderLayout.CENTER);
		JButton refreshPortsBtn = new JButton("Refresh");
		refreshPortsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.updateSerialPortsList();
			}
		});
		serialPortPanel.add(refreshPortsBtn, BorderLayout.EAST);
		radioInitGrid.add(serialPortPanel);

		//Baud rate dropdown
		JPanel baudPanel = new JPanel(new BorderLayout());
		baudPanel.add(new JLabel("LoRa Baud Rate: "), BorderLayout.WEST);
		baudList = new JComboBox<Integer>(baudRates);
		baudList.setSelectedIndex(4);
		controller.updateSelectedBaudRate((int) baudList.getSelectedItem()); //initialize model
		baudList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.updateSelectedBaudRate((int) baudList.getSelectedItem());
			}
		});
		baudPanel.add(baudList, BorderLayout.CENTER);
		radioInitGrid.add(baudPanel);


		//Initialize Arduino Connection Button
		JButton initArduinoButton = new JButton("Initialize Arduino Connection");
		initArduinoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					controller.initLoRa();
					controller.getLoRa().startListening();
					addToReceiveText("Success! Initialized connection to the Arduino :)");
					addToReceiveText("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
							+ System.getProperty("line.separator"));
				} catch (LoRaException e1) {
					e1.printStackTrace();
					numErr++;
					addToReceiveText("Error ("
							+ numErr
							+ "): Could not connect to Arduino :( Make sure port isn't being used by another program (including this one)!");
				}
			}
		});
		radioInitGrid.add(initArduinoButton);
		radioInitPanel.add(radioInitGrid, BorderLayout.CENTER);

		//Send Packet Title and Button
		JPanel sendPacketsPanel = new JPanel(new BorderLayout());
		JPanel sendPacketsGrid = new JPanel(new GridLayout(5, 2));
		JLabel sendTitle = new JLabel("Send Packets", JLabel.CENTER);
		sendTitle.setFont(titleFont);
		sendPacketsPanel.add(sendTitle, BorderLayout.NORTH);
		sendPacketsPanel.add(sendPacketsGrid, BorderLayout.CENTER);

		// Send buttons
		JButton radio_prepareBtn = new JButton("Prepare for Launch");
		radio_prepareBtn.setVisible(true);
		radio_prepareBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.sendCommand(CommandType.LAUNCH);
				}
			}
		});
		sendPacketsGrid.add(radio_prepareBtn, BorderLayout.CENTER);
		
		JButton radio_cancelBtn = new JButton("Cancel Launch");
		radio_cancelBtn.setVisible(true);
		radio_cancelBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.sendCommand(CommandType.CANCEL);
				}
			}
		});
		sendPacketsGrid.add(radio_cancelBtn, BorderLayout.CENTER);

		/**
		//Send Custom Packet Textbox
		JPanel customPacketEntry = new JPanel(new BorderLayout());
		customPacketEntry.add(new JLabel("Send Packet: "), BorderLayout.WEST);
		sendEdit = new JTextField("", 20);
		customPacketEntry.add(sendEdit, BorderLayout.CENTER);
		sendPacketsGrid.add(customPacketEntry,BorderLayout.SOUTH);

		sendPacketsPanel.add(sendPacketsGrid, BorderLayout.CENTER);
		*/

		//Initialize radio data list
		JPanel radioData = new JPanel(new BorderLayout());
		radioData.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
		JPanel radioDataGrid = new JPanel(new GridLayout(4, 2));
		JLabel mostRecentPacket = new JLabel("Most Recent Data Packet:");
		radioDataGrid.add(mostRecentPacket);
		JLabel parsedPacket = new JLabel("Parsed Data Packet:");
		radioDataGrid.add(parsedPacket);
		radioData.add(radioDataGrid, BorderLayout.CENTER);

		
		JPanel PContainer = new JPanel(new BorderLayout());
		PContainer.add(radioInitPanel, BorderLayout.NORTH);
		PContainer.add(sendPacketsPanel, BorderLayout.CENTER);
		PContainer.add(radioData, BorderLayout.SOUTH);


		/*-- Received Packets Panel-- */
		JPanel receivePanel = new JPanel(new BorderLayout());
		receiveText = new JTextArea(40, 60);
		receiveText.setBackground(Color.white);
		receiveText.setFont(textAreaFont);
		receiveText.setLineWrap(true);
		receiveText.setWrapStyleWord(true);
		receiveText.setEditable(false);
		JScrollPane receiveScrollPlane = new JScrollPane(receiveText,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JLabel receiveTitle = new JLabel("Received Packets", JLabel.CENTER);
		receiveTitle.setFont(titleFont);
		receivePanel.add(receiveTitle, BorderLayout.NORTH);
		receivePanel.add(receiveScrollPlane,BorderLayout.CENTER); 
		
		/*-- Status Panel --*/

		radioPanel.add(PContainer,BorderLayout.WEST);
		radioPanel.add(receivePanel,BorderLayout.CENTER);


		// Text area stuff...
	}

	private void initializeSettingsTab() {
		/* ~~~~~~~~ GENERAL SETTINGS ~~~~~~~~ */
		//container
		generalSettingsPanel = new JPanel();
		//label
		generalSettingsPanelLabel = new JLabel("General");
		//elements

		clearDataButton = new JButton("Clear Data");
		clearDataButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.clearData();
				}
			}
		});

		//add elements to container
		generalSettingsPanel.setLayout(new BoxLayout(generalSettingsPanel, BoxLayout.PAGE_AXIS));
		generalSettingsPanel.add(generalSettingsPanelLabel);
		generalSettingsPanel.add(clearDataButton);
		/* ~~~~~~ END GENERAL SETTINGS ~~~~~~ */

		/* ~~~~~~~~ MAP SETTINGS ~~~~~~~~ */
		//container
		mapSettingsPanel = new JPanel();
		//label
		mapSettingsPanelLabel = new JLabel("Map");
		//elements
		manualPointEntryCheckBox = new JCheckBox("Enable Manual Coordinate Entry");
		manualPointEntryCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					manualInputPanel.setVisible(manualPointEntryCheckBox.isSelected());
				}
			}
		});
		manualPointEntryCheckBox.setSelected(false);

		tileLocationChooser = new JFileChooser();
		tileLocationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		tileLocationChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept( File file ) {
				return file.isDirectory();
			}

			@Override
			public String getDescription() {
				return "Directories Only";
			}
		});
		tileLocationChooserButton = new JButton("Load Map Tiles");
		tileLocationChooserButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					//set default directory to 
					tileLocationChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
					int returnVal = tileLocationChooser.showOpenDialog(view);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						System.out.println("You have elected to use the following tile source folder: " +
								tileLocationChooser.getSelectedFile().getName());
						File f = tileLocationChooser.getSelectedFile();
						controller.addTilesToMap(f);
					}
				}
			}
		});

		defaultLocationChooser = new JButton("Set Default Map Location");

		//add elements to container
		mapSettingsPanel.setLayout(new BoxLayout(mapSettingsPanel, BoxLayout.PAGE_AXIS));
		mapSettingsPanel.add(mapSettingsPanelLabel);
		mapSettingsPanel.add(tileLocationChooserButton);
		mapSettingsPanel.add(defaultLocationChooser);
		mapSettingsPanel.add(manualPointEntryCheckBox);
		/* ~~~~~~ END MAP SETTINGS ~~~~~~ */
		

		/* ~~~~~~ TESTING SETTINGS ~~~~~~ */
		//container
		testingSettingsPanel = new JPanel();
		//label
		testingSettingsPanelLabel = new JLabel("Testing");
		//elements
		testingCheckBox = new JCheckBox("Enable Testing Mode");
		ActionListener testingCheckBoxActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				controller.testing = selected;
				controller.refreshDisplay();
			}
		};
		testingCheckBox.addActionListener(testingCheckBoxActionListener);
		testingCheckBox.setSelected(false);

		debugPrintoutsCheckBox = new JCheckBox("Show Debug Printouts");
		ActionListener debugPrintoutsCheckBoxActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				infologscrollpane.setVisible(selected);
			}
		};
		//enabled by default
		debugPrintoutsCheckBox.setSelected(false);
		debugPrintoutsCheckBox.addActionListener(debugPrintoutsCheckBoxActionListener);


		gpsSimFileChooserButton = new JButton("Choose GPS Simulation File");
		gpsSimFileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"GPSIM files only", "gpsim");
		gpsSimFileChooser.setFileFilter(filter);
		gpsSimFileChooserButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					gpsSimFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
					int returnVal = gpsSimFileChooser.showOpenDialog(view);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						System.out.println("You have elected to use this GPS simulation file: " +
								gpsSimFileChooser.getSelectedFile().getName());
						File f = gpsSimFileChooser.getSelectedFile();
						controller.resetTestSender(f);
					}
				}
			}
		});

		//add elements to container
		testingSettingsPanel.setLayout(new BoxLayout(testingSettingsPanel, BoxLayout.PAGE_AXIS));
		testingSettingsPanel.add(testingSettingsPanelLabel);
		testingSettingsPanel.add(testingCheckBox);
		testingSettingsPanel.add(debugPrintoutsCheckBox);
		testingSettingsPanel.add(gpsSimFileChooserButton);
		/* ~~~~ END TESTING SETTINGS ~~~~ */
		

		//add borders for testing
		//testingSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
		//generalSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
		//mapSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));


		/* ~~~~~~~ FINAL ASSEMBLY ~~~~~~~ */
		settingsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.2; c.weighty = 0.3;
		c.insets = new Insets(0,100,0,0);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridy = 0; c.gridx = 0;
        generalSettingsPanel.setPreferredSize(new Dimension(300, 200));
		settingsPanel.add(generalSettingsPanel, c);
		c.weightx = 0.2;
		c.gridy = 0; c.gridx = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(0,0,0,0);
		mapSettingsPanel.setPreferredSize(new Dimension(300, 200));
		settingsPanel.add(mapSettingsPanel, c);
		c.weightx = 0.2;
		c.gridy = 0; c.gridx = 2;
		c.insets = new Insets(0,0,0,100);
		c.anchor = GridBagConstraints.NORTHEAST;
		testingSettingsPanel.setPreferredSize(new Dimension(300, 200));
		settingsPanel.add(testingSettingsPanel, c);
	}

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ *
	 *                                                                       *
	 *                       .: UILITY FUNCTIONS :.                          *
	 *                                                                       *
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */


	public void updateLatestPosition (String position) {
		latestPosition.setText(position);
	}

	public void updateGPSFix (boolean fix) {
		if (fix) {
			gpsStatus.setIcon(ImageFactory.enabledImage());
		}
		else {
			gpsStatus.setIcon(ImageFactory.disabledImage());
		}
	}

	/* Getters and Setters for packet counters*/
	public int getNumSent() { return numSent;}
	public void incNumSent() { numSent++; }
	public int getNumRec() { return numRec; }
	public void incNumRec() { numRec++; }
	public int getNumError() { return numErr; }
	public void incNumError() { numErr++; }
	public void resetPacketCounters() { numSent=0; numRec=0; numErr=0; }

	//get updated data from Radio and display it
	public void updateRadioData (String updateLat, String updateLongi, String updateAlt, String updateFlag) {
		lat.setText(updateLat);
		longi.setText(updateLongi);
		alt.setText(updateAlt);
		flag.setText(updateFlag);
	}

	public void updateViewerTree(JMapViewerTree tree) {
		treeMap = tree;
	}

	public void updateAnalytics
	(double latitude, 
			double longitude, 
			double altitude, 
			long time, 
			double rotation, 
			double acceleration_x,
			double acceleration_y,
			double acceleration_z,
			double temp) {

		//TODO: implement temp
		if (!hasLaunched) {
			startTime = time - 1;
			prevTime = time - 1;
			prevLatitude = latitude;
			prevLongitude = longitude;
			prevSpeed = 0.0;
			hasLaunched = true;
		}

		altitudeTrace.addPoint(time, altitude);
		rotationTrace.addPoint(time, rotation);
		//FIXME: ADD ACCELERATION COMPONENTS
		double acceleration = 
				Math.sqrt
				(Math.pow(acceleration_x, 2) + 
						Math.pow(acceleration_y, 2) + 
						Math.pow(acceleration_z, 2));
		accelTrace.addPoint(time, acceleration); 

		double deltaLat = metersPerDegLat(latitude)-metersPerDegLat(prevLatitude);
		double deltaLon = metersPerDegLon(longitude) - metersPerDegLon(prevLongitude);
		double deltaTime = ((double) (time-prevTime))/1000;

		//double currSpeed = Math.sqrt(deltaAlt*deltaAlt+deltaLat*deltaLat+deltaLon*deltaLon)/deltaTime; 
		double currSpeed = prevSpeed + deltaTime*acceleration;
		currentSpeed.setText(""+currSpeed);

		if (currSpeed > maxAscentSpeedValue){
			maxAscentSpeedValue = currSpeed;
			maxAscentSpeed.setText(""+maxAscentSpeedValue);
		}

		currentAcceleration.setText(""+acceleration);

		double currDriftSpeed = Math.sqrt(deltaLat*deltaLat+deltaLon*deltaLon)/deltaTime;
		if (currDriftSpeed > maxDriftSpeedValue) {
			maxDriftSpeedValue = currDriftSpeed;
			maxDriftSpeed.setText(""+maxDriftSpeedValue);
		}


		String northsouth = ""; String westeast = "";
		if (Math.abs(deltaLat) > driftTol){
			northsouth = deltaLat > 0 ? "North" : "South";
		}
		if (Math.abs(deltaLon) > driftTol){
			westeast = deltaLon < 0 ? "West" : "East";
		}
		currentBearing.setText(northsouth.isEmpty() && westeast.isEmpty() ? "None" : northsouth+" "+westeast);

		currentAltitude.setText(""+altitude);
		if (altitude >= maxAltitudeValue){
			maxAltitudeValue = altitude;
			maxAltitude.setText(""+maxAltitudeValue);
		} else {
			hasApogeed = true;
		}

		if (!hasApogeed){
			currentRotation.setText(""+rotation);
			if (Math.abs(rotation) >= Math.abs(maxRotationValue)){
				maxRotationValue = rotation;
				maxRotation.setText(""+maxRotationValue);
			}
			numRotationDataPoints++;
			averageRotationValue = (averageRotationValue*numRotationDataPoints + rotation)/(numRotationDataPoints);
			averageRotation.setText(""+averageRotationValue);
		} else {
			currentRotation.setText("---");
		}

		String elapsedTime = ""+((double) (time-startTime))/1000;
		elapsedTimeSinceLaunch.setText(elapsedTime);
		if (hasApogeed && !hasApogeedFlag){
			timeToApogeeSinceLaunch.setText(elapsedTime);
			hasApogeedFlag = true;
		}

		//Update Variables
		prevLatitude = latitude;
		prevLongitude = longitude;
		prevTime = time;
	}

	public double metersPerDegLat(double lat){
		return 111132.954 - 559.82*Math.cos(2*lat) + 1.175*Math.cos(4*lat)-0.0023*Math.cos(lat*6);
	}

	public double metersPerDegLon(double lon){
		return 111412.84*Math.cos(lon) - 93.5*Math.cos(3*lon) - 0.118*Math.cos(5*lon);
	}
	//	
	//	/** //MOVED TO CONTROLLER
	//	 * updated the Serial Port List (i.e. after a refresh)
	//	 * @void
	//	 */
	//	public void updateSerialPortsList() {
	//		ArrayList<String> comboBoxList = new ArrayList<String>();
	//		Enumeration portList = CommPortIdentifier.getPortIdentifiers();// this line was false
	//		
	//		while (portList.hasMoreElements()) {
	//			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
	//			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	//			comboBoxList.add(portId.getName());
	//			// System.out.println(portId.getName());
	//			} else {
	//			// System.out.println(portId.getName());
	//			}
	//		}
	//
	//		// update list...
	//		serialPortsList.removeAllItems();
	//		for (String s : comboBoxList) {
	//			serialPortsList.addItem(s);
	//		}
	//	}

	public void updateSerialPortsList (List<String> ports) {
		serialPortsList.removeAllItems();
		for (String p : ports) {
			serialPortsList.addItem(p);
		}
	}


	/**
	 * Adds text to the Received Packets Box
	 * @param txt	text to add
	 */
	public void addToReceiveText(String txt) {
		receiveText.setText(receiveText.getText() + "- " + txt + System.getProperty("line.separator"));
		receiveText.setCaretPosition(receiveText.getDocument().getLength()); // locks scroll at bottom
	}

	/**
	 * After a newline, prints the given string to the text area
	 * in the Control panel of the main window.
	 * @param s
	 */
	public void controlLog(String s) {
		infolog.append(s + System.getProperty("line.separator"));
		infolog.setCaretPosition(infolog.getDocument().getLength()); // locks scroll at bottom
		controlLogToFile(s);
	}

	private void controlLogToFile (String s) {
		//TODO
	}

	/**
	 * Write a message to the log file
	 * @param msg	msg to write
	 */
	/*public void logMessage(String msg) {
	log.info(msg);
	}*/

	private void updateZoomParameters() {
		if (mperpLabelValue!=null)
			mperpLabelValue.setText(String.format("%.2f",map().getMeterPerPixel()));
		if (zoomValue!=null)
			zoomValue.setText(String.format("%s", map().getZoom()));
	}

	@Override
	public void processCommand(JMVCommandEvent command) {
		if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) ||
				command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
			updateZoomParameters();
		}
	}

	/*------------------ Control & Tracking Tab Update Methods ----------------*/

	public void addMapMarkerDot (MapMarkerDot m) {
		map().addMapMarker(m);
	}

	public void clearMapMarkers () {
		map().setMapMarkerList(new ArrayList<MapMarker>());
	}

	public void showMapMarkers (boolean b) {
		map().setMapMarkerVisible(b);
	}

	public void setCameraStatus (Status st) {
		switch (st) {
		case ENABLED:
			cameraStatus.setIcon(ImageFactory.enabledImage());
			break;
		case BUSY:
			cameraStatus.setIcon(ImageFactory.busyImage());
			break;
		case DISABLED:
			cameraStatus.setIcon(ImageFactory.disabledImage());
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	public void setGPSStatus (Status st) {
		switch (st) {
		case ENABLED:
			gpsStatus.setIcon(ImageFactory.enabledImage());
			break;
		case BUSY:
			gpsStatus.setIcon(ImageFactory.busyImage());
			break;
		case DISABLED:
			gpsStatus.setIcon(ImageFactory.disabledImage());
			break;
		default:
			throw new IllegalArgumentException
			("edu.cornell.rocketry.gui.GSGui#setGPSStatus: Invalid Status");
		}
	}

	public void setInitializationStatus (Status st) {
		switch (st) {
		case ENABLED:
			initStatus.setIcon(ImageFactory.enabledImage());
			break;
		case BUSY:
			initStatus.setIcon(ImageFactory.busyImage());
			break;
		case DISABLED:
			initStatus.setIcon(ImageFactory.disabledImage());
			break;
		default:
			throw new IllegalArgumentException
			("edu.cornell.rocketry.gui.GSGui#setInitializationStatus: Invalid Status");
		}
	}

	public void setLaunchStatus (Status st) {
		switch (st) {
		case ENABLED:
			launchStatus.setIcon(ImageFactory.enabledImage());
			break;
		case BUSY:
			launchStatus.setIcon(ImageFactory.busyImage());
			break;
		case DISABLED:
			launchStatus.setIcon(ImageFactory.disabledImage());
			break;
		default:
			throw new IllegalArgumentException
			("edu.cornell.rocketry.gui.GSGui#setLaunchStatus: Invalid Status");
		}
	}

	public void setLandedStatus (Status st) {
		switch (st) {
		case ENABLED:
			landedStatus.setIcon(ImageFactory.enabledImage());
			break;
		case BUSY:
			landedStatus.setIcon(ImageFactory.busyImage());
			break;
		case DISABLED:
			landedStatus.setIcon(ImageFactory.disabledImage());
			break;
		default:
			throw new IllegalArgumentException
			("edu.cornell.rocketry.GSGui#setLandedStatus: Invalid Status");
		}
	}
}