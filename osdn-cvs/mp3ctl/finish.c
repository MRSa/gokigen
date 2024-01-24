/**
 * ------------------------------------------------------------------------------
 *   finish.c  :  �A�v���P�[�V�����̏I������
 *
 * ------------------------------------------------------------------------------
 */
#include <highgui.h>

#include "mydefine.h"
#include "myproto.h"

/*
 *  �A�v���P�[�V�����̂��Ƃ��܂�
 */
void finish(MyConfiguration *config, MyWorkArea *work)
{
    //    ���[�N�̈���J������
    cvReleaseMemStorage (&work->storage);

    //    �̈���J������
    cvReleaseImage(&(work->edgeImage));
    cvReleaseImage(&(work->redImage));
    cvReleaseImage(&(work->backImage));

    //    �L���v�`�����������
    cvReleaseCapture(&(work->capture));

    //    �E�B���h�E��j������
    cvDestroyWindow(work->mainWindow);
    cvDestroyWindow(work->backWindow);

    // ���y�t�@�C�����X�g���������
    releaseFileList(numberOfPlayingFiles, &playingFileList);
}
