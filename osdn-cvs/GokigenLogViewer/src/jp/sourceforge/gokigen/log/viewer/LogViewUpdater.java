package jp.sourceforge.gokigen.log.viewer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 *  ログデータを取得して、更新する処理！
 * 
 * @author MRSa
 *
 */
public class LogViewUpdater implements MySpinnerDialog.IExectionTask
{
	private final int BUFFER_SIZE = 4096;
	private MySpinnerDialog busyDialog = null;
	private List<SymbolListArrayItem> listItems = null;
    private Activity parent = null;

    private int     tempCount = 0;
    
    private String detailDataToShow = "";
	
	public LogViewUpdater(Activity arg)
    {
        busyDialog = new MySpinnerDialog(arg);
        parent = arg;
    }

    /**
     *  ログデータの更新処理！
     * 
     * @param filterText
     */
    public void refreshLogData()
    {
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
    	return (parent.getString(R.string.busyMessage));
    }

    /**
     *  処理実行前の準備 (なにかあれば)
     *  
     */
	public void prepareTask(Activity parent)
	{
        tempCount++;
	}
	
	/**
	 *  時間のかかる処理を実行する
	 *  
	 */
    public void executeTask()
    {
        try
        {
        	// Preferencesからデータをとってくる
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        	String ringbuffer = preferences.getString("useRingBuffer", "main");
        	String filterString = preferences.getString("filterText", "");
        	String filterRegEx = preferences.getString("filterRegEx", "");
        	String logFormat = preferences.getString("logFormat", "brief");
        	String filterSpec =  preferences.getString("filterSpec", "*:v");
        	if (filterSpec.length() < 3)
        	{
        		// 不正な設定のときには、デフォルトの値を設定する。
        		filterSpec = "*:v";
        	}

            // アイテムパーサ
            SymbolListArrayItem.ItemParser itemParser = decideItemParser(logFormat);

        	// logcatコマンドの生成
        	ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");
            commandLine.add("-d");       //  -d:  dump the log and then exit (don't block)
            commandLine.add("-b");       //  -b <buffer> : request alternate ring buffer ('main' (default), 'radio', 'events')
            commandLine.add(ringbuffer); //     <buffer> option.
            commandLine.add("-v");       //  -v <format> :  Sets the log print format, where <format> is one of:
            commandLine.add(logFormat);  //                 brief process tag thread raw time threadtime long
            commandLine.add(filterSpec); //  フィルタースペック

            // logcatコマンドを実行
            Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
            
            // 応答データを取得する
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), BUFFER_SIZE);
            String line = null;

            // リストに表示するアイテムを生成する
            listItems = null;
            listItems = new ArrayList<SymbolListArrayItem>();
            do
            {
                 line = bufferedReader.readLine();

                 // フィルタのチェック
                 boolean isMatched = false;
                 try
                 {
                	 int filterLength = filterString.length();
                     int filterRegExLength = filterRegEx.length();
                     if ((filterLength == 0)&&(filterRegExLength == 0))
                     {
                    	 // フィルタが設定されていなければＯＫ
                    	 isMatched = true;
                     }
                     else if ((filterLength > 0)&&(line.contains(filterString) == true))
                     {
                    	 // 文字列が指定されていた！
                    	 isMatched = true;
                     }
                     else if ((filterRegExLength > 0)&&(line.matches(filterRegEx) == true))
                     {
                    	 // 正規表現にマッチした！
                    	 isMatched = true;
                     }                	 
                 }
                 catch (Exception ex)
                 {
                	 //
                 }
                 
                 // 指定されているフィルタにかかるものだけ抽出し、表示する
                 if (isMatched == true)
                 {
                	 SymbolListArrayItem listItem = new SymbolListArrayItem(itemParser.parseIconResource(line), itemParser.parseTextResource1st(line), itemParser.parseTextResource2nd(line), itemParser.parseTextResource3rd(line), itemParser.parseSubIconResource(line));
                     listItems.add(listItem);
                 }
            } while (line != null);

        } catch (Exception ex)
        {
            // 例外発生...でもなにもしない
        }    	
    }

    /**
     *  処理終了時に画面を更新する処理
     */
    public void finishTask(Activity parent)
    {
    	try
    	{    		
    		// リストアダプターを生成し、設定する
            ListView listView = (ListView) parent.findViewById(R.id.messageListView);
            ListAdapter adapter = new SymbolListArrayAdapter(parent,  R.layout.listview, listItems);
            listView.setAdapter(adapter);

            // アイテムを選択したときの処理
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //@Override
                public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
                {
                    ListView listView = (ListView) parentView;
                    SymbolListArrayItem item = (SymbolListArrayItem) listView.getItemAtPosition(position);

                    /// リストが選択されたときの処理...データを開く
                    showDetailData(item.getTextResource3rd());
                }
            });

            // 現在時刻とログ件数を取得
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String data = dateFormat.format(calendar.getTime());
            data = data + "\nCount : " + listItems.size();

            // 値を設定する
            TextView numberView = (TextView) parent.findViewById(R.id.BottomInformationArea);
            numberView.setText(data);
    	}
    	catch (Exception ex)
    	{
    		// 何もしない。
    	}
    }

    /**
     *    詳細データを応答する
     * 
     * @return
     */
    public String getDetailData()
    {
    	return (detailDataToShow);
    }
    
    /**
     *   詳細データを表示する
     * 
     * @param detailData
     */
    private void showDetailData(String detailData)
    {
    	detailDataToShow = detailData;
    	parent.showDialog(R.id.detail_dialog);
    }
    
    /**
     *  アイテムを解析するパーサーを決定する
     * 
     * @param logFormat  ログフォーマット
     * @return  解析用パーサ
     */
    private SymbolListArrayItem.ItemParser decideItemParser(String logFormat)
    {

    	if (logFormat.contains("threadtime") == true)
    	{
    		// threadtime
            return (new LogThreadtimeFormatParser());
    	}
    	else if (logFormat.contains("process") == true)
    	{
    		// process
            return (new LogProcessFormatParser());
    	}
    	else if (logFormat.contains("tag") == true)
    	{
    		// tag
            return (new LogTagFormatParser());
    	}
    	else if (logFormat.contains("raw") == true)
    	{
    		// raw
            return (new LogRawFormatParser());
    	}
    	else if (logFormat.contains("time") == true)
    	{
    		// time
            return (new LogTimeFormatParser());
    	}
    	else if (logFormat.contains("thread") == true)
    	{
    		// thread
            return (new LogThreadFormatParser());
    	}
    	else if (logFormat.contains("long") == true)
    	{
    		// long
            return (new LogLongFormatParser());
    	}

        // brief  	
        return (new LogBriefFormatParser());
    }
}
