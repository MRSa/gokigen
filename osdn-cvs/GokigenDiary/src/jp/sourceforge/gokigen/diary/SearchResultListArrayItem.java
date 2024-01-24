package jp.sourceforge.gokigen.diary;

/**
 *  検索結果の１アイテムを格納するクラス
 * 
 * @author MRSa
 *
 */
public class SearchResultListArrayItem
{
    private int iconResource = 0;
    private String dateText = "";
    private String contentText = "";
    private String referenceText = "";

    /**
     *  コンストラクタ
     * @param iconId
     * @param textId
     */
    public SearchResultListArrayItem(String date, int iconId, String content, String reference)
    {
    	dateText = date;
    	iconResource = iconId;
    	contentText = content;
    	referenceText = reference;
    }
    
    public int getIconId()
    {
        return (iconResource);
    }

    public String getDate()
    {
    	return (dateText);
    }

    public String getContent()
    {
    	return (contentText);
    }
    
    public String getReference()
    {
    	return (referenceText);
    }
}
