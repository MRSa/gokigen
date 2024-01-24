package jp.sourceforge.gokigen.qsteer.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class TouchMotionRecognizer implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener
{

    private Activity parent = null;  // 親分
    private GestureDetector gestureDetector = null;
    private ScaleGestureDetector scaleGestureDetector = null;
    private IMoveCommand moveCommand = null;  // 

    private boolean onScaling = false;  // ピンチイン・ピンチアウト操作しているかどうかを示す
	private boolean onGestureProcessed = false;   // 長押し時の処理を行なっているかどうかを示す。

	private float tempPosX = Float.MIN_VALUE;
	private float tempPosY = Float.MIN_VALUE;
	private float downPosX = Float.MIN_VALUE;
	private float downPosY = Float.MIN_VALUE;
	
    /**
     * 
     * @param parent
     * @param drawTrigger
     * @param usbAccessory
     */
	public TouchMotionRecognizer(Activity parent, TouchMotionRecognizer.IMoveCommand command)
	{
	    this.parent = parent;
	    this.moveCommand = command;
	    gestureDetector = new GestureDetector(parent, this);
	    scaleGestureDetector = new ScaleGestureDetector(parent, this);
	}

	public void prepareToStart(int width, int height)
    {
    	
    }
	
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean isDraw = false;

        /** スケールジェスチャ(マルチタッチのジェスチャ)を拾う **/
        isDraw = scaleGestureDetector.onTouchEvent(event);
    	if ((onScaling == true)||(scaleGestureDetector.isInProgress() == true))
    	{
    		//  マルチタッチ操作中...
        	Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchEvent() : multi touch...");
    		//return (true);
    	}

        /**  ジェスチャーを拾う...   **/
        isDraw = gestureDetector.onTouchEvent(event);
        if (isDraw == true)
        {
        	Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchEvent() : isDraw == true");
        	return (isDraw);
        }

    	int action = event.getAction();
    	
    	
        if (action == MotionEvent.ACTION_UP)
        {
        	// タッチが離されたときの処理...
            isDraw = onTouchUp(event);
            return (isDraw);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (preferences.getBoolean("ContinueTurn", false) == true)
        {
            // パラメータを確認して、ずーっと動かし続けるモードだった時の処理。。。        	
        	isDraw = publishContinueTurnCommand();
        	return (isDraw);
        }        

        //Log.v(Main.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchEvent() : " + action);
        if (action == MotionEvent.ACTION_DOWN)
        {
        	// タッチされたとき
        	isDraw = onTouchDown(event);
        }
        else if (action == MotionEvent.ACTION_MOVE)
        {
        	// タッチされたまま動かされたときの処理
            isDraw = onTouchMove(event);
        }
        return (isDraw);
    }

    /**
     *   タッチされたタイミングでの処理
     * @param event
     * @return
     */
    private boolean onTouchDown(MotionEvent event)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchDown() "  + event.getX()  + "," + event.getY());

        // タッチ位置を記憶する
    	downPosX = event.getX();
    	downPosY = event.getY();

        return (true);
    }

    /**
     *   タッチされたタイミングでの処理
     * @param event
     * @return
     */
    private boolean onTouchMove(MotionEvent event)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchMove() "  + event.getX()  + "," + event.getY());
        
        // タッチ位置を記憶する
        tempPosX = event.getX();
        tempPosY = event.getY();

        float x = tempPosX - downPosX;
        float y = tempPosY - downPosY;
        
        float absX = Math.abs(x);

        byte cmd1 = 0x00;
        byte cmd2 = 0x00;
 
        if (y > 0)
        {
        	if (absX < 50.0f)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_FWD);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_FWD);
        	}
        	else if (x < 0.0f)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_FWD_LEFT);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_FWD_LEFT);        		
        	}
        	else  // if (x >= 0)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_FWD_RIGHT);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_FWD_RIGHT);        		
        	}
        }
        else
        {
        	if (absX < 50.0f)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_BACK);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_BACK);
        	}
        	else if (x < 0.0f)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_BACK_LEFT);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_BACK_LEFT);        		
        	}
        	else  // if (x >= 0)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_BACK_RIGHT);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_BACK_RIGHT);        		
        	}
        }

        // 動作コマンドの発行
        moveCommand.publishMove(IMoveCommand.COMMAND_DOUBLE, cmd1, cmd2, IMoveCommand.LED_ON);
        return (true);
    }

    /**
     *   publishContinueTurnCommand() : BandAは左旋回、BandBは右旋回のコマンドを発行し続ける
     * 
     * @return true
     */
    private boolean publishContinueTurnCommand()
    {
        byte cmd1 =  (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_FWD_LEFT);
        byte cmd2 =  (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_FWD_RIGHT);

        // 動作コマンドの発行
        moveCommand.publishMove(IMoveCommand.COMMAND_DOUBLE, cmd1, cmd2, IMoveCommand.LED_ON);
        return (true);
    }
    
    /**
     *   タッチが離されたタイミングでの処理
     * @param event
     * @return
     */
    private boolean onTouchUp(MotionEvent event)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchUp() "  + event.getX()  + "," + event.getY());

        boolean longPress = false;
        if (onGestureProcessed == true)
        {
        	// ロングタッチ中だった場合...フラグを落とす
        	onGestureProcessed = false;
        	longPress = true;
        }

        // タッチ位置をオブジェクト画像の座標に変換する
    	float x = event.getX();
    	float y = event.getY();

    	tempPosX = Float.MIN_VALUE;
    	tempPosY = Float.MIN_VALUE;
    	downPosX = Float.MIN_VALUE;
    	downPosY = Float.MIN_VALUE;

    	// 動作を止める
    	moveCommand.publishMove(IMoveCommand.COMMAND_DOUBLE, (byte) (IMoveCommand.BAND_A|IMoveCommand.MOVE_STOP), (byte) (IMoveCommand.BAND_B|IMoveCommand.MOVE_STOP), IMoveCommand.LED_OFF);
    	return (true);
    }

    /**
     *    GestureDetector.OnGestureListener の実装
     */
    public boolean onDown(MotionEvent event)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onDown() "  + event.getX()  + "," + event.getY());    	  
        return (false);    	  
    }

    /**
     *    GestureDetector.OnGestureListener の実装
     */
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onFling() "  + velocityX  + "," + velocityY);    	  
        return (false);    	  
    }

    /**
     *    GestureDetector.OnGestureListener の実装
     */
    public void onLongPress(MotionEvent event)
    {
  	    Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onLongPress() "  + event.getX()  + "," + event.getY());   
    }

    /**
     *    GestureDetector.OnGestureListener の実装
     */
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onScroll() "  + distanceX  + "," + distanceY);    	  
        return (false);    	  
    }

    /**
     *    GestureDetector.OnGestureListener の実装
     */
    public void onShowPress(MotionEvent event)
    {
       Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onShowPress() "  + event.getX()  + "," + event.getY());    	  
    }

    /**
     *    GestureDetector.OnGestureListener の実装
     */
    public boolean onSingleTapUp(MotionEvent event)
    {
          Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onSingleTapUp() "  + event.getX()  + "," + event.getY());
          return (false);
    }

    /**
     *   （ScaleGestureDetector.OnScaleGestureListener の実装）
     * 
     * @param detector
     * @return
     */
    public boolean onScale(ScaleGestureDetector detector)
    {
        float scaleFactor = detector.getScaleFactor();
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onScale() : " + scaleFactor );

        return (false);
    }

    /**
     *   （ScaleGestureDetector.OnScaleGestureListener の実装）
     *   
     * 
     */
    public  boolean onScaleBegin(ScaleGestureDetector detector)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onScaleBegin() " );
  	    return (true);
    }

    /**
     *   （ScaleGestureDetector.OnScaleGestureListener の実装）
     *   
     */
    public void	 onScaleEnd(ScaleGestureDetector detector)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onScaleEnd() " );
  	    onScaling = false;
    }

    /**
     * 
     * 
     * @author MRSa
     *
     */
    public interface IMoveCommand
    {
    	public final byte  LED_OFF = 0x00;
    	public final byte  LED_ON = (byte) 0xff;

    	public final byte COMMAND_SINGLE = 0x01;
    	public final byte COMMAND_DOUBLE = 0x02;
    	public final byte COMMAND_STOP = (byte) 0xff;

    	public final byte BAND_A  = 0x00;
    	public final byte BAND_B  = 0x10;
    	public final byte BAND_C  = 0x20;
    	public final byte BAND_D  = 0x30;
    	public final byte BAND_A2 = 0x40;
    	public final byte BAND_B2 = 0x50;
    	public final byte BAND_C2 = 0x60;
    	public final byte BAND_D2 = 0x70;
    	
    	public final byte MOVE_FWD = 0x01;
    	public final byte MOVE_BACK = 0x02;
    	public final byte MOVE_LEFT = 0x03;
    	public final byte MOVE_RIGHT = 0x04;
    	public final byte MOVE_FWD_DASH = 0x05;
    	public final byte MOVE_FWD_LEFT = 0x06;
    	public final byte MOVE_FWD_RIGHT = 0x07;
    	public final byte MOVE_FWD_LEFT_DASH = 0x08;
    	public final byte MOVE_FWD_RIGHT_DASH = 0x09;
    	public final byte MOVE_BACK_LEFT = 0x0a;
    	public final byte MOVE_BACK_RIGHT = 0x0b;
    	public final byte MOVE_BACK_DASH = 0x0c;
    	public final byte MOVE_BACK_LEFT_DASH = 0x0d;
    	public final byte MOVE_BACK_RIGHT_DASH =0x0e;
    	public final byte MOVE_STOP = 0x0f;
    	public final byte MOVE_MASK = 0x0f;

    	public abstract void publishMove(byte command, byte arg1, byte arg2, byte arg3);
    }
}
