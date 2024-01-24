package jp.sfjp.gokigen.okaken;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

/**
 *   MainDrawer 起動時に表示するActivityにある SurfaceView の描画クラス
 * 
 * @author MRSa
 *
 */
public class MainDrawer implements ICanvasDrawer
{
	public static final int BACKGROUND_COLOR_DEFAULT = 0xff004000;
	private int backgroundColor = BACKGROUND_COLOR_DEFAULT;

	private static final int VIBRATION_DURATION = 50;

	private static final int INSTRUCTION_NOT_YET = 0;
	private static final int INSTRUCTION_SCENE_1 = 1;
	private static final int INSTRUCTION_SCENE_2 = 2;
	private static final int INSTRUCTION_SCENE_3 = 3;
	private static final int INSTRUCTION_SCENE_4 = 4;
	private static final int INSTRUCTION_SCENE_5 = 5;
	private static final int INSTRUCTION_SCENE_6 = 6;
	private static final int INSTRUCTION_SCENE_7 = 7;
	private static final int INSTRUCTION_DONE      = 100;

	private static final int INSTRUCTION_NEXT_PAGE = 8;	  // 8 * 500 msで説明の次ページ送り
	
	private float screenWidth = 0.0f;  // 表示領域の幅
	private float screenHeight = 0.0f; // 表示領域の高さ
	
	//private String backgroundBitmapUri = null;
	private Bitmap backgroundBitmap = null;
	private Bitmap peachBitmap  = null;
	private Bitmap momotaroBitmap = null;
	private Bitmap momotaroBitmap_normal = null;
	private Bitmap momotaroBitmap_sad = null;
	private Bitmap momotaroBitmap_happy = null;
	
	private Bitmap instruction_timer_bitmap = null;
    private Bitmap instruction_radar_bitmap = null;
	private Bitmap instruction_ok_bitmap = null;
	private Bitmap instruction_ng_bitmap = null;
	private Bitmap instruction_bitmap = null;
	private int       instruction_scene = INSTRUCTION_NOT_YET;
	private int       instruction_timer = 0;
    private RectF   instructionButton_RectF = null;

	private float downPosX = Float.MIN_VALUE;
	private float downPosY = Float.MIN_VALUE;

	private Activity parent = null;
	//private IGameInformationProvider provider = null;
	private IActivityOpener activityOpener = null;

	private Vibrator vibrator = null;
	
	private boolean isShowInstruction = false;

	public MainDrawer(Activity parent, IActivityOpener opener)
    {
    	this.parent = parent;
    	this.activityOpener = opener;
    	
    	// 桃を読み込む
    	peachBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.peach);

