package jp.sfjp.gokigen.okaken;

import java.util.Timer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 *    もぐらたたきゲーム的
 * 
 * @author MRSa
 *
 */
public class MoleGameActivity extends Activity  implements ClockTimer.ITimeoutReceiver, GameInformationProvider.IGameStatusListener, IActivityOpener
{
	public static final int NUMBER_OF_MOLE_COLUMNS = 3;
	public static final int NUMBER_OF_MOLE_ROWS = 4;
	public static final int NUMBER_OF_QUESTIONS = NUMBER_OF_MOLE_COLUMNS * (NUMBER_OF_MOLE_ROWS - 1);
	public static final int NUMBER_OF_MOLE_HOLES = NUMBER_OF_QUESTIONS - 1;

	private ClockTimer myTimer = null;
	private Timer timer = null;
    private static final long duration = 100;   // 100ms
    private MoleGameDrawer drawer = null;
    private QuestionnaireProvider questionProvider = null;
    private GameInformationProvider gameStatusHolder = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** 全画面表示にする **/
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /** タイトルを消す **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.molegame);

        // ゲーム状態の保持オブジェクトの生成
        questionProvider = new QuestionnaireProvider(this, NUMBER_OF_QUESTIONS);
        gameStatusHolder = new GameInformationProvider(this, duration, questionProvider, this);

        // 画面描画クラスの設定
        drawer = new MoleGameDrawer(this, gameStatusHolder, this);
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) findViewById(R.id.MoleGameCanvasView);
        surfaceView.setCanvasDrawer(drawer);  
    }

    /**
     *  メニューの生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //MenuItem menuItem = menu.add(Menu.NONE, Gokigen.MENU_ID_ABOUT, Menu.NONE, getString(R.string.about_gokigen));
    	//menuItem.setIcon(android.R.drawable.ic_menu_info_details);
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *  メニューアイテムの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = false;

        return (result);
    }
    
    /**
     *  メニュー表示前の処理
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        //menu.findItem(Gokigen.MENU_ID_ABOUT).setVisible(true);
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
    	if ((drawer != null)&&(drawer.isGameOverDrawn() == true))
    	{
            // ゲームオーバー時の描画が済んでいるときには、タイマを止めてみる。
    		return;
    	}
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
    	return (null);
    }

    /**
     *  ダイアログ表示の準備
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
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
    	if ((drawer != null)&&(drawer.isGameOverDrawn() == true))
    	{
                // ゲームオーバー時の描画が済んでいるときにはタイムアウトが発生しても何もしない。
    		    return;
    	}

    	if (gameStatusHolder != null)
    	{
    		gameStatusHolder.receivedTimeout();    		
    	}

    	// 画面の再描画指示。。。
    	final GokigenSurfaceView surfaceView0 = (GokigenSurfaceView) findViewById(R.id.MoleGameCanvasView);
    	surfaceView0.doDraw();
    }
    
    public  void changedCurrentGameStatus(int status)
    {
    	// 画面の再描画指示。。
    	final GokigenSurfaceView surfaceView1 = (GokigenSurfaceView) findViewById(R.id.MoleGameCanvasView);
    	surfaceView1.doDraw();    	
    }

    public  void triggeredGameStatus(int status)
    {
    	// 画面の再描画指示。。
    	final GokigenSurfaceView surfaceView1 = (GokigenSurfaceView) findViewById(R.id.MoleGameCanvasView);
    	surfaceView1.doDraw();    	
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
            Intent intent = new Intent(this, jp.sfjp.gokigen.okaken.ResultActivity.class);
            intent.putExtra(Gokigen.APP_INFORMATION_STORAGE, questionProvider);
            startActivity(intent);
        }
        catch (Exception ex)
        {
        	// 例外発生時...
        }
    }
}
