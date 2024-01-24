#include <stdio.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

/** preparation.c **/
extern EGLDisplay egl_disp;
extern EGLSurface egl_surface;


/** shader.c **/
extern GLuint g_hShaderProgram;
extern GLuint g_VertexLoc;
extern GLuint g_ColorLoc;
extern GLuint g_MVPMLoc;


extern int prepareShaderProgram(void);


/** プロトタイプ宣言 **/
int  startDrawObject(void);
int  drawObject(char command);
void finishDrawObject(void);
int  checkFinish(char command);


/**
 *  描画処理の前処理 (シェーダプログラムの登録、とか)
 */
int startDrawObject(void)
{
    int ret = 0;

    /* 初期設定 */
    glClearColor(0.3, 0.3, 1.0, 0.0);
    glEnable(GL_DEPTH_TEST);
    glDisable(GL_CULL_FACE);

  
#if 0
    /* 光源の初期設定 */
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    glLightfv(GL_LIGHT0, GL_DIFFUSE, lightcol);
    glLightfv(GL_LIGHT0, GL_SPECULAR, lightcol);
    glLightfv(GL_LIGHT0, GL_AMBIENT, lightamb);
    glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, GL_TRUE);
#endif

    ret = prepareShaderProgram();
    if (ret < 0)
    {
        /* シェーダプログラムがよめなかった場合...  */
        return (ret);
    }


    /* 初回の描画指令を出す */
    drawObject(0);

    return (ret);
}


/**
 *  描画処理 (繰り返し呼ばれる)
 */
int drawObject(char command)
{

    // 画面更新
    eglSwapBuffers(egl_disp, egl_surface);

    return (checkFinish(command));
}

/**
 *  描画処理 (繰り返し呼ばれる)
 */
int drawObjecOld(char command)
{
    GLfloat fsize = 0.5f * 2.0f;
    GLfloat vertexPositions[] =
    {
        0.0f,  fsize,  0.0f, 1.0f,
       -fsize, -fsize,  0.0f, 1.0f,
        fsize, -fsize,  0.0f, 1.0f,
    };

    GLfloat vertexColors[] =
    {
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f
    };

    glClearColor(0.0f, 0.0f, 0.5f, 1.0f);
    glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

    glUseProgram(g_hShaderProgram);

    glVertexAttribPointer(g_VertexLoc, 4, GL_FLOAT, 0, 0, vertexPositions);
    glEnableVertexAttribArray(g_VertexLoc);

    glVertexAttribPointer(g_ColorLoc, 4, GL_FLOAT, 0, 0, vertexColors);
    glEnableVertexAttribArray(g_ColorLoc);

    glDrawArrays(GL_TRIANGLE_STRIP, 0, 3);

    glDisableVertexAttribArray(g_VertexLoc);
    glDisableVertexAttribArray(g_ColorLoc);

    // 画面更新
    eglSwapBuffers(egl_disp, egl_surface);

    return (checkFinish(command));
}

/**
 *  描画処理の終了処理 (あれば何かする)
 */
void finishDrawObject(void)
{
  /* いまのところ、何もしない */
}

/**
 *  プログラムを終了させるかどうかチェックする
 *    (スペースキーを押すと終了とする)
 */
int checkFinish(char command)
{
    int retCode = 0;
    if (command <= ' ')
    {
      /* アプリケーションを終了させる */
        return (-1);
    }

    switch (command)
    {
      default:
        /* 何もしない */
        break;
    }
    return (retCode);
}
