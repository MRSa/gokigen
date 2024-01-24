package jp.sfjp.gokigen.okaken;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface ICanvasDrawer
{	
    public abstract void prepareToStart(int width, int height);
    public abstract void changedScreenProperty(int format, int width, int height);

    public abstract void drawOnCanvas(Canvas canvas);
    
    public abstract boolean onTouchEvent(MotionEvent event);

}
