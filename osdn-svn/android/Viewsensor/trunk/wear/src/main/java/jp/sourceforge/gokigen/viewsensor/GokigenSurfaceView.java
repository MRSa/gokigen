package jp.sourceforge.gokigen.viewsensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *
 * 
 * @author MRSa
 *
 */
public class GokigenSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = "GokigenSurfaceView";
	ICanvasDrawer canvasDrawer = null;
	
	/**
     *
     * @param context
     */
	public GokigenSurfaceView(Context context)
    {
    	super(context);
    	initializeSelf(context, null);
    }

	/**
	 *  �R���X�g���N�^
	 * @param context
	 * @param attrs
	 */
	public GokigenSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initializeSelf(context, attrs);
	}

    /**
     *
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
    	SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // 最前面に表示する
        setZOrderOnTop(true);

        // 画面を透過させる
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    /**
     *
     * 
     * @param drawer
     */
    public void setCanvasDrawer(ICanvasDrawer drawer)
    {
        canvasDrawer = drawer;
    }

    /**
     *
     * 
     */
    public void surfaceCreated(SurfaceHolder aHolder)
    {
        try
        {
        	if (canvasDrawer != null)
        	{
        	    canvasDrawer.prepareToStart(getWidth(), getHeight());
        	}
        	doDraw();
        }
        catch (Exception ex)
        {
            //
        }
    }
    

    /**
     *
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
     *
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
        try
        {
        	if (canvasDrawer != null)
        	{
        	    canvasDrawer.changedScreenProperty(format, width, height);
        	}
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
     *
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
     *
     */
    public void doDraw()
    {
        if (Log.isLoggable(TAG, Log.VERBOSE))
        {
            Log.v(TAG, "doDraw()");
        }

		SurfaceHolder drawHolder = getHolder();
    	try
    	{
            Canvas canvas = drawHolder.lockCanvas();
    		if (canvas == null)
    		{
                if (Log.isLoggable(TAG, Log.DEBUG))
                {
                    Log.d(TAG, "doDraw()  canvas is null.");
                }
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
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "ex.(doDraw())>" + ex.toString() + " " + ex.getMessage());
            }
    	}
    }

    /**
     *
     *
     */
    public interface ICanvasDrawer
    {
        public abstract void prepareToStart(int width, int height);
        public abstract void changedScreenProperty(int format, int width, int height);
        public abstract void drawOnCanvas(Canvas canvas);
        public abstract boolean onTouchEvent(MotionEvent event);
    }
}
