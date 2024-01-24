/**
 * ------------------------------------------------------------------------------
 *   ctl_mr999.c  :  MR-999の制御実行
 *
 * ------------------------------------------------------------------------------
 */
#include <cv.h>
#include <highgui.h>
#include <usb.h>
#include "io_usbio.h"
#include "mydefine.h"

/* MR-999 の制御用変数 */
struct usb_dev_handle *usbio_device;



/*
 *  アームの動作全体の停止
 */
unsigned char stopArm(unsigned char value)
{
    return (255);
}


/*
 *  指の動作停止　
 */
unsigned char stopGripper(unsigned char value)
{
  return ((value & ((unsigned char) 255 - 3)));
}


/*
 *  指の動作指示
 */
unsigned char controlGripper(int isGrip, unsigned char value)
{
    unsigned char data = value & (255 - 3);

    return ((isGrip != 0) ? (data + 2) :  (data + 1));
}

/*
 *  腕の動作停止 (こいつはポート１に送信すること！)
 */
unsigned char stopWrist(unsigned char value)
{
    return (value & (255 - 3));
}


/*
 *  腕の動作指示 (こいつはポート１に送信すること！)
 */
unsigned char controlWrist(int isTurnLeft, unsigned char value)
{
    unsigned char data = value & (255 - 3);

    return ((isTurnLeft != 0) ? (data + 2) :  (data + 1));
}


/*
 *  肘の動作停止
 */
unsigned char stopElbow(unsigned char value)
{
    return (value & (255 - 12));
}

/*
 *  肘の動作指示
 */
unsigned char controlElbow(int isUp, unsigned char value)
{
    unsigned char data = value & (255 - 12);

    return ((isUp != 0) ? (data + 4) :  (data + 8));
}

/*
 *  肩の動作停止
 */
unsigned char stopShoulder(unsigned char value)
{
    return (value & (255 - 192));
}

/*
 *  肩の動作指示
 */
unsigned char controlShoulder(int isUp, unsigned char value)
{
    unsigned char data = value & (255 - 192);

    return ((isUp != 0) ? (data + 128) :  (data + 64));
}

/*
 *  Baseの動作停止
 */
unsigned char stopBase(unsigned char value)
{
    return (value & (255 - 48));
}

/*
 *  Baseの動作指示
 */
unsigned char controlBase(int isTurnLeft, unsigned char value)
{
    unsigned char data = value & (255 - 48);

    return ((isTurnLeft != 0) ? (data + 32) :  (data + 16));
}



/*******************  ここから イベントのハンドリング処理 *******************/

/*
 *  キー入力した
 */
int handleKey(int key)
{
    return (0);
}


/*
 *  線分検出処理
 */
int handleLine(MyConfiguration *config, MyWorkArea *work)
{
    unsigned char data = 255;

    if (work->lineStatus == MY_LINESTATUS_RIGHT_UP)
    {
        data = controlBase(0, data);
    }
    else if (work->lineStatus == MY_LINESTATUS_LEFT_UP)
    {
        data = controlBase(1, data);
    }
    else if (work->lineStatus == MY_LINESTATUS_VERTICAL_LEFT)
    {
        data = controlGripper(0, data);
    }
    else if (work->lineStatus == MY_LINESTATUS_VERTICAL_RIGHT)
    {
        data = controlGripper(0, data);
    }
    else if (work->lineStatus == MY_LINESTATUS_HORIZONTAL_HIGH)
    {
        data = controlGripper(1, data);
    }
    else if (work->lineStatus == MY_LINESTATUS_HORIZONTAL_LOW)
    {
        data = controlGripper(1, data);
    }
    else  // if (work->lineStatus == MY_LINESTATUS_NONE)
    {
        data = stopArm(data);
    }
    output_to_usbio(usbio_device, 0, data);

    return (0);
}
