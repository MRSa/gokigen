/**
 * ------------------------------------------------------------------------------
 *   detectline.c :  線分検出
 *
 * ------------------------------------------------------------------------------
 */
#include <cv.h>
#include <highgui.h>

#include <stdio.h>

#include "mydefine.h"
#include "myproto.h"

/**
 *  画像から線分を検出して表示する
 */
void detectLine(MyConfiguration *config, MyWorkArea *work)
{
    int      diffX, diffY;
    int      diffAbsX, diffAbsY;
    int      centerX, centerY;
    CvSeq   *lines = 0;
    CvPoint *point, pt1, pt2;

    CvScalar lowerValue;
    CvScalar upperValue;

    // 抽出する赤の成分の設定
    lowerValue.val[0] = 2.0;
    lowerValue.val[1] = 2.0;
    lowerValue.val[2] = 70.0;
    lowerValue.val[3] = 0.0;

    upperValue.val[0] = 40.0;   // 40ぐらいまでか？
    upperValue.val[1] = 40.0;   // 40ぐらいまでか？
    upperValue.val[2] = 255.0;
    upperValue.val[3] = 0.0;

    // 赤色成分を抽出する
    cvInRangeS(work->frameImage, lowerValue, upperValue, work->redImage);

    // エッジ検出
    cvCanny(work->redImage, work->edgeImage, 64.0, 128.0, 3);


    // 確率的ハフ変換でラインを検出する
    lines = 0;
    lines = cvHoughLines2(work->edgeImage, work->storage, CV_HOUGH_PROBABILISTIC, 1, CV_PI / 180, config->lineSize, config->lineSize, 20);
    if (lines->total == 0)
    {
        // ラインが検出できなかった...終了する
        work->previousLineStatus = work->lineStatus;
        work->lineStatus = MY_LINESTATUS_NONE;
        work->lineCount  = 0;
        return;
    }

    // 画像の中心座標を取得する
    centerX = (cvGetSize(work->frameImage).width)  / 2;
    centerY = (cvGetSize(work->frameImage).height) / 2;

    // 最初に検出した１本のラインの状態を確認する
    point = (CvPoint *) cvGetSeqElem (lines, 0);

    // 線分の端点座標から差分を確認する
    diffX = point[1].x - point[0].x;
    diffY = point[1].y - point[0].y;

    diffAbsX = abs(diffX);
    diffAbsY = abs(diffY);

    // 縦線と認識
    if (diffAbsX < config->lineSize)
    {
        if (point[0].x < centerX)
        {
            // 画面の左半分に現れている、と認識
            if (work->lineStatus != MY_LINESTATUS_VERTICAL_LEFT)
            {
                work->previousFrameCount = work->lineFrameCount;
                work->lineFrameCount = work->previousFrameCount;
                work->previousLineStatus = (work->lineStatus == MY_LINESTATUS_NONE) ? work->previousLineStatus : work->lineStatus;
                work->lineCount  = 0;
                work->lineStatus = MY_LINESTATUS_VERTICAL_LEFT;
            }
            work->lineCount++;

            pt1.x = 8;
            pt1.y = 0;
            pt2.x = 8;
            pt2.y = 16;
        }
        else // if (point[0].x >= centerX)
        {
            // 画面の右半分に現れている、と認識
            if (work->lineStatus != MY_LINESTATUS_VERTICAL_RIGHT)
            {
                work->previousFrameCount = work->lineFrameCount;
                work->lineFrameCount = work->previousFrameCount;
                work->previousLineStatus = (work->lineStatus == MY_LINESTATUS_NONE) ? work->previousLineStatus : work->lineStatus;
                work->lineCount  = 0;
                work->lineStatus = MY_LINESTATUS_VERTICAL_RIGHT;
            }
            work->lineCount++;

            pt1.x = 12;
            pt1.y = 0;
            pt2.x = 12;
            pt2.y = 16;
        }
    }
    // 横線と認識
    else if (diffAbsY < config->lineSize)
    {
        if (point[0].y < centerY)
        {
            // 画面の上半分に現れている、と認識
            if (work->lineStatus != MY_LINESTATUS_HORIZONTAL_HIGH)
            {
                work->previousFrameCount = work->lineFrameCount;
                work->lineFrameCount = work->previousFrameCount;
                work->previousLineStatus = (work->lineStatus == MY_LINESTATUS_NONE) ? work->previousLineStatus : work->lineStatus;
                work->lineCount  = 0;
                work->lineStatus = MY_LINESTATUS_HORIZONTAL_HIGH;
            }
            work->lineCount++;

            pt1.x = 0;
            pt1.y = 4;
            pt2.x = 16;
            pt2.y = 4;
    }
        else // if (point[0].y >= centerY)
        {
            // 画面の下半分に現れている、と認識
            if (work->lineStatus != MY_LINESTATUS_HORIZONTAL_LOW)
            {
                work->previousFrameCount = work->lineFrameCount;
                work->lineFrameCount = work->previousFrameCount;
                work->previousLineStatus = (work->lineStatus == MY_LINESTATUS_NONE) ? work->previousLineStatus : work->lineStatus;
                work->lineCount  = 0;
                work->lineStatus = MY_LINESTATUS_HORIZONTAL_LOW;
            }
            work->lineCount++;

            pt1.x = 0;
            pt1.y = 12;
            pt2.x = 16;
            pt2.y = 12;
    }
    }
    // ななめ線と認識(左上→右下)
    else if (((diffX > 0)&&(diffY > 0))||((diffX < 0)&&(diffY < 0)))
    {
        if (work->lineStatus != MY_LINESTATUS_LEFT_UP)
        {
            work->previousFrameCount = work->lineFrameCount;
            work->lineFrameCount = work->previousFrameCount;
            work->previousLineStatus = (work->lineStatus == MY_LINESTATUS_NONE) ? work->previousLineStatus : work->lineStatus;
            work->lineCount  = 0;
            work->lineStatus = MY_LINESTATUS_LEFT_UP;
        }
        work->lineCount++;

        pt1.x = 0;
        pt1.y = 0;
        pt2.x = 16;
        pt2.y = 16;
    }
    // ななめ線と認識(右上→左下)
    else //
    {
        if (work->lineStatus != MY_LINESTATUS_RIGHT_UP)
        {
            work->previousFrameCount = work->lineFrameCount;
            work->lineFrameCount = work->previousFrameCount;
            work->previousLineStatus = (work->lineStatus == MY_LINESTATUS_NONE) ? work->previousLineStatus : work->lineStatus;
            work->lineCount  = 0;
            work->lineStatus = MY_LINESTATUS_RIGHT_UP;
        }
        work->lineCount++;

        pt1.x = 16;
        pt1.y = 0;
        pt2.x = 0;
        pt2.y = 16;
    } 
    cvLine (work->frameImage, pt1, pt2, CV_RGB (0, 255, 0), 1, 8, 0);

    return;
}
