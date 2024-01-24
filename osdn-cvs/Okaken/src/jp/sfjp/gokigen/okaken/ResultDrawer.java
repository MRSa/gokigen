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
 *    ���ʕ\���p�̃L�����o�X�N���X
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

	private float screenWidth = 0.0f;  // �\���̈�̕�
	private float screenHeight = 0.0f; // �\���̈�̍���
	
	//private String backgroundBitmapUri = null;
	private Bitmap backgroundBitmap = null;
    private ArrayList<Bitmap> momotaroBitmaps = null;

    // 4���̃O���t���x��
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
	 *    �R���X�g���N�^
	 * 
	 * @param parent
	 * @param resultProvider
	 */
	public ResultDrawer(Activity parent, IResultProvider resultProvider)
    {
    	this.parent = parent;
    	this.resultProvider = resultProvider;
    	
		// �������낳��̃r�b�g�}�b�v��ǂݍ���ł݂�
    	prepareMomotaroBitmaps(parent);

    	// ���[�_�[�`���[�g�p�̃A�C�R����ǂݏo��
    	placeBitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.place)), RADARICON_SIZE, RADARICON_SIZE, false);
    	peopleBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.people)), RADARICON_SIZE, RADARICON_SIZE, false);
    	foodBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.food)), RADARICON_SIZE, RADARICON_SIZE, false);
    	specialBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.special)), RADARICON_SIZE, RADARICON_SIZE, false);

    	// �o�C�u���[�^�T�[�r�X���E��
    	vibrator = (Vibrator) parent.getSystemService(Context.VIBRATOR_SERVICE);

    	// ���ʂ���͂��ă��b�Z�[�W�𐶐�����
    	resultMessage = analysisResult(resultProvider);
    }
	
    /**
     *    �������낳��̃r�b�g�}�b�v��ǂݍ���ŕێ�����
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
		  // �\����ʃT�C�Y���o����
		  screenWidth = width;
		  screenHeight = height;    	    	
    }

	  /**
	   * 
	   * @param uri
	   */
	  public void updateBackgroundBitmap(String uri, int width, int height)
	  {
		  // �w�i�摜�̕�������L������
		  // backgroundBitmapUri = uri;

		  // �Ƃ肠�����A�w�i�摜���N���A���ăK�x�R������B
		  backgroundBitmap = null;
		  //System.gc();
		  if (uri.length() <= 0)
		  {
			  // �w�i�摜�̎w�肪�Ȃ������̂ŁA�����Ń��^�[������B
			  return;
		  }
		  try
		  {
			  // �ݒ肷��������O�Ɏc���Ă݂�
			  Log.v(Gokigen.APP_IDENTIFIER, "ResultDrawer::updateBackgroundBitmap() : w:" + width + " , h:"+ height + " " + uri);

			  // �w�i�摜���擾���Đݒ肷��B
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
	     *    ��ʂ𑀍삵�����̏���...
	     * 
	     */
	    public boolean onTouchEvent(MotionEvent event)
	    {
	    	boolean isDraw = false;

	        // �^�b�`�|�W�V�������L������i�`���^�����O�h�~�j
		    int action = event.getAction();
	        if (action == MotionEvent.ACTION_DOWN)
	        {        	
	            downPosX = event.getX();
		        downPosY = event.getY();
	    		if ((infoArea != null)&&(infoArea.contains(downPosX, downPosY) == true))
	    		{
	    			// �\�����[�h��؂�ւ���
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
	    		// �w�i�F������
	    		canvas.drawColor(backgroundColor);	    			

    			// �^�C�g���\��
    			drawTitle(canvas);
    			
    			// ���[�_�[�`���[�g�̕\��
    			drawRadarChart(canvas);

    			// ��������A�C�R���̕\��
    			drawMomotaroIcon(canvas);
    			
    			// �Q�[���̏����o�͂���
                drawGameInformation(canvas);    			
	    	}
	        catch (Exception ex)
	    	{
	    		
	    	}
	}

    /**
     *   ���[�_�[�`���[�g��\������
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

    	// �g�̏����F�g�F�𔒂ɂ��āA������Ɠ��߂ɂ���
		Paint framePaint = new Paint();
		framePaint.setColor(Color.WHITE);
		framePaint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
		framePaint.setStyle(Paint.Style.STROKE);

		// �g�̒��Ƀ��[�_�[�`���[�g��\������
        FourAxisRadarChartDrawer.drawRadarChartBase(canvas, centerX, centerY, radius, graphMargin, framePaint);

        // ���ɃA�C�R����\��t����
        FourAxisRadarChartDrawer.drawRadarAxisIcons(canvas, centerX, centerY, radius, placeBitmap, peopleBitmap, foodBitmap, specialBitmap);

        //  ���ʃO���t��`�悷��
        FourAxisRadarChartDrawer.drawRadarChartLine(canvas, centerX, centerY, radius, resultProvider.getScore(1), resultProvider.getScore(2), resultProvider.getScore(3), resultProvider.getScore(4));
	}
    
    /**
     *   ��������A�C�R���̕\�����s��
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
		// �����F�𔒂ɂ��āA������Ɖe�t���ɂ���
	    Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);


        // ���ʂ̕\��
        int score = Math.round(resultProvider.getScore(0) * 100.0f);
	    String timeString = parent.getString(R.string.game_score) + " " + score + parent.getString(R.string.game_pts);

	    // �t�H���g�̃T�C�Y��������2�{�Ɋg�債�Ă݂�B
	    int widthOfFont = (int) (screenWidth  - (screenHeight / 3.0f + 40.0f + RADARICON_SIZE)) / 2;
	    float fontSize = TextDrawingUtility.decideFontSize(timeString, widthOfFont, -1, paint);
	    paint.setTextSize(fontSize);
	     
	     //canvas.drawText(timeString, 10, screenHeight - 100, paint);
	     //canvas.drawText(timeString, 10, 50, paint);
	     canvas.drawText(timeString, 10.0f, 10.0f - paint.getFontMetrics().top + 5.0f, paint);
	     
	    // ���b�Z�[�W�\���g�̕\��
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

	    //  �\���ꏊ��ݒ肷��
	    if (isMessageMode == true)
	    {
	    	//  �񓚌��ʕ]�����b�Z�[�W�\��
	    	drawAnsweredAnalysisResult(canvas, infoArea);
	    }
	    else
	    {		    
		    //  �񓚎��ԃO���t�\��
		    drawAnsweredTimeChart(canvas, infoArea);
	    }
    }

	/**
	 *    �񓚌��ʂ���͂��ĕ\������
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

	    // �����̑傫�������߂�
	    paint.setTextSize(TextDrawingUtility.decideFontSize(resultMessage, (int)  (area.right - area.left - marginX * 2.0f), (int) (area.bottom - area.top - marginY * 2.0f), 24, paint));
	    
	    // ���b�Z�[�W��\������
	    TextDrawingUtility.drawTextRegion(canvas, resultMessage, area.left + marginX, area.top + marginY, area.right - marginX, paint);
	}

	/**
	 *    �񓚎��Ԃ̐��ڂ�_�O���t�ŕ\������
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

	    // ���E�^�C�g���̕\���F�ݒ�
	    Paint paint = new Paint();
	    paint.setColor(Color.WHITE);
	    paint.setStyle(Paint.Style.FILL);

	    // �⏕���̕\���ݒ�
	    Paint axisPaint = new Paint();
	    axisPaint.setColor(Color.GRAY);
	    axisPaint.setStyle(Paint.Style.FILL);

	    // OK�̖_�O���t�̐F�ݒ�
	    Paint okPaint = new Paint();
	    okPaint.setARGB(255, 79, 129, 189);
	    okPaint.setStyle(Paint.Style.FILL);

	    // NG�̖_�O���t�̐F�ݒ�
	    Paint ngPaint = new Paint();
	    ngPaint.setARGB(255, 255, 0, 0);
	    ngPaint.setStyle(Paint.Style.FILL);

	    // �^�C�g���̕\��
	    String title = parent.getString(R.string.detail_title_answertime);

	    // �����̑傫�������߂�
	    paint.setTextSize(TextDrawingUtility.decideFontSize(title, (int)  (width), (int) (margin * 4.0f), 16, paint));

	    float titleX = area.right - paint.measureText(title) - margin;

	    canvas.drawText(title, titleX, area.top + margin - paint.getFontMetrics().top, paint);

	    // ���̕\��
	    canvas.drawLine(axisX, (area.top + margin), axisX, (area.bottom - margin), paint);
	    canvas.drawLine((area.left + margin), axisY, (area.right - margin), axisY, paint);

	    // �⏕���̕\��  (500ms���Ƃɐ�������)
	    for (float time = 500.0f; time < 3000.0f; time = time + 500.0f)
	    {
	    	float axisLine = (time / 3000.0f);
	    	axisLine = (axisY - 1.0f) - (axisLine * height);
		    canvas.drawLine((area.left + margin), axisLine, (area.right - margin), axisLine, axisPaint);
	    }
	    
	    // �_�O���t�̕\��
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
     *    �{�A�v���̃^�C�g����\������B
     * 
     * @param canvas
     */
    private void drawTitle(Canvas canvas)
    {
        // �����̕`��X�^�C��
    	Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
        paint.setStyle(Paint.Style.STROKE);

        // �g�̕`��X�^�C��
        Paint framePaint = new Paint();
    	framePaint.setColor(Color.WHITE);
    	framePaint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
    	framePaint.setStyle(Paint.Style.STROKE);
    	framePaint.setStrokeWidth(2.0f);
    }

	/**
	 *    �Q�[���̌��ʂ���͂��āA�\�����郁�b�Z�[�W�𐶐�����
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
        	// max �� min �̍���0.1�������������ɂ́A�񓚓I�Ƀo�����X���Ƃ�Ă���A�Ƃ���B
        	if (max > 0.85f)
        	{
        		// �悢�����Ńo�����X���Ƃ�Ă���Ƃ�...
        		messageToShow = messageToShow + "" + parent.getString(R.string.result_category_balance_good);
        		messageToShow = messageToShow + "�@" + parent.getString(resId);
        	    return (messageToShow);
        	}
        	else
        	{
        	    messageToShow = messageToShow + "" + parent.getString(R.string.result_category_balance_normal);        		
        	}
        }
        if (min < 0.40f)
        {
        	// ���镪�삪�����ꍇ...
    	    messageToShow = messageToShow + "" +  parent.getString(minCatId) + parent.getString(R.string.result_category_weak);        		
        }
        if (max > 0.99f)
        {
        	// ���镪�삪�����̎�
    	    messageToShow = messageToShow + "" +  parent.getString(maxCatId) + parent.getString(R.string.result_category_good);       	
        }
        else if (max >0.85f)
        {
        	// ���镪��̐��т��ǂ���
    	    messageToShow = messageToShow + "" +  parent.getString(maxCatId) + parent.getString(R.string.result_category_better);       	
        }
		messageToShow = messageToShow + "�@" + parent.getString(resId);
	    return (messageToShow);
	}

	/**
	 *   �ǂ̃X�R�A����������B�i���\�[�XID���܂߂�)
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
