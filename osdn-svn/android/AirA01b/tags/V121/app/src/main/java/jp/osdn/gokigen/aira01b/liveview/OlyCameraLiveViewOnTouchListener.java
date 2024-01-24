package jp.osdn.gokigen.aira01b.liveview;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import jp.osdn.gokigen.aira01b.R;
import jp.osdn.gokigen.aira01b.liveview.phonecamera.IPhoneCameraShutter;
import jp.osdn.gokigen.aira01b.olycamerawrapper.IOlyCameraCoordinator;
import jp.osdn.gokigen.aira01b.preference.ICameraPropertyAccessor;

/**
 *
 *
 */
class OlyCameraLiveViewOnTouchListener  implements View.OnClickListener, View.OnTouchListener
{
    private final String TAG = toString();
    private final Context context;
    private IOlyCameraCoordinator camera = null;
    private IPhoneCameraShutter phoneShutter = null;
    private IStatusViewDrawer statusDrawer = null;
    private ILiveImageStatusNotify liveImageView = null;
    private final SharedPreferences preferences;

    OlyCameraLiveViewOnTouchListener(Context context)
    {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    void prepareInterfaces(IOlyCameraCoordinator cameraCoordinator, IPhoneCameraShutter phoneShutter, IStatusViewDrawer statusDrawer, ILiveImageStatusNotify liveImageView)
    {
        this.camera = cameraCoordinator;
        this.phoneShutter = phoneShutter;
        this.statusDrawer = statusDrawer;
        this.liveImageView = liveImageView;
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        Log.v(TAG, "onClick() : " + id);
        switch (id)
        {
            case R.id.shutterImageView:
                pushShutterButton();
                break;

            case R.id.phoneCameraView:
                //phoneShutter.onTouchedPreviewArea();
                break;

            case R.id.manualFocusImageView:
                camera.toggleManualFocus();
                break;

            case R.id.AutoFocusLockImageView:
                camera.unlockAutoFocus();
                break;

            case R.id.AutoExposureLockImageView:
                camera.toggleAutoExposure();
                break;

            case R.id.buildImageView:
                camera.configure_expert();
                break;

            case R.id.configImageView:
                camera.configure();
                break;

            case R.id.FocusAssistImageView:
                liveImageView.toggleFocusAssist();
                statusDrawer.updateFocusAssistStatus();
                break;

            case R.id.showGridSettingView:
                liveImageView.toggleShowGridFrame();
                statusDrawer.updateGridFrameStatus();
                break;

            case R.id.favoriteSettingsImageView:
                statusDrawer.showFavoriteSettingDialog();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int id = v.getId();
        Log.v(TAG, "onTouch() : " + id);
        if (id == R.id.cameraLiveImageView)
        {
            return (camera.driveAutoFocus(event));
        }
        else if (id == R.id.phoneCameraView)
        {
            phoneShutter.onTouchedPreviewArea();
            return (true);
        }
        return (false);
    }

    /**
     *   シャッターボタンが押された！
     *   （現在は、連続撮影モードやムービー撮影についてはまだ非対応）
     */
    private void pushShutterButton()
    {
        boolean isShootOnlyCamera = false;
        if (preferences != null)
        {
            isShootOnlyCamera = preferences.getBoolean(ICameraPropertyAccessor.SHOOT_ONLY_CAMERA, false);
            String showSampleImage = preferences.getString(ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE, ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE_DEFAULT_VALUE);
            if (!showSampleImage.equals("0"))
            {
                // 作例表示モードのとき...スマホ内蔵カメラでは撮影しない
                isShootOnlyCamera = true;
            }
        }

        // カメラ側のシャッターを押す
        camera.singleShot();
        if (!isShootOnlyCamera)
        {
            // スマートフォンのシャッターを切る
            phoneShutter.onPressedPhoneShutter();
        }
        else
        {
            // パラメータが ON (ONLY CAMERA)の時は、スマートフォン側の撮影は行わない。
            // （本体カメラのシャッターを切らない時だけ、Toastで通知する。）
            Toast.makeText(context, R.string.shoot_camera, Toast.LENGTH_SHORT).show();
        }
    }
}
