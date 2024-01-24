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
 *  LocationHolderService : �ʒu���������I�Ɋm�F���Asd card�Ƀ��O�o�͂���T�[�r�X�B
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

        /** intent�̕t�������擾����  **/
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

        /** �Ď��Ԋu�̍ŏ��l�`�F�b�N **/
        if (timeoutValue < MINIMUM_DURATION)
        {
            timeoutValue = MINIMUM_DURATION;  // �ŏ��l�����������ꍇ�A�ŏ��l�ɍX�V����
        }
        
        /**  �ʒu�����擾����ݒ���s�� **/
        try
        {
            if (locationService != null)
            {
                locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeoutValue, 0, locationReceiver);
                if (useNetworkGps == true)
                {
                    // �ʒu�����l�b�g���[�N�o�R�ł��擾����悤�ɕύX����
                    locationService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, timeoutValue, 0, locationReceiver);
                }
            }
            Log.v(Main.APP_IDENTIFIER, "Start watching every " + timeoutValue + "ms");
        }
        catch (Exception ex)
        {
            // �Ȃɂ����Ȃ�
        }

        /** �����Ď�(���O�t�@�C���o��)���s���ݒ������ **/
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
            // �^�C�}�Ď�
            Log.v(Main.APP_IDENTIFIER, "TimerAborted() " + ex.toString() + "(" + ex.getMessage() + ")");
        }
    }

    /**
     *  �^�C���A�E�g���o�I ���̃^�C�~���O�ňʒu�������O�o�͂���I
     */
    public boolean receiveTimeout()
    {
        // �ʒu�����t�@�C���ɏo�͂���
        outputLocation();

        // �ʒu�ω���Intent�Œʒm����...
        sendBroadcast(new Intent(ACTION));

        return (true);
    }

    /**
     *  �^�C�}�[���J�n����
     */
    public void timerStarted()
    {
        Log.v(Main.APP_IDENTIFIER, "Timer Started.");
    }

    /**
     *  �^�C�}�[�Ď�����~�����I
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
        /**  �ʒu���𓮂������ςȂ��Ȃ�A�I��������  **/
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
     *  ���݈ʒu������������
     * @return
     */
    public MyLocation getLocation()
    {
        return (lastLocation);
    }

    /**
     *  �ʒu�����t�@�C���o�͂���
     * 
     */
    private void outputLocation()
    {
        if (lastLocationTime == lastLocation.getTime())
        {
            // �O�񏑂����݂����ʒu���Ɠ����ʒu������...�����ăt�@�C���ɏ������܂Ȃ�
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
            
            // �t�@�C���ɏ������݂��� (����Open & Close����悤�ύX����)
            fileName = fileUtility.decideFileNameWithDate("location.csv");
            oStream = fileUtility.openFileStream(fileName, true);
            oStream.write(message.getBytes(), 0, message.length());
            oStream.flush();
            oStream.close();
        }
        catch (Exception ex)
        {
            // ��O�����A���̎|���O�o�͂��s���B
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
     *  �ʒu��񂪍X�V���ꂽ�I
     *  
     *  LocationListener::onLocationChanged()
     *  
     */
    public void onLocationChanged(Location location)
    {
        try
        {
            // �ʒu�����N���X�ɕۑ�����
            lastLocation.setLocation(location);
        }
        catch (Exception ex)
        {
            //
        }

        if (isFirstTime == true)
        {
            // ����̂݃t�@�C���ɏo�͂���
            receiveTimeout();
            isFirstTime = false;
        }

        // �ʒu�ω���Intent�Œʒm����...
        sendBroadcast(new Intent(ACTION));
    }
}
