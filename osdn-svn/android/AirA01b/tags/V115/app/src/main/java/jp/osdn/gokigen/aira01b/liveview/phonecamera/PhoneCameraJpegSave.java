package jp.osdn.gokigen.aira01b.liveview.phonecamera;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PhoneCameraJpegSave implements Camera.PictureCallback
{
    private final String TAG = toString();
    private final Context context;
    private final IPhoneCameraShutter finishedCallback;

    public PhoneCameraJpegSave(Context context, IPhoneCameraShutter callback)
    {
        this.context = context;
        this.finishedCallback = callback;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera)
    {
        Log.v(TAG, "PhoneCameraJpegSave::onPictureTaken()");
        if (bytes == null)
        {
            Log.v(TAG, "PhoneCameraJpegSave::onPictureTaken() : Picture data is NULL.");
            finishedCallback.onSavedPicture(false);
            return;
        }
        /**
        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            bitmap = null;
            System.gc();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (bitmap != null)
        {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "", null);
            return;
        }

         **/
        try
        {
            Calendar calendar = Calendar.getInstance();
            String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime()) + ".jpg";

            final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/aira01a/";
            String filepath = new File(directoryPath.toLowerCase(), filename).getPath();

            final File directory = new File(directoryPath);
            if (!directory.exists())
            {
                boolean ret = directory.mkdirs();
                if (!ret)
                {
                    Log.v(TAG, "mkdir Fail : " + directoryPath);
                }
            }
            FileOutputStream outputStream = new FileOutputStream(filepath);
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();

            long now = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            ContentResolver resolver =context.getContentResolver();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, filepath);
            values.put(MediaStore.Images.Media.DATE_ADDED, now);
            values.put(MediaStore.Images.Media.DATE_TAKEN, now);
            values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
            values.put(MediaStore.Images.Media.ORIENTATION, 90);
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finishedCallback.onSavedPicture(true);
    }
}
