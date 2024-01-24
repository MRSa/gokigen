package jp.sourceforge.gokigen.mr999ctl;

import android.content.Context;
import android.util.AttributeSet;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *  カメラ画像表示クラス (ん・ぱかさんのコードを参考にさせていただいた。)
 *    ⇒ http://www.saturn.dti.ne.jp/~npake/android/CameraEx/index.html
 *  
 * @author MRSa
 *
 */
public class CameraViewer extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder holder = null;
    private Camera        camera = null;
    
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
        // カメラの初期化
        try
        {
            camera = Camera.open();
            camera.setPreviewDisplay(aHolder);
        }
        catch (Exception ex)
        {
            //
            // カメラが設定できない場合には、何もしない
            // 
        }
    }
    
    /**
     *  サーフェイス変更イベントの処理
     * 
     */
    public void surfaceChanged(SurfaceHolder aHolder, int format, int width, int height)
    {
        //  カメラのプレビュー開始処理
        try
        {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            camera.setParameters(parameters);
        
            camera.startPreview();
        }
        catch (Exception ex)
        {
            //
            //
            //
        }
    }

    /**
     *  サーフェイス開放イベントの処理
     * 
     */
    public void surfaceDestroyed(SurfaceHolder aHolder)
    {
        // カメラのプレビュー停止処理
        try
        {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        catch (Exception ex)
        {
            //            
        }        
    }
}
