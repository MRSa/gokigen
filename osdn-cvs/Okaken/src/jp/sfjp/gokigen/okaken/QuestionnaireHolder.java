package jp.sfjp.gokigen.okaken;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *    問題の選択肢を管理するクラス
 * 
 * @author MRSa
 *
 */
public class QuestionnaireHolder  implements Parcelable 
{
	//private int level = 0;                          // 問題のレベル    （難易度）
	//private int category = 0;            // 問題のカテゴリ （問題のジャンル）
	private int currentSelectionList = 0;    // 登録されている問題の選択肢の数
	private String correctAnswer  = null;  // 正解の選択肢
	private ArrayList<String> answers = null;  // 間違いの選択肢
	private String     hintText = null;        //  問題の回答への手がかり
	private String     detailText = null;     //   正答の解説
	private String     optionText = null;    //  より詳しく知るためのURL
	
	/**
	 *     問題の設定(正解のみ、誤答は別途)
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
     *    誤答の回答を設定する
     * 
     * @param answer
     */
    public void addAnswer(String answer)
    {
        answers.add(answer);
        currentSelectionList = currentSelectionList + 1;
    }
    
    /**
     *    回答の選択肢数を取得する
     * 
     * @return
     */
    public int getNumberOfAnswers()
    {
    	return (currentSelectionList);
    }
    
    /**
     *    問題の選択肢を取得する
     * 
     * @param index
     * @return  選択肢の文字列。
     */
    public String getAnswer(int index)
    {
        if (index == 0)
        {
        	// index番号が0の場合には、正解を返す。
        	return (correctAnswer);
        }
        if ((index < 0)||(index >= currentSelectionList))
        {
            return (null);
        }
        return (answers.get(index - 1));        	
    }

    /**
     *    問題のヒントを応答する
     * 
     * @return
     */
    public String getHintText()
    {
    	return (hintText);
    }

    /**
     *    問題の解説を応答する
     * 
     * @return
     */
    public String getDetailText()
    {
    	return (detailText);
    }

    /**
     *    問題について、より詳しく知るためのURLを応答する
     * 
     * @return
     */
    public String getOptionText()
    {
    	return (optionText);
    }

    /**
     *    Parcelable インタフェースの実装
     * 
     */
    public int describeContents()
    {
        return (0);  
    }

    /**
     *    Parcelable インタフェースの実装
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
     *    Parcelable インタフェースの実装
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
     *    Parcelable インタフェースの実装
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
