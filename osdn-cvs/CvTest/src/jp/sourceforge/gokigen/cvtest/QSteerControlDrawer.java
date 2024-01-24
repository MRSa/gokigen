package jp.sourceforge.gokigen.cvtest;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 
 * @author MRSa
 *
 */
public class QSteerControlDrawer implements ICanvasDrawer, TouchMotionRecognizer.IMoveCommand
{
	private final int bgColor = 0;   // 透明黒色
	
    //private Activity parent = null;  // 親分
    private IRedrawer drawTrigger = null;  // 
    private TouchMotionRecognizer eventHandler = null;
    private ImageProcessor imageProcessor = null;
    
    private byte bandACommand = 0x00;   // 最後に発行したコマンド
    private byte bandBCommand = 0x00;   // 最後に発行したコマンド 
    
    /**
     *     コンストラクタ
     * 
     */
	public QSteerControlDrawer(Activity parent, IRedrawer trigger, ImageProcessor imageProcessor)
	{
	    //this.parent = parent;
	    this.drawTrigger =trigger;
	    this.imageProcessor = imageProcessor;

	    // TouchMotionRecognizer
	    eventHandler = new TouchMotionRecognizer(parent, this);
	}

	/**
	 *   起動時の処理...
	 */
	public void prepareToStart(int width, int height)
    {
        eventHandler.prepareToStart(width, height);
        
        if (imageProcessor != null)
        {
            imageProcessor.prepareToStart(width, height);
        }
    }

	/**
	 * 
	 */
	public void finished()
	{
        if (imageProcessor != null)
        {
            imageProcessor.finished();
        }
	}
	
	/**
	 *    画面サイズが変わったとき...
	 */
    public void changedScreenProperty(int format, int width, int height)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "QSteerControlDrawer::changedScreenProperty() " + " f:" + format + " w:" + width + " h:" + height);
    }

    /**
     *   画面描画メイン処理
     *   （描画する内容は、本メソッドで書けば表示できる）
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	// Log.v(GokigenSymbols.APP_IDENTIFIER, "QSteerControlDrawer::drawOnCanvas()");
    	try
    	{
    		// 画面全体をクリアする
    		canvas.drawColor(bgColor, PorterDuff.Mode.CLEAR);

            // 背景画像を表示
    		drawBackground(canvas);

    		// オブジェクトを表示
    		drawObjects(canvas);

    		// 発行したコマンドを表示
    		drawPublishedCommands(canvas);
    	}
    	catch (Exception ex)
    	{
    		// 例外発生...でもそのときには何もしない
    		Log.v(GokigenSymbols.APP_IDENTIFIER, "drawOnCanvas() ex: " + ex.getMessage());
    	}
    }

    /**
     *   タッチイベントの処理
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	return (eventHandler.onTouchEvent(event));
    }

    /**
     *     USBアクセサリに動作コマンドを発行する
     * 
     */
    public void publishMove(byte command, byte arg1, byte arg2, byte arg3)
    {
        byte[] message = new byte[4];
        message[0] = command;
        message[1] = arg1;
        message[2] = arg2;
        message[3] = arg3;

        bandACommand = (byte) (arg1 & TouchMotionRecognizer.IMoveCommand.MOVE_MASK);
        bandBCommand = (byte) (arg2 & TouchMotionRecognizer.IMoveCommand.MOVE_MASK);

        drawTrigger.redraw();
    }

    /**
     *    背景を描画する。
     * 
     * @param canvas
     */
    private void drawBackground(Canvas canvas)
    {
    	if (imageProcessor != null)
    	{
    		imageProcessor.drawBackground(canvas);
    	}    	
    }

    /**
     *    オブジェクトを描画する。
     * 
     * @param canvas
     */
    private void drawObjects(Canvas canvas)
    {
    	if (imageProcessor != null)
    	{
    		imageProcessor.drawObjects(canvas);
    	}
    }

    /**
     *    コマンド発行結果を描画する
     * 
     * @param canvas
     */
    private void drawPublishedCommands(Canvas canvas)
	{
    	Paint paint = new Paint();

    	paint.setColor(Color.argb(64, 0, 0, 0));
    	canvas.drawRect(10.0f, 40.0f, 90.0f, 100.0f, paint);
    	
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(16.0f);
        paint.setStrokeWidth(1.0f);
        drawCommand(canvas, paint, bandACommand, 20.0f, 60.0f);

        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(16.0f);
        paint.setStrokeWidth(1.0f);
        drawCommand(canvas, paint, bandBCommand, 20.0f, 90.0f);
	}

    /**
     *    コマンド発行結果を描画する
     * 
     * @param canvas
     * @param command
     * @param offsetX
     * @param offsetY
     */
    private void drawCommand(Canvas canvas, Paint paint, byte command, float offsetX, float offsetY)
    {
    	String message = "";
    	switch (command)
    	{
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD:
          	message = "↑";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK:
          	message = "↓";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_LEFT:
          	message = "←";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_RIGHT:
          	message = "→";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_DASH:
          	message = "↑↑";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_LEFT:
          	message = "←↑";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_RIGHT:
          	message = "↑→";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_LEFT_DASH:
          	message = "←←↑";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_RIGHT_DASH:
          	message = "↑→→";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_LEFT:
          	message = "←↓";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_RIGHT:
          	message = "↓→";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_DASH:
          	message = "↓↓";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_LEFT_DASH:
          	message = "←←↓";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_RIGHT_DASH:
          	message = "↓→→";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_STOP:
          default:
        	message = "STOP";
            break;
    	}
        canvas.drawText(" " + message, offsetX, offsetY, paint);
    }

    /**
     *    画面再描画のトリガ
     * 
     * @author MRSa
     */
    public interface IRedrawer
    {	
        public abstract void redraw();
    }
}
