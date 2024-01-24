package jp.osdn.gokigen.aira01b.liveview;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraStatusListener;
import jp.osdn.gokigen.aira01b.R;
import jp.osdn.gokigen.aira01b.olycamerawrapper.IOlyCameraProperty;
import jp.osdn.gokigen.aira01b.olycamerawrapper.IOlyCameraPropertyProvider;

/**
 *   カメラのインジケータ表示
 *
 */
class CameraControlPanel implements  ICameraPanelDrawer, OLYCameraStatusListener, View.OnClickListener, View.OnTouchListener, View.OnLongClickListener, GestureDetector.OnGestureListener
{
    private static final float MARGIN_FULL = 4.0f;
    private static final float MARGIN_HALF = 2.0f;
    private static final float MARGIN_ROUND = 12.0f;
    private static final float VEROCITY_THRESHOLD = 1.4f;
    private static final float MATRIX_X = 5.0f;
    private static final float MATRIX_Y = 8.0f;
    private static final int PANELAREA_LOWER = 0;
    private static final int PANELAREA_UPPER = 1;



    private String information = "";

    private int value = 0;
    private int currentColor = 0;
    private String warning;
    private String actualAperture;
    private String actualShutter;
    private String actualIso;
    private float actualFocal;
    private Map<String, String> cameraValues = null;
    private List<String> propertyValueList = null;
    private int currentPropertyIndex = -1;
    private int defaultPropertyIndex = -1;
    private int panelArea = PANELAREA_LOWER;
    private final View parent;
    private final IOlyCameraPropertyProvider provider;
    private final String TAG = toString();
    //private final GestureDetector gestureDetector;

    CameraControlPanel(View parent, IOlyCameraPropertyProvider provider)
    {
        this.parent = parent;
        this.provider = provider;
        //this.gestureDetector = new GestureDetector(parent.getContext(), this);
    }

    @Override
    public void onClick(View v)
    {
        Log.v(TAG, "onClick()  ");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        boolean ret = true;
        Log.v(TAG, "onTouch() : [" + event.getX() + "," + event.getY() + "] ");

        if (event.getActionMasked() == MotionEvent.ACTION_UP)
        {
            Log.v(TAG, "onTouch() UP : [" + event.getX() + "," + event.getY() + "] ");
            if ((information != null)&&(propertyValueList != null)&&(currentPropertyIndex != defaultPropertyIndex))
            {
                // 最初の値とインデックスが動いていたら、プロパティを更新する
                Log.v(TAG, "UPDATE PROPERTY : " + defaultPropertyIndex + " -> " + currentPropertyIndex);
                try
                {
                    updateProperty(information, propertyValueList.get(currentPropertyIndex));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            // 対象の情報をクリアする
            information = "";
            propertyValueList = null;
            currentPropertyIndex = -1;
            defaultPropertyIndex = -1;
        }
        else if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
        {
            Log.v(TAG, "onTouch() MOVE : [" + event.getX() + "," + event.getY() + "] ");
            int history = event.getHistorySize() - 1;
            if (history > 0)
            {
                onFling(event, event, event.getX() - event.getHistoricalX(history), event.getY() - event.getHistoricalY(history));  // 逆な気がするが...
                //onFling(event, event, event.getHistoricalX(history) - event.getX(), event.getHistoricalY(history) - event.getY());
            }
        }
        else if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            Log.v(TAG, "onTouch() DOWN : [" + event.getX() + "," + event.getY() + "] ");
            onDown(event);
        }
        else
        {
            ret = false;
        }
        return (ret);
        //return (gestureDetector.onTouchEvent(event));
    }

    @Override
    public boolean onLongClick(View v)
    {
        Log.v(TAG, "onLongClick()  ");
        return (false);
    }

    @Override
    public void drawCameraPanel(Canvas canvas)
    {
        Log.v(TAG, "drawCameraPanel()");

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), paint);

