package jp.sourceforge.gokigen.android.sample;

/**
 *   タイマ監視用のインタフェース
 * @author MRSa
 */
public interface ITimerReceiver
{
    /**  タイムアウト検出 **/
    public abstract boolean receiveTimeout();

    /**  タイマーの監視開始 **/
    public abstract void timerStarted();

    /**  タイマーの監視終了 **/
    public abstract void timerStopped(String reason);
}
