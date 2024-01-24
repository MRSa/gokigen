package jp.sourceforge.gokigen.diary;

/**
 *  日時入力ダイアログのインタフェース
 * 
 * @author MRSa
 *
 */
public interface IDateTimeInputDialogListener
{
    /** 日時が入力された！ **/
	public abstract void inputDateTimeEntered(boolean useCurrent, int year, int month, int day, int hour, int minite);
	
	/** 日時入力をキャンセルした！ **/
    public abstract void inputDateTimeCanceled();
}
