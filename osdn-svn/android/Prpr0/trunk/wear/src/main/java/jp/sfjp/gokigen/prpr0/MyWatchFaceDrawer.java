package jp.sfjp.gokigen.prpr0;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.format.Time;
import android.util.Log;

import java.util.Locale;

/**
 * 時計の盤面を描画する
 * Created by MRSa on 2014/12/20.
 */
class MyWatchFaceDrawer
{
    private static final String TAG = "MyWatchFaceDrawer";
    private MyWatchFaceHolder mHolder = null;
    private boolean isShowSeconds = false;

    /**
     *   コンストラクタ
     *
     */
    MyWatchFaceDrawer(MyWatchFaceHolder holder)
    {
        mHolder = holder;
    }

    /**
     *   初期化
     *
     */
    void initialize(boolean isShowSeconds)
    {
        this.isShowSeconds = isShowSeconds;
    }

    /**
     *   時計の描画
     * @param canvas      描画するキャンバス
     * @param bounds      描画領域の大きさ
     * @param timeToDraw 描画する時刻
     */
    void doDraw(Canvas canvas, Rect bounds, Time timeToDraw)
    {
        if (mHolder == null)
        {
            // 情報保持オブジェクトがない場合はログ表示で終了
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "doDraw: data holder is null.");
            }
            return;
        }
        int width = bounds.width();
        int height = bounds.height();

        // 背景画像の描画
        Bitmap backgroundBitmap = mHolder.getBackgroundScaledBitmap(width, height);
        if (backgroundBitmap != null)
        {
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }

        String timeString;
        if (isShowSeconds)
        {
            // 現在時刻を hh:mm.ss 形式にする
            timeString = formatTwoDigitNumber(timeToDraw.hour) + MyWatchFaceHolder.TIME_SEPARATOR + formatTwoDigitNumber(timeToDraw.minute) + MyWatchFaceHolder.SECOND_SEPARATOR + formatTwoDigitNumber(timeToDraw.second);
        }
        else
        {
            // 現在時刻を hh:mm 形式にする
            timeString = formatTwoDigitNumber(timeToDraw.hour) + MyWatchFaceHolder.TIME_SEPARATOR + formatTwoDigitNumber(timeToDraw.minute);
        }

        // 時刻の表示位置を決める
        Paint textPaint = mHolder.getTimePaint();
        float textWidth = textPaint.measureText(timeString);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontSize = Math.abs(fontMetrics.descent + fontMetrics.ascent) / 2.0f;
        float baseY = height - fontSize - mHolder.getYOffset();
        float baseX = (width - textWidth) / 2.0f;

        // 背景の描画(半透明にする)
        canvas.drawRoundRect((baseX - 5.0f), baseY + fontMetrics.ascent, (baseX + textWidth +5.0f), (baseY + fontMetrics.descent), 10.0f, 10.0f, mHolder.getBackPaint());

        // 時刻の描画
        canvas.drawText(timeString, baseX, baseY, textPaint);
    }

    /**
     *   数字を二桁の数値に揃える
     *
     * @param data 数値
     * @return  string data
     */
    private String formatTwoDigitNumber(int data)
    {
        return String.format(Locale.ENGLISH, "%02d", data);
    }
}
