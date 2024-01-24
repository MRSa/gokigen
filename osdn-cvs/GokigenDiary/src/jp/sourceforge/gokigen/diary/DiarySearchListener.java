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

    private Activity parent = null;  // �e��

    
	List<SearchResultListArrayItem> itemListToShow = null;

    
    private ProgressDialog progressDialog = null;
    private String tempKeyword = null;
    private String tempDirectory = null;

    
    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public DiarySearchListener(Activity argument)
    {
        parent = argument;
        progressDialog = new ProgressDialog(parent);
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
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

        // �����L�[���[�h�𕜋�������
        String keyword = preferences.getString("searchKeyword", "");
        if (keyword.length() > 0)
        {
            final EditText diary  = (EditText) parent.findViewById(R.id.searchKeywordArea);
            diary.setText(keyword);        	
        }
        
    	// �����{�^���Ƃ̃����N
        final ImageButton nextMonthButton = (ImageButton) parent.findViewById(R.id.searchNextMonth);        
        nextMonthButton.setOnClickListener(this);

        // �O���{�^���Ƃ̃����N
        final ImageButton prevMonthButton = (ImageButton) parent.findViewById(R.id.searchPreviousMonth);
        prevMonthButton.setOnClickListener(this);

    	
    	// �����{�^���Ƃ̃����N
        final ImageButton searchButton = (ImageButton) parent.findViewById(R.id.doSearchButton);        
        searchButton.setOnClickListener(this);
    	
    	// �}�C�N�N���{�^���Ƃ̃����N
        final ImageButton micButton = (ImageButton) parent.findViewById(R.id.inputVoiceButton);

        // �����F�� (�I�t���C�����[�h���`�F�b�N���A�I�t���C�����ɂ̓{�^����\�����Ȃ�)
        boolean isOffline = preferences.getBoolean("offlineMode", false);        
        PackageManager pm = parent.getPackageManager();
    	List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
    	if ((activities.size() != 0)&&(isOffline != true))
        {
            micButton.setOnClickListener(this);
    	}
    	else
    	{
    		// �}�C�N�{�^��������
    		micButton.setVisibility(View.INVISIBLE);
    	}
    	
    }

    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
    
    }

    /**
     *  �I������
     */
    public void shutdown()
    {
    	
    }
    
    /**
     *  ����ʂ���߂��Ă����Ƃ�...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if ((requestCode == R.id.inputVoiceButton)&&(resultCode == Activity.RESULT_OK))
        {
    		String message = "";

    		// �����F����������ǂݏo���I
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Iterator<String> iterator = matches.iterator();
            while(iterator.hasNext())
            {
                message = message + iterator.next();
            } 

            // ������𖖔��ɒǉ�����
            final EditText diary  = (EditText) parent.findViewById(R.id.searchKeywordArea);
            message = diary.getText().toString() + message;
            diary.setText(message);
        }
    	if (requestCode == R.id.searchResultView)
    	{
    		//
    	}

    	System.gc();  // �ꉞ�A�K�x�R��...
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        // �����p�̏���ǂݏo���B
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        String getDirectory = preferences.getString("searchDirectory", "");
        String message = preferences.getString("searchKeyword", "");

        int id = v.getId();
        if (id == R.id.inputVoiceButton)
        {
        	// �}�C�N�ŃL�[���[�h�������F�� (�������AAndroid 1.6���ƁA�f�t�H���g���P�[�������ʗp���Ȃ��炵��...)
            String myLocale = preferences.getString("myLocale", "en");

            Log.v(Main.APP_IDENTIFIER, "MyLocale :" + myLocale);
            
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, parent.getString(R.string.speechRecognization));
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, myLocale);
            parent.startActivityForResult(intent, R.id.inputVoiceButton);
        }

    	// �������s�I
        final EditText diary  = (EditText) parent.findViewById(R.id.searchKeywordArea);
        message = diary.getText().toString();
        if (message.length() <= 0)
        {
        	// �����񂪓��͂���Ă��Ȃ�...�������Ȃ��B
            return;
        }
        if (id == R.id.doSearchButton)
        {
        	// �������s�I
            executeSearch(message, getDirectory);
        }
        else if (id == R.id.searchNextMonth)
        {
        	// �����̃f�[�^����������
        	searchNextMonth(message, getDirectory);
        }
        else if (id == R.id.searchPreviousMonth)
        {
        	// �O���̃f�[�^����������
        	searchPreviousMonth(message, getDirectory);
        }
        return;
    }

    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // int id = v.getId();
        // int action = event.getAction();

        return (false);
    }

    /**
     *   ���j���[�ւ̃A�C�e���ǉ�
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
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
//    	menu.findItem(MENU_ID_SEARCH_CONDITION).setVisible(true);
    	return;
    }

    /**
     *   ���j���[�̃A�C�e�����I�����ꂽ�Ƃ��̏���
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	boolean result = false;
    	switch (item.getItemId())
    	{
/*    	  case MENU_ID_SEARCH_CONDITION:
            // �����I�v�V�����̐ݒ聕����! (�_�C�A���O�Őݒ肷��I)
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
     *   �����̂Ńf�[�^����������
     * 
     */
    private void searchNextMonth(String searchKeyword, String searchDirectory)
    {
    	int slashIndex = searchDirectory.lastIndexOf("/");
    	if (slashIndex <= 0)
    	{
    		// �X���b�V�����������Ȃ������B�B�I������B
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
            // ���N��1���ɂ���
    		yearInt = yearInt + 1;
            int prefixIndex = searchDirectory.substring(0, slashIndex).lastIndexOf("/");    		
            nextMonth = searchDirectory.substring(0, prefixIndex) + "/" + yearInt + "/" + yearInt + "01";
    	}
    	else
    	{
    		// 1���������߂�
    		int yearMonth = Integer.parseInt(yearMonthString);
    		yearMonth = yearMonth + 1;
            nextMonth = searchDirectory.substring(0, slashIndex) + "/" + yearMonth;    		
    	}
        executeSearch(searchKeyword, nextMonth);
    }
    
    /**
     *  �O���̃f�[�^����������
     * 
     */
    private void searchPreviousMonth(String searchKeyword, String searchDirectory)
    {
    	int slashIndex = searchDirectory.lastIndexOf("/");
    	if (slashIndex <= 0)
    	{
    		// �X���b�V�����������Ȃ������B�B�I������B
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
            // �O�N��12���ɂ���
    		yearInt = yearInt - 1;
            int prefixIndex = searchDirectory.substring(0, slashIndex).lastIndexOf("/");    		
            nextMonth = searchDirectory.substring(0, prefixIndex) + "/" + yearInt + "/" + yearInt + "12";
    	}
    	else
    	{
    		// 1�����߂�
    		int yearMonth = Integer.parseInt(yearMonthString);
    		yearMonth = yearMonth - 1;
            nextMonth = searchDirectory.substring(0, slashIndex) + "/" + yearMonth;    		
    	}
        executeSearch(searchKeyword, nextMonth);
    }
    
    /**
     *  ���������s����I
     * 
     * @param searchKeyword ����������
     * @param searchDirectory �����f�B���N�g��
     */
    private void executeSearch(String searchKeyword, String searchDirectory)
    {
        //  �v���O���X�_�C�A���O�i�u������...�v�j��\������B
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(parent.getString(R.string.searching));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // �L�[���[�h�ƌ����f�B���N�g�����L������
		storeSearchKeyword(searchKeyword, searchDirectory);

        tempKeyword = searchKeyword;
        tempDirectory = searchDirectory;

        /**
         *  �_�C�A���O�\�����̏���
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
             *   ��ʂ̍X�V
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
     *  �A�C�e���𒊏o����
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

        // ���P�ʂ̌��������s�I
        String[] dirList = checkDirectory.list();
        if (dirList != null)
        {
            // List �� items ���\�[�g����I 
            java.util.Arrays.sort(dirList);
            
            // �t�@�C���ꗗ�����グ��
            for (String dirName : dirList)
            {
                // �T�u�f�B���N�g�������ׂăX�L��������
            	searchMain(searchKeyword, searchDirectory + "/" + dirName, itemListToShow);
            }
        }
    	return;
    }

    /**
     *  �������ʂ�\������I
     */
    private void updateResultList()
    {
    	try
        {
    		// ���X�g�A�_�v�^�[�𐶐����A�ݒ肷��
            ListView resultListView = (ListView) parent.findViewById(R.id.searchResultView);
            ListAdapter adapter = new SearchResultListArrayAdapter(parent, R.layout.searchresult, itemListToShow);
            resultListView.setAdapter(adapter);
            resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //@Override
                public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
                {
                    ListView listView = (ListView) parentView;
                    SearchResultListArrayItem item = (SearchResultListArrayItem) listView.getItemAtPosition(position);

                    /** ���X�g���I�����ꂽ�Ƃ��̏���...�f�[�^���J��  **/
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
     *  ���͍ς݂̃f�[�^��\������
     * 
     * @param fileName
     */
    private void openDiaryData(String name)
    {
        // �n���f�[�^������� Intent�Ƃ���
        Intent intent = new Intent(parent, jp.sourceforge.gokigen.diary.DiaryDataView.class);
        intent.putExtra(DiaryDataViewListener.DIARY_FILE, name);

        // �f�[�^�\���pActivity���N������
        parent.startActivityForResult(intent, R.id.searchResultView);
    }
    
    /**
     *  �t�@�C�����X�g����
     * 
     * @param keyword
     * @param directory
     */
    private void searchMain(String keyword, String directory, List<SearchResultListArrayItem> items)
    {
		// �����W�v
    	try
        {
    		// �f�B���N�g��
            File checkDirectory = new File(directory);
            if (checkDirectory.exists() == false)
            {
                // �f�[�^���Ȃ�...�I������
                return;
            }

            // ���P�ʂ̌��������s�I
            String[] dirList = checkDirectory.list();
            if (dirList != null)
            {
                // List �� items ���\�[�g����I 
                java.util.Arrays.sort(dirList);
                
                // �t�@�C���ꗗ�����グ��
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
     *   �t�@�C���̒��ɃL�[���[�h���܂܂�Ă��邩�`�F�b�N����
     * 
     * @param fileName
     * @param keyword
     * @return
     */
    private SearchResultListArrayItem parseDataFileName(String directory, String fileName, String keyword)
    {
        // �ʒu���t�@�C�����ǂ����`�F�b�N
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
    			// �^�O�`�F�b�N...
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
     *  �L�[���[�h�̃`�F�b�N ...
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
     *   �����������L������
     * 
     */
    private void storeSearchKeyword(String keyword, String searchDirectory)
    {
        if ((keyword == null)&&(searchDirectory == null))
        {
        	// �w�肳��Ă��Ȃ������ꍇ�ɂ́A���������I������
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
