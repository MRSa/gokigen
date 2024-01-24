//=========================================================
// LPC1114 Project （EQmon --- XBでログ記録するよ版）
//=========================================================
// File Name : main.c
// Function  : Main Routine
//---------------------------------------------------------
// Rev.01 2010.08.01 Munetomo Maruyama
//---------------------------------------------------------
// Copyright (C) 2010-2011 Munetomo Maruyama
//=========================================================
// ---- License Information -------------------------------
// Anyone can FREELY use this code fully or partially
// under conditions shown below.
// 1. You may use this code only for individual purpose,
//    and educational purpose.
//    Do not use this code for business even if partially.
// 2. You should use this code under the GNU GPL.
// 3. You should remain this header text in your codes
//   including Copyright credit and License Information.
// 4. Your codes should inherit this license information.
//=========================================================
// ---- Patent Notice -------------------------------------
// I have not cared whether this system (hw + sw) causes
// infringement on the patent, copyright, trademark,
// or trade secret rights of others. You have all
// responsibilities for determining if your designs
// and products infringe on the intellectual property
// rights of others, when you use technical information
// included in this system for your business.
//=========================================================
// ---- Disclaimers ---------------------------------------
// The function and reliability of this system are not
// guaranteed. They may cause any damages to loss of
// properties, data, money, profits, life, or business.
// By adopting this system even partially, you assume
// all responsibility for its use.
//=========================================================

#ifdef __USE_CMSIS
#include "LPC11xx.h"
#endif

#include "color_led.h"
#include "array_com.h"
#include "mems.h"
#include "systick.h"
#include "uart.h"
#include "pff.h"
#include "diskio.h"

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    このプログラムについて
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//    OBの加速度センサの値を、XBのtick timeを付加して、XBEE およびメモリカード内の
//   ファイル(logdata.dat)へ出力する。
//   ファイルの末尾まで記録すると、ファイルの先頭記録位置に戻り、記録を継続する。
//
//  - XBEEから、Q または q が入力されると、ロギングを終了してファイルをクローズする。
//
//  - メモリカード書き込みエラー、またはロギングが終了しているときには、LEDを光らせて
//   知らせる。
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//  [注意事項]
//   あらかじめmicroSDカードに logdata.dat を作成しておく必要がある。
//  ファイルサイズは、512の倍数にする必要がある。(最小ファイルサイズは1536バイト)
//  (ファイル生成ができない制約があるため、ひとつのファイルをずっと使いまわす。)
//
//   > サンプルの logdata.dat は、88,531,456bytes (172913×512bytes)で使用している。
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//　[ログフォーマット]
//      ・先頭 1024バイト : ヘッダ部（未使用）
//      ・1025バイト以降  : データレコード
//
//         データ1件は、"78 56 34 12 xx yy zz 00"、バイナリデータ。
//             ・123456 : tick time (リトルエンディアン)
//             ・xx : X軸加速度 (-128～127)
//             ・yy : y軸加速度 (-128～127)
//             ・zz : z軸加速度 (-128～127)
//             ・00 : 固定データ (ゼロ)
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

//-----------------------
// Main Routine
//-----------------------
int main(void)
{
    uint8_t  character;
    uint8_t  res;
    uint32_t port;
    uint32_t got;

    uint32_t tickTime;
    uint32_t recordNumber;
    uint32_t count;
    uint8_t receiveBuffer[16];
    int8_t *memsValue;

    FRESULT result;
    WORD    dataToWrite;
    WORD    dataToWritten;
    static FATFS fatfs;

    //
    // 初期化
    //
    Init_SysTick();
    Init_Array_COM();
    Array_COM_ID_Assignment();
    Init_Color_LED();
    UARTInit(9600);

    // microSDカードの初期化
    res = disk_initialize();
    if (res == 0)
    {
        // ファイル(lodata.dat)のオープン
        result = pf_mount(&fatfs);
        if (result == FR_OK)
        {
            result = pf_open("logdata.dat");
        }
        if (result == FR_OK)
        {
            // 書き込みデータ、ヘッダ部は飛ばしたところに移動
            result = pf_lseek(1024);
        }
        if (result != FR_OK)
        {
            // データファイルのオープンに失敗した...
            res = 11;
        }
        recordNumber = 0;
        dataToWrite = 8;
    }

    //
    // Main Loop
    //
    while(1)
    {
        // MBに付いている LEDの点灯を実行するやつ
        if (res != 0)
        {
            // microSDのアクセスに失敗した場合には、LEDを点灯させる
            Draw_Color_LED();
        }

        //
        // 隣のArrayからデータがきていたら、データを格納する
        got = 0;
        for (port = 0; port < 4; port++)
        {
            if (Array_COM_Port_Open(port))
            {
                if ((count = Array_COM_Rx_Multi_Bytes(port, (receiveBuffer + 4), 0)) > 0)
                {
                    // 先頭に tick時間を入れる
                    got = 1;
                    tickTime = Get_Ticks();
                    receiveBuffer[0] = ((uint8_t *)&tickTime)[0];
                    receiveBuffer[1] = ((uint8_t *)&tickTime)[1];
                    receiveBuffer[2] = ((uint8_t *)&tickTime)[2];
                    receiveBuffer[3] = ((uint8_t *)&tickTime)[3];

                    memsValue = (int8_t*) (receiveBuffer + 4);
                }
            }
        }

        // データを受信していた場合！
        if (got == 1)
        {
            got = 0;
            if (res == 0)
            {
                memsValue[3] = 0;

                // ファイルに書く
                result = pf_write(receiveBuffer, dataToWrite, &dataToWritten);
                if (dataToWrite > dataToWritten)
                {
                    // ファイルの末尾まで書き出した...レコードの先頭に戻ってから、ファイルに書き出す
                    pf_lseek(1024);
                    result = pf_write(receiveBuffer, dataToWrite, &dataToWritten);
                }
                if (result != FR_OK)
                {
                    // データの書き出しに失敗した...
                    res = 22;
                }
            }
            // シリアルポートにデータを送信
            UART_printf("0x%08x,%d,%d,%d\r\n", tickTime, memsValue[0], memsValue[1], memsValue[2]);
        }

        // 端末から受信データがあるか？
        if (UARTReceive_Check())
        {
            character = (BYTE) UARTReceive_Byte();

            // ロギング終了指示が来ていたとき...
            if ((character == 'q')||(character == 'Q'))
            {
                // 書き込みデータをフラッシュさせる
                if (res == 0)
                {
                    pf_write(0, 0 , &dataToWritten);
                }
                //　ロギングが終了したことを知らせる
                UART_printf("\r\n\r\n DATA LOGGING FINISHED \r\n\r\n");
                break;
            }
        }
    }

    while(1)
    {
        // データロギング終了時には、LEDを点灯させる
        Draw_Color_LED();
    }
    return 0;
}

//=========================================================
// End of Program
//=========================================================
