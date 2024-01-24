package jp.sourceforge.gokigen.diary;

/**
 *  ジオコーディング結果を応答するインタフェース
 * 
 * @author MRSa
 *
 */
public interface IGeocoderResultReceiver
{
	/**
	 *  ジオコーディング結果を渡す
	 * 
	 *   @param resultString ジオコーディング結果
	 **/
    public abstract void receivedResult(MyLocation resultString);
}
