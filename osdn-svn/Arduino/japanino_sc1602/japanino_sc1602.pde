/*---------------------------------------------------*
 *
 *
 *
 *
 *---------------------------------------------------*/
#include <LiquidCrystal.h>

#define RECV_MAX 46    // Buffer size of serial receiving buffer
char receiveBuffer[RECV_MAX + 1];  // Serial Receiving buffer

int LED = 13;          // a LED on the Japanino  Board
int Vo = 10;           // Contrast Control Pin (it uses D10 pin)
int CONTRAST = 15;     // Contrast Level (PWM : 15 is my favorite)
int duration = 500;    // LED blinking duration (milliseconds)
int ledStatus  = 0;    // LED light ON or NOT

//  LCD Board Control Library(SC1602 compatible)
//     - +5V    : (1)VDD
//     - GND    : (2)Vss
//     - D10    : (3)Vo
//     - D11    : (4)RS
//     - GND    : (5)Read/Write
//     - D9     : (6)Enable
//     -        : (7)-(10)DB0-DB3 NC
//     - D4-D7  : (11)-(14)DB4-DB7
LiquidCrystal lcd(11, 9, 4, 5, 6, 7);

/*
 *  Power-On message
 *
 */
void initialMessage()
{
  lcd.begin(16, 2);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("<<< POWER ON >>>");
  lcd.setCursor(2, 1);
  lcd.print("Japanino.");
  
  byte bitChar[8] = {
    B00000,
    B01010,
    B11111,
    B11111,
    B01110,
    B00100,
    B00000,
  };
  lcd.createChar(0, bitChar);
  lcd.setCursor(15, 1);
  lcd.write(0);
  delay(1000);   // 1sec.
}

/*
 *  Print message to LCD.
 *
 */
void writeMessage()
{
  char line1[RECV_MAX + 1];
  char line2[RECV_MAX + 1];

  // copy to line buffer from receive buffer
  char mode  =0;
  int count = 0;
  int count1 = 0;
  int count2 = 0;
  char ch = receiveBuffer[count];
  while (((ch != 0)&&(ch != '\\'))||(count < RECV_MAX))
  {
      if (mode == 0)
      {
        if (ch == '!')
        {
          mode = 1;
        }
        else
        {
          line1[count1] = ch;
          count1++;
        }
      }
      else
      {
        line2[count2] = ch;
        count2++;
      }
      count++;
      ch = receiveBuffer[count];
  }
  line1[count1] = 0;
  line2[count2] = 0;

  lcd.begin(16, 2);
  lcd.clear();
  if (line1[0] != 0)
  {
      lcd.setCursor(0,0);
      lcd.print(line1);
  }
  if (line2[0] != 0)
  {
      lcd.setCursor(0,1);
      lcd.print(line2);
  }
}

void setup()
{
  Serial.begin(9600);
  Serial.print("\r\nStart Japanino!\r\n");

  pinMode(LED, OUTPUT); 
  pinMode(Vo, OUTPUT); 
  analogWrite(Vo, CONTRAST);
  lcd.begin(16, 2);

  receiveBuffer[0] = ' ';
  receiveBuffer[1] = 0;

  initialMessage();
}

byte blinkLED(byte status)
{
    if (status == 0)
    {
        digitalWrite(LED, LOW);
        return (255);
    }
    digitalWrite(LED, HIGH);
    return (0);
}

void loop()
{
    static byte count = 0;
    while (Serial.available() > 0)
    {
        byte ch = Serial.read();
        receiveBuffer[count] = ch;
        count++;
        if ((ch == 0)||(ch == '\\')||(count == RECV_MAX))
        {
           // BUFFER FULL or DATA END
           receiveBuffer[count - 1] = ' ';
           receiveBuffer[count] = 0;
           count = 0;
           Serial.print(receiveBuffer);
           Serial.print("\r\n");
        }
    }
    ledStatus = blinkLED(ledStatus);
    writeMessage();
    delay(duration);
}

