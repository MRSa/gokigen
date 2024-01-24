package jp.osdn.gokigen.aira01b.olycamerawrapper;

import android.content.Context;
import android.util.Log;

import java.util.Map;
import java.util.TreeSet;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraStatusListener;
import jp.osdn.gokigen.aira01b.liveview.ICameraStatusDisplay;
import jp.osdn.gokigen.aira01b.R;

/**
 *   OLYCameraStatusListenerの実装
 *   (LiveViewFragment用)
 */
public class CameraStatusListenerImpl implements OLYCameraStatusListener, ICameraStatusSummary
{
    private final String TAG = this.toString();

    public static final String APERTURE_VALUE = "ActualApertureValue";
    public static final String SHUTTER_SPEED = "ActualShutterSpeed";
    public static final String EXPOSURE_COMPENSATION = "ActualExposureCompensation";
    public static final String ISO_SENSITIVITY = "ActualIsoSensitivity";
    public static final String RECORDABLEIMAGES = "RemainingRecordableImages";
    public static final String MEDIA_BUSY = "MediaBusy";
    public static final String MEDIA_ERROR = "MediaError";
    public static final String DETECT_FACES = "DetectedHumanFaces";
    public static final String FOCAL_LENGTH = "ActualFocalLength";
    public static final String ACTUAL_ISO_SENSITIVITY_WARNING = "ActualIsoSensitivityWarning";
    public static final String EXPOSURE_WARNING = "ExposureWarning";
    public static final String EXPOSURE_METERING_WARNING = "ExposureMeteringWarning";
    public static final String HIGH_TEMPERATURE_WARNING = "HighTemperatureWarning";
    public static final String LEVEL_GAUGE = "LevelGauge";
    public static final String LENS_MOUNT_STATUS = "LensMountStatus";
    public static final String MEDIA_MOUNT_STATUS = "MediaMountStatus";
    public static final String REMAINING_RECORDABLE_TIME = "RemainingRecordableTime";
    public static final String MINIMUM_FOCAL_LENGTH = "MinimumFocalLength";
    public static final String MAXIMUM_FOCAL_LENGTH = "MaximumFocalLength";

    private final ICameraStatusDisplay display;
    private final Context context;

    /**
     *   コンストラクタ
     *
     */
    public CameraStatusListenerImpl(Context context, ICameraStatusDisplay parent)
    {
        this.context = context;
        this.display = parent;
    }

    @Override
    public void onUpdateStatus(OLYCamera camera, final String name)
    {
        if (name == null)
        {
            // name がないとき、何もしない
            return;
        }
        switch (name)
        {
            case APERTURE_VALUE:
            case SHUTTER_SPEED:
            case ISO_SENSITIVITY:
            case FOCAL_LENGTH:
            case EXPOSURE_WARNING:
            case EXPOSURE_METERING_WARNING:
            case HIGH_TEMPERATURE_WARNING:
                // ある一定の値が更新された時だけ
                break;

            default:
                // 他の値が変わった場合には、ログだけ残して何もしない。
                Log.v(TAG, "onUpdateStatus() : " + name);
                return;
            //break;
        }
        display.updateCameraStatus(geCameraStatusMessage(camera, name));
    }

    /**
     *  表示用のメッセージを生成する
     *
     */
    @Override
    public String geCameraStatusMessage(OLYCamera camera, String name)
    {
        String message = name;
        String warn = "";
        try
        {
            // 警告メッセージを生成
            if (camera.isHighTemperatureWarning())
            {
                warn = warn + " " + context.getString(R.string.high_temperature_warning);
            }
            if (camera.isExposureMeteringWarning())
            {
                warn = warn + " " + context.getString(R.string.exposure_metering_warning);
            }
            if (camera.isActualIsoSensitivityWarning())
            {
                warn = warn + " " + context.getString(R.string.iso_sensitivity_warning);
            }

            TreeSet<String> treeSet = new TreeSet<>();
            treeSet.add(IOlyCameraProperty.TAKE_MODE);
            treeSet.add(IOlyCameraProperty.WB_MODE);
            treeSet.add(IOlyCameraProperty.AE_MODE);
            treeSet.add(IOlyCameraProperty.EXPOSURE_COMPENSATION);
            Map<String, String> values = camera.getCameraPropertyValues(treeSet);
            //for (Map.Entry<String, String> entry : values.entrySet())
            //{
            //    Log.v(TAG, "STATUS : " + entry.getKey() + " : " + entry.getValue());
            //}
            String takeMode = camera.getCameraPropertyValueTitle(values.get(IOlyCameraProperty.TAKE_MODE));
            String wbMode = camera.getCameraPropertyValueTitle(values.get(IOlyCameraProperty.WB_MODE));
            String aeMode = camera.getCameraPropertyValueTitle(values.get(IOlyCameraProperty.AE_MODE));
            String aperture = camera.getCameraPropertyValueTitle(camera.getActualApertureValue());
            String iso = camera.getCameraPropertyValueTitle(camera.getActualIsoSensitivity());
            String shutter = camera.getCameraPropertyValueTitle(camera.getActualShutterSpeed());
            message = "  " + takeMode + " " + shutter + " F" + aperture + " ISO" + iso + " " + wbMode + " [" + aeMode + "]" + warn;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (message);
    }
}
