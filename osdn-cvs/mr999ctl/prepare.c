/**
 * ------------------------------------------------------------------------------
 *   prepare.c  :  �A�v���P�[�V�����̏���������
 *                (���[�J���ϐ��̏������ƁA�v���O�����̏�����)
 * ------------------------------------------------------------------------------
 */
#include <stdio.h>
#include <highgui.h>
#include <usb.h>

#include "mydefine.h"
#include "myproto.h"

#include "io_usbio.h"
extern struct usb_dev_handle *usbio_device;


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
    struct usb_device *dev;

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



    /*--------------------*/
    /* Open USB-IO Device */
    /*--------------------*/
    dev = find_morphy_usbio(dev);
    if (dev == NULL)
    {
        //    USB-IO�f�o�C�X��������Ȃ������ꍇ
        printf( "ERR>Cannot find any USB-IO device.\n" );
        return (MY_FALSE);
    }
    
    usbio_device = open_morphy(dev);
    if (usbio_device == NULL)
    {
        //    USB-IO�f�o�C�X���I�[�v���ł��Ȃ������ꍇ
        printf( "ERR>Cannot Open a USB-IO.\n" );
        return (MY_FALSE);
    }

    return (MY_TRUE);
}
