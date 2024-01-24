package jp.sourceforge.gokigen.memoma;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 *   �߂��܂̃I�u�W�F�N�g�E���C���E���x����`�悷�郁�\�b�h�Q
 *   (���݂̂Ƃ���AMeMoMaCanvasDrawer�N���X����ǂ��o���Ă�������...)
 * 
 * @author MRSa
 *
 */
public class ObjectShapeDrawer
{


    static public float drawObjectOval(Canvas canvas, RectF objectShape, Paint paint)
    {
        // �ȉ~�`�̕`��
        canvas.drawOval(objectShape, paint);

        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2);
    }

    static public float drawObjectRect(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �l�p�`��`�悷��
        canvas.drawRect(objectShape, paint);
        return (0.0f);
    }

    static public float drawObjectRoundRect(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �ۊp�l�p�`�̕`��
        canvas.drawRoundRect(objectShape, MeMoMaObjectHolder.ROUNDRECT_CORNER_RX, MeMoMaObjectHolder.ROUNDRECT_CORNER_RY, paint);

        return (0.0f);
    }

    static public float drawObjectDiamond(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �H�`�̕`��
        Path path = new Path();
        path.moveTo(objectShape.centerX(), objectShape.top);
        path.lineTo(objectShape.left, objectShape.centerY());
        path.lineTo(objectShape.centerX(), objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.centerY());
        path.lineTo(objectShape.centerX(), objectShape.top);
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2);
    }

    static public float drawObjectKeyboard(Canvas canvas, RectF objectShape, Paint paint)
    {
		// ��`(�L�[�{�[�h�^)�̕`��
        Path path = new Path();
        path.moveTo(objectShape.left, objectShape.centerY() - MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN);
        path.lineTo(objectShape.left, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.top);
        path.lineTo(objectShape.left, objectShape.centerY() - MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN);
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN);
    }

    static public float drawObjectParallelogram(Canvas canvas, RectF objectShape, Paint paint)
    {
		// ���s�l�ӌ`�̕`��
        Path path = new Path();
        path.moveTo(objectShape.left + MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN, objectShape.top);
        path.lineTo(objectShape.left, objectShape.bottom);
        path.lineTo(objectShape.right - MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.top);
        path.lineTo(objectShape.left + MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN, objectShape.top);
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2.0f);
    }

    static public float drawObjectHexagonal(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �Z�p�`�̕`��
        Path path = new Path();
        float margin = MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 2;
        path.moveTo(objectShape.left + margin, objectShape.top);
        path.lineTo(objectShape.left, objectShape.centerY());
        path.lineTo(objectShape.left + margin, objectShape.bottom);
        path.lineTo(objectShape.right - margin, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.centerY());
        path.lineTo(objectShape.right - margin, objectShape.top);
        path.lineTo(objectShape.left + margin, objectShape.top);
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2.0f);
    }

    static public float drawObjectPaper(Canvas canvas, RectF objectShape, Paint paint)
    {
		// ���ނ̌`�̕`��
        float margin = MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 3.0f;
        Path path = new Path();
        path.moveTo(objectShape.left, objectShape.top);
        path.lineTo(objectShape.left, objectShape.bottom - margin);
        path.cubicTo((objectShape.left + objectShape.centerX()) / 2.0f , objectShape.bottom, (objectShape.right + objectShape.centerX()) / 2.0f, objectShape.bottom - margin, objectShape.right, objectShape.bottom - margin);
        path.lineTo(objectShape.right, objectShape.top);
        path.lineTo(objectShape.left, objectShape.top);
        canvas.drawPath(path, paint);
        return ( - MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN);
    }

    static public float drawObjectDrum(Canvas canvas, RectF objectShape, Paint paint, Paint.Style paintStyle)
    {
		float margin = MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 1.0f;
		// �~���̕`��
        Path path = new Path();
        path.moveTo(objectShape.left, objectShape.top);
        path.arcTo(new RectF(objectShape.left, objectShape.top,objectShape.right, objectShape.top + margin), 180.0f, 359.999f, true);
        path.lineTo(objectShape.left, objectShape.bottom - (margin / 2.0f));
        path.arcTo(new RectF(objectShape.left, objectShape.bottom - margin, objectShape.right, objectShape.bottom), 180.0f, -180.0f, true);
        path.lineTo(objectShape.right, objectShape.top + (margin / 2.0f));
        if (paintStyle != Paint.Style.STROKE)
        {
        	// �h��Ԃ��p�ɐ��̗̈��ǉ�����
            path.arcTo(new RectF(objectShape.left, objectShape.top, objectShape.right, objectShape.top + margin), 180.0f, 180.0f, true);
            path.lineTo(objectShape.left, objectShape.bottom - (margin / 2.0f));
            path.arcTo(new RectF(objectShape.left, objectShape.bottom - margin, objectShape.right, objectShape.bottom), 180.0f, -180.0f, true);
        }
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2.0f);
    }

    static public float drawObjectCircle(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �~��`�悷��
        canvas.drawCircle(objectShape.centerX(), objectShape.centerY(), ((objectShape.right - objectShape.left)/ 2.0f), paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2.0f);
    }

    static public float drawObjectNoRegion(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �����\�����Ȃ��Ƃ킩��Ȃ��̂ŁA���x���������Ƃ��ɂ͘g��\������
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(objectShape, paint);
        paint.setColor(Color.WHITE);
        return (0.0f);
    }

    static public float drawObjectLoopStart(Canvas canvas, RectF objectShape, Paint paint)
    {
		// ���[�v�J�n�}�`�̕`��
        Path path = new Path();
        float margin = MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 2;
        path.moveTo(objectShape.left + margin, objectShape.top);
        path.lineTo(objectShape.left, objectShape.centerY());
        path.lineTo(objectShape.left, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.centerY());
        path.lineTo(objectShape.right - margin, objectShape.top);
        path.lineTo(objectShape.left + margin, objectShape.top);
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2.0f);
    }

    static public float drawObjectLoopEnd(Canvas canvas, RectF objectShape, Paint paint)
    {
		// ���[�v�I���}�`�̕`��
        Path path = new Path();
        float margin = MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 2;
        path.moveTo(objectShape.left, objectShape.top);
        path.lineTo(objectShape.left, objectShape.centerY());
        path.lineTo(objectShape.left + margin, objectShape.bottom);
        path.lineTo(objectShape.right - margin, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.centerY());
        path.lineTo(objectShape.right, objectShape.top);
        path.lineTo(objectShape.left, objectShape.top);
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2.0f);
    }

    static public float drawObjectLeftArrow(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �������}�`�̕`��
        Path path = new Path();
        float margin = MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 4.0f;
        path.moveTo(objectShape.left + margin, objectShape.top);
        path.lineTo(objectShape.left, objectShape.centerY());
        path.lineTo(objectShape.left + margin, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.top);
        path.lineTo(objectShape.left + margin, objectShape.top);
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2.0f);
    }

    static public float drawObjectDownArrow(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �������}�`�̕`��
        Path path = new Path();
        float margin = MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 2;
        path.moveTo(objectShape.left, objectShape.top);
        path.lineTo(objectShape.left, objectShape.centerY() + margin);
        path.lineTo(objectShape.centerX(), objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.centerY() + margin);
        path.lineTo(objectShape.right, objectShape.top);
        path.lineTo(objectShape.left, objectShape.top);
        canvas.drawPath(path, paint);
        return (- MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN);
    }

    static public float drawObjectUpArrow(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �㑤���}�`�̕`��
        Path path = new Path();
        float margin = MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 2.0f;
        path.moveTo(objectShape.centerX(), objectShape.top);
        path.lineTo(objectShape.left, objectShape.centerY() - margin);
        path.lineTo(objectShape.left, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.centerY() - margin);
        path.lineTo(objectShape.centerX(), objectShape.top);
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2.0f);
    }

    static public float drawObjectRightArrow(Canvas canvas, RectF objectShape, Paint paint)
    {
		// �E�����}�`�̕`��
        Path path = new Path();
        float margin = MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 4.0f;
        path.moveTo(objectShape.left, objectShape.top);
        path.lineTo(objectShape.left, objectShape.bottom);
        path.lineTo(objectShape.right - margin, objectShape.bottom);
        path.lineTo(objectShape.right, objectShape.centerY());
        path.lineTo(objectShape.right - margin, objectShape.top);
        path.lineTo(objectShape.left, objectShape.top);
        canvas.drawPath(path, paint);
        return (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN / 2.0f);
    }

    static public void drawLineStraight(Canvas canvas)
    {
    	
    }
	  /**
	   *    ����`�悷�鏈�� (�c���[�\����)
	   * 
	   * @param canvas
	   * @param paint
	   * @param x1
	   * @param y1
	   * @param checkVaule
	   * @param isXaxis
	   */
	static public void drawArrowTree(Canvas canvas, Paint paint, float x1, float y1, float checkValue, boolean isXaxis)
    {
		float margin = 8.0f;
	  	float direction = 1.0f;
	  	if (isXaxis == true)
        {
	  	    direction = (checkValue < x1) ? -1.0f : 1.0f;
            canvas.drawLine(x1, y1, (x1 + direction * margin), (y1 - margin), paint);
	        canvas.drawLine(x1, y1, (x1 + direction * margin), (y1 + margin), paint);
	  	}
	  	else
	  	{
	  	    direction = (checkValue < y1) ? -1.0f : 1.0f;
	        canvas.drawLine(x1, y1, (x1 - margin), (y1 + direction * margin), paint);
	        canvas.drawLine(x1, y1, (x1 + margin), (y1 + direction * margin), paint);    		  
	  	}
    }
	  /**
	   *    ����`�悷�鏈��
	   * 
	   * @param canvas
	   * @param paint
	   * @param x1
	   * @param y1
	   * @param x2
	   * @param y2
	   */
	  static public void drawArrow(Canvas canvas, Paint paint, float x1, float y1, float x2, float y2)
	  {
          // �����̒���
		  float moveX = 14.0f;
		  
		  // �ڑ����̌X�����A�ǂꂭ�炢�̊p�x�œ����Ă��邩�H
		  float centerDegree = (float) (Math.atan2((y2 - y1) , (x2 - x1))  * 180.0d / Math.PI);

		  // x1, y1 �� x2, y2 �������痈�����ɍ��킹������`�悷��
		  // (2�{�A�P�{�Â����āA�������]�s��ŉ�]�����Ă���)

		  // ��]�s��̏���
		  Matrix matrix1 = new Matrix();
		  matrix1.setRotate((centerDegree + 30), x1, y1);
	      Matrix matrix2 = new Matrix();
		  matrix2.setRotate((centerDegree - 30), x1, y1);

		  // ��������������A��]�s��ŉ�]������
		  Path pathLine1 = new Path();
	      pathLine1.moveTo(x1, y1);
	      pathLine1.lineTo(x1 + moveX, y1);
	      pathLine1.transform(matrix1);
	      canvas.drawPath(pathLine1, paint);

		  Path pathLine2 = new Path();
	      pathLine2.moveTo(x1, y1);
	      pathLine2.lineTo(x1 + moveX, y1);
	      pathLine2.transform(matrix2);
	      canvas.drawPath(pathLine2, paint);
    }

	  /**
	     *    �I�u�W�F�N�g�̃��x����\������
	     * 
	     * @param canvas
	     * @param paint
	     * @param pos
	     */
	    public static void drawTextLabel(Canvas canvas, Paint paint, MeMoMaObjectHolder.PositionObject pos, RectF region, int displayObjectInformation, float offsetX, float offsetY)
	    {
    		// �^�C�g���̐擪������\������ꍇ...
        	String labelToShow = pos.label;
        	if (displayObjectInformation == 0)
        	{
            	float width = region.width() - MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN_WIDTH;
                int textLen = paint.breakText(pos.label, true, width, null);  // �ȗ�������ǉ����邩��A���̂Ԃ񌸂炷
            	labelToShow = labelToShow.substring(0, textLen);
            	if (labelToShow != pos.label)
            	{
            		// truncate �����ꍇ�ɂ́A�ȗ��������o���B
            		labelToShow = labelToShow + "...";
            	}
        	}

        	if (Paint.Style.valueOf(pos.paintStyle) != Paint.Style.STROKE)
	        {
	    	    // �I�u�W�F�N�g��h��Ԃ��̂Ƃ��́A�����̐F��ݒ肷��
	            paint.setColor(pos.labelColor);
	        }
	        
	        // ������������Ɖe�t���ɂ���
	        paint.setShadowLayer(0.5f, 0.5f, 0.5f, Color.DKGRAY);
	        
	        // ���[�U�`�F�b�N�̕`��
	        if (pos.userChecked == true)
	        {
	        	canvas.drawText("*", region.centerX(), region.top + (MeMoMaCanvasDrawer.OBJECTLABEL_MARGIN * 2.0f), paint);
	       }
	        
	        // �����\��
	        if (pos.strokeWidth != 0.0f)
	        {
	        	// ���̂܂ܕ\������ƁA�ǂ߂Ȃ��̂ŁA�����𒲐����A�A���_�[���C�����������Ƃɂ���
	        	paint.setStrokeWidth(0.0f);
	            paint.setSubpixelText(true);
	            paint.setUnderlineText (true);
	        }

	        if (displayObjectInformation == 0)
	        {            
	        	// �P�s�������\�����Ȃ��ꍇ...���̂܂ܕ\�����ďI������
	            canvas.drawText(labelToShow, (region.left + offsetX),   (region.centerY() + offsetY), paint);
	            return;
	        }
	        
	        float tall = paint.getFontMetrics().top + 1.0f;
	        float posX = (region.left + offsetX);
	        float posY =  (region.centerY() + offsetY);
	        float width = region.right - region.left - 12.0f;  // ��

	        int startChar  = 0;
	        int endChar = pos.label.length();
	        do
	        {
	            int textLen = paint.breakText(pos.label, startChar, endChar, true, width, null);
	            canvas.drawText(labelToShow, startChar, (startChar +textLen), posX, posY, paint);

	            posY = posY - tall;
	            startChar = startChar + textLen;
	        } while (startChar < endChar);  
	    }
}
