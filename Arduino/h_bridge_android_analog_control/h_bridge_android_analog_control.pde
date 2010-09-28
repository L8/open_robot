#include <MeetAndroid.h>
#include <Servo.h>      // include the servo library

Servo servoMotor;         // creates an instance of the servo object to control a servo
MeetAndroid meetAndroid;  // creates an instance of Amarino's MeetAndroid to send and receive messages

const int ledPin =  12;       // the number of the LED pin

const int motorEnablePin = 5;
const int directionPin1 = 4;
const int directionPin2 = 6;

const int servoPin = 12;

const int potPin = 0;


// variables will change:
int potValue = 0;            // variable for storing the potentiometer status
int outputValue = 0;         // variable used to pass the output value for motorEnablePin
int currDirectionAngle = 89; // variable used to store direction angle between 0 and 179

boolean motorDirectionIsForward = true;

void setup() {
  Serial.begin(115200);
  // initialize the LED pin as an output:
  pinMode(ledPin, OUTPUT);      
  pinMode(motorEnablePin, OUTPUT);
  pinMode(directionPin1, OUTPUT);
  pinMode(directionPin2, OUTPUT); 
  
  servoMotor.attach(servoPin);  // attaches the servo on pin 2 to the servo object

   meetAndroid.registerFunction(acceptControlInput, 'c');
   meetAndroid.registerFunction(motorDirectionChange, 'd');
}

void loop(){
  
  meetAndroid.receive(); 
  
  
  // read the analog in value:
  potValue = analogRead(potPin);            
  
  // change the analog out value:
  analogWrite(motorEnablePin, outputValue);
  servoMotor.write(currDirectionAngle);
  
  if (motorDirectionIsForward) {     
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

void acceptControlInput(byte flag, byte numOfValues)
{
  int value = meetAndroid.getInt();
  
  outputValue = map(value, 0, 125, 0, 255);
  currDirectionAngle = map(value, 0, 125, 0, 179);

}

void motorDirectionChange(byte flag, byte numOfValues)
{
  int value = meetAndroid.getInt();
  
  if (value > 0) {
    motorDirectionIsForward = true;
  } else if (value < 0) {
    motorDirectionIsForward = false;
  }
}




