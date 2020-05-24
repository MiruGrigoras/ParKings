import RPi.GPIO as GPIO
import time
import lcddriver
from firebase import firebase


firebase = firebase.FirebaseApplication('https://parkings-104.firebaseio.com/', None)			//get firebase reference
display = lcddriver.lcd()						//initialise LCD
GPIO.setmode(GPIO.BOARD)			//set to use pin numbers for the GPIO control module (GPIO = general purpose input/output)
GPIO.setup(11, GPIO.OUT)				//set pin 11 as output
servo = GPIO.PWM(11,50)					//initialise pin 11 to use for servo and fer 50 HZ as frequency for servo
servo.start(7.5)									//start servo on 90 degrees position

totalSpots = 0					
totalEnters = 0
totalExits = 0

parkingLotRef = firebase.get('/parking_lots', 0)				//get reference to the Iulius Mall parking lot
title = parkingLotRef.get('title', None)

spotsRef = parkingLotRef.get("spots", None)
for spot in spotsRef:														//count existing parking spots
    totalSpots = totalSpots+1

try:
    while True:																	//infinite loop
        print(title)
        display.lcd_display_string(title, 1)														//display parking lot title
        parkingLotRef = firebase.get('/parking_lots', 0)									//get reference to the Iulius Mall parking lot							
        enterRef = parkingLotRef.get("needs_to_lift_enter", None)				//reference to
        exitRef = parkingLotRef.get("needs_to_lift_exit", None)
        
        if enterRef == True:															//check if there is a request to lift barrier for enter
            if totalEnters-totalExits < totalSpots:							//will not lift barrier if there are no more spots available
                totalEnters = totalEnters + 1									
                servo.ChangeDutyCycle(12.5)								//make servo move 90 degrees anticlockwise
                time.sleep(3)
                servo.ChangeDutyCycle(7.5)									//make servo move back 90 degrees clockwise
                firebase.put("parking_lots/0", "needs_to_lift_enter", False)				//set flag to false
        
        if exitRef == True:															//check if there is a request to lift barrier for exit
            totalExits = totalExits + 1
			servo.ChangeDutyCycle(12.5)
            time.sleep(3)
            servo.ChangeDutyCycle(7.5)
            firebase.put("parking_lots/0", "needs_to_lift_exit", False)
        
        availableSpots = totalSpots - totalEnters + totalExits				//calculate available spots based on number of enters/exits
        if availableSpots != 0:
            display.lcd_clear()																//delete previous display content
            if availableSpots == 1:
                display.lcd_display_string(str(availableSpots)+" free spot", 2)				//display number of available spots
            else:
                display.lcd_display_string(str(availableSpots)+" free spots", 2)
        else:
            display.lcd_display_string("No free spots", 2)
        print (totalEnters, " / ", totalExits, " / ", totalSpots)
        
except KeyboardInterrupt:
    print("Cleaning up!")									//in case of CTRL+C, shut everything down
    display.lcd_clear()
    servo.stop()
    GPIO.cleanup()
except requests.exceptions.ConnectionError:
    servo.stop()													//in case of network connection loss, shut everything down
    GPIO.cleanup()
    print("Connection refused")