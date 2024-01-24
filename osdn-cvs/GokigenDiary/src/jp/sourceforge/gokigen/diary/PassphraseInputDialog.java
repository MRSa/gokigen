package jp.sourceforge.gokigen.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 *  �p�X���[�h���͂̃_�C�A���O�N���X
 *  (�p�X���[�h�̃`�F�b�N�́AIPassphraseInputCallback �ōs��)
 * 
 * @author MRSa
 *
 */
public class PassphraseInputDialog
{
	private Activity context = null;
	private int titleId = -1;
    private int iconId = -1;
	private boolean isCancel = true;
    private IPassphraseInputCallback callbackInterface = null;
	
	/**
	 *   �R���X�g���N�^
	 * @param arg
	 */
	public PassphraseInputDialog(Activity arg, IPassphraseInputCallback callback)
	{
		context = arg;
		callbackInterface = callback;
	}

	/**
	 *  �\������f�[�^������
	 * @param icon
	 * @param title
	 * @param defaultText
	 * @param cancelAvailable
	 */
    public void prepare(int icon, int title, boolean cancelAvailable)
    {
    	iconId = icon;
		titleId = title;
		isCancel = cancelAvailable;
    }
	
    /**
     *  ���͍ς݃p�X���[�h���N���A����
     * 
     * @param dialog
     */
    public void clearPhrase(Dialog layout)
    {
        try
        {
        	final EditText  passphrase = (EditText) layout.findViewById(R.id.inputPassphraseArea);
            passphrase.setText("");
        }
        catch (Exception ex)
        {
        	// �G���[����...
        }
    }

    /**
     *   �_�C�A���O����������
     * @return
     */
    public Dialog getDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.passphraseinputdialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (iconId > 0)
        {
        	builder.setIcon(iconId);
        }
        if (titleId > 0)
        {
        	builder.setTitle(context.getString(titleId));
        }

        // ���݂̓��̓f�[�^���_�C�A���O�Ɋi�[����
        final EditText  passphrase = (EditText) layout.findViewById(R.id.inputPassphraseArea);
        passphrase.setText("");

        builder.setView(layout);
        builder.setCancelable(isCancel);
        builder.setPositiveButton(context.getString(R.string.confirmYes), new DialogInterface.OnClickListener()
              {
                   public void onClick(DialogInterface dialog, int id)
                   {
                	   if (callbackInterface == null)
                	   {
                		   dialog.cancel();
                		   System.gc();
                		   return;
                	   }
                	   String data = "";
                       try
                       {
                	       data = passphrase.getText().toString();
                       }
                       catch (Exception ex)
                       {
                    	   // 
                       }
                	   boolean isCancel = callbackInterface.inputPassphraseFinished(data);
                	   if (isCancel == true)
                	   {
                           dialog.cancel();
                	   }
                	   System.gc();
                   }
               });
/*
        builder.setNegativeButton(context.getString(R.string.confirmNo), new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                	   if (callbackInterface == null)
                	   {
                		   dialog.cancel();
                		   System.gc();
                		   return;
                	   }
                	  callbackInterface.inputPassphraseCanceled();
                      dialog.cancel();
                      System.gc();
                   }
               });
*/
        return (builder.create());
    }
}
