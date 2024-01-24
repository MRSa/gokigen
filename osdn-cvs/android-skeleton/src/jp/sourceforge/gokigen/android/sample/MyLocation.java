package jp.sourceforge.gokigen.android.sample;

import android.location.Location;

/**
 *   位置情報を記憶するクラス
 *   (Locationクラスの中身が変更できないので...)
 * 
 * @author MRSa
 *
 */
public class MyLocation
{
    private double           lastLatitude = 0.0;
    private double           lastLongitude = 0.0;
    private long             lastChangedTime   = 0;
    private long             lastLocationTime  = 0;
    private float            lastAccuracy = 0;
    private double           lastAltitude = 0.0;
    private float            lastSpeed    = 0;
    private float            lastBearing  = 0;
    
    private String            locationInfo = "";
    
    /**
     *  コンストラクタ
     */
    public MyLocation()
    {
    }

    /**
     *   位置情報を記録する
     * @param location
     * @return 位置が変更された(true) / 位置が前のまま(false)
     */
    public boolean setLocation(Location location)
    {
        boolean result = false;
        double latitude = Math.abs(lastLatitude - location.getLatitude());
        double longitude = Math.abs(lastLongitude - location.getLongitude());

        if ((latitude > 0.0001)||(longitude > 0.0001))
        {
            lastChangedTime = location.getTime();
        }
        
        lastLocationTime = location.getTime();
        // if ((latitude > 0.0001)||(longitude > 0.0001))
        {
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            lastAccuracy = location.getAccuracy();
            lastAltitude = location.getAltitude();
            lastSpeed = location.getSpeed();
            lastBearing = location.getBearing();

            result = true;
        }

        locationInfo = GeocoderWrapper.getLocationString(location.getLatitude(), location.getLongitude());
        return (result);
    }

    /**
     *  位置情報を更新する
     * 
     * @param time
     * @param latitude
     * @param longitude
     * @return
     */
    public void setLocation(long time, double latitude, double longitude)
    {
        lastLocationTime = time;
        lastLatitude = latitude;
        lastLongitude = longitude;
    }
    
    /**
     *  位置情報文字列を書き換える
     * 
     * @param info 位置情報文字列
     */
    public void setLocationInfo(String info)
    {
    	locationInfo = info;
    }    

    /**
     *  位置情報文字列を応答する
     * 
     * @return  位置情報文字列
     */
    public String getLocationInfo()
    {
    	return (locationInfo);
    }

    /**
     *  
     * @return
     */
    public long getStayMinites()
    {
        return (((lastChangedTime - lastLocationTime) / 1000) / 60);
    }        
    
    /**
     *  位置情報メッセージを応答する
     * @return
     */
    public String getLocationSummaryString()
    {
        // Excel用の時間...
        double locTimeForExcel = (lastLocationTime / 86400000.0) + 25569.0 + 0.375;
        long   diffTime = lastLocationTime - lastChangedTime;

        //  記録に残す位置情報を取得する
        String message = "X,";
        message = message + lastLocationTime  + ",";    // 時刻
        message = message + lastLatitude + ",";         // 緯度
        message = message + lastLongitude + ",";        // 経度
        message = message + lastAltitude + ",";         //
        message = message + lastBearing + ",";
        message = message + lastSpeed + ",";
        message = message + lastAccuracy + ",";
        message = message + lastChangedTime + ",";      // 変化検出時刻
        message = message + locTimeForExcel + ",";      // Excel用時刻
        message = message + diffTime + "\r\n";

        return (message);
    }

    /**
     *   緯度を取得する
     * @return  緯度
     */
    public double getLatitude()
    {
        return (lastLatitude);
    }

    /**
     *   経度を取得する
     * @return  経度
     */
    public double getLongitude()
    {
        return (lastLongitude);
    }
    
    /**
     *  時間を取得する
     * @return (位置情報の)時間
     */
    public long getTime()
    {
        return (lastLocationTime);
    }

}
