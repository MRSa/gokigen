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
 *  ���O�f�[�^���擾���āA�X�V���鏈���I
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
     *  ���O�f�[�^�̍X�V�����I
     * 
     * @param filterText
     */
    public void refreshLogData()
    {
         try
         {
        	 // ���������s����
        	 busyDialog.executeTask(this);
         }
         catch (Exception ex)
         {
        	 // �Ȃɂ����Ȃ�
         }
    }

    /**
     * �X�s�i�[�ɕ\�����郁�b�Z�[�W����������
     * 
     */
    public String getSpinnerMessage(Activity parent)
    {
    	return (parent.getString(R.string.busyMessage));
    }

    /**
     *  �������s�O�̏��� (�Ȃɂ������)
     *  
     */
	public void prepareTask(Activity parent)
	{
        tempCount++;
	}
	
	/**
	 *  ���Ԃ̂����鏈�������s����
	 *  
	 */
    public void executeTask()
    {
        try
        {
        	// Preferences����f�[�^���Ƃ��Ă���
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        	String ringbuffer = preferences.getString("useRingBuffer", "main");
        	String filterString = preferences.getString("filterText", "");
        	String filterRegEx = preferences.getString("filterRegEx", "");
        	String logFormat = preferences.getString("logFormat", "brief");
        	String filterSpec =  preferences.getString("filterSpec", "*:v");
        	if (filterSpec.length() < 3)
        	{
        		// �s���Ȑݒ�̂Ƃ��ɂ́A�f�t�H���g�̒l��ݒ肷��B
        		filterSpec = "*:v";
        	}

            // �A�C�e���p�[�T
            SymbolListArrayItem.ItemParser itemParser = decideItemParser(logFormat);

        	// logcat�R�}���h�̐���
        	ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");
            commandLine.add("-d");       //  -d:  dump the log and then exit (don't block)
            commandLine.add("-b");       //  -b <buffer> : request alternate ring buffer ('main' (default), 'radio', 'events')
            commandLine.add(ringbuffer); //     <buffer> option.
            commandLine.add("-v");       //  -v <format> :  Sets the log print format, where <format> is one of:
            commandLine.add(logFormat);  //                 brief process tag thread raw time threadtime long
            commandLine.add(filterSpec); //  �t�B���^�[�X�y�b�N

            // logcat�R�}���h�����s
            Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
            
            // �����f�[�^���擾����
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), BUFFER_SIZE);
            String line = null;

            // ���X�g�ɕ\������A�C�e���𐶐�����
            listItems = null;
            listItems = new ArrayList<SymbolListArrayItem>();
            do
            {
                 line = bufferedReader.readLine();

                 // �t�B���^�̃`�F�b�N
                 boolean isMatched = false;
                 try
                 {
                	 int filterLength = filterString.length();
                     int filterRegExLength = filterRegEx.length();
                     if ((filterLength == 0)&&(filterRegExLength == 0))
                     {
                    	 // �t�B���^���ݒ肳��Ă��Ȃ���΂n�j
                    	 isMatched = true;
                     }
                     else if ((filterLength > 0)&&(line.contains(filterString) == true))
                     {
                    	 // �����񂪎w�肳��Ă����I
                    	 isMatched = true;
                     }
                     else if ((filterRegExLength > 0)&&(line.matches(filterRegEx) == true))
                     {
                    	 // ���K�\���Ƀ}�b�`�����I
                    	 isMatched = true;
                     }                	 
                 }
                 catch (Exception ex)
                 {
                	 //
                 }
                 
                 // �w�肳��Ă���t�B���^�ɂ�������̂������o���A�\������
                 if (isMatched == true)
                 {
                	 SymbolListArrayItem listItem = new SymbolListArrayItem(itemParser.parseIconResource(line), itemParser.parseTextResource1st(line), itemParser.parseTextResource2nd(line), itemParser.parseTextResource3rd(line), itemParser.parseSubIconResource(line));
                     listItems.add(listItem);
                 }
            } while (line != null);

        } catch (Exception ex)
        {
            // ��O����...�ł��Ȃɂ����Ȃ�
        }    	
    }

    /**
     *  �����I�����ɉ�ʂ��X�V���鏈��
     */
    public void finishTask(Activity parent)
    {
    	try
    	{    		
    		// ���X�g�A�_�v�^�[�𐶐����A�ݒ肷��
            ListView listView = (ListView) parent.findViewById(R.id.messageListView);
            ListAdapter adapter = new SymbolListArrayAdapter(parent,  R.layout.listview, listItems);
            listView.setAdapter(adapter);

            // �A�C�e����I�������Ƃ��̏���
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //@Override
                public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
                {
                    ListView listView = (ListView) parentView;
                    SymbolListArrayItem item = (SymbolListArrayItem) listView.getItemAtPosition(position);

                    /// ���X�g���I�����ꂽ�Ƃ��̏���...�f�[�^���J��
                    showDetailData(item.getTextResource3rd());
                }
            });

            // ���ݎ����ƃ��O�������擾
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String data = dateFormat.format(calendar.getTime());
            data = data + "\nCount : " + listItems.size();

            // �l��ݒ肷��
            TextView numberView = (TextView) parent.findViewById(R.id.BottomInformationArea);
            numberView.setText(data);
    	}
    	catch (Exception ex)
    	{
    		// �������Ȃ��B
    	}
    }

    /**
     *    �ڍ׃f�[�^����������
     * 
     * @return
     */
    public String getDetailData()
    {
    	return (detailDataToShow);
    }
    
    /**
     *   �ڍ׃f�[�^��\������
     * 
     * @param detailData
     */
    private void showDetailData(String detailData)
    {
    	detailDataToShow = detailData;
    	parent.showDialog(R.id.detail_dialog);
    }
    
    /**
     *  �A�C�e������͂���p�[�T�[�����肷��
     * 
     * @param logFormat  ���O�t�H�[�}�b�g
     * @return  ��͗p�p�[�T
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
