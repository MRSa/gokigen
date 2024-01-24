/* ------------------------------------------------------------------------------
 *  VIDEO CAPTURE PROGRAM (OpenCVを利用した、ちょっとしたテストプログラム)
 *
 *
 *    === 説明 ===
 *       本プログラムは、接続されたカメラの画像を指定された加工を行い表示する
 *     プログラムです。表示している画像をファイル保存することもできます。
 *
 *       bgFile.jpg というファイルが存在すれば、別ウィンドウで表示し、現在の
 *     加工モードを表示することができます。
 *
 *
 *    === 起動オプション ===
 *
 *      -hqvga  160x120 で画像キャプチャを行います
 *      -qvga   320x240 で画像キャプチャます  デフォルト表示です
 *      -vga    640x240 で画像キャプチャを行います
 *
 *
 *    === 本プログラムで受け付けるキー操作 ===
 *
 *        n : 通常画像
 *        g : グレースケール画像
 *        e : エッジ検出画像
 *        v : エッジ検出/単色カラー成分抽出
 *        b : 2値表示画像
 *        l : 赤色線分の検出
 *        o : 輪郭抽出画像
 *        p : 画像差分検出モード (まだ作成中)
 *        m : 単色カラー抽出モード (まだ作成中)
 *        
 *        . : エッジ検出-2 の検出方法変更 (Laplace/Sobel)
 *
 *        u : 2値表示のしきい値変更 (+10)
 *        d : 2値表示のしきい値変更 (-10)
 *
 *        c : 表示している画像を保存 (ファイル名は "年-月-日-時分秒.jpg")
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
