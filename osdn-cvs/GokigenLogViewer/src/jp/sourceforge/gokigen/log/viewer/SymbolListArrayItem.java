package jp.sourceforge.gokigen.log.viewer;

public class SymbolListArrayItem
{
    private int iconResource = 0;
    private int subIconResource = 0;
    private String textResource1st = "";
    private String textResource2nd = "";
    private String textResource3rd = "";

    /**
     *  �R���X�g���N�^
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

    /**
     *  �o�^����A�C�e���̃p�[�T�i�C���^�t�F�[�X�j
     * 
     * @author MRSa
     *
     */
    public interface ItemParser
    {
        /**
         *  �f�[�^�ɑΉ������A�C�R�����\�[�X����������
         * 
         * @param content �f�[�^
         * @return �A�C�R���̃��\�[�XID
         */
        public abstract int parseIconResource(String content);

        /**
         *  �f�[�^�ɑΉ������T�u�A�C�R�����\�[�X����������
         * 
         * @param content �f�[�^
         * @return �A�C�R���̃T�u���\�[�XID
         */
        public abstract int parseSubIconResource(String content);
        
        /**
         *   1�Ԗڂ̃f�[�^��������͂���������
         * 
         * @param content �f�[�^
         * @return 1�Ԗڂ̃f�[�^�����i������j
         */
        public abstract String parseTextResource1st(String content);
        
        /**
         *   2�Ԗڂ̃f�[�^��������͂���������
         * 
         * @param content �f�[�^
         * @return 2�Ԗڂ̃f�[�^�����i������j
         */
        public abstract String parseTextResource2nd(String content);

        /**
         *   3�Ԗڂ̃f�[�^��������͂���������
         * @param content  �f�[�^
         * @return 3�Ԗڂ̃f�[�^�����i������j
         */
        public abstract String parseTextResource3rd(String content);
    }
}
