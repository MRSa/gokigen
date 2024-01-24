package jp.sourceforge.gokigen.graphic.test;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 *  センサをラップするクラス
 * @author MRSa
 *
 */
public class SensorWrapper implements SensorEventListener
{
	private Activity            parent        = null;
	private SensorManager       sensorService = null;
	private ISensorNotification subscriber    = null;
    private List<Sensor>        sensors       = null;
	private int                 sensorType    = Sensor.TYPE_ALL;

	/**
	 *  コンストラクタ
	 * @param argument
	 */
    public SensorWrapper(Activity argument, ISensorNotification receiver)
    {
        parent = argument;
        sensorType = receiver.getMonitorSensorType();
	    subscriber = receiver;
    }
	
    /**
     *   センサの準備
     * @return
     */
    public boolean prepareSensor()
    {
        try
        {
        	sensors  = null;
        	sensorService = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
            if (sensorService == null)
            {
        	    return (false);
            }
            sensors = sensorService.getSensorList(sensorType);
        }
        catch (Exception ex)
        {
        	sensorService = null;
        	return (false);
        }
        return (true);
    }

    /**
     *  センサの監視を開始する
     * @return  true : 監視開始, false : 開始失敗
     */
    public boolean startWatch()
    {
        try
        {
        	for (int index = 0; index < sensors.size(); index++)
            {
            	try
            	{
                    Sensor sensor = sensors.get(index);
                    sensorService.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            	}
                catch (Exception e)
                {
                	//
                	//
                	//
                }
            }
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

    	if (subscriber.sensorNotify(event.sensor) == false)
    	{
    		// 先に進めさせない...
    		return;
    	}
    	subscriber.onSensorChanged(event);
    	return;
    }
}
