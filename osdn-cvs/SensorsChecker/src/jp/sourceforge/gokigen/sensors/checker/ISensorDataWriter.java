package jp.sourceforge.gokigen.sensors.checker;

import android.location.Location;

public interface ISensorDataWriter
{
    /** センサデータを出力する **/
	public abstract void writeSensorData(long currentTime, int sensorType, String data);

	/** 位置情報データを出力する **/
	public abstract void writeLocationData(Location location, long duration);

}
