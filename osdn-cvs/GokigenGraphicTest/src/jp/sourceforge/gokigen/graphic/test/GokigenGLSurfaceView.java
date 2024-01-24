package jp.sourceforge.gokigen.graphic.test;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GokigenGLSurfaceView extends GLSurfaceView
{
    private Triangle mTriangle = null;
//    private Square   mSquare   = null;
	
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
        mTriangle.setOrientationHolder(holder);
    }

    /**
     *   クラスの初期化処理...レンダラを設定する
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
        setEGLConfigChooser(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        
        // レンダラを設定する
        mTriangle = new Triangle();
        setRenderer(new GokigenGLRenderer(context, mTriangle));

//        mSquare = new Square();
//        setRenderer(new GokigenGLRenderer(context, mSquare));
    }
}
