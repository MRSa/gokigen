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
    config->lineSize   = 48;

    // ���[�N�̈�̏�����
    memset(work, 0x00, sizeof(MyWorkArea));

    // �E�B���h�E���̂��L�^����
    work->mainWindow = mainWindow;
    work->backWindow = backWindow;

}


/*
 *  �v���O�����̏�����
 */
int doInitialize(MyConfiguration *config, MyWorkArea *work)
{

    // ���y�t�@�C�������w�肳��Ă��Ȃ������ꍇ...
    if (strlen(config->dataFileName) <= 0)
    {
        printf("ERR>PLAYING FILE NAME IS NOT SPECIFIED.\n");
        return (MY_FALSE);
    }

    // ���y�t�@�C�����X�g��ǂݏo��
    numberOfPlayingFiles = parseFileList(config->dataFileName, &playingFileList);
    if (numberOfPlayingFiles < 0)
    {
        // �������̊m�ۂɎ��s�����ꍇ...
        printf("ERR>MEMORY ALLOCATION ERROR ('%s')\n", config->dataFileName);
        return (MY_FALSE);
    }
    if (numberOfPlayingFiles == 0)
    {
        // �t�@�C�������ЂƂ��L�q����Ă��Ȃ������ꍇ...
        printf("ERR>Cannot find any playing file in '%s'.\n", config->dataFileName);
        releaseFileList(numberOfPlayingFiles, &playingFileList);
        return (MY_FALSE);
    }

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
    work->edgeImage     = cvCreateImage(cvGetSize(work->frameImage), IPL_DEPTH_8U, 1);
    work->redImage      = cvCreateImage(cvGetSize(work->frameImage), IPL_DEPTH_8U, 1);

    work->storage = cvCreateMemStorage(0);

    return (MY_TRUE);
}
