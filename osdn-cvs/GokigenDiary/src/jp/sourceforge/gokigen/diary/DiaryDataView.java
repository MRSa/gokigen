package jp.sourceforge.gokigen.diary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 *  データビュークラス！
 * 
 * @author MRSa
 *
 */
public class DiaryDataView extends Activity
{

    private DiaryDataViewListener listener = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** リスナクラスを生成 **/
        listener = new DiaryDataViewListener((Activity) this);

        ///** 全画面表示にする **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** タイトルを消す **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /** 画面の準備 **/
        setContentView(R.layout.diarydataview);

        /** リスナクラスの準備 **/
        listener.prepareListener();

        /** クラスの準備する **/
        listener.prepareOther();
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
     *   メニュー表示前の処理
     * @param menu
     * @return
     */
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	listener.onPrepareOptionsMenu(menu);
        return (super.onPrepareOptionsMenu(menu));
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
     *  アプリ終了時の処理
     * 
     */
    @Override
    protected void onDestroy()
    {
//          Log.v(Main.APP_IDENTIFIER, "DiaryDataView::onDestroy()");
    	listener.finishListener();
        super.onDestroy();
    }

    /**
     * 
     */
    @Override
    protected void onStart()
    {
//        Log.v(Main.APP_IDENTIFIER, "DiaryDataView::onStart()");
        super.onStart();
    }

    /**
     * 
     */
    @Override
    protected void onStop()
    {
//        Log.v(Main.APP_IDENTIFIER, "DiaryDataView::onStop()");
        super.onStop();
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

    /**
     *  ダイアログ表示の準備
     * 
     */
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
