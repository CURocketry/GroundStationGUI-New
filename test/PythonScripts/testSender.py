# encoding: utf8
import serial
import time
import struct

# copied from Gus's west.gpsim - the first two are latitude and longitude, and the others I'm not so sure
# maybe altitude and x/y/z?
west_campus_test = [
	(42.446243,-76.490225,325.0,0.0,0.0,0.0),
	(42.446777,-76.490300,340.0,5.0,0.0,0.0),
	(42.447331,-76.490311,330.0,10.0,0.0,0.0),
	(42.447798,-76.490322,350.0,15.0,0.0,0.0),
	(42.447640,-76.490097,350.0,20.0,0.0,0.0),
	(42.447038,-76.489796,360.0,25.0,0.0,0.0)]

def do_write(ser, s):
	# http://stackoverflow.com/questions/12214801/print-a-string-as-hex-bytes
	#print("".join("{:02x}".format(c) for c in s))
	print(ascii(s))
	ser.write(s)

def int_to_bytes(i):
	return bytearray(struct.pack(">i", i))

def float_to_bytes(f):
	return bytearray(struct.pack(">f", f))

def sendPacket(packet):
	"""packet is a tuple of the form
	(time, lat, long, alt, x, y, z) where time is an int and the rest are floats"""
	do_write(ser, int_to_bytes(packet[0]))
	for i in packet[1:]:
		do_write(ser, b"\x01")
		do_write(ser, float_to_bytes(i))

with serial.Serial('COM8', 19200, writeTimeout=15) as ser:  # open serial port
	print(ser.name)         # check which port was really used
	print('is open? %s' % ser.is_open)

	# sendPacket((1258, 10.5, 20.5, 30.5, 40.5, 50.5, 60.5))
	# sendPacket((-11, -1.5, -2.5, -3.5, -4.5, -5.5, -6.5))

	counter = 0
	for (lat, lon, alt, x, y, z) in west_campus_test:
		counter += 1
		sendPacket((counter, lat, lon, alt, x, y, z))
		time.sleep(1);

	print('finished writing')

ser.close()
print('closed')