    	// ももたろさんを読み込む
    	Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.momo_l3);
    	momotaroBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.momo_l6);
    	momotaroBitmap_sad = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.momo_l2);
       	momotaroBitmap_happy = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.momo_l1b);
    	momotaroBitmap_normal = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	
    	// 操作説明のビットマップを読み込む
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.instruction_ok);
    	instruction_ok_bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.instruction_ng);
    	instruction_ng_bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.instruction_normal);
    	instruction_bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.instruction_radar);
    	instruction_radar_bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.instruction_time);
    	instruction_timer_bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);

    	// バイブレータサービスを拾う
    	vibrator = (Vibrator) parent.getSystemService(Context.VIBRATOR_SERVICE);
    }
	
    public void prepareToStart(int width, int height)
    {
    	
    	
    }

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
			  Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::updateBackgroundBitmap() : w:" + width + " , h:"+ height + " " + uri);

			  // 背景画像を取得して設定する。
			  backgroundBitmap = ImageLoader.getBitmapFromUri(parent, ImageLoader.parseUri(uri), width, height);
		  }
		  catch (Exception ex)
		  {
			  Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::updateBackgroundBitmap() : " + uri + " , "+ ex.toString());
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

	    		if (isShowInstruction == false)
	    		{
	    			// 通常のタイトル表示
	    			drawTitle(canvas);

	    			// ゲームの情報を出力する
	                drawGameInformation(canvas);    			
	    		}
	    		else
	    		{
	    			//  説明表示のモードのとき
	    			drawInstruction(canvas);
	    		}
	    	}
	        catch (Exception ex)
	    	{
	    		
	    	}
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

	     String timeString = parent.getString(R.string.title_startInformation);

	     // フォントのサイズを調整する
	     paint.setTextSize(TextDrawingUtility.decideFontSize(timeString, (int) (screenWidth - 60), -1, paint));
		
	     canvas.drawText(timeString, 10, screenHeight - 130, paint);
	     //canvas.drawText(timeString, 10, 50, paint);
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

		// 桃を表示する
        float top = (screenHeight  - peachBitmap.getHeight()) / 2.0f;
        float left = (screenWidth - peachBitmap.getWidth()) / 2.0f;
		canvas.drawBitmap(peachBitmap, left, top, paint);    
		
        // タイトルを表示する
		String titleString = parent.getString(R.string.app_name);
		paint.setTextSize(TextDrawingUtility.decideFontSize(titleString, (int) (screenWidth - 20.0f), 0, paint));
		canvas.drawText(titleString, 10, (30 - paint.getFontMetrics().top), paint);		
		
		// 画面下部に表示するボタン（ぽいもの）のフォントのサイズを調整する。
        String buttonString = parent.getString(R.string.title_btnInstruction);
        float fontWidth =  (screenWidth / 4.0f);
        paint.setTextSize(TextDrawingUtility.decideFontSize(buttonString, (int) (fontWidth), 0, paint));        
        FontMetrics metrics = paint.getFontMetrics();        
        float fontHeight = metrics.bottom - metrics.top;
        instructionButton_RectF = new RectF((screenWidth - fontWidth  - 16.0f), (screenHeight - fontHeight - 12.0f), (screenWidth - 6.0f), (screenHeight - 4.0f));
        
		// 画面下部にボタン（ぽいもの）を表示する処理
    	canvas.drawRoundRect(instructionButton_RectF, 5.0f, 5.0f, framePaint);
    	canvas.drawText(buttonString, (screenWidth - fontWidth - 11.0f) , (screenHeight - fontHeight - 8.0f) - metrics.top, paint);
    }

    /**
     *    説明を表示するシーンを切り替える
     * 
     * @param canvas
     */
    private void drawInstruction(Canvas canvas)
    {
       // シーン別に表示内容を変える
 	   switch (instruction_scene)
	   {
	     case INSTRUCTION_NOT_YET:
	    	 drawInstruction(canvas, momotaroBitmap, null, R.string.gameInstruction_Start);
             break;
	     case INSTRUCTION_SCENE_1:
	    	 drawInstruction(canvas, momotaroBitmap_normal, null, R.string.gameInstruction_001);
             break;
	     case INSTRUCTION_SCENE_2:
	    	 drawInstruction(canvas, momotaroBitmap, instruction_bitmap, R.string.gameInstruction_002);
             break;
	     case INSTRUCTION_SCENE_3:
	    	 drawInstruction(canvas, momotaroBitmap_happy, instruction_ok_bitmap, R.string.gameInstruction_003);
             break;
	     case INSTRUCTION_SCENE_4:
	    	 drawInstruction(canvas, momotaroBitmap_sad, instruction_ng_bitmap, R.string.gameInstruction_004);
            break;
	     case INSTRUCTION_SCENE_5:
	    	 drawInstruction(canvas, momotaroBitmap, instruction_timer_bitmap, R.string.gameInstruction_005);
             break;
	     case INSTRUCTION_SCENE_6:
	    	 drawInstruction(canvas, momotaroBitmap_normal, instruction_radar_bitmap, R.string.gameInstruction_006);
             break;
	     case INSTRUCTION_SCENE_7:
	    	 drawInstruction(canvas, momotaroBitmap, null, R.string.gameInstruction_007);
             break;
	     case INSTRUCTION_DONE:
	     default:
	    	 drawInstruction(canvas, momotaroBitmap_normal, null, R.string.gameInstruction_Done);
             break;
	   }
 	   // タイマーが一定時間を超えた場合には、次ページの表示に自動的に進む
 	  instruction_timer++;
 	   if (instruction_timer > INSTRUCTION_NEXT_PAGE)
 	   {
 		   updateInstructionScene();
 	   } 	   
    }

    /**
     *    説明を表示する
     * 
     * @param canvas
     */
    private void drawInstruction(Canvas canvas, Bitmap momotaro, Bitmap instruction, int messageResId)
    {
    	// メッセージ表示用のサイズを調整する
    	Paint paint = new Paint();
    	int fontSize = 32;
    	paint.setTextSize(fontSize);
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
        FontMetrics fontMetrics = paint.getFontMetrics();
        float heightTextMargin = fontMetrics.bottom + fontMetrics.leading - fontMetrics.top + 2.0f;
        float momotaroHeight = momotaro.getHeight();
        float instructionHeight = instruction_bitmap.getHeight() ;
        float textTopHeight = screenHeight - momotaroHeight - instructionHeight - (heightTextMargin * 4) - 6.0f;

        // 説明用の文字列を取得する
        String message = parent.getString(messageResId);
                
        // 説明用の文字列を表示するための吹き出しの枠を試しに準備してみる（説明用の文字列があったときだけ）
        if (message.length() > 0)
        {
            Paint backPaint = new Paint();
            backPaint.setColor(Color.WHITE);
            backPaint.setStyle(Paint.Style.STROKE);
            //canvas.drawRect(8, textTopHeight - 1, screenWidth - 18, screenHeight - momotaroHeight - (heightTextMargin / 2.0f), backPaint);
            canvas.drawRoundRect(new RectF(8, textTopHeight - 1, screenWidth - 18, screenHeight - momotaroHeight - (heightTextMargin / 2.0f)), 8, 8, backPaint);

            // 説明用の文字列を表示する
            TextDrawingUtility.drawTextRegion(canvas, message, 10, textTopHeight, screenWidth - 20, paint);
        }

        // 説明用のビットマップが引数に指定されていた場合、説明用のビットマップを表示する
        if (instruction != null)
        {
            canvas.drawBitmap(instruction, 20, screenHeight - momotaroHeight - instructionHeight - heightTextMargin, paint);  
        }

        // タイトルの大きさ等を調整する
    	String title = parent.getString(R.string.title_btnInstruction);
    	fontSize = TextDrawingUtility.decideFontSize(title, (int) screenWidth, 0, paint);
    	paint.setTextSize(fontSize);
        fontMetrics = paint.getFontMetrics();
        heightTextMargin = fontMetrics.bottom + fontMetrics.leading - fontMetrics.top + 2.0f;
        float titleSize = (heightTextMargin + (fontMetrics.bottom - fontMetrics.top));
        
        if (((message.length() == 0))||(textTopHeight > titleSize))
        {
            // 画面上部にタイトルを表示する
            canvas.drawText(title, 0, heightTextMargin, paint);
        }
        //Log.v(Gokigen.APP_IDENTIFIER, "topMessage: " + textTopHeight + "  titleBottom:" + titleSize);
        
        // 画面左下にももたろさんの顔を表示する
        canvas.drawBitmap(momotaro, 0, screenHeight - momotaroHeight - 2.0f, paint);    
    }
    
    /**
     *    操作説明画面を出す
     * 
     */
    public void showInstruction(int id)
    {
    	//Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::showInstruction() : " + id); 

    	// 説明表示モードと通常のタイトル表示モードを切り替える
    	isShowInstruction = (isShowInstruction == true) ? false : true;
    }
    
    /**
     *    画面を操作した時の処理...
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	boolean isDraw = false;
        if (isShowInstruction == false)
    	{
    		//  タイトル表示モード
    		isDraw = onTouchTitleMode(event);
    	}
    	else
    	{
    		//  せつめい表示モード
    		isDraw = onTouchInstructionMode(event);
    	}

        // タッチポジションを記憶する（チャタリング防止）
	    int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {        	
            downPosX = event.getX();
	        downPosY = event.getY();
        }
        else if (action == MotionEvent.ACTION_UP)
        {
            downPosX = Float.MIN_VALUE;
	        downPosY = Float.MIN_VALUE;
        }
    	return (isDraw);
    }
    
    /**
     *    タイトル表示モードの時に画面にタッチされたとき
     * 
     * @param event
     */
    private boolean onTouchTitleMode(MotionEvent event)
    {
        // Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::onTouchEvent() : "); 
	    int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
    		// ぶるぶるさせる
    		vibrator.vibrate(VIBRATION_DURATION);
    	
    		if (instructionButton_RectF.contains(event.getX(), event.getY()) == true)
    		{
    			// せつめい表示モードに切り替える
    			isShowInstruction = true;
    			return (true);
    		}
    	
    		if (activityOpener != null)
        	{
    			// ゲームスタート！
        		activityOpener.requestToStartActivity(0);
        	}
    		return (true);
        }    	
        return (false);
    }

    /**
     *    せつめい表示モードの時に画面にタッチされたとき
     * 
     * @param event
     */
    private boolean onTouchInstructionMode(MotionEvent event)
    {
	    int action = event.getAction();
        //Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::onTouchEvent() : " + action); 
        if ((action == MotionEvent.ACTION_DOWN)&&(downPosX == Float.MIN_VALUE))
        {
        	// タッチ一発目のとき...
        	
    		// ぶるぶるさせる
    		vibrator.vibrate(VIBRATION_DURATION);

    		// 次の表示へ進める。
    		updateInstructionScene();
    		
    		return (true);
        }
        return (false);
   }

   /**
    * 　説明表示モードのとき、次に表示するシーンを切り替える
    * 
    */
   private void updateInstructionScene()
   {
	   int nextScene = INSTRUCTION_NOT_YET;
	   switch (instruction_scene)
	   {
	     case INSTRUCTION_NOT_YET:
             nextScene = INSTRUCTION_SCENE_1;
             break;
	     case INSTRUCTION_SCENE_1:
             nextScene = INSTRUCTION_SCENE_2;
             break;
	     case INSTRUCTION_SCENE_2:
             nextScene = INSTRUCTION_SCENE_3;
             break;
	     case INSTRUCTION_SCENE_3:
             nextScene = INSTRUCTION_SCENE_4;
             break;
	     case INSTRUCTION_SCENE_4:
             nextScene = INSTRUCTION_SCENE_5;
             break;
	     case INSTRUCTION_SCENE_5:
             nextScene = INSTRUCTION_SCENE_6;
             break;
	     case INSTRUCTION_SCENE_6:
             nextScene = INSTRUCTION_SCENE_7;
             break;
	     case INSTRUCTION_SCENE_7:
             nextScene = INSTRUCTION_DONE;
             //break;
	     case INSTRUCTION_DONE:
	     default:
	    	 // 説明モードを終了する
             nextScene = INSTRUCTION_NOT_YET;
             isShowInstruction = false;
             break;
	   }
	   instruction_timer = 0;
	   instruction_scene = nextScene;
   }
}
