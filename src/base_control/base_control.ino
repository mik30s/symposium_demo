#include <SCServo.h>
#include <Thread.h>

SCServo SERVO;

void setup()
{
  Serial.begin(9600);
  Serial1.begin(1000000);
  SERVO.pSerial = &Serial1;
  delay(500);
  SERVO.EnableTorque(3, 1);
  SERVO.WritePos(3, 1023, 3000);// Servo ID:2, rotate to the position:0x2FF
}

void loop() {  
    SERVO.WritePos(3, 800, 2000);// Servo ID:2, rotate to the position:0x2FF 
    delay(2000);
    SERVO.WritePos(3, 200, 2000);// Servo ID:2, rotate to the position:0x2FF
    delay(2000);
}



