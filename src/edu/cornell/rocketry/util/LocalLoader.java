package edu.cornell.rocketry.util;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;


/**
 * A class with functions for creating a MemoryTileCache with 
 *   all of the tiles found on the local machine. 
 * By creating a TileCache with all of the local files, we will be 
 *   able to avoid having to access a server to load maps.
 *   
 * @author Gus
 *
 */
public class LocalLoader {
	
	/** 
	 * Creates a cache for use in JMapViewer that contains all tiles stored
	 *   locally. 
	 * The root directory where tiles are to be found is hard-coded as 
	 *   "tiles", as this is the default location that JTileDownloader stores them.
	 * @param src the TileSource that all created Tiles will be linked to.
	 * @return MemoryTileCache full of all tiles found on local machine.
	 */
	public static MemoryTileCache buildLocalCache (TileSource src) {
		
		//the default for jTileDownloader is to stick stuff in a new folder called "tiles"
		//in the working directory, so this works on my machine.
		String pathToTiles = "tiles";
		
		File tileDirectory = new File (pathToTiles);
		
		//for constant add/remove complexity
		LinkedList<Tile> tiles = new LinkedList<Tile>();
		
		//currently, adds all files found in the directory
		buildTileList(tiles, tileDirectory, src);
		
		//Create a MemoryTileCache and add the Tiles that we get from the system.
		
		MemoryTileCache returnObject = new MemoryTileCache();
		for(Tile tile: tiles) {
			returnObject.addTile(tile);
		}
		return returnObject;
	}
	
	/**
	 * Builds a list of all of the tiles that have an image in the
	 *   given file directory.
	 * The directory structure should be as follows:
	 *   tiles/zoom/num1/num2.png
	 *   to each file.
	 * @param acc the LinkedList that all Tiles will be added to.
	 * @param dir the root directory for all tile files.
	 * @param src the TileSource that all tiles will be linked to
	 *   (TileSource required for Tile constructor)
	 */
	public static void buildTileList (LinkedList<Tile> acc, File dir, TileSource src) {
		File[] contents = dir.listFiles();
		if (contents == null) {
			//if this is actually a file, then create a tile, add to acc
			Tile t = tileFromFile(dir, src);
			if (t != null)
				acc.add(t);			
		} else {
			//this is a directory, so build on each of sub-directories/files
			for (int i = 0; i < contents.length; i++) {
				buildTileList (acc, contents[i], src);
			}
		}
	}
	
	/**
	 * Creates a Tile object from the given file, and assigns 
	 *   the provided TileSource as the source for the particular
	 *   file.
	 * Requires that the file be in the original place from the
	 *   JTileDownloader download, such that the filepath is in the 
	 *   following format: tiles/zoom/num1/num2.png 
	 * @param f the filename where the tile 
	 * @param src the TileSource that will be assigned to the returned Tile
	 * @return Tile created as above
	 */
	public static Tile tileFromFile (File f, TileSource src) {
		try {
			//System.out.println ("Creating tile from file: " + f.toString());
			//dir.toString() will print out tiles/12  /345 /234 .png, for example.
			//parsed below as:              tiles/zoom/num1/num2.png
			String sep = File.separator;
			if(sep.equals("\\")) {
				//regex needs backslashes to be escaped
				sep = "\\\\";
			}
			String[] addressArray = f.toString().split(sep);
			
			int index = -1;
			for (int i = 0; i < addressArray.length; i++) {
				if (addressArray[i].equals("tiles")) index = i;
			}
			if (index == -1) throw new IllegalArgumentException ("Tile root directory must be named 'tiles'");
			
			int zoom = Integer.parseInt(addressArray[index+1]);
			int num1 = Integer.parseInt(addressArray[index+2]);
			int num2 = Integer.parseInt(addressArray[index+3].split("\\.")[0]);
			
			//placeholder, and in case cannot find file
			BufferedImage image = Tile.LOADING_IMAGE; 
			
			//get the tile image from the file
			try {
				image = ImageIO.read(f);
			} catch (IOException ioe) {
				System.err.println("COULD NOT FIND FILE: " + f.toString());
				//ioe.printStackTrace();
			} catch (IllegalArgumentException iae) {
				System.err.println("COULD NOT FIND FILE2: " + f.toString());
				iae.printStackTrace();
			}
			
			//finally, create the file with the appropriate image.
			Tile t = new Tile(src, num1, num2, zoom, image);
			return t;
		} catch (Exception e) {
			System.out.println("LocalLoader#tileFromFile(..): Error creating tile from file:");
			System.out.println(f.toString());
			//e.printStackTrace();
		}
		return null;
	}
	
	
	/* 
	 * For Testing Only
	 * 
	 * Note: the Tile constructor will complain because we're
	 * passing in null as the TileSource. In order to test
	 * the functionality of these methods, comment out the
	 * following line in the constructor in Tile.java:
	 * this.key = getTileKey(source, xtile, ytile, zoom);
	 */
	public static void main (String[] args) {
		buildLocalCache(null);
	}
	
	
}
