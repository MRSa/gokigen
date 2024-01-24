package jp.sfjp.gokigen.prpr0;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *  外部ストレージにデータを記録するために使うユーティリティ (使えるのか...）
 *  
 * @author MRSa
 */
public class ExternalStorageFileUtility 
{
    private static final String TAG = "ExternalStorageFileUtility";

	private final int COPY_BUFFER_SIZE = 32768;
	private final int BUFFER_MARGIN    = 4;

	private String baseDirectory = "/";

    /**
     *   コンストラクタ
     *
     * @param offsetDir
     */
    public ExternalStorageFileUtility(String offsetDir)
    {
         prepareBaseDirectory(offsetDir);
    }
    
    /**
     *   記録のベースディレクトリを作成し、記録の準備
     * 
     */
    private boolean prepareBaseDirectory(String offsetDir)
    {
        String gokigenDirectory = Environment.getExternalStorageDirectory().getPath() + "/Gokigen";
        try
        {
            File baseDir = new File(gokigenDirectory);
            if (baseDir.exists() == false)
            {
                if (baseDir.mkdirs() == false)
                {
                    baseDirectory = Environment.getExternalStorageDirectory().getPath();
                    baseDir = null;
                    return (false);
                }
            }
            gokigenDirectory = gokigenDirectory + offsetDir;
            baseDir = null;
            baseDir = new File(gokigenDirectory);
            if (baseDir.exists() == false)
            {
                if (baseDir.mkdirs() == false)
                {
                    baseDirectory = Environment.getExternalStorageDirectory().getPath() + "/Gokigen";
                    baseDir = null;
                    return (false);                    
                }
            }
            baseDirectory = gokigenDirectory;
            return (true);
        }
        catch (Exception ex)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "prepareBaseDirectory() : " + ex.getMessage());
            }
        }
        baseDirectory = Environment.getExternalStorageDirectory().getPath();
        return (false);
    }

     public String getGokigenDirectory()
    {
        return (baseDirectory);
    }    
    
    /**
     *
     * 
     * @param dirName
     * @return
     */
    public boolean makeDirectory(String dirName)
    {
        String makeDir = baseDirectory + "/" + dirName;
        try
        {
            File dir = new File(makeDir);
            if (dir.exists() == false)
            {
                return (dir.mkdirs());
            }
            return (true);
        }
        catch (Exception ex)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "makeDirectory() : " + ex.getMessage());
            }
        }
        return (false);
    }

    public FileOutputStream openFileStream(String fileName, boolean isAppend)
    {
        try
        {
            String targetName = baseDirectory + "/" + fileName;
            FileOutputStream fileStream = new FileOutputStream(targetName, isAppend);
            return (fileStream);
        }
        catch (Exception e)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "openFileStream() : " + e.getMessage());
            }
        }
        return (null);
    }    

    private String decideFileNameWithSpecifiedDate(String fileName, String year, String month, String date)
    {
        String directory = year;
        
        makeDirectory(year);

        directory = directory + "/" + year + month;

        makeDirectory(directory);

        directory = directory + "/" + year + month + date;

        makeDirectory(directory);
        
        return (directory + "/" + year + month + date + "-" + fileName);
    }

    public String decideFileNameWithDate(String fileName)
    {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");

        String year  = yearFormat.format(calendar.getTime());
        String month = monthFormat.format(calendar.getTime());
        String date  = dateFormat.format(calendar.getTime());

        return (decideFileNameWithSpecifiedDate(fileName, year, month, date));
    }

    public String decideFileNameWithSpecifiedDate(String fileName, int year, int month, int day)
    {
        String yearStr = year + "";
        String monthStr = "";
        if (month < 10)
        {
            monthStr = "0" + month;	
        }
        else
        {
        	monthStr = month + "";
        }
        String dayStr = "";
        if (day < 10)
        {
            dayStr = "0" + day;
        }
        else
        {
        	dayStr = day + "";
        }
        return (decideFileNameWithSpecifiedDate(fileName, yearStr, monthStr, dayStr));
    }

    public String decideDateDirectory(int year, int month, int date)
    {
        String addMonth = "";
        String addDate  = "";
        String directory = baseDirectory + "/" + year + "/";
        if (month < 10)
        {
            addMonth =  "0";
        }
        if (date < 10)
        {
            addDate = "0";
        }
        directory = directory + year + addMonth + month + "/" + year + addMonth + month + addDate + date + "/";        
        return (directory);
    }

	public boolean copyFile(String destFileName, String srcFileName)
	{
		File srcFile = null;
		File dstFile = null;
		
		boolean     ret = false;
		InputStream   is = null;
		OutputStream  os = null;

		if (destFileName == srcFileName)
		{
			return (false);
		}
		
		try
		{
			srcFile = new File(srcFileName);
			if (srcFile.exists() != true)
			{
				return (false);
			}
			is = new FileInputStream(srcFile);

			long dataFileSize = srcFile.length();
			byte[] buffer = new byte[COPY_BUFFER_SIZE + BUFFER_MARGIN];

			dstFile = new File(destFileName);
			if (dstFile.exists() == true)
			{
				dstFile.delete();
			}

			os = new FileOutputStream(dstFile);
			if ((is != null)&&(os != null))
			{
				while (dataFileSize > 0)
				{
			        int size = is.read(buffer, 0, COPY_BUFFER_SIZE);
			        if (size <= 0)
			        {
			        	break;
			        }
			        os.write(buffer, 0, size);
				}
			}
			os.flush();
			os.close();
			is.close();
			
			dstFile = null;
			srcFile = null;
			buffer = null;
			is = null;
			os = null;
			ret = true;
			System.gc();
		}
		catch (Exception e)
		{
			try
			{
				if (is != null)
				{
					is.close();
				}
			}
			catch (Exception e2)
			{
				//
			}
				
			try
			{
				if (os != null)
				{
					os.close();
				}
			}
			catch (Exception e2)
			{
				//
			}
			is = null;
			os = null;
			srcFile = null;
			dstFile = null;
			System.gc();

			return (false);
		}
		return (ret);
	}

    /**
     *   BITMAPをPNGイメージにして保存する
     *
     * @param contentResolver ギャラリーに登録する場合に指定する
     * @param targetImage      PNGで保存するBitmap
     * @return  保存したビットマップのファイル名(nullの場合には保存失敗)
     */
    public String putPngImageFromBitmap(ContentResolver contentResolver, String appendDirectory, Bitmap targetImage)
    {
        String fileName = null;
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

            /** ContentResolver が指定されていた場合には、ギャラリーに追加する **/
            if (contentResolver != null)
            {
                ContentValues values = new ContentValues(10);
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.DATA, imagePath);
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
        catch (Exception ee)
        {
            return (null);
        }
        return (fileName);
    }
}
