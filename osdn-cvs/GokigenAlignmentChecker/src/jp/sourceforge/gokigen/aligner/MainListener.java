package jp.sourceforge.gokigen.aligner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MainListener  implements OnClickListener, OnTouchListener, ICanvasDrawer
{
    public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);
    public static final int MENU_ID_ABOUT = (Menu.FIRST + 2);
    public static final int MENU_ID_CAPTURE = (Menu.FIRST + 3);
    public static final int MENU_ID_UNDO = (Menu.FIRST + 4);
    public static final int MENU_ID_RESET = (Menu.FIRST + 5);
    public static final int MENU_ID_CHECKIN =  (Menu.FIRST + 6);

    private Activity parent = null;  // 親分
    private ImageAdjuster imageSetter = null;
    private IBodyDrawer bodyDrawer = null;

    /**
     *  コンストラクタ
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
        imageSetter = new ImageAdjuster(parent);
        imageSetter.setIsClearBitmap(false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        String shapeType = preferences.getString("showShapeType", "0");
        int reportType = 0;
        try
        {
        	reportType = Integer.parseInt(shapeType);
        }
        catch (Exception ex)
        {
        	//
        	Log.v(Main.APP_IDENTIFIER, "cannot get ShapeType.");
        }
        if (reportType == 0)
        {
            bodyDrawer = new GokigenSideBodyDrawer(parent, imageSetter);
        }
        else
        {
            bodyDrawer = new GokigenFrontBodyDrawer(parent, imageSetter);
        }
        bodyDrawer.setMessage("");
    
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
        try
        {

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
    	// プレファレンスから画像ファイルを読み出す
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        String filenameToShow = preferences.getString(Main.APP_EXAMINE_FILENAME, "");
        int minLen = Environment.getExternalStorageDirectory().getPath().length();
        if (filenameToShow.length() <= minLen)
        {
            ////////// 表示する画像ファイルがなかったとき。。。キャプチャ画面を開く //////////
        	showCapture();
        	return;
        }

        // 表示データを画面に表示する        
        final ImageView imgView = (ImageView) parent.findViewById(R.id.PictView);
        imageSetter.setImage(imgView, filenameToShow);

        Log.v(Main.APP_IDENTIFIER, "File name to show : " + filenameToShow);
        
        // 体の形を表示する
        prepareBodyShape();
        return;	 
    }

    /**
     *  イメージデータを表示後に呼び出される処理
     * 
     */
    private void prepareBodyShape()
    {
        // グラフ(スケール)部分の領域
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.InfoView);
        view.setCanvasDrawer(this);
        view.setOnClickListener(this);
        view.setOnTouchListener(this);
        view.setPixelFormat(PixelFormat.TRANSLUCENT);
        view.bringToFront();
        view.doDraw();
        view.setPixelFormat(PixelFormat.TRANSLUCENT);
    }

    /**
     *  終了準備
     */
    public void shutdown()
    {

    }

    /**
     *  キャンバスにデータを描画する
     * 
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	//Log.v(Main.APP_IDENTIFIER, "MainListener::drawOnCanvas()");
    	try
    	{
    		// 再描画を実行
            bodyDrawer.drawOnCanvas(canvas, 0);
    	}
    	catch (Exception ex)
    	{
    		// 例外発生...でもそのときには何もしない
    		Log.v(Main.APP_IDENTIFIER, "drawOnCanvas()" + ex.getMessage());
    	}
    	return;
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
        return (false);
    }

    /**
     *   触られたときの処理
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	boolean ret = bodyDrawer.onTouchEvent(event);
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.InfoView);
        view.doDraw();

        return (ret);
    }
    

    /**
     *  トラックボールが動かされたときの処理
     * 
     * @param event
     * @return
     */
    public boolean onTrackballEvent(MotionEvent event)
    {
    	//Log.v(Main.APP_IDENTIFIER, "onTrackballEvent()...");
    	boolean ret = bodyDrawer.onTrackballEvent(event);
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.InfoView);
        view.doDraw();
    	return (ret);
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
        MenuItem menuItem;

        // アライメントチェックの実行
        menuItem = menu.add(Menu.NONE, MENU_ID_CHECKIN, Menu.NONE, parent.getString(R.string.checkin_name));
        menuItem.setIcon(R.drawable.ic_menu_mark);
        
        // キャプチャ画面の表示
        menuItem = menu.add(Menu.NONE, MENU_ID_CAPTURE, Menu.NONE, parent.getString(R.string.captureScreen_name));
        menuItem.setIcon(android.R.drawable.ic_menu_camera);
        
        // 位置アンドゥ
        menuItem = menu.add(Menu.NONE, MENU_ID_UNDO, Menu.NONE, parent.getString(R.string.undo_name));
        menuItem.setIcon(R.drawable.ic_menu_revert);
        
        // 位置リセット
        menuItem = menu.add(Menu.NONE, MENU_ID_RESET, Menu.NONE, parent.getString(R.string.reset_name));
        menuItem.setIcon(R.drawable.ic_menu_refresh);
        
        // 設定項目の表示
        menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
        menuItem.setIcon(android.R.drawable.ic_menu_preferences);
        // クレジットの表示
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
        menu.findItem(MENU_ID_CHECKIN).setVisible(true);
        menu.findItem(MENU_ID_UNDO).setVisible(true);
        menu.findItem(MENU_ID_CAPTURE).setVisible(true);
        menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
        menu.findItem(MENU_ID_RESET).setVisible(true);
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
          case MENU_ID_CAPTURE:
            // キャプチャ画面の表示
        	showCapture();
            result = true;
            break;

          case MENU_ID_PREFERENCES:
            // 設定項目の表示
        	showPreference();
            result = true;
            break;

          case MENU_ID_ABOUT:
      		// アプリのクレジット表示
      	    parent.showDialog(R.id.info_about_gokigen);
        	result = true;
        	break;

          case MENU_ID_UNDO:
            // 操作のアンドゥ
        	bodyDrawer.undo();
        	refreshView();
            result = true;
            break;

          case MENU_ID_RESET:
            // 操作のリセット
          	bodyDrawer.reset();
        	refreshView();
            break;

          case MENU_ID_CHECKIN:
        	// アライメントのチェックを実行する
        	doCheckAllignment();
        	break;
            
          default:
            result = false;
            break;
        }
        return (result);
    }

    /**
     *  画面を再描画する
     * 
     */
    private void refreshView()
    {
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.InfoView);
        view.doDraw();
    }
    
    /**
     *  キャプチャ画面を表示する処理
     * 
     */
    private void showCapture()
    {
        try
        {
            Intent captureIntent = new Intent(parent,jp.sourceforge.gokigen.aligner.CaptureScreen.class);
            parent.startActivityForResult(captureIntent, R.id.captureInformationArea);
        }
        catch (Exception e)
        {
             // 例外発生...
            Log.v(Main.APP_IDENTIFIER, "Launch Fail(Capture) : " + e.getMessage());
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
            Intent prefIntent = new Intent(parent, jp.sourceforge.gokigen.aligner.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
            Log.v(Main.APP_IDENTIFIER, "Launch Fail(Preference) : " + e.getMessage() + " " + e.toString());
        }
    }

    /**
     *   アライメントのチェックを実行する
     * 
     */
    private void doCheckAllignment()
    {
    	bodyDrawer.storePosition();
        return;
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
