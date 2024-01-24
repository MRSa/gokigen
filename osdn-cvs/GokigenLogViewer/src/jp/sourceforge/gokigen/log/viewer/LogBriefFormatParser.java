package jp.sourceforge.gokigen.log.viewer;

public class LogBriefFormatParser implements SymbolListArrayItem.ItemParser
{
    /**
     *  アイコンのリソースを応答する
     * 
     * @param content データ
     * @return アイコンのリソースID
     */
    public int parseIconResource(String content)
    {
    	return (LogFormatParserUtility.parseIconResource(content));
    }

    /**
     *  サブアイコンのリソースを応答する
     * 
     * @param content データ
     * @return サブアイコンのリソースID
     */
    public int parseSubIconResource(String content)
    {
    	return (LogFormatParserUtility.parseSubIconResource(content));
    }
    
    /**
     *   1番目のデータ部分を解析し応答する
     * 
     * @param content データ
     * @return 1番目のデータ部分（文字列）
     */
    public String parseTextResource1st(String content)
    {
    	return ("");
    }
    
    /**
     *   2番目のデータ部分を解析し応答する
     * 
     * @param content データ
     * @return 2番目のデータ部分（文字列）
     */
    public String parseTextResource2nd(String content)
    {
    	return (content.substring(2));
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
