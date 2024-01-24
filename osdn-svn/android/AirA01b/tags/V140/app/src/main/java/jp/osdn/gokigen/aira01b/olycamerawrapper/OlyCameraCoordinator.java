package jp.osdn.gokigen.aira01b.olycamerawrapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.MotionEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.co.olympus.camerakit.OLYCameraLiveViewListener;
import jp.co.olympus.camerakit.OLYCameraStatusListener;
import jp.osdn.gokigen.aira01b.IAirA01BInterfacesProvider;
import jp.osdn.gokigen.aira01b.R;
import jp.osdn.gokigen.aira01b.liveview.IAutoFocusFrameDisplay;
import jp.osdn.gokigen.aira01b.liveview.ICameraStatusDisplay;

/**
 *   OlyCameraCoordinator : Olympus Air との接続、切断の間をとりもつクラス。
 *                         (OLYCameraクラスの実体を保持する)
 *
 *    1. クラスを作成する
 *    2. connectWifi() でカメラと接続する
 *    3. disconnect() でカメラと切断する
 *
 *    X. onDisconnectedByError() でカメラの通信状態が変更されたことを受信する
 *    o. CameraInteractionCoordinator.ICameraCallback でカメラとの接続状態を通知する
 *
 */
public class OlyCameraCoordinator implements OLYCameraConnectionListener, IOlyCameraCoordinator, IIndicatorControl, ICameraRunMode, IOLYCameraObjectProvider
{
    private final String TAG = toString();
    private final Context context;
    private final Executor cameraExecutor = Executors.newFixedThreadPool(1);
    private final BroadcastReceiver connectionReceiver;
    private final OLYCamera camera;
    private final IAirA01BInterfacesProvider interfaceProvider;

    // 本クラスの配下のカメラ制御クラス群
    private final AutoFocusControl autoFocus;
    private final SingleShotControl singleShot;
    private final OlyCameraPropertyProxy propertyProxy;
    private final LoadSaveCameraProperties loadSaveCameraProperties;

    private boolean isWatchingWifiStatus = false;
    private boolean isManualFocus = false;
    private boolean isAutoFocusLocked = false;
    private boolean isExposureLocked = false;

