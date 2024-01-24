package jp.sourceforge.gokigen.viewsensor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by MRSa on 2015/01/02.
 */
public class SensorInfoDrawer implements GokigenSurfaceView.ICanvasDrawer, GestureDetector.OnGestureListener
{
    private static final String TAG = "SensorInfoDrawer";

    private Context mContext = null;
    private SensorWatcher.IPressureValueHolder mPressureHolder = null;
    private SensorWatcher.IRotationVectorValueHolder mRotationVectorHolder = null;
    private SensorWatcher.ICalibrationExecutor mCalibrator = null;

    private boolean mCalibrationMode = false;
    private boolean mDrawPressureMode = false;
    private float mTextSize = 0.0f;
    private float mDirectionTextSize = 0.0f;

    private float mAltitudeCalibrationValue = 0.0f;
    private String mLabelCalibration = "";

    private long mVibrationDuration = 50; // 50ms

    private GestureDetector mDetector = null;
    private Vibrator mVibrator = null;

    /**
     *
     *
     */
    public SensorInfoDrawer(Context context)
    {
        mContext = context;
        mLabelCalibration = context.getResources().getString(R.string.lbl_calib);
        mTextSize = context.getResources().getDimension(R.dimen.text_size);
        mDirectionTextSize = context.getResources().getDimension(R.dimen.direction_text_size);
        mDetector = new GestureDetector(context, this);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     *
     *
     */
    public void prepare(SensorWatcher.IPressureValueHolder pressure, SensorWatcher.IRotationVectorValueHolder rotationVector, SensorWatcher.ICalibrationExecutor calibrator)
    {
        mPressureHolder = pressure;
        mRotationVectorHolder = rotationVector;
        mCalibrator = calibrator;
    }

    @Override
    public void prepareToStart(int width, int height)
    {

    }

    @Override
    public void changedScreenProperty(int format, int width, int height)
    {

    }

    @Override
    public void drawOnCanvas(Canvas canvas)
    {
        // 画面を半透明色で塗りつぶす
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (mDrawPressureMode == false)
        {
            //  コンパス描画モード
            drawOnCanvasCompass(canvas);
            return;
        }
        if (mCalibrationMode == false)
        {
            // 圧力/高度 表示モード
            drawOnCanvasPressure(canvas);
            return;
        }

        // 高度補正モード
        drawOnCanvasAltitudeCalibrate(canvas);
        return;
    }


    /**
     *  高度補正モード
     *
     */
    private void drawOnCanvasAltitudeCalibrate(Canvas canvas)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float centerX = width / 2.0f;
        float centerY = height / 2.0f;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);
        paint.setTextSize(mTextSize / 2.0f);
        //paint.setStrokeWidth(1.0f);

        // ラベルの表示
        float textWidth = paint.measureText(mLabelCalibration);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontSize = Math.abs(fontMetrics.descent + fontMetrics.ascent) / 2.0f;
        float baseY = mTextSize + fontSize;
        float baseX = (width - textWidth) / 2.0f;
        canvas.drawText(mLabelCalibration, baseX, baseY, paint);

        paint.setTextSize(mTextSize);

        // 高度データの表示
        String altitudeValue = String.format("%.0f", mAltitudeCalibrationValue) + "m";
        textWidth = paint.measureText(altitudeValue);
        fontMetrics = paint.getFontMetrics();
        fontSize = Math.abs(fontMetrics.descent + fontMetrics.ascent) / 2.0f;
        baseY = centerY + fontSize;
        baseX = (width - textWidth) / 2.0f;
        canvas.drawText(altitudeValue, baseX, baseY, paint);

