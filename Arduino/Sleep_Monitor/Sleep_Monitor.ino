// BT
#include <SoftwareSerial.h>
SoftwareSerial BTSerial(10, 11); // RX | TX

// heart rate 
#include <DFRobot_Heartrate.h>
#include "DFRobot_Heartrate.h"
#define heartratePin A1
DFRobot_Heartrate heartrate(ANALOG_MODE); ///< ANALOG_MODE or DIGITAL_MODE

// gyroscope 
#include<Wire.h>
const int MPU=0x68; 
int16_t AcX,AcY,AcZ,Tmp,GyX,GyY,GyZ;

// Temperature 
#include <OneWire.h>
#include <DallasTemperature.h>
#define ONE_WIRE_BUS 3 // Data wire is plugged into pin 3 on the Arduino
OneWire oneWire(ONE_WIRE_BUS); // Setup a oneWire instance to communicate with any OneWire devices
DallasTemperature sensors(&oneWire); // Pass our oneWire reference to Dallas Temperature.
int tempResolution = 10;
DeviceAddress insideThermometer = { 0x28, 0xFF, 0x4E, 0xC8, 0x7A, 0x18, 0x01, 0x67 }; // this needs to be found by another sketch.

// ==============================
String serial_string = "";
int delay_count = 0;


// ======================
void setup() {
  // Serial Monitor 
  Serial.begin(115200);
  Serial.println("debug: setup begins");
  // HC-05 default speed in AT command more
  BTSerial.begin(9600);  
  
  // Gyroscope 
  Wire.begin();
  Wire.beginTransmission(MPU);
  Wire.write(0x6B); 
  Wire.write(0);    
  Wire.endTransmission(true); 

  // temp 
  sensors.begin();  // Start up the library 
  sensors.setResolution(insideThermometer, tempResolution);  // set the resolution
}

void loop() {

  HeartRateSensor();
  delay(20);
  delay_count += 1; 

  if (delay_count == 5) {
    //100ms  
    TempSensor();
    }
  else if (delay_count == 10) {
    // 200ms 
    TempSensor();
    GyroscopeSensor();
    Serial.println(serial_string);
    writeString(serial_string);
    serial_string = ""; 
    delay_count = 0; // reset the count 
    }
  
  // Arduino to BT, input on the serial monitor 
  if (Serial.available()>0)
    BTSerial.write(Serial.read());
}


void HeartRateSensor() {
  // heart rate sensor 
  // digital mode 
//  uint8_t rateValue;
//  heartrate.getValue(heartratePin); ///< A1 foot sampled values
//  rateValue = heartrate.getRate(); ///< Get heart rate value 
  // analog mode 
  int rateValue = analogRead(heartratePin); 
  if(rateValue)  {
    Serial.println(rateValue);
    serial_string += "HR" + String(rateValue) + "\n\r";
  }
  
}

void GyroscopeSensor() {
Wire.beginTransmission(MPU);
  Wire.write(0x3B);  
  Wire.endTransmission(false);
  Wire.requestFrom(MPU,12,true);  
  AcX=Wire.read()<<8|Wire.read();    
  AcY=Wire.read()<<8|Wire.read();  
  AcZ=Wire.read()<<8|Wire.read();  
  GyX=Wire.read()<<8|Wire.read();  
  GyY=Wire.read()<<8|Wire.read();  
  GyZ=Wire.read()<<8|Wire.read();  

  Serial.print("Accelerometer: ");
  Serial.print("X = "); Serial.print(AcX);
  Serial.print(" | Y = "); Serial.print(AcY);
  Serial.print(" | Z = "); Serial.println(AcZ); 
  serial_string += "AC" + String(AcX) + "," + String(AcY) + "," + String(AcZ) + "\n\r";
 
  Serial.print("Gyroscope: ");
  Serial.print("X = "); Serial.print(GyX);
  Serial.print(" | Y = "); Serial.print(GyY);
  Serial.print(" | Z = "); Serial.println(GyZ);
  Serial.println(" ");
  serial_string += "GY" + String(GyX) + "," + String(GyY) + "," + String(GyZ) + "\n\r";
  // delay(200);

  
}

void TempSensor(){ 
  // print temperature
  //delay(2000);
  Serial.print("Getting temperatures...\n\r");
  sensors.requestTemperatures();
  Serial.print("Inside temperature is: ");
  printTemperature(insideThermometer);
  Serial.print("\n\r"); 
}


void printTemperature(DeviceAddress deviceAddress){ 
  // print temperature 
  float tempC = sensors.getTempC(deviceAddress);
  if (tempC == -127.00) { 
    Serial.print("Error getting temperature"); 
    } 
  else { 
    Serial.print("C: "); 
    Serial.print(tempC); 
    serial_string += "TEMP" + String(tempC) + "\n\r";
    Serial.print(" F: ");
    Serial.print(DallasTemperature::toFahrenheit(tempC));
    }
}

void writeString(String stringData) { 
  // Used to serially push out a String with Serial.write()
  for (int i = 0; i < stringData.length(); i++)
  {
    BTSerial.write(stringData[i]);   // Push each char 1 by 1 on each loop pass
  }

}// end writeString
