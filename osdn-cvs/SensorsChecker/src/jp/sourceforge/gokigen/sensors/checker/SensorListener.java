package jp.sourceforge.gokigen.sensors.checker;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

/**
 *  センサの状態を取得し、表示する！
 * 
 * @author MRSa
 *
 */
public class SensorListener implements ISensorNotification
{
	private static final int MONITOR_SENSOR = Sensor.TYPE_ALL;  // 監視するセンサ

	private Activity                 parent     = null;
    private int                      outputLine = 0;    
    private Map<String, SensorViewer> sensorList = null;
    private ISensorDataWriter         sensorWriter = null;

    private double                  currentLatitude = 0.0;
    private double                  currentLongitude = 0.0;
    private long                    currentLocTime  = 0;
    
    /**
     *   コンストラクタ
     */
    public SensorListener(Activity argument)
    {
    	parent     = argument;
    	sensorList = new HashMap<String, SensorViewer>();
    }

    /**
     *   センサ出力先を設定する
     * @param writer
     */
    public void setSensorWriter(ISensorDataWriter writer)
    {
        sensorWriter = writer;    	
    }

    /**
     *  センサの変更イベントを受信した！
     */
    public boolean sensorNotify(Sensor device)
    {
    	return (true);
    }
  
    /**
     *  位置情報の変更イベントを受信した！
     * 
     */
    public boolean locationNotify(String provider)
    {
    	return (true);
    }
    
    /**
     *  位置情報の値が変更になった！
     * 
     */
    public void onLocationChanged(Location location)
    {
        String message = "???";
        if (location != null)
        {
            long changedTime = location.getTime();
            double latitude  = location.getLatitude();
            double longitude = location.getLongitude();

            // 位置情報
        	message = "Location :";
        	message = message + "\n\t" + "Latitude \t" + String.valueOf(latitude);
        	message = message + "\n\t" + "Longitude \t" + String.valueOf(longitude);
        	message = message + "\n\t" + "Accuracy \t" + String.valueOf(location.getAccuracy());
        	message = message + "\n\t" + "Altitude \t" + String.valueOf(location.getAltitude());
        	message = message + "\n\t" + "Time \t" + String.valueOf(changedTime);
        	message = message + "\n\t" + "Speed \t" + String.valueOf(location.getSpeed());
        	message = message + "\n\t" + "Bearing \t" + String.valueOf(location.getBearing());

        	/** 位置が移動したか確認し、位置が変わっていたらファイルに記録する  **/
            if ((latitude != currentLatitude)||(longitude != currentLongitude))
            {
            	if (sensorWriter != null)
            	{
            		sensorWriter.writeLocationData(location, (changedTime - currentLocTime));
            	}
            	currentLocTime = changedTime;
            	currentLatitude = latitude;
            	currentLongitude = longitude;            	
            }
        }
        
    	/** メッセージを表示する **/
        TextView messageArea = (TextView) parent.findViewById(R.id.locLine);
        messageArea.setText(message.toCharArray(), 0, message.length());     
    }

    /**
     *  センサの変更イベント処理する！
     */
    public void onSensorChanged(SensorEvent event)
    {
    	long currentTimeMillis = System.currentTimeMillis();  // 現在時刻の取得

        String key = "" + event.sensor.getType();

        SensorViewer data = sensorList.get(key);
        if (data == null)
        {
        	int field = decideLine(outputLine);        	
        	outputLine++;
        	if (outputLine > 10)
        	{
        		outputLine = 0;
        	}
        	data = new SensorViewer(field, event.sensor.getType(), event.sensor.getName());
        	sensorList.put(key, data);
        }

        /** 出力するメッセージを作る **/
        String writeMessage = setWriteMessage(event, data);

        /** センサーデータを出力する **/
        if ((sensorWriter != null)&&(writeMessage != null))
        {
            sensorWriter.writeSensorData(currentTimeMillis, data.getSensorType(), writeMessage);
        }

    	/** 表示するメッセージを作る **/
    	String message = setMessage(event, data);


    	/** メッセージを表示する **/
        TextView messageArea = (TextView) parent.findViewById(data.getFieldId());
        messageArea.setText(message.toCharArray(), 0, message.length());     
    }
    
    /**
     *   出力メッセージを作る
     * @param data
     * @return
     */
    private String setWriteMessage(SensorEvent event, SensorViewer data)
    {
    	if (data.getSensorType() == Sensor.TYPE_ACCELEROMETER)
    	{
        	String message = "";
        	try
        	{
        	    message = event.values[0] + "\t" + event.values[1] + "\t" + event.values[2];
        	}
        	catch (Exception ex)
        	{
        		message = "";
        	}
        	return (message);
    	}
		return (null);
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
      		message = message + "Orientation: \n\t" + parseOrientation(event);
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
    	    message = "X:" + event.values[0] + "\n\tY:" + event.values[1] + "\n\tZ:" + event.values[2] + " deg";
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
        return (MONITOR_SENSOR);
    }
    
    /**
     *  表示場所を設定する
     * @param count
     * @return
     */
    private int decideLine(int count)
    {
       int field = R.id.line0;
       
        switch (count)
        {
          case 1:
            field = R.id.line1;
            break;

          case 2:
              field = R.id.line2;
              break;

          case 3:
              field = R.id.line3;
              break;

          case 4:
              field = R.id.line4;
              break;

          case 5:
              field = R.id.line5;
              break;

          case 6:
              field = R.id.line6;
              break;

          case 7:
              field = R.id.line7;
              break;

          case 8:
              field = R.id.line8;
              break;

          case 9:
              field = R.id.line9;
              break;

          case 10:
              field = R.id.line10;
              break;

          case 0:
          default:
            field = R.id.line0;
            break;
        }
        return (field);
    }
    
    /**
     *  センサの情報
     * @author MRSa
     *
     */
    private class SensorViewer
    {
    	private int   fieldId = R.id.line0;
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
