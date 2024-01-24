package jp.osdn.gokigen.aira01a;

import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;

/**
 *
 *
 * Created by MRSa on 2016/04/29.
 */
public class LiveViewFragment extends Fragment implements IStatusViewDrawer
{
    private final String TAG = this.toString();
    private OLYCamera camera = null;

    private CameraLiveViewListenerImpl liveViewListener = null;
    private CameraPropertyListenerImpl propertyListener = null;
    private CameraRecordingListenerImpl recordingListener = null;
    private CameraRecordingSupportsListenerImpl recordingSupportsListener = null;
    private CameraStatusListenerImpl statusListener = null;
    private CameraViewOnTouchClickListenerImpl viewOnTouchClickListener = null;

    private CameraController cameraController = null;
    private CameraTakePicture cameraTakePicture = null;

    private ImageView unlockImageView = null;
    private CameraLiveImageView imageView = null;
    private ImageView batteryLevelImageView = null;
    private TextView remainingRecordableImagesTextView = null;
    private ImageView drivemodeImageView = null;
    private TextView takemodeTextView = null;
    private TextView shutterSpeedTextView = null;
    private TextView apertureValueTextView = null;
    private TextView exposureCompensationTextView = null;
    private TextView isoSensitivityTextView = null;
    private TextView focalLengthTextView = null;
    private ImageView whiteBalanceImageView = null;
    private ImageView settingImageView = null;
    private ImageView shutterImageView = null;
    private ImageView playbackImageView = null;
    private ImageView zoomOutImageView = null;
    private ImageView zoomInImageView = null;

    private CameraPropertyHolder exposureCompensationHolder = null;
    private CameraPropertyHolder drivemodeHolder = null;
    private CameraPropertyHolder takemodeHolder = null;
    private CameraPropertyHolder whiteBalanceHolder = null;
    private CameraPropertyHolder apertureHolder = null;
    private CameraPropertyHolder shutterSpeedHolder = null;
    private CameraPropertyHolder isoSensitivityHolder = null;

    // TODO: きれいにしたい
    @SuppressWarnings("serial")
    private static final Map<String, Integer> drivemodeIconList = new HashMap<String, Integer>() {
        {
            put("<TAKE_DRIVE/DRIVE_NORMAL>"  , R.drawable.icn_drive_setting_single);
            put("<TAKE_DRIVE/DRIVE_CONTINUE>", R.drawable.icn_drive_setting_seq_l);
        }
    };

    // TODO: きれいにしたい
    @SuppressWarnings("serial")
    private static final Map<String, Integer> whiteBalanceIconList = new HashMap<String, Integer>() {
        {
            put("<WB/WB_AUTO>"          , R.drawable.icn_wb_setting_wbauto);
            put("<WB/MWB_SHADE>"        , R.drawable.icn_wb_setting_16);
            put("<WB/MWB_CLOUD>"        , R.drawable.icn_wb_setting_17);
            put("<WB/MWB_FINE>"         , R.drawable.icn_wb_setting_18);
            put("<WB/MWB_LAMP>"         , R.drawable.icn_wb_setting_20);
            put("<WB/MWB_FLUORESCENCE1>", R.drawable.icn_wb_setting_35);
            put("<WB/MWB_WATER_1>"      , R.drawable.icn_wb_setting_64);
            put("<WB/WB_CUSTOM1>"       , R.drawable.icn_wb_setting_512);
        }
    };

    // TODO: きれいにしたい
    @SuppressWarnings("serial")
    private static final Map<String, Integer> batteryIconList = new HashMap<String, Integer>() {
        {
            put("<BATTERY_LEVEL/UNKNOWN>"       , R.drawable.tt_icn_battery_unknown);
            put("<BATTERY_LEVEL/CHARGE>"        , R.drawable.tt_icn_battery_charge);
            put("<BATTERY_LEVEL/EMPTY>"         , R.drawable.tt_icn_battery_empty);
            put("<BATTERY_LEVEL/WARNING>"       , R.drawable.tt_icn_battery_half);
            put("<BATTERY_LEVEL/LOW>"           , R.drawable.tt_icn_battery_middle);
            put("<BATTERY_LEVEL/FULL>"          , R.drawable.tt_icn_battery_full);
            put("<BATTERY_LEVEL/EMPTY_AC>"       , R.drawable.tt_icn_battery_supply_empty);
            put("<BATTERY_LEVEL/SUPPLY_WARNING>", R.drawable.tt_icn_battery_supply_half);
            put("<BATTERY_LEVEL/SUPPLY_LOW>"    , R.drawable.tt_icn_battery_supply_middle);
            put("<BATTERY_LEVEL/SUPPLY_FULL>"   , R.drawable.tt_icn_battery_supply_full);
        }
    };

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");

