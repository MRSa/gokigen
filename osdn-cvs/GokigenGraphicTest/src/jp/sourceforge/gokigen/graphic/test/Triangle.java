package jp.sourceforge.gokigen.graphic.test;

import javax.microedition.khronos.opengles.GL10;

import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public class Triangle implements IGraphicsDrawer
{
    private final static int VERTS = 3;

    private FloatBuffer mFVertexBuffer = null;
    private FloatBuffer mTexBuffer     = null;
    private ShortBuffer mIndexBuffer   = null;
    
    private IOrientationHolder mOrientationHolder = null;

    /**
     *   コンストラクタ
     */
    public Triangle()
    {
    	//
    }

    /**
     *  オリエンテーションホルダーを設定する
     * @param holder
     */
    public void setOrientationHolder(IOrientationHolder holder)
    {
        mOrientationHolder = holder;
    }

    /**
     *   オブジェクトの準備
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

        ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();

        // A unit-sided equalateral triangle centered on the origin.
        float[] coords =
        {
            // X, Y, Z
            -0.5f, -0.25f,        0.0f,
             0.5f, -0.25f,        0.0f,
             0.0f,  0.559016994f, 0.0f
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
     *  オブジェクトを描画する
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
     *  オブジェクト描画前の処理を行う
     * 
     */
	public void preprocessDraw(GL10 gl)
	{
        if (mOrientationHolder == null)
        {	    		   
            // 回転角度の指定 (単位 : 度)
            long time = SystemClock.uptimeMillis() % 4000L;
            float angle = 0.090f * ((int) time) * 2.0f;
            gl.glRotatef(angle, 0, 1.0f, 0.0f);  // Y軸周りに回転
            
            return;
        }

        /** センサからの情報をもとにして回転させる **/
        float angle = 0.0f;
        angle = mOrientationHolder.getOrientationY();
        gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);  // X軸周りに回転
        
        angle = mOrientationHolder.getOrientationZ();
        gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);  // Y軸周りに回転

        angle = mOrientationHolder.getOrientationX();
        gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);  // Z軸周りに回転
        
        return;
	}
}
