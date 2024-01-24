package jp.sfjp.gokigen.okaken;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;

/**
 *    問題回答の描画クラス
 * 
 * @author MRSa
 *
 */
public class MoleGameDrawer implements ICanvasDrawer, SoundPool.OnLoadCompleteListener
{
	public static final int BACKGROUND_COLOR_DEFAULT = 0xff004000;
	private static final int VIBRATION_DURATION_OK = 50;
	private static final int VIBRATION_DURATION_NG = 150;
	private static final int VIBRATION_DURATION_NEXT = 50;

	private static final int RADARICON_SIZE = 24;
	
	private int backgroundColor = BACKGROUND_COLOR_DEFAULT;
	
	private long currentTimeMillis = 0;
	private int    positionIndex = -1;
	private int    answeredPositionIndex = -1;
	private float downPosX = Float.MIN_VALUE;
	private float downPosY = Float.MIN_VALUE;

	private int    timerCount = 0;

	private float screenWidth = 0.0f;  // 表示領域の幅
	private float screenHeight = 0.0f; // 表示領域の高さ

	//private String backgroundBitmapUri = null;
	private Bitmap backgroundBitmap = null;
	private Bitmap peachBitmap  = null;          // 問題を表示してない時の桃
	private Bitmap openPeachBitmap  = null;   // 問題表示中の桃
	private Bitmap okBitmap  = null;
	private Bitmap ngBitmap  = null;
	
	private Bitmap placeBitmap = null;
	private Bitmap peopleBitmap = null;
	private Bitmap foodBitmap = null;
	private Bitmap specialBitmap = null;
	
	private Activity parent = null;
	private IActivityOpener activityOpener = null;
	private IGameInformationProvider provider = null;
	private Vibrator vibrator = null;

	private RectF detailButtonRect = null;
	
	private boolean isGameOver = false;

	// 効果音設定...    
    private SoundPool  soundEffect = null;
    private int            seOkId= 0;
    private int            seNgId= 0;
    
    private boolean workAround = false;
	
