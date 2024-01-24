package jp.osdn.gokigen.aira01b.olycamerawrapper;

import android.content.Context;
import android.view.MotionEvent;

import jp.co.olympus.camerakit.OLYCameraLiveViewListener;
import jp.co.olympus.camerakit.OLYCameraStatusListener;

/**
 *
 *
 */
public interface IOlyCameraCoordinator
{
    // WIFI 接続系
    void startWatchWifiStatus(Context context);
    void stopWatchWifiStatus(Context context);
    boolean isWatchWifiStatus();

    // Preference設定系 ...本来はここからサービスしないほうがよさ気
    void configure();
    void configure_expert();

    /** カメラ接続系 **/
    void disconnect(final boolean powerOff);
    void connect();

    /** ライブビュー関係 **/
    void changeLiveViewSize(String size);
    void setLiveViewListener(OLYCameraLiveViewListener listener);
    void startLiveView();
    void stopLiveView();

    /** オートフォーカス機能の実行 **/
    boolean driveAutoFocus(MotionEvent event);
    void unlockAutoFocus();

    /** シングル撮影機能の実行 **/
    void singleShot();

    /** AE Lockの設定・解除、 AF/MFの切替え **/
    void toggleAutoExposure();
    void toggleManualFocus();

    /** カメラの状態取得 **/
    boolean isManualFocus();
    boolean isAFLock();
    boolean isAELock();

    /** GPS関連 **/
    void setGeolocation(String nmeaLocation);
    void clearGeolocation();

    /** カメラの状態変化リスナの設定 **/
    void setCameraStatusListener(OLYCameraStatusListener listener);

    /** カメラの状態サマリ(のテキスト情報)を取得する **/
    String getCameraStatusSummary(ICameraStatusSummary decoder);

    // カメラプロパティアクセスインタフェース
    IOlyCameraPropertyProvider getCameraPropertyProvider();

    // カメラプロパティのロード・セーブインタフェース
    ILoadSaveCameraProperties getLoadSaveCameraProperties();

    // カメラの動作モード変更インタフェース
    ICameraRunMode getChangeRunModeExecutor();
}
