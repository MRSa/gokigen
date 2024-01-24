package jp.sourceforge.gokigen.memoma;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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
	private int    icon = 0;
	private String title = null;

	/**
	 *   �R���X�g���N�^
	 * @param arg
	 */
	public TextEditDialog(Context arg, int titleIcon)
	{
		context = arg;
		icon = titleIcon;
	}

	/**
	 *  �N���X�̏���
	 * @param receiver
	 * @param initialMessage
	 */
	public void prepare(Dialog layout, ITextEditResultReceiver receiver, String titleMessage, String initialMessage, boolean isSingleLine)
	{
		if (receiver != null)
		{
			resultReceiver = receiver;
		}
        try
        {
            final TextView  editComment = (TextView)  layout.findViewById(R.id.editTextArea);
            if (titleMessage != null)
            {
                layout.setTitle(titleMessage);
                title = titleMessage;
            }

            // �e�L�X�g���̓G���A�̕�����ݒ肷��
            if (initialMessage != null)
            {
                editComment.setText(initialMessage);
            }
            else
            {
                editComment.setText("");
            }

            // ���͗̈�̍s�����X�V����
            editComment.setSingleLine(isSingleLine);
        }
        catch (Exception ex)
        {
        	// ���O�����f���āA�������Ȃ�
        	Log.v(Main.APP_IDENTIFIER, "TextEditDialog::prepare() " + ex.toString());
        }
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

    public interface ITextEditResultReceiver
    {
        public abstract boolean finishTextEditDialog(String message);
        public abstract boolean cancelTextEditDialog();
    }
}
