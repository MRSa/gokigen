package jp.sourceforge.gokigen.graphic.test;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

/**
 *  センサの状態を取得し、表示する！
 * 
 * @author MRSa
 *
 */
public class SensorListener implements ISensorNotification, IOrientationHolder
{
	private int                       monitorSensor = Sensor.TYPE_ALL;  // 監視するセンサ
	private Activity                  parent        = null;
    private Map<String, SensorViewer> sensorList    = null;
    
    private float                    orientationX  = 0.0f;
    private float                    orientationY  = 0.0f;
    private float                    orientationZ  = 0.0f;    

    /**
     *   コンストラクタ
     */
    public SensorListener(Activity argument, int sensor)
    {
    	parent     = argument;
    	sensorList = new HashMap<String, SensorViewer>();
    	monitorSensor = sensor;
    }

    public float getOrientationX()
    {
    	return (orientationX);
    }
    

    public float getOrientationY()
    {
    	return (orientationY);
    }

    public float getOrientationZ()
    {
    	return (orientationZ);
    }
    
    /**
     *  センサの変更イベントを受信した！
     */
    public boolean sensorNotify(Sensor device)
    {
    	return (true);
    }
    
    /**
     *  センサの変更イベント処理する！
     */
    public void onSensorChanged(SensorEvent event)
    {

        String key = "" + event.sensor.getType();

        SensorViewer data = sensorList.get(key);
        if (data == null)
        {
        	int field = R.id.sensorData;
        	data = new SensorViewer(field, event.sensor.getType(), event.sensor.getName());
        	sensorList.put(key, data);
        }

    	/** 表示するメッセージを作る **/
    	String message = setMessage(event, data);

    	/** メッセージを表示する **/
        TextView messageArea = (TextView) parent.findViewById(data.getFieldId());
        messageArea.setText(message.toCharArray(), 0, message.length());     
    }
    
    /**
     *   表示メッセージを作る
     * @param data
     * @return
     */
    private String setMessage(SensorEvent event, SensorViewer data)
    {
    	String message = "";
    	switch (data.getSensorType())
    	{
    	  case Sensor.TYPE_ACCELEROMETER:
      		message = message + "Accelerometer: \n\t" + parseAccelerometer(event);
      		//message = message + "Accelerometer: \n\t" + parseUnknown(event);
            break;

    	  case Sensor.TYPE_GYROSCOPE:
      		message = message + "Gyroscope: \n\t" + parseGyroscope(event);
      		//message = message + "Gyroscope: \n\t" + parseUnknown(event);
            break;

    	  case Sensor.TYPE_LIGHT:
      		message = message + "Light: \n\t" + parseLight(event);
      		//message = message + "Light: \n\t" + parseUnknown(event);
            break;

    	  case Sensor.TYPE_MAGNETIC_FIELD:
      	    message = message + "MagneticField: \n\t" + parseMagneticField(event);
      		//message = message + "MagneticField: \n\t" + parseUnknown(event);
            break;

    	  case Sensor.TYPE_ORIENTATION:
      		message = message + "Orientation: \t\t" + parseOrientation(event);
      		//message = message + "Orientation: \n\t" + parseUnknown(event);
            break;

    	  case Sensor.TYPE_PRESSURE:
      		message = message + "Pressure: \n\t" + parsePressure(event);
      		//message = message + "Pressure: \n\t" + parseUnknown(event);
            break;

    	  case Sensor.TYPE_PROXIMITY:
      		message = message + "Proximity: \n\t" + parseProximity(event);
      		//message = message + "Proximity: \n\t" + parseUnknown(event);
            break;

    	  case Sensor.TYPE_TEMPERATURE:
      		message = message + "Temperature: \n\t" + parseTemperature(event);
      		//message = message + "Temperature: \n\t" + parseUnknown(event);
            break;

    	  default:
    		message = message + "?(" + data.getSensorType() + "): \n\t" + parseUnknown(event);
            break;
    	}
    	return (message);
    }


