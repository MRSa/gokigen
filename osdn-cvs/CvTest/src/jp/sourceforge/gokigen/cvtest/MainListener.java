package jp.sourceforge.gokigen.cvtest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

/**
 *    リスナクラス
 * 
 * @author MRSa
 *
 */
public class MainListener implements OnClickListener, OnTouchListener, QSteerControlDrawer.IRedrawer
{
    public final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);
    public final int MENU_ID_ABOUT = (Menu.FIRST + 2);
    public final int MENU_ID_TEST = (Menu.FIRST + 3);

    private Activity parent = null;  // 親分
    private QSteerControlDrawer canvasDrawer = null;
    private CaptureOverlayDrawer overlayDrawer = null;
    private ImageProcessor imageProcessor = null;
 		
    /**
     *  コンストラクタ
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
        imageProcessor = new ImageProcessor(this);
        canvasDrawer = new QSteerControlDrawer(argument, this, imageProcessor);
        overlayDrawer = new CaptureOverlayDrawer(argument, imageProcessor);
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
        // 「実行中」の表示を消す
    	parent.setProgressBarIndeterminateVisibility(false);
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
    	// 条件に合わせて、描画クラスを変更する
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        surfaceView.setCanvasDrawer(canvasDrawer);
        surfaceView.setTranslucent();

        // カメラ画像の上に重ねあわせて表示する描画クラスを設定する
        final GokigenSurfaceView overlayView = (GokigenSurfaceView) parent.findViewById(R.id.OverlayView);
        overlayView.setCanvasDrawer(overlayDrawer);
        overlayView.setTranslucent();

        // カメラ画像受信時の処理クラスを設定
        final CameraViewer cameraView = (CameraViewer) parent.findViewById(R.id.CameraView);
        cameraView.setPreviewCallback(imageProcessor);
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
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
    	//menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
    	menuItem.setIcon(android.R.drawable.ic_menu_preferences);

    	menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, parent.getString(R.string.about_gokigen));
    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
    	//menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);

    	menuItem = menu.add(Menu.NONE, MENU_ID_TEST, Menu.NONE, parent.getString(R.string.app_name));
    	//menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
    	menuItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);

    	return (menu);
    }
    
    /**
     *   メニュー表示前の処理
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
    	menu.findItem(MENU_ID_PREFERENCES).setVisible(false);
    	menu.findItem(MENU_ID_ABOUT).setVisible(true);
    	menu.findItem(MENU_ID_TEST).setVisible(false);
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
    	  case MENU_ID_ABOUT:
    		showAboutGokigen();
    		result = true;
    		break;
    	  case MENU_ID_TEST:
      		showTestScreen();
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
     *  テスト用画面を表示する処理
     */
    private void showTestScreen()
    {
        try
        {
            // 別の画面を呼び出す
            //Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.qsteer.drive.Sample1Java.class);
            //parent.startActivityForResult(prefIntent, 0);
        	Log.v(GokigenSymbols.APP_IDENTIFIER, "called showTestScreen() ");
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
            Log.v(GokigenSymbols.APP_IDENTIFIER, "showTestScreen() : " + e.toString());
        }
    }

    /**
     *  設定画面を表示する処理
     */
    private void showPreference()
    {
        try
        {
            // 設定画面を呼び出す
            Intent prefIntent = new Intent(parent, jp.sourceforge.gokigen.cvtest.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }
    }

    public void onSaveInstanceState(Bundle outState)
    {
	    /* ここで状態を保存 */ 
	    //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onSaveInstanceState()");
    }
    
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	/* ここで状態を復元 */
	    //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onRestoreInstanceState()");
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
    	return (null);
    }

    /**
     *  ダイアログ表示の準備
     * 
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
    	
    }

    /**
     *   画面を再描画する。
     * 
     */
    public void redraw()
    {
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        surfaceView.doDraw();
        
        final GokigenSurfaceView overlayView = (GokigenSurfaceView) parent.findViewById(R.id.OverlayView);
        overlayView.doDraw();
    }

    /**
     *    USBアクセサリが有効・無効になったときに呼び出される。
     * 
     * @param isEnable
     */
    public void enableControls(boolean isEnable)
    {
    	if (isEnable == true)
    	{
    	    showControls();    		
    	}
    	else
    	{
            parent.setContentView(R.layout.no_device);
    	}    	
    }

    private void showControls()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        boolean isSingleMode = preferences.getBoolean("SingleControl", false);
        if (isSingleMode == true)
        {
        	// シングル操作モード
        }
        else
        {
        	// デュアル操作モード
        }
        parent.setContentView(R.layout.main);
    }
    
    /**
     *    USBアクセサリからメッセージを受信した時の処理 
     * 
     * @param msg
     */
    public void receivedMessage(Message msg)
    {
    	
    }
}
