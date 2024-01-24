package jp.sfjp.gokigen.okaken;

import android.content.Context;

/**
 *   実行結果の取得
 * 
 * @author MRSa
 *
 */
public interface IResultProvider
{
	/**  結果のスコアを応答する **/
    public abstract float getScore(int category);   

    /**  ゲームレベルを応答する **/
    public abstract int getResultGameLevel();
    
    /**  回答した数を取得する **/
    public abstract int getNumberOfAnsweredQuestions();

    /**  回答した情報を取得する **/
    public abstract SymbolListArrayItem getAnsweredInformation(Context context, int index);
}
