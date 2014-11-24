package jTile.src.org.openstreetmap.gui.jmapviewer;

//License: GPL. Copyright 2008 by Jan Peter Stotz

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * Abstract base class for all mouse controller implementations. For
 * implementing your own controller create a class that derives from this one
 * and implements one or more of the following interfaces:
 * <ul>
 * <li>{@link MouseListener}</li>
 * <li>{@link MouseMotionListener}</li>
 * <li>{@link MouseWheelListener}</li>
 * </ul>
 * 
 * @author Jan Peter Stotz
 */
public abstract class JMapController {

    protected JMapViewer map;

    public JMapController(JMapViewer map) {
        this.map = map;
    }

    public void addListeners() {
        if (this instanceof MouseListener)
            map.addMouseListener((MouseListener) this);
        if (this instanceof MouseWheelListener)
            map.addMouseWheelListener((MouseWheelListener) this);
        if (this instanceof MouseMotionListener)
            map.addMouseMotionListener((MouseMotionListener) this);
    }
}
