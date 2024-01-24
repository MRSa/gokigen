package jp.osdn.gokigen.aira01a.playback;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.FragmentActivity;
import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraFileInfo;
import jp.osdn.gokigen.aira01a.R;


/**
 *
 *
 */
public class ImageDownloader
{
    private final String TAG = this.toString();
    private final String JPEG_SUFFIX = ".jpg";
    private final String RAW_SUFFIX = ".orf";
    private final FragmentActivity activity;
    private final OLYCamera camera;
    private final MyJpegDownloader jpegDownloader;
    private final MyMovieDownloader movieDownloader;
    private Callback callback = null;
    private int successCount;
    private int failureCount;
    private int currentCount;
    private int loopCount;
    private int maxCount;
    private  List<OLYCameraContentInfoEx> contentList;
    private float imageSize;
    private boolean getWithRaw;
    private boolean getRaw;
    private boolean requestAbort;

    public interface Callback
    {
        void finishedDownloadMulti(boolean isAbort, int successCount, int failureCount);
    }

    ImageDownloader(FragmentActivity activity, OLYCamera camera)
    {
        this.activity = activity;
        this.camera = camera;
        jpegDownloader  = new MyJpegDownloader();
        movieDownloader = new MyMovieDownloader();
    }

    void startDownloadMulti(@NonNull List<OLYCameraContentInfoEx> contentList, float imageSize, boolean getWithRaw, Callback callback)
    {
        this.contentList = contentList;
        this.imageSize = imageSize;
        this.getWithRaw = getWithRaw;
        this.callback = callback;
        this.successCount = 0;
        this.failureCount = 0;
        this.currentCount = 0;
        this.loopCount = 1;
        this.maxCount = contentList.size() * (getWithRaw ? 2 : 1);
        this.getRaw = false;
        this.requestAbort = false;
        try
        {
            // 再生モードかどうかを確認して、再生モードでなかった場合には再生モードに切り替える。
            // (なぜか落ちていることがある...)
            OLYCamera.RunMode runMode = camera.getRunMode();
            if (runMode != OLYCamera.RunMode.Playback)
            {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try
                        {
                            Log.v(TAG, "changeRunMode(OLYCamera.RunMode.Playback) : Start");
                            camera.changeRunMode(OLYCamera.RunMode.Playback);
                            Log.v(TAG, "changeRunMode(OLYCamera.RunMode.Playback) : End");
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                kickDownloadImage();
                            }
                        });
                    }
                };
                thread.start();
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kickDownloadImage();
                    }
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ダウンロードの開始...
     */
    private void kickDownloadImage()
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            String filenameHeader = new SimpleDateFormat("yyyyMMdd_HHmmss_", Locale.getDefault()).format(calendar.getTime());

            OLYCameraContentInfoEx contentInfo = contentList.get(currentCount);
            String fileName = contentInfo.getFileInfo().getFilename().toLowerCase();

            if (getRaw)
            {
                // RAWの取得ターンになっていた場合の処理
                if (contentInfo.hasRaw())
                {
                    // RAWファイルを持っていたら、RAWファイルを取得する
                    // fileName = fileName.replace(JPEG_SUFFIX, RAW_SUFFIX);
                    movieDownloader.startDownload(filenameHeader, contentInfo);
                }
                else
                {
                    finishedDownload(true, true);
                }
                return;
            }
            if (fileName.endsWith(JPEG_SUFFIX))
            {
                jpegDownloader.startDownload(filenameHeader, contentInfo, imageSize);
            }
            else
            {
                movieDownloader.startDownload(filenameHeader, contentInfo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void finishedDownload(boolean isSuccess, boolean skip)
    {
        // ダウンロード回数をカウントアップする。
        if (!skip)
        {
            if (isSuccess)
            {
                successCount++;
            } else {
                failureCount++;
            }
        }
        loopCount++;
        currentCount++;
        if (requestAbort)
        {
            // ダウンロードの中断が指示された。終了とする
            if (callback != null)
            {
                callback.finishedDownloadMulti(true, successCount, failureCount);
            }
            return;
        }

        if (contentList.size() > currentCount)
        {
            // 次のダウンロードに進む
            kickDownloadImage();
            return;
        }
        // 枚数オーバー
        if ((getWithRaw)&&(!getRaw))
        {
            // 二順目、RAWの取得を行う。
            getRaw = true;
            currentCount = 0;
            kickDownloadImage();
            return;
        }

        // ダウンロード完了！
        if (callback != null)
        {
            callback.finishedDownloadMulti(false, successCount, failureCount);
        }
    }

    /**
     *  JPEGファイルのダウンロード
     */
    private class MyJpegDownloader implements OLYCamera.DownloadImageCallback
    {
        private ProgressDialog downloadDialog = null;
        private String filename = null;

        /**
         * コンストラクタ
         */
        MyJpegDownloader()
        {
            //
        }

        /**
         * 静止画のダウンロード開始指示
         */
        void startDownload(@NonNull String fileNameHeader, @NonNull OLYCameraContentInfoEx content, float downloadImageSize)
        {
            this.filename = fileNameHeader + content.getFileInfo().getFilename();
            Log.v(TAG, "startDownload() " + filename);
            downloadDialog = new ProgressDialog(activity);
            downloadDialog.setTitle(activity.getString(R.string.dialog_download_title) + " (" + loopCount  + " / " + maxCount + ")");
            downloadDialog.setMessage(activity.getString(R.string.dialog_download_message) + " " + filename);
            downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            downloadDialog.setCancelable(false);
            downloadDialog.show();

            // Download the image.
            try
            {
                OLYCameraFileInfo file = content.getFileInfo();
                String path = file.getDirectoryPath() + "/" + file.getFilename();
                camera.downloadImage(path, downloadImageSize, this);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        /**
         * 進行中の表示 (進捗バーの更新)
         *
         * @param progressEvent 進捗情報
         */
        @Override
        public void onProgress(OLYCamera.ProgressEvent progressEvent)
        {
            if (downloadDialog != null)
            {
                int percent = (int) (progressEvent.getProgress() * 100.0f);
                downloadDialog.setProgress(percent);
                //downloadDialog.setCancelable(progressEvent.isCancellable()); // キャンセルできるようにしないほうが良さそうなので
            }
        }

        /**
         * ファイル受信終了時の処理
         *
         * @param bytes 受信バイト数
         * @param map   ファイルの情報
         */
        @Override
        public void onCompleted(byte[] bytes, Map<String, Object> map)
        {
            final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + activity.getString(R.string.app_name2) + "/";
            final String filepath = new File(directoryPath.toLowerCase(), filename).getPath();

            // ファイルを保存する
            try {
                final File directory = new File(directoryPath);
                if (!directory.exists())
                {
                    if (!directory.mkdirs())
                    {
                        Log.v(TAG, "MKDIR FAIL. : " + directoryPath);
                    }
                }
                FileOutputStream outputStream = new FileOutputStream(filepath);
                outputStream.write(bytes);
                outputStream.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadDialog != null)
                        {
                            downloadDialog.dismiss();
                        }
                        downloadDialog = null;
                        finishedDownload(false, false);
                    }
                });
                // ダウンロード失敗時(保存失敗)には、ギャラリーにデータ登録を行わない。
                return;
            }

            boolean hasGps = false;
            float[] latLong = new float[2];
            try
            {
                //
                ExifInterface exif = new ExifInterface(filepath);
                hasGps = exif.getLatLong(latLong);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            // ギャラリーに受信したファイルを登録する
            try
            {
                long now = System.currentTimeMillis();
                ContentValues values = new ContentValues();
                ContentResolver resolver = activity.getContentResolver();
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.DATA, filepath);
                values.put(MediaStore.Images.Media.DATE_ADDED, now);
                values.put(MediaStore.Images.Media.DATE_TAKEN, now);
                if ((hasGps) && (latLong.length >= 2))
                {
                    values.put(MediaStore.Images.Media.LATITUDE, latLong[0]);
                    values.put(MediaStore.Images.Media.LONGITUDE, latLong[1]);
                }
                values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
                values.put(MediaStore.Images.Media.ORIENTATION, getRotationDegrees(bytes, map));
                final Uri insertedImage = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Log.v(TAG, " get : " + insertedImage);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadDialog != null)
                        {
                            downloadDialog.dismiss();
                        }
                        downloadDialog = null;
                        finishedDownload(true, false);
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadDialog != null)
                        {
                            downloadDialog.dismiss();
                        }
                        downloadDialog = null;
                        finishedDownload(true, false);
                    }
                });
            }
        }

        /**
         * エラー発生時の処理
         *
         * @param e エラーの情報
         */
        @Override
        public void onErrorOccurred(Exception e)
        {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (downloadDialog != null)
                    {
                        downloadDialog.dismiss();
                    }
                    downloadDialog = null;
                    finishedDownload(false, false);
                }
            });
        }
    }

    /**
     * 動画(とRAWファイル)のダウンロード
     */
    private class MyMovieDownloader implements OLYCamera.DownloadLargeContentCallback
    {
        private ProgressDialog downloadDialog = null;
        private String filename = null;
        private String filepath = null;
        private FileOutputStream outputStream = null;

        /**
         * コンストラクタ
         *
         */
        MyMovieDownloader()
        {
            //
        }

        /**
         * ダウンロードの開始
         */
        void startDownload(@NonNull String fileNameHeader, @NonNull OLYCameraContentInfoEx content)
        {
            Log.v(TAG, "startDownload() " + content.getFileInfo().getFilename());
            downloadDialog = new ProgressDialog(activity);
            downloadDialog.setTitle(activity.getString(R.string.dialog_download_file_title) + " (" + loopCount + " / " + maxCount + ")");
            downloadDialog.setMessage(activity.getString(R.string.dialog_download_message) + " " + content.getFileInfo().getFilename());
            downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            downloadDialog.setCancelable(false);
            downloadDialog.show();

            // Download the image.
            try
            {
                OLYCameraFileInfo file = content.getFileInfo();
                String targetFileName = file.getFilename().toLowerCase();
                if (content.hasRaw())
                {
                    targetFileName = targetFileName.replace(JPEG_SUFFIX, RAW_SUFFIX);
                }
                filename = fileNameHeader + targetFileName;
                String path = file.getDirectoryPath() + "/" + targetFileName;
                Log.v(TAG, "downloadLargeContent : " + path);
                camera.downloadLargeContent(path, this);

                final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + activity.getString(R.string.app_name2) + "/";
                filepath = new File(directoryPath.toLowerCase(), filename).getPath();
                try
                {
                    final File directory = new File(directoryPath);
                    if (!directory.exists())
                    {
                        if (!directory.mkdirs())
                        {
                            Log.v(TAG, "MKDIR FAIL. : " + directoryPath);
                        }
                    }
                    outputStream = new FileOutputStream(filepath);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadDialog != null)
                            {
                                downloadDialog.dismiss();
                            }
                            downloadDialog = null;
                            finishedDownload(false, false);
                        }
                    });
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public void onProgress(byte[] bytes, OLYCamera.ProgressEvent progressEvent)
        {
            if (downloadDialog != null)
            {
                int percent = (int) (progressEvent.getProgress() * 100.0f);
                downloadDialog.setProgress(percent);
                //downloadDialog.setCancelable(progressEvent.isCancellable()); // キャンセルできるようにしないほうが良さそうなので
            }
            try
            {
                if (outputStream != null)
                {
                    outputStream.write(bytes);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted()
        {
            try
            {
                if (outputStream != null)
                {
                    outputStream.flush();
                    outputStream.close();
                    outputStream = null;
                }
                if (!filename.endsWith(RAW_SUFFIX))
                {
                    // ギャラリーに受信したファイルを登録する
                    long now = System.currentTimeMillis();
                    ContentValues values = new ContentValues();
                    ContentResolver resolver = activity.getContentResolver();
                    values.put(MediaStore.Images.Media.MIME_TYPE, "video/mp4");
                    values.put(MediaStore.Images.Media.DATA, filepath);
                    values.put(MediaStore.Images.Media.DATE_ADDED, now);
                    values.put(MediaStore.Images.Media.DATE_TAKEN, now);
                    values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
                    final Uri content = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                }
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        if (downloadDialog != null)
                        {
                            downloadDialog.dismiss();
                        }
                        downloadDialog = null;
                        finishedDownload(true, false);
                    }
                });
            }
            catch (Exception e)
            {
                final String message = e.getMessage();
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (downloadDialog != null)
                        {
                            downloadDialog.dismiss();
                        }
                        downloadDialog = null;
                        finishedDownload(false, false);
                    }
                });
            }
        }

        @Override
        public void onErrorOccurred(Exception e)
        {
            e.printStackTrace();
            try
            {
                if (outputStream != null)
                {
                    outputStream.flush();
                    outputStream.close();
                    outputStream = null;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (downloadDialog != null)
                    {
                        downloadDialog.dismiss();
                    }
                    downloadDialog = null;
                    finishedDownload(false, false);
                }
            });
        }
    }

    private int getRotationDegrees(byte[] data, Map<String, Object> metadata)
    {
        int degrees = 0;
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;

        if (metadata != null && metadata.containsKey("Orientation"))
        {
            try
            {
                orientation = Integer.parseInt((String) metadata.get("Orientation"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            // Gets image orientation to display a picture.
            try
            {
                File tempFile = File.createTempFile("temp", null);
                {
                    FileOutputStream outStream = new FileOutputStream(tempFile.getAbsolutePath());
                    outStream.write(data);
                    outStream.close();
                }
                ExifInterface exifInterface = new ExifInterface(tempFile.getAbsolutePath());
                orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                if (!tempFile.delete())
                {
                    Log.v(TAG, "temp file delete failure.");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                //degrees = 0;
                break;
        }
        return (degrees);
    }
}
