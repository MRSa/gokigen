/*
  Paper Sumo Battle Field ... PSBF

    --- 3-axis Sensor, button, LED rx/tx 

 */
#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

/* 3-axis sensor input port(A) */
#define  AXISa1		A0
#define  AXISa2		A1
#define  AXISa3		A2

/* 3-axis sensor input port(B) */
#define  AXISb1		A3
#define  AXISb2		A4
#define  AXISb3		A5

/* tatakon (drum controller) A */
#define TATAKON_A1      A8
#define TATAKON_A2      A9
#define TATAKON_A3      A10
#define TATAKON_A4      A11

/* tatakon (drum controller) B */
#define TATAKON_B1      A12
#define TATAKON_B2      A13
#define TATAKON_B3      A14
#define TATAKON_B4      A15

/* momentary switch (DI) */
#define  BUTTON            9  // BUTTON : D9

/* MOTORS (Ardumoto) */
#define MOTOR_A_PWM        3  // PWM-A : D3
#define MOTOR_B_PWM       11  // PWM-B : D11
#define MOTOR_A_DIRECTION 12  // DIR-A : D12
#define MOTOR_B_DIRECTION 13  // DIR-B : D13

/* LEDs */
#define  LED1              4  // LED1 : D4
#define  LED2              5  // LED2 : D5
#define  LED3              6  // LED3 : D6
#define  LED4              7  // LED4 : D7
#define  LED5              8  // LED5 : D8

/* operation lamp */
#define  BOARDLED         10  //  BOARDLED : D10

/* communication packet data length */
#define  PACKET_LENGTH     4  // 4 byte

/* LED STATUS */
#define  LED_OFF        0
#define  LED_ON         1
#define  LED_BLINK      2

#define  CHECK_UPPER    15
#define  CHECK_LOWER   -15

#define  TATAKON_CHECK_UPPER    5
#define  TATAKON_CHECK_LOWER   -5


AndroidAccessory acc("Gokigen Project", "PaperSumoBattleField", "Paper Sumo BattleField for Arduino Board", "1.0", "http://sourceforge.jp/projects/gokigen/wiki/PaperSumoBattleField", "0000000012345678");

/** prototypes **/
void setup();
void loop();


/** VARIABLES **/
int button;
int led1, led2, led3, led4, led5;
int axisA1, axisA2, axisA3;
int axisB1, axisB2, axisB3;
int tatakonA1, tatakonA2, tatakonA3, tatakonA4;
int tatakonB1, tatakonB2, tatakonB3, tatakonB4;
int boardLed;

/*
    init_leds()

 */
void init_leds()
{
   pinMode(LED1, OUTPUT);
    digitalWrite(LED1, LOW);

    pinMode(LED2, OUTPUT);
    digitalWrite(LED2, LOW);

    pinMode(LED3, OUTPUT);
    digitalWrite(LED3, LOW);

    pinMode(LED4, OUTPUT);
    digitalWrite(LED4, LOW);

    pinMode(LED5, OUTPUT);
    digitalWrite(LED5, LOW);

    led1 = LED_OFF;
    led2 = LED_OFF;
    led3 = LED_OFF;
    led4 = LED_OFF;
    led5 = LED_OFF;
    boardLed = LED_OFF;

    pinMode(BOARDLED, OUTPUT);
    digitalWrite(BOARDLED, HIGH);
    
}
/*
    init_motors()
*/
void init_motors()
{
    pinMode(MOTOR_A_PWM, OUTPUT);
    pinMode(MOTOR_B_PWM, OUTPUT);

    pinMode(MOTOR_A_DIRECTION, OUTPUT);
    pinMode(MOTOR_B_DIRECTION, OUTPUT);
    
    analogWrite(MOTOR_A_PWM, 0);
    analogWrite(MOTOR_B_PWM, 0);
    
    digitalWrite(MOTOR_A_DIRECTION, LOW);  // normal Direction
    digitalWrite(MOTOR_B_DIRECTION, LOW);  // normal Direction
}


/*
    init_sensors()

 */
