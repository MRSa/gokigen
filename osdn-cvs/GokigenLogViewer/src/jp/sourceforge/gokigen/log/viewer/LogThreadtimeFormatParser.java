package jp.sourceforge.gokigen.log.viewer;

public class LogThreadtimeFormatParser implements SymbolListArrayItem.ItemParser
{
    /**
     *  �A�C�R���̃��\�[�X����������
     * 
     * @param content �f�[�^
     * @return �A�C�R���̃��\�[�XID
     */
    public int parseIconResource(String content)
    {
    	return (0);
    }

    /**
     *  �T�u�A�C�R���̃��\�[�X����������
     * 
     * @param content �f�[�^
     * @return �T�u�A�C�R���̃��\�[�XID
     */
    public int parseSubIconResource(String content)
    {
    	return (0);
    }
    
    /**
     *   1�Ԗڂ̃f�[�^��������͂���������
     * 
     * @param content �f�[�^
     * @return 1�Ԗڂ̃f�[�^�����i������j
     */
    public String parseTextResource1st(String content)
    {
    	int index = content.indexOf(".");
    	if (index <= 0)
    	{
    		// "." �̏ꏊ�擾���s
    		return ("");
    	}
    	return (content.substring(0, index));
    }
    
    /**
     *   2�Ԗڂ̃f�[�^��������͂���������
     * 
     * @param content �f�[�^
     * @return 2�Ԗڂ̃f�[�^�����i������j
     */
    public String parseTextResource2nd(String content)
    {
    	int index = content.indexOf(".");
    	if (index <= 0)
    	{
    		// "/" �̏ꏊ�擾���s
    		return (content);
    	}
    	int index2 = content.indexOf(" ", index);
    	if (index2 <= 0)
    	{
    		// "/" �̏ꏊ�擾���s
    		return (content);
    	}
    	return (content.substring(index2 + 1));
    }

    /**
     *   3�Ԗڂ̃f�[�^��������͂���������
     * @param content  �f�[�^
     * @return 3�Ԗڂ̃f�[�^�����i������j
     */
    public String parseTextResource3rd(String content)
    {
    	return (LogFormatParserUtility.parseTextResource3rd(content));
    }
}
