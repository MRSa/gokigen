package jp.sourceforge.gokigen.taimen;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 *   メモま！ のメイン画面処理
 *   
 * @author MRSa
 */
public class MainListener implements OnClickListener, OnTouchListener, OnKeyListener
{
	    public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);    // 設定画面の表示
	    public static final int MENU_ID_ABOUT_GOKIGEN = (Menu.FIRST + 2);  // アプリケーションの情報表示

	    private Activity parent = null;  // 親分
	    private TextEditDialog editTextDialog = null;   // テキスト編集用ダイアログ
	    private SelectFeatureListener featureListener = null;  // 機能選択用のリスナ
	    private TaimenCanvasDrawer canvasDrawer = null; // 画像の表示
	    private TaimenMultiTouchCanvasDrawer multiDrawer = null;  // 画像の表示
			
	    /**
	     *  コンストラクタ
	     * @param argument
	     */
	    public MainListener(Activity argument)
	    {
	        parent = argument;
	        editTextDialog = new TextEditDialog(parent);
	        featureListener = new SelectFeatureListener(parent);
	        canvasDrawer = new TaimenCanvasDrawer();
	        multiDrawer = new TaimenMultiTouchCanvasDrawer();
	    }

	    /**
	     *  がっつりこのクラスにイベントリスナを接続する
	     * 
	     */
	    public void prepareListener()
	    {
	    	// 機能選択ボタン
	        final ImageButton selectButton = (ImageButton) parent.findViewById(R.id.SelectButton);
	        selectButton.setOnClickListener(featureListener);

            // 情報テキスト
	        final TextView infoText = (TextView) parent.findViewById(R.id.MeMoMaInfo);
	        infoText.setOnClickListener(this);
	        
	        // 画面描画クラス
	        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
	        surfaceView.setCanvasDrawer(canvasDrawer);
	        surfaceView.setOnTouchListener(this);
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
	        // 画面描画クラス
	        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
	        surfaceView.setCanvasDrawer(canvasDrawer);

	    	//  どういう表示するかを取得する
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
	    	boolean useMultiTouch = preferences.getBoolean("useMultiTouchAPI", false);
	    	if (useMultiTouch == true)
	    	{
		        surfaceView.setCanvasDrawer(multiDrawer);	    		
	    	}
	    	else
	    	{
		        surfaceView.setCanvasDrawer(canvasDrawer);	    		
	    	}
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
	    	//Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onClick() ");
	         if (id == R.id.MeMoMaInfo)
	         {
	        	 // テキスト編集ダイアログを表示する
                 showInfoMessageEditDialog();
	         }
	    }

	    /**
	     *   触られたときの処理
	     * 
	     */
	    public boolean onTouch(View v, MotionEvent event)
	    {
	        int id = v.getId();
	        // int action = event.getAction();

	        //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onTouch() ");

	        if (id == R.id.GraphicView)
	        {
	        	// 画面をタッチした！
	            ((GokigenSurfaceView) v).onTouchEvent(event);
	            return (true);
	        }
	        
	        return (false);
	    }

	    /**
	     *  キーを押したときの操作
	     */
	    public boolean onKey(View v, int keyCode, KeyEvent event)
	    {
	        int action = event.getAction();
	        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
	        {
	        	//
	        }

	        Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onKey() ");
	        return (false);
	    }

	    /**
	     *   メニューへのアイテム追加
	     * @param menu
	     * @return
	     */
	    public Menu onCreateOptionsMenu(Menu menu)
	    {
	    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
	    	menuItem.setIcon(android.R.drawable.ic_menu_preferences);
	        menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT_GOKIGEN, Menu.NONE, parent.getString(R.string.about_gokigen));
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
	    	menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
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
	    	    showPreference();
	    		result = true;
	    		break;

	    	  case MENU_ID_ABOUT_GOKIGEN:
	    		  showAboutGokigen();
		    		result = true;
		    		break;

	    	  default:
	    		result = false;
	    		break;
	    	}
	    	return (result);
	    }

	    /**
	     *   アプリの情報を表示する
	     * 
	     */
	    private void showAboutGokigen()
	    {
	        // アプリの情報(クレジット)を表示する！
	    	parent.showDialog(R.id.info_about_gokigen);
	    }    

	    /**
	     *    メッセージ編集ダイアログを表示する
	     * 
	     */
	    private void showInfoMessageEditDialog()
	    {
	    	parent.showDialog(R.id.editTextArea);        	    	
	    }

	    /**
	     *    メッセージ編集ダイアログの表示を準備する
	     * 
	     */
	    private void prepareInfoMessageEditDialog(Dialog dialog)
	    {
       		TextView txtArea = (TextView) parent.findViewById(R.id.MeMoMaInfo);
         	editTextDialog.prepare((TextEditDialog.ITextEditResultReceiver) new TextEditReceiver(parent, "MeMoMaInfo", R.id.MeMoMaInfo), 0, "", (String) txtArea.getText());
	    }
	    
	    /**
	     *  設定画面を表示する処理
	     */
	    private void showPreference()
	    {
	        try
	        {
	            // 設定画面を呼び出す
	            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.taimen.Preference.class);
	            parent.startActivityForResult(prefIntent, 0);
	        }
	        catch (Exception e)
	        {
	             // 例外発生...なにもしない。
	        	 //updater.showMessage("ERROR", MainUpdater.SHOWMETHOD_DONTCARE);
	        }
	    }

	    /**
	     *  ダイアログの生成
	     * 
	     */
	    public Dialog onCreateDialog(int id)
	    {
            if (id == R.id.info_about_gokigen)
    	    {
            	// クレジットダイアログを表示
    		    CreditDialog dialog = new CreditDialog(parent);
    		    return (dialog.getDialog());
    	    }
            if (id == R.id.editTextArea)
            {
        		// 変更するテキストを表示
                return (editTextDialog.getDialog());
            }
	    	return (null);
	    }

	    /**
	     *  ダイアログ表示の準備
	     * 
	     */
	    public void onPrepareDialog(int id, Dialog dialog)
	    {
            if (id == R.id.editTextArea)
            {
            	// 変更するデータを表示する
            	prepareInfoMessageEditDialog(dialog);
            	return;
            }    	
	    }
}
