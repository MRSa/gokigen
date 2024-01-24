package jp.sfjp.gokigen.okaken;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;

/**
 *    結果表示用のキャンバスクラス
 * 
 * @author MRSa
 *
 */
public class ResultDrawer implements ICanvasDrawer
{
	private static final int RADARICON_SIZE = 20;

	public static final int BACKGROUND_COLOR_DEFAULT = 0xff004000;
	private int backgroundColor = BACKGROUND_COLOR_DEFAULT;

	private static final int VIBRATION_DURATION = 50;

	private float screenWidth = 0.0f;  // 表示領域の幅
	private float screenHeight = 0.0f; // 表示領域の高さ
	
	//private String backgroundBitmapUri = null;
	private Bitmap backgroundBitmap = null;
    private ArrayList<Bitmap> momotaroBitmaps = null;

    // 4軸のグラフラベル
	private Bitmap placeBitmap = null;
	private Bitmap peopleBitmap = null;
	private Bitmap foodBitmap = null;
	private Bitmap specialBitmap = null;

	private float downPosX = Float.MIN_VALUE;
	private float downPosY = Float.MIN_VALUE;

	private Activity parent = null;
	private Vibrator vibrator = null;
	private IResultProvider resultProvider = null;
	
	private boolean isMessageMode = true;
	private String resultMessage = "";
	private RectF  infoArea = null;

