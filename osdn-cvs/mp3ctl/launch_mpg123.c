/**
 *   mpg123を利用して、音楽ファイルを再生する
 *
 */
#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <string.h>
#include <wait.h>
#include <signal.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>

#include <gdk/gdk.h>


#define MPG123_BINARY "mpg123"   /* 起動するmpg123のバイナリファイル名   */
#define MAX_ARGUMENTS 10         /* 引数の最大値 */
#define DATA_BUFFER_SIZE 1024    /* 引数１件あたりの最大サイズ(ちょっと大きめ) */

int  currentPlayingFile = 0;     /* 現在再生しているファイル（曲順の番号） */
int  currentPlayingStatus = 0;   /* 現在再生しているか否かの情報 */
int  numberOfPlayingFiles = 0;   /* 抱え込んでいる再生ファイル数 */
char **playingFileList;          /* 再生ファイル数の実体 */

static pid_t pid = 0;
static int   mpg123_pipe[2];
static int   mpg123_debugMode = 0;
static gint  mpg123_pipe_read_id = -1;

/*  プロトタイプ宣言 */
void stop_playing_music(void);
int start_play_music(char *fileToPlay);

/**
 *  mpg123から受けとった標準出力のデータを解析する
 *
 */
void parseMpg123StdOut(char *buf, int size)
{
    /*  とりあえず、何もしません */
    return;
}

/**
 *   mpg123から出力された表示（標準出力への表示）を読み出す
 *
 */
void mpg123_pipe_read_cb(gpointer data, int fd, GdkInputCondition condition)
{
    gint r;
    gchar buf[DATA_BUFFER_SIZE];

    r = read(fd, buf, sizeof(buf));

    if (mpg123_debugMode != 0)
    {
        if (mpg123_debugMode > 2)
        {
            printf("mpg123 read input %d bytes\n", r);
        }
        printf("- - - - -\n");
        printf("%s", buf);
        printf("\n- - - - -\n");
    }

    if (r < -1)
    {
        /* データ読み出しエラー */
        printf(("ERR>error reading from mpg123!\n"));
        stop_playing_music();
        return;
    }
    if (r == 0)
    {
        /* データ出力終了 (= 音楽再生の完了) */
        printf(("INF>mpg123 disappeared! (unexpected EOF)\n"));
        stop_playing_music();
        return;
    }

    /* 読み込んだデータを解析する */
    parseMpg123StdOut(buf, r);

    return;
}

/**
 *   mpg123を利用して音楽ファイルの再生を行う
 * 
 * (応答コード)
 *   -1 : mpg123の起動準備失敗
 *    0 : mpg123の起動失敗
 *    1 : mpg123の起動成功
 */
int start_play_music(char *fileToPlay)
{
    int ret;
    pid_t frk_pid;
    char exec_bin[32];
    char cmd_arguments[MAX_ARGUMENTS][512];
    char *cmd_ptr[MAX_ARGUMENTS];

    mpg123_debugMode = 3;


    /* mpg123 呼び出し引数の作成 */
    sprintf(exec_bin, "%s", MPG123_BINARY);

    memset(cmd_arguments, 0x00, sizeof(cmd_arguments));
    memset(cmd_ptr, 0x00, sizeof(cmd_ptr));
    strncpy(cmd_arguments[0], fileToPlay, 512 - 1);

    cmd_ptr[0] = exec_bin;
    cmd_ptr[1] = cmd_arguments[0];

    ret = pipe(mpg123_pipe);
    if (ret != 0)
    {
        fprintf(stderr, "pipe failed\n");
        return (-1);
    }

    if (mpg123_debugMode != 0)
    {
        printf("opening to play : %s (%s)\n", fileToPlay, cmd_arguments[0]);
    }

    /* fork & exec */
    frk_pid = fork();
    if (frk_pid < (pid_t) 0)
    {
        /* The fork failed. */
        fprintf(stderr, "ERR>fork() failed.\n");
        pid = 0;
        currentPlayingStatus = 0;
        return (0);
    }

    if (frk_pid == (pid_t) 0)
    {
        /* 子プロセス側の処理 */
        dup2(mpg123_pipe[1], 2);
        close(mpg123_pipe[0]);

        /* set the group (session) id to this process for future killing */
        setsid();

        /** Launch MPG123 **/
        execvp(exec_bin, cmd_ptr);
        printf("ERR>Unable to run '%s'.\n", exec_bin);
        _exit(1);
    }

    /* 親プロセス側の処理 */
    pid = (int) frk_pid;
    currentPlayingStatus = (int) frk_pid;

    fcntl(mpg123_pipe[0], F_SETFL, O_NONBLOCK);
    close(mpg123_pipe[1]);
    mpg123_pipe[1] = -1;

    if (mpg123_debugMode != 0)
    {
        printf("mpg123 pid = %d\n", pid);
    }

    /* パイプ入力の接続 */
    mpg123_pipe_read_id = gdk_input_add(mpg123_pipe[0], GDK_INPUT_READ,
                                        mpg123_pipe_read_cb, GINT_TO_POINTER(FALSE));

    return (1);
}

