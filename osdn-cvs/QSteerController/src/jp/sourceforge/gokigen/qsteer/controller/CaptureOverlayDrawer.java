package jp.sourceforge.gokigen.qsteer.controller;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;

/**
 *   �v���r���[�摜�̏�ɕ\������f�[�^��`�悷��N���X�B
 * 
 * @author MRSa
 *
 */
public class CaptureOverlayDrawer implements ICanvasDrawer
{
	private final int bgColor = 0;   // �������F
	
	private int tick = 0; // �_�~�[
	
    //private Activity parent = null;  // �e��
    private ImageProcessor imageProcessor = null;
    
    /**
     *     �R���X�g���N�^
     * 
     */
	public CaptureOverlayDrawer(Activity parent, ImageProcessor imageProcessor)
	{
	    //this.parent = parent;
	    this.imageProcessor = imageProcessor;
	}

	/**
	 *   �N�����̏���...
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
	 *    ��ʃT�C�Y���ς�����Ƃ�...
	 */
    public void changedScreenProperty(int format, int width, int height)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "CaptureOverlayDrawer::changedScreenProperty() " + " f:" + format + " w:" + width + " h:" + height);
    }

    /**
     *   ��ʕ`�惁�C������
     *   �i�`�悷����e�́A�{���\�b�h�ŏ����Ε\���ł���j
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	// Log.v(GokigenSymbols.APP_IDENTIFIER, "CaptureOverlayDrawer::drawOnCanvas()");
    	try
    	{
    		// ��ʑS�̂��N���A����
    		canvas.drawColor(bgColor, PorterDuff.Mode.CLEAR);

            // �w�i�摜��\��
    		drawBackground(canvas);

    		// �I�u�W�F�N�g��\��
    		drawObjects(canvas);
    	}
    	catch (Exception ex)
    	{
    		// ��O����...�ł����̂Ƃ��ɂ͉������Ȃ�
    		Log.v(GokigenSymbols.APP_IDENTIFIER, "CaptureOverlayDrawer::drawOnCanvas() ex: " + ex.getMessage());
    	}
    }

    /**
     *   �^�b�`�C�x���g�̏���
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	// �^�b�`���ꂽ���ʃ��t���b�V�����s
    	if (event.getAction() == MotionEvent.ACTION_UP)
    	{
    		// �^�b�`�������ꂽ��A�摜��ۊǂ���
    		imageProcessor.updateBackgroundImage();
    	}
    	return (true);
    }

    /**
     *    �w�i��`�悷��B
     * 
     * @param canvas
     */
    private void drawBackground(Canvas canvas)
    {
    }

    /**
     *    �I�u�W�F�N�g��`�悷��B
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
