package jp.sfjp.gokigen.okaken;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *    カテゴリ別のスコア情報を記録する
 * 
 * @author MRSa
 *
 */
public class ScoreSummaryHolder  implements Parcelable 
{
    private int correct = 0;
    private int wrong = 0;
    private int timeout = 0;

    private long answeredTime = 0;
    private int category = 0;
    
    public ScoreSummaryHolder(int category)
    {
    	this.category = category;
    }
    
    public void addAnsweredTime(long time)
    {
    	answeredTime = answeredTime + time;
    }

    public void incrementCorrect()
    {
    	correct++;
    }
    public void incrementWrong()
    {
    	wrong++;
    }
    public void incrementTimeout()
    {
    	timeout++;
    }

    public float getScore()
    {
    	if ((correct == 0)&&(wrong == 0))
    	{
    		return (0.0f);
    	}        	
	    //Log.v(Gokigen.APP_IDENTIFIER, "Category:" + category + " correct : " + correct + "  wrong : " + wrong + "  timeout : " + timeout);
    	float score = (float) correct / (float) (correct + wrong + timeout);
    	return (score);
    }

    public int getCorrect()
    {
    	return (correct);
    }
    
    public int getWrong()
    {
    	return (wrong);
    }

    public int getTimeout()
    {
    	return (timeout);
    }
    
    /**  そのうち使うかもしれないので... **/
    /**
    public long getAnsweredTime()
    {
    	return (answeredTime);
    }
    **/


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
    	out.writeInt(correct);
    	out.writeInt(wrong);
    	out.writeInt(timeout);
    	out.writeLong(answeredTime);
    	out.writeInt(category);
    }

    /**
     *    Parcelable インタフェースの実装
     * 
     */
    public static final Parcelable.Creator<ScoreSummaryHolder> CREATOR
        = new Parcelable.Creator<ScoreSummaryHolder>()
        {
    	    public ScoreSummaryHolder createFromParcel(Parcel in)
    	    {
    	    	return (new ScoreSummaryHolder(in));
    	    }
 
    	    public ScoreSummaryHolder[] newArray(int size)
    	    {
    	    	return (new ScoreSummaryHolder[size]);
    	    }    	    
        };

    /**
     *    Parcelable インタフェースの実装
     * 
     */
    private ScoreSummaryHolder(Parcel in)
    {
        correct = in.readInt();
        wrong = in.readInt();
        timeout = in.readInt();
        answeredTime = in.readLong();
        category = in.readInt();
    }


}
