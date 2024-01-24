package jp.sourceforge.gokigen.capture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class NumberDrawer implements IGraphicsDrawer
{
    private GokigenGLUtilities mGLutil = null;
    private int mNumberTextureID = 0;
    
    private final static int VERTS = 4;

    private FloatBuffer mFVertexBuffer   = null;
    private ShortBuffer mIndexBuffer     = null;

    private FloatBuffer mShowVertBuffer  = null;
    private FloatBuffer mNumBuffer       = null;

    private INumberValueProvider mDataSource = null;
    
    /**
     *  �R���X�g���N�^
     */
	public NumberDrawer(GokigenGLUtilities glUtil, INumberValueProvider data)
    {
    	mGLutil = glUtil;
    	mDataSource = data;
    }

    /**
     *   �I�u�W�F�N�g�̏���
     */
    public void prepareObject()
    {
        ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();

        ByteBuffer sbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
        sbb.order(ByteOrder.nativeOrder());
        mShowVertBuffer = sbb.asFloatBuffer();

        ByteBuffer nbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
        nbb.order(ByteOrder.nativeOrder());
        mNumBuffer = nbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 3);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();

        // A unit-sided square centered on the origin.
        float[] coords =
        {
            // X, Y, Z
            0.25f, 0.25f,  1.0f,
            0.25f, 0.00f,  1.0f,
            0.00f, 0.25f,  1.0f,
            0.00f, 0.00f,  1.0f,
        };

        for (int i = 0; i < VERTS; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                mFVertexBuffer.put(coords[i * 3 + j] * 1.0f);
            }
        }
        mFVertexBuffer.position(0);

        for (int i = 0; i < VERTS; i++)
        {
            mIndexBuffer.put((short) i);
        }
        mIndexBuffer.position(0);
    }

    /**
     *   ���l��\������ꏊ�����肷��
     * @param index  ���ʒu (�E�ˍ��A�A�A��ԉE���[��)
     */
    private void decideNumberCoordinates(int index)
    {
        mFVertexBuffer.position(0);
        mShowVertBuffer.position(0);
        for (int i = 0; i < VERTS; i++)
        {
            for (int j = 0; j < 3; j++)
            {
            	// X = 1.20, Y = -2.0 ���s�ړ�
            	// (-0.50)...
            	float offset = 0;
            	if (j == 0)
            	{
            		offset = 1.20f + 0.25f * index;
            	}
            	else if (j == 1)
            	{
            		offset = -2.00f;
            	}
            	mShowVertBuffer.put(mFVertexBuffer.get() + offset);
            }
        }
        mShowVertBuffer.position(0);
        mFVertexBuffer.position(0);
    }
    
    /**
     *  �e�N�X�`���摜�̒�����A�^����ꂽ���l�ɑΉ�����摜��؂�o��
     * @param num  ���l
     */
    private void decideNumberTexture(int num)
    {
        float dataX = 0.00f;
        float dataY = 0.00f;

        dataX = (num % 4) * 0.25f;
        dataY = (num / 4) * 0.25f;

        mNumBuffer.position(0);

        mNumBuffer.put(dataX + 0.00f);   // X���W
        mNumBuffer.put(dataY + 0.00f);   // Y���W

        mNumBuffer.put(dataX + 0.00f);   // X���W
        mNumBuffer.put(dataY + 0.25f);   // Y���W
        
        mNumBuffer.put(dataX + 0.25f);   // X���W
        mNumBuffer.put(dataY + 0.00f);   // Y���W

        mNumBuffer.put(dataX + 0.25f);   // X���W
        mNumBuffer.put(dataY + 0.25f);   // Y���W

        mNumBuffer.position(0);
    }

    /**
     *  �����N���X(���̂Q)
     *  
     */
	public void prepareDrawer(GL10 gl)
	{
		mNumberTextureID = mGLutil.prepareTexure(gl, R.drawable.number);
	}
	
	/**
	 *  �`��O�̏��������s����
	 *  
	 */
	public void preprocessDraw(GL10 gl)
	{
		gl.glActiveTexture(GL10.GL_TEXTURE0);                     // �e�N�X�`�����j�b�g0��ݒ�
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mNumberTextureID);    // �e�N�X�`�����o�C���h (mTextureID�̃r�b�g�}�b�v�𗘗p)
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);  // �e�N�X�`����s�������ɌJ��Ԃ�
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);  // �e�N�X�`����t�������ɌJ��Ԃ�		
	}

    /**
     *  �`������s����
     *  
     */
	public void drawObject(GL10 gl)
	{
        // �f�[�^���X�V����
        mDataSource.update();

        // ����
        int digits = mDataSource.getNumberOfDigits();
        for (int index = 0; index < digits; index++)
        {
        	decideNumberCoordinates(index);
        	
		    gl.glFrontFace(GL10.GL_CCW);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mShowVertBuffer);
        
            decideNumberTexture(mDataSource.getNumber(index));

            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mNumBuffer);
            gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, VERTS, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        }
	}
}
