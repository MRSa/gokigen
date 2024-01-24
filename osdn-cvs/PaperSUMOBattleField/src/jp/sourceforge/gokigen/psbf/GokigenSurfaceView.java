package jp.sourceforge.gokigen.psbf;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
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
            //
        	doDraw();
        }
        catch (Exception ex)
        {
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
        		// Log.v(PSBFMain.APP_IDENTIFIER, "GokigenSurfaceView::doDraw()  canvas is null." );
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
    		Log.v(PSBFMain.APP_IDENTIFIER, "ex.(doDraw())>" +  ex.toString() + " " + ex.getMessage());
    	}
    }

    /**
     *    Interface ICanvasDrawer
     * 
     * @author MRSa
     *
     */
    public interface ICanvasDrawer
    {
    	/** ��ʕ`�� **/
        public abstract void drawOnCanvas(Canvas canvas);
        
        /** �^�b�`���ꂽ���̏��� **/
        public abstract boolean onTouchEvent(MotionEvent event);
    }
}
