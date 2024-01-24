package jp.sourceforge.gokigen.diary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *  �N���W�b�g��\������
 * 
 * @author MRSa
 *
 */
public class CreditDialog
{
	private Activity context = null;

	/**
	 *   �R���X�g���N�^
	 * @param arg
	 */
	public CreditDialog(Activity arg)
	{
		context = arg;
	}

    /**
     *   �_�C�A���O����������
     * @return
     */
    public Dialog getDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.creditdialog,
    	                               (ViewGroup) context.findViewById(R.id.layout_root));

    	TextView text = (TextView) layout.findViewById(R.id.creditmessage);
    	text.setText(context.getString(R.string.app_credit));
 //   	ImageView image = (ImageView) layout.findViewById(R.id.crediticon);
 //   	image.setImageResource(R.drawable.icon);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setIcon(R.drawable.icon);
        builder.setView(layout);
        builder.setCancelable(true);
        return (builder.create());
    }
    
    
}
