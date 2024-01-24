package jp.sourceforge.gokigen.diary;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * 
 * @author MRSa
 */
public class MainListener implements OnClickListener, OnTouchListener, OnKeyListener, ICalendarDatePickup, IPassphraseInputCallback
{
    public static final int MENU_ID_PREFERENCES   = (Menu.FIRST + 1);
    public static final int MENU_ID_ABOUT_GOKIGEN = (Menu.FIRST + 2);
    public static final int MENU_ID_GOKIGEN_GRAPH = (Menu.FIRST + 3);
    public static final int MENU_ID_MOVE_TODAY    = (Menu.FIRST + 4);

    public static final String INTENTINFO_DURATION = "LOCATION_DURATION";
    public static final String INTENTINFO_GPSTYPE  = "LOCATION_GPSTYPE";

    private Activity parent = null;  // 親分

    private LocationListenerService locationHolderService = null;

    private ExternalStorageFileUtility fileUtility = null;

    private final LocationDataReceiver locationReceiver = new LocationDataReceiver();

    private int showYear = 2010;
    private int showMonth = 9;
    private int showDay = 9;
    
    private int totalNumber = 0;
    private CalendarDialog calendardialog = null;
    private PassphraseInputDialog passphrasedialog = null;
    
    private boolean launchOk = true;
    
    /**
     *  コンストラクタ
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        boolean needCheckPassphrase = preferences.getBoolean("secretMode", false);   
        if (needCheckPassphrase == true)
        {
            // パスワードのチェックが必要...
            launchOk = false;
        }
        else
        {
            launchOk = true;
        }
    	
    	//  「入力」ボタンが押されたときの処理
        final ImageButton inputButton = (ImageButton) parent.findViewById(R.id.OpenInput);
        inputButton.setOnClickListener(this);

        // 現在位置確認ボタンとのリンク
        final ImageButton locationButton = (ImageButton) parent.findViewById(R.id.showMap);
        locationButton.setOnClickListener(this);

        // 検索ボタンとのリンク
        final ImageButton searchButton = (ImageButton) parent.findViewById(R.id.dataSearchButton);
        searchButton.setOnClickListener(this);

        // グラフボタンとのリンク
        final ImageButton gokigenButton = (ImageButton) parent.findViewById(R.id.showGokigenGraphButoon);
        gokigenButton.setOnClickListener(this);

        // 前日ボタンとのリンク
        final ImageButton previousButton = (ImageButton) parent.findViewById(R.id.movePreviousDay);
        previousButton.setOnClickListener(this);
        
        // 明日ボタンとのリンク
        final ImageButton nextButton = (ImageButton) parent.findViewById(R.id.moveNextDay);
        nextButton.setOnClickListener(this);
        
        // GPSの受信サービスを起動
        prepareGpsService();

        if (launchOk == false)
    	{
        	// パスワードロック中...一覧表示はパスワードが一致するまで表示しない。
    	    return;
    	}

        // 記入済みデータの一覧準備
        prepareListView();
    }
    
    /**
     *  データ一覧ビュー領域を準備する
     * 
     */
    private void prepareListView()
    {
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);

        // 今日ボタンとのリンク
        final Button todayButton = (Button) parent.findViewById(R.id.todayButton);
        todayButton.setOnClickListener(this);

        // 日付表示ボタンとのリンク
        final Button dateSelectionButton = (Button) parent.findViewById(R.id.dateSelectionButton);
        dateSelectionButton.setOnClickListener(this);

