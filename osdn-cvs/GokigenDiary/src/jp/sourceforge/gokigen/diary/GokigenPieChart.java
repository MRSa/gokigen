package jp.sourceforge.gokigen.diary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

/**
 *  �~�O���t�̕`��N���X
 * 
 * @author MRSa
 *
 */
public class GokigenPieChart implements IGokigenGraphDrawer
{
	private Context parent = null;

	/**
	 *  �R���X�g���N�^
	 * 
	 */
	public GokigenPieChart(Context arg)
	{
        parent = arg;
	}
	   

	/**
	 *  ����
	 * 
	 */
	public void prepare()
	{
        // �������Ȃ�
	}
	
 
    /**
	 *  �`�惁�C������
	 * 
	 */
    public void drawOnCanvas(Canvas canvas, int reportType, GokigenGraphDataHolder dataHolder)
    {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        
        
        float hMargin    = 5;
        float lineWidth  = 115;
        float lineHeight = 22;
        float vMargin    = 6;

        Bitmap area = BitmapFactory.decodeResource(parent.getResources(), R.drawable.emo_im_cool_s);
        int iconScaledHeight = area.getScaledHeight(canvas);
        int iconHeight = area.getHeight();
        lineHeight = (iconHeight > iconScaledHeight) ? (iconHeight + 2) : (iconScaledHeight + 2);

        // 1���̃f�[�^�̕��v�Z
        float textWidth = paint.measureText(" 000: 100%");
        int iconWidth = area.getWidth();
        lineWidth = textWidth + iconWidth + vMargin + 25;
        
        //  �}��\���G���A�̗̈�v�Z
        float startX = (width - (lineWidth * 2)) / 2;
        float startY = height - (lineHeight * 5) - hMargin;
        
        //  �~�O���t�̗̈�v�Z
        float radius = height - (startY - 1);
        float centerX = width / 2;
        float centerY = radius;
        if (centerX < radius)
        {
            radius = centerX;
        }
        
        // ���̕����L��������A�~�O���t�Ɩ}������ɕ��ׂĕ\������
        if (width > ((radius * 2) + lineWidth * 2 + vMargin))
        {
            centerX = radius + (vMargin / 2);
            startX = width - ((lineWidth * 2) + vMargin / 2);
        }
        
        //  �}��\���G���A�̘g��\������
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(startX, startY, startX, startY + lineHeight * 5, paint);
        canvas.drawLine(startX + lineWidth * 2, startY, startX + lineWidth * 2, startY + lineHeight * 5, paint);
        canvas.drawLine(startX, startY, startX + lineWidth * 2, startY, paint);
        canvas.drawLine(startX, startY + lineHeight * 5, startX + lineWidth * 2, startY + lineHeight * 5, paint);
        for (float i = startY; i < startY + lineHeight * 5; i = i + lineHeight)
        {
            canvas.drawLine(startX, i, startX + lineWidth * 2, i, paint);
        }
        canvas.drawLine(startX + lineWidth, startY, startX + lineWidth, startY + lineHeight * 5, paint);

        // �g�[�^���̃f�[�^������\������
        int totalCount = dataHolder.getTotalDataCount();
        canvas.drawText("Total: " + (int) totalCount, startX + lineWidth + 30, startY - lineHeight, paint);
        if (totalCount < 1)
        {
        	// ���Ƀg�[�^������ύX���� (divide by zero�̑΍�)
        	totalCount = 1;
        }
        
        // �}��̎��́i�G���[�V�����A�C�R���Ȃǁj��`��
        for (int i = 0; i < 5; i++)
        {
            int iconId = DecideEmotionIcon.decideEmotionIconFromIndex(i, true);
            int dataCount = dataHolder.getDataCount(i);
            Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), iconId);
            
            paint.setColor(DecideEmotionIcon.decideEmotionIconColor(iconId));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(startX + 4, startY + lineHeight * i + 5, startX + 22,  startY + lineHeight * i + 17, paint);

            paint.setColor(Color.LTGRAY);
            canvas.drawBitmap(bitmap, startX + 30, startY + lineHeight * i + 1, paint);
            canvas.drawText(dataCount + ": " + (int) ((dataCount * 100) / totalCount)  + "%", startX + 35 + iconWidth, startY + lineHeight * (i + 1) - 2, paint);
        }
        for (int i = 0; i < 5; i++)
        {
            int iconId = DecideEmotionIcon.decideEmotionIconFromIndex(i + 5, true);
            int dataCount = dataHolder.getDataCount(i + 5);
            Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), iconId);

            paint.setColor(DecideEmotionIcon.decideEmotionIconColor(iconId));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(startX + lineWidth + 4, startY + lineHeight * i + 5, startX + lineWidth + 25,  startY + lineHeight * i + 17, paint);

            paint.setColor(Color.LTGRAY);
            canvas.drawBitmap(bitmap, startX + lineWidth + 30, startY + lineHeight * i + 1, paint);
            canvas.drawText(dataCount + ": " + (int) ((dataCount * 100) / totalCount) + "%", startX + lineWidth + 35 + iconWidth, startY + lineHeight * (i + 1) - 2, paint);
        }
        
        // �~�O���t��`��
        paint.setAntiAlias(true);
        paint.setColor(Color.LTGRAY);
        canvas.drawCircle(centerX, height - (startY - 1), radius, paint);   // �x�[�X�̉~�O���t

        try
        {
            float startDegree = 0;
            RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            int itemCount = dataHolder.getDataItemCount();
            for (int i = 0; i < itemCount; i++)
            {
            	int dataCount = dataHolder.getDataCount(i);
                float endDegree = ((float) dataCount / (float) totalCount) * 360;
                if (endDegree >= 360)
                {
                    endDegree = (float) 359.9;
                }
                if (endDegree != 0)
                {
                    int iconId = DecideEmotionIcon.decideEmotionIconFromIndex(i, true);
                    paint.setColor(DecideEmotionIcon.decideEmotionIconColor(iconId));
                    canvas.drawArc(rect, startDegree, endDegree, true, paint);

                    // �~�O���t��ɃA�C�R����ݒ肷��
                    float offset = (float) 0.9 * (float) 2.0 / (float) 3.0;
                    if ((i % 2 == 0))
                    {
                        offset = (float) 1.1 * (float) 2.0 / (float) 3.0;
                    }
                    float left = centerX + (float) (radius * offset * Math.cos(Math.toRadians((double) startDegree + (endDegree / 2)))) - 11;
                    float top =  centerY + (float) (radius * offset * Math.sin(Math.toRadians((double) startDegree + (endDegree / 2)))) - 11;
                    Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), iconId);
                    canvas.drawBitmap(bitmap, left, top, paint);
                    
                    startDegree = startDegree + endDegree;
                }
            }
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "??? " + ex.getMessage());
        }
    }

	/**
	 *  �g��
	 * 
	 */
    public void actionZoomIn()
    {
        // �������Ȃ�    	
    }

	/**
	 *  �k��
	 * 
	 */
    public void actionZoomOut()
    {
        // �������Ȃ�    	
    }

	/**
	 *  �O�f�[�^
	 * 
	 */
    public boolean actionShowPreviousData()
    {
        // �������Ȃ�
        return (true);
    }

	/**
	 *  ��f�[�^
	 *
	 */
    public boolean actionShowNextData()
    {
        // �������Ȃ�
        return (true);    	
    }
}
