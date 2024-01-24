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
 *    ���񓚂̕`��N���X
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

	private float screenWidth = 0.0f;  // �\���̈�̕�
	private float screenHeight = 0.0f; // �\���̈�̍���

	//private String backgroundBitmapUri = null;
	private Bitmap backgroundBitmap = null;
	private Bitmap peachBitmap  = null;          // ����\�����ĂȂ����̓�
	private Bitmap openPeachBitmap  = null;   // ���\�����̓�
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

	// ���ʉ��ݒ�...    
    private SoundPool  soundEffect = null;
    private int            seOkId= 0;
    private int            seNgId= 0;
    
    private boolean workAround = false;
	
	/**
	 *   �R���X�g���N�^...�g�p����r�b�g�}�b�v����ǂݍ���ŕێ�����
	 * 
	 * @param parent
	 * @param infoProvider
	 */
	public MoleGameDrawer(Activity parent, IGameInformationProvider infoProvider, IActivityOpener opener)
    {
    	this.parent = parent;
    	this.provider = infoProvider;
    	this.activityOpener = opener;
    	
    	// ����ǂݍ���
    	peachBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.peach);
    	openPeachBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.peach_open);
    	// ����ǂݍ���
    	okBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.good);
    	// �~��ǂݍ���
    	ngBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.bad);

    	// ���[�_�[�`���[�g�p�̃A�C�R����ǂݏo��
    	placeBitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.place)), RADARICON_SIZE, RADARICON_SIZE, false);
    	peopleBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.people)), RADARICON_SIZE, RADARICON_SIZE, false);
    	foodBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.food)), RADARICON_SIZE, RADARICON_SIZE, false);
    	specialBitmap =  Bitmap.createScaledBitmap((BitmapFactory.decodeResource(parent.getResources(), R.drawable.special)), RADARICON_SIZE, RADARICON_SIZE, false);
    	
    	// �o�C�u���[�^�T�[�r�X���E��
    	vibrator = (Vibrator) parent.getSystemService(Context.VIBRATOR_SERVICE);
        isGameOver = false;
    	workAround = false;

        // ���ʉ��Đ��N���X�̐ݒ�
        soundEffect = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundEffect.setOnLoadCompleteListener(this);

        // ���ʉ��̏���
        seOkId = soundEffect.load(parent, R.raw.sound_ok, 1);   // OK���ʉ��̃��[�h
        seNgId = soundEffect.load(parent, R.raw.sound_ng, 1);   // NG���ʉ��̃��[�h
    }
	
	/**
	 * �@�N�����̏���
	 * 
	 */
    public void prepareToStart(int width, int height)
    {
		  // �\����ʃT�C�Y���o����
		  //screenWidth = width;
		  //screenHeight = height;

    	// �N�����́A�Q�[���I�[�o�[�\���𕜋�������
   	    isGameOver = false;
   	    workAround = false;
   	}
    
    /**
     *   ��ʃT�C�Y���ύX���ꂽ���ɌĂ΂��
     * 
     */
    public void changedScreenProperty(int format, int width, int height)
    {
		  // �\����ʃT�C�Y���o����
		  screenWidth = width;
		  screenHeight = height;    	
    }

	  /**
	   * �@�w�i�ɕ\������摜��ݒ肷��
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
			  Log.v(Gokigen.APP_IDENTIFIER, "MoleGameDrawer::updateBackgroundBitmap() : w:" + width + " , h:"+ height + " " + uri);

			  // �w�i�摜���擾���Đݒ肷��B
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
	   *    �Q�[���I�[�o�[�̃L�����o�X�f�[�^�X�V���I�����Ă��邩�ǂ���
	   * 
	   * @return
	   */
	  public boolean isGameOverDrawn()
	  {
		  return (isGameOver);
	  }
	  
	 /**
	  *    ���݂̉񓚃��x���󋵂��C���[�W�ŕ\������B
	  * 
	  * @param canvas
	  */
    private void drawCurrentLevel(Canvas canvas, Bitmap drawBitmap)
    {
    	// �r�b�g�}�b�v�̑傫����ύX���� (TODO ����ύX�����I�j
    	int width = (int) (screenWidth / MoleGameActivity.NUMBER_OF_MOLE_COLUMNS - 2.0f);
    	int height =(int) (screenHeight / MoleGameActivity.NUMBER_OF_MOLE_ROWS - 4.0f); 
        Bitmap bitmap = Bitmap.createScaledBitmap(drawBitmap, width, height, false);
 
    	// �`��ʒu�̌���
        float bitmapTop = screenHeight - bitmap.getHeight();
        float bitmapLeft = screenWidth - bitmap.getWidth();
        canvas.drawBitmap(bitmap, bitmapLeft, bitmapTop, new Paint());        
    }
	  
    /**
     *    �Q�[���I���I �̃��b�Z�[�W��\������
     * 
     * @param canvas
     */
    private void drawGameOverMessage(Canvas canvas)
    {
    	// �Q�[�����ʂ̃��x���������r�b�g�}�b�v���܂��\������
    	drawCurrentLevel(canvas, provider.getResultLevelbitmap());

    	// �����F�̏��� : ���F�ɂ��Ă�����Ɖe�t���ɂ���
    	Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
    	
        // �g�̏����F�g�F�𔒂ɂ��āA������Ɠ��߂ɂ���
		Paint framePaint = new Paint();
		framePaint.setColor(Color.DKGRAY);
		framePaint.setShadowLayer(0.6f, 0.6f, 0.6f, Color.DKGRAY);
		framePaint.setColor(0xaa444444);  // Color.DKGRAY
		framePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // �g�̏����F�g�F�𔒂ɂ��āA������Ɠ��߂ɂ���
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
        	// �K�K�b�ƁA�Q�[���I�����ɘg���g�傷��i������Ƃ����������A�j���[�V�����j
            canvas.drawRoundRect(new RectF(width - timerCount * wideSize, height - timerCount * highSize, width + wideSize * timerCount,  height + timerCount * highSize), 5, 5, framePaint);
            canvas.drawRoundRect(new RectF(width - timerCount * wideSize, height - timerCount * highSize, width + wideSize * timerCount,  height + timerCount * highSize), 5, 5, radarPaint);
            return;
        }
        timerCount--; // �I�[�o�[�����T�C�Y�����Ƃɖ߂�
        float heightMargin = timerCount * highSize;

        // ----- �Q�[�����s���ʂ̕\���A������Ƃ���΂�ǂ���  -----

        //  �\���p�̘g�����
        canvas.drawRoundRect(new RectF(5.0f, height - heightMargin, screenWidth - 5.0f,  height + heightMargin), 5, 5, framePaint);
        canvas.drawRoundRect(new RectF(5.0f, height - heightMargin, screenWidth - 5.0f,  height + heightMargin), 5, 5, radarPaint);

        float graphWidth = screenWidth / 4.0f;
        float graphOffsetX = 30.0f;
        float graphMargin = 10.0f;

        // ��ʏ㕔�ɕ����ɂ�錋�ʕ\���i�œK���K�v�j
        String gameOverString = parent.getString(R.string.result_title);
        float titleSize = TextDrawingUtility.decideFontHeightSize(gameOverString, (int) graphOffsetX, 0, paint);
        paint.setTextSize(titleSize);
        canvas.drawText(gameOverString, 10.0f, height - heightMargin - paint.getFontMetrics().top, paint);
        
        // �g�̒��Ƀ��[�_�[�`���[�g��\������
        FourAxisRadarChartDrawer.drawRadarChartBase(canvas, graphOffsetX + graphWidth, height, graphWidth, graphMargin, radarPaint);

        // ���ɃA�C�R����\��t����
        FourAxisRadarChartDrawer.drawRadarAxisIcons(canvas, graphOffsetX + graphWidth, height, graphWidth, placeBitmap, peopleBitmap, foodBitmap, specialBitmap);

        //  ���ʃO���t��`�悷��
        FourAxisRadarChartDrawer.drawRadarChartLine(canvas, (graphOffsetX + graphWidth), height, graphWidth, provider.getScore(1), provider.getScore(2), provider.getScore(3), provider.getScore(4));

        //  �����X�R�A���O���t�̉��ɂł��ł��ƕ\������
        int score = Math.round(provider.getScore(0) * 100.0f);
        float textWidth = (screenWidth - 10.0f) - (graphOffsetX+ graphWidth) * 2.0f;
        String scoreString = score + parent.getString(R.string.game_pts);
    	paint.setTextSize(TextDrawingUtility.decideFontSize(scoreString, (int) (textWidth), 0, paint));
    	canvas.drawText(scoreString, ((graphOffsetX+ graphWidth) * 2.0f), (height + graphWidth) - paint.getFontMetrics().bottom, paint);

        // �h�����Ƃ��킵���h �{�^����\������
        String buttonString = parent.getString(R.string.game_showDetailInformation);
        float textHeight = ((height + heightMargin) - 8.0f) - (height + graphWidth);
        float buttonSize = TextDrawingUtility.decideFontSize(buttonString, (int) (screenWidth - 15.0f - ((graphOffsetX+ graphWidth) * 2.0f)), (int) (textHeight), 0, paint);
        paint.setTextSize(buttonSize);
        textHeight = ((heightMargin - 8.0f - graphWidth) -  (paint.getFontMetrics().bottom - paint.getFontMetrics().top)) / 2.0f;
        detailButtonRect = new RectF(((graphOffsetX+ graphWidth) * 2.0f), (height + graphWidth), screenWidth - 12.0f, ((height + heightMargin) - 8.0f));
        canvas.drawRoundRect(detailButtonRect, 5.0f, 5.0f, radarPaint);
    	canvas.drawText(buttonString, ((graphOffsetX+ graphWidth) * 2.0f + 5.0f), ((height + graphWidth) - 4.0f) - paint.getFontMetrics().top + textHeight, paint);
    	
    	// GAME OVER�\���̎��ɂ́A��ʕ\���̍X�V�����Ȃ��B
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
    	// �Q�[���̏����o�͂���
    	if (provider != null)
    	{
	        // �����F�𔒂ɂ��āA������Ɖe�t���ɂ���
    		Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);

            // �t�H���g�̃T�C�Y��ݒ肷��
        	String message = parent.getString(R.string.game_readyInformation);
            float fontSize = TextDrawingUtility.decideFontSize(message, (int) (screenWidth - 60), -1, paint);
            paint.setTextSize(fontSize);

            // �`��ʒu�����߂Ă݂�
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
            	// �Q�[�����s���̕\��

            	// ���݂̃��x���������r�b�g�}�b�v�̕\��
            	drawCurrentLevel(canvas, provider.getCurrentLevelbitmap());

            	// �c�莞�Ԃ̕\��
            	drawTimeBar(canvas, provider.getRemainPercent());

            	timerCount = 0;
            }    	     
    	}
    }
    
    /**
     *     ��ʕ`�惁�C��
     * 
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	try
    	{    		
    		// �w�i�F������
    		canvas.drawColor(backgroundColor);	    			

    		// �w�i�摜���ݒ肳��Ă����ꍇ�́A�w�i�摜��`�悷��
    		if (backgroundBitmap != null)
    		{
    		    canvas.drawBitmap(backgroundBitmap, 0, 0, new Paint());
    		}

    		// �Q�[���̐i�s��񂪎��Ȃ��ꍇ�́A�����Ő܂�Ԃ�
    		if (provider == null)
    		{
    			return;
    		}

    		// ����\������
    		drawMoleHoles(canvas);

    		// �Q�[���̏����o�͂���
            drawGameInformation(canvas);

            // �Q�[���̎��s��Ԃ��擾����
	        int status = provider.getCurrentGameStatus();
	        switch (status)
	        {
	          case GameInformationProvider.STATUS_GAME_PLAYING:
	        	  // �Q�[�����{��...������`�悷��
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
     *    ����\������
     * 
     * @param canvas
     * @param index
     */
    private void drawCorrectMark(Canvas canvas, int index)
    {
		//  �\������ꏊ�����肷��
		float widthMargin = getPositionWidth(index);
		float heightMargin = getPositionHeight(index);

		// ����\������
	    canvas.drawBitmap(okBitmap, widthMargin, heightMargin - 15, new Paint());     	
    }

    /**
     *    �~��\������
     * 
     * @param canvas
     * @param index
     */
    private void drawWrongtMark(Canvas canvas, int index)
    {
		//  �\������ꏊ�����肷��
		float widthMargin = getPositionWidth(index);
		float heightMargin = getPositionHeight(index);

		// �~��\������
		canvas.drawBitmap(ngBitmap, widthMargin + 10, heightMargin, new Paint());
    }

    /**
     *    �񓚌��ʁi�� �܂��� �~�j��\������
     * 
     * @param canvas
     */
    private void drawGameAnswer(Canvas canvas)
    {
		// ���݂̏�Ԃ�m��
        int isAnswer = provider.getCurrentAnswerStatus();
		if (isAnswer == IGameInformationProvider.ANSWER_NOT_YET)
		{
			// �܂�����...�^�b�v�����ʒu���m�F����
	    	if ((positionIndex < 0)||(positionIndex > MoleGameActivity.NUMBER_OF_MOLE_HOLES))
	    	{
	    		// �^�b�v�ʒu���ُ�Ȃ̂Ŗ�������
	    		return;
	    	}
	    	// �񓚈ʒu���L������
	    	answeredPositionIndex = positionIndex;
	    	positionIndex = -1;
	    	
			long duration = 0;  // �u���u�����鎞�ԊԊu...
			int soundEffectId  = 0;

			//  �\������ꏊ�̖��͐������ǂ����`�F�b�N����
			MoleGameQuestionHolder question = provider.getGameQuestion(answeredPositionIndex);
			if (question.isCorrectQuestion() == true)
			{
		        // ����\������
				drawCorrectMark(canvas, answeredPositionIndex);
			    provider.changeAnswerStatus(IGameInformationProvider.ANSWER_CORRECT, currentTimeMillis, question);
			    duration = VIBRATION_DURATION_OK;
			    soundEffectId  = seOkId;
			}
	        else
			{
				// �~��\������
	        	drawWrongtMark(canvas, answeredPositionIndex);
			    provider.changeAnswerStatus(IGameInformationProvider.ANSWER_WRONG, currentTimeMillis, question);
	    		duration = VIBRATION_DURATION_NG;
			    soundEffectId  = seNgId;
			}
			// �u���u��������
			vibrator.vibrate(duration);

            if (soundEffect != null)
            {
                // ���ʉ���炷�B
                soundEffect.play(soundEffectId, 1, 1, 3, 0, 1);
            }
		}
		else if (isAnswer == IGameInformationProvider.ANSWER_CORRECT)
		{
			// �^�b�v�����ʒu���m�F����
	    	if ((answeredPositionIndex < 0)||(answeredPositionIndex > MoleGameActivity.NUMBER_OF_MOLE_HOLES))
	    	{
	    		// �^�b�v�ʒu���ُ�Ȃ̂Ŗ�������
	    		return;
	    	}

	    	// ����\������
			drawCorrectMark(canvas, answeredPositionIndex);
		}
		else if  (isAnswer == IGameInformationProvider.ANSWER_WRONG)
		{
			// �^�b�v�����ʒu���m�F����
	    	if ((answeredPositionIndex < 0)||(answeredPositionIndex > MoleGameActivity.NUMBER_OF_MOLE_HOLES))
	    	{
	    		// �^�b�v�ʒu���ُ�Ȃ̂Ŗ�������
	    		return;
	    	}

	    	// �~��\������
        	drawWrongtMark(canvas, answeredPositionIndex);			
		}
    }

    /**
     *     �^�b�`�������W�iX���j���擾����
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
     *     �^�b�`�������W(Y��)���擾����
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
     *    �^�C�}�[�̎��Ԃ�\������
     * 
     * @param canvas
     */
    private void drawTimeBar(Canvas canvas, float remainPercent)
    {
    	Paint paint = new Paint();
    	
    	// �o�[��\������F�����߂�
    	if (remainPercent < 0.12f)
    	{
    		// �c�莞�Ԃ�12%��؂�����ԐF�ɂ���
    		paint.setColor(Color.RED);
    	} else if (remainPercent < 0.45f)
    	{
    		// �c�莞�Ԃ�45%��؂����物�F�ɂ���
    		paint.setColor(Color.YELLOW);
    	}
    	else
    	{
            paint.setColor(Color.BLUE);    		
    	}
        float width = screenWidth / MoleGameActivity.NUMBER_OF_MOLE_COLUMNS * 2.0f;
        float height = screenHeight / MoleGameActivity.NUMBER_OF_MOLE_ROWS / 6.0f;

        // �l�p�`��\������
        canvas.drawRect(new RectF(1.0f, (screenHeight - height), (width * remainPercent), (screenHeight - 1.0f)), paint);
        
    }

    /**
     *    ��蕶(�Ɩ��̕\���ʒu)��\������
     * 
     * @param canvas
     */
    private void drawMoleHoles(Canvas canvas)
    {
        float width = screenWidth / MoleGameActivity.NUMBER_OF_MOLE_COLUMNS;
        float height = screenHeight / MoleGameActivity.NUMBER_OF_MOLE_ROWS;
        float widthMargin = (width - peachBitmap.getWidth()) / 2.0f;  // ���S��
        float heightMargin = (height - peachBitmap.getHeight());        // ����
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
                    //  ���͕\������Ă��Ȃ�
                	canvas.drawBitmap(peachBitmap, widthMargin + x * width, heightMargin + y * height, new Paint());
                }
                else
                {
                	// ���i�̊Ŕj��\������
                	Paint paint = new Paint();
        	        paint.setColor(Color.WHITE);
        	    	paint.setStyle(Paint.Style.STROKE);
        	    	paint.setStrokeJoin(Paint.Join.ROUND);

        	    	// ��蕶��\������w�i�g��\������
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
                    
                	// �����̑傫�������߂� (�ő�t�H���g�T�C�Y��20dip)
                    int maxFontSize =  (int) (20.0f * parent.getResources().getDisplayMetrics().density + 0.5f);
                    //paint.setTextSize(TextDrawingUtility.decideFontSize(question.getQuestion(), (int) (width - widthTextMargin - widthTextMargin),(int) (heightMargin  - height / 10 - marginY), 0, paint));
                    //paint.setTextSize(TextDrawingUtility.decideFontSize(question.getQuestion(), (int) ((width - widthTextMargin - widthTextMargin) * 2 - 4),(int) ((heightMargin  - height / 10 - marginY) / 2), maxFontSize, paint));
                    paint.setTextSize(TextDrawingUtility.decideFontSize(question.getQuestion(), (int) (width - (widthTextMargin * 5)),(int) (heightMargin  - (height / 10) - (marginY * 3)), maxFontSize, paint));

                    // ��蕶��Y���̈ʒu���ړ�������
                    FontMetrics fontMetrics = paint.getFontMetrics();

                    // ��蕶��\������
                    //canvas.drawText(question.getQuestion(), (x * width) + widthTextMargin, (y * height) + (fontMetrics.bottom - fontMetrics.top + 2.0f), paint);
                    TextDrawingUtility.drawTextRegion(canvas, question.getQuestion(), posX + widthTextMargin, posY - fontMetrics.bottom, (posX + width - widthTextMargin), paint);

                    // ����\������
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
        	// �^�b�`���ꂽ�Ƃ�
        	isDraw = onTouchDown(event);
        }
        else if (action == MotionEvent.ACTION_MOVE)
        {
        	// �^�b�`���ꂽ�܂ܓ������ꂽ�Ƃ��̏���
        	isDraw = onTouchMove(event);
        }
        else if (action == MotionEvent.ACTION_UP)
        {
        	// �^�b�`�������ꂽ�Ƃ��̏���...
            isDraw = onTouchUp(event);
        }
    	return (isDraw);
    }

    /**
     *    �^�b�`�����ꏊ���C���f�b�N�X������
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
     *   �^�b�`���ꂽ�^�C�~���O�ł̏���
     * @param event
     * @return
     */
    private boolean onTouchDown(MotionEvent event)
    {
    	// �^�b�`�ʒu���L������
    	downPosX = event.getX();
    	downPosY = event.getY();

    	// �^�b�`�����ꏊ�ƃ^�b�`���Ԃ��擾����
    	currentTimeMillis = System.currentTimeMillis();
    	positionIndex = onTouchedPosition(downPosX, downPosY);
    	
    	// �Q�[���I�[�o�[�̂Ƃ�
    	if (provider.getCurrentGameStatus() == IGameInformationProvider.STATUS_GAME_OVER)
    	{
    		// �Q�[���I�[�o�[��Ԃ̃`�F�b�N���������͂������ǂ����`�F�b�N����
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
    		// �{�^���G���A���Ȃ����� ... �I������
    		return;
    	}
    	if (detailButtonRect.contains(downPosX, downPosY) == true)
    	{
    		// �����Ƃ��킵���{�^���������ꂽ�Ƃ��I

    		// �u���u��������
			vibrator.vibrate(VIBRATION_DURATION_NEXT);
    		
			// Activity��ʂ̂��̂ɑJ�ڂ�����...
			activityOpener.requestToStartActivity(0);
    		
    	}
    }

    /**
     *   �^�b�`���ړ��������̏���
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
     *   �^�b�`�������ꂽ�^�C�~���O�ł̏���
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
