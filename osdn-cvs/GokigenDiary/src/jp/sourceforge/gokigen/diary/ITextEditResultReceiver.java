package jp.sourceforge.gokigen.diary;

public interface ITextEditResultReceiver
{
    public abstract boolean finishTextEditDialog(String message);
    public abstract boolean cancelTextEditDialog();
}
