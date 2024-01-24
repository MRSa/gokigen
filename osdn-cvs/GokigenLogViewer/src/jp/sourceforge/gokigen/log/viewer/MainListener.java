package jp.sourceforge.gokigen.log.viewer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 *  画面操作をディスパッチするクラス
 *  (Activityと分けるべきかどうか...自分は分けるほうをとっているが...
 * 
 * @author MRSa
 *
 */
public class MainListener implements OnClickListener, OnTouchListener
{
    public final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);
    public final int MENU_ID_ABOUT      = (Menu.FIRST + 3);
    public final int MENU_ID_EXPORT     = (Menu.FIRST + 4);
    public final int MENU_ID_TOP        = (Menu.FIRST + 5);
    public final int MENU_ID_BOTTOM     = (Menu.FIRST + 6);
    public final int MENU_ID_CLEAR      = (Menu.FIRST + 7);

    private Activity parent = null;  // 親分
	private LogViewUpdater viewUpdater = null;  // ログを表示するクラス
	private LogExporter    logExporter = null;  // ログを書き出すクラス
	private LogClearUpdater clearUpdater = null;  // ログをクリアするクラス

	
	/**
     *  コンストラクタ
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
    	// 
    	viewUpdater = new LogViewUpdater(parent);
    	logExporter = new LogExporter(parent);
    	clearUpdater = new LogClearUpdater(parent);

        // 更新ボタンとのリンク
        final Button refreshButton = (Button) parent.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);    	
    }

    /**
     *  終了準備
     */
    public void finishListener()
    {

    }

    /**
     *  スタート準備
     */
    public void prepareToStart()
    {
    	// ログ画面の更新
    	refreshLogView();    
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
        int id = v.getId();
        if (id == R.id.refreshButton)
        {
            // 更新ボタンが押された！
        	refreshLogView();
        }
    }

    /**
     *  表示するログを更新する
     * 
     */
    private void refreshLogView()
    {
        viewUpdater.refreshLogData();    	
    }
    
    /**
     *   触られたときの処理
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
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
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_EXPORT, Menu.NONE, parent.getString(R.string.save));
    	menuItem.setIcon(android.R.drawable.ic_menu_save);

    	menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
    	menuItem.setIcon(android.R.drawable.ic_menu_preferences);

    	menuItem = menu.add(Menu.NONE, MENU_ID_CLEAR, Menu.NONE, parent.getString(R.string.clear));
    	menuItem.setIcon(android.R.drawable.ic_menu_delete);

    	menuItem = menu.add(Menu.NONE, MENU_ID_TOP, Menu.NONE, parent.getString(R.string.top));
    	menuItem.setIcon(R.drawable.ic_menu_back);

    	menuItem = menu.add(Menu.NONE, MENU_ID_BOTTOM, Menu.NONE, parent.getString(R.string.bottom));
    	menuItem.setIcon(R.drawable.ic_menu_forward);

    	menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, parent.getString(R.string.about));
    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);

    	return (menu);
    }
    
    /**
     *   メニュー表示前の処理
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
    	menu.findItem(MENU_ID_EXPORT).setVisible(true);
    	menu.findItem(MENU_ID_TOP).setVisible(true);
    	menu.findItem(MENU_ID_BOTTOM).setVisible(true);
    	menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
    	menu.findItem(MENU_ID_CLEAR).setVisible(true);
    	menu.findItem(MENU_ID_ABOUT).setVisible(true);
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
    	  case MENU_ID_PREFERENCES:
    		// 設定項目の設定
    	    showPreference();
    		result = true;
    		break;

    	  case MENU_ID_EXPORT:
            // 表示中データのファイル出力
    		logExporter.exportLogData(null);
      		result = true;
      		break;

    	  case MENU_ID_ABOUT:
    		// アプリのクレジット表示
    	    parent.showDialog(R.id.info_about_gokigen);
      		result = true;
      		break;

    	  case MENU_ID_TOP:
    		// ログの先頭に移動する
    		moveToTop();
    		result = true;
    		break;
      		
    	  case MENU_ID_BOTTOM:
    		// ログの末尾に移動する
    		moveToBottom();
      		result = true;
      		break;	

    	  case MENU_ID_CLEAR:
    		// ログをクリアする
    		clearLog();
    		result = true;
    		break;

    	  default:
    		result = false;
    		break;
    	}
    	return (result);
    }

    /**
     *  設定画面を表示する処理
     */
    private void showPreference()
    {
        try
        {
            // 設定画面を呼び出す
            Intent prefIntent = new Intent(parent, jp.sourceforge.gokigen.log.viewer.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }
    }

    /**
     *  ログの先頭へ表示を移動させる
     * 
     */
    private void moveToTop()
    {
        ListView listView = (ListView) parent.findViewById(R.id.messageListView);
	    listView.setSelection(0);
    }

    /**
     *  ログの末尾へ表示を移動させる
     */
    private void moveToBottom()
    {
        ListView listView = (ListView) parent.findViewById(R.id.messageListView);
	    listView.setSelection(listView.getCount() - 1);    	
    }

    /** 
     * ログをクリアする
     * 
     *
     */
    private void clearLog()
    {
    	clearUpdater.clearLogData();
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
    	if (id == R.id.detail_dialog)
    	{
    		// データの詳細を取得
    		DetailDialog dialog = new DetailDialog(parent);
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
    	if (id == R.id.detail_dialog)
    	{
    		// データの詳細を取得
        	EditText view = (EditText) dialog.findViewById(R.id.detailmessage);
        	if (view != null)
        	{
                view.setText(viewUpdater.getDetailData());
            }
    	}
    }
}
