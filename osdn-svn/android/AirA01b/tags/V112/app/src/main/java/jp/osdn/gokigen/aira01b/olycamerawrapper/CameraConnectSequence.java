package jp.osdn.gokigen.aira01b.olycamerawrapper;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.osdn.gokigen.aira01b.IAirA01BInterfacesProvider;
import jp.osdn.gokigen.aira01b.preference.ICameraPropertyAccessor;

/**
 *   Olympusカメラとの接続処理
 *
 */
public class CameraConnectSequence implements Runnable
{
    private final OLYCamera camera;
    private final ICameraStatusReceiver cameraStatusReceiver;
    private final ICameraPropertyAccessor propertyAccessor;

    /**
     *   コンストラクタ
     */
    public CameraConnectSequence(OLYCamera camera, IAirA01BInterfacesProvider provider)
    {
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
        String statusMessage = "start";
        try
        {
            statusMessage = "connectWifi";
            cameraStatusReceiver.onStatusNotify(statusMessage);
            camera.connect(OLYCamera.ConnectionType.WiFi);

            statusMessage = "changeRunMode";
            cameraStatusReceiver.onStatusNotify(statusMessage);
            camera.changeRunMode(OLYCamera.RunMode.Recording);

            statusMessage = "restoreCameraSettings";
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
        cameraStatusReceiver.onStatusNotify("connected");
        cameraStatusReceiver.onCameraConnected();
    }
}
