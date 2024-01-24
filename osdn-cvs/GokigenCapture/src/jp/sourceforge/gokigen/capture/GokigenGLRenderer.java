package jp.sourceforge.gokigen.capture;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 *  OpenGL�̕`��N���X
 *  
 * @author MRSa
 *
 */
public class GokigenGLRenderer implements Renderer
{
    private IGraphicsDrawer    mDrawer        = null;
    private IGraphicsDrawer    mNumberDrawer  = null;
//    private Context            mContext       = null;

    /**
     *  �R���X�g���N�^
     * @param context
     */
    public GokigenGLRenderer(Context context, IGraphicsDrawer drawer)
    {
//        mContext  = context;
        mDrawer   = drawer;

        /** ���� **/
    	mDrawer.prepareObject();
    }
    
    /**
     *  ���l�`��N���X��ݒ肷��
     * @param drawer
     */
    public void setNumberDrawer(IGraphicsDrawer drawer)
    {
        // ���l��`�悷��N���X��ݒ肷��
    	mNumberDrawer = drawer;

    	/** ���� **/
    	mNumberDrawer.prepareObject();
    }

    /**
     *  ��������
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        gl.glDisable(GL10.GL_DITHER);

        /*
         * Some one-time OpenGL initialization can be made here
         * probably based on features of this particular context
         */
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

//        gl.glClearColor(.5f, .5f, .5f, 1);   // �w�i�F���D�F��
//        gl.glClearColor(.0f, .0f, .0f, 1);     // �w�i�F�����F��
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */
        mDrawer.prepareDrawer(gl);
        if (mNumberDrawer != null)
        {
            mNumberDrawer.prepareDrawer(gl);
        }
    }

    /**
     *  �`�揈��
     */
    public void onDrawFrame(GL10 gl)
    {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        gl.glDisable(GL10.GL_DITHER);                                                   // DITHER��OFF�ɂ���
//        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);  // �e�N�X�`���ƃ|���S���̐F���u�����h����

        /*
         * Usually, the first thing one might want to do is to clear
         * the screen. The most efficient way of doing this is to use
         * glClear().
         */
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);  // �X�N���[������������

        /*
         * Now we're ready to draw some 3D objects
         */
        gl.glMatrixMode(GL10.GL_MODELVIEW);   // ���f�����_�ɂ���
        gl.glLoadIdentity();                  // �P�ʍs����Z�b�g

        /** ���_�̐ݒ� : (0, 0, -5)�̈ʒu����A���_������B�������Y���Ƃ���(0, 1, 0) **/
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);             // ���_���W��ON
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);      // �e�N�X�`�����W�� ON

        // ���l�̕\��...
        if (mNumberDrawer != null)
        {
        	try
        	{
                mNumberDrawer.preprocessDraw(gl);
                mNumberDrawer.drawObject(gl);        	
        	}
        	catch (Exception ex)
        	{
        	}
        }

        // �\�����C��...
        mDrawer.preprocessDraw(gl);
        mDrawer.drawObject(gl);
    }

    /**
     *  ��ʃT�C�Y���ς�����Ƃ��̏���	
     */
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        gl.glViewport(0, 0, width, height);

        /*
         * Set our projection matrix. This doesn't have to be done
         * each time we draw, but usually a new projection needs to
         * be set when the viewport is resized.
         */
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);    
    }
}
