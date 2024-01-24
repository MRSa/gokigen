package jp.osdn.gokigen.aira01b.preference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import org.opencv.core.Core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;

import jp.osdn.gokigen.aira01b.IAirA01BInterfacesProvider;
import jp.osdn.gokigen.aira01b.R;
import jp.osdn.gokigen.aira01b.olycamerawrapper.IOlyCameraProperty;
import jp.osdn.gokigen.aira01b.olycamerawrapper.CameraPowerOff;
import jp.osdn.gokigen.aira01b.olycamerawrapper.ICameraRunMode;
import jp.osdn.gokigen.aira01b.olycamerawrapper.ICameraHardwareStatus;
import jp.osdn.gokigen.aira01b.olycamerawrapper.IOlyCameraPropertyProvider;

/**
 *   SettingFragment
 *
 */
public class PreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, PreferenceSynchronizer.IPropertySynchronizeCallback, IPreferenceIntentCaller
{
    private final String TAG = toString();
    private IOlyCameraPropertyProvider propertyInterface = null;
    private ICameraHardwareStatus hardwareStatusInterface = null;
    private ICameraRunMode changeRunModeExecutor = null;
    private CameraPowerOff powerOffController = null;
    private PreferenceScreenArbitrator screenArbitrator = null;
    private SharedPreferences preferences = null;
    private ProgressDialog busyDialog = null;
    private PreferenceSynchronizer preferenceSynchronizer = null;

    public void setInterface(Context context, IAirA01BInterfacesProvider factory, ICameraRunMode runModeExecutor) {
        Log.v(TAG, "setInterface()");
        this.propertyInterface = factory.getPropertyProvider();
        this.changeRunModeExecutor = runModeExecutor;
        hardwareStatusInterface = this.propertyInterface.getHardwareStatus();
        powerOffController = new CameraPowerOff(context, factory.getChangeSceneCoordinator());
        powerOffController.prepare();
        screenArbitrator = new PreferenceScreenArbitrator(factory.getChangeSceneCoordinator(), this);
        screenArbitrator.prepare();
    }

    /**
     *
     *
     */
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Log.v(TAG, "onAttach()");

