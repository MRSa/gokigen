package jp.sourceforge.gokigen.log.viewer;

public class LogThreadtimeFormatParser implements SymbolListArrayItem.ItemParser
{
    /**
     *  アイコンのリソースを応答する
     * 
     * @param content データ
     * @return アイコンのリソースID
     */
    public int parseIconResource(String content)
    {
    	return (0);
    }

    /**
     *  サブアイコンのリソースを応答する
     * 
     * @param content データ
     * @return サブアイコンのリソースID
     */
    public int parseSubIconResource(String content)
    {
    	return (0);
    }
    
    /**
     *   1番目のデータ部分を解析し応答する
     * 
     * @param content データ
     * @return 1番目のデータ部分（文字列）
     */
    public String parseTextResource1st(String content)
    {
    	int index = content.indexOf(".");
    	if (index <= 0)
    	{
    		// "." の場所取得失敗
    		return ("");
    	}
    	return (content.substring(0, index));
    }
    
    /**
     *   2番目のデータ部分を解析し応答する
     * 
     * @param content データ
     * @return 2番目のデータ部分（文字列）
     */
    public String parseTextResource2nd(String content)
    {
    	int index = content.indexOf(".");
    	if (index <= 0)
    	{
    		// "/" の場所取得失敗
    		return (content);
    	}
    	int index2 = content.indexOf(" ", index);
    	if (index2 <= 0)
    	{
    		// "/" の場所取得失敗
    		return (content);
    	}
    	return (content.substring(index2 + 1));
    }

    /**
     *   3番目のデータ部分を解析し応答する
     * @param content  データ
     * @return 3番目のデータ部分（文字列）
     */
    public String parseTextResource3rd(String content)
    {
    	return (LogFormatParserUtility.parseTextResource3rd(content));
    }
}
