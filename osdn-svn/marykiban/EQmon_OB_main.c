//=========================================================
// LPC1114 Project （EQmon : OBで利用するよ版）
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
#include "oled.h"
#include "systick.h"

//-----------------------
// Main Routine
//-----------------------
int main(void)
{
    uint32_t port;
    uint32_t tick_curr;
    uint32_t tick_scene_intv;
    uint32_t tick_scene_intv_prev = 0;
    uint32_t tick_count = 0;

    //
    // 初期化
    //
    Init_SysTick();
    Init_Array_COM();
    Array_COM_ID_Assignment();
    Init_Color_LED();
    Init_OLED();
    Init_MEMS();

    // OLED画面をクリアする
    OLED_Clear_Screen(OLED_BLK);

    // OLED画面にアプリタイトル(EQmon)を表示する
    OLED_printf_Font(OLED_FONT_SMALL);
    OLED_printf_Position(0, 0);
    OLED_printf_Color(OLED_RED, OLED_BLK);
    OLED_printf("EQmon");

    // <MEMS> というタイトルを表示
    OLED_printf_Font(OLED_FONT_SMALL);
    OLED_printf_Position(0, 4);
    OLED_printf_Color(OLED_GRN, OLED_BLK);
    OLED_printf("<MEMS>\n");

    //
    // Main Loop
    //
    while(1)
    {
        // MEMSデータの記憶用変数を記憶。
        static int8_t mems[3];

        // Update Interval
        tick_curr = Get_Ticks();
        tick_scene_intv = tick_curr - tick_scene_intv_prev;
        if (tick_scene_intv > 9) // ≒ 0.1secおき(0.09sec)
        {
            // 現在tick timeを記憶
            tick_scene_intv_prev = tick_curr;
            tick_count = tick_count + 1;

            // MEMS状態を取得
            mems[0] = MEMS_Get_X();
            mems[1] = MEMS_Get_Y();
            mems[2] = MEMS_Get_Z();

            // 加速度情報を他のポートに出力する
            for (port = 0; port < 4; port++)
            {
                if (Array_COM_Port_Open(port))
                {
                    Array_COM_Tx_Multi_Bytes(port, (uint8_t*) &mems, sizeof(mems));
                }
            }

            // カウンタの表示
            OLED_printf_Position(0, 1);
            OLED_printf_Color(OLED_YEL, OLED_BLK);
            OLED_printf("Cnt: ");
            OLED_printf_Color(OLED_CYN, OLED_BLK);
            OLED_printf("0x%08x\n\n", tick_count);

            OLED_printf_Position(0, 5);

            // X軸データの画面表示
            OLED_printf_Color(OLED_YEL, OLED_BLK);
            OLED_printf("X=");
            OLED_printf_Color(OLED_CYN, OLED_BLK);
            OLED_printf("%4d\n", (int32_t) mems[0]);

            // Y軸データの画面表示
            OLED_printf_Color(OLED_YEL, OLED_BLK);
            OLED_printf("Y=");
            OLED_printf_Color(OLED_CYN, OLED_BLK);
            OLED_printf("%4d\n", (int32_t) mems[1]);

            // Z軸データの画面表示
            OLED_printf_Color(OLED_YEL, OLED_BLK);
            OLED_printf("Z=");
            OLED_printf_Color(OLED_CYN, OLED_BLK);
            OLED_printf("%4d\n\n", (int32_t) mems[2]);
        }
    }
    return 0;
}

//=========================================================
// End of Program
//=========================================================
