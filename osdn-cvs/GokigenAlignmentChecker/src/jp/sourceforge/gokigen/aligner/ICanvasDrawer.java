package jp.sourceforge.gokigen.aligner;

import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public interface ICanvasDrawer
{	
    public abstract void drawOnCanvas(Canvas canvas);
    
    public abstract boolean onTouchEvent(MotionEvent event);
    
    public abstract boolean onTrackballEvent(MotionEvent event);

    public abstract boolean onKeyDown(int keyCode, KeyEvent event);

}
