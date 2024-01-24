package jp.sfjp.gokigen.okaken;

import android.graphics.Bitmap;

public interface IGameInformationProvider
{
	public static final int STATUS_READY = 1;
    public static final int STATUS_GO = 2;
    public static final int STATUS_GAME_PLAYING = 3;
    public static final int STATUS_GAME_OVER = 4;
    
    public static final int ANSWER_NOT_YET = 0;  // Ç‹ÇæìöÇ¶ÇƒÇ‡ÇÁÇ¡ÇƒÇ¢Ç»Ç¢
    public static final int ANSWER_CORRECT = 1;   // ê≥âÅI
    public static final int ANSWER_WRONG  = -1;  // ä‘à·Ç¢
	
	public abstract int changeGameStatus(int status);
	public abstract int getCurrentGameStatus();

    public abstract int changeAnswerStatus(int status, long changedTime, MoleGameQuestionHolder question);
    public abstract int getCurrentAnswerStatus();

    public abstract int    getProvidedAnswers();
    public abstract int    getCorrectAnswers();
    public abstract int    getWrongAnswers();
    public abstract int    getCurrentLevel();
    public abstract float getRemainPercent();
    public abstract float getScore(int category);    
    public abstract Bitmap getCurrentLevelbitmap();
    public abstract Bitmap getResultLevelbitmap();

    public abstract MoleGameQuestionHolder getGameQuestion(int index);

}
