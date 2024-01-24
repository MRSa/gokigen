/**
 * ------------------------------------------------------------------------------
 *   finish.c  :  アプリケーションの終了処理
 *
 * ------------------------------------------------------------------------------
 */
#include <highgui.h>

#include "mydefine.h"
#include "myproto.h"

/*
 *  アプリケーションのあとしまつ
 */
void finish(MyConfiguration *config, MyWorkArea *work)
{
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

    // 音楽ファイルリストを解放する
    releaseFileList(numberOfPlayingFiles, &playingFileList);
}
