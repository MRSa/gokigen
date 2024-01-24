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

    sprintf(buffer, "PICKLINE");

    // 背景画の準備
    cvReleaseImage(&work->backScreen);
    work->backScreen = cvCloneImage(work->backImage);

    // フォントの書き込み
    cvInitFont(&font, CV_FONT_HERSHEY_PLAIN, 1,1, 0, 1, 8);
    cvPutText(work->backScreen, buffer, cvPoint(350, 265), &font, CV_RGB(250, 0, 0));

    // メッセージの表示
    if (strlen(work->message) > 0)
    {
        cvPutText(work->backScreen, work->message, cvPoint(350, 247), &font, CV_RGB(250, 0, 0));
    }

    // 背景画を表示する
    cvShowImage(work->backWindow, work->backScreen);
    return;
}
