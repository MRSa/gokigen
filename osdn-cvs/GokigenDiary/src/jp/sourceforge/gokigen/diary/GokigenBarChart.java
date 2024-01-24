package jp.sourceforge.gokigen.diary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 *  棒グラフの描画クラス
 * 
 * @author MRSa
 *
 */
public class GokigenBarChart implements IGokigenGraphDrawer
{
	private Context parent = null;

	/**
     *  コンストラクタ
     * 
     */
	public GokigenBarChart(Context arg)
    {
        parent = arg;
    }

	/**
	 *  準備
	 * 
	 */
	public void prepare()
	{
        // 何もしない
	}
	
	/**
	 *  描画メイン処理
	 * 
	 */
    public void drawOnCanvas(Canvas canvas, int reportType, GokigenGraphDataHolder dataHolder)
    {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        // 描画領域のサイズを取得する
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

        // データの種類数から棒グラフの幅を求める
        int nofItems = dataHolder.getDataItemCount();
        float barWidth = (width - (barMargin * (nofItems + 1))) / nofItems;

        // 描画が必要な棒グラフの最大の高さを決める (ある数よりも小さい場合には、同じスケールにする）
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

        // データ1件あたりの高さを求める
        float barUnit = (height - bottomMargin - topMargin) / maxCount;
        
        // 棒グラフの描画処理
        float drawX = barMargin;
        for (int index = 0; index < nofItems; index++)
        {
        	int itemCount =  dataHolder.getDataCount(index);
            float barHeight = (float) itemCount * barUnit;
            float bottom = height - bottomMargin;
            int iconId = DecideEmotionIcon.decideEmotionIconFromIndex(index, true);

            // 棒グラフの描画
            paint.setColor(DecideEmotionIcon.decideEmotionIconColor(iconId));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect((drawX), (bottom - barHeight), (drawX + barWidth), bottom, paint);

            // アイコンの描画 (棒グラフの上に表示する)
            Bitmap bitmap = BitmapFactory.decodeResource(parent.getResources(), iconId);
            canvas.drawBitmap(bitmap, (drawX + iconMargin), (bottom - barHeight - topMargin), paint);
            
            // データ数の表示
            paint.setColor(Color.WHITE);
            canvas.drawText("" + itemCount, drawX + 2, (bottom + textMargin), paint);

            drawX = drawX + barWidth + barMargin;
        }

        // トータル件数の表示
        int totalCount = dataHolder.getTotalDataCount();
        paint.setColor(Color.WHITE);
        canvas.drawText("Total: " + totalCount, barMargin, 18, paint);
    }

	/**
	 *  拡大
	 * 
	 */
    public void actionZoomIn()
    {
        // 何もしない    	
    }

	/**
	 *  縮小
	 * 
	 */
    public void actionZoomOut()
    {
        // 何もしない    	
    }

	/**
	 *  前データ
	 * 
	 */
    public boolean actionShowPreviousData()
    {
        // 何もしない
        return (true);
    }

	/**
	 *  後データ
	 *
	 */
    public boolean actionShowNextData()
    {
        // 何もしない
        return (true);    	
    }
}
