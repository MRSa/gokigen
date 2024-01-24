package jp.sfjp.gokigen.okaken;

import android.content.Context;

/**
 *   ���s���ʂ̎擾
 * 
 * @author MRSa
 *
 */
public interface IResultProvider
{
	/**  ���ʂ̃X�R�A���������� **/
    public abstract float getScore(int category);   

    /**  �Q�[�����x������������ **/
    public abstract int getResultGameLevel();
    
    /**  �񓚂��������擾���� **/
    public abstract int getNumberOfAnsweredQuestions();

    /**  �񓚂��������擾���� **/
    public abstract SymbolListArrayItem getAnsweredInformation(Context context, int index);
}
