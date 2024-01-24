package jp.sourceforge.gokigen.cvtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *  ��ʂɕ`�悷��N���X
 * 
 * @author MRSa
 *
 */
public class GokigenSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
	ICanvasDrawer canvasDrawer = null;
	
	/**
     *  �R���X�g���N�^
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
     *   �N���X�̏���������
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
    	SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    /**
     *   ��ʂ𓧉߂�����
     * 
     */
    public void setTranslucent()
    {
        // ��ʂ𓧉߂�����
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true) ;
    }
    
    /**
     *  �f�[�^�������݃N���X�̐ݒ�
     * 
     * @param drawer
     */
    public void setCanvasDrawer(ICanvasDrawer drawer)
    {
        canvasDrawer = drawer;
    }

    /**
     *   �T�[�t�F�C�X�����C�x���g�̏���
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
     *   �^�b�`�C�x���g
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
     *  �T�[�t�F�C�X�ύX�C�x���g�̏���
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
     *  �T�[�t�F�C�X�J���C�x���g�̏���
     * 
     */
    public void surfaceDestroyed(SurfaceHolder aHolder)
    {
        try
        {
        	if (canvasDrawer != null)
        	{
        	    canvasDrawer.finished();
        	}
        }
        catch (Exception ex)
        {
            //            
        }        
    }

    /**
     *  �O���t�B�b�N��`�悷��
     */
    public void doDraw()
    {
		//Log.v(GokigenSymbols.APP_IDENTIFIER, "GokigenSurfaceView::doDraw()");

		SurfaceHolder drawHolder = getHolder();
    	try
    	{
            Canvas canvas = drawHolder.lockCanvas();
    		if (canvas == null)
    		{
    			// �`��̈悪���Ȃ����甲���Ă��܂�
        		Log.v(GokigenSymbols.APP_IDENTIFIER, "GokigenSurfaceView::doDraw()  canvas is null." );
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
    		Log.v(GokigenSymbols.APP_IDENTIFIER, "ex.(doDraw())>" +  ex.toString() + " " + ex.getMessage());
    	}
    }
}
