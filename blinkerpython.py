#this is the basic format we will probably use for the feeder motor

#this is the python versio
#import libraries
import RPi.GPIO as GPIO
import time

#GPIO Basic initialization
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

#Use a variable for the Pin to use.
#this depends on what voltage we want, and how I wire it.

led = 17
#Initialize your pin
GPIO.setup(led,GPIO.OUT)

#Turn on the LED by setting it to 1
print "LED on"
GPIO.output(led,1)

#Wait 5s
time.sleep(5)

#Turn off the LED by setting it to 0
print "LED off"
GPIO.output(led,0)