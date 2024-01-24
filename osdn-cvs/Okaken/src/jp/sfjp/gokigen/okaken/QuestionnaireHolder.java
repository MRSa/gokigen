package jp.sfjp.gokigen.okaken;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *    ���̑I�������Ǘ�����N���X
 * 
 * @author MRSa
 *
 */
public class QuestionnaireHolder  implements Parcelable 
{
	//private int level = 0;                          // ���̃��x��    �i��Փx�j
	//private int category = 0;            // ���̃J�e�S�� �i���̃W�������j
	private int currentSelectionList = 0;    // �o�^����Ă�����̑I�����̐�
	private String correctAnswer  = null;  // �����̑I����
	private ArrayList<String> answers = null;  // �ԈႢ�̑I����
	private String     hintText = null;        //  ���̉񓚂ւ̎肪����
	private String     detailText = null;     //   �����̉��
	private String     optionText = null;    //  ���ڂ����m�邽�߂�URL
	
	/**
	 *     ���̐ݒ�(�����̂݁A�듚�͕ʓr)
	 * 
	 * @param correctAnswer
	 */
    public QuestionnaireHolder(int category, String correctAnswer, String hintText, String detailText, String optionText)
    {
    	answers = new ArrayList<String>();
    	this.correctAnswer = correctAnswer;
    	this.hintText = hintText;
    	this.detailText = detailText;
    	this.optionText = optionText;
    	currentSelectionList = 1;
    }

    /**
     *    �듚�̉񓚂�ݒ肷��
     * 
     * @param answer
     */
    public void addAnswer(String answer)
    {
        answers.add(answer);
        currentSelectionList = currentSelectionList + 1;
    }
    
    /**
     *    �񓚂̑I���������擾����
     * 
     * @return
     */
    public int getNumberOfAnswers()
    {
    	return (currentSelectionList);
    }
    
    /**
     *    ���̑I�������擾����
     * 
     * @param index
     * @return  �I�����̕�����B
     */
    public String getAnswer(int index)
    {
        if (index == 0)
        {
        	// index�ԍ���0�̏ꍇ�ɂ́A������Ԃ��B
        	return (correctAnswer);
        }
        if ((index < 0)||(index >= currentSelectionList))
        {
            return (null);
        }
        return (answers.get(index - 1));        	
    }

    /**
     *    ���̃q���g����������
     * 
     * @return
     */
    public String getHintText()
    {
    	return (hintText);
    }

    /**
     *    ���̉������������
     * 
     * @return
     */
    public String getDetailText()
    {
    	return (detailText);
    }

    /**
     *    ���ɂ��āA���ڂ����m�邽�߂�URL����������
     * 
     * @return
     */
    public String getOptionText()
    {
    	return (optionText);
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
        out.writeInt(currentSelectionList);
        out.writeString(correctAnswer);
        out.writeStringList(answers);
        out.writeString(hintText);
        out.writeString(detailText);
        out.writeString(optionText);
    }

    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    public static final Parcelable.Creator<QuestionnaireHolder> CREATOR
        = new Parcelable.Creator<QuestionnaireHolder>()
        {
    	    public QuestionnaireHolder createFromParcel(Parcel in)
    	    {
    	    	return (new QuestionnaireHolder(in));
    	    }
 
    	    public QuestionnaireHolder[] newArray(int size)
    	    {
    	    	return (new QuestionnaireHolder[size]);
    	    }    	    
        };

    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    private QuestionnaireHolder(Parcel in)
    {
    	answers = new ArrayList<String>();
    	currentSelectionList = in.readInt();
    	correctAnswer = in.readString();
    	 in.readStringList(answers);
    	 hintText = in.readString();
    	 detailText = in.readString();
    	 optionText = in.readString();
    }
}
