package jp.sourceforge.gokigen.diary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 *  �_�O���t�̕`��N���X
 * 
 * @author MRSa
 *
 */
public class GokigenBarChart implements IGokigenGraphDrawer
{
	private Context parent = null;

	/**
     *  �R���X�g���N�^
     * 
     */
	public GokigenBarChart(Context arg)
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

        // �`��̈�̃T�C�Y���擾����
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Bitmap area = BitmapFactory.decodeResource(parent.getResources(), R.drawable.emo_im_cool_s);
        int iconScaledHeight = area.getScaledHeight(canvas);
        int iconHeight = area.getHeight();
        float topMargin = (iconHeight > iconScaledHeight) ? (iconHeight + 2) : (iconScaledHeight + 2);

        float barMargin = 7;
        float bottomMargin = 16;
        float textMargin = 12;
        float iconMargin = 2;

        // �f�[�^�̎�ސ�����_�O���t�̕������߂�
        int nofItems = dataHolder.getDataItemCount();
        float barWidth = (width - (barMargin * (nofItems + 1))) / nofItems;

        // �`�悪�K�v�Ȗ_�O���t�̍ő�̍��������߂� (���鐔�����������ꍇ�ɂ́A�����X�P�[���ɂ���j
        int maxCount = 15;
        if (reportType == GokigenGraphListener.REPORTTYPE_MONTHLY)
        {
            maxCount = 70;
        }
        for (int index = 0; index < nofItems; index++)
        {
            int count = dataHolder.getDataCount(index);
            if (maxCount < count)
            {
            	maxCount = count;
            }
        }

        // �f�[�^1��������̍��������߂�
        float barUnit = (height - bottomMargin - topMargin) / maxCount;
        
        // �_�O���t�̕`�揈��
        float drawX = barMargin;
        for (int index = 0; index < nofItems; index++)
        {
        	int itemCount =  dataHolder.getDataCount(index);
            float barHeight = (float) itemCount * barUnit;
            float bottom = height - bottomMargin;
            int iconId = DecideEmotionIcon.decideEmotionIconFromIndex(index, true);

            // �_�O���t�̕`��
            paint.setColor(DecideEmotionIcon.decideEmotionIconColor(iconId));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect((drawX), (bottom - barHeight), (drawX + barWidth), bottom, paint);

            // �A�C�R���̕`�� (�_�O���t�̏�ɕ\������)
            Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), iconId);
            canvas.drawBitmap(bitmap, (drawX + iconMargin), (bottom - barHeight - topMargin), paint);
            
            // �f�[�^���̕\��
            paint.setColor(Color.WHITE);
            canvas.drawText("" + itemCount, drawX + 2, (bottom + textMargin), paint);

            drawX = drawX + barWidth + barMargin;
        }

        // �g�[�^�������̕\��
        int totalCount = dataHolder.getTotalDataCount();
        paint.setColor(Color.WHITE);
        canvas.drawText("Total: " + totalCount, barMargin, 18, paint);
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
