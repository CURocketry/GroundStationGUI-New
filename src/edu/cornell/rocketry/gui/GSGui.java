// License: GPL. For details, see Readme.txt file.
package edu.cornell.rocketry.gui;

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
import edu.cornell.rocketry.util.CommandTask;
import edu.cornell.rocketry.util.GPSResponse;
import edu.cornell.rocketry.util.RocketSimulator;
import edu.cornell.rocketry.util.PayloadStatus;
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
    private JPanel downloadPanel;
    private JPanel xbeePanel;
    private JPanel settingsPanel;
    
    JTabbedPane tabbedPane;
    
    /*------------------------ Control Tab Fields ---------------------------*/
    JPanel status;
    JPanel gpsControls;
    JPanel payloadControls;
    
    
    JPanel statusSection;
    JPanel controlsSection;
    JPanel trajpanel;
    
    JScrollPane infologscrollpane;
    JTextArea infolog;
    
    JLabel payloadStatusLabel;
    JLabel payloadStatus;
    
    JButton settings = new JButton ("Settings");
    JButton startGPSButton = new JButton("Start GPS");
    JButton stopGPSButton = new JButton("Stop GPS");
    
    JButton enablePayloadButton = new JButton("Enable Payload");
    JButton disablePayloadButton = new JButton("Disable Payload");
    
    private Plot3DPanel trajectoryplot = new Plot3DPanel(); //FIXME


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
    JLabel lonLabel = new JLabel("Longitude: ");
    JLabel latLabel = new JLabel("Latitude: ");
    JButton manualInputButton = new JButton("Add Map Marker");
    JButton clearMapMarkersButton = new JButton("Clear Map Markers");
    
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
	private JTextArea rocketText, payloadText;
	private JTextField sendEdit;
	private final static Font titleFont = new Font("Arial", Font.BOLD, 20);
	private final static Font textAreaFont = new Font("Arial", Font.PLAIN, 10);
	
	protected XBeeAddress64 selectedAddress;				//selected address
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
	private JCheckBox showMinimapCheckBox;
	private JButton defaultLocationChooser;
	private JCheckBox manualPointEntryCheckBox;
	
	/* OTHER */
	//container
	private JPanel otherSettingsPanel;
	//label
	private JLabel otherSettingsPanelLabel;
	//elements
	private JCheckBox autoRetryOnCommFailureCheckBox;
	

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
        
        initializeRecoveryTab();
        
        initializeDownloadTab();
        
        initializeXBeeTab();
        
        initializeSettingsTab();
        
        /*------------------ Create Tabbed Pane & Add Tabs ------------------*/   
        
        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Control", null, controlPanel, "GS Control Tab");
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
            	  System.out.println("Giving to Control");
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
        payloadStatusLabel = new JLabel("Payload Status");
        payloadStatusLabel.setOpaque(false);
        payloadStatusLabel.setForeground(Color.WHITE);
        payloadStatus = new JLabel();
        payloadStatus.setIcon(new ImageIcon ("./assets/red_icon_20_20.jpg"));
        payloadStatus.setOpaque(false);
        status.setOpaque(false);
        status.add(payloadStatusLabel, BorderLayout.WEST);
        status.add(payloadStatus, BorderLayout.EAST);
        
        
        //start GPS button
        startGPSButton.setVisible(true);
        startGPSButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	clearMapMarkers();
                	//no longer necessary 3/7/15
                	//controller.sendCommand (CommandTask.StopGPS);
                	controller.sendCommand (CommandTask.StartGPS);
                }
            }
        });
        //stop GPS button
        stopGPSButton.setVisible(true);
        stopGPSButton.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		if (e.getButton() == MouseEvent.BUTTON1) {
        			controller.sendCommand(CommandTask.StopGPS);
        		}
        	}
        });
        //enable payload button
        enablePayloadButton.setVisible(true);
        enablePayloadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    controller.sendCommand (CommandTask.EnablePayload);
                }
            }
        });
        //disable payload button
        disablePayloadButton.setVisible(true);
        disablePayloadButton.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		if (e.getButton() == MouseEvent.BUTTON1){
        			controller.sendCommand (CommandTask.DisablePayload);
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
        
        statusSection.setOpaque(false);
        infologscrollpane.setOpaque(false);
        
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
        controlPanel.add(enablePayloadButton, c);

        c.gridx = 3;
        c.gridy = 1;
        controlPanel.add(disablePayloadButton, c);
        
        controlPanel.setVisible(true);
        controlPanel.validate();
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
		JLabel payloadTitle = new JLabel ("Payload",JLabel.LEFT);
		payloadTitle.setFont(titleFont);
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
		tablePanel.add(payloadTitle);
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
    	        //infologpanel.setVisible(selected);
    	      }
    	};
    	debugPrintoutsCheckBox.addActionListener(debugPrintoutsCheckBoxActionListener);
    	debugPrintoutsCheckBox.setSelected(false);
    	
    	
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
    	
    	showMinimapCheckBox = new JCheckBox("Show Minimap");
    	
    	defaultLocationChooser = new JButton("Set Default Map Location");
    	
    	
    	//add elements to container
    	mapSettingsPanel.setLayout(new BoxLayout(mapSettingsPanel, BoxLayout.Y_AXIS));
    	mapSettingsPanel.add(mapSettingsPanelLabel);
    	mapSettingsPanel.add(tileLocationChooserButton);
    	mapSettingsPanel.add(showMinimapCheckBox);
    	mapSettingsPanel.add(defaultLocationChooser);
    	mapSettingsPanel.add(manualPointEntryCheckBox);
    	/* ~~~~~~ END MAP SETTINGS ~~~~~~ */
    	
    	/* ~~~~~~~ OTHER SETTINGS ~~~~~~~ */
    	//container
    	otherSettingsPanel = new JPanel(new BorderLayout());
    	//label
    	otherSettingsPanelLabel = new JLabel("Other");
    	//elements
    	autoRetryOnCommFailureCheckBox = new JCheckBox("Auto Retry on Comm Failure");
    	//add elements to container
    	otherSettingsPanel.setLayout(new BoxLayout(otherSettingsPanel, BoxLayout.Y_AXIS));
    	otherSettingsPanel.add(otherSettingsPanelLabel);
    	otherSettingsPanel.add(autoRetryOnCommFailureCheckBox);
    	/* ~~~~~ END OTHER SETTINGS ~~~~~ */
    	
    	
    	//add borders for testing
    	testingSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
    	mapSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
    	otherSettingsPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
    	
    	
    	/* ~~~~~~~ FINAL ASSEMBLY ~~~~~~~ */
    	settingsPanel = new JPanel(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	c.ipadx = 50; c.ipady = 50;
    	c.weightx = 0.2;
    	c.gridy = 0; c.gridx = 0;
    	c.anchor = GridBagConstraints.LINE_START;
    	settingsPanel.add(testingSettingsPanel, c);
    	c.weightx = 0.2;
    	c.gridy = 0; c.gridx = 1;
    	c.anchor = GridBagConstraints.CENTER;
    	settingsPanel.add(mapSettingsPanel, c);
    	c.weightx = 0.2;
    	c.gridy = 0; c.gridx = 2;
    	c.anchor = GridBagConstraints.LINE_END;
    	settingsPanel.add(otherSettingsPanel, c);
    }
    
    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ *
     *                                                                       *
     *                       .: UILITY FUNCTIONS :.                          *
     *                                                                       *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	
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
	 * @param txt			text to add
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
	 * @param msg			msg to write
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
    
    public void setPayloadStatus (PayloadStatus st) {
    	switch (st) {
    	case Enabled:
    		payloadStatus.setIcon(new ImageIcon("./assets/green_icon_20_20.jpg"));
    		break;
    	case Busy:
    		payloadStatus.setIcon(new ImageIcon("./assets/yellow_icon_20_20.jpg"));
    		break;
    	case Disabled:
    		payloadStatus.setIcon(new ImageIcon("./assets/red_icon_20_20.jpg"));
    		break;
    	default:
    		throw new IllegalArgumentException();
    	}
    }

    
}
