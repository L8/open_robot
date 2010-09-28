#include <MeetAndroid.h>

// declare MeetAndroid so that functions can be called with it
MeetAndroid meetAndroid;

const int buttonPin = 13;     // the number of the pushbutton pin
const int ledPin =  12;       // the number of the LED pin

const int motorEnablePin = 5;
const int directionPin1 = 4;
const int directionPin2 = 6;


// variables will change:
int buttonState = 0;         // variable for reading the pushbutton status

void setup() {
  Serial.begin(115200);
  // initialize the LED pin as an output:
  pinMode(ledPin, OUTPUT);      
  pinMode(motorEnablePin, OUTPUT);
  pinMode(directionPin1, OUTPUT);
  pinMode(directionPin2, OUTPUT);
  
  // initialize the pushbutton pin as an input:
  pinMode(buttonPin, INPUT);  

  digitalWrite(motorEnablePin, HIGH); 
  
  meetAndroid.registerFunction(changeDirection, 'c');
}

void loop(){
  
  meetAndroid.receive(); 

  // check if the pushbutton is pressed.
  // if it is, the buttonState is HIGH:
  if (buttonState == HIGH) {     
    // turn LED on:    
    digitalWrite(ledPin, HIGH); 
    digitalWrite(directionPin1, LOW);   // set leg 1 of the H-bridge low
    digitalWrite(directionPin2, HIGH);  // set leg 2 of the H-bridge high 
  } 
  else {
    // turn LED off:
    digitalWrite(ledPin, LOW); 
    digitalWrite(directionPin1, HIGH);  // set leg 1 of the H-bridge high
    digitalWrite(directionPin2, LOW);   // set leg 2 of the H-bridge low 
  }
  delay(15);
}

void changeDirection(byte flag, byte numOfValues)
{
  buttonState = meetAndroid.getInt();
}

