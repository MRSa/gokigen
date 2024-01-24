package jp.sourceforge.gokigen.log.viewer;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *  �O���X�g���[�W�Ƀf�[�^���L�^���邽�߂Ɏg�����[�e�B���e�B
 *  
 * @author MRSa
 */
public class ExternalStorageFileUtility 
{
	private final int COPY_BUFFER_SIZE = 32768;
	private final int BUFFER_MARGIN    = 4;

	private String baseDirectory = "/";

    /**
     *   �R���X�g���N�^
     * 
     */
    public ExternalStorageFileUtility(String offsetDir)
    {
        /** �x�[�X�f�B���N�g���̍쐬 (����Ή������Ȃ�) **/
        prepareBaseDirectory(offsetDir);
    }
    
    /**
     *   �x�[�X�f�B���N�g���̌�����s��
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
                    // �x�[�X�f�B���N�g���쐬���s...�I������
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
                    // �x�[�X�f�B���N�g���쐬���s...�I������
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
            Log.v(Main.APP_IDENTIFIER, "prepareBaseDirectory() : " + ex.getMessage());
        }
        baseDirectory = Environment.getExternalStorageDirectory().getPath();
        return (false);
    }

    /**
     *  �x�[�X�f�B���N�g�����擾����
     * @return  �������݂��s���A�v���p�x�[�X�f�B���N�g��
     */
    public String getGokigenDirectory()
    {
        return (baseDirectory);
    }    
    
    /**
     *  �f�B���N�g�����쐬����
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
            Log.v(Main.APP_IDENTIFIER, "makeDirectory() : " + ex.getMessage());
        }
        return (false);
    }

    /**
     *  �L�^�t�@�C�����I�[�v������    
     * @param fileName  �t�@�C����
     * @param isAppend  �ǋL���[�h�ŃI�[�v�����邩�H
     * @return  �t�@�C���X�g���[��
     */
    public FileOutputStream openFileStream(String fileName, boolean isAppend)
    {
        try
        {
            String targetName = baseDirectory + "/" + fileName;
            Log.v(Main.APP_IDENTIFIER, "START>openFileStream() : " + targetName);
            FileOutputStream fileStream = new FileOutputStream(targetName, isAppend);
            return (fileStream);
        }
        catch (Exception e)
        {
            Log.v(Main.APP_IDENTIFIER, "ERR>openFileStream() : " + e.getMessage());
        }
        return (null);
    }    

    /**
     *   ���P�ʂŃf�B���N�g�����쐬���A�����ɏ������ނ��߂̃t�@�C���������肷��
     * @return �t�@�C������
     */
    private String decideFileNameWithSpecifiedDate(String fileName, String year, String month, String date)
    {
        String directory = year;
        
        // �N�̃f�B���N�g�����@��
        makeDirectory(year);

        directory = directory + "/" + year + month;

        // �N���̃f�B���N�g�����@��
        makeDirectory(directory);

        directory = directory + "/" + year + month + date;

        // �N�����̃f�B���N�g�����@��
        makeDirectory(directory);
        
        return (directory + "/" + year + month + date + "-" + fileName);
    }

    /**
     *   ���P�ʂŃf�B���N�g�����쐬���A�����ɏ������ނ��߂̃t�@�C���������肷��
     * @return �t�@�C������
     */
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

    /**
     *   ���P�ʂŃf�B���N�g�����쐬���A�����ɏ������ނ��߂̃t�@�C���������肷��
     * @return �t�@�C������
     */
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

    /**
     *  ���t�f�B���N�g�������擾����
     * @param year
     * @param month
     * @param date
     * @return
     */
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

    /**
     * �t�@�C���̃R�s�[ (kaniFiler���玝���Ă���...)
     * 
     * @param destFileName �R�s�[��t�@�C�� (full path)
     * @param srcFileName  �R�s�[���t�@�C�� (full path)
     */
	public boolean copyFile(String destFileName, String srcFileName)
	{
		File srcFile = null;
		File dstFile = null;
		
		boolean     ret = false;
		InputStream   is = null;
		OutputStream  os = null;

		if (destFileName == srcFileName)
		{
			// �t�@�C�����������������ꍇ�ɂ̓R�s�[�����s���Ȃ�
			return (false);
		}
		
		try
		{
			srcFile = new File(srcFileName);
			if (srcFile.exists() != true)
			{
				// �t�@�C�������݂��Ȃ������A�A�A�I������
				return (false);
			}
			is = new FileInputStream(srcFile);

			long dataFileSize = srcFile.length();
			byte[] buffer = new byte[COPY_BUFFER_SIZE + BUFFER_MARGIN];

			dstFile = new File(destFileName);
			if (dstFile.exists() == true)
			{
				// �t�@�C�������݂����A�A�A�폜���č�蒼��
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
			// ��O�����I�I�I
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
}
