package jp.osdn.gokigen.aira01a.myprops;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.osdn.gokigen.aira01a.olycamera.CameraPropertyListenerImpl;

/**
 *   カメラプロパティを一括でバックアップしたり、リストアしたりするクラス
 *
 */
public class CameraPropertyBackupRestore
{
    private final String TAG = toString();

    static final int MAX_STORE_PROPERTIES = 96;   // お気に入り設定の最大記憶数...
    static final String TITLE_KEY = "CameraPropTitleKey";
    static final String DATE_KEY = "CameraPropDateTime";

    private final Context parent;
    private final OLYCamera camera;

    public CameraPropertyBackupRestore(Context context, OLYCamera camera)
    {
        this.camera = camera;
        this.parent = context;
    }

    /**
     *   カメラの現在の設定を本体から読みだして記憶する
     *
     */
    public void storeCameraSettings(String idHeader)
    {
        // カメラから設定を一括で読みだして、Preferenceに記録する
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
            //Log.v(TAG, "CameraPropertyBackupRestore::storeCameraSettings() : " + idHeader);

            if (values != null)
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
                SharedPreferences.Editor editor = preferences.edit();
                for (String key : values.keySet())
                {
                    editor.putString(idHeader + key, values.get(key));
                    //Log.v(TAG, "storeCameraSettings(): " + idHeader + key + " , " + values.get(key));
                }
                DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                editor.putString(idHeader + DATE_KEY, dateFormat.format(new Date()));
                //editor.commit();
                editor.apply();

                Log.v(TAG, "storeCameraSettings() COMMITED : " + idHeader);
            }
        }
    }

    /**
     *   Preferenceにあるカメラの設定をカメラに登録する
     *　(注： Read Onlyなパラメータを登録しようとするとエラーになるので注意）
     */
    public void restoreCameraSettings(String idHeader)
    {
        //Log.v(TAG, "restoreCameraSettings() : START [" + idHeader + "]");

        // Restores my settings.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (camera.isConnected())
        {
            Map<String, String> values = new HashMap<>();
            Set<String> names = camera.getCameraPropertyNames();
            for (String name : names)
            {
                String value = preferences.getString(idHeader + name, null);
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
            //Log.v(TAG, "restoreCameraSettings() : END [" + idHeader + "]" + " " + values.size());
        }
    }

    public void setCameraSettingDataName(String idHeader, String dataName)
    {
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(idHeader + TITLE_KEY, dataName);
            editor.apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public interface IPropertiesOperation
    {
        void loadProperties(final Activity activity, final String id, final String name);
        void saveProperties(final Activity activity, final String id, final String name);
    }

}
