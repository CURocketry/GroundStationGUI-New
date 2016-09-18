// License: GPL. For details, see Readme.txt file.
package org.openstreetmap.gui.jmapviewer;

import java.io.File;
import java.io.IOException;

import org.openstreetmap.gui.jmapviewer.JobDispatcher.JobThread;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;


public class TileController {
    protected TileLoader tileLoader;
    protected TileCache tileCache;
    protected TileSource tileSource;

    JobDispatcher jobDispatcher;

    public TileController(TileSource source, TileCache tileCache, TileLoaderListener listener) {
        tileSource = new OsmTileSource.CycleMap();
        //TODO: don't hardcode these things
        
        try {
            File tileDir = new File("tiles"); //TODO: don't hardcode
			//tileLoader = (TileLoader) new OsmFileCacheTileLoader(listener, tileDir); //new OsmTileLoader(listener);
			tileLoader = (TileLoader) new LocalTileLoader(listener, tileDir); //new OsmTileLoader(listener);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        this.tileCache = tileCache;
        jobDispatcher = JobDispatcher.getInstance();
    }

    /**
     * retrieves a tile from the cache. If the tile is not present in the cache
     * a load job is added to the working queue of {@link JobThread}.
     *
     * @param tilex the X position of the tile
     * @param tiley the Y position of the tile
     * @param zoom the zoom level of the tile
     * @return specified tile from the cache or <code>null</code> if the tile
     *         was not found in the cache.
     */
    public Tile getTile(int tilex, int tiley, int zoom) {
        int max = (1 << zoom);
        if (tilex < 0 || tilex >= max || tiley < 0 || tiley >= max)
            return null;
        Tile tile = tileCache.getTile(tileSource, tilex, tiley, zoom);
        if (tile == null) {
            tile = new Tile(tileSource, tilex, tiley, zoom);
            //tileCache.addTile(tile);
            tile.loadPlaceholderFromCache(tileCache);
        }
        if (tile.error) {
            tile.loadPlaceholderFromCache(tileCache);
        }
        if (!tile.isLoaded()) {
        	tile.loadPlaceholderFromCache(tileCache);
        	//OH WELL. DON'T TRY TO DOWNLOAD!
            //jobDispatcher.addJob(tileLoader.createTileLoaderJob(tile));
        	
        	//LocalTileJob newJob = new LocalTileJob(tile);
        	//System.out.println(newJob);
        	jobDispatcher.addJob(tileLoader.createTileLoaderJob(tile));
        	
            //System.out.println("we just found a thing not from a cache");
        	//tile.setImage(Tile.ERROR_IMAGE);
        	//tile.error = true;
        }
        return tile;
    }

    public TileCache getTileCache() {
        return tileCache;
    }

    public void setTileCache(TileCache tileCache) {
        this.tileCache = tileCache;
    }

    public TileLoader getTileLoader() {
        return tileLoader;
    }

    public void setTileLoader(TileLoader tileLoader) {
        this.tileLoader = tileLoader;
    }

    public TileSource getTileLayerSource() {
        return tileSource;
    }

    public TileSource getTileSource() {
        return tileSource;
    }

    public void setTileSource(TileSource tileSource) {
        this.tileSource = tileSource;
    }

    /**
     *
     */
    public void cancelOutstandingJobs() {
        jobDispatcher.cancelOutstandingJobs();
    }
}