        // Preference をつかまえる
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferenceSynchronizer == null) {
            preferenceSynchronizer = new PreferenceSynchronizer(this.propertyInterface, preferences, this);
        }

        // Preference を初期設定する
        initializePreferences();

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Preferenceの初期化...
     */
    private void initializePreferences() {
        Map<String, ?> items = preferences.getAll();
        SharedPreferences.Editor editor = preferences.edit();
        if (!items.containsKey(ICameraPropertyAccessor.FRAME_GRID)) {
            editor.putString(ICameraPropertyAccessor.FRAME_GRID, ICameraPropertyAccessor.FRAME_GRID_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.IMAGE_CONVERTER)) {
            editor.putString(ICameraPropertyAccessor.IMAGE_CONVERTER, ICameraPropertyAccessor.IMAGE_CONVERTER_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.TAKE_MODE)) {
            editor.putString(ICameraPropertyAccessor.TAKE_MODE, ICameraPropertyAccessor.TAKE_MODE_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.COLOR_TONE)) {
            editor.putString(ICameraPropertyAccessor.COLOR_TONE, ICameraPropertyAccessor.COLOR_TONE_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.AE_MODE)) {
            editor.putString(ICameraPropertyAccessor.AE_MODE, ICameraPropertyAccessor.AE_MODE_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.WB_MODE)) {
            editor.putString(ICameraPropertyAccessor.WB_MODE, ICameraPropertyAccessor.WB_MODE_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.EXPOSURE_COMPENSATION)) {
            editor.putString(ICameraPropertyAccessor.EXPOSURE_COMPENSATION, ICameraPropertyAccessor.EXPOSURE_COMPENSATION_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.SHUTTER_SPEED)) {
            editor.putString(ICameraPropertyAccessor.SHUTTER_SPEED, ICameraPropertyAccessor.SHUTTER_SPEED_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.APERTURE)) {
            editor.putString(ICameraPropertyAccessor.APERTURE, ICameraPropertyAccessor.APERTURE_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.ISO_SENSITIVITY)) {
            editor.putString(ICameraPropertyAccessor.ISO_SENSITIVITY, ICameraPropertyAccessor.ISO_SENSITIVITY_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.LIVE_VIEW_QUALITY)) {
            editor.putString(ICameraPropertyAccessor.LIVE_VIEW_QUALITY, ICameraPropertyAccessor.LIVE_VIEW_QUALITY_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.SOUND_VOLUME_LEVEL)) {
            editor.putString(ICameraPropertyAccessor.SOUND_VOLUME_LEVEL, ICameraPropertyAccessor.SOUND_VOLUME_LEVEL_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.RAW)) {
            editor.putBoolean(ICameraPropertyAccessor.RAW, true);
        }
        if (!items.containsKey(ICameraPropertyAccessor.SOUND_VOLUME_LEVEL)) {
            editor.putString(ICameraPropertyAccessor.SOUND_VOLUME_LEVEL, ICameraPropertyAccessor.SOUND_VOLUME_LEVEL_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.PHONE_CAMERA_ID)) {
            editor.putString(ICameraPropertyAccessor.PHONE_CAMERA_ID, ICameraPropertyAccessor.PHONE_CAMERA_ID_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.PHONE_CAMERA_ID)) {
            editor.putString(ICameraPropertyAccessor.PHONE_CAMERA_ROTATION, ICameraPropertyAccessor.PHONE_CAMERA_ROTATION_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.SHOOT_ONLY_CAMERA)) {
            editor.putBoolean(ICameraPropertyAccessor.SHOOT_ONLY_CAMERA, false);
        }
        if (!items.containsKey(ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE)) {
            editor.putString(ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE, ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE_DEFAULT_VALUE);
        }
        if (!items.containsKey(ICameraPropertyAccessor.USE_CUSTOM_SPLASH)) {
            editor.putBoolean(ICameraPropertyAccessor.USE_CUSTOM_SPLASH, false);
        }
        if (!items.containsKey(ICameraPropertyAccessor.SELECT_SAMPLE_IMAGE)) {
            editor.putString(ICameraPropertyAccessor.SELECT_SAMPLE_IMAGE, "");
        }
        editor.apply();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.v(TAG, "onCreatePreferences()");

        //super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        {
            final HashMap<String, String> sizeTable = new HashMap<>();
            sizeTable.put("QVGA", "(320x240)");
            sizeTable.put("VGA", "(640x480)");
            sizeTable.put("SVGA", "(800x600)");
            sizeTable.put("XGA", "(1024x768)");

            ListPreference liveViewQuality = (ListPreference) findPreference(ICameraPropertyAccessor.LIVE_VIEW_QUALITY);
            liveViewQuality.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String key = (String) newValue;
                    preference.setSummary(newValue + " " + sizeTable.get(key));
                    return (true);
                }
            });
            liveViewQuality.setSummary(liveViewQuality.getValue() + " " + sizeTable.get(liveViewQuality.getValue()));
        }
        {
            ListPreference listPref = (ListPreference) findPreference(ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE);
            listPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String value = getString(R.string.pref_show_sample_image_0); // newValue.equals("0")
                    if (newValue.equals("1")) {
                        value = getString(R.string.pref_show_sample_image_1);
                    }
                    preference.setSummary(value);
                    return (true);
                }
            });
        }

        findPreference("exit_application").setOnPreferenceClickListener(powerOffController);
        findPreference("playback_camera").setOnPreferenceClickListener(screenArbitrator);
        //findPreference("playback_phone").setOnPreferenceClickListener(screenArbitrator);

        findPreference("select_sample_image").setOnPreferenceClickListener(screenArbitrator);
        findPreference("select_splash_image").setOnPreferenceClickListener(screenArbitrator);
        findPreference("manipulate_image").setOnPreferenceClickListener(screenArbitrator);
    }

    /**
     * ハードウェアのサマリ情報を取得し設定する
     */
    private void setHardwareSummary() {
        // レンズ状態
        findPreference("lens_status").setSummary(hardwareStatusInterface.getLensMountStatus());

        // メディア状態
        findPreference("media_status").setSummary(hardwareStatusInterface.getMediaMountStatus());

        // 焦点距離
        String focalLength;
        float minLength = hardwareStatusInterface.getMinimumFocalLength();
        float maxLength = hardwareStatusInterface.getMaximumFocalLength();
        float actualLength = hardwareStatusInterface.getActualFocalLength();
        if (minLength == maxLength) {
            focalLength = String.format(Locale.ENGLISH, "%3.0fmm", actualLength);
        } else {
            focalLength = String.format(Locale.ENGLISH, "%3.0fmm - %3.0fmm (%3.0fmm)", minLength, maxLength, actualLength);
        }
        findPreference("focal_length").setSummary(focalLength);

        // カメラのバージョン
        try {
            Map<String, Object> hardwareInformation = hardwareStatusInterface.inquireHardwareInformation();
            findPreference("camera_version").setSummary((String) hardwareInformation.get(OLYCamera.HARDWARE_INFORMATION_CAMERA_FIRMWARE_VERSION_KEY));

            // 取得した一覧はログに出力する。)
            Log.v(TAG, "- - - - -");
            for (Map.Entry<String, Object> entry : hardwareInformation.entrySet()) {
                String value = (String) entry.getValue();
                Log.v(TAG, entry.getKey() + " : " + value);
            }
            Log.v(TAG, "- - - - -");
        } catch (Exception e) {
            findPreference("camera_version").setSummary("Unknown");
        }
    }

    private void setCameraProperty(String name, String value) {
        String propertyValue = "<" + name + "/" + value + ">";
        Log.v(TAG, "setCameraProperty() : " + propertyValue);
        propertyInterface.setCameraPropertyValue(name, propertyValue);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume() Start");

        // 撮影モードかどうかを確認して、撮影モードではなかったら撮影モードに切り替える
        if ((changeRunModeExecutor != null) && (!changeRunModeExecutor.isRecordingMode())) {
            // Runモードを切り替える。（でも切り替えると、設定がクリアされてしまう...。
            changeRunModeExecutor.changeRunMode(true);
        }
        synchronizeCameraProperties(true);
        Log.v(TAG, "onResume() End");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause() Start");

        // Preference変更のリスナを解除
        preferences.unregisterOnSharedPreferenceChangeListener(this);

        Log.v(TAG, "onPause() End");
    }

    /**
     * カメラプロパティとPreferenceとの同期処理を実行
     */
    private void synchronizeCameraProperties(boolean isPropertyLoad) {
        // 実行中ダイアログを取得する
        busyDialog = new ProgressDialog(getActivity());
        busyDialog.setTitle(getString(R.string.dialog_title_loading_properties));
        busyDialog.setMessage(getString(R.string.dialog_message_loading_properties));
        busyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        busyDialog.setCancelable(false);
        busyDialog.show();

        // データ読み込み処理（別スレッドで実行）
        if (isPropertyLoad) {
            new Thread(preferenceSynchronizer).start();
        }
    }

    /**
     * Preferenceが更新された時に呼び出される処理
     *
     * @param sharedPreferences sharedPreferences
     * @param key               変更されたキー
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v(TAG, "onSharedPreferenceChanged() : " + key);
        boolean value;
        if (key != null)
        {
            switch (key)
            {
                case ICameraPropertyAccessor.RAW:
                    value = preferences.getBoolean(key, true);
                    setBooleanPreference(key, key, value);
                    String propertyValue = (value) ? "ON" : "OFF";
                    setCameraProperty(IOlyCameraProperty.RAW, propertyValue);
                    break;

                case ICameraPropertyAccessor.SHOOT_ONLY_CAMERA:
                    value = preferences.getBoolean(key, true);
                    setBooleanPreference(key, key, value);
                    break;

                case ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE:
                    setShowSampleImageModePreference();
                    break;

                case ICameraPropertyAccessor.USE_CUSTOM_SPLASH:
                    value = preferences.getBoolean(key, false);
                    setBooleanPreference(key, key, value);
                    break;

                default:
                    String strValue = preferences.getString(key, "");
                    setListPreference(key, key, strValue);
                    String propertyKey = convertKeyFromPreferenceToCameraPropertyKey(key);
                    if (propertyKey != null)
                    {
                        setCameraProperty(propertyKey, strValue);
                    }
                    break;
            }
        }
    }

    private void setShowSampleImageModePreference()
    {
        ListPreference listPref = (ListPreference) findPreference(ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE);
        String value = preferences.getString(ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE, ICameraPropertyAccessor.SHOW_SAMPLE_IMAGE_DEFAULT_VALUE);
        String summary = getString(R.string.pref_show_sample_image_0); // value.equals("0")
        if (value.equals("1")) {
            summary = getString(R.string.pref_show_sample_image_1);
        }
        listPref.setSummary(summary);
    }

    private void setSampleImageFileName()
    {
        String fileName = "";
        String value = preferences.getString(ICameraPropertyAccessor.SELECT_SAMPLE_IMAGE, "");
        if (value.length() > 0)
        {
            fileName = value.substring(value.lastIndexOf("/") + 1);
        }
        Log.v(TAG, "image File : " + fileName + " (" + value + ")");
        findPreference("select_sample_image").setSummary(fileName);
    }

    /**
     * ListPreference の表示データを設定
     *
     * @param pref_key     Preference(表示)のキー
     * @param key          Preference(データ)のキー
     * @param defaultValue Preferenceのデフォルト値
     */
    private void setListPreference(String pref_key, String key, String defaultValue) {
        ListPreference pref;
        pref = (ListPreference) findPreference(pref_key);
        String value = preferences.getString(key, defaultValue);
        if (pref != null) {
            pref.setValue(value);
            pref.setSummary(value);
        }
    }

    /**
     * BooleanPreference の表示データを設定
     *
     * @param pref_key     Preference(表示)のキー
     * @param key          Preference(データ)のキー
     * @param defaultValue Preferenceのデフォルト値
     */
    private void setBooleanPreference(String pref_key, String key, boolean defaultValue) {
        CheckBoxPreference pref = (CheckBoxPreference) findPreference(pref_key);
        if (pref != null) {
            boolean value = preferences.getBoolean(key, defaultValue);
            pref.setChecked(value);
        }
    }

    private String convertKeyFromPreferenceToCameraPropertyKey(String key) {
        String target = null;
        if (key == null) {
            return (null);
        }
        switch (key) {
            case ICameraPropertyAccessor.TAKE_MODE:
                target = IOlyCameraProperty.TAKE_MODE;
                break;

            case ICameraPropertyAccessor.COLOR_TONE:
                target = IOlyCameraProperty.COLOR_TONE;
                break;

            case ICameraPropertyAccessor.AE_MODE:
                target = IOlyCameraProperty.AE_MODE;
                break;

            case ICameraPropertyAccessor.WB_MODE:
                target = IOlyCameraProperty.WB_MODE;
                break;

            case ICameraPropertyAccessor.EXPOSURE_COMPENSATION:
                target = IOlyCameraProperty.EXPOSURE_COMPENSATION;
                break;

            case ICameraPropertyAccessor.SHUTTER_SPEED:
                target = IOlyCameraProperty.SHUTTER_SPEED;
                break;

            case ICameraPropertyAccessor.APERTURE:
                target = IOlyCameraProperty.APERTURE;
                break;

            case ICameraPropertyAccessor.ISO_SENSITIVITY:
                target = IOlyCameraProperty.ISO_SENSITIVITY;
                break;

            case ICameraPropertyAccessor.SOUND_VOLUME_LEVEL:
                target = IOlyCameraProperty.SOUND_VOLUME_LEVEL;
                break;

            default:
                // target == null
                break;
        }
        return (target);
    }

    /**
     * カメラプロパティの同期処理終了通知
     */
    @Override
    public void synchronizedProperty() {
        Activity activity = getActivity();
        if (activity == null) {
            try {
                busyDialog.dismiss();
                busyDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Preferenceの画面に反映させる
                    setListPreference("take_mode", ICameraPropertyAccessor.TAKE_MODE, ICameraPropertyAccessor.TAKE_MODE_DEFAULT_VALUE);
                    setListPreference("color_tone", ICameraPropertyAccessor.COLOR_TONE, ICameraPropertyAccessor.COLOR_TONE_DEFAULT_VALUE);
                    setListPreference("ae_mode", ICameraPropertyAccessor.AE_MODE, ICameraPropertyAccessor.AE_MODE_DEFAULT_VALUE);
                    setListPreference("wb_mode", ICameraPropertyAccessor.WB_MODE, ICameraPropertyAccessor.WB_MODE_DEFAULT_VALUE);
                    setListPreference("exposure_compensation", ICameraPropertyAccessor.EXPOSURE_COMPENSATION, ICameraPropertyAccessor.EXPOSURE_COMPENSATION_DEFAULT_VALUE);
                    setListPreference("shutter_speed", ICameraPropertyAccessor.SHUTTER_SPEED, ICameraPropertyAccessor.SHUTTER_SPEED_DEFAULT_VALUE);
                    setListPreference("aperture", ICameraPropertyAccessor.APERTURE, ICameraPropertyAccessor.APERTURE_DEFAULT_VALUE);
                    setListPreference("iso_sensitivity", ICameraPropertyAccessor.ISO_SENSITIVITY, ICameraPropertyAccessor.ISO_SENSITIVITY_DEFAULT_VALUE);
                    setListPreference("sound_volume_level", ICameraPropertyAccessor.SOUND_VOLUME_LEVEL, ICameraPropertyAccessor.SOUND_VOLUME_LEVEL_DEFAULT_VALUE);
                    setShowSampleImageModePreference();
                    setSampleImageFileName();
                    setBooleanPreference("raw", ICameraPropertyAccessor.RAW, true);
                    setBooleanPreference("only_camera", ICameraPropertyAccessor.SHOOT_ONLY_CAMERA, false);
                    setBooleanPreference("use_custom_splash", ICameraPropertyAccessor.USE_CUSTOM_SPLASH, false);

                    // カメラキットのバージョン
                    findPreference("camerakit_version").setSummary(OLYCamera.getVersion());
                    if (hardwareStatusInterface != null) {
                        // その他のハードウェア情報の情報設定
                        setHardwareSummary();
                    }

                    // OpenCV Version
                    findPreference("opencv_version").setSummary(Core.VERSION + "");

                    // 実行中ダイアログを消す
                    busyDialog.dismiss();
                    busyDialog = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult() : start");

        switch (resultCode)
        {
            case Activity.RESULT_OK:
                String filePath = "";
                String[] projection = {MediaStore.MediaColumns.DATA};
                try
                {
                    Cursor cursor = getActivity().getContentResolver().query(data.getData(), projection, null, null, null);
                    if (cursor != null)
                    {
                        if (cursor.getCount() > 0)
                        {
                            cursor.moveToNext();
                            filePath = cursor.getString(0);
                        }
                        cursor.close();
                    }
                    setActivityResultValue(requestCode, filePath);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
        Log.v(TAG, "onActivityResult() : end");
    }

    public void setActivityResultValue(int requestCode, String filePath)
    {
        String key;
        if (requestCode == ICameraPropertyAccessor.SELECT_SAMPLE_IMAGE_CODE)
        {
            key = ICameraPropertyAccessor.SELECT_SAMPLE_IMAGE;
        }
        else if (requestCode == ICameraPropertyAccessor.SELECT_SPLASH_IMAGE_CODE)
        {
            key = ICameraPropertyAccessor.SELECT_SPLASH_IMAGE;
        }
        else
        {
            // 何もしない
            return;
        }
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        findPreference(key).setSummary(fileName);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, filePath);
        editor.apply();
        Log.v(TAG, " key : " + key + " image File : " + fileName + " (" + filePath + ")");
        //Toast.makeText(getContext(), "Selected :" + fileName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void selectImageFileFromGallery(int code)
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, code);
    }
}
