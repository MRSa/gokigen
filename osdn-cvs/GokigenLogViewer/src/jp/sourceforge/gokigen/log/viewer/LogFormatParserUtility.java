package jp.sourceforge.gokigen.log.viewer;

public class LogFormatParserUtility
{
    /**
     *  �A�C�R���̃��\�[�X����������
     * 
     * @param content �f�[�^
     * @return �A�C�R���̃��\�[�XID
     */
    static public int parseIconResource(String content)
    {
    	int iconId = 0;
    	int index = content.indexOf("/");
    	if (index <= 0)
    	{
    		// "/" �̏ꏊ�擾���s
    		return (0);
    	}
    	String data = content.substring((index - 1), index);
    	if (data.length() > 0)
    	{
    		switch (data.getBytes()[0])
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
    	}
    	return (iconId);
    }

    /**
     *  �T�u�A�C�R���̃��\�[�X����������
     * 
     * @param content �f�[�^
     * @return �T�u�A�C�R���̃��\�[�XID
     */
    static public int parseSubIconResource(String content)
    {
    	return (0);
    }

    /**
     *   3�Ԗڂ̃f�[�^��������͂���������
     * @param content  �f�[�^
     * @return 3�Ԗڂ̃f�[�^�����i������j
     */
    static public String parseTextResource3rd(String content)
    {
    	return (content);
    }
}