	/**
	 *   コンストラクタ...使用するビットマップ等を読み込んで保持する
	 * 
	 * @param parent
	 * @param infoProvider
	 */
	public MoleGameDrawer(Activity parent, IGameInformationProvider infoProvider, IActivityOpener opener)
    {
    	this.parent = parent;
    	this.provider = infoProvider;
    	this.activityOpener = opener;
    	
    	// 桃を読み込む
    	peachBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.peach);
    	openPeachBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.peach_open);
    	// ○を読み込む
    	okBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.good);
    	// ×を読み込む
    	ngBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.bad);

    	// レーダーチャート用のアイコンを読み出す
    	placeBitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.place)), RADARICON_SIZE, RADARICON_SIZE, false);
    	peopleBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.people)), RADARICON_SIZE, RADARICON_SIZE, false);
    	foodBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.food)), RADARICON_SIZE, RADARICON_SIZE, false);
    	specialBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.special)), RADARICON_SIZE, RADARICON_SIZE, false);
    	
    	// バイブレータサービスを拾う
    	vibrator = (Vibrator) parent.getSystemService(Context.VIBRATOR_SERVICE);
        isGameOver = false;
    	workAround = false;

        // 効果音再生クラスの設定
        soundEffect = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundEffect.setOnLoadCompleteListener(this);

        // 効果音の準備
        seOkId = soundEffect.load(parent, R.raw.sound_ok, 1);   // OK効果音のロード
        seNgId = soundEffect.load(parent, R.raw.sound_ng, 1);   // NG効果音のロード
    }
	
	/**
	 * 　起動時の準備
	 * 
	 */
    public void prepareToStart(int width, int height)
    {
		  // 表示画面サイズを覚える
		  //screenWidth = width;
		  //screenHeight = height;

    	// 起動時は、ゲームオーバー表示を復旧させる
   	    isGameOver = false;
   	    workAround = false;
   	}
    
    /**
     *   画面サイズが変更された時に呼ばれる
     * 
     */
    public void changedScreenProperty(int format, int width, int height)
    {
		  // 表示画面サイズを覚える
		  screenWidth = width;
		  screenHeight = height;    	
    }

	  /**
	   * 　背景に表示する画像を設定する
	   * 
	   * @param uri
	   */
	  public void updateBackgroundBitmap(String uri, int width, int height)
	  {
		  // 背景画像の文字列を記憶する
		  // backgroundBitmapUri = uri;

		  // とりあえず、背景画像をクリアしてガベコレする。
		  backgroundBitmap = null;
		  //System.gc();
		  if (uri.length() <= 0)
		  {
			  // 背景画像の指定がなかったので、ここでリターンする。
			  return;
		  }
		  try
		  {
			  // 設定する情報をログに残してみる
			  Log.v(Gokigen.APP_IDENTIFIER, "MoleGameDrawer::updateBackgroundBitmap() : w:" + width + " , h:"+ height + " " + uri);

			  // 背景画像を取得して設定する。
			  backgroundBitmap = ImageLoader.getBitmapFromUri(parent, ImageLoader.parseUri(uri), width, height);
		  }
		  catch (Exception ex)
		  {
			  Log.v(Gokigen.APP_IDENTIFIER, "MoleGameDrawer::updateBackgroundBitmap() : " + uri + " , "+ ex.toString());
			  ex.printStackTrace();
			  backgroundBitmap = null;
			  //backgroundBitmapUri = "";
			  System.gc();
		  }	
		  return;
	  }	

	  /**
	   * 
	   * 
	   * @param soundPool
	   * @param sampleId
	   * @param status
	   */
	  public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
	  {
          Log.v(Gokigen.APP_IDENTIFIER, "onLoadComplete : " + sampleId + "  status: " + status);
	  }

	  /**
	   *    ゲームオーバーのキャンバスデータ更新が終了しているかどうか
	   * 
	   * @return
	   */
	  public boolean isGameOverDrawn()
	  {
		  return (isGameOver);
	  }
	  
	 /**
	  *    現在の回答レベル状況をイメージで表示する。
	  * 
	  * @param canvas
	  */
    private void drawCurrentLevel(Canvas canvas, Bitmap drawBitmap)
    {
    	// ビットマップの大きさを変更する (TODO 毎回変更かい！）
    	int width = (int) (screenWidth / MoleGameActivity.NUMBER_OF_MOLE_COLUMNS - 2.0f);
    	int height =(int) (screenHeight / MoleGameActivity.NUMBER_OF_MOLE_ROWS - 4.0f); 
        Bitmap bitmap = Bitmap.createScaledBitmap(drawBitmap, width, height, false);
 
    	// 描画位置の決定
        float bitmapTop = screenHeight - bitmap.getHeight();
        float bitmapLeft = screenWidth - bitmap.getWidth();
        canvas.drawBitmap(bitmap, bitmapLeft, bitmapTop, new Paint());        
    }
	  
    /**
     *    ゲーム終了！ のメッセージを表示する
     * 
     * @param canvas
     */
    private void drawGameOverMessage(Canvas canvas)
    {
    	// ゲーム結果のレベルを示すビットマップをまず表示する
    	drawCurrentLevel(canvas, provider.getResultLevelbitmap());

    	// 文字色の準備 : 白色にしてちょっと影付きにする
    	Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
    	
        // 枠の準備：枠色を白にして、ちょっと透過にする
		Paint framePaint = new Paint();
		framePaint.setColor(Color.DKGRAY);
		framePaint.setShadowLayer(0.6f, 0.6f, 0.6f, Color.DKGRAY);
		framePaint.setColor(0xaa444444);  // Color.DKGRAY
		framePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // 枠の準備：枠色を白にして、ちょっと透過にする
		Paint radarPaint = new Paint();
		radarPaint.setColor(Color.WHITE);
		radarPaint.setShadowLayer(0.6f, 0.6f, 0.6f, Color.DKGRAY);
		radarPaint.setStyle(Paint.Style.STROKE);

		float wideSize = (25.0f  / 480.0f) * screenWidth;
    	float highSize = (19.0f / 800.0f) * screenHeight;
	
        float height = screenHeight / 2.0f;
        float width =  screenWidth / 2.0f;
        timerCount++;
        if ((width - timerCount * wideSize) > 5.0f)
        {
        	// ガガッと、ゲーム終了時に枠を拡大する（ちょっとした無理やりアニメーション）
            canvas.drawRoundRect(new RectF(width - timerCount * wideSize, height - timerCount * highSize, width + wideSize * timerCount,  height + timerCount * highSize), 5, 5, framePaint);
            canvas.drawRoundRect(new RectF(width - timerCount * wideSize, height - timerCount * highSize, width + wideSize * timerCount,  height + timerCount * highSize), 5, 5, radarPaint);
            return;
        }
        timerCount--; // オーバーしたサイズをもとに戻す
        float heightMargin = timerCount * highSize;

        // ----- ゲーム実行結果の表示、ちょっとがんばりどころ  -----

        //  表示用の枠を作る
        canvas.drawRoundRect(new RectF(5.0f, height - heightMargin, screenWidth - 5.0f,  height + heightMargin), 5, 5, framePaint);
        canvas.drawRoundRect(new RectF(5.0f, height - heightMargin, screenWidth - 5.0f,  height + heightMargin), 5, 5, radarPaint);

        float graphWidth = screenWidth / 4.0f;
        float graphOffsetX = 30.0f;
        float graphMargin = 10.0f;

        // 画面上部に文字による結果表示（最適化必要）
        String gameOverString = parent.getString(R.string.result_title);
        float titleSize = TextDrawingUtility.decideFontHeightSize(gameOverString, (int) graphOffsetX, 0, paint);
        paint.setTextSize(titleSize);
        canvas.drawText(gameOverString, 10.0f, height - heightMargin - paint.getFontMetrics().top, paint);
        
        // 枠の中にレーダーチャートを表示する
        FourAxisRadarChartDrawer.drawRadarChartBase(canvas, graphOffsetX + graphWidth, height, graphWidth, graphMargin, radarPaint);

        // 軸にアイコンを貼り付ける
        FourAxisRadarChartDrawer.drawRadarAxisIcons(canvas, graphOffsetX + graphWidth, height, graphWidth, placeBitmap, peopleBitmap, foodBitmap, specialBitmap);

        //  結果グラフを描画する
        FourAxisRadarChartDrawer.drawRadarChartLine(canvas, (graphOffsetX + graphWidth), height, graphWidth, provider.getScore(1), provider.getScore(2), provider.getScore(3), provider.getScore(4));

        //  総合スコアをグラフの横にでかでかと表示する
        int score = Math.round(provider.getScore(0) * 100.0f);
        float textWidth = (screenWidth - 10.0f) - (graphOffsetX+ graphWidth) * 2.0f;
        String scoreString = score + parent.getString(R.string.game_pts);
    	paint.setTextSize(TextDrawingUtility.decideFontSize(scoreString, (int) (textWidth), 0, paint));
    	canvas.drawText(scoreString, ((graphOffsetX+ graphWidth) * 2.0f), (height + graphWidth) - paint.getFontMetrics().bottom, paint);

        // ”もっとくわしく” ボタンを表示する
        String buttonString = parent.getString(R.string.game_showDetailInformation);
        float textHeight = ((height + heightMargin) - 8.0f) - (height + graphWidth);
        float buttonSize = TextDrawingUtility.decideFontSize(buttonString, (int) (screenWidth - 15.0f - ((graphOffsetX+ graphWidth) * 2.0f)), (int) (textHeight), 0, paint);
        paint.setTextSize(buttonSize);
        textHeight = ((heightMargin - 8.0f - graphWidth) -  (paint.getFontMetrics().bottom - paint.getFontMetrics().top)) / 2.0f;
        detailButtonRect = new RectF(((graphOffsetX+ graphWidth) * 2.0f), (height + graphWidth), screenWidth - 12.0f, ((height + heightMargin) - 8.0f));
        canvas.drawRoundRect(detailButtonRect, 5.0f, 5.0f, radarPaint);
    	canvas.drawText(buttonString, ((graphOffsetX+ graphWidth) * 2.0f + 5.0f), ((height + graphWidth) - 4.0f) - paint.getFontMetrics().top + textHeight, paint);
    	
    	// GAME OVER表示の時には、画面表示の更新をしない。
    	if (workAround == true)
    	{
   	        isGameOver = true;
    	}
    	workAround = true;
        return;
    }
    
	/**
     * 
     * 
     * @param canvas
     */
    private void drawGameInformation(Canvas canvas)
    {
    	// ゲームの情報を出力する
    	if (provider != null)
    	{
	        // 文字色を白にして、ちょっと影付きにする
    		Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);

            // フォントのサイズを設定する
        	String message = parent.getString(R.string.game_readyInformation);
            float fontSize = TextDrawingUtility.decideFontSize(message, (int) (screenWidth - 60), -1, paint);
            paint.setTextSize(fontSize);

            // 描画位置を決めてみる
            float fontHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
	        float width = 10; // (screenWidth / NUMBER_OF_MOLE_COLUMNS) * (NUMBER_OF_MOLE_COLUMNS - 1);
	        float height = screenHeight - fontHeight;

	        int status = provider.getCurrentGameStatus();
            if (status == GameInformationProvider.STATUS_READY)
            {
       	        canvas.drawText(parent.getString(R.string.game_readyInformation), width, height, paint);
            }
            else if (status == GameInformationProvider.STATUS_GO)
            {
       	        canvas.drawText(parent.getString(R.string.game_goInformation), width, height, paint);            	
            }
            else if (status == GameInformationProvider.STATUS_GAME_OVER)
            {
            	drawGameOverMessage(canvas);
            }
            else
            {
            	// ゲーム実行中の表示

            	// 現在のレベルを示すビットマップの表示
            	drawCurrentLevel(canvas, provider.getCurrentLevelbitmap());

            	// 残り時間の表示
            	drawTimeBar(canvas, provider.getRemainPercent());

            	timerCount = 0;
            }    	     
    	}
    }
    
    /**
     *     画面描画メイン
     * 
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	try
    	{    		
    		// 背景色を入れる
    		canvas.drawColor(backgroundColor);	    			

    		// 背景画像が設定されていた場合は、背景画像を描画する
    		if (backgroundBitmap != null)
    		{
    		    canvas.drawBitmap(backgroundBitmap, 0, 0, new Paint());
    		}

    		// ゲームの進行情報が取れない場合は、ここで折り返す
    		if (provider == null)
    		{
    			return;
    		}

    		// 桃を表示する
    		drawMoleHoles(canvas);

    		// ゲームの情報を出力する
            drawGameInformation(canvas);

            // ゲームの実行状態を取得する
	        int status = provider.getCurrentGameStatus();
	        switch (status)
	        {
	          case GameInformationProvider.STATUS_GAME_PLAYING:
	        	  // ゲーム実施中...答えを描画する
                  drawGameAnswer(canvas);
	        	  break;

	          default:
	        	  break;
	        }
    	}
    	catch (Exception ex)
    	{
    		
    	}
    }

    /**
     *    ○を表示する
     * 
     * @param canvas
     * @param index
     */
    private void drawCorrectMark(Canvas canvas, int index)
    {
		//  表示する場所を決定する
		float widthMargin = getPositionWidth(index);
		float heightMargin = getPositionHeight(index);

		// ○を表示する
	    canvas.drawBitmap(okBitmap, widthMargin, heightMargin - 15, new Paint());     	
    }

    /**
     *    ×を表示する
     * 
     * @param canvas
     * @param index
     */
    private void drawWrongtMark(Canvas canvas, int index)
    {
		//  表示する場所を決定する
		float widthMargin = getPositionWidth(index);
		float heightMargin = getPositionHeight(index);

		// ×を表示する
		canvas.drawBitmap(ngBitmap, widthMargin + 10, heightMargin, new Paint());
    }

    /**
     *    回答結果（○ または ×）を表示する
     * 
     * @param canvas
     */
    private void drawGameAnswer(Canvas canvas)
    {
		// 現在の状態を知る
        int isAnswer = provider.getCurrentAnswerStatus();
		if (isAnswer == IGameInformationProvider.ANSWER_NOT_YET)
		{
			// まだ未回答...タップした位置を確認する
	    	if ((positionIndex < 0)||(positionIndex > MoleGameActivity.NUMBER_OF_MOLE_HOLES))
	    	{
	    		// タップ位置が異常なので無視する
	    		return;
	    	}
	    	// 回答位置を記憶する
	    	answeredPositionIndex = positionIndex;
	    	positionIndex = -1;
	    	
			long duration = 0;  // ブルブルする時間間隔...
			int soundEffectId  = 0;

			//  表示する場所の問題は正解かどうかチェックする
			MoleGameQuestionHolder question = provider.getGameQuestion(answeredPositionIndex);
			if (question.isCorrectQuestion() == true)
			{
		        // ○を表示する
				drawCorrectMark(canvas, answeredPositionIndex);
			    provider.changeAnswerStatus(IGameInformationProvider.ANSWER_CORRECT, currentTimeMillis, question);
			    duration = VIBRATION_DURATION_OK;
			    soundEffectId  = seOkId;
			}
	        else
			{
				// ×を表示する
	        	drawWrongtMark(canvas, answeredPositionIndex);
			    provider.changeAnswerStatus(IGameInformationProvider.ANSWER_WRONG, currentTimeMillis, question);
	    		duration = VIBRATION_DURATION_NG;
			    soundEffectId  = seNgId;
			}
			// ブルブルさせる
			vibrator.vibrate(duration);

            if (soundEffect != null)
            {
                // 効果音を鳴らす。
                soundEffect.play(soundEffectId, 1, 1, 3, 0, 1);
            }
		}
		else if (isAnswer == IGameInformationProvider.ANSWER_CORRECT)
		{
			// タップした位置を確認する
	    	if ((answeredPositionIndex < 0)||(answeredPositionIndex > MoleGameActivity.NUMBER_OF_MOLE_HOLES))
	    	{
	    		// タップ位置が異常なので無視する
	    		return;
	    	}

	    	// ○を表示する
			drawCorrectMark(canvas, answeredPositionIndex);
		}
		else if  (isAnswer == IGameInformationProvider.ANSWER_WRONG)
		{
			// タップした位置を確認する
	    	if ((answeredPositionIndex < 0)||(answeredPositionIndex > MoleGameActivity.NUMBER_OF_MOLE_HOLES))
	    	{
	    		// タップ位置が異常なので無視する
	    		return;
	    	}

	    	// ×を表示する
        	drawWrongtMark(canvas, answeredPositionIndex);			
		}
    }

    /**
     *     タッチした座標（X軸）を取得する
     * 
     * @param index
     * @return
     */
    private float getPositionWidth(int index)
    {
    	int  x = index % MoleGameActivity.NUMBER_OF_MOLE_COLUMNS; 
        float width = screenWidth / MoleGameActivity.NUMBER_OF_MOLE_COLUMNS;
        float widthMargin = (width - peachBitmap.getWidth()) / 2.0f;

        return (widthMargin + width * x);
    }

    /**
     *     タッチした座標(Y軸)を取得する
     * 
     * @param index
     * @return
     */
    private float getPositionHeight(int index)
    {
     	int  y = index / MoleGameActivity.NUMBER_OF_MOLE_COLUMNS;
        float height = screenHeight / MoleGameActivity.NUMBER_OF_MOLE_ROWS;
        float heightMargin = (height - peachBitmap.getHeight());

        return (heightMargin + height * y);
    }

    /**
     *    タイマーの時間を表示する
     * 
     * @param canvas
     */
    private void drawTimeBar(Canvas canvas, float remainPercent)
    {
    	Paint paint = new Paint();
    	
    	// バーを表示する色を決める
    	if (remainPercent < 0.12f)
    	{
    		// 残り時間が12%を切ったら赤色にする
    		paint.setColor(Color.RED);
    	} else if (remainPercent < 0.45f)
    	{
    		// 残り時間が45%を切ったら黄色にする
    		paint.setColor(Color.YELLOW);
    	}
    	else
    	{
            paint.setColor(Color.BLUE);    		
    	}
        float width = screenWidth / MoleGameActivity.NUMBER_OF_MOLE_COLUMNS * 2.0f;
        float height = screenHeight / MoleGameActivity.NUMBER_OF_MOLE_ROWS / 6.0f;

        // 四角形を表示する
        canvas.drawRect(new RectF(1.0f, (screenHeight - height), (width * remainPercent), (screenHeight - 1.0f)), paint);
        
    }

    /**
     *    問題文(と問題の表示位置)を表示する
     * 
     * @param canvas
     */
    private void drawMoleHoles(Canvas canvas)
    {
        float width = screenWidth / MoleGameActivity.NUMBER_OF_MOLE_COLUMNS;
        float height = screenHeight / MoleGameActivity.NUMBER_OF_MOLE_ROWS;
        float widthMargin = (width - peachBitmap.getWidth()) / 2.0f;  // 中心寄せ
        float heightMargin = (height - peachBitmap.getHeight());        // 下寄せ
        float widthTextMargin = 2.0f;

        for (int y = 0; y < (MoleGameActivity.NUMBER_OF_MOLE_ROWS - 1); y++)
        {
        	for (int x = 0; x < MoleGameActivity.NUMBER_OF_MOLE_COLUMNS; x++)
        	{
    	        int status = provider.getCurrentGameStatus();
        		int index = (y * MoleGameActivity.NUMBER_OF_MOLE_COLUMNS) + x;
                MoleGameQuestionHolder question = provider.getGameQuestion(index);
                if ((status != GameInformationProvider.STATUS_GAME_PLAYING)||(question.isExistQuestion() == false))
                {
                    //  問題は表示されていない
                	canvas.drawBitmap(peachBitmap, widthMargin + x * width, heightMargin + y * height, new Paint());
                }
                else
                {
                	// 問題（の看板）を表示する
                	Paint paint = new Paint();
        	        paint.setColor(Color.WHITE);
        	    	paint.setStyle(Paint.Style.STROKE);
        	    	paint.setStrokeJoin(Paint.Join.ROUND);

        	    	// 問題文を表示する背景枠を表示する
        	    	float marginY = 2.0f;
        	    	float posX = x * width;
        	    	float posY = y * height ;
        	    	Path path = new Path();
                    path.moveTo(posX + 1, posY + marginY);
                    path.lineTo((posX + width) - 1, posY + marginY);
                    path.lineTo((posX +  width) - 1, heightMargin + (posY - height) + (height * 9 / 10));
                    path.lineTo(((posX + posX + width) / 2) + 3, heightMargin + (posY - height) + (height * 9 / 10));
                    path.lineTo(((posX + posX + width) / 2), heightMargin + posY);
                    path.lineTo(((posX + posX + width) / 2) - 3, heightMargin + (posY - height) + (height * 9 / 10));
                    path.lineTo(posX + 1, heightMargin + (posY -  height) + (height * 9 / 10));
                    path.lineTo(posX + 1, posY + marginY);
                    canvas.drawPath(path, paint);
                    
                	// 文字の大きさを決める (最大フォントサイズは20dip)
                    int maxFontSize =  (int) (20.0f * parent.getResources().getDisplayMetrics().density + 0.5f);
                    //paint.setTextSize(TextDrawingUtility.decideFontSize(question.getQuestion(), (int) (width - widthTextMargin - widthTextMargin),(int) (heightMargin  - height / 10 - marginY), 0, paint));
                    //paint.setTextSize(TextDrawingUtility.decideFontSize(question.getQuestion(), (int) ((width - widthTextMargin - widthTextMargin) * 2 - 4),(int) ((heightMargin  - height / 10 - marginY) / 2), maxFontSize, paint));
                    paint.setTextSize(TextDrawingUtility.decideFontSize(question.getQuestion(), (int) (width - (widthTextMargin * 5)),(int) (heightMargin  - (height / 10) - (marginY * 3)), maxFontSize, paint));

                    // 問題文のY軸の位置を移動させる
                    FontMetrics fontMetrics = paint.getFontMetrics();

                    // 問題文を表示する
                    //canvas.drawText(question.getQuestion(), (x * width) + widthTextMargin, (y * height) + (fontMetrics.bottom - fontMetrics.top + 2.0f), paint);
                    TextDrawingUtility.drawTextRegion(canvas, question.getQuestion(), posX + widthTextMargin, posY - fontMetrics.bottom, (posX + width - widthTextMargin), paint);

                    // 桃を表示する
                	canvas.drawBitmap(openPeachBitmap, widthMargin + posX, heightMargin + posY, new Paint());
                }
        	}
        }
    }

    /**
     * 
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        // Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::onTouchEvent() : "); 
        boolean isDraw = false;

	    int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
        	// タッチされたとき
        	isDraw = onTouchDown(event);
        }
        else if (action == MotionEvent.ACTION_MOVE)
        {
        	// タッチされたまま動かされたときの処理
        	isDraw = onTouchMove(event);
        }
        else if (action == MotionEvent.ACTION_UP)
        {
        	// タッチが離されたときの処理...
            isDraw = onTouchUp(event);
        }
    	return (isDraw);
    }

    /**
     *    タッチした場所をインデックス化する
     * @param x
     * @param y
     * @return
     */
    private int onTouchedPosition(float x, float y)
    {
        float width = screenWidth / MoleGameActivity.NUMBER_OF_MOLE_COLUMNS;
        float height = screenHeight / MoleGameActivity.NUMBER_OF_MOLE_ROWS;

        int posX = Math.round((x / width) - 0.5f);
        int posY = Math.round((y / height) - 0.5f);

        // Log.v(Gokigen.APP_IDENTIFIER, "touched : " + x + ", " + y + " [" + posX + ", " + posY +"]");
    	return (posY * MoleGameActivity.NUMBER_OF_MOLE_COLUMNS + posX);
    }

    /**
     *   タッチされたタイミングでの処理
     * @param event
     * @return
     */
    private boolean onTouchDown(MotionEvent event)
    {
    	// タッチ位置を記憶する
    	downPosX = event.getX();
    	downPosY = event.getY();

    	// タッチした場所とタッチ時間を取得する
    	currentTimeMillis = System.currentTimeMillis();
    	positionIndex = onTouchedPosition(downPosX, downPosY);
    	
    	// ゲームオーバーのとき
    	if (provider.getCurrentGameStatus() == IGameInformationProvider.STATUS_GAME_OVER)
    	{
    		// ゲームオーバー状態のチェックを解除をはずすかどうかチェックする
    	    checkGameOverRestart(positionIndex);
    	}
        return (true);
    }

    /**
     * 
     * 
     * @param index
     */
    private void checkGameOverRestart(int index)
    {
    	if (detailButtonRect == null)
    	{
    		// ボタンエリアがなかった ... 終了する
    		return;
    	}
    	if (detailButtonRect.contains(downPosX, downPosY) == true)
    	{
    		// もっとくわしくボタンが押されたとき！

    		// ブルブルさせる
			vibrator.vibrate(VIBRATION_DURATION_NEXT);
    		
			// Activityを別のものに遷移させる...
			activityOpener.requestToStartActivity(0);
    		
    	}
    }

    /**
     *   タッチしつつ移動した時の処理
     * @param event
     * @return
     */
    private boolean onTouchMove(MotionEvent event)
    {
    	//downPosX = Float.MIN_VALUE;
    	//downPosY = Float.MIN_VALUE;
    	//positionIndex = -1;

    	return (false);
    }	

    /**
     *   タッチが離されたタイミングでの処理
     * @param event
     * @return
     */
    private boolean onTouchUp(MotionEvent event)
    {
    	downPosX = Float.MIN_VALUE;
    	downPosY = Float.MIN_VALUE;
    	positionIndex = -1;
    	currentTimeMillis = -1;

    	return (false);
    }	

}
