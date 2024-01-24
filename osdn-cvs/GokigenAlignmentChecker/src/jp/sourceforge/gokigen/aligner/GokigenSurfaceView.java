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
 *  �`�悷�邭�炷
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

        // ��ʂ𓧉߂�����
        //getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    /**
     *  �s�N�Z���t�H�[�}�b�g��ݒ肷��
     * @param format
     */
    public void setPixelFormat(int format)
    {
        getHolder().setFormat(format);    	
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
            //
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
     *  ��ʂ̃g���b�N�{�[�����������ꂽ�Ƃ��̏���
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
            // ��O�����������Ƃ��ɂ́A�������Ȃ��B
        }
    	return (super.onTrackballEvent(event));    	
    }

    /**
     * �L�[�C�x���g����
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        boolean ret = false;
        
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
     *  �T�[�t�F�C�X�J���C�x���g�̏���
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
     *  �O���t�B�b�N��`�悷��
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
    			// �`��̈悪���Ȃ����甲���Ă��܂�
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
