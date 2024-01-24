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
    config->lineSize   = 48;

    // ワーク領域の初期化
    memset(work, 0x00, sizeof(MyWorkArea));

    // ウィンドウ名称を記録する
    work->mainWindow = mainWindow;
    work->backWindow = backWindow;

}


/*
 *  プログラムの初期化
 */
int doInitialize(MyConfiguration *config, MyWorkArea *work)
{

    // 音楽ファイル名が指定されていなかった場合...
    if (strlen(config->dataFileName) <= 0)
    {
        printf("ERR>PLAYING FILE NAME IS NOT SPECIFIED.\n");
        return (MY_FALSE);
    }

    // 音楽ファイルリストを読み出す
    numberOfPlayingFiles = parseFileList(config->dataFileName, &playingFileList);
    if (numberOfPlayingFiles < 0)
    {
        // メモリの確保に失敗した場合...
        printf("ERR>MEMORY ALLOCATION ERROR ('%s')\n", config->dataFileName);
        return (MY_FALSE);
    }
    if (numberOfPlayingFiles == 0)
    {
        // ファイル名がひとつも記述されていなかった場合...
        printf("ERR>Cannot find any playing file in '%s'.\n", config->dataFileName);
        releaseFileList(numberOfPlayingFiles, &playingFileList);
        return (MY_FALSE);
    }

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
    work->edgeImage     = cvCreateImage(cvGetSize(work->frameImage), IPL_DEPTH_8U, 1);
    work->redImage      = cvCreateImage(cvGetSize(work->frameImage), IPL_DEPTH_8U, 1);

    work->storage = cvCreateMemStorage(0);

    return (MY_TRUE);
}