        // 圧力データの表示
        String pressureValue = String.format("%.1f",mPressureHolder.getPressure()) + " hPa";
        float offset = mPressureHolder.getPressureOffset();
        if (offset != 0.0f)
        {
            // 補正値が設定されていた場合は、追加で表示させる
            pressureValue = pressureValue + " (" + String.format("%+.1f", mPressureHolder.getPressureOffset()) + ")";
        }
        drawSupportText(canvas, pressureValue);
    }



    /**
     *   圧力/高度 表示モード
     */
    private void drawOnCanvasPressure(Canvas canvas)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float centerX = width / 2.0f;
        float centerY = height / 2.0f;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(mTextSize);
        //paint.setStrokeWidth(1.0f);


        // 高度データの表示
        String altitudeValue = String.format("%.0f", mPressureHolder.getAltitude(true)) + "m";

        float textWidth = paint.measureText(altitudeValue);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontSize = Math.abs(fontMetrics.descent + fontMetrics.ascent) / 2.0f;
        float baseY = centerY + fontSize;
        float baseX = (width - textWidth) / 2.0f;
        canvas.drawText(altitudeValue, baseX, baseY, paint);

        // 圧力データの表示
        String pressureValue = String.format("%.1f",mPressureHolder.getPressure()) + " hPa";
        drawSupportText(canvas, pressureValue);
    }

    /**
     *   コンパス描画モード
     *
     */
    private void drawOnCanvasCompass(Canvas canvas)
    {

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float centerX = width / 2.0f;
        float centerY = height / 2.0f;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3.0f);

        // 方位磁針の描画（北の方向を指す）
        double rad = mRotationVectorHolder.getNorthAngle();
        float targetX = (float) (Math.sin(-rad) * (double) width) + centerX;
        float targetY = (float) (-Math.cos(-rad) * (double) height) + centerY;
        canvas.drawLine(targetX, targetY, centerX, centerY, paint);

        //  "西" と "東" の表示を追加する
        Bitmap eastBitmap, westBitmap;
        try {
            double eastRad = rad - (3.14159265d * 0.5d);
            double westRad = rad + (3.14159265d * 0.5d);

            Paint bitmapPaint = new Paint();
            eastBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_east);
            westBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_west);
            float bitmapWidth = eastBitmap.getWidth();
            float bitmapHeight = eastBitmap.getHeight();
            float bitmapX = (float) (Math.sin(-eastRad) * (double) (width / 2.0d - bitmapWidth)) + centerX;
            float bitmapY = (float) ((-Math.cos(-eastRad) * (double) (height / 2.0d - bitmapHeight))) + centerY;
            canvas.drawBitmap(eastBitmap, bitmapX, bitmapY, bitmapPaint);

            bitmapX = (float) (Math.sin(-westRad) * (double) (width / 2.0d - bitmapWidth)) + centerX;
            bitmapY = (float) ((-Math.cos(-westRad) * (double) (height/ 2.0d - bitmapHeight))) + centerY;
            canvas.drawBitmap(westBitmap, bitmapX, bitmapY, bitmapPaint);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // 圧力データ
        String pressureValue = String.format("%.1f",mPressureHolder.getPressure()) + " hPa";

        // 高度データ
        String altitudeValue = String.format("%.0f", mPressureHolder.getAltitude(true)) + " m";

        // 角度データ
        //String degreeAngle = String.format("%.1f",Math.toDegrees(rad));

        drawSupportText(canvas, pressureValue + "(" + altitudeValue + ")");
    }

    /**
     *    画面下部に小さく文字表示
     *
     * @param canvas
     * @param showValue
     */
    private void drawSupportText(Canvas canvas, String showValue)
    {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        //paint.setStrokeWidth(1.0f);
        paint.setAntiAlias(true);

        float textWidth = paint.measureText(showValue);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontSize = Math.abs(fontMetrics.descent + fontMetrics.ascent) / 2.0f;
        float baseY = height - fontSize - 5.0f;
        float baseX = (width - textWidth) / 2.0f;
        canvas.drawText(showValue, baseX, baseY, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return (mDetector.onTouchEvent(event));
    }

    // GestureDetector.OnGestureListener
    @Override
    public boolean onDown(MotionEvent e)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onDown() " + "[" + e.getX() +","+ e.getY() + "]");
        }
        return (true);
    }

    // GestureDetector.OnGestureListener
    @Override
    public void onShowPress(MotionEvent e)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onShowPress() ");
        }
    }

    // GestureDetector.OnGestureListener
    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onSingleTapUp() ");
        }
        if (mCalibrationMode == true)
        {
            // 補正モード時には何もしない
            return (false);
        }
        // 表示の切替え (コンパスモードと高度計モード）
        mDrawPressureMode = (mDrawPressureMode == true) ? false : true;
        return (true);
    }

    // GestureDetector.OnGestureListener
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onScroll: " + "[" + e1.getX() + "," + e1.getY() +"]-[" + e2.getX() + "," + e2.getY() +"]" + "[" + distanceX + "," + distanceY+"]" );
        }

        if (mCalibrationMode == false)
        {
            return (false);
        }

        // 補正モード時...
        return (changeCalibrationUpDown(e2.getY() - e1.getY(), 0.0f, 0.20f));
    }

    // GestureDetector.OnGestureListener
    @Override
    public void onLongPress(MotionEvent e)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onLongPress() " );
        }

        if (mDrawPressureMode == true)
        {
            if (mCalibrationMode == true)
            {
                // 高度補正情報をリセットする
                mCalibrationMode = false;
                mPressureHolder.clearPressureOffset();
                mAltitudeCalibrationValue = 0.0f;

                // 補正モードの設定終了を知らせるために振動させる
                if (mVibrator != null)
                {
                    // ブルブルさせる
                    mVibrator.vibrate(mVibrationDuration);
                }
            }
            else
            {
                // 高度設定を speech で入力する (モードに入ったことを知らせるために振動させる)
                if (mCalibrator != null)
                {
                    if (mVibrator != null)
                    {
                        // ブルブルさせる
                        mVibrator.vibrate(mVibrationDuration * 3);
                    }
                    mCalibrator.startCalibrate();
                }
            }
        }

    }

    // GestureDetector.OnGestureListener
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onFling: " + "[" + e1.getX() + "," + e1.getY() +"]-[" + e2.getX() + "," + e2.getY() +"] (" + velocityX + "," + velocityY+ ")");
        }

        boolean isEffect = false;
        if (velocityX < 0.0f)
        {
            if (Math.abs(velocityX) > Math.abs(velocityY))
            {
                // 左へフリック
                isEffect = changeCalibrationMode();
            }
        }

        float moveX = (e2.getX() - e1.getX());
        float moveY = (e2.getY() - e1.getY());
        if ((mCalibrationMode == true)&&(isEffect == false)&&(Math.abs(moveX) < Math.abs(moveY)))
        {
            // 上下へフリック
            isEffect = changeCalibrationUpDown(moveY, velocityY, 1.0f);
        }
        return (isEffect);
    }


    /**
     *
     * @return  補正実施(true) / 補正しない (false)
     */
    private boolean changeCalibrationMode()
    {
        boolean isEffect = false;
        if (mDrawPressureMode == true)
        {
            isEffect = true;
            if (mCalibrationMode == false)
            {
                // 補正モードをONにする
                mCalibrationMode = true;
                mAltitudeCalibrationValue =  mPressureHolder.getAltitude(true);
            }
            else
            {
                // 補正モードを抜ける
                mCalibrationMode = false;
                mPressureHolder.setAltitude(mAltitudeCalibrationValue);
            }

            // 補正モードの設定開始・設定終了を知らせるために振動させる
            if (mVibrator != null)
            {
                // ブルブルさせる
                mVibrator.vibrate(mVibrationDuration);
            }
        }
        return (isEffect);
    }

    /**
     *   キャリブレーションデータの調整
     *
     * @param distance
     * @param velocityY
     * @param sensitivity
     * @return
     */
    private boolean changeCalibrationUpDown(float distance, float velocityY, float sensitivity)
    {
        float velocity = Math.abs(velocityY);
        float data =  (distance < 0.0f) ? -sensitivity : sensitivity;
        if (velocity > 1000.0f)
        {
            data = data * 5.0f;
        }
        mAltitudeCalibrationValue = mAltitudeCalibrationValue + data;

        return (true);
    }
}
