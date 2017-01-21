# encoding: utf8
import serial
import time
import struct


def do_write(ser, s):
	# http://stackoverflow.com/questions/12214801/print-a-string-as-hex-bytes
	#print("".join("{:02x}".format(c) for c in s))
	print(ascii(s))
	ser.write(s)

def int_to_bytes(i):
	return bytearray(struct.pack(">i", i))

def float_to_bytes(f):
	return bytearray(struct.pack(">f", f))


with serial.Serial('COM8', 19200, writeTimeout=15) as ser:  # open serial port
	print(ser.name)         # check which port was really used
	print('is open? %s' % ser.is_open)

	do_write(ser, int_to_bytes(1258))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(10.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(20.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(30.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(40.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(50.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(60.5))


	do_write(ser, int_to_bytes(-11))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(-1.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(-2.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(-3.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(-4.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(-5.5))
	do_write(ser, b"\x01")
	do_write(ser, float_to_bytes(-6.5))

	print('finished writing')

ser.close()
print('closed')

#<PACKET: (time: 1258, lat: 10.500000, long: 20.500000, alt: 30.500000, x: 40.500000, y: 50.500000, z: 60.500000)>
#<PACKET: (time: 1258, lat: 10.500000, long: 20.500000, alt: 30.500000, x: 40.500000, y: 50.500000, z: 32.000000)>
