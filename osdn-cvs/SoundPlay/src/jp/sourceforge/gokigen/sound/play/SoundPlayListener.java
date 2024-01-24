package jp.sourceforge.gokigen.sound.play;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 *    メインリスナディスパッチャ
 *    
 * @author MRSa
 *
 */
public class SoundPlayListener implements OnTouchListener, OnKeyListener
{
/*
	private static final int ANDEXPLORER_OPEN_FILE_REQUEST = 0;
	private static final int ANDEXPLORER_OPEN_FOLDER_REQUEST = 1;
	private static final int ANDEXPLORER_SAVE_FILE_REQUEST = 2;
	private static final int ANDEXPLORER_SAVE_FOLDER_REQUEST = 3;
	private static final int ANDEXPLORER_UNCOMPRESS_REQUEST = 4;
*/
    private static final int OPEN_PREFERENCE = 0;
	private static final int OPEN_FILE_REQUEST = 100;
	
	public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);    // 設定画面の表示
    public static final int MENU_ID_ABOUT_GOKIGEN = (Menu.FIRST + 2);  // アプリケーションの情報表示
    public static final int MENU_ID_SELECT_FILE = (Menu.FIRST + 3);  // 音楽ファイル(MP3ファイル)の選択

    private Activity parent = null;  // 親分
    private SoundPlayer player = null;
    private SoundVisualizerListener visualizerListener = null;

    /**
     *  コンストラクタ
     * @param argument
     */
    public SoundPlayListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
        // 画面表示用のリスナ生成
        visualizerListener = new SoundVisualizerListener(parent);

        // 画面描画クラスの設定
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        surfaceView.setCanvasDrawer(visualizerListener);
        visualizerListener.setGokigenSurfaceView(surfaceView);

        player = new SoundPlayer(parent, visualizerListener);

    	// PLAYボタン
        final Button playButton = (Button) parent.findViewById(R.id.PlayButton);
        playButton.setOnClickListener(player);

    	// PAUSEボタン
        final ImageButton pauseButton = (ImageButton) parent.findViewById(R.id.PauseButton);
        pauseButton.setOnClickListener(player);

        // STOPボタン
        final ImageButton stopButton = (ImageButton) parent.findViewById(R.id.StopButton);
        stopButton.setOnClickListener(player);    

        // 再生ファイル名を表示
        final TextView playFile = (TextView) parent.findViewById(R.id.SoundInfo);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        playFile.setText(preferences.getString("playFileName", ""));
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

    }


    /**
     *  終了準備
     */
    public void shutdown()
    {
        player.finish();
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
        	Log.v(Main.APP_IDENTIFIER, "File Selected : " + fileName);

        	// ファイル名をPreferenceに記録
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("playFileName", fileName);
            editor.commit();

            // 再生ファイル名を画面表示
            final TextView playFile = (TextView) parent.findViewById(R.id.SoundInfo);
            playFile.setText(fileName);
       
        }
        else if (requestCode == OPEN_PREFERENCE)
        {
            // 再生ファイル名を表示
            final TextView playFile = (TextView) parent.findViewById(R.id.SoundInfo);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            playFile.setText(preferences.getString("playFileName", ""));
        }
    	
    	// 画面表示の準備を実行...
    	prepareToStart();
    }

    /**
     *   触られたときの処理
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // int id = v.getId();
        // int action = event.getAction();

        //Log.v(Main.APP_IDENTIFIER, "SoundPlayListener::onTouch() ");
    	
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

        // Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onKey() ");
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

    	  case MENU_ID_SELECT_FILE:
    		  selectMusicFileName();
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
     *  設定画面を表示する処理
     */
    private void showPreference()
    {
        try
        {
            // 設定画面を呼び出す
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.sound.play.Preference.class);
            parent.startActivityForResult(prefIntent, OPEN_PREFERENCE);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }
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
        /*
        if (id == R.id.editTextArea)
        {
    		// 変更するテキストを表示
            return (editTextDialog.getDialog());
        }
        */
    	return (null);
    }

    /**
     *  ダイアログ表示の準備
     * 
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
    	/*
        if (id == R.id.editTextArea)
        {
        	// 変更するデータを表示する
        	prepareInfoMessageEditDialog(dialog);
        	return;
        } 
        */   	
    }
}
/*
    AndExplorer の Intent使用方法解説
      → http://www.lysesoft.com/products/andexplorer/intent.html
*/