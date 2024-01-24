#include <stdio.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

extern EGLDisplay egl_disp;
extern EGLSurface egl_surface;

GLuint g_hShaderProgram = 0;
GLuint g_VertexLoc      = 0;
GLuint g_ColorLoc       = 1;

/**
 *  バーテックスシェーダのプログラム
 */
const char* g_strVSProgram =
  "attribute vec4 g_vVertex;   \n"
  "attribute vec4 g_vColor;    \n"
  "varying   vec4 g_vVSColor;  \n"
  "                            \n"
  "void main()                 \n"
  "{                           \n"
  "  gl_Position = vec4(g_vVertex.x, \n"
  "                     g_vVertex.y, \n"
  "                     g_vVertex.z, \n"
  "                     g_vVertex.w);\n"
  "  g_vVSColor = g_vColor;    \n"
  "}                           \n";


/**
 *  フラグメントシェーダのプログラム
 */
const char* g_strFSProgram =
  "#ifdef GL_FLAGMENT_PRECISION_HIGH  \n"
  "  precision highp float;    \n"
  "#else                       \n"
  "  precision mediump float;  \n"
  "#endif                      \n"
  "                            \n"
  "varying vec4 g_vVSColor;    \n"
  "                            \n"
  "void main()                 \n"
  "{                           \n"
  "  gl_FragColor = g_vVSColor;\n"
  "}                           \n";

/** プロトタイプ宣言 **/
int  checkFinish(char command);
int  drawObject(char command);

/**
 *  描画処理の前処理 (シェーダプログラムの登録、とか)
 */
int startDrawObject(void)
{
    GLuint hVertexShader;
    GLuint hFragmentShader;

    GLint nCompileResult = 0;
    GLint nLinkResult = 0;

    hVertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(hVertexShader, 1, &g_strVSProgram, NULL);
    glCompileShader(hVertexShader);

    glGetShaderiv(hVertexShader, GL_COMPILE_STATUS, &nCompileResult);
    if (!nCompileResult)
    {
        int  logLength;
        char logBuffer[1024];
        glGetShaderInfoLog(hVertexShader, sizeof(logBuffer), &logLength, logBuffer);
        fprintf(stderr, "ERR(v)>%s\n", logBuffer);
        return (-1);
    }

    hFragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(hFragmentShader, 1, &g_strFSProgram, NULL);
    glCompileShader(hFragmentShader);

    glGetShaderiv(hFragmentShader, GL_COMPILE_STATUS, &nCompileResult);
    if (!nCompileResult)
    {
        int  logLength;
        char logBuffer[1024];
        glGetShaderInfoLog(hFragmentShader, sizeof(logBuffer), &logLength, logBuffer);
        fprintf(stderr, "ERR(f)>%s\n", logBuffer);
        return (-1);
    }

    g_hShaderProgram = glCreateProgram();
    glAttachShader(g_hShaderProgram, hVertexShader);
    glAttachShader(g_hShaderProgram, hFragmentShader);

    glBindAttribLocation(g_hShaderProgram, g_VertexLoc, "g_vVertex");
    glBindAttribLocation(g_hShaderProgram, g_ColorLoc,  "g_vColor");
    glLinkProgram(g_hShaderProgram);


    glGetProgramiv(g_hShaderProgram, GL_LINK_STATUS, &nLinkResult);
    if (!nLinkResult)
    {
        int  logLength;
        char logBuffer[1024];
        glGetProgramInfoLog(g_hShaderProgram, sizeof(logBuffer), &logLength, logBuffer);
        fprintf(stderr, "ERR>%s\n", logBuffer);
        return (-2);
    }
    glDeleteShader(hVertexShader);
    glDeleteShader(hFragmentShader);


    /* 描画指令を出して表示 */
    drawObject(0);

    return (0);
}

/**
 *  描画処理 (繰り返し呼ばれる)
 */
int drawObject(char command)
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