        // 今日の日付に設定する
        moveToToday();
    }
    
    /**
     *   一覧を今日の日付に更新する
     * 
     */
    private void moveToToday()
    {
        Calendar calendar = Calendar.getInstance();
        showYear = calendar.get(Calendar.YEAR);
        showMonth = calendar.get(Calendar.MONTH) + 1;
        showDay = calendar.get(Calendar.DAY_OF_MONTH);

        updateDateList();
    }

    /**
     *   一覧を指定した日付のものに更新する
     * 
     */
    private void updateDateList()
    {
        // ボタンに一覧を表示する日付を設定する
        Button dateSelectionButton = (Button) parent.findViewById(R.id.dateSelectionButton);
        String dateString = "" + showYear + "/" + showMonth + "/" + showDay;
        dateSelectionButton.setText(dateString);        
    	
        updateDataListView();
    }

    /**
     *   一覧を相対的に日を移動させる
     * 
     * 
     */
    private void moveDay(int value)
    {
    	Calendar calendar = new GregorianCalendar();
    	calendar.set(showYear, (showMonth - 1), showDay);
        calendar.add(Calendar.DATE, value);

        showYear = calendar.get(Calendar.YEAR);
        showMonth = calendar.get(Calendar.MONTH) + 1;
        showDay = calendar.get(Calendar.DATE);

        updateDateList();
    	return;
    }

    /**
     *   日時情報を設定する
     * 
     */
    public void decideDate(int year, int month, int day)
    {
        showYear = year;
        showMonth = month;
        showDay = day;

        updateDateList();
    	return;    	
    }
    
    /**
     *  付加情報を取得する
     * 
     */
    public boolean setAppendCharacter(int year, int month, char[] appendChar)
    {
        String dir = fileUtility.decideDateDirectory(year, month, 1);
        dir = dir.substring(0, dir.length() - 1);

        int slashIndex = dir.lastIndexOf("/");
    	String yearMonthString = dir.substring(0,  slashIndex + 1);

    	File checkDirectory = new File(yearMonthString);
        if (checkDirectory.exists() == false)
        {
            return (false);
        }

        // 月単位のファイルを取り出す
        String[] dirList = checkDirectory.list();
        if (dirList != null)
        {
            // List の items をソートする！ 
            java.util.Arrays.sort(dirList);
            
            // ファイル一覧を作り上げる
            for (String dirName : dirList)
            {
            	try
            	{
            		int base = year * 10000 + month * 100;
            		int value = Integer.parseInt(dirName) - base;
            		if ((value > 0)&&(value < CalendarDialog.NUMBER_OF_CALENDAR_BUTTONS))
            		{
            			// ディレクトリがある日付に点を打つ
                	    appendChar[value - 1] = '.';
            		}            		
            	}
            	catch (Exception ex)
            	{
                	Log.v(Main.APP_IDENTIFIER, "PARSE ERROR(DIRNAME) : " + dirName);            		
            	}
            }
        }
    	return (true);
    }    
    
    /**
     *  GPSの受信サービスを起動する
     */
    private void prepareGpsService()
    {
        /** 利用ＧＰＳタイプ、監視する時間間隔を取得する  **/
        boolean useNetworkGps = false;
        long    timeoutValue = 10 * 60 * 1000;
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

            // パラメータからデータを読み出す！
            useNetworkGps = preferences.getBoolean("useNetworkGps", false);
            timeoutValue  = Long.parseLong(preferences.getString("timeoutDuration", "7")) * 60 * 1000; // デフォルトは7分間隔で監視
        }
        catch (Exception ex)
        {
            // 特に何もしない
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }

        /** 設定値を渡しつつ、サービスを起動する **/
        Intent intent = new Intent(parent, LocationListenerService.class);
        intent.putExtra(INTENTINFO_DURATION, timeoutValue);
        intent.putExtra(INTENTINFO_GPSTYPE, useNetworkGps);
        parent.startService(intent);

        /** **/
        IntentFilter filter = new IntentFilter(LocationListenerService.ACTION);
        parent.registerReceiver(locationReceiver, filter);

        /** サービスにバインド **/
        parent.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

     }
    
    /**
     * 
     */
    public void finishListener()
    {
    	/** カレンダーダイアログを消しちゃう **/
        if (calendardialog != null)
        {
        	calendardialog.dismiss();
        }
    	
        /** バインド解除 **/
        parent.unbindService(serviceConnection);
        serviceConnection = null;
        
        /** 登録解除 **/
        parent.unregisterReceiver(locationReceiver);
        
        /**  サービスを停止する  **/
        Intent intent = new Intent(parent, LocationListenerService.class);
        parent.stopService(intent);

    }

    
    /**
     *  スタート準備
     */
    public void prepareToStart()
    {
    	if (launchOk == false)
    	{
    		// パスワードロック中...パスワード入力ダイアログを表示する
        	parent.showDialog(R.id.passphraseInput);
    	    return;
    	}
    }

    /**
     *  終了準備
     */
    public void shutdown()
    {
    	// 
    }
    
    /**
     *  他画面から戻ってきたとき...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == R.id.OpenInput)
        {
/**
            String message = parent.getString(R.string.cancelWrite);

            // 書き込みが成功した、とのことだった
            if (resultCode == DiaryInput.RESULT_DATA_WRITE_SUCCESS)
            {
                // 書き込みに成功した
                message = parent.getString(R.string.dataWritten);
            }
            else if (resultCode == DiaryInput.RESULT_DATA_WRITE_FAILURE)
            {
                // 書き出しに失敗した
                message = parent.getString(R.string.failedToWrite);
             }
            else
            {
                // キャンセルしたときには、Toastを出さない。
                return;
            }
               Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();
**/

            // 画像ファイル名をクリアする
            clearPictureFilePreference();        
        }

        if (requestCode == R.id.searchForm)
        {
            // 検索条件をクリアする
            clearSearchConditionPreference();
        }
        
        if (requestCode == R.id.gokigenGraphView)
        {
        	// グラフ表示条件をクリアする
        	clearGraphDatePreference();
        }
        
        // 画面を再表示したとき、リストを更新する(一応...)
        updateDataListView(); 
    }

    /**
     *   クリックされたときの処理
     */
    public void onClick(View v)
    {
    	if (launchOk == false)
    	{
    		// パスワードロック中...
    		return;
    	}

        int id = v.getId();
        if (id == R.id.OpenInput)
        {
            // 日記入力画面を開く
            showInputDiary(id);
        }
        else if (id == R.id.showMap)
        {
            // 地図を表示
            showLocationMap(id);
        }
        else if (id == R.id.dataSearchButton)
        {
        	// 検索画面を表示
        	showSearchScreen();
        }
        else if (id == R.id.showGokigenGraphButoon)
        {
        	// グラフを表示
        	showGokigenGraph();
        }
        else if (id == R.id.dateSelectionButton)
        {
            // 日付選択ダイアログを開く
            parent.showDialog(id);
        }
        else if (id == R.id.todayButton)
        {
            // 今日の表示に更新する
            moveToToday();
        }
        else if (id == R.id.movePreviousDay)
        {
            // 前の日に移動する
        	moveDay(-1);
        }
        else if (id == R.id.moveNextDay)
        {
            // 次の日に移動する
        	moveDay(1);
        }
        else
        {
            // unknown click event
        }
        
    }

    /**
     *  日付選択ボタンを表示する ... カレンダー形式で日付選択させるように変更したため、廃止。
     */
    private void showDatePickerDialog()
    {
         DatePickerDialog dlg = new DatePickerDialog(parent,  new DatePickerDialog.OnDateSetListener() 
         {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                showYear = year;
                showMonth = monthOfYear + 1;
                showDay = dayOfMonth;
                
                final Button dateSelectionButton = (Button) parent.findViewById(R.id.dateSelectionButton);
                String dateString = "" + showYear + "/" + showMonth + "/" + showDay;
                dateSelectionButton.setText(dateString);
                
                updateDataListView();
            }
         }, showYear, showMonth - 1, showDay);
         
         dlg.show();
    }
    
    /**
     * 
     * 
     * 
     * @return
     */
    private String getContentPreviewData(String fileName)
    {
        // 開くファイル名を生成する
        String dir = fileUtility.decideDateDirectory(showYear, showMonth, showDay);
        String name = dir + "/" + fileName;
        String contentData = "";
    	File checkFile = new File(name);
    	int fileLength = (int) checkFile.length();
		FileInputStream is = null;
        try
        {
        	byte[] buffer = new byte[fileLength];
    	    is = new FileInputStream(checkFile);
    	    is.read(buffer, 0, fileLength);
            is.close();
    		String data = new String(buffer);
        	int index = data.lastIndexOf("<revisedmessage>");
        	int tokenLength = 16;
        	if (index < 0)
        	{
        		index = data.indexOf("<message>");
        		tokenLength = 9;
        	}
            if (index < 0)
            {
            	// データがなかった...
            	return ("");
            }
            int start = index + tokenLength;
            int last = data.indexOf("<", start);
            if ((start + 32) < last)
            {
            	contentData = data.substring(start, (start + 32)) + "…";
            }
            else if (last < 0)
            {
            	contentData = data.substring(start);
            }
            else
            {
            	contentData = data.substring(start, last);
            }        	
        }
        catch (Exception ex)
        {
            if (is != null)
            {
            	try
            	{
                    is.close();
            	}
            	catch (Exception e)
            	{
                    //            		
            	}
            }
        }   
        return (contentData);
    }

    /**
     *  位置情報ファイル名かどうかチェックし、位置情報ファイル名のみ時刻情報として切り出し、表示する
     * 
     * @param name
     * @return
     */
    private SymbolListArrayItem parseLocationFileName(String name, boolean simpleList)
    {
        // データファイルかどうかチェック
        if (name.endsWith(".txt") == false)
        {
            return (null);
        }

        int pictureIcon = 0;
        int start = name.indexOf("P_");
        if (start > 0)
        {
            // 画像ファイルつき!
            pictureIcon = R.drawable.ic_attachment;
        }
        start = name.indexOf("Q_");
        if (start > 0)
        {
            // 画像ファイルつき! 共有した！
            pictureIcon = R.drawable.ic_share_attachment;
        }
        start = name.indexOf("S_");
        if (start > 0)
        {
            // 共有した！
            pictureIcon = R.drawable.ic_share_none;
        }

        start = name.indexOf("-diary");
        if (start < 0)
        {
            return (null);
        }
        int end = name.indexOf("_");
        if (end < 0)
        {
            return (null);
        }
        
        int prefix = name.indexOf(".");
        if (prefix < end)
        {
            return (null);
        }
        
        start = start + 6;

        String gokigenCategory = name.substring((start + 10), (start + 11));
        // TODO : ここで表示すべきカテゴリのアイテムか吟味し、以降の処理を考える
        
        String dateString = name.substring(start, (start + 2)) + ":" + name.substring((start + 2), (start + 4));
        String gokigenRate = name.substring((start + 7), (start + 9));
        String gokigenNumber = name.substring(start + 12, prefix);

        //Log.v(Main.APP_IDENTIFIER, "gokigenNumber : " + gokigenNumber);        

        // アイコンのIDを決める
        int gokigenIcon = DecideEmotionIcon.decideEmotionIcon(gokigenRate);
        int number = 0;
        String contentString = gokigenNumber;
        try
        {
            number = Integer.parseInt(gokigenNumber);
            if (number == 0)
            {
            	contentString = "";
            }            
            if (simpleList == false)
            {
            	// コンテンツの中身を取ってきて一覧表示
            	contentString = contentString + " " + getContentPreviewData(name);
            }
        }
        catch (Exception ex)
        {
            //
        }
        totalNumber = totalNumber + number;
        SymbolListArrayItem item = new SymbolListArrayItem(gokigenIcon, dateString, name, contentString, pictureIcon, number);
        return (item);
    }
    
    /**
     *  一覧表示情報を更新する
     * 
     */
    private void updateDataListView()
    {
        //Log.v(Main.APP_IDENTIFIER, "MainListener::updateDataListView()");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

        // パラメータからデータを読み出す！
        boolean simpleList = preferences.getBoolean("listPreviewContent", false);

        // 一覧を表示したいディレクトリの決定
        String dir = fileUtility.decideDateDirectory(showYear, showMonth, showDay);
        
        List<SymbolListArrayItem> items = new ArrayList<SymbolListArrayItem>();

        File targetDir = null;
        try
        {
            targetDir = new File(dir);
            if (targetDir.exists() == false)
            {
                Toast.makeText(parent, showYear + "/" + showMonth + "/" + showDay  + " : " + parent.getString(R.string.cannotOpenDirectory), Toast.LENGTH_SHORT).show();
            }
            String[] dirList = targetDir.list();
            totalNumber = 0;
            if (dirList != null)
            {
                // List の items をソートする！ 
                java.util.Arrays.sort(dirList);

                // ファイル一覧を作り上げる
                for (String dirName : dirList)
                {
                    SymbolListArrayItem listItem = parseLocationFileName(dirName, simpleList);
                    if (listItem != null)
                    {
                        items.add(listItem);
                    }
                }
                
            }

            // リストアダプターを生成し、設定する
            ListView fileListView = (ListView) parent.findViewById(R.id.messageListView);
            ListAdapter adapter = new SymbolListArrayAdapter(parent, R.layout.emotionicons, items);
            fileListView.setAdapter(adapter);
            //fileListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //@Override
                public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
                {
                    ListView listView = (ListView) parentView;
                    SymbolListArrayItem item = (SymbolListArrayItem) listView.getItemAtPosition(position);

                    /** リストが選択されたときの処理...データを開く  **/
                    openDiaryData(item.getTextResource2nd());
                }
            });
            
            // リストを末尾にスクロールさせる
            fileListView.setSelection(fileListView.getCount() - 1);
            
            String numberValue = "";
            if (totalNumber != 0)
            {
                numberValue = parent.getString(R.string.totalNumberTitle) + " " + totalNumber + preferences.getString("amountUnit", "");
            }

            // 値を設定する
            TextView numberView = (TextView) parent.findViewById(R.id.NumberArea);
            numberView.setText(numberValue);
            
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EX : " + ex.getMessage() + ", dir : " + dir);            
        }
        
    }
    
    /**
     *  入力済みのデータを表示する
     * 
     * @param fileName
     */
    private void openDiaryData(String fileName)
    {
        // 開くファイル名を生成する
        String dir = fileUtility.decideDateDirectory(showYear, showMonth, showDay);
        String name = dir + fileName;
        
        // 渡すデータを作って Intentとする
        Intent intent = new Intent(parent, jp.sourceforge.gokigen.diary.DiaryDataView.class);
        intent.putExtra(DiaryDataViewListener.DIARY_FILE, name);

        // データ表示用Activityを起動する
        parent.startActivityForResult(intent, R.id.messageListView);
    }
    
    
    /**
     *   触られたときの処理
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
    	if (launchOk == false)
    	{
    		// パスワードロック中
    		return (false);
    	}

    	// int id = v.getId();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
        }
        return (false);
    }

    /**
     *  キーを押す
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
    	if (launchOk == false)
    	{
    		// パスワードロック中
    		return (false);
    	}

    	int action = event.getAction();
        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {
        }        
        return (false);
    }
    

    /**
     *   メニューへのアイテム追加
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
        menuItem.setIcon(android.R.drawable.ic_menu_preferences);

        menuItem = menu.add(Menu.NONE, MENU_ID_MOVE_TODAY, Menu.NONE, parent.getString(R.string.today));
        menuItem.setIcon(android.R.drawable.ic_menu_today);

        menuItem = menu.add(Menu.NONE, MENU_ID_GOKIGEN_GRAPH, Menu.NONE, parent.getString(R.string.gokigengraph_name));
        menuItem.setIcon(R.drawable.ic_menu_emoticons);

        menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT_GOKIGEN, Menu.NONE, parent.getString(R.string.about_gokigen));
        menuItem.setIcon(android.R.drawable.ic_menu_info_details);

        return (menu);
    }

    /**
     *   メニュー表示前の処理
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
        menu.findItem(MENU_ID_MOVE_TODAY).setVisible(true);
        menu.findItem(MENU_ID_GOKIGEN_GRAPH).setVisible(true);
        menu.findItem(MENU_ID_ABOUT_GOKIGEN).setVisible(true);
        return;
    }

    /**
     *   メニューのアイテムが選択されたときの処理
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if (launchOk == false)
    	{
    		// パスワードロック中
    		return (false);
    	}

        boolean result = false;
        switch (item.getItemId())
        {
          case MENU_ID_PREFERENCES:
            // 設定画面を表示する
            showPreference();
            result = true;
            break;

          case MENU_ID_ABOUT_GOKIGEN:
            // アプリの説明を表示する
            showAboutGokigen();
            result = true;
            break;

          case MENU_ID_GOKIGEN_GRAPH:
        	// グラフ画面を表示する
        	showGokigenGraph();
        	result = true;
        	break;

          case MENU_ID_MOVE_TODAY:
            // 今日の表示に更新する
            moveToToday();
        	result = true;
        	break;
        	
          default:
            result = false;
            break;
        }
        return (result);
    }

    /**
     *  ダイアログ表示の準備 (生成時、1回だけ設定する項目)
     * 
     */
    public Dialog onCreateDialog(int id)
    {
    	if (id == R.id.dateSelectionButton)
    	{
    		calendardialog = null;
    		calendardialog = new CalendarDialog(parent);
    		calendardialog.prepare(this);
    		return (calendardialog.getDialog());
    	}
    	else if (id == R.id.info_about_gokigen)
    	{
    		CreditDialog dialog = new CreditDialog(parent);
    		return (dialog.getDialog());
    	}
    	else if (id == R.id.passphraseInput)
    	{
    		passphrasedialog = null;
    		passphrasedialog = new PassphraseInputDialog(parent, this);
    		passphrasedialog.prepare(0, R.string.passPhraseTitle, false);
    		return (passphrasedialog.getDialog());
    	}
        return (null);
    }
    	
    /**
     *  ダイアログ表示の準備 (開くたびに設定する項目)
     * 
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
    	if (id == R.id.dateSelectionButton)
    	{
    		calendardialog.setYearMonth(dialog, showYear, showMonth);    		
    	}
    	else if (id == R.id.passphraseInput)
    	{
    		passphrasedialog.clearPhrase(dialog);
    	}
    	else
    	{
            // 何もしない...
    	}    	
    }

    /**
     *   アプリの情報を表示する
     * 
     */
    private void showAboutGokigen()
    {
        // アプリの情報(クレジット)を表示する！
    	parent.showDialog(R.id.info_about_gokigen);
    }    

    /**
     *  キーワード検索画面を表示する処理
     */
    private void showSearchScreen()
    {
        try
        {
            String dir = fileUtility.decideDateDirectory(showYear, showMonth, showDay);
            String dateString = "" + showYear + "/" + showMonth + "/" + showDay;

            // 検索画面を呼び出す
            Intent searchIntent = new Intent(parent,jp.sourceforge.gokigen.diary.DiarySearch.class);
            searchIntent.putExtra(DiarySearch.TARGET_DIR, dir);
            searchIntent.putExtra(DiarySearch.TARGET_LABEL, dateString);
            parent.startActivityForResult(searchIntent, R.id.searchForm);
        }
        catch (Exception e)
        {
             // 例外発生...
            Log.v(Main.APP_IDENTIFIER, "showPreference() : " + e.getMessage());
        }    	
    }

    /**
     *  設定画面を表示する処理
     */
    private void showPreference()
    {
        try
        {
            // 設定画面を呼び出す
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.diary.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // 例外発生...
            Log.v(Main.APP_IDENTIFIER, "showPreference() : " + e.getMessage());
        }
    }

    /**
     *  メッセージ入力画面を表示する処理
     * @param buttonId  表示するトリガとなったオブジェクトのID
     */
    private void showInputDiary(int buttonId)
    {
        try
        {
            // 編集Activityを呼び出してみる
            Intent inputIntent = new Intent(parent, jp.sourceforge.gokigen.diary.DiaryInput.class);
            parent.startActivityForResult(inputIntent, buttonId);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }
    }

    /**
     *  ごきげんグラフ画面を表示する
     * @param buttonId
     */
    private void showGokigenGraph()
    {
        try
        {
        	Log.v(Main.APP_IDENTIFIER, "MainListener::showGokigenGraph()");
        	
        	// グラフ描画日を入れる
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("graphYear",  showYear);
            editor.putInt("graphMonth", showMonth);
            editor.putInt("graphDay",   showDay);
            editor.commit();

            // グラフ描画Activityを呼び出してみる
            Intent graphIntent = new Intent(parent, jp.sourceforge.gokigen.diary.GokigenGraph.class);
            graphIntent.putExtra(GokigenGraph.TARGET_YEAR, showYear);
            graphIntent.putExtra(GokigenGraph.TARGET_MONTH, showMonth);
            graphIntent.putExtra(GokigenGraph.TARGET_DAY, showDay);
            parent.startActivityForResult(graphIntent, R.id.gokigenGraphView);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }   	
    }
    
    
    /**
     *  地図を表示する
     * 
     * @param buttonId
     */
    private void showLocationMap(int buttonId)
    {        
        try
        {
            // オフラインモードかチェックし、オフライン時にはマップ画面を開かないようにする
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            boolean isOffline = preferences.getBoolean("offlineMode", false);
            if (isOffline == true)
            {
                Toast.makeText(parent, parent.getString(R.string.warn_offline), Toast.LENGTH_SHORT).show();
                return;
            }

            // Intentで表示する
            String addMonth = "";
            if (showMonth < 10)
            {
                addMonth =  "0";
            }
            String addDate = "";
            if (showDay < 10)
            {
                addDate = "0";
            }            
            String locationFile =  fileUtility.decideDateDirectory(showYear, showMonth, showDay) + showYear + addMonth + showMonth + addDate + showDay + "-" + "location.csv";
            Intent locationIntent = new Intent(parent, jp.sourceforge.gokigen.diary.LocationMap.class);
            locationIntent.putExtra(LocationMap.LOCATION_FILE, locationFile);
            locationIntent.putExtra(LocationMap.LOCATION_ISCURRENT, true);
            parent.startActivityForResult(locationIntent, buttonId);
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }        
    }

    /**
     *  起動パスワードのチェックを行う
     * 
     * @param checkData
     * @return
     */
    public boolean inputPassphraseFinished(String checkData)
    {
        try
        {
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            String passPhrase = preferences.getString("passPhrase", "gokigen");
            if (checkData.matches(passPhrase) == true)
            {
            	launchOk = true;

            	// 記入済みデータの一覧を準備する
                prepareListView();
            }
            else
            {
                // Log.v(Main.APP_IDENTIFIER, "I: " + checkData + " T: " + passPhrase);
            	
            	// パスワードがマッチしなかった場合...
                Toast.makeText(parent, parent.getString(R.string.passphraseIsWrong), Toast.LENGTH_SHORT).show();
            
                // 終了します。
            	parent.finish();
            }
        }
        catch (Exception ex)
        {
        	//
        	Log.v(Main.APP_IDENTIFIER, "Ex, inputPassphraseFinished() : " + checkData);
        }
        return (true);
    }

    /**
     *  パスワード入力がキャンセルされた
     * 
     */
    public  void inputPassphraseCanceled()
    {
         // 何も処理しない
    }


    /**
     *   
     * 
     */
    private void clearPictureFilePreference()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("takenPictureFileName", "");
        editor.commit();
    }

    /**
     *   
     * 
     */
    private void clearSearchConditionPreference()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("searchCondition", "");
        editor.putString("searchDirectory", "");
        editor.commit();
    }

    /**
     *  グラフ表示する年月日をクリアする
     * 
     * @param year
     * @param month
     * @param day
     */
    private void clearGraphDatePreference()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("graphYear",  0);
        editor.putInt("graphMonth", 0);
        editor.putInt("graphDay",   0);
        editor.commit();    	
    }

    /**
     *  現在位置をデータに格納する
     * 
     * @param data
     * @return
     */
    private void storeCurrentLocation(MyLocation data)
    {
    	String locationString = "";
        String locationTimeString = "";
        int currentLatitude = 0;
        int currentLongitude = 0;
        try
        {
            // 記録データの準備
            locationTimeString = GeocoderWrapper.getDateTimeString(data.getTime());
            locationString = data.getLocationInfo();
            currentLatitude = (int) (data.getLatitude() * 1E6);
            currentLongitude = (int) (data.getLongitude() * 1E6);

            // 位置情報をPreferenceに書き込む
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("Latitude",  currentLatitude);
            editor.putInt("Longitude", currentLongitude);
            editor.putString("LocationName", locationString);
            editor.putString("LocationTime", locationTimeString);
            editor.commit();
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "GeocoderWrapper::storeCurrentLocation()" + ex.toString() + " " + ex.getMessage() + " " + currentLatitude + "," + currentLongitude + " " + locationString);
        }
        return;
    }

    /**
     *   サービスとこのActivityクラスを結びつける！
     * 
     */    
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        //@Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            locationHolderService = ((LocationListenerService.LocationHolderBinder)service).getService();
        }

        //@Override
        public void onServiceDisconnected(ComponentName className)
        {
            locationHolderService = null;
            System.gc();
        }
    };
    /**
     *  サービスから送られてきたIntentを受信する！
     * @author MRSa
     *
     */
    private class LocationDataReceiver extends BroadcastReceiver implements IGeocoderResultReceiver
    {
        private GeocoderWrapper geocoder = null;
        private boolean        checkGeocoding = false;

    	/**
         *   サービスから通知を受けた！
         */
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String message = "";
            String dateData = "";
            String   myLocale = "en";
            try
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
                myLocale = preferences.getString("myLocale", "en");

                MyLocation data = locationHolderService.getLocation();

                // 現在時刻を示す文字列を取得する
                dateData = GeocoderWrapper.getDateTimeString(data.getTime());

                // ジオコーディングを実施すると、電波条件が悪い場合、入力中に画面がロックアップするのを防止する
                // (UIスレッドとは切り離して動かせ、と書いてあった...)
                message = GeocoderWrapper.getLocationString(data.getLatitude(), data.getLongitude());

                // 現在の位置情報を一時記録する (ジオコーディング実施後に位置を記憶する)
                //message =  storeCurrentLocation(data);
                
                // ジオコーディング(現在位置の住所取得)を指示 (バックグラウンドで処理。)
                if (checkGeocoding == false)
                {
                    checkGeocoding = true;

                    /** ジオコーダーの準備(AsyncTaskは1回しか使えないらしいので **/
                    geocoder = null;
                    geocoder = new GeocoderWrapper(parent, this, new Locale(myLocale));
                    geocoder.execute(data);                	
                }

                //  現在時刻と位置(緯度・経度の文字列)を表示する...
                message = message + "\n" + "(" + dateData + ")";
                TextView printArea = (TextView) parent.findViewById(R.id.InformationArea);
                printArea.setText(message.toCharArray(), 0, message.length());
            }
            catch (Exception ex)
            {
                // 例外発生、その旨画面表示を行う。
                message = "Ex :" + ex.toString() + " (" + ex.getMessage() + "), MSG : " + message;
                Log.v(Main.APP_IDENTIFIER, message);

                TextView printArea = (TextView) parent.findViewById(R.id.InformationArea);
                printArea.setText(message.toCharArray(), 0, message.length());
            }            
        }

        /**
         *  ジオコーディングが完了したときの処理
         *  (位置の文字列を表示する)
         */
        public void  receivedResult(MyLocation location)
        {
            try
            {
                // 位置情報をPreferenceに記録する
            	storeCurrentLocation(location);
            	
            	//  現在時刻と位置(緯度・経度)を表示する
                String locationTimeString = GeocoderWrapper.getDateTimeString(location.getTime());
                String message = location.getLocationInfo();
                message = message + "\n" + "(" + locationTimeString + ")";
                TextView printArea = (TextView) parent.findViewById(R.id.InformationArea);
                printArea.setText(message.toCharArray(), 0, message.length());
            }
            catch (Exception ex)
            {
            	Log.v(Main.APP_IDENTIFIER, "MainListener::receivedResult() " + ex.toString());
            }
            
            // ジオコーディング中のフラグを解除する
            checkGeocoding = false;
        }
    }
}
