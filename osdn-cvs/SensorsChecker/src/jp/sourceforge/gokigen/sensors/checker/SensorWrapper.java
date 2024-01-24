package jp.sourceforge.gokigen.sensors.checker;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

/**
 *  センサをラップするクラス
 * @author MRSa
 *
 */
public class SensorWrapper implements SensorEventListener, LocationListener
{
	private Activity            parent          = null;
	private LocationManager     locationService = null;
	private SensorManager       sensorService   = null;
	private ISensorNotification subscriber      = null;
    private List<Sensor>        sensors         = null;
	private int                 sensorType      = Sensor.TYPE_ALL;
	private int                locationStatus   = LocationProvider.OUT_OF_SERVICE;

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
            
            locationService = (LocationManager) parent.getSystemService(Context.LOCATION_SERVICE);
        }
        catch (Exception ex)
        {
        	sensorService = null;
        	locationService = null;
        	return (false);
        }
        return (true);
    }

    /**
     *  センサの監視を開始する
     * @return  true : 監視開始, false : 開始失敗
     */
    public boolean startWatch(PreferenceUtility prefUtil)
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
            if (locationService != null)
            {
            	if ((prefUtil != null)&&(prefUtil.getValueBoolean("useNetworkGps") == true))
            	{
                    locationService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            	}
            	else
            	{
                    locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            	}
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(parent, "EXCEPTION : " + ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
            if (locationService != null)
            {
            	locationService.removeUpdates(this);
            }
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

    /**
     *  位置情報が更新された！
     */
    public void onLocationChanged(Location location)
    {
    	if (subscriber == null)
    	{
    		// 受信待ちがいなかった
    		return;
    	}

    	if (subscriber.locationNotify(location.getProvider()) == false)
    	{
    		// 先に進めさせない...
    		return;
    	}

    	// 位置情報が変わった...
    	subscriber.onLocationChanged(location);
    	return;
    }
    
    /**
     *   
     * 
     */
    public void onProviderDisabled(String provider)
    {
    	String message = provider + " : DISABLED";
        Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();    	
    }
    
    /**
     * 
     * 
     */
    public void onProviderEnabled(String provider)
    {
    	String message = provider + " : ENABLED";
        Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();    	
    }

    /**
     * 
     * 
     */
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    	String message = provider + " : ";
        switch (status)
        {
          case LocationProvider.AVAILABLE:
        	message = message + "AVAILABLE";
            break;

          case LocationProvider.OUT_OF_SERVICE:
            message = message + "OUT OF SERVICE";
            break;

          case LocationProvider.TEMPORARILY_UNAVAILABLE:
            message = message + "TEMPORARILY UNAVAILABLE";
            break;

          default:
        	message = message + " ???(" + status + ")";
            break;
        }
        if (locationStatus != status)
        {
            Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();
            locationStatus = status;
        }
    }
}
