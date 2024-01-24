package jp.sourceforge.gokigen.aligner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 *  �܂���O���t�̕`��N���X
 * 
 * @author MRSa
 *
 */
public class GokigenScaleDrawer implements IGokigenGraphDrawer
{
	private Context parent = null;
	
    private final int MAXIMUM_SCALE = 2;
    private final int MINIMUM_SCALE = 0;
    private int graphScale = MINIMUM_SCALE;
    private int graphRange = 0;
    private String messageToShow = "";
    
    private int shapeType = 0;

	/**
     *  �R���X�g���N�^
     * 
     */
	public GokigenScaleDrawer(Context arg)
	{
        parent = arg;
	}	
	

	/**
	 *  ����
	 * 
	 */
	public void prepare()
	{
        // �\�����郁�b�Z�[�W������������
		setMessage(null);
	}
 
	/**
	 *  �\�����郁�b�Z�[�W��ݒ肷��
	 * 
	 * @param message
	 */
	public void setMessage(String message)
	{
		if (message == null)
		{
	        // �\�����郁�b�Z�[�W��ݒ肷��
			messageToShow = parent.getString(R.string.captureInfo);			
		}
		else
		{
            // �w�肳�ꂽ���b�Z�[�W��ݒ肷��
            messageToShow = message;
		}
		return;
	}
	
	/**
	 *  �`�惁�C������
	 * 
	 */
    public void drawOnCanvas(Canvas canvas, int reportType)
    {
        //Log.v(Main.APP_IDENTIFIER, "GokigenScaleDrawer::drawOnCanvas()");
    	
    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);

        // �̂̌`��I������
        if (shapeType == 1)
        {
            // �̂̃��C����\������(����)
            paint.setColor(Color.BLACK);
            drawBodyFront(canvas, paint, 1, 1);
          
            paint.setColor(Color.WHITE);
            drawBodyFront(canvas, paint, 0, 0);
        }
        else // if (shapeType == 0)
        {
            // �̂̃��C����\������(����)
            paint.setColor(Color.BLACK);
            drawBodySide(canvas, paint, 1, 1);
          
            paint.setColor(Color.WHITE);
            drawBodySide(canvas, paint, 0, 0);
        }


        int width = canvas.getWidth();
        int height = canvas.getHeight();
