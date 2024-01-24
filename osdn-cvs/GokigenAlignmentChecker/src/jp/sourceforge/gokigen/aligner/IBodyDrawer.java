package jp.sourceforge.gokigen.aligner;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface IBodyDrawer
{
    public abstract boolean onTouchEvent(MotionEvent event);
    public abstract boolean onTrackballEvent(MotionEvent event);
    public abstract void storePosition();

    /** 表示するメッセージを設定する **/
    public abstract void setMessage(String message);

    /** 描画実施 **/
    public abstract void drawOnCanvas(Canvas canvas, int reportType);
 
    /** 操作をリセットする **/
    public abstract void reset();
    
    /** ひとつ操作を戻す **/
    public abstract void undo();
    
 }
