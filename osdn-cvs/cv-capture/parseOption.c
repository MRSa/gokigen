/**
 * ------------------------------------------------------------------------------
 *   parseOption.c :  アプリケーションの引数を解析する
 *
 * ------------------------------------------------------------------------------
 */
#include <string.h>

#include <highgui.h>
#include "mydefine.h"

/**
 *  引数を解析する処理
 */
void parseOption(int argc, char **argv, MyConfiguration *config)
{
    int count;

    // 引数が指定されていた場合...
    if (argc > 1)
    {
        for (count = 1; count < argc; count++)
        {
            // 引数のチェック処理
            argv++;
            if (strstr(*argv, "-vga") != 0)
            {
                config->cameraSize = MY_CAMERASIZE_VGA;
	    }
            else if (strstr(*argv, "-qvga") != 0)
            {
                config->cameraSize = MY_CAMERASIZE_QVGA;
	    }
            else if (strstr(*argv, "-hqvga") != 0)
            {
                config->cameraSize = MY_CAMERASIZE_HQVGA;
	    }
        }
    }
    return;
}
