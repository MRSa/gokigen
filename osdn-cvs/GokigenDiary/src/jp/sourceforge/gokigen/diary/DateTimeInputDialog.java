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
 * �������͂̃_�C�A���O�N���X
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
	 *   �R���X�g���N�^
	 * @param arg
	 */
	public DateTimeInputDialog(Activity arg, IDateTimeInputDialogListener callback)
	{
		context = arg;
		callbackInterface = callback;
	}
	
    /**
     * ���ݓ�����ݒ肷��
     * 
     * @param dialog
     */
    public void setDateAndTime(Dialog layout, int icon)
    {
    	iconId = icon;

    	try
    	{
            // ���݂̎������_�C�A���O�ɐݒ肷��
    		Calendar calendar = Calendar.getInstance();
            DatePicker date = (DatePicker) layout.findViewById(R.id.datePickerInput);
        	TimePicker time = (TimePicker) layout.findViewById(R.id.timePickerInput);

        	date.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        	time.setIs24HourView(true);
        	time.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
           	time.setCurrentMinute(calendar.get(Calendar.MINUTE));

            // �`�F�b�N�{�b�N�X�̃`�F�b�N���N���A����
           	CheckBox box  = (CheckBox) layout.findViewById(R.id.useCurrentDateTime);
           	box.setChecked(false);
    	}
    	catch (Exception ex)
    	{
    	    // 
    	}
    }

    /**
     *   �_�C�A���O����������
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
                    	   // ���C�A�E�g����f�[�^���Ƃ��Ă���
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
                       
                       // �����񂪓��͂��ꂽ�I
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
