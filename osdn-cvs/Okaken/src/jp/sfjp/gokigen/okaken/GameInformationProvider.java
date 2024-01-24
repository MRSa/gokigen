package jp.sfjp.gokigen.okaken;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 *    ゲームの状態や時間、回答を提供するクラス
 * 
 * @author MRSa
 */
public class GameInformationProvider implements IGameInformationProvider
{
    public static final int NUMBER_OF_QUESTIONS = 9;
    public static final int WAITANSWER_TIMEOUT = 30; //25;
    public static final int ANSWERED_TIMEOUT = 1;  // 

    private static final long SHOW_GAME_PLAYING_MSECONDS  = 25000;   //  25000ms = 25sec.
    //private static final long SHOW_GAME_PLAYING_MSECONDS  = 10000;   //  10sec. (for demo version)
    private static final long SHOW_TITILE_MSECONDS = 1500;  // 1500ms
    private static final long SHOW_READY_MSECONDS = 750;   //   750ms
    
    private long currentTimer = 0;
    private long countUpOffset = 0;

    private int waitAnswerTimer = 0;
    private int gameAnswer = ANSWER_NOT_YET;
    private int currentGameStatus = STATUS_READY;
    private int currentLevel = 0;

    private int wrongAnswer = 0;
    private int correctAnswer = 0;
    //private int providedAnswer = 0;

    private QuestionnaireProvider questionProvider = null;
    private IGameStatusListener listener = null;

    private int nofLevel = 0; 
    private ArrayList<Bitmap> momotaroBitmaps = null;

    private int maxWrongCombo = 0;
    private int maxCorrectCombo = 0;
    private int comboCount = 0;
    private boolean previousAnswerIsCorrect = false;
    
