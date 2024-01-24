/**
 * ------------------------------------------------------------------------------
 *   cameraControl.c  :  �L���v�`���T�C�Y��ݒ肷��
 *
 * ------------------------------------------------------------------------------
 */
#include <highgui.h>
#include "mydefine.h"

void setCameraCaptureSize(CvCapture *capture, int cameraSize)
{
    if (cameraSize == MY_CAMERASIZE_VGA)
    {
        // VGA�T�C�Y�̉摜�ɂ���
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, (double) 640.0);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, (double) 480.0);
    }
    else if (cameraSize == MY_CAMERASIZE_QVGA)
    {
        // QVGA�T�C�Y�̉摜�ɂ���
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, (double) 320.0);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, (double) 240.0);
    }
    else // if (cameraSize == MY_CAMERASIZE_HQVGA)
    {
        // HQVGA�T�C�Y�̉摜�ɂ���
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, (double) 160.0);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, (double) 120.0);
    }
}
