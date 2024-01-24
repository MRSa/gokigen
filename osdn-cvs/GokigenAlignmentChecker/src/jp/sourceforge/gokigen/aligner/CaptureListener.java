package jp.sourceforge.gokigen.aligner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class CaptureListener implements OnClickListener, OnTouchListener
{
    public static final int MENU_ID_ABOUT = (Menu.FIRST + 2);

    private Activity parent = null;  // 親分
    private GokigenGraphListener graphListener = null;

    /**
     *  コンストラクタ
     * @param argument
     */
    public CaptureListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
        try
        {
            /** 描画クラスの準備 **/
        	graphListener = new GokigenGraphListener(parent);
        	graphListener.prepareListener();
        }
        catch (Exception ex)
        {
            // ダミーのガベコレ
            System.gc();
        }
    }

    /**
     *  スタート準備
     */
    public void prepareToStart()
    {
    	graphListener.prepareListener();
    }

    /**
     *  終了準備
     */
    public void shutdown()
    {

    }
    
    /**
     *  他画面から戻ってきたとき...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    /**
     *   クリックされたときの処理
     */
    public void onClick(View v)
    {
        // int id = v.getId();
    }

    /**
     *   キーが押されたときの処理
     * 
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return (graphListener.onKeyDown(keyCode, event));
    }

    /**
     *   触られたときの処理
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        // int action = event.getAction();
    	//Log.v(Main.APP_IDENTIFIER, "CaptureListener::onTouchEvent() :" + event.getAction());

        return (false);
    }
    /**
     *   触られたときの処理
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
    	//Log.v(Main.APP_IDENTIFIER, "onTouch() :" + event.getAction());

    	// int id = v.getId();
        // int action = event.getAction();

        return (false);
    }

    /**
     *   メニューへのアイテム追加
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {
        return (null);
/*
    	MenuItem menuItem;

        // クレジットの表示
        menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, parent.getString(R.string.about));
    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);

        return (menu);
*/
    }
    
    /**
     *   メニュー表示前の処理
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
        //menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
    	//menu.findItem(MENU_ID_ABOUT).setVisible(true);
        return;
    }

    /**
     *   メニューのアイテムが選択されたときの処理
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = false;
        switch (item.getItemId())
        {
         case MENU_ID_ABOUT:
      		// アプリのクレジット表示
      	    parent.showDialog(R.id.info_about_gokigen);
        	result = true;
        	break;

          default:
            result = false;
            break;
        }
        return (result);
    }
    
    /**
     *  ダイアログの生成
     * 
     */
    public Dialog onCreateDialog(int id)
    {
    	if (id == R.id.info_about_gokigen)
    	{
    		CreditDialog dialog = new CreditDialog(parent);
    		return (dialog.getDialog());
    	}
   	    return (null);
    }

    /**
     *  ダイアログ表示の準備
     * 
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {

    }
}
