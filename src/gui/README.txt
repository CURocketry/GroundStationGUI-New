CURocketry Ground Station GUI

Gus Donnelly (gmd68@cornell.edu)
Jerome Francis

Source code drawn heavily from:
JMapViewer:      http://wiki.openstreetmap.org/wiki/JMapViewer
JTileDownloader: http://wiki.openstreetmap.org/wiki/JTileDownloader


************IMPLEMENTATION NOTES************
IMPORTANT - Do not import jTile.src.* for ANYTHING other than files also in
   a jTile.src.* directory. The files appear the same (e.g. jTile.src.org...JMapViewer
   and org...JMapViewer, but they are not! Importing the wrong file will 
   cause dependency errors :O.

Idea for implementing the equivalent of a LocalTileSource:
 - The constructor for a JMapViewer in src.org.openstreetmap.gui.jmapviewer
   takes in a MemoryTileCache object, which stores local copies of tiles so
   as to not constantly query the server. Presently, this is a fresh cache,
   with no data already stored in it. 
   If we can build a cache up by adding all of tiles that we'll need from the
   local machine, we should be able to run JMapViewer without modification
   and it will draw from everything we have locally.
   We could either (a) let the program try to query the server for tiles 
   that we don't locally have, or (b) disable it from doing so by setting the
   max number of query threads to 0, also in the constructor for JMapViewer.


************RUNNING THE GUI************
GUI: GuiMain.java in src/gui
 - for functionality including control and recovery tracking.
 
JTileDownloader: JTileDownloaderStart.java in 
	jTile.src.org.openstreetmap.fma.jtiledownloader
 - to download local copies of the map for use without internet connection.
 - this functionality will eventually be embedded in the Download tab
   of the main GUI.
 
************CONTROL TAB************

TODO

************TRACKING TAB************

TODO

************DOWNLOAD TAB************

TODO
