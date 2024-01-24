package jp.osdn.gokigen.aira01b.olycamerawrapper;

import android.content.Context;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.osdn.gokigen.aira01b.IAirA01BInterfacesProvider;
import jp.osdn.gokigen.aira01b.R;
import jp.osdn.gokigen.aira01b.preference.ICameraPropertyAccessor;

/**
 *   Olympusカメラとの接続処理
 *
 */
public class CameraConnectSequence implements Runnable
{
    private final Context context;
    private final OLYCamera camera;
    private final ICameraStatusReceiver cameraStatusReceiver;
    private final ICameraPropertyAccessor propertyAccessor;

    /**
     *   コンストラクタ
     */
    public CameraConnectSequence(Context context, OLYCamera camera, IAirA01BInterfacesProvider provider)
    {
        this.context = context;
        this.camera =camera;
        this.cameraStatusReceiver = provider.getStatusReceiver();
        this.propertyAccessor = provider.getPropertyAccessor();
    }

    /**
     *   カメラとの接続実処理
     *
     */
    @Override
    public void run()
    {
        String statusMessage = context.getString(R.string.connect_start);
        try
        {
            statusMessage = context.getString(R.string.connect_check_wifi);
            cameraStatusReceiver.onStatusNotify(statusMessage);
            camera.connect(OLYCamera.ConnectionType.WiFi);

            statusMessage = context.getString(R.string.connect_change_run_mode);
            cameraStatusReceiver.onStatusNotify(statusMessage);
            camera.changeRunMode(OLYCamera.RunMode.Recording);

            statusMessage = context.getString(R.string.connect_restore_camera_settings);
            cameraStatusReceiver.onStatusNotify(statusMessage);
            propertyAccessor.restoreCameraSettings(null);
       }
        catch (OLYCameraKitException e)
        {
            cameraStatusReceiver.onCameraOccursException(statusMessage, e);
            e.printStackTrace();
            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        // カメラとの接続確立を通知する
        cameraStatusReceiver.onStatusNotify(context.getString(R.string.connect_connected));
        cameraStatusReceiver.onCameraConnected();
    }
}