void init_sensors()
{
    analogReference(EXTERNAL);
  
    pinMode(AXISa1, INPUT);
    pinMode(AXISa2, INPUT);
    pinMode(AXISa3, INPUT);

    pinMode(AXISb1, INPUT);
    pinMode(AXISb2, INPUT);
    pinMode(AXISb3, INPUT);

    pinMode(TATAKON_A1, INPUT);
    pinMode(TATAKON_A2, INPUT);
    pinMode(TATAKON_A3, INPUT);
    pinMode(TATAKON_A4, INPUT);

    pinMode(TATAKON_B1, INPUT);
    pinMode(TATAKON_B2, INPUT);
    pinMode(TATAKON_B3, INPUT);
    pinMode(TATAKON_B4, INPUT);

}

/*
  setup() :  prepare hardwares

 */
void setup()
{
    Serial.begin(115200);
    Serial.print("\r\nStart");

    init_leds();
    init_motors();
    init_sensors();

    pinMode(BUTTON, INPUT);
    
    axisA1 = 0; // analogRead(AXISa1);
    axisA2 = 0; // analogRead(AXISa2);
    axisA3 = 0; // analogRead(AXISa3);

    axisB1 = 0; // analogRead(AXISb1);
    axisB2 = 0; // analogRead(AXISb2);
    axisB3 = 0; // analogRead(AXISb3);

    button = digitalRead(BUTTON);
    
    tatakonA1 = analogRead(TATAKON_A1);
    tatakonA2 = analogRead(TATAKON_A2);
    tatakonA3 = analogRead(TATAKON_A3);
    tatakonA4 = analogRead(TATAKON_A4);
    
    tatakonB1 = analogRead(TATAKON_B1);
    tatakonB2 = analogRead(TATAKON_B2);
    tatakonB3 = analogRead(TATAKON_B3);
    tatakonB4 = analogRead(TATAKON_B4);

    acc.powerOn();
}

/*
   loop() : main routine

 */
