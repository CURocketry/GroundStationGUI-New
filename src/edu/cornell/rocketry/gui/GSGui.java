package edu.cornell.rocketry.gui;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterSimple;

import jTile.src.org.openstreetmap.fma.jtiledownloader.config.AppConfiguration;
import jTile.src.org.openstreetmap.fma.jtiledownloader.views.main.JTileDownloaderMainViewPanel;
import jTile.src.org.openstreetmap.fma.jtiledownloader.views.main.MainPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
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
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;

import edu.cornell.rocketry.gui.Controller;
import edu.cornell.rocketry.util.CommandType;
import edu.cornell.rocketry.util.TEMResponse;
import edu.cornell.rocketry.util.GPSStatus;
import edu.cornell.rocketry.util.ImageFactory;
import edu.cornell.rocketry.util.RocketSimulator;
import edu.cornell.rocketry.util.CameraStatus;
import edu.cornell.rocketry.xbee.OutgoingPacket;
import edu.cornell.rocketry.xbee.OutgoingPacketType;
import edu.cornell.rocketry.xbee.XBeeListenerThread;
import edu.cornell.rocketry.xbee.XBeeSender;
import edu.cornell.rocketry.xbee.XBeeSenderException;
import gnu.io.CommPortIdentifier;


/**
 * Demonstrates the usage of {@link JMapViewer}
 *
 * @author Jan Peter Stotz
 *
 */
public class GSGui extends JFrame implements JMapViewerEventListener {

	
	
	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ *
     *                                                                       *
     *                     .: GUI FIELD DECLARATION :.                       *
     *                                                                       *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    private static final long serialVersionUID = 1L;
    

    final GSGui view = this;
    
    private Controller controller;// = new Controller(this);

    //tab panel declarations
    JMapViewerTree treeMap = null;
    private JPanel controlPanel;
    private JPanel analyticsPanel;
    private JPanel downloadPanel;
    private JPanel xbeePanel;
    private JPanel settingsPanel;
    
    JTabbedPane tabbedPane;
    
    /*------------------------ Control Tab Fields ---------------------------*/
    JPanel status;
    JPanel gpsControls;
    JPanel cameraControls;
    JPanel latestPositionPanel;
    JLabel latestPosition;
    
    
    JPanel statusSection;
    JPanel controlsSection;
    JPanel trajpanel;
    
    JPanel cameraStatusContainer;
    JPanel gpsStatusContainer;
    
    JScrollPane infologscrollpane;
    JTextArea infolog;
    
    JLabel cameraStatusLabel;
    JLabel cameraStatus;
    
    JLabel gpsStatusLabel;
    JLabel gpsStatus;
    
    JButton settings = new JButton ("Settings");
    JButton startGPSButton = new JButton("Start GPS");
    JButton stopGPSButton = new JButton("Stop GPS");
    
    JButton enableCameraButton = new JButton("Enable Camera");
    JButton disableCameraButton = new JButton("Disable Camera");
    
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
    
    public static Chart2D altitudeChart = new Chart2D();
    public static ITrace2D altitudeTrace = new Trace2DSimple();
    public static Chart2D rotationChart = new Chart2D();
    public static ITrace2D rotationTrace = new Trace2DSimple();
    public static Chart2D accelChart = new Chart2D();
    public static ITrace2D accelTrace = new Trace2DSimple();
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
    
    
    
    /*--------------------------- XBee Tab Fields ---------------------------*/
    
    public static final Integer[] baudRates = {4800, 9600, 19200, 38400, 57600, 115200};
	public static final String[] addresses = { 
	"1: 0013A200 / 40BF5647", 
	"2: 0013A200 / 40BF56A5",
	"3: 0013A200 / 409179A7",
	"4: 0013A200 / 4091796F"
	};
	public static final XBeeAddress64 addr[] = {
	  new XBeeAddress64(0, 0x13, 0xa2, 0, 0x40, 0xbf, 0x56, 0x47),	//long cable
	  new XBeeAddress64(0, 0x13, 0xa2, 0, 0x40, 0xbf, 0x56, 0xa5),	//new xbees, small cable
	  new XBeeAddress64(0, 0x13, 0xa2, 0, 0x40, 0x91, 0x79, 0xa7),
	  new XBeeAddress64(0, 0x13, 0xa2, 0, 0x40, 0x91, 0x79, 0x6f)
	};
	
	private int numRec = 0; 	//number received packets
	private int numSent = 0;	//number sent packets
	private int numErr = 0; 	//number error packets

