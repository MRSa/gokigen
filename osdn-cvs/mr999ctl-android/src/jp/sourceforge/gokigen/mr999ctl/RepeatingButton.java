package jp.sourceforge.gokigen.mr999ctl;

import java.util.TimerTask;
import android.widget.Button;
import android.view.MotionEvent;
import android.content.Context;
import android.util.AttributeSet;

/**
 *   ���s�[�g���E�����߂̃{�^��
 *   
 *   �ȉ��̃T�C�g���Q�l�ɁA�쐬���Ă݂��B(�Ƃ����Ă��A�قڂ��̂܂�...)
 *    �� http://blog.spleenware.com/2009/09/auto-repeat-buttons-in-android.html
 *    
 *   �� ���������ǁA�A�A����͎g��Ȃ��ł��݂����B���Ԃ�B
 *    
 * @author MRSa
 *
 */
public class RepeatingButton extends Button
{
    public RepeatingButton(Context c)
    {
        super(c);
    }
    
    public RepeatingButton(Context c, AttributeSet attrs)
    {
        super(c, attrs);
    }

    private static final long INITIAL_DELAY = 100;
    private static final long REPEAT_INTERVAL = 100;
    private TimerTask mTask = new TimerTask()
    {
        @Override
        public void run()
        {
            if (isPressed())
            {
                performClick();  // �N���b�N���ꂽ���Ƃ��V�~�����[�g����B
                postDelayed(this, REPEAT_INTERVAL);
            }
        }        
    };

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            // ����̃A�N�V����...
            postDelayed(mTask, INITIAL_DELAY);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            removeCallbacks(mTask);
        }
        return (super.onTouchEvent(event));
    }   
}
