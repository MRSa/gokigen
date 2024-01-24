package jp.sourceforge.gokigen.aligner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity
{
    public static final String APP_IDENTIFIER = "Gokigen";
    public static final String APP_BASEDIR = "/Shisei";
    public static final String APP_EXAMINE_FILENAME = "fileNameToCheck";

    private MainListener   listener = null;     // イベント処理クラス
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** リスナクラスを生成 **/
        listener = new MainListener((Activity) this);

        /** 全画面表示にする **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /** タイトルも消す **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /** 画面の準備 **/
        setContentView(R.layout.main);

        /** イベントリスナを準備する **/
        listener.prepareListener();
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
     *  キーが押されたときの処理
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        try
        {
        	// キーが押されたことを通知する
        	if (listener.onKeyDown(keyCode, event) == true)
        	{
        		return (true);
        	}
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
        }
        return (super.onKeyDown(keyCode, event));
    }

    /**
     *  画面がタッチされたときの処理
     * 
     */
    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        try
        {
        	// 触られたことを通知する
        	if (listener.onTouchEvent(event) == true)
        	{
        		return (true);
        	}
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
        }
    	return (super.onTouchEvent(event));
    }

    /**
     *  画面がタッチされたときの処理
     * 
     */
    @Override
    public boolean onTrackballEvent (MotionEvent event)
    {
    	//Log.v(Main.APP_IDENTIFIER, "Main::onTrackballEvent()");
        try
        {
        	// 触られたことを通知する
        	if (listener.onTrackballEvent(event) == true)
        	{
        		return (true);
        	}
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
        }
    	return (super.onTouchEvent(event));
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
     *  ダイアログ表示の準備
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
    	listener.onPrepareDialog(id, dialog);
    	return;
    }

}
