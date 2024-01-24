/**
 * ------------------------------------------------------------------------------
 *   saveImage.c  :  �C���[�W��JPEG�t�@�C���ŕۑ�����
 *                  (�t�@�C�����́AYYYY-MM-DD-hhmmss.jpg)
 * ------------------------------------------------------------------------------
 */
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <cv.h>
#include <highgui.h>

/*
   �C���[�W���t�@�C���ɕۑ�����
 */
int saveImageToFile(IplImage *outputImage)
{
    char fileNameBuffer[40];

    // �t�@�C������N����-�����b�Ő�������
    time_t t;
    struct tm *tmp;
    t = time(NULL);
    tmp = localtime(&t);
    if (tmp != NULL)
    {
        strftime(fileNameBuffer, (sizeof(fileNameBuffer) - 1), "%Y-%m-%d-%H%M%S", tmp);
        strcat(fileNameBuffer, ".jpg");
    }
    else
    {
        sprintf(fileNameBuffer, "ImageFileXXX.jpg");
    }
    return (cvSaveImage(fileNameBuffer, outputImage));
}
