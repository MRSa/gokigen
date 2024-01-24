package jp.sourceforge.gokigen.capture;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.app.Activity;

public class GokigenSurfaceView extends GLSurfaceView
{
	private GokigenGLUtilities mGLutil = null;
	private SquareDrawer mSquareDrawer = null;
	private GokigenGLRenderer mRenderer = null;

    /**
     *  コンストラクタ
     * @param context
     */
    public GokigenSurfaceView(Context context)
    {
        super(context);
        initializeSelf(context, null);
    }

    /**
     *  コンストラクタ (レイアウトマネージャ経由で呼び出されたときに利用)
     * @param context
     * @param attrs
     */
    public GokigenSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeSelf(context, attrs);
    }

    /**
     *  軸ホルダーを設定する    
     * @param holder
     */
    public void setOrientationHolder(IOrientationHolder holder)
    {
    	mSquareDrawer.setOrientationHolder(holder);
    }
    
    /**
     *   よくわからんけれども、追加のシーケンス設定を行う
     * @param parent
     */
    
    public void prepareAdditionalSequence(Activity parent)
    {
        // とりあえず、口だけ用意。
    }
    
    /**
     *   クラスの初期化処理...レンダラを設定する
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
        //setEGLConfigChooser(false);
    	setEGLConfigChooser(5,6,5, 8, 16, 0);
        setFocusable(true);
        setFocusableInTouchMode(true);
        
        // OpenGLレンダラ用のユーティリティを生成
        mGLutil = new GokigenGLUtilities(context);

        // レンダラを設定する
        mSquareDrawer = new SquareDrawer(mGLutil);
        mRenderer = new GokigenGLRenderer(context, mSquareDrawer);
        mRenderer.setNumberDrawer(new NumberDrawer(mGLutil, new TimeValueProvider()));

        setRenderer(mRenderer);

        // 画面を透過させる
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }
    
    /**
     *   サーフェイス生成イベントの処理
     * 
     */
    public void surfaceCreated(SurfaceHolder aHolder)
    {
    	super.surfaceCreated(aHolder);
    }

    /**
     *  サーフェイス変更イベントの処理
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
    	super.surfaceChanged(aHolder, format, width, height);
    }

    /**
     *  サーフェイス開放イベントの処理
     * 
     */
    public void surfaceDestroyed(SurfaceHolder aHolder)
    {
    	super.surfaceDestroyed(aHolder);
    }
}
