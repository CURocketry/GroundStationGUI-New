package org.openstreetmap.gui.jmapviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openstreetmap.gui.jmapviewer.interfaces.TileJob;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;

public class LocalTileJob implements TileJob {
	InputStream input = null;

	Tile tile;
	File tileCacheDir;
	File tileFile = null;
	long fileAge = 0;
	boolean fileTilePainted = false;
    protected TileLoaderListener listener;

	public LocalTileJob(Tile tile, TileLoaderListener listener) {
		this.tile = tile;
		this.listener = listener;
	}
	

	@Override
	public Tile getTile() {
		return tile;
	}
	
    protected File getTileFile() {
        return new File(tileCacheDir + "/" + tile.getZoom() + "_" + tile.getXtile() + "_" + tile.getYtile() + "."
                + tile.getSource().getTileType());
    }

	@Override
	public void run() {
		synchronized (tile) {
			if ((tile.isLoaded() && !tile.hasError()) || tile.isLoading())
				return;
			tile.loaded = false;
			tile.error = false;
			tile.loading = true;
		}
		
		boolean success = loadTileFromFile();
		if (!success) {
			//System.err.println("Can't find the tile " + tile + "!");
			tile.setError("Local file not found for this tile!");
		}
	}
	protected boolean loadTileFromFile() {
        try {
            tileFile = getTileFile();
            if (!tileFile.exists())
                return false;

            try (FileInputStream fin = new FileInputStream(tileFile)) {
                if (fin.available() == 0)
                    throw new IOException("File empty");
                tile.loadImage(fin);
            }

            tile.setLoaded(true);
            listener.tileLoadingFinished(tile, true);// TODO
            //System.out.println("I should be alerting a listener here.");
            fileTilePainted = true;
            return true;
                
        } catch (Exception e) {
            tileFile.delete();
            tileFile = null;
            fileAge = 0;
        }
        return false;
    }
}