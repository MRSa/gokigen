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
    unsigned long vanishedCheckCount = 0;

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

            //  背景画を表示する (32回に1回)
            if ((work->fCount % 32) == 0)
            {
                updateBackground(config, work);
            }

            // 画像を表示する
            cvShowImage(work->mainWindow, work->frameImage);
        }

        // 音楽再生中の処理
        if (currentPlayingStatus != 0)
        {
            if (work->lineStatus == MY_LINESTATUS_NONE)
            {
                if (vanishedCheckCount + 8 <= work->frameCount)
                {
                    // 再生中にラインが8フレーム検出できなかった場合には音楽再生を停止する
                    stop_playing_music();
                }
                work->fCount = 31;
            }
            else if (work->lineCount == 8)
            {
                if (work->lineStatus == MY_LINESTATUS_RIGHT_UP)
                {
                    // 再生中に右上がりのラインを8フレーム検出した場合には、1曲先に送る
                    stop_playing_music();
                    work->fCount = 31;
                }
                else if (work->lineStatus == MY_LINESTATUS_LEFT_UP)
                {
                    // 再生中に左上がりのラインを8フレーム検出した場合には、1曲前に戻す
                    currentPlayingFile--;
                    if (currentPlayingFile < 0)
                    {
                        // 末尾の曲にする
                        currentPlayingFile = numberOfPlayingFiles - 1;
                    }

                    stop_playing_music();
                    work->fCount = 31;
                }
                vanishedCheckCount = work->frameCount;
            }
            else
            {
                vanishedCheckCount = work->frameCount;
            }
        }
        else    //  音楽の再生が停止しているときの処理
        {
            //  音楽の再生を開始するかどうかのチェック (ラインが検出されていれば音楽再生)
            if  (work->lineStatus == MY_LINESTATUS_NONE)
            {
                // ラインがない場合には、何もしない
            }
            else if ((work->lineStatus == MY_LINESTATUS_HORIZONTAL_LOW)||(work->lineStatus == MY_LINESTATUS_HORIZONTAL_HIGH)||
                     (work->lineStatus == MY_LINESTATUS_LEFT_UP))
            {
                // リピート再生(一つ前再生)を実施する
                currentPlayingFile--;
                if (currentPlayingFile < 0)
                {
                    // 最後の曲にする
                    currentPlayingFile = numberOfPlayingFiles - 1;
                }

                // 音楽の再生を開始する
                start_play_music(playingFileList[currentPlayingFile]);

                // 次の曲のファイル名を決定する
                currentPlayingFile++;
                if (currentPlayingFile >= numberOfPlayingFiles)
                {
                    // 最後まで再生が行われていた場合には、先頭から音楽を再生する
                    currentPlayingFile = 0;
                }
                work->fCount = 31;
            }
            else // if  ((work->lineStatus != MY_LINESTATUS_VERTICAL_LEFT)||(work->lineStatus != MY_LINESTATUS_VERTICAL_RIGHT)
                 //     (work->lineStatus == MY_LINESTATUS_RIGHT_UP)||(work->lineStatus == MY_LINESTATUS_LEFT_UP))
            {
                // 音楽の再生を開始する
                start_play_music(playingFileList[currentPlayingFile]);

                // 次の曲のファイル名を決定する
                currentPlayingFile++;
                if (currentPlayingFile >= numberOfPlayingFiles)
                {
                    // 最後まで再生が行われていた場合には、先頭から音楽を再生する
                    currentPlayingFile = 0;
                }
                work->fCount = 31;
            }
        }

        // キー入力をチェックする
        key = cvWaitKey(10);
        if (key > 0)
        {
            if (key == 'r') 
            {
                // rキー : 再生曲を先頭にする
                currentPlayingFile = 0;
                return;
            }
            if (key == 'q') 
            {
                // qキー : 音を止めて、メインループを抜ける
                stop_playing_music();
                return;
            }
        }
    }  // メインループの終わり

    //  音楽の再生停止
    stop_playing_music();

    return;
}
