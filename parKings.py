import RPi.GPIO as GPIO
import time
import lcddriver
from firebase import firebase


firebase = firebase.FirebaseApplication('https://parkings-104.firebaseio.com/', None)
display = lcddriver.lcd()
GPIO.setmode(GPIO.BOARD)
GPIO.setup(11, GPIO.OUT)
servo = GPIO.PWM(11,50)
servo.start(7.5)

totalSpots = 0
totalEnters = 0
totalExits = 0

parkingLotRef = firebase.get('/parking_lots', 0)
title = parkingLotRef.get('title', None)

spotsRef = parkingLotRef.get("spots", None)
for spot in spotsRef:
    totalSpots = totalSpots+1

try:
    while True:
        print(title)
        display.lcd_display_string(title, 1)
        parkingLotRef = firebase.get('/parking_lots', 0)
        enterRef = parkingLotRef.get("needs_to_lift_enter", None)
        exitRef = parkingLotRef.get("needs_to_lift_exit", None)
        
        if enterRef == True:
            if totalEnters-totalExits < totalSpots:
                totalEnters = totalEnters + 1
                servo.ChangeDutyCycle(12.5)
                time.sleep(3)
                servo.ChangeDutyCycle(7.5)
                firebase.put("parking_lots/0", "needs_to_lift_enter", False)
        
        if exitRef == True:
            totalExits = totalExits + 1
            servo.ChangeDutyCycle(12.5)
            time.sleep(3)
            servo.ChangeDutyCycle(7.5)
            firebase.put("parking_lots/0", "needs_to_lift_exit", False)
        
        availableSpots = totalSpots-totalEnters+totalExits
        if availableSpots != 0:
            display.lcd_clear()
            if availableSpots == 1:
                display.lcd_display_string(str(availableSpots)+" free spot", 2)
            else:
                display.lcd_display_string(str(availableSpots)+" free spots", 2)
        else:
            display.lcd_display_string("No free spots", 2)
        print (totalEnters, " / ", totalExits, " / ", totalSpots)
        
except KeyboardInterrupt:
    print("Cleaning up!")
    display.lcd_clear()
    servo.stop()
    GPIO.cleanup()
except requests.exceptions.ConnectionError:
    servo.stop()
    GPIO.cleanup()
    print("Connection refused")