package jp.sourceforge.gokigen.clock;

import javax.microedition.khronos.opengles.GL10;
import android.os.SystemClock;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public class SquareDrawer implements IGraphicsDrawer
{
    private final static int VERTS = 4;

    private FloatBuffer mFVertexBuffer = null;
    private FloatBuffer mTexBuffer     = null;
    private ShortBuffer mIndexBuffer   = null;
    
    private IOrientationHolder mOrientationHolder = null;

    private int            mDroidTextureID    = 0;

    private GokigenGLUtilities mGLutil = null;

    /**
     *   �R���X�g���N�^
     */
    public SquareDrawer(GokigenGLUtilities glUtil)
    {
        mGLutil = glUtil;
    }

    /**
     *  �I���G���e�[�V�����z���_�[��ݒ肷��
     * @param holder
     */
    public void setOrientationHolder(IOrientationHolder holder)
    {
        mOrientationHolder = holder;
    }

    /**
     *   �I�u�W�F�N�g�̏���
     */
    public void prepareObject()
    {
        // Buffers to be passed to gl*Pointer() functions
        // must be direct, i.e., they must be placed on the
        // native heap where the garbage collector cannot
        // move them.
        //
        // Buffers with multi-byte datatypes (e.g., short, int, float)
        // must have their byte order set to native order

        ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();

        ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexBuffer = tbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 3);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();

        // A unit-sided square centered on the origin.
        float[] coords =
        {
            // X, Y, Z
            -0.5f, -0.5f,        0.0f,
             0.5f, -0.5f,        0.0f,
            -0.5f,  0.5f,        0.0f,
             0.5f,  0.5f,        0.0f,
        };

        for (int i = 0; i < VERTS; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                mFVertexBuffer.put(coords[i * 3 + j] * 2.0f);
            }
        }

        for (int i = 0; i < VERTS; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                mTexBuffer.put(coords[i * 3 + j] * 2.0f + 0.5f);
            }
        }

        for (int i = 0; i < VERTS; i++)
        {
            mIndexBuffer.put((short) i);
        }

        mFVertexBuffer.position(0);
        mTexBuffer.position(0);
        mIndexBuffer.position(0);
    }

    /**
     *  drawer�̏���...
     */
    public void prepareDrawer(GL10 gl)
    {
        mDroidTextureID = mGLutil.prepareTexure(gl, R.drawable.tex);
    }
    
    /**
     *  �I�u�W�F�N�g��`�悷��
     */
    public void drawObject(GL10 gl)
    {
        gl.glFrontFace(GL10.GL_CCW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, VERTS, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    }
    
    /**
     *  �I�u�W�F�N�g�`��O�̏������s��
     * 
     */
    public void preprocessDraw(GL10 gl)
    {
        gl.glActiveTexture(GL10.GL_TEXTURE0);                     // �e�N�X�`�����j�b�g0��ݒ�
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mDroidTextureID);    // �e�N�X�`�����o�C���h (mTextureID�̃r�b�g�}�b�v�𗘗p)
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);  // �e�N�X�`����s�������ɌJ��Ԃ�
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);  // �e�N�X�`����t�������ɌJ��Ԃ�

        // �f���p�̉�]
        if (mOrientationHolder == null)
        {                   
            // ��]�p�x�̎w�� (�P�� : �x)
            long time = SystemClock.uptimeMillis() % 4000L;
            float angle = 0.090f * ((int) time) * 2.0f;
            gl.glRotatef(angle, 0, 1.0f, 0.0f);  // Y������ɉ�]
            
            return;
        }

        // �Z���T����̏������Ƃɂ��ĉ�]������
        float angle = 0.0f;
        angle = mOrientationHolder.getOrientationY();
        gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);  // X������ɉ�]
        
        angle = mOrientationHolder.getOrientationZ();
        gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);  // Y������ɉ�]

        angle = mOrientationHolder.getOrientationX();
        gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);  // Z������ɉ�]
        
        return;
    }
}
