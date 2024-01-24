package jp.osdn.gokigen.aira01a;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;

/**
 *  カメラの制御と状態を管理するクラス
 *
 * Created by MRSa on 2016/04/30.
 */
public class CameraController implements ITakePictureRequestedControl
{
    private final String TAG = this.toString();
    private LiveViewFragment parent = null;
    private OLYCamera camera = null;
    private boolean enabledFocusLock = false;

    /**
     *   constructor
     *
     * @param parent  親のFragment
     */
    public CameraController(LiveViewFragment parent)
    {
        this.parent = parent;
    }

    /**
     *   OLYMPUS Cameraオブジェクトの登録
     *
     * @param camera  OLYMPUS Camera オブジェクト
     */
    public void setCamera(OLYCamera camera)
    {
        this.camera = camera;
    }

    /**
     *  タッチシャッターが有効かどうかを応答する
     *
     * @return   true : enable touch shutter / false : disable touch shutter
     */
    public boolean getTouchShutterStatus()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent.getActivity());
        return (preferences.getBoolean("touch_shutter", false));
    }

    /**
     *
     *
     * @param isSelected
     */
    @Override
    public void setShutterImageSelected(boolean isSelected)
    {
        parent.setShutterImageSelected(isSelected);
    }

    /**
     *
     *
     */
    @Override
    public boolean getFocusFrameStatus()
    {
        return (enabledFocusLock);
    }

    /**
     *
     *
     * @param isShow
     */
    @Override
    public void setFocusFrameStatus(boolean isShow)
    {
        enabledFocusLock = isShow;
    }

    /**
     *
     *
     */
    @Override
    public void hideFocusFrame()
    {
        parent.getLiveImageView().hideFocusFrame();
    }

    /**
     *
     *
     * @param rect
     * @param status
     */
    @Override
    public void showFocusFrame(RectF rect, CameraLiveImageView.FocusFrameStatus status)
    {
        parent.getLiveImageView().showFocusFrame(rect, status);
    }

    /**
     *
     *
     * @param rect
     * @param status
     * @param duration
     */
    @Override
    public void showFocusFrame(RectF rect, CameraLiveImageView.FocusFrameStatus status, double duration)
    {
        parent.getLiveImageView().showFocusFrame(rect, status, duration);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public float getIntrinsicContentSizeWidth()
    {
        return (parent.getLiveImageView().getIntrinsicContentSizeWidth());
    }

    /**
     *
     *
     * @return
     */
    @Override
    public float getIntrinsicContentSizeHeight()
    {
        return (parent.getLiveImageView().getIntrinsicContentSizeHeight());
    }

    /**
     *
     * @param resId
     * @param message
     */
    @Override
    public void presentMessage(int resId, String message)
    {
        parent.presentMessage(parent.getString(resId), message);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Activity getActivity()
    {
        return (parent.getActivity());
    }

    /**
     *   電動ズーム機能を持つレンズが装着されているか確認
     *
     * @return  true ; 電動ズーム付き / false : 電動ズームなし
     */
    public boolean isElectricZoomLens()
    {
        return (camera.getLensMountStatus()).contains("electriczoom");
    }

    /**
     *   現在ズーム中か確認する
     *
     * @return  true : ズーム中  / false : ズーム中でない
     */
    public boolean isZooming()
    {
        return (camera.isDrivingZoomLens());
    }

    /**
     *   ズームレンズを動作させる
     *
     * @param direction ズームさせる方向 (+ズームイン / - ズームアウト)
     */
    public void driveZoomLens(int direction)
    {
        try
        {
            // レンズがサポートする焦点距離と、現在の焦点距離を取得する
            float minLength = camera.getMinimumFocalLength();
            float maxLength = camera.getMaximumFocalLength();
            float targetFocalLength = camera.getActualFocalLength();

            if (direction > 0)
            {
                // ズームインする
                // TODO: ステップズームにしたい
                targetFocalLength = targetFocalLength * 1.1f;
            }
            else
            {
                // ズームアウトする
                // TODO: ステップズームにしたい
                targetFocalLength = targetFocalLength * 0.9f;
            }

            // 焦点距離が最大値・最小値を超えないようにする
            if (targetFocalLength > maxLength)
            {
                targetFocalLength = maxLength;
            }
            if (targetFocalLength < minLength)
            {
                targetFocalLength = minLength;
            }

            // レンズをズーム動作する
            camera.startDrivingZoomLensToFocalLength(targetFocalLength);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     *   指定したプロパティが設定可能か確認する
     *
     * @return  設定可否
     */
    public boolean canSetCameraProperty(String name)
    {
        return (camera.canSetCameraProperty(name));
    }

    /**
     *  設定したカメラのプロパティの値を取得する
     *
     * @return  カメラプロパティ
     */
    public String getCameraPropertyValue(String name)
    {
        String property = "";
        try {
            property = camera.getCameraPropertyValue(name);
        }
        catch (OLYCameraKitException e)
        {
            e.printStackTrace();
        }
        return (property);

    }

    /**
     *   カメラのプロパティ名（表示用）を取得する
     *
     * @return  カメラプロパティ表示タイトル
     */
    public String getCameraPropertyValueTitle(String name)
    {
        try
        {
            return (camera.getCameraPropertyValueTitle(camera.getCameraPropertyValue(name)));
        }
        catch (OLYCameraKitException e)
        {
            e.printStackTrace();
        }
        return ("");
    }

    /**
     *   現在のISO感度を応答する
     *
     * @return ISO感度
     */
    public String getActualIsoSensitivity()
    {
        String value = camera.getCameraPropertyValueTitle(camera.getActualIsoSensitivity());
        if (value == null)
        {
            value = "";
        }
        return (value);
    }

    /**
     *  現在の焦点距離を応答する
     *
     * @return  焦点距離 (17mm といった文字列)
     */
    public String getFocalLength()
    {
        return (String.format(Locale.ENGLISH, "%3.0fmm", camera.getActualFocalLength()));
    }

    /**
     *  ISO感度を更新する
     *
     */
    public void changeIsoSensitivity()
    {
        parent.presentPropertyValueList(parent.getIsoSensitivityHolder(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setPropertyValue(parent.getIsoSensitivityHolder(), which);
            }
        });
    }

    /**
     *  シャッタースピードを更新する
     *
     */
    public void changeShutterSpeed()
    {
        parent.presentPropertyValueList(parent.getShutterSpeedHolder(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setPropertyValue(parent.getShutterSpeedHolder(), which);
            }
        });
    }

    /**
     *   絞り値を更新する
     *
     *
     */
    public void changeApertureValue()
    {
        parent.presentPropertyValueList(parent.getApertureHolder(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                setPropertyValue(parent.getApertureHolder(), which);
            }
        });
    }

    /**
     *   ホワイトバランスを更新する
     *
     */
    public void changeWhiteBalance()
    {
        parent.presentPropertyValueList(parent.getWhiteBalanceHolder(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                setPropertyValue(parent.getWhiteBalanceHolder(), which);
                finishWhiteBalance();
            }
        });
    }

    /**
     *   ホワイトバランスの更新処理（後処理）
     *
     */
    private void finishWhiteBalance()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parent.updateWhiteBalanceImageView();
            }
        });
    }

    /**
     *   ドライブモードを更新する
     *
     *
     */
    public void changeDriveMode()
    {
        parent.presentPropertyValueList(parent.getDrivemodeHolder(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                setPropertyValue(parent.getDrivemodeHolder(), which);
                finishDriveMode();
            }
        });
    }

    /**
     *   ドライブモードの更新後処理
     *
     */
    private void finishDriveMode()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                parent.updateDrivemodeImageView();
            }
        });
    }

    /**
     *
     *
     */
    public void changeExposureCompensation()
    {
        parent.presentPropertyValueList(parent.getExposureCompensationHolder(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                setPropertyValue(parent.getExposureCompensationHolder(), which);
            }
        });
    }

    /**
     *
     *
     */
    public void changeTakeMode()
    {
        parent.presentPropertyValueList(parent.getTakemodeHolder(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                setPropertyValue(parent.getTakemodeHolder(), which);
                finishTakeMode();
            }
        });
    }

    /**
     *
     *
     */
    private void finishTakeMode()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                parent.updateTakemodeTextView();
                try
                {
                    camera.clearAutoFocusPoint();
                    camera.unlockAutoFocus();
                }
                catch (OLYCameraKitException e)
                {
                    e.printStackTrace();
                }
                enabledFocusLock = false;
                parent.getLiveImageView().hideFocusFrame();
            }
        });
    }

    /**
     *   カメラを撮影モードに切り替える
     *
     * @return  true : 切り替え成功 / false : 切替え失敗
     */
    public boolean changeToRecordingMode()
    {
        Log.v(TAG, "changeToRecordingMode()");
        if (camera.getRunMode() == OLYCamera.RunMode.Recording)
        {
            Log.v(TAG, "changeToRecordingMode() End");
            return (true);
        }
        boolean changeMode = changeRunMode(OLYCamera.RunMode.Recording);
        restoreCameraSettings();
        Log.v(TAG, "changeToRecordingMode() End");
        return (changeMode);
    }

    /**
     *  カメラを再生モードに切り替える
     *
     * @return  true : 切り替え成功 / false : 切替え失敗
     */
    public boolean changeToPlaybackMode()
    {
        Log.v(TAG, "changeToPlaybackMode()");
        if (camera.getRunMode() == OLYCamera.RunMode.Playback)
        {
            Log.v(TAG, "changeToPlaybackMode() End");
            return (true);
        }
        storeCameraSettings();
        boolean ret = changeRunMode(OLYCamera.RunMode.Playback);
        Log.v(TAG, "changeToPlaybackMode() End");
        return (ret);
    }

    /**
     *   カメラの動作モードを切り替える
     *
     * @param mode
     * @return
     */
    private boolean changeRunMode(OLYCamera.RunMode mode)
    {
        boolean ret = false;
        try
        {
            //dumpCameraPropertyValues();
            camera.changeRunMode(mode);
            ret = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (ret);
    }

    /**
     *  カメラのプロパティを取得してログに出力する
     *  （デバッグ用）
     */
    private void dumpCameraPropertyValues()
    {
        try
        {
            Map<String, String> values = null;
            values = camera.getCameraPropertyValues(camera.getCameraPropertyNames());
            if (values != null) {
                for (String key : values.keySet()) {
                    Log.v(TAG, "dumpCameraPropertyValues(): " + values.get(key));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   カメラの現在の設定を本体から読みだしてPreferenceに記憶する
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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        if (camera.isConnected())
        {
            Map<String, String> values = new HashMap<String, String>();
            Set<String> names = camera.getCameraPropertyNames();
            for (String name : names)
            {
                String value = preferences.getString(name, null);
                if (value != null)
                {
                    if (!CameraPropertyListenerImpl.checkReadOnlyProperty(name))
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
     *   現在の動作モードを取得する
     * @return
     */
    public OLYCamera.RunMode getRunMode()
    {
        return (camera.getRunMode());
    }

    /**
     *　 プロパティの一覧を取得する
     *
     * @param propertyName
     * @return
     */
    public List<String> getPropertyList(String propertyName)
    {
        try
        {
            return (camera.getCameraPropertyValueList(propertyName));
        }
        catch (OLYCameraKitException e)
        {
            e.printStackTrace();
            return (null);
        }
    }

    /**
     *   プロパティを設定する
     *
     * @param holder
     * @param which
     */
    private void setPropertyValue(CameraPropertyHolder holder, int which)
    {
        try
        {
            holder.getTargetView().setSelected(false);
            String value = holder.getValueList().get(which);
            if (value != null)
            {
                //Log.v(TAG, "SET VALUE:" + holder.getPropertyName() + " (" + value + ")");
                camera.setCameraPropertyValue(holder.getPropertyName(), value);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   UIスレッドでタスクを実行する
     *
     * @param action
     */
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
