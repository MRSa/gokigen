package jp.sourceforge.gokigen.android.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 *  �e�L�X�g�ҏW�̃_�C�A���O
 * 
 * @author MRSa
 *
 */
public class TextEditDialog
{
	private Context context = null;
	private ITextEditResultReceiver resultReceiver = null;
    private String  message = "";
	private String  title = "";
	private int    icon = 0;

	/**
	 *   �R���X�g���N�^
	 * @param arg
	 */
	public TextEditDialog(Context arg)
	{
		context = arg;
	}

	/**
	 *  �N���X�̏���
	 * @param receiver
	 * @param initialMessage
	 */
	public void prepare(ITextEditResultReceiver receiver, int titleIcon, String titleMessage, String initialMessage)
	{
		if (receiver != null)
		{
			resultReceiver = receiver;
		}
		icon = titleIcon;
		title = titleMessage;
        message = initialMessage;		
	}

    /**
     *   �e�L�X�g�ҏW�_�C�A���O����������
     * @return
     */
    public Dialog getDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.messagedialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final TextView  editComment = (TextView)  layout.findViewById(R.id.editTextArea);

        // �\������f�[�^�i�A�C�R���A�_�C�A���O�^�C�g���A���b�Z�[�W�j����������
        if (icon != 0)
        {
            builder.setIcon(icon);
        }
        if (title != null)
        {
            builder.setTitle(title);
        }
        if (message != null)
        {
            editComment.setText(message);
        }

        builder.setView(layout);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getString(R.string.confirmYes), new DialogInterface.OnClickListener()
              {
                   public void onClick(DialogInterface dialog, int id)
                   {
                	   boolean ret = false;
                	   if (resultReceiver != null)
                	   {
                	       resultReceiver.finishTextEditDialog(editComment.getText().toString());
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
        builder.setNegativeButton(context.getString(R.string.confirmNo), new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                	   boolean ret = false;
                	   if (resultReceiver != null)
                	   {
                	       resultReceiver.cancelTextEditDialog();
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
}
