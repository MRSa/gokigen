package jp.sourceforge.gokigen.viewsensor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import  android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by MRSa on 2015/01/02.
 */
public class MainListener implements WatchViewStub.OnLayoutInflatedListener, OnApplyWindowInsetsListener, SensorWatcher.ISensorChanged,
        SensorWatcher.IPressureValueHolder, SensorWatcher.IRotationVectorValueHolder, SensorWatcher.ICalibrationExecutor
{
    private static final String TAG = "MainListener";

    public static final int SPEECH_REQUEST_CALIBRATION = 10;

    private final int mSamplingPeriodUs = SensorManager.SENSOR_DELAY_NORMAL;
    private final int mMaxReportLatencyUs = 1000000; // 10000000;

    /*
    private WearableListView mListView = null;
    private SymbolListArrayAdapter adapter = null;
    private ArrayList<SymbolListArrayItem> mListItems = null;
    */
    private Activity mActivity = null;
    private boolean mIsRound = false;
    private SensorInfoDrawer mSensorInfoDrawer = null;
    private SensorWrapper mSensorWrapper = null;

    private boolean mIsUseGeomagneticRotationVector = false;

    private float mCurrentPressure = 0.0f;

    private float mPressureOffset = 0.0f;

    private float mCurrentNorthAngle = 0.0f;

    /** 一時的に使用するデータ(北の方角を知るため使用する) **/
    private float[] mGravity = new float[3];
    private float[] mMagneticField = new float[3];
    private float[] mRotationMatrix = new float[16];
    private float[] mI = new float[16];
    private float[] mVector = new float[3];

    private GokigenSurfaceView mSurfaceView = null;

    /**
     *   コンストラクタ
     *
     */
    public MainListener(Activity context)
    {
        this.mActivity = context;
        // センサのセットアップを行う
        mSensorWrapper = new SensorWrapper(context);
        mSensorInfoDrawer = new SensorInfoDrawer(context);
        setupSensors();
    }

    public void setUseGeomagneticRotationVectorSensor(boolean isUseGeomagnetic)
    {
        mIsUseGeomagneticRotationVector = isUseGeomagnetic;
    }

    public void setIsRound(boolean isRound)
    {
        mIsRound = isRound;
    }

    /**
     *   レイアウトが確定したときに呼び出される
     *   （WatchViewStub.OnLayoutInflatedListenerの実装）
     * @param stub
     */
    @Override
    public void onLayoutInflated(WatchViewStub stub)
    {
/*
        // リストに表示するアイテムを生成する
        mListItems = null;
        mListItems = new ArrayList<SymbolListArrayItem>();

        SymbolListArrayItem item;
        item = new SymbolListArrayItem(0, "00", "GOKIGEN GOKIGEN");
        mListItems.add(item);

        item = new SymbolListArrayItem(0, "01", "abcdefghijklmnopqrstuvwxyz");
        mListItems.add(item);

        item = new SymbolListArrayItem(0, "02", "XXXXXXXXXX111111111");
        mListItems.add(item);

        item = new SymbolListArrayItem(0, "03", "YYYYYYYYYYXXXXXXXXXX");
        mListItems.add(item);

        item = new SymbolListArrayItem(0, "04", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        mListItems.add(item);

        item = new SymbolListArrayItem(0, "05", "QQQQQQQQQQQQQQQQQQQQQQQQQQ");
        mListItems.add(item);

        adapter = new SymbolListArrayAdapter(mListItems);

        mListView = (WearableListView) stub.findViewById(R.id.list_view);
        if (mListView != null)
        {
            mListView.setAdapter(adapter);
        }
*/
    }

    /**
     *
     *
     * @param v
     * @param insets
     * @return
     */
    @Override
    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets)
    {
        v.onApplyWindowInsets(insets);
        mIsRound = insets.isRound();
        if (Log.isLoggable(TAG, Log.INFO))
        {
            Log.i(TAG, "onApplyWindowInsets: " + (mIsRound ? "round" : "square"));
        }

        if (mIsRound == true)
        {
            // ○ の表示
            layoutWindowRound(v);
        }
        else
        {
            // □ の表示
            layoutWindowSquare(v);
        }

        return (insets);
    }

    public void onPause()
    {
        try
        {
            // センサの監視を停止する
            mSensorWrapper.finishWatch();
        }
        catch (Exception ex)
        {
            // 何もしない
        }
    }

    public void onResume()
    {
        try
        {
            // センサの監視を開始する
            mSensorWrapper.startWatch(mSamplingPeriodUs, mMaxReportLatencyUs);
        }
        catch (Exception ex)
        {
            // なにもしない
        }
    }

    public void onDestroy()
    {
        mSensorWrapper.dispose();
    }


    /**
     *
     * @param v
     */
    private void layoutWindowRound(View v)
    {
        CircledImageView imageView = (CircledImageView) v.findViewById(R.id.circle_view);
        Drawable backgroundDrawable = mActivity.getResources().getDrawable(R.drawable.yamabg);
        if (backgroundDrawable != null)
        {
            imageView.setImageDrawable(backgroundDrawable);
        }
        mSurfaceView = (GokigenSurfaceView) v.findViewById(R.id.round_canvas_view);
        if (mSurfaceView != null)
        {
            mSurfaceView.setCanvasDrawer(mSensorInfoDrawer);
        }
    }

    /**
     *
     * @param v
     */
    private void layoutWindowSquare(View v)
    {
        ImageView imageView = (ImageView) v.findViewById(R.id.image_view);
        Drawable backgroundDrawable = mActivity.getResources().getDrawable(R.drawable.yamabg);
        if (backgroundDrawable != null)
        {
            imageView.setImageDrawable(backgroundDrawable);
        }
        GokigenSurfaceView surfaceView = (GokigenSurfaceView) v.findViewById(R.id.rect_canvas_view);
        if (surfaceView != null)
        {
            surfaceView.setCanvasDrawer(mSensorInfoDrawer);
        }
    }

    /**
     *   監視するセンサのセットアップを行う
     *
     */
    private void setupSensors()
    {
        SensorWatcher watcher = null;

        // Pressure Sensor
        watcher = new SensorWatcher(Sensor.TYPE_PRESSURE, this);
        watcher.readyReport(true);
        mSensorWrapper.addSensorReceiver(watcher);

        // Rotation Vector Sensor
        if (mIsUseGeomagneticRotationVector == true)
        {
            watcher = new SensorWatcher(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, this);
        }
        else
        {
            watcher = new SensorWatcher(Sensor.TYPE_ROTATION_VECTOR, this);
        }
        watcher.readyReport(true);
        mSensorWrapper.addSensorReceiver(watcher);

        // pressure sensor offset
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mPressureOffset = preference.getFloat("mPressureOffset", 0.0f);

/*
        watcher = new SensorWatcher(Sensor.TYPE_GRAVITY, this);
        watcher.readyReport(true);
        mSensorWrapper.addSensorReceiver(watcher);

        watcher = new SensorWatcher(Sensor.TYPE_ACCELEROMETER, this);
        watcher.readyReport(true);
        mSensorWrapper.addSensorReceiver(watcher);

        watcher = new SensorWatcher(Sensor.TYPE_MAGNETIC_FIELD, this);
        watcher.readyReport(true);
        mSensorWrapper.addSensorReceiver(watcher);
*/

       mSensorInfoDrawer.prepare(this, this, this);
    }

    /**
     *
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        boolean isUpdate = false;
        int sensorType = event.sensor.getType();
        if ((sensorType == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)||(sensorType == Sensor.TYPE_ROTATION_VECTOR))
        {
            isUpdate = updateRotationVectorValues(event.values);
        }
        else if (sensorType == Sensor.TYPE_PRESSURE)
        {
            float pressure =  event.values[0];
            if (Math.abs(mCurrentPressure - pressure) > 0.5f)
            {
                mCurrentPressure = pressure;
                if (Log.isLoggable(TAG, Log.DEBUG))
                {
                    Log.d(TAG, "PRESSURE : " + mCurrentPressure + "hPa");
                }
                isUpdate = true;
            }
        }
        else if ((sensorType == Sensor.TYPE_GRAVITY)||(sensorType == Sensor.TYPE_ACCELEROMETER))
        {
            mGravity = event.values.clone();
            isUpdate = updateNorthAngle(mGravity, mMagneticField);
        }
        else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD)
        {
            mMagneticField = event.values.clone();
            isUpdate = updateNorthAngle(mGravity, mMagneticField);
        }
        else
        {
            if (Log.isLoggable(TAG, Log.INFO))
            {
                Log.i(TAG, "onSensorChanged() " + event.sensor.getName() + " " + event.values[0]);
            }
        }
        if ((mSurfaceView != null)&&(isUpdate == true))
        {
            mSurfaceView.doDraw();
        }
    }

    /**
     *
     *
     * @param values
     * @return
     */
    private boolean updateRotationVectorValues(float[] values)
    {
        boolean isUpdate = false;
        SensorManager.getRotationMatrixFromVector(mRotationMatrix, values);
        SensorManager.getOrientation(mRotationMatrix, mVector);

        float angle = mVector[0];
        if (Math.abs(mCurrentNorthAngle - angle) > 0.001f)
        {
            mCurrentNorthAngle = angle;
            isUpdate = true;
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "NORTH Angle ," + mCurrentNorthAngle);
            }
        }
        return (isUpdate);
    }

    /**
     *
     *
    * @return
            */
    private boolean updateNorthAngle(float[] gravity, float[] geomagnetic)
    {
        boolean isUpdate = false;
        SensorManager.getRotationMatrix(mRotationMatrix, mI, gravity, geomagnetic);
        SensorManager.getOrientation(mRotationMatrix, mVector);

        float angle = mVector[0];
        if (Math.abs(mCurrentNorthAngle - angle) > 0.001f)
        {
            mCurrentNorthAngle = angle;
            isUpdate = true;
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "North Angle ," + mCurrentNorthAngle);
            }
        }
        return (isUpdate);
    }

    /**
     *
     * @return
     */
    public float getPressure()
    {
        return (mCurrentPressure);
    }

    /**
     *  高度を応答
     *
     * @return
     */
    public float getAltitude(boolean isCalibrate)
    {
        float pressure = mCurrentPressure;
        if (isCalibrate == true)
        {
            pressure = mCurrentPressure - mPressureOffset;
        }
        float altitude = 44330.8f * ((1.0f) - (float) Math.pow((pressure / 1013.2427d), 0.190263d));
        // float altitude = (float) ((Math.pow((1013.2427d / (double) pressure), (1.0d / 5.257d))*(15.0d + 273.15d)) / 0.0065d);
        return (altitude);
    }

    /**
     *   高度補正値をリセット
     *
     */
    public void clearPressureOffset()
    {
        mPressureOffset = 0.0f;

        // 補正値を記憶する
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mActivity);
        SharedPreferences.Editor editor = preference.edit();
        editor.putFloat("mPressureOffset", mPressureOffset);
        editor.commit();

        // 高度の設定を通知する
        String message = mActivity.getString(R.string.reset_altitude);
        kickConfirmationActivity(message);

    }

    /**
     *   高度補正値を返す
     *
     * @return
     */
    public float getPressureOffset()
    {
        return (mPressureOffset);
    }

    /**
     *   高度補正値を設定
     *
     * @param value
     */
    public void setAltitude(float value)
    {
        float pressure = (float) (1013.2427d * Math.pow(((288.15d - 0.0065d * (double) value)/ 288.15d), 5.25588d));
        //float pressure = (float) (1013.2427d * Math.pow(1.0d - ((0.0065d * (double)value) / (15.0d + 0.0065 * value + 273.15d)),5.25588d));

        mPressureOffset = mCurrentPressure - pressure;

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(mActivity);
        SharedPreferences.Editor editor = preference.edit();
        editor.putFloat("mPressureOffset", mPressureOffset);
        editor.commit();

        // 高度の設定を通知する
        String message = mActivity.getString(R.string.set_altitude_prefix) + String.format("%.0f", value) + mActivity.getString(R.string.set_altitude_suffix);
        kickConfirmationActivity(message);
    }

    /**
     *   高度補正の実行。。。
     *
     */
    public void startCalibrate()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mActivity.startActivityForResult(intent, SPEECH_REQUEST_CALIBRATION);
    }

    /**
     *　　Activityの応答を表示
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ((requestCode == SPEECH_REQUEST_CALIBRATION)&&(resultCode == Activity.RESULT_OK))
        {
            // ここで解析結果（文字情報）を拾う
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Iterator<String> iterator = result.iterator();
            while (iterator.hasNext())
            {
                String word = iterator.next();
                int value = toInteger(word);
                if (value >= 0)
                {
                    // 値が設定されていることがわかった！ 高度を設定する！
                    float floatValue = value;
                    setAltitude(floatValue);
                    return (true);
                }
            }
        }
        return (false);
    }

    /**
     *   文字列を数値に変換してみる...
     *
     * @param word
     * @return
     */
    private int toInteger(String word)
    {
        int number = -1;
        try
        {
            number = Integer.parseInt(word);
        }
        catch (Exception e)
        {
            number = -1;
        }
        return (-1);
    }

    /**
     *   成功の ConfirmationActivity を発行する
     *
     * @param message
     */
    private void kickConfirmationActivity(String message)
    {
        Intent intent = new Intent(mActivity, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, message);
        mActivity.startActivity(intent);
    }

    /**
     *
     * @return
     */
    public float getNorthAngle()
    {
        return (mCurrentNorthAngle);
    }
}
