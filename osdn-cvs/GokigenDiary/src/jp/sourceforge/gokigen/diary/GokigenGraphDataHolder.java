package jp.sourceforge.gokigen.diary;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import android.util.Log;

/**
 *  ��������O���t�ɕ\������f�[�^���i�[����N���X
 * 
 * @author MRSa
 *
 */
public class GokigenGraphDataHolder
{
    private ExternalStorageFileUtility fileUtility = null;
    private Vector<gokigenData> gokigenItems = null;
    private int dataHolderCount = 0;
    private int[] dataHolder = null;
    private float totalCount = 0;

    /**
     *  �R���X�g���N�^
     * 
     */
	public GokigenGraphDataHolder()
    {
        gokigenItems = new Vector<gokigenData>();
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);
    }

	/**
	 *  �f�[�^��ǂݏo��
	 * 
	 * @param reportType
	 * @param showYear
	 * @param showMonth
	 * @param showDay
	 */
	public void parseGokigenItems(int reportType, int showYear, int showMonth, int showDay)
	{
        String dataDir = fileUtility.decideDateDirectory(showYear, showMonth, showDay);
        gokigenItems.clear();
        
        switch (reportType)
        {
          case GokigenGraphListener.REPORTTYPE_MONTHLY:
          	scanDataMonthly(dataDir);
            break;

          case GokigenGraphListener.REPORTTYPE_WEEKLY:
          	scanDataWeekly(showYear, showMonth, showDay);
            break;

          case GokigenGraphListener.REPORTTYPE_DAILY:
          default:
        	scanDataDaily(dataDir);
        	break;
        }
        parseGokigenDatas();
	}

    /**
     *  ���̓��̃f�[�^���X�L��������
     * 
     * @param directory
     */
	private void scanDataDaily(String directory)
    {
        scanDataItems(directory);		
    }
	
    /**
     *   �T�̃f�[�^���X�L��������
     * 
     * @param showYear  �X�L�����J�n����N
     * @param showMonth �X�L�����J�n���錎
     * @param showDay   �X�L�����J�n�����
     */
	private void scanDataWeekly(int showYear, int showMonth, int showDay)
    {
        // ���̂�����낤...���͂܂��B
    }

    /**
     *  �f�[�^�������W�v����
     * 
     * @param directory
     */
	private void scanDataMonthly(String dataDir)
    {        
        String getDirectory = dataDir.substring(0, dataDir.substring(0, dataDir.length() - 2).lastIndexOf("/"));  
        try
        {
            File checkDirectory = new File(getDirectory);
            if (checkDirectory.exists() == false)
            {
                // �f�[�^���Ȃ�...�I������
                return;
            }

            String[] dirList = checkDirectory.list();
            if (dirList != null)
            {
                // List �� items ���\�[�g����I 
                java.util.Arrays.sort(dirList);
                
                // �t�@�C���ꗗ�����グ��
                for (String dirName : dirList)
                {
                    // �T�u�f�B���N�g�������ׂăX�L��������
                    scanDataItems(getDirectory + "/" + dirName);
                }
            }    
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EX : " + ex.getMessage() + ", dir : " + getDirectory);
        }            
		
    }

    /**
     *  �f�B���N�g�����ɂ���f�[�^���X�L��������
     * 
     * @param directory
     */
	private void scanDataItems(String directory)
    {
        try
        {
            File scanDir = new File(directory);
            if (scanDir.exists() == false)
            {
               // �f�[�^���Ȃ�...�I������
               return;
            }
            String[] dirList = scanDir.list();
            if (dirList != null)
            {
                // List �� items ���\�[�g����I 
                java.util.Arrays.sort(dirList);
                
                // �t�@�C���ꗗ�����グ��
                for (String dirName : dirList)
                {
                    gokigenData listItem = parseDataFileName(dirName);
                    if (listItem != null)
                    {
                        gokigenItems.add(listItem);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EX : " + ex.getMessage() + ", dirName : " + directory);
        }
    }

    /**
     *  �f�[�^�t�@�C��������͂��A�O���t�p���f�[�^�Ƃ���gokigenData�N���X�֕ϊ�����
     *  (�O���t�\���Ɏg�����f�[�^�́A�t�@�C��������擾���ׂĎ擾�\�Ȃ̂�)
     * 
     * @param   fileName ��͂��邽�߂̃t�@�C����
     * @return  gokigenData�`���Anull�̏ꍇ�ɂ͉�͎��s
     */
    private gokigenData parseDataFileName(String fileName)
    {
        // �t�@�C�����̑Ó����`�F�b�N (��������)
        if (fileName.endsWith(".txt") == false)
        {
            return (null);
        }

        int start = fileName.indexOf("-diary");
        if (start < 0)
        {
            return (null);
        }

        int end = fileName.indexOf("_");
        if (end < 0)
        {
            return (null);
        }
        
        int prefix = fileName.indexOf(".");
        if (prefix < end)
        {
            return (null);
        }
        // �t�@�C�����̑Ó����`�F�b�N (�����܂�)

        // gokigenData�N���X�̃f�[�^���쐬
        try
        {
            start = start + 6;

            String dateString = fileName.substring(6, 8);
            String hourString = fileName.substring(start, (start + 2));
            String miniteString = fileName.substring((start + 2), (start + 4));
            String secondString = fileName.substring((start + 4), (start + 6));
            String rateString = fileName.substring((start + 7), (start + 9));

            int date = Integer.parseInt(dateString);
            int hour = Integer.parseInt(hourString);
            int minite = Integer.parseInt(miniteString);
            int second = Integer.parseInt(secondString);
            int rate = Integer.parseInt(rateString);
            int timeSecond = second + (60 * minite) + (60 * 60 * hour);
            
            return (new gokigenData(timeSecond, rate, date));
        }
        catch (Exception ex)
        {
            // exception���o���Ƃ��ɂ́A��͎��s�Ƃ��ĉ������Ȃ�
        }
        return (null);
    }

    /**
     *  ��������f�[�^�̃f�[�^�ꗗ����������
     * 
     * @return ��������f�[�^�̃f�[�^�ꗗ
     */
    public Vector<gokigenData> getDataList()
    {
        return (gokigenItems);
    }


    /**
     *  �f�[�^����͂���
     * 
     */
    private void parseGokigenDatas()
    {
        dataHolderCount = DecideEmotionIcon.numberOfEmotionIcons();
        dataHolder = new int[dataHolderCount];
        totalCount = 0;

        try
        {
            // �f�[�^������������
            for (int index = 0; index < dataHolderCount; index++)
            {
                dataHolder[index] = 0;
            }

            // �f�[�^�����o���Ĕz��ɓ����
            Enumeration<gokigenData> e = gokigenItems.elements();
            while (e.hasMoreElements())
            {
                gokigenData data = (gokigenData) e.nextElement();

                int index = DecideEmotionIcon.decideEmotionIconIndex(DecideEmotionIcon.decideEmotionIcon((int) data.getRate()));
                dataHolder[index] = dataHolder[index] + 1;
                totalCount = totalCount + 1;
            }
        }
        catch (Exception ex)
        {
            
        }       
     }

    /**
     *  �f�[�^�̐����擾����
     * 
     * @param index
     * @return
     */
    public int getDataCount(int index)
    {
    	int result = 0;
        try
        {
        	result = dataHolder[index];
        }
        catch (Exception ex)
        {
        	
        }
        return (result);
    }

    /**
     *  �f�[�^�̃A�C�e�������擾����
     * 
     * @return
     */
    public int getDataItemCount()
    {
    	return (dataHolderCount);
    }
    
    /**
     *  �f�[�^�̌�����������
     * 
     * @return
     */
    public int getTotalDataCount()
    {
    	return ((int) totalCount);
    }
    
    /**
     *  ��������f�[�^���L������
     * @author MRSa
     *
     */
    public class gokigenData
    {
        private int gokigenRate = 0;
        private int timeSeconds = 0;
        private int day = 0;

        /**
         *  �R���X�g���N�^
         *
         *  @param time ���̓��̌o�ߕb��    (�ߑO�O������̌o�ߕb��)
         *  @param rete ��������x��        (0�`50)
         *  @param date �f�[�^�̓��𐔎���  (20100903 �݂����ȂW���̐���)
         *
         */        
        public gokigenData(int time, int rate, int date)
        {
            timeSeconds = time;
            gokigenRate = rate;
            day = date;
        }
        
        public float getTime()
        {
            return ((float) timeSeconds);
        }
        
        public float getRate()
        {
            return ((float) gokigenRate);
        }
        
        public float getDay()
        {
            return ((float) day);
        }
    }
}
