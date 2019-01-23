 //If youre not using a BTBee connect set the pin connected to the KEY pin high
#include <SoftwareSerial.h>
#include <LiquidCrystal.h>

bool mFinished=false;
char totalString[50];
char commandChar;
int power_LED=A0;
int transceive_LED=A1;
int counter=0;
SoftwareSerial BTSerial(10, 11);
LiquidCrystal lcd(8, 9, 4, 5, 6, 7);
void setup() {
   Serial.begin(9600);
  BTSerial.begin(38400);
 lcd.begin(16,2);
 lcd.setCursor(2,0);
 lcd.print("Voice Based ");
 lcd.setCursor(2,1);
 lcd.print("Notice Board");
 delay(2000);
  pinMode(transceive_LED,OUTPUT);
   pinMode(power_LED,OUTPUT);
   
   digitalWrite(power_LED,HIGH);
     mLCDScroll();
}
char incomingByte = 0;


void loop() {
incomingByte=BTSerial.available();

if(BTSerial.available()>0){
  delay(500);
 for(int i=0;i<incomingByte;i++){
    if(counter==0){
      lcd.clear();
      counter=1;
    }else{
      if(mFinished){
        lcd.clear();
        mFinished=false;
        
      }
        int inputCharacter = BTSerial.read();
        if(i==16){
          lcd.setCursor(0,1);
          lcd.write(inputCharacter);
        }else if(i==31){
        delay(4000);
        if(incomingByte>48){
          lcd.clear();
        }
          lcd.write(inputCharacter);
        }else if(i==47){
          lcd.setCursor(0,1);
          lcd.write(inputCharacter);
          delay(4000);
        }else if(i==64){
          delay(4000);
        if(incomingByte>79){
          lcd.clear();
        }
          lcd.write(inputCharacter);
        }else if(i==79){
          lcd.setCursor(0,1);
          lcd.write(inputCharacter);
          delay(4000);
        }else{
        lcd.write(inputCharacter);
        Serial.write(inputCharacter);
       // lcd.clear();
        }
    }
  
 }
   
    mFinished=true;
    
  
    
    
    }
  
  

  
  
  // BTSerial.flush();
  
}
void mLCDScroll(){
  
    for (int positionCounter = 0; positionCounter < 13; positionCounter++) {
    // scroll one position left:
    lcd.scrollDisplayLeft();
    // wait a bit:
    delay(150);
  }

  // scroll 29 positions (string length + display length) to the right
  // to move it offscreen right:
  for (int positionCounter = 0; positionCounter < 29; positionCounter++) {
    // scroll one position right:
    lcd.scrollDisplayRight();
    // wait a bit:
    delay(150);
  }

  // scroll 16 positions (display length + string length) to the left
  // to move it back to center:
  for (int positionCounter = 0; positionCounter < 16; positionCounter++) {
    // scroll one position left:
    lcd.scrollDisplayLeft();
    // wait a bit:
    delay(150);
  }

  // delay at the end of the full loop:
  delay(1000);
  
  
}

void WriteStringToLCD(const char *s){
				int counter;
	while(*s){
		counter+=1;
		//WriteDataToLCD(*s);   // print first character on LCD 
                lcd.print(*s);
	s++;
		if(counter==13){
			delay(500);
		//WriteCommandToLCD(0xC2);
                lcd.setCursor(0,2);
		}
                
	}
}



