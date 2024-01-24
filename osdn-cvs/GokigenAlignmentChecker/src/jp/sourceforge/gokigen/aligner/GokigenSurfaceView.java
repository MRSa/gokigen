package jp.sourceforge.gokigen.aligner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *  描画するくらす
 * 
 * @author MRSa
 *
 */
public class GokigenSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
	ICanvasDrawer canvasDrawer = null;
	
	/**
     *  コンストラクタ
     * @param context
     */
	public GokigenSurfaceView(Context context)
    {
    	super(context);
    	initializeSelf(context, null);
    }

	/**
	 *  コンストラクタ
	 * @param context
	 * @param attrs
	 */
	public GokigenSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initializeSelf(context, attrs);
	}

    /**
     *   クラスの初期化処理
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
   	
    	SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // 画面を透過させる
        //getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    /**
     *  ピクセルフォーマットを設定する
     * @param format
     */
    public void setPixelFormat(int format)
    {
        getHolder().setFormat(format);    	
    }
    
    /**
     *  データ書き込みクラスの設定
     * 
     * @param drawer
     */
    public void setCanvasDrawer(ICanvasDrawer drawer)
    {
        canvasDrawer = drawer;

    }

    /**
     *   サーフェイス生成イベントの処理
     * 
     */
    public void surfaceCreated(SurfaceHolder aHolder)
    {
        try
        {
            //
        }
        catch (Exception ex)
        {
            //
        }
    }
    

    /**
     *   タッチイベント
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean ret = false;
    	if (canvasDrawer != null)
    	{
    	    ret = canvasDrawer.onTouchEvent(event);
    	    if (ret == true)
    	    {
    	    	doDraw();
    	    }
    	}
        return (ret);
    }

    /**
     *  画面のトラックボールが動かされたときの処理
     * 
     */
    @Override
    public boolean onTrackballEvent(MotionEvent event)
    {
    	Log.v(Main.APP_IDENTIFIER, "  onTrackballEvent()");
    	boolean ret = false;
        try
        {
        	if (canvasDrawer != null)
        	{
        	    ret = canvasDrawer.onTrackballEvent(event);
        	    if (ret == true)
        	    {
        	    	doDraw();
        	    }
        	}
            return (ret);
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
        }
    	return (super.onTrackballEvent(event));    	
    }

    /**
     * キーイベント処理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        boolean ret = false;
        
        return (ret);
    }

    /**
     *  サーフェイス変更イベントの処理
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
        try
        {
            //
        	doDraw();
        }
        catch (Exception ex)
        {
            //
            //
            //
        }
    }

    /**
     *  サーフェイス開放イベントの処理
     * 
     */
    public void surfaceDestroyed(SurfaceHolder aHolder)
    {
        try
        {
            //            
        }
        catch (Exception ex)
        {
            //            
        }        
    }

    /**
     *  グラフィックを描画する
     */
    public void doDraw()
    {
	    //Log.v(Main.APP_IDENTIFIER, "GokigenSurfaceView::doDraw()");

		SurfaceHolder drawHolder = getHolder();
    	try
    	{
            Canvas canvas = drawHolder.lockCanvas();
    		if (canvas == null)
    		{
    			// 描画領域が取れないから抜けてしまう
    			return;
    		}
            canvas.save();
            //////////////////////////////////////////////
            if (canvasDrawer != null)
            {
            	canvasDrawer.drawOnCanvas(canvas);
            }
            /////////////////////////////////////////////
            canvas.restore();
            drawHolder.unlockCanvasAndPost(canvas);
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "ex.(doDraw())>" +  ex.toString() + " " + ex.getMessage());
    	}
    }

}
