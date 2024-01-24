package jp.osdn.gokigen.aira01b.liveview;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import jp.osdn.gokigen.aira01b.R;

/**
 *   画像の保管クラス
 *
 */
class StoreImage implements IStoreImage
{
    private final String TAG = toString();
    private final Context context;

    StoreImage(Context context)
    {
        this.context = context;
    }

    @Override
    public void doStore(final Bitmap target, final Location location)
    {
        // 保存処理(プログレスダイアログ（「保存中...」）を表示して処理する)
        final ProgressDialog saveDialog = new ProgressDialog(context);
        saveDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        saveDialog.setMessage(context.getString(R.string.data_saving));
        saveDialog.setIndeterminate(true);
        saveDialog.setCancelable(false);
        saveDialog.show();
        Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                System.gc();
                saveImageImpl(target, location);
                System.gc();
                saveDialog.dismiss();
            }
        });
        try
        {
            thread.start();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.gc();
        }
    }

    /**
     *   ビットマップイメージをファイルに出力する
     *
     * @param targetImage  出力するビットマップイメージ
     */
    private void saveImageImpl(Bitmap targetImage, Location location)
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + context.getString(R.string.app_name2) + "/";
            String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime()) + "_lv.jpg";
            String filepath = new File(directoryPath.toLowerCase(), filename).getPath();

            final File directory = new File(directoryPath);
            if (!directory.exists())
            {
                if (!directory.mkdirs())
                {
                    Log.v(TAG, "MKDIR FAIL. : " + directoryPath);
                }
            }
            FileOutputStream outputStream = new FileOutputStream(filepath);
            targetImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            long now = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            ContentResolver resolver = context.getContentResolver();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, filepath);
            values.put(MediaStore.Images.Media.DATE_ADDED, now);
            values.put(MediaStore.Images.Media.DATE_TAKEN, now);
            values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
            if (location != null)
            {
                // 位置情報を入れる
                values.put(MediaStore.Images.Media.LATITUDE, location.getLatitude());
                values.put(MediaStore.Images.Media.LONGITUDE, location.getLongitude());
            }
            //values.put(MediaStore.Images.Media.WIDTH, targetImage.getWidth());
            //values.put(MediaStore.Images.Media.HEIGHT, targetImage.getHeight());
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}
