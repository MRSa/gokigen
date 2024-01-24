/**
 * ------------------------------------------------------------------------------
 *   background.c :  �w�i�E�B���h�E��ݒ肷��
 *
 * ------------------------------------------------------------------------------
 */
#include <stdio.h>
#include <cv.h>
#include <highgui.h>
#include "mydefine.h"

/**
 *  �w�i��̕\��
 */
void setBackground(char *backImageFileName, MyWorkArea *work)
{
    // �w�i���w�肳��Ă��Ȃ��ꍇ�A���̂܂ܖ߂�
    if (backImageFileName == NULL)
    {
        return;
    }

    // �摜�̓ǂݍ���
    work->backImage = cvLoadImage(backImageFileName, CV_LOAD_IMAGE_COLOR);
    if (work->backImage != NULL)
    {
        // �w�i����R�s�[����
        work->backScreen = cvCloneImage(work->backImage);

        // �摜���ǂ߂���A�\������
        cvShowImage(work->backWindow, work->backImage);
    }

    return;
}

/**
 *  �w�i��̕\��
 */
void updateBackground(MyConfiguration *config, MyWorkArea *work)
{
    CvFont     font;              // �t�H���g�p�̈�
    char       buffer[40];

    if (work->backImage == NULL)
    {
        // �w�i�悪�Ȃ���Ε\�����Ȃ�
        return;
    }

    sprintf(buffer, "PICKLINE");

    // �w�i��̏���
    cvReleaseImage(&work->backScreen);
    work->backScreen = cvCloneImage(work->backImage);

    // �t�H���g�̏�������
    cvInitFont(&font, CV_FONT_HERSHEY_PLAIN, 1,1, 0, 1, 8);
    cvPutText(work->backScreen, buffer, cvPoint(350, 265), &font, CV_RGB(250, 0, 0));

    // ���b�Z�[�W�̕\��
    if (strlen(work->message) > 0)
    {
        cvPutText(work->backScreen, work->message, cvPoint(350, 247), &font, CV_RGB(250, 0, 0));
    }

    // �w�i���\������
    cvShowImage(work->backWindow, work->backScreen);
    return;
}