        float height_unit = canvas.getHeight() / MATRIX_Y;
        float width_unit = canvas.getWidth() / MATRIX_X;    // ズーム入れたら6にしたいが...
        float half_unit = width_unit / 2.0f;
        if (height_unit < width_unit)
        {
            half_unit = height_unit / 2.0f;
        }

        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.LTGRAY);
        RectF takeModeRect = new RectF(MARGIN_HALF, MARGIN_HALF, (width_unit * 1.0f) - MARGIN_FULL, (height_unit * 3.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(takeModeRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        RectF shutterRect = new RectF((width_unit * 1.0f) + MARGIN_HALF, MARGIN_HALF, (width_unit * 3.0f) - MARGIN_FULL, (height_unit * 3.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(shutterRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        RectF apertureRect = new RectF((width_unit * 3.0f) + MARGIN_HALF, MARGIN_HALF, (width_unit * 5.0f) - MARGIN_FULL, (height_unit * 3.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(apertureRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);


        RectF isoRect = new RectF((width_unit * 0.0f) + MARGIN_HALF, (height_unit * 3.0f) + MARGIN_HALF, (width_unit * 2.0f) - MARGIN_FULL, (height_unit * 5.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(isoRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        RectF expRect = new RectF((width_unit * 2.0f) + MARGIN_HALF, (height_unit * 3.0f) + MARGIN_HALF, (width_unit * 4.0f) - MARGIN_FULL, (height_unit * 5.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(expRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        RectF aeRect = new RectF((width_unit * 4.0f) + MARGIN_HALF, (height_unit * 3.0f) + MARGIN_HALF, (width_unit * 5.0f) - MARGIN_FULL, (height_unit * 5.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(aeRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        RectF wbRect = new RectF((width_unit * 0.0f) + MARGIN_HALF, (height_unit * 5.0f) + MARGIN_HALF, (width_unit * 2.0f) - MARGIN_FULL, (height_unit * 7.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(wbRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        RectF filterRect = new RectF((width_unit * 2.0f) + MARGIN_HALF, (height_unit * 5.0f) + MARGIN_HALF, (width_unit * 4.0f) - MARGIN_FULL, (height_unit * 7.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(filterRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        RectF driveRect = new RectF((width_unit * 4.0f) + MARGIN_HALF, (height_unit * 5.0f) + MARGIN_HALF, (width_unit * 5.0f) - MARGIN_FULL, (height_unit * 7.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(driveRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);


        RectF focalRect = new RectF((width_unit * 0.3f) + MARGIN_HALF, (height_unit * 7.0f) + MARGIN_HALF, (width_unit * 2.0f) - MARGIN_FULL, (height_unit * 8.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(focalRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        RectF warnRect = new RectF((width_unit * 2.0f) + MARGIN_HALF, (height_unit * 7.0f) + MARGIN_HALF, (width_unit * 4.0f) - MARGIN_FULL, (height_unit * 8.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(warnRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        RectF battRect = new RectF((width_unit * 4.0f) + MARGIN_HALF, (height_unit * 7.0f) + MARGIN_HALF, (width_unit * 5.0f) - MARGIN_FULL, (height_unit * 8.0f) - MARGIN_FULL);
        //canvas.drawRoundRect(battRect, MARGIN_ROUND, MARGIN_ROUND, rectPaint);

        int drawColor = Color.LTGRAY;
        if (cameraValues != null)
        {
            String takeMode = provider.getCameraPropertyValueTitle(cameraValues.get(IOlyCameraProperty.TAKE_MODE));
            drawString(canvas, takeModeRect, takeMode, drawColor);

            String shutter = provider.getCameraPropertyValueTitle(actualShutter);
            drawString(canvas, shutterRect, shutter, drawColor);

            String aperture = provider.getCameraPropertyValueTitle(actualAperture);
            if (aperture != null)
            {
                drawString(canvas, apertureRect, "F" + aperture, drawColor);
            }

            String iso = provider.getCameraPropertyValueTitle(cameraValues.get(IOlyCameraProperty.ISO_SENSITIVITY));
            if (iso != null)
            {
                switch (iso)
                {
                    case "Auto":
                        if (actualIso != null)
                        {
                            iso = "iso" + provider.getCameraPropertyValueTitle(actualIso);
                        }
                        else
                        {
                            iso = "iso-A";
                        }
                        break;

                    case "null":
                        iso = "";
                        break;

                    default:
                        iso = "ISO" + iso;
                        break;
                }
                drawString(canvas, isoRect, iso, drawColor);
            }

            String exp = provider.getCameraPropertyValueTitle(cameraValues.get(IOlyCameraProperty.EXPOSURE_COMPENSATION));
            drawString(canvas, expRect, exp, drawColor);

            String colorTone = provider.getCameraPropertyValueTitle(cameraValues.get(IOlyCameraProperty.COLOR_TONE));
            drawString(canvas, filterRect, colorTone, drawColor);

            //String drive = provider.getCameraPropertyValueTitle(cameraValues.get(IOlyCameraProperty.DRIVE_MODE));
            //drawString(canvas, driveRect, drive, drawColor);
            drawDriveModeStatus(canvas, driveRect, cameraValues.get(IOlyCameraProperty.DRIVE_MODE));

            //String ae_Mode = provider.getCameraPropertyValueTitle(cameraValues.get(IOlyCameraProperty.AE_MODE));
            //drawString(canvas, aeRect, ae_Mode, drawColor);
            drawAeModeStatus(canvas, aeRect, cameraValues.get(IOlyCameraProperty.AE_MODE));

            String wb_Mode = provider.getCameraPropertyValueTitle(cameraValues.get(IOlyCameraProperty.WB_MODE));
            drawString(canvas, wbRect, wb_Mode, drawColor);

            //String battery = provider.getCameraPropertyValueTitle(cameraValues.get(IOlyCameraProperty.BATTERY_LEVEL));
            //drawString(canvas, battRect, battery, drawColor);
            drawBatteryStatus(canvas, battRect, cameraValues.get(IOlyCameraProperty.BATTERY_LEVEL));

            String focalLength = String.format(Locale.ENGLISH, "%2.1fmm", actualFocal);
            drawString(canvas, focalRect, focalLength, drawColor);
        }

        ///////  (警告があったら)警告の表示  /////
        if (warning != null)
        {
            drawString(canvas, warnRect, warning, Color.argb(0xff, 0xDA, 0xA5, 0x20));
        }

        ///// 操作中パネル表示 /////
        if ((information != null)&&(information.length() > 0))
        {
            drawConsolePanel(canvas, width_unit, height_unit);
        }

        ///// 動作インジケータ(5回に1回、色を更新) /////
        if (value  % 5 == 0)
        {
            if (currentColor == Color.LTGRAY)
            {
                currentColor = Color.DKGRAY;
            }
            else
            {
                currentColor = Color.LTGRAY;
            }
        }
        rectPaint.setColor(currentColor);
        float radius = half_unit - MARGIN_ROUND;
        canvas.drawCircle((0.0f + radius + MARGIN_HALF), (canvas.getHeight() - radius - MARGIN_HALF), radius, rectPaint);
        value++;
    }

    /**
     *
     *
     *
     */
    private void drawConsolePanel(Canvas canvas, float unit_x, float unit_y)
    {
        String output = information;
        RectF areaRect, outputRect, leftRect, centerRect, rightRect;

        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        if (panelArea == PANELAREA_UPPER)
        {
            //canvas.drawRect((unit_x * 0.1f),  (unit_y * 0.8f), (unit_x * 4.81f), (unit_y * 2.95f), paint);
            areaRect = new RectF((unit_x * 0.1f),  (unit_y * 0.7f), (unit_x * 4.81f), (unit_y * 2.95f));
            canvas.drawRoundRect(areaRect, MARGIN_ROUND, MARGIN_ROUND, paint);
            outputRect = new RectF((unit_x * 0.1f) + MARGIN_HALF, (unit_y * 1.5f) + MARGIN_HALF, (unit_x * 0.9f) - MARGIN_FULL, (unit_y * 2.8f) - MARGIN_FULL);
            leftRect = new RectF((unit_x * 1.0f) + MARGIN_HALF, (unit_y * 1.9f) + MARGIN_HALF, (unit_x * 1.8f) - MARGIN_FULL, (unit_y * 2.8f) - MARGIN_FULL);
            centerRect = new RectF((unit_x * 2.0f) + MARGIN_HALF, (unit_y * 0.8f) + MARGIN_HALF, (unit_x * 3.9f) - MARGIN_FULL, (unit_y * 2.8f) - MARGIN_FULL);
            rightRect = new RectF((unit_x * 4.0f) + MARGIN_HALF, (unit_y * 1.9f) + MARGIN_HALF, (unit_x * 4.8f) - MARGIN_FULL, (unit_y * 2.8f) - MARGIN_FULL);

            // 設定対象値を示す線を引く
            paint.setColor(Color.BLACK);
            canvas.drawLine((unit_x * 2.0f) + MARGIN_HALF, (unit_y * 2.8f), (unit_x * 3.9f) - MARGIN_FULL, (unit_y * 2.8f), paint);
        }
        else   // PANELAREA_LOWER
        {
            //canvas.drawRect((unit_x * 0.1f),  (unit_y * 5.8f), (unit_x * 4.8f), (unit_y * 7.95f), paint);
            areaRect = new RectF((unit_x * 0.1f),  (unit_y * 5.7f), (unit_x * 4.8f), (unit_y * 7.95f));
            canvas.drawRoundRect(areaRect, MARGIN_ROUND, MARGIN_ROUND, paint);
            outputRect = new RectF((unit_x * 0.1f) + MARGIN_HALF, (unit_y * 6.5f) + MARGIN_HALF, (unit_x * 0.9f) - MARGIN_FULL, (unit_y * 7.8f) - MARGIN_FULL);
            leftRect = new RectF((unit_x * 1.0f) + MARGIN_HALF, (unit_y * 6.9f) + MARGIN_HALF, (unit_x * 1.8f) - MARGIN_FULL, (unit_y * 7.8f) - MARGIN_FULL);
            centerRect = new RectF((unit_x * 2.0f) + MARGIN_HALF, (unit_y * 5.8f) + MARGIN_HALF, (unit_x * 3.9f) - MARGIN_FULL, (unit_y * 7.8f) - MARGIN_FULL);
            rightRect = new RectF((unit_x * 4.0f) + MARGIN_HALF, (unit_y * 6.9f) + MARGIN_HALF, (unit_x * 4.8f) - MARGIN_FULL, (unit_y * 7.8f) - MARGIN_FULL);

            // 設定対象値を示す線を引く
            paint.setColor(Color.BLACK);
            canvas.drawLine((unit_x * 2.0f) + MARGIN_HALF, (unit_y * 7.8f), (unit_x * 3.9f) - MARGIN_FULL, (unit_y * 7.8f), paint);
        }

        //  選択中のカメラプロパティ名を表示する
        int outputColor = Color.BLACK;
        drawString(canvas, outputRect, output, outputColor);

        if ((propertyValueList != null)&&(propertyValueList.size() > 0))
        {
            String previous ="";
            int previousColor = Color.DKGRAY;
            if (currentPropertyIndex > 0)
            {
                int indexToSet = currentPropertyIndex - 1;
                previous = provider.getCameraPropertyValueTitle(propertyValueList.get(indexToSet));
            }
            int centerColor = Color.BLACK;
            String center = provider.getCameraPropertyValueTitle(propertyValueList.get(currentPropertyIndex));

            String next = "";
            int nextColor = Color.DKGRAY;
            if ((currentPropertyIndex + 1) < propertyValueList.size())
            {
                int indexToSet = currentPropertyIndex + 1;
                next = provider.getCameraPropertyValueTitle(propertyValueList.get(indexToSet));
            }
            drawString(canvas, leftRect, previous, previousColor);
            drawString(canvas, centerRect, center, centerColor);
            drawString(canvas, rightRect, next, nextColor);
        }
    }

    /**
     *
     */
    private  void updateProperty(final String name, final String value)
    {
        try
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        provider.setCameraPropertyValue(name, value);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     *   指定された枠にバッテリー状態を表示する
     *
     *
     */
    private void drawBatteryStatus(Canvas canvas, RectF region, String value)
    {
        if ((canvas == null)||(region == null)||(value == null))
        {
            // 何もせずに折り返す
            return;
        }
        int id;
        switch (value)
        {
            case "<BATTERY_LEVEL/CHARGE>":
                id = R.drawable.tt_icn_battery_charge;
                break;
            case "<BATTERY_LEVEL/EMPTY>":
                id = R.drawable.tt_icn_battery_empty;
                break;
            case "<BATTERY_LEVEL/WARNING>":
                id = R.drawable.tt_icn_battery_half;
                break;
            case "<BATTERY_LEVEL/LOW>":
                id = R.drawable.tt_icn_battery_middle;
                break;
            case "<BATTERY_LEVEL/FULL>":
                id = R.drawable.tt_icn_battery_full;
                break;
            case "<BATTERY_LEVEL/EMPTY_AC>":
                id = R.drawable.tt_icn_battery_supply_empty;
                break;
            case "<BATTERY_LEVEL/SUPPLY_WARNING>":
                id = R.drawable.tt_icn_battery_supply_half;
                break;
            case "<BATTERY_LEVEL/SUPPLY_LOW>":
                id = R.drawable.tt_icn_battery_supply_middle;
                break;
            case "<BATTERY_LEVEL/SUPPLY_FULL>":
                id = R.drawable.tt_icn_battery_supply_full;
                break;
            case "<BATTERY_LEVEL/UNKNOWN>":
            default:
                Log.v(TAG, "BATT: " + value);
                id = R.drawable.tt_icn_battery_unknown;
                break;
        }
        canvas.drawBitmap(BitmapFactory.decodeResource(parent.getResources(), id), null, region, new Paint());
    }


    /**
     *   resion内に文字を表示する
     *
     */
    private void drawString(Canvas canvas, RectF region, String target, int color)
    {
        if ((target == null)||(target.length() <= 0))
        {
            return;
        }

        Paint textPaint = new Paint();
        textPaint.setColor(color);
        textPaint.setAntiAlias(true);

        float maxWidth = region.width() - (MARGIN_FULL * 2);
        float textSize = region.height() - (MARGIN_FULL * 2);
        textPaint.setTextSize(textSize);
        float textWidth = textPaint.measureText(target);
        while (maxWidth < textWidth)
        {
            // テキストサイズが横幅からあふれるまでループ
            textPaint.setTextSize(--textSize);
            textWidth = textPaint.measureText(target);
        }

        // センタリングするための幅を取得
        float margin = (region.width() - textWidth) / 2.0f;

        // 文字を表示する
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        canvas.drawText(target, (region.left + margin), region.bottom - fontMetrics.descent, textPaint);
        //canvas.drawText(target, (region.left + MARGIN_FULL), region.bottom - fontMetrics.bottom, textPaint);
        //canvas.drawText(target, (region.left + MARGIN_FULL), region.bottom - fontMetrics.descent, textPaint);
        //canvas.drawText(target, (region.left + MARGIN_FULL), region.top + (fontMetrics.descent - fontMetrics.ascent), textPaint);
     }


    /**
     *   指定された枠にドライブモードを表示する
     *
     *
     */
    private void drawDriveModeStatus(Canvas canvas, RectF region, String value)
    {
        if ((canvas == null)||(region == null)||(value == null))
        {
            // 何もせずに折り返す
            return;
        }
        int id;
        switch (value)
        {
            case "<TAKE_DRIVE/DRIVE_NORMAL>":
                id = R.drawable.rm_icn_drive_setting_single;
                break;
            case "<TAKE_DRIVE/DRIVE_CONTINUE>":
                id = R.drawable.rm_icn_drive_setting_seq_l;
                break;

            default:
                Log.v(TAG, ": " + value);
                id = R.drawable.ic_warning_white_24dp;
                break;
        }
        canvas.drawBitmap(BitmapFactory.decodeResource(parent.getResources(), id), null, region, new Paint());
    }


    /**
     *   指定された枠に測光モードを表示する
     *
     *
     */
    private void drawAeModeStatus(Canvas canvas, RectF region, String value)
    {
        if ((canvas == null)||(region == null)||(value == null))
        {
            // 何もせずに折り返す
            return;
        }
        int id;
        switch (value)
        {
            case "<AE/AE_CENTER>":
                id = R.drawable.ic_center_focus_weak_white_24dp;
                break;
            case "<AE/AE_ESP>":
                id = R.drawable.ic_crop_free_white_24dp;
                break;
            case "<AE/AE_PINPOINT>":
                id = R.drawable.ic_center_focus_strong_white_24dp;
                break;

            default:
                Log.v(TAG, ": " + value);
                id = R.drawable.ic_warning_white_24dp;
                break;
        }
        canvas.drawBitmap(BitmapFactory.decodeResource(parent.getResources(), id), null, region, new Paint());
    }


    @Override
    public void onUpdateStatus(OLYCamera olyCamera, String s)
    {
        Log.v(TAG, "onUpdateStatus() : " + s);
        warning = "";
        try
        {
            // 警告メッセージを生成
            if (olyCamera.isMediaError())
            {
                warning = warning + " " +  parent.getContext().getString(R.string.media_error);
            }
            if (olyCamera.isMediaBusy())
            {
                warning = warning + " " +  parent.getContext().getString(R.string.media_busy);
            }
            if (olyCamera.isHighTemperatureWarning())
            {
                warning = warning + " " +  parent.getContext().getString(R.string.high_temperature_warning);
            }
            if ((olyCamera.isExposureMeteringWarning())||(olyCamera.isExposureWarning()))
            {
                warning = warning + " " + parent.getContext().getString(R.string.exposure_metering_warning);
            }
            if (olyCamera.isActualIsoSensitivityWarning())
            {
                warning = warning + " " + parent.getContext().getString(R.string.iso_sensitivity_warning);
            }

            TreeSet<String> treeSet = new TreeSet<>();
            treeSet.add(IOlyCameraProperty.TAKE_MODE);
            treeSet.add(IOlyCameraProperty.WB_MODE);
            treeSet.add(IOlyCameraProperty.AE_MODE);
            treeSet.add(IOlyCameraProperty.APERTURE);
            treeSet.add(IOlyCameraProperty.COLOR_TONE);
            treeSet.add(IOlyCameraProperty.SHUTTER_SPEED);
            treeSet.add(IOlyCameraProperty.ISO_SENSITIVITY);
            treeSet.add(IOlyCameraProperty.EXPOSURE_COMPENSATION);
            treeSet.add(IOlyCameraProperty.BATTERY_LEVEL);
            treeSet.add(IOlyCameraProperty.DRIVE_MODE);

            cameraValues = olyCamera.getCameraPropertyValues(treeSet);
            actualShutter = olyCamera.getActualShutterSpeed();
            actualAperture = olyCamera.getActualApertureValue();
            actualIso = olyCamera.getActualIsoSensitivity();
            actualFocal = olyCamera.getActualFocalLength();

/*
            //for (Map.Entry<String, String> entry : values.entrySet())
            //{
            //    Log.v(TAG, "STATUS : " + entry.getKey() + " : " + entry.getValue());
            //}
            String takeMode = olyCamera.getCameraPropertyValueTitle(values.get(IOlyCameraProperty.TAKE_MODE));
            String wbMode = olyCamera.getCameraPropertyValueTitle(values.get(IOlyCameraProperty.WB_MODE));
            String aeMode = olyCamera.getCameraPropertyValueTitle(values.get(IOlyCameraProperty.AE_MODE));
            String aperture = olyCamera.getCameraPropertyValueTitle(olyCamera.getActualApertureValue());
            String iso = olyCamera.getCameraPropertyValueTitle(olyCamera.getActualIsoSensitivity());
            String shutter = olyCamera.getCameraPropertyValueTitle(olyCamera.getActualShutterSpeed());
            message = "  " + takeMode + " " + shutter + " F" + aperture + " ISO" + iso + " " + wbMode + " [" + aeMode + "] " + warn;
*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        parent.postInvalidate();
    }

    /**
     *   タッチしたエリアがどのプロパティなのかを応答する
     *
     */
    private String checkArea(MotionEvent e)
    {
        String property;
        float unitX = parent.getWidth() / MATRIX_X;
        float unitY = parent.getHeight() / MATRIX_Y;

        float x = e.getX();
        float y = e.getY();

        int areaX = (int) Math.floor(x / unitX);
        int areaY = (int) Math.floor(y / unitY);

        panelArea = PANELAREA_LOWER;
        if ((areaX < 1)&&(areaY < 3))
        {
            property = IOlyCameraProperty.TAKE_MODE;
        }
        else if ((areaX < 3)&&(areaY < 3))
        {
            property = IOlyCameraProperty.SHUTTER_SPEED;
        }
        else if ((areaX < 5)&&(areaY < 3))
        {
            property = IOlyCameraProperty.APERTURE;
        }
        else if ((areaX < 2)&&(areaY < 5))
        {
            property = IOlyCameraProperty.ISO_SENSITIVITY;
        }
        else if ((areaX < 4)&&(areaY < 5))
        {
            property = IOlyCameraProperty.EXPOSURE_COMPENSATION;
        }
        else if ((areaX < 5)&&(areaY < 5))
        {
            property = IOlyCameraProperty.AE_MODE;
        }
        else if ((areaX < 2)&&(areaY < 7))
        {
            property = IOlyCameraProperty.WB_MODE;
            panelArea = PANELAREA_UPPER;
        }
        else if ((areaX < 4)&&(areaY < 7))
        {
            property = IOlyCameraProperty.COLOR_TONE;
            panelArea = PANELAREA_UPPER;
        }
        else if ((areaX < 5)&&(areaY < 7))
        {
            property = IOlyCameraProperty.DRIVE_MODE;
            panelArea = PANELAREA_UPPER;
        }
        else
        {
            property = "";
        }
        Log.v(TAG, "[" + areaX + "," + areaY + "]");

        // カメラプロパティを読み出して設定
        if (property.length() > 0)
        {
            try
            {
                if (provider.canSetCameraProperty(property))
                {
                    propertyValueList = provider.getCameraPropertyValueList(property);
                    if (propertyValueList != null)
                    {
                        currentPropertyIndex = propertyValueList.indexOf(provider.getCameraPropertyValue(property));
                        defaultPropertyIndex = currentPropertyIndex;
                    }
                }
                else
                {
                    propertyValueList = null;
                    currentPropertyIndex = -1;
                    defaultPropertyIndex = -1;
                    property = "";
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                propertyValueList = null;
                currentPropertyIndex = -1;
                defaultPropertyIndex = -1;
                property = "";
            }
        }
        //property = property + " [" + areaX + "," + areaY + "]";
        return (property);
    }


    @Override
    public boolean onDown(MotionEvent event)
    {
        Log.v(TAG, "onDown() ");
        if (parent == null)
        {
            return (false);
        }
        information = checkArea(event);
        return (false);
    }

    @Override
    public void onShowPress(MotionEvent e)
    {
        Log.v(TAG, "onShowPress()  ");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        return (false);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e)
    {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        Log.v(TAG, "onFling()  [" + velocityX + "," + velocityY + "]");
        try
        {
            int direction = 0;
            if ((propertyValueList != null)&&(currentPropertyIndex != -1)&&(Math.abs(velocityX) > VEROCITY_THRESHOLD))
            {
                if (velocityX < 0)
                {
                    Log.v(TAG, "onFling()  DOWN");
                    if (currentPropertyIndex > 0)
                    {
                        // ひとつ小さくする
                        direction = -1;
                    }
                }
                else if (velocityX > 0)
                {
                    Log.v(TAG, "onFling()  UP");
                    if (currentPropertyIndex < (propertyValueList.size() - 1))
                    {
                        // ひとつおおきくする
                        direction = +1;
                    }
                }
                currentPropertyIndex = currentPropertyIndex + direction;
                parent.postInvalidate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (true);
    }
}
