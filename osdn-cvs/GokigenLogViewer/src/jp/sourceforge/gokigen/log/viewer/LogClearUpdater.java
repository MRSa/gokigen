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
     *   �R���X�g���N�^
     * 
     * @param arg
     */
	public LogClearUpdater(Activity arg)
    {
        busyDialog = new MySpinnerDialog(arg);
    }

    /**
     *  ���O�f�[�^�̍X�V�����I
     * 
     * @param filterText
     */
    public void clearLogData()
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
        	// logcat�R�}���h�̐���
        	ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");
            commandLine.add("-c");       //  -c: Clears (flushes) the entire log and exits.

            // logcat�R�}���h�����s
            Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));

            // ���X�g�ɕ\������A�C�e���𐶐�����
            listItems = null;
            listItems = new ArrayList<SymbolListArrayItem>();

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
     *   �ڍ׃f�[�^��\������
     * 
     * @param detailData
     */
    private void showDetailData(String detailData)
    {
    	
    }
}
