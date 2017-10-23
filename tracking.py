import cv2
import imutils
import serial
import numpy as np 
from time import sleep
from math import atan2
import argparse
import math
import struct
from collections import deque

JOINT_A1 = 0
JOINT_A2 = 1

angles = [90,150]
joint_positions = [(0,0),(0,0)]
servo_positions = []

class ServoController:
    START_CMD = 's'
    MOVE_CMD = 'm'

    def __init__(self):
        self.ser = serial.Serial('/dev/ttyACM1')
        self.ser.baudrate = 9600
    
    def update_joint_angles(self,x, y):
        """
        Moves the motor by 1 degree.
        """
        a2 = np.ceil(np.arctan2(np.sqrt(1-((x**2 + y**2 - 6**2 - 10**2)/2*6*10)),(x**2 + y**2 - 6**2 - 10**2)/2*6*10))
        a1 = np.ceil(np.arctan2(y,x) - np.arctan2(10*np.sin(a2), 6+10*np.cos(a2)))
        return a1, a2

    def send(self,cmd, servo_pos_array):
        """
        Sends angles to microcontroller
        """
        state = self.ser.read();
        print("state: ", state)
        a1 = int(servo_pos_array[JOINT_A1])
        a2 = int(servo_pos_array[JOINT_A2])
        print "a1, a2 = ", a1, a2

        if (state == 'w'):
            if (cmd == self.START_CMD
                or cmd == self.MOVE_CMD):
                self.ser.write(struct.pack('b',ord(cmd)))
                self.ser.flush()
            sleep(1)
            self.ser.write(str(a1)+'s')
            self.ser.write(str(a2)+'s')


def adjust_gamma(img, gamma=1.0):
    """
    applies gamma correction
    """
    ig = 1 / gamma
    table = np.array([((i / 255.0) ** ig) * 255 for i in np.arange(0,256)], dtype=np.uint8)
    return cv2.LUT(img, table)


if __name__ == '__main__':
    # create servo controller
    servo_cntl = ServoController()

    # hsv values low and high values for 
    # objects
    green_lower = (0, 162, 255)
    green_upper = (255, 255, 255)

    ap = argparse.ArgumentParser()
    ap.add_argument('-c', '--camera',     required=True, help='camera to use [0,1,2...]')
    ap.add_argument('-g', '--glevel',     required=True, help='gamma correction level')
    ap.add_argument('-q', '--queue_size', required=True, help='size of point queue. ')

    args = vars(ap.parse_args())

    camera = int(args.get('camera'))
    gamma = float(args.get('glevel'))
    queue_sz = int(args.get('queue_size'))

    # store the objects points in list
    point_trail = deque(maxlen=queue_sz)  

    #use default camera
    camera = cv2.VideoCapture(camera)

    # on start set arm to base poition.
    servo_positions = [float(a) * 5.115 for a in angles]
    print("start angles (A1=90,A2=90) (servo): ", servo_positions)
    servo_cntl.send(servo_cntl.START_CMD, servo_positions)   

    #start streaming frames from camera
    while True:
        hasFrame, frame = camera.read()

        if not hasFrame: break

        # resize the frame.
        frame = imutils.resize(frame, width=600)
        frame = adjust_gamma(frame, gamma)

        # convert current frame to HSV values
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

        # mask the object with color green
        # with morphological operations
        mask = cv2.inRange(hsv, green_lower, green_upper)
        mask = cv2.erode(mask, None, iterations=10)
        mask = cv2.dilate(mask, None, iterations=2)

        # find the objects external (outermost) contours.
        _, contours, _ = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        cv2.drawContours(mask, contours, 0, (0,255,0), 3)
        center = None

        if len(contours) > 0:
            #use the largest counter by area
            #and use it to compute the encclosing circle
           
            largest_contour = max(contours, key=cv2.contourArea)
            (x, y), radius = cv2.minEnclosingCircle(largest_contour)
            # find the most intense regions of the circle
            M = cv2.moments(largest_contour)
           
            if M["m00"] == 0: M["m00"] = 1
            center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))

            # draw a circle at the center
            if radius > 10:
                # draw outer circle
                cv2.circle(frame, (int(x), int(y)), int(radius), (0,255,0), 2)
                # draw inner circle
                cv2.circle(frame, center, 5, (0,255,255), -1)

                for i in range(len(point_trail)):
                    if point_trail[i - 1] == None or point_trail[i] == None:
                        continue
                    if len(point_trail) >= 10 and i == 1:
                        # compute the change in both y and x
                        dx = point_trail[-10][0] - point_trail[i][0]
                        dy = point_trail[-10][1] - point_trail[i][1]

                        # if there is significant move ment in x direction
                        cv2.putText(frame, "dx: {}, dy: {}".format(dx, dy),
                                    (10, frame.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX,
                                    0.35, (0, 0, 255), 1)

        cv2.imshow('image', frame)
        cv2.imshow('mask', mask)
        cv2.imshow('hsv', hsv)

        if (cv2.waitKey(1) & 255) == ord('q'):
            break

    camera.release()
    cv2.destroyAllWindows()









