# Cornell Rocketry Team Communications Team GUI

This is a Java application that runs the GUI that monitors and saves data packets from the rocket and controls any system on the rocket that must be controlled remotely.

The main part of the code is in in the folder [src/edu/cornell/rocketry/](src/edu/cornell/rocketry/). For the moment, the actual README file is actually in [src/edu/cornell/rocketry/main/README.txt](src/edu/cornell/rocketry/main/README.txt).

## Known Issues
- The file system is quite messy, since a lot of the libraries are imported in their entirety, and spread out across the system, obscuring our actual code.
- There're several sort-of-but-not-really duplicates between files in jTile.src.org.openstreetmap.gui.jmapviewer and files in org.openstreetmap.gui.jmapviewer, but different parts of the code depend on different versions. I don't think that we can easily replace one with the other by just replacing all of one with all of the other due to weird dependency issues, but we should probably collapse the two versions down into a single one to avoid future confusion.

## TODOs
- Incorporate the other README into this one to have a single, consistent file.
- Check that the black wood background that we're currently using isn't under copyright.