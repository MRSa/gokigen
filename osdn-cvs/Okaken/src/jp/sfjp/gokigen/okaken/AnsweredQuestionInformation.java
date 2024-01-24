package jp.sfjp.gokigen.okaken;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *    ���̉񓚏󋵂��Ǘ�����N���X
 * 
 * @author MRSa
 *
 */
public class AnsweredQuestionInformation implements Parcelable 
{
	private int          category = 0;
	private boolean isCorrect = false;
	private long       answeredTime = 0;
	private long       startTime = 0;
	private String     answeredText = null;

	/**
	 *    �R���X�g���N�^
	 *    
 	 */
	public AnsweredQuestionInformation(int category)
	{
		this.category = category;
	}

	/**
	 *   �R���X�g���N�^
	 *   
	 * @param isCorrect
	 * @param answeredTime
	 */
	public AnsweredQuestionInformation(int category, boolean isCorrect, long answeredTime, long startTime, String answeredText)
	{
		this.category = category;
		this.isCorrect = isCorrect;
		this.answeredTime = answeredTime;
		this.startTime = startTime;
		this.answeredText = answeredText;
	}

	/**
	 *   �o�肵�����Ԃ��L�^����
	 * 
	 * @param time
	 */
	public void setStartTime(long time)
	{
		startTime = time;
	}    	
	
	/**
     *    ���̎�ʂ���������
     * 
     * @return
     */
    public int getCategory()
    {
    	return (category);
    }

    /**
     * 
     * 
     * @return
     */
	public boolean getIsCorrect()
	{
		return (isCorrect);
	}

    /**
     * 
     * 
     * @return
     */
	public long getAnsweredTime()
	{
		return (answeredTime);
	}
	
    /**
     * 
     * 
     * @return
     */
	public long getStartTime()
	{
		return (startTime);
	}
	
    /**
     * 
     * 
     * @return
     */
	public String getAnsweredString()
	{
		return (answeredText);
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
        out.writeInt(category);
        out.writeBooleanArray(new boolean[] {isCorrect});
    	out.writeLong(answeredTime);
    	out.writeLong(startTime);
    	out.writeString(answeredText);
    }

    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    public static final Parcelable.Creator<AnsweredQuestionInformation> CREATOR
        = new Parcelable.Creator<AnsweredQuestionInformation>()
        {
    	    public AnsweredQuestionInformation createFromParcel(Parcel in)
    	    {
    	    	return (new AnsweredQuestionInformation(in));
    	    }
 
    	    public AnsweredQuestionInformation[] newArray(int size)
    	    {
    	    	return (new AnsweredQuestionInformation[size]);
    	    }    	    
        };

    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    private AnsweredQuestionInformation(Parcel in)
    {
    	category = in.readInt();
        boolean[] booleans = new boolean[1];
    	in.readBooleanArray(booleans);
    	isCorrect = booleans[0];
        answeredTime = in.readLong();
        startTime = in.readLong();
        answeredText = in.readString();
    }
}
