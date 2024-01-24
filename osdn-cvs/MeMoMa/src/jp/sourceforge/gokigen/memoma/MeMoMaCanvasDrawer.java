package jp.sourceforge.gokigen.memoma;

import java.util.Enumeration;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import 	android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 *    �����܂̕`��N���X
 *    
 * @author MRSa
 *
 */
public class MeMoMaCanvasDrawer implements  ICanvasDrawer,  GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener, SeekBar.OnSeekBarChangeListener
{
	public static final float OBJECTLABEL_MARGIN = 8.0f;
	public static final float OBJECTLABEL_MARGIN_WIDTH = 24.0f;

	public static final int BACKGROUND_COLOR_DEFAULT = 0xff004000;
	private int backgroundColor = BACKGROUND_COLOR_DEFAULT;
	
	private MeMoMaObjectHolder.PositionObject selectedPosition = null;
	private float tempPosX = Float.MIN_VALUE;
	private float tempPosY = Float.MIN_VALUE;
	private float downPosX = Float.MIN_VALUE;
	private float downPosY = Float.MIN_VALUE;
	
	// �ȉ��̒l�́AMeMoMaListener�ŏ����l��ݒ肷��
	private int objectStyle = MeMoMaObjectHolder.DRAWSTYLE_RECTANGLE;
	
	private LineStyleHolder lineStyleHolder = null;

	private float drawScale = 1.0f;    // �\���̔{��
	private float drawTransX  = 0.0f;   // ���s�ړ����� (X)
	private float drawTransY  = 0.0f;   // ���s�ړ����� (Y)
	private boolean onScaling = false;  // �s���`�C���E�s���`�A�E�g���삵�Ă��邩�ǂ���������
	private int currentScaleBar= 50;  // ���݂̃s���`�C���E�s���`�A�E�g�{��
	
	private boolean onGestureProcessed = false;   // ���������̏������s�Ȃ��Ă��邩�ǂ����������B
	
	private float screenWidth = 0.0f;  // �\���̈�̕�
	private float screenHeight = 0.0f; // �\���̈�̍���

	private int displayObjectInformation = 1;  // �I�u�W�F�N�g���x���̕\��
	
	private String backgroundBitmapUri = null;
	private Bitmap backgroundBitmap = null;

	private MeMoMaObjectHolder objectHolder = null;
	private MeMoMaConnectLineHolder lineHolder = null;
	private IObjectSelectionReceiver selectionReceiver = null;

	private GestureDetector gestureDetector = null;
	private ScaleGestureDetector scaleGestureDetector = null;

	private Activity parent = null;
	
	/**
      *   �R���X�g���N�^
      *   
      */
	  public MeMoMaCanvasDrawer(Activity argument, MeMoMaObjectHolder object, LineStyleHolder styleHolder, IObjectSelectionReceiver receiver)
	  {
		  objectHolder = object;
		  lineHolder = objectHolder.getConnectLineHolder();
		  selectionReceiver = receiver;
		  lineStyleHolder = styleHolder;
		  parent = argument;

		  // �W�F�X�`�������o����N���X�𐶐�����
		  gestureDetector = new GestureDetector(argument, this);
		  scaleGestureDetector = new ScaleGestureDetector(argument, this);

		  // �Y�[���{����W�J����
		  restoreTranslateAndZoomScale();
	  }

	  /**
	   *   �I�u�W�F�N�g�̌`���ύX����
	   *   (�����Ŏw�肳�ꂽ�`��̃`�F�b�N���s���Ă����B)
	   * 
	   * @param style
	   */
	  public void setObjectStyle(int style)
	  {
		  switch (style)
		  {
		    case MeMoMaObjectHolder.DRAWSTYLE_OVAL:
		    case MeMoMaObjectHolder.DRAWSTYLE_ROUNDRECT:
		    case MeMoMaObjectHolder.DRAWSTYLE_RECTANGLE:
		    case MeMoMaObjectHolder.DRAWSTYLE_DIAMOND:
		    case MeMoMaObjectHolder.DRAWSTYLE_KEYBOARD:
		    case MeMoMaObjectHolder.DRAWSTYLE_PAPER:
		    case MeMoMaObjectHolder.DRAWSTYLE_DRUM:
		    case MeMoMaObjectHolder.DRAWSTYLE_PARALLELOGRAM:
		    case MeMoMaObjectHolder.DRAWSTYLE_HEXAGONAL:
		    case MeMoMaObjectHolder.DRAWSTYLE_CIRCLE:
		    case MeMoMaObjectHolder.DRAWSTYLE_NO_REGION:
		    case MeMoMaObjectHolder.DRAWSTYLE_LOOP_START:
		    case MeMoMaObjectHolder.DRAWSTYLE_LOOP_END:
		    case MeMoMaObjectHolder.DRAWSTYLE_LEFT_ARROW:
		    case MeMoMaObjectHolder.DRAWSTYLE_DOWN_ARROW:
		    case MeMoMaObjectHolder.DRAWSTYLE_UP_ARROW:
		    case MeMoMaObjectHolder.DRAWSTYLE_RIGHT_ARROW:
		    	objectStyle = style;
			    break;

		    default:
		    	objectStyle = MeMoMaObjectHolder.DRAWSTYLE_RECTANGLE;
			    break;
		  }	
	  }

	  /**
	   * 
	   * @param uri
	   */
	  public void updateBackgroundBitmap(String uri, int width, int height)
	  {
		  // �w�i�摜�̕�������L������
		  backgroundBitmapUri = uri;

		  // �Ƃ肠�����A�w�i�摜���N���A���ăK�x�R������B
		  backgroundBitmap = null;
		  System.gc();
		  if (uri.isEmpty() == true)
		  {
			  // �w�i�摜�̎w�肪�Ȃ������̂ŁA�����Ń��^�[������B
			  return;
		  }
		  try
		  {
			  // �Ƃ肠�����ݒ肷��������O�Ɏc���Ă݂�
			  Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::updateBackgroundBitmap() : w:" + width + " , h:"+ height + " " + uri);

			  // �w�i�摜���擾���Đݒ肷��B
			  backgroundBitmap = ImageLoader.getBitmapFromUri(parent, ImageLoader.parseUri(uri), width, height);
		  }
		  catch (Exception ex)
		  {
			  Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::updateBackgroundBitmap() : " + uri + " , "+ ex.toString());
			  ex.printStackTrace();
			  backgroundBitmap = null;
			  backgroundBitmapUri = "";
			  System.gc();
		  }	
		  return;
	  }	  
	  
	  /**
	   *   �w�i�摜��ݒ肷��
	   *   
	   * @param uri
	   */
	  public void setBackgroundUri(String uri)
	  {
		  backgroundBitmapUri = uri;
	  }
	  
	  /**
	   *   �w�i�F��(�������)�ݒ肷��
	   * 
	   * @param colorString
	   */
	  public void setBackgroundColor(String colorString)
	  {
		  try
		  {
			  backgroundColor = Color.parseColor(colorString);
			  return;
		  }
		  catch (Exception ex)
		  {
			  //
              //Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.toString() + " " + ex.getMessage());
		  }
		  backgroundColor = BACKGROUND_COLOR_DEFAULT;
	  }

	  /**
	   *    ���������W�b�N��ݒ肷��
	   * 
	   */
	  public void prepareToStart(int width, int height)
	  {
          Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::prepareToStart() " + "x:" + width + " , " + "y:" + height);

          // �w�i�摜���X�V����
		  //updateBackgroundBitmap(backgroundBitmapUri, width, height);

		  // Preference��ǂݏo��
		  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
		  displayObjectInformation = Integer.parseInt(preferences.getString("objectPrintData", "1"));
	  }	

	  /**
	   *    ��ʂ̑傫�����ς���Ă��܂����ꍇ...
	   * 
	   */
	  public  void changedScreenProperty(int format, int width, int height)
	  {
		  // �w�i�摜���X�V����
		  updateBackgroundBitmap(backgroundBitmapUri, width, height);
		  
		  // �\����ʃT�C�Y���o����
		  screenWidth = width;
		  screenHeight = height;

          Log.v(Main.APP_IDENTIFIER, "changedScreenProperty() " + "x:" + width + " , " + "y:" + height);
	  }


	  /**
	   *    �L�����o�X�ɃI�u�W�F�N�g�i�Ɛڑ����j��\������
	   * 
	   */
	  public void drawOnCanvas(Canvas canvas)
	  {
	    	//Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::drawOnCanvas()");
	    	try
	    	{
	    		// ��ʑS�̂��N���A����
	    		//canvas.drawColor(Color.argb(backgroundColorAlfa, backgroundColorRed, backgroundColorGreen, backgroundColorBlue), Mode.CLEAR);
	    		canvas.drawColor(backgroundColor);	    			
	    		
	    		// �w�i�摜���ݒ肳��Ă����ꍇ�́A�w�i�摜��`�悷��
	    		if (backgroundBitmap != null)
	    		{
	    		    canvas.drawBitmap(backgroundBitmap, 0, 0, new Paint());
	    		}

	    		// �\���ʒu���ړ�������
	    		canvas.translate(drawTransX, drawTransY);

	    		// ��ʂ̕\���̈���g��E�k������
	    		canvas.scale(drawScale, drawScale);

	    		// �I�u�W�F�N�g�Ԃ̐ڑ��������ׂĕ\������
	    		drawConnectionLines(canvas, 0.0f, 0.0f);
	    		
                // �I�u�W�F�N�g�����ׂĕ\��
	    		drawObjects(canvas, 0.0f, 0.0f);

                /**  �ړ������ǂ����̃`�F�b�N���s���B **/
	    		if (isFlicking(canvas) == true)
	    		{
                    // �ړ����̏ꍇ�A�t���b�N���̋O�Ղƌ��݈ʒu��\������
		            drawTrackAndPositions(canvas);
	    		}
	    	}
	    	catch (Exception ex)
	    	{
	    		// ��O����...�ł����̂Ƃ��ɂ͉������Ȃ�
	    		Log.v(Main.APP_IDENTIFIER, "drawOnCanvas() ex: " + ex.getMessage());
	    	}
	  }

	  /**
	   *    �I�u�W�F�N�g��BitmapCanvas��ɕ`��
	   * 
	   */
	  public void drawOnBitmapCanvas(Canvas canvas, float offsetX, float offsetY)
	  {
	    	try
	    	{
	    		Paint paint = new Paint();

	    		// ��ʑS�̂��N���A����
	    		canvas.drawColor(backgroundColor);	    			
	    		
	    		// �w�i�摜���ݒ肳��Ă����ꍇ�́A�w�i�摜��`�悷��
	    		if (backgroundBitmap != null)
	    		{
	    		    canvas.drawBitmap(backgroundBitmap, offsetX, offsetY, paint);
	    		}

	    		// �I�u�W�F�N�g�Ԃ̐ڑ��������ׂĕ\������
	    		drawConnectionLines(canvas, offsetX, offsetY);
	    		
                // �I�u�W�F�N�g�����ׂĕ\��
	    		drawObjects(canvas, offsetX, offsetY);
	    		
	    		// �^�C�g���Ƃ߂��܂̃A�C�R����\������ : �����̐F�͍��ł����̂���...
	    		Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), R.drawable.icon1);
	    		canvas.drawBitmap(bitmap, 2.0f, 2.0f, paint);
	    		canvas.drawText(objectHolder.getDataTitle(), (bitmap.getWidth() + 10.0f), 32.0f, paint);

	    	}
	    	catch (Exception ex)
	    	{
	    		// ��O����...�ł����̂Ƃ��ɂ͉������Ȃ�
	    		Log.v(Main.APP_IDENTIFIER, "drawOnBitmapCanvas() ex: " + ex.toString() + " " + ex.getMessage());
	    	}
	  }

	  /**
	   *    �I�u�W�F�N�g�Ԃ̐ڑ�����\������
	   * 
	   * @param canvas
	   */
	  private void drawConnectionLines(Canvas canvas, float offsetX, float offsetY)
	  {
	        // �I�u�W�F�N�g�̐F�Ƙg����ݒ肷�� �i�A�����p�j
	    	Paint paint = new Paint();
	        paint.setColor(Color.WHITE);
	    	paint.setStyle(Paint.Style.STROKE);

	        // �I�u�W�F�N�g�̐F�Ƙg����ݒ肷��  �i�_���p�j
	    	Paint dashLinePaint = new Paint();
	    	dashLinePaint.setColor(Color.WHITE);
	    	dashLinePaint.setStyle(Paint.Style.STROKE);
	    	dashLinePaint.setPathEffect(new DashPathEffect(new float[]{ 5.0f, 5.0f }, 0));	    	
	    	
	    	// �I�u�W�F�N�g�̕`�� �i�ێ����Ă�����̂͂��ׂĕ\������j
	    	Enumeration<Integer> keys = lineHolder.getLineKeys();
	        while (keys.hasMoreElements())
	        {
	            Integer key = keys.nextElement();
	            MeMoMaConnectLineHolder.ObjectConnector line = lineHolder.getLine(key);
	            if (line.key > 0)
	            {
                    // ���ۂɃ��C��������
	            	drawLine(canvas, paint, dashLinePaint, line, offsetX, offsetY);
	            }
	            else
	            {
	            	// �����͌Ă΂�Ȃ��͂��B�B�B�������͂��̂��̂��c���Ă���
	            	Log.v(Main.APP_IDENTIFIER, "DETECTED DELETED LINE");
	            }
	        }
	  }

	  /** 
	   *    �ڑ������������
       *
	   * @param canvas
	   * @param paint
	   * @param line
	   */
	  public void drawLine(Canvas canvas, Paint paint, Paint dashPaint, MeMoMaConnectLineHolder.ObjectConnector line, float offsetX, float offsetY)
	  {
		  try
		  {
			  if ((objectHolder == null)||(canvas == null))
			  {
				  // �Ȃɂ����Ȃ�
				  return;
			  }

			  MeMoMaObjectHolder.PositionObject from = objectHolder.getPosition(line.fromObjectKey);
			  MeMoMaObjectHolder.PositionObject to = objectHolder.getPosition(line.toObjectKey);
			  if ((from == null)||(to == null))
			  {
				  // �Ȃɂ����Ȃ�
				  return;
			  }

			  // ���C���̑�����ݒ肷��B
			  paint.setStrokeWidth((float) line.lineThickness);

			  // ���C���̑�����ݒ肷��B
			  dashPaint.setStrokeWidth((float) line.lineThickness);

			  // ���C���̃X�^�C��(�A���� or �_��)��ݒ肷��
			  Paint linePaint = (line.lineShape == LineStyleHolder.LINESHAPE_DASH) ? dashPaint : paint;
			  
			  // �����l�Ƃ��āA�e�I�u�W�F�N�g�̒��S���W��ݒ肷��
			  float startX = from.rect.centerX() + offsetX;
			  float endX = to.rect.centerX() + offsetX;
			  float startY = from.rect.centerY() + offsetY;
			  float endY = to.rect.centerY() + offsetY;
			  
			  // Y���W�̐��̈ʒu��␳����
			  if (from.rect.bottom < to.rect.top)
			  {
                  startY = from.rect.bottom + offsetY;
                  endY = to.rect.top + offsetY;
			  }
			  else if (from.rect.top > to.rect.bottom)
			  {
                  startY = from.rect.top + offsetY;
                  endY = to.rect.bottom + offsetY;
			  }

			  // X���W�̐��̈ʒu��␳���� (Y���W���␳����Ă��Ȃ��Ƃ�)
              if ((startY != (from.rect.top + offsetY))&&(startY != (from.rect.bottom + offsetY)))
			  {
    			  if (from.rect.right < to.rect.left)
    			  {
                      startX = from.rect.right + offsetX;
                      endX = to.rect.left + offsetX;
    			  }
    			  else if (from.rect.left > to.rect.right)
    			  {
                      startX = from.rect.left + offsetX;
                      endX = to.rect.right + offsetX; 
    			  }
			  }

			  if ((line.lineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_NO_ARROW)||
					  (line.lineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_L_ARROW)||
					  (line.lineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_R_ARROW))
			  {
				  // �c���[�`���̂悤�ɐڑ����� ... 
				  if (startX == (from.rect.centerX() + offsetX))
				  {
					  float middleY = (startY + endY) / 2;
				      canvas.drawLine(startX, startY, startX, middleY, linePaint);
				      canvas.drawLine(startX, middleY, endX, middleY, linePaint);
				      canvas.drawLine(endX, middleY, endX, endY, linePaint);
				      
				      /**  �₶�邵�����鏈�� **/
				      if (line.lineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_L_ARROW)
				      {
				    	  // �n�_�ɖ�������
				    	  ObjectShapeDrawer.drawArrowTree(canvas, paint, startX,startY, middleY, false);
				      }
				      else if (line.lineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_R_ARROW)
				      {
				    	  // �I�_�ɖ�������
				    	  ObjectShapeDrawer.drawArrowTree(canvas, paint, endX, endY, middleY, false);
				      }
				  }
				  else
				  {
					  float middleX = (startX + endX) / 2;
				      canvas.drawLine(startX, startY, middleX, startY, linePaint);
				      canvas.drawLine(middleX, startY, middleX, endY, linePaint);
				      canvas.drawLine(middleX, endY, endX, endY, linePaint);

				      /**  �₶�邵(�O�p�`)�����鏈�� **/
				      if (line.lineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_L_ARROW)
				      {
				    	  // �n�_�ɖ�������
				    	  ObjectShapeDrawer.drawArrowTree(canvas, paint, startX, startY, middleX, true);
				      }
				      else if (line.lineStyle == LineStyleHolder.LINESTYLE_TREESTYLE_R_ARROW)
				      {
				    	  // �I�_�ɖ�������
				    	  ObjectShapeDrawer.drawArrowTree(canvas, paint, endX,endY, middleX, true);
				      }
				  }
			  }
			  else if ((line.lineStyle == LineStyleHolder.LINESTYLE_CURVESTYLE_NO_ARROW)||
					  (line.lineStyle == LineStyleHolder.LINESTYLE_CURVESTYLE_L_ARROW)||
					  (line.lineStyle == LineStyleHolder.LINESTYLE_CURVESTYLE_R_ARROW))
			  {
                  // �Ȑ��Őڑ�����
				  float middleX = (startX + endX) / 2;
				  float middleY = (startY + endY) / 2;
				  float x1 = (startX + middleX) / 2;
				  float y1 = middleY;
				  float x2 = (middleX + endX) / 2;
				  float y2 = middleY;
				  
			      Path pathLine = new Path();
			      pathLine.moveTo(startX, startY);
			      pathLine.cubicTo(x1, y1, x2, y2, endX, endY);
			      canvas.drawPath(pathLine, linePaint);

			      /**  �₶�邵�����鏈�� **/
			      if (line.lineStyle == LineStyleHolder.LINESTYLE_CURVESTYLE_L_ARROW)
			      {
			    	  // �n�_�ɖ�������
			    	  ObjectShapeDrawer.drawArrow(canvas, paint, startX, startY, endX, endY);
			      }
			      else if (line.lineStyle == LineStyleHolder.LINESTYLE_CURVESTYLE_R_ARROW)
			      {
			    	  // �I�_�ɖ�������
			    	  ObjectShapeDrawer.drawArrow(canvas, paint, endX, endY, startX, startY);
			      }
			  }
              else  // if (line.lineStyle == MeMoMaConnectLineHolder.LINESTYLE_STRAIGHT)
			  {
			      // �����Őڑ�����
			      canvas.drawLine(startX, startY, endX, endY, linePaint);
			      
			      /**  �₶�邵�����鏈�� **/
			      if (line.lineStyle == LineStyleHolder.LINESTYLE_STRAIGHT_L_ARROW)
			      {
			    	  // �n�_�ɖ�������
			    	  ObjectShapeDrawer.drawArrow(canvas, paint, startX, startY, endX, endY);
			      }
			      else if (line.lineStyle == LineStyleHolder.LINESTYLE_STRAIGHT_R_ARROW)
			      {
			    	  // �I�_�ɖ�������
			    	  ObjectShapeDrawer.drawArrow(canvas, paint, endX, endY, startX, startY);
			      }
			  }
		  }
		  catch (Exception ex)
		  {
			  // �Ȃɂ����Ȃ�
			  Log.v(Main.APP_IDENTIFIER, "EXCEPTION :" + ex.toString());
		  }
	  }	  
	  
    /**
     *   �I�u�W�F�N�g�𓮂����Ă���Œ����ǂ����̔�����s���B
     * 
     * @param canvas
     * @return  true�Ȃ�A�������Ă���Œ�
     */
    private boolean isFlicking(Canvas canvas)
    {
        //int width = canvas.getWidth();
        //int height = canvas.getHeight();
        if ((tempPosX == Float.MIN_VALUE)||(tempPosY == Float.MIN_VALUE))
        {
            return (false);
        }
        return (true);
    }

    /**
     *   �t���b�N���̋O�Ղƌ��ݒn�_��\������
     * 
     * @param canvas
     */
    private void drawTrackAndPositions(Canvas canvas)
    {
        // �t���b�N���̋O�Ղ�\������
    	float x = (tempPosX - drawTransX) / drawScale;
    	float y = (tempPosY - drawTransY) / drawScale;

    	Paint paint = new Paint();
    	paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        if (selectedPosition != null)
        {
        	float objX = (selectedPosition.rect.right - selectedPosition.rect.left) / 2;
        	float objY = (selectedPosition.rect.bottom - selectedPosition.rect.top) / 2;
    	    canvas.drawLine(selectedPosition.rect.centerX(), selectedPosition.rect.centerY(), x, y, paint);
            canvas.drawRect((x - objX), (y - objY), (x + objX), (y + objY), paint);

            // ���ݒn�_�̕\��
            drawObject(canvas, selectedPosition, true, 0.0f, 0.0f);
        }
        else   // �I�u�W�F�N�g��I�����̕\��
        {
    		int data = selectionReceiver.touchedVacantArea();
    		if (data ==OperationModeHolder.OPERATIONMODE_MOVE)
    		{
                // �ړ����[�h�̂Ƃ�... �i�\���̈���ړ�������j
    			drawTransX = (tempPosX - downPosX);
    			drawTransY = (tempPosY - downPosY);

    			//  �\���̈�̈ړ����L������
    	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
    	        SharedPreferences.Editor editor = preferences.edit();
    	        editor.putFloat("drawTransX", drawTransX);
    	        editor.putFloat("drawTransY", drawTransY);
    	        editor.commit();    		
    		}
    		else
    		{
    			// �ړ����[�h�ȊO
            	paint.setColor(Color.YELLOW);
        	    canvas.drawLine(((downPosX) / drawScale), ((downPosY) / drawScale), x,  y, paint);
    		}
        }
    }

    /**
     *    �I�u�W�F�N�g�i�P�j��\������
     * 
     * @param canvas
     * @param paint
     * @param pos
     */
    private void drawObject(Canvas canvas, MeMoMaObjectHolder.PositionObject object, boolean isMoving, float offsetX, float offsetY)
    {
    	float label_offsetX = OBJECTLABEL_MARGIN;
    	float label_offsetY = 0.0f;

        // �I�u�W�F�N�g�̐F�Ƙg����ݒ肷��
    	Paint paint = new Paint();
    	if (isMoving == true)
    	{
            paint.setColor(Color.YELLOW);
        	paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(object.strokeWidth);
    	}
    	else
    	{
            paint.setColor(object.objectColor);
            paint.setStyle(Paint.Style.valueOf(object.paintStyle));
            paint.setStrokeWidth(object.strokeWidth);
    	}
 
       // �}�`�̌`��ɍ��킹�ĕ`�悷��
    	RectF objectShape = new RectF(object.rect);
    	objectShape.left = objectShape.left + offsetX;
    	objectShape.right = objectShape.right + offsetX;
    	objectShape.top = objectShape.top + offsetY;
    	objectShape.bottom = objectShape.bottom + offsetY;
    	
    	if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_OVAL)
		{
			// �ȉ~�`�̕`��
    		label_offsetY = ObjectShapeDrawer.drawObjectOval(canvas, objectShape, paint);
		}
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_ROUNDRECT)
		{
			// �ۊp�l�p�`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectRoundRect(canvas, objectShape, paint);
		}
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_DIAMOND)
		{
			// �H�`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectDiamond(canvas, objectShape, paint);
            label_offsetX = OBJECTLABEL_MARGIN;
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_KEYBOARD)
		{
			// ��`(�L�[�{�[�h�^)�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectKeyboard(canvas, objectShape, paint);
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_PARALLELOGRAM)
		{
			// ���s�l�ӌ`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectParallelogram(canvas, objectShape, paint);
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_HEXAGONAL)
		{
			// �Z�p�`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectHexagonal(canvas, objectShape, paint);
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_PAPER)
		{
			// ���ނ̌`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectPaper(canvas, objectShape, paint);
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_DRUM)
		{
			// �~���̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectDrum(canvas, objectShape, paint, Paint.Style.valueOf(object.paintStyle));
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_CIRCLE)
		{
			// �~��`�悷��
	   		label_offsetY = ObjectShapeDrawer.drawObjectCircle(canvas, objectShape, paint);
		}
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_NO_REGION)
		{
			  // �g�Ȃ���`��i�H�j���� ... �Ȃɂ����Ȃ�
			  if (object.label.length() == 0)
			  {
				  // �����\�����Ȃ��Ƃ킩��Ȃ��̂ŁA���x���������Ƃ��ɂ͘g��\������
				  ObjectShapeDrawer.drawObjectNoRegion(canvas, objectShape, paint);
			  }
		}
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_LOOP_START)
		{
			// ���[�v�J�n�}�`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectLoopStart(canvas, objectShape, paint);
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_LOOP_END)
		{
			// ���[�v�I���}�`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectLoopEnd(canvas, objectShape, paint);
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_LEFT_ARROW)
		{
			// �������}�`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectLeftArrow(canvas, objectShape, paint);
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_DOWN_ARROW)
		{
			// �������}�`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectDownArrow(canvas, objectShape, paint);
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_UP_ARROW)
		{
			// �㑤���}�`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectUpArrow(canvas, objectShape, paint);
        }
		else if (object.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_RIGHT_ARROW)
		{
			// �E�����}�`�̕`��
	   		label_offsetY = ObjectShapeDrawer.drawObjectRightArrow(canvas, objectShape, paint);
        }
		else // if (pos.drawStyle == MeMoMaObjectHolder.DRAWSTYLE_RECTANGLE)
        {
            // �l�p�`��`�悷��
            label_offsetY = ObjectShapeDrawer.drawObjectRect(canvas, objectShape, paint);
        }

        // �����T�C�Y��ݒ肷��B
        paint.setTextSize(object.fontSize);

        // �������x����\������
       	ObjectShapeDrawer.drawTextLabel(canvas, paint, object, objectShape, displayObjectInformation, label_offsetX, label_offsetY);
    }

    /**
     *   �I�u�W�F�N�g�����ׂĕ\������
     *
     * @param canvas
     */
    private void drawObjects(Canvas canvas , float offsetX, float offsetY)
    {
    	// �I�u�W�F�N�g�̕`�� �i�ێ����Ă�����̂͂��ׂĕ\������j
    	Enumeration<Integer> keys = objectHolder.getObjectKeys();
        while (keys.hasMoreElements())
        {
            Integer key = keys.nextElement();
            MeMoMaObjectHolder.PositionObject pos = objectHolder.getPosition(key);
            drawObject(canvas, pos, false, offsetX, offsetY);
        }
    }

    /**
     *   �^�b�`���ꂽ�^�C�~���O�ł̏���
     * @param event
     * @return
     */
    private boolean onTouchDown(MotionEvent event)
    {
    	// �^�b�`�ʒu���L������
    	downPosX = event.getX() - drawTransX;
    	downPosY = event.getY() - drawTransY;

    	// �^�b�`�ʒu���I�u�W�F�N�g�摜�̍��W�ɕϊ�����
    	float x = downPosX / drawScale;
    	float y = downPosY / drawScale;

    	// �^�b�`�ʒu�ɃI�u�W�F�N�g�����݂��邩�m�F����
    	selectedPosition = checkSelectedObject(x, y);
    	if (selectedPosition == null)
    	{
    		// �ŏ��Ƀ^�b�v�����Ƃ��̈ʒu�� selectedPosition�ɐݒ肷��
    		int data = selectionReceiver.touchedVacantArea();
    		if (data == OperationModeHolder.OPERATIONMODE_CREATE)
    		{
    			// �I�u�W�F�N�g�쐬���[�h�̂Ƃ�...�I�u�W�F�N�g�𐶐�����
        		selectedPosition = objectHolder.createPosition(x, y, objectStyle);
        		
        		// �I�u�W�F�N�g���������ꂽ���Ƃ�ʒm����
        		selectionReceiver.objectCreated();        		
    		}
    		else if (data ==OperationModeHolder.OPERATIONMODE_MOVE)
    		{
    			// �ړ����[�h�̂Ƃ�
    		}
	        else // if (data ==ChangeDrawMode.OPERATIONMODE_DELETE)
	        {
	        	// �폜���[�h�̂Ƃ�...�������Ȃ�
	        }
    	}
        return (false);
    }

    /**
     *   �^�b�`�������ꂽ�^�C�~���O�ł̏���
     * @param event
     * @return
     */
    private boolean onTouchUp(MotionEvent event)
    {
    	boolean longPress = false;
        if (onGestureProcessed == true)
        {
        	// �����O�^�b�`���������ꍇ...�t���O�𗎂Ƃ�
        	onGestureProcessed = false;
        	longPress = true;
        }

        // �^�b�`�ʒu���I�u�W�F�N�g�摜�̍��W�ɕϊ�����
    	float x = (event.getX() - drawTransX) / drawScale;
    	float y = (event.getY() - drawTransY) / drawScale;

    	if (selectedPosition == null)
        {
        	int data = selectionReceiver.touchUppedVacantArea();
        	if (data == OperationModeHolder.OPERATIONMODE_DELETE)
        	{
                if ((tempPosX == Float.MIN_VALUE)||(tempPosY == Float.MIN_VALUE)||(downPosX == Float.MIN_VALUE)||(downPosY == Float.MIN_VALUE))
                {
                	// �^�b�`���Q�����Ă��Ȃ��̂ŁA�������Ȃ��B
            		Log.v(Main.APP_IDENTIFIER, "onTouchUp : (" + downPosX + "," + downPosY + ") [" + drawScale + "] (" + tempPosX + "," + tempPosY + ") [" + drawTransX + "," + drawTransY + "]");
                    return (false);	
                }

        		// �^�b�`�������ꂽ�ʒu�ɃI�u�W�F�N�g�����炸�A�I�u�W�F�N�g����I���������ꍇ...�I�u�W�F�N�g���q�����Ă��郉�C����ؒf����
                disconnectObjects((downPosX / drawScale) , (downPosY / drawScale), ((tempPosX - drawTransX) / drawScale), ((tempPosY - drawTransY) / drawScale));
                
    			// �ړ��ʒu���N���A����
    			tempPosX = Float.MIN_VALUE;
	        	tempPosY = Float.MIN_VALUE;
	        	downPosX = Float.MIN_VALUE;
	        	downPosY = Float.MIN_VALUE;
                return (true);
        	}

        	// �ړ��ʒu���N���A����
			tempPosX = Float.MIN_VALUE;
        	tempPosY = Float.MIN_VALUE;
        	downPosX = Float.MIN_VALUE;
        	downPosY = Float.MIN_VALUE;
        	return (true);
        }

        if (selectedPosition.rect.contains(x, y) == true)
    	{
        	//  �^�b�`�������ꂽ�ʒu���^�b�`�����I�u�W�F�N�g�Ɠ����ʒu�������ꍇ......

        	// �^�b�`�������ꂽ�ʒu��F������
        	float diffX = Math.abs(event.getX() - drawTransX - downPosX);
        	float diffY = Math.abs(event.getY() - drawTransY - downPosY);    	

    		// �^�b�`�������ꂽ�ʒu�������Ă����ꍇ�A�I�u�W�F�N�g�ʒu�̔������Ɣ��肷��B
    		if (((diffX > 2.0f)||(diffY > 2.0f))||(longPress == true))
    		{
    	        // �^�b�`�������ꂽ�ꏊ�ɂ̓I�u�W�F�N�g���Ȃ������ꍇ...�I�u�W�F�N�g�����̈ʒu�Ɉړ�������
    	    	Log.v(Main.APP_IDENTIFIER, "MOVE OBJECT : (" + diffX + "," + diffY + ")");
    			moveObjectPosition(x, y);
    			return (true);
    		}
    		
            //  �^�b�`�������ꂽ�ʒu�Ɨ����ꂽ�ʒu�������ʒu�������ꍇ......�A�C�e�����I�����ꂽ�A�ƔF������B        	
    		Log.v(Main.APP_IDENTIFIER, " ITEM SELECTED :" + x + "," + y);
    		if (selectionReceiver != null)
    		{
    			// �A�C�e�����I�����ꂽ��I�Ƌ�����
    			boolean isDraw = selectionReceiver.objectSelected(selectedPosition.getKey());

    			// �ړ��ʒu���N���A����
    			tempPosX = Float.MIN_VALUE;
	        	tempPosY = Float.MIN_VALUE;
        		return (isDraw);
    		}
    	}

    	// �^�b�`�������ꂽ�ʒu�ɃI�u�W�F�N�g�����邩�ǂ����̃`�F�b�N
    	MeMoMaObjectHolder.PositionObject position = checkSelectedObject(x, y);
        if ((position != null)&&(longPress == false))
        {
        	// ���̃I�u�W�F�N�g�Əd�Ȃ�悤�ɑ��삵���A���̏ꍇ�́A�I�u�W�F�N�g�Ԃ�����Ȃ���
        	// �i�������A�{�^���𒷉������Ă��Ȃ������Ƃ��B�j
        	lineHolder.setLines(selectedPosition.getKey(), position.getKey(), lineStyleHolder);
        	tempPosX = Float.MIN_VALUE;
        	tempPosY = Float.MIN_VALUE;
        	return (true);
        }
        
        // �^�b�`�������ꂽ�ꏊ�ɂ̓I�u�W�F�N�g���Ȃ������ꍇ...�I�u�W�F�N�g�����̈ʒu�Ɉړ�������
        moveObjectPosition(x, y);
/*
        tempPosX = Float.MIN_VALUE;
    	tempPosY = Float.MIN_VALUE;
    	float positionX = alignPosition(x, (objectSizeX / 2) * (-1));
    	float positionY = alignPosition(y, (objectSizeY / 2) * (-1));
    	selectedPosition.rect = new  android.graphics.RectF(positionX, positionY, (positionX + objectSizeX), (positionY + objectSizeY));
    	// selectedPosition.drawStyle = objectStyle;   // �s�v�A�ŏ��ɐ�������Ƃ������K�v
*/
    	return (true);
    }
    
    /**
     *   �I�u�W�F�N�g�̈ʒu���ړ�������
     * 
     * @param x
     * @param y
     */
    private void moveObjectPosition(float x, float y)
    {
        tempPosX = Float.MIN_VALUE;
    	tempPosY = Float.MIN_VALUE;
    	float sizeX = selectedPosition.rect.right - selectedPosition.rect.left;
    	float sizeY = selectedPosition.rect.bottom - selectedPosition.rect.top;
    	
    	float positionX = alignPosition(x, (sizeX / 2) * (-1));
    	float positionY = alignPosition(y, (sizeY / 2) * (-1));
    	selectedPosition.rect = new  android.graphics.RectF(positionX, positionY, (positionX + sizeX), (positionY + sizeY));
    	
    	return;
    }
    
	  /**
	   *    onTouchEvent : ��ʂ��^�b�`�������̃C�x���g����
	   *    (true �Ȃ�A��ʕ`������{����)
	   */
	  public boolean onTouchEvent(MotionEvent event)
	  {
            boolean isDraw = false;

            /** �X�P�[���W�F�X�`��(�}���`�^�b�`�̃W�F�X�`��)���E�� **/
            isDraw = scaleGestureDetector.onTouchEvent(event);
        	if ((onScaling == true)||(scaleGestureDetector.isInProgress() == true))
        	{
        		//  �}���`�^�b�`���쒆...
        		return (true);
        	}
        	
	        /**  ��ɃW�F�X�`���[���E���Ă݂悤...   **/
            isDraw = gestureDetector.onTouchEvent(event);
            if (isDraw == true)
            {
            	Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onTouchEvent() : isDraw == true");
            	return (isDraw);
            }

	    	int action = event.getAction();
	    	
	    	//Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onTouchEvent() : " + action);
	        if (action == MotionEvent.ACTION_DOWN)
	        {
	        	// �^�b�`���ꂽ�Ƃ�
	        	isDraw = onTouchDown(event);
	        }
	        else if (action == MotionEvent.ACTION_MOVE)
	        {
	        	// �^�b�`���ꂽ�܂ܓ������ꂽ�Ƃ��̏���
	        	tempPosX = event.getX();
                tempPosY = event.getY();
                isDraw = true;
	        }
	        else if (action == MotionEvent.ACTION_UP)
	        {
	        	// �^�b�`�������ꂽ�Ƃ��̏���...
	            isDraw = onTouchUp(event);
	        }

	        return (isDraw);
	  }

	  /**
	   *   �^�e���R�ʒu�����킹����悤�A��������B
	   * @param pos
	   * @return
	   */
	  private float alignPosition(float pos, float offset)
	  {
		  // �ʒu�𒲐�����B
		  return (pos + offset);
	  }	

	  /**
	   *    �ʒu���画�肵�A�I�������I�u�W�F�N�g����������
	   *    �i�I�u�W�F�N�g���I������Ă��Ȃ��ꍇ�ɂ́Anull����������j
	   * @param x
	   * @param y
	   * @return
	   */
	  private MeMoMaObjectHolder.PositionObject checkSelectedObject(float x, float y)
	  {
          Enumeration<Integer> keys = objectHolder.getObjectKeys();
	      //Log.v(Main.APP_IDENTIFIER, "CHECK POS "  + x + "," + y);
		  while (keys.hasMoreElements())
		  {
			  Integer key = keys.nextElement();
			  MeMoMaObjectHolder.PositionObject pos = objectHolder.getPosition(key);
			  if (pos.rect.contains(x, y) == true)
			  {
				      Log.v(Main.APP_IDENTIFIER, "SELECTED :"  + pos.rect.centerX() + "," + pos.rect.centerY() +  " KEY :" + key);
				      return (pos);
			  }
			  //Log.v(Main.APP_IDENTIFIER, "NOT MATCH :"   + pos.rect.centerX() + "," + pos.rect.centerY());
		  }
		  //Log.v(Main.APP_IDENTIFIER, "RETURN NULL...");
		  return (null);
	  }	

	  /**
	   *   ���ƌ�������I�u�W�F�N�g�ڑ��������ׂč폜����
	   * 
	   * @param startX
	   * @param startY
	   * @param endX
	   * @param endY
	   */
      private void disconnectObjects(float startX, float startY, float endX, float endY)
      {
    	    Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::disconnectObjects() [" + startX + "," + startY +"]-[" + endX + "," + endY + "]");
		    try
		    {
		    	Enumeration<Integer> keys = lineHolder.getLineKeys();
		        while (keys.hasMoreElements())
		        {
		            Integer key = keys.nextElement();
		            MeMoMaConnectLineHolder.ObjectConnector line = lineHolder.getLine(key);
		            if (line.key > 0)
		            {
		            	    // ���̎n�_�ƏI�_�����o��
		    			    MeMoMaObjectHolder.PositionObject from = objectHolder.getPosition(line.fromObjectKey);
		    			    MeMoMaObjectHolder.PositionObject to = objectHolder.getPosition(line.toObjectKey);

		    			    // �����������Ă��邩�`�F�b�N����
		    			    if (checkIntersection(startX, startY, endX, endY, from.rect.centerX(),  from.rect.centerY(),  to.rect.centerX(), to.rect.centerY()) == true)		    			    
		    			    {
                                // �����������Ă����I ����؂�I
		    			    	//Log.v(Main.APP_IDENTIFIER, "CUT LINE [" +  from.rect.centerX() + "," +  from.rect.centerY() +"]-[" + to.rect.centerX() + "," + to.rect.centerY() + "]");
			    			    lineHolder.disconnectLines(line.key);
		    			    }		    			    
		    		 }
		        }
		    }
		    catch (Exception ex)
		    {
		    	// ��O�����A�ł��Ȃɂ����Ȃ�
		    }
      }
      
      /**
       *    �����������Ă��邩�`�F�b�N����
       * 
       * @param x1  ���P�̎n�_ (X���W)
       * @param y1  ���P�̎n�_ (Y���W)
       * @param x2  ���P�̏I�_ (X���W)
       * @param y2  ���P�̏I�_ (Y���W)
       * @param x3  ���Q�̎n�_ (X���W)
       * @param y3  ���Q�̎n�_ (Y���W)
       * @param x4  ���Q�̏I�_ (X���W)
       * @param y4  ���Q�̏I�_ (Y���W)
       * @return  true �Ȃ�����������Ă���
       */
      private boolean checkIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4)
      {
          // �������s���ǂ����̃`�F�b�N���s��
    	  float denominator = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
    	  if (Math.abs(denominator) < 0.00001)
    	  {
    		  // �������s�ƔF���A�������Ȃ�
    		  return (false);
    	  }

          float tempX = x3 - x1;
          float tempY = y3 - y1;
          float dR = (((y4 - y3) * tempX) - ((x4 - x3) * tempY)) / denominator;
          float dS = (((y2 - y1) * tempX) - ((x2 - x1) * tempY)) / denominator;
 
    	  // �Q�����̌�_�����߂�
    	  //float crossX, crossY;
          //crossX = x1 + dR * (x2 - x1);
          //crossY = y1 + dR * (y2 - y1);

          // ��_���������ɂ��邩�ǂ������`�F�b�N����
          if ((dR >= 0)&&(dR <= 1)&&(dS >= 0)&&(dS <= 1))
          {
        	  return (true);
          }
          return (false);
      }

      /**
       *   ���s�ړ��E�Y�[���̃T�C�Y�����Z�b�g����
       * 
       */
      public void resetScaleAndLocation(SeekBar zoomBar)
      {
    	    // ���s�ړ������Z�b�g����
    	    drawTransX = 0.0f;
    	    drawTransY = 0.0f;
    	    
            // �v���O���X�o�[�̈ʒu�����Z�b�g����
    	    drawScale = 1.0f;
    	    zoomBar.setProgress(50);

    	    // preference�ɏ�Ԃ��L�^����
  	        recordTranslateAndZoomScale(50);
      }

      /**
       *    �X���C�h�o�[��ύX���ꂽ���̏���
       */
      public void zoomScaleChanged(int progress)
      {
    	  float val = ((float) progress - 50.0f) / 50.0f;

    	  // �O�̕\���̈�T�C�Y���擾
    	  float prevSizeWidth = screenWidth * drawScale;
    	  float prevSizeHeight = screenHeight * drawScale;

    	  //  �\���{����ύX���A�{������ʂɕ\������
    	  drawScale = (float) Math.round(Math.pow(10.0, val) * 10.0) / 10.0f;
    	  TextView  textview = (TextView) parent.findViewById(R.id.ZoomRate);
    	  textview.setText("x" + drawScale);

    	  // ���݂̕\���̈�T�C�Y���擾
    	  float showSizeWidth = screenWidth * drawScale;
    	  float showSizeHeight = screenHeight * drawScale;

    	  // �{���ɂ��킹�ĕ��s�ړ�����ꏊ�𒲐�����
    	  drawTransX = (prevSizeWidth - showSizeWidth) / 2.0f  + drawTransX;
    	  drawTransY = (prevSizeHeight - showSizeHeight) / 2.0f + drawTransY;
          
	      // preference�ɏ�Ԃ��L�^����
	      recordTranslateAndZoomScale(progress);
      }

      /**
       *    ���s�ړ���ԂƔ{���̏�Ԃ��L������
       * 
       */
      private void recordTranslateAndZoomScale(int progress)
      {
    	  //Log.v(Main.APP_IDENTIFIER, "recordTranslateAndZoomScale() : x" + drawScale + " X:" + drawTransX + " Y: " + drawTransY);
          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
          SharedPreferences.Editor editor = preferences.edit();
          editor.putFloat("drawScale", drawScale);
          editor.putFloat("drawTransX", drawTransX);
          editor.putFloat("drawTransY", drawTransY);
          editor.putInt("zoomProgress", progress);
          editor.commit();    	  
      }

      /**
       *    ���s�ړ���ԂƔ{���̏�Ԃ��L������
       * 
       */
      private void restoreTranslateAndZoomScale()
      {
          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
          drawScale = preferences.getFloat("drawScale", 1.0f);
          drawTransX = preferences.getFloat("drawTransX", 0.0f);
          drawTransY = preferences.getFloat("drawTransY", 0.0f);
    	  Log.v(Main.APP_IDENTIFIER, "restoreTranslateAndZoomScale() : x" + drawScale + " X:" + drawTransX + " Y: " + drawTransY);
      }

      /**
       *    GestureDetector.OnGestureListener �̎���
       */
      public boolean onDown(MotionEvent event)
      {
          //Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onDown() "  + event.getX()  + "," + event.getY());    	  
          return (false);    	  
      }

      /**
       *    GestureDetector.OnGestureListener �̎���
       */
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
      {
          //Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onFling() "  + velocityX  + "," + velocityY);    	  
          return (false);    	  
      }

      /**
       *    GestureDetector.OnGestureListener �̎���
       */
      public void onLongPress(MotionEvent event)
      {
    	  Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onLongPress() "  + event.getX()  + "," + event.getY());   

    	  // �^�b�`�ʒu���I�u�W�F�N�g�摜�̍��W�ɕϊ�����
    	  float x = (event.getX() - drawTransX) / drawScale;
      	  float y = (event.getY() - drawTransY) / drawScale;

    	  // �^�b�`�ʒu�ɃI�u�W�F�N�g�����݂��邩�m�F����
          MeMoMaObjectHolder.PositionObject  position = checkSelectedObject(x, y);
      	  if (position != null)
      	  {
      		  // ���������������{���Ă��邱�Ƃ��L������
      	      onGestureProcessed = true;

      		  // �^�b�`�����ꏊ�ɃI�u�W�F�N�g�����݂����I�I
      	      selectionReceiver.objectSelectedContext(position.getKey());
      	  }          
      }

      /**
       *    GestureDetector.OnGestureListener �̎���
       */
      public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
      {
          //Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onScroll() "  + distanceX  + "," + distanceY);    	  
          return (false);    	  
      }

      /**
       *    GestureDetector.OnGestureListener �̎���
       */
      public void onShowPress(MotionEvent event)
      {
         //Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onShowPress() "  + event.getX()  + "," + event.getY());    	  
      }

      /**
       *    GestureDetector.OnGestureListener �̎���
       */
      public boolean onSingleTapUp(MotionEvent event)
      {
            //Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onSingleTapUp() "  + event.getX()  + "," + event.getY());
            return (false);
      }

      /**
       *    �X���C�h�o�[��ύX���ꂽ���̏���
       *    (SeekBar.OnSeekBarChangeListener �̎���)
       */
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
      {	
    	    // ��ʕ`��̔{����ύX����
    	    zoomScaleChanged(progress);

    	    // ��ʕ`��N���X�ɍĕ`����w������
	        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
	        surfaceView.doDraw();
      }

      /**
       *    SeekBar.OnSeekBarChangeListener �̎���
       */
      public void onStartTrackingTouch(SeekBar seekBar)
      {
           // �������Ȃ�
      }

      /**
       *    SeekBar.OnSeekBarChangeListener �̎���
       */
      public void onStopTrackingTouch(SeekBar seekBar)
      {
    	   // �������Ȃ� 
      }

      /**
       *   �iScaleGestureDetector.OnScaleGestureListener �̎����j
       * 
       * @param detector
       * @return
       */
      public boolean onScale(ScaleGestureDetector detector)
      {
          float scaleFactor = detector.getScaleFactor();
          //Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onScale() : " + scaleFactor + " (" + currentScaleBar + ")");

          /** ��ʕ\���̔{�����ύX���ꂽ�I�@x < 1 : �k���A 1 < x : �g�� **/
          if (scaleFactor < 1.0f)
          {
        	  currentScaleBar = (currentScaleBar == 0) ? 0 : currentScaleBar - 1;
          }
          else if (scaleFactor > 1.0f)
          {
        	  currentScaleBar = (currentScaleBar == 100) ? 100 : currentScaleBar + 1;
          }
          zoomScaleChanged(currentScaleBar);

          return (onScaling);
      }

      /**
       *   �iScaleGestureDetector.OnScaleGestureListener �̎����j
       *   
       * 
       */
      public  boolean	 onScaleBegin(ScaleGestureDetector detector)
      {
          //Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onScaleBegin() ");
          SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
          currentScaleBar = preferences.getInt("zoomProgress", 50);
    	  onScaling = true;
    	  return (onScaling);
      }

      /**
       *   �iScaleGestureDetector.OnScaleGestureListener �̎����j
       *   
       */
      public void	 onScaleEnd(ScaleGestureDetector detector)
      {
          //Log.v(Main.APP_IDENTIFIER, "MeMoMaCanvasDrawer::onScaleEnd() " + currentScaleBar);
    	  onScaling = false;
    	  
    	  // �V�[�N�o�[��ݒ肵�A�l���L������
	      final SeekBar seekbar = (SeekBar) parent.findViewById(R.id.ZoomInOut);
	      seekbar.setProgress(currentScaleBar);	        
	      zoomScaleChanged(currentScaleBar);
      }
 }
