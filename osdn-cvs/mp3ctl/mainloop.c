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
    unsigned long vanishedCheckCount = 0;

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

            //  �w�i���\������ (32���1��)
            if ((work->fCount % 32) == 0)
            {
                updateBackground(config, work);
            }

            // �摜��\������
            cvShowImage(work->mainWindow, work->frameImage);
        }

        // ���y�Đ����̏���
        if (currentPlayingStatus != 0)
        {
            if (work->lineStatus == MY_LINESTATUS_NONE)
            {
                if (vanishedCheckCount + 8 <= work->frameCount)
                {
                    // �Đ����Ƀ��C����8�t���[�����o�ł��Ȃ������ꍇ�ɂ͉��y�Đ����~����
                    stop_playing_music();
                }
                work->fCount = 31;
            }
            else if (work->lineCount == 8)
            {
                if (work->lineStatus == MY_LINESTATUS_RIGHT_UP)
                {
                    // �Đ����ɉE�オ��̃��C����8�t���[�����o�����ꍇ�ɂ́A1�Ȑ�ɑ���
                    stop_playing_music();
                    work->fCount = 31;
                }
                else if (work->lineStatus == MY_LINESTATUS_LEFT_UP)
                {
                    // �Đ����ɍ��オ��̃��C����8�t���[�����o�����ꍇ�ɂ́A1�ȑO�ɖ߂�
                    currentPlayingFile--;
                    if (currentPlayingFile < 0)
                    {
                        // �����̋Ȃɂ���
                        currentPlayingFile = numberOfPlayingFiles - 1;
                    }

                    stop_playing_music();
                    work->fCount = 31;
                }
                vanishedCheckCount = work->frameCount;
            }
            else
            {
                vanishedCheckCount = work->frameCount;
            }
        }
        else    //  ���y�̍Đ�����~���Ă���Ƃ��̏���
        {
            //  ���y�̍Đ����J�n���邩�ǂ����̃`�F�b�N (���C�������o����Ă���Ή��y�Đ�)
            if  (work->lineStatus == MY_LINESTATUS_NONE)
            {
                // ���C�����Ȃ��ꍇ�ɂ́A�������Ȃ�
            }
            else if ((work->lineStatus == MY_LINESTATUS_HORIZONTAL_LOW)||(work->lineStatus == MY_LINESTATUS_HORIZONTAL_HIGH)||
                     (work->lineStatus == MY_LINESTATUS_LEFT_UP))
            {
                // ���s�[�g�Đ�(��O�Đ�)�����{����
                currentPlayingFile--;
                if (currentPlayingFile < 0)
                {
                    // �Ō�̋Ȃɂ���
                    currentPlayingFile = numberOfPlayingFiles - 1;
                }

                // ���y�̍Đ����J�n����
                start_play_music(playingFileList[currentPlayingFile]);

                // ���̋Ȃ̃t�@�C���������肷��
                currentPlayingFile++;
                if (currentPlayingFile >= numberOfPlayingFiles)
                {
                    // �Ō�܂ōĐ����s���Ă����ꍇ�ɂ́A�擪���特�y���Đ�����
                    currentPlayingFile = 0;
                }
                work->fCount = 31;
            }
            else // if  ((work->lineStatus != MY_LINESTATUS_VERTICAL_LEFT)||(work->lineStatus != MY_LINESTATUS_VERTICAL_RIGHT)
                 //     (work->lineStatus == MY_LINESTATUS_RIGHT_UP)||(work->lineStatus == MY_LINESTATUS_LEFT_UP))
            {
                // ���y�̍Đ����J�n����
                start_play_music(playingFileList[currentPlayingFile]);

                // ���̋Ȃ̃t�@�C���������肷��
                currentPlayingFile++;
                if (currentPlayingFile >= numberOfPlayingFiles)
                {
                    // �Ō�܂ōĐ����s���Ă����ꍇ�ɂ́A�擪���特�y���Đ�����
                    currentPlayingFile = 0;
                }
                work->fCount = 31;
            }
        }

        // �L�[���͂��`�F�b�N����
        key = cvWaitKey(10);
        if (key > 0)
        {
            if (key == 'r') 
            {
                // r�L�[ : �Đ��Ȃ�擪�ɂ���
                currentPlayingFile = 0;
                return;
            }
            if (key == 'q') 
            {
                // q�L�[ : �����~�߂āA���C�����[�v�𔲂���
                stop_playing_music();
                return;
            }
        }
    }  // ���C�����[�v�̏I���

    //  ���y�̍Đ���~
    stop_playing_music();

    return;
}
