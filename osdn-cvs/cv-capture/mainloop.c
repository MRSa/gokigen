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

// 加工表示(プロトタイプ)
void convertVision(IplImage *source, IplImage *destination);

/*
 *  メイン処理
 *
 */
void mainLoop(MyConfiguration *config, MyWorkArea *work)
{
   int key   = 0;         // キー入力用の変数

   // 2値データの初期しきい値
   work->binLevel = 120;  

   // 表示領域を設定する
   work->screenImage = work->frameImage;

    //    メインループ
    while (1)
    {
        (work->fCount)++;      // カウントアップ

        //    カメラからの入力画像1フレームをframeImageに格納する
        work->frameImage = cvQueryFrame(work->capture);
        if (work->frameImage != NULL)
        {
            // frameImageにデータに格納できた場合...
            switch (config->showMode)
            {
              case MY_SHOWMODE_GRAY:
                // グレイスケールに変換する
                cvCvtColor(work->frameImage, work->grayImage, CV_BGR2GRAY);
                break;

              case MY_SHOWMODE_EDGE:
                // グレイスケールに変換後、エッジの検出を行う
                cvCvtColor(work->frameImage, work->grayImage, CV_BGR2GRAY);
		cvCanny(work->grayImage, work->outputImage, 64.0, 128.0, 3);
                break;

              case MY_SHOWMODE_BINARY:
                // 2値変換
                cvCvtColor(work->frameImage, work->grayImage, CV_BGR2GRAY);
                cvThreshold(work->grayImage, work->outputImage, work->binLevel, 255.0, CV_THRESH_BINARY);
                break;

              case MY_SHOWMODE_VISION:
                // 画像加工の表示
                convertVision(work->frameImage, work->outputImage);
                break;

              case MY_SHOWMODE_NORMAL:
              default:
                // カメラでキャプチャした画像をそのまま使う
                break;
            }

            //  背景画を表示する (32回に1回)
            if ((work->fCount % 32) == 0)
            {
                updateBackground(config, work);
            }

            // 画像を表示する
            cvShowImage(work->mainWindow, work->screenImage);
            if (work->doCapture == MY_TRUE)
            {
                saveImageToFile(work->screenImage);
                work->doCapture = MY_FALSE;
            }
	}

        // キー入力をチェックする
        key = cvWaitKey(10);
        if (key > 0)
        {
            if (key == 'n') 
            {
                // nキー : 標準画像モードに切り替える
                config->showMode = MY_SHOWMODE_NORMAL;
                work->screenImage = work->frameImage;
                work->binLevel = 120;
                work->fCount = 31;
            }
            else if (key == 'g') 
            {
                // gキー : グレー画像モードに切り替える
                config->showMode = MY_SHOWMODE_GRAY;
                work->screenImage = work->grayImage;
                work->fCount = 31;
            }
            else if (key == 'e') 
            {
                // eキー : エッジ画像モードに切り替える
                config->showMode = MY_SHOWMODE_EDGE;
                work->screenImage = work->outputImage;
                work->fCount = 31;
            }
            else if (key == 'b') 
            {
                // bキー : 二値画像モードに切り替える
                config->showMode = MY_SHOWMODE_BINARY;
                work->screenImage = work->outputImage;
                work->fCount = 31;
            }
            else if (key == 'v') 
            {
                // vキー : 加工表示モードに切り替える
                config->showMode = MY_SHOWMODE_VISION;
                work->screenImage = work->outputImage;
                work->fCount = 31;
            }
            else if (key == 'c') 
            {
                // cキー : 画像キャプチャを実行する
                work->doCapture = MY_TRUE;
                work->fCount = 31;
            }
            else if (key == 'u') 
            {
                // uキー : 2値化レベルを変更する
                work->binLevel = work->binLevel + 10;
                if (work->binLevel > 250)
                {
                    work->binLevel = 10;
                }
                work->fCount = 31;
            }
            else if (key == 'd') 
            {
                // dキー : 2値化レベルを変更する
                work->binLevel = work->binLevel - 10;
                if (work->binLevel < 10)
                {
                    work->binLevel = 250;
                }
                work->fCount = 31;
            }
            else if (key == 'q') 
            {
                // qキー : メインループを抜ける
                return;
            }
        }
    }  // メインループの終わり
    return;
}

/*
 *  画素を加工する
 */
void convertVision(IplImage *source, IplImage *destination)
{
    int x, y;
    CvScalar s;

    double i, j, r, g, b;

    // 画素単位で色を変換する
    for (y = 0; y < source->height; y++)
    {
        for (x = 0; x < source->width; x++)
        {
            // 画素値を取得
            s = cvGet2D(source, y, x);
/*
            // 輝度に変換
            i = ( s.val[ 2 ] * 0.299 + s.val[ 1 ] * 0.587 + s.val[ 0 ] * 0.114 ) / 255.0;

            j = i - 0.5;
            
            // 赤成分
            r = j > 0 ? j * j * 4 : 0;

            // 緑成分
            g = i * i;

            // 青成分
            b = j > 0 ? j * j * 4 * 0.8 : 0;

            s.val[ 0 ] = b * 255.0;
            s.val[ 1 ] = g * 255.0;
            s.val[ 2 ] = r * 255.0;
*/
            // 画像に書き込み
            cvSet2D(destination, y, x, s);
        }
    }
    return;
}
