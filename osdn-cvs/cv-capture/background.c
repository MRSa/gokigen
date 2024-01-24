/**
 * ------------------------------------------------------------------------------
 *   background.c :  背景ウィンドウを設定する
 *
 * ------------------------------------------------------------------------------
 */
#include <stdio.h>
#include <cv.h>
#include <highgui.h>
#include "mydefine.h"

/**
 *  背景画の表示
 */
void setBackground(char *backImageFileName, MyWorkArea *work)
{
    // 背景が指定されていない場合、そのまま戻る
    if (backImageFileName == NULL)
    {
        return;
    }

    // 画像の読み込み
    work->backImage = cvLoadImage(backImageFileName, CV_LOAD_IMAGE_COLOR);
    if (work->backImage != NULL)
    {
        // 背景画をコピーする
        work->backScreen = cvCloneImage(work->backImage);

        // 画像が読めたら、表示する
        cvShowImage(work->backWindow, work->backImage);
    }

    return;
}

/**
 *  背景画の表示
 */
void updateBackground(MyConfiguration *config, MyWorkArea *work)
{
    CvFont     font;              // フォント用領域
    char       buffer[40];

    if (work->backImage == NULL)
    {
        // 背景画がなければ表示しない
        return;
    }

    switch (config->showMode)
    {
      case MY_SHOWMODE_GRAY:
        // グレー表示
        sprintf(buffer, "GRAY");
        break;

      case MY_SHOWMODE_EDGE:
        // エッジ表示
        sprintf(buffer, "EDGE");
        break;

      case MY_SHOWMODE_VISION:
        // 加工表示
        sprintf(buffer, "VISION");
        break;

      case MY_SHOWMODE_BINARY:
        // ２値表示
        sprintf(buffer, "BINARY : %d", work->binLevel);
        break;

      case MY_SHOWMODE_NORMAL:
      default:
        // カラー表示
        sprintf(buffer, "NORMAL");
        break;
    }

    if (work->doCapture == MY_TRUE)
    {
        // 画面キャプチャの実施
        sprintf(buffer, "CAPTURED");
    }

    // 背景画の準備
    cvReleaseImage(&work->backScreen);
    work->backScreen = cvCloneImage(work->backImage);


    // フォントの書き込み
    cvInitFont(&font, CV_FONT_HERSHEY_PLAIN, 1,1, 0, 1, 8);
    cvPutText(work->backScreen, buffer, cvPoint(350, 265), &font, CV_RGB(250, 0, 0));

    // 背景画を表示する
    cvShowImage(work->backWindow, work->backScreen);
    return;
}
