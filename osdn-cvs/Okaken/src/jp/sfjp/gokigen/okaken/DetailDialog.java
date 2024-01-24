package jp.sfjp.gokigen.okaken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *  クレジットを表示する
 * 
 * @author MRSa
 *
 */
public class DetailDialog
{
	private Activity context = null;

	/**
	 *   コンストラクタ
	 * @param arg
	 */
	public DetailDialog(Activity arg)
	{
		context = arg;
	}

    /**
     *   ダイアログを応答する
     * @return
     */
    public Dialog getDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.detaildialog, (ViewGroup) null);  //  ?? http://www.mail-archive.com/android-developers@googlegroups.com/msg162003.html より
 //    View layout = inflater.inflate(R.layout.creditdialog, (ViewGroup) context.findViewById(R.id.layout_root));

 //   	TextView text = (TextView) layout.findViewById(R.id.creditmessage);
 //   	text.setText(context.getString(R.string.app_credit));
 //   	ImageView image = (ImageView) layout.findViewById(R.id.crediticon);
 //   	image.setImageResource(R.drawable.icon);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(context.getString(R.string.app_name));
//        builder.setIcon(R.drawable.icon);
        builder.setView(layout);
        builder.setCancelable(true);
        return (builder.create());
    }
    
    /**
     *    OK/NGのアイコンを設定する
     * 
     * @param image
     * @param isCorrect
     */
    private void setIsCorrectIcon(ImageView image, SymbolListArrayItem item)
    {
        if (item.getIsCorrect() == true)
        {
        	image.setImageResource(R.drawable.good);
        }
        else
        {
        	image.setImageResource(R.drawable.bad);
        }	
    }
    
    /**
     * 
     *     
     * @param view
     * @param item
     */
    private void setAnswerString(TextView view, SymbolListArrayItem item)
    {
    	String answerToShow = item.getAnsweredString();
    	if (item.getIsCorrect() == false)
    	{
    		answerToShow = answerToShow + " (" + item.getCorrectAnswerString() +")"; 
    	}
    	view.setText(answerToShow);
    }
    
    /**
     * 
     *     
     * @param view
     * @param item
     */
    private void setAnsweredTime(TextView view, SymbolListArrayItem item)
    {
    	String answerTimeString = context.getString(R.string.detail_answertime) + " ";
    	float answerTime = (float) item.getAnsweredTime() / 1000;
    	answerTimeString = answerTimeString + answerTime;
    	answerTimeString = answerTimeString + " " + context.getString(R.string.detail_unit_answertime);
    	view.setText(answerTimeString);
    }
    
    /**
     * 
     *     
     * @param view
     * @param item
     */
    private void setDetailString(TextView view, SymbolListArrayItem item)
    {  	
    	String textToShow = item.getDetailString();
    	textToShow = "\n" + textToShow;
    	view.setText(textToShow);
    }

    /**
     * 
     *     
     * @param view
     * @param item
     */    
    private void setOptionString(TextView view, SymbolListArrayItem item)
    {
    	String textToShow = context.getString(R.string.detail_message_url);
    	textToShow = "\n" + textToShow + " " + item.getOptionString();
    	view.setText(textToShow);
    }

    /**
     *    ダイアログの情報を更新する
     * 
     */
    public void onPrepareDialog(Dialog dialog, SymbolListArrayItem item)
    {
		Log.v(Gokigen.APP_IDENTIFIER, "DetailDialog::onPrepareDialog() : " + item.getAnsweredString());    		
        try
        {
        	setIsCorrectIcon((ImageView) dialog.findViewById(R.id.answerStatusIcon), item);
        	setAnswerString((TextView) dialog.findViewById(R.id.answerStringArea), item);
        	setAnsweredTime((TextView) dialog.findViewById(R.id.answerTimeArea), item);
        	setDetailString((TextView) dialog.findViewById(R.id.answerDetailArea), item);
        	setOptionString((TextView) dialog.findViewById(R.id.answerUrlArea), item);
        }
		catch (Exception ex)
		{
			Log.v(Gokigen.APP_IDENTIFIER, "Ex>DetailDialog::onPrepareDialog() " + ex.toString());
		}
		return;
    }
}
