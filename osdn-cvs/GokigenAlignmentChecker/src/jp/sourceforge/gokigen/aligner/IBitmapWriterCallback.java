package jp.sourceforge.gokigen.aligner;

/**
 * 
 * 
 * @author MRSa
 *
 */
public interface IBitmapWriterCallback
{
	public abstract void onProgressUpdate();
    public abstract void finishedWrite(boolean result);
}
