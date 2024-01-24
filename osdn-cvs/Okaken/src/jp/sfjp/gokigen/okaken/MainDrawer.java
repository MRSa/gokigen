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
 *   MainDrawer �N�����ɕ\������Activity�ɂ��� SurfaceView �̕`��N���X
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

	private static final int INSTRUCTION_NEXT_PAGE = 8;	  // 8 * 500 ms�Ő����̎��y�[�W����
	
	private float screenWidth = 0.0f;  // �\���̈�̕�
	private float screenHeight = 0.0f; // �\���̈�̍���
	
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
    	
    	// ����ǂݍ���
    	peachBitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.peach);

    	// �������낳���ǂݍ���
    	Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.momo_l3);
    	momotaroBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.momo_l6);
    	momotaroBitmap_sad = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.momo_l2);
       	momotaroBitmap_happy = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.momo_l1b);
    	momotaroBitmap_normal = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
    	
    	// ��������̃r�b�g�}�b�v��ǂݍ���
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

    	// �o�C�u���[�^�T�[�r�X���E��
    	vibrator = (Vibrator) parent.getSystemService(Context.VIBRATOR_SERVICE);
    }
	
    public void prepareToStart(int width, int height)
    {
    	
    	
    }

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
			  Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::updateBackgroundBitmap() : w:" + width + " , h:"+ height + " " + uri);

			  // �w�i�摜���擾���Đݒ肷��B
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
	    		// �w�i�F������
	    		canvas.drawColor(backgroundColor);	    			

	    		// �w�i�摜���ݒ肳��Ă����ꍇ�́A�w�i�摜��`�悷��
	    		if (backgroundBitmap != null)
	    		{
	    		    canvas.drawBitmap(backgroundBitmap, 0, 0, new Paint());
	    		}

	    		if (isShowInstruction == false)
	    		{
	    			// �ʏ�̃^�C�g���\��
	    			drawTitle(canvas);

	    			// �Q�[���̏����o�͂���
	                drawGameInformation(canvas);    			
	    		}
	    		else
	    		{
	    			//  �����\���̃��[�h�̂Ƃ�
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
		// �����F�𔒂ɂ��āA������Ɖe�t���ɂ���
	    Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);

	     String timeString = parent.getString(R.string.title_startInformation);

	     // �t�H���g�̃T�C�Y�𒲐�����
	     paint.setTextSize(TextDrawingUtility.decideFontSize(timeString, (int) (screenWidth - 60), -1, paint));
		
	     canvas.drawText(timeString, 10, screenHeight - 130, paint);
	     //canvas.drawText(timeString, 10, 50, paint);
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

		// ����\������
        float top = (screenHeight  - peachBitmap.getHeight()) / 2.0f;
        float left = (screenWidth - peachBitmap.getWidth()) / 2.0f;
		canvas.drawBitmap(peachBitmap, left, top, paint);    
		
        // �^�C�g����\������
		String titleString = parent.getString(R.string.app_name);
		paint.setTextSize(TextDrawingUtility.decideFontSize(titleString, (int) (screenWidth - 20.0f), 0, paint));
		canvas.drawText(titleString, 10, (30 - paint.getFontMetrics().top), paint);		
		
		// ��ʉ����ɕ\������{�^���i�ۂ����́j�̃t�H���g�̃T�C�Y�𒲐�����B
        String buttonString = parent.getString(R.string.title_btnInstruction);
        float fontWidth =  (screenWidth / 4.0f);
        paint.setTextSize(TextDrawingUtility.decideFontSize(buttonString, (int) (fontWidth), 0, paint));        
        FontMetrics metrics = paint.getFontMetrics();        
        float fontHeight = metrics.bottom - metrics.top;
        instructionButton_RectF = new RectF((screenWidth - fontWidth  - 16.0f), (screenHeight - fontHeight - 12.0f), (screenWidth - 6.0f), (screenHeight - 4.0f));
        
		// ��ʉ����Ƀ{�^���i�ۂ����́j��\�����鏈��
    	canvas.drawRoundRect(instructionButton_RectF, 5.0f, 5.0f, framePaint);
    	canvas.drawText(buttonString, (screenWidth - fontWidth - 11.0f) , (screenHeight - fontHeight - 8.0f) - metrics.top, paint);
    }

    /**
     *    ������\������V�[����؂�ւ���
     * 
     * @param canvas
     */
    private void drawInstruction(Canvas canvas)
    {
       // �V�[���ʂɕ\�����e��ς���
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
 	   // �^�C�}�[����莞�Ԃ𒴂����ꍇ�ɂ́A���y�[�W�̕\���Ɏ����I�ɐi��
 	  instruction_timer++;
 	   if (instruction_timer > INSTRUCTION_NEXT_PAGE)
 	   {
 		   updateInstructionScene();
 	   } 	   
    }

    /**
     *    ������\������
     * 
     * @param canvas
     */
    private void drawInstruction(Canvas canvas, Bitmap momotaro, Bitmap instruction, int messageResId)
    {
    	// ���b�Z�[�W�\���p�̃T�C�Y�𒲐�����
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

        // �����p�̕�������擾����
        String message = parent.getString(messageResId);
                
        // �����p�̕������\�����邽�߂̐����o���̘g�������ɏ������Ă݂�i�����p�̕����񂪂������Ƃ������j
        if (message.length() > 0)
        {
            Paint backPaint = new Paint();
            backPaint.setColor(Color.WHITE);
            backPaint.setStyle(Paint.Style.STROKE);
            //canvas.drawRect(8, textTopHeight - 1, screenWidth - 18, screenHeight - momotaroHeight - (heightTextMargin / 2.0f), backPaint);
            canvas.drawRoundRect(new RectF(8, textTopHeight - 1, screenWidth - 18, screenHeight - momotaroHeight - (heightTextMargin / 2.0f)), 8, 8, backPaint);

            // �����p�̕������\������
            TextDrawingUtility.drawTextRegion(canvas, message, 10, textTopHeight, screenWidth - 20, paint);
        }

        // �����p�̃r�b�g�}�b�v�������Ɏw�肳��Ă����ꍇ�A�����p�̃r�b�g�}�b�v��\������
        if (instruction != null)
        {
            canvas.drawBitmap(instruction, 20, screenHeight - momotaroHeight - instructionHeight - heightTextMargin, paint);  
        }

        // �^�C�g���̑傫�����𒲐�����
    	String title = parent.getString(R.string.title_btnInstruction);
    	fontSize = TextDrawingUtility.decideFontSize(title, (int) screenWidth, 0, paint);
    	paint.setTextSize(fontSize);
        fontMetrics = paint.getFontMetrics();
        heightTextMargin = fontMetrics.bottom + fontMetrics.leading - fontMetrics.top + 2.0f;
        float titleSize = (heightTextMargin + (fontMetrics.bottom - fontMetrics.top));
        
        if (((message.length() == 0))||(textTopHeight > titleSize))
        {
            // ��ʏ㕔�Ƀ^�C�g����\������
            canvas.drawText(title, 0, heightTextMargin, paint);
        }
        //Log.v(Gokigen.APP_IDENTIFIER, "topMessage: " + textTopHeight + "  titleBottom:" + titleSize);
        
        // ��ʍ����ɂ������낳��̊��\������
        canvas.drawBitmap(momotaro, 0, screenHeight - momotaroHeight - 2.0f, paint);    
    }
    
    /**
     *    ���������ʂ��o��
     * 
     */
    public void showInstruction(int id)
    {
    	//Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::showInstruction() : " + id); 

    	// �����\�����[�h�ƒʏ�̃^�C�g���\�����[�h��؂�ւ���
    	isShowInstruction = (isShowInstruction == true) ? false : true;
    }
    
    /**
     *    ��ʂ𑀍삵�����̏���...
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	boolean isDraw = false;
        if (isShowInstruction == false)
    	{
    		//  �^�C�g���\�����[�h
    		isDraw = onTouchTitleMode(event);
    	}
    	else
    	{
    		//  ���߂��\�����[�h
    		isDraw = onTouchInstructionMode(event);
    	}

        // �^�b�`�|�W�V�������L������i�`���^�����O�h�~�j
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
     *    �^�C�g���\�����[�h�̎��ɉ�ʂɃ^�b�`���ꂽ�Ƃ�
     * 
     * @param event
     */
    private boolean onTouchTitleMode(MotionEvent event)
    {
        // Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::onTouchEvent() : "); 
	    int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
    		// �Ԃ�Ԃ邳����
    		vibrator.vibrate(VIBRATION_DURATION);
    	
    		if (instructionButton_RectF.contains(event.getX(), event.getY()) == true)
    		{
    			// ���߂��\�����[�h�ɐ؂�ւ���
    			isShowInstruction = true;
    			return (true);
    		}
    	
    		if (activityOpener != null)
        	{
    			// �Q�[���X�^�[�g�I
        		activityOpener.requestToStartActivity(0);
        	}
    		return (true);
        }    	
        return (false);
    }

    /**
     *    ���߂��\�����[�h�̎��ɉ�ʂɃ^�b�`���ꂽ�Ƃ�
     * 
     * @param event
     */
    private boolean onTouchInstructionMode(MotionEvent event)
    {
	    int action = event.getAction();
        //Log.v(Gokigen.APP_IDENTIFIER, "MainDrawer::onTouchEvent() : " + action); 
        if ((action == MotionEvent.ACTION_DOWN)&&(downPosX == Float.MIN_VALUE))
        {
        	// �^�b�`�ꔭ�ڂ̂Ƃ�...
        	
    		// �Ԃ�Ԃ邳����
    		vibrator.vibrate(VIBRATION_DURATION);

    		// ���̕\���֐i�߂�B
    		updateInstructionScene();
    		
    		return (true);
        }
        return (false);
   }

   /**
    * �@�����\�����[�h�̂Ƃ��A���ɕ\������V�[����؂�ւ���
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
	    	 // �������[�h���I������
             nextScene = INSTRUCTION_NOT_YET;
             isShowInstruction = false;
             break;
	   }
	   instruction_timer = 0;
	   instruction_scene = nextScene;
   }
}
