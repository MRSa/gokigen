package jp.sourceforge.gokigen.memoma;

import android.content.Context;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class ObjectOperationCommandHolder implements ItemSelectionDialog.ISelectionItemHolder
{
    public static final int OBJECTOPERATION_DELETE = 0;
    public static final int OBJECTOPERATION_DUPLICATE =1;
    public static final int OBJECTOPERATION_SIZEBIGGER = 2;
    public static final int OBJECTOPERATION_SIZESMALLER = 3;
//    public static final int OBJECTOPERATION_BRINGTOP = 4;
	
	private Context parent = null;
	
	/**
	 *    �R���X�g���N�^
	 * 
	 */
    public ObjectOperationCommandHolder(Context context)
    {
    	parent = context;
    }	

    /**
     * 
     * 
     */
    public boolean isMultipleSelection()
	{
		return (false);
		
	}

    /**
     * 
     * 
     */
	public String[] getItems()
	{
        String[] ret = new String[4];
        ret[0] = parent.getString(R.string.object_delete);
        ret[1] = parent.getString(R.string.object_duplicate);
        ret[2] = parent.getString(R.string.object_bigger);
        ret[3] = parent.getString(R.string.object_smaller);
        //ret[4] = parent.getString(R.string.object_bringtop);

        return (ret);
	}
	
	/**
	 * 
	 * 
	 */
    public String  getItem(int index)
    {
    	String message = "";
    	switch (index)
    	{
          case OBJECTOPERATION_DELETE:
    		message = parent.getString(R.string.object_delete);
    		break;
          case OBJECTOPERATION_DUPLICATE:
      		message = parent.getString(R.string.object_duplicate);
      		break;
          case OBJECTOPERATION_SIZEBIGGER:
        	message = parent.getString(R.string.object_bigger);
        	break;
          case OBJECTOPERATION_SIZESMALLER:
        	message = parent.getString(R.string.object_smaller);
         	break;
/*
          case OBJECTOPERATION_BRINGTOP:
        	message = parent.getString(R.string.object_bringtop);
         	break;
*/
          default:
         	break;
    	}
    	return (message);
    }

    /** �����I�����Ɏg�p���� **/
    
    /**
     * 
     */
    public boolean[] getSelectionStatus()
    {
    	return (null);
    }
    
    /**
     * 
     */
    public void setSelectionStatus(int index, boolean isSelected)
    {
    	  // �Ȃɂ����Ȃ�
    }
}
