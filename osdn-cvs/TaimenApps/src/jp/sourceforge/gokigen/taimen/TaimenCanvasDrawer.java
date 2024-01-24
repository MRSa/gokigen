package jp.sourceforge.gokigen.taimen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

/**
 *    �`��N���X (�V���O���^�b�`�^�C�v)
 *    
 * @author MRSa
 *
 */
public class TaimenCanvasDrawer implements  ICanvasDrawer
{
	private int backgroundColorRed = 0x00;
	private  int backgroundColorGreen = 0x40;
	private  int backgroundColorBlue = 0x00;
	private float tempPosX = -1;
	private float tempPosY = -1;
	private float positionX = -1;
	private float positionY = -1;
	private float penSize = 8;
    
	/**
      *   �R���X�g���N�^
      *   
      */
	  public TaimenCanvasDrawer()
	  {

	  }
	  
	  /**
	   *   �w�i�̐F��ύX����
	   * 
	   * @param color
	   */
	  public void changeBackgroundColor(int red, int green, int blue)
	  {
		  backgroundColorRed = red;
		  backgroundColorGreen = green;
		  backgroundColorBlue = blue;
	  }
	  
	  public void setPenSize(float size)
	  {
		  penSize = size;
	  }

	  
	  /**
	   * 
	   * 
	   */
	  public void drawOnCanvas(Canvas canvas)
	  {
	    	Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::drawOnCanvas()");
	    	try
	    	{
	            // ��ʂ�h��Ԃ�
	    		canvas.drawColor(Color.rgb(backgroundColorRed, backgroundColorGreen, backgroundColorBlue));
	    		
	    		// ��ʂ̕`��
	    		//int width = canvas.getWidth();
	            //int height = canvas.getHeight();
	    		if ((positionX < 0)||(positionY < 0))
	    		{
                    return;
	    		}
	    		Paint paint = new Paint();
	            paint.setColor(Color.WHITE);
	            paint.setStyle(Paint.Style.FILL);
	            if ((tempPosX < 0)||(tempPosY < 0))
	            {
	                canvas.drawCircle(positionX, positionY, penSize, paint);
	            }
	            else
	            {
	            	// �t���b�N���̋O�Ղ�\������
                    paint.setColor(Color.GRAY);
	            	canvas.drawLine(positionX, positionY, tempPosX, tempPosY, paint);
                    canvas.drawCircle(tempPosX, tempPosY, penSize, paint);	            	

                    // ���ݒn�_�̕\��
    	            paint.setColor(Color.WHITE);
	                canvas.drawCircle(positionX, positionY, penSize, paint);
	            }
	    	}
	    	catch (Exception ex)
	    	{
	    		// ��O����...�ł����̂Ƃ��ɂ͉������Ȃ�
	    		Log.v(Main.APP_IDENTIFIER, "drawOnCanvasSingle() ex: " + ex.getMessage());
	    	}
	  }

	  /**
	   *   �^�b�`�����ꏊ���E��
	   * 
	   * @param event
	   */
	  public boolean onTouchEvent(MotionEvent event)
	  {
          boolean isDraw = false;

	    	int action = event.getAction();
	    	Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onTouchEvent() : " + action);
	        if (action == MotionEvent.ACTION_DOWN)
	        {
	            //
	        }
	        else if (action == MotionEvent.ACTION_MOVE)
	        {
	        	tempPosX = event.getX();
              tempPosY = event.getY();
	             isDraw = true;
	        }
	        else if ((action == MotionEvent.ACTION_CANCEL)||(action == MotionEvent.ACTION_OUTSIDE))
	        {
	        	tempPosX = -1;
	        	tempPosY = -1;
	        	isDraw = true;
	        }
	        else if (action == MotionEvent.ACTION_UP)
	        {
	        	 tempPosX = -1;
	        	 tempPosY = -1;
	        	 positionX = event.getX();
	        	 positionY = event.getY(); 
	             isDraw = true;
	        }
		  	return (isDraw);		  
	  }	  
}
