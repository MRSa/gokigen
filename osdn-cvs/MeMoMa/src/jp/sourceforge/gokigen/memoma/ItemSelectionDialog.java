package jp.sourceforge.gokigen.memoma;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;


/**
 *   �A�C�e����I������_�C�A���O����������
 * 
 * @author MRSa
 *
 */
public class ItemSelectionDialog
{
	private Context context = null;
	private ISelectionItemReceiver resultReceiver = null;
	private ISelectionItemHolder dataHolder = null;
	private String  title = "";

	public ItemSelectionDialog(Context arg)
	{
		context = arg;
	}

	/**
	 *  �N���X�̏���
	 * @param receiver
	 * @param initialMessage
	 */
	public void prepare(ISelectionItemReceiver receiver, ISelectionItemHolder holder,  String titleMessage)
	{
		title = titleMessage;
		resultReceiver = receiver;
		dataHolder = holder;
	}
	
	/**
     *   �m�F�_�C�A���O����������
     * @return
     */
    public Dialog getDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        
        // �\������f�[�^�i�_�C�A���O�^�C�g���A���b�Z�[�W�j����������
        if (title != null)
        {
            builder.setTitle(title);
        }
        builder.setCancelable(false);
        if (dataHolder != null)
        {
        	if (dataHolder.isMultipleSelection() == false)
        	{
                builder.setItems(dataHolder.getItems(), new DialogInterface.OnClickListener()
                {
                	public void onClick(DialogInterface dialog, int id)
                	{
                 	   boolean ret = false;
                	   if (resultReceiver != null)
                	   {
                	       resultReceiver.itemSelected(id, dataHolder.getItem(id));
                	   }
                       if (ret == true)
                       {
                    	   dialog.dismiss();
                       }
                       else
                       {
                           dialog.cancel();
                       }
                       System.gc();
                		
                	}
                }); 
        	}
            else
            {
            	/**  �����I���̑I�������������� **/
                builder.setMultiChoiceItems(dataHolder.getItems(), dataHolder.getSelectionStatus(), new DialogInterface.OnMultiChoiceClickListener()
                {
                	public void onClick(DialogInterface dialog, int which, boolean isChecked)
                	{
                  	    if (resultReceiver != null)
                	    {
                	        resultReceiver.itemSelected(which, dataHolder.getItem(which));
                	    }                		
                	}
                });

                /**  �����I�����ɂ́AOK�{�^�����������Ƃ��ɑI�����m�肳����B **/
                builder.setPositiveButton(context.getString(R.string.confirmYes), new DialogInterface.OnClickListener()
                {
                     public void onClick(DialogInterface dialog, int id)
                     {
                  	   boolean ret = false;
                  	   if (resultReceiver != null)
                  	   {
                  	       resultReceiver.itemSelectedMulti(dataHolder.getItems(), dataHolder.getSelectionStatus());
                  	   }
                         if (ret == true)
                         {
                      	   dialog.dismiss();
                         }
                         else
                         {
                             dialog.cancel();
                         }
                         System.gc();
                     }
                 });
            }
        }

//        builder.setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, OnMultiChoiceClickListener listener)
/**
**/
        builder.setNegativeButton(context.getString(R.string.confirmNo), new DialogInterface.OnClickListener()
        {
                   public void onClick(DialogInterface dialog, int id)
                   {
                	   boolean ret = false;
                	   if (resultReceiver != null)
                	   {
                	       resultReceiver.canceledSelection();
                	   }
                       if (ret == true)
                       {
                    	   dialog.dismiss();
                       }
                       else
                       {
                           dialog.cancel();
                       }
                       System.gc();
                   }
        });
        return (builder.create());    	
    }

    public interface ISelectionItemHolder
    {
    	public abstract boolean isMultipleSelection();

    	public abstract String[] getItems();
        public abstract String  getItem(int index);

        /** �����I�����Ɏg�p���� **/
        public abstract boolean[] getSelectionStatus();
        public abstract void setSelectionStatus(int index, boolean isSelected);
    };
    
    public interface ISelectionItemReceiver
    {
        public abstract void itemSelected(int index, String itemValue);
        public abstract void itemSelectedMulti(String[] items, boolean[] status);
        public abstract void canceledSelection();
    }
}
