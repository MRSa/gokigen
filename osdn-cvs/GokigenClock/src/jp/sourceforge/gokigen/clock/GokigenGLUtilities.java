package jp.sourceforge.gokigen.clock;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 *   OpenGL利用用のユーティリティクラス！
 * @author MRSa
 *
 */
public class GokigenGLUtilities
{
    private Context         mContext          = null;

    /**
     *  コンストラクタ
     */
    public GokigenGLUtilities(Context context)
    {
        mContext  = context;
    }

    /**
     *  テクスチャの準備
     * @param gl
     * @param resourceId
     * @return
     */
    public int prepareTexure(GL10 gl, int resourceId)
    {
        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,     GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,     GL10.GL_CLAMP_TO_EDGE);

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

        /////////////////////////////////////////////////////////////////////
        InputStream is = mContext.getResources().openRawResource(resourceId);
        Bitmap bitmap;
        try
        {
            bitmap = BitmapFactory.decodeStream(is);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                // Ignore.
            }
        }
        try
        {
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        catch (Exception ex)
        {
            // ignore
        }
        return (textures[0]);
    }    
}