        if (cameraController == null)
        {
            cameraController = new CameraController(this);
        }
        if ((cameraTakePicture == null)&&(camera != null)&&(cameraController != null))
        {
            cameraTakePicture = new CameraTakePicture(camera, cameraController);
        }
        if (liveViewListener == null)
        {
            liveViewListener = new CameraLiveViewListenerImpl();
        }
        if (recordingListener == null)
        {
            recordingListener = new CameraRecordingListenerImpl(this);
        }
        if (propertyListener == null)
        {
            propertyListener = new CameraPropertyListenerImpl(this);
        }
        if (recordingSupportsListener == null)
        {
            recordingSupportsListener = new CameraRecordingSupportsListenerImpl(this);
        }
        if (statusListener == null)
        {
            statusListener = new CameraStatusListenerImpl(this);
        }
    }

    /**
     * @param context
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Log.v(TAG, "onAttach()");
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_live_view, container, false);

        //
        prepareViewObjectHolders(view);

        if (liveViewListener == null)
        {
            liveViewListener = new CameraLiveViewListenerImpl();
        }
        liveViewListener.setCameraLiveImageView(imageView);

        if (recordingListener == null)
        {
            recordingListener = new CameraRecordingListenerImpl(this);
        }
        recordingListener.setImageView(shutterImageView);

        //
        preparePropertyHolders();

        if (viewOnTouchClickListener == null)
        {
            viewOnTouchClickListener = new CameraViewOnTouchClickListenerImpl(this, cameraController, cameraTakePicture);
        }
        // Click または touch された時のリスナクラスを設定する
        imageView.setOnTouchListener(viewOnTouchClickListener);
        shutterImageView.setOnTouchListener(viewOnTouchClickListener);
        drivemodeImageView.setOnClickListener(viewOnTouchClickListener);
        takemodeTextView.setOnClickListener(viewOnTouchClickListener);
        shutterSpeedTextView.setOnClickListener(viewOnTouchClickListener);
        apertureValueTextView.setOnClickListener(viewOnTouchClickListener);
        exposureCompensationTextView.setOnClickListener(viewOnTouchClickListener);
        isoSensitivityTextView.setOnClickListener(viewOnTouchClickListener);
        whiteBalanceImageView.setOnClickListener(viewOnTouchClickListener);
        settingImageView.setOnClickListener(viewOnTouchClickListener);
        unlockImageView.setOnClickListener(viewOnTouchClickListener);
        playbackImageView.setOnClickListener(viewOnTouchClickListener);
        zoomInImageView.setOnClickListener(viewOnTouchClickListener);
        zoomOutImageView.setOnClickListener(viewOnTouchClickListener);

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
        Log.v(TAG, "onResume()");

/*
        // ダイアログを表示する(念のため...)
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.dialog_start_wait_message));
        dialog.setTitle(getString(R.string.dialog_start_wait_title));
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
*/
        controlTouchShutter();
        if (camera != null)
        {
            camera.setLiveViewListener(liveViewListener);
            camera.setCameraPropertyListener(propertyListener);
            camera.setCameraStatusListener(statusListener);
            camera.setRecordingListener(recordingListener);
            camera.setRecordingSupportsListener(recordingSupportsListener);
            if (cameraController != null)
            {
                if (cameraTakePicture == null)
                {
                    cameraTakePicture = new CameraTakePicture(camera, cameraController);
                }

                cameraController.changeToRecordingMode();

                /** ズームレンズのボタンを表示するかどうかを決める **/
                if (cameraController.isElectricZoomLens())
                {
                    updateZoomButton(View.VISIBLE);
                }
                else
                {
                    updateZoomButton(View.INVISIBLE);
                }
            }
        }
        updateView();
        resetAutoFocus();

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
        Log.v(TAG, "onPause()");

        if (camera != null)
        {
            camera.setLiveViewListener(null);
            camera.setCameraPropertyListener(null);
            camera.setCameraStatusListener(null);
            camera.setRecordingListener(null);
            camera.setRecordingSupportsListener(null);
        }
    }

    /**
     * カメラクラスをセットする
     *
     * @param camera
     */
    public void setCamera(OLYCamera camera)
    {
        Log.v(TAG, "setCamera()");
        this.camera = camera;
        if (cameraController == null)
        {
            cameraController = new CameraController(this);
        }
        cameraController.setCamera(camera);
    }

    /**
     *
     *
     */
    private void updateView()
    {
        updateDrivemodeImageView();
        updateTakemodeTextView();
        updateShutterSpeedTextView();
        updateApertureValueTextView();
        updateExposureCompensationTextView();
        updateIsoSensitivityTextView();
        updateFocalLengthView();
        updateWhiteBalanceImageView();
        updateBatteryLevelImageView();
        updateRemainingRecordableImagesTextView();
    }

    /**
     *
     *
     */
    private void resetAutoFocus()
    {
        cameraTakePicture.resetAutoFocus();
    }

    public boolean isFocusPointArea(PointF point)
    {
        // If the focus point is out of area, ignore the touch.
        return (imageView.isContainsPoint(point));
    }

    public PointF getPointWithEvent(MotionEvent event)
    {
        return (imageView.getPointWithEvent(event));
    }

    /**
     *
     *
     */
    private void controlTouchShutter()
    {
        boolean touchShutterStatus = cameraController.getTouchShutterStatus();
        unlockImageView.setVisibility(touchShutterStatus ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     *
     *
     */
    public void updateDrivemodeImageView()
    {
        drivemodeImageView.setEnabled(drivemodeHolder.canSetCameraProperty());

        String drivemode = drivemodeHolder.getCameraPropertyValue();
        if (drivemodeIconList.containsKey(drivemode))
        {
            int resId = drivemodeIconList.get(drivemode);
            drivemodeImageView.setImageResource(resId);
        }
        else
        {
            drivemodeImageView.setImageDrawable(null);
        }
    }

    public void updateTakemodeTextView()
    {
        takemodeTextView.setEnabled(takemodeHolder.canSetCameraProperty());
        takemodeTextView.setText(takemodeHolder.getCameraPropertyValueTitle());

        // Changing take mode may have an influence for drive mode and white balance.
        updateDrivemodeImageView();
        updateShutterSpeedTextView();
        updateApertureValueTextView();
        updateExposureCompensationTextView();
        updateIsoSensitivityTextView();
        updateWhiteBalanceImageView();
        updateFocalLengthView();
    }

    public void updateShutterSpeedTextView()
    {
        shutterSpeedTextView.setEnabled(shutterSpeedHolder.canSetCameraProperty());
        shutterSpeedTextView.setText(shutterSpeedHolder.getCameraPropertyValueTitle());
    }

    public void updateApertureValueTextView()
    {
        apertureValueTextView.setEnabled(apertureHolder.canSetCameraProperty());
        String title = apertureHolder.getCameraPropertyValueTitle();
        if (title == null)
        {
            title = "";
        }
        else
        {
            title = String.format("F%s", title);
        }
        apertureValueTextView.setText(title);
    }

    public void updateExposureCompensationTextView()
    {
        exposureCompensationTextView.setEnabled(exposureCompensationHolder.canSetCameraProperty());
        exposureCompensationTextView.setText(exposureCompensationHolder.getCameraPropertyValueTitle());
    }

    public void updateIsoSensitivityTextView()
    {
        isoSensitivityTextView.setEnabled(isoSensitivityHolder.canSetCameraProperty());
        String titlePrefix = "ISO";
        String title = isoSensitivityHolder.getCameraPropertyValueTitle();
        if ("Auto".equals(title))
        {
            titlePrefix = "ISO-A";
            title = cameraController.getActualIsoSensitivity();
        }
        isoSensitivityTextView.setText(String.format("%s\n%s" ,titlePrefix, title));
    }

    /**
     *
     */
    @Override
    public void updateFocalLengthView()
    {
        focalLengthTextView.setText(cameraController.getFocalLength());
    }

    public void updateWhiteBalanceImageView()
    {
        whiteBalanceImageView.setEnabled(whiteBalanceHolder.canSetCameraProperty());
        String value = whiteBalanceHolder.getCameraPropertyValue();
        if (whiteBalanceIconList.containsKey(value))
        {
            int resId = whiteBalanceIconList.get(value);
            whiteBalanceImageView.setImageResource(resId);
        }
        else
        {
            whiteBalanceImageView.setImageDrawable(null);
        }
    }

    public void updateBatteryLevelImageView()
    {
        String value = cameraController.getCameraPropertyValue(CameraPropertyListenerImpl.CAMERA_PROPERTY_BATTERY_LEVEL);
        if (batteryIconList.containsKey(value))
        {
            int resId = batteryIconList.get(value);
            if (resId != 0) {
                batteryLevelImageView.setImageResource(resId);
            } else {
                batteryLevelImageView.setImageDrawable(null);
            }
        } else {
            batteryLevelImageView.setImageDrawable(null);
        }
    }

    public void updateRemainingRecordableImagesTextView()
    {
        final String text;
        if (camera.isConnected() || camera.getRunMode() == OLYCamera.RunMode.Recording)
        {
            if (camera.isMediaBusy())
            {
                text = "BUSY";
            }
            else
            {
                text = String.format(Locale.getDefault(), "%d", camera.getRemainingImageCapacity());
            }
        }
        else
        {
            text = "???";
        }
        remainingRecordableImagesTextView.setText(text);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    public void presentMessage(String title, String message)
    {
        Context context = getActivity();
        if (context == null)
        {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message);
        builder.show();
    }

    /**
     *
     * @param holder
     * @param listener
     */
    public void presentPropertyValueList(CameraPropertyHolder holder, DialogInterface.OnClickListener listener)
    {
        holder.prepare();
        holder.getTargetView().setSelected(true);
        List<String> list = holder.getValueList();
        String initialValue = holder.getCameraPropertyValue();

        FragmentActivity activity = getActivity();
        if (activity == null)
        {
            return;
        }

        String[] items = new String[list.size()];
        for (int ii = 0; ii < items.length; ++ii)
        {
            items[ii] = camera.getCameraPropertyValueTitle(list.get(ii));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setSingleChoiceItems(items, list.indexOf(initialValue), listener);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                drivemodeImageView.setSelected(false);
                takemodeTextView.setSelected(false);
                shutterSpeedTextView.setSelected(false);
                apertureValueTextView.setSelected(false);
                exposureCompensationTextView.setSelected(false);
                isoSensitivityTextView.setSelected(false);
                whiteBalanceImageView.setSelected(false);
            }
        });
        builder.show();
    }

    /**
     *
     *
     * @return CameraLiveImageView オブジェクト
     */
    public CameraLiveImageView getLiveImageView()
    {
        return (imageView);
    }

    /**
     *   設定項目を表示する
     *
     */
    public void transToSettingFragment()
    {
        SettingFragment fragment = new SettingFragment();
        fragment.setCamera(camera);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_in);
        transaction.replace(getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     *   カメラ内の画像を表示する
     *
     */
    public void transToPlaybackFragment()
    {
        ImageGridViewFragment fragment = new ImageGridViewFragment();
        fragment.setCamera(camera);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_in);
        transaction.replace(getId(), fragment); // transaction.replace(R.id.gragment1, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setShutterImageSelected(boolean isSelected)
    {
        shutterImageView.setSelected(isSelected);
    }

    public CameraPropertyHolder getExposureCompensationHolder()
    {
        return (exposureCompensationHolder);
    }

    public CameraPropertyHolder getTakemodeHolder()
    {
        return (takemodeHolder);
    }
    public CameraPropertyHolder getDrivemodeHolder()
    {
        return (drivemodeHolder);
    }

    public CameraPropertyHolder getWhiteBalanceHolder()
    {
        return (whiteBalanceHolder);
    }

    public CameraPropertyHolder getApertureHolder()
    {
        return (apertureHolder);
    }

    public CameraPropertyHolder getShutterSpeedHolder()
    {
        return (shutterSpeedHolder);
    }

    public CameraPropertyHolder getIsoSensitivityHolder()
    {
        return (isoSensitivityHolder);
    }

    private void preparePropertyHolders()
    {
        if (cameraController == null)
        {
            cameraController = new CameraController(this);
        }
        exposureCompensationHolder = new CameraPropertyHolder(CameraPropertyListenerImpl.CAMERA_PROPERTY_EXPOSURE_COMPENSATION, exposureCompensationTextView, cameraController);
        drivemodeHolder = new CameraPropertyHolder(CameraPropertyListenerImpl.CAMERA_PROPERTY_DRIVE_MODE, drivemodeImageView, cameraController);
        takemodeHolder =  new CameraPropertyHolder(CameraPropertyListenerImpl.CAMERA_PROPERTY_TAKE_MODE, takemodeTextView, cameraController);
        whiteBalanceHolder = new CameraPropertyHolder(CameraPropertyListenerImpl.CAMERA_PROPERTY_WHITE_BALANCE, whiteBalanceImageView, cameraController);
        apertureHolder =  new CameraPropertyHolder(CameraPropertyListenerImpl.CAMERA_PROPERTY_APERTURE_VALUE, apertureValueTextView, cameraController);
        shutterSpeedHolder = new CameraPropertyHolder(CameraPropertyListenerImpl.CAMERA_PROPERTY_SHUTTER_SPEED, shutterSpeedTextView, cameraController);
        isoSensitivityHolder = new CameraPropertyHolder(CameraPropertyListenerImpl.CAMERA_PROPERTY_ISO_SENSITIVITY, isoSensitivityTextView, cameraController);
    }

    private void prepareViewObjectHolders(View view)
    {
        imageView = (CameraLiveImageView) view.findViewById(R.id.cameraLiveImageView);
        batteryLevelImageView = (ImageView) view.findViewById(R.id.batteryLevelImageView);
        remainingRecordableImagesTextView = (TextView) view.findViewById(R.id.remainingRecordableImagesTextView);
        drivemodeImageView = (ImageView) view.findViewById(R.id.drivemodeImageView);
        takemodeTextView = (TextView) view.findViewById(R.id.takemodeTextView);
        shutterSpeedTextView = (TextView) view.findViewById(R.id.shutterSpeedTextView);
        apertureValueTextView = (TextView) view.findViewById(R.id.apertureValueTextView);
        exposureCompensationTextView = (TextView) view.findViewById(R.id.exposureCompensationTextView);
        isoSensitivityTextView = (TextView) view.findViewById(R.id.isoSensitivityTextView);
        focalLengthTextView = (TextView) view.findViewById(R.id.focalLengthTextView);
        whiteBalanceImageView = (ImageView) view.findViewById(R.id.whiteBalaneImageView);
        shutterImageView = (ImageView) view.findViewById(R.id.shutterImageView);
        settingImageView = (ImageView) view.findViewById(R.id.settingImageView);
        unlockImageView = (ImageView) view.findViewById(R.id.unlockImageView);
        playbackImageView = (ImageView) view.findViewById(R.id.showPlaybackImageView);
        zoomInImageView = (ImageView) view.findViewById(R.id.zoomInImageView);
        zoomOutImageView = (ImageView) view.findViewById(R.id.zoomOutImageView);
    }

    /**
     *  ズームボタンの表示を切り替える
     *
     * @param visibility
     */
    private void updateZoomButton(int visibility)
    {
        if (zoomInImageView != null)
        {
            zoomInImageView.setVisibility(visibility);
        }
        if (zoomOutImageView != null)
        {
            zoomOutImageView.setVisibility(visibility);
        }
    }

}
