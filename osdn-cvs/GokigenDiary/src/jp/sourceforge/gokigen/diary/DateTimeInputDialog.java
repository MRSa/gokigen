package jp.sourceforge.gokigen.diary;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * 日時入力のダイアログクラス
 * 
 * @author MRSa
 *
 */
public class DateTimeInputDialog
{
	private Activity context = null;
	private int titleId = -1;
    private int iconId = -1;
    private IDateTimeInputDialogListener callbackInterface = null;
	
	/**
	 *   コンストラクタ
	 * @param arg
	 */
	public DateTimeInputDialog(Activity arg, IDateTimeInputDialogListener callback)
	{
		context = arg;
		callbackInterface = callback;
	}
	
    /**
     * 現在日時を設定する
     * 
     * @param dialog
     */
    public void setDateAndTime(Dialog layout, int icon)
    {
    	iconId = icon;

    	try
    	{
            // 現在の時刻をダイアログに設定する
    		Calendar calendar = Calendar.getInstance();
            DatePicker date = (DatePicker) layout.findViewById(R.id.datePickerInput);
        	TimePicker time = (TimePicker) layout.findViewById(R.id.timePickerInput);

        	date.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        	time.setIs24HourView(true);
        	time.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
           	time.setCurrentMinute(calendar.get(Calendar.MINUTE));

            // チェックボックスのチェックをクリアする
           	CheckBox box  = (CheckBox) layout.findViewById(R.id.useCurrentDateTime);
           	box.setChecked(false);
    	}
    	catch (Exception ex)
    	{
    	    // 
    	}
    }

    /**
     *   ダイアログを応答する
     * @return
     */
    public Dialog getDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.datetimepickerdialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (iconId > 0)
        {
        	builder.setIcon(iconId);
        }
        if (titleId > 0)
        {
        	builder.setTitle(context.getString(titleId));
        }

        builder.setView(layout);
        builder.setCancelable(true);
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

                	   boolean useCurrentDateTime = true;
                	   int year = 0;
                	   int month = 0;
                	   int day = 0;
                	   int hour = 0;
                	   int minite = 0;
                       try
                       {
                    	   // レイアウトからデータをとってくる
                           DatePicker date = (DatePicker) layout.findViewById(R.id.datePickerInput);
                       	   TimePicker time = (TimePicker) layout.findViewById(R.id.timePickerInput);
                       	   CheckBox   box  = (CheckBox) layout.findViewById(R.id.useCurrentDateTime);
                       	   
                       	   useCurrentDateTime = box.isChecked();
                       	   year = date.getYear();
                    	   month = date.getMonth();
                    	   day = date.getDayOfMonth();
                    	   hour = time.getCurrentHour();
                    	   minite = time.getCurrentMinute();
                       }
                       catch (Exception ex)
                       {
                    	   // 
                       }
                       
                       // 文字列が入力された！
                       callbackInterface.inputDateTimeEntered(useCurrentDateTime, year, (month + 1), day, hour, minite);
                       dialog.cancel();
                	   System.gc();
                   }
               });

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
                	  callbackInterface.inputDateTimeCanceled();
                      dialog.cancel();
                      System.gc();
                   }
               });
        return (builder.create());
    }
}
