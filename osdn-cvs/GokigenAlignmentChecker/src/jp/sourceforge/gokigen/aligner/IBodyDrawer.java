package jp.sourceforge.gokigen.aligner;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface IBodyDrawer
{
    public abstract boolean onTouchEvent(MotionEvent event);
    public abstract boolean onTrackballEvent(MotionEvent event);
    public abstract void storePosition();

    /** �\�����郁�b�Z�[�W��ݒ肷�� **/
    public abstract void setMessage(String message);

    /** �`����{ **/
    public abstract void drawOnCanvas(Canvas canvas, int reportType);
 
    /** ��������Z�b�g���� **/
    public abstract void reset();
    
    /** �ЂƂ����߂� **/
    public abstract void undo();
    
 }
