# Cornell Rocketry Team Communications Team GUI

This is a Java application that runs the GUI that monitors and saves data packets from the rocket and controls any system on the rocket that must be controlled remotely.

The main part of the code is in in the folder [src/edu/cornell/rocketry/](src/edu/cornell/rocketry/). For the moment, the actual README file is actually in [src/edu/cornell/rocketry/main/README.txt](src/edu/cornell/rocketry/main/README.txt).

Upon running, data is stored to a file of the form [log/tem_log_file_XXXXX.csv](log/tem_log_file_XXXXX.csv), where XXXXX is the starting time as a Unix epoch (milliseconds since January 1, 1970.

For testing, it is probably useful to have [com0com](http://com0com.sourceforge.net/) installed, which can redirect COM ports between each other. Also, there are a couple of Python scripts (to be run in Python 3; I don't know if it works in Python 2) in [test/PythonScripts](test/PythonScripts). Note that if you're testing sending and receiving, you may get some blocking in the Java code unless there's something (like these Python scripts) to receive stuff from the other end of the pipeline.

The RXTX library requires some setup. In Eclipse, make sure there's exactly one RXTXcomm.jar that's located at GroundStationGUI-New/lib/mfz-rxtx-2.2-20081207-win-x64, and upon opening up the dropdown, the Native library location is set to the same thing (the folder GroundStationGUI-New/lib/mfz-rxtx-2.2-20081207-win-x64). (While you're in there, also make sure you have JUnit in your path, too.)

When loading tiles in the Settings Tab, select the entire tiles folder. When downloading tiles on the Download tab, click on Slippy Map chooser. In the popup, zoom in with double clicks or mouse scroll, move around with right-click, and select a region with left-click. Enter the zoom level(s) desired in Output Zoom Levels, and then click "Download Tiles." After this, reload all the tiles in the Settings Tab.

## Known Issues
- The file system is quite messy, since a lot of the libraries are imported in their entirety, and spread out across the system, obscuring our actual code.
- There're several sort-of-but-not-really duplicates between files in jTile.src.org.openstreetmap.gui.jmapviewer and files in org.openstreetmap.gui.jmapviewer, but different parts of the code depend on different versions. I don't think that we can easily replace one with the other by just replacing all of one with all of the other due to weird dependency issues, but we should probably collapse the two versions down into a single one to avoid future confusion.

## TODOs
- Finish documentation. Current documentation status is in [documentation_log.md](documentation_log.md).
- Incorporate the other README into this one to have a single, consistent file.
- Check that the black wood background that we're currently using isn't under copyright.
- edu.cornell.rocketry.comm.receive.IncomingPacket has a note that says "FIXME: PARSE AS SIGNED EXCEPT FOR TEMPERATURE." If we're currently misinterpreting the format of the received data, this may be a major bug. Also, do we need to handle malformed data, in case something gets corrupted
along the way (we may get an IndexOutOfBounds or AssertionError otherwise, which may be problematic if the error crashes our whole program, but not as problematic if it only crashes one thread that can be restarted)
- Replace "TEM" with "TRACER"
- Create proper Unit Tests for some of these functions
- Double-check edu.cornell.rocketry.util.LocalLoader
- the GUI->Model/Controller->LoRa pipeline is incomplete (baud rate has no effect right now)
- Trying to reinitialize the "GS XBee" (actually the LoRa) for some reason takes a long time; ideally we should also just have a "close connection" button.

## Questions
- edu.cornell.rocketry.comm.receive.XBeeListenerThread : does it only work if you have a real XBee, or does it work for testing, too?
- edu.cornell.rocketry.util.DataLogger : What's the difference between log and logHeader?