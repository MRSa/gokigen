/**
 * ------------------------------------------------------------------------------
 *   parseOption.c :  �A�v���P�[�V�����̈�������͂���
 *
 * ------------------------------------------------------------------------------
 */
#include <string.h>

#include <highgui.h>
#include "mydefine.h"

/**
 *  ��������͂��鏈��
 */
void parseOption(int argc, char **argv, MyConfiguration *config)
{
    int count;

    // �������w�肳��Ă����ꍇ...
    if (argc > 1)
    {
        for (count = 1; count < argc; count++)
        {
            // �����̃`�F�b�N����
            argv++;
            if (strstr(*argv, "-vga") != 0)
            {
                config->cameraSize = MY_CAMERASIZE_VGA;
                config->lineSize   = 96;
	    }
            else if (strstr(*argv, "-qvga") != 0)
            {
                config->cameraSize = MY_CAMERASIZE_QVGA;
                config->lineSize   = 48;
	    }
            else if (strstr(*argv, "-hqvga") != 0)
            {
                config->cameraSize = MY_CAMERASIZE_HQVGA;
                config->lineSize   = 24;
	    }
            else // 
            {
	      strncpy(config->dataFileName, *argv, 511);
	    }
        }
    }
    return;
}
