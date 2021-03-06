#include <MeetAndroid.h>
#include <Servo.h>      // include the servo library

#define SERVO_OFF 90

Servo servoMotor;         // creates an instance of the servo object to control a servo
Servo driveMotor;
MeetAndroid meetAndroid;  // creates an instance of Amarino's MeetAndroid to send and receive messages

const int ledPin =  13;       // the number of the LED pin

const int isReadingInputPin = 12;
const int drivePin = 3;
const int servoPin = 2;

int servoValue = SERVO_OFF;
int motorValue = SERVO_OFF;

int killEnabled = 0;

void setup() {
  Serial.begin(115200);
  //Serial.println("HELLLLLLLLOOOOOOOOOO");
 
  pinMode(ledPin, OUTPUT);   
  pinMode(isReadingInputPin, OUTPUT);  
  pinMode(drivePin, OUTPUT);
  pinMode(servoPin, OUTPUT);
  
  servoMotor.attach(servoPin);  // attaches the servo on pin 2 to the servo object
  driveMotor.attach(drivePin);
  
  servoMotor.write(SERVO_OFF);
  driveMotor.write(SERVO_OFF);

  meetAndroid.registerFunction(acceptControlInput, 'c');
  meetAndroid.registerFunction(killEverything, 'd');
}

void loop(){
  digitalWrite(isReadingInputPin, LOW);
  meetAndroid.receive(); 
  
  
  if (killEnabled == 1) {
      servoMotor.write(SERVO_OFF);
      driveMotor.write(SERVO_OFF);
  } else {
    servoMotor.write(servoValue);
    driveMotor.write(motorValue);
  }
  
  delay(15);
}

void acceptControlInput(byte flag, byte numOfValues)
{
  digitalWrite(isReadingInputPin, HIGH);
  int data[numOfValues];
  meetAndroid.getIntValues(data);
  
  servoValue = map(data[0], 0, 250, 40, 139);
  motorValue = map(data[1], 0, 250, 50, 129);
  
  
  /*
  Serial.print("servo raw =\t");
  Serial.print(data[0]);
  Serial.print("\t processed =\t");
  Serial.print(servoValue);  
  Serial.print("\t\tmotor raw =\t");
  Serial.print(data[1]);
  Serial.print("\t processed =\t");
  Serial.println(motorValue);  
*/
}

void killEverything(byte flag, byte numOfValues)
{
 
  int value = meetAndroid.getInt();
  if (value == 1) {
      killEnabled = 1;
  } else {
    killEnabled = 0;
  }
}

