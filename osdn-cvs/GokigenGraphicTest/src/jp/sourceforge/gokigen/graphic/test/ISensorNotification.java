package jp.sourceforge.gokigen.graphic.test;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

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

    /** �Z���T�l���ω������I **/
    public abstract void onSensorChanged(SensorEvent event);
}
