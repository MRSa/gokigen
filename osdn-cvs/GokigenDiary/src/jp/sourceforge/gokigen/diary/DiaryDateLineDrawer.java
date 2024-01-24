package jp.sourceforge.gokigen.diary;

import java.io.File;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

public class DiaryDateLineDrawer implements ICanvasDrawer
{
	int    totalDatas = 0;
    int    currentPosition = 0;
    int    dayItems[] = null;
    String dataFiles[] = null;
    String dataDirectory = null;

    /**
     *  コンストラクタ
     * 
     */
	public DiaryDateLineDrawer()
    {
        // Log.v(Main.APP_IDENTIFIER, "DiaryDateLineDrawer::DiaryDateLineDrawer() ");
    }
	
	/**
	 *  ファイル名をもらって、表示するデータを決める
	 * 
	 * @param targetFileName
	 */
	public void prepare(String fileName)
	{
        // Log.v(Main.APP_IDENTIFIER, "DiaryDateLineDrawer::prepare() " + fileName);

    	// データの順番を確認する
		checkDataOrder(fileName);
	}

	/**
	 *  表示データをひとつ前に移動させる
	 * 
	 * @return 前にあるファイル名
	 */
	public String moveToPreviousData()
	{
		if (currentPosition > 0)
		{
			currentPosition--;
		}
		String fileName = dataFiles[currentPosition];
		return (dataDirectory + "/" + fileName);		
	}

	/**
	 *  表示データをひとつ次に移動させる
	 * 
	 * @return 次にあるファイル名
	 */
	public String moveToNextData()
	{
		if (currentPosition < (totalDatas - 1))
		{
            currentPosition++;
		}
		String fileName = dataFiles[currentPosition];
		return (dataDirectory + "/" + fileName);		
    }

	/**
	 *  描画する
	 * 
	 */
    public void drawOnCanvas(Canvas canvas)
    {
    	//Log.v(Main.APP_IDENTIFIER, "DiaryDateLineDrawer::drawOnCanvas() " + currentPosition + "/" + totalDatas);
        Paint paint = new Paint();
        paint.setColor(0x00004000);

        int widthMargin = 8;
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float bandWidth = width - widthMargin * 2;

        // 描画領域を塗りつぶす
        //canvas.drawColor(0x00004000);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawRect((float) 0, (float) 0, (float) width, (float) height, paint);			

        // スケールの表示
        paint.setColor(Color.LTGRAY);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawLine(widthMargin, (height / 2), (width - widthMargin), (height / 2), paint);

        // 2時間おきに区切り線を入れる
        for (int index = 0; index <= 12; index++)
        {
        	float drawX = widthMargin + ((bandWidth * index) / 12);
        	float drawY = (height / 2);
        	if ((index % 3) == 0)
            {
        	    // 6時間単位では、線を上に伸ばす
        	    canvas.drawLine(drawX, (drawY - 3), drawX, (drawY + 3), paint);
            }
        	else
            {
        	    canvas.drawLine(drawX, (drawY), drawX, (drawY + 3), paint);
            }
        }

        float markerHeight = height / 2;

        // 時分にあわせたマーカーを描画
        paint.setColor(Color.GREEN);
		for (int index = 0; index < totalDatas; index++)
		{
		    
			// dayItemsは秒単位なので...
			float dataPosition = ((float) dayItems[index]) * ((float) bandWidth) / (60 * 60 * 24) + widthMargin;
            canvas.drawRect((dataPosition - 1), (markerHeight - 3), (dataPosition + 1),(markerHeight + 1), paint);			
        	//canvas.drawRect((dataPosition - 1), (0), (dataPosition + 1),(height), paint);			
		}

        // 現在データの位置を示すマーカーを描画
		if (totalDatas > currentPosition)
		{
			float dataPosition = ((float) dayItems[currentPosition]) * ((float) bandWidth) / (60 * 60 * 24) + widthMargin;
	        paint.setColor(Color.YELLOW);
		    canvas.drawRect((dataPosition - 2), (markerHeight - 4) , (dataPosition + 2), (markerHeight + 4), paint);
		}

        // ラベルを表示
        paint.setColor(Color.LTGRAY);
        canvas.drawText("0", (widthMargin), (height - 2), paint);
        canvas.drawText("12", (width / 2), (height - 2), paint);
        canvas.drawText("24", (widthMargin + bandWidth), (height - 2), paint);
        
        //Log.v(Main.APP_IDENTIFIER, "DiaryDateLineDrawer::drawOnCanvas() finished.");
    }

    /**
     *  記録データの日時(文字列)を応答する
     * 
     * @return 日時の文字列
     */
    public String getDateTimeString()
    {
    	String outputData = "";
    	
    	outputData = " " + (currentPosition + 1) + "/" + totalDatas + " ";
    	return (outputData);
    }
    
    /**
     *  タッチされたときの処理
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	// とりあえず何もしない
        return (false);
    }
    
	/**
     *  一日に含まれるデータの順番を検索する
     * 
     * @param directory
     */
	private void checkDataOrder(String targetFileName)
    {
        try
        {
    		int slashIndex = targetFileName.lastIndexOf("/");
    		dataDirectory = targetFileName.substring(0, slashIndex);
            String fileString = targetFileName.substring(slashIndex + 1);
    		totalDatas = 0;
            dayItems = null;
            File checkDirectory = new File(dataDirectory);
            if (checkDirectory.exists() == false)
            {
                // データがない...終了する
                Log.v(Main.APP_IDENTIFIER, "checkDataOrder() abort... " + dataDirectory);
                return;
            }

            String[] dirList = checkDirectory.list();
            if (dirList != null)
            {
                // List の items をソートする！ 
                java.util.Arrays.sort(dirList);
                
                dayItems = new int[dirList.length];
                dataFiles = new String[dirList.length];
                
                // ファイル一覧を作り上げる
                for (String fileName : dirList)
                {
                    // データファイルかどうかチェック
                    if (fileName.endsWith(".txt") == false)
                    {
                        continue;
                    }

                    // ファイル名の時刻部分を抽出する
            		int hourIndex = fileName.indexOf("-diary") + 6;
            		if (hourIndex > 0)
            		{
                        try
                        {
                   		    String hourString = fileName.substring(hourIndex, hourIndex + 2);
                    		String minString = fileName.substring(hourIndex + 2, hourIndex + 4);
                    		String secString = fileName.substring(hourIndex + 4, hourIndex + 6);                    		
                            int seconds = (Integer.parseInt(hourString, 10) * 60 * 60) + (Integer.parseInt(minString, 10) * 60) + (Integer.parseInt(secString, 10));

                            // 現在の位置を示すもの 
                            if (fileName.matches(fileString) == true)
                            {
                            	// データの順番と経過秒数を記憶する
                            	currentPosition = totalDatas;
                            }

                            // データの秒数とファイル名を記憶する
                            dayItems[totalDatas] = seconds;
                            dataFiles[totalDatas] = fileName;
                            
                            // データ数をカウントアップ
                            totalDatas++;
                        }
                        catch (Exception e)
                        {
                        	// 何もしない
                        }
             		}   
                }
            }    
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EX : " + ex.getMessage() + ", fileName : " + targetFileName);
        }
    }
}
