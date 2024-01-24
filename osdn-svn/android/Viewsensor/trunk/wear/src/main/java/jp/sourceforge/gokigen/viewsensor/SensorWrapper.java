package jp.sourceforge.gokigen.viewsensor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.util.Log;

/**
 *  センサをラップするクラス
 *
 *
 *
 * @author MRSa
 *
 */
public class SensorWrapper extends TriggerEventListener implements SensorEventListener
{
    private static final String TAG = "SensorWrapper";

    private Context                             context        = null;
    private SensorManager                       sensorService = null;
    private Hashtable<Integer, ISensorReceiver> subscriber    = null;
    private List<Sensor>                        sensors        = null;

    private boolean                            isReadySensor = false;

    /**
     * コンストラクタ
     * @param argument
     */
    public SensorWrapper(Context argument)
    {
        context = argument;
        subscriber = new Hashtable<Integer, ISensorReceiver>();
        subscriber.clear();

        isReadySensor = prepareSensor();
    }

    /**
     *   終了時処理
     */
    public void dispose()
    {
        subscriber.clear();
        subscriber = null;
    }

    public boolean addSensorReceiver(ISensorReceiver receiver)
    {
        if (isReadySensor == false)
        {
            return (false);
        }
        if (receiver == null)
        {
            return (false);
        }
        subscriber.put(receiver.getSensorType(), receiver);
        return (true);
    }

    /**
     *   センサの準備
     *
     * @return
     */
    private boolean prepareSensor()
    {
        try
        {
            sensors  = null;
            sensorService = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            if (sensorService == null)
            {
                return (false);
            }
            sensors = sensorService.getSensorList(Sensor.TYPE_ALL);
        }
        catch (Exception ex)
        {
            sensorService = null;
            return (false);
        }
        return (true);
    }

    /**
     *  センサの監視を開始する ... 指定されたセンサのみ監視登録する
     * @return  true : 監視開始, false : 開始失敗
     */
    public boolean startWatch(int samplingPeriodUs, int maxReportLatencyUs)
    {
        try
        {
            if (subscriber == null)
            {
                if (Log.isLoggable(TAG, Log.DEBUG))
                {
                    Log.d(TAG, "ISensorReceiver is nothing.");
                }
                return (false);
            }
            for (Sensor sensor : sensors)
            {
                try
                {
                    // センサの監視登録
                    int sensorType = sensor.getType();
                    if (Log.isLoggable(TAG, Log.VERBOSE))
                    {
                        Log.v(TAG, "Sensor : " + sensor.getName() + " " + sensorType);
                    }
                    ISensorReceiver receiver = subscriber.get(sensorType);
                    if (receiver == null)
                    {
                        // 監視するセンサが登録されていなかった場合、先に進む
                        continue;
                    }
                    if (receiver.getSensorType() == sensorType)
                    {
                        //if (sensor.isWakeUpSensor() == false)
                        {
                            // 通常のセンサの場合...
                            sensorService.registerListener(this, sensor, samplingPeriodUs, maxReportLatencyUs);
                        }
                        //else
                        {
                            // Wake up sensor の場合...
                        }
                    }
                }
                catch (Exception e)
                {
                    //
                    if (Log.isLoggable(TAG, Log.INFO))
                    {
                        Log.i(TAG, "registerListener exception:" + e.getMessage());
                    }
                }
            }
            sensorService.flush(this);  // センサ情報をクリアする
        }
        catch (Exception ex)
        {
            return (false);
        }
        return (true);
    }

    /**
     *  センサの監視をやめる
     */
    public void finishWatch()
    {
        if (sensors == null)
        {
            return;
        }
        try
        {
            // センサ情報をクリアして監視を停止する
            sensorService.flush(this);
            sensorService.unregisterListener(this);
        }
        catch (Exception e)
        {
            //
            //
            //
        }
        return;
    }

    /**
     *   Accuracyが変更された！
     */
    public void  onAccuracyChanged(Sensor sensor, int accuracy)
    {
        //
    }

    /**
     *  センサの値が更新された！
     */
    public void  onSensorChanged(SensorEvent event)
    {
        if (subscriber == null)
        {
            // 受信待ちがいなかった
            return;
        }

        // イベント配信先を探す
        int sensorType = event.sensor.getType();
        ISensorReceiver receiver = subscriber.get(sensorType);
        if (receiver.getSensorType() != sensorType)
        {
            return;
        }

        // センサ変更イベントを配信しても良いかどうか確認
        if (receiver.sensorNotify(event.sensor) == false)
        {
            // 先に進めさせない...
            return;
        }

        // センサ変更イベントを配信
        receiver.onSensorChanged(event);
        return;
    }

    /**
     *   トリガイベントを受信した！
     *    (TriggerEventListenerの実装)
     *
     * @param event
     */
    public void onTrigger(TriggerEvent event)
    {
        //
    }

    /**
     *  センサ変更イベントの通知インタフェース
     * @author MRSa
     *
     */
    public interface ISensorReceiver
    {
        /** 監視するセンサタイプを応答する **/
        public abstract int getSensorType();

        /** センサ変更イベントを受信した！ **/
        public abstract boolean sensorNotify(Sensor device);

        /** センサ値が変化した！ **/
        public abstract void onSensorChanged(SensorEvent event);
    }
}
