package jp.sourceforge.gokigen.aligner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 *  折れ線グラフの描画クラス
 * 
 * @author MRSa
 *
 */
public class GokigenScaleDrawer implements IGokigenGraphDrawer
{
	private Context parent = null;
	
    private final int MAXIMUM_SCALE = 2;
    private final int MINIMUM_SCALE = 0;
    private int graphScale = MINIMUM_SCALE;
    private int graphRange = 0;
    private String messageToShow = "";
    
    private int shapeType = 0;

	/**
     *  コンストラクタ
     * 
     */
	public GokigenScaleDrawer(Context arg)
	{
        parent = arg;
	}	
	

	/**
	 *  準備
	 * 
	 */
	public void prepare()
	{
        // 表示するメッセージを初期化する
		setMessage(null);
	}
 
	/**
	 *  表示するメッセージを設定する
	 * 
	 * @param message
	 */
	public void setMessage(String message)
	{
		if (message == null)
		{
	        // 表示するメッセージを設定する
			messageToShow = parent.getString(R.string.captureInfo);			
		}
		else
		{
            // 指定されたメッセージを設定する
            messageToShow = message;
		}
		return;
	}
	
	/**
	 *  描画メイン処理
	 * 
	 */
    public void drawOnCanvas(Canvas canvas, int reportType)
    {
        //Log.v(Main.APP_IDENTIFIER, "GokigenScaleDrawer::drawOnCanvas()");
    	
    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);

        // 体の形を選択する
        if (shapeType == 1)
        {
            // 体のラインを表示する(正面)
            paint.setColor(Color.BLACK);
            drawBodyFront(canvas, paint, 1, 1);
          
            paint.setColor(Color.WHITE);
            drawBodyFront(canvas, paint, 0, 0);
        }
        else // if (shapeType == 0)
        {
            // 体のラインを表示する(側面)
            paint.setColor(Color.BLACK);
            drawBodySide(canvas, paint, 1, 1);
          
            paint.setColor(Color.WHITE);
            drawBodySide(canvas, paint, 0, 0);
        }


        int width = canvas.getWidth();
        int height = canvas.getHeight();
/*
        float middleY = height / 2;
        float middleX = width / 2;
        
        // 十字のスケールを表示する。
        paint.setColor(Color.BLACK);
        canvas.drawLine(0, (middleY + 1), (float) width, (middleY + 1), paint);
        canvas.drawLine((middleX + 1), 0, (middleX + 1), (float) height, paint);

        paint.setColor(Color.WHITE);
        canvas.drawLine(0, middleY, (float) width, middleY, paint);
        canvas.drawLine(middleX, 0, middleX, (float) height, paint);
*/

        // 文字列の大きさを設定する
        Rect bounds = new Rect();
        paint.getTextBounds(messageToShow, 0, messageToShow.length(), bounds);
        
        // インフォメーションを表示
        paint.setColor(Color.BLACK);
        canvas.drawText(messageToShow, (width - (bounds.width() + 4)), (height - (bounds.height() + 4)), paint);

