package jp.sourceforge.gokigen.psbf;

import java.io.File;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class MainListener implements OnClickListener, OnTouchListener, SoundVisualizerListener.IDataCaptureListener
{
    private static final int OPEN_PREFERENCE = 0;
	private static final int OPEN_FILE_REQUEST = 100;

	public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);
    public static final int MENU_ID_ABOUT_GOKIGEN = (Menu.FIRST + 2);  // アプリケーションの情報表示
    public static final int MENU_ID_SELECT_FILE = (Menu.FIRST + 3);  // 音楽ファイル(MP3ファイル)の選択
    public static final int MENU_ID_FORCE_DISPLAY = (Menu.FIRST + 4);  // 画面の強制表示
    public static final int MENU_ID_QUIT = (Menu.FIRST + 5); // アプリケーション終了

    private PSBFBaseActivity parent = null;  // 親分
    
    private SoundPlayer player = null;
    private SoundVisualizerListener soundListener = null;
    private boolean motorControlSignal = false;
    private long motorControlCount = 0;

    /**
     *  コンストラクタ
     * @param argument
     */
    public MainListener(PSBFBaseActivity argument)
    {
        parent = argument;
        soundListener = new SoundVisualizerListener(argument);
        player = new SoundPlayer(argument, soundListener);
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
    	
     }

    /**
     *   コントロール用のリスナ設定
     * 
     */
    public void prepareControlListeners(InputController inputController)
    {
        // 画面描画クラスの設定
    	try
    	{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        	int operationMode = Integer.parseInt(preferences.getString("operationMode", "2"));
        	if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
        	{
                // 通常操作モード
                SumoGameController controller = inputController.getGameController();
                if (controller != null)
                {
                	controller.prepare();
                }
        	}
        	else if (operationMode == PSBFBaseActivity.OPERATIONMODE_DEMONSTRATION)
        	{
                // デモンストレーションモード  
                final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
                surfaceView.setCanvasDrawer(soundListener);
                soundListener.setGokigenSurfaceView(surfaceView);
                soundListener.setDataCaptureListener(this);
        	}
        	else  // if (operationMode == PSBFBaseActivity.OPERATIONMODE_MANUAL)
        	{
        		// マニュアル操作モード
        	}
    	}
    	catch (Exception ex)
    	{
    		// ログ表示
    	    Log.v(PSBFMain.APP_IDENTIFIER, "prepareToStart() EX: " + ex.toString());
    	}
    }

    /**
     *  終了準備
     */
    public void finishListener()
    {
    	// 再生中の時には、音を止める。
        if ((player != null)&&(player.isPlaying() == true))
        {
        	player.finish();
        }
    }

    /**
     *  スタート準備
     */
    public void prepareToStart()
    {
    	try
    	{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        	int operationMode = Integer.parseInt(preferences.getString("operationMode", "2"));
        	if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
        	{
                // 通常操作モード ... 後ほど設定する
        	}
        	else if (operationMode == PSBFBaseActivity.OPERATIONMODE_DEMONSTRATION)
        	{
                // デモンストレーションモード  
        		prepareGokigenSurfaceView();
                player.playSound();
        	}
        	else  // if (operationMode == PSBFBaseActivity.OPERATIONMODE_MANUAL)
        	{
        		// マニュアル操作モード
        	}
    	}
    	catch (Exception ex)
    	{
    		// ログ表示
    	    Log.v(PSBFMain.APP_IDENTIFIER, "prepareToStart() EX: " + ex.toString());
    	}
    }
    /**
     *   コントロール用のリスナ設定
     * 
     */
    private void prepareGokigenSurfaceView()
    {
        // 画面描画クラスの設定
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        if (surfaceView != null)
        {
            surfaceView.setCanvasDrawer(soundListener);
            soundListener.setGokigenSurfaceView(surfaceView);
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
        if (requestCode == OPEN_FILE_REQUEST)
        {
        	// 音楽ファイルの選択...
        	Uri selectFile = data.getData();   // AndExporler から取得する。
        	String fileName = selectFile.toString().substring(7);   // "file://" を切り取る。
        	Log.v(PSBFMain.APP_IDENTIFIER, "File Selected : " + fileName);

        	// ファイル名をPreferenceに記録
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("playFileName", fileName);
            editor.commit();

            /**
            // 再生ファイル名を画面表示
            final TextView playFile = (TextView) parent.findViewById(R.id.SoundInfo);
            playFile.setText(fileName);
            **/
        }
        else if (requestCode == OPEN_PREFERENCE)
        {
            // 設定画面から帰ってきた時、
        }

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
    	menuItem = menu.add(Menu.NONE, MENU_ID_SELECT_FILE, Menu.NONE, parent.getString(R.string.selectFile));
    	menuItem.setIcon(android.R.drawable.ic_menu_view);
        menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT_GOKIGEN, Menu.NONE, parent.getString(R.string.about_gokigen));
        menuItem.setIcon(android.R.drawable.ic_menu_info_details);
        menuItem = menu.add(Menu.NONE, MENU_ID_FORCE_DISPLAY, Menu.NONE, parent.getString(R.string.simulate));
        menuItem = menu.add(Menu.NONE, MENU_ID_QUIT, Menu.NONE, parent.getString(R.string.quit));
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
        menu.findItem(MENU_ID_ABOUT_GOKIGEN).setVisible(true);
    	menu.findItem(MENU_ID_SELECT_FILE).setVisible(true);
    	menu.findItem(MENU_ID_FORCE_DISPLAY).setVisible(true);
    	menu.findItem(MENU_ID_QUIT).setVisible(true);
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
            result = false;
            break;

          case MENU_ID_ABOUT_GOKIGEN:
              showAboutGokigen();
                result = true;
                break;

    	  case MENU_ID_SELECT_FILE:
    		  selectMusicFileName();
    		  result = true;
    		  break;
    	  case MENU_ID_FORCE_DISPLAY:
    		  forceDisplay();
    		  result = true;
    		  break;
    	  case MENU_ID_QUIT:
    		  finishApplication();
    		  result = true;
    		  break;
          default:
            result = false;
            break;
        }
        return (result);
    }

    private void finishApplication()
    {
        parent.finish();
        System.exit(0);
    }

    private void forceDisplay()
    {
    	parent.showControls();
    }
    
    
    /**
     *  設定画面を表示する処理
     */
    private void showPreference()
    {
        try
        {
            // 設定画面を呼び出す
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.psbf.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }
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
     *    音声ファイルの選択
     * 
     */
    private void selectMusicFileName()
    {
    	try
    	{
        	Intent intent = new Intent();
        	intent.setAction(Intent.ACTION_PICK);
        	Uri startDir = Uri.fromFile(new File("/sdcard"));
        	intent.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.file");
        	intent.putExtra("browser_filter_extension_whitelist", "*.mp3");
        	intent.putExtra("explorer_title", "Open MP3 File...");
        	intent.putExtra("browser_title_background_color", "440000AA");
        	intent.putExtra("browser_title_foreground_color", "FFFFFFFF");
        	intent.putExtra("browser_list_background_color", "66000000");
        	intent.putExtra("browser_list_fontscale", "120%");
        	intent.putExtra("browser_list_layout", "2"); 
        	parent.startActivityForResult(intent, OPEN_FILE_REQUEST);
    	}
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }
    }

	/** タッチイベントを受信した時の処理 **/
    public boolean onTouchEventReceived(MotionEvent event)
    {
    	return (false);
    }

    /** 音楽データを受信した時の処理 **/
    public void onMusicDataReceived(float current, float max)
    {
    	 // 音楽にあわせて、モータを動かす！
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
    	int volumeRate = Integer.parseInt(preferences.getString("volumeRate", "10"));    	
    	int volumeOffsetA = Integer.parseInt(preferences.getString("volumeOffsetA", "1"));    	
    	int volumeOffsetB = Integer.parseInt(preferences.getString("volumeOffsetB", "1"));    	
    	long volumeCount = Integer.parseInt(preferences.getString("volumeCount", "1"));    	

    	motorControlCount++;
    	if ((motorControlCount % volumeCount) != 0)
    	{
            // モータ制御間隔の合間だったので何もしない
    		return;
    	}
    	
    	// モーターの出力？
    	float motorDriveRate = (volumeRate / 10) * current;
    	int value = (int) motorDriveRate;

    	// どっちのモータを動かす？
    	byte command = PSBFBaseActivity.MOTOR_A;
    	String motorType = "A";
    	if (motorControlSignal == true)
    	{
    		command = PSBFBaseActivity.MOTOR_A;
    		motorControlSignal = false;
    		value = value + volumeOffsetA;
    		motorType = "A";
    	}
    	else
    	{
    		command = PSBFBaseActivity.MOTOR_B;
    		motorControlSignal = true;
    		value = value + volumeOffsetB;
    		motorType = "B";
    	}
    	
    	// ADKへリクエストを送信
    	Log.v(PSBFMain.APP_IDENTIFIER, "MOVE MOTOR : " + motorType + " " + value);
        parent.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, command, value);

    }
}
