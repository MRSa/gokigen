package jp.sourceforge.gokigen.mr999ctl;

import jp.sourceforge.gokigen.mr999ctl.MainScreenListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.Menu;

/**
 *  MR-999CTL : MR-999 (ロボットアーム) 制御アプリ（メインクラス）
 * @author MRSa
 *
 */
public class MR999mainctl extends Activity
{
    MainScreenListener listener = null;  // イベント処理クラス
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        /**  イベントを処理するクラスを生成する   **/
        listener = new MainScreenListener((Activity) this, new PreferenceHolder(this));

        /** 全画面表示にする **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /** タイトルを消す **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        /** 画面表示のレイアウトを生成する **/
        setContentView(R.layout.main);

        /** イベントリスナを準備する */
        listener.prepareListener();
    
    }

    /**
     *  メニューについて、今回は何もしない
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return (false);
    }

    /**
     *   メニューについて、今回は何もしない
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	return (false);
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
}
