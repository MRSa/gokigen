package jp.sourceforge.gokigen.diary;

import java.util.Enumeration;

import jp.sourceforge.gokigen.diary.GokigenGraphDataHolder.gokigenData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 *  折れ線グラフの描画クラス
 * 
 * @author MRSa
 *
 */
public class GokigenLineChart implements IGokigenGraphDrawer
{
	private Context parent = null;
	
    private final int MAXIMUM_SCALE = 2;
    private final int MINIMUM_SCALE = 0;
    private int graphScale = MINIMUM_SCALE;
    private int graphRange = 0;

	
	/**
     *  コンストラクタ
     * 
     */
	public GokigenLineChart(Context arg)
	{
        parent = arg;
	}	
	

	/**
	 *  準備
	 * 
	 */
	public void prepare()
	{
        // 何もしない
	}
	
	/**
	 *  描画メイン処理
	 * 
	 */
    public void drawOnCanvas(Canvas canvas, int reportType, GokigenGraphDataHolder dataHolder)
    {
    	// 描画領域のサイズを取得する
    	if (reportType == GokigenGraphListener.REPORTTYPE_DAILY)
    	{
    		drawOnCanvasLineChart(canvas, dataHolder);
    	}
    	else if (reportType == GokigenGraphListener.REPORTTYPE_MONTHLY)
    	{
    		drawOnCanvasLineChartMonthly(canvas, dataHolder);
    	}
    	else
    	{
    		// 未対応のグラフ形式...なにもしない
    	}
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
	
    /**
     *  キャンバスにデータを描画する
     *  (折れ線グラフ)
     * 
     */
    private void drawOnCanvasLineChartMonthly(Canvas canvas, GokigenGraphDataHolder dataHolder)
    {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float offset  = (float) 16.0;
        float hOffset = (float) 20.0;
        float positionX = offset;
        float positionY = (float) height - hOffset;
        
        float rateBand  = ((float) height - hOffset) / (float) (52);
        float rateLine  = 0;

        float dayOffset = 0;
        float showDay = 32;
        float subLine = 5;
        if (graphScale == 2)
        {
            showDay = 8;
            subLine = 1;
            dayOffset = 8 * graphRange;
        }
        else if (graphScale == 1)
        {
            showDay = 16;
            subLine = 3;
            dayOffset = 16 * graphRange;
        }
        float oneDay = ((float) width - offset) / (float) showDay;

        
        paint.setColor(Color.DKGRAY);
        for (rateLine = 5; rateLine <= 50; rateLine = rateLine + 5)
        {
            float point = rateLine * rateBand;
            canvas.drawLine((float) (offset / 2), (positionY - point), (float) width, (positionY - point), paint);
        }

        paint.setColor(Color.DKGRAY);
        canvas.drawText("x" + (graphScale + 1) + " " + graphRange, (width - 28), hOffset, paint);
        
        float scaleDay = 0;
        for (float dayLine = 0; dayLine < showDay; dayLine = dayLine + subLine)
        {
            scaleDay = dayLine * oneDay;
            paint.setColor(Color.DKGRAY);
            canvas.drawLine(positionX + scaleDay, (float) 0.0, positionX + scaleDay, (float) (height - hOffset / 2), paint);

            paint.setColor(Color.LTGRAY);
            canvas.drawText("" + ((int) ((int) dayLine + (int) dayOffset)), positionX + scaleDay - 16, (float) (height - hOffset / 2) + 2, paint);
        }
        
        // 15日のラインを引く
        //scaleDay = 15 * oneDay;
        //paint.setColor(Color.GREEN);
        //canvas.drawLine(positionX + scaleDay, (float) 0.0, positionX + scaleDay, (float) (height - hOffset / 2), paint);
        
        // 原点のラインを引く
        paint.setColor(Color.WHITE);
        canvas.drawLine(positionX, (float) 0.0, positionX, (float) height, paint);
        canvas.drawLine((float) 0.0, positionY, (float) width, positionY, paint);

        try
        {
            // イテレータで（２回）まわして記録領域をプロットする
            boolean isFinished = false;
            float   finishY  = 0;
            float previousX = offset;
            float previousY = height - hOffset;
            paint.setColor(0xff008000);
            
            // グラフ（線）を書く
            Enumeration<gokigenData> e = dataHolder.getDataList().elements();
            while (e.hasMoreElements())
            {
                gokigenData data = (gokigenData) e.nextElement();

                float checkDay = data.getDay();
                if ((checkDay >= dayOffset)&&(checkDay <= dayOffset + showDay))
                {
                    float x = (checkDay - dayOffset) * oneDay + positionX;
                    float y = positionY - (data.getRate() * rateBand);
                    canvas.drawLine(previousX, previousY, x, y, paint);
                    previousX = x;
                    previousY = y;
                }
                else
                {
                    if (checkDay < dayOffset)
                    {
                        //float x = (timeSeconds < lowerLimitSecond) ? positionX : (positionX + axisX);
                        //float y = positionY - (data.getRate() * rateBand);
                        //canvas.drawLine(previousX, previousY, x, y, paint);
                        previousY = positionY - (data.getRate() * rateBand);
                    }
                    else
                    {
                        if (isFinished == false)
                        {
                            finishY = positionY - (data.getRate() * rateBand);
                            isFinished = true;
                        }        
                    }
                }
            }
            if (isFinished == true)
            {
                canvas.drawLine(previousX, previousY, (float) width, finishY, paint);
            }
            else
            {
                canvas.drawLine(previousX, previousY, (float) width, previousY, paint);
            }

            // アイコンを描画する
            Enumeration<gokigenData> e2 = dataHolder.getDataList().elements();
            while (e2.hasMoreElements())
            {
                gokigenData data = (gokigenData) e2.nextElement();

                float checkDay = data.getDay();
                if ((checkDay >= dayOffset)&&(checkDay <= dayOffset + showDay))
                {
                    float x = (checkDay - dayOffset) * oneDay + positionX;
                    float y = positionY - (data.getRate() * rateBand);
                    Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), DecideEmotionIcon.decideEmotionIconSmall((int)data.getRate())); 
                    canvas.drawBitmap(bitmap, x - 15, y - 15, paint);
                }
            }
        }
        catch (Exception ex)
        {
            //             
        }
    }
    /**
     *  キャンバスにデータを描画する
     *  (折れ線グラフ)
     * 
     */
    private void drawOnCanvasLineChart(Canvas canvas, GokigenGraphDataHolder dataHolder)
    {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float offset  = (float) 16.0;
        float hOffset = (float) 20.0;
        float positionX = offset;
        float positionY = (float) height - hOffset;
        
        float rateBand  = ((float) height - hOffset) / (float) (52);
        float rateLine  = 0;
        
        //        float oneDaySecond = ((float) width - offset) / (float) (60 * 60 * 24);
        float axisX = ((float) width - offset);  //  / ((float) 24 * 60 * 60);

        paint.setColor(Color.DKGRAY);
        for (rateLine = 5; rateLine <= 50; rateLine = rateLine + 5)
        {
            float point = rateLine * rateBand;
            canvas.drawLine((float) (offset / 2), (positionY - point), (float) width, (positionY - point), paint);
        }
        
        // 拡大：縮小の表示を用意する
        paint.setColor(Color.DKGRAY);
        canvas.drawText("x" + (graphScale + 1) + " " + graphRange, (positionX + (22 * axisX) / 24), hOffset, paint);

        float scaleTime = 0;
        paint.setColor(Color.LTGRAY);
        canvas.drawText(decideXaxisLabel(graphScale, graphRange, 0), positionX - 16, (float) (height - hOffset / 2) + 2, paint);

        scaleTime = (4 * axisX) / 24;
        paint.setColor(Color.DKGRAY);
        canvas.drawLine(positionX + scaleTime, (float) 0.0, positionX + scaleTime, (float) (height - hOffset / 2), paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawText(decideXaxisLabel(graphScale, graphRange, 1), positionX + scaleTime - 16, (float) (height - hOffset / 2) + 2, paint);

        scaleTime = (8 * axisX) / 24;
        paint.setColor(Color.DKGRAY);
        canvas.drawLine(positionX + scaleTime, (float) 0.0, positionX + scaleTime, (float) (height - hOffset / 2), paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawText(decideXaxisLabel(graphScale, graphRange, 2), positionX + scaleTime - 16, (float) (height - hOffset / 2) + 2, paint);

        scaleTime = (12 * axisX) / 24;
        paint.setColor(Color.DKGRAY);
        canvas.drawLine(positionX + scaleTime, (float) 0.0, positionX + scaleTime, (float) (height - hOffset / 2), paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawText(decideXaxisLabel(graphScale, graphRange, 3), positionX + scaleTime - 16, (float) (height - hOffset / 2) + 2, paint);

        scaleTime = (16 * axisX) / 24;
        paint.setColor(Color.DKGRAY);
        canvas.drawLine(positionX + scaleTime, (float) 0.0, positionX + scaleTime, (float) (height - hOffset / 2), paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawText(decideXaxisLabel(graphScale, graphRange, 4), positionX + scaleTime - 16, (float) (height - hOffset / 2) + 2, paint);

        scaleTime = (20 * axisX) / 24;
        paint.setColor(Color.DKGRAY);
        canvas.drawLine(positionX + scaleTime, (float) 0.0, positionX + scaleTime, (float) (height - hOffset / 2), paint);
        paint.setColor(Color.LTGRAY);
        canvas.drawText(decideXaxisLabel(graphScale, graphRange, 5), positionX + scaleTime - 16, (float) (height - hOffset / 2) + 2, paint);
        
        // 原点のラインを引く
        paint.setColor(Color.WHITE);
        canvas.drawLine(positionX, (float) 0.0, positionX, (float) height, paint);
        canvas.drawLine((float) 0.0, positionY, (float) width, positionY, paint);

        float bandSeconds = axisX;
        if (graphScale == 2)
        {
            bandSeconds = axisX / (60 * 60 * 6);
        }
        else if (graphScale == 1)
        {
            bandSeconds = axisX / (60 * 60 * 12);
        }
        else // if (graphScale == 0)
        {
            bandSeconds = axisX / (60 * 60 * 24);            
        }

        try
        {
            float lowerLimitSecond = decideStartSeconds(graphScale, graphRange);
            float upperLimitSecond = decideEndSeconds(graphScale, graphRange);
            
            // イテレータで（２回）まわして記録領域をプロットする
            float previousX = offset;
            float previousY = height - hOffset;
            paint.setColor(0xff008000);
           
            // グラフ（線）を書く
            boolean isFinished = false;
            float   finishY  = 0;
            Enumeration<gokigenData> e = dataHolder.getDataList().elements();
            while (e.hasMoreElements())
            {
                gokigenData data = (gokigenData) e.nextElement();

                float timeSeconds = data.getTime();
                if ((timeSeconds >= lowerLimitSecond)&&(timeSeconds <= upperLimitSecond))
                {
                    float x = (timeSeconds - lowerLimitSecond) * bandSeconds + positionX;
                    float y = positionY - (data.getRate() * rateBand);
                    canvas.drawLine(previousX, previousY, x, y, paint);
                    previousX = x;
                    previousY = y;
                }
                else
                {
                    if (timeSeconds < lowerLimitSecond)
                    {
                        //float x = (timeSeconds < lowerLimitSecond) ? positionX : (positionX + axisX);
                        //float y = positionY - (data.getRate() * rateBand);
                        //canvas.drawLine(previousX, previousY, x, y, paint);
                        previousY = positionY - (data.getRate() * rateBand);
                    }
                    else
                    {
                        if (isFinished == false)
                        {
                            finishY = positionY - (data.getRate() * rateBand);
                            isFinished = true;
                        }
                                                
                    }
                }
            }
            if (isFinished == true)
            {
                canvas.drawLine(previousX, previousY, (float) width, finishY, paint);
            }
            else
            {
                canvas.drawLine(previousX, previousY, (float) width, previousY, paint);
            }
            
            // アイコンを描画する
            Enumeration<gokigenData> e2 = dataHolder.getDataList().elements();
            while (e2.hasMoreElements())
            {
                gokigenData data = (gokigenData) e2.nextElement();
                float timeSeconds = data.getTime();
                if ((timeSeconds >= lowerLimitSecond)&&(timeSeconds <= upperLimitSecond))
                {
                    float x = (timeSeconds - lowerLimitSecond) * bandSeconds + positionX;
                    float y = positionY - (data.getRate() * rateBand);
                    Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), DecideEmotionIcon.decideEmotionIcon((int)data.getRate())); 
                    canvas.drawBitmap(bitmap, x - 15, y - 15, paint);
                }
            }
        }
        catch (Exception ex)
        {
            //             
        }
    }

    /**
     *   X軸に表示するスケールの文字列を応答する
     * 
     * 
     * @param scale
     * @param range
     * @param position
     * @return
     */
    private String decideXaxisLabel(int scale, int range, int position)
    {
        String retValue = "";
        if (scale == 0)
        {
            switch (position)
            {
              case 5:
                retValue = "20";
                break;

              case 4:
                retValue = "16";
                break;
                      
              case 3:
                retValue = "12";
                break;

              case 2:
                retValue = "08";
                break;

              case 1:
                retValue = "04";
                break;
            
              case 0:
              default:
                retValue = "00";
                break;
            }
            return (retValue);
        }
        if (scale == 1)
        {
            if (range == 1)
            {
                switch (position)
                {
                  case 5:
                    retValue = "22";
                    break;

                  case 4:
                    retValue = "20";
                    break;
                          
                  case 3:
                    retValue = "18";
                    break;

                  case 2:
                    retValue = "16";
                    break;

                  case 1:
                    retValue = "14";
                    break;
                
                  case 0:
                  default:
                    retValue = "12";
                    break;
                }
            }
            else
            {
                switch (position)
                {
                  case 5:
                    retValue = "10";
                    break;

                  case 4:
                    retValue = "08";
                    break;
                          
                  case 3:
                    retValue = "06";
                    break;

                  case 2:
                    retValue = "04";
                    break;

                  case 1:
                    retValue = "02";
                    break;
                
                  case 0:
                  default:
                    retValue = "00";
                    break;
                }
            }
            return (retValue);
        }

        /* if (scale == 2) */
        if (range == 3)
        {
            switch (position)
            {
              case 5:
                retValue = "23";
                break;

              case 4:
                retValue = "22";
                break;
                      
              case 3:
                retValue = "21";
                break;

              case 2:
                retValue = "20";
                break;

              case 1:
                retValue = "19";
                break;
            
              case 0:
              default:
                retValue = "18";
                break;
            }
        }
        else if (range == 2)
        {
            switch (position)
            {
              case 5:
                retValue = "17";
                break;

              case 4:
                retValue = "16";
                break;
                      
              case 3:
                retValue = "15";
                break;

              case 2:
                retValue = "14";
                break;

              case 1:
                retValue = "13";
                break;
            
              case 0:
              default:
                retValue = "12";
                break;
            }
        }
        else if (range == 1)
        {
            switch (position)
            {
              case 5:
                retValue = "11";
                break;

              case 4:
                retValue = "10";
                break;
                      
              case 3:
                retValue = "09";
                break;

              case 2:
                retValue = "08";
                break;

              case 1:
                retValue = "07";
                break;
            
              case 0:
              default:
                retValue = "06";
                break;
            }
        }
        else
        {
            switch (position)
            {
              case 5:
                retValue = "05";
                break;

              case 4:
                retValue = "04";
                break;
                      
              case 3:
                retValue = "03";
                break;

              case 2:
                retValue = "02";
                break;

              case 1:
                retValue = "01";
                break;
            
              case 0:
              default:
                retValue = "00";
                break;
            }
        }
        return (retValue);
    }


    float decideStartSeconds(int scale, int range)
    {
        if (scale == 0)
        {
            return (0);
        }
        else if (scale == 1)
        {
            if (range == 1)
            {
                return (12 * 60 * 60);
            }
            else  // if (range == 0)
            {
                return (0);
            }
        }
        // else if (scale == 2)
        
        if (range == 3)
        {
            return (18 * 60 * 60);            
        }        
        else if (range == 2)
        {
            return (12 * 60 * 60);
        }
        
        else if (range == 1)
        {
            return (6 * 60 * 60);
        }
        return (0);
    }

    float decideEndSeconds(int scale, int range)
    {
        if (scale == 0)
        {
            return (24 * 60 * 60);
        }
        else if (scale == 1)
        {
            if (range == 1)
            {
                return (24 * 60 * 60);
            }
            else  // if (range == 0)
            {
                return (12 * 60 * 60);
            }
        }
        // else if (scale == 2)
        
        if (range == 3)
        {
            return (24 * 60 * 60);            
        }        
        else if (range == 2)
        {
            return (18 * 60 * 60);
        }
        
        else if (range == 1)
        {
            return (12 * 60 * 60);
        }
        return (6 * 60 * 60);
    }     
}
