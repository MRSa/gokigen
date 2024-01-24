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
    private final int SHUFFLE_COUNT = 250;   //  問題文をまぜまぜする回数
	
	private ArrayList<MoleGameQuestionHolder> gameQuestionObjects = null;

    private ArrayList<QuestionnaireHolder> questionnairesList = null;
    private ArrayList<QuestionnaireHolder> questionnairesListCat1 = null;  // カテゴリ１
    private ArrayList<QuestionnaireHolder> questionnairesListCat2 = null;  // カテゴリ２
    private ArrayList<QuestionnaireHolder> questionnairesListCat3 = null;  // カテゴリ 3
    private ArrayList<QuestionnaireHolder> questionnairesListCat4 = null;  // カテゴリ 4

    private ArrayList<ScoreSummaryHolder> categoryScores = null;  // カテゴリ別のスコア
    private ArrayList<AnsweredQuestionInformation> answerTimeMillsList = null;    // 回答した情報

    private int   currentQuestionProvided = -1;
    private int   numberOfQuestions = 0;
    private int   gameResultLevel = 4;

    private Random shuffleGenerator = null;
    private Random randomGenerator = null;

    /**
     *   コンストラクタ
     *   
     * @param context
     * @param numberOfQuestions
     */
    public QuestionnaireProvider(Context context, int numberOfQuestions)
    {
    	// 乱数生成器
    	randomGenerator = new Random();
    	
    	// ゲームの情報を入れてみた
    	this.numberOfQuestions = numberOfQuestions;
		gameQuestionObjects = new ArrayList<MoleGameQuestionHolder>();
		for (int index = 0; index < numberOfQuestions; index++)
		{
		    gameQuestionObjects.add(new MoleGameQuestionHolder());
		}

		// 問題文を読み込んでみる
		loadQuestionnaires(context, R.raw.questionnaire);

        // 読み込んだ問題文をシャッフルする
		shuffleQuestionnaires();

		// 問題文の出題順番を作り上げる
		prepareQuestionnaries();
		
    }

    /**
	 *    現在出題中の問題管理オブジェクトを応答する(なければnull)
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
     *     回答データをクリアする
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
     *    問題をカテゴリの順番がまんべんなく出題されるように並べる
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
		
		// 分野別のコンテナをクリアする
		questionnairesListCat1 = null;
		questionnairesListCat2 = null;
		questionnairesListCat3 = null;
		questionnairesListCat4 = null;
		System.gc();		
    }

    /**
     *    指定されたリソースIDのRawファイルから、問題データを読み出す。
     *    （漢字コードがUTF-8で記述されたCSV形式で、データが以下の順番に並んでいるもの）
     * 
     *       (予約),カテゴリ,レベル,(予約),(ヒント),(コメント),正答,誤答,...
     *         - 0: (予約)      : 予約領域(現在ゼロを入れている)
     *         - 1: カテゴリ   : 問題の種別(1:土地,2:人物,3:食べ物,4:銘品その他)
     *         - 2: レベル      : 問題の難易度(現在未使用)
     *         - 3: (Option)   : 回答をより詳しく知るためのURL(仮に正解を含めた選択肢の数を入れている、現在未使用) 
     *         - 4: (ヒント)    : 回答に迷った時に表示するヒント
     *         - 5: (コメント) : 回答についてのコメント
     *         - 6: 正答          : 正解の答え
     *         - 7～: 誤答       : 間違った答え （7番目以降のデータ)
     * 
     * @param resId   リソースファイルのID
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
        			// 問題文を選択して入れ込む
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
     *    問題をシャッフルする
     * 
     */
    private void shuffleQuestionnaires()
    {
    	// 乱数生成器
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
     * @param isCorrect  正解 or 間違い
     * @param timeMills  回答した時刻
     * @param answeredText  回答した文字列
     */
    public void setAnsweredTime(boolean isCorrect, long timeMills, MoleGameQuestionHolder question)
    {
    	AnsweredQuestionInformation get = answerTimeMillsList.get(currentQuestionProvided);
    	int category = get.getCategory();
    	long startTime = get.getStartTime();
    	answerTimeMillsList.set(currentQuestionProvided, new AnsweredQuestionInformation(category, isCorrect, timeMills, startTime, question.getQuestion()));    	
    }

    /**
     *    回答終了時の回答状況を確認する
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
        				// タイムアウト検出
        				holder.incrementTimeout();
        			}
        			else if (result.getIsCorrect() == true)
        			{
        				// 正答
        				holder.incrementCorrect();
        				holder.addAnsweredTime(answeredTime);
        			}
        			else
        			{
        				// 誤答
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
	 *   出題した問題数を答える
	 * 
	 */
    public int getProvidedAnswers()
    {
    	return (currentQuestionProvided);
    }

    /**
     *    トータルのスコアを生成し、応答する（当面は正答率としておく）
     * 
     * @return   当面は正答率
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
    		// ゼロ割が発生しないようにする...
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
     *    カテゴリ別のスコアを生成し、応答する（当面は正答率としておく）
     * 
     * @param category  カテゴリ番号（１はじまり）
     * @return
     */
    private float getCategoryScore(int category)
    {
    	ScoreSummaryHolder holder = categoryScores.get(category - 1);
    	return (holder.getScore());
    }

    /**
     *    ゲームレベルを応答する
     *    (IResultProviderの実装)
     */
    public  int getResultGameLevel()
    {
        return (gameResultLevel);	
    }

    /** 
     *    回答した数を応答する
     *    (IResultProviderの実装)
     */
    public int getNumberOfAnsweredQuestions()
    {
    	//Log.v(Gokigen.APP_IDENTIFIER, "QuestionnaireProvider::getNumberOfAnsweredQuestions() : " + currentQuestionProvided);
    	return (currentQuestionProvided);    	
    }

    /**
     *     指定した回答情報を応答する
     *    (IResultProviderの実装)
     * 
     */
    public SymbolListArrayItem getAnsweredInformation(Context context, int index)
    {
    	QuestionnaireHolder holder = questionnairesList.get(index);                // 元データを取得する
    	AnsweredQuestionInformation info = answerTimeMillsList.get(index);  // 回答済データを取得する
        if ((holder == null)||(info == null))
        {
        	Log.v(Gokigen.APP_IDENTIFIER, "QuestionnaireProvider::getAnsweredInformation() : " + index + " is null.");
        	return (null);
        }
        long time = info.getAnsweredTime() - info.getStartTime();
        if ((time > 100000)||(time < 0))
        {
        	// 回答時間が異常だった場合...
        	time = 0;
        }
        String detailString = " " + holder.getDetailText();  //
        String hintString = " " + holder.getHintText();       //
        String optionString = "  " + holder.getOptionText(); //
    	SymbolListArrayItem item = new SymbolListArrayItem(context, info.getIsCorrect(), info.getCategory(), info.getAnsweredString(),  holder.getAnswer(0), time, hintString, detailString, optionString);
    	return (item);
    }

    /**
     *    回答成果を答える
     * 
     * @param category   カテゴリ番号（１はじまり）
     * @return  スコア（0.0f ～ 1.0f までの数値）
     */
    public float getScore(int category)
    {
        if (categoryScores == null)
        {
        	// スコア情報が生成されていないのでスコアはゼロ
        	return (0.0f);
        }
        try
        {
        	if (category == 0)
        	{
        		// 総合スコアがほしい場合...
        		return (getTotalScore());
        	}
        	// カテゴリ別のスコアがほしい場合...
            return (getCategoryScore(category));
        }
        catch (Exception ex)
        {
        	// 例外発生...何もしない
        }
        return (0.0f);
    }
    
    /**
     *    問題を更新する
     * 
     */
    public void updateQuestionData()
    {
    	// 問題文を全てクリアする
    	resetQuestionData();
    	
    	currentQuestionProvided++;
    	if (currentQuestionProvided >= questionnairesList.size())
    	{
    		currentQuestionProvided = 0;
    	}

    	// 問題を出題した時間を記憶する
    	AnsweredQuestionInformation ans = answerTimeMillsList.get(currentQuestionProvided);
    	ans.setStartTime(System.currentTimeMillis());

    	// 問題を取得する
    	QuestionnaireHolder holder = questionnairesList.get(currentQuestionProvided);

    	// 正解を設定する
        int questionLocation = randomGenerator.nextInt(numberOfQuestions);
		gameQuestionObjects.get(questionLocation).setQuestion(holder.getAnswer(0), true);

    	// TODO:  難度に合わせて、選択肢を減らす（処理を入れても良いかも）
        //      （現在の正答レベルを見て、holder.getNumberOfAnswers() 全てを設定しないようにする）
		for (int answers = 1; answers < holder.getNumberOfAnswers(); answers++)
		{
			do
			{
				// 回答が登録されていない、かぶらない表示場所を探す
				questionLocation = randomGenerator.nextInt(numberOfQuestions);
			} while (gameQuestionObjects.get(questionLocation).isExistQuestion() == true);
			gameQuestionObjects.get(questionLocation).setQuestion(holder.getAnswer(answers), false);
		}
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
     *    Parcelable インタフェースの実装
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
     *    Parcelable インタフェースの実装
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

    	// 乱数生成器
    	randomGenerator = new Random();
    	shuffleGenerator = new Random();
    }
}
