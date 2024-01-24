package jp.sourceforge.gokigen.capture;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

public class MainListener implements OnClickListener, OnTouchListener, OnKeyListener
{
    public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);

    private SensorListener sensorHandler = null; // センサイベント処理クラス
    private SensorWrapper  sensors       = null;  // センサラッパー

    private Activity parent = null;  // 親分
    private MainUpdater updater = null;
		
    /**
     *  コンストラクタ
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
        updater = new MainUpdater(argument);
    
        sensorHandler = new SensorListener(argument, Sensor.TYPE_ORIENTATION);
        sensors  = new SensorWrapper(argument, sensorHandler);
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {   
        /** センサの監視準備 **/        
        sensors.prepareSensor();

        /** センサ情報を描画クラスとつなぐ **/
        try
        {
        	final GokigenSurfaceView glSurfaceView  = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
            glSurfaceView.setOrientationHolder(sensorHandler);
        	glSurfaceView.prepareAdditionalSequence(parent);

        	/* 画像(GLの方)を押したらキャプチャ... */
        	glSurfaceView.setOnKeyListener(this);

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
        // センサの監視開始
    	sensors.startWatch();    	
    }

    /**
     *  終了準備
     */
    public void shutdown()
    {
    	// センサの監視停止
    	sensors.finishWatch();	
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
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
        }
        return (false);
    }

    /**
     *  キーを押す
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
		int action = event.getAction();
		if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
		{
		}    	
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
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.capture.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        	 updater.showMessage("ERROR", MainUpdater.SHOWMETHOD_DONTCARE);
        }
    }
}
