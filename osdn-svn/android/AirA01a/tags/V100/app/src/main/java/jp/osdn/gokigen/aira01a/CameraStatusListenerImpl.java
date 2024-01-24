package jp.osdn.gokigen.aira01a;

import android.app.Activity;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraStatusListener;

/**
 *   OLYCameraStatusListenerの実装
 *   (LiveViewFragment用)
 * Created by MRSa on 2016/04/29.
 */
public class CameraStatusListenerImpl implements OLYCameraStatusListener
{
    private final String TAG = this.toString();

    public static final String CAMERA_STATUS_APERTURE_VALUE = "ActualApertureValue";
    public static final String CAMERA_STATUS_SHUTTER_SPEED = "ActualShutterSpeed";
    public static final String CAMERA_STATUS_EXPOSURE_COMPENSATION = "ActualExposureCompensation";
    public static final String CAMERA_STATUS_ISO_SENSITIVITY = "ActualIsoSensitivity";
    public static final String CAMERA_STATUS_RECORDABLEIMAGES = "RemainingRecordableImages";
    public static final String CAMERA_STATUS_MEDIA_BUSY = "MediaBusy";

    private LiveViewFragment parent = null;

    /**
     *   コンストラクタ
     *
     */
    public CameraStatusListenerImpl(LiveViewFragment parent)
    {
        this.parent = parent;
    }

    @Override
    public void onUpdateStatus(OLYCamera camera, final String name)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (name.equals(CAMERA_STATUS_APERTURE_VALUE)) {
                    parent.updateApertureValueTextView();
                } else if (name.equals(CAMERA_STATUS_SHUTTER_SPEED)) {
                    parent.updateShutterSpeedTextView();
                } else if (name.equals(CAMERA_STATUS_EXPOSURE_COMPENSATION)) {
                    parent.updateExposureCompensationTextView();
                } else if (name.equals(CAMERA_STATUS_ISO_SENSITIVITY)) {
                    parent.updateIsoSensitivityTextView();
                } else if (name.equals(CAMERA_STATUS_RECORDABLEIMAGES) || name.equals(CAMERA_STATUS_MEDIA_BUSY)) {
                    parent.updateRemainingRecordableImagesTextView();
                }
            }
        });
    }

    private void runOnUiThread(Runnable action)
    {
        Activity activity = parent.getActivity();
        if (activity == null)
        {
            return;
        }
        activity.runOnUiThread(action);
    }
}
