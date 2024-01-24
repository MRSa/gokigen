/*
  Q-Steer Control Sketch

    --- OUT : IR-LED, LED(Red)
    --- IN  : SW1(Black), SW2(Red) 

 */
#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

/* OUTPUT */
#define IR_LED   2     // D2
#define LED      4     // D4
#define BOARDLED 13    // D13

/* INPUT */
#define SWITCH1  10    // D10
#define SWITCH2  12    // D12

/* COMMAND MASK */
#define COMMAND_MASK  0x0f  // COMMAND (BIT MASK)

/* TRANSMIT BAND */
#define  BAND_MASK    0xf0  // BAND (BIT MASK)
#define  BAND_A       0x00  // QSteer (Band A)
#define  BAND_B       0x10  // QSteer (Band B)
#define  BAND_C       0x20  // QSteer (Band C)
#define  BAND_D       0x30  // QSteer (Band D)

#define  BAND_A2      0x40  // ChoroQ Hybrid (Band A)
#define  BAND_B2      0x50  // ChoroQ Hybrid (Band B)
#define  BAND_C2      0x60  // ChoroQ Hybrid (Band C)
#define  BAND_D2      0x70  // ChoroQ Hybrid (Band D)

/* COMMAND SEND DELAY */
/*    from http://d.hatena.ne.jp/o2mana/20091122/1258912540 */
#define  A_DELAY     7540  // QSteer (Band A)  9ms
#define  B_DELAY    21860  // QSteer (Band B) 25ms
#define  C_DELAY    36200  // QSteer (Band C) 41ms
#define  D_DELAY    50570  // QSteer (Band D) 57ms

#define  A2_DELAY    8700  // ChoroQ Hybrid (Band A) => 870 * 10
#define  B2_DELAY   26100  // ChoroQ Hybrid (Band B) => 870 * 30
#define  C2_DELAY   43500  // ChoroQ Hybrid (Band C) => 870 * 50
#define  D2_DELAY   60900  // ChoroQ Hybrid (Band D) => 870 * 70

#define  SEND_HEADER    1740
#define  DELAY_BIT_SPACE 400

#define  SEND_SHORT      430
#define  SEND_LONG       900

/* communication packet data length */
#define  PACKET_LENGTH     4  // 4 byte

AndroidAccessory acc("Gokigen Project", "QSteerControl", "ChoroQ Controller for ADK", "1.0", "http://sourceforge.jp/projects/gokigen/wiki/QsteerControl", "0000000012345699");

/** prototypes **/
void setup();
void loop();

void sendIR(byte band, byte command);
void setSendData(int time);
void sendIRmain();

/** VARIABLES **/
int boardLed;
int sw1, sw2;

int sendBuffer[24];
int sendBufferIndex;
int led_emo;

/*
    init_leds()

 */
void init_leds()
{
    pinMode(IR_LED, OUTPUT);
    digitalWrite(IR_LED, LOW);

    pinMode(LED, OUTPUT);
    digitalWrite(LED, LOW);

    boardLed = LOW;
    pinMode(BOARDLED, OUTPUT);
    digitalWrite(BOARDLED, LOW);
    
    led_emo = LOW;
}

/*
      init_buttons()
*/
void init_buttons()
{
    pinMode(SWITCH1, INPUT);
    sw1 =  digitalRead(SWITCH1);

    pinMode(SWITCH2, INPUT);
    sw2 =  digitalRead(SWITCH2);
}

/*
  setup() :  prepare hardwares

 */
void setup()
{
    Serial.begin(115200);
    Serial.print("\r\nStart");

    init_leds();
    init_buttons();

    acc.powerOn();
}

/*
   loop() : main routine

 */
