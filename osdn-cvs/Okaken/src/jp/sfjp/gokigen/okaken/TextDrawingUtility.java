package jp.sfjp.gokigen.okaken;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.Log;

public class TextDrawingUtility
{
	private static final int FONT_SIZE_MAX = 128;           // �ő�t�H���g�T�C�Y
    private static final int FONT_SIZE_DEFAULT = 14;    // �W���t�H���g�T�C�Y
    private static final int FONT_SIZE_MIN = 8;             // �ŏ��t�H���g�T�C�Y

	
    /**
     *    �E�[�Ő܂�Ԃ��āA�������\������
     * 
     * @param canvas       Canvas
     * @param message    �\�����镶����
     * @param fontSize     �t�H���g�T�C�Y
     * @param left            �\������ꏊ�̍��[
     * @param top            �\������ꏊ�̏�[
     * @param right          �\������ꏊ�̉E�[
     * @param paint         Paint
     */
    public static void drawTextRegion(Canvas canvas, String message, float left, float top, float right, Paint paint)
    {
        FontMetrics fontMetrics = paint.getFontMetrics();
        //float heightTextMargin = fontMetrics.bottom + fontMetrics.leading - fontMetrics.top + 2.0f;
        float heightTextMargin = fontMetrics.bottom - fontMetrics.top;

        int row = 1;
        int start = 0;
        int length = message.length();
    	int index = 1;
    	float areaX = right - left;
        while (start < length)
        {
        	float width = 0;
        	while ((index < length)&&(width < areaX))
        	{
        		index++;
        		width = paint.measureText(message.substring(start, index));
        	} 
        	if (width > areaX)
        	{
        		index = index - 1;
        	}
        	String outputToShow = message.substring(start, index);
        	canvas.drawText(outputToShow, left, top + (heightTextMargin * row), paint);
        	//Log.v(Gokigen.APP_IDENTIFIER, "MSG: " + outputToShow + " start :" + start +" index :" + index + " width :" + width + " right : " + right + " me :" + paint.measureText(outputToShow));
        	row = row + 1;
        	start = index;
        }
    }

    /**
     *  �w�肵�����ɓ��肫��t�H���g�̍ő�T�C�Y��Ԃ�
     *  
     */
    public static int decideFontSize(String target, int width, int maxFontSize, Paint paint)
    {
    	int startFontSize = (maxFontSize <= 0) ? FONT_SIZE_MAX : (int) maxFontSize;
        for (int fontSize = startFontSize; fontSize > FONT_SIZE_MIN; fontSize = fontSize - 2)
        {
            paint.setTextSize(fontSize);
            if ( width >= paint.measureText(target))
            {
                return fontSize;
            }
        }
        return (FONT_SIZE_DEFAULT);
    }

    /** �w�肵�������ɓ��肫��t�H���g�̍ő�T�C�Y��Ԃ� **/
    public static int decideFontHeightSize(String target, int height, int maxFontSize, Paint paint)
    {
    	int startFontSize = (maxFontSize <= 0) ? FONT_SIZE_MAX : maxFontSize;
        for (int fontSize = startFontSize; fontSize > FONT_SIZE_MIN; fontSize = fontSize - 2)
        {
            paint.setTextSize(fontSize);
            FontMetrics metrics = paint.getFontMetrics();
            float fontHeight = metrics.bottom - metrics.top;
            if (height >= fontHeight)
            {
                return (fontSize);
            }
        }
        return (FONT_SIZE_DEFAULT);
    } 

    /**
     *    �����ƕ����w�肵�āA����ɓ���t�H���g�T�C�Y�����߂�
     * 
     * @param target
     * @param width
     * @param height
     * @param paint
     * @return
     */
    public static int decideFontSize(String target, int width, int height, int maxFontSize, Paint paint)
    {
    	int measureWidth = width;
    	int startFontSize = (maxFontSize <= 0) ? FONT_SIZE_MAX : (int) maxFontSize;
        for (int fontSize = startFontSize; fontSize > FONT_SIZE_MIN; fontSize = fontSize - 2)
        {
        	paint.setTextSize(fontSize);
            FontMetrics metrics = paint.getFontMetrics();
            // int lines = (int) (height / (metrics.bottom - metrics.top));
            int lines = (int) (Math.floor(height / (metrics.bottom - metrics.top)));
            if (lines <= 0)
            {
            	lines = 1;
            }
            measureWidth = width * lines;
            if ( measureWidth >= paint.measureText(target))
            {
                //Log.v(Gokigen.APP_IDENTIFIER, "decideFontSize() w:" + width + " h:" + height + " measure: "  + measureWidth +" msg: " + paint.measureText(target) + "  " + target);
                return (fontSize);
            }
        }
        return (FONT_SIZE_DEFAULT);
    }
}
