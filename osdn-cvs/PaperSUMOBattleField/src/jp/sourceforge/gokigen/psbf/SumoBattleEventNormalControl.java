package jp.sourceforge.gokigen.psbf;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
/**
 *   通常モードで動作中、ゲーム状態変更・イベント発生に伴う処理
 * 
 * @author MRSa
 *
 */
public class SumoBattleEventNormalControl implements SumoGameController.ISumoGameEventReceiver, GokigenSurfaceView.ICanvasDrawer
{
    private PSBFBaseActivity mActivity = null;
    private GokigenSurfaceView view = null;
    private SumoGameController sumoController = null;
    
	private int backgroundColorRed = 0x00;
	private  int backgroundColorGreen = 0x40;
	private  int backgroundColorBlue = 0x00;

	private Paint painter = new Paint();
    
    private boolean gameFinished = false;

    /**
     *   コンストラクタ
     *   
     * @param hostActivity
     */
    public SumoBattleEventNormalControl(PSBFBaseActivity hostActivity, SumoGameController controller)
    {
        mActivity = hostActivity;
        sumoController = controller;

        painter.setAntiAlias(true);
    	painter.setStrokeWidth(2.0f);
        painter.setColor(Color.WHITE);
    	painter.setStyle(Paint.Style.STROKE);
    }

    /**
     *   転倒検出！
     */
    public void detectFalldown(int fighterId)
    {
    	if (gameFinished == true)
    	{
    		// すでに一度転倒を検出済。何もしない。
    		return;
    	}
    	
    	// ゲーム終了中...
    	gameFinished = true;
    	
    	// モータ停止処理
    	Log.v(PSBFMain.APP_IDENTIFIER, "STOP MOTORs (FALLDOWN NORMAL)");
    	mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_A, 0);

        // 一応念のため、ウェイトを入れておく。
        wait(60);

        mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_B, 0);

        // 一応念のため、ウェイトを入れておく。
        wait(60);

    	// モータ動作をラッチ（モータ制御コマンドの送出を停止）する
    	mActivity.setSendCommandLatch(true);
    	
    	// 画面更新
    	update();
    }

    /**
     *    すこし動作を停止する
     *    
     * @param ms
     */
    private void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);  // wait...
        }
        catch (Exception ex)
        {
        	//
        }
    }

    /**
     *    クラスの実行準備
     * 
     */
    public void prepare()
    {
    	try
    	{
            final GokigenSurfaceView surfaceView = (GokigenSurfaceView) mActivity.findViewById(R.id.GraphicViewMain);
    		surfaceView.setCanvasDrawer(this);
           	view = surfaceView;
    		update();
    	}
    	catch (Exception ex)
    	{
    		
    	}
    }    
    
    /**
     *   画面更新のトリガー (画面表示を更新する...)
     */
    public void update()
    {
    	try
    	{
    		view.doDraw();
    	}
    	catch (Exception ex)
    	{
    		//
    	}
    }

    /**
     *   ゲームスタート！
     */
    public void startGame()
    {
    	// 画面更新...
    	update();
    }
    
    /**
     *   状況をリセット
     * 
     */
    public void resetField()
    {
        gameFinished = false;

        // 画面更新
        update();
    }

    /**
     *   画面描画
     * 
     */
    public void drawOnCanvas(Canvas canvas)
    {
        try
        {
    		// 画面全体を塗りつぶし
    		canvas.drawColor(Color.rgb(backgroundColorRed, backgroundColorGreen, backgroundColorBlue));

            // モータ動作状態を表示する    		
    		drawLatchedStatus(canvas);

    		// ゲーム状態を表示する
    		drawGameStatus(canvas);
    		
        }
        catch (Exception ex)
        {
        	// 
        }
    	
    }

    /**
     *    動作停止中かどうかの表示を行う
     * 
     * @param canvas
     */
    private void drawLatchedStatus(Canvas canvas)
    {
		boolean isLatched = mActivity.getSendCommandStatus();
		
		if (isLatched == true)
		{
	        painter.setTextSize(24);
	        painter.setColor(Color.YELLOW);
	        String msg = "モータ動作停止中";
	        canvas.drawText(msg, 10, 20, painter);
			// 
		}
		else
		{
	        painter.setTextSize(24);
	        painter.setColor(Color.WHITE);
	        String msg = " ";
	        canvas.drawText(msg, 10, 20, painter);
			// 			
		}
    }
    
    /**
     *    ゲーム状態を表示する
     * 
     * @param canvas
     */
    private void drawGameStatus(Canvas canvas)
    {
		if (gameFinished == true)
		{
			// ゲーム終了状態 ： 勝負がついていることを表示する
	        painter.setTextSize(100);
	        painter.setColor(Color.YELLOW);
	        String msg = "勝負あり！";
	        canvas.drawText(msg, 10, 150, painter);

	        //  勝者を表示する
	        int id = sumoController.getWinner();
	        String winner = " ";
	        if (id == SumoGameController.FIGHTER_A)
	        {
		        painter.setColor(Color.MAGENTA);
	        	winner = "西 (W)";
	        }
	        else if (id == SumoGameController.FIGHTER_B)
	        {
		        painter.setColor(Color.CYAN);
	        	winner = "東 (E)";
	        }
	        canvas.drawText(winner, 100, 300, painter);
		}
		else
		{
			// ゲーム進行中の表示...
	        painter.setTextSize(100);
	        painter.setColor(Color.WHITE);
	        String msg = "叩け！";
	        canvas.drawText(msg, 100, 150, painter);
		}
    }

    /**
     *   画面がタップされた！
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	Log.v(PSBFMain.APP_IDENTIFIER, "SumoBattleDrawer::onTouchEvent()");
    	update();
        return (false);    	
    }
}
