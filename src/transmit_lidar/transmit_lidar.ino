#include <Wire.h>
#include <LIDARLite.h>
#include <SCServo.h>

#define BUF_SIZE 10
#define ACK_CMD "ack"
#define READY_CMD "ready"
#define SENTINEL 65365

LIDARLite lidarDevice;
String data;

///\brief sends a packet of lidar data as string of bytes
void sendLidarData() {
  static long count = 0;
//  for (int i = 0; i < BUF_SIZE; i++) {
//    data += String(lidarDevice.distance()) + String(",");
//  }
  Serial3.println(lidarDevice.distance() + String(","));
  // Serial.println(data);
  data = "";
}

void setup()
{
  data.reserve(BUF_SIZE*2);
  Serial3.begin(9600); //set baud rate
  Serial.begin(9600);
  lidarDevice.begin(0, true);
  lidarDevice.configure(0);
}

void loop()
{
  sendLidarData();
  // delay(300);
}
    
