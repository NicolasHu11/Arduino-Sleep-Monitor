


#include <SoftwareSerial.h>

SoftwareSerial BTSerial(10, 11); // RX | TX
char serialByte = '0';



#define  LED_PIN  12

void setup()
{
  pinMode(LED_PIN, OUTPUT);
  pinMode(9, OUTPUT);  // this pin will pull the HC-05 pin 34 (key pin) HIGH to switch module to AT mode
  digitalWrite(9, HIGH);
  Serial.begin(9600);
  
  Serial.println("Enter AT commands:");
  BTSerial.begin(38400);  // HC-05 default speed in AT command more
}

void loop()
{

  // Keep reading from HC-05 and send to Arduino Serial Monitor
  if (BTSerial.available()){
    serialByte = BTSerial.read();
    Serial.print(serialByte);
  }
      
    
  // Keep reading from Arduino Serial Monitor and send to HC-05
  if (Serial.available())
    
     BTSerial.write(Serial.read());
}
