package jp.sourceforge.gokigen.diary;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import android.util.Log;

/**
 *  ごきげんグラフに表示するデータを格納するクラス
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
     *  コンストラクタ
     * 
     */
	public GokigenGraphDataHolder()
    {
        gokigenItems = new Vector<gokigenData>();
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);
    }

	/**
	 *  データを読み出す
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
     *  その日のデータをスキャンする
     * 
     * @param directory
     */
	private void scanDataDaily(String directory)
    {
        scanDataItems(directory);		
    }
	
    /**
     *   週のデータをスキャンする
     * 
     * @param showYear  スキャン開始する年
     * @param showMonth スキャン開始する月
     * @param showDay   スキャン開始する日
     */
	private void scanDataWeekly(int showYear, int showMonth, int showDay)
    {
        // そのうち作ろう...今はまだ。
    }

    /**
     *  データを月次集計する
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
                // データがない...終了する
                return;
            }

            String[] dirList = checkDirectory.list();
            if (dirList != null)
            {
                // List の items をソートする！ 
                java.util.Arrays.sort(dirList);
                
                // ファイル一覧を作り上げる
                for (String dirName : dirList)
                {
                    // サブディレクトリをすべてスキャンする
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
     *  ディレクトリ内にあるデータをスキャンする
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
               // データがない...終了する
               return;
            }
            String[] dirList = scanDir.list();
            if (dirList != null)
            {
                // List の items をソートする！ 
                java.util.Arrays.sort(dirList);
                
                // ファイル一覧を作り上げる
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
     *  データファイル名を解析し、グラフ用元データとしてgokigenDataクラスへ変換する
     *  (グラフ表示に使う元データは、ファイル名から取得すべて取得可能なので)
     * 
     * @param   fileName 解析するためのファイル名
     * @return  gokigenData形式、nullの場合には解析失敗
     */
    private gokigenData parseDataFileName(String fileName)
    {
        // ファイル名の妥当性チェック (ここから)
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
        // ファイル名の妥当性チェック (ここまで)

        // gokigenDataクラスのデータを作成
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
            // exceptionが出たときには、解析失敗として何もしない
        }
        return (null);
    }

    /**
     *  ごきげんデータのデータ一覧を応答する
     * 
     * @return ごきげんデータのデータ一覧
     */
    public Vector<gokigenData> getDataList()
    {
        return (gokigenItems);
    }


    /**
     *  データを解析する
     * 
     */
    private void parseGokigenDatas()
    {
        dataHolderCount = DecideEmotionIcon.numberOfEmotionIcons();
        dataHolder = new int[dataHolderCount];
        totalCount = 0;

        try
        {
            // データを初期化する
            for (int index = 0; index < dataHolderCount; index++)
            {
                dataHolder[index] = 0;
            }

            // データを取り出して配列に入れる
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
     *  データの数を取得する
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
     *  データのアイテム数を取得する
     * 
     * @return
     */
    public int getDataItemCount()
    {
    	return (dataHolderCount);
    }
    
    /**
     *  データの個数を応答する
     * 
     * @return
     */
    public int getTotalDataCount()
    {
    	return ((int) totalCount);
    }
    
    /**
     *  ごきげんデータを記憶する
     * @author MRSa
     *
     */
    public class gokigenData
    {
        private int gokigenRate = 0;
        private int timeSeconds = 0;
        private int day = 0;

        /**
         *  コンストラクタ
         *
         *  @param time その日の経過秒数    (午前０時からの経過秒数)
         *  @param rete ごぎげん度数        (0～50)
         *  @param date データの日を数字で  (20100903 みたいな８桁の数字)
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
