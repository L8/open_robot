/*
  Receives Test Events from your phone.
  After it gets a test message the led 13 will blink.
*/
 
#include <MeetAndroid.h>

MeetAndroid meetAndroid;
int onboardLed = 13;

int isOn = 0;

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
   Serial.print("Setup finished... \n");
}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
}

/*
 * This method is called constantly.
 * note: flag is in this case 'A' and numOfValues is 1 (since test event sends a random int)
 */
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
 
 /*
  // the test event in Amarino generates a random value between 0 and 255
  int randomValue = meetAndroid.getInt();
  
  flushLed(300);
  flushLed(300);
  flushLed(randomValue);
  */
}

void flushLed(int time)
{
  digitalWrite(onboardLed, LOW);
  delay(time);
  digitalWrite(onboardLed, HIGH);
  delay(time);
}

