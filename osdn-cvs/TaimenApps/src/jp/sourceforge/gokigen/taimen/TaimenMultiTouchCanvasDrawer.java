package jp.sourceforge.gokigen.taimen;

import java.util.Enumeration;
import java.util.Hashtable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

/**
 *    描画クラス(マルチタッチタイプ)
 *    
 * @author MRSa
 *
 */
public class TaimenMultiTouchCanvasDrawer implements  ICanvasDrawer
{
	private int backgroundColorRed = 0x00;
	private  int backgroundColorGreen = 0x00;
	private  int backgroundColorBlue = 0x40;
	private float penSize = 8;

	class TouchPoint
    {
	    public float posX;
        public float posY;
        public float pressure;
    }
    Hashtable<Integer, TouchPoint> touchPoints;
    
	/**
      *   コンストラクタ
      *   
      */
	  public TaimenMultiTouchCanvasDrawer()
	  {
		  touchPoints = new Hashtable<Integer, TouchPoint>();
	  }
	  
	  /**
	   *   背景の色を変更する
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
	            // 画面を塗りつぶし
	    		canvas.drawColor(Color.rgb(backgroundColorRed, backgroundColorGreen, backgroundColorBlue));
	    		
	    		// 画面の描画
	    		Paint paint = new Paint();
	            paint.setColor(Color.WHITE);
	            paint.setStyle(Paint.Style.FILL);
	            
	            // マルチタッチのポイントを設定
	            Enumeration<Integer> keys = touchPoints.keys();
	            while (keys.hasMoreElements()) 
	            {
	            	Integer key = (Integer) keys.nextElement();
	            	TouchPoint point = touchPoints.get(key);
	            	if (point.pressure > 0)
	            	{
	                    canvas.drawCircle(point.posX, point.posY, (point.pressure * penSize), paint);
	            	}
	            }
	    	}
	    	catch (Exception ex)
	    	{
	    		// 例外発生...でもそのときには何もしない
	    		Log.v(Main.APP_IDENTIFIER, "drawOnCanvasSingle() ex: " + ex.getMessage());
	    	}
	  }
	  
	  /**
	   *   タッチした場所を拾い、ハッシュテーブルに格納する。
	   *   ( http://www.gcd.org/blog/2010/07/613/ のコードをいただく )
	   * 
	   * @param event
	   */
	  public boolean onTouchEvent(MotionEvent event)
	  {
           int count = event.getPointerCount();
           touchPoints.clear();
 		   for (int index = 0; index < count; index++)
		   {
               int id = event.getPointerId(index);
			   TouchPoint p = new TouchPoint();
			    p.posX = event.getX(index);
			    p.posY = event.getY(index);
			    p.pressure = event.getPressure(index);
			    touchPoints.put(id, p);
            }
 		   return ((count > 0) ? true : false);
	  }
}
