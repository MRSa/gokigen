package jp.sourceforge.gokigen.qsteer.controller;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;

/**
 *   プレビュー画像の上に表示するデータを描画するクラス。
 * 
 * @author MRSa
 *
 */
public class CaptureOverlayDrawer implements ICanvasDrawer
{
	private final int bgColor = 0;   // 透明黒色
	
	private int tick = 0; // ダミー
	
    //private Activity parent = null;  // 親分
    private ImageProcessor imageProcessor = null;
    
    /**
     *     コンストラクタ
     * 
     */
	public CaptureOverlayDrawer(Activity parent, ImageProcessor imageProcessor)
	{
	    //this.parent = parent;
	    this.imageProcessor = imageProcessor;
	}

	/**
	 *   起動時の処理...
	 */
	public void prepareToStart(int width, int height)
    {

    }

	/**
	 * 
	 */
	public void finished()
	{
	}
	
	/**
	 *    画面サイズが変わったとき...
	 */
    public void changedScreenProperty(int format, int width, int height)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "CaptureOverlayDrawer::changedScreenProperty() " + " f:" + format + " w:" + width + " h:" + height);
    }

    /**
     *   画面描画メイン処理
     *   （描画する内容は、本メソッドで書けば表示できる）
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	// Log.v(GokigenSymbols.APP_IDENTIFIER, "CaptureOverlayDrawer::drawOnCanvas()");
    	try
    	{
    		// 画面全体をクリアする
    		canvas.drawColor(bgColor, PorterDuff.Mode.CLEAR);

            // 背景画像を表示
    		drawBackground(canvas);

    		// オブジェクトを表示
    		drawObjects(canvas);
    	}
    	catch (Exception ex)
    	{
    		// 例外発生...でもそのときには何もしない
    		Log.v(GokigenSymbols.APP_IDENTIFIER, "CaptureOverlayDrawer::drawOnCanvas() ex: " + ex.getMessage());
    	}
    }

    /**
     *   タッチイベントの処理
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	// タッチされたら画面リフレッシュ実行
    	if (event.getAction() == MotionEvent.ACTION_UP)
    	{
    		// タッチが離されたら、画像を保管する
    		imageProcessor.updateBackgroundImage();
    	}
    	return (true);
    }

    /**
     *    背景を描画する。
     * 
     * @param canvas
     */
    private void drawBackground(Canvas canvas)
    {
    }

    /**
     *    オブジェクトを描画する。
     * 
     * @param canvas
     */
    private void drawObjects(Canvas canvas)
    {
    	Paint paint = new Paint();
    	paint.setColor(Color.argb(0, 0, 0, 0));
    	canvas.drawRect(10.0f, 200.0f, 90.0f, 230.0f, paint);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(12.0f);
        paint.setStrokeWidth(1.0f);
        canvas.drawText("" + tick, 20.0f, 215.0f, paint);
        tick++;
        
        imageProcessor.drawMovingHistory(canvas);
    }
}
