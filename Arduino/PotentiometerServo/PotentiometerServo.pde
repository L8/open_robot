 #include <Servo.h>      // include the servo library

 Servo servoMotor;       // creates an instance of the servo object to control a servo

 int analogPin = 0;      // the analog pin that the sensor is on
 int analogValue = 0;    // the value returned from the analog sensor

 int servoPin = 2;       
 
 void setup() { 
   Serial.begin(9600);
   servoMotor.attach(servoPin);  // attaches the servo on pin 2 to the servo object
 } 

 void loop() 
 { 
  
   analogValue = analogRead(analogPin);                 // read the analog input (value between 0 and 1023)
   analogValue = map(analogValue, 0, 1023, 0, 179);     // map the analog value (0 - 1023) to the angle of the servo (0 - 179)
   servoMotor.write(analogValue);                       // write the new mapped analog value to set the position of the servo
  
   delay(15);                                           // waits for the servo to get there 
 }
