#define MAX_MODES 7

typedef enum LightMode {
	LightModeNone = 0x0,
	LightModeRed = 0x1,
	LightModeGreen = 0x2,
	LightModeBlue = 0x4
} LightMode;


// OUTPUT pins
int redPin = 13;
int greenPin = 12;
int bluePin = 11;

// INPUT pins
int switchPin = 2;

// Variables
int switchValue;
int val;
int buttonState;
int lightMode;

int temp1; 

boolean temp2;

void setup()   {                
  // initialize the digital pin as an output:
  pinMode(redPin, OUTPUT);     
  pinMode(greenPin, OUTPUT);     
  pinMode(bluePin, OUTPUT);     
  pinMode(switchPin, INPUT);

  // setup serial connection at 9600 bps
  Serial.begin(9600);
  temp1 = digitalRead(switchPin);
  temp2 = 0;
  lightMode = LightModeNone;
}


void loop()                     
{
  val = digitalRead(switchPin);

  if (val != temp1) {
     temp1 = val;
     if (val == HIGH) {
     	temp2 = !temp2;
	Serial.println(temp2 ? "ON" : "OFF");
     if (lightMode < MAX_MODES) {
     	lightMode = lightMode + 1;
     } else {
       lightMode = LightModeNone;
     }
      digitalWrite(redPin, LOW);
      digitalWrite(greenPin, LOW); 
      digitalWrite(bluePin, LOW);     
  
if (lightMode & LightModeRed) {
     digitalWrite(redPin, HIGH);
}
if (lightMode & LightModeGreen) {
     digitalWrite(greenPin, HIGH);
}  
if (lightMode & LightModeBlue) {
     digitalWrite(bluePin, HIGH);
}
     }
  }




}
