# import the necessary packages
import argparse
import datetime
import imutils
import time
import cv2
import sys
 
# construct the argument parser and parse the arguments
ap = argparse.ArgumentParser()
ap.add_argument("-v", "--video", help="path to the video file")
ap.add_argument("-a", "--min-area", type=int, default=500, help="minimum area size")
args = vars(ap.parse_args())
 
# if the video argument is None, then we are reading from webcam
if args.get("video", None) is None:
	camera = cv2.VideoCapture(0)
	time.sleep(0.25)
 
# otherwise, we are reading from a video file
else:
	camera = cv2.VideoCapture(args["video"])
 
# initialize the first frame in the video stream
firstFrame = None
count = 0
sum = 0
max_rect = 0
n_frames = 0
total_detect = 0

# loop over the frames of the video
while True:
	# grab the current frame and initialize the occupied/unoccupied
	# text
	(grabbed, frame) = camera.read()
	text = "Unoccupied"
 
	# if the frame could not be grabbed, then we have reached the end
	# of the video
	if not grabbed:
		break
 
	# resize the frame, convert it to grayscale, and blur it
	frame = imutils.resize(frame, width=500)
	gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
	gray = cv2.GaussianBlur(gray, (21, 21), 0)
 
	# if the first frame is None, initialize it
	if firstFrame is None:
		firstFrame = gray
		continue
# compute the absolute difference between the current frame and
	# first frame
	frameDelta = cv2.absdiff(firstFrame, gray)
	thresh = cv2.threshold(frameDelta, 2, 255, cv2.THRESH_BINARY)[1]

	# dilate the thresholded image to fill in holes, then find contours
	# on thresholded image
	thresh = cv2.dilate(thresh, None, iterations=2)
	_,cnts,_ = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,
		cv2.CHAIN_APPROX_SIMPLE)

	firstFrame = gray;
	n_frames = n_frames + 1
	# loop over the contours
	for c in cnts:
		# if the contour is too small, ignore it
		if cv2.contourArea(c) < args["min_area"]:
			continue
 
		# compute the bounding box for the contour, draw it on the frame,
		# and update the text
		(x, y, w, h) = cv2.boundingRect(c)
		cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
		text = "Occupied"
		total_detect += 1
		sum += cv2.contourArea(c)
		if max_rect < cv2.contourArea(c):
			max_rect = cv2.contourArea(c)	
# draw the text and timestamp on the frame
	cv2.putText(frame, "Room Status: {}".format(text), (10, 20),
		cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
	cv2.putText(frame, datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p"),
		(10, frame.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)
 	
	if text == "Occupied" : 
		count += 1 

	# show the frame and record if the user presses a key
	cv2.imshow("Security Feed", frame)
	cv2.imshow("Thresh", thresh)
	#cv2.imshow("Frame Delta", frameDelta)
	key = cv2.waitKey(1) & 0xFF
	
	# if the `q` key is pressed, break from the lop
	if key == ord("q"):
		break
 
# cleanup the camera and close any open windows
print "NUMERO DE FRAMES = "
print n_frames
print "NUMERO DE FRAMES DE DETECCIO ="
print count
print "DETECCIONS TOTALS = "
print total_detect
print "SUMA AREES DE DETECCIO ="
print sum 
if count == 0:
	count = 1
if max_rect == 0:
	max_rect = 1
if total_detect == 0:
	total_detect = 1
if n_frames == 0:
	n_frames = 1
if sum == 0:
	sum = 1
print "AREA MAXIMA = "
print max_rect
print "RELACIO DETECCIONS PER FRAME = "
detect_per_frame = total_detect/count
print  detect_per_frame
if detect_per_frame == 1:
	detect_per_frame = 1.5
print "AREA MITJA PER FRAME = "
area_mitja = sum/total_detect
max_rect = area_mitja + (area_mitja/2)
print area_mitja
print "MOVIMENT MAXIM = "
mov_max = max_rect*(n_frames*detect_per_frame)
print mov_max
total = (sum*100)/mov_max 
print "PERCENTATGE DE MOVIMENT = " 
print total

sys.stdout.flush() 
camera.release() 
cv2.destroyAllWindows()
