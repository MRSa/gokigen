package jp.sourceforge.gokigen.diary;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class DiarySearchListener implements OnClickListener, OnTouchListener
{
//    public final int MENU_ID_SEARCH_CONDITION = (Menu.FIRST + 201);

    private Activity parent = null;  // 親分

    
	List<SearchResultListArrayItem> itemListToShow = null;

    
    private ProgressDialog progressDialog = null;
    private String tempKeyword = null;
    private String tempDirectory = null;

    
    /**
     *  コンストラクタ
     * @param argument
     */
    public DiarySearchListener(Activity argument)
    {
        parent = argument;
        progressDialog = new ProgressDialog(parent);
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

        String getDirectory = preferences.getString("searchDirectory", "");
        if (getDirectory.length() < 1)
        {
            String targetDir = parent.getIntent().getStringExtra(DiarySearch.TARGET_DIR);
            getDirectory = targetDir.substring(0, targetDir.substring(0, targetDir.length() - 2).lastIndexOf("/"));
            storeSearchKeyword(null, getDirectory);
        }

        // 検索キーワードを復旧させる
        String keyword = preferences.getString("searchKeyword", "");
        if (keyword.length() > 0)
        {
            final EditText diary  = (EditText) parent.findViewById(R.id.searchKeywordArea);
            diary.setText(keyword);        	
        }
        
    	// 次月ボタンとのリンク
        final ImageButton nextMonthButton = (ImageButton) parent.findViewById(R.id.searchNextMonth);        
        nextMonthButton.setOnClickListener(this);

        // 前月ボタンとのリンク
        final ImageButton prevMonthButton = (ImageButton) parent.findViewById(R.id.searchPreviousMonth);
        prevMonthButton.setOnClickListener(this);

    	
    	// 検索ボタンとのリンク
        final ImageButton searchButton = (ImageButton) parent.findViewById(R.id.doSearchButton);        
        searchButton.setOnClickListener(this);
    	
    	// マイク起動ボタンとのリンク
        final ImageButton micButton = (ImageButton) parent.findViewById(R.id.inputVoiceButton);

        // 音声認識 (オフラインモードかチェックし、オフライン時にはボタンを表示しない)
        boolean isOffline = preferences.getBoolean("offlineMode", false);        
        PackageManager pm = parent.getPackageManager();
    	List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
    	if ((activities.size() != 0)&&(isOffline != true))
        {
            micButton.setOnClickListener(this);
    	}
    	else
    	{
    		// マイクボタンを消す
    		micButton.setVisibility(View.INVISIBLE);
    	}
    	
    }

    /**
     *  スタート準備
     */
    public void prepareToStart()
    {
    
    }

    /**
     *  終了準備
     */
    public void shutdown()
    {
    	
    }
    
    /**
     *  他画面から戻ってきたとき...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if ((requestCode == R.id.inputVoiceButton)&&(resultCode == Activity.RESULT_OK))
        {
    		String message = "";

    		// 音声認識した情報を読み出す！
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Iterator<String> iterator = matches.iterator();
            while(iterator.hasNext())
            {
                message = message + iterator.next();
            } 

            // 文字列を末尾に追加する
            final EditText diary  = (EditText) parent.findViewById(R.id.searchKeywordArea);
            message = diary.getText().toString() + message;
            diary.setText(message);
        }
    	if (requestCode == R.id.searchResultView)
    	{
    		//
    	}

    	System.gc();  // 一応、ガベコレ...
    }

    /**
     *   クリックされたときの処理
     */
    public void onClick(View v)
    {
        // 検索用の情報を読み出す。
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        String getDirectory = preferences.getString("searchDirectory", "");
        String message = preferences.getString("searchKeyword", "");

        int id = v.getId();
        if (id == R.id.inputVoiceButton)
        {
        	// マイクでキーワードを音声認識 (しかし、Android 1.6だと、デフォルトロケールしか通用しないらしい...)
            String myLocale = preferences.getString("myLocale", "en");

            Log.v(Main.APP_IDENTIFIER, "MyLocale :" + myLocale);
            
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, parent.getString(R.string.speechRecognization));
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, myLocale);
            parent.startActivityForResult(intent, R.id.inputVoiceButton);
        }

    	// 検索実行！
        final EditText diary  = (EditText) parent.findViewById(R.id.searchKeywordArea);
        message = diary.getText().toString();
        if (message.length() <= 0)
        {
        	// 文字列が入力されていない...何もしない。
            return;
        }
        if (id == R.id.doSearchButton)
        {
        	// 検索実行！
            executeSearch(message, getDirectory);
        }
        else if (id == R.id.searchNextMonth)
        {
        	// 次月のデータを検索する
        	searchNextMonth(message, getDirectory);
        }
        else if (id == R.id.searchPreviousMonth)
        {
        	// 前月のデータを検索する
        	searchPreviousMonth(message, getDirectory);
        }
        return;
    }

    /**
     *   触られたときの処理
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // int id = v.getId();
        // int action = event.getAction();

        return (false);
    }

    /**
     *   メニューへのアイテム追加
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {
//    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_SEARCH_CONDITION, Menu.NONE, parent.getString(R.string.setSearchCondition));
//    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);

    	return (menu);
    }
    
    /**
     *   メニュー表示前の処理
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
//    	menu.findItem(MENU_ID_SEARCH_CONDITION).setVisible(true);
    	return;
    }

    /**
     *   メニューのアイテムが選択されたときの処理
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	boolean result = false;
    	switch (item.getItemId())
    	{
/*    	  case MENU_ID_SEARCH_CONDITION:
            // 検索オプションの設定＆検索! (ダイアログで設定する！)
    		result = true;
    		break;
*/
    	  default:
    		result = false;
    		break;
    	}
    	return (result);
    }

    /**
     *   来月のでデータを検索する
     * 
     */
    private void searchNextMonth(String searchKeyword, String searchDirectory)
    {
    	int slashIndex = searchDirectory.lastIndexOf("/");
    	if (slashIndex <= 0)
    	{
    		// スラッシュが見つけられなかった。。終了する。
    		return;
    	}

    	String nextMonth = "";
    	String yearMonthString = searchDirectory.substring(slashIndex + 1);
    	String monthString = yearMonthString.substring(4);
    	String yearString = yearMonthString.substring(0, 4);
    	int monthInt = Integer.parseInt(monthString);
    	int yearInt = Integer.parseInt(yearString);
    	if (monthInt == 12)
    	{
            // 次年の1月にする
    		yearInt = yearInt + 1;
            int prefixIndex = searchDirectory.substring(0, slashIndex).lastIndexOf("/");    		
            nextMonth = searchDirectory.substring(0, prefixIndex) + "/" + yearInt + "/" + yearInt + "01";
    	}
    	else
    	{
    		// 1ヶ月すすめる
    		int yearMonth = Integer.parseInt(yearMonthString);
    		yearMonth = yearMonth + 1;
            nextMonth = searchDirectory.substring(0, slashIndex) + "/" + yearMonth;    		
    	}
        executeSearch(searchKeyword, nextMonth);
    }
    
    /**
     *  前月のデータを検索する
     * 
     */
    private void searchPreviousMonth(String searchKeyword, String searchDirectory)
    {
    	int slashIndex = searchDirectory.lastIndexOf("/");
    	if (slashIndex <= 0)
    	{
    		// スラッシュが見つけられなかった。。終了する。
    		return;
    	}

    	String nextMonth = "";
    	String yearMonthString = searchDirectory.substring(slashIndex + 1);
    	String monthString = yearMonthString.substring(4);
    	String yearString = yearMonthString.substring(0, 4);
    	int monthInt = Integer.parseInt(monthString);
    	int yearInt = Integer.parseInt(yearString);
    	if (monthInt == 1)
    	{
            // 前年の12月にする
    		yearInt = yearInt - 1;
            int prefixIndex = searchDirectory.substring(0, slashIndex).lastIndexOf("/");    		
            nextMonth = searchDirectory.substring(0, prefixIndex) + "/" + yearInt + "/" + yearInt + "12";
    	}
    	else
    	{
    		// 1ヶ月戻す
    		int yearMonth = Integer.parseInt(yearMonthString);
    		yearMonth = yearMonth - 1;
            nextMonth = searchDirectory.substring(0, slashIndex) + "/" + yearMonth;    		
    	}
        executeSearch(searchKeyword, nextMonth);
    }
    
    /**
     *  検索を実行する！
     * 
     * @param searchKeyword 検索文字列
     * @param searchDirectory 検索ディレクトリ
     */
    private void executeSearch(String searchKeyword, String searchDirectory)
    {
        //  プログレスダイアログ（「検索中...」）を表示する。
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(parent.getString(R.string.searching));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // キーワードと検索ディレクトリを記憶する
		storeSearchKeyword(searchKeyword, searchDirectory);

        tempKeyword = searchKeyword;
        tempDirectory = searchDirectory;

        /**
         *  ダイアログ表示中の処理
         * 
         */
        Thread thread = new Thread(new Runnable()
        {  
            public void run()
            {
            	try
            	{
            		retrieveItems(tempKeyword, tempDirectory);
            		handler.sendEmptyMessage(0);
            	}
            	catch (Exception ex)
            	{
            		Log.v(Main.APP_IDENTIFIER, "ex. :" + ex.getMessage() + " KW: " + tempKeyword + " DIR: " + tempDirectory);
            	}
            }

            /**
             *   画面の更新
             */
            private final Handler handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                	updateResultList();
        			progressDialog.dismiss();
                }
            };   
        });
        try
        {
            thread.start();
        }
        catch (Exception ex)
        {

        }
    }

    /**
     *  アイテムを抽出する
     * 
     * @param searchKeyword
     * @param searchDirectory
     */
    private void retrieveItems(String searchKeyword, String searchDirectory)
    {
    	itemListToShow = new ArrayList<SearchResultListArrayItem>();

        File checkDirectory = new File(searchDirectory);
        if (checkDirectory.exists() == false)
        {
            return;
        }

        // 月単位の検索を実行！
        String[] dirList = checkDirectory.list();
        if (dirList != null)
        {
            // List の items をソートする！ 
            java.util.Arrays.sort(dirList);
            
            // ファイル一覧を作り上げる
            for (String dirName : dirList)
            {
                // サブディレクトリをすべてスキャンする
            	searchMain(searchKeyword, searchDirectory + "/" + dirName, itemListToShow);
            }
        }
    	return;
    }

    /**
     *  検索結果を表示する！
     */
    private void updateResultList()
    {
    	try
        {
    		// リストアダプターを生成し、設定する
            ListView resultListView = (ListView) parent.findViewById(R.id.searchResultView);
            ListAdapter adapter = new SearchResultListArrayAdapter(parent, R.layout.searchresult, itemListToShow);
            resultListView.setAdapter(adapter);
            resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //@Override
                public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
                {
                    ListView listView = (ListView) parentView;
                    SearchResultListArrayItem item = (SearchResultListArrayItem) listView.getItemAtPosition(position);

                    /** リストが選択されたときの処理...データを開く  **/
                    openDiaryData(item.getReference());
                }
            });

            int nofMatchedItems = itemListToShow.size();            
            String msg = tempDirectory.substring(tempDirectory.lastIndexOf("/") + 1) + "  " + parent.getString(R.string.nofMatchedItems) + " " + nofMatchedItems;

            final TextView diary  = (TextView) parent.findViewById(R.id.SearchInformationArea);
            diary.setText(msg);
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EX : " + ex.getMessage() + " KW : " + tempKeyword + " DIR : " + tempDirectory);
        }
        System.gc();
        return;
    }
    
    /**
     *  入力済みのデータを表示する
     * 
     * @param fileName
     */
    private void openDiaryData(String name)
    {
        // 渡すデータを作って Intentとする
        Intent intent = new Intent(parent, jp.sourceforge.gokigen.diary.DiaryDataView.class);
        intent.putExtra(DiaryDataViewListener.DIARY_FILE, name);

        // データ表示用Activityを起動する
        parent.startActivityForResult(intent, R.id.searchResultView);
    }
    
    /**
     *  ファイルリストから
     * 
     * @param keyword
     * @param directory
     */
    private void searchMain(String keyword, String directory, List<SearchResultListArrayItem> items)
    {
		// 月次集計
    	try
        {
    		// ディレクトリ
            File checkDirectory = new File(directory);
            if (checkDirectory.exists() == false)
            {
                // データがない...終了する
                return;
            }

            // 月単位の検索を実行！
            String[] dirList = checkDirectory.list();
            if (dirList != null)
            {
                // List の items をソートする！ 
                java.util.Arrays.sort(dirList);
                
                // ファイル一覧を作り上げる
                for (String dirName : dirList)
                {
                	SearchResultListArrayItem listItem = parseDataFileName(directory, dirName, keyword);
                    if (listItem != null)
                    {
                        items.add(listItem);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EX : " + ex.getMessage() + ", dir : " + directory);
        }    		
    }    

    /**
     *   ファイルの中にキーワードが含まれているかチェックする
     * 
     * @param fileName
     * @param keyword
     * @return
     */
    private SearchResultListArrayItem parseDataFileName(String directory, String fileName, String keyword)
    {
        // 位置情報ファイルかどうかチェック
        if (fileName.endsWith(".txt") == false)
        {
            return (null);
        }
        int start = fileName.indexOf("-diary");
    	if (start <= 0)
    	{
    		return (null);
    	}
        String gokigenRate = fileName.substring((start + 13), (start + 15));
        int gokigenIcon = DecideEmotionIcon.decideEmotionIcon(gokigenRate);

        String dateText = "";
        String outputText = fileName;
        String openFileName = directory + "/" + fileName;
    	File checkFile = new File(openFileName);
    	if (checkFile.canRead() == false)
    	{
    		checkFile = null;
    		return (null);
    	}
    	int fileLength = (int) checkFile.length();
    	boolean isMatch = false;
		FileInputStream is = null;
    	try
    	{
    		byte[] buffer = new byte[fileLength];
    		is = new FileInputStream(checkFile);
    		is.read(buffer, 0, fileLength);
            is.close();
    		String data = new String(buffer);
    		
    		int index = checkKeyword(data, keyword);
    		if (index > 0)
    		{
    			// タグチェック...
    			isMatch = true;
    			int startIndex = data.indexOf(">", (index - 4)) + 1;
    			if (startIndex > index)
    			{
    				startIndex = index - 4;
    			}
    			else
    			{
    			    startIndex = index;
    			}
    			int endIndex = data.indexOf("<", index);
    			if (endIndex > (index + 12))
    			{
    				endIndex = index + 12;
    			}
    			outputText = data.substring(startIndex, endIndex);
    			dateText = fileName.substring(4, 6) + "/" + fileName.substring(6, 8) + " " + fileName.substring(14, 16) + ":" + fileName.substring(16, 18) + " ";
    		}
    		data = null;
    		buffer = null;
    		is = null;
    	}
    	catch (Exception ex)
    	{
    		if (is != null)
    		{
    			try
    			{
    			    is.close();
    			}
    			catch (Exception ee)
    			{
    				
    			}
    		}
            Log.v(Main.APP_IDENTIFIER, "ex: " + ex.getMessage() + "  " + openFileName);
    	    return (null);
    	}
    	if (isMatch == false)
    	{
    		return (null);
    	}
    	SearchResultListArrayItem item = new SearchResultListArrayItem(dateText, gokigenIcon, outputText, openFileName);
    	return (item);
    }

    /**
     *  キーワードのチェック ...
     * 
     * @param data
     * @param keyword
     * @return
     */
    private int checkKeyword(String data, String keyword)
    {
        return (data.indexOf(keyword));	
    }
    
    /**
     *   検索条件を記憶する
     * 
     */
    private void storeSearchKeyword(String keyword, String searchDirectory)
    {
        if ((keyword == null)&&(searchDirectory == null))
        {
        	// 指定されていなかった場合には、何もせず終了する
        	return;
        }
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        if (keyword != null)
        {
            editor.putString("searchKeyword", keyword);
        }
        if (searchDirectory != null)
        {
            editor.putString("searchDirectory", searchDirectory);
        }
        editor.commit();
    }
}