    private String parseAccelerometer(SensorEvent event)
    {
       /**
          All values are in SI units (m/s^2) and measure the acceleration applied to the phone minus the force of gravity.

            values[0]: Acceleration minus Gx on the x-axis
            values[1]: Acceleration minus Gy on the y-axis
            values[2]: Acceleration minus Gz on the z-axis        

            Examples:
              # When the device lies flat on a table and is pushed on its left side toward the right,
                the x acceleration value is positive.
              # When the device lies flat on a table, the acceleration value is +9.81,
                which correspond to the acceleration of the device (0 m/s^2) minus the force of gravity (-9.81 m/s^2).
              # When the device lies flat on a table and is pushed toward the sky with an acceleration of A m/s^2, 
               the acceleration value is equal to A+9.81 which correspond to the acceleration of the device (+A m/s^2) minus 
               the force of gravity (-9.81 m/s^2).        
        **/

    	String message = "";
    	try
    	{
    		message = "X:" + event.values[0] + "\n\tY:" + event.values[1] + "\n\tZ:" + event.values[2] + " m/s^2";
    	}
    	catch (Exception ex)
    	{
    		message = "---";
    	}
    	return (message);
    }
    
    
    private String parseGyroscope(SensorEvent event)
    {
    	String message = "";
    	try
    	{
    		message = "" + event.values[0] + " ???";
    	}
    	catch (Exception ex)
    	{
    		message = "---";
    	}    	
    	return (message);
    }
     
    
    private String parseLight(SensorEvent event)
    {
        /** values[0]: Ambient light level in SI lux units  **/
    	String message = "";
    	try
    	{
    		message = event.values[0] + " lux";
    	}
    	catch (Exception ex)
    	{
    		message = "---";
    	}
    	return (message);
    }
    
    
    private String parseMagneticField(SensorEvent event)
    {
    	/** All values are in micro-Tesla (uT) and measure the ambient magnetic field in the X, Y and Z axis.  **/

    	String message = "";
    	try
    	{
    	    message = "X:" + event.values[0] + "\n\tY:" + event.values[1] + "\n\tZ:" + event.values[2] + " uT";
    	}
    	catch (Exception ex)
    	{
    		message = "---";
    	}
    	return (message);
    }
    
    private String parseOrientation(SensorEvent event)
    {
        /**
    	    All values are angles in degrees.

    	    values[0]: Azimuth, angle between the magnetic north direction 
    	               and the Y axis, around the Z axis (0 to 359). 0=North, 90=East, 180=South, 270=West

            values[1]: Pitch, rotation around X axis (-180 to 180), with positive values when the z-axis moves toward the y-axis.

    	    values[2]: Roll, rotation around Y axis (-90 to 90), with positive values when the x-axis moves away from the z-axis.

            Note: This definition is different from yaw, pitch and roll used in aviation 
                 where the X axis is along the long side of the plane (tail to nose).

            Note: It is preferable to use getRotationMatrix() in conjunction with remapCoordinateSystem()
                 and getOrientation() to compute these values; while it may be more expensive, it is usually more accurate. 
        **/

    	String message = "";
    	try
    	{
    		orientationX = event.values[0];
    		orientationY = event.values[1];
    		orientationZ = event.values[2];
    	    message = "X:" + event.values[0] + "\tY:" + event.values[1] + "\tZ:" + event.values[2] + "";
    	}
    	catch (Exception ex)
    	{
    		message = "---";
    	}
    	return (message);
    }
    
    private String parsePressure(SensorEvent event)
    {
    	String message = "";
    	try
    	{
    		message = "" + event.values[0] + " ???";
    	}
    	catch (Exception ex)
    	{
    		message = "---";
    	}    	
    	return (message);
    }
    
    private String parseProximity(SensorEvent event)
    {
        /**
           values[0]: Proximity sensor distance measured in centimeters

           Note that some proximity sensors only support a binary "close" or "far" measurement.
           In this case, the sensor should report its maxRange value in the "far" state 
           and a value less than maxRange in the "near" state.     	
    	**/

    	String message = "";
    	try
    	{
    		message = "" + event.values[0] + " cm";
    	}
    	catch (Exception ex)
    	{
    		message = "---";
    	}    	
    	return (message);
    }
    
    private String parseTemperature(SensorEvent event)
    {
    	String message = "";
    	try
    	{
    		message = "" + event.values[0] + " degC";
    	}
    	catch (Exception ex)
    	{
    		message = "---";
    	}    	
    	return (message);
    }
    
    private String parseUnknown(SensorEvent event)
    {
    	String message = "";
    	try
    	{
    		int len = event.values.length;
    		for (int index = 0; index < len; index++)
    		{
    			message = message + "[" + index + "]" +  event.values[index] + " ";
    		}
    	}
    	catch (Exception ex)
    	{
    		message = "---";
    	}    	
    	return (message);
    }

    /**
	 *  監視するセンサタイプを応答する
	 * @return
	 */
    public int getMonitorSensorType()
    {
        return (monitorSensor);
    }
    
    /**
     *  センサの情報
     * @author MRSa
     *
     */
    private class SensorViewer
    {
    	private int   fieldId = R.id.sensorData;
    	private int   sensorType = 0;
    	private String sensorName = "";

    	/**
    	 *  コンストラクタ
    	 * @param id
    	 * @param name
    	 */
        public SensorViewer(int id, int type, String name)
        {
            fieldId    = id;
            sensorType = type;
            sensorName = name;
        }
        
        public int getFieldId()
        {
        	return (fieldId);
        }
        
        public String getSensorName()
        {
        	return (sensorName);
        }
        
        public int getSensorType()
        {
        	return (sensorType);
        }        
    }    
}
