package jp.sourceforge.gokigen.diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 *  ごきげんグラフの表示画面
 * @author MRSa
 *
 */
public class GokigenGraph extends Activity
{
	static public final String TARGET_YEAR = "jp.sourceforge.gokigen.diary.GraphYear";
	static public final String TARGET_MONTH = "jp.sourceforge.gokigen.diary.GraphMonthl";
	static public final String TARGET_DAY = "jp.sourceforge.gokigen.diary.GraphDay";

	GokigenGraphListener listener = null;
	
	/**
	 *  生成時処理
	 * 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** リスナクラスを生成 **/
        listener = new GokigenGraphListener((Activity) this);

        ///** 全画面表示にする **/
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** タイトルを消す **/       
        //requestWindowFeature(Window.FEATURE_NO_TITLE);


        /** 画面の準備 **/
        setContentView(R.layout.gokigengraph);

        /** リスナクラスの準備 **/
        listener.prepareExtraDatas(getIntent());
        listener.prepareListener();
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
     *  終了時処理
     */
    @Override
    protected void onDestroy()
    {
        listener.finishListener();
        super.onDestroy();
    }

    /**
     *  開始処理
     */
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    /**
     *  停止処理
     */
    @Override
    protected void onStop()
    {
        super.onStop();
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
}
