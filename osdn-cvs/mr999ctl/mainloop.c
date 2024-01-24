/**
 * ------------------------------------------------------------------------------
 *   mainloop.c  :  メイン処理ループ
 *
 * ------------------------------------------------------------------------------
 */
#include <cv.h>
#include <highgui.h>

#include "mydefine.h"
#include "myproto.h"


/*
 *  メイン処理
 *
 */
void mainLoop(MyConfiguration *config, MyWorkArea *work)
{
    // キー入力用の変数
    int key   = 0;

    work->fCount = 31;

    //    メインループ
    while (1)
    {
        // カウントアップ
        (work->fCount)++;
     
        // カメラからの入力画像1フレームをframeImageに格納する
        work->frameImage = cvQueryFrame(work->capture);
        if (work->frameImage != NULL)
        {
            // フレームカウントをカウントアップ
            (work->frameCount)++;

            // 線分検出
            detectLine(config, work);

            // 線分の検出後処理
            if (handleLine(config, work) < 0)
            {
               return;
            }

            //  背景画を表示する (32回に1回)
            if ((work->fCount % 32) == 0)
            {
                updateBackground(config, work);
            }

            // 画像を表示する
            cvShowImage(work->mainWindow, work->frameImage);
	}

        // キー入力をチェックする
        key = cvWaitKey(10);
        if (key > 0)
        {
            if (key == 'q') 
            {
                // qキー : メインループを抜ける
                return;
            }
            if (handleKey(key) < 0)
            {
                // 負の値が帰ってくると : メインループを抜ける
                return;
            }
        }
    }  // メインループの終わり
    return;
}
