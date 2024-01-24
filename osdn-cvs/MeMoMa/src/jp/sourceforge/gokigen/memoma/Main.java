package jp.sourceforge.gokigen.memoma;

//import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

/**
 *   メイン画面の処理
 * 
 * @author MRSa
 *
 */
public class Main extends  Activity
{
    public static final String APP_IDENTIFIER = "Gokigen";
    public static final String APP_BASEDIR = "/MeMoMa";
	public static final String APP_NAMESPACE = "gokigen";

    private MeMoMaListener listener = null;  // イベント処理クラス
    private MeMoMaDataInOutManager backupManager = null;  // バックアップ・リストア処理

    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
          super.onCreate(savedInstanceState);

          /** データ保存・展開クラス を生成する **/
          backupManager = new MeMoMaDataInOutManager((Activity) this);
          
          /** リスナクラスを生成する **/
          listener = new MeMoMaListener((Activity) this, backupManager);

          /** タイトルにプログレスバーを出せるようにする **/
         requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

          /** タイトルバーにアクションバーを出す **/
//       requestWindowFeature(Window.FEATURE_ACTION_BAR);
         requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

          /** レイアウトを設定する **/
          setContentView(R.layout.main);
           
          /** リスナクラスの準備 **/       
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
     *   終了時の処理
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
     * 
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
            super.onSaveInstanceState(outState);
            if (listener != null)
            {
                // ここでActivityの情報を覚える
            	listener.onSaveInstanceState(outState);
            }
    }

    /**
     * 
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        if (listener != null)
        {
            // ここでActivityの情報を展開する
        	listener.onRestoreInstanceState(savedInstanceState);
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
        }
    }    
}
