const int buttonPin = 13;     // the number of the pushbutton pin
const int ledPin =  12;       // the number of the LED pin

const int motorEnablePin = 5;
const int directionPin1 = 4;
const int directionPin2 = 6;

const int potPin = 0;


// variables will change:
int buttonState = 0;         // variable for reading the pushbutton status
int potValue = 0;            // variable for storing the potentiometer status
int outputValue = 0;         // variable used to pass the output value for motorEnablePin

void setup() {
  Serial.begin(115200);
  // initialize the LED pin as an output:
  pinMode(ledPin, OUTPUT);      
  pinMode(motorEnablePin, OUTPUT);
  pinMode(directionPin1, OUTPUT);
  pinMode(directionPin2, OUTPUT);
  
  // initialize the pushbutton pin as an input:
  pinMode(buttonPin, INPUT);  

  //digitalWrite(motorEnablePin, HIGH); 
}

void loop(){
  // read the state of the pushbutton value:
  buttonState = digitalRead(buttonPin);
  
  
  // read the analog in value:
  potValue = analogRead(potPin);            
  // map it to the range of the analog out:
  outputValue = map(potValue, 0, 1023, 0, 255);  
  // change the analog out value:
  analogWrite(motorEnablePin, outputValue);


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
  
  delay(10);
}
