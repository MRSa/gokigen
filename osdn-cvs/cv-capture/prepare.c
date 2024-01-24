/**
 * ------------------------------------------------------------------------------
 *   prepare.c  :  アプリケーションの初期化処理
 *                (ローカル変数の初期化と、プログラムの初期化)
 * ------------------------------------------------------------------------------
 */
#include <stdio.h>
#include <highgui.h>

#include "mydefine.h"
#include "myproto.h"

/*
 *  ローカル変数の初期化
 */
void prepare(char *mainWindow, char*backWindow, MyConfiguration *config, MyWorkArea *work)
{
    // 設定の初期化
    memset(config, 0x00, sizeof(MyConfiguration));
    config->cameraSize = MY_CAMERASIZE_QVGA;
    config->showMode   = MY_SHOWMODE_NORMAL;

    // ワーク領域の初期化
    memset(work, 0x00, sizeof(MyWorkArea));
    work->doCapture = MY_FALSE;

    // ウィンドウ名称を記録する
    work->mainWindow = mainWindow;
    work->backWindow = backWindow;

}


/*
 *  プログラムの初期化
 */
int doInitialize(MyConfiguration *config, MyWorkArea *work)
{
    //    カメラを初期化する
    work->capture = cvCreateCameraCapture(0);
    if (work->capture == NULL) 
    {
        //    カメラが見つからなかった場合
        printf( "ERR>Cannot find any camera device.\n" );
        return (MY_FALSE);
    }

    // 取り込む画像サイズを設定する
    setCameraCaptureSize(work->capture, config->cameraSize);

    // 1枚目の画像を取得する
    work->frameImage = NULL;
    while (work->frameImage == NULL)
    {
        work->frameImage  = cvQueryFrame(work->capture);
    }
    // 画像サイズを設定する
    work->grayImage = cvCreateImage(cvGetSize(work->frameImage), IPL_DEPTH_8U, 1);
    work->outputImage = cvCreateImage(cvGetSize(work->frameImage), IPL_DEPTH_8U, 1);

    return (MY_TRUE);
}