    /**
     * コンストラクタ
     */
    public OlyCameraCoordinator(Context context, IAirA01BInterfacesProvider interfaceProvider)
    {
        this.interfaceProvider = interfaceProvider;
        this.context = context;

        // OLYMPUS CAMERA クラスの初期化、リスナの設定
        camera = new OLYCamera();
        camera.setContext(context.getApplicationContext());
        camera.setConnectionListener(this);

        // 本クラスの配下のカメラ制御クラス群の設定
        autoFocus = new AutoFocusControl(camera, interfaceProvider, this); // AF制御
        singleShot = new SingleShotControl(camera, interfaceProvider, this);  // 撮影
        propertyProxy = new OlyCameraPropertyProxy(camera); // カメラプロパティ

        loadSaveCameraProperties = new LoadSaveCameraProperties(context, propertyProxy, this);

        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceiveBroadcastOfConnection(context, intent);
            }
        };
    }

    /**
     * Wifi接続状態の監視
     * (接続の実処理は onReceiveBroadcastOfConnection() で実施)
     */
    @Override
    public void startWatchWifiStatus(Context context)
    {
        interfaceProvider.getStatusReceiver().onStatusNotify("prepare");

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(connectionReceiver, filter);
        isWatchingWifiStatus = true;
    }

    /**
     * Wifi接続状態の監視終了
     */
    @Override
    public void stopWatchWifiStatus(Context context)
    {
        context.unregisterReceiver(connectionReceiver);
        isWatchingWifiStatus = false;
        disconnect(false);
    }

    /**
     * Wifi接続状態の監視処理を行っているかどうか
     *
     * @return true : 監視中 / false : 停止中
     */
    @Override
    public boolean isWatchWifiStatus() {
        return (isWatchingWifiStatus);
    }

    /**
     *   設定系...
     *
     */
    @Override
    public void configure()
    {
        // カメラの設定画面へ切り替える
        interfaceProvider.getChangeSceneCoordinator().changeSceneToConfiguration();
    }

    /**
     *   詳細設定系...
     */
    @Override
    public void configure_expert()
    {
        // カメラプロパティ一覧画面へ切り替える
        interfaceProvider.getChangeSceneCoordinator().changeSceneToCameraPropertyList();
    }

    /**
     * 　 カメラとの接続を解除する
     *
     * @param powerOff 真ならカメラの電源オフを伴う
     */
    @Override
    public void disconnect(final boolean powerOff) {
        disconnectFromCamera(powerOff);
        interfaceProvider.getStatusReceiver().onCameraDisconnected();
    }

    /**
     * カメラとの再接続を指示する
     */
    @Override
    public void connect() {
        connectToCamera();
    }

    /**
     * カメラの通信状態変化を監視するためのインターフェース
     *
     * @param camera 例外が発生した OLYCamera
     * @param e      カメラクラスの例外
     */
    @Override
    public void onDisconnectedByError(OLYCamera camera, OLYCameraKitException e) {
        // カメラが切れた時に通知する
        interfaceProvider.getStatusReceiver().onCameraDisconnected();
    }

    /**
     * Wifiが使える状態だったら、カメラと接続して動作するよ
     */
    private void onReceiveBroadcastOfConnection(Context context, Intent intent) {
        interfaceProvider.getStatusReceiver().onStatusNotify(context.getString(R.string.connect_check_wifi));

        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            if (wifiManager.isWifiEnabled() && info != null && info.getNetworkId() != -1) {
                // カメラとの接続処理を行う
                connectToCamera();
            }
        }
    }

    /**
     * カメラとの切断処理
     */
    private void disconnectFromCamera(final boolean powerOff) {
        try {
            cameraExecutor.execute(new CameraDisconnectSequence(camera, interfaceProvider.getPropertyAccessor(), powerOff));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * カメラとの接続処理
     */
    private void connectToCamera()
    {
        try {
            cameraExecutor.execute(new CameraConnectSequence(context, camera, interfaceProvider));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ライブビューの設定
     */
    public void setLiveViewListener(OLYCameraLiveViewListener listener) {
        try {
            camera.setLiveViewListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビューの解像度を設定する
     *
     */
    @Override
    public void changeLiveViewSize(String size)
    {
        try {
            camera.changeLiveViewSize(CameraPropertyUtilities.toLiveViewSizeType(size));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビューの開始
     *
     */
    @Override
    public void startLiveView()
    {
        try {
            camera.startLiveView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *   ライブビューの終了
     *
     */
    @Override
    public void stopLiveView()
    {
        try {
            camera.stopLiveView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * フォーカスロックの実行
     */
    public boolean driveAutoFocus(MotionEvent event)
    {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
        {
            return (false);
        }
        IAutoFocusFrameDisplay frameDisplay = interfaceProvider.getAutoFocusFrameInterface();
        if (frameDisplay != null)
        {
            PointF point = frameDisplay.getPointWithEvent(event);
            if (frameDisplay.isContainsPoint(point))
            {
                return (autoFocus.lockAutoFocus(point));
            }
        }
        return (false);
    }

    /**
     * フォーカスロックの解除
     */
    public void unlockAutoFocus()
    {
        autoFocus.unlockAutoFocus();
        IAutoFocusFrameDisplay focusFrame = interfaceProvider.getAutoFocusFrameInterface();
        if (focusFrame != null)
        {
            focusFrame.hideFocusFrame();
        }
        isAutoFocusLocked = false;
    }

    /**
     * 画像を１枚撮影
     */
    public void singleShot() {
        singleShot.singleShot();
    }

    @Override
    public void toggleAutoExposure()
    {
        try
        {
            if (isExposureLocked)
            {
                Log.v(TAG, "toggleAutoExposure() : unlockAutoExposure()");
                camera.unlockAutoExposure();
            }
            else
            {
                Log.v(TAG, "toggleAutoExposure() : lockAutoExposure()");
                camera.lockAutoExposure();
            }
            updateIndicatorScreen(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void toggleManualFocus()
    {
        try
        {
            boolean isHideFocusFrame = false;
            String property_name = IOlyCameraProperty.FOCUS_STILL;
            String poverty_value = "<" + IOlyCameraProperty.FOCUS_STILL + "/";

            // マニュアルフォーカス切替え
            if (!isManualFocus)
            {
                // AF -> MF  : オートフォーカスを解除して設定する
                Log.v(TAG, "toggleManualFocus() : to " + IOlyCameraProperty.FOCUS_MF);
                poverty_value = poverty_value + IOlyCameraProperty.FOCUS_MF + ">";
                camera.unlockAutoFocus();
                camera.setCameraPropertyValue(property_name, poverty_value);
                isHideFocusFrame = true;
            }
            else
            {
                // MF -> AF
                Log.v(TAG, "toggleManualFocus() : to " + IOlyCameraProperty.FOCUS_SAF);
                poverty_value = poverty_value + IOlyCameraProperty.FOCUS_SAF + ">";
                camera.setCameraPropertyValue(property_name, poverty_value);
            }
            updateIndicatorScreen(isHideFocusFrame);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateIndicatorScreen(boolean isHideFocusFrame)
    {
        isManualFocus();
        if (interfaceProvider != null)
        {
            if (isHideFocusFrame)
            {
                isAutoFocusLocked = false;
                IAutoFocusFrameDisplay focusFrame = interfaceProvider.getAutoFocusFrameInterface();
                if (focusFrame != null)
                {
                    focusFrame.hideFocusFrame();
                }
            }
            ICameraStatusDisplay display = interfaceProvider.getCameraStatusInterface();
            if (display != null)
            {
                display.updateCameraStatus();
            }
        }
    }

    @Override
    public boolean isManualFocus()
    {
        isManualFocus = propertyProxy.isManualFocus();
        return (isManualFocus);
    }

    @Override
    public boolean isAFLock()
    {
        return (isAutoFocusLocked);
    }

    @Override
    public boolean isAELock()
    {
        isExposureLocked = propertyProxy.isExposureLocked();
        return (isExposureLocked);
    }

    @Override
    public void setCameraStatusListener(OLYCameraStatusListener listener)
    {
        camera.setCameraStatusListener(listener);
    }

    @Override
    public String getCameraStatusSummary(ICameraStatusSummary decoder)
    {
        return (decoder.geCameraStatusMessage(camera, ""));
    }

    @Override
    public void changeRunMode(boolean isRecording)
    {
        OLYCamera.RunMode runMode = (isRecording) ? OLYCamera.RunMode.Recording : OLYCamera.RunMode.Playback;
        Log.v(TAG, "changeRunMode() : " + runMode);
        try
        {
            camera.changeRunMode(runMode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRecordingMode()
    {
        boolean isRecordingMode = false;
        try
        {
            OLYCamera.RunMode runMode = camera.getRunMode();
            isRecordingMode =  (runMode == OLYCamera.RunMode.Recording);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return (isRecordingMode);
    }

    @Override
    public IOlyCameraPropertyProvider getCameraPropertyProvider()
    {
        return (propertyProxy);
    }

    @Override
    public ILoadSaveCameraProperties getLoadSaveCameraProperties()
    {
        return (loadSaveCameraProperties);
    }

    @Override
    public ICameraRunMode getChangeRunModeExecutor()
    {
        return (this);
    }

    @Override
    public void onAfLockUpdate(boolean isAfLocked)
    {
        isAutoFocusLocked = isAfLocked;
        updateIndicatorScreen(false);
    }

    @Override
    public OLYCamera getOLYCamera()
    {
        return (camera);
    }
}
