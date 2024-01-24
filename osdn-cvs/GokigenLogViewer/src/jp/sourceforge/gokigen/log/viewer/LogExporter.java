package jp.sourceforge.gokigen.log.viewer;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 *  ログデータをファイルに出力する処理！
 * 
 * @author MRSa
 *
 */
public class LogExporter  implements MySpinnerDialog.IExectionTask
{
	private MySpinnerDialog busyDialog = null;
	private ExternalStorageFileUtility fileUtility = null;
	private String outputLogFileName = null;
	private ListAdapter adapter = null;
	
	/**
	 *   コンストラクタ
	 * 
	 * @param arg
	 */
    public LogExporter(Activity arg)
    {
        busyDialog = new MySpinnerDialog(arg);
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);
    }

    /**
     *  ログデータのファイル出力処理！
     * 
     * @param filterName  ファイル名 (nullなら自分で生成)
     */
    public void exportLogData(String fileName)
    {
    	outputLogFileName = fileName;
        try
        {
       	   // 処理を実行する
       	   busyDialog.executeTask(this);
        }
        catch (Exception ex)
        {
       	 // なにもしない
        }
    }
    
    /**
     * スピナーに表示するメッセージを応答する
     * 
     */
    public String getSpinnerMessage(Activity parent)
    {
    	return (parent.getString(R.string.busyOutputLogMessage));
    }

    /**
     *  処理実行前の準備 (なにかあれば)
     *  
     */
	public void prepareTask(Activity parent)
	{
		// リストアダプターを生成し、設定する
        ListView listView = (ListView) parent.findViewById(R.id.messageListView);
        adapter = listView.getAdapter();
	}
    
	/**
	 *  時間のかかる処理を実行する
	 *  
	 */    
    public void executeTask()
    {
    	String outputFileName = outputLogFileName;
    	if (outputFileName == null)
    	{
    		outputFileName = createFileName();
    	}
    	
    	FileOutputStream out = fileUtility.openFileStream(outputFileName, false);
    	try
    	{
    	    int count = adapter.getCount();
    	    for (int index = 0; index < count; index++)
    	    {
    	        SymbolListArrayItem item = (SymbolListArrayItem) adapter.getItem(index);
    	        String data = item.getTextResource3rd() + "\r\n";
                out.write(data.getBytes());
            }
    		out.flush();
            out.close();
    	}
    	catch (Exception ex)
    	{
            Log.v(Main.APP_IDENTIFIER, "ERR>LogExporter::executeTask() : " + ex.getMessage());
    	}
    	out = null;
    	outputLogFileName = null;
    	System.gc();    
    }

    /**
     *  処理終了時に画面を更新する処理
     */
    public void finishTask(Activity parent)
    {
    	try
    	{
    		// 何もしない。
   	    }
    	catch (Exception ex)
    	{
    		// 何もしない。
    	}
    }

    
    /**
     *  ファイル名を生成する...
     * 
     * @return
     */
    private String createFileName()
    {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "logcat" + dateFormat.format(calendar.getTime()) + ".txt";
        
        return (fileName);
    }
}
