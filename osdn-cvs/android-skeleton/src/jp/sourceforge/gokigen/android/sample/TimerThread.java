package jp.sourceforge.gokigen.android.sample;


/**
 *  タイマ監視スレッド
 *  (一定周期でITimerReceiverにタイムアウト通知を行う)
 * 
 */
public class TimerThread extends Thread
{
    ITimerReceiver  mClient = null;
    boolean        mContinue = true;
    long           mDuration = 5 * 60 * 1000;

    /**
     *  コンストラクタ
     *
     * @param client   報告先
     * @param duration 報告間隔 (単位： ms)
     */
    public TimerThread(ITimerReceiver client, long duration)
    {
        mClient = client;
        mDuration = duration;
    }

    /**
     *   タイマ監視メイン
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
     *  クライアントから監視を停止させる
     */    
    public void stopWatchdog()
    {
        mContinue = false;
    }
}
