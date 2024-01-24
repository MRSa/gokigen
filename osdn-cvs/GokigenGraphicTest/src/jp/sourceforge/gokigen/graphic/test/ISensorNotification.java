package jp.sourceforge.gokigen.graphic.test;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

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

    /** センサ値が変化した！ **/
    public abstract void onSensorChanged(SensorEvent event);
}
