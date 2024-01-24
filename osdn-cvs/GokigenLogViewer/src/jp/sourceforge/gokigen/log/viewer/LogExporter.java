package jp.sourceforge.gokigen.log.viewer;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 *  ���O�f�[�^���t�@�C���ɏo�͂��鏈���I
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
	 *   �R���X�g���N�^
	 * 
	 * @param arg
	 */
    public LogExporter(Activity arg)
    {
        busyDialog = new MySpinnerDialog(arg);
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);
    }

    /**
     *  ���O�f�[�^�̃t�@�C���o�͏����I
     * 
     * @param filterName  �t�@�C���� (null�Ȃ玩���Ő���)
     */
    public void exportLogData(String fileName)
    {
    	outputLogFileName = fileName;
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
    	return (parent.getString(R.string.busyOutputLogMessage));
    }

    /**
     *  �������s�O�̏��� (�Ȃɂ������)
     *  
     */
	public void prepareTask(Activity parent)
	{
		// ���X�g�A�_�v�^�[�𐶐����A�ݒ肷��
        ListView listView = (ListView) parent.findViewById(R.id.messageListView);
        adapter = listView.getAdapter();
	}
    
	/**
	 *  ���Ԃ̂����鏈�������s����
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
     *  �����I�����ɉ�ʂ��X�V���鏈��
     */
    public void finishTask(Activity parent)
    {
    	try
    	{
    		// �������Ȃ��B
   	    }
    	catch (Exception ex)
    	{
    		// �������Ȃ��B
    	}
    }

    
    /**
     *  �t�@�C�����𐶐�����...
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
