/**--------------------------------------*/
/**  OpenGL ES 動作実験用のサンプルプログラム  */
/**--------------------------------------*/
#include <stdio.h>
#include <curses.h>

/* preparation.c */
extern int prepareOpenGL(void); /* OpenGL ES利用のための初期化処理 */
extern void finishOpenGL(void); /* OpenGL ESの利用終了の処理 */

/* drawer.c */
extern int startDrawObject(void);     /* 初回の描画処理      */
extern int drawObject(char command);  /* キー入力後の描画処理 */
extern void finishDrawObject(void);   /* 終了前の描画処理    */

/**
 *  メイン処理
 *    - サンプルプログラムなので、初期化処理後、描画処理をループさせ、
 *     終了処理を走らせるだけの単純な処理。
 *
 *    - キー入力データを描画処理部に渡すので、押されたキーを
 *     （描画処理部が）判断し、プログラムの終了タイミングを検出する。
 */
int main(int argc, char *argv[])
{
    int ret = 0;
    int key = 0;

    /* OpenGL ESの初期化処理 */
    ret = prepareOpenGL();
    if (ret < 0)
    {
        fprintf(stderr, "ERR>OpenGL ES initialize failure.\n");
        return (ret);
    }

    /* initialize libncurses */
    initscr();
    cbreak();
    noecho();

    /**********************************************/
    /* 描画処理(メインループ)...終了判定は描画処理側で実施 */
    /**********************************************/
    ret = startDrawObject();
    while (ret >= 0)
    {
        key = getch();
        ret = drawObject((char) key & 0xff);
    }
    finishDrawObject();
    /**********************************************/

    /* finish libncurses */
    endwin();

    /* OpenGL ESの後処理 */
    finishOpenGL();

    return (0);
}
