package jp.osdn.gokigen.aira01a;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

//.PreferenceFragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;

/**
 *   SettingFragment
 *   (ほぼOLYMPUS imagecapturesampleのサンプルコードそのまま)
 *
 */
public class SettingFragment extends PreferenceFragmentCompat
{
    private final String TAG = this.toString();

    private SharedPreferences preferences;
    private OLYCamera camera = null;

    public void setCamera(OLYCamera camera)
    {
        this.camera = camera;
    }

    private boolean isShowPreviewEnabled()
    {
        return  (preferences != null)&&(preferences.getBoolean("show_preview", true));
    }

    private boolean isTakeRaw()
    {
        return  (preferences != null)&&(preferences.getBoolean("raw", true));
    }

    private boolean isWbKeepWarmColors()
    {
        return  (preferences != null)&&(preferences.getBoolean("auto_wb_denkyu_colored_leaving", false));
    }

    private boolean isFullTimeAF()
    {
        return  (preferences != null)&&(preferences.getBoolean("full_time_af", false));
    }

    private String getCompressibilityRatio()
    {
        if (preferences != null)
        {
            return (preferences.getString("compressibility_ratio", "CMP_2_7"));
        }
        return ("CMP_2_7");

    }

    private String getContinuousShootingVelocity()
    {
        if (preferences != null)
        {
            return (preferences.getString("shooting_velocity", "10"));
        }
        return ("10");
    }

    private String getSoundVolume()
    {
        if (preferences != null)
        {
            return (preferences.getString("sound_volume_level", "OFF"));
        }
        return ("OFF");
    }

    private String getFaceScan()
    {
        if (preferences != null)
        {
            return preferences.getString("face_scan", "FACE_SCAN_OFF");
        }
        return ("FACE_SCAN_OFF");
    }

    private String getLiveViewQuality()
    {
        if (preferences != null)
        {
            return preferences.getString("live_view_quality", "QVGA");
        }
        return "QVGA";
    }

    private String getImageSize()
    {
        if (preferences != null)
        {
            return preferences.getString("image_size", "4608x3456");
        }
        return "4608x3456";
    }

    private String getArtFilterMode()
    {
        if (preferences != null)
        {
            return preferences.getString("recently_art_filter", "POPART");
        }
        return "POPART";
    }

    private String getColorTone()
    {
        if (preferences != null)
        {
            return (preferences.getString("color_tone", "I_FINISH"));
        }
        return "I_FINISH";
    }

    private String getColorCreatorColor()
    {
        if (preferences != null)
        {
            return (preferences.getString("color_creator_color", "0"));
        }
        return "0";
    }

    private String getColorCreatorVivid()
    {
        if (preferences != null)
        {
            return (preferences.getString("color_creator_vivid", "0"));
        }
        return "0";
    }

    private String getMonotoneFilter()
    {
        if (preferences != null)
        {
            return (preferences.getString("monotonefilter_monochrome", "NORMAL"));
        }
        return "NORMAL";
    }

    private String getMonotoneColor()
    {
        if (preferences != null)
        {
            return (preferences.getString("monotonecolor_monochrome", "NORMAL"));
        }
        return "NORMAL";
    }

    @Override
    public void onAttach(Context activity)
    {
        super.onAttach(activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Map<String, ?> items = preferences.getAll();
        SharedPreferences.Editor editor = preferences.edit();
        if (!items.containsKey("touch_shutter")) {
            editor.putBoolean("touch_shutter", true);
        }
        if (!items.containsKey("show_preview")) {
            editor.putBoolean("show_preview", true);
        }
        if (!items.containsKey("raw")) {
            editor.putBoolean("raw", true);
        }
        if (!items.containsKey("sound_volume_level")) {
            editor.putString("sound_volume_level", "OFF");
        }
        if (!items.containsKey("face_scan")) {
            editor.putString("face_scan", "FACE_SCAN_OFF");
        }
        if (!items.containsKey("recently_art_filter")) {
            editor.putString("recently_art_filter", "POPART");
        }
        if (!items.containsKey("image_size")) {
            editor.putString("image_size", "4608x3456");
        }
        if (!items.containsKey("shooting_velocity")) {
            editor.putString("shooting_velocity", "10");
        }
        if (!items.containsKey("compressibility_ratio")) {
            editor.putString("compressibility_ratio", "CMP_2_7");
        }
        if (!items.containsKey("auto_wb_denkyu_colored_leaving")) {
            editor.putBoolean("auto_wb_denkyu_colored_leaving", false);
        }
        if (!items.containsKey("full_time_af")) {
            editor.putBoolean("full_time_af", false);
        }
        if (!items.containsKey("bracket_pict_popart")) {
            editor.putBoolean("bracket_pict_popart", false);
        }
        if (!items.containsKey("bracket_pict_fantasic_focus")) {
            editor.putBoolean("bracket_pict_fantasic_focus", false);
        }
        if (!items.containsKey("bracket_pict_daydream")) {
            editor.putBoolean("bracket_pict_daydream", false);
        }
        if (!items.containsKey("bracket_pict_light_tone")) {
            editor.putBoolean("bracket_pict_light_tone", false);
        }
        if (!items.containsKey("bracket_pict_rough_monochrome")) {
            editor.putBoolean("bracket_pict_rough_monochrome", false);
        }
        if (!items.containsKey("bracket_pict_toy_photo")) {
            editor.putBoolean("bracket_pict_toy_photo", false);
        }
        if (!items.containsKey("bracket_pict_miniature")) {
            editor.putBoolean("bracket_pict_miniature", false);
        }
        if (!items.containsKey("bracket_pict_cross_process")) {
            editor.putBoolean("bracket_pict_cross_process", false);
        }
        if (!items.containsKey("bracket_pict_gentle_sepia")) {
            editor.putBoolean("bracket_pict_gentle_sepia", false);
        }
        if (!items.containsKey("bracket_pict_dramatic_tone")) {
            editor.putBoolean("bracket_pict_dramatic_tone", false);
        }
        if (!items.containsKey("bracket_pict_ligne_clair")) {
            editor.putBoolean("bracket_pict_ligne_clair", false);
        }
        if (!items.containsKey("bracket_pict_pastel")) {
            editor.putBoolean("bracket_pict_pastel", false);
        }
        if (!items.containsKey("bracket_pict_vintage")) {
            editor.putBoolean("bracket_pict_vintage", false);
        }
        if (!items.containsKey("bracket_pict_partcolor")) {
            editor.putBoolean("bracket_pict_partcolor", false);
        }

        if(!items.containsKey("color_tone")) {
            editor.putString("color_tone", "I_FINISH");
        }

        if(!items.containsKey("color_creator_color")) {
            editor.putString("color_creator_color", "0");
        }

        if(!items.containsKey("color_creator_vivid")) {
            editor.putString("color_creator_vivid", "0");
        }

        if(!items.containsKey("monotonefilter_monochrome")) {
            editor.putString("monotonefilter_monochrome", "NORMAL");
        }

        if(!items.containsKey("monotonecolor_monochrome")) {
            editor.putString("monotonecolor_monochrome", "NORMAL");
        }

        if(!items.containsKey("auto_bracketing")) {
            editor.putString("auto_bracketing", "0");
        }

        if(!items.containsKey("shooting_count")) {
            editor.putString("shooting_count", "3");
        }

        //editor.commit();
        editor.apply();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        //super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        {
            final HashMap<String, String> sizeTable = new HashMap<String, String>();
            sizeTable.put("QVGA", "(320x240)");
            sizeTable.put("VGA", "(640x480)");
            sizeTable.put("SVGA", "(800x600)");
            sizeTable.put("XGA", "(1024x768)");

            ListPreference liveViewQuality = (ListPreference)findPreference("live_view_quality");

            liveViewQuality.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue + " " + sizeTable.get(newValue));
                    return true;
                }
            });
            liveViewQuality.setSummary(liveViewQuality.getValue() + " " + sizeTable.get(liveViewQuality.getValue()));
        }

        findPreference("power_off").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainActivity activity = (MainActivity)getActivity();
                activity.disconnectWithPowerOff(true);
                return true;
            }
        });

        /****
         String key = "";
         try
         {
         // 音量の現在設定を表示
         key = "SOUND_VOLUME_LEVEL";
         String data[] = camera.decodeCameraPropertyValue(camera.getCameraPropertyValue(key));
         findPreference("sound_volume_level").setSummary(data[1]);

         // 静止画サイズの現在設定を表示
         key = "IMAGESIZE";
         findPreference("image_size").setSummary(camera.getCameraPropertyValueTitle(camera.getCameraPropertyValue(key)));

         // 顔検出の現在設定を表示
         key = "FACE_SCAN";
         findPreference("face_scan").setSummary(camera.getCameraPropertyValueTitle(camera.getCameraPropertyValue(key)));

         // アートフィルターの現在設定を表示
         key = "RECENTLY_ART_FILTER";
         findPreference("recently_art_filter").setSummary(camera.getCameraPropertyValueTitle(camera.getCameraPropertyValue(key)));

         }
         catch (Exception e)
         {
         // 何もしない
         String message = "ERROR to get camera property : " + key;
         Log.v(TAG, message);
         e.printStackTrace();
         }
         ****/

        // カメラキットのバージョン
        findPreference("camerakit_version").setSummary(OLYCamera.getVersion());

        // レンズ状態
        findPreference("lens_status").setSummary(camera.getLensMountStatus());

        // メディア状態
        findPreference("media_status").setSummary(camera.getMediaMountStatus());

        // 焦点距離
        String focalLength;
        float minLength = camera.getMinimumFocalLength();
        float maxLength = camera.getMaximumFocalLength();
        float actualLength = camera.getActualFocalLength();
        if (minLength == maxLength)
        {
            focalLength = String.format(Locale.ENGLISH, "%3.0fmm", actualLength);
        }
        else
        {
            focalLength = String.format(Locale.ENGLISH, "%3.0fmm - %3.0fmm (%3.0fmm)", minLength, maxLength, actualLength);
        }
        findPreference("focal_length").setSummary(focalLength);

        // カメラのバージョン
        try
        {
            Map<String, Object> hardwareInformation = camera.inquireHardwareInformation();
            findPreference("camera_version").setSummary((String)hardwareInformation.get(OLYCamera.HARDWARE_INFORMATION_CAMERA_FIRMWARE_VERSION_KEY));
        } catch (OLYCameraKitException e) {
            findPreference("camera_version").setSummary("Unknown");
        }
    }

    private void setProperty(String name, String value, String message)
    {
        try
        {
            camera.setCameraPropertyValue(name, value);
        }
        catch (OLYCameraKitException e)
        {
            Log.w(TAG, message);
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();

        if (camera.isConnected())
        {
            String message = "";
            String value;

            // Apply settings
            try {
                message = "To change the live view size is failed.";
                camera.changeLiveViewSize(CameraCoordinator.toLiveViewSize(getLiveViewQuality()));
            }
            catch (Exception e)
            {
                Log.w(TAG, message);
            }

            /** TODO: 多くなってきたので、今後、一括登録を考える **/
            message = "To change sound volume is failed.";
            value = "<SOUND_VOLUME_LEVEL/" + getSoundVolume() + ">";
            setProperty("SOUND_VOLUME_LEVEL", value, message);

            message = "To change the rec-view is failed.";
            value = isShowPreviewEnabled() ? "<RECVIEW/ON>" : "<RECVIEW/OFF>";
            setProperty("RECVIEW", value, message);

            message = "To change RAW is failed.";
            value = isTakeRaw() ?  "<RAW/ON>" : "<RAW/OFF>";
            setProperty("RAW", value, message);

            message = "To change image size is failed.";
            value = "<IMAGESIZE/" + getImageSize() + ">";
            setProperty("IMAGESIZE", value, message);

            message = "To change keeping warm colors is failed.";
            value = isWbKeepWarmColors() ? "<AUTO_WB_DENKYU_COLORED_LEAVING/ON>" : "<AUTO_WB_DENKYU_COLORED_LEAVING/OFF>";
            setProperty("AUTO_WB_DENKYU_COLORED_LEAVING", value, message);

            message = "To change shooting velocity is failed.";
            value = "<CONTINUOUS_SHOOTING_VELOCITY/" + getContinuousShootingVelocity() + ">";
            setProperty("CONTINUOUS_SHOOTING_VELOCITY", value, message);

            message = "To change compressibility ratio is failed.";
            value = "<COMPRESSIBILITY_RATIO/" + getCompressibilityRatio() + ">";
            setProperty("COMPRESSIBILITY_RATIO", value, message);

            message = "To change FULL TIME AF is failed.";
            value = isFullTimeAF() ?  "<FULL_TIME_AF/ON>" : "<FULL_TIME_AF/OFF>";
            setProperty("FULL_TIME_AF", value, message);

            message = "To change face scan mode is failed.";
            value = "<FACE_SCAN/" + getFaceScan() + ">";
            setProperty("FACE_SCAN", value, message);

            message = "To change art filter is failed.";
            value = "<RECENTLY_ART_FILTER/" + getArtFilterMode() + ">";
            setProperty("RECENTLY_ART_FILTER", value, message);

            message = "To change color tone is failed.";
            value = "<COLORTONE/" + getColorTone() + ">";
            setProperty("COLORTONE", value, message);

            message = "To change color creator color is failed.";
            value = "<COLOR_CREATOR_COLOR/" + getColorCreatorColor() + ">";
            setProperty("COLOR_CREATOR_COLOR", value, message);

            message = "To change color creator vivid is failed.";
            value = "<COLOR_CREATOR_VIVID/" + getColorCreatorVivid() + ">";
            setProperty("COLOR_CREATOR_VIVID", value, message);

            message = "To change monotone filter is failed.";
            value = "<MONOTONEFILTER_MONOCHROME/" + getMonotoneFilter() + ">";
            setProperty("MONOTONEFILTER_MONOCHROME", value, message);

            message = "To change monotone color is failed.";
            value = "<MONOTONECOLOR_MONOCHROME/" + getMonotoneColor() + ">";
            setProperty("MONOTONECOLOR_MONOCHROME", value, message);

            /****/

        }
    }
}