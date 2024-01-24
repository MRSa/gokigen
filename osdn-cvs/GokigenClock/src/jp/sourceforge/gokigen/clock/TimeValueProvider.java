package jp.sourceforge.gokigen.clock;

import java.util.Calendar;

/**
 *  ���ݎ�����z�z����
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
     *  �R���X�g���N�^
     */
    public TimeValueProvider()
    {

    }
    
    /**
     *  �f�[�^�����X�V����
     */
    public void update()
    {
        mCalendar = Calendar.getInstance();
        mCurrentSeconds = mCalendar.get(Calendar.SECOND);
        mCurrentMinites = mCalendar.get(Calendar.MINUTE);
        mCurrentHour    = mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     *  �P���̐��l�i�����j����������
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
            // �w�肳�ꂽ���̐��l���ُ�̏ꍇ�ɂ́A�󔒂���������
            returnNumber = (INumberValueProvider.SPACE);
        }
        return (returnNumber);
    }

    /**
     *  �\������f�[�^�̌�������������
     */
    //@Override
    public int getNumberOfDigits()
    {
        return (8);
    }
}
