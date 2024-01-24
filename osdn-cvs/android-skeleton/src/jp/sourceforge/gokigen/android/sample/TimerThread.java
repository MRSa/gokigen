package jp.sourceforge.gokigen.android.sample;


/**
 *  �^�C�}�Ď��X���b�h
 *  (��������ITimerReceiver�Ƀ^�C���A�E�g�ʒm���s��)
 * 
 */
public class TimerThread extends Thread
{
    ITimerReceiver  mClient = null;
    boolean        mContinue = true;
    long           mDuration = 5 * 60 * 1000;

    /**
     *  �R���X�g���N�^
     *
     * @param client   �񍐐�
     * @param duration �񍐊Ԋu (�P�ʁF ms)
     */
    public TimerThread(ITimerReceiver client, long duration)
    {
        mClient = client;
        mDuration = duration;
    }

    /**
     *   �^�C�}�Ď����C��
     * 
     */
    public void run()
    {
        String message = "";
        try
        {
            mClient.timerStarted();
            while (mContinue == true)
            {
                Thread.sleep(mDuration);
                mContinue = mClient.receiveTimeout();
            }
            message = "Continuous flag is down.";
        }
        catch (Exception ex)
        {
            message = ex.getMessage();
        }
        mClient.timerStopped(message);
    }

    /**
     *  �N���C�A���g����Ď����~������
     */    
    public void stopWatchdog()
    {
        mContinue = false;
    }
}
