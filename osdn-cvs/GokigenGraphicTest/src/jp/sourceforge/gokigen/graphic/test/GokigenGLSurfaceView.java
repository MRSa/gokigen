package jp.sourceforge.gokigen.graphic.test;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GokigenGLSurfaceView extends GLSurfaceView
{
    private Triangle mTriangle = null;
//    private Square   mSquare   = null;
	
    /**
     *  �R���X�g���N�^
     * @param context
     */
    public GokigenGLSurfaceView(Context context)
    {
        super(context);
        initializeSelf(context, null);
    }

    /**
     *  �R���X�g���N�^ (���C�A�E�g�}�l�[�W���o�R�ŌĂяo���ꂽ�Ƃ��ɗ��p)
     * @param context
     * @param attrs
     */
    public GokigenGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeSelf(context, attrs);
    }

    /**
     *  ���z���_�[��ݒ肷��    
     * @param holder
     */
    public void setOrientationHolder(IOrientationHolder holder)
    {
        mTriangle.setOrientationHolder(holder);
    }

    /**
     *   �N���X�̏���������...�����_����ݒ肷��
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
        setEGLConfigChooser(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        
        // �����_����ݒ肷��
        mTriangle = new Triangle();
        setRenderer(new GokigenGLRenderer(context, mTriangle));

//        mSquare = new Square();
//        setRenderer(new GokigenGLRenderer(context, mSquare));
    }
}
