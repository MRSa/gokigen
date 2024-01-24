package jp.sfjp.gokigen.okaken;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 *    回答結果の１件の情報をまとめたクラス
 * 
 * @author MRSa
 *
 */
public class SymbolListArrayItem
{
	private static final int ICON_SIZE = 20;

    private Bitmap iconResource = null;
    private boolean isCorrect = false;
	private int category = 0;
	private String answered = null;
	private String correctAnswer = null;
	private long answeredTime = 0;
	private String hint = null;
	private String detail = null;
	private String option = null;
	
	/**
	 *    コンストラクタ
	 * 
	 * @param context
	 * @param isCorrect
	 * @param category
	 * @param answered
	 * @param correctAnswer
	 * @param answeredTime
	 * @param hint
	 * @param detail
	 * @param option
	 */
    public SymbolListArrayItem(Context context, boolean isCorrect, int category, String answered, String correctAnswer, long answeredTime, String hint, String detail, String option)
    {
        this.isCorrect = isCorrect;
        if (isCorrect == true)
        {
        	// OKアイコンを設定する
        	iconResource = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(context.getResources(), R.drawable.good)), ICON_SIZE, ICON_SIZE, false);
        }
        else
        {
        	// NGアイコンを設定する
        	iconResource = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(context.getResources(), R.drawable.bad)), ICON_SIZE, ICON_SIZE, false);        	
        }
    	this.category = category;
        this.answered = answered;
    	this.correctAnswer = correctAnswer;
    	this.answeredTime = answeredTime;
    	this.hint = hint;
    	this.detail = detail;
    	this.option = option;
    }

    /**
     * 
     * @return
     */
    public Bitmap getIconResource()
    {
        return (iconResource);
    }

    public boolean getIsCorrect()
    {
    	return (isCorrect);
    }

    public int getCategory()
	{
		return (category);
	}

    public String getAnsweredString()
    {
    	return (answered);
    }
    
    public String getCorrectAnswerString()
    {
    	return (correctAnswer);
    }

    public long getAnsweredTime()
    {
    	return (answeredTime);
    }

    public String getHintString()
    {
    	return (hint);
    }
    
    public String getDetailString()
    {
    	return (detail);
    }
    
    public String getOptionString()
    {
    	return (option);
    }
}
