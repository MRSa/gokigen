package jp.sourceforge.gokigen.sound.play;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.media.audiofx.Visualizer;
import android.util.Log;
import android.view.MotionEvent;

public class SoundVisualizerListener implements Visualizer.OnDataCaptureListener, GokigenSurfaceView.ICanvasDrawer
{
//    private Activity parent = null;  // 親分
    private GokigenSurfaceView view = null;
    
    private byte[] fftBuffer = null;
    private byte[] waveBuffer = null;

    private int samplingRate = 0;
    private float maxValue = 0;
    
	private int backgroundColorRed = 0x00;
	private  int backgroundColorGreen = 0x40;
	private  int backgroundColorBlue = 0x00;

	private Paint painter = new Paint();
	
    public SoundVisualizerListener(Activity  argument)
    {
    	//parent = argument;
    	
    	painter.setAntiAlias(true);
    	painter.setStrokeWidth(2.0f);
        painter.setColor(Color.WHITE);
    	painter.setStyle(Paint.Style.STROKE);
     }
   
    public void setGokigenSurfaceView(GokigenSurfaceView surfaceView)
    {
       	view = surfaceView;
       	view.doDraw();
    }
    
	/** 画面描画メイン **/
    public void drawOnCanvas(Canvas canvas)
    {
    	//Log.v(Main.APP_IDENTIFIER, "SoundVisualizerListener::drawOnCanvas()");
    	try
    	{
    		// 画面全体を塗りつぶし
    		canvas.drawColor(Color.rgb(backgroundColorRed, backgroundColorGreen, backgroundColorBlue));

    		if ((fftBuffer == null)&&(waveBuffer == null))
    		{
    		    return;
    		}
    		
    		if (fftBuffer != null)
    		{
    			drawFft(canvas);
    		}
    		if (waveBuffer != null)
    		{
    			drawWave(canvas);
    		}
    		
            // オブジェクトをすべて表示
    		drawObjects(canvas);
    	}
    	catch (Exception ex)
    	{
    		// 例外発生...でもそのときには何もしない
    		Log.v(Main.APP_IDENTIFIER, "drawOnCanvas() ex: " + ex.toString());
    	}
    	
    }
	/** 画面描画メイン **/
    public void drawFft(Canvas canvas)
    {
        // Log.v(Main.APP_IDENTIFIER, "SoundVisualizerListener::drawObjects()");	
        int offset, counter;
    	float width, height, center;
    	float max, min, maxPosition;
    	float x1, y1, x2, y2;

    	width = (canvas.getWidth() / ((float) (fftBuffer.length - 2) / (2 * 1)));
        center = (canvas.getHeight() / (float) 2);
        height = 1; // (canvas.getHeight() / (float) 256 * 2 / 3);// * (-1);

        painter.setColor(Color.DKGRAY);
        canvas.drawLine(0, center, canvas.getWidth(), center, painter);
        
        y1 = 0;
        y2 = 0;
        max = 0;
        min = 0;
        offset = 1;
        counter = 0;
        painter.setColor(Color.WHITE);
        for (int index = 2; index < (fftBuffer.length - 1); index = index + 2 * offset, counter = counter + 2)
        {
            x1 = (counter) * width;
    	    y1 = (float) Math.sqrt(((double) fftBuffer[index + 0] * (double) fftBuffer[index + 0]) + ((double) fftBuffer[index + 1] * fftBuffer[index + 1])) * height;

            x2 = (counter + 1) * width;
            y2 = (float) Math.sqrt(((double) fftBuffer[index + 2] * (double) fftBuffer[index + 2]) + ((double) fftBuffer[index + 3] * fftBuffer[index + 3])) * height;

            if (y1 > max)
            {
            	max = y1;
            }
            if (y2 > max)
            {
            	max = y2;
            }
            if (y1 < min)
            {
            	min = y1;
            }
            if (y2 < min)
            {
            	min = y2;
            }
            if (max > maxValue)
            {
            	maxValue = max;
            }

            y1 = y1 * (-1) + center;
            y2 = y2 * (-1) + center;
        	canvas.drawLine(x1, y1, x2, y2, painter);
            
            if ((index + 4 * offset) >= (fftBuffer.length - 1))
            {
            	break;
            }
        }
        painter.setTextSize(16);
        painter.setColor(Color.YELLOW);
        String msg = "Rate : " + samplingRate + " max: " + max + " (TOP : " + maxValue + ")";
        canvas.drawText(msg, 10, 30, painter);
        painter.setStyle(Style.FILL);
        painter.setColor(Color.DKGRAY);
        canvas.drawRect(0, 50, canvas.getWidth(), 70, painter);
        float bandWidth = (max / 256)  * canvas.getWidth();
        painter.setColor(Color.BLUE);
        canvas.drawRect(0, 50, bandWidth, 70, painter);
        painter.setColor(Color.RED);
        maxPosition = maxValue / 256 * canvas.getWidth();
        canvas.drawLine(maxPosition, 50, maxPosition, 70, painter);
        painter.setColor(Color.WHITE);
    }

    /** 画面描画メイン **/
    public void drawWave(Canvas canvas)
    {
        float x1, y1, x2, y2;
        float width, height, center;
        int offset = 4;
    	
    	// Log.v(Main.APP_IDENTIFIER, "SoundVisualizerListener::drawObjects()");	
        width = canvas.getWidth() / (float) waveBuffer.length;
        center = (canvas.getHeight() / (float) 2);
//        height = (canvas.getHeight() / (float) 256 * 2 / 3);// * (-1);
        height = 1;
        painter.setColor(Color.DKGRAY);
        canvas.drawLine(0, center, canvas.getWidth(), center, painter);

        painter.setColor(Color.WHITE);
        for (int index = 0; index < (waveBuffer.length - 1); index = index + offset)
        {
        	x1 = index * width;
        	y1 = (waveBuffer[index]) * height;

        	x2 = (index + offset) * width;
        	y2 = (waveBuffer[index + offset]) * height;
        	
        	canvas.drawLine(x1, y1 + center, x2, y2 + center, painter);
            if ((index +offset * 2) >= (waveBuffer.length - 1))
            {
            	break;
            }
        }
        painter.setTextSize(16);
        canvas.drawText("Rate : " + samplingRate, 10, 30, painter);
    }

	/** 画面描画メイン **/
    public void drawObjects(Canvas canvas)
    {
        // Log.v(Main.APP_IDENTIFIER, "SoundVisualizerListener::drawObjects()");	
    }
    
    /** タッチされた時の処理 **/
    public boolean onTouchEvent(MotionEvent event)
    {
        Log.v(Main.APP_IDENTIFIER, "SoundVisualizerListener::onTouchEvent()");	
    	return (false);
    }
    
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate)
    {
        Log.v(Main.APP_IDENTIFIER, "SoundVisualizerListener::onFftDataCapture() : " + fft.length);	
        fftBuffer = fft;
        this.samplingRate = samplingRate;
        
        /** 受信データを表示する **/
        if (view != null)
        {
    	    view.doDraw();
        }
    }
    
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate)
    {
        Log.v(Main.APP_IDENTIFIER, "SoundVisualizerListener::onWaveFormDataCapture() : " + waveform.length);
        waveBuffer = waveform;
        this.samplingRate = samplingRate;
        
        /** 受信データを表示する **/
        if (view != null)
        {
    	    view.doDraw();
        }
    }

}
