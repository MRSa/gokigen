package jp.sourceforge.gokigen.diary;

public interface IPassphraseInputCallback
{
	/** �f�[�^����͂��� **/
    public abstract boolean inputPassphraseFinished(String data);

    /** ���͂𒆎~����... **/
    public abstract void inputPassphraseCanceled();
}
