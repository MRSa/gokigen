package jp.sourceforge.gokigen.diary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class DiaryInput extends Activity implements ILocationReceiver
{
    static public final String ENTRY_RATING = "jp.sourceforge.gokigen.diary.entryRating";
    static public final String ENTRY_DIARY = "jp.sourceforge.gokigen.diary.entryDiary";
    
    static public final String INPUT_INFORMATION = "jp.sourceforge.gokigen.diary.information";
    static public final String CURRENT_LATITUDE  = "jp.sourceforge.gokigen.diary.latitude";
    static public final String CURRENT_LONGITUDE  = "jp.sourceforge.gokigen.diary.longitude";

    static public final int RESULT_DATA_WRITE_FAILURE  = Activity.RESULT_FIRST_USER + 1;
    static public final int RESULT_DATA_WRITE_SUCCESS  = Activity.RESULT_FIRST_USER + 2;

    static public final int REQUEST_CAMERA = Activity.RESULT_FIRST_USER + 1000;

    static public final int DIALOG_PICKUP_EMOTIONSYMBOL_ID = 0;
    static public final int DIALOG_CLOSE_CONFIRMATION_ID = 1;

    private LocationManager  locationService = null;
    private DiaryInputListener   listener = null;     // �C�x���g�����N���X

    private LocationListenerImpl locationReceiver = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** ���X�i�N���X�𐶐� **/
        listener = new DiaryInputListener((Activity) this);

        ///** �S��ʕ\���ɂ��� **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** �^�C�g�������� **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        /** ��ʂ̏��� **/
        setContentView(R.layout.diaryinput);

        /** ���X�i�N���X�̏��� **/       
        listener.prepareListener();

        /** �ʒu���T�[�r�X�̏��� **/
        prepareLocationService();
    }

    /**
     *  ���j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        listener.onCreateOptionsMenu(menu);
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *  ���j���[�A�C�e���̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return (listener.onOptionsItemSelected(item));
    }

    /**
     *  ���j���[�\���O�̏���
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        listener.onPrepareOptionsMenu(menu);
        return (super.onPrepareOptionsMenu(menu));
    }

    /**
     *  ��ʂ����ɉ�����Ƃ��̏���
     */
    @Override
    public void onPause()
    {
        super.onPause();

        try
        {
            // ������~�߂�悤�C�x���g�����N���X�Ɏw������
            listener.shutdown();            
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }
    }

    /**
     *  ��ʂ��\�ɏo�Ă����Ƃ��̏���
     */
    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            // ���쏀������悤�C�x���g�����N���X�Ɏw������
            listener.prepareToStart();
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }
    }

    /**
     * 
     * 
     */
    @Override
    protected void onDestroy()
    {
        listener.finishListener();
        finishLocationService();
        super.onDestroy();
    }

    /**
     * 
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        
        // �ʒu���T�[�r�X�𓮂����͂��߂�
        startLocationService();
    }

    /**
     * 
     */
    @Override
    protected void onStop()
    {
        super.onStop();

        // �ʒu���T�[�r�X���Ƃ߂�
        stopLocationService();
    }

    /**
     *  �_�C�A���O�̐���
     * 
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	return (listener.onCreateDialog(id));
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
    	listener.onPrepareDialog(id, dialog);
    	return;
    }

    /**
     *  �q��ʂ��牞������������Ƃ��̏���
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            // �q��ʂ������������̉����������C�x���g�����N���X�Ɉ˗�����
            listener.onActivityResult(requestCode, resultCode, data);
        }
        catch (Exception ex)
        {
            // ��O�����������Ƃ��ɂ́A�������Ȃ��B
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }   
    }
     
    /**
     * 
     */
    private void prepareLocationService()
    {
        try
        {
            /** �f�B���N�g���̏��� **/        
            locationService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationReceiver = new LocationListenerImpl(this);
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "Ex :" + ex.toString() + " " + ex.getMessage());
            locationService = null;
            locationReceiver = null;
            System.gc();
        }
       
    }

    /**
     * 
     */
    private void finishLocationService()
    {
        locationService = null;
        locationReceiver = null;
        System.gc();
    }

    /**
     * 
     */
    private void startLocationService()
    {
        Log.v(Main.APP_IDENTIFIER, "DiaryInput::startLocationService()");
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean useNetworkGps = preferences.getBoolean("useNetworkGps", false);
            long timeoutValue  = 60 * 1000; // Long.parseLong(preferences.getString("timeoutDuration", "7")) * 60 * 1000;
            // �ʒu�����l�b�g���[�N�o�R�ł��擾����悤�ɕύX����
            locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeoutValue, 0, locationReceiver);
            if (useNetworkGps == true)
            {
                locationService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, timeoutValue, 0, locationReceiver);
            }
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "Ex :" + ex.toString() + " " + ex.getMessage());
            locationService = null;
        }
    }
    
    /**
     * 
     */
    private void stopLocationService()
    {
        Log.v(Main.APP_IDENTIFIER, "DiaryInput::stopLocationService()");
        try
        {
            if (locationService != null)
            {
                locationService.removeUpdates(locationReceiver);
            }
        }
        catch (Exception ex)
        {
            
        }
    }
    
    /**
     *  �ʒu��񂪍X�V���ꂽ�I
     *  
     *  LocationListener::onLocationChanged()
     *  
     */
    public void onLocationChanged(Location location)
    {
        //
    }
}
