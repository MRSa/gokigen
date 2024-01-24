package jp.osdn.gokigen.aira01b.liveview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
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
    private Activity activity = null;

    StoreImage(Context context)
    {
        this.context = context;
    }

    @Override
    public void doStore(final Bitmap target, final Location location, final boolean isShare)
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
                saveImageImpl(target, location, isShare);
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

    @Override
    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    /**
     *   ビットマップイメージをファイルに出力する
     *
     * @param targetImage  出力するビットマップイメージ
     */
    private void saveImageImpl(Bitmap targetImage, Location location, boolean isShare)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                values.put(MediaStore.Images.Media.WIDTH, targetImage.getWidth());
                values.put(MediaStore.Images.Media.HEIGHT, targetImage.getHeight());
            }
            final Uri pictureUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (isShare)
            {
                shareContent(pictureUri);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    /**
     *   共有の呼び出し
     *
     * @param pictureUri  画像ファイル名
     */
    private void shareContent(final Uri pictureUri)
    {
        // activity が nullなら、共有はしない
        if (activity == null)
        {
            return;
        }
        try
        {
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, pictureUri);
                    activity.startActivityForResult(intent, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
