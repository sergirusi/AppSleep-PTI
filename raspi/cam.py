import picamera 
from time import sleep
import os

camera = picamera.PiCamera()

try: 
	os.remove('test2.h264')
	os.remove('test2.mp4')
except: 
	print "no esta"

camera.start_recording('test2.h264')
sleep(5)
camera.stop_recording()

os.system("MP4Box -add test2.h264 test2.mp4")
