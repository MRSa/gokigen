package jp.sourceforge.gokigen.viewsensor;

/**
 *
 *
 */
public class SymbolListArrayItem
{
    private int mIconId = 0;
    private String mDataLabel = "";
    private String mDataValue = "";

    /**
     *  コンストラクタ
     * @param resId リソースID
     * @param label ラベル
     * @param value 設定値
     */
    public SymbolListArrayItem(int resId, String label, String value)
    {
        mIconId = resId;
        mDataLabel = label;
        mDataValue = value;
    }

    public int getIconId()
    {
        return (mIconId);
    }

    public void setIconId(int iconId)
    {
        mIconId = iconId;
    }

    public String getLabel()
    {
        return (mDataLabel);
    }

    public void setLabel(String textData)
    {
        mDataLabel = textData;
    }

    public String getValue()
    {
        return (mDataValue);
    }

    public void setValue(String textData)
    {
        mDataValue = textData;
    }

}
