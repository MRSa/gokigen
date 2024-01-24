package jp.sourceforge.gokigen.android.sample;

public class SymbolListArrayItem
{
    private int iconResource = 0;
    private int subIconResource = 0;
    private String textResource1st = "";
    private String textResource2nd = "";
    private String textResource3rd = "";

    /**
     *  コンストラクタ
     * @param iconId
     * @param textId
     */
    public SymbolListArrayItem(int iconId1, String textData1, String textData2, String textData3, int iconId2)
    {
        iconResource = iconId1;
        subIconResource = iconId2;
        textResource1st = textData1;
        textResource2nd = textData2;
        textResource3rd = textData3;
    }
    
    public int getIconResource()
    {
        return (iconResource);
    }
    
    public int getSubIconResource()
    {
        return (subIconResource);
    }

    public void setIconResource(int iconId)
    {
        iconResource = iconId;
    }

    public void setSubIconResource(int iconId)
    {
        subIconResource = iconId;
    }

    public String getTextResource1st()
    {
        return (textResource1st);
    }

    public void setTextResource1st(String textData)
    {
        textResource1st = textData;
    }    

    public String getTextResource2nd()
    {
        return (textResource2nd);
    }

    public void setTextResource2nd(String textData)
    {
        textResource2nd = textData;
    }
    
    public String getTextResource3rd()
    {
        return (textResource3rd);
    }
    
    public void setTextResource3rd(String textData)
    {
        textResource3rd = textData;
    }
}
