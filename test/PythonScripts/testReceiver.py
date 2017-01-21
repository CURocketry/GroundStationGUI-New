# encoding: utf8

import serial

with serial.Serial('COM8', 19200, timeout=5) as ser:
	print('opened new thing')
	s = ser.read(100)        # read up to ten bytes (timeout)
	print(s)