/*
        float middleY = height / 2;
        float middleX = width / 2;
        
        // �\���̃X�P�[����\������B
        paint.setColor(Color.BLACK);
        canvas.drawLine(0, (middleY + 1), (float) width, (middleY + 1), paint);
        canvas.drawLine((middleX + 1), 0, (middleX + 1), (float) height, paint);

        paint.setColor(Color.WHITE);
        canvas.drawLine(0, middleY, (float) width, middleY, paint);
        canvas.drawLine(middleX, 0, middleX, (float) height, paint);
*/

        // ������̑傫����ݒ肷��
        Rect bounds = new Rect();
        paint.getTextBounds(messageToShow, 0, messageToShow.length(), bounds);
        
        // �C���t�H���[�V������\��
        paint.setColor(Color.BLACK);
        canvas.drawText(messageToShow, (width - (bounds.width() + 4)), (height - (bounds.height() + 4)), paint);

        paint.setColor(Color.LTGRAY);
        canvas.drawText(messageToShow, (width - (bounds.width() + 5)), (height - (bounds.height() + 5)), paint);
    }

	/**
	 *  �k��
	 * 
	 */
    public void actionZoomOut()
    {
    	if (graphScale > MINIMUM_SCALE)
    	{
    		graphScale--;
    	}
    	if ((graphScale == 1)&&(graphRange > 1))
    	{
    		graphRange = 1;
    	}
    	else
    	{
    		graphRange = 0;
    	}
    }

    /**
     *  ���炾�̃��C����\������ (����)
     * 
     * @param canvas
     * @param paint
     * @param offsetX
     * @param offsetY
     */
    private void drawBodyFront(Canvas canvas, Paint paint, float offsetX, float offsetY)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // ����`�悷��
        paint.setStyle(Paint.Style.STROKE);
        float centerX = ((float) 32 + 16) / (float) 300 * width + offsetX;
        float centerY = (float) height / (float) 2 + offsetY;
        float radius = ((float) 32 - 16) / (float) 300 * width;
        canvas.drawCircle(centerX, centerY, radius, paint);

        // ���`�悷��
        float startX = (float) (64) / (float) 300 * width + offsetX;
        float length = (float) (16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);
        
        // ����`�悷��
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        float lengthY = (((float) 16) / ((float) 300) * width);
        float startY = (float) height / (float) 2;
        canvas.drawLine(startX, (startY + offsetY), startX, (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + offsetY), startX, (startY + lengthY + offsetY), paint);

        // �r��`�悷��
        length = (float) (56) / (float) 300 * width;
        float lengthY2 =  (((float) 40) / ((float) 300) * width);
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY2 + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY2 + offsetY), paint);
        
        startX = startX + length;
        length = (float) (64) / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY2 + offsetY), (startX + length), (startY - lengthY2 + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY2 + offsetY), (startX + length), (startY + lengthY2 + offsetY), paint);
        
        // ����`�悷��
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        length = (float) (80) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // ����`�悷��
        startX = (float) (64 + 16 + 80) / (float) 300 * width + offsetX;
        length = (float) (16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // ���Ղ�`�悷��
        startX = (float) (64 + 16 + 80 + 16) / (float) 300 * width + offsetX;
        length = (float) (8) / (float) 300 * width;
        lengthY = (((float) 16) / ((float) 300) * width);
        startY = (float) height / (float) 2;
        canvas.drawLine(startX, (startY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);
    
        // ��������`�悷��
        startX = (float) (64 + 16 + 80 + 16 + 8) / (float) 300 * width + offsetX;
        length = (float) 56 / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);

        // ����`�悷��
        startX = (float) (64 + 16 + 80 + 16 + 8 + 56) / (float) 300 * width + offsetX;
        length = (float) 56 / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);
    }
    
    /**
     *  ���炾�̃��C����\������ (����)
     * 
     * @param canvas
     * @param paint
     * @param offsetX
     * @param offsetY
     */
    private void drawBodySide(Canvas canvas, Paint paint, float offsetX, float offsetY)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // ����`�悷��
        paint.setStyle(Paint.Style.STROKE);
        float centerX = ((float) 32 + 16) / (float) 300 * width + offsetX;
        float centerY = (float) height / (float) 2 + offsetY;
        float radius = ((float) 32 - 16) / (float) 300 * width;
        RectF oval = new RectF((centerX - radius), (centerY - radius / 3), (centerX + radius), (centerY + radius / 3));
        canvas.drawOval(oval, paint);
        
        // ���`�悷��
        float startX = ((float) 64) / (float) 300 * width + offsetX;
        float length = (float) (64 + 16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // �r��`�悷��
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        length = (float) (40) / (float) 300 * width;
        float lengthY2 =  (((float) 5) / ((float) 300) * width);
        canvas.drawLine(startX, centerY, (startX + length), centerY - lengthY2, paint);

        startX = (float) (64 + 16 + 40) / (float) 300 * width + offsetX;
        length = (float) (56) / (float) 300 * width;
        canvas.drawLine(startX, centerY - lengthY2, (startX + length), centerY + lengthY2, paint);
         
        // ����`�悷��
        
        // ���`����`�悷��
        startX = ((float) 64 + 16) / (float) 300 * width + offsetX;
        length = (float) (80 + 16 + 8 + 56 + 56) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        
/*
        // ���`�悷��
        float startX = (float) (64) / (float) 300 * width + offsetX;
        float length = (float) (16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);
        
        // ����`�悷��
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        float lengthY = (((float) 16) / ((float) 300) * width);
        float startY = (float) height / (float) 2;
        canvas.drawLine(startX, (startY + offsetY), startX, (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + offsetY), startX, (startY + lengthY + offsetY), paint);

        // �r��`�悷��
        length = (float) (56) / (float) 300 * width;
        float lengthY2 =  (((float) 40) / ((float) 300) * width);
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY2 + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY2 + offsetY), paint);
        
        startX = startX + length;
        length = (float) (64) / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY2 + offsetY), (startX + length), (startY - lengthY2 + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY2 + offsetY), (startX + length), (startY + lengthY2 + offsetY), paint);
        
        // ����`�悷��
        startX = (float) (64 + 16) / (float) 300 * width + offsetX;
        length = (float) (80) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // ����`�悷��
        startX = (float) (64 + 16 + 80) / (float) 300 * width + offsetX;
        length = (float) (16) / (float) 300 * width;
        canvas.drawLine(startX, centerY, (startX + length), centerY, paint);

        // ���Ղ�`�悷��
        startX = (float) (64 + 16 + 80 + 16) / (float) 300 * width + offsetX;
        length = (float) (8) / (float) 300 * width;
        lengthY = (((float) 16) / ((float) 300) * width);
        startY = (float) height / (float) 2;
        canvas.drawLine(startX, (startY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);
    
        // ��������`�悷��
        startX = (float) (64 + 16 + 80 + 16 + 8) / (float) 300 * width + offsetX;
        length = (float) 56 / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);

        // ����`�悷��
        startX = (float) (64 + 16 + 80 + 16 + 8 + 56) / (float) 300 * width + offsetX;
        length = (float) 56 / (float) 300 * width;
        canvas.drawLine(startX, (startY - lengthY + offsetY), (startX + length), (startY - lengthY + offsetY), paint);
        canvas.drawLine(startX, (startY + lengthY + offsetY), (startX + length), (startY + lengthY + offsetY), paint);
*/
    }

	/**
	 *  �g��
	 * 
	 */
    public void actionZoomIn()
    {
        if (graphScale < MAXIMUM_SCALE)
        {
            graphScale++;
        }
    }

	/**
	 *  �O�f�[�^
	 * 
	 */
    public boolean actionShowPreviousData()
    {
    	if (graphRange > 0)
    	{
    		graphRange--;
    		return (false);
    	}
    	
        // �ЂƂO�̃f�[�^�ɍX�V����...
    	if (graphScale == 2)
    	{
    		graphRange = 3;
    	}
    	else if (graphScale == 1)
    	{
    		graphRange = 1;
    	}
    	else
    	{
    		graphRange = 0;
    	}    	
        return (true);
    }

	/**
	 *  ��f�[�^
	 *
	 */
    public boolean actionShowNextData()
    {
    	if (graphScale == 1)
    	{
    		if (graphRange == 0)
    		{
    			graphRange++;
    			return (false);
    		}
    	}
    	else if (graphScale == 2)
    	{
    		if (graphRange < 3)
    		{
    			graphRange++;
    			return (false);
    		}
    	}
        graphRange = 0;
        return (true);    	
    }
    
    public void reset()
    {
    	// �Ȃɂ����Ȃ�
    }
    
    public void undo()
    {
    	// �Ȃɂ����Ȃ�
    }

    /**
     * �`��^�C�v��ݒ肷��
     */
    public void setDrawType(int type)
    {
    	shapeType = type;
    }
}
