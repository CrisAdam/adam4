

int ch1, ch2, ch3; // store value

void setup()
{                
  Serial.begin(38400);
  pinMode(21, INPUT); // Set our input pins as such
  pinMode(22, INPUT); // Set our input pins as such
  pinMode(23, INPUT);
  Serial.println("Starting serial");
//  pinMode(21, INPUT);
}

int val;

void loop()                     
{
  ch1 = pulseIn(22, HIGH, 25000);
  ch2 = pulseIn(22, HIGH, 25000); // Read the pulse width of 
  ch3 = pulseIn(23, HIGH, 25000); // each channel

  
  Serial.print("ch1 ");
  Serial.print(ch1);
  Serial.print(" ch2 ");
  Serial.print(ch2);
  Serial.print(" ch3 ");
  Serial.print(ch3);
  Serial.println("");
  delay(50);
}

