package jp.sourceforge.gokigen.diary;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 *  
 * @author MRSa
 *
 */
public class LocationListenerImpl implements LocationListener
{
//    private int locationStatus  = LocationProvider.OUT_OF_SERVICE;
    private ILocationReceiver receiver = null;
    
    /**
     *  コンストラクタ
     * 
     */
    public LocationListenerImpl(ILocationReceiver arg)
    {
        receiver = arg;
    }
    
    /**
     *  LocationListener::onProviderDisabled()
     * 
     */
    public void onProviderDisabled(String provider)
    {
        Log.v(Main.APP_IDENTIFIER, "onProviderDisabled() : " + provider);
    }
    
    /**
     *  LocationListener::onProviderEnabled()
     * 
     */
    public void onProviderEnabled(String provider)
    {
        Log.v(Main.APP_IDENTIFIER, "onProviderEnabled() : " + provider);
    }

    /**
     *   LocationListener::onStatusChanged()
     * 
     */
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
//        locationStatus = status;
/*
    	String message = provider + " : ";
        switch (status)
        {
          case LocationProvider.AVAILABLE:
            message = message + "AVAILABLE";
            break;

          case LocationProvider.OUT_OF_SERVICE:
            message = message + "OUT OF SERVICE";
            break;

          case LocationProvider.TEMPORARILY_UNAVAILABLE:
            message = message + "TEMPORARILY UNAVAILABLE";
            break;

          default:
            message = message + " ???(" + status + ")";
            break;
        }

        if (locationStatus != status)
        {
            locationStatus = status;
            Log.v(Main.APP_IDENTIFIER, "onStatusChanged() " + provider + ", " + message);
        }
*/
    }

    /**
     *  位置情報受信！    
     */
    public void onLocationChanged(Location location)
    {
        // 位置情報をログ出力する
        //Log.v(Main.APP_IDENTIFIER, "onLocationChanged() [" + location.getLatitude() + "," + location.getLongitude() + "]");
        if (receiver != null)
        {
            receiver.onLocationChanged(location);
        }
    }    
}
