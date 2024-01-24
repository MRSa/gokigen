package jp.sourceforge.gokigen.graphic.test;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 *  �Z���T�����b�v����N���X
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
	 *  �R���X�g���N�^
	 * @param argument
	 */
    public SensorWrapper(Activity argument, ISensorNotification receiver)
    {
        parent = argument;
        sensorType = receiver.getMonitorSensorType();
	    subscriber = receiver;
    }
	
    /**
     *   �Z���T�̏���
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
     *  �Z���T�̊Ď����J�n����
     * @return  true : �Ď��J�n, false : �J�n���s
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
     *  �Z���T�̊Ď�����߂�
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
     *   Accuracy���ύX���ꂽ�I
     */
    public void  onAccuracyChanged(Sensor sensor, int accuracy)
    {
    	
    }

    /**
     *  �Z���T�̒l���X�V���ꂽ�I
     */
    public void  onSensorChanged(SensorEvent event)
    {
    	if (subscriber == null)
    	{
    		// ��M�҂������Ȃ�����
    		return;
    	}

    	if (subscriber.sensorNotify(event.sensor) == false)
    	{
    		// ��ɐi�߂����Ȃ�...
    		return;
    	}
    	subscriber.onSensorChanged(event);
    	return;
    }
}
