package jp.sourceforge.gokigen.log.viewer;

public interface ITextEditResultReceiver
{
    public abstract boolean finishTextEditDialog(String message);
    public abstract boolean cancelTextEditDialog();
}
