CURocketry Ground Station GUI

Gus Donnelly (gmd68@cornell.edu)

Source code drawn heavily from:
JMapViewer:      http://wiki.openstreetmap.org/wiki/JMapViewer
JTileDownloader: http://wiki.openstreetmap.org/wiki/JTileDownloader


************IMPLEMENTATION NOTES************
IMPORTANT - Do not import jTile.src.* for ANYTHING other than files also in
   a jTile.src.* directory. The files appear the same (e.g. jTile.src.org...JMapViewer
   and org...JMapViewer, but they are not! Importing the wrong file will 
   cause dependency errors :O.
   
Code is structured as follows:

     GSGui (view)      XBeeSender
    	  |              /
    	  |            /
    	  |       RealSender
    	  |        /
    	  |      /  
    	  |    /  _____ TestSender
     	  |  /  /                 \
   	 Controller                     \
   	  |  | \ \                   GPSSpoofer
   	  |  |  \  \                      /
   	  |  |   \   \                  /
   	  |  |    \    \              /
   	  |  |     \     TestReceiver
   	  |  |      \
   	  |  |       \
   	  |  |        RealReceiver ------ XBeeListenerThread
   	  |   \
   	  |   testModel
   	  |
    realModel
    
 - Test* is for running simulations of the rocket. They're so deeply integrated
   because the vast majority of what we'll be doing is testing.
 - Test* is exclusively for testing with only software; no XBee.
 - There are two different models so that we can store real data and test data
   at the same time. The data displayed will be dependent on whether or not
   the user has dictated (through some as of 1/24/15 nonexistent check box)
   that we're currently testing. Both co-exist because it may be useful to have
   a 
 - Some sort of payload simulator may be added between the TestSender and 
   TestReceiver in order to simulate enabling/disabling and tracking the 
   payload, or maybe GPSSpoofer will just be buffed to include this 
   functionality.
    
    
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
   
GPSSpoof spoofs GPS packets defined in ./assets/gps_spoof_west_campus.csv
 - The format of this file is:
     latitude,longitude,altitude,time
   for each GPS location on the path.
   The program interperets this file and assigns types as follows:
     double, double, int, long
 - In the future, the user will be able to select which gps path to spoof.
 - As of 1/24/15, you can see the spoofing in action (that's such a fun word)
   by zooming into Ithaca on the map (which still, as of this time, requires
   an internet connection - local maps coming soon!) and clicking the
   "Start Sequence" button in the control tab. Yay map markers!
   
The comm package (edu.cornell.rocketry.comm):
 - Receiver/Sender: interfaces that define behavior for an additional
   abstraction between the Controller and the XBee / test code. This way, if 
   we ever have to change the XBee code, we can just change the code in the
   Sender/Receiver; seems easier.
 - Communication types (e.g. GPSResponse, CommandResponse) are defined in 
   edu.cornell.rocketry.util.
   Note to self: CommandRequest might be a logical addition?
   
Cleaning Up and Organizing
 - As of 1/24/15, there is still some code left over from the XBee GUI that
   hasn't yet been properly distributed to the corresponding 
   model/view/controller components. Again, coming soon.
 - As of 1/24/15, there are a few extraneous files (e.g. RunnableFactory) that
   were useful in past implementations but that do not currently have a use.
   I (gmd68) may delete them soon.
   
Specifications
 - As of 1/24/15, they're kind of randomly there, randomly not. They're coming,
   and will be full soon.
   
Other
 - Yes, the weird black wood background is temporary.
 - You'll see some info messages in the System console and the GUI console
   in the Control tab. These will be made more concise.
   


************RUNNING THE GUI************
GUI: Main.java in src.edu.cornell.rocketry.gui
 - for functionality including control and recovery tracking, and XBee Setup.
 
JTileDownloader: JTileDownloaderStart.java in 
	jTile.src.org.openstreetmap.fma.jtiledownloader
 - to download local copies of the map for use without internet connection.
 - this functionality will eventually be embedded in the Download tab
   of the main GUI.
 
************CONTROL TAB*************

TODO

************RECOVERY TAB************

Before you use this tab, you have to set the folder where you downloaded the
tiles to (see Download Tab). In particular, go to Settings and then choose 
Load Map Tiles. Select the "tiles" folder (the entire folder).

TODO

************DOWNLOAD TAB************ (Derrick)

This tab downloads tiles from the internet and puts it on your local machine.
Once this is done, the GUI can actually read the files and put it on the
Recovery Tab (and Control Tab), since this should be the only tab that
ever tries to access the internet.

The best way to use this is in Main/Bounding Box (Lat/Lon). The Slippy Map
chooser button brings up a box where you can select the region of interest
(click and drag to choose the area; right click and drag to move around the
world). Select the output zoom level (smaller zoom level == more zoomed out).
Change the source from Mapnik (which doesn't work for some reason -
OpenCycleMap worked for me). Then, click Download Tiles. If all goes well, a
popup will appear, and the images being downloaded will be seen as they are
downloaded.

TODO: If I'm understanding this correctly, most of these options are unuseful
(dummied out, even?). We should remove such vestiges from this tab.

*************XBEE TAB***************

TODO


************TILE CACHING************ (Derrick)

So I don't quite understand what's going on with this. From what I understand,
there is some cache in main memory of some tiles... somewhere in the code.
Its creation involves calls to LocalLoader (in edu/cornell/rocketry/util),
which seems to be somewhere in Controller (edu/cornell/rocketry/gui/controller).
This right now seems to be dumping the contents of our current "tiles"
directory into this cache.

At some point in the code (I don't know where), we call getTile in
TileController. This either gets the requested tile from cache (I think), or
tells the JobDispatcher to schedule a fetch from disk, i.e. a LocalTileJob. And
there's a LocalTileLoader in between, for some reason; I don't actually know
its functionality since it seems to exist purely to create LocalTileJobs. I'm
also the one who implemented both of them, so if something seems off with
LocalTileJob and LocalTileLoader (or how they're called in TileController), it
probably is wrong - please correct it and/or tell me about it. By the way, all
the aforementioned classes are in org/openstreetmap/gui/jmapviewer.

It's worth noting that there's a lot of duplication between the folders
org/openstreetmap/gui/jmapviewer and jTile/src/org/openstreetmap/gui/jmapviewer,
but they're not the same. Don't get confused between the two.

TODO: Right now, if the tiles trying to be fetched are missing, for some reason,
there's a big pile-up of jobs in the jobs queue, causing an OutOfMemoryError.
I don't know why that is, but we should stop that from happening.