void loop()
{
    static byte count = 0;
    static byte ledStatus = 0;
    byte msg[PACKET_LENGTH];
    
    count = count + 1;

    /** READ BUTTON STATUS **/
    sw1 =  digitalRead(SWITCH1);
    sw2 =  digitalRead(SWITCH2);

    /** CHECK BOARDS **/
    if (acc.isConnected())
    {
        int len = acc.read(msg, sizeof(msg), 1);
        if (len > 0)
        {
            // assumes only one command per packet
            if (msg[0] == 0x01)
            {
                // SINGLE CONTROL MODE
                sendIR(msg[1]);
                digitalWrite(LED, msg[2]);
            }
            else if (msg[0] == 0x02)
            {
                // DUAL CONTROL MODE
                sendIR(msg[1]);
                sendIR(msg[2]);
                digitalWrite(LED, msg[3]);
            }
            else if (msg[0] == 0xff)
            {
                // STOP (and LED,IR-OFF)
                byte command = msg[1] | 0x0f;
                sendIR(command);
                command = msg[2] | 0x0f;
                sendIR(command);
                digitalWrite(LED, LOW);
                digitalWrite(IR_LED, LOW);
            }
        }
    }
    else
    {
        // reset outputs to default values on disconnect
        boardLed = ledStatus;

        if (sw1 != 0)
        {
            // LED ON (FWD)
            led_emo = LOW;
            digitalWrite(LED, HIGH);
            sendIR(BAND_A | 0x01);
            sendIR(BAND_B | 0x01);
        }
        if (sw2 != 0)
        {
            // LED ON (BACK)
            led_emo = LOW;
            digitalWrite(LED, HIGH);
            sendIR(BAND_A | 0x02);
            sendIR(BAND_B | 0x02);
        }
        if ((sw1 == 0)&&(sw2 == 0))
        {
            // LED OFF (STOP)
            if (led_emo == LOW)
            {
                sendIR(BAND_A | 0x0f);
                sendIR(BAND_B | 0x0f);
                led_emo = HIGH;
            }
            digitalWrite(IR_LED, LOW);
            digitalWrite(LED, LOW);
       }
    }
    
    /** LED CONTROL LOGIC (BLINK) **/
    count++;
    if (count % 128 == 0)
    {
        if (ledStatus == LOW)
        {
          /* BOARDLED */
          ledStatus = HIGH;
          digitalWrite(BOARDLED, HIGH);
        }
        else
        {
          /** BOARDLED **/
          ledStatus = LOW;
          digitalWrite(BOARDLED, LOW);
          digitalWrite(IR_LED, LOW);
        }
    }
    delay(2);  // wait 2ms.
}

/*
    sendIR()
 */
void sendIR(byte message)
{
    byte band = message & BAND_MASK;    
    byte command = message & COMMAND_MASK;
  
    /** DECIDE BAND DELAY **/
    int delay = A_DELAY;
    switch (band)
    {
      case BAND_D2:
        delay = D2_DELAY;
        command = command | 0x30;
        break;

      case BAND_C2:
        delay = C2_DELAY;
        command = command | 0x20;
        break;

      case BAND_B2:
        delay = B2_DELAY;
        command = command | 0x10;
        break;

      case BAND_A2:
        delay = A2_DELAY;
        break;

      case BAND_D:
        delay = D_DELAY;
        command = command | 0x30;
        break;

      case BAND_C:
        delay = C_DELAY;
        command = command | 0x20;
        break;

      case BAND_B:
        delay = B_DELAY;
        command = command | 0x10;
        break;

      case BAND_A:
      default:
        delay = A_DELAY;
        break;
    }

    /** BUILD A SEND IR COMMAND **/
    sendBufferIndex = 0;
    setSendData(delay);       // send delay
    setSendData(SEND_HEADER); // header data

    /** PARSE COMMAND **/
    for (int i = 5; i >= 0; i--)
    {
      setSendData(DELAY_BIT_SPACE);
      int value = (bitRead(command, i) == 1) ? SEND_LONG : SEND_SHORT;
      setSendData(value);
    }
    sendIRmain();
}

/* 
   setSendData()
 */
void setSendData(int time)
{
    sendBuffer[sendBufferIndex] = time;
    sendBufferIndex++;
}

/*
   sendIRmain()
      from http://d.hatena.ne.jp/o2mana/20091122/1258912540
 */
void sendIRmain()
{
    int limit = sendBufferIndex;
    for (int cnt = 0; cnt <  limit ; cnt++)
    {
        unsigned long len = sendBuffer[cnt];
        unsigned long us = micros();
        do
        {
            digitalWrite(IR_LED, 0 + (cnt&1));
            delayMicroseconds(8);
            digitalWrite(IR_LED, 0);
            delayMicroseconds(7);
        } while (long(us + len - micros()) > 0);
    }
    sendBufferIndex = 0;
}

