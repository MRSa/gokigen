/**
 * ------------------------------------------------------------------------------
 *   finish.c  :  アプリケーションの終了処理
 *
 * ------------------------------------------------------------------------------
 */
#include <highgui.h>

#include "mydefine.h"

/*
 *  アプリケーションのあとしまつ
 */
void finish(MyConfiguration *config, MyWorkArea *work)
{
    //    領域を開放する
    cvReleaseImage(&(work->outputImage));
    cvReleaseImage(&(work->grayImage));
    if (work->backImage != NULL)
    {
        cvReleaseImage(&(work->backImage));
    }

    //    キャプチャを解放する
    cvReleaseCapture(&(work->capture));

    //    ウィンドウを破棄する
    cvDestroyWindow(work->mainWindow);
    cvDestroyWindow(work->backWindow);
}
