
#include <MeetAndroid.h>

MeetAndroid meetAndroid;
int onboardLed = 9;
int isOn = 0;

int calibrationTime = 30;        

//the time when the sensor outputs a low impulse
long unsigned int lowIn;         

//the amount of milliseconds the sensor has to be low 
//before we assume all motion has stopped
long unsigned int pause = 5000;  

boolean lockLow = true;
boolean takeLowTime;  

int pirPin = 3;    //the digital pin connected to the PIR sensor's output
int ledPin = 13;


void setup()  
{
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(115200); 
  
  // register callback functions, which will be called when an associated event occurs.
  // - the first parameter is the name of your function (see below)
  // - match the second parameter ('A', 'B', 'a', etc...) with the flag on your Android application
  meetAndroid.registerFunction(toggleLED, 'c');  

  pinMode(onboardLed, OUTPUT);
  digitalWrite(onboardLed, LOW);
  
   pinMode(pirPin, INPUT);
  pinMode(ledPin, OUTPUT);
  digitalWrite(pirPin, LOW);

  //give the sensor some time to calibrate
  Serial.print("calibrating sensor ");
    for(int i = 0; i < calibrationTime; i++){
      Serial.print(".");
      delay(1000);
      }
    Serial.println(" done");
    Serial.println("SENSOR ACTIVE");
    delay(50);
  
   Serial.print("Setup finished... \n");
}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
  
  // PIR Logic
    if(digitalRead(pirPin) == HIGH){
       digitalWrite(ledPin, HIGH);   //the led visualizes the sensors output pin state
       if(lockLow){  
         //makes sure we wait for a transition to LOW before any further output is made:
         lockLow = false;            
         //Serial.println("---");
         //Serial.print("motion detected at ");
         meetAndroid.send("1");
         Serial.print(millis()/1000);
         //Serial.println(" sec"); 
         delay(50);
        }         
        takeLowTime = true;
       }

     if(digitalRead(pirPin) == LOW){       
       digitalWrite(ledPin, LOW);  //the led visualizes the sensors output pin state

       if(takeLowTime){
        lowIn = millis();          //save the time of the transition from high to LOW
        takeLowTime = false;       //make sure this is only done at the start of a LOW phase
        }
       //if the sensor is low for more than the given pause, 
       //we assume that no more motion is going to happen
       if(!lockLow && millis() - lowIn > pause){  
           //makes sure this block of code is only executed again after 
           //a new motion sequence has been detected
           lockLow = true;                        
           //Serial.print("motion ended at ");      //output
           meetAndroid.send("0");
           //Serial.print((millis() - pause)/1000);
           //Serial.println(" sec");
           delay(50);
           }
       }
}


void toggleLED(byte flag, byte numOfValues)
{
  Serial.print("toggleLED");
  if (isOn == 0) {
    digitalWrite(onboardLed, HIGH);
    isOn = 1;
  } else {
    digitalWrite(onboardLed, LOW);
    isOn = 0;
  }
 
