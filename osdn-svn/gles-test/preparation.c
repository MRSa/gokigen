/**
 *  OpenGL ES(2.0)を利用するための初期化処理・終了処理
 *
 *    - SharpのNetWalker(PC-Z1)向け
 */
#include <stdio.h>
#include <fcntl.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>


EGLDisplay egl_disp    = EGL_NO_DISPLAY;
EGLSurface egl_surface = EGL_NO_SURFACE;
EGLContext egl_context = NULL;

/**
 *   OpenGL ESを利用するための初期化処理
 *
 */
int prepareOpenGL(void)
{
  EGLConfig  config;
  EGLint     major, minor;
  EGLint     nofConfigs = 0;

  static const EGLint attrib[]=
  {
    EGL_RED_SIZE,       4,
    EGL_GREEN_SIZE,     4,
    EGL_BLUE_SIZE,      4,
    EGL_ALPHA_SIZE,     4,
    EGL_SURFACE_TYPE,   EGL_WINDOW_BIT,
    EGL_DEPTH_SIZE,     0,
    EGL_STENCIL_SIZE,   0,
    EGL_SAMPLES,        0,
    EGL_NONE
  };

  static const EGLint ctxAttribList[] = 
  {
    EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE
  };

  /* バージョン番号格納領域をクリアする */
  major = 0;
  minor = 0;

  /* 1. 描画対象のネイティブディスプレイの取得 */
  egl_disp =  EGL_NO_DISPLAY;
  egl_disp = eglGetDisplay(EGL_DEFAULT_DISPLAY);
  if (egl_disp == EGL_NO_DISPLAY)
  {
      /* 失敗 */
      fprintf(stderr, "ERR>eglGetDisplay() ret:0x%x\n", (int) egl_disp);
      return (-1);
  }

  /* 2. EGLの初期化 */
  if (!eglInitialize(egl_disp, &major, &minor))
  {
      /* 初期化失敗... */
      fprintf(stderr, "ERR>eglInitialize()\n");
      return (-2);
  }

  eglBindAPI(EGL_OPENGL_ES_API);

  /* Open GLのメジャーバージョンとマイナーバージョンを表示 */
  fprintf(stderr, "EGL Version %d.%d\n", (int) major, (int) minor);

  /* 3. EGLコンフィグレーションの選択  */
  if (!eglChooseConfig(egl_disp, attrib, &config, 1, &nofConfigs))
  {
      /* 失敗 */
      fprintf(stderr, "ERR>eglChooseConfig()\n");
      return (-3);
  }

  /* 4. EGLウィンドウサーフェイスの生成 */
  egl_surface = eglCreateWindowSurface(egl_disp, config, open("/dev/fb0", O_RDWR), NULL);
  if (egl_surface == EGL_NO_SURFACE)
  {
      /* 失敗 */
      fprintf(stderr, "ERR>eglCreateWindowSurface()\n");
      return (-4);
  }

  /* 5. EGLグラフィックスコンテキストの作成 */
  egl_context = eglCreateContext(egl_disp, config, EGL_NO_CONTEXT, ctxAttribList);
  if (egl_context == EGL_NO_CONTEXT)
  {
      /* 失敗 */
      fprintf(stderr, "ERR>eglCreateContext()\n");
      return (-5);
  }

  /* 6. EGLグラフィックスコンテキストのカレント設定 */
  if (!eglMakeCurrent(egl_disp, egl_surface, egl_surface, egl_context))
  {
      /* 失敗 */
      fprintf(stderr, "ERR>eglMakeCurrent()\n");
      return (-6);
  }

  /* 準備完了 */
  return (0);
}

/**
 *   OpenGL ESを使ったあとの後片付けを実施
 *
 */
void finishOpenGL(void)
{
    /* 1. カレントコンテキストをクリア */
    eglMakeCurrent(egl_disp, egl_surface, egl_surface, egl_context);

    /* 2. EGLコンテキストの解放 */
    eglDestroyContext(egl_disp, egl_context);

    /* 3. EGLサーフェイスの解放 */
    eglDestroySurface(egl_disp, egl_surface);

    /* 4. EGLディスプレイの解放 */
    eglMakeCurrent(egl_disp, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglTerminate(egl_disp);

    eglReleaseThread();

    return;
}
