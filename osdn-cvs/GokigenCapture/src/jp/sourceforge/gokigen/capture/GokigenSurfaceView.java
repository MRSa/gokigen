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
     *  �R���X�g���N�^
     * @param context
     */
    public GokigenSurfaceView(Context context)
    {
        super(context);
        initializeSelf(context, null);
    }

    /**
     *  �R���X�g���N�^ (���C�A�E�g�}�l�[�W���o�R�ŌĂяo���ꂽ�Ƃ��ɗ��p)
     * @param context
     * @param attrs
     */
    public GokigenSurfaceView(Context context, AttributeSet attrs)
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
     *   �悭�킩��񂯂�ǂ��A�ǉ��̃V�[�P���X�ݒ���s��
     * @param parent
     */
    
    public void prepareAdditionalSequence(Activity parent)
    {
        // �Ƃ肠�����A�������p�ӁB
    }
    
    /**
     *   �N���X�̏���������...�����_����ݒ肷��
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
        //setEGLConfigChooser(false);
    	setEGLConfigChooser(5,6,5, 8, 16, 0);
        setFocusable(true);
        setFocusableInTouchMode(true);
        
        // OpenGL�����_���p�̃��[�e�B���e�B�𐶐�
        mGLutil = new GokigenGLUtilities(context);

        // �����_����ݒ肷��
        mSquareDrawer = new SquareDrawer(mGLutil);
        mRenderer = new GokigenGLRenderer(context, mSquareDrawer);
        mRenderer.setNumberDrawer(new NumberDrawer(mGLutil, new TimeValueProvider()));

        setRenderer(mRenderer);

        // ��ʂ𓧉߂�����
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }
    
    /**
     *   �T�[�t�F�C�X�����C�x���g�̏���
     * 
     */
    public void surfaceCreated(SurfaceHolder aHolder)
    {
    	super.surfaceCreated(aHolder);
    }

    /**
     *  �T�[�t�F�C�X�ύX�C�x���g�̏���
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
    	super.surfaceChanged(aHolder, format, width, height);
    }

    /**
     *  �T�[�t�F�C�X�J���C�x���g�̏���
     * 
     */
    public void surfaceDestroyed(SurfaceHolder aHolder)
    {
    	super.surfaceDestroyed(aHolder);
    }
}
