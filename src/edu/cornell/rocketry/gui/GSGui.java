// License: GPL. For details, see Readme.txt file.
package edu.cornell.rocketry.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import edu.cornell.rocketry.gui.Handler;

/**
 * Demonstrates the usage of {@link JMapViewer}
 *
 * @author Jan Peter Stotz
 *
 */
public class GSGui extends JFrame implements JMapViewerEventListener /*, ActionListener*/  {

    private static final long serialVersionUID = 1L;

    private JMapViewerTree treeMap = null;
    
    private JPanel controlPanel = new JPanel ();
    private JPanel downloadPanel = new JPanel ();
    private JPanel xbeePanel = new JPanel ();

    private JLabel zoomLabel=null;
    private JLabel zoomValue=null;

    private JLabel mperpLabelName=null;
    private JLabel mperpLabelValue = null;
    
    private Handler handler;
    
    JPanel panel = new JPanel();
    JPanel panelTop = new JPanel();
    JPanel panelBottom = new JPanel();
    JPanel helpPanel = new JPanel();
    
    JButton sequenceButton = new JButton("Start Sequence");
    JButton payloadButton = new JButton("Enable Payload");

    /**
     * Constructs the {@code Demo}.
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
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel("Welcome");          
        label.setFont(new Font("Helvetica", Font.ROMAN_BASELINE, 13));          
        controlPanel.add(label, BorderLayout.CENTER); 
        
        //Sequence Button
        
        //sequenceButton.addActionListener(this);
        sequenceButton.setActionCommand("sequence");
        controlPanel.add(sequenceButton);
        sequenceButton.setVisible(true);
        
        sequenceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Handler.handler.hitSequence();
                    
                }
            }
        });
        
        //Payload Button
               
        //payloadButton.addActionListener(this);
        payloadButton.setActionCommand("payload");
        controlPanel.add(payloadButton); 
        payloadButton.setVisible(true);
        
        payloadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Handler.handler.hitPayload();
                    
                }
            }
        });
        

        
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
        
        
        
        
        
        
        /*------------------ Create Tabbed Pane & Add Tabs ------------------*/   
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Control", null, controlPanel, "GS Control Tab");
        tabbedPane.addTab("Recovery", null, treeMap, "Recovery Tracking Tab");
        tabbedPane.addTab("Download", null, downloadPanel, "Map Downloading Tab");
        
        /* Activate the Tabbed Pane */
        setContentPane(tabbedPane);
        //getContentPane().addChild(tabbedPane);
        
        ///////////////////////////////////////
        
    }
    
    /*------------------------------ Aliases --------------------------------*/
    
    private JMapViewer map(){
        return treeMap.getViewer();
    }
    private static Coordinate c(double lat, double lon){
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


	
}
