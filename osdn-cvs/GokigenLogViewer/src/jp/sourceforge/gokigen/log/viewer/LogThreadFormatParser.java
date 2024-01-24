package jp.sourceforge.gokigen.log.viewer;

public class LogThreadFormatParser implements SymbolListArrayItem.ItemParser
{
    /**
     *  �A�C�R���̃��\�[�X����������
     * 
     * @param content �f�[�^
     * @return �A�C�R���̃��\�[�XID
     */
    public int parseIconResource(String content)
    {
        int iconId = 0;
		switch (content.getBytes()[0])
		{
		  case 'E':
	        iconId = R.drawable.e;
			break;
		  
	      case 'W':
			iconId = R.drawable.w;
			break;
		  
	      case 'I':
			iconId = R.drawable.i;
			break;
		  
	      case 'D':
			iconId = R.drawable.d;
		    break;
		  
		  case 'V':
			iconId = R.drawable.v;
			break;
		  
		  default:
	        iconId = R.drawable.warning;
	        break;    		    
		}
	    return (iconId);
    }

    /**
     *  �T�u�A�C�R���̃��\�[�X����������
     * 
     * @param content �f�[�^
     * @return �T�u�A�C�R���̃��\�[�XID
     */
    public int parseSubIconResource(String content)
    {
    	return (LogFormatParserUtility.parseSubIconResource(content));
    }
    
    /**
     *   1�Ԗڂ̃f�[�^��������͂���������
     * 
     * @param content �f�[�^
     * @return 1�Ԗڂ̃f�[�^�����i������j
     */
    public String parseTextResource1st(String content)
    {
    	int top = content.indexOf(":");
    	int last = content.indexOf(")");
    	return (content.substring((top + 1), last));
    }
    
    /**
     *   2�Ԗڂ̃f�[�^��������͂���������
     * 
     * @param content �f�[�^
     * @return 2�Ԗڂ̃f�[�^�����i������j
     */
    public String parseTextResource2nd(String content)
    {
    	int top = content.indexOf(")");
    	return (content.substring((top + 1)));
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
