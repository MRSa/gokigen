/* ------------------------------------------------------------------------------
 *  VIDEO CAPTURE PROGRAM (OpenCV�𗘗p�����A������Ƃ����e�X�g�v���O����)
 *
 *
 *    === ���� ===
 *       �{�v���O�����́A�ڑ����ꂽ�J�����̉摜���w�肳�ꂽ���H���s���\������
 *     �v���O�����ł��B�\�����Ă���摜���t�@�C���ۑ����邱�Ƃ��ł��܂��B
 *
 *       bgFile.jpg �Ƃ����t�@�C�������݂���΁A�ʃE�B���h�E�ŕ\�����A���݂�
 *     ���H���[�h��\�����邱�Ƃ��ł��܂��B
 *
 *
 *    === �N���I�v�V���� ===
 *
 *      -hqvga  160x120 �ŉ摜�L���v�`�����s���܂�
 *      -qvga   320x240 �ŉ摜�L���v�`���܂�  �f�t�H���g�\���ł�
 *      -vga    640x240 �ŉ摜�L���v�`�����s���܂�
 *
 *
 *    === �{�v���O�����Ŏ󂯕t����L�[���� ===
 *
 *        n : �ʏ�摜
 *        g : �O���[�X�P�[���摜
 *        e : �G�b�W���o�摜
 *        v : �G�b�W���o/�P�F�J���[�������o
 *        b : 2�l�\���摜
 *        l : �ԐF�����̌��o
 *        o : �֊s���o�摜
 *        p : �摜�������o���[�h (�܂��쐬��)
 *        m : �P�F�J���[���o���[�h (�܂��쐬��)
 *        
 *        . : �G�b�W���o-2 �̌��o���@�ύX (Laplace/Sobel)
 *
 *        u : 2�l�\���̂������l�ύX (+10)
 *        d : 2�l�\���̂������l�ύX (-10)
 *
 *        c : �\�����Ă���摜��ۑ� (�t�@�C������ "�N-��-��-�����b.jpg")
 *
 *        q : �A�v�����I��������
 *  ------------------------------------------------------------------------------
 */
#include <stdio.h>
#include <highgui.h>
#include "mydefine.h"
#include "myproto.h"

/**
 *  ���C������...
 */
int main(int argc, char **argv)
{
    MyConfiguration  config;                      //  �A�v���P�[�V�����ݒ���
    MyWorkArea       workArea;                    //  ���[�N�G���A

    char windowNameCapture[]    = "Capture";      // �L���v�`�������摜��\������E�B���h�E�̖��O
    char windowNameBackground[] = "Background";   // �w�i�摜��\������E�B���h�E�̖��O
    char backgroundFileName[]   = "bgFile.jpg";   // �w�i�摜�̃t�@�C����

    // ��������
    prepare(windowNameCapture, windowNameBackground, &config, &workArea);

    // �����̃`�F�b�N�����s�A�ݒ�l�ɔ��f������
    parseOption(argc, argv, &config);

    // �E�B���h�E�𐶐����� (�w�i�E�B���h�E)
    cvNamedWindow(workArea.backWindow, CV_WINDOW_AUTOSIZE);

    // �E�B���h�E�𐶐����� (�L���v�`���E�B���h�E)
    cvNamedWindow(workArea.mainWindow, CV_WINDOW_AUTOSIZE);

    // �w�i���ݒ肷��
    setBackground(backgroundFileName, &workArea);

    //    �v���O����������������
    if (doInitialize(&config, &workArea) == MY_FALSE)
    {
        printf( "ERR>PROGRAM ABORT.\n" );
        return (-1);
    }

    // ���C�����[�v
    mainLoop(&config, &workArea);

    // ���Ƃ��܂�
    finish(&config, &workArea);

    return (0);
}
