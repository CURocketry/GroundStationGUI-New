package org.openstreetmap.gui.jmapviewer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.openstreetmap.gui.jmapviewer.interfaces.TileJob;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;

public class LocalTileLoader implements TileLoader {
    protected TileLoaderListener listener;
    protected String cacheDirBase;

    public LocalTileLoader(TileLoaderListener map, File cacheDir) throws IOException  {
        if (cacheDir == null || (!cacheDir.exists() && !cacheDir.mkdirs()))
            throw new IOException("Cannot access cache directory");
        
        this.listener = map;
        cacheDirBase = cacheDir.getAbsolutePath();
        //sourceCacheDirMap = new HashMap<>();
    }
    
	@Override
	public TileJob createTileLoaderJob(Tile tile) {
		// TODO Auto-generated method stub
		return new LocalTileJob(tile, listener);
	}

}
