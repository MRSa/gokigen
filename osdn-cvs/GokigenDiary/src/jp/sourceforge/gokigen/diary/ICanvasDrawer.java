package jp.sourceforge.gokigen.diary;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface ICanvasDrawer
{	
    public abstract void drawOnCanvas(Canvas canvas);
    
    public abstract boolean onTouchEvent(MotionEvent event);

}
