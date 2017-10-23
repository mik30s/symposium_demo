#include <SCServo.h>

SCServo servoChain;
#define START_CMD "s"
#define MOVE_CMD "m"
#define WAIT_CMD  "w"

int ar[7] = {1, 2,3,4,5, 6, 7};

String cmd(WAIT_CMD);

long readServoAngle() {
  long angle = Serial.parseInt();
  Serial.println("Read angle: " + String(angle));
  return angle;
}

int initServos() {
  delay(500);
  servoChain.EnableTorque(3, 1); 
  servoChain.EnableTorque(1, 1);
  return 0;
}

void moveArm(int a1, int a2, int duration) {
    servoChain.WritePos(1, a1, duration);// Servo ID:1, rotate to the position:0x2FF
    servoChain.WritePos(3, a2, duration);// Servo ID:1, rotate to the position:0x2FF
    delay(duration);
}

void setup()
{
  Serial.begin(9600);
  Serial1.begin(1000000);
  servoChain.pSerial = &Serial1;
 
  initServos();
  // wait for start command
  cmd = Serial.read();
  while (cmd != START_CMD) {
    Serial.println("command was: " + cmd);
    delay(1000);
    cmd = Serial.read();
    Serial.write(WAIT_CMD);
  }
 
  Serial.println("started with: "+ cmd + '\n');
  long a1 = readServoAngle();
  long a2 = readServoAngle();
  Serial.println("[setup] Moving to base with: a1=" + String(a1) + " a2=" + String(a2));
  
  // set arm to base pose
  moveArm(a1,a2,3000);
  
  // set command back to wait state
  cmd = WAIT_CMD;
}


void loop()
{
  cmd = Serial.read();
  while (!cmd.equals(MOVE_CMD)) {
    delay(1000);
    cmd = Serial.read();
    Serial.write(WAIT_CMD);
  }
  
  long a1 = readServoAngle();
  long a2 = readServoAngle();
  Serial.println("[loop] Moving to base with: a1=" + String(a1) + " a2=" + String(a2));
  // set arm to base pose
  moveArm(a1,a2,3000);
}







