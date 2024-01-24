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
 *  パスワード入力のダイアログクラス
 *  (パスワードのチェックは、IPassphraseInputCallback で行う)
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
	 *   コンストラクタ
	 * @param arg
	 */
	public PassphraseInputDialog(Activity arg, IPassphraseInputCallback callback)
	{
		context = arg;
		callbackInterface = callback;
	}

	/**
	 *  表示するデータを入れる
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
     *  入力済みパスワードをクリアする
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
        	// エラー処理...
        }
    }

    /**
     *   ダイアログを応答する
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

        // 現在の入力データをダイアログに格納する
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
