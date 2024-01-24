#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#define BUFFER_MARGIN  8

GLuint g_hShaderProgram = 0;
GLuint g_VertexLoc      = 0;
GLuint g_ColorLoc       = 1;
GLuint g_MVPMLoc        = 2;

GLuint g_normalMatrixLoc = 3;

/* プロトタイプ宣言 */
long getFileSize(char *targetFileName);
int readContentFromFile(char *targetFileName, char **content);


/**
 *  シェーダプログラムの登録処理
 */
int prepareShaderProgram(void)
{
    GLuint hVertexShader;
    GLuint hFragmentShader;

    GLint nCompileResult = 0;
    GLint nLinkResult = 0;


    char *vertexProgram = NULL;
    char *fragmentProgram = NULL;
    if (readContentFromFile("shader.vert", &vertexProgram) < 0)
    {
        return (-1);
    }
    if (readContentFromFile("shader.frag", &fragmentProgram) < 0)
    {
        if (vertexProgram != NULL)
        {
            free(vertexProgram);
        }
        return (-1);
    }

    hVertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(hVertexShader, 1,  (const char **) &vertexProgram, NULL);
    glCompileShader(hVertexShader);

    glGetShaderiv(hVertexShader, GL_COMPILE_STATUS, &nCompileResult);
    if (!nCompileResult)
    {
        int  logLength;
        char logBuffer[1024];
        glGetShaderInfoLog(hVertexShader, sizeof(logBuffer), &logLength, logBuffer);
        fprintf(stderr, "ERR(v)>%s\n", logBuffer);
        if (vertexProgram != NULL)
        {
            free(vertexProgram);
        }
        if (fragmentProgram != NULL)
        {
            free(fragmentProgram);
        }
        return (-1);
    }

    hFragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(hFragmentShader, 1, (const char **) &fragmentProgram, NULL);
    glCompileShader(hFragmentShader);


    if (vertexProgram != NULL)
    {
        free(vertexProgram);
    }
    if (fragmentProgram != NULL)
    {
        free(fragmentProgram);
    }


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
    glBindAttribLocation(g_hShaderProgram, g_ColorLoc,  "g_vVSColor");

    g_MVPMLoc          = glGetUniformLocation(g_hShaderProgram, "u_modelViewProjMatrix");
    g_normalMatrixLoc  = glGetUniformLocation(g_hShaderProgram, "u_normalMatrix");

    /* Set up a uniform variable for the shaders */
    glUniform3f(glGetUniformLocation(g_hShaderProgram, "lightDir"), 0, 0, 1);

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

    return (0);
}


/**
 *  ファイルサイズを取得する
 */
long getFileSize(char *targetFileName)
{
    long filesize = -1;

    FILE *fp = fopen(targetFileName, "rb");
    if (fp == NULL)
    {
        return (-1);
    }
    fseek(fp, 0L, SEEK_END);
    filesize = ftell(fp);
    fclose(fp);

    return (filesize);
}


/**
 *   ファイルから情報を読み出す
 *   (読み込んだ領域のメモリを確保するので、忘れず開放すること！)
 */
int readContentFromFile(char *targetFileName, char **content)
{
    long fileSize = 0;  /* 読み込むファイルのファイルサイズ */
    FILE *fp = NULL;


    /*  とりあえず初期化しておく */
    *content = NULL;

    /* ファイルサイズを取得 */
    fileSize = getFileSize(targetFileName);
    if (fileSize <= 0)
    {
        /* ファイルサイズ異常 */
        fprintf(stderr, "ERR>File not found (%s)\n", targetFileName);
        return (-1);
    }

    /* 領域確保 */
    *content = malloc(fileSize + BUFFER_MARGIN);
    if (*content == NULL)
    {
        /* 領域確保失敗 */
        fprintf(stderr, "ERR>malloc(%ld)\n", (fileSize + BUFFER_MARGIN));
        return (-2);
    }
    memset(*content, 0x00, (fileSize + BUFFER_MARGIN));


    /* 指定されたファイルをオープンする */
    fp = fopen(targetFileName, "r");
    if (fp == NULL)
    {
        fprintf(stderr, "ERR>File not found (%s)...\n", targetFileName);
        free(*content);
        *content = NULL;
        return (-3);
    }

    /* ファイルをがっつり読み込む */
    if (fread(*content, 1, fileSize, fp) <= 0)
    {
        fprintf(stderr, "ERR>fread()...%ld\n", (fileSize + BUFFER_MARGIN));
        free(*content);
        *content = NULL;
        return (-4);
    }

    /* ファイルをクローズして終了する */
    fclose(fp);

    return (0);
}
