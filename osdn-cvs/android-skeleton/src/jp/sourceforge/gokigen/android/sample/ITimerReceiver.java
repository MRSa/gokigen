package jp.sourceforge.gokigen.android.sample;

/**
 *   �^�C�}�Ď��p�̃C���^�t�F�[�X
 * @author MRSa
 */
public interface ITimerReceiver
{
    /**  �^�C���A�E�g���o **/
    public abstract boolean receiveTimeout();

    /**  �^�C�}�[�̊Ď��J�n **/
    public abstract void timerStarted();

    /**  �^�C�}�[�̊Ď��I�� **/
    public abstract void timerStopped(String reason);
}