void loop()
{
    static byte count = 0;
    static byte ledStatus = 0;
    byte data;
    unsigned int analogValue;
    int tempValue;
    int checkBand;
    byte msg[4];
    
    count = count + 1;

    if (acc.isConnected())
    {
        int len = acc.read(msg, sizeof(msg), 1);
        if (len > 0)
        {
            // assumes only one command per packet
            if (msg[0] == 0x02)
            {
              if (msg[1] == 0x00)
              {
                    data = (0xff & msg[2]);          
                    if (data == 0x00)
                    {
                        led1 = LED_OFF;
                    }
                    else if (data > 0x7f)
                    {
                        led1 = LED_BLINK;
                    }
                    else
                    {
                        led1 = LED_ON;
                    }                    
                    //analogWrite(LED1, data);
              }
              else if (msg[1] == 0x01)
              {
                    data = (0x00ff & msg[2]);
                    if (data == 0x00)
                    {
                        led2 = LED_OFF;
                    }
                    else if (data > 0x7f)
                    {
                        led2 = LED_BLINK;
                    }
                    else
                    {
                        led2 = LED_ON;
                    }                    
                    //analogWrite(LED2, data);
              }
              else if (msg[1] == 0x02)
              {
                    data = (0x00ff & msg[2]);
                    if (data == 0x00)
                    {
                        led3 = LED_OFF;
                    }
                    else if (data > 0x7f)
                    {
                        led3 = LED_BLINK;
                    }
                    else
                    {
                        led3 = LED_ON;
                    }                    
                    //analogWrite(LED3, data);
              }
              else if (msg[1] == 0x03)
              {
                    data = (0x00ff & msg[2]);
                    if (data == 0x00)
                    {
                        led4 = LED_OFF;
                    }
                    else if (data > 0x7f)
                    {
                        led4 = LED_BLINK;
                    }
                    else
                    {
                        led4 = LED_ON;
                    }                    
                    //analogWrite(LED4, data);
              }
              else if (msg[1] == 0x04)
              {
                    data = (0x00ff & msg[2]);
                    if (data == 0x00)
                    {
                        led5 = LED_OFF;
                    }
                    else if (data > 0x7f)
                    {
                        led5 = LED_BLINK;
                    }
                    else
                    {
                        led5 = LED_ON;
                    }                    
                    //analogWrite(LED5, data);
              }
            }

            /******** MOTOR CONTROL { ********/
            // assumes only one command per packet
            if (msg[0] == 0x03)
            {
              data = (0xff & msg[2]);
              if (msg[1] == 0x00)
              {
                  analogWrite(MOTOR_A_PWM, data);
              }
              else if (msg[1] == 0x01)
              {
                  analogWrite(MOTOR_B_PWM, data);
              }
            }
            /******** } MOTOR CONTROL ********/
        }

        /*  AXISa1 */
        analogValue = analogRead(AXISa1);
        checkBand = analogValue - axisA1;
        if ((checkBand > CHECK_UPPER)||(checkBand < CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x00;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            axisA1 = analogValue;
        }

        /*  AXISa2 */
        analogValue = analogRead(AXISa2);
        checkBand = analogValue - axisA2;
        if ((checkBand > CHECK_UPPER)||(checkBand < CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x01;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            axisA2 = analogValue;
        }

        /*  AXISa3 */
        analogValue = analogRead(AXISa3);
        checkBand = analogValue - axisA3;
        if ((checkBand > CHECK_UPPER)||(checkBand < CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x02;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            axisA3 = analogValue;
        }

        /*  BUTTON */
        analogValue = digitalRead(BUTTON);
        if (analogValue != button)
        {
            msg[0] = 0x01;
            msg[1] = 0x03;
            if (analogValue >= 1)
            {
              analogValue = 1023;
            }
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            button = analogValue;
        }

        /*  AXISb1 */
        analogValue = analogRead(AXISb1);
        checkBand = analogValue - axisB1;
        if ((checkBand > CHECK_UPPER)||(checkBand < CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x04;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            axisB1 = analogValue;
        }

        /*  AXISb2 */
        analogValue = analogRead(AXISb2);
        checkBand = analogValue - axisB2;
        if ((checkBand > CHECK_UPPER)||(checkBand < CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x05;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            axisB2 = analogValue;
        }

        /*  AXISb3 */
        analogValue = analogRead(AXISb3);
        checkBand = analogValue - axisB3;
        if ((checkBand > CHECK_UPPER)||(checkBand < CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x06;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            axisB3 = analogValue;
        }

        /*  TATAKON A1 */
        analogValue = analogRead(TATAKON_A1);
        checkBand = analogValue - tatakonA1;
        if ((checkBand > TATAKON_CHECK_UPPER)||(checkBand < TATAKON_CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x07;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            tatakonA1 = analogValue;
        }

        /*  TATAKON A2 */
        analogValue = analogRead(TATAKON_A2);
        checkBand = analogValue - tatakonA2;
        if ((checkBand > TATAKON_CHECK_UPPER)||(checkBand < TATAKON_CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x08;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            tatakonA2 = analogValue;
        }

        /*  TATAKON A3 */
        analogValue = analogRead(TATAKON_A3);
        checkBand = analogValue - tatakonA3;
        if ((checkBand > TATAKON_CHECK_UPPER)||(checkBand < TATAKON_CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x09;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            tatakonA3 = analogValue;
        }

        /*  TATAKON A4 */
        analogValue = analogRead(TATAKON_A4);
        checkBand = analogValue - tatakonA4;
        if ((checkBand > TATAKON_CHECK_UPPER)||(checkBand < TATAKON_CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x0a;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            tatakonA4 = analogValue;
        }

        /*  TATAKON B1 */
        analogValue = analogRead(TATAKON_B1);
        checkBand = analogValue - tatakonB1;
        if ((checkBand > TATAKON_CHECK_UPPER)||(checkBand < TATAKON_CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x0b;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            tatakonB1 = analogValue;
        }

        /*  TATAKON B2 */
        analogValue = analogRead(TATAKON_B2);
        checkBand = analogValue - tatakonB2;
        if ((checkBand > TATAKON_CHECK_UPPER)||(checkBand < TATAKON_CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x0c;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            tatakonB2 = analogValue;
        }

        /*  TATAKON B3 */
        analogValue = analogRead(TATAKON_B3);
        checkBand = analogValue - tatakonB3;
        if ((checkBand > TATAKON_CHECK_UPPER)||(checkBand < TATAKON_CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x0d;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            tatakonB3 = analogValue;
        }

        /*  TATAKON B4 */
        analogValue = analogRead(TATAKON_B4);
        checkBand = analogValue - tatakonB4;
        if ((checkBand > TATAKON_CHECK_UPPER)||(checkBand < TATAKON_CHECK_LOWER))
        {
            msg[0] = 0x01;
            msg[1] = 0x0e;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);

            tatakonB4 = analogValue;
        }
        
        /*  BOARD LED STATUS  */
        if (ledStatus != boardLed)
        {
            if (ledStatus != LED_OFF)
            {
                // change LED_OFF => LED_ON
                analogValue = 1023;
            }
            else
            {
                // change LED_ON => LED_OFF
                analogValue = 0;
            }
            msg[0] = 0x01;
            msg[1] = 0x0f;
            msg[2] = ((analogValue & 0xff00) >> 8); 
            msg[3] = (analogValue & 0x00ff);
            acc.write(msg, PACKET_LENGTH);
            boardLed = ledStatus;
        }        
    }
    else
    {
        // reset outputs to default values on disconnect
        digitalWrite(LED1, LOW);
        digitalWrite(LED2, LOW);
        digitalWrite(LED3, LOW);
        digitalWrite(LED4, LOW);
        digitalWrite(LED5, LOW);

        led1 = LED_OFF;
        led2 = LED_OFF;
        led3 = LED_OFF;
        led4 = LED_OFF;
        led5 = LED_OFF;
        boardLed = ledStatus;
    }
    
    /** LED CONTROL LOGIC (ON/OFF/BLINK) **/
    count++;
    if (count % 16 == 0)     //  16 * 16 = 256ms cycle
    {
        if (ledStatus == LED_OFF)
        {
          /* BOARDLED */
          ledStatus = LED_ON;
          digitalWrite(BOARDLED, LOW);

          /* LED1 */
          if (led1 != LED_ON)
          {
              digitalWrite(LED1, LOW);
          }
          else
          {
              digitalWrite(LED1, HIGH);
          }
          /* LED2 */
          if (led2 != LED_ON)
          {
              digitalWrite(LED2, LOW);
          }
          else
          {
              digitalWrite(LED2, HIGH);
          }
          /* LED3 */
          if (led3 != LED_ON)
          {
              digitalWrite(LED3, LOW);
          }
          else
          {
              digitalWrite(LED3, HIGH);
          }
          /* LED4 */
          if (led4 != LED_ON)
          {
              digitalWrite(LED4, LOW);
          }
          else
          {
              digitalWrite(LED4, HIGH);
          }
          /* LED5 */
          if (led5 != LED_ON)
          {
              digitalWrite(LED5, LOW);
          }
          else
          {
              digitalWrite(LED5, HIGH);
          }
        }
        else
        {
          /** BOARDLED **/
          ledStatus = LED_OFF;
          digitalWrite(BOARDLED, HIGH);

          /* LED1 */
          if (led1 != LED_OFF)
          {
              digitalWrite(LED1, HIGH);
          }
          else
          {
              digitalWrite(LED1, LOW);
          }
          /* LED2 */
          if (led2 != LED_OFF)
          {
              digitalWrite(LED2, HIGH);
          }
          else
          {
              digitalWrite(LED2, LOW);
          }
          /* LED3 */
          if (led3 != LED_OFF)
          {
              digitalWrite(LED3, HIGH);
          }
          else
          {
              digitalWrite(LED3, LOW);
          }
          /* LED4 */
          if (led4 != LED_OFF)
          {
              digitalWrite(LED4, HIGH);
          }
          else
          {
              digitalWrite(LED4, LOW);
          }
          /* LED5 */
          if (led5 != LED_OFF)
          {
              digitalWrite(LED5, HIGH);
          }
          else
          {
              digitalWrite(LED5, LOW);
          }
        }
    }
    //delay(35);    // wait 35ms.    
    //delay(20);  // wait 20ms.
    //delay(10);  // wait 10ms.
    delay(16);  // wait 16ms.
}

