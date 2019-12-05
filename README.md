# Arduino-Sleep-Monitor
This is a class project for BME590 taught by Prof. Adam Wax, 2019 Fall, Duke university 


#  how to install 
## hardware - arduino 
- upload `Sleep_Monitor.ino` in `Arduino` folder to your arduino
- to use temperature sensor, you need to find the correct address for it first. Upload `one_wire_address_finder.ino` and checkout the address in serial monitor

## software - android studio app 
- download the github repo
- open project in Android studio, gradle will take some time to build. 
- install the app on an Android tablet
- open app and connect to Arduino via bluetooth, 
- click Signal button for real time signal plot, click Analysis button for the processed signals and sleep time


# Overview

### Sleep Monitor = heart rate + breath rate + body movement 

- The heart rate sensor and the gyroscope sensor will be worn at the wrist. 
- The temperature sensor will be placed under thenose to measure breath rate by monitoring changes of temperature

- Measure data with three sensors and display raw signals on app in real time 
- Process sensor signals and display results on app (24h)
- Analyze subject’s sleep duration and sleep quality
- Display sleep analysis  for the past day/week on app

