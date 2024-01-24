package jp.sfjp.gokigen.okaken;

import java.util.Timer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class OkakenActivity extends Activity implements ClockTimer.ITimeoutReceiver, IActivityOpener, OnClickListener
{
	private ClockTimer myTimer = null;
	private Timer timer = null;
    private static final long duration = 500;   // 500ms
    private MainDrawer drawer = null;
    //private GameInformationProvider gameStatusHolder = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** 全画面表示にする **/
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /** タイトルを消す **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Preferenceを取得する
        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	// String colorString = preferences.getString("backgroundColor", "0xff004000");

        // 画面表示のレイアウトを設定する
        setContentView(R.layout.main);
        
        // 画面描画クラスの設定
        drawer = new MainDrawer(this, this);
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) findViewById(R.id.MainView);
        surfaceView.setCanvasDrawer(drawer);

        // ボタンの設定
        final Button instructionButton = (Button) findViewById(R.id.InstructionButton);
        instructionButton.setOnClickListener(this);
        //surfaceView.setOnTouchListener(this);
    
    }

    /**
     *  メニューの生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add(Menu.NONE, Gokigen.MENU_ID_ABOUT, Menu.NONE, getString(R.string.about_gokigen));
    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *  メニューアイテムの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = false;
        switch (item.getItemId())
        {
          case Gokigen.MENU_ID_ABOUT:
        	// Aboutメニューが選択されたときは、クレジットダイアログを表示する
        	showDialog(R.id.info_about_gokigen);
            result = true;
            break;

          default:
            result = false;
            break;
        }
        return (result);
    }
    
    /**
     *  メニュー表示前の処理
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(Gokigen.MENU_ID_ABOUT).setVisible(true);
        return (super.onPrepareOptionsMenu(menu));
    }

    /**
     *  画面が裏に回ったときの処理
     */
    @Override
    public void onPause()
    {
        super.onPause();
        stopTimer();

    }
    
    /**
     *  画面が表に出てきたときの処理
     */
    @Override
    public void onResume()
    {
        super.onResume();
        startTimer();
    }
    
    /**
     *  子画面から応答をもらったときの処理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            // 子画面からもらった情報の応答処理
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
        }
    } 

    /**
     *  ダイアログ表示（初回）の準備
     * 
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == R.id.info_about_gokigen)
	    {
        	// クレジットダイアログを表示
		    CreditDialog dialog = new CreditDialog(this);
		    return (dialog.getDialog());
	    }
    	return (null);
    }

    /**
     *  ダイアログ表示の準備
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
        if (id == R.id.info_about_gokigen)
	    {
            // クレジットダイアログを表示するときには何もしない。
        	return;
	    }
    	// ダイアログ情報を更新する場合には、ここに追加する
    	return;
    }  
    /**
     *   タイムアウト受信時の処理...
     * 
     */
    public void receivedTimeout()
    {
        // Log.v(Gokigen.APP_IDENTIFIER, "receivedTimeout()");    

    	// 画面の再描画指示。。。（0.5secおき？）
    	final GokigenSurfaceView surfaceView0 = (GokigenSurfaceView) findViewById(R.id.MainView);
    	surfaceView0.doDraw();
    }

    /**
     *   クリックされたときの処理
     */
    public void onClick(View v)
    {
        int id = v.getId();
    	drawer.showInstruction(id);

    	// 画面の再描画指示。。。（0.5secおき？）
    	final GokigenSurfaceView surfaceView1 = (GokigenSurfaceView) findViewById(R.id.MainView);
    	surfaceView1.doDraw();

    }
    
    /**
     *  Activityを切り替える
     * 
     * @param fileName
     */
    public void requestToStartActivity(int id)
    {
        try
        {
            // Activityを起動する
            Intent intent = new Intent(this, jp.sfjp.gokigen.okaken.MoleGameActivity.class);
            startActivityForResult(intent, id);
        }
        catch (Exception ex)
        {
        	// 例外発生時...
        }
    }
    
    /**
     * 
     * 
     */
    private void stopTimer()
    {
        try
        {
            // TODO: 動作を止めるようイベント処理クラスに指示する
        	if (timer != null)
        	{
        		timer.cancel();
        		timer = null;
        	}
           	myTimer = null;
        }
        catch (Exception ex)
        {
            // 何もしない
        }    	
    }
    
    /**
     * 
     * 
     */
    private void startTimer()
    {
        try
        {
        	if (timer != null)
        	{
        		timer.cancel();
        		timer = null;
        	}
        	timer = new Timer();
        	
        	// タイマータスクの準備
           	myTimer = null;
        	myTimer = new ClockTimer(this);
        	timer.scheduleAtFixedRate(myTimer, duration, duration);
        }
        catch (Exception ex)
        {
            // なにもしない
        }    	
    }
}