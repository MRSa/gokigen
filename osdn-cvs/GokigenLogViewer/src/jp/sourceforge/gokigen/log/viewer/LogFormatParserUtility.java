package jp.sourceforge.gokigen.log.viewer;

public class LogFormatParserUtility
{
    /**
     *  アイコンのリソースを応答する
     * 
     * @param content データ
     * @return アイコンのリソースID
     */
    static public int parseIconResource(String content)
    {
    	int iconId = 0;
    	int index = content.indexOf("/");
    	if (index <= 0)
    	{
    		// "/" の場所取得失敗
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
     *  サブアイコンのリソースを応答する
     * 
     * @param content データ
     * @return サブアイコンのリソースID
     */
    static public int parseSubIconResource(String content)
    {
    	return (0);
    }

    /**
     *   3番目のデータ部分を解析し応答する
     * @param content  データ
     * @return 3番目のデータ部分（文字列）
     */
    static public String parseTextResource3rd(String content)
    {
    	return (content);
    }
}
