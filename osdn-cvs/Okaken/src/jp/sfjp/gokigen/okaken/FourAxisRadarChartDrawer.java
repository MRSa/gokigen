package jp.sfjp.gokigen.okaken;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class FourAxisRadarChartDrawer
{

    /**
     *     レーダーチャートの下の線を引く
     * 
     * @param canvas
     * @param centerX
     * @param centerY
     * @param radius
     * @param margin
     * @param paint
     */
    public static void drawRadarChartBase(Canvas canvas, float centerX, float centerY, float radius, float margin, Paint paint)
    {
        // まずは円
        canvas.drawCircle(centerX, centerY, radius, paint);
    	
        // 目盛線の描画（X軸)
        canvas.drawLine((centerX - radius - margin), centerY, centerX + radius + margin, centerY, paint);
        for (float part = 0.0f; part < 1.0f; part = part + 0.25f)
        {
        	canvas.drawLine((centerX - radius * part), (centerY - margin), (centerX - radius * part), (centerY + margin), paint);
        	canvas.drawLine((centerX + radius * part), (centerY - margin), (centerX + radius * part), (centerY + margin), paint);
        }

        // 目盛線の描画（Y軸)
        canvas.drawLine(centerX, (centerY - radius - margin), centerX, (centerY + radius + margin), paint);
        for (float part = 0.0f; part < 1.0f; part = part + 0.25f)
        {
        	canvas.drawLine((centerX - margin), centerY - radius * part, (centerX + margin), centerY - radius * part, paint);
        	canvas.drawLine((centerX - margin), centerY + radius * part, (centerX + margin), centerY + radius * part, paint);
        }
    	
    }

    public static void drawRadarAxisIcons(Canvas canvas, float centerX, float centerY, float radius, Bitmap cat1, Bitmap cat2, Bitmap cat3, Bitmap cat4)
    {
    	float marginX = 3.0f;
    	float marginY = 3.0f;
    	Paint paint = new Paint();
        canvas.drawBitmap(cat1, centerX + marginX, centerY - radius - cat1.getHeight(), paint);
        canvas.drawBitmap(cat2, centerX + radius + marginX, centerY + marginY, paint);
        canvas.drawBitmap(cat3, centerX - cat3.getWidth() - marginX, centerY + radius + marginY, paint);
        canvas.drawBitmap(cat4, centerX - radius - cat3.getWidth(), centerY - cat4.getHeight() - marginY, paint);
    	
    }
    
    
    /**
     *    レーダーチャートの線を引く
     * 
     * @param canvas
     * @param centerX
     * @param centerY
     * @param radius
     * @param cat1
     * @param cat2
     * @param cat3
     * @param cat4
     */
    public static void drawRadarChartLine(Canvas canvas, float centerX, float centerY, float radius, float cat1, float cat2, float cat3, float cat4)
    {
        Paint linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
		linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5.0f);   // ちょっと太めの線にする
    	Path path = new Path();
        path.moveTo(centerX, centerY - radius * cat1);  // 軸１
        path.lineTo(centerX + radius * cat2, centerY);    // 軸２
        path.lineTo(centerX, centerY + radius * cat3);    // 軸３
        path.lineTo(centerX - radius * cat4, centerY);     // 軸４
        path.lineTo(centerX, centerY - radius * cat1);     // 軸1
        canvas.drawPath(path, linePaint);
    }
    
	
	
}
