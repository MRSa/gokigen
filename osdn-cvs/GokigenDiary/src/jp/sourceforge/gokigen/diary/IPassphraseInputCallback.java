package jp.sourceforge.gokigen.diary;

public interface IPassphraseInputCallback
{
	/** ƒf[ƒ^‚ğ“ü—Í‚µ‚½ **/
    public abstract boolean inputPassphraseFinished(String data);

    /** “ü—Í‚ğ’†~‚µ‚½... **/
    public abstract void inputPassphraseCanceled();
}
