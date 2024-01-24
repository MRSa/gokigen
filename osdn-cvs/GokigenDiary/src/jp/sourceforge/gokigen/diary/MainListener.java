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

    private Activity parent = null;  // �e��

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
     *  �R���X�g���N�^
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        boolean needCheckPassphrase = preferences.getBoolean("secretMode", false);   
        if (needCheckPassphrase == true)
        {
            // �p�X���[�h�̃`�F�b�N���K�v...
            launchOk = false;
        }
        else
        {
            launchOk = true;
        }
    	
    	//  �u���́v�{�^���������ꂽ�Ƃ��̏���
        final ImageButton inputButton = (ImageButton) parent.findViewById(R.id.OpenInput);
        inputButton.setOnClickListener(this);

        // ���݈ʒu�m�F�{�^���Ƃ̃����N
        final ImageButton locationButton = (ImageButton) parent.findViewById(R.id.showMap);
        locationButton.setOnClickListener(this);

        // �����{�^���Ƃ̃����N
        final ImageButton searchButton = (ImageButton) parent.findViewById(R.id.dataSearchButton);
        searchButton.setOnClickListener(this);

        // �O���t�{�^���Ƃ̃����N
        final ImageButton gokigenButton = (ImageButton) parent.findViewById(R.id.showGokigenGraphButoon);
        gokigenButton.setOnClickListener(this);

        // �O���{�^���Ƃ̃����N
        final ImageButton previousButton = (ImageButton) parent.findViewById(R.id.movePreviousDay);
        previousButton.setOnClickListener(this);
        
        // �����{�^���Ƃ̃����N
        final ImageButton nextButton = (ImageButton) parent.findViewById(R.id.moveNextDay);
        nextButton.setOnClickListener(this);
        
        // GPS�̎�M�T�[�r�X���N��
        prepareGpsService();

        if (launchOk == false)
    	{
        	// �p�X���[�h���b�N��...�ꗗ�\���̓p�X���[�h����v����܂ŕ\�����Ȃ��B
    	    return;
    	}

        // �L���ς݃f�[�^�̈ꗗ����
        prepareListView();
    }
    
    /**
     *  �f�[�^�ꗗ�r���[�̈����������
     * 
     */
    private void prepareListView()
    {
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);

        // �����{�^���Ƃ̃����N
        final Button todayButton = (Button) parent.findViewById(R.id.todayButton);
        todayButton.setOnClickListener(this);

        // ���t�\���{�^���Ƃ̃����N
        final Button dateSelectionButton = (Button) parent.findViewById(R.id.dateSelectionButton);
        dateSelectionButton.setOnClickListener(this);

        // �����̓��t�ɐݒ肷��
        moveToToday();
    }
    
    /**
     *   �ꗗ�������̓��t�ɍX�V����
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
     *   �ꗗ���w�肵�����t�̂��̂ɍX�V����
     * 
     */
    private void updateDateList()
    {
        // �{�^���Ɉꗗ��\��������t��ݒ肷��
        Button dateSelectionButton = (Button) parent.findViewById(R.id.dateSelectionButton);
        String dateString = "" + showYear + "/" + showMonth + "/" + showDay;
        dateSelectionButton.setText(dateString);        
    	
        updateDataListView();
    }

    /**
     *   �ꗗ�𑊑ΓI�ɓ����ړ�������
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
     *   ��������ݒ肷��
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
     *  �t�������擾����
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

        // ���P�ʂ̃t�@�C�������o��
        String[] dirList = checkDirectory.list();
        if (dirList != null)
        {
            // List �� items ���\�[�g����I 
            java.util.Arrays.sort(dirList);
            
            // �t�@�C���ꗗ�����グ��
            for (String dirName : dirList)
            {
            	try
            	{
            		int base = year * 10000 + month * 100;
            		int value = Integer.parseInt(dirName) - base;
            		if ((value > 0)&&(value < CalendarDialog.NUMBER_OF_CALENDAR_BUTTONS))
            		{
            			// �f�B���N�g����������t�ɓ_��ł�
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
     *  GPS�̎�M�T�[�r�X���N������
     */
    private void prepareGpsService()
    {
        /** ���p�f�o�r�^�C�v�A�Ď����鎞�ԊԊu���擾����  **/
        boolean useNetworkGps = false;
        long    timeoutValue = 10 * 60 * 1000;
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

            // �p�����[�^����f�[�^��ǂݏo���I
            useNetworkGps = preferences.getBoolean("useNetworkGps", false);
            timeoutValue  = Long.parseLong(preferences.getString("timeoutDuration", "7")) * 60 * 1000; // �f�t�H���g��7���Ԋu�ŊĎ�
        }
        catch (Exception ex)
        {
            // ���ɉ������Ȃ�
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }

        /** �ݒ�l��n���A�T�[�r�X���N������ **/
        Intent intent = new Intent(parent, LocationListenerService.class);
        intent.putExtra(INTENTINFO_DURATION, timeoutValue);
        intent.putExtra(INTENTINFO_GPSTYPE, useNetworkGps);
        parent.startService(intent);

        /** **/
        IntentFilter filter = new IntentFilter(LocationListenerService.ACTION);
        parent.registerReceiver(locationReceiver, filter);

        /** �T�[�r�X�Ƀo�C���h **/
        parent.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

     }
    
    /**
     * 
     */
    public void finishListener()
    {
    	/** �J�����_�[�_�C�A���O���������Ⴄ **/
        if (calendardialog != null)
        {
        	calendardialog.dismiss();
        }
    	
        /** �o�C���h���� **/
        parent.unbindService(serviceConnection);
        serviceConnection = null;
        
        /** �o�^���� **/
        parent.unregisterReceiver(locationReceiver);
        
        /**  �T�[�r�X���~����  **/
        Intent intent = new Intent(parent, LocationListenerService.class);
        parent.stopService(intent);

    }

    
    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
    	if (launchOk == false)
    	{
    		// �p�X���[�h���b�N��...�p�X���[�h���̓_�C�A���O��\������
        	parent.showDialog(R.id.passphraseInput);
    	    return;
    	}
    }

    /**
     *  �I������
     */
    public void shutdown()
    {
    	// 
    }
    
    /**
     *  ����ʂ���߂��Ă����Ƃ�...
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

            // �������݂����������A�Ƃ̂��Ƃ�����
            if (resultCode == DiaryInput.RESULT_DATA_WRITE_SUCCESS)
            {
                // �������݂ɐ�������
                message = parent.getString(R.string.dataWritten);
            }
            else if (resultCode == DiaryInput.RESULT_DATA_WRITE_FAILURE)
            {
                // �����o���Ɏ��s����
                message = parent.getString(R.string.failedToWrite);
             }
            else
            {
                // �L�����Z�������Ƃ��ɂ́AToast���o���Ȃ��B
                return;
            }
               Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();
**/

            // �摜�t�@�C�������N���A����
            clearPictureFilePreference();        
        }

        if (requestCode == R.id.searchForm)
        {
            // �����������N���A����
            clearSearchConditionPreference();
        }
        
        if (requestCode == R.id.gokigenGraphView)
        {
        	// �O���t�\���������N���A����
        	clearGraphDatePreference();
        }
        
        // ��ʂ��ĕ\�������Ƃ��A���X�g���X�V����(�ꉞ...)
        updateDataListView(); 
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
    	if (launchOk == false)
    	{
    		// �p�X���[�h���b�N��...
    		return;
    	}

        int id = v.getId();
        if (id == R.id.OpenInput)
        {
            // ���L���͉�ʂ��J��
            showInputDiary(id);
        }
        else if (id == R.id.showMap)
        {
            // �n�}��\��
            showLocationMap(id);
        }
        else if (id == R.id.dataSearchButton)
        {
        	// ������ʂ�\��
        	showSearchScreen();
        }
        else if (id == R.id.showGokigenGraphButoon)
        {
        	// �O���t��\��
        	showGokigenGraph();
        }
        else if (id == R.id.dateSelectionButton)
        {
            // ���t�I���_�C�A���O���J��
            parent.showDialog(id);
        }
        else if (id == R.id.todayButton)
        {
            // �����̕\���ɍX�V����
            moveToToday();
        }
        else if (id == R.id.movePreviousDay)
        {
            // �O�̓��Ɉړ�����
        	moveDay(-1);
        }
        else if (id == R.id.moveNextDay)
        {
            // ���̓��Ɉړ�����
        	moveDay(1);
        }
        else
        {
            // unknown click event
        }
        
    }

    /**
     *  ���t�I���{�^����\������ ... �J�����_�[�`���œ��t�I��������悤�ɕύX�������߁A�p�~�B
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
        // �J���t�@�C�����𐶐�����
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
            	// �f�[�^���Ȃ�����...
            	return ("");
            }
            int start = index + tokenLength;
            int last = data.indexOf("<", start);
            if ((start + 32) < last)
            {
            	contentData = data.substring(start, (start + 32)) + "�c";
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
     *  �ʒu���t�@�C�������ǂ����`�F�b�N���A�ʒu���t�@�C�����̂ݎ������Ƃ��Đ؂�o���A�\������
     * 
     * @param name
     * @return
     */
    private SymbolListArrayItem parseLocationFileName(String name, boolean simpleList)
    {
        // �f�[�^�t�@�C�����ǂ����`�F�b�N
        if (name.endsWith(".txt") == false)
        {
            return (null);
        }

        int pictureIcon = 0;
        int start = name.indexOf("P_");
        if (start > 0)
        {
            // �摜�t�@�C����!
            pictureIcon = R.drawable.ic_attachment;
        }
        start = name.indexOf("Q_");
        if (start > 0)
        {
            // �摜�t�@�C����! ���L�����I
            pictureIcon = R.drawable.ic_share_attachment;
        }
        start = name.indexOf("S_");
        if (start > 0)
        {
            // ���L�����I
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
        // TODO : �����ŕ\�����ׂ��J�e�S���̃A�C�e�����ᖡ���A�ȍ~�̏������l����
        
        String dateString = name.substring(start, (start + 2)) + ":" + name.substring((start + 2), (start + 4));
        String gokigenRate = name.substring((start + 7), (start + 9));
        String gokigenNumber = name.substring(start + 12, prefix);

        //Log.v(Main.APP_IDENTIFIER, "gokigenNumber : " + gokigenNumber);        

        // �A�C�R����ID�����߂�
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
            	// �R���e���c�̒��g������Ă��Ĉꗗ�\��
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
     *  �ꗗ�\�������X�V����
     * 
     */
    private void updateDataListView()
    {
        //Log.v(Main.APP_IDENTIFIER, "MainListener::updateDataListView()");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

        // �p�����[�^����f�[�^��ǂݏo���I
        boolean simpleList = preferences.getBoolean("listPreviewContent", false);

        // �ꗗ��\���������f�B���N�g���̌���
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
                // List �� items ���\�[�g����I 
                java.util.Arrays.sort(dirList);

                // �t�@�C���ꗗ�����グ��
                for (String dirName : dirList)
                {
                    SymbolListArrayItem listItem = parseLocationFileName(dirName, simpleList);
                    if (listItem != null)
                    {
                        items.add(listItem);
                    }
                }
                
            }

            // ���X�g�A�_�v�^�[�𐶐����A�ݒ肷��
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

                    /** ���X�g���I�����ꂽ�Ƃ��̏���...�f�[�^���J��  **/
                    openDiaryData(item.getTextResource2nd());
                }
            });
            
            // ���X�g�𖖔��ɃX�N���[��������
            fileListView.setSelection(fileListView.getCount() - 1);
            
            String numberValue = "";
            if (totalNumber != 0)
            {
                numberValue = parent.getString(R.string.totalNumberTitle) + " " + totalNumber + preferences.getString("amountUnit", "");
            }

            // �l��ݒ肷��
            TextView numberView = (TextView) parent.findViewById(R.id.NumberArea);
            numberView.setText(numberValue);
            
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EX : " + ex.getMessage() + ", dir : " + dir);            
        }
        
    }
    
    /**
     *  ���͍ς݂̃f�[�^��\������
     * 
     * @param fileName
     */
    private void openDiaryData(String fileName)
    {
        // �J���t�@�C�����𐶐�����
        String dir = fileUtility.decideDateDirectory(showYear, showMonth, showDay);
        String name = dir + fileName;
        
        // �n���f�[�^������� Intent�Ƃ���
        Intent intent = new Intent(parent, jp.sourceforge.gokigen.diary.DiaryDataView.class);
        intent.putExtra(DiaryDataViewListener.DIARY_FILE, name);

        // �f�[�^�\���pActivity���N������
        parent.startActivityForResult(intent, R.id.messageListView);
    }
    
    
    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
    	if (launchOk == false)
    	{
    		// �p�X���[�h���b�N��
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
     *  �L�[������
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
    	if (launchOk == false)
    	{
    		// �p�X���[�h���b�N��
    		return (false);
    	}

    	int action = event.getAction();
        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {
        }        
        return (false);
    }
    

    /**
     *   ���j���[�ւ̃A�C�e���ǉ�
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
     *   ���j���[�\���O�̏���
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
     *   ���j���[�̃A�C�e�����I�����ꂽ�Ƃ��̏���
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if (launchOk == false)
    	{
    		// �p�X���[�h���b�N��
    		return (false);
    	}

        boolean result = false;
        switch (item.getItemId())
        {
          case MENU_ID_PREFERENCES:
            // �ݒ��ʂ�\������
            showPreference();
            result = true;
            break;

          case MENU_ID_ABOUT_GOKIGEN:
            // �A�v���̐�����\������
            showAboutGokigen();
            result = true;
            break;

          case MENU_ID_GOKIGEN_GRAPH:
        	// �O���t��ʂ�\������
        	showGokigenGraph();
        	result = true;
        	break;

          case MENU_ID_MOVE_TODAY:
            // �����̕\���ɍX�V����
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
     *  �_�C�A���O�\���̏��� (�������A1�񂾂��ݒ肷�鍀��)
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
     *  �_�C�A���O�\���̏��� (�J�����тɐݒ肷�鍀��)
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
            // �������Ȃ�...
    	}    	
    }

    /**
     *   �A�v���̏���\������
     * 
     */
    private void showAboutGokigen()
    {
        // �A�v���̏��(�N���W�b�g)��\������I
    	parent.showDialog(R.id.info_about_gokigen);
    }    

    /**
     *  �L�[���[�h������ʂ�\�����鏈��
     */
    private void showSearchScreen()
    {
        try
        {
            String dir = fileUtility.decideDateDirectory(showYear, showMonth, showDay);
            String dateString = "" + showYear + "/" + showMonth + "/" + showDay;

            // ������ʂ��Ăяo��
            Intent searchIntent = new Intent(parent,jp.sourceforge.gokigen.diary.DiarySearch.class);
            searchIntent.putExtra(DiarySearch.TARGET_DIR, dir);
            searchIntent.putExtra(DiarySearch.TARGET_LABEL, dateString);
            parent.startActivityForResult(searchIntent, R.id.searchForm);
        }
        catch (Exception e)
        {
             // ��O����...
            Log.v(Main.APP_IDENTIFIER, "showPreference() : " + e.getMessage());
        }    	
    }

    /**
     *  �ݒ��ʂ�\�����鏈��
     */
    private void showPreference()
    {
        try
        {
            // �ݒ��ʂ��Ăяo��
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.diary.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // ��O����...
            Log.v(Main.APP_IDENTIFIER, "showPreference() : " + e.getMessage());
        }
    }

    /**
     *  ���b�Z�[�W���͉�ʂ�\�����鏈��
     * @param buttonId  �\������g���K�ƂȂ����I�u�W�F�N�g��ID
     */
    private void showInputDiary(int buttonId)
    {
        try
        {
            // �ҏWActivity���Ăяo���Ă݂�
            Intent inputIntent = new Intent(parent, jp.sourceforge.gokigen.diary.DiaryInput.class);
            parent.startActivityForResult(inputIntent, buttonId);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        }
    }

    /**
     *  ��������O���t��ʂ�\������
     * @param buttonId
     */
    private void showGokigenGraph()
    {
        try
        {
        	Log.v(Main.APP_IDENTIFIER, "MainListener::showGokigenGraph()");
        	
        	// �O���t�`���������
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("graphYear",  showYear);
            editor.putInt("graphMonth", showMonth);
            editor.putInt("graphDay",   showDay);
            editor.commit();

            // �O���t�`��Activity���Ăяo���Ă݂�
            Intent graphIntent = new Intent(parent, jp.sourceforge.gokigen.diary.GokigenGraph.class);
            graphIntent.putExtra(GokigenGraph.TARGET_YEAR, showYear);
            graphIntent.putExtra(GokigenGraph.TARGET_MONTH, showMonth);
            graphIntent.putExtra(GokigenGraph.TARGET_DAY, showDay);
            parent.startActivityForResult(graphIntent, R.id.gokigenGraphView);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        }   	
    }
    
    
    /**
     *  �n�}��\������
     * 
     * @param buttonId
     */
    private void showLocationMap(int buttonId)
    {        
        try
        {
            // �I�t���C�����[�h���`�F�b�N���A�I�t���C�����ɂ̓}�b�v��ʂ��J���Ȃ��悤�ɂ���
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            boolean isOffline = preferences.getBoolean("offlineMode", false);
            if (isOffline == true)
            {
                Toast.makeText(parent, parent.getString(R.string.warn_offline), Toast.LENGTH_SHORT).show();
                return;
            }

            // Intent�ŕ\������
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
             // ��O����...�Ȃɂ����Ȃ��B
        }        
    }

    /**
     *  �N���p�X���[�h�̃`�F�b�N���s��
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

            	// �L���ς݃f�[�^�̈ꗗ����������
                prepareListView();
            }
            else
            {
                // Log.v(Main.APP_IDENTIFIER, "I: " + checkData + " T: " + passPhrase);
            	
            	// �p�X���[�h���}�b�`���Ȃ������ꍇ...
                Toast.makeText(parent, parent.getString(R.string.passphraseIsWrong), Toast.LENGTH_SHORT).show();
            
                // �I�����܂��B
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
     *  �p�X���[�h���͂��L�����Z�����ꂽ
     * 
     */
    public  void inputPassphraseCanceled()
    {
         // �����������Ȃ�
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
     *  �O���t�\������N�������N���A����
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
     *  ���݈ʒu���f�[�^�Ɋi�[����
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
            // �L�^�f�[�^�̏���
            locationTimeString = GeocoderWrapper.getDateTimeString(data.getTime());
            locationString = data.getLocationInfo();
            currentLatitude = (int) (data.getLatitude() * 1E6);
            currentLongitude = (int) (data.getLongitude() * 1E6);

            // �ʒu����Preference�ɏ�������
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
     *   �T�[�r�X�Ƃ���Activity�N���X�����т���I
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
     *  �T�[�r�X���瑗���Ă���Intent����M����I
     * @author MRSa
     *
     */
    private class LocationDataReceiver extends BroadcastReceiver implements IGeocoderResultReceiver
    {
        private GeocoderWrapper geocoder = null;
        private boolean        checkGeocoding = false;

    	/**
         *   �T�[�r�X����ʒm���󂯂��I
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

                // ���ݎ�����������������擾����
                dateData = GeocoderWrapper.getDateTimeString(data.getTime());

                // �W�I�R�[�f�B���O�����{����ƁA�d�g�����������ꍇ�A���͒��ɉ�ʂ����b�N�A�b�v����̂�h�~����
                // (UI�X���b�h�Ƃ͐؂藣���ē������A�Ə����Ă�����...)
                message = GeocoderWrapper.getLocationString(data.getLatitude(), data.getLongitude());

                // ���݂̈ʒu�����ꎞ�L�^���� (�W�I�R�[�f�B���O���{��Ɉʒu���L������)
                //message =  storeCurrentLocation(data);
                
                // �W�I�R�[�f�B���O(���݈ʒu�̏Z���擾)���w�� (�o�b�N�O���E���h�ŏ����B)
                if (checkGeocoding == false)
                {
                    checkGeocoding = true;

                    /** �W�I�R�[�_�[�̏���(AsyncTask��1�񂵂��g���Ȃ��炵���̂� **/
                    geocoder = null;
                    geocoder = new GeocoderWrapper(parent, this, new Locale(myLocale));
                    geocoder.execute(data);                	
                }

                //  ���ݎ����ƈʒu(�ܓx�E�o�x�̕�����)��\������...
                message = message + "\n" + "(" + dateData + ")";
                TextView printArea = (TextView) parent.findViewById(R.id.InformationArea);
                printArea.setText(message.toCharArray(), 0, message.length());
            }
            catch (Exception ex)
            {
                // ��O�����A���̎|��ʕ\�����s���B
                message = "Ex :" + ex.toString() + " (" + ex.getMessage() + "), MSG : " + message;
                Log.v(Main.APP_IDENTIFIER, message);

                TextView printArea = (TextView) parent.findViewById(R.id.InformationArea);
                printArea.setText(message.toCharArray(), 0, message.length());
            }            
        }

        /**
         *  �W�I�R�[�f�B���O�����������Ƃ��̏���
         *  (�ʒu�̕������\������)
         */
        public void  receivedResult(MyLocation location)
        {
            try
            {
                // �ʒu����Preference�ɋL�^����
            	storeCurrentLocation(location);
            	
            	//  ���ݎ����ƈʒu(�ܓx�E�o�x)��\������
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
            
            // �W�I�R�[�f�B���O���̃t���O����������
            checkGeocoding = false;
        }
    }
}
