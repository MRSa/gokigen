package jp.sfjp.gokigen.okaken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class QuestionnaireProvider  implements Parcelable, IResultProvider
{
    private final int SHUFFLE_COUNT = 250;   //  ��蕶���܂��܂������
	
	private ArrayList<MoleGameQuestionHolder> gameQuestionObjects = null;

    private ArrayList<QuestionnaireHolder> questionnairesList = null;
    private ArrayList<QuestionnaireHolder> questionnairesListCat1 = null;  // �J�e�S���P
    private ArrayList<QuestionnaireHolder> questionnairesListCat2 = null;  // �J�e�S���Q
    private ArrayList<QuestionnaireHolder> questionnairesListCat3 = null;  // �J�e�S�� 3
    private ArrayList<QuestionnaireHolder> questionnairesListCat4 = null;  // �J�e�S�� 4

    private ArrayList<ScoreSummaryHolder> categoryScores = null;  // �J�e�S���ʂ̃X�R�A
    private ArrayList<AnsweredQuestionInformation> answerTimeMillsList = null;    // �񓚂������

    private int   currentQuestionProvided = -1;
    private int   numberOfQuestions = 0;
    private int   gameResultLevel = 4;

    private Random shuffleGenerator = null;
    private Random randomGenerator = null;

    /**
     *   �R���X�g���N�^
     *   
     * @param context
     * @param numberOfQuestions
     */
    public QuestionnaireProvider(Context context, int numberOfQuestions)
    {
    	// ����������
    	randomGenerator = new Random();
    	
    	// �Q�[���̏������Ă݂�
    	this.numberOfQuestions = numberOfQuestions;
		gameQuestionObjects = new ArrayList<MoleGameQuestionHolder>();
		for (int index = 0; index < numberOfQuestions; index++)
		{
		    gameQuestionObjects.add(new MoleGameQuestionHolder());
		}

		// ��蕶��ǂݍ���ł݂�
		loadQuestionnaires(context, R.raw.questionnaire);

        // �ǂݍ��񂾖�蕶���V���b�t������
		shuffleQuestionnaires();

		// ��蕶�̏o�菇�Ԃ����グ��
		prepareQuestionnaries();
		
    }

    /**
	 *    ���ݏo�蒆�̖��Ǘ��I�u�W�F�N�g����������(�Ȃ����null)
	 * 
	 */
    public MoleGameQuestionHolder getGameQuestion(int index)
    {
    	if ((index < 0)||(index >= numberOfQuestions))
    	{
    		return (null);
    	}
    	try
    	{
            return (gameQuestionObjects.get(index));
    	}
    	catch (Exception ex)
    	{
    		
    	}
    	return (null);
    }

    /**
     *     �񓚃f�[�^���N���A����
     * 
     */
    private void resetQuestionData()
    {
		for (int index = 0; index < numberOfQuestions; index++)
		{
			gameQuestionObjects.get(index).resetQuestion();
		}
    }
    
    /**
     *    �����J�e�S���̏��Ԃ��܂�ׂ�Ȃ��o�肳���悤�ɕ��ׂ�
     * 
     */
    private void prepareQuestionnaries()
    {
    	questionnairesList = null;
		questionnairesList =  new ArrayList<QuestionnaireHolder>();
		answerTimeMillsList = null;
        answerTimeMillsList = new ArrayList<AnsweredQuestionInformation>();
		
		int cat1Size = questionnairesListCat1.size();
		int cat2Size = questionnairesListCat2.size();
		int cat3Size = questionnairesListCat3.size();
		int cat4Size = questionnairesListCat4.size();
		int cat1Index = 0;
		int cat2Index = 0;
		int cat3Index = 0;
		int cat4Index = 0;
		
		do
		{
			if (cat1Index < cat1Size)
			{
				QuestionnaireHolder question1 = questionnairesListCat1.get(cat1Index);
				questionnairesList.add(question1);
				answerTimeMillsList.add(new AnsweredQuestionInformation(1));
				cat1Index++;
			}
			if (cat2Index < cat2Size)
			{
				QuestionnaireHolder question2 = questionnairesListCat2.get(cat2Index);
				questionnairesList.add(question2);
				answerTimeMillsList.add(new AnsweredQuestionInformation(2));
				cat2Index++;
			}
			if (cat3Index < cat3Size)
			{
				QuestionnaireHolder question3 = questionnairesListCat3.get(cat3Index);
				questionnairesList.add(question3);
				answerTimeMillsList.add(new AnsweredQuestionInformation(3));
				cat3Index++;
			}
			if (cat4Index < cat4Size)
			{
				QuestionnaireHolder question4 = questionnairesListCat4.get(cat4Index);
				questionnairesList.add(question4);
				answerTimeMillsList.add(new AnsweredQuestionInformation(4));
				cat4Index++;
			}
		} while ((cat1Index < cat1Size)&&(cat2Index < cat2Size)&&(cat3Index < cat3Size)&&(cat3Index < cat3Size));
		
		// ����ʂ̃R���e�i���N���A����
		questionnairesListCat1 = null;
		questionnairesListCat2 = null;
		questionnairesListCat3 = null;
		questionnairesListCat4 = null;
		System.gc();		
    }

    /**
     *    �w�肳�ꂽ���\�[�XID��Raw�t�@�C������A���f�[�^��ǂݏo���B
     *    �i�����R�[�h��UTF-8�ŋL�q���ꂽCSV�`���ŁA�f�[�^���ȉ��̏��Ԃɕ���ł�����́j
     * 
     *       (�\��),�J�e�S��,���x��,(�\��),(�q���g),(�R�����g),����,�듚,...
     *         - 0: (�\��)      : �\��̈�(���݃[�������Ă���)
     *         - 1: �J�e�S��   : ���̎��(1:�y�n,2:�l��,3:�H�ו�,4:���i���̑�)
     *         - 2: ���x��      : ���̓�Փx(���ݖ��g�p)
     *         - 3: (Option)   : �񓚂����ڂ����m�邽�߂�URL(���ɐ������܂߂��I�����̐������Ă���A���ݖ��g�p) 
     *         - 4: (�q���g)    : �񓚂ɖ��������ɕ\������q���g
     *         - 5: (�R�����g) : �񓚂ɂ��ẴR�����g
     *         - 6: ����          : �����̓���
     *         - 7�`: �듚       : �Ԉ�������� �i7�Ԗڈȍ~�̃f�[�^)
     * 
     * @param resId   ���\�[�X�t�@�C����ID
     */
    private void loadQuestionnaires(Context context, int resId)
    {
    	try
    	{
    	    questionnairesListCat1 =  new ArrayList<QuestionnaireHolder>();
    	    questionnairesListCat2 =  new ArrayList<QuestionnaireHolder>();
    	    questionnairesListCat3 =  new ArrayList<QuestionnaireHolder>();
    	    questionnairesListCat4 =  new ArrayList<QuestionnaireHolder>();

    	    Resources res = context.getResources();  
        	BufferedReader buf = new BufferedReader(new InputStreamReader(res.openRawResource(resId),  "UTF-8"));
        	while (buf.ready())
        	{
        		//
        		String lineValue = buf.readLine();
        		String[] values = lineValue.split(",");
        		if (values.length > 7)
        		{
        			// ��蕶��I�����ē��ꍞ��
        			int category = Integer.parseInt(values[1]);
         			QuestionnaireHolder questionnaire = new QuestionnaireHolder(category, values[6], values[4], values[5], values[3]);
        		    for (int index = 7; index < values.length; index++)
        		    {
        		    	questionnaire.addAnswer(values[index]);
        		    }
        		    if (category == 1)
        		    {
            		    questionnairesListCat1.add(questionnaire);         		    	
        		    }
        		    else if (category == 2)
        		    {
            		    questionnairesListCat2.add(questionnaire);         		    	
        		    }
        		    else if (category == 3)
        		    {
            		    questionnairesListCat3.add(questionnaire);         		    	
        		    }
        		    else //if (category == 4)
        		    {
            		    questionnairesListCat4.add(questionnaire);         		    	
        		    }        		    
        		}
        		else
        		{
                    Log.v(Gokigen.APP_IDENTIFIER, "loadQuestionnaires() : Wong data '" + lineValue + "'");
        		}
            }
        	buf.close();
    	}
    	catch (Exception ex)
    	{
    		//
    		Log.v(Gokigen.APP_IDENTIFIER, "QuestionnaireProvider::loadQuestionnaires() ex. : " + ex.getMessage());
    	}
    	finally
    	{
             // 
    	}
    }

    /**
     *    �����V���b�t������
     * 
     */
    private void shuffleQuestionnaires()
    {
    	// ����������
    	shuffleGenerator = new Random();

    	int catSize = questionnairesListCat1.size();
		for (int index = 0; index < SHUFFLE_COUNT; index++)
		{
    	    int index1 = shuffleGenerator.nextInt(catSize);
    	    int index2 = shuffleGenerator.nextInt(catSize);
    	    
    	    QuestionnaireHolder obj1 = questionnairesListCat1.get(index1);
    	    QuestionnaireHolder obj2 = questionnairesListCat1.get(index2);
    	    questionnairesListCat1.set(index2, obj1);
    	    questionnairesListCat1.set(index1, obj2);
		}

		catSize = questionnairesListCat2.size();
		for (int index = 0; index < SHUFFLE_COUNT; index++)
		{
    	    int index1 = shuffleGenerator.nextInt(catSize);
    	    int index2 = shuffleGenerator.nextInt(catSize);
    	    
    	    QuestionnaireHolder obj1 = questionnairesListCat2.get(index1);
    	    QuestionnaireHolder obj2 = questionnairesListCat2.get(index2);
    	    questionnairesListCat2.set(index2, obj1);
    	    questionnairesListCat2.set(index1, obj2);
		}

		catSize = questionnairesListCat3.size();
		for (int index = 0; index < SHUFFLE_COUNT; index++)
		{
    	    int index1 = shuffleGenerator.nextInt(catSize);
    	    int index2 = shuffleGenerator.nextInt(catSize);
    	    
    	    QuestionnaireHolder obj1 = questionnairesListCat3.get(index1);
    	    QuestionnaireHolder obj2 = questionnairesListCat3.get(index2);
    	    questionnairesListCat3.set(index2, obj1);
    	    questionnairesListCat3.set(index1, obj2);
		}

		catSize = questionnairesListCat4.size();
		for (int index = 0; index < SHUFFLE_COUNT; index++)
		{
    	    int index1 = shuffleGenerator.nextInt(catSize);
    	    int index2 = shuffleGenerator.nextInt(catSize);
    	    
    	    QuestionnaireHolder obj1 = questionnairesListCat4.get(index1);
    	    QuestionnaireHolder obj2 = questionnairesListCat4.get(index2);
    	    questionnairesListCat4.set(index2, obj1);
    	    questionnairesListCat4.set(index1, obj2);
		}
    }
    
    /**
     * 
     * @param isCorrect  ���� or �ԈႢ
     * @param timeMills  �񓚂�������
     * @param answeredText  �񓚂���������
     */
    public void setAnsweredTime(boolean isCorrect, long timeMills, MoleGameQuestionHolder question)
    {
    	AnsweredQuestionInformation get = answerTimeMillsList.get(currentQuestionProvided);
    	int category = get.getCategory();
    	long startTime = get.getStartTime();
    	answerTimeMillsList.set(currentQuestionProvided, new AnsweredQuestionInformation(category, isCorrect, timeMills, startTime, question.getQuestion()));    	
    }

    /**
     *    �񓚏I�����̉񓚏󋵂��m�F����
     * 
     */
    public void analysisAnsweredQuestions()
    {
    	categoryScores = null;
    	categoryScores = new ArrayList<ScoreSummaryHolder>();
    	categoryScores.add(new  ScoreSummaryHolder(1));
    	categoryScores.add(new  ScoreSummaryHolder(2));
    	categoryScores.add(new  ScoreSummaryHolder(3));
    	categoryScores.add(new  ScoreSummaryHolder(4));

    	try
    	{
        	Log.v(Gokigen.APP_IDENTIFIER, "------ analysisAnsweredQuestions() : nofAnswer (" + currentQuestionProvided +") ------");
    	    for (int index = 0; index < currentQuestionProvided; index++)
        	{
        		AnsweredQuestionInformation result = answerTimeMillsList.get(index);
        		long answeredTime = result.getAnsweredTime() - result.getStartTime();
        		ScoreSummaryHolder holder = categoryScores.get(result.getCategory() - 1);
        		if (holder != null)
        		{
        			if (result.getAnsweredString()  == null)
        			{
        				// �^�C���A�E�g���o
        				holder.incrementTimeout();
        			}
        			else if (result.getIsCorrect() == true)
        			{
        				// ����
        				holder.incrementCorrect();
        				holder.addAnsweredTime(answeredTime);
        			}
        			else
        			{
        				// �듚
        				holder.incrementWrong();
        			}
        		}
        	    Log.v(Gokigen.APP_IDENTIFIER, (index + 1) + "[" +  result.getCategory() + "]" + result.getAnsweredString() + " (" + result.getIsCorrect() +  ") Time: " + answeredTime + " ms");
        	}
    	    Log.v(Gokigen.APP_IDENTIFIER, "- - - - - - - - - - -");
    	}
    	catch (Exception ex)
    	{
    		//
    	}
    }

    /**
	 *   �o�肵����萔�𓚂���
	 * 
	 */
    public int getProvidedAnswers()
    {
    	return (currentQuestionProvided);
    }

    /**
     *    �g�[�^���̃X�R�A�𐶐����A��������i���ʂ͐������Ƃ��Ă����j
     * 
     * @return   ���ʂ͐�����
     */
    private float getTotalScore()
    {
    	int correct = 0;
    	int wrong = 0;
    	int timeout = 0;
    	int max = categoryScores.size();
    	for (int index = 0; index < max; index++)
    	{
    		ScoreSummaryHolder holder = categoryScores.get(index);
    		correct = correct + holder.getCorrect();
    		wrong = wrong + holder.getWrong();
    		timeout = timeout + holder.getTimeout();
    	}
    	if ((correct == 0)&&(wrong == 0))
    	{
    		// �[�������������Ȃ��悤�ɂ���...
    		gameResultLevel = 9;
    		return (0.0f);
    	}
	    //Log.v(Gokigen.APP_IDENTIFIER, "correct : " + correct + "  wrong : " + wrong + "  timeout : " + timeout);
    	float totalScore = (float) ((float) correct / (float) (correct + wrong + timeout));
    	gameResultLevel = calculateGameResultLevel(totalScore);
    	return (totalScore);
    }
    
    /**
     * 
     * 
     * @param score
     * @return
     */
    private int calculateGameResultLevel(float score)
    {
    	int level = 4;
    	if (score < 0.1)
    	{
    		level = 9;
    	}
    	else if (score < 0.15)
    	{
    		level = 8;
    	}
    	else if (score < 0.2)
    	{
    		level = 7;
    	}
    	else if (score < 0.3)
    	{
    		level = 6;
    	}
    	else if (score < 0.4)
    	{
    		level = 5;
    	}
    	else if (score < 0.6)
    	{
    		level = 4;
    	}
    	else if (score < 0.7)
    	{
    		level = 3;
    	}
    	else if (score < 0.8)
    	{
    		level = 2;
    	}
    	else if (score < 0.95)
    	{
    		level  = 1;
    	}
    	else
    	{
    		level = 0;
    	}
    	return (level);
    }
    
    /**
     *    �J�e�S���ʂ̃X�R�A�𐶐����A��������i���ʂ͐������Ƃ��Ă����j
     * 
     * @param category  �J�e�S���ԍ��i�P�͂��܂�j
     * @return
     */
    private float getCategoryScore(int category)
    {
    	ScoreSummaryHolder holder = categoryScores.get(category - 1);
    	return (holder.getScore());
    }

    /**
     *    �Q�[�����x������������
     *    (IResultProvider�̎���)
     */
    public  int getResultGameLevel()
    {
        return (gameResultLevel);	
    }

    /** 
     *    �񓚂���������������
     *    (IResultProvider�̎���)
     */
    public int getNumberOfAnsweredQuestions()
    {
    	//Log.v(Gokigen.APP_IDENTIFIER, "QuestionnaireProvider::getNumberOfAnsweredQuestions() : " + currentQuestionProvided);
    	return (currentQuestionProvided);    	
    }

    /**
     *     �w�肵���񓚏�����������
     *    (IResultProvider�̎���)
     * 
     */
    public SymbolListArrayItem getAnsweredInformation(Context context, int index)
    {
    	QuestionnaireHolder holder = questionnairesList.get(index);                // ���f�[�^���擾����
    	AnsweredQuestionInformation info = answerTimeMillsList.get(index);  // �񓚍σf�[�^���擾����
        if ((holder == null)||(info == null))
        {
        	Log.v(Gokigen.APP_IDENTIFIER, "QuestionnaireProvider::getAnsweredInformation() : " + index + " is null.");
        	return (null);
        }
        long time = info.getAnsweredTime() - info.getStartTime();
        if ((time > 100000)||(time < 0))
        {
        	// �񓚎��Ԃ��ُ킾�����ꍇ...
        	time = 0;
        }
        String detailString = " " + holder.getDetailText();  //
        String hintString = " " + holder.getHintText();       //
        String optionString = "  " + holder.getOptionText(); //
    	SymbolListArrayItem item = new SymbolListArrayItem(context, info.getIsCorrect(), info.getCategory(), info.getAnsweredString(),  holder.getAnswer(0), time, hintString, detailString, optionString);
    	return (item);
    }

    /**
     *    �񓚐��ʂ𓚂���
     * 
     * @param category   �J�e�S���ԍ��i�P�͂��܂�j
     * @return  �X�R�A�i0.0f �` 1.0f �܂ł̐��l�j
     */
    public float getScore(int category)
    {
        if (categoryScores == null)
        {
        	// �X�R�A��񂪐�������Ă��Ȃ��̂ŃX�R�A�̓[��
        	return (0.0f);
        }
        try
        {
        	if (category == 0)
        	{
        		// �����X�R�A���ق����ꍇ...
        		return (getTotalScore());
        	}
        	// �J�e�S���ʂ̃X�R�A���ق����ꍇ...
            return (getCategoryScore(category));
        }
        catch (Exception ex)
        {
        	// ��O����...�������Ȃ�
        }
        return (0.0f);
    }
    
    /**
     *    �����X�V����
     * 
     */
    public void updateQuestionData()
    {
    	// ��蕶��S�ăN���A����
    	resetQuestionData();
    	
    	currentQuestionProvided++;
    	if (currentQuestionProvided >= questionnairesList.size())
    	{
    		currentQuestionProvided = 0;
    	}

    	// �����o�肵�����Ԃ��L������
    	AnsweredQuestionInformation ans = answerTimeMillsList.get(currentQuestionProvided);
    	ans.setStartTime(System.currentTimeMillis());

    	// �����擾����
    	QuestionnaireHolder holder = questionnairesList.get(currentQuestionProvided);

    	// ������ݒ肷��
        int questionLocation = randomGenerator.nextInt(numberOfQuestions);
		gameQuestionObjects.get(questionLocation).setQuestion(holder.getAnswer(0), true);

    	// TODO:  ��x�ɍ��킹�āA�I���������炷�i���������Ă��ǂ������j
        //      �i���݂̐������x�������āAholder.getNumberOfAnswers() �S�Ă�ݒ肵�Ȃ��悤�ɂ���j
		for (int answers = 1; answers < holder.getNumberOfAnswers(); answers++)
		{
			do
			{
				// �񓚂��o�^����Ă��Ȃ��A���Ԃ�Ȃ��\���ꏊ��T��
				questionLocation = randomGenerator.nextInt(numberOfQuestions);
			} while (gameQuestionObjects.get(questionLocation).isExistQuestion() == true);
			gameQuestionObjects.get(questionLocation).setQuestion(holder.getAnswer(answers), false);
		}
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
    	out.writeTypedList(gameQuestionObjects);
    	out.writeTypedList(questionnairesList);
    	out.writeTypedList(questionnairesListCat1);
    	out.writeTypedList(questionnairesListCat2);
    	out.writeTypedList(questionnairesListCat3);
    	out.writeTypedList(questionnairesListCat4);
    	out.writeTypedList(categoryScores);
    	out.writeTypedList(answerTimeMillsList);
        out.writeInt(currentQuestionProvided);
        out.writeInt(numberOfQuestions);
        out.writeInt(gameResultLevel);
    }

    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    public static final Parcelable.Creator<QuestionnaireProvider> CREATOR
        = new Parcelable.Creator<QuestionnaireProvider>()
        {
    	    public QuestionnaireProvider createFromParcel(Parcel in)
    	    {
    	    	return (new QuestionnaireProvider(in));
    	    }
 
    	    public QuestionnaireProvider[] newArray(int size)
    	    {
    	    	return (new QuestionnaireProvider[size]);
    	    }    	    
        };

    /**
     *    Parcelable �C���^�t�F�[�X�̎���
     * 
     */
    private QuestionnaireProvider(Parcel in)
    {
    	gameQuestionObjects = in.createTypedArrayList(MoleGameQuestionHolder.CREATOR);  
    	questionnairesList =  in.createTypedArrayList(QuestionnaireHolder.CREATOR);  
    	questionnairesListCat1 =  in.createTypedArrayList(QuestionnaireHolder.CREATOR);  
    	questionnairesListCat2 =  in.createTypedArrayList(QuestionnaireHolder.CREATOR);  
    	questionnairesListCat3 =  in.createTypedArrayList(QuestionnaireHolder.CREATOR);  
    	questionnairesListCat4 =  in.createTypedArrayList(QuestionnaireHolder.CREATOR);  

    	categoryScores =  in.createTypedArrayList(ScoreSummaryHolder.CREATOR);   
    	answerTimeMillsList  =  in.createTypedArrayList(AnsweredQuestionInformation.CREATOR);  

    	currentQuestionProvided = in.readInt();
    	numberOfQuestions = in.readInt();
    	gameResultLevel = in.readInt();

    	// ����������
    	randomGenerator = new Random();
    	shuffleGenerator = new Random();
    }
}
