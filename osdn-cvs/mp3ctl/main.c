/* ------------------------------------------------------------------------------
 *  MPG123再生フロントエンドプログラム (OpenCVを利用した、ちょっとしたテストプログラム)
 *
 *
 *    === 説明 ===
 *       本プログラムは、接続されたカメラの画像を使い、赤線を検出してmpg123へ
 *     音楽ファイルの再生指示を送るプログラムです。
 *
 *       本プログラムの実行には、カメラおよび mpg123 のインストールが必要です。
 *     また、mpg123で再生する音楽ファイルリスト(テキストファイル)が必要です
 *
 *       なお、bgFile.jpg というファイルが存在すれば、別ウィンドウで表示し、
 *     現在の再生状況を表示することができます。
 *
 *
 *    === 利用方法 ===
 *
 *      mpg123 [起動オプション] 音楽ファイルリスト
 *       ※ 音楽ファイルリストは、1行に１つの音楽ファイルのパスを書いた
 *         テキストファイルです。
 *
 *    === 起動オプション ===
 *
 *      -hqvga  160x120 で画像キャプチャを行います
 *      -qvga   320x240 で画像キャプチャます  デフォルト表示です
 *      -vga    640x240 で画像キャプチャを行います
 *
 *
 *    === 本プログラムで受け付ける赤ライン状態 ===
 *
 *      (赤線なし) : 音楽再生停止
 *      縦線       : 通常再生
 *      横線       : リピート再生 (同じ曲を繰り返し再生します)
 *      左上→右下 : 1曲前の曲を再生
 *      右上>>左下 : 1曲後の曲を再生 
 *
 *
 *
 *
 *    === 本プログラムで受け付けるキー操作 ===
 *
 *        r : 再生曲リストをリセット (1曲めから)
 *
 *        q : アプリを終了させる
 *  ------------------------------------------------------------------------------
 */
#include <stdio.h>
#include <highgui.h>
#include "mydefine.h"
#include "myproto.h"

/**
 *  メイン処理...
 */
int main(int argc, char **argv)
{
    MyConfiguration  config;                      //  アプリケーション設定情報
    MyWorkArea       workArea;                    //  ワークエリア

    char windowNameCapture[]    = "Capture";      // キャプチャした画像を表示するウィンドウの名前
    char windowNameBackground[] = "Background";   // 背景画像を表示するウィンドウの名前
    char backgroundFileName[]   = "bgFile.jpg";   // 背景画像のファイル名

    // 準備する
    prepare(windowNameCapture, windowNameBackground, &config, &workArea);

    // 引数のチェックを実行、設定値に反映させる
    parseOption(argc, argv, &config);

    // ウィンドウを生成する (背景ウィンドウ)
    cvNamedWindow(workArea.backWindow, CV_WINDOW_AUTOSIZE);

    // ウィンドウを生成する (キャプチャウィンドウ)
    cvNamedWindow(workArea.mainWindow, CV_WINDOW_AUTOSIZE);

    // 背景画を設定する
    setBackground(backgroundFileName, &workArea);

    //    プログラムを初期化する
    if (doInitialize(&config, &workArea) == MY_FALSE)
    {
        printf( "ERR>PROGRAM ABORT.\n" );
        return (-1);
    }

    // メインループ
    mainLoop(&config, &workArea);

    // あとしまつ
    finish(&config, &workArea);

    return (0);
}
