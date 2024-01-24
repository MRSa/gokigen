package jp.sfjp.gokigen.prpr0;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

/**
 *   時計描画用データの保持クラス
 *
 * Created by MRSa on 2014/12/20.
 */
public class MyWatchFaceHolder
{
    private static final String TAG = "MyWatchFaceHolder";
    private static final Typeface BOLD_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
    private static final Typeface NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    public static final String TIME_SEPARATOR = ":";
    public static final String DATE_SEPARATOR = "-";

    /* device features */
    private boolean mLowBitAmbient = false;
    private boolean mBurnInProtection = false;
    private boolean mIsRoundShape = false;

    /* graphic objects */
    private int nofBitmaps = 0;
    private int currentBackgroundBitmap = 0;
    private Hashtable<Integer, Bitmap> mBackgroundBitmaps = null;
    //private Hashtable<Integer, Bitmap> mBackgroundScaledBitmaps = null;

    /* 文字表示用の情報 */
    private float mTextSize = 0;
    private float mYOffset = 0;

    private Paint mTimePaint = null;
    private Paint mBackPaint = null;

    private ExternalStorageFileUtility fileUtility = null;

    /**
     *  コンストラクタ
     */
    public MyWatchFaceHolder(Context context)
    {
        //  ファイルユーティリティの作成
        fileUtility = new ExternalStorageFileUtility("/prpr");
        fileUtility.makeDirectory("/all");

        initialize(context);
    }

    /**
     *  初期化処理
     *
     * @return true : success, false : failure
     */
    private boolean initialize(Context context)
    {
        initializeGraphicObjects(context);
        initializeDrawTextObjects(context);
        //initializePeriodicTimer();
        //initializeTimeZoneChangeReceiver();
        return (true);
    }

    public void onPropertiesChanged(Bundle properties)
    {
        //mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        //mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
    }

    public boolean setAmbientMode(boolean inAmbientMode, boolean previousInAmbientMode)
    {
        if (inAmbientMode == previousInAmbientMode)
        {
            return (false);
        }
        return (true);
    }

    /**
     *   終了時処理
     *
     */
    public void dispose()
    {
        mBackgroundBitmaps.clear();
        mBackgroundBitmaps = null;
        System.gc();
    }

    /**
     *
     *
     * @param isRound
     */
    public void setIsRound(boolean isRound, Resources resources)
    {
        if (mIsRoundShape == isRound)
        {
            return;
        }
        /** update watch face shape **/
        mIsRoundShape = isRound;
        mTextSize = resources.getDimension(isRound ? R.dimen.text_size_round : R.dimen.text_size);
    }

    /**
     *
     * @return
     */
    public Paint getTimePaint()
    {
        mTimePaint.setTextSize(mTextSize);
        return (mTimePaint);
    }

    /**
     *
     * @return
     */
    public Paint getBackPaint()
    {
        return (mBackPaint);
    }

    /**
     *
     * @return
     */
    public float getYOffset()
    {
        return (mYOffset);
    }

    /**
     *
     */
    private void initializeGraphicObjects(Context context)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "initializeGraphicObjects " + Integer.toString(currentBackgroundBitmap));
        }
        mBackgroundBitmaps = null;
        System.gc();

        mBackgroundBitmaps = new Hashtable<Integer, Bitmap>();
        mBackgroundBitmaps.clear();

        //mBackgroundScaledBitmaps = new Hashtable<Integer, Bitmap>();
        //mBackgroundScaledBitmaps.clear();

        /** 表示画像の初期化 **/
        currentBackgroundBitmap = 0;

        /** ファイルからビットマップを展開する **/
        int nofImages = extractBitmaps();
        if (Log.isLoggable(TAG, Log.INFO))
        {
            Log.i(TAG, "read Images : " + Integer.toString(nofImages));
        }
        if (nofImages > 0)
        {
            // イメージが１つでも送り込まれていた場合、デフォルトの画像は表示しない
            return;
        }

        /** 背景画像（デフォルト）の設定 **/
/**/
        setBackgroundBitmap(context, R.drawable.background);
        //setBackgroundBitmap(context, R.drawable.background1);
        setBackgroundBitmap(context, R.drawable.background2);
        //setBackgroundBitmap(context, R.drawable.background3);
        setBackgroundBitmap(context, R.drawable.background4);
        setBackgroundBitmap(context, R.drawable.background5);
        setBackgroundBitmap(context, R.drawable.background6);
