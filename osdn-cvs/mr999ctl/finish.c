/**
 * ------------------------------------------------------------------------------
 *   finish.c  :  アプリケーションの終了処理
 *
 * ------------------------------------------------------------------------------
 */
#include <highgui.h>

#include "mydefine.h"

#include "io_usbio.h"
extern struct usb_dev_handle *usbio_device;

/*
 *  アプリケーションのあとしまつ
 */
void finish(MyConfiguration *config, MyWorkArea *work)
{
    // USB-IOデバイスの後始末
    if (usbio_device != NULL)
    {
        close_morphy(usbio_device);
    }

    //    ワーク領域を開放する
    cvReleaseMemStorage (&work->storage);

    //    領域を開放する
    cvReleaseImage(&(work->edgeImage));
    cvReleaseImage(&(work->redImage));
    cvReleaseImage(&(work->backImage));

    //    キャプチャを解放する
    cvReleaseCapture(&(work->capture));

    //    ウィンドウを破棄する
    cvDestroyWindow(work->mainWindow);
    cvDestroyWindow(work->backWindow);

}
