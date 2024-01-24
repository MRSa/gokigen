/**
 * ------------------------------------------------------------------------------
 *   finish.c  :  �A�v���P�[�V�����̏I������
 *
 * ------------------------------------------------------------------------------
 */
#include <highgui.h>

#include "mydefine.h"

#include "io_usbio.h"
extern struct usb_dev_handle *usbio_device;

/*
 *  �A�v���P�[�V�����̂��Ƃ��܂�
 */
void finish(MyConfiguration *config, MyWorkArea *work)
{
    // USB-IO�f�o�C�X�̌�n��
    if (usbio_device != NULL)
    {
        close_morphy(usbio_device);
    }

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

}
