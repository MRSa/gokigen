package jp.sourceforge.gokigen.sensors.checker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;

/**
 *  センサ変更イベントの通知インタフェース
 * @author MRSa
 *
 */
public interface ISensorNotification
{
    /** 監視するセンサタイプを応答する **/
	public abstract int getMonitorSensorType();
	
	/** センサ変更イベントを受信した！ **/
    public abstract boolean sensorNotify(Sensor device);

    /** 位置変更イベントを受信した！ **/
    public abstract boolean locationNotify(String provider);

    /** センサ値が変化した！ **/
    public abstract void onSensorChanged(SensorEvent event);

    /** 位置が変化した！ **/
    public abstract void onLocationChanged(Location location);
}
