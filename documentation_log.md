# A list of files that have already been documented vs those that still need to be documented.

## Already Documented

* edu.cornell.rocketry.comm.receive
    * Receiver (interface)
    * RealReceiver
    * TestReceiver
    * IncomingPacket
    * TEMResponse
    * TEMStatusFlag
    * XBeeListenerThread
* edu.cornell.rocketry.comm.send
    * Sender (interface)    
    * RealSender
    * TestSender
    * CommandType (enum)
    * CommandFlag
    * Command
    * CommandRecept
    * OutgoingPacket (interface)
    * OutgoingCommandPacket
    * OutgoingStringPacket
    * XBeeSender
    * XBeeSenderException (exception)
* edu.cornell.rocketry.util
    * Status (enum)
    * LoggerLevel (enum)
    * Pair 
    * LocalLoader: **We'll have to double-check the correctness of this, although it's all commented**
    * ImageFactory
    * ErrorLogger (static class)
    * DataLogger



## Not Yet Documented/Documentation Status Unknown

* edu.cornell.rocketry.util
    * DataLogger
* edu.cornell.rocketry.*
    * all other files

## Files to Be Removed

* edu.cornell.rocketry.comm.receive
    * IncomingPacket_OLD
* edu.cornell.rocketry.util
    * RunnableFactory: aside from the constructor, this entire class is commented out. Check if it's being used before deleting.