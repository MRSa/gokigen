package jp.sourceforge.gokigen.viewsensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * Created by MRSa on 2015/01/02.
 */
public class SensorWatcher implements SensorWrapper.ISensorReceiver
{
    private int mSensorType = 0;
    private boolean mIsReport = false;
    private ISensorChanged mReporter = null;

    public SensorWatcher(int sensorType, ISensorChanged reporter)
    {
        mSensorType = sensorType;
        mReporter = reporter;
    }

    public void readyReport(boolean isReport)
    {
        mIsReport = isReport;
    }


    @Override
    public int getSensorType()
    {
        return (mSensorType);
    }

    @Override
    public boolean sensorNotify(Sensor device)
    {
        return (mIsReport);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (mReporter != null)
        {
            mReporter.onSensorChanged(event);
        }
    }

    /**
     *
     *
     */
    public interface ISensorChanged
    {
        public abstract void onSensorChanged(SensorEvent event);
    }

    public interface IPressureValueHolder
    {
        public abstract float getPressure();
        public abstract float getPressureOffset();
        public abstract float getAltitude(boolean isCalibrate);
        public abstract void  setAltitude(float value);
        public abstract void  clearPressureOffset();
    }

    public interface IRotationVectorValueHolder
    {
        public abstract float getNorthAngle();
    }

    public interface ICalibrationExecutor
    {
        public abstract void startCalibrate();
    }
}
