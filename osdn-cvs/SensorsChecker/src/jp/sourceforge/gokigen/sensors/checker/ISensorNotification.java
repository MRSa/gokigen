package jp.sourceforge.gokigen.sensors.checker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;

/**
 *  �Z���T�ύX�C�x���g�̒ʒm�C���^�t�F�[�X
 * @author MRSa
 *
 */
public interface ISensorNotification
{
    /** �Ď�����Z���T�^�C�v���������� **/
	public abstract int getMonitorSensorType();
	
	/** �Z���T�ύX�C�x���g����M�����I **/
    public abstract boolean sensorNotify(Sensor device);

    /** �ʒu�ύX�C�x���g����M�����I **/
    public abstract boolean locationNotify(String provider);

    /** �Z���T�l���ω������I **/
    public abstract void onSensorChanged(SensorEvent event);

    /** �ʒu���ω������I **/
    public abstract void onLocationChanged(Location location);
}
