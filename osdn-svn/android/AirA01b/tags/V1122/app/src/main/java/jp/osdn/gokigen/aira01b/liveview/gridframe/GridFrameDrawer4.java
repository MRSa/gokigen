package jp.osdn.gokigen.aira01b.liveview.gridframe;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 *    4x4 のグリッドと対角線の表示
 */
public class GridFrameDrawer4 implements IGridFrameDrawer
{
    /**
     *
     *
     */
    @Override
    public void drawFramingGrid(Canvas canvas, RectF rect, Paint paint)
    {
        float w = (rect.right - rect.left) / 4.0f;
        float h = (rect.bottom - rect.top) / 4.0f;

        canvas.drawLine(rect.left + w, rect.top, rect.left + w, rect.bottom, paint);
        canvas.drawLine(rect.left + 2.0f * w, rect.top, rect.left + 2.0f * w, rect.bottom, paint);
        canvas.drawLine(rect.left + 3.0f * w, rect.top, rect.left + 3.0f * w, rect.bottom, paint);
        canvas.drawLine(rect.left, rect.top + h, rect.right, rect.top + h, paint);
        canvas.drawLine(rect.left, rect.top + 2.0f * h, rect.right, rect.top + 2.0f * h, paint);
        canvas.drawLine(rect.left, rect.top + 3.0f * h, rect.right, rect.top + 3.0f * h, paint);

        canvas.drawLine(rect.left, rect.top, rect.right, rect.bottom, paint);
        canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, paint);

        canvas.drawRect(rect, paint);
    }

    @Override
    public int getDrawColor()
    {
        return (Color.argb(130,235,235,235));
    }
}
