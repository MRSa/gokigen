/**
 * ------------------------------------------------------------------------------
 *   finish.c  :  �A�v���P�[�V�����̏I������
 *
 * ------------------------------------------------------------------------------
 */
#include <highgui.h>

#include "mydefine.h"

/*
 *  �A�v���P�[�V�����̂��Ƃ��܂�
 */
void finish(MyConfiguration *config, MyWorkArea *work)
{
    //    �̈���J������
    cvReleaseImage(&(work->outputImage));
    cvReleaseImage(&(work->grayImage));
    if (work->backImage != NULL)
    {
        cvReleaseImage(&(work->backImage));
    }

    //    �L���v�`�����������
    cvReleaseCapture(&(work->capture));

    //    �E�B���h�E��j������
    cvDestroyWindow(work->mainWindow);
    cvDestroyWindow(work->backWindow);
}
