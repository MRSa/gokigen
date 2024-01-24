package jp.sourceforge.gokigen.diary;

import com.google.android.maps.MapActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class LocationMap extends MapActivity
{
    static public final String LOCATION_FILE       = "jp.sourceforge.gokigen.diary.locationCsv";
    static public final String LOCATION_ISCURRENT  = "jp.sourceforge.gokigen.diary.isCurrent";
    static public final String LOCATION_ICONID     = "jp.sourceforge.gokigen.diary.locIcon";
    static public final String LOCATION_LATITUDE = "jp.sourceforge.gokigen.diary.latitude";
    static public final String LOCATION_LONGITUDE = "jp.sourceforge.gokigen.diary.longitude";
    static public final String LOCATION_MESSAGE = "jp.sourceforge.gokigen.diary.locMsg";

    LocationMapListener  listener = null;   // イベント処理クラス

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        listener = new LocationMapListener((Activity) this);

        ///** 全画面表示にする **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** タイトルを消す **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /** 画面の準備 **/
        setContentView(R.layout.locationmap);

        /** イベントリスナを準備する **/
        listener.prepareListener();
    }

    /**
     *  経路表示するか？
     */
    @Override
    protected boolean isRouteDisplayed()
    {
       return (false);
    }

    /**
     *  メニューの生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu = listener.onCreateOptionsMenu(menu);
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
            // 何もしない
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
            // なにもしない
        }
    }
    
    /**
     *  子画面から応答をもらったときの処理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            // 子画面からもらった情報の応答処理をイベント処理クラスに依頼する
            listener.onActivityResult(requestCode, resultCode, data);
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
        }
    }    

    /**
     *  ダイアログ表示の準備
     * 
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	return (listener.onCreateDialog(id));
    }
    
    /**
     *   終了(削除)時に呼ばれる処理
     */
    @Override
    protected void onDestroy()
    {
        listener.finishListener();
        super.onDestroy();        
    }
    
}
