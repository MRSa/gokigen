/**
 * ------------------------------------------------------------------------------
 *   cameraControl.c  :  キャプチャサイズを設定する
 *
 * ------------------------------------------------------------------------------
 */
#include <highgui.h>
#include "mydefine.h"

void setCameraCaptureSize(CvCapture *capture, int cameraSize)
{
    if (cameraSize == MY_CAMERASIZE_VGA)
    {
        // VGAサイズの画像にする
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, (double) 640.0);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, (double) 480.0);
    }
    else if (cameraSize == MY_CAMERASIZE_QVGA)
    {
        // QVGAサイズの画像にする
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, (double) 320.0);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, (double) 240.0);
    }
    else // if (cameraSize == MY_CAMERASIZE_HQVGA)
    {
        // HQVGAサイズの画像にする
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, (double) 160.0);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, (double) 120.0);
    }
}
