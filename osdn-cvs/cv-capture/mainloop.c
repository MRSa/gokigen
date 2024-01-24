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

// ���H�\��(�v���g�^�C�v)
void convertVision(IplImage *source, IplImage *destination);

/*
 *  ���C������
 *
 */
void mainLoop(MyConfiguration *config, MyWorkArea *work)
{
   int key   = 0;         // �L�[���͗p�̕ϐ�

   // 2�l�f�[�^�̏����������l
   work->binLevel = 120;  

   // �\���̈��ݒ肷��
   work->screenImage = work->frameImage;

    //    ���C�����[�v
    while (1)
    {
        (work->fCount)++;      // �J�E���g�A�b�v

        //    �J��������̓��͉摜1�t���[����frameImage�Ɋi�[����
        work->frameImage = cvQueryFrame(work->capture);
        if (work->frameImage != NULL)
        {
            // frameImage�Ƀf�[�^�Ɋi�[�ł����ꍇ...
            switch (config->showMode)
            {
              case MY_SHOWMODE_GRAY:
                // �O���C�X�P�[���ɕϊ�����
                cvCvtColor(work->frameImage, work->grayImage, CV_BGR2GRAY);
                break;

              case MY_SHOWMODE_EDGE:
                // �O���C�X�P�[���ɕϊ���A�G�b�W�̌��o���s��
                cvCvtColor(work->frameImage, work->grayImage, CV_BGR2GRAY);
		cvCanny(work->grayImage, work->outputImage, 64.0, 128.0, 3);
                break;

              case MY_SHOWMODE_BINARY:
                // 2�l�ϊ�
                cvCvtColor(work->frameImage, work->grayImage, CV_BGR2GRAY);
                cvThreshold(work->grayImage, work->outputImage, work->binLevel, 255.0, CV_THRESH_BINARY);
                break;

              case MY_SHOWMODE_VISION:
                // �摜���H�̕\��
                convertVision(work->frameImage, work->outputImage);
                break;

              case MY_SHOWMODE_NORMAL:
              default:
                // �J�����ŃL���v�`�������摜�����̂܂܎g��
                break;
            }

            //  �w�i���\������ (32���1��)
            if ((work->fCount % 32) == 0)
            {
                updateBackground(config, work);
            }

            // �摜��\������
            cvShowImage(work->mainWindow, work->screenImage);
            if (work->doCapture == MY_TRUE)
            {
                saveImageToFile(work->screenImage);
                work->doCapture = MY_FALSE;
            }
	}

        // �L�[���͂��`�F�b�N����
        key = cvWaitKey(10);
        if (key > 0)
        {
            if (key == 'n') 
            {
                // n�L�[ : �W���摜���[�h�ɐ؂�ւ���
                config->showMode = MY_SHOWMODE_NORMAL;
                work->screenImage = work->frameImage;
                work->binLevel = 120;
                work->fCount = 31;
            }
            else if (key == 'g') 
            {
                // g�L�[ : �O���[�摜���[�h�ɐ؂�ւ���
                config->showMode = MY_SHOWMODE_GRAY;
                work->screenImage = work->grayImage;
                work->fCount = 31;
            }
            else if (key == 'e') 
            {
                // e�L�[ : �G�b�W�摜���[�h�ɐ؂�ւ���
                config->showMode = MY_SHOWMODE_EDGE;
                work->screenImage = work->outputImage;
                work->fCount = 31;
            }
            else if (key == 'b') 
            {
                // b�L�[ : ��l�摜���[�h�ɐ؂�ւ���
                config->showMode = MY_SHOWMODE_BINARY;
                work->screenImage = work->outputImage;
                work->fCount = 31;
            }
            else if (key == 'v') 
            {
                // v�L�[ : ���H�\�����[�h�ɐ؂�ւ���
                config->showMode = MY_SHOWMODE_VISION;
                work->screenImage = work->outputImage;
                work->fCount = 31;
            }
            else if (key == 'c') 
            {
                // c�L�[ : �摜�L���v�`�������s����
                work->doCapture = MY_TRUE;
                work->fCount = 31;
            }
            else if (key == 'u') 
            {
                // u�L�[ : 2�l�����x����ύX����
                work->binLevel = work->binLevel + 10;
                if (work->binLevel > 250)
                {
                    work->binLevel = 10;
                }
                work->fCount = 31;
            }
            else if (key == 'd') 
            {
                // d�L�[ : 2�l�����x����ύX����
                work->binLevel = work->binLevel - 10;
                if (work->binLevel < 10)
                {
                    work->binLevel = 250;
                }
                work->fCount = 31;
            }
            else if (key == 'q') 
            {
                // q�L�[ : ���C�����[�v�𔲂���
                return;
            }
        }
    }  // ���C�����[�v�̏I���
    return;
}

/*
 *  ��f�����H����
 */
void convertVision(IplImage *source, IplImage *destination)
{
    int x, y;
    CvScalar s;

    double i, j, r, g, b;

    // ��f�P�ʂŐF��ϊ�����
    for (y = 0; y < source->height; y++)
    {
        for (x = 0; x < source->width; x++)
        {
            // ��f�l���擾
            s = cvGet2D(source, y, x);
/*
            // �P�x�ɕϊ�
            i = ( s.val[ 2 ] * 0.299 + s.val[ 1 ] * 0.587 + s.val[ 0 ] * 0.114 ) / 255.0;

            j = i - 0.5;
            
            // �Ԑ���
            r = j > 0 ? j * j * 4 : 0;

            // �ΐ���
            g = i * i;

            // ����
            b = j > 0 ? j * j * 4 * 0.8 : 0;

            s.val[ 0 ] = b * 255.0;
            s.val[ 1 ] = g * 255.0;
            s.val[ 2 ] = r * 255.0;
*/
            // �摜�ɏ�������
            cvSet2D(destination, y, x, s);
        }
    }
    return;
}
