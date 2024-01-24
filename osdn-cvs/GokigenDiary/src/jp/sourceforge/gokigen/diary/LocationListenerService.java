package jp.sourceforge.gokigen.diary;

import java.io.FileOutputStream;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import android.util.Log;

/**
 *  LocationHolderService : 位置情報を周期的に確認し、sd cardにログ出力するサービス。
 * @author MRSa
 *
 */
public class LocationListenerService extends Service implements ITimerReceiver, ILocationReceiver
{
    public static final String ACTION = "Location Holder Service";
    public static final long   MINIMUM_DURATION = 10 * 1000;

//    private ExternalStorageFileUtility fileUtility = null;
    private TimerThread      mTimer = null;
    private LocationManager  locationService = null;
    private MyLocation        lastLocation = null;
    private long             lastLocationTime = 0;
    private boolean          isFirstTime = true;

    private LocationListenerImpl locationReceiver = null;
    
    /**
     * 
     * @author MRSa
     *
     */
    class LocationHolderBinder extends Binder
    {
        LocationListenerService getService()
        {
            return (LocationListenerService.this);
        }
    }
    
    /**
     * 
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        try
        {
            locationService = (LocationManager) (LocationListenerService.this).getSystemService(Context.LOCATION_SERVICE);
            locationReceiver = new LocationListenerImpl(this);
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "Ex :" + ex.toString() + " " + ex.getMessage());
            locationService = null;
        }
        lastLocation = new MyLocation();

        String message = getString(R.string.app_name);
        message = message + getString(R.string.app_started);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 
     */
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        /** intentの付加情報を取得する  **/
        boolean useNetworkGps = false;
        long     timeoutValue  = 10 * 60 * 1000;
        try
        {
            useNetworkGps = intent.getBooleanExtra(MainListener.INTENTINFO_GPSTYPE, false);
            timeoutValue  = intent.getLongExtra(MainListener.INTENTINFO_DURATION, (5 * 60 * 1000));
        }
        catch (Exception ex)
        {
            //
            Log.v(Main.APP_IDENTIFIER, "Ex : " + ex.toString() + " " + ex.getMessage());
        }

        /** 監視間隔の最小値チェック **/
        if (timeoutValue < MINIMUM_DURATION)
        {
            timeoutValue = MINIMUM_DURATION;  // 最小値よりも小さい場合、最小値に更新する
        }
        
        /**  位置情報を取得する設定を行う **/
        try
        {
            if (locationService != null)
            {
                locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeoutValue, 0, locationReceiver);
                if (useNetworkGps == true)
                {
                    // 位置情報をネットワーク経由でも取得するように変更する
                    locationService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, timeoutValue, 0, locationReceiver);
                }
            }
            Log.v(Main.APP_IDENTIFIER, "Start watching every " + timeoutValue + "ms");
        }
        catch (Exception ex)
        {
            // なにもしない
        }

        /** 周期監視(ログファイル出力)を行う設定をする **/
        try
        {
            if (mTimer != null)
            {
                mTimer.stopWatchdog();
                mTimer = null;
            }
            mTimer = new TimerThread(this, timeoutValue);
            mTimer.start();
        }
        catch (Exception ex)
        {
            // タイマ監視
            Log.v(Main.APP_IDENTIFIER, "TimerAborted() " + ex.toString() + "(" + ex.getMessage() + ")");
        }
    }

    /**
     *  タイムアウト検出！ このタイミングで位置情報をログ出力する！
     */
    public boolean receiveTimeout()
    {
        // 位置情報をファイルに出力する
        outputLocation();

        // 位置変化をIntentで通知する...
        sendBroadcast(new Intent(ACTION));

        return (true);
    }

    /**
     *  タイマーが開始した
     */
    public void timerStarted()
    {
        Log.v(Main.APP_IDENTIFIER, "Timer Started.");
    }

    /**
     *  タイマー監視が停止した！
     */
    public void timerStopped(String reason)
    {
        Log.v(Main.APP_IDENTIFIER, "Timer Stopped. (" + reason + ")");
    }
    
    /**
     * 
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        return (new LocationHolderBinder());
    }

    /**
     * 
     */
    @Override
    public void onRebind(Intent intent)
    {
        super.onRebind(intent);
    }

    /**
     * 
     */
    @Override
    public boolean onUnbind(Intent intent)
    {
        return (super.onUnbind(intent));
    }
    
    /**
     * 
     */
    @Override
    public void onDestroy()
    {
        /**  位置情報を動かしっぱなしなら、終了させる  **/
        if (locationService != null)
        {
            locationService.removeUpdates(locationReceiver);
        }

        String message = getString(R.string.app_name);
        message = message + getString(R.string.app_finished);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        
        if (mTimer != null)
        {
            mTimer.stopWatchdog();
            mTimer = null;
        }
        locationService = null;
        locationReceiver = null;
        System.gc();
    }

    /**
     *  現在位置情報を応答する
     * @return
     */
    public MyLocation getLocation()
    {
        return (lastLocation);
    }

    /**
     *  位置情報をファイル出力する
     * 
     */
    private void outputLocation()
    {
        if (lastLocationTime == lastLocation.getTime())
        {
            // 前回書き込みした位置情報と同じ位置だった...あえてファイルに書き込まない
            Log.v(Main.APP_IDENTIFIER, "<<< Same Location >>> : no output");
            return;
        }

        String fileName = "";
        String message = "";
        FileOutputStream oStream = null;
        try
        {
            message = lastLocation.getLocationSummaryString();

            ExternalStorageFileUtility fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);
            
            // ファイルに書き込みする (毎回Open & Closeするよう変更する)
            fileName = fileUtility.decideFileNameWithDate("location.csv");
            oStream = fileUtility.openFileStream(fileName, true);
            oStream.write(message.getBytes(), 0, message.length());
            oStream.flush();
            oStream.close();
        }
        catch (Exception ex)
        {
            // 例外発生、その旨ログ出力を行う。
            message = "outputLocation() Ex:" + ex.toString() + " " + ex.getMessage() + ", output : " + message;
            Log.v(Main.APP_IDENTIFIER, message);
            if (oStream != null)
            {
                try
                {
                    oStream.flush();
                }
                catch (Exception e)
                {
                    //
                }
                try
                {                    
                    oStream.close();
                }
                catch (Exception e)
                {
                    //
                }
            }
        }
        oStream = null;
        System.gc();
    }
    
    /**
     *  位置情報が更新された！
     *  
     *  LocationListener::onLocationChanged()
     *  
     */
    public void onLocationChanged(Location location)
    {
        try
        {
            // 位置情報をクラスに保存する
            lastLocation.setLocation(location);
        }
        catch (Exception ex)
        {
            //
        }

        if (isFirstTime == true)
        {
            // 初回のみファイルに出力する
            receiveTimeout();
            isFirstTime = false;
        }

        // 位置変化をIntentで通知する...
        sendBroadcast(new Intent(ACTION));
    }
}
