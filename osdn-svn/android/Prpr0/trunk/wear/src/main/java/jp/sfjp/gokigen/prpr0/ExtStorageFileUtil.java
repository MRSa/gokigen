package jp.sfjp.gokigen.prpr0;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 *  外部ストレージにデータを記録するために使うユーティリティ (使えるのか...）
 *  
 * @author MRSa
 */
class ExtStorageFileUtil
{
    private static final String TAG = "ExtStorageFileUtil";
	private String baseDirectory = "/";

    /**
     *   コンストラクタ
     *
     * @param offsetDir オフセットディレクトリ
     */
    ExtStorageFileUtil(String offsetDir)
    {
         prepareBaseDirectory(offsetDir);
    }
    
    /**
     *   記録のベースディレクトリを作成し、記録の準備
     * 
     */
    private void prepareBaseDirectory(String offsetDir)
    {
        String gokigenDirectory = Environment.getExternalStorageDirectory().getPath() + "/Gokigen";
        try
        {
            File baseDir = new File(gokigenDirectory);
            if (!baseDir.exists())
            {
                if (!baseDir.mkdirs())
                {
                    baseDirectory = Environment.getExternalStorageDirectory().getPath();
                    return;
                }
            }
            gokigenDirectory = gokigenDirectory + offsetDir;
            baseDir = new File(gokigenDirectory);
            if (!baseDir.exists())
            {
                if (!baseDir.mkdirs())
                {
                    baseDirectory = Environment.getExternalStorageDirectory().getPath() + "/Gokigen";
                    return;
                }
            }
            baseDirectory = gokigenDirectory;
            return;
        }
        catch (Exception ex)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "prepareBaseDirectory() : " + ex.getMessage());
            }
        }
        baseDirectory = Environment.getExternalStorageDirectory().getPath();
    }

    /**
     *   ベースディレクトリを応答する
     *
     * @return  ファイルのあるベースディレクトリ
     */

    String getGokigenDirectory()
    {
        return (baseDirectory);
    }    
    
    /**
     *   ディレクトリを作成する
     * 
     * @param dirName ディレクトリ名
     */
    void makeDirectory(String dirName)
    {
        String makeDir = baseDirectory + "/" + dirName;
        try
        {
            File dir = new File(makeDir);
            if (!dir.exists())
            {
                boolean ret = dir.mkdirs();
                if (!ret)
                {
                    if (Log.isLoggable(TAG, Log.VERBOSE))
                    {
                        Log.v(TAG, "makeDirectory() : false ");
                    }
                }
            }
        }
        catch (Exception ex)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "makeDirectory() : " + ex.getMessage());
            }
        }
    }

    /**
     *   BITMAPをPNGイメージにして保存する
     *
     * @param targetImage      PNGで保存するBitmap
     * @return  保存したビットマップのファイル名(nullの場合には保存失敗)
     */
    //String putPngImageFromBitmap(ContentResolver contentResolver, String appendDirectory, Bitmap targetImage)
    String putPngImageFromBitmap(String appendDirectory, Bitmap targetImage)
    {
        String fileName;
        try
        {
            long dateTime = Calendar.getInstance().getTime().getTime();
            fileName = dateTime + ".png";
            String imagePath =  baseDirectory;
            if (appendDirectory != null)
            {
                imagePath = imagePath + appendDirectory;
            }
            imagePath = imagePath + "/" + fileName;
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(imagePath));
            targetImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
/*
            // ContentResolver が指定されていた場合には、ギャラリーに追加する
            if (contentResolver != null)
            {
                ContentValues values = new ContentValues(10);
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.DATA, imagePath);
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
*/
        }
        catch (Exception ee)
        {
            return (null);
        }
        return (fileName);
    }
}