	/**
	 *    コンストラクタ
	 * 
	 * @param parent
	 * @param resultProvider
	 */
	public ResultDrawer(Activity parent, IResultProvider resultProvider)
    {
    	this.parent = parent;
    	this.resultProvider = resultProvider;
    	
		// ももたろさんのビットマップを読み込んでみる
    	prepareMomotaroBitmaps(parent);

    	// レーダーチャート用のアイコンを読み出す
    	placeBitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.place)), RADARICON_SIZE, RADARICON_SIZE, false);
    	peopleBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.people)), RADARICON_SIZE, RADARICON_SIZE, false);
    	foodBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.food)), RADARICON_SIZE, RADARICON_SIZE, false);
    	specialBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.special)), RADARICON_SIZE, RADARICON_SIZE, false);

    	// バイブレータサービスを拾う
    	vibrator = (Vibrator) parent.getSystemService(Context.VIBRATOR_SERVICE);

    	// 結果を解析してメッセージを生成する
    	resultMessage = analysisResult(resultProvider);
    }
	
    /**
     *    ももたろさんのビットマップを読み込んで保持する
     * 
     */
    private void prepareMomotaroBitmaps(Context context)
    {
    	Bitmap bitmap = null;
    	momotaroBitmaps = new ArrayList<Bitmap>();

    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l0);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l1);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l2);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l3);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l4);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l5);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l6);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l7);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l8);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    	bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.momo_l9);
    	momotaroBitmaps.add(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 2.0f), (int) (bitmap.getHeight() / 2.0f), false));
    }

	/**
	 * 
	 */
    public void prepareToStart(int width, int height)
    {
    	
    	
    }

	/**
	 * 
	 */
    public void changedScreenProperty(int format, int width, int height)
    {
		  // 表示画面サイズを覚える
		  screenWidth = width;
		  screenHeight = height;    	    	
    }

	  /**
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
			  Log.v(Gokigen.APP_IDENTIFIER, "ResultDrawer::updateBackgroundBitmap() : w:" + width + " , h:"+ height + " " + uri);

			  // 背景画像を取得して設定する。
			  backgroundBitmap = ImageLoader.getBitmapFromUri(parent, ImageLoader.parseUri(uri), width, height);
		  }
		  catch (Exception ex)
		  {
			  Log.v(Gokigen.APP_IDENTIFIER, "ResultDrawer::updateBackgroundBitmap() : " + uri + " , "+ ex.toString());
			  ex.printStackTrace();
			  backgroundBitmap = null;
			  //backgroundBitmapUri = "";
			  System.gc();
		  }	
		  return;
	  }	  

	    /**
	     *    画面を操作した時の処理...
	     * 
	     */
	    public boolean onTouchEvent(MotionEvent event)
	    {
	    	boolean isDraw = false;

	        // タッチポジションを記憶する（チャタリング防止）
		    int action = event.getAction();
	        if (action == MotionEvent.ACTION_DOWN)
	        {        	
	            downPosX = event.getX();
		        downPosY = event.getY();
	    		if ((infoArea != null)&&(infoArea.contains(downPosX, downPosY) == true))
	    		{
	    			// 表示モードを切り替える
	    	        vibrator.vibrate(VIBRATION_DURATION);
	    	        isMessageMode =  (isMessageMode == true) ? false : true;
	    			return (true);
	    		}
	        }
	        else if (action == MotionEvent.ACTION_UP)
	        {
	            downPosX = Float.MIN_VALUE;
		        downPosY = Float.MIN_VALUE;
	        }
	    	return (isDraw);
	    }

	  /**
	     * 
	     * 
	     */
    public void drawOnCanvas(Canvas canvas)
	{
	    	try
	    	{
	    		// 背景色を入れる
	    		canvas.drawColor(backgroundColor);	    			

    			// タイトル表示
    			drawTitle(canvas);
    			
    			// レーダーチャートの表示
    			drawRadarChart(canvas);

    			// ももたろアイコンの表示
    			drawMomotaroIcon(canvas);
    			
    			// ゲームの情報を出力する
                drawGameInformation(canvas);    			
	    	}
	        catch (Exception ex)
	    	{
	    		
	    	}
	}

    /**
     *   レーダーチャートを表示する
     * 
     * @param canvas
     */
    private void drawRadarChart(Canvas canvas)
	{
    	float centerX, centerY, radius;
    	//float width = (screenWidth > screenHeight) ? screenHeight : screenWidth;
    	float graphMargin = 8.0f;
    	//radius = width / 6.0f;
    	radius = screenHeight / 6.0f;
    	centerX = screenWidth - (radius + graphMargin * 4.0f);
    	centerY = radius + graphMargin * 4.0f;

    	// 枠の準備：枠色を白にして、ちょっと透過にする
		Paint framePaint = new Paint();
		framePaint.setColor(Color.WHITE);
		framePaint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
		framePaint.setStyle(Paint.Style.STROKE);

		// 枠の中にレーダーチャートを表示する
        FourAxisRadarChartDrawer.drawRadarChartBase(canvas, centerX, centerY, radius, graphMargin, framePaint);

        // 軸にアイコンを貼り付ける
        FourAxisRadarChartDrawer.drawRadarAxisIcons(canvas, centerX, centerY, radius, placeBitmap, peopleBitmap, foodBitmap, specialBitmap);

        //  結果グラフを描画する
        FourAxisRadarChartDrawer.drawRadarChartLine(canvas, centerX, centerY, radius, resultProvider.getScore(1), resultProvider.getScore(2), resultProvider.getScore(3), resultProvider.getScore(4));
	}
    
    /**
     *   ももたろアイコンの表示を行う
     * 
     * @param canvas
     */
    private void drawMomotaroIcon(Canvas canvas)
    {
    	int level = resultProvider.getResultGameLevel();
    	if (level < 0)
    	{
    		level = 0;
    	}
    	else if (level > 9)
    	{
    		level = 9;
    	}
    	Bitmap momotaroBitmap = momotaroBitmaps.get(level);
    	float left = screenWidth - momotaroBitmap.getWidth() - 16.0f;
    	float top = screenHeight - momotaroBitmap.getHeight() - 8.0f;
    	canvas.drawBitmap(momotaroBitmap, left, top, new Paint());
    }
    
	/**
	 * 
	 * 
	 * @param canvas
	 */
	private void drawGameInformation(Canvas canvas)
    {
		// 文字色を白にして、ちょっと影付きにする
	    Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);


        // 結果の表示
        int score = Math.round(resultProvider.getScore(0) * 100.0f);
	    String timeString = parent.getString(R.string.game_score) + " " + score + parent.getString(R.string.game_pts);

	    // フォントのサイズを試しに2倍に拡大してみる。
	    int widthOfFont = (int) (screenWidth  - (screenHeight / 3.0f + 40.0f + RADARICON_SIZE)) / 2;
	    float fontSize = TextDrawingUtility.decideFontSize(timeString, widthOfFont, -1, paint);
	    paint.setTextSize(fontSize);
	     
	     //canvas.drawText(timeString, 10, screenHeight - 100, paint);
	     //canvas.drawText(timeString, 10, 50, paint);
	     canvas.drawText(timeString, 10.0f, 10.0f - paint.getFontMetrics().top + 5.0f, paint);
	     
	    // メッセージ表示枠の表示
		Paint framePaint = new Paint();
		framePaint.setColor(Color.WHITE);
		framePaint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
		framePaint.setStyle(Paint.Style.STROKE);
	    float top = 10.0f - paint.getFontMetrics().top + paint.getFontMetrics().bottom + 16.0f;
	    float left = 5.0f;
	    float bottom = screenHeight - 5.0f;	    
	    float right = screenWidth  - (screenHeight / 3.0f + 40.0f + RADARICON_SIZE);
	    infoArea = null;
	    infoArea = new RectF(left, top, right, bottom);
	    canvas.drawRoundRect(infoArea, 5.0f, 5.0f, framePaint); 

	    //  表示場所を設定する
	    if (isMessageMode == true)
	    {
	    	//  回答結果評価メッセージ表示
	    	drawAnsweredAnalysisResult(canvas, infoArea);
	    }
	    else
	    {		    
		    //  回答時間グラフ表示
		    drawAnsweredTimeChart(canvas, infoArea);
	    }
    }

	/**
	 *    回答結果を解析して表示する
	 * 
	 * @param canvas
	 * @param area
	 */
	private void drawAnsweredAnalysisResult(Canvas canvas, RectF area)
	{
    	float marginX = 10.0f;
    	float marginY = 5.0f;
		Paint paint = new Paint();
	    paint.setColor(Color.WHITE);
	    paint.setStyle(Paint.Style.FILL);

	    // 文字の大きさを決める
	    paint.setTextSize(TextDrawingUtility.decideFontSize(resultMessage, (int)  (area.right - area.left - marginX * 2.0f), (int) (area.bottom - area.top - marginY * 2.0f), 24, paint));
	    
	    // メッセージを表示する
	    TextDrawingUtility.drawTextRegion(canvas, resultMessage, area.left + marginX, area.top + marginY, area.right - marginX, paint);
	}

	/**
	 *    回答時間の推移を棒グラフで表示する
	 * 
	 * @param canvas
	 * @param area
	 */
    private void drawAnsweredTimeChart(Canvas canvas, RectF area)
    {
    	float margin = 5.0f;
    	float axisX = area.left + 10.0f;
    	float axisY = area.bottom - 10.0f;
    	float width = (area.right - area.left) - (margin * 2.0f);
    	float height = (area.bottom - area.top) - (margin * 4.0f);

	    int answers = resultProvider.getNumberOfAnsweredQuestions();
	    float wide = (width / (float) (answers)) - 3.0f;

	    // 軸・タイトルの表示色設定
	    Paint paint = new Paint();
	    paint.setColor(Color.WHITE);
	    paint.setStyle(Paint.Style.FILL);

	    // 補助線の表示設定
	    Paint axisPaint = new Paint();
	    axisPaint.setColor(Color.GRAY);
	    axisPaint.setStyle(Paint.Style.FILL);

	    // OKの棒グラフの色設定
	    Paint okPaint = new Paint();
	    okPaint.setARGB(255, 79, 129, 189);
	    okPaint.setStyle(Paint.Style.FILL);

	    // NGの棒グラフの色設定
	    Paint ngPaint = new Paint();
	    ngPaint.setARGB(255, 255, 0, 0);
	    ngPaint.setStyle(Paint.Style.FILL);

	    // タイトルの表示
	    String title = parent.getString(R.string.detail_title_answertime);

	    // 文字の大きさを決める
	    paint.setTextSize(TextDrawingUtility.decideFontSize(title, (int)  (width), (int) (margin * 4.0f), 16, paint));

	    float titleX = area.right - paint.measureText(title) - margin;

	    canvas.drawText(title, titleX, area.top + margin - paint.getFontMetrics().top, paint);

	    // 軸の表示
	    canvas.drawLine(axisX, (area.top + margin), axisX, (area.bottom - margin), paint);
	    canvas.drawLine((area.left + margin), axisY, (area.right - margin), axisY, paint);

	    // 補助線の表示  (500msごとに線を引く)
	    for (float time = 500.0f; time < 3000.0f; time = time + 500.0f)
	    {
	    	float axisLine = (time / 3000.0f);
	    	axisLine = (axisY - 1.0f) - (axisLine * height);
		    canvas.drawLine((area.left + margin), axisLine, (area.right - margin), axisLine, axisPaint);
	    }
	    
	    // 棒グラフの表示
	    for (int index = 0; index < answers; index++)
	    {
	    	SymbolListArrayItem item = resultProvider.getAnsweredInformation(parent, index);
	    	Paint drawPaint = (item.getIsCorrect() == true) ? okPaint : ngPaint; 
	    	float high = ((float) item.getAnsweredTime() / 3000.0f);
	    	if (high > 1.0f)
	    	{
	    		high = 1.0f;
	    	}
	    	high = (axisY - 1.0f) - (high * height);
	    	if (high > 0.0f)
	    	{
                canvas.drawRect(((axisX + ((float) index * wide)) + 2.0f), high,  (axisX + ((float) (index+ 1.0f) * wide)), (axisY), drawPaint);
	    	}
	    }
    }

    /**
     *    本アプリのタイトルを表示する。
     * 
     * @param canvas
     */
    private void drawTitle(Canvas canvas)
    {
        // 文字の描画スタイル
    	Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
        paint.setStyle(Paint.Style.STROKE);

        // 枠の描画スタイル
        Paint framePaint = new Paint();
    	framePaint.setColor(Color.WHITE);
    	framePaint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
    	framePaint.setStyle(Paint.Style.STROKE);
    	framePaint.setStrokeWidth(2.0f);
    }

	/**
	 *    ゲームの結果を解析して、表示するメッセージを生成する
	 * 
	 * @param resultProvider
	 * @return
	 */
	public String analysisResult(IResultProvider resultProvider)
	{
        float score = resultProvider.getScore(0);
        float score1 = resultProvider.getScore(1);
        float score2 = resultProvider.getScore(2);
        float score3 = resultProvider.getScore(3);
        float score4 = resultProvider.getScore(4);
        int resId = 0;
        int maxCatId = 0;
        int minCatId = 0;

        if (score <= 0.00f)
        {
        	resId = R.string.result_summary_00;
        }
        else if (score < 0.05f)
        {
        	resId = R.string.result_summary_01;        	
        }
        else if (score < 0.10f)
        {
        	resId = R.string.result_summary_02;
        }
        else if (score < 0.15f)
        {
        	resId = R.string.result_summary_03;        	
        }
        else if (score < 0.20f)
        {
        	resId = R.string.result_summary_04;        	
        }
        else if (score < 0.25f)
        {
        	resId = R.string.result_summary_05;        	
        }
        else if (score < 0.30f)
        {
        	resId = R.string.result_summary_06;        	
        }
        else if (score < 0.35f)
        {
        	resId = R.string.result_summary_07;        	
        }
        else if (score < 0.40f)
        {
        	resId = R.string.result_summary_08;        	
        }
        else if (score < 0.425f)
        {
        	resId = R.string.result_summary_09;        	
        }
        else if (score < 0.450f)
        {
        	resId = R.string.result_summary_10;        	
        }
        else if (score < 0.475f)
        {
        	resId = R.string.result_summary_11;        	
        }
        else if (score < 0.500f)
        {
        	resId = R.string.result_summary_12;        	
        }
        else if (score < 0.525f)
        {
        	resId = R.string.result_summary_13;        	
        }
        else if (score < 0.550f)
        {
        	resId = R.string.result_summary_14;        	
        }
        else if (score < 0.575f)
        {
        	resId = R.string.result_summary_15;        	
        }
        else if (score < 0.600f)
        {
        	resId = R.string.result_summary_16;        	
        }
        else if (score < 0.625f)
        {
        	resId = R.string.result_summary_17;        	
        }
        else if (score < 0.650f)
        {
        	resId = R.string.result_summary_18;        	
        }
        else if (score < 0.675f)
        {
        	resId = R.string.result_summary_19;        	
        }
        else if (score < 0.700f)
        {
        	resId = R.string.result_summary_20;        	
        }
        else if (score < 0.725f)
        {
        	resId = R.string.result_summary_21;
        }
        else if (score < 0.750f)
        {
        	resId = R.string.result_summary_22;
        }
        else if (score < 0.800f)
        {
        	resId = R.string.result_summary_23;
        }
        else if (score < 0.825f)
        {
        	resId = R.string.result_summary_24;
        }
        else if (score < 0.850f)
        {
        	resId = R.string.result_summary_25;
        }
        else if (score < 0.875f)
        {
        	resId = R.string.result_summary_26;
        }
        else if (score < 0.900f)
        {
        	resId = R.string.result_summary_27;
        }
        else if (score < 0.925f)
        {
        	resId = R.string.result_summary_28;
        }
        else if (score < 0.950f)
        {
        	resId = R.string.result_summary_29;
        }
        else if (score < 0.975f)
        {
        	resId = R.string.result_summary_30;
        }
        else if (score < 0.995f)
        {
        	resId = R.string.result_summary_31;
        }
        else // if (score >= 1.0f)
        {
        	resId = R.string.result_summary_32;
        }
        
        float max = Math.max(score1, Math.max(score2, Math.max(score3, score4)));
        float min = Math.min(score1, Math.min(score2, Math.min(score3, score4)));
        maxCatId = decideScoreCategoryResId(max, score1, score2, score3, score4);
        minCatId = decideScoreCategoryResId(min, score1, score2, score3, score4);

	    String messageToShow = "";
        if ((max - min) < 0.10f)
        {
        	// max と min の差が0.1よりも小さい時には、回答的にバランスがとれている、とする。
        	if (max > 0.85f)
        	{
        		// よい方向でバランスがとれているとき...
        		messageToShow = messageToShow + "" + parent.getString(R.string.result_category_balance_good);
        		messageToShow = messageToShow + "　" + parent.getString(resId);
        	    return (messageToShow);
        	}
        	else
        	{
        	    messageToShow = messageToShow + "" + parent.getString(R.string.result_category_balance_normal);        		
        	}
        }
        if (min < 0.40f)
        {
        	// ある分野が悪い場合...
    	    messageToShow = messageToShow + "" +  parent.getString(minCatId) + parent.getString(R.string.result_category_weak);        		
        }
        if (max > 0.99f)
        {
        	// ある分野が完璧の時
    	    messageToShow = messageToShow + "" +  parent.getString(maxCatId) + parent.getString(R.string.result_category_good);       	
        }
        else if (max >0.85f)
        {
        	// ある分野の成績が良い時
    	    messageToShow = messageToShow + "" +  parent.getString(maxCatId) + parent.getString(R.string.result_category_better);       	
        }
		messageToShow = messageToShow + "　" + parent.getString(resId);
	    return (messageToShow);
	}

	/**
	 *   どのスコアか応答する。（リソースIDを含めて)
	 * 
	 * @param check
	 * @param cat1
	 * @param cat2
	 * @param cat3
	 * @param cat4
	 * @return
	 */
	private int decideScoreCategoryResId(float check, float cat1, float cat2, float cat3, float cat4)
	{
		int resId = 0;
		if (check == cat1)
		{
			resId = R.string.result_category_1;
		}
		else if (check == cat2)
		{
			resId = R.string.result_category_2;	
		}
		else if (check == cat3)
		{
			resId = R.string.result_category_3;	
		}
		else // else if (check == cat4)
		{
			resId = R.string.result_category_4;
		}
		return (resId);
	}	
}