	private JLabel packetLabel;
	private JLabel nLabel;

	private JTextArea receiveText;
	private JTextArea rocketText, cameraText;
	private JTextField sendEdit;
	private final static Font titleFont = new Font("Arial", Font.BOLD, 20);
	private final static Font textAreaFont = new Font("Arial", Font.PLAIN, 10);
	
	protected XBeeAddress64 selectedAddress;	//selected address
	protected int selectedBaud = 57600; //serial comm rate

	protected JComboBox<String> serialPortsList, addressesList;
	protected JComboBox<Integer> baudList;
	
	private JPanel statusPanel, dataPanel, tablePanel;
	private static JLabel lat,longi,alt,flag;
	
	
	//private static Logger log = Logger.getLogger(GSGui.class.getName());
    
	
	
	
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
    public GSGui() {
        super("CURocketry Ground Station GUI");
        setSize(500, 500);
        
        ImageFactory.init();

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
        	System.out.println("Closing log file");
        	controller.logger().close();
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
        
        initializeXBeeTab();
        
        initializeSettingsTab();
        
        /*------------------ Create Tabbed Pane & Add Tabs ------------------*/   
        
        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Control", null, controlPanel, "GS Control Tab");
        tabbedPane.addTab("Analytics", null, analyticsPanel, "Analytics Tab");
        tabbedPane.addTab("Recovery", null, treeMap, "Recovery Tracking Tab");
        tabbedPane.addTab("Download", null, downloadPanel, "Map Downloading Tab");
        tabbedPane.addTab("XBee", null, xbeePanel, "XBee Setup Tab");
        tabbedPane.addTab("Settings", null, settingsPanel, "Settings Tab");
        
        /* Activate the Tabbed Pane */
        setContentPane(tabbedPane);
        //getContentPane().addChild(tabbedPane);
        
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
                  c.gridy = 2;
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
    
    JMapViewer map(){
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
        
        //controlPanel.setBackground(Color.WHITE);
        //Image background = Toolkit.getDefaultToolkit().createImage("./assets/black_wood_background.jpg");
        //controlPanel.drawImage(background, 0, 0, null);
        controlPanel.setLayout(new GridBagLayout());
        
        //status indicators
        status = new JPanel(new BorderLayout());
        status.setOpaque(false);
        cameraStatusContainer = new JPanel(new BorderLayout());
        gpsStatusContainer = new JPanel(new BorderLayout());
        
        //display of last known position
        latestPositionPanel = new JPanel(new BorderLayout());
        latestPositionPanel.setOpaque(false);
        latestPosition = new JLabel("no data");
        JLabel latestPositionLabel = new JLabel("Last Known Position: ");
        latestPositionLabel.setForeground(Color.WHITE);
        latestPosition.setForeground(Color.WHITE);
        latestPositionPanel.add(latestPositionLabel, BorderLayout.WEST);
        latestPositionPanel.add(latestPosition, BorderLayout.EAST);
        
        
        cameraStatusLabel = new JLabel("Camera Status: ");
        cameraStatusLabel.setOpaque(false);
        cameraStatusLabel.setForeground(Color.WHITE);
        cameraStatus = new JLabel();
        cameraStatus.setIcon(ImageFactory.disabledImage());
        cameraStatus.setOpaque(false);
        
        cameraStatusContainer.add(cameraStatusLabel, BorderLayout.WEST);
        cameraStatusContainer.add(cameraStatus, BorderLayout.EAST);
        
        gpsStatusLabel = new JLabel("GPS Status: ");
        gpsStatusLabel.setOpaque(false);
        gpsStatusLabel.setForeground(Color.WHITE);
        gpsStatus = new JLabel();
        gpsStatus.setIcon(ImageFactory.disabledImage());
        gpsStatus.setOpaque(false);
        
        
        gpsStatusContainer.add(gpsStatusLabel, BorderLayout.WEST);
        gpsStatusContainer.add(gpsStatus, BorderLayout.EAST);
        
        gpsStatusContainer.setOpaque(false);
        cameraStatusContainer.setOpaque(false);
        
        status.add(gpsStatusContainer, BorderLayout.WEST);
        status.add(cameraStatusContainer, BorderLayout.EAST);
        
        
        //start GPS button
        startGPSButton.setVisible(true);
        startGPSButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	clearMapMarkers();
                	//no longer necessary 3/7/15
                	//controller.sendCommand (CommandTask.StopGPS);
                	controller.commController().startListening();
                	controller.sendCommand (CommandType.TRANSMIT_START);
                }
            }
        });
        //stop GPS button
        stopGPSButton.setVisible(true);
        stopGPSButton.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
	        	if (e.getButton() == MouseEvent.BUTTON1) {
	        		controller.sendCommand(CommandType.TRANSMIT_HALT);
	        	}
        	}
        });
        //enable camera button
        enableCameraButton.setVisible(true);
        enableCameraButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    controller.sendCommand (CommandType.ENABLE_CAMERA);
                }
            }
        });
        //disable camera button
        disableCameraButton.setVisible(true);
        disableCameraButton.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
	        	if (e.getButton() == MouseEvent.BUTTON1){
	        		controller.sendCommand (CommandType.DISABLE_CAMERA);
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
        
        infologscrollpane.setVisible(false); //by default, toggled in settings
        
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
        c.gridy = 2;
        c.weightx = 1.0;
        c.weighty = 0.9;
        controlPanel.add(trajpanel, c);

        c.gridx = 2;
        c.gridy = 2;
        controlPanel.add(treeMap.getViewer(), c);

        c.gridx = 0;
        c.gridy = 3;
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
        controlPanel.add(startGPSButton, c);
        
        c.gridx = 1;
        c.gridy = 1;
        controlPanel.add(stopGPSButton, c);
        
        c.gridx = 2;
        c.gridy = 1;
        controlPanel.add(enableCameraButton, c);

        c.gridx = 3;
        c.gridy = 1;
        controlPanel.add(disableCameraButton, c);
        
        controlPanel.setVisible(true);
        controlPanel.validate();
    }

    public void initializeAnalyticsTab(){
        analyticsPanel = new JPanel();
        JPanel data = new JPanel(new GridBagLayout()); 
        GridBagConstraints c = new GridBagConstraints();
        
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
        
        analyticsPanel.add(data);
        
        c.ipadx = 7; c.ipady = 7;
        c.gridx = 0; c.gridy = 0;
        data.add(currentSpeedLabel, c);
        c.gridx = 1; c.gridy = 0;
        data.add(currentSpeed, c);
        
        c.gridx = 2; c.gridy = 0;
        data.add(maxAscentSpeedLabel, c);
        c.gridx = 3; c.gridy = 0;
        data.add(maxAscentSpeed, c);
        
        c.gridx = 4; c.gridy = 0;
        data.add(currentAccelerationLabel, c);
        c.gridx = 5; c.gridy = 0;
        data.add(currentAcceleration, c);
        
        c.gridx = 6; c.gridy = 0;
        data.add(maxDriftSpeedLabel, c);
        c.gridx = 7; c.gridy = 0;
        data.add(maxDriftSpeed, c);
        
        c.gridx = 0; c.gridy = 1;
        data.add(currentBearingLabel, c);
        c.gridx = 1; c.gridy = 1;
        data.add(currentBearing, c);
        
        c.gridx = 2; c.gridy = 1;
        data.add(currentAltitudeLabel, c);
        c.gridx = 3; c.gridy = 1;
        data.add(currentAltitude, c);   
        
        c.gridx = 4; c.gridy = 1;
        data.add(maxAltitudeLabel, c);
        c.gridx = 5; c.gridy = 1;
        data.add(maxAltitude, c);
        
        c.gridx = 6; c.gridy = 1;
        data.add(currentRotationLabel, c);
        c.gridx = 7; c.gridy = 1;
        data.add(currentRotation, c);
        
        c.gridx = 0; c.gridy = 2;
        data.add(averageRotationLabel, c);
        c.gridx = 1; c.gridy = 2;
        data.add(averageRotation, c);
        
        c.gridx = 2; c.gridy = 2;
        data.add(maxRotationLabel, c);
        c.gridx = 3; c.gridy = 2;
        data.add(maxRotation, c);
       
        c.gridx = 4; c.gridy = 2;
        data.add(elapsedTimeSinceLaunchLabel, c);
        c.gridx = 5; c.gridy = 2;
        data.add(elapsedTimeSinceLaunch, c);
        
        c.gridx = 6; c.gridy = 2;
        data.add(timeToApogeeSinceLaunchLabel, c);
        c.gridx = 7; c.gridy = 2;
        data.add(timeToApogeeSinceLaunch, c);
        
        c.gridx = 0; c.gridy = 3;
        data.add(atimeLabel, c);
        c.gridx = 1; c.gridy = 3;
        //atimeInput.setSize(100, 200);
        c.fill = GridBagConstraints.BOTH;
        data.add(atimeInput, c);
        //c.gridwidth = 1;
        c.gridx = 2; c.gridy = 3;
        data.add(alimitMapMarkersButton, c);
        
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
      
        c.gridx = 0; c.gridy = 4;
        c.ipadx = 300;
        c.ipady = 300;
        c.gridwidth = 2;
        data.add(altitudeChart, c);
        
        c.gridx = 3; c.gridy = 4;
        c.ipadx = 300;
        c.ipady = 300;
        c.gridwidth = 2;
        data.add(rotationChart, c);
        
        c.gridx = 6; c.gridy = 4;
        c.ipadx = 300;
        c.ipady = 300;
        c.gridwidth = 2;
        data.add(accelChart, c);
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
    
    private void initializeXBeeTab() {
    	PropertyConfigurator.configure("./lib/log4j.properties");

	// Layout GUI
	xbeePanel = new JPanel(new BorderLayout());

	/*-- Setup XBees Panel --*/
	
	JPanel xbeeInitPanel = new JPanel(new BorderLayout());
	JLabel xbeeInitLabel = new JLabel("Setup XBees", JLabel.CENTER);
	xbeeInitLabel.setFont(titleFont);
	xbeeInitPanel.add(xbeeInitLabel, BorderLayout.NORTH);
	JPanel xbeeInitGrid = new JPanel(new GridLayout(5, 2));
	
	//XBee Serial Port Label
	JPanel serialPortPanel = new JPanel(new BorderLayout());
	serialPortPanel.add(new JLabel("GS XBee Serial Port: "), BorderLayout.WEST);

	//Serial port dropdown
	serialPortsList = new JComboBox<String>(); //initialize empty dropdown
	controller.updateSerialPortsList();
	serialPortsList.setSelectedIndex(serialPortsList.getItemCount() - 1);

	//Refresh serial ports button
	serialPortPanel.add(serialPortsList, BorderLayout.CENTER);
	JButton refreshPortsBtn = new JButton("Refresh");
	refreshPortsBtn.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	controller.updateSerialPortsList();
	}
	});
	serialPortPanel.add(refreshPortsBtn, BorderLayout.EAST);
	xbeeInitGrid.add(serialPortPanel);

	//Wireless Address Dropdown
	JPanel addressPanel = new JPanel(new BorderLayout());
	addressPanel.add(new JLabel("Remote XBee Address: "), BorderLayout.WEST);
	addressesList = new JComboBox<String>(addresses);
	addressesList.setSelectedIndex(0);
	controller.updateSelectedAddress();
	addressesList.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	controller.updateSelectedAddress();
	}
	});
	addressPanel.add(addressesList, BorderLayout.CENTER);
	xbeeInitGrid.add(addressPanel);
	
	//Baud rate dropdown
	JPanel baudPanel = new JPanel(new BorderLayout());
	baudPanel.add(new JLabel("XBee Baud Rate: "), BorderLayout.WEST);
	baudList = new JComboBox<Integer>(baudRates);
	baudList.setSelectedIndex(4);
	controller.updateSelectedBaudRate();
	addressesList.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	controller.updateSelectedBaudRate();
	}
	});
	baudPanel.add(baudList, BorderLayout.CENTER);
	xbeeInitGrid.add(baudPanel);
	

	//Initialize GS XBee Button
	JButton initXBeeButton = new JButton("Initialize GS XBee");
	initXBeeButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	try {
	controller.initXbee();
	addToReceiveText("Success! Initialized GS XBee :)");
	addToReceiveText("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
	+ System.getProperty("line.separator"));
	} catch (XBeeException e1) {
	e1.printStackTrace();
	numErr++;
	addToReceiveText("Error ("
	+ numErr
	+ "): Could not connect to XBee :( Make sure port isn't being used by another program (including this one)!");
	}
	}
	});
	xbeeInitGrid.add(initXBeeButton);
	xbeeInitPanel.add(xbeeInitGrid, BorderLayout.CENTER);
	
	//Send Packet Title and Button
	JPanel sendPacketsPanel = new JPanel(new BorderLayout());
	JPanel sendPacketsGrid = new JPanel(new GridLayout(5, 2));
	JLabel sendTitle = new JLabel("Send Packets", JLabel.CENTER);
	sendTitle.setFont(titleFont);
	sendPacketsPanel.add(sendTitle, BorderLayout.NORTH);

	//Test Send Button
	JButton testSendBtn = new JButton("Send Test");
	testSendBtn.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	controller.sendXBeePacket("(Test Packet)");
	}
	});
	sendPacketsGrid.add(testSendBtn);
	
	//Send custom data box
	JButton customDataBtn = new JButton("Send Data");
	customDataBtn.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	controller.sendXBeePacket(sendEdit.getText());
	}

	});

	sendPacketsGrid.add(customDataBtn, BorderLayout.CENTER);
	
	//Send Custom Packet Textbox
	JPanel customPacketEntry = new JPanel(new BorderLayout());
	customPacketEntry.add(new JLabel("Send Packet: "), BorderLayout.WEST);
	sendEdit = new JTextField("", 20);
	customPacketEntry.add(sendEdit, BorderLayout.CENTER);
	sendPacketsGrid.add(customPacketEntry,BorderLayout.SOUTH);
	
	sendPacketsPanel.add(sendPacketsGrid, BorderLayout.CENTER);
	
	JPanel PContainer = new JPanel(new BorderLayout());
	PContainer.add(xbeeInitPanel, BorderLayout.NORTH);
	PContainer.add(sendPacketsPanel, BorderLayout.CENTER);

	/*-- Received Packets Panel-- */
	JPanel receivePanel = new JPanel(new BorderLayout());
	receiveText = new JTextArea(40, 60);
	receiveText.setBackground(Color.white);
	receiveText.setFont(textAreaFont);
	receiveText.setLineWrap(true);
	receiveText.setWrapStyleWord(true);
	receiveText.setEditable(false);
	JScrollPane receiveScrollPlane = new JScrollPane(receiveText);

	JLabel receiveTitle = new JLabel("Received Packets", JLabel.CENTER);
	receiveTitle.setFont(titleFont);
	receivePanel.add(receiveTitle, BorderLayout.NORTH);
	receivePanel.add(receiveScrollPlane,BorderLayout.EAST);
	
	/*-- Status Panel --*/
	statusPanel = new JPanel();
	JLabel statusTitle = new JLabel ("STATUS",JLabel.LEFT);
	statusTitle.setFont(titleFont);
	statusPanel.add(statusTitle);
	
	dataPanel = new JPanel (new BorderLayout());
	tablePanel = new JPanel (new GridLayout(3,5));
	JLabel rocketTitle = new JLabel ("Rocket",JLabel.LEFT);
	rocketTitle.setFont(titleFont);
	JLabel latTitle = new JLabel ("Latitude",JLabel.LEFT);
	latTitle.setFont(titleFont);
	JLabel longTitle = new JLabel ("Longitude",JLabel.LEFT);
	longTitle.setFont(titleFont);
	JLabel altTitle = new JLabel ("Altitude",JLabel.LEFT);
	altTitle.setFont(titleFont);
	JLabel enableTitle = new JLabel ("Enabled (Yes/No)",JLabel.LEFT);
	enableTitle.setFont(titleFont);

	tablePanel.add(new JLabel("", JLabel.LEFT)); //TODO!!!
	tablePanel.add(latTitle);
	tablePanel.add(longTitle);
	tablePanel.add(altTitle);
	tablePanel.add(enableTitle);
	tablePanel.add(rocketTitle);	
	lat = new JLabel("0", JLabel.LEFT);
	tablePanel.add(lat);
	longi = new JLabel("0",JLabel.LEFT);
	tablePanel.add(longi); 
	alt = new JLabel("0",JLabel.LEFT);
	tablePanel.add(alt);
	flag = new JLabel("-",JLabel.LEFT);
	tablePanel.add(flag);
	tablePanel.add(new JLabel("N/A", JLabel.LEFT));
	tablePanel.add(new JLabel("N/A", JLabel.LEFT));
	tablePanel.add(new JLabel("N/A", JLabel.LEFT));
	tablePanel.add(new JLabel("N/A", JLabel.LEFT));
	
	dataPanel.add(statusPanel, BorderLayout.NORTH);
	dataPanel.add(tablePanel, BorderLayout.SOUTH);

	xbeePanel.add(dataPanel, BorderLayout.SOUTH);
	
	xbeePanel.add(PContainer,BorderLayout.WEST);
	xbeePanel.add(receivePanel,BorderLayout.CENTER);
	
	
	// Text area stuff...
    }
    
    private void initializeSettingsTab() {
    	
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
        testingSettingsPanel.setLayout(new BoxLayout(testingSettingsPanel, BoxLayout.Y_AXIS));
        testingSettingsPanel.add(testingSettingsPanelLabel);
    	testingSettingsPanel.add(testingCheckBox);
    	testingSettingsPanel.add(debugPrintoutsCheckBox);
    	testingSettingsPanel.add(gpsSimFileChooserButton);
    	/* ~~~~ END TESTING SETTINGS ~~~~ */
    	
    	
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
    	mapSettingsPanel.setLayout(new BoxLayout(mapSettingsPanel, BoxLayout.Y_AXIS));
    	mapSettingsPanel.add(mapSettingsPanelLabel);
    	mapSettingsPanel.add(tileLocationChooserButton);
    	mapSettingsPanel.add(defaultLocationChooser);
    	mapSettingsPanel.add(manualPointEntryCheckBox);
    	/* ~~~~~~ END MAP SETTINGS ~~~~~~ */
    	
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
    	generalSettingsPanel.setLayout(new BoxLayout(generalSettingsPanel, BoxLayout.Y_AXIS));
    	generalSettingsPanel.add(generalSettingsPanelLabel);
    	generalSettingsPanel.add(clearDataButton);
    	/* ~~~~~~ END GENERAL SETTINGS ~~~~~~ */
    	
    	
    	//add borders for testing
    	testingSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
    	generalSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
    	mapSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
    	
    	
    	/* ~~~~~~~ FINAL ASSEMBLY ~~~~~~~ */
    	settingsPanel = new JPanel(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	c.ipadx = 50; c.ipady = 50;
    	c.weightx = 0.2;
    	c.gridy = 0; c.gridx = 1;
    	c.anchor = GridBagConstraints.LINE_END;
    	settingsPanel.add(testingSettingsPanel, c);
    	c.weightx = 0.2;
    	c.gridy = 0; c.gridx = 2;
    	c.anchor = GridBagConstraints.CENTER;
    	settingsPanel.add(mapSettingsPanel, c);
    	c.weightx = 0.2;
    	c.gridy = 0; c.gridx = 0;
    	c.anchor = GridBagConstraints.LINE_START;
    	settingsPanel.add(generalSettingsPanel, c);
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
	
	//get updated data from XBee and display it
	public void updateXBeeData (String updateLat, String updateLongi, String updateAlt, String updateFlag) {
	lat.setText(updateLat);
	longi.setText(updateLongi);
	alt.setText(updateAlt);
	flag.setText(updateFlag);
	}

	public void updateViewerTree(JMapViewerTree tree) {
	treeMap = tree;
	}

    public void updateAnalytics(double latitude, double longitude, double altitude, 
            long time, double rotation, double acceleration){
        if (!hasLaunched){
            startTime = time - 1;
            prevTime = time - 1;
            prevLatitude = latitude;
            prevLongitude = longitude;
            prevSpeed = 0.0;
            hasLaunched = true;
        }
        
        altitudeTrace.addPoint(time, altitude);
        rotationTrace.addPoint(time, rotation);
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
		serialPortsList.removeAllItems();
		for (String s : comboBoxList) {
		serialPortsList.addItem(s);
		}
	}

	/**
	 * Adds text to the Received Packets Box
	 * @param txt	text to add
	 */
	public void addToReceiveText(String txt) {
	receiveText.setText(receiveText.getText() + "- " + txt + System.getProperty("line.separator"));
	receiveText.setCaretPosition(receiveText.getDocument().getLength()); // locks scroll at bottom
	//logMessage(txt);
	}
	
	/**
	 * After a newline, prints the given string to the text area
	 * in the Control panel of the main window.
	 * @param s
	 */
	public void controlLog(String s) {
	//infolog.setText(infolog.getText() + ">> " + s + System.getProperty("line.separator"));
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
    
    public void setCameraStatus (CameraStatus st) {
    	switch (st) {
    	case Enabled:
    	cameraStatus.setIcon(ImageFactory.enabledImage());
    	break;
    	case Busy:
    	cameraStatus.setIcon(ImageFactory.busyImage());
    	break;
    	case Disabled:
    	cameraStatus.setIcon(ImageFactory.disabledImage());
    	break;
    	default:
    	throw new IllegalArgumentException();
    	}
    }
    
    public void setGPSStatus (GPSStatus st) {
    	switch (st) {
    	case Fix:
    	gpsStatus.setIcon(ImageFactory.enabledImage());
    	break;
    	case Unknown:
    	gpsStatus.setIcon(ImageFactory.busyImage());
    	break;
    	case NoFix:
    	gpsStatus.setIcon(ImageFactory.disabledImage());
    	break;
    	default:
    	throw new IllegalArgumentException();
    	}
    }
}