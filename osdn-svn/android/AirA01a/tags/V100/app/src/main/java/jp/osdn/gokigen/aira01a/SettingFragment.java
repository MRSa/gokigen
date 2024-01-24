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

        private String getLiveViewQuality()
        {
            if (preferences != null)
            {
                return preferences.getString("live_view_quality", "QVGA");
            }
            return "QVGA";
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
            //editor.commit();
            editor.apply();
        }

        @Override
        //public void onCreate(Bundle savedInstanceState)
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

        @Override
        public void onPause()
        {
            super.onPause();

            if (camera.isConnected())
            {
                // Applies the live preview quality.
                try {
                    camera.changeLiveViewSize(CameraCoordinator.toLiveViewSize(getLiveViewQuality()));
                }
                catch (OLYCameraKitException e1)
                {
                    Log.w(TAG, "To change the live view size is failed.");
                }

                String recviewValue = isShowPreviewEnabled() ? "<RECVIEW/ON>" : "<RECVIEW/OFF>";
                try
                {
                    camera.setCameraPropertyValue("RECVIEW", recviewValue);
                }
                catch (OLYCameraKitException e1)
                {
                    Log.w(TAG, "To change the rec-view is failed.");
                }
            }
        }
}
