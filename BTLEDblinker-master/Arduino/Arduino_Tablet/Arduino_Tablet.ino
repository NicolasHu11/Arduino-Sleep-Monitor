
#include <SoftwareSerial.h>

SoftwareSerial BTSerial(10, 11); // RX | TX
char serialByte = '0';
char serialByteold = '0';

int LED_sign_PIN=3;
int LED_state_PIN = 4;
int LED_result_PIN = 2; 
int BUTTON_PLUS_PIN = 8;
int BUTTON_A_PIN = 6;
int BUTTON_B_PIN = 7;
int BUTTON_EQUAL_PIN=9; 

int A_buttonPushCounter = 0;   // counter for the number of button presses
int A_buttonState = 0;         // current state of the button
int A_lastButtonState = 0;     // previous state of the button

int B_buttonPushCounter = 0;   // counter for the number of button presses
int B_buttonState = 0;         // current state of the button
int B_lastButtonState = 0;     // previous state of the button

int state_buttonState=0;
int state_lastButtonState=0;
int state_buttonPushCounter=0;

int result_buttonstate=0;


void setup() {
  pinMode(BUTTON_PLUS_PIN, INPUT);
  pinMode(BUTTON_A_PIN, INPUT);
  pinMode(BUTTON_B_PIN, INPUT);
  pinMode(BUTTON_EQUAL_PIN, INPUT);
  pinMode(LED_state_PIN, OUTPUT); 
  pinMode(LED_result_PIN, OUTPUT); 
  pinMode(LED_sign_PIN,OUTPUT);
  Serial.begin(9600);
  Serial.println("Press buttons now");
  BTSerial.begin(9600);  // HC-05 default speed in AT command more
}

void loop() {
  // BT to Arduino, write function on Android
  if (BTSerial.available()){
    serialByte = BTSerial.read();
    
    Serial.print(serialByte); 
    Serial.println(serialByteold);
    if (serialByte != serialByteold)
    
    Serial.print("Display Result ");
    // display_result(serialByte - '0') // convert char to int
    
    // use LED for simplicity for now 
      if( serialByte = '1'){
        digitalWrite(LED_result_PIN, HIGH);
        delay(1000);
      }
      if( serialByte = '0'){ 
        digitalWrite(LED_result_PIN, LOW);
        delay (1000);
        //serialByteold='0';
        }
  }
      //;
      serialByteold = serialByte;
    
  // Arduino to BT, need to enter manually
  if (Serial.available()>0)
    BTSerial.write(Serial.read());


  // input number A
  A_buttonState = digitalRead(BUTTON_A_PIN); //read the state of the button
  B_buttonState = digitalRead(BUTTON_B_PIN);
  state_buttonState=digitalRead(BUTTON_PLUS_PIN);
  if (A_buttonState != A_lastButtonState) {
    // if the state has changed, increment the counter
    if (A_buttonState == HIGH) {
      // if the current state is HIGH then the button went from off to on:
      A_buttonPushCounter++;
      digitalWrite(LED_result_PIN, HIGH);   
      delay(200);                       
      digitalWrite(LED_result_PIN, LOW);   
      delay(200);     
      Serial.println("on");
      Serial.print("number of button A pushes: ");
      Serial.println(A_buttonPushCounter);
    } else {
      // if the current state is LOW then the button went from on to off:
      Serial.println("off");
    }
    // Delay a little bit to avoid bouncing
    delay(100);
  }
  // save the current state as the last state, for next time through the loop
  A_lastButtonState = A_buttonState;

  // input number B
  if (B_buttonState != B_lastButtonState) {
    // if the state has changed, increment the counter
    if (B_buttonState == HIGH) {
      // if the current state is HIGH then the button went from off to on:
      B_buttonPushCounter++;
      digitalWrite(LED_result_PIN, HIGH);   
      delay(200);                       
      digitalWrite(LED_result_PIN, LOW);   
      delay(200); 
      Serial.println("on");
      Serial.print("number of button B pushes: ");
      Serial.println(B_buttonPushCounter);
    } else {
      // if the current state is LOW then the button went from on to off:
      Serial.println("off");
    }
    // Delay a little bit to avoid bouncing
    delay(100);
  }
  // save the current state as the last state, for next time through the loop
  B_lastButtonState = B_buttonState;


  if (state_buttonState != state_lastButtonState) {
    // if the state has changed, increment the counter
    if (state_buttonState == HIGH) {
      // if the current state is HIGH then the button went from off to on:
      state_buttonPushCounter++;
    }
    delay(100);
    state_lastButtonState=state_buttonState;
  }

  if(state_buttonPushCounter%2==1)
  {
    digitalWrite(LED_state_PIN,HIGH);
  }
  else{
    digitalWrite(LED_state_PIN,LOW);
  }



  // press button EQUAL to ouput the green light, the # of blink as the result 
  result_buttonstate = digitalRead(BUTTON_EQUAL_PIN);
  int COUNT=0;
  char operator_flag = ' ';
  if (result_buttonstate == 1) { // press result button and LED starts to blink
    // using BT, we don't calculate the result, but write input data to BT/Tablet 
    delay(100);
    Serial.println("calculating result");
    if(state_buttonPushCounter%2==1)
    {
//      COUNT=A_buttonPushCounter-B_buttonPushCounter;
      Serial.println("minus is active");
      operator_flag = '-';
    }

    else
    {
//      COUNT=A_buttonPushCounter+B_buttonPushCounter;
      Serial.println("plus is active");
      operator_flag = '+';
    }
    // No calculation for the result or     
    // write to BT
    String write_string = String(A_buttonPushCounter) + String(operator_flag) + String(B_buttonPushCounter);
    Serial.print("string to write: ");
    Serial.println(write_string);
    writeString(write_string);
//    BTSerial.write(A_buttonPushCounter);
//    BTSerial.write(operator_flag);
//    BTSerial.write(" 1");

    // reset values 
    A_buttonPushCounter=0;
    B_buttonPushCounter=0;
    state_buttonPushCounter = 0;
    digitalWrite(LED_sign_PIN,LOW);
    
    delay(100);    
    }
    
}


void display_result(int COUNT){
    // read COUNT from BT, which is calculated by tablet
      if (COUNT >= 0) { 
      for (int i = 0; i < COUNT; i++) {
        // BUTTON blink
        digitalWrite(LED_result_PIN, HIGH);   
        delay(200);                       
        digitalWrite(LED_result_PIN, LOW);   
        delay(200);                  
      }
      }
    else {
        digitalWrite(LED_sign_PIN,HIGH);
        for (int i = 0; i < -COUNT; i++) {
        // BUTTON blink
        digitalWrite(LED_result_PIN, HIGH);   
        delay(200);                       
        digitalWrite(LED_result_PIN, LOW);   
        delay(200);                    
      }
      }
    Serial.print("The result is: ");
    Serial.println(COUNT);   
}


void writeString(String stringData) { // Used to serially push out a String with Serial.write()

  for (int i = 0; i < stringData.length(); i++)
  {
    BTSerial.write(stringData[i]);   // Push each char 1 by 1 on each loop pass
  }

}// end writeString
