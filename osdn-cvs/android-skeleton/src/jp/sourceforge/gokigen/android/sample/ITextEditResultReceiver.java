package jp.sourceforge.gokigen.android.sample;

public interface ITextEditResultReceiver
{
    public abstract boolean finishTextEditDialog(String message);
    public abstract boolean cancelTextEditDialog();
}
