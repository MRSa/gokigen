package jp.sfjp.gokigen.okaken;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *   
 * 
 * @author MRSa
 *
 */
public class MoleGameQuestionHolder implements Parcelable 
{
	private boolean existQuestion = false;
	private boolean isCorrect = false;
    private String  message = "";

    /**
     *     �R���X�g���N�^
     * 
     */
    public MoleGameQuestionHolder()
    {
 	    //
    }
	
    /**
     * 
     * 
     */
    public void resetQuestion()
    {
    	existQuestion = false;
    	this.message = "";
    }
    
    /**
     * 
     * 
     * @param message
     * @param hint
     * @param detail
     * @param isCorrect
     */
    public void setQuestion(String message, boolean isCorrect)
    {
    	    this.isCorrect = isCorrect;
    	    this.message = message;
    	    existQuestion = true;
    }
    
    /**
     * 
     * 
     * @return
     */
    public boolean isExistQuestion()
    {
    	return (existQuestion);
    }
  
    /**
     * 
     * 
     * @return
     */
    public String getQuestion()
    {
        if (existQuestion == true)
        {
        	return (message);
        }
        return ("");    	
    }

    /**
     * 
     * 
     * @return
     */
    public boolean isCorrectQuestion()
    {
        if ((existQuestion == true)&&(isCorrect == true))
        {
        	return (true);
        }
        return (false);
    }    


    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    public int describeContents()
    {
        return (0);  
    }

    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(message);
        //boolean[] boolArray = out.createBooleanArray();
        out.writeBooleanArray(new boolean[] {existQuestion, isCorrect});
    }

    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    public static final Parcelable.Creator<MoleGameQuestionHolder> CREATOR
        = new Parcelable.Creator<MoleGameQuestionHolder>()
        {
    	    public MoleGameQuestionHolder createFromParcel(Parcel in)
    	    {
    	    	return (new MoleGameQuestionHolder(in));
    	    }
 
    	    public MoleGameQuestionHolder[] newArray(int size)
    	    {
    	    	return (new MoleGameQuestionHolder[size]);
    	    }    	    
        };

    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    private MoleGameQuestionHolder(Parcel in)
    {
    	message = in.readString();
    	boolean[] booleans = new boolean[2];
    	in.readBooleanArray(booleans);
    	existQuestion = booleans[0];
    	isCorrect = booleans[1];
    }
}
