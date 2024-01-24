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
import java.util.Random;

import static android.support.wearable.watchface.WatchFaceService.PROPERTY_LOW_BIT_AMBIENT;

/**
 *   時計描画用データの保持クラス
 *
 * Created by MRSa on 2014/12/20.
 */
class MyWatchFaceHolder
{
    private static final String TAG = "MyWatchFaceHolder";
    private static final int THRESHOLD_RANDOM_SHOW = 60;    // ランダムに表示するかどうかの切替え
    private static final Typeface BOLD_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
    private static final Typeface NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    static final String TIME_SEPARATOR = ":";
    static final String SECOND_SEPARATOR = ".";
    // private static final String DATE_SEPARATOR = "-";

    /* device features */
    //private boolean mLowBitAmbient = false;
    //private boolean mBurnInProtection = false;
    private boolean mIsRoundShape = false;

    /* graphic objects */
    private boolean isRandom = false;
    private boolean doUseExternalFiles = false;
    private int nofBitmaps = 0;
    private int currentBackgroundBitmap = 0;
    private Hashtable<Integer, Integer> mBackgroundBitmapIds = null;
    private Hashtable<Integer, String> mBackgroundBitmapFileNames = null;

    /* 文字表示用の情報 */
    private float mTextSize = 0;
    private float mYOffset = 0;

    private Paint mTimePaint = null;
    private Paint mBackPaint = null;
    private Context context = null;

    private ExtStorageFileUtil fileUtility = null;

    private Random randomGenerator = null;

    /**
     *  コンストラクタ
     */
    MyWatchFaceHolder(Context parent)
    {
        String offset = "/prpr";
        String dir = "/all";

        //  ファイルユーティリティの作成
        fileUtility = new ExtStorageFileUtil(offset);
        fileUtility.makeDirectory(dir);

        // 乱数生成器
        randomGenerator = new Random();

        this.context = parent;

        initialize();
    }

    /**
     *  初期化処理
     *
     */
    private void initialize()
    {
        initializeGraphicObjects();
        initializeDrawTextObjects();
        //initializePeriodicTimer();
        //initializeTimeZoneChangeReceiver();

        // ビットマップ数がある程度を超えると、ランダムで表示する
        if (nofBitmaps > THRESHOLD_RANDOM_SHOW)
        {
            isRandom = true;
        }
        Log.v(TAG, "nofBitmap: " + nofBitmaps + " rnd : " + isRandom);
    }

    void onPropertiesChanged(Bundle properties)
    {
        Log.v(TAG, "PROPERTY_LOW_BIT_AMBIENT " + properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false));
        //mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        //mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
    }

    boolean setAmbientMode(boolean inAmbientMode, boolean previousInAmbientMode)
    {
        return (!(inAmbientMode == previousInAmbientMode));
    }

    /**
     *   終了時処理 (記憶していた画像を示す配列を消去する)
     *
     */
    void dispose()
    {
        if (mBackgroundBitmapIds != null)
        {
            mBackgroundBitmapIds.clear();
            mBackgroundBitmapIds = null;
        }

        if (mBackgroundBitmapFileNames != null)
        {
            mBackgroundBitmapFileNames.clear();
            mBackgroundBitmapFileNames = null;
        }
        System.gc();
    }

    /**
     *
     *
     */
    void setIsRound(boolean isRound, Resources resources)
    {
        if (mIsRoundShape == isRound)
        {
            return;
        }
        // update watch face shape
        mIsRoundShape = isRound;
        mTextSize = resources.getDimension(isRound ? R.dimen.text_size_round : R.dimen.text_size);
    }

    /**
     *
     */
    Paint getTimePaint()
    {
        mTimePaint.setTextSize(mTextSize);
        return (mTimePaint);
    }

    /**
     *
     */
    Paint getBackPaint()
    {
        return (mBackPaint);
    }

    /**
     *
     */
    float getYOffset()
    {
        return (mYOffset);
    }

    /**
     *
     */
    private void initializeGraphicObjects()
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "initializeGraphicObjects " + Integer.toString(currentBackgroundBitmap));
        }
        mBackgroundBitmapIds = null;
        mBackgroundBitmapFileNames = null;

        mBackgroundBitmapFileNames = new Hashtable<>();
        mBackgroundBitmapFileNames.clear();

        ///表示画像の初期化
        currentBackgroundBitmap = 0;

        // ファイルからビットマップを展開する
        int nofImages = extractBitmaps();
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "read Images : " + Integer.toString(nofImages));
        }
        if (nofImages > 0)
        {
            // イメージが１つでも送り込まれていた場合、デフォルトの画像は表示しない
            doUseExternalFiles = true;
            return;
        }

        // 背景画像（デフォルト）の設定
        mBackgroundBitmapIds = new Hashtable<>();
        mBackgroundBitmapIds.clear();

/**/
        setupBackgroundBitmapFromResId(R.drawable.background);
        //setupBackgroundBitmapFromResId(R.drawable.background1);
        setupBackgroundBitmapFromResId(R.drawable.background2);
        //setupBackgroundBitmapFromResId(R.drawable.background3);
        setupBackgroundBitmapFromResId(R.drawable.background4);
        setupBackgroundBitmapFromResId(R.drawable.background5);
        setupBackgroundBitmapFromResId(R.drawable.background6);