/**
 *   音楽ファイルの再生を止める (mpg123を止める)
 *
 */
void stop_playing_music(void)
{
    pid_t t;

    /* 音楽再生中でなければ何もせずに retrun する */
    if (pid <= 0)
    {
        return;
    }

    if (mpg123_debugMode != 0)
    {
        printf("sending SIGTERM to pid = -%d\n", pid);
    }

    /* kill the entire mpg123 group to work around mpg123 buffer bug */
    if (kill(-pid, SIGINT) == -1)
    {
        if (mpg123_debugMode != 0)
        {
            printf("waiting 1 sec to try again: pid = -%d\n", pid);
        }
        sleep(1);
        if (kill(-pid, SIGINT) == -1)
        {
            printf(("Failed to successfully send signal to pid = %d\n"), (int)pid);
        }
    }

    if (mpg123_debugMode != 0)
    {
        printf("first waitpid = %d\n", (int)pid);
    }

    waitpid (pid, NULL, WNOHANG);
    kill(-pid, SIGKILL);
    waitpid (pid, NULL, 0);

#if 0
    t = waitpid (pid, NULL, WNOHANG);
    if (t != pid && t != -1)
    {
        if (mpg123_debugMode != 0)
        {
            printf("second waitpid, 1st returned: %d\n", t);
        }
        /* this one WILL HANG, so if someone sent a SIGSTOP to mpg123... */
        waitpid (pid, NULL, 0);
        if (mpg123_debugMode != 0)
        {
            printf("second waitpid done.\n");
        }
    }
#endif

    /** パイプの切り離し **/
    if (mpg123_pipe_read_id != -1)
    {
        gtk_input_remove(mpg123_pipe_read_id);
    }
    mpg123_pipe_read_id = -1;
    close(mpg123_pipe[0]);
    mpg123_pipe[0] = -1;

    /* あとしまつ */
    pid = 0;
    currentPlayingStatus = 0;
    if (mpg123_debugMode)
    {
        printf("mpg123 closed\n");
    }
}

/*
 *   ファイルの中に書かれているファイル名のリストを取得する
 */
int parseFileList(char *targetFileName, char ***fileList)
{
    int maxFileNameList = 250;  /* 解析する最大のファイル数 */
    int fileCount = -1;
    int bufferSize;
    int nameSize;

    char **buffer;
    char readBuffer[1024];
    char *ptr;

    FILE *fp;

    /* 指定されたファイルをオープンする */
    fp = fopen(targetFileName, "r");
    if (fp == NULL)
    {
        printf(" File not found (%s) ", targetFileName);
        return (fileCount);
    }

    bufferSize = sizeof(char *) * (maxFileNameList + 1);
    *fileList  = malloc(bufferSize);
    if (*fileList == 0)
    {
        /*  領域確保に失敗 */
        return (fileCount);
    }
    memset(*fileList, 0x00, bufferSize);
    buffer = *fileList;
    fileCount = 0;

    /* 解析のメイン処理 */
    while (fgets(readBuffer, sizeof(readBuffer) - 1, fp) != NULL)
    {
        ptr = readBuffer;        
        while (*ptr >= ' ')
        {
            ptr++;
        }
        *ptr = 0;
        nameSize = strlen(readBuffer) + 1;
        *buffer = malloc(nameSize);
        memset(*buffer, 0x00, nameSize); 
        sprintf(*buffer, "%s", readBuffer);
        buffer++;
        fileCount++;

        if (fileCount >= maxFileNameList)
        {
            break;
        }
    }
    fclose(fp);

    return (fileCount);
}

/*
 *  ファイル名リストを解放する。
 */
void releaseFileList(int fileNameCount, char ***fileList)
{
    int index;
    for (index = 0; index < fileNameCount; index++)
    {
        free((*fileList)[index]);
        (*fileList)[index] = 0;
    }

    if (fileNameCount >= 0)
    {
        free(*fileList);
        *fileList = 0;
    }
    return;
}

/*
 *  参考資料： gqmpeg-0.20.0 の io_mpg123.c
 */