/**/
/**
        setBackgroundBitmap(context, R.drawable.aoi01);
        setBackgroundBitmap(context, R.drawable.aoi02);
        setBackgroundBitmap(context, R.drawable.aoi03);
        setBackgroundBitmap(context, R.drawable.aoi04);
        setBackgroundBitmap(context, R.drawable.aoi05);
        setBackgroundBitmap(context, R.drawable.aoi06);
        setBackgroundBitmap(context, R.drawable.aoi07);
        setBackgroundBitmap(context, R.drawable.aoi08);
        setBackgroundBitmap(context, R.drawable.aoi09);
        setBackgroundBitmap(context, R.drawable.aoi10);
        setBackgroundBitmap(context, R.drawable.aoi11);
        setBackgroundBitmap(context, R.drawable.aoi12);
        setBackgroundBitmap(context, R.drawable.aoi13);
        setBackgroundBitmap(context, R.drawable.aoi14);
        setBackgroundBitmap(context, R.drawable.aoi15);
        setBackgroundBitmap(context, R.drawable.aoi16);
        setBackgroundBitmap(context, R.drawable.aoi17);
        setBackgroundBitmap(context, R.drawable.aoi18);
        setBackgroundBitmap(context, R.drawable.aoi19);
        setBackgroundBitmap(context, R.drawable.aoi20);
        setBackgroundBitmap(context, R.drawable.aoi21);
        setBackgroundBitmap(context, R.drawable.aoi22);
        setBackgroundBitmap(context, R.drawable.aoi23);
        setBackgroundBitmap(context, R.drawable.aoi24);
        setBackgroundBitmap(context, R.drawable.aoi25);
        setBackgroundBitmap(context, R.drawable.aoi26);
        setBackgroundBitmap(context, R.drawable.aoi27);
        setBackgroundBitmap(context, R.drawable.aoi28);
        setBackgroundBitmap(context, R.drawable.aoi29);
        setBackgroundBitmap(context, R.drawable.aoi30);
        setBackgroundBitmap(context, R.drawable.aoi31);
        setBackgroundBitmap(context, R.drawable.aoi32);
        setBackgroundBitmap(context, R.drawable.aoi33);
        setBackgroundBitmap(context, R.drawable.aoi34);
        setBackgroundBitmap(context, R.drawable.aoi35);
        setBackgroundBitmap(context, R.drawable.aoi36);
        setBackgroundBitmap(context, R.drawable.aoi37);
        setBackgroundBitmap(context, R.drawable.aoi38);
        setBackgroundBitmap(context, R.drawable.aoi39);
        setBackgroundBitmap(context, R.drawable.aoi40);
        setBackgroundBitmap(context, R.drawable.aoi41);
        setBackgroundBitmap(context, R.drawable.aoi42);
        setBackgroundBitmap(context, R.drawable.aoi43);
        setBackgroundBitmap(context, R.drawable.aoi44);
        setBackgroundBitmap(context, R.drawable.aoi45);
        setBackgroundBitmap(context, R.drawable.aoi46);
        setBackgroundBitmap(context, R.drawable.aoi47);
        setBackgroundBitmap(context, R.drawable.aoi48);
        setBackgroundBitmap(context, R.drawable.aoi49);
        setBackgroundBitmap(context, R.drawable.aoi50);
        setBackgroundBitmap(context, R.drawable.aoi51);
        setBackgroundBitmap(context, R.drawable.aoi52);
        setBackgroundBitmap(context, R.drawable.aoi53);
        setBackgroundBitmap(context, R.drawable.aoi54);
        setBackgroundBitmap(context, R.drawable.aoi55);
        setBackgroundBitmap(context, R.drawable.aoi56);
        setBackgroundBitmap(context, R.drawable.aoi57);
        setBackgroundBitmap(context, R.drawable.aoi58);
        setBackgroundBitmap(context, R.drawable.aoi59);
        setBackgroundBitmap(context, R.drawable.aoi60);
        setBackgroundBitmap(context, R.drawable.aoi61);
        setBackgroundBitmap(context, R.drawable.aoi62);
        setBackgroundBitmap(context, R.drawable.aoi63);
        setBackgroundBitmap(context, R.drawable.aoi64);
        setBackgroundBitmap(context, R.drawable.aoi65);
        setBackgroundBitmap(context, R.drawable.aoi66);
        setBackgroundBitmap(context, R.drawable.aoi67);
        setBackgroundBitmap(context, R.drawable.aoi68);
        setBackgroundBitmap(context, R.drawable.aoi69);

        setBackgroundBitmap(context, R.drawable.aohina01);
        setBackgroundBitmap(context, R.drawable.aohina02);
        setBackgroundBitmap(context, R.drawable.aohina03);
        setBackgroundBitmap(context, R.drawable.aohina04);
        setBackgroundBitmap(context, R.drawable.aohina05);
        setBackgroundBitmap(context, R.drawable.aohina06);
        setBackgroundBitmap(context, R.drawable.aohina07);
        setBackgroundBitmap(context, R.drawable.aohina08);
        setBackgroundBitmap(context, R.drawable.aohina09);
        setBackgroundBitmap(context, R.drawable.aohina10);
        setBackgroundBitmap(context, R.drawable.aohina11);
        setBackgroundBitmap(context, R.drawable.aohina12);
        setBackgroundBitmap(context, R.drawable.aohina13);
        setBackgroundBitmap(context, R.drawable.aohina14);
        setBackgroundBitmap(context, R.drawable.aohina15);
        setBackgroundBitmap(context, R.drawable.aohina16);
        setBackgroundBitmap(context, R.drawable.aohina17);
        setBackgroundBitmap(context, R.drawable.aohina18);
        setBackgroundBitmap(context, R.drawable.aohina19);
        setBackgroundBitmap(context, R.drawable.aohina20);
        setBackgroundBitmap(context, R.drawable.aohina21);
        setBackgroundBitmap(context, R.drawable.aohina22);
        setBackgroundBitmap(context, R.drawable.aohina23);
        setBackgroundBitmap(context, R.drawable.aohina24);
        setBackgroundBitmap(context, R.drawable.aohina25);
        setBackgroundBitmap(context, R.drawable.aohina26);
        setBackgroundBitmap(context, R.drawable.aohina27);
        setBackgroundBitmap(context, R.drawable.aohina28);
        setBackgroundBitmap(context, R.drawable.aohina29);
        setBackgroundBitmap(context, R.drawable.aohina30);
        setBackgroundBitmap(context, R.drawable.aohina31);
        setBackgroundBitmap(context, R.drawable.aohina32);
        setBackgroundBitmap(context, R.drawable.aohina33);
        setBackgroundBitmap(context, R.drawable.aohina34);
        setBackgroundBitmap(context, R.drawable.aohina35);
        setBackgroundBitmap(context, R.drawable.aohina36);

        setBackgroundBitmap(context, R.drawable.yama01);

        setBackgroundBitmap(context, R.drawable.hinata01);
        setBackgroundBitmap(context, R.drawable.hinata02);
        setBackgroundBitmap(context, R.drawable.hinata03);
        setBackgroundBitmap(context, R.drawable.hinata04);
        setBackgroundBitmap(context, R.drawable.hinata05);
        setBackgroundBitmap(context, R.drawable.hinata06);
        setBackgroundBitmap(context, R.drawable.hinata07);
        setBackgroundBitmap(context, R.drawable.hinata08);
        setBackgroundBitmap(context, R.drawable.hinata09);
        setBackgroundBitmap(context, R.drawable.hinata10);
        setBackgroundBitmap(context, R.drawable.hinata11);
        setBackgroundBitmap(context, R.drawable.hinata12);
        setBackgroundBitmap(context, R.drawable.hinata13);
        setBackgroundBitmap(context, R.drawable.hinata14);
        setBackgroundBitmap(context, R.drawable.hinata15);
        setBackgroundBitmap(context, R.drawable.hinata16);
        setBackgroundBitmap(context, R.drawable.hinata17);
        setBackgroundBitmap(context, R.drawable.hinata18);
        setBackgroundBitmap(context, R.drawable.hinata19);
        setBackgroundBitmap(context, R.drawable.hinata20);
        setBackgroundBitmap(context, R.drawable.hinata21);
        setBackgroundBitmap(context, R.drawable.hinata22);
        setBackgroundBitmap(context, R.drawable.hinata23);
        setBackgroundBitmap(context, R.drawable.hinata24);
        setBackgroundBitmap(context, R.drawable.hinata25);
        setBackgroundBitmap(context, R.drawable.hinata26);
        setBackgroundBitmap(context, R.drawable.hinata27);
        setBackgroundBitmap(context, R.drawable.hinata28);
        setBackgroundBitmap(context, R.drawable.hinata29);
        setBackgroundBitmap(context, R.drawable.hinata30);
        setBackgroundBitmap(context, R.drawable.hinata31);
        setBackgroundBitmap(context, R.drawable.hinata32);
        setBackgroundBitmap(context, R.drawable.hinata33);
        setBackgroundBitmap(context, R.drawable.hinata34);
        setBackgroundBitmap(context, R.drawable.hinata35);
        setBackgroundBitmap(context, R.drawable.hinata36);
        setBackgroundBitmap(context, R.drawable.hinata37);
        setBackgroundBitmap(context, R.drawable.hinata38);
        setBackgroundBitmap(context, R.drawable.hinata38);
        setBackgroundBitmap(context, R.drawable.hinata39);

        setBackgroundBitmap(context, R.drawable.lets1);
        setBackgroundBitmap(context, R.drawable.lets2);
        System.gc();
**/
    }

    /**
     *   背景画像の記憶(ビットマップ展開)
     *
     * @param context リソース
     * @param id         画像のID
     */
    private void setBackgroundBitmap(Context context, int id)
    {
        nofBitmaps++;
        try
        {
            Drawable backgroundDrawable = context.getResources().getDrawable(id);
            mBackgroundBitmaps.put(nofBitmaps, ((BitmapDrawable) backgroundDrawable).getBitmap());
        }
        catch (Exception ex)
        {
            // 例外発生...
            if (nofBitmaps != 0)
            {
                nofBitmaps--;
            }
            if (Log.isLoggable(TAG, Log.WARN))
            {
                Log.w(TAG, "setBackgroundBitmap exception ("+ id + ") "+ ex.getMessage());
            }
        }
    }


    /**
     *   ファイルをビットマップとして読み込む
     *
     * @return  読み込んだビットマップファイル数
     */
    private int extractBitmaps()
    {
        int nofReadImages = 0;
        String directory = fileUtility.getGokigenDirectory() + "/all";
        File file = new File(directory);
        File [] fileList = file.listFiles();
        if (fileList == null)
        {
            return (0);
        }
        for (File fileName : fileList)
        {
            InputStream in = null;
            try
            {
                in = new FileInputStream(fileName);
                Bitmap readImage = BitmapFactory.decodeStream(new BufferedInputStream(in));
                if (readImage != null)
                {
                    nofBitmaps++;
                    mBackgroundBitmaps.put(nofBitmaps, readImage);
                    nofReadImages++;
                    if (Log.isLoggable(TAG, Log.INFO))
                    {
                        Log.i(TAG, "image file : " + fileName.getName());
                    }
                }
                System.gc();
            }
            catch (Exception e)
            {
                if (Log.isLoggable(TAG, Log.WARN))
                {
                    Log.w(TAG, "extractBitmaps ex. : " + e.getMessage());
                }
            }
            finally
            {
                try
                {
                    if (in != null)
                    {
                        in.close();
                    }
                }
                catch (Exception ee)
                {
                    if (Log.isLoggable(TAG, Log.WARN))
                    {
                        Log.w(TAG, "EX.: " + ee.getMessage());
                    }
                }
            }
        }
        return (nofReadImages);
    }


    /**
     *   returns background bitmap (scaled)
     *
     * @param width
     * @param height
     * @return  scaled bitmap
     */
    public Bitmap getBackgroundScaledBitmap(int width, int height)
    {
        currentBackgroundBitmap++;
        if (currentBackgroundBitmap > nofBitmaps)
        {
            currentBackgroundBitmap = 1;
        }
        Bitmap scaledBitmap;
/**
        Bitmap scaledBitmap = mBackgroundScaledBitmaps.get(currentBackgroundBitmap);
        // Draw the background, scaled to fit.
        if (scaledBitmap == null
                || scaledBitmap.getWidth() != width
                || scaledBitmap.getHeight() != height)
        {
**/
            // メモリ容量削減のため、毎回ビットマップサイズを変換して生成する
            scaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmaps.get(currentBackgroundBitmap), width, height, true /* filter */);
/**
            mBackgroundScaledBitmaps.put(currentBackgroundBitmap, scaledBitmap);
        }
**/
        return (scaledBitmap);
    }

    /**
     *
     */
    private void initializeDrawTextObjects(Context context)
    {
        Resources resources = context.getResources();

        /* create graphic styles */
        mTimePaint = createTextPaint(Color.parseColor("white"), BOLD_TYPEFACE);
        mBackPaint = createTextPaint(Color.argb(80, 0, 0, 0), NORMAL_TYPEFACE);

        /** default watch face is square **/
        mIsRoundShape = false;
        mTextSize = resources.getDimension(R.dimen.text_size);
        mYOffset = resources.getDimension(R.dimen.x_offset);
    }

    /**
     *  文字表示用のPaintクラス作成メソッド
     * @param defaultInteractiveColor
     * @param typeface
     * @return
     */
    private Paint createTextPaint(int defaultInteractiveColor, Typeface typeface)
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(defaultInteractiveColor);
        paint.setTypeface(typeface);
        paint.setAntiAlias(true);
        paint.setTextSize(mTextSize);
        return (paint);
    }
}
