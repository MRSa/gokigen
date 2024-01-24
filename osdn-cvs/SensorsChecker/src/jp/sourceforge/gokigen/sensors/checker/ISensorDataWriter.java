package jp.sourceforge.gokigen.sensors.checker;

import android.location.Location;

public interface ISensorDataWriter
{
    /** �Z���T�f�[�^���o�͂��� **/
	public abstract void writeSensorData(long currentTime, int sensorType, String data);

	/** �ʒu���f�[�^���o�͂��� **/
	public abstract void writeLocationData(Location location, long duration);

}