/**/
/**
        setupBackgroundBitmapFromResId(R.drawable.musasabi);
        setupBackgroundBitmapFromResId(R.drawable.musasabi2);
        setupBackgroundBitmapFromResId(R.drawable.musasabi3);

        setupBackgroundBitmapFromResId(R.drawable.aoi01);
        setupBackgroundBitmapFromResId(R.drawable.aoi02);
        setupBackgroundBitmapFromResId(R.drawable.aoi03);
        setupBackgroundBitmapFromResId(R.drawable.aoi04);
        setupBackgroundBitmapFromResId(R.drawable.aoi05);
        setupBackgroundBitmapFromResId(R.drawable.aoi06);
        setupBackgroundBitmapFromResId(R.drawable.aoi07);
        setupBackgroundBitmapFromResId(R.drawable.aoi08);
        setupBackgroundBitmapFromResId(R.drawable.aoi09);
        setupBackgroundBitmapFromResId(R.drawable.aoi10);
        setupBackgroundBitmapFromResId(R.drawable.aoi11);
        setupBackgroundBitmapFromResId(R.drawable.aoi12);
        setupBackgroundBitmapFromResId(R.drawable.aoi13);
        setupBackgroundBitmapFromResId(R.drawable.aoi14);
        setupBackgroundBitmapFromResId(R.drawable.aoi15);
        setupBackgroundBitmapFromResId(R.drawable.aoi16);
        setupBackgroundBitmapFromResId(R.drawable.aoi17);
        setupBackgroundBitmapFromResId(R.drawable.aoi18);
        setupBackgroundBitmapFromResId(R.drawable.aoi19);
        setupBackgroundBitmapFromResId(R.drawable.aoi20);
        setupBackgroundBitmapFromResId(R.drawable.aoi21);
        setupBackgroundBitmapFromResId(R.drawable.aoi22);
        setupBackgroundBitmapFromResId(R.drawable.aoi23);
        setupBackgroundBitmapFromResId(R.drawable.aoi24);
        setupBackgroundBitmapFromResId(R.drawable.aoi25);
        setupBackgroundBitmapFromResId(R.drawable.aoi26);
        setupBackgroundBitmapFromResId(R.drawable.aoi27);
        setupBackgroundBitmapFromResId(R.drawable.aoi28);
        setupBackgroundBitmapFromResId(R.drawable.aoi29);
        setupBackgroundBitmapFromResId(R.drawable.aoi30);
        setupBackgroundBitmapFromResId(R.drawable.aoi31);
        setupBackgroundBitmapFromResId(R.drawable.aoi32);
        setupBackgroundBitmapFromResId(R.drawable.aoi33);
        setupBackgroundBitmapFromResId(R.drawable.aoi34);
        setupBackgroundBitmapFromResId(R.drawable.aoi35);
        setupBackgroundBitmapFromResId(R.drawable.aoi36);
        setupBackgroundBitmapFromResId(R.drawable.aoi37);
        setupBackgroundBitmapFromResId(R.drawable.aoi38);
        setupBackgroundBitmapFromResId(R.drawable.aoi39);
        setupBackgroundBitmapFromResId(R.drawable.aoi40);
        setupBackgroundBitmapFromResId(R.drawable.aoi41);
        setupBackgroundBitmapFromResId(R.drawable.aoi42);
        setupBackgroundBitmapFromResId(R.drawable.aoi43);
        setupBackgroundBitmapFromResId(R.drawable.aoi44);
        setupBackgroundBitmapFromResId(R.drawable.aoi45);
        setupBackgroundBitmapFromResId(R.drawable.aoi46);
        setupBackgroundBitmapFromResId(R.drawable.aoi47);
        setupBackgroundBitmapFromResId(R.drawable.aoi48);
        setupBackgroundBitmapFromResId(R.drawable.aoi49);
        setupBackgroundBitmapFromResId(R.drawable.aoi50);
        setupBackgroundBitmapFromResId(R.drawable.aoi51);
        setupBackgroundBitmapFromResId(R.drawable.aoi52);
        setupBackgroundBitmapFromResId(R.drawable.aoi53);
        setupBackgroundBitmapFromResId(R.drawable.aoi54);
        setupBackgroundBitmapFromResId(R.drawable.aoi55);
        setupBackgroundBitmapFromResId(R.drawable.aoi56);
        setupBackgroundBitmapFromResId(R.drawable.aoi57);
        setupBackgroundBitmapFromResId(R.drawable.aoi58);
        setupBackgroundBitmapFromResId(R.drawable.aoi59);
        setupBackgroundBitmapFromResId(R.drawable.aoi60);
        setupBackgroundBitmapFromResId(R.drawable.aoi61);
        setupBackgroundBitmapFromResId(R.drawable.aoi62);
        setupBackgroundBitmapFromResId(R.drawable.aoi63);
        setupBackgroundBitmapFromResId(R.drawable.aoi64);
        setupBackgroundBitmapFromResId(R.drawable.aoi65);
        setupBackgroundBitmapFromResId(R.drawable.aoi66);
        setupBackgroundBitmapFromResId(R.drawable.aoi67);
        setupBackgroundBitmapFromResId(R.drawable.aoi68);
        setupBackgroundBitmapFromResId(R.drawable.aoi69);
        setupBackgroundBitmapFromResId(R.drawable.aoi70);
        setupBackgroundBitmapFromResId(R.drawable.aoi71);
        setupBackgroundBitmapFromResId(R.drawable.aoi72);
        setupBackgroundBitmapFromResId(R.drawable.aoi73);
        setupBackgroundBitmapFromResId(R.drawable.aoi74);
        setupBackgroundBitmapFromResId(R.drawable.aoi75);
        setupBackgroundBitmapFromResId(R.drawable.aoi76);
        setupBackgroundBitmapFromResId(R.drawable.aoi77);
        setupBackgroundBitmapFromResId(R.drawable.aoi78);
        setupBackgroundBitmapFromResId(R.drawable.aoi79);
        setupBackgroundBitmapFromResId(R.drawable.aoi80);
        setupBackgroundBitmapFromResId(R.drawable.aoi81);
        setupBackgroundBitmapFromResId(R.drawable.aoi82);
        setupBackgroundBitmapFromResId(R.drawable.aoi83);
        setupBackgroundBitmapFromResId(R.drawable.aoi84);
        setupBackgroundBitmapFromResId(R.drawable.aoi85);
        setupBackgroundBitmapFromResId(R.drawable.aoi86);
        setupBackgroundBitmapFromResId(R.drawable.aoi87);
        setupBackgroundBitmapFromResId(R.drawable.aoi88);
        setupBackgroundBitmapFromResId(R.drawable.aoi89);
        setupBackgroundBitmapFromResId(R.drawable.aoi90);
        setupBackgroundBitmapFromResId(R.drawable.aoi91);
        setupBackgroundBitmapFromResId(R.drawable.aoi92);
        setupBackgroundBitmapFromResId(R.drawable.aoi93);
        setupBackgroundBitmapFromResId(R.drawable.aoi94);
        setupBackgroundBitmapFromResId(R.drawable.aoi95);
        setupBackgroundBitmapFromResId(R.drawable.aoi96);
        setupBackgroundBitmapFromResId(R.drawable.aoi97);
        setupBackgroundBitmapFromResId(R.drawable.aoi98);
        setupBackgroundBitmapFromResId(R.drawable.aoi99);
        setupBackgroundBitmapFromResId(R.drawable.aoi100);
        setupBackgroundBitmapFromResId(R.drawable.aoi101);
        setupBackgroundBitmapFromResId(R.drawable.aoi102);
        setupBackgroundBitmapFromResId(R.drawable.aoi103);
        setupBackgroundBitmapFromResId(R.drawable.aoi104);
        setupBackgroundBitmapFromResId(R.drawable.aoi105);
        setupBackgroundBitmapFromResId(R.drawable.aoi106);
        setupBackgroundBitmapFromResId(R.drawable.aoi107);
        setupBackgroundBitmapFromResId(R.drawable.aoi108);
        setupBackgroundBitmapFromResId(R.drawable.aoi109);
        setupBackgroundBitmapFromResId(R.drawable.aoi110);
        setupBackgroundBitmapFromResId(R.drawable.aoi111);
        setupBackgroundBitmapFromResId(R.drawable.aoi112);
        setupBackgroundBitmapFromResId(R.drawable.aoi113);
        setupBackgroundBitmapFromResId(R.drawable.aoi114);
        setupBackgroundBitmapFromResId(R.drawable.aoi115);
        setupBackgroundBitmapFromResId(R.drawable.aoi116);
        setupBackgroundBitmapFromResId(R.drawable.aoi117);
        setupBackgroundBitmapFromResId(R.drawable.aoi118);
        setupBackgroundBitmapFromResId(R.drawable.aoi119);
        setupBackgroundBitmapFromResId(R.drawable.aoi120);
        setupBackgroundBitmapFromResId(R.drawable.aoi121);
        setupBackgroundBitmapFromResId(R.drawable.aoi122);
        setupBackgroundBitmapFromResId(R.drawable.aoi123);
        setupBackgroundBitmapFromResId(R.drawable.aoi124);
        setupBackgroundBitmapFromResId(R.drawable.aoi125);
        setupBackgroundBitmapFromResId(R.drawable.aoi126);
        setupBackgroundBitmapFromResId(R.drawable.aoi127);
        setupBackgroundBitmapFromResId(R.drawable.aoi128);
        setupBackgroundBitmapFromResId(R.drawable.aoi129);
        setupBackgroundBitmapFromResId(R.drawable.aoi130);
        setupBackgroundBitmapFromResId(R.drawable.aoi131);
        setupBackgroundBitmapFromResId(R.drawable.aoi132);
        setupBackgroundBitmapFromResId(R.drawable.aoi133);
        setupBackgroundBitmapFromResId(R.drawable.aoi134);
        setupBackgroundBitmapFromResId(R.drawable.aoi135);
        setupBackgroundBitmapFromResId(R.drawable.aoi136);
        setupBackgroundBitmapFromResId(R.drawable.aoi137);
        setupBackgroundBitmapFromResId(R.drawable.aoi138);
        setupBackgroundBitmapFromResId(R.drawable.aoi139);
        setupBackgroundBitmapFromResId(R.drawable.aoi140);
        setupBackgroundBitmapFromResId(R.drawable.aoi141);
        setupBackgroundBitmapFromResId(R.drawable.aoi142);
        setupBackgroundBitmapFromResId(R.drawable.aoi143);
        setupBackgroundBitmapFromResId(R.drawable.aoi144);
        setupBackgroundBitmapFromResId(R.drawable.aoi145);
        setupBackgroundBitmapFromResId(R.drawable.aoi146);
        setupBackgroundBitmapFromResId(R.drawable.aoi147);
        setupBackgroundBitmapFromResId(R.drawable.aoi148);
        setupBackgroundBitmapFromResId(R.drawable.aoi149);
        setupBackgroundBitmapFromResId(R.drawable.aoi150);
        setupBackgroundBitmapFromResId(R.drawable.aoi151);
        setupBackgroundBitmapFromResId(R.drawable.aoi152);
        setupBackgroundBitmapFromResId(R.drawable.aoi153);
        setupBackgroundBitmapFromResId(R.drawable.aoi154);
        setupBackgroundBitmapFromResId(R.drawable.aoi155);
        setupBackgroundBitmapFromResId(R.drawable.aoi156);
        setupBackgroundBitmapFromResId(R.drawable.aoi157);
        setupBackgroundBitmapFromResId(R.drawable.aoi158);
        setupBackgroundBitmapFromResId(R.drawable.aoi159);
        setupBackgroundBitmapFromResId(R.drawable.aoi160);
        setupBackgroundBitmapFromResId(R.drawable.aoi161);
        setupBackgroundBitmapFromResId(R.drawable.aoi162);
        setupBackgroundBitmapFromResId(R.drawable.aoi163);
        setupBackgroundBitmapFromResId(R.drawable.aoi164);
        setupBackgroundBitmapFromResId(R.drawable.aoi165);
        setupBackgroundBitmapFromResId(R.drawable.aoi166);
        setupBackgroundBitmapFromResId(R.drawable.aoi167);
        setupBackgroundBitmapFromResId(R.drawable.aoi168);
        setupBackgroundBitmapFromResId(R.drawable.aoi169);
        setupBackgroundBitmapFromResId(R.drawable.aoi170);
        setupBackgroundBitmapFromResId(R.drawable.aoi171);
        setupBackgroundBitmapFromResId(R.drawable.aoi172);
        setupBackgroundBitmapFromResId(R.drawable.aoi173);
        setupBackgroundBitmapFromResId(R.drawable.aoi174);
        setupBackgroundBitmapFromResId(R.drawable.aoi175);
        setupBackgroundBitmapFromResId(R.drawable.aoi176);
        setupBackgroundBitmapFromResId(R.drawable.aoi177);
        setupBackgroundBitmapFromResId(R.drawable.aoi178);
        setupBackgroundBitmapFromResId(R.drawable.aoi179);
        setupBackgroundBitmapFromResId(R.drawable.aoi180);
        setupBackgroundBitmapFromResId(R.drawable.aoi181);
        setupBackgroundBitmapFromResId(R.drawable.aoi182);
        setupBackgroundBitmapFromResId(R.drawable.aoi183);
        setupBackgroundBitmapFromResId(R.drawable.aoi184);
        setupBackgroundBitmapFromResId(R.drawable.aoi185);
        setupBackgroundBitmapFromResId(R.drawable.aoi186);
        setupBackgroundBitmapFromResId(R.drawable.aoi187);
        setupBackgroundBitmapFromResId(R.drawable.aoi188);
        setupBackgroundBitmapFromResId(R.drawable.aoi189);
        setupBackgroundBitmapFromResId(R.drawable.aoi190);
        setupBackgroundBitmapFromResId(R.drawable.aoi191);
        setupBackgroundBitmapFromResId(R.drawable.aoi192);
        setupBackgroundBitmapFromResId(R.drawable.aoi193);
        setupBackgroundBitmapFromResId(R.drawable.aoi194);
        setupBackgroundBitmapFromResId(R.drawable.aoi195);
        setupBackgroundBitmapFromResId(R.drawable.aoi196);
        setupBackgroundBitmapFromResId(R.drawable.aoi197);
        setupBackgroundBitmapFromResId(R.drawable.aoi198);
        setupBackgroundBitmapFromResId(R.drawable.aoi199);
        setupBackgroundBitmapFromResId(R.drawable.aoi200);
        setupBackgroundBitmapFromResId(R.drawable.aoi201);
        setupBackgroundBitmapFromResId(R.drawable.aoi202);
        setupBackgroundBitmapFromResId(R.drawable.aoi203);
        setupBackgroundBitmapFromResId(R.drawable.aoi204);
        setupBackgroundBitmapFromResId(R.drawable.aoi205);
        setupBackgroundBitmapFromResId(R.drawable.aoi206);
        setupBackgroundBitmapFromResId(R.drawable.aoi207);
        setupBackgroundBitmapFromResId(R.drawable.aoi208);
        setupBackgroundBitmapFromResId(R.drawable.aoi209);
        setupBackgroundBitmapFromResId(R.drawable.aoi210);
        setupBackgroundBitmapFromResId(R.drawable.aoi211);
        setupBackgroundBitmapFromResId(R.drawable.aoi212);
        setupBackgroundBitmapFromResId(R.drawable.aoi213);
        setupBackgroundBitmapFromResId(R.drawable.aoi214);
        setupBackgroundBitmapFromResId(R.drawable.aoi215);
        setupBackgroundBitmapFromResId(R.drawable.aoi216);
        setupBackgroundBitmapFromResId(R.drawable.aoi217);
        setupBackgroundBitmapFromResId(R.drawable.aoi218);
        setupBackgroundBitmapFromResId(R.drawable.aoi219);
        setupBackgroundBitmapFromResId(R.drawable.aoi220);
        setupBackgroundBitmapFromResId(R.drawable.aoi221);
        setupBackgroundBitmapFromResId(R.drawable.aoi222);
        setupBackgroundBitmapFromResId(R.drawable.aoi223);
        setupBackgroundBitmapFromResId(R.drawable.aoi224);
        setupBackgroundBitmapFromResId(R.drawable.aoi225);
        setupBackgroundBitmapFromResId(R.drawable.aoi226);
        setupBackgroundBitmapFromResId(R.drawable.aoi227);
        setupBackgroundBitmapFromResId(R.drawable.aoi228);
        setupBackgroundBitmapFromResId(R.drawable.aoi229);
        setupBackgroundBitmapFromResId(R.drawable.aoi230);
        setupBackgroundBitmapFromResId(R.drawable.aoi231);
        setupBackgroundBitmapFromResId(R.drawable.aoi232);
        setupBackgroundBitmapFromResId(R.drawable.aoi233);
        setupBackgroundBitmapFromResId(R.drawable.aoi234);
        setupBackgroundBitmapFromResId(R.drawable.aoi235);
        setupBackgroundBitmapFromResId(R.drawable.aoi236);
        setupBackgroundBitmapFromResId(R.drawable.aoi237);
        setupBackgroundBitmapFromResId(R.drawable.aoi238);
        setupBackgroundBitmapFromResId(R.drawable.aoi239);
        setupBackgroundBitmapFromResId(R.drawable.aoi240);
        setupBackgroundBitmapFromResId(R.drawable.aoi241);
        setupBackgroundBitmapFromResId(R.drawable.aoi242);
        setupBackgroundBitmapFromResId(R.drawable.aoi243);
        setupBackgroundBitmapFromResId(R.drawable.aoi244);
        setupBackgroundBitmapFromResId(R.drawable.aoi245);
        setupBackgroundBitmapFromResId(R.drawable.aoi246);
        setupBackgroundBitmapFromResId(R.drawable.aoi247);
        setupBackgroundBitmapFromResId(R.drawable.aoi248);
        setupBackgroundBitmapFromResId(R.drawable.aoi249);
        setupBackgroundBitmapFromResId(R.drawable.aoi250);
        setupBackgroundBitmapFromResId(R.drawable.aoi251);
        setupBackgroundBitmapFromResId(R.drawable.aoi252);
        setupBackgroundBitmapFromResId(R.drawable.aoi253);
        setupBackgroundBitmapFromResId(R.drawable.aoi254);
        setupBackgroundBitmapFromResId(R.drawable.aoi255);
        setupBackgroundBitmapFromResId(R.drawable.aoi256);
        setupBackgroundBitmapFromResId(R.drawable.aoi257);
        setupBackgroundBitmapFromResId(R.drawable.aoi258);
        setupBackgroundBitmapFromResId(R.drawable.aoi259);
        setupBackgroundBitmapFromResId(R.drawable.aoi260);
        setupBackgroundBitmapFromResId(R.drawable.aoi261);
        setupBackgroundBitmapFromResId(R.drawable.aoi262);
        setupBackgroundBitmapFromResId(R.drawable.aoi263);
        setupBackgroundBitmapFromResId(R.drawable.aoi264);
        setupBackgroundBitmapFromResId(R.drawable.aoi265);
        setupBackgroundBitmapFromResId(R.drawable.aoi266);
        setupBackgroundBitmapFromResId(R.drawable.aoi267);
        setupBackgroundBitmapFromResId(R.drawable.aoi268);
        setupBackgroundBitmapFromResId(R.drawable.aoi269);
        setupBackgroundBitmapFromResId(R.drawable.aoi270);
        setupBackgroundBitmapFromResId(R.drawable.aoi271);
        setupBackgroundBitmapFromResId(R.drawable.aoi272);
        setupBackgroundBitmapFromResId(R.drawable.aoi273);
        setupBackgroundBitmapFromResId(R.drawable.aoi274);
        setupBackgroundBitmapFromResId(R.drawable.aoi275);
        setupBackgroundBitmapFromResId(R.drawable.aoi276);
        setupBackgroundBitmapFromResId(R.drawable.aoi277);
        setupBackgroundBitmapFromResId(R.drawable.aoi278);
        setupBackgroundBitmapFromResId(R.drawable.aoi279);
        setupBackgroundBitmapFromResId(R.drawable.aoi280);
        setupBackgroundBitmapFromResId(R.drawable.aoi281);
        setupBackgroundBitmapFromResId(R.drawable.aoi282);
        setupBackgroundBitmapFromResId(R.drawable.aoi283);
        setupBackgroundBitmapFromResId(R.drawable.aoi284);
        setupBackgroundBitmapFromResId(R.drawable.aoi285);
        setupBackgroundBitmapFromResId(R.drawable.aoi286);
        setupBackgroundBitmapFromResId(R.drawable.aoi287);
        setupBackgroundBitmapFromResId(R.drawable.aoi288);
        setupBackgroundBitmapFromResId(R.drawable.aoi289);
        setupBackgroundBitmapFromResId(R.drawable.aoi290);
        setupBackgroundBitmapFromResId(R.drawable.aoi291);
        setupBackgroundBitmapFromResId(R.drawable.aoi292);
        setupBackgroundBitmapFromResId(R.drawable.aoi293);
        setupBackgroundBitmapFromResId(R.drawable.aoi294);
        setupBackgroundBitmapFromResId(R.drawable.aoi295);
        setupBackgroundBitmapFromResId(R.drawable.aoi296);
        setupBackgroundBitmapFromResId(R.drawable.aoi297);
        setupBackgroundBitmapFromResId(R.drawable.aoi298);
        setupBackgroundBitmapFromResId(R.drawable.aoi299);
        setupBackgroundBitmapFromResId(R.drawable.aoi300);
        setupBackgroundBitmapFromResId(R.drawable.aoi301);
        setupBackgroundBitmapFromResId(R.drawable.aoi302);
        setupBackgroundBitmapFromResId(R.drawable.aoi303);
        setupBackgroundBitmapFromResId(R.drawable.aoi304);
        setupBackgroundBitmapFromResId(R.drawable.aoi305);
        setupBackgroundBitmapFromResId(R.drawable.aoi306);
        setupBackgroundBitmapFromResId(R.drawable.aoi307);
        setupBackgroundBitmapFromResId(R.drawable.aoi308);
        setupBackgroundBitmapFromResId(R.drawable.aoi309);
        setupBackgroundBitmapFromResId(R.drawable.aoi310);
        setupBackgroundBitmapFromResId(R.drawable.aoi311);
        setupBackgroundBitmapFromResId(R.drawable.aoi312);
        setupBackgroundBitmapFromResId(R.drawable.aoi313);
        setupBackgroundBitmapFromResId(R.drawable.aoi314);
        setupBackgroundBitmapFromResId(R.drawable.aoi315);
        setupBackgroundBitmapFromResId(R.drawable.aoi316);
        setupBackgroundBitmapFromResId(R.drawable.aoi317);
        setupBackgroundBitmapFromResId(R.drawable.aoi318);
        setupBackgroundBitmapFromResId(R.drawable.aoi319);
        setupBackgroundBitmapFromResId(R.drawable.aoi320);
        setupBackgroundBitmapFromResId(R.drawable.aoi321);
        setupBackgroundBitmapFromResId(R.drawable.aoi322);
        setupBackgroundBitmapFromResId(R.drawable.aoi323);
        setupBackgroundBitmapFromResId(R.drawable.aoi324);
        setupBackgroundBitmapFromResId(R.drawable.aoi325);
        setupBackgroundBitmapFromResId(R.drawable.aoi326);
        setupBackgroundBitmapFromResId(R.drawable.aoi327);
        setupBackgroundBitmapFromResId(R.drawable.aoi328);
        setupBackgroundBitmapFromResId(R.drawable.aoi329);
        setupBackgroundBitmapFromResId(R.drawable.aoi330);
        setupBackgroundBitmapFromResId(R.drawable.aoi331);
        setupBackgroundBitmapFromResId(R.drawable.aoi332);
        setupBackgroundBitmapFromResId(R.drawable.aoi333);
        setupBackgroundBitmapFromResId(R.drawable.aoi334);
        setupBackgroundBitmapFromResId(R.drawable.aoi335);
        setupBackgroundBitmapFromResId(R.drawable.aoi336);

        setupBackgroundBitmapFromResId(R.drawable.aohina01);
        setupBackgroundBitmapFromResId(R.drawable.aohina02);
        setupBackgroundBitmapFromResId(R.drawable.aohina03);
        setupBackgroundBitmapFromResId(R.drawable.aohina04);
        setupBackgroundBitmapFromResId(R.drawable.aohina05);
        setupBackgroundBitmapFromResId(R.drawable.aohina06);
        setupBackgroundBitmapFromResId(R.drawable.aohina07);
        setupBackgroundBitmapFromResId(R.drawable.aohina08);
        setupBackgroundBitmapFromResId(R.drawable.aohina09);
        setupBackgroundBitmapFromResId(R.drawable.aohina10);
        setupBackgroundBitmapFromResId(R.drawable.aohina11);
        setupBackgroundBitmapFromResId(R.drawable.aohina12);
        setupBackgroundBitmapFromResId(R.drawable.aohina13);
        setupBackgroundBitmapFromResId(R.drawable.aohina14);
        setupBackgroundBitmapFromResId(R.drawable.aohina15);
        setupBackgroundBitmapFromResId(R.drawable.aohina16);
        setupBackgroundBitmapFromResId(R.drawable.aohina17);
        setupBackgroundBitmapFromResId(R.drawable.aohina18);
        setupBackgroundBitmapFromResId(R.drawable.aohina19);
        setupBackgroundBitmapFromResId(R.drawable.aohina20);
        setupBackgroundBitmapFromResId(R.drawable.aohina21);
        setupBackgroundBitmapFromResId(R.drawable.aohina22);
        setupBackgroundBitmapFromResId(R.drawable.aohina23);
        setupBackgroundBitmapFromResId(R.drawable.aohina24);
        setupBackgroundBitmapFromResId(R.drawable.aohina25);
        setupBackgroundBitmapFromResId(R.drawable.aohina26);
        setupBackgroundBitmapFromResId(R.drawable.aohina27);
        setupBackgroundBitmapFromResId(R.drawable.aohina28);
        setupBackgroundBitmapFromResId(R.drawable.aohina29);
        setupBackgroundBitmapFromResId(R.drawable.aohina30);
        setupBackgroundBitmapFromResId(R.drawable.aohina31);
        setupBackgroundBitmapFromResId(R.drawable.aohina32);
        setupBackgroundBitmapFromResId(R.drawable.aohina33);
        setupBackgroundBitmapFromResId(R.drawable.aohina34);
        setupBackgroundBitmapFromResId(R.drawable.aohina35);
        setupBackgroundBitmapFromResId(R.drawable.aohina36);
        setupBackgroundBitmapFromResId(R.drawable.aohina37);
        setupBackgroundBitmapFromResId(R.drawable.aohina38);
        setupBackgroundBitmapFromResId(R.drawable.aohina39);
        setupBackgroundBitmapFromResId(R.drawable.aohina40);
        setupBackgroundBitmapFromResId(R.drawable.aohina41);
        setupBackgroundBitmapFromResId(R.drawable.aohina42);
        setupBackgroundBitmapFromResId(R.drawable.aohina43);
        setupBackgroundBitmapFromResId(R.drawable.aohina44);
        setupBackgroundBitmapFromResId(R.drawable.aohina45);
        setupBackgroundBitmapFromResId(R.drawable.aohina46);
        setupBackgroundBitmapFromResId(R.drawable.aohina47);
        setupBackgroundBitmapFromResId(R.drawable.aohina48);
        setupBackgroundBitmapFromResId(R.drawable.aohina49);
        setupBackgroundBitmapFromResId(R.drawable.aohina50);
        setupBackgroundBitmapFromResId(R.drawable.aohina51);
        setupBackgroundBitmapFromResId(R.drawable.aohina52);
        setupBackgroundBitmapFromResId(R.drawable.aohina53);
        setupBackgroundBitmapFromResId(R.drawable.aohina54);
        setupBackgroundBitmapFromResId(R.drawable.aohina55);
        setupBackgroundBitmapFromResId(R.drawable.aohina56);
        setupBackgroundBitmapFromResId(R.drawable.aohina57);
        setupBackgroundBitmapFromResId(R.drawable.aohina58);
        setupBackgroundBitmapFromResId(R.drawable.aohina59);
        setupBackgroundBitmapFromResId(R.drawable.aohina60);
        setupBackgroundBitmapFromResId(R.drawable.aohina61);
        setupBackgroundBitmapFromResId(R.drawable.aohina62);
        setupBackgroundBitmapFromResId(R.drawable.aohina63);
        setupBackgroundBitmapFromResId(R.drawable.aohina64);
        setupBackgroundBitmapFromResId(R.drawable.aohina65);
        setupBackgroundBitmapFromResId(R.drawable.aohina66);
        setupBackgroundBitmapFromResId(R.drawable.aohina67);
        setupBackgroundBitmapFromResId(R.drawable.aohina68);
        setupBackgroundBitmapFromResId(R.drawable.aohina69);
        setupBackgroundBitmapFromResId(R.drawable.aohina70);
        setupBackgroundBitmapFromResId(R.drawable.aohina71);
        setupBackgroundBitmapFromResId(R.drawable.aohina72);
        setupBackgroundBitmapFromResId(R.drawable.aohina73);
        setupBackgroundBitmapFromResId(R.drawable.aohina74);
        setupBackgroundBitmapFromResId(R.drawable.aohina75);
        setupBackgroundBitmapFromResId(R.drawable.aohina76);
        setupBackgroundBitmapFromResId(R.drawable.aohina77);
        setupBackgroundBitmapFromResId(R.drawable.aohina78);
        setupBackgroundBitmapFromResId(R.drawable.aohina79);
        setupBackgroundBitmapFromResId(R.drawable.aohina80);
        setupBackgroundBitmapFromResId(R.drawable.aohina81);
        setupBackgroundBitmapFromResId(R.drawable.aohina82);
        setupBackgroundBitmapFromResId(R.drawable.aohina83);
        setupBackgroundBitmapFromResId(R.drawable.aohina84);
        setupBackgroundBitmapFromResId(R.drawable.aohina85);
        setupBackgroundBitmapFromResId(R.drawable.aohina86);
        setupBackgroundBitmapFromResId(R.drawable.aohina87);
        setupBackgroundBitmapFromResId(R.drawable.aohina88);
        setupBackgroundBitmapFromResId(R.drawable.aohina89);
        setupBackgroundBitmapFromResId(R.drawable.aohina90);
        setupBackgroundBitmapFromResId(R.drawable.aohina91);
        setupBackgroundBitmapFromResId(R.drawable.aohina92);
        setupBackgroundBitmapFromResId(R.drawable.aohina93);
        setupBackgroundBitmapFromResId(R.drawable.aohina94);
        setupBackgroundBitmapFromResId(R.drawable.aohina95);
        setupBackgroundBitmapFromResId(R.drawable.aohina96);
        setupBackgroundBitmapFromResId(R.drawable.aohina97);
        setupBackgroundBitmapFromResId(R.drawable.aohina98);
        setupBackgroundBitmapFromResId(R.drawable.aohina99);
        setupBackgroundBitmapFromResId(R.drawable.aohina100);
        setupBackgroundBitmapFromResId(R.drawable.aohina101);
        setupBackgroundBitmapFromResId(R.drawable.aohina102);
        setupBackgroundBitmapFromResId(R.drawable.aohina103);
        setupBackgroundBitmapFromResId(R.drawable.aohina104);
        setupBackgroundBitmapFromResId(R.drawable.aohina105);
        setupBackgroundBitmapFromResId(R.drawable.aohina106);
        setupBackgroundBitmapFromResId(R.drawable.aohina107);
        setupBackgroundBitmapFromResId(R.drawable.aohina108);
        setupBackgroundBitmapFromResId(R.drawable.aohina109);
        setupBackgroundBitmapFromResId(R.drawable.aohina110);
        setupBackgroundBitmapFromResId(R.drawable.aohina111);
        setupBackgroundBitmapFromResId(R.drawable.aohina112);
        setupBackgroundBitmapFromResId(R.drawable.aohina113);
        setupBackgroundBitmapFromResId(R.drawable.aohina114);
        setupBackgroundBitmapFromResId(R.drawable.aohina115);
        setupBackgroundBitmapFromResId(R.drawable.aohina116);
        setupBackgroundBitmapFromResId(R.drawable.aohina117);
        setupBackgroundBitmapFromResId(R.drawable.aohina118);
        setupBackgroundBitmapFromResId(R.drawable.aohina119);
        setupBackgroundBitmapFromResId(R.drawable.aohina120);
        setupBackgroundBitmapFromResId(R.drawable.aohina121);
        setupBackgroundBitmapFromResId(R.drawable.aohina122);
        setupBackgroundBitmapFromResId(R.drawable.aohina123);
        setupBackgroundBitmapFromResId(R.drawable.aohina124);
        setupBackgroundBitmapFromResId(R.drawable.aohina125);
        setupBackgroundBitmapFromResId(R.drawable.aohina126);


        setupBackgroundBitmapFromResId(R.drawable.yama01);
        setupBackgroundBitmapFromResId(R.drawable.yama02);
        setupBackgroundBitmapFromResId(R.drawable.yama03);
        setupBackgroundBitmapFromResId(R.drawable.yama04);
        setupBackgroundBitmapFromResId(R.drawable.yama05);
        setupBackgroundBitmapFromResId(R.drawable.yama06);
        setupBackgroundBitmapFromResId(R.drawable.yama07);
        setupBackgroundBitmapFromResId(R.drawable.yama08);
        setupBackgroundBitmapFromResId(R.drawable.yama09);
        setupBackgroundBitmapFromResId(R.drawable.yama10);
        setupBackgroundBitmapFromResId(R.drawable.yama11);
        setupBackgroundBitmapFromResId(R.drawable.yama12);
        setupBackgroundBitmapFromResId(R.drawable.yama13);
        setupBackgroundBitmapFromResId(R.drawable.yama14);
        setupBackgroundBitmapFromResId(R.drawable.yama15);
        setupBackgroundBitmapFromResId(R.drawable.yama16);
        setupBackgroundBitmapFromResId(R.drawable.yama17);
        setupBackgroundBitmapFromResId(R.drawable.yama18);
        setupBackgroundBitmapFromResId(R.drawable.yama19);
        setupBackgroundBitmapFromResId(R.drawable.yama20);
        setupBackgroundBitmapFromResId(R.drawable.yama21);
        setupBackgroundBitmapFromResId(R.drawable.yama22);
        setupBackgroundBitmapFromResId(R.drawable.yama23);
        setupBackgroundBitmapFromResId(R.drawable.yama24);
        setupBackgroundBitmapFromResId(R.drawable.yama25);
        setupBackgroundBitmapFromResId(R.drawable.yama26);
        setupBackgroundBitmapFromResId(R.drawable.yama27);
        setupBackgroundBitmapFromResId(R.drawable.yama28);
        setupBackgroundBitmapFromResId(R.drawable.yama29);
        setupBackgroundBitmapFromResId(R.drawable.yama30);
        setupBackgroundBitmapFromResId(R.drawable.yama31);
        setupBackgroundBitmapFromResId(R.drawable.yama32);
        setupBackgroundBitmapFromResId(R.drawable.yama33);
        setupBackgroundBitmapFromResId(R.drawable.yama34);
        setupBackgroundBitmapFromResId(R.drawable.yama35);
        setupBackgroundBitmapFromResId(R.drawable.yama36);
        setupBackgroundBitmapFromResId(R.drawable.yama37);
        setupBackgroundBitmapFromResId(R.drawable.yama38);
        setupBackgroundBitmapFromResId(R.drawable.yama39);
        setupBackgroundBitmapFromResId(R.drawable.yama40);
        setupBackgroundBitmapFromResId(R.drawable.yama41);
        setupBackgroundBitmapFromResId(R.drawable.yama42);
        setupBackgroundBitmapFromResId(R.drawable.yama43);
        setupBackgroundBitmapFromResId(R.drawable.yama44);
        setupBackgroundBitmapFromResId(R.drawable.yama45);
        setupBackgroundBitmapFromResId(R.drawable.yama46);
        setupBackgroundBitmapFromResId(R.drawable.yama47);
        setupBackgroundBitmapFromResId(R.drawable.yama48);
        setupBackgroundBitmapFromResId(R.drawable.yama49);
        setupBackgroundBitmapFromResId(R.drawable.yama50);
        setupBackgroundBitmapFromResId(R.drawable.yama51);
        setupBackgroundBitmapFromResId(R.drawable.yama52);
        setupBackgroundBitmapFromResId(R.drawable.yama53);
        setupBackgroundBitmapFromResId(R.drawable.yama54);
        setupBackgroundBitmapFromResId(R.drawable.yama55);
        setupBackgroundBitmapFromResId(R.drawable.yama56);
        setupBackgroundBitmapFromResId(R.drawable.yama57);
        setupBackgroundBitmapFromResId(R.drawable.yama58);
        setupBackgroundBitmapFromResId(R.drawable.yama59);
        setupBackgroundBitmapFromResId(R.drawable.yama60);
        setupBackgroundBitmapFromResId(R.drawable.yama61);
        setupBackgroundBitmapFromResId(R.drawable.yama62);
        setupBackgroundBitmapFromResId(R.drawable.yama63);
        setupBackgroundBitmapFromResId(R.drawable.yama64);
        setupBackgroundBitmapFromResId(R.drawable.yama65);
        setupBackgroundBitmapFromResId(R.drawable.yama66);
        setupBackgroundBitmapFromResId(R.drawable.yama67);
        setupBackgroundBitmapFromResId(R.drawable.yama68);
        setupBackgroundBitmapFromResId(R.drawable.yama69);
        setupBackgroundBitmapFromResId(R.drawable.yama70);
        setupBackgroundBitmapFromResId(R.drawable.yama71);
        setupBackgroundBitmapFromResId(R.drawable.yama72);
        setupBackgroundBitmapFromResId(R.drawable.yama73);

        setupBackgroundBitmapFromResId(R.drawable.yuuka01);
        setupBackgroundBitmapFromResId(R.drawable.yuuka02);
        setupBackgroundBitmapFromResId(R.drawable.yuuka03);
        setupBackgroundBitmapFromResId(R.drawable.yuuka04);
        setupBackgroundBitmapFromResId(R.drawable.yuuka05);
        setupBackgroundBitmapFromResId(R.drawable.yuuka06);
        setupBackgroundBitmapFromResId(R.drawable.yuuka07);
        setupBackgroundBitmapFromResId(R.drawable.yuuka08);
        setupBackgroundBitmapFromResId(R.drawable.yuuka09);

        setupBackgroundBitmapFromResId(R.drawable.aohono01);
        setupBackgroundBitmapFromResId(R.drawable.aohono02);
        setupBackgroundBitmapFromResId(R.drawable.aohono03);
        setupBackgroundBitmapFromResId(R.drawable.aohono04);
        setupBackgroundBitmapFromResId(R.drawable.aohono05);
        setupBackgroundBitmapFromResId(R.drawable.aohono06);
        setupBackgroundBitmapFromResId(R.drawable.aohono07);
        setupBackgroundBitmapFromResId(R.drawable.aohono08);

        setupBackgroundBitmapFromResId(R.drawable.honoka01);
        setupBackgroundBitmapFromResId(R.drawable.honoka02);
        setupBackgroundBitmapFromResId(R.drawable.honoka03);
        setupBackgroundBitmapFromResId(R.drawable.honoka04);
        setupBackgroundBitmapFromResId(R.drawable.honoka05);
        setupBackgroundBitmapFromResId(R.drawable.honoka06);
        setupBackgroundBitmapFromResId(R.drawable.honoka07);
        setupBackgroundBitmapFromResId(R.drawable.honoka08);
        setupBackgroundBitmapFromResId(R.drawable.honoka09);
        setupBackgroundBitmapFromResId(R.drawable.honoka10);
        setupBackgroundBitmapFromResId(R.drawable.honoka11);
        setupBackgroundBitmapFromResId(R.drawable.honoka12);
        setupBackgroundBitmapFromResId(R.drawable.honoka13);
        setupBackgroundBitmapFromResId(R.drawable.honoka14);
        setupBackgroundBitmapFromResId(R.drawable.honoka15);
        setupBackgroundBitmapFromResId(R.drawable.honoka16);
        setupBackgroundBitmapFromResId(R.drawable.honoka17);
        setupBackgroundBitmapFromResId(R.drawable.honoka18);
        setupBackgroundBitmapFromResId(R.drawable.honoka19);
        setupBackgroundBitmapFromResId(R.drawable.honoka20);
        setupBackgroundBitmapFromResId(R.drawable.honoka21);
        setupBackgroundBitmapFromResId(R.drawable.honoka22);
        setupBackgroundBitmapFromResId(R.drawable.honoka23);


        setupBackgroundBitmapFromResId(R.drawable.kaede01);
        setupBackgroundBitmapFromResId(R.drawable.kaede02);
        setupBackgroundBitmapFromResId(R.drawable.kaede03);
        setupBackgroundBitmapFromResId(R.drawable.kaede04);
        setupBackgroundBitmapFromResId(R.drawable.kaede05);
        setupBackgroundBitmapFromResId(R.drawable.kaede06);
        setupBackgroundBitmapFromResId(R.drawable.kaede07);
        setupBackgroundBitmapFromResId(R.drawable.kaede08);
        setupBackgroundBitmapFromResId(R.drawable.kaede09);
        setupBackgroundBitmapFromResId(R.drawable.kaede10);
        setupBackgroundBitmapFromResId(R.drawable.kaede11);
        setupBackgroundBitmapFromResId(R.drawable.kaede12);
        setupBackgroundBitmapFromResId(R.drawable.kaede13);
        setupBackgroundBitmapFromResId(R.drawable.kaede14);
        setupBackgroundBitmapFromResId(R.drawable.kaede15);
        setupBackgroundBitmapFromResId(R.drawable.kaede16);
        setupBackgroundBitmapFromResId(R.drawable.kaede17);
        setupBackgroundBitmapFromResId(R.drawable.kaede18);
        setupBackgroundBitmapFromResId(R.drawable.kaede19);
        setupBackgroundBitmapFromResId(R.drawable.kaede20);
        setupBackgroundBitmapFromResId(R.drawable.kaede21);
        setupBackgroundBitmapFromResId(R.drawable.kaede22);
        setupBackgroundBitmapFromResId(R.drawable.kaede23);
        setupBackgroundBitmapFromResId(R.drawable.kaede24);
        setupBackgroundBitmapFromResId(R.drawable.kaede25);
        setupBackgroundBitmapFromResId(R.drawable.kaede26);
        setupBackgroundBitmapFromResId(R.drawable.kaede27);
        setupBackgroundBitmapFromResId(R.drawable.kaede28);
        setupBackgroundBitmapFromResId(R.drawable.kaede29);
        setupBackgroundBitmapFromResId(R.drawable.kaede30);
        setupBackgroundBitmapFromResId(R.drawable.kaede31);
        setupBackgroundBitmapFromResId(R.drawable.kaede32);
        setupBackgroundBitmapFromResId(R.drawable.kaede33);
        setupBackgroundBitmapFromResId(R.drawable.kaede34);
        setupBackgroundBitmapFromResId(R.drawable.kaede35);
        setupBackgroundBitmapFromResId(R.drawable.kaede36);
        setupBackgroundBitmapFromResId(R.drawable.kaede37);
        setupBackgroundBitmapFromResId(R.drawable.kaede38);
        setupBackgroundBitmapFromResId(R.drawable.kaede39);
        setupBackgroundBitmapFromResId(R.drawable.kaede40);
        setupBackgroundBitmapFromResId(R.drawable.kaede41);
        setupBackgroundBitmapFromResId(R.drawable.kaede42);
        setupBackgroundBitmapFromResId(R.drawable.kaede43);
        setupBackgroundBitmapFromResId(R.drawable.kaede44);
        setupBackgroundBitmapFromResId(R.drawable.kaede45);
        setupBackgroundBitmapFromResId(R.drawable.kaede46);
        setupBackgroundBitmapFromResId(R.drawable.kaede47);
        setupBackgroundBitmapFromResId(R.drawable.kaede48);
        setupBackgroundBitmapFromResId(R.drawable.kaede49);
        setupBackgroundBitmapFromResId(R.drawable.kaede50);
        setupBackgroundBitmapFromResId(R.drawable.kaede51);
        setupBackgroundBitmapFromResId(R.drawable.kaede52);
        setupBackgroundBitmapFromResId(R.drawable.kaede53);
        setupBackgroundBitmapFromResId(R.drawable.kaede54);
        setupBackgroundBitmapFromResId(R.drawable.kaede55);
        setupBackgroundBitmapFromResId(R.drawable.kaede56);
        setupBackgroundBitmapFromResId(R.drawable.kaede57);
        setupBackgroundBitmapFromResId(R.drawable.kaede58);
        setupBackgroundBitmapFromResId(R.drawable.kaede59);
        setupBackgroundBitmapFromResId(R.drawable.kaede60);
        setupBackgroundBitmapFromResId(R.drawable.kaede61);
        setupBackgroundBitmapFromResId(R.drawable.kaede62);
        setupBackgroundBitmapFromResId(R.drawable.kaede63);

        setupBackgroundBitmapFromResId(R.drawable.kaehina01);
        setupBackgroundBitmapFromResId(R.drawable.kaehina02);
        setupBackgroundBitmapFromResId(R.drawable.kaehina03);
        setupBackgroundBitmapFromResId(R.drawable.kaehina04);
        setupBackgroundBitmapFromResId(R.drawable.kaehina05);
        setupBackgroundBitmapFromResId(R.drawable.kaehina06);
        setupBackgroundBitmapFromResId(R.drawable.kaehina07);
        setupBackgroundBitmapFromResId(R.drawable.kaehina08);
        setupBackgroundBitmapFromResId(R.drawable.kaehina09);

        setupBackgroundBitmapFromResId(R.drawable.kaeyuu01);
        setupBackgroundBitmapFromResId(R.drawable.kaeyuu02);
        setupBackgroundBitmapFromResId(R.drawable.kaeyuu03);

        setupBackgroundBitmapFromResId(R.drawable.aokae01);
        setupBackgroundBitmapFromResId(R.drawable.aokae02);
        setupBackgroundBitmapFromResId(R.drawable.aokae03);
        setupBackgroundBitmapFromResId(R.drawable.kaeao01);
        setupBackgroundBitmapFromResId(R.drawable.kaeao02);
        setupBackgroundBitmapFromResId(R.drawable.kaeao03);
        setupBackgroundBitmapFromResId(R.drawable.kaeao04);
        setupBackgroundBitmapFromResId(R.drawable.kaeao05);

        setupBackgroundBitmapFromResId(R.drawable.kokoao01);
        setupBackgroundBitmapFromResId(R.drawable.kokoao02);
        setupBackgroundBitmapFromResId(R.drawable.kokoao03);
        setupBackgroundBitmapFromResId(R.drawable.kokoao04);
        setupBackgroundBitmapFromResId(R.drawable.kokoao05);
        setupBackgroundBitmapFromResId(R.drawable.kokoao06);
        setupBackgroundBitmapFromResId(R.drawable.kokoao07);
        setupBackgroundBitmapFromResId(R.drawable.kokoao08);
        setupBackgroundBitmapFromResId(R.drawable.kokoao09);
        setupBackgroundBitmapFromResId(R.drawable.kokoao10);
        setupBackgroundBitmapFromResId(R.drawable.kokoao11);
        setupBackgroundBitmapFromResId(R.drawable.kokoao12);
        setupBackgroundBitmapFromResId(R.drawable.kokoao13);
        setupBackgroundBitmapFromResId(R.drawable.kokoao14);
        setupBackgroundBitmapFromResId(R.drawable.kokoao15);
        setupBackgroundBitmapFromResId(R.drawable.kokoao16);
        setupBackgroundBitmapFromResId(R.drawable.kokoao17);
        setupBackgroundBitmapFromResId(R.drawable.kokoao18);
        setupBackgroundBitmapFromResId(R.drawable.kokoao19);
        setupBackgroundBitmapFromResId(R.drawable.kokoao20);
        setupBackgroundBitmapFromResId(R.drawable.kokoao21);
        setupBackgroundBitmapFromResId(R.drawable.kokoao22);
        setupBackgroundBitmapFromResId(R.drawable.kokoao23);
        setupBackgroundBitmapFromResId(R.drawable.kokoao24);
        setupBackgroundBitmapFromResId(R.drawable.kokoao25);
        setupBackgroundBitmapFromResId(R.drawable.kokoao26);

        setupBackgroundBitmapFromResId(R.drawable.kokohina01);
        setupBackgroundBitmapFromResId(R.drawable.kokohina02);
        setupBackgroundBitmapFromResId(R.drawable.kokohina03);
        setupBackgroundBitmapFromResId(R.drawable.kokohina04);
        setupBackgroundBitmapFromResId(R.drawable.kokohina05);
        setupBackgroundBitmapFromResId(R.drawable.kokohina06);
        setupBackgroundBitmapFromResId(R.drawable.kokohina07);
        setupBackgroundBitmapFromResId(R.drawable.kokohina08);
        setupBackgroundBitmapFromResId(R.drawable.kokohina09);
        setupBackgroundBitmapFromResId(R.drawable.kokohina10);
        setupBackgroundBitmapFromResId(R.drawable.kokohina11);
        setupBackgroundBitmapFromResId(R.drawable.kokohina12);
        setupBackgroundBitmapFromResId(R.drawable.kokohina13);
        setupBackgroundBitmapFromResId(R.drawable.kokohina14);

        setupBackgroundBitmapFromResId(R.drawable.kokokae01);
        setupBackgroundBitmapFromResId(R.drawable.kokokae02);
        setupBackgroundBitmapFromResId(R.drawable.kokokae03);
        setupBackgroundBitmapFromResId(R.drawable.kokokae04);
        setupBackgroundBitmapFromResId(R.drawable.kokokae05);
        setupBackgroundBitmapFromResId(R.drawable.kokokae06);
        setupBackgroundBitmapFromResId(R.drawable.kokokae07);
        setupBackgroundBitmapFromResId(R.drawable.kokokae08);
        setupBackgroundBitmapFromResId(R.drawable.kokokae09);
        setupBackgroundBitmapFromResId(R.drawable.kokokae10);
        setupBackgroundBitmapFromResId(R.drawable.kokokae11);
        setupBackgroundBitmapFromResId(R.drawable.kokokae12);

        setupBackgroundBitmapFromResId(R.drawable.hinahono01);

        setupBackgroundBitmapFromResId(R.drawable.hinata01);
        setupBackgroundBitmapFromResId(R.drawable.hinata02);
        setupBackgroundBitmapFromResId(R.drawable.hinata03);
        setupBackgroundBitmapFromResId(R.drawable.hinata04);
        setupBackgroundBitmapFromResId(R.drawable.hinata05);
        setupBackgroundBitmapFromResId(R.drawable.hinata06);
        setupBackgroundBitmapFromResId(R.drawable.hinata07);
        setupBackgroundBitmapFromResId(R.drawable.hinata08);
        setupBackgroundBitmapFromResId(R.drawable.hinata09);
        setupBackgroundBitmapFromResId(R.drawable.hinata10);
        setupBackgroundBitmapFromResId(R.drawable.hinata11);
        setupBackgroundBitmapFromResId(R.drawable.hinata12);
        setupBackgroundBitmapFromResId(R.drawable.hinata13);
        setupBackgroundBitmapFromResId(R.drawable.hinata14);
        setupBackgroundBitmapFromResId(R.drawable.hinata15);
        setupBackgroundBitmapFromResId(R.drawable.hinata16);
        setupBackgroundBitmapFromResId(R.drawable.hinata17);
        setupBackgroundBitmapFromResId(R.drawable.hinata18);
        setupBackgroundBitmapFromResId(R.drawable.hinata19);
        setupBackgroundBitmapFromResId(R.drawable.hinata20);
        setupBackgroundBitmapFromResId(R.drawable.hinata21);
        setupBackgroundBitmapFromResId(R.drawable.hinata22);
        setupBackgroundBitmapFromResId(R.drawable.hinata23);
        setupBackgroundBitmapFromResId(R.drawable.hinata24);
        setupBackgroundBitmapFromResId(R.drawable.hinata25);
        setupBackgroundBitmapFromResId(R.drawable.hinata26);
        setupBackgroundBitmapFromResId(R.drawable.hinata27);
        setupBackgroundBitmapFromResId(R.drawable.hinata28);
        setupBackgroundBitmapFromResId(R.drawable.hinata29);
        setupBackgroundBitmapFromResId(R.drawable.hinata30);
        setupBackgroundBitmapFromResId(R.drawable.hinata31);
        setupBackgroundBitmapFromResId(R.drawable.hinata32);
        setupBackgroundBitmapFromResId(R.drawable.hinata33);
        setupBackgroundBitmapFromResId(R.drawable.hinata34);
        setupBackgroundBitmapFromResId(R.drawable.hinata35);
        setupBackgroundBitmapFromResId(R.drawable.hinata36);
        setupBackgroundBitmapFromResId(R.drawable.hinata37);
        setupBackgroundBitmapFromResId(R.drawable.hinata38);
        setupBackgroundBitmapFromResId(R.drawable.hinata38);
        setupBackgroundBitmapFromResId(R.drawable.hinata39);
        setupBackgroundBitmapFromResId(R.drawable.hinata40);
        setupBackgroundBitmapFromResId(R.drawable.hinata41);
        setupBackgroundBitmapFromResId(R.drawable.hinata42);
        setupBackgroundBitmapFromResId(R.drawable.hinata43);
        setupBackgroundBitmapFromResId(R.drawable.hinata44);
        setupBackgroundBitmapFromResId(R.drawable.hinata45);
        setupBackgroundBitmapFromResId(R.drawable.hinata46);
        setupBackgroundBitmapFromResId(R.drawable.hinata47);
        setupBackgroundBitmapFromResId(R.drawable.hinata48);
        setupBackgroundBitmapFromResId(R.drawable.hinata49);
        setupBackgroundBitmapFromResId(R.drawable.hinata50);
        setupBackgroundBitmapFromResId(R.drawable.hinata51);
        setupBackgroundBitmapFromResId(R.drawable.hinata52);
        setupBackgroundBitmapFromResId(R.drawable.hinata53);
        setupBackgroundBitmapFromResId(R.drawable.hinata54);
        setupBackgroundBitmapFromResId(R.drawable.hinata55);
        setupBackgroundBitmapFromResId(R.drawable.hinata56);
        setupBackgroundBitmapFromResId(R.drawable.hinata57);
        setupBackgroundBitmapFromResId(R.drawable.hinata58);
        setupBackgroundBitmapFromResId(R.drawable.hinata59);
        setupBackgroundBitmapFromResId(R.drawable.hinata60);
        setupBackgroundBitmapFromResId(R.drawable.hinata61);
        setupBackgroundBitmapFromResId(R.drawable.hinata62);
        setupBackgroundBitmapFromResId(R.drawable.hinata63);
        setupBackgroundBitmapFromResId(R.drawable.hinata64);
        setupBackgroundBitmapFromResId(R.drawable.hinata65);
        setupBackgroundBitmapFromResId(R.drawable.hinata66);
        setupBackgroundBitmapFromResId(R.drawable.hinata67);
        setupBackgroundBitmapFromResId(R.drawable.hinata68);
        setupBackgroundBitmapFromResId(R.drawable.hinata69);
        setupBackgroundBitmapFromResId(R.drawable.hinata70);
        setupBackgroundBitmapFromResId(R.drawable.hinata71);
        setupBackgroundBitmapFromResId(R.drawable.hinata72);
        setupBackgroundBitmapFromResId(R.drawable.hinata73);
        setupBackgroundBitmapFromResId(R.drawable.hinata74);
        setupBackgroundBitmapFromResId(R.drawable.hinata75);
        setupBackgroundBitmapFromResId(R.drawable.hinata76);
        setupBackgroundBitmapFromResId(R.drawable.hinata77);
        setupBackgroundBitmapFromResId(R.drawable.hinata78);
        setupBackgroundBitmapFromResId(R.drawable.hinata79);
        setupBackgroundBitmapFromResId(R.drawable.hinata80);
        setupBackgroundBitmapFromResId(R.drawable.hinata81);
        setupBackgroundBitmapFromResId(R.drawable.hinata82);
        setupBackgroundBitmapFromResId(R.drawable.hinata83);
        setupBackgroundBitmapFromResId(R.drawable.hinata84);
        setupBackgroundBitmapFromResId(R.drawable.hinata85);
        setupBackgroundBitmapFromResId(R.drawable.hinata86);
        setupBackgroundBitmapFromResId(R.drawable.hinata87);
        setupBackgroundBitmapFromResId(R.drawable.hinata88);
        setupBackgroundBitmapFromResId(R.drawable.hinata89);
        setupBackgroundBitmapFromResId(R.drawable.hinata90);
        setupBackgroundBitmapFromResId(R.drawable.hinata91);
        setupBackgroundBitmapFromResId(R.drawable.hinata92);
        setupBackgroundBitmapFromResId(R.drawable.hinata93);
        setupBackgroundBitmapFromResId(R.drawable.hinata94);
        setupBackgroundBitmapFromResId(R.drawable.hinata95);
        setupBackgroundBitmapFromResId(R.drawable.hinata96);
        setupBackgroundBitmapFromResId(R.drawable.hinata97);
        setupBackgroundBitmapFromResId(R.drawable.hinata98);
        setupBackgroundBitmapFromResId(R.drawable.hinata99);
        setupBackgroundBitmapFromResId(R.drawable.hinata100);
        setupBackgroundBitmapFromResId(R.drawable.hinata101);
        setupBackgroundBitmapFromResId(R.drawable.hinata102);
        setupBackgroundBitmapFromResId(R.drawable.hinata103);
        setupBackgroundBitmapFromResId(R.drawable.hinata104);
        setupBackgroundBitmapFromResId(R.drawable.hinata105);
        setupBackgroundBitmapFromResId(R.drawable.hinata106);
        setupBackgroundBitmapFromResId(R.drawable.hinata107);
        setupBackgroundBitmapFromResId(R.drawable.hinata108);
        setupBackgroundBitmapFromResId(R.drawable.hinata109);
        setupBackgroundBitmapFromResId(R.drawable.hinata110);
        setupBackgroundBitmapFromResId(R.drawable.hinata111);
        setupBackgroundBitmapFromResId(R.drawable.hinata112);
        setupBackgroundBitmapFromResId(R.drawable.hinata113);
        setupBackgroundBitmapFromResId(R.drawable.hinata114);
        setupBackgroundBitmapFromResId(R.drawable.hinata115);
        setupBackgroundBitmapFromResId(R.drawable.hinata116);
        setupBackgroundBitmapFromResId(R.drawable.hinata117);
        setupBackgroundBitmapFromResId(R.drawable.hinata118);
        setupBackgroundBitmapFromResId(R.drawable.hinata119);
        setupBackgroundBitmapFromResId(R.drawable.hinata120);
        setupBackgroundBitmapFromResId(R.drawable.hinata121);
        setupBackgroundBitmapFromResId(R.drawable.hinata122);
        setupBackgroundBitmapFromResId(R.drawable.hinata123);
        setupBackgroundBitmapFromResId(R.drawable.hinata124);
        setupBackgroundBitmapFromResId(R.drawable.hinata125);
        setupBackgroundBitmapFromResId(R.drawable.hinata126);
        setupBackgroundBitmapFromResId(R.drawable.hinata127);
        setupBackgroundBitmapFromResId(R.drawable.hinata128);
        setupBackgroundBitmapFromResId(R.drawable.hinata129);
        setupBackgroundBitmapFromResId(R.drawable.hinata130);
        setupBackgroundBitmapFromResId(R.drawable.hinata131);
        setupBackgroundBitmapFromResId(R.drawable.hinata132);
        setupBackgroundBitmapFromResId(R.drawable.hinata133);
        setupBackgroundBitmapFromResId(R.drawable.hinata134);
        setupBackgroundBitmapFromResId(R.drawable.hinata135);
        setupBackgroundBitmapFromResId(R.drawable.hinata136);
        setupBackgroundBitmapFromResId(R.drawable.hinata137);
        setupBackgroundBitmapFromResId(R.drawable.hinata138);
        setupBackgroundBitmapFromResId(R.drawable.hinata139);
        setupBackgroundBitmapFromResId(R.drawable.hinata140);
        setupBackgroundBitmapFromResId(R.drawable.hinata141);
        setupBackgroundBitmapFromResId(R.drawable.hinata142);
        setupBackgroundBitmapFromResId(R.drawable.hinata143);
        setupBackgroundBitmapFromResId(R.drawable.hinata144);
        setupBackgroundBitmapFromResId(R.drawable.hinata145);
        setupBackgroundBitmapFromResId(R.drawable.hinata146);
        setupBackgroundBitmapFromResId(R.drawable.hinata147);
        setupBackgroundBitmapFromResId(R.drawable.hinata148);
        setupBackgroundBitmapFromResId(R.drawable.hinata149);
        setupBackgroundBitmapFromResId(R.drawable.hinata150);
        setupBackgroundBitmapFromResId(R.drawable.hinata151);
        setupBackgroundBitmapFromResId(R.drawable.hinata152);
        setupBackgroundBitmapFromResId(R.drawable.hinata153);
        setupBackgroundBitmapFromResId(R.drawable.hinata154);
        setupBackgroundBitmapFromResId(R.drawable.hinata155);
        setupBackgroundBitmapFromResId(R.drawable.hinata156);
        setupBackgroundBitmapFromResId(R.drawable.hinata157);
        setupBackgroundBitmapFromResId(R.drawable.hinata158);
        setupBackgroundBitmapFromResId(R.drawable.hinata159);
        setupBackgroundBitmapFromResId(R.drawable.hinata160);
        setupBackgroundBitmapFromResId(R.drawable.hinata161);
        setupBackgroundBitmapFromResId(R.drawable.hinata162);
        setupBackgroundBitmapFromResId(R.drawable.hinata163);
        setupBackgroundBitmapFromResId(R.drawable.hinata164);
        setupBackgroundBitmapFromResId(R.drawable.hinata165);
        setupBackgroundBitmapFromResId(R.drawable.hinata166);
        setupBackgroundBitmapFromResId(R.drawable.hinata167);
        setupBackgroundBitmapFromResId(R.drawable.hinata168);
        setupBackgroundBitmapFromResId(R.drawable.hinata169);
        setupBackgroundBitmapFromResId(R.drawable.hinata170);
        setupBackgroundBitmapFromResId(R.drawable.hinata171);
        setupBackgroundBitmapFromResId(R.drawable.hinata172);
        setupBackgroundBitmapFromResId(R.drawable.hinata173);
        setupBackgroundBitmapFromResId(R.drawable.hinata174);
        setupBackgroundBitmapFromResId(R.drawable.hinata175);
        setupBackgroundBitmapFromResId(R.drawable.hinata176);
        setupBackgroundBitmapFromResId(R.drawable.hinata177);
        setupBackgroundBitmapFromResId(R.drawable.hinata178);
        setupBackgroundBitmapFromResId(R.drawable.hinata179);
        setupBackgroundBitmapFromResId(R.drawable.hinata180);
        setupBackgroundBitmapFromResId(R.drawable.hinata181);
        setupBackgroundBitmapFromResId(R.drawable.hinata182);
        setupBackgroundBitmapFromResId(R.drawable.hinata183);
        setupBackgroundBitmapFromResId(R.drawable.hinata184);
        setupBackgroundBitmapFromResId(R.drawable.hinata185);
        setupBackgroundBitmapFromResId(R.drawable.hinata186);
        setupBackgroundBitmapFromResId(R.drawable.hinata187);
        setupBackgroundBitmapFromResId(R.drawable.hinata188);
        setupBackgroundBitmapFromResId(R.drawable.hinata189);
        setupBackgroundBitmapFromResId(R.drawable.hinata190);
        setupBackgroundBitmapFromResId(R.drawable.hinata191);
        setupBackgroundBitmapFromResId(R.drawable.hinata192);
        setupBackgroundBitmapFromResId(R.drawable.hinata193);
        setupBackgroundBitmapFromResId(R.drawable.hinata194);
        setupBackgroundBitmapFromResId(R.drawable.hinata195);
        setupBackgroundBitmapFromResId(R.drawable.hinata196);
        setupBackgroundBitmapFromResId(R.drawable.hinata197);
        setupBackgroundBitmapFromResId(R.drawable.hinata198);
        setupBackgroundBitmapFromResId(R.drawable.hinata199);
        setupBackgroundBitmapFromResId(R.drawable.hinata200);
        setupBackgroundBitmapFromResId(R.drawable.hinata201);
        setupBackgroundBitmapFromResId(R.drawable.hinata202);
        setupBackgroundBitmapFromResId(R.drawable.hinata203);
        setupBackgroundBitmapFromResId(R.drawable.hinata204);
        setupBackgroundBitmapFromResId(R.drawable.hinata205);
        setupBackgroundBitmapFromResId(R.drawable.hinata206);
        setupBackgroundBitmapFromResId(R.drawable.hinata207);
        setupBackgroundBitmapFromResId(R.drawable.hinata208);
        setupBackgroundBitmapFromResId(R.drawable.hinata209);
        setupBackgroundBitmapFromResId(R.drawable.hinata210);
        setupBackgroundBitmapFromResId(R.drawable.hinata211);
        setupBackgroundBitmapFromResId(R.drawable.hinata212);
        setupBackgroundBitmapFromResId(R.drawable.hinata213);
        setupBackgroundBitmapFromResId(R.drawable.hinata214);
        setupBackgroundBitmapFromResId(R.drawable.hinata215);
        setupBackgroundBitmapFromResId(R.drawable.hinata216);
        setupBackgroundBitmapFromResId(R.drawable.hinata217);
        setupBackgroundBitmapFromResId(R.drawable.hinata218);
        setupBackgroundBitmapFromResId(R.drawable.hinata219);
        setupBackgroundBitmapFromResId(R.drawable.hinata220);
        setupBackgroundBitmapFromResId(R.drawable.hinata221);
        setupBackgroundBitmapFromResId(R.drawable.hinata222);
        setupBackgroundBitmapFromResId(R.drawable.hinata223);
        setupBackgroundBitmapFromResId(R.drawable.hinata224);
        setupBackgroundBitmapFromResId(R.drawable.hinata225);
        setupBackgroundBitmapFromResId(R.drawable.hinata226);
        setupBackgroundBitmapFromResId(R.drawable.hinata227);
        setupBackgroundBitmapFromResId(R.drawable.hinata228);
        setupBackgroundBitmapFromResId(R.drawable.hinata229);
        setupBackgroundBitmapFromResId(R.drawable.hinata230);
        setupBackgroundBitmapFromResId(R.drawable.hinata231);
        setupBackgroundBitmapFromResId(R.drawable.hinata232);
        setupBackgroundBitmapFromResId(R.drawable.hinata233);
        setupBackgroundBitmapFromResId(R.drawable.hinata234);
        setupBackgroundBitmapFromResId(R.drawable.hinata235);
        setupBackgroundBitmapFromResId(R.drawable.hinata236);
        setupBackgroundBitmapFromResId(R.drawable.hinata237);
        setupBackgroundBitmapFromResId(R.drawable.hinata238);
        setupBackgroundBitmapFromResId(R.drawable.hinata238);
        setupBackgroundBitmapFromResId(R.drawable.hinata239);
        setupBackgroundBitmapFromResId(R.drawable.hinata240);
        setupBackgroundBitmapFromResId(R.drawable.hinata241);
        setupBackgroundBitmapFromResId(R.drawable.hinata242);
        setupBackgroundBitmapFromResId(R.drawable.hinata243);
        setupBackgroundBitmapFromResId(R.drawable.hinata244);
        setupBackgroundBitmapFromResId(R.drawable.hinata245);
        setupBackgroundBitmapFromResId(R.drawable.hinata246);
        setupBackgroundBitmapFromResId(R.drawable.hinata247);
        setupBackgroundBitmapFromResId(R.drawable.hinata248);
        setupBackgroundBitmapFromResId(R.drawable.hinata249);
        setupBackgroundBitmapFromResId(R.drawable.hinata250);
        setupBackgroundBitmapFromResId(R.drawable.hinata251);
        setupBackgroundBitmapFromResId(R.drawable.hinata252);
        setupBackgroundBitmapFromResId(R.drawable.hinata253);
        setupBackgroundBitmapFromResId(R.drawable.hinata254);
        setupBackgroundBitmapFromResId(R.drawable.hinata255);
        setupBackgroundBitmapFromResId(R.drawable.hinata256);
        setupBackgroundBitmapFromResId(R.drawable.hinata257);
        setupBackgroundBitmapFromResId(R.drawable.hinata258);
        setupBackgroundBitmapFromResId(R.drawable.hinata259);
        setupBackgroundBitmapFromResId(R.drawable.hinata260);
        setupBackgroundBitmapFromResId(R.drawable.hinata261);
        setupBackgroundBitmapFromResId(R.drawable.hinata262);
        setupBackgroundBitmapFromResId(R.drawable.hinata263);

        setupBackgroundBitmapFromResId(R.drawable.lets1);
        setupBackgroundBitmapFromResId(R.drawable.lets2);

        setupBackgroundBitmapFromResId(R.drawable.kokona01);
        setupBackgroundBitmapFromResId(R.drawable.kokona02);
        setupBackgroundBitmapFromResId(R.drawable.kokona03);
        setupBackgroundBitmapFromResId(R.drawable.kokona04);
        setupBackgroundBitmapFromResId(R.drawable.kokona05);
        setupBackgroundBitmapFromResId(R.drawable.kokona06);
        setupBackgroundBitmapFromResId(R.drawable.kokona07);
        setupBackgroundBitmapFromResId(R.drawable.kokona08);
        setupBackgroundBitmapFromResId(R.drawable.kokona09);
        setupBackgroundBitmapFromResId(R.drawable.kokona10);
        setupBackgroundBitmapFromResId(R.drawable.kokona11);
        setupBackgroundBitmapFromResId(R.drawable.kokona12);
        setupBackgroundBitmapFromResId(R.drawable.kokona13);
        setupBackgroundBitmapFromResId(R.drawable.kokona14);
        setupBackgroundBitmapFromResId(R.drawable.kokona15);
        setupBackgroundBitmapFromResId(R.drawable.kokona16);
        setupBackgroundBitmapFromResId(R.drawable.kokona17);
        setupBackgroundBitmapFromResId(R.drawable.kokona18);
        setupBackgroundBitmapFromResId(R.drawable.kokona19);
        setupBackgroundBitmapFromResId(R.drawable.kokona20);
        setupBackgroundBitmapFromResId(R.drawable.kokona21);
        setupBackgroundBitmapFromResId(R.drawable.kokona22);
        setupBackgroundBitmapFromResId(R.drawable.kokona23);
        setupBackgroundBitmapFromResId(R.drawable.kokona24);
        setupBackgroundBitmapFromResId(R.drawable.kokona25);
        setupBackgroundBitmapFromResId(R.drawable.kokona26);
        setupBackgroundBitmapFromResId(R.drawable.kokona27);
        setupBackgroundBitmapFromResId(R.drawable.kokona28);
        setupBackgroundBitmapFromResId(R.drawable.kokona29);
        setupBackgroundBitmapFromResId(R.drawable.kokona30);
        setupBackgroundBitmapFromResId(R.drawable.kokona31);
        setupBackgroundBitmapFromResId(R.drawable.kokona32);
        setupBackgroundBitmapFromResId(R.drawable.kokona33);
        setupBackgroundBitmapFromResId(R.drawable.kokona34);
        setupBackgroundBitmapFromResId(R.drawable.kokona35);
        setupBackgroundBitmapFromResId(R.drawable.kokona36);
        setupBackgroundBitmapFromResId(R.drawable.kokona37);
        setupBackgroundBitmapFromResId(R.drawable.kokona38);
        setupBackgroundBitmapFromResId(R.drawable.kokona39);
        setupBackgroundBitmapFromResId(R.drawable.kokona40);
        setupBackgroundBitmapFromResId(R.drawable.kokona41);
        setupBackgroundBitmapFromResId(R.drawable.kokona42);
        setupBackgroundBitmapFromResId(R.drawable.kokona43);
        setupBackgroundBitmapFromResId(R.drawable.kokona44);
        setupBackgroundBitmapFromResId(R.drawable.kokona45);
        setupBackgroundBitmapFromResId(R.drawable.kokona46);
        setupBackgroundBitmapFromResId(R.drawable.kokona47);
        setupBackgroundBitmapFromResId(R.drawable.kokona48);
        setupBackgroundBitmapFromResId(R.drawable.kokona49);
        setupBackgroundBitmapFromResId(R.drawable.kokona50);
        setupBackgroundBitmapFromResId(R.drawable.kokona51);
        setupBackgroundBitmapFromResId(R.drawable.kokona52);
        setupBackgroundBitmapFromResId(R.drawable.kokona53);
        setupBackgroundBitmapFromResId(R.drawable.kokona54);
        setupBackgroundBitmapFromResId(R.drawable.kokona55);
        setupBackgroundBitmapFromResId(R.drawable.kokona56);
        setupBackgroundBitmapFromResId(R.drawable.kokona57);
        setupBackgroundBitmapFromResId(R.drawable.kokona58);
        setupBackgroundBitmapFromResId(R.drawable.kokona59);
        setupBackgroundBitmapFromResId(R.drawable.kokona60);
        setupBackgroundBitmapFromResId(R.drawable.kokona61);
        setupBackgroundBitmapFromResId(R.drawable.kokona62);
        setupBackgroundBitmapFromResId(R.drawable.kokona63);
        setupBackgroundBitmapFromResId(R.drawable.kokona64);
        setupBackgroundBitmapFromResId(R.drawable.kokona65);
        setupBackgroundBitmapFromResId(R.drawable.kokona66);
        setupBackgroundBitmapFromResId(R.drawable.kokona67);
        setupBackgroundBitmapFromResId(R.drawable.kokona68);
        setupBackgroundBitmapFromResId(R.drawable.kokona69);
        setupBackgroundBitmapFromResId(R.drawable.kokona70);
        setupBackgroundBitmapFromResId(R.drawable.kokona71);
        setupBackgroundBitmapFromResId(R.drawable.kokona72);
        setupBackgroundBitmapFromResId(R.drawable.kokona73);
        setupBackgroundBitmapFromResId(R.drawable.kokona74);
        setupBackgroundBitmapFromResId(R.drawable.kokona75);

        setupBackgroundBitmapFromResId(R.drawable.kuma);

        System.gc();
**/
    }

    /**
     *   背景画像の記憶(ビットマップのリソースID記憶)
     *
     * @param id         画像のリソースID
     */
    private void setupBackgroundBitmapFromResId(int id)
    {
        nofBitmaps++;
        try
        {
            mBackgroundBitmapIds.put(nofBitmaps, id);
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
                Log.w(TAG, "setupBackgroundBitmapFromResId exception ("+ id + ") "+ ex.getMessage());
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
                    // 一度読みだして、ビットマップと認識した場合だけファイル名をビットマップファイルとして記憶する
                    nofBitmaps++;
                    mBackgroundBitmapFileNames.put(nofBitmaps, fileName.getName());
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
     *   decide background bitmap (index number)
     *
     * @return  index number
     */
    private int decideBackgroundBitmap()
    {
        if (!isRandom)
        {
            // 画像を順番に表示する
            currentBackgroundBitmap++;
            if (currentBackgroundBitmap > nofBitmaps)
            {
                currentBackgroundBitmap = 1;
            }
            return (currentBackgroundBitmap);
        }

        // 乱数で表示する画像を決定する
        return (randomGenerator.nextInt(nofBitmaps) + 1);
    }

    /**
     *   returns background bitmap (scaled)
     *
     */
    Bitmap getBackgroundScaledBitmap(int width, int height)
    {
        // ヒープメモリ消費削減のため、Bitmapは毎回取得する (これでも描画が間に合うようだ...)
        // 表示するビットマップデータを特定する
        int backgroundBitmapIndex = decideBackgroundBitmap();
        Bitmap bitmapToShow;
        try
        {
            if (doUseExternalFiles)
            {
                // 外部ファイルからビットマップデータを読み出す
                String fileName = mBackgroundBitmapFileNames.get(backgroundBitmapIndex);
                String targetFile = fileUtility.getGokigenDirectory() + "/all/" + fileName;
                bitmapToShow = BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(targetFile)));
            }
            else
            {
                // リソースからビットマップデータを読み出す
                int resId = mBackgroundBitmapIds.get(backgroundBitmapIndex);
                Drawable backgroundDrawable = context.getResources().getDrawable(resId);
                bitmapToShow = ((BitmapDrawable) backgroundDrawable).getBitmap();
            }
        }
        catch (Exception e)
        {
            // エラーが出た場合には、デフォルトのビットマップを読みだして表示する
            Drawable backgroundDrawable = context.getResources().getDrawable(R.drawable.background3);
            bitmapToShow = ((BitmapDrawable) backgroundDrawable).getBitmap();
        }

        //  表示するビットマップのサイズを画面サイズに合わせてリサイズしてビットマップデータを返す
        Bitmap scaledBitmap = null;
        try
        {
            scaledBitmap = Bitmap.createScaledBitmap(bitmapToShow, width, height, true /* filter */);
        }
        catch (Exception ee)
        {
            if (Log.isLoggable(TAG, Log.WARN))
            {
                Log.w(TAG, "getBackgroundScaledBitmap(): " + ee.getMessage());
            }
        }
        return (scaledBitmap);
    }

    /**
     *
     */
    private void initializeDrawTextObjects()
    {
        Resources resources = context.getResources();

        // create graphic styles
        mTimePaint = createTextPaint(Color.parseColor("white"), BOLD_TYPEFACE);
        mBackPaint = createTextPaint(Color.argb(80, 0, 0, 0), NORMAL_TYPEFACE);

        // default watch face is square
        mIsRoundShape = false;
        mTextSize = resources.getDimension(R.dimen.text_size);
        mYOffset = resources.getDimension(R.dimen.x_offset);
    }

    /**
     *  文字表示用のPaintクラス作成メソッド
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
