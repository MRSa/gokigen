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
    private DiaryInputListener   listener = null;     // イベント処理クラス

    private LocationListenerImpl locationReceiver = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** リスナクラスを生成 **/
        listener = new DiaryInputListener((Activity) this);

        ///** 全画面表示にする **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** タイトルを消す **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        /** 画面の準備 **/
        setContentView(R.layout.diaryinput);

        /** リスナクラスの準備 **/       
        listener.prepareListener();

        /** 位置情報サービスの準備 **/
        prepareLocationService();
    }

    /**
     *  メニューの生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        listener.onCreateOptionsMenu(menu);
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *  メニューアイテムの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return (listener.onOptionsItemSelected(item));
    }

    /**
     *  メニュー表示前の処理
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        listener.onPrepareOptionsMenu(menu);
        return (super.onPrepareOptionsMenu(menu));
    }

    /**
     *  画面が裏に回ったときの処理
     */
    @Override
    public void onPause()
    {
        super.onPause();

        try
        {
            // 動作を止めるようイベント処理クラスに指示する
            listener.shutdown();            
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }
    }

    /**
     *  画面が表に出てきたときの処理
     */
    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            // 動作準備するようイベント処理クラスに指示する
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
        
        // 位置情報サービスを動かしはじめる
        startLocationService();
    }

    /**
     * 
     */
    @Override
    protected void onStop()
    {
        super.onStop();

        // 位置情報サービスをとめる
        stopLocationService();
    }

    /**
     *  ダイアログの生成
     * 
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	return (listener.onCreateDialog(id));
    }

    /**
     *  ダイアログ表示の準備
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
    	listener.onPrepareDialog(id, dialog);
    	return;
    }

    /**
     *  子画面から応答をもらったときの処理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            // 子画面からもらった情報の応答処理をイベント処理クラスに依頼する
            listener.onActivityResult(requestCode, resultCode, data);
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
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
            /** ディレクトリの準備 **/        
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
            // 位置情報をネットワーク経由でも取得するように変更する
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
     *  位置情報が更新された！
     *  
     *  LocationListener::onLocationChanged()
     *  
     */
    public void onLocationChanged(Location location)
    {
        //
    }
}
