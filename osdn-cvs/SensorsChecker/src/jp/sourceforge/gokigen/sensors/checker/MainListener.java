package jp.sourceforge.gokigen.sensors.checker;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import java.io.FileOutputStream;
import android.location.Location;

public class MainListener implements OnClickListener, OnTouchListener, ISensorDataWriter
{
    public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);

    
    private FileOutputStream fileOutputStream         = null;
    private FileOutputStream locationFileOutputStream = null;
    private PreferenceUtility prefUtil = null;
    private long writeTime = 0;
    
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
    
        sensorHandler = new SensorListener(argument);
        sensorHandler.setSensorWriter(this);

        sensors  = new SensorWrapper(argument, sensorHandler);
        
        prefUtil = new PreferenceUtility(argument);
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
        /** センサの監視準備 **/        
        sensors.prepareSensor();
    }

    /**
     *  スタート準備
     */
    public void prepareToStart()
    {
        // センサの監視開始
    	sensors.startWatch(prefUtil);    	

    	// 位置情報を記録するファイルのオープン
    	closeFileStream(locationFileOutputStream);
        locationFileOutputStream = openFileStream(prefUtil.getValueBoolean("isRecordLocation"), 
        		                                  prefUtil.getValueString("locationFileNameValue"),
        		                                  prefUtil.getValueBoolean("isAppendLocation"));

        // センサ情報を記録するファイルのオープン
        closeFileStream(fileOutputStream);       
        fileOutputStream = openFileStream(prefUtil.getValueBoolean("isRecord"),
        		                          prefUtil.getValueString("fileNameValue"),
        		                          prefUtil.getValueBoolean("isAppend"));
    }

    /**
     *   センサ情報をファイル出力する  
     */
	public void writeLocationData(Location location, long duration)
	{
		if (locationFileOutputStream == null)
		{
			return;
		}

		String outData = "";
		try
        {
            // センサ情報を文字列化する
            outData = outData + String.valueOf(location.getTime()) + ", ";      // 時間
            outData = outData + String.valueOf(location.getLatitude()) + ", ";  // 緯度
            outData = outData + String.valueOf(location.getLongitude()) + ", "; // 経度
            outData = outData + String.valueOf(location.getAccuracy()) + ", ";  // 正確性？
            outData = outData + String.valueOf(location.getAltitude()) + ", ";  // 高度
            outData = outData + String.valueOf(location.getSpeed()) + ", ";     // 速度
            outData = outData + String.valueOf(location.getBearing()) + ", ";   // ふるまい？
            outData = outData + String.valueOf(duration) + "\r\n";			    // 前回からの時間
			locationFileOutputStream.write(outData.getBytes());	
        }
        catch (Exception e)
        {
        	
        }
        return;		
	}    
    
    /**
     *   センサ情報をファイル出力する  
     */
	public void writeSensorData(long currentTime, int sensorType, String data)
	{
		String outData = currentTime + "\t" + data + "\r\n";
		if ((fileOutputStream != null)&&(writeTime + 100 <= currentTime))
		{
			try
			{
				if (sensorType == Sensor.TYPE_ACCELEROMETER)
				{
			        fileOutputStream.write(outData.getBytes());
				}
			}
			catch (Exception e)
			{
				//
			}
			writeTime = currentTime;
		}
		return;
	}

    /**
     *  記録ファイルをオープンする	
     * @param isRecord  オープンするかどうか確認
     * @param fileName  ファイル名
     * @return  ファイルストリーム
     */
	private FileOutputStream openFileStream(boolean isRecord, String fileName, boolean isAppend)
	{
        if (isRecord == false)
        {
            return (null);
        }

		try
        {
            if (fileName.startsWith("/sdcard/") == false)
            {
                fileName = "/sdcard/sensor.txt";
            }
            return (new FileOutputStream(fileName, isAppend));
        }
        catch (Exception e)
        {
        	
        }
		return (null);
	}
	
	/**
	 *  記録ファイルをクローズする
	 * @param stream ファイルストリーム
	 */
	private void closeFileStream(FileOutputStream stream)
	{
        try
        {
            if (stream != null)
            {
            	stream.close();
            }
        }
        catch (Exception ex)
        {
        	
        }
	
	}
	
	/**
     *  終了準備
     */
    public void shutdown()
    {
    	closeFileStream(locationFileOutputStream);
        locationFileOutputStream = null;
        
        closeFileStream(fileOutputStream);
        fileOutputStream = null;

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
        // ファイルをオープンするか...?
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
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.sensors.checker.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        	 updater.showMessage("ERROR", MainUpdater.SHOWMETHOD_DONTCARE);
        }
    }
}
