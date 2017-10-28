#include <Wire.h>
#include <LIDARLite.h>

#define BUF_SIZE 256
#define ACK_CMD "ack"
#define READY_CMD "ready"
#define SENTINEL 65365

LIDARLite lidarDevice;
String data;

///\brief sends a packet of lidar data as string of bytes
void sendLidarData() {
  static long count = 0;
  // get 250 distance values
  for (uint8_t i = 0; i < BUF_SIZE; i++) {
    data += String(lidarDevice.distance()) + String(",");
  }
  Serial3.println(data.c_str());
  Serial.println(data.c_str());
}

void setup()
{
  data.reserve(BUF_SIZE);
  Serial3.begin(57600); //set baud rate
  Serial.begin(9600);
  lidarDevice.begin(0, true);
  lidarDevice.configure(0);
}

void loop()
{
  sendLidarData();
  delay(100);
}
    
