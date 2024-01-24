package jp.sfjp.gokigen.okaken;

import java.util.TimerTask;
import android.os.Handler;

/**
 *    ClockTimer : タイムアウトを待つ
 * 
 * @author MRSa
 *
 */
public class ClockTimer extends TimerTask
{
    private Handler handler = null;
    private ITimeoutReceiver receiver = null;
	
    /**
     *    コンストラクタ
     * 
     * @param context
     */
    public ClockTimer(ITimeoutReceiver receiver) 
	{
	    handler = new Handler();
        this.receiver = receiver;
	}

	@Override
	public void run()
	{
        handler.post(new Runnable()
        {
	        @Override
	        public void run()
	        {
	        	if (receiver != null)
	        	{
	        		receiver.receivedTimeout();
	        	}
	        }
	      });
	}

	/**
	 *  
	 * @author MRSa
	 *
	 */
	public interface ITimeoutReceiver
	{
        /** タイムアウト発生 **/
        public abstract void receivedTimeout();		
	}
}
