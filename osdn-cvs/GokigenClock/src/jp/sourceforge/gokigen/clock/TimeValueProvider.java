package jp.sourceforge.gokigen.clock;

import java.util.Calendar;

/**
 *  現在時刻を配布する
 * @author MRSa
 *
 */
public class TimeValueProvider implements INumberValueProvider
{
    private Calendar mCalendar          = null;
    private int     mCurrentSeconds     = 0;
    private int     mCurrentMinites     = 0;
    private int     mCurrentHour        = 0;

    /**
     *  コンストラクタ
     */
    public TimeValueProvider()
    {

    }
    
    /**
     *  データ情報を更新する
     */
    public void update()
    {
        mCalendar = Calendar.getInstance();
        mCurrentSeconds = mCalendar.get(Calendar.SECOND);
        mCurrentMinites = mCalendar.get(Calendar.MINUTE);
        mCurrentHour    = mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     *  １桁の数値（文字）を応答する
     */
    //@Override
    public int getNumber(int index)
    {
        int returnNumber = 0;
        if (index == 0)
        {
            returnNumber = (mCurrentSeconds % 10);
        }
        else if (index == 1)
        {
            returnNumber = (mCurrentSeconds / 10);
        }
        else if (index == 2)
        {
            returnNumber = (INumberValueProvider.COLON);
        }
        else if (index == 3)
        {
            returnNumber = (mCurrentMinites % 10);
        }
        else if (index == 4)
        {
            returnNumber = (mCurrentMinites / 10);
        }
        else if (index == 5)
        {
            returnNumber = (INumberValueProvider.COLON);
        }
        else if (index == 6)
        {
            returnNumber = (mCurrentHour % 10);
        }
        else if (index == 7)
        {
            returnNumber = (mCurrentHour / 10);
        }
        else
        {
            // 指定された桁の数値が異常の場合には、空白を応答する
            returnNumber = (INumberValueProvider.SPACE);
        }
        return (returnNumber);
    }

    /**
     *  表示するデータの桁数を応答する
     */
    //@Override
    public int getNumberOfDigits()
    {
        return (8);
    }
}
