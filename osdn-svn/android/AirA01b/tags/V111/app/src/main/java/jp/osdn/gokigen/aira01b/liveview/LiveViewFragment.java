package jp.osdn.gokigen.aira01b.liveview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.osdn.gokigen.aira01b.MyInterfaceProvider;
import jp.osdn.gokigen.aira01b.R;
import jp.osdn.gokigen.aira01b.liveview.phonecamera.PhoneCameraView;
import jp.osdn.gokigen.aira01b.olycamerawrapper.CameraStatusListenerImpl;
import jp.osdn.gokigen.aira01b.olycamerawrapper.ICameraRunMode;
import jp.osdn.gokigen.aira01b.olycamerawrapper.IOlyCameraCoordinator;
import jp.osdn.gokigen.aira01b.preference.ICameraPropertyAccessor;

/**
 *  撮影用ライブビュー画面
 *
 */
public class LiveViewFragment extends Fragment implements IStatusViewDrawer, ICameraStatusDisplay
{
    private final String TAG = this.toString();

    private IOlyCameraCoordinator camera = null;
    private MyInterfaceProvider factory = null;
    private ICameraRunMode changeRunModeExecutor = null;
    private OlyCameraLiveViewOnTouchListener onTouchListener = null;
    private CameraLiveViewListenerImpl liveViewListener = null;
    private CameraStatusListenerImpl statusListener = null;

    private TextView statusArea = null;
    private CameraLiveImageView imageView = null;

    private ImageView manualFocus = null;
    private ImageView afLock = null;
    private ImageView aeLock = null;
    private ImageView focusAssist = null;
    private ImageView showGrid = null;

    private boolean imageViewCreated = false;
    private View myView = null;
    private String messageValue = "";

    /**
     *
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        if (liveViewListener == null)
        {
            liveViewListener = new CameraLiveViewListenerImpl();
        }
        if (onTouchListener == null)
        {
            onTouchListener = new OlyCameraLiveViewOnTouchListener(getContext());
        }
        if (statusListener == null)
        {
            statusListener = new CameraStatusListenerImpl(getContext(), this);
        }
    }

    /**
     *
     *
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Log.v(TAG, "onAttach()");
    }

    /**
     *
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_live_view, container, false);
        if (imageViewCreated)
        {
            Log.v(TAG, "onCreateView() : called again, so do nothing...");
            return (myView);
        }

        imageView = (CameraLiveImageView) view.findViewById(R.id.cameraLiveImageView);
        imageView.setOnClickListener(onTouchListener);
        imageView.setOnTouchListener(onTouchListener);

        liveViewListener.setCameraLiveImageView(imageView);
        if (factory != null)
        {
            factory.setAutoFocusFrameDisplay(imageView);
        }

        PhoneCameraView phoneCameraView = (PhoneCameraView) view.findViewById(R.id.phoneCameraView);
        phoneCameraView.setOnClickListener(onTouchListener);
        phoneCameraView.setOnTouchListener(onTouchListener);

        // カメラ画像の大きさを動的に調整（したい）
        //phoneCameraView.getViewTreeObserver().addOnGlobalLayoutListener(phoneCameraView);

        ImageView shutter = (ImageView) view.findViewById(R.id.shutterImageView);
        shutter.setOnClickListener(onTouchListener);

        ImageView config = (ImageView) view.findViewById(R.id.configImageView);
        config.setOnClickListener(onTouchListener);

        ImageView build = (ImageView) view.findViewById(R.id.buildImageView);
        build.setOnClickListener(onTouchListener);

        manualFocus = (ImageView) view.findViewById(R.id.manualFocusImageView);
        manualFocus.setOnClickListener(onTouchListener);

        afLock = (ImageView) view.findViewById(R.id.AutoFocusLockImageView);
        afLock.setOnClickListener(onTouchListener);

        aeLock = (ImageView) view.findViewById(R.id.AutoExposureLockImageView);
        aeLock.setOnClickListener(onTouchListener);

        focusAssist = (ImageView) view.findViewById(R.id.FocusAssistImageView);
        focusAssist.setOnClickListener(onTouchListener);

        showGrid = (ImageView) view.findViewById(R.id.showGridSettingView);
        showGrid.setOnClickListener(onTouchListener);

        statusArea = (TextView) view.findViewById(R.id.informationMessageTextView);

        onTouchListener.prepareInterfaces(camera, phoneCameraView, this, imageView);

        imageViewCreated = true;
        myView = view;
        return (view);
    }

    /**
     *
     *
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();
        Log.v(TAG, "onStart()");

    }

    /**
     *
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
        Log.v(TAG, "onResume() Start");

        // 撮影モードかどうかを確認して、撮影モードではなかったら撮影モードに切り替える
        if ((changeRunModeExecutor != null)&&(!changeRunModeExecutor.isRecordingMode()))
        {
            // Runモードを切り替える。（でも切り替えると、設定がクリアされてしまう...。
            changeRunModeExecutor.changeRunMode(true);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // ステータスの変更を通知してもらう
        camera.setCameraStatusListener(statusListener);

        // ステータスの初期情報を表示する
        updateStatusView(camera.getCameraStatusSummary(statusListener));

        // ライブビューの開始
        camera.changeLiveViewSize(preferences.getString(ICameraPropertyAccessor.LIVE_VIEW_QUALITY, ICameraPropertyAccessor.LIVE_VIEW_QUALITY_DEFAULT_VALUE));
        camera.setLiveViewListener(liveViewListener);
        liveViewListener.setCameraLiveImageView(imageView);
        camera.startLiveView();

        Log.v(TAG, "onResume() End");
    }

    /**
     *
     *
     */
    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(TAG, "onPause() Start");

