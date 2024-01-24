package jp.osdn.gokigen.aira01a;

import android.app.Activity;
import android.util.Log;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraStatusListener;

/**
 *   OLYCameraStatusListenerの実装
 *   (LiveViewFragment用)
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
    public static final String CAMERA_STATUS_MEDIA_ERROR = "MediaError";
    public static final String CAMERA_STATUS_DETECT_FACES = "DetectedHumanFaces";
    public static final String CAMERA_STATUS_FOCAL_LENGTH = "ActualFocalLength";
    public static final String CAMERA_STATUS_ACTUAL_ISO_SENSITIITY_WARNING = "ActualIsoSensitivityWarning";
    public static final String CAMERA_STATUS_EXPOSURE_WARNING = "ExposureWarning";
    public static final String CAMERA_STATUS_EXPOSURE_METERING_WARNING = "ExposureMeteringWarning";
    public static final String CAMERA_STATUS_HIGH_TEMPERATURE_WARNING = "HighTemperatureWarning";
    public static final String CAMERA_STATUS_LEVEL_GAUGE = "LevelGauge";

    /**
            // まだ実装していないステータス ... たぶん必要ない...
            "LensMountStatus"              // レンズマウント状態
            "MediaMountStatus"             // メディアマウント状態
            "RemainingRecordableTime"      // 撮影動画の最大秒数
            "MinimumFocalLength"           // 最小焦点距離
            "MaximumFocalLength"           // 最大焦点距離
     **/

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
                if (name.equals(CAMERA_STATUS_APERTURE_VALUE))
                {
                    parent.updateApertureValueTextView();
                } else if (name.equals(CAMERA_STATUS_SHUTTER_SPEED))
                {
                    parent.updateShutterSpeedTextView();
                } else if (name.equals(CAMERA_STATUS_EXPOSURE_COMPENSATION))
                {
                    parent.updateExposureCompensationTextView();
                } else if (name.equals(CAMERA_STATUS_ISO_SENSITIVITY))
                {
                    parent.updateIsoSensitivityTextView();
                } else if (name.equals(CAMERA_STATUS_RECORDABLEIMAGES) ||
                            name.equals(CAMERA_STATUS_MEDIA_BUSY) ||
                            name.equals(CAMERA_STATUS_MEDIA_ERROR))
                {
                    parent.updateRemainingRecordableImagesTextView();
                } else if (name.equals(CAMERA_STATUS_DETECT_FACES))
                {
                    parent.detectedHumanFaces();
                } else if (name.equals(CAMERA_STATUS_FOCAL_LENGTH))
                {
                    parent.updateFocalLengthView();
                } else if (name.equals(CAMERA_STATUS_EXPOSURE_WARNING) ||
                            name.equals(CAMERA_STATUS_EXPOSURE_METERING_WARNING) ||
                            name.equals(CAMERA_STATUS_ACTUAL_ISO_SENSITIITY_WARNING) ||
                            name.equals(CAMERA_STATUS_HIGH_TEMPERATURE_WARNING))
                {
                    parent.updateWarningTextView();
                } else if (name.equals(CAMERA_STATUS_LEVEL_GAUGE))
                {
                    // デジタル水準器の情報が更新された
                }
                else
                {
                    // まだ実装していない状態変化をロギングする
                    // Log.v(TAG, "onUpdateStatus() :" + name);
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
