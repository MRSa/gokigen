package jp.osdn.gokigen.aira01a;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;

/**
 *   撮影後画像の描画（表示）に特化したクラス
 *
 * Created by MRSa on 2016/04/29.
 */
public class RecordedImageDrawer
{
    private final String TAG = this.toString();

    private Activity parent = null;
    private RecordingSupportsListenerImpl listener = null;
    private OLYCamera camera = null;
    private ImageView imageView = null;
    private byte[] data;
    private Map<String, Object> metadata;

    /**
     * コンストラクタ : コンストラクタでは何もしない
     *
     * @param targetView
     *
     */
    public RecordedImageDrawer(ImageView targetView)
    {
        //
        this.imageView = targetView;
        this.listener = new RecordingSupportsListenerImpl(this, null);
    }

    /**
     *   カメラ関係の設定を更新する
     *
     * @param camera
     * @param activity
     */
    public void setImageArea(OLYCamera camera, Activity activity)
    {
        this.camera = camera;
        this.parent = activity;
    }

    /**
     *   描画領域を設定する
     *
     *   @param targetView  描画領域
     *
     */
    public void setTargetView(ImageView targetView)
    {
        this.imageView = targetView;
    }

    /**
     *   表示する画像を受け取る
     *
     * @param data
     * @param metadata
     */
    public void setImageData(byte[] data, Map<String, Object> metadata)
    {
        this.data = data;
        this.metadata = metadata;
    }

    /**
     *   画像描画の開始
     *
     */
    public void startDrawing()
    {
        if (camera != null)
        {
            camera.setRecordingSupportsListener(listener);
            doDraw();
        }
    }

    /**
     *   画像描画の終了
     *
     */
    public void stopDrawing()
    {
        if (camera != null)
        {
            camera.setRecordingSupportsListener(null);
        }
    }

    /**
     *   ビットマップデータを貰って画像描画を実行する
     *
     * @param camera
     * @param data
     * @param metadata
     */
    public void onReceiveCapturedImagePreview(OLYCamera camera, byte[] data, Map<String, Object> metadata)
    {
        this.data = data;
        this.metadata = metadata;
        if (parent != null)
        {
            parent.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    doDraw();
                }
            });
        }
    }

    private void doDraw()
    {
        if (imageView != null)
        {
            imageView.setImageBitmap(createRotatedBitmap(data, metadata));
        }
    }

    private Bitmap createRotatedBitmap(byte[] data, Map<String, Object> metadata)
    {
        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        }
        if (bitmap == null)
        {
            return null;
        }

        int degrees = getRotationDegrees(data, metadata);
        if (degrees != 0) {
            Matrix m = new Matrix();
            m.postRotate(degrees);
            try {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }
            catch (OutOfMemoryError e)
            {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private int getRotationDegrees(byte[] data, Map<String, Object> metadata)
    {
        int degrees = 0;
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;

        if (metadata != null && metadata.containsKey("Orientation")) {
            orientation = Integer.parseInt((String) metadata.get("Orientation"));
        } else {
            // Gets image orientation to display a picture.
            try {
                File tempFile = File.createTempFile("temp", null);
                {
                    FileOutputStream outStream = new FileOutputStream(tempFile.getAbsolutePath());
                    outStream.write(data);
                    outStream.close();
                }

                ExifInterface exifInterface = new ExifInterface(tempFile.getAbsolutePath());
                orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                tempFile.delete();
            }
            catch (IOException e)
            {

            }
        }

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_NORMAL:
                degrees = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;
            default:
                break;
        }

        return degrees;
    }

}
