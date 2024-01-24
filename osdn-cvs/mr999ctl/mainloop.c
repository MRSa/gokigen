/**
 * ------------------------------------------------------------------------------
 *   mainloop.c  :  ���C���������[�v
 *
 * ------------------------------------------------------------------------------
 */
#include <cv.h>
#include <highgui.h>

#include "mydefine.h"
#include "myproto.h"


/*
 *  ���C������
 *
 */
void mainLoop(MyConfiguration *config, MyWorkArea *work)
{
    // �L�[���͗p�̕ϐ�
    int key   = 0;

    work->fCount = 31;

    //    ���C�����[�v
    while (1)
    {
        // �J�E���g�A�b�v
        (work->fCount)++;
     
        // �J��������̓��͉摜1�t���[����frameImage�Ɋi�[����
        work->frameImage = cvQueryFrame(work->capture);
        if (work->frameImage != NULL)
        {
            // �t���[���J�E���g���J�E���g�A�b�v
            (work->frameCount)++;

            // �������o
            detectLine(config, work);

            // �����̌��o�㏈��
            if (handleLine(config, work) < 0)
            {
               return;
            }

            //  �w�i���\������ (32���1��)
            if ((work->fCount % 32) == 0)
            {
                updateBackground(config, work);
            }

            // �摜��\������
            cvShowImage(work->mainWindow, work->frameImage);
	}

        // �L�[���͂��`�F�b�N����
        key = cvWaitKey(10);
        if (key > 0)
        {
            if (key == 'q') 
            {
                // q�L�[ : ���C�����[�v�𔲂���
                return;
            }
            if (handleKey(key) < 0)
            {
                // ���̒l���A���Ă���� : ���C�����[�v�𔲂���
                return;
            }
        }
    }  // ���C�����[�v�̏I���
    return;
}
