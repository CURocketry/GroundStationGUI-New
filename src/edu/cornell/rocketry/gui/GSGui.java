// License: GPL. For details, see Readme.txt file.
package edu.cornell.rocketry.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
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
import edu.cornell.rocketry.util.GPSSpoofer;
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
public class GSGui extends JFrame 
			implements JMapViewerEventListener /*, ActionListener*/  {

    private static final long serialVersionUID = 1L;

    
    private JMapViewerTree treeMap = null;
    private JPanel controlPanel;
    private JPanel downloadPanel;
    private JPanel xbeePanel;
    
    //controller (here called handler)
    private Controller handler = new Controller(this);
    
    /*------------------------ Control Tab Fields ---------------------------*/
    JPanel minimap;
    JPanel status;
    JPanel controls;
    JPanel infologpanel;
    
    JScrollPane infologscrollpane;
    JTextArea infolog;
    
    JLabel payloadStatusLabel;
    JLabel payloadStatus;
    
    JButton settings = new JButton ("Settings");
    JButton sequenceButton = new JButton("Start Sequence");
    JButton payloadButton = new JButton("Enable Payload");

    /*------------------------ Recovery Tab Fields --------------------------*/
    private JLabel zoomLabel=null;
    private JLabel zoomValue=null;
    
    private JLabel mperpLabelName=null;
    private JLabel mperpLabelValue = null;
    
    JPanel panel = new JPanel();
    JPanel panelTop = new JPanel();
    JPanel panelBottom = new JPanel();
    JPanel helpPanel = new JPanel();
    
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
	private int selectedBaud = 57600; //serial comm rate
	private XBeeAddress64 selectedAddress;				//selected address
	
	public XBee xbee; //keep as public reference @see XBeeListenerThread.java
	XBeeListenerThread xbeeListener;
	
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

	private JComboBox<String> serialPortsList, addressesList;
	private JComboBox<Integer> baudList;
	
	private JPanel statusPanel, dataPanel, tablePanel;
	private static JLabel lat,longi,alt,flag;
	
	
	private static Logger log = Logger.getLogger(GSGui.class.getName());
	
	
	/* Getters and Setters for packet counters*/
	public int getNumSent() { return numSent;}
	public void incNumSent() { numSent++; }
	public int getNumRec() { return numRec; }
	public void incNumRec() { numRec++; }
	public int getNumError() { return numErr; }
	public void incNumError() { numErr++; }
	public void resetPacketCounters() { numSent=0; numRec=0; numErr=0; }
	

    /**
     * Constructs the Rocketry GS Gui.
     */
    public GSGui() {
        super("CURocketry Ground Station GUI");
        setSize(500, 500);
        
        
        treeMap = new JMapViewerTree("Zones", false);

        // Listen to the map viewer for user operations so components will
        // receive events and update
        map().addJMVListener(this);

        // final JMapViewer map = new JMapViewer(new MemoryTileCache(),4);
        // map.setTileLoader(new OsmFileCacheTileLoader(map));
        // new DefaultMapController(map);

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        
        
        
        /*--------------------------- Control Tab ---------------------------*/
        
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
        payloadStatus.setOpaque(true);
        status.setOpaque(false);
        status.add(payloadStatusLabel, BorderLayout.NORTH);
        status.add(payloadStatus, BorderLayout.SOUTH);
        
        //controls
        controls = new JPanel(new BorderLayout());
        //sequence button
        sequenceButton.setVisible(true);
        sequenceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    //handler.hitSequence();
                	
                	handler.sendCommand (CommandTask.StartTestSequence);
                }
            }
        });
        //payload button     
        payloadButton.setActionCommand("payload");
        payloadButton.setVisible(true);
        payloadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    handler.sendCommand (CommandTask.EnablePayload);
                }
            }
        });
        controls.add(sequenceButton, BorderLayout.LINE_START);
        controls.add(payloadButton, BorderLayout.LINE_END);
        controls.setOpaque(false);
        
        //info log
        infologpanel = new JPanel(new BorderLayout ());
        infolog = new JTextArea (); 
        infolog.setLineWrap(true);
        infolog.setEditable(false);
        infolog.setWrapStyleWord(true);
        infologscrollpane = new JScrollPane(infolog);
        infologscrollpane.setPreferredSize(new Dimension(150, 150));
        infologpanel.add(infologscrollpane);
        
        
        //minimap
        minimap = new JPanel(new BorderLayout ());
        JLabel tmp = new JLabel("minimap");
        minimap.add(tmp);
        
        //add sections to display
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.ipadx = 0; c.ipady = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.1; c.weighty = 0.1;
        	controlPanel.add(status, c);
        c.gridx = 0; c.gridy = 1;
        c.ipadx = 0; c.ipady = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.1; c.weighty = 0.1;
        	controlPanel.add(controls, c);
        c.gridx = 2; c.gridy = 0;
        c.ipadx = 10; c.ipady = 10;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.weightx = 0.5; c.weighty = 0.5;
        	controlPanel.add(minimap, c);
        c.gridx = 0; c.gridy = 10;
        c.gridwidth = 3;
        c.ipadx = 0; c.ipady = 0;
        c.anchor = GridBagConstraints.PAGE_END;
        c.fill = GridBagConstraints.HORIZONTAL;
        	controlPanel.add(infologpanel, c);
        
        controlPanel.setVisible(true);
        
        controlPanel.validate();
        
        /*-------------------------- Tracking Tab ---------------------------*/
        

        
        mperpLabelName=new JLabel("Meters/Pixels: ");
        mperpLabelValue=new JLabel(String.format("%s",map().getMeterPerPixel()));

        zoomLabel=new JLabel("Zoom: ");
        zoomValue=new JLabel(String.format("%s", map().getZoom()));

        treeMap.add(panel, BorderLayout.NORTH); //remove treeMap for this and one below
        treeMap.add(helpPanel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout());
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(panelBottom, BorderLayout.SOUTH);
        JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
                + "left double click or mouse wheel to zoom.");
        helpPanel.add(helpLabel);
        JButton button = new JButton("setDisplayToFitMapMarkers");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                map().setDisplayToFitMapMarkers();
            }
        });
        JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[] { new OsmTileSource.Mapnik(),
                new OsmTileSource.CycleMap(), new BingAerialTileSource(), new MapQuestOsmTileSource(), new MapQuestOpenAerialTileSource() });
        tileSourceSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map().setTileSource((TileSource) e.getItem());
            }
        });
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
        panelTop.add(tileSourceSelector);
        panelTop.add(tileLoaderSelector);
        final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
        showMapMarker.setSelected(map().getMapMarkersVisible());
        showMapMarker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                map().setMapMarkerVisible(showMapMarker.isSelected());
            }
        });
        panelBottom.add(showMapMarker);
        ///
        final JCheckBox showTreeLayers = new JCheckBox("Tree Layers visible");
        showTreeLayers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                treeMap.setTreeVisible(showTreeLayers.isSelected());
            }
        });
        panelBottom.add(showTreeLayers);
        ///
        final JCheckBox showToolTip = new JCheckBox("ToolTip visible");
        showToolTip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                map().setToolTipText(null);
            }
        });
        panelBottom.add(showToolTip);
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
        final JCheckBox scrollWrapEnabled = new JCheckBox("Scrollwrap enabled");
        scrollWrapEnabled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                map().setScrollWrapEnabled(scrollWrapEnabled.isSelected());
            }
        });
        panelBottom.add(scrollWrapEnabled);
        panelBottom.add(button);

        panelTop.add(zoomLabel);
        panelTop.add(zoomValue);
        panelTop.add(mperpLabelName);
        panelTop.add(mperpLabelValue);
        
        //This line was previously necessary as the treeMap (the map portion of the GUI) 
        //took up the entire display. Now, it is part of a tab, so it is added as such instead.
        //add(treeMap, BorderLayout.CENTER);
        

/* **** Adding Markers & Sections to Map -- Remove for our implementation **** */
        /*
        LayerGroup germanyGroup = new LayerGroup("Germany");
        Layer germanyWestLayer = germanyGroup.addLayer("Germany West");
        Layer germanyEastLayer = germanyGroup.addLayer("Germany East");
        MapMarkerDot eberstadt = new MapMarkerDot(germanyEastLayer, "Eberstadt", 49.814284999, 8.642065999);
        MapMarkerDot ebersheim = new MapMarkerDot(germanyWestLayer, "Ebersheim", 49.91, 8.24);
        MapMarkerDot empty = new MapMarkerDot(germanyEastLayer, 49.71, 8.64);
        MapMarkerDot darmstadt = new MapMarkerDot(germanyEastLayer, "Darmstadt", 49.8588, 8.643);
        map().addMapMarker(eberstadt);
        map().addMapMarker(ebersheim);
        map().addMapMarker(empty);
        Layer franceLayer = treeMap.addLayer("France");
        map().addMapMarker(new MapMarkerDot(franceLayer, "La Gallerie", 48.71, -1));
        map().addMapMarker(new MapMarkerDot(43.604, 1.444));
        map().addMapMarker(new MapMarkerCircle(53.343, -6.267, 0.666));
        map().addMapRectangle(new MapRectangleImpl(new Coordinate(53.343, -6.267), new Coordinate(43.604, 1.444)));
        map().addMapMarker(darmstadt);
        treeMap.addLayer(germanyWestLayer);
        treeMap.addLayer(germanyEastLayer);

        MapPolygon bermudas = new MapPolygonImpl(c(49,1), c(45,10), c(40,5));
        map().addMapPolygon( bermudas );
        map().addMapPolygon( new MapPolygonImpl(germanyEastLayer, "Riedstadt", ebersheim, darmstadt, eberstadt, empty));

        map().addMapMarker(new MapMarkerCircle(germanyWestLayer, "North of Suisse", new Coordinate(48, 7), .5));
        Layer spain = treeMap.addLayer("Spain");
        map().addMapMarker(new MapMarkerCircle(spain, "La Garena", new Coordinate(40.4838, -3.39), .002));
        spain.setVisible(false);

        Layer wales = treeMap.addLayer("UK");
        map().addMapRectangle(new MapRectangleImpl(wales, "Wales", c(53.35,-4.57), c(51.64,-2.63)));

        // map.setDisplayPosition(new Coordinate(49.807, 8.6), 11);
        // map.setTileGridVisible(true);
		*/
        
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
                if(showToolTip.isSelected()) map().setToolTipText(map().getPosition(p).toString());
            }
        });
    
        
        
        
        
        
        
        /*-------------------------- Download Tab ---------------------------*/
        
        
        
        
        
        
        
        
        
        /*---------------------------- XBee Tab -----------------------------*/
        
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
		updateSerialPortsList();
		serialPortsList.setSelectedIndex(serialPortsList.getItemCount() - 1);

		//Refresh serial ports button
		serialPortPanel.add(serialPortsList, BorderLayout.CENTER);
		JButton refreshPortsBtn = new JButton("Refresh");
		refreshPortsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSerialPortsList();
			}
		});
		serialPortPanel.add(refreshPortsBtn, BorderLayout.EAST);
		xbeeInitGrid.add(serialPortPanel);

		//Wireless Address Dropdown
		JPanel addressPanel = new JPanel(new BorderLayout());
		addressPanel.add(new JLabel("Remote XBee Address: "), BorderLayout.WEST);
		addressesList = new JComboBox<String>(addresses);
		addressesList.setSelectedIndex(0);
		selectedAddress = addr[addressesList.getSelectedIndex()]; //set default address
		addressesList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedAddress = addr[addressesList.getSelectedIndex()]; //set active address
			}
		});
		addressPanel.add(addressesList, BorderLayout.CENTER);
		xbeeInitGrid.add(addressPanel);
		
		//Baud rate dropdown
		JPanel baudPanel = new JPanel(new BorderLayout());
		baudPanel.add(new JLabel("XBee Baud Rate: "), BorderLayout.WEST);
		baudList = new JComboBox<Integer>(baudRates);
		baudList.setSelectedIndex(4);
		selectedBaud = (int) baudList.getSelectedItem(); //set default address
		addressesList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedBaud = (int) baudList.getSelectedItem(); //set active address
			}
		});
		baudPanel.add(baudList, BorderLayout.CENTER);
		xbeeInitGrid.add(baudPanel);
		

		//Initialize GS XBee Button
		JButton initXBeeButton = new JButton("Initialize GS XBee");
		initXBeeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					initXbee();
					addToReceiveText("Success! Initialized GS XBee :)");
					addToReceiveText("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
							+ System.getProperty("line.separator"));
				} catch (XBeeException e1) {
				e1.printStackTrace();
					numErr++;
					addToReceiveText("Error ("
							+ numErr
							+ "): Could not connect to XBee :( make sure port isn't being used by another program (including this one)!");
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
				sendXBeePacket("(Test Packet)");
			}
		});
		sendPacketsGrid.add(testSendBtn);
		
		//Send custom data box
		JButton customDataBtn = new JButton("Send Data");
		customDataBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendXBeePacket(sendEdit.getText());
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

		tablePanel.add(new JLabel("", JLabel.LEFT));
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
        
        
        
        
        /*------------------ Create Tabbed Pane & Add Tabs ------------------*/   
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Control", null, controlPanel, "GS Control Tab");
        tabbedPane.addTab("Recovery", null, treeMap, "Recovery Tracking Tab");
        tabbedPane.addTab("Download", null, downloadPanel, "Map Downloading Tab");
        tabbedPane.addTab("XBee", null, xbeePanel, "XBee Setup Tab");
        
        /* Activate the Tabbed Pane */
        setContentPane(tabbedPane);
        //getContentPane().addChild(tabbedPane);
        
        setVisible(true);
        
        
        ///////////////////////////////////////
        
        
        
        /*---------------------------- Other --------------------------------*/
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        
    }
    
    /*------------------------------ Aliases --------------------------------*/
    
    private JMapViewer map(){
        return treeMap.getViewer();
    }
    @SuppressWarnings("unused")
	private static Coordinate c (double lat, double lon){
        return new Coordinate(lat, lon);
    }
    
