package jp.sourceforge.gokigen.diary;

import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 *  
 * @author MRSa
 *
 */
public class Main extends MapActivity
{
    private MainListener   listener = null;     // イベント処理クラス

    public static final String APP_IDENTIFIER = "Gokigen";
    public static final String APP_BASEDIR = "/Diary";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** リスナクラスを生成 **/
        listener = new MainListener((Activity) this);

        /** 画面の準備 **/
        setContentView(R.layout.main);

        /** リスナクラスの準備 **/       
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
        super.onDestroy();
    }

    /**
     * 
     */
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    /**
     * 
     */
    @Override
    protected void onStop()
    {
        super.onStop();
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
}
