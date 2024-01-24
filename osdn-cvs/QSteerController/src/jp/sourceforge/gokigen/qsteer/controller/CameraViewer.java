package jp.sourceforge.gokigen.qsteer.controller;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
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
	private final int TEXTURE_NAME = 10;
	
    private SurfaceHolder              holder = null;
    private Camera                       camera = null;
    private ICameraDataReceiver  cameraDataReceiver = null;

    private byte[]                         frameBuffer = null;
    private int                              frameWidth;
    private int                              frameHeight;

    /**
     *  コンストラクタ
     * @param context
     */
    public CameraViewer(Context context)
    {
        super(context);
        initializeSelf(context, null);
        Log.i(GokigenSymbols.APP_IDENTIFIER, "Instantiated, new " + this.getClass());
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
        Log.i(GokigenSymbols.APP_IDENTIFIER, ">>Instantiated new " + this.getClass());
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
     * 
     * 
     * @throws IOException
     */
    public void setPreview() throws IOException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            camera.setPreviewTexture(new SurfaceTexture(TEXTURE_NAME));
        }
        else
        {
            camera.setPreviewDisplay(null);
        }
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
                camera.setParameters(parameters);

                int previewFormat = parameters.getPreviewFormat();
                int bitsPerPixel = ImageFormat.getBitsPerPixel(previewFormat);
                // int byteperpixel = bitsPerPixel / 8;
                Camera.Size camerasize = parameters.getPreviewSize();
                frameWidth = camerasize.width;
                frameHeight = camerasize.height;
                int frame_size = ((frameWidth * frameHeight) * bitsPerPixel) / 8;                
                frameBuffer = new byte[frame_size];

                camera.addCallbackBuffer(frameBuffer);
        		camera.setPreviewCallbackWithBuffer(this);  // これで onPreviewFrame() が呼ばれる。
                camera.startPreview();
                // camera.setPreviewCallback(this);
            }
            catch (Exception ex)
            {
                // カメラが設定できない場合には、その原因をログして終わる(何もしない)
            	Log.v(GokigenSymbols.APP_IDENTIFIER, "surfaceCreated() " + ex.toString());
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
    	if (cameraDataReceiver != null)
    	{
    		cameraDataReceiver.prepareToReceive();
    	}
    }    
    
    /**
     *  サーフェイス変更イベントの処理
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
    	// 何もしない (別のところで実施)
    	configureFormat(format, width, height);

    	synchronized (this)
	    {
            if (cameraDataReceiver != null)
            {
            	cameraDataReceiver.onPreviewStared(width, height, frameWidth, frameHeight);
            }
	    }
    }

    /**
     * 
     * @param format
     * @param width
     * @param height
     */
    private void configureFormat(int format, int width, int height)
    {
        // とりあえずログ出力する
    	Log.v(GokigenSymbols.APP_IDENTIFIER, "configureFormat()  f:" + format + " w:" + width + " h:" + height);
        if (camera != null)
        {
            Camera.Parameters params = camera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            frameWidth = width;
            frameHeight = height;

            // カメラのプレビューサイズは最小化する...
            {
                int  minDiff = Integer.MAX_VALUE;
                for (Camera.Size size : sizes)
                {
                    if (Math.abs(size.height - height) < minDiff)
                    {
                        frameWidth = size.width;
                        frameHeight = size.height;
                        minDiff = Math.abs(size.height - height);
                    }
                }
            }
            params.setPreviewSize(frameWidth, frameHeight);
            camera.setParameters(params);

            params = camera.getParameters();
        }
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
        	cameraDataReceiver.onPreviewFrame(arg0, arg1);
        }

        // 記録バッファを指定する。（再利用）
        camera.addCallbackBuffer(frameBuffer);
	}
}