/***** Defining Button Actions *****/
    
    /*
    @Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		switch (e.getActionCommand()) {
		case "sequence":
			Handler.handler.hitSequence();
			break;
		case "payload":
			Handler.handler.hitPayload();
			break;
		default:
			System.err.println("gui.GuiMain#actionPerformed: button command not recognized");
		
		}
	}
	*/
    
    public void initXbee() throws XBeeException {

		// get selected serial port...
		String selSerial = (String) serialPortsList.getSelectedItem();

		if (xbee != null && xbee.isConnected()) {
			xbee.close();
			xbeeListener.stopListening();
		}
		

		System.out.println(selSerial);
		xbee.open(selSerial, selectedBaud); //open port
		xbeeListener = new XBeeListenerThread(handler.receiver(false)); //init a new listener thread
		xbeeListener.start();

		resetPacketCounters();
	}
    
	
	public boolean sendXBeePacket(String msg) {
		OutgoingPacket payload = new OutgoingPacket(OutgoingPacketType.TEST);
		try {
			XBeeSender mailman = new XBeeSender(xbee, selectedAddress, payload);
			mailman.send();
			addToReceiveText("Sent (" + numSent + "): " + msg);
			return true;
		}
		catch (XBeeSenderException e) {
			addToReceiveText("Error (" + numErr + "): " + e.getMessage());
			incNumError();
			return false;
		}
	}
	
	
	//get updated data from XBee and display it
	public void updateXBeeData (String updateLat, String updateLongi, String updateAlt, String updateFlag) {
		lat.setText(""+updateLat);
		longi.setText(""+updateLongi);
		alt.setText(""+updateAlt);
		flag.setText(""+updateFlag);
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
		logMessage(txt);
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
	public void logMessage(String msg) {
		log.info(msg);
	}
    
    private void updateZoomParameters() {
        if (mperpLabelValue!=null)
            mperpLabelValue.setText(String.format("%s",map().getMeterPerPixel()));
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
    
    public void addMapMarkerDot (String name, double lat, double lon) {
    	Coordinate c = new Coordinate (lat, lon);
    	MapMarkerDot m = new MapMarkerDot(name, c);
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
