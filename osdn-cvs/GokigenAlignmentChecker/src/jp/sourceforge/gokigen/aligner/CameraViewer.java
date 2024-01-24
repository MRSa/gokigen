package jp.sourceforge.gokigen.aligner;

import android.content.Context;
import android.util.AttributeSet;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *  カメラ画像表示クラス (ん・ぱかさんのコードを参考にさせていただいた。)
 *    ⇒ http://www.saturn.dti.ne.jp/~npake/android/CameraEx/index.html
 *  
 *  ※ 他グラフィックス描画と同時表示するために、ちょいと加工。
 *    (次の場所にあったコードを参考にさせていただいた。)
 *   "Camera image as an OpenGL texture on top of the native camera viewfinder"
 *    ⇒ http://nhenze.net/?p=172
 *
 *  
 * @author MRSa
 *
 */
public class CameraViewer extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback
{
    private SurfaceHolder          holder = null;
    private Camera                 camera = null;
    private ICameraDataReceiver    cameraDataReceiver = null;
    
    /**
     *  コンストラクタ
     * @param context
     */
    public CameraViewer(Context context)
    {
        super(context);
        initializeSelf(context, null);
    }

    /**
     *  コンストラクタ (レイアウトマネージャ経由で呼び出されたときに利用)
     * @param context
     * @param attrs
     */
    public CameraViewer(Context context, AttributeSet attrs)
    {
        super(context, attrs);        
        initializeSelf(context, attrs);
    }
   
    /**
     *   クラスの初期化処理
     * @param context
     * @param attrs
     */
    private void initializeSelf(Context context, AttributeSet attrs)
    {
        // サーフェイスホルダーの生成        
        holder = getHolder();
        holder.addCallback(this);

        // プッシュバッファの指定
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    /**
     *   サーフェイス生成イベントの処理
     * 
     */
    public void surfaceCreated(SurfaceHolder aHolder)
    {
        synchronized (this)
        {
            // カメラの初期化とプレビュー開始
            try
            {
                camera = Camera.open();
                camera.setPreviewDisplay(aHolder);

                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(getWidth(), getHeight());
                //parameters.setPreviewFormat(PixelFormat.JPEG);
                camera.setParameters(parameters);

                camera.startPreview();
        		camera.setPreviewCallback(this);
            }
            catch (Exception ex)
            {
                //
                // カメラが設定できない場合には、何もしない
                // 
            }
        }
    }
     
    /**
     *  プレビューコールバック処理クラスを設定する
     * @param callback
     */
    public void setPreviewCallback(ICameraDataReceiver callback)
    {
    	cameraDataReceiver = callback;
    }    
    
    /**
     *  サーフェイス変更イベントの処理
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
    	// 何もしない (別のところで実施)
    }

    /**
     *  サーフェイス開放イベントの処理
     * 
     */
    public void surfaceDestroyed(SurfaceHolder aHolder)
    {
        synchronized (this)
        {
            // カメラのプレビュー停止処理
            try
            {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
                cameraDataReceiver = null;
            }
            catch (Exception ex)
            {
                //            
            }
            
            // 一応、ガベコレも入れておく...
            System.gc();
        }
    }

    /**
     *   カメラのプレビュー画像を受信したときの処理
     *   (他のプレビュー処理クラスにパススルーする)
     */
    public void onPreviewFrame(byte[] arg0, Camera arg1)
	{
        if (cameraDataReceiver != null)
        {
        	cameraDataReceiver.onPreviewFrame(arg0, arg1, getWidth(), getHeight());
        }
	}
}
