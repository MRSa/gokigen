package jp.osdn.gokigen.aira01a;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;

/**
 * CameraInteractionCoordinator : Olympus Air との接続、切断の間をとりもつクラス。
 *                                (OLYCameraクラスの実体を保持する)
 *
 *    1. クラスを作成する
 *    2. prepare() で接続の準備を行う
 *    3. connect() でカメラと接続する
 *    4. disconnect() でカメラと切断する
 *
 *    X. onDisconnectedByError() でカメラの通信状態が変更されたことを受信する
 *    o. CameraInteractionCoordinator.ICameraCallback でカメラとの接続状態を通知する
 *
 */
public class CameraCoordinator implements OLYCameraConnectionListener
{
    private final String TAG = this.toString();
    private Activity parent = null;
    private ICameraCallback callbackReceiver = null;
    private IStatusView statusView = null;
    private Executor connectionExecutor = Executors.newFixedThreadPool(1);
    private BroadcastReceiver connectionReceiver;
    private OLYCamera camera = null;
    private String connectStatusMessage = "";
    private boolean isConnect = false;

    /**
     * コンストラクタ
     */
    public CameraCoordinator(Activity context, ICameraCallback receiver)
    {
        this.parent = context;
        this.callbackReceiver = receiver;

        // OLYMPUS CAMERA クラスの初期化、リスナの設定
        camera = new OLYCamera();
        camera.setContext(context.getApplicationContext());
        camera.setConnectionListener(this);

        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceiveBroadcastOfConnection(context, intent);
            }
        };
    }

    /**
     * クラスの初期化準備を行う
     */
    public boolean prepare(IStatusView statusView)
    {
        this.statusView = statusView;
        if (callbackReceiver == null)
        {
            return (false);
        }
        return (true);
    }

    /**
     * カメラとの接続
     * (接続の実処理は onReceiveBroadcastOfConnection() で実施)
     */
    public void connect()
    {
        connectStatusMessage = "prepare";
        updateStatus();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        parent.registerReceiver(connectionReceiver, filter);
        isConnect = true;
    }

    /**
     * カメラから切断
     */
    public void disconnect()
    {
        isConnect = false;
        parent.unregisterReceiver(connectionReceiver);
        disconnect(false);
    }

    /**
     *   接続処理を行っているかどうかを応答する
     *
     * @return  true : 接続中 / false : 切断中
     */
    public boolean isConnect()
    {
        return (isConnect);
    }

    /**
     *   Wifiが使える状態だったら、カメラと接続して動作するよ
     *
     */
    private void onReceiveBroadcastOfConnection(Context context, Intent intent)
    {
        connectStatusMessage = "check wifi";
        updateStatus();

        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            WifiManager wifiManager = (WifiManager)parent.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            if (wifiManager.isWifiEnabled() && info != null && info.getNetworkId() != -1)
            {
                startConnectingCamera();
            }
        }
    }

    /**
     *   現在状態をを表示する
     *
     */
    private void updateStatus()
    {
        parent.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if ((statusView != null)&&(connectStatusMessage != null))
                {
                    statusView.setInformationText(connectStatusMessage);
                }
            }
        });

    }

    /**
     *   カメラとの接続処理...LiveViewの開始まで
     *
     */
    private void startConnectingCamera()
    {
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
                try
                {
                    connectStatusMessage = "connect";
                    updateStatus();
                    camera.connect(OLYCamera.ConnectionType.WiFi);

                    connectStatusMessage = "changeLiveViewSize";
                    updateStatus();
                    camera.changeLiveViewSize(toLiveViewSize(preferences.getString("live_view_quality", "QVGA")));

                    connectStatusMessage = "changeRunMode";
                    updateStatus();
                    camera.changeRunMode(OLYCamera.RunMode.Recording);

                    connectStatusMessage = "restoreCameraSettings";
                    updateStatus();
                    // SharedPreferenceに記録していた前回のカメラ設定値を書き戻す
                    restoreCameraSettings();
                    if (!camera.isAutoStartLiveView())
                    {
                        connectStatusMessage = "startLiveView";
                        updateStatus();
                        camera.startLiveView();
                    }
                }
                catch (OLYCameraKitException e)
                {
                    callbackReceiver.onCameraOccursException(connectStatusMessage, e);
                    return;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return;
                }

                connectStatusMessage = "connected";
                updateStatus();

                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callbackReceiver.onCameraConnected(camera);
                    }
                });
            }
        });
    }

    /**
     *　 カメラとの接続を解除する
     *
     * @param powerOff 真ならカメラの電源オフを伴う
     */
    public void disconnect(final boolean powerOff)
    {
        callbackReceiver.onCameraDisconnected();

        connectionExecutor.execute(new Runnable() {
            @Override
            public void run()
            {
                // カメラの設定値をSharedPreferenceに記録する
                storeCameraSettings();

                // カメラをPowerOffして接続を切る
                try
                {
                    camera.disconnectWithPowerOff(powerOff);
                }
                catch (OLYCameraKitException e)
                {
                    // エラー情報をログに出力する
                    Log.w(TAG, "To disconnect from the camera is failed. : " + e.getLocalizedMessage());
                }
            }
        });
    }

    /**
     *   カメラの現在の設定を本体から読みだして記憶する
     *
     */
    private void storeCameraSettings()
    {
        // カメラから設定を読みだして、Preferenceに記録する
        if (camera.isConnected())
        {
            Map<String, String> values = null;
            try
            {
                values = camera.getCameraPropertyValues(camera.getCameraPropertyNames());
            }
            catch (OLYCameraKitException e)
            {
                Log.w(TAG, "To get the camera properties is failed: " + e.getMessage());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
	    
            if (values != null)
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
                SharedPreferences.Editor editor = preferences.edit();
                for (String key : values.keySet())
                {
                    editor.putString(key, values.get(key));
                    //Log.v(TAG, "storeCameraSettings(): " + values.get(key));
                }
                //editor.commit();
                editor.apply();
            }
        }
    }

    /**
     *   Preferenceにあるカメラの設定をカメラに登録する
     *　(注： Read Onlyなパラメータを登録しようとするとエラーになるので注意）
     */
    private void restoreCameraSettings()
    {
        // Restores my settings.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (camera.isConnected())
        {
            Map<String, String> values = new HashMap<String, String>();
            Set<String> names = camera.getCameraPropertyNames();
            for (String name : names)
            {
                String value = preferences.getString(name, null);
                if (value != null)
                {
                    if (!CameraPropertyListenerImpl.canSetCameraProperty(name))
                    {
                        // Read Onlyのプロパティを除外して登録
                        values.put(name, value);
                        //Log.v(TAG, "restoreCameraSettings(): " + value);
                    }
                }
            }
            if (values.size() > 0)
            {
                try
                {
                    camera.setCameraPropertyValues(values);
                }
                catch (OLYCameraKitException e)
                {
                    Log.w(TAG, "To change the camera properties is failed: " + e.getMessage());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *   toLiveViewSize() : スクリーンサイズの文字列から、OLYCamera.LiveViewSize型へ変換する
     *
     * @param quality スクリーンサイズ文字列
     * @return OLYCamera.LiveViewSize型
     */
    static final OLYCamera.LiveViewSize toLiveViewSize(String quality)
    {
        if (quality.equalsIgnoreCase("QVGA"))
        {
            return OLYCamera.LiveViewSize.QVGA;
        }
        else if (quality.equalsIgnoreCase("VGA"))
        {
            return OLYCamera.LiveViewSize.VGA;
        } else if (quality.equalsIgnoreCase("SVGA"))
        {
            return OLYCamera.LiveViewSize.SVGA;
        } else if (quality.equalsIgnoreCase("XGA"))
        {
            return OLYCamera.LiveViewSize.XGA;
        }
        return OLYCamera.LiveViewSize.QVGA;
    }

    /**
     *  カメラの通信状態変化を監視するためのインターフェース
     *
     * @param camera 例外が発生した OLYCamera
     * @param e  カメラクラスの例外
     */
    @Override
    public void onDisconnectedByError(OLYCamera camera, OLYCameraKitException e)
    {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callbackReceiver.onCameraDisconnected();
            }
        });
    }

    /**
     *   カメラとの再接続を指示する
     *
     */
    public void retryConnect()
    {
        startConnectingCamera();
    }

    /**
     *　 CameraInteractionCoordinatorクラスのcallback
     *
     */
    public interface ICameraCallback
    {
        void onCameraConnected(OLYCamera myCamera);
        void onCameraDisconnected();
        void onCameraOccursException(String message, Exception e);
    }

    public interface IStatusView
    {
        void setInformationText(String message);
    }
}
