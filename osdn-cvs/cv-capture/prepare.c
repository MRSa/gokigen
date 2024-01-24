/**
 * ------------------------------------------------------------------------------
 *   prepare.c  :  �A�v���P�[�V�����̏���������
 *                (���[�J���ϐ��̏������ƁA�v���O�����̏�����)
 * ------------------------------------------------------------------------------
 */
#include <stdio.h>
#include <highgui.h>

#include "mydefine.h"
#include "myproto.h"

/*
 *  ���[�J���ϐ��̏�����
 */
void prepare(char *mainWindow, char*backWindow, MyConfiguration *config, MyWorkArea *work)
{
    // �ݒ�̏�����
    memset(config, 0x00, sizeof(MyConfiguration));
    config->cameraSize = MY_CAMERASIZE_QVGA;
    config->showMode   = MY_SHOWMODE_NORMAL;

    // ���[�N�̈�̏�����
    memset(work, 0x00, sizeof(MyWorkArea));
    work->doCapture = MY_FALSE;

    // �E�B���h�E���̂��L�^����
    work->mainWindow = mainWindow;
    work->backWindow = backWindow;

}


/*
 *  �v���O�����̏�����
 */
int doInitialize(MyConfiguration *config, MyWorkArea *work)
{
    //    �J����������������
    work->capture = cvCreateCameraCapture(0);
    if (work->capture == NULL) 
    {
        //    �J������������Ȃ������ꍇ
        printf( "ERR>Cannot find any camera device.\n" );
        return (MY_FALSE);
    }

    // ��荞�މ摜�T�C�Y��ݒ肷��
    setCameraCaptureSize(work->capture, config->cameraSize);

    // 1���ڂ̉摜���擾����
    work->frameImage = NULL;
    while (work->frameImage == NULL)
    {
        work->frameImage  = cvQueryFrame(work->capture);
    }
    // �摜�T�C�Y��ݒ肷��
    work->grayImage = cvCreateImage(cvGetSize(work->frameImage), IPL_DEPTH_8U, 1);
    work->outputImage = cvCreateImage(cvGetSize(work->frameImage), IPL_DEPTH_8U, 1);

    return (MY_TRUE);
}
