/**
 * ------------------------------------------------------------------------------
 *   saveImage.c  :  イメージをJPEGファイルで保存する
 *                  (ファイル名は、YYYY-MM-DD-hhmmss.jpg)
 * ------------------------------------------------------------------------------
 */
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <cv.h>
#include <highgui.h>

/*
   イメージをファイルに保存する
 */
int saveImageToFile(IplImage *outputImage)
{
    char fileNameBuffer[40];

    // ファイル名を年月日-時分秒で生成する
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
