package jp.sourceforge.gokigen.diary;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class CalendarDialog implements OnClickListener, DialogInterface.OnClickListener
{
	private Context context = null;

	public static final int NUMBER_OF_CALENDAR_BUTTONS = 42;
	
	private int currentYear = 0;
	private int currentMonth = 0;
	private int monthStartIndex = 0;
	private ICalendarDatePickup resultReceiver = null;
	
	private AlertDialog dialog = null;
	
	/**
	 *   カレンダーダイアログのコンストラクタ
	 * 
	 * @param arg
	 */
	public CalendarDialog(Context arg)
	{
	    context = arg;
	}
		
	
	/**
	 *  準備クラス
	 * 
	 * @return
	 */
	public boolean prepare(ICalendarDatePickup receiver)
	{
		resultReceiver = receiver;
		return (true);
	}	

	/**
	 *  ダイアログを(強制的に)閉じる
	 * 
	 */
	public void dismiss()
	{
		if (dialog != null)
		{
		    dialog.dismiss();
        }
	}
	
    /**
     *   クリックされたときの処理
     */
    public void onClick(View v)
    {
        int id = v.getId();
    	try
    	{
            if (id == R.id.todaySelectButton)
            {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                resultReceiver.decideDate(year, month, day);
            	dialog.dismiss();
            	return;
            }
            else if (id == R.id.showNextMonth)
            {
            	currentMonth++;
            	setCalendarButton(dialog);
            	return;        	
            }
            else if (id == R.id.showPreviousMonth)
            {
            	currentMonth--;
            	setCalendarButton(dialog);
                return;
            }

            // 日付ボタンが選択されたとき！
        	int index = getCalendarIndexFromButtonId(id);
            int day =  index - monthStartIndex + 1;

            resultReceiver.decideDate(currentYear, currentMonth, day);
        	dialog.dismiss();
    	}
    	catch (Exception ex)
    	{
    		try
    		{
    	        dialog.dismiss();
    		}
    		catch (Exception e)
    		{
    			//
    		}
    	}
    	return;
    }

    /**
     *   クリックされたときの処理
     * 
     */
    public void onClick(DialogInterface dialog, int which)
    {
        Log.v(Main.APP_IDENTIFIER, "CalendarDialog::onClick(DialogInterface dialog, int which)" + " " + which);    	
    }
    
    /**
     *  ボタンラベルを設定する
     * 
     * @param visible
     * @param view
     * @param buttonIndex
     * @param label
     */
    private void setButtonLabel(boolean visible, Dialog view, int buttonIndex, String label)
    {
    	try
    	{   	
            Button calendarLabel = (Button) view.findViewById(decideButtonIdfromIndex(buttonIndex));
            if (visible == false)
            {
                calendarLabel.setClickable(false);
                calendarLabel.setVisibility(View.INVISIBLE);
                calendarLabel.setText("");
                return;
            }	
            calendarLabel.setClickable(true);
            calendarLabel.setVisibility(View.VISIBLE);
            calendarLabel.setText(label);
        }
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "Calendar::setButtonLabel() [" + buttonIndex + "," + label + "]");
    	}
    	return;
    }

    /**
     *  月の動きボタンを移動させる
     * 
     * @param view
     */
    private void prepareButtons(View view)
    {
        ImageButton btnImage = (ImageButton) view.findViewById(R.id.showNextMonth);
        btnImage.setOnClickListener(this);

        btnImage = (ImageButton) view.findViewById(R.id.showPreviousMonth);
        btnImage.setOnClickListener(this);

        Button btn = (Button) view.findViewById(R.id.todaySelectButton);
        btn.setOnClickListener(this);

        // カレンダーボタン
        for (int index = 0; index < NUMBER_OF_CALENDAR_BUTTONS; index++)
        {
        	btn = (Button) view.findViewById(decideButtonIdfromIndex(index));
            btn.setOnClickListener(this);
        }

    }

    /**
     *   カレンダーの設定
     * 
     * 
     * @param layout
     */
    private void setCalendarButton(Dialog layout)
    {

    	Calendar calendar = new GregorianCalendar();
    	calendar.set(currentYear, (currentMonth - 1), 1);

    	currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;

        char [] appendChar = new char[NUMBER_OF_CALENDAR_BUTTONS];
        for (int index = 0; index < NUMBER_OF_CALENDAR_BUTTONS; index++)
        {
        	appendChar[index] = ' ';
        }        
        // 追加のテキストデータをもらう。
        try
        {
            resultReceiver.setAppendCharacter(currentYear, currentMonth, appendChar);
        }
        catch (Exception ex)
        {
        	// ダイアログを閉じる
        	try
        	{
        	    dialog.dismiss();
        	}
        	catch (Exception e)
        	{
        		// 何もしない
        	}
        }

        // テキストで日時を表示する
        TextView field = (TextView) layout.findViewById(R.id.showDayYear);
        field.setText(currentYear + "/" + currentMonth);
//        DateFormat dateF = new SimpleDateFormat("yyyy/MM");
//        field.setText(dateF.format(calendar.getTime()));

        // その月の最初の曜日を取得する
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == Calendar.SUNDAY)
        {
        	monthStartIndex = 0;
        }
        else if (week == Calendar.MONDAY)
        {
        	monthStartIndex = 1;
        }
        else if (week == Calendar.TUESDAY)
        {
        	monthStartIndex = 2;
        }
        else if (week == Calendar.WEDNESDAY)
        {
        	monthStartIndex = 3;
        }
        else if (week == Calendar.THURSDAY)
        {
        	monthStartIndex = 4;
        }
        else if (week == Calendar.FRIDAY)
        {
        	monthStartIndex = 5;
        }
        else if (week == Calendar.SATURDAY)
        {
        	monthStartIndex = 6;
        }
        
        for (int index = 0; index < monthStartIndex; index++)
        {
            setButtonLabel(false, layout, index, "");       	
        }

     	calendar.set(currentYear, currentMonth, 0);
        int lastIndex =calendar.get(Calendar.DATE);
        for (int index = 1; index <= lastIndex; index++)
        {
        	if (appendChar[index - 1] == '_')
        	{
        		// アンダーバーの時にはアンダーラインを引く
                setButtonLabel(true, layout, (index + monthStartIndex - 1), "" + index + appendChar[index - 1]);
        	}
        	else
        	{
                setButtonLabel(true, layout, (index + monthStartIndex - 1), index + "" + appendChar[index - 1]);        		
        	}
        }
        
        for (int index = (monthStartIndex + lastIndex); index < NUMBER_OF_CALENDAR_BUTTONS; index++)
        {
            setButtonLabel(false, layout, index, "");        	
        }
    }
    
    /**
     * 
     * @param year
     * @param month
     */
    public void setYearMonth(Dialog dialog, int year, int month)
    {
    	currentYear = year;
    	currentMonth = month;
    	setCalendarButton(dialog);
    }

    /**
     *  カレンダーダイアログの表示
     * 
     * @param message
     * @return
     */
    public Dialog getDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.calendardialog, null);

        // カレンダー上のボタンを準備する
        prepareButtons(layout);
        
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        //dialogBuilder.setTitle(context.getString(R.string.dateSelection));

        dialogBuilder.setView(layout);
        dialogBuilder.setCancelable(true);        
        //dialogBuilder.setPositiveButton("OK", this);

        dialog = dialogBuilder.create();
        return (dialog);
    }

    /**
     *   
     * 
     * @param index
     * @return
     */
    private int decideButtonIdfromIndex(int index)
    {
    	int id = R.id.Calendar00;
        switch (index)
        {
          case 1:
        	id = R.id.Calendar01;
        	break;
        
          case 2:
          	id = R.id.Calendar02;
          	break;
          case 3:
          	id = R.id.Calendar03;
          	break;
          case 4:
          	id = R.id.Calendar04;
          	break;
          case 5:
          	id = R.id.Calendar05;
          	break;
          case 6:
          	id = R.id.Calendar06;
          	break;
          case 7:
          	id = R.id.Calendar10;
          	break;
          case 8:
          	id = R.id.Calendar11;
          	break;
          case 9:
          	id = R.id.Calendar12;
          	break;
          case 10:
          	id = R.id.Calendar13;
          	break;
          case 11:
          	id = R.id.Calendar14;
          	break;
          case 12:
          	id = R.id.Calendar15;
          	break;
          case 13:
          	id = R.id.Calendar16;
          	break;
          case 14:
          	id = R.id.Calendar20;
          	break;
          case 15:
          	id = R.id.Calendar21;
          	break;
          case 16:
          	id = R.id.Calendar22;
          	break;
          case 17:
          	id = R.id.Calendar23;
          	break;
          case 18:
          	id = R.id.Calendar24;
          	break;
          case 19:
          	id = R.id.Calendar25;
          	break;
          case 20:
          	id = R.id.Calendar26;
          	break;
          case 21:
          	id = R.id.Calendar30;
          	break;
          case 22:
          	id = R.id.Calendar31;
          	break;
          case 23:
          	id = R.id.Calendar32;
          	break;
          case 24:
          	id = R.id.Calendar33;
          	break;
          case 25:
          	id = R.id.Calendar34;
          	break;
          case 26:
          	id = R.id.Calendar35;
          	break;
          case 27:
          	id = R.id.Calendar36;
          	break;
          case 28:
          	id = R.id.Calendar40;
          	break;
          case 29:
          	id = R.id.Calendar41;
          	break;
          case 30:
          	id = R.id.Calendar42;
          	break;
          case 31:
          	id = R.id.Calendar43;
          	break;
          case 32:
          	id = R.id.Calendar44;
          	break;
          case 33:
          	id = R.id.Calendar45;
          	break;
          case 34:
          	id = R.id.Calendar46;
          	break;
          case 35:
          	id = R.id.Calendar50;
          	break;
          case 36:
          	id = R.id.Calendar51;
          	break;
          case 37:
          	id = R.id.Calendar52;
          	break;
          case 38:
          	id = R.id.Calendar53;
          	break;
          case 39:
          	id = R.id.Calendar54;
          	break;
          case 40:
          	id = R.id.Calendar55;
          	break;
          case 41:
            id = R.id.Calendar56;
            break;
          case 0:
          default:
        	id = R.id.Calendar00;
        	break;
        }
        return (id);
    }

    /**
     * 
     * 
     * @param buttonId
     * @return
     */
    private int getCalendarIndexFromButtonId(int buttonId)
    {
    	int index = 0;
    	switch (buttonId)
    	{
    	  case R.id.Calendar00:
      	    index = 0;
            break;
    	  case R.id.Calendar01:
        	index = 1;
            break;
    	  case R.id.Calendar02:
        	index = 2;
            break;
    	  case R.id.Calendar03:
        	index = 3;
            break;
    	  case R.id.Calendar04:
        	index = 4;
            break;
    	  case R.id.Calendar05:
        	index = 5;
            break;
    	  case R.id.Calendar06:
        	index = 6;
            break;
    	
    	  case R.id.Calendar10:
        	index = 7;
            break;
      	  case R.id.Calendar11:
          	index = 8;
              break;
      	  case R.id.Calendar12:
          	index = 9;
              break;
      	  case R.id.Calendar13:
          	index = 10;
              break;
      	  case R.id.Calendar14:
          	index = 11;
              break;
      	  case R.id.Calendar15:
          	index = 12;
              break;
      	  case R.id.Calendar16:
          	index = 13;
              break;
    	
          	
    	  case R.id.Calendar20:
        	index = 14;
            break;
      	  case R.id.Calendar21:
          	index = 15;
              break;
      	  case R.id.Calendar22:
          	index = 16;
              break;
      	  case R.id.Calendar23:
          	index = 17;
              break;
      	  case R.id.Calendar24:
          	index = 18;
              break;
      	  case R.id.Calendar25:
          	index = 19;
              break;
      	  case R.id.Calendar26:
          	index = 20;
              break;         	
            	
    	  case R.id.Calendar30:
        	index = 21;
            break;
      	  case R.id.Calendar31:
          	index = 22;
              break;
      	  case R.id.Calendar32:
          	index = 23;
              break;
      	  case R.id.Calendar33:
          	index = 24;
              break;
      	  case R.id.Calendar34:
          	index = 25;
              break;
      	  case R.id.Calendar35:
          	index = 26;
              break;
      	  case R.id.Calendar36:
          	index = 27;
              break;
          	
    	  case R.id.Calendar40:
        	index = 28;
            break;
      	  case R.id.Calendar41:
          	index = 29;
              break;
      	  case R.id.Calendar42:
          	index = 30;
              break;
      	  case R.id.Calendar43:
          	index = 31;
              break;
      	  case R.id.Calendar44:
          	index = 32;
              break;
      	  case R.id.Calendar45:
          	index = 33;
              break;
      	  case R.id.Calendar46:
          	index = 34;
              break;
            	
    	  case R.id.Calendar50:
        	index = 35;
            break;
      	  case R.id.Calendar51:
          	index = 36;
              break;
      	  case R.id.Calendar52:
          	index = 37;
              break;
      	  case R.id.Calendar53:
          	index = 38;
              break;
      	  case R.id.Calendar54:
          	index = 39;
              break;
      	  case R.id.Calendar55:
          	index = 40;
              break;
      	  case R.id.Calendar56:
          	index = 41;
              break;

      	  default:
    	    index = 0;
            break;
    	}
        return (index);
    }
    
}