    /**
     * 
     * 
     * @param milliseconds
     * @param statusListener
     */
    public GameInformationProvider(Context context, long milliseconds, QuestionnaireProvider provider, IGameStatusListener statusListener)
	{
		countUpOffset = milliseconds; 
		listener = statusListener;
    	
		// ゲームの情報を入れてみた
		questionProvider = provider;
		//questionProvider = new QuestionnaireProvider(context, NUMBER_OF_QUESTIONS);
    	
		// ももたろさんのビットマップを読み込んでみる
    	prepareMomotaroBitmaps(context);
	}

    
    /**
     *    ももたろさんのビットマップを読み込んで保持する
     * 
     */
    private void prepareMomotaroBitmaps(Context context)
    {
    	momotaroBitmaps = new ArrayList<Bitmap>();
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l0));
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l1));
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l2));
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l3));
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l4));
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l5));
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l6));
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l7));
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l8));
    	momotaroBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l9));
    	nofLevel = momotaroBitmaps.size() - 1;

    	currentLevel = 3;
    }

    /**
     * 
     * 
     * @return
     */
	public int changeGameStatus(int status)
	{
    	switch (status)
    	{
    	  case STATUS_READY:
        	break;
    	  case STATUS_GAME_OVER:
        	break;
    	  case STATUS_GO:
    	  case STATUS_GAME_PLAYING:
    	  default:
    		// ステータス遷移異常、ステータスは更新しない
    		return (currentGameStatus);
    	}
		currentGameStatus = status;	
		return (currentGameStatus);
	}

    /**
     *   現在の状態を応答する
     * 
     * @return
     */
	public int getCurrentGameStatus()
	{
		return (currentGameStatus);		
	}

	/**
	 *   出題した問題数を答える
	 * 
	 */
    public int getProvidedAnswers()
    {
    	return (questionProvider.getProvidedAnswers());
    }

    /**
     *  正しく回答した数を答える
     * 
     * @return
     */
	public int getCorrectAnswers()
    {
        return (correctAnswer);
    }

	/**
     *    間違った回答数を答える
     * 
     * @return
     */
	public int getWrongAnswers()
    {
        return (wrongAnswer);
    }

    /**
     *    現在のゲームレベルを取得する
     * 
     * @return
     */
    public int    getCurrentLevel()
    {
    	return (currentLevel);    	
    }

    /**
     *    現在のゲームレベルのイメージを取得する
     * 
     */
    public Bitmap getCurrentLevelbitmap()
    {
    	return (momotaroBitmaps.get(currentLevel));
    }

    /**
     *    ゲーム結果レベルのイメージを取得する
     * 
     */
    public Bitmap getResultLevelbitmap()
    {
    	int level = questionProvider.getResultGameLevel();
    	return (momotaroBitmaps.get(level));
    }
    
    /**
     *    スコアを応答する
     *    
     *    @param category スコアのカテゴリ指定（０の時は総合スコア）
     * 
     */
    public float getScore(int category)
    {
    	return (questionProvider.getScore(category));
    }

    /**
     *    残り時間の割合を取得する
     * 
     * @return
     */
    public float getRemainPercent()
    {
    	float time = SHOW_GAME_PLAYING_MSECONDS - currentTimer;
    	if (time < 0.0f)
    	{
    		time = 0.0f;
    	}
    	return (time / SHOW_GAME_PLAYING_MSECONDS);
    }

    /**
     * 　問題に回答したかどうかを応答する
     * 
     * @return
     */
    public int getCurrentAnswerStatus()
    {
    	return (gameAnswer);
    }

    /**
     * 
     * 
     * @param status
     */
    public int changeAnswerStatus(int status, long changedTime, MoleGameQuestionHolder question)
    {
    	switch (status)
    	{
    	  case ANSWER_WRONG:
    		if (previousAnswerIsCorrect == true)
    		{
    			// 正解から間違いに切り替わった...
    			comboCount = 0;
    			
    			// 現在のレベルを元に戻す
    			currentLevel = 4;
    		}
    		else
    		{
    			// 間違いが連続した
    			comboCount = comboCount + 1;
    			if (maxWrongCombo < comboCount)
    			{
    				maxWrongCombo = comboCount;
    			}
    			
    			if (currentLevel < nofLevel)
    			{
    				// 一つ大きなレベルにする
        			currentLevel = currentLevel + 1;
    			}
    		}
    		wrongAnswer = wrongAnswer + 1;
        	questionProvider.setAnsweredTime(false, changedTime, question);
    		previousAnswerIsCorrect = false;
        	break;

    	  case ANSWER_CORRECT:
      		if (previousAnswerIsCorrect == false)
    		{
    			// 間違いが正解に切り替わった...
    			comboCount = 0;

    			// 現在のレベルを元に戻す
    			currentLevel = 3;
    		}
    		else
    		{
    			// 正解が連続した
    			comboCount = comboCount + 1;
    			if (maxCorrectCombo < comboCount)
    			{
    				maxCorrectCombo = comboCount;
    			}
    			if (currentLevel > 0)
    			{
        			// 一つ小さなレベルにする
        			currentLevel = currentLevel - 1;    				
    			}
    		}
    		correctAnswer  = correctAnswer + 1;
        	questionProvider.setAnsweredTime(true, changedTime, question);
    		previousAnswerIsCorrect = true;
        	break;
    	  case ANSWER_NOT_YET:
          	break;
    	  case STATUS_GAME_PLAYING:
    	  default:
    		// ステータス遷移異常、ステータスは更新しない
    		return (gameAnswer);
    	}
    	gameAnswer = status;
    	waitAnswerTimer = 0;
    	return (status);
    }

    /**
     * 
     * 
     * @return
     */
    private boolean updateAnswerStatus()
    {
    	if (currentGameStatus == STATUS_GAME_OVER)
    	{
    		//  ゲーム状態が「ゲーム終了」の場合には、回答状態を更新しないようにする
    		return (true);
    	}

    	waitAnswerTimer++;
    	if (((gameAnswer == ANSWER_NOT_YET)&&(waitAnswerTimer > WAITANSWER_TIMEOUT))||
    		((gameAnswer != ANSWER_NOT_YET)&&(waitAnswerTimer > ANSWERED_TIMEOUT)))
    	{
    		// 未回答時間のタイムアウトを検出した。または、回答結果の待ち時間を超えた（次の問題文に更新する）
    		questionProvider.updateQuestionData();
    		if (currentGameStatus == STATUS_GAME_PLAYING)
    		{
                /**
    			if ((((providedAnswer - (correctAnswer + wrongAnswer)) % 2) == 0)&&(currentLevel < nofLevel))
        		{
        			// 一つ大きなレベルにする
        			currentLevel = currentLevel + 1;
        		}
        		**/
    		}

    		// 回答を未回答状態にする
        	gameAnswer = ANSWER_NOT_YET;
    		waitAnswerTimer = 0;
            return (true);
    	}
    	return (true);
    }

    /**
     * 
     * 
     * @return
     */
    private boolean updateGameStatus()
    {
    	boolean isTransitionOccurred = false;
    	switch (currentGameStatus)
    	{
    	  case STATUS_READY:
    		if (currentTimer > SHOW_TITILE_MSECONDS)
    		{
    			// ステート遷移を検出...
    			currentGameStatus = STATUS_GO;
    			isTransitionOccurred = true;
    		}
        	break;
    	  case STATUS_GO:
      		if (currentTimer > SHOW_READY_MSECONDS)
    		{
    			// ステート遷移を検出...
    			currentGameStatus = STATUS_GAME_PLAYING;
    			isTransitionOccurred = true;
    		}
        	break;
    	  case STATUS_GAME_PLAYING:
    		if (currentTimer > SHOW_GAME_PLAYING_MSECONDS)
    		{
    			// ステート遷移を検出...
    			currentGameStatus = STATUS_GAME_OVER;
    			isTransitionOccurred = true;

    			// ゲーム終了。ゲーム状態をチェックする
            	//questionProvider.setAnsweredTime(false, System.currentTimeMillis());
        		Log.v(Gokigen.APP_IDENTIFIER, ":::::GAME OVER:::::");
        		questionProvider.analysisAnsweredQuestions();
        		Log.v(Gokigen.APP_IDENTIFIER, "    MAX CORRECT COMBO : " + maxCorrectCombo);
        		Log.v(Gokigen.APP_IDENTIFIER, "    MAX WRONG   COMBO : " + maxWrongCombo);
    		}
        	break;
    	  case STATUS_GAME_OVER:
        	break;
    	  default:
    	    break;
    	}
    	return (isTransitionOccurred);
    }

    /**
	 *    問題の情報を取得する
	 * 
	 */
    public MoleGameQuestionHolder getGameQuestion(int index)
    {
        return (questionProvider.getGameQuestion(index));
    }
    
    /**
     *   タイムアウト受信時の処理...
     * 
     */
    public void receivedTimeout()
    {
        //Log.v(Gokigen.APP_IDENTIFIER, "receivedTimeout()");    	
    	currentTimer = currentTimer + countUpOffset;
        if (updateGameStatus() == true)
        {
        	currentTimer = 0;
        	if (listener != null)
        	{
        		// 状態の変更を通知する
        		listener.changedCurrentGameStatus(currentGameStatus);
        	}
        }
        if (updateAnswerStatus() == true)
        {
        	if (listener != null)
        	{
        		// 状態の変更を通知する
        		listener.triggeredGameStatus(currentGameStatus);
        	}
        }
    }

    /**
     * 
     * 
     * @author MRSa
     */
     public interface IGameStatusListener
     {
	     public abstract void changedCurrentGameStatus(int status);
	     public abstract void triggeredGameStatus(int positionIndex);
     }
}
