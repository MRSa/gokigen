package jp.sourceforge.gokigen.psbf;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 *    紙相撲キットのメイン部分
 * 
 * @author MRSa
 *
 */
public class PSBFMain extends  PSBFBaseActivity
{
    public static final String APP_IDENTIFIER = "Gokigen";
    public static final String APP_BASEDIR = "/PSBF";

    private MainListener listener = null;  // イベント処理クラス

    private OutputController mOutputController = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** リスナクラスの生成 **/
        listener = new MainListener(this);
        
        /** レイアウトの反映...これはベースクラスでやる **/
       // setContentView(R.layout.main);

        /** リスナクラスの準備 **/       
        listener.prepareListener();        
    }

    /**
     * 
     * 
     */
    @Override
    protected void hideControls()
    {
        super.hideControls();
        mOutputController = null;
    }

    /**
     * 
     * 
     */
    protected void showControls()
    {
        super.showControls();

        mOutputController = new OutputController(this, mInputController);
        mOutputController.accessoryAttached();
        
        listener.prepareControlListeners(mInputController);
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
        boolean ret = listener.onOptionsItemSelected(item);
        if (ret == false)
        {
            ret = super.onOptionsItemSelected(item);
        }
        return (ret);
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
    public void onDestroy()
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
