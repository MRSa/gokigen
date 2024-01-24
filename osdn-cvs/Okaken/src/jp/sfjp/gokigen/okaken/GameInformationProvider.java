package jp.sfjp.gokigen.okaken;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 *    �Q�[���̏�Ԃ⎞�ԁA�񓚂�񋟂���N���X
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
    	
		// �Q�[���̏������Ă݂�
		questionProvider = provider;
		//questionProvider = new QuestionnaireProvider(context, NUMBER_OF_QUESTIONS);
    	
		// �������낳��̃r�b�g�}�b�v��ǂݍ���ł݂�
    	prepareMomotaroBitmaps(context);
	}

    
    /**
     *    �������낳��̃r�b�g�}�b�v��ǂݍ���ŕێ�����
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
    		// �X�e�[�^�X�J�ڈُ�A�X�e�[�^�X�͍X�V���Ȃ�
    		return (currentGameStatus);
    	}
		currentGameStatus = status;	
		return (currentGameStatus);
	}

    /**
     *   ���݂̏�Ԃ���������
     * 
     * @return
     */
	public int getCurrentGameStatus()
	{
		return (currentGameStatus);		
	}

	/**
	 *   �o�肵����萔�𓚂���
	 * 
	 */
    public int getProvidedAnswers()
    {
    	return (questionProvider.getProvidedAnswers());
    }

    /**
     *  �������񓚂������𓚂���
     * 
     * @return
     */
	public int getCorrectAnswers()
    {
        return (correctAnswer);
    }

	/**
     *    �Ԉ�����񓚐��𓚂���
     * 
     * @return
     */
	public int getWrongAnswers()
    {
        return (wrongAnswer);
    }

    /**
     *    ���݂̃Q�[�����x�����擾����
     * 
     * @return
     */
    public int    getCurrentLevel()
    {
    	return (currentLevel);    	
    }

    /**
     *    ���݂̃Q�[�����x���̃C���[�W���擾����
     * 
     */
    public Bitmap getCurrentLevelbitmap()
    {
    	return (momotaroBitmaps.get(currentLevel));
    }

    /**
     *    �Q�[�����ʃ��x���̃C���[�W���擾����
     * 
     */
    public Bitmap getResultLevelbitmap()
    {
    	int level = questionProvider.getResultGameLevel();
    	return (momotaroBitmaps.get(level));
    }
    
    /**
     *    �X�R�A����������
     *    
     *    @param category �X�R�A�̃J�e�S���w��i�O�̎��͑����X�R�A�j
     * 
     */
    public float getScore(int category)
    {
    	return (questionProvider.getScore(category));
    }

    /**
     *    �c�莞�Ԃ̊������擾����
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
     * �@���ɉ񓚂������ǂ�������������
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
    			// ��������ԈႢ�ɐ؂�ւ����...
    			comboCount = 0;
    			
    			// ���݂̃��x�������ɖ߂�
    			currentLevel = 4;
    		}
    		else
    		{
    			// �ԈႢ���A������
    			comboCount = comboCount + 1;
    			if (maxWrongCombo < comboCount)
    			{
    				maxWrongCombo = comboCount;
    			}
    			
    			if (currentLevel < nofLevel)
    			{
    				// ��傫�ȃ��x���ɂ���
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
    			// �ԈႢ�������ɐ؂�ւ����...
    			comboCount = 0;

    			// ���݂̃��x�������ɖ߂�
    			currentLevel = 3;
    		}
    		else
    		{
    			// �������A������
    			comboCount = comboCount + 1;
    			if (maxCorrectCombo < comboCount)
    			{
    				maxCorrectCombo = comboCount;
    			}
    			if (currentLevel > 0)
    			{
        			// ������ȃ��x���ɂ���
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
    		// �X�e�[�^�X�J�ڈُ�A�X�e�[�^�X�͍X�V���Ȃ�
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
    		//  �Q�[����Ԃ��u�Q�[���I���v�̏ꍇ�ɂ́A�񓚏�Ԃ��X�V���Ȃ��悤�ɂ���
    		return (true);
    	}

    	waitAnswerTimer++;
    	if (((gameAnswer == ANSWER_NOT_YET)&&(waitAnswerTimer > WAITANSWER_TIMEOUT))||
    		((gameAnswer != ANSWER_NOT_YET)&&(waitAnswerTimer > ANSWERED_TIMEOUT)))
    	{
    		// ���񓚎��Ԃ̃^�C���A�E�g�����o�����B�܂��́A�񓚌��ʂ̑҂����Ԃ𒴂����i���̖�蕶�ɍX�V����j
    		questionProvider.updateQuestionData();
    		if (currentGameStatus == STATUS_GAME_PLAYING)
    		{
                /**
    			if ((((providedAnswer - (correctAnswer + wrongAnswer)) % 2) == 0)&&(currentLevel < nofLevel))
        		{
        			// ��傫�ȃ��x���ɂ���
        			currentLevel = currentLevel + 1;
        		}
        		**/
    		}

    		// �񓚂𖢉񓚏�Ԃɂ���
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
    			// �X�e�[�g�J�ڂ����o...
    			currentGameStatus = STATUS_GO;
    			isTransitionOccurred = true;
    		}
        	break;
    	  case STATUS_GO:
      		if (currentTimer > SHOW_READY_MSECONDS)
    		{
    			// �X�e�[�g�J�ڂ����o...
    			currentGameStatus = STATUS_GAME_PLAYING;
    			isTransitionOccurred = true;
    		}
        	break;
    	  case STATUS_GAME_PLAYING:
    		if (currentTimer > SHOW_GAME_PLAYING_MSECONDS)
    		{
    			// �X�e�[�g�J�ڂ����o...
    			currentGameStatus = STATUS_GAME_OVER;
    			isTransitionOccurred = true;

    			// �Q�[���I���B�Q�[����Ԃ��`�F�b�N����
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
	 *    ���̏����擾����
	 * 
	 */
    public MoleGameQuestionHolder getGameQuestion(int index)
    {
        return (questionProvider.getGameQuestion(index));
    }
    
    /**
     *   �^�C���A�E�g��M���̏���...
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
        		// ��Ԃ̕ύX��ʒm����
        		listener.changedCurrentGameStatus(currentGameStatus);
        	}
        }
        if (updateAnswerStatus() == true)
        {
        	if (listener != null)
        	{
        		// ��Ԃ̕ύX��ʒm����
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
