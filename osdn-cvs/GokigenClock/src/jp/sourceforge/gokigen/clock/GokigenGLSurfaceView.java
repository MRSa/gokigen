package jp.sourceforge.gokigen.clock;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GokigenGLSurfaceView extends GLSurfaceView
{
    private GokigenGLUtilities mGLutil = null;
    private SquareDrawer mSquareDrawer = null;
    
    /**
     *  コンストラクタ
     * @param context
     */
    public GokigenGLSurfaceView(Context context)
    {
        super(context);
        initializeSelf(context, null);
    }

    /**
     *  コンストラクタ (レイアウトマネージャ経由で呼び出されたときに利用)
     * @param context
     * @param attrs
     */
    public GokigenGLSurfaceView(Context context, AttributeSet attrs)
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
     *   クラスの初期化処理...レンダラを設定する
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
        //setEGLConfigChooser(false);        // これだと画面透過はダメ！
    	setEGLConfigChooser(5,6,5, 8, 16, 0);
        setFocusable(true);
        setFocusableInTouchMode(true);

        // OpenGLレンダラ用のユーティリティを生成
        mGLutil = new GokigenGLUtilities(context);

        // レンダラを設定する
        mSquareDrawer = new SquareDrawer(mGLutil);
        GokigenGLRenderer renderer = new GokigenGLRenderer(context, mSquareDrawer);
        renderer.setNumberDrawer(new NumberDrawer(mGLutil, new TimeValueProvider()));
        setRenderer(renderer);

        // 画面を透過させる
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }
}
