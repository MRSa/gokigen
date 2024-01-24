package jp.sourceforge.gokigen.log.viewer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LogClearUpdater implements MySpinnerDialog.IExectionTask
{
	private MySpinnerDialog busyDialog = null;
	private List<SymbolListArrayItem> listItems = null;
    private int     tempCount = 0;
	
    /**
     *   コンストラクタ
     * 
     * @param arg
     */
	public LogClearUpdater(Activity arg)
    {
        busyDialog = new MySpinnerDialog(arg);
    }

    /**
     *  ログデータの更新処理！
     * 
     * @param filterText
     */
    public void clearLogData()
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
        	// logcatコマンドの生成
        	ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");
            commandLine.add("-c");       //  -c: Clears (flushes) the entire log and exits.

            // logcatコマンドを実行
            Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));

            // リストに表示するアイテムを生成する
            listItems = null;
            listItems = new ArrayList<SymbolListArrayItem>();

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
     *   詳細データを表示する
     * 
     * @param detailData
     */
    private void showDetailData(String detailData)
    {
    	
    }
}
