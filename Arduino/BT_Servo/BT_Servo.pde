
#include <MeetAndroid.h>
#include <Servo.h>  

// declare MeetAndroid so that you can call functions with it
MeetAndroid meetAndroid;
Servo servoMotor; 


 int analogPin = 0;      // the analog pin that the sensor is on
 int analogValue = 0;    // the value returned from the analog sensor
 int servoPin = 2;       
 int currentServoValue = 0;
 
void setup()  
{
  
  Serial.begin(115200);
 servoMotor.attach(servoPin);  // attaches the servo on pin 2 to the servo object 
  
  meetAndroid.registerFunction(actuateServo, 'c');
  //meetAndroid.registerFunction(controlDriveMotor, 'b');
}

void loop()
{
  meetAndroid.receive(); 
  servoMotor.write(currentServoValue);
  delay(15); 
}


void actuateServo(byte flag, byte numOfValues)
{
  currentServoValue = meetAndroid.getInt();
}

void controlDriveMotor(byte flag, byte numOfValues)
{
  // drive motor code...
}


