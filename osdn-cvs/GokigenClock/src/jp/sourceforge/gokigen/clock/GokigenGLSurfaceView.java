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
        mSquareDrawer.setOrientationHolder(holder);
    }

    /**
     *   �N���X�̏���������...�����_����ݒ肷��
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
        //setEGLConfigChooser(false);        // ���ꂾ�Ɖ�ʓ��߂̓_���I
    	setEGLConfigChooser(5,6,5, 8, 16, 0);
        setFocusable(true);
        setFocusableInTouchMode(true);

        // OpenGL�����_���p�̃��[�e�B���e�B�𐶐�
        mGLutil = new GokigenGLUtilities(context);

        // �����_����ݒ肷��
        mSquareDrawer = new SquareDrawer(mGLutil);
        GokigenGLRenderer renderer = new GokigenGLRenderer(context, mSquareDrawer);
        renderer.setNumberDrawer(new NumberDrawer(mGLutil, new TimeValueProvider()));
        setRenderer(renderer);

        // ��ʂ𓧉߂�����
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }
}