        // ライブビューの停止
        camera.stopLiveView();
        camera.setLiveViewListener(null);
        liveViewListener.setCameraLiveImageView(null);

        Log.v(TAG, "onPause() End");
    }

    /**
     * カメラクラスをセットする
     *
     */
    public void setInterfaces(IOlyCameraCoordinator camera, MyInterfaceProvider factory)
    {
        Log.v(TAG, "setInterfaces()");
        this.camera = camera;
        this.factory = factory;
        this.changeRunModeExecutor = camera.getChangeRunModeExecutor();

        factory.setStatusInterface(this);
        factory.setStatusViewDrawer(this);
        //if (imageView != null)
        {
        //    factory.setAutoFocusFrameDisplay(imageView);
        }
    }

    @Override
    public void updateFocusAssistStatus()
    {
        updateCameraPropertyStatus();
    }

    @Override
    public void updateGridFrameStatus()
    {
        updateCameraPropertyStatus();
    }

    @Override
    public void updateTakeMode()
    {
        updateCameraPropertyStatus();
    }

    @Override
    public void updateDriveMode()
    {
        updateCameraPropertyStatus();
    }

    @Override
    public void updateWhiteBalance()
    {
        updateCameraPropertyStatus();
    }

    @Override
    public void updateBatteryLevel()
    {
        updateCameraPropertyStatus();
    }

    @Override
    public void updateAeMode()
    {
        updateCameraPropertyStatus();
    }

    @Override
    public void updateAeLockState()
    {
        updateCameraPropertyStatus();
    }

    @Override
    public void updateCameraStatus()
    {
        updateCameraPropertyStatus();
    }

    @Override
    public void updateCameraStatus(String message)
    {
        updateStatusView(message);
    }

    /**
     *
     *
     */
    private void updateCameraPropertyStatus()
    {
        runOnUiThread(new Runnable ()
        {
            /**
             *   カメラの状態(インジケータ)を更新する
             */
            @Override
            public void run()
            {
                if (camera == null)
                {
                    return;
                }
                Log.v(TAG, "--- UPDATE CAMERA PROPERTY (START) ---");
                if (manualFocus != null)
                {
                    manualFocus.setSelected(camera.isManualFocus());
                }
                if (afLock != null)
                {
                    afLock.setSelected(camera.isAFLock());
                }
                if (aeLock != null)
                {
                    aeLock.setSelected(camera.isAELock());
                }
                if ((focusAssist != null)&&(imageView != null))
                {
                    focusAssist.setSelected(imageView.isFocusAssist());
                }
                if ((showGrid != null)&&(imageView != null))
                {
                    showGrid.setSelected(imageView.isShowGrid());
                }
                Log.v(TAG, "--- UPDATE CAMERA PROPERTY (END) ---");
            }
        });
    }

    /**
     *   表示エリアに文字を表示する
     *
     */
    public void updateStatusView(String message)
    {
        messageValue = message;
        runOnUiThread(new Runnable()
        {
            /**
             * カメラの状態(ステータステキスト）を更新する
             * (ステータステキストは、プライベート変数で保持して、書き換える)
             */
            @Override
            public void run()
            {
                if (statusArea != null)
                {
                    statusArea.setText(messageValue);
                }
            }
        });
    }

    private void runOnUiThread(Runnable action)
    {
        Activity activity = getActivity();
        if (activity == null)
        {
            return;
        }
        activity.runOnUiThread(action);
    }
}