        paint.setColor(Color.LTGRAY);
        canvas.drawText(messageToShow, (width - (bounds.width() + 5)), (height - (bounds.height() + 5)), paint);
    }

	/**
	 *  縮小
	 * 
	 */
    public void actionZoomOut()
    {
    	if (graphScale > MINIMUM_SCALE)
    	{
    		graphScale--;
    	}
    	if ((graphScale == 1)&&(graphRange > 1))
    	{
    		graphRange = 1;
    	}
    	else
    	{
    		graphRange = 0;
    	}
    }

    /**
     *  からだのラインを表示する (正面)
     * 
     * @param canvas
     * @param paint
     * @param offsetX
     * @param offsetY
     */
    private void drawBodyFront(Canvas canvas, Paint paint, float offsetX, float offsetY)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // 頭を描画する
        paint.setStyle(Paint.Style.STROKE);
        float centerX = ((float) 32 + 16) / (float) 300 * width + offsetX;
        float centerY = (float) height / (float) 2 + offsetY;
        float radius = ((float) 32 - 16) / (float) 300 * width;
        canvas.drawCircle(centerX, centerY, radius, paint);

        // 首を描画する
        float startX = (float) (64) / (float) 300 * width + offsetX;
        float length = (float) (16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);
        
        // 肩を描画する
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        float lengthY = (((float) 16) / ((float) 300) * width);
        float startY = (float) height / (float) 2;
        canvas.drawLine(startX, (startY + offsetY), startX, (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + offsetY), startX, (startY + lengthY + offsetY), paint);

        // 腕を描画する
        length = (float) (56) / (float) 300 * width;
        float lengthY2 =  (((float) 40) / ((float) 300) * width);
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY2 + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY2 + offsetY), paint);
        
        startX = startX + length;
        length = (float) (64) / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY2 + offsetY), (startX + length), (startY - lengthY2 + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY2 + offsetY), (startX + length), (startY + lengthY2 + offsetY), paint);
        
        // 胴を描画する
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        length = (float) (80) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // 腰を描画する
        startX = (float) (64 + 16 + 80) / (float) 300 * width + offsetX;
        length = (float) (16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // 骨盤を描画する
        startX = (float) (64 + 16 + 80 + 16) / (float) 300 * width + offsetX;
        length = (float) (8) / (float) 300 * width;
        lengthY = (((float) 16) / ((float) 300) * width);
        startY = (float) height / (float) 2;
        canvas.drawLine(startX, (startY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);
    
        // 太ももを描画する
        startX = (float) (64 + 16 + 80 + 16 + 8) / (float) 300 * width + offsetX;
        length = (float) 56 / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);

        // 足を描画する
        startX = (float) (64 + 16 + 80 + 16 + 8 + 56) / (float) 300 * width + offsetX;
        length = (float) 56 / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);
    }
    
    /**
     *  からだのラインを表示する (側面)
     * 
     * @param canvas
     * @param paint
     * @param offsetX
     * @param offsetY
     */
    private void drawBodySide(Canvas canvas, Paint paint, float offsetX, float offsetY)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // 頭を描画する
        paint.setStyle(Paint.Style.STROKE);
        float centerX = ((float) 32 + 16) / (float) 300 * width + offsetX;
        float centerY = (float) height / (float) 2 + offsetY;
        float radius = ((float) 32 - 16) / (float) 300 * width;
        RectF oval = new RectF((centerX - radius), (centerY - radius / 3), (centerX + radius), (centerY + radius / 3));
        canvas.drawOval(oval, paint);
        
        // 首を描画する
        float startX = ((float) 64) / (float) 300 * width + offsetX;
        float length = (float) (64 + 16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // 腕を描画する
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        length = (float) (40) / (float) 300 * width;
        float lengthY2 =  (((float) 5) / ((float) 300) * width);
        canvas.drawLine(startX, centerY, (startX + length), centerY - lengthY2, paint);

        startX = (float) (64 + 16 + 40) / (float) 300 * width + offsetX;
        length = (float) (56) / (float) 300 * width;
        canvas.drawLine(startX, centerY - lengthY2, (startX + length), centerY + lengthY2, paint);
         
        // 腰を描画する
        
        // 胴～足を描画する
        startX = ((float) 64 + 16) / (float) 300 * width + offsetX;
        length = (float) (80 + 16 + 8 + 56 + 56) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        
/*
        // 首を描画する
        float startX = (float) (64) / (float) 300 * width + offsetX;
        float length = (float) (16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);
        
        // 肩を描画する
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        float lengthY = (((float) 16) / ((float) 300) * width);
        float startY = (float) height / (float) 2;
        canvas.drawLine(startX, (startY + offsetY), startX, (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + offsetY), startX, (startY + lengthY + offsetY), paint);

        // 腕を描画する
        length = (float) (56) / (float) 300 * width;
        float lengthY2 =  (((float) 40) / ((float) 300) * width);
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY2 + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY2 + offsetY), paint);
        
        startX = startX + length;
        length = (float) (64) / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY2 + offsetY), (startX + length), (startY - lengthY2 + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY2 + offsetY), (startX + length), (startY + lengthY2 + offsetY), paint);
        
        // 胴を描画する
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        length = (float) (80) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // 腰を描画する
        startX = (float) (64 + 16 + 80) / (float) 300 * width + offsetX;
        length = (float) (16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // 骨盤を描画する
        startX = (float) (64 + 16 + 80 + 16) / (float) 300 * width + offsetX;
        length = (float) (8) / (float) 300 * width;
        lengthY = (((float) 16) / ((float) 300) * width);
        startY = (float) height / (float) 2;
        canvas.drawLine(startX, (startY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);
    
        // 太ももを描画する
        startX = (float) (64 + 16 + 80 + 16 + 8) / (float) 300 * width + offsetX;
        length = (float) 56 / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);

        // 足を描画する
        startX = (float) (64 + 16 + 80 + 16 + 8 + 56) / (float) 300 * width + offsetX;
        length = (float) 56 / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);
*/
    }

	/**
	 *  拡大
	 * 
	 */
    public void actionZoomIn()
    {
        if (graphScale < MAXIMUM_SCALE)
        {
            graphScale++;
        }
    }

	/**
	 *  前データ
	 * 
	 */
    public boolean actionShowPreviousData()
    {
    	if (graphRange > 0)
    	{
    		graphRange--;
    		return (false);
    	}
    	
        // ひとつ前のデータに更新する...
    	if (graphScale == 2)
    	{
    		graphRange = 3;
    	}
    	else if (graphScale == 1)
    	{
    		graphRange = 1;
    	}
    	else
    	{
    		graphRange = 0;
    	}    	
        return (true);
    }

	/**
	 *  後データ
	 *
	 */
    public boolean actionShowNextData()
    {
    	if (graphScale == 1)
    	{
    		if (graphRange == 0)
    		{
    			graphRange++;
    			return (false);
    		}
    	}
    	else if (graphScale == 2)
    	{
    		if (graphRange < 3)
    		{
    			graphRange++;
    			return (false);
    		}
    	}
        graphRange = 0;
        return (true);    	
    }
    
    public void reset()
    {
    	// なにもしない
    }
    
    public void undo()
    {
    	// なにもしない
    }

    /**
     * 描画タイプを設定する
     */
    public void setDrawType(int type)
    {
    	shapeType = type;
    }
}
