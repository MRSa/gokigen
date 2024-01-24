package jp.sourceforge.gokigen.memoma;

import java.io.BufferedReader;
import java.io.FileReader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 *  �f�[�^���t�@�C���ɕۑ�����Ƃ��p �A�N�Z�X���b�p (�񓯊����������s)
 *  
 *  AsyncTask
 *    MeMoMaObjectHolder : ���s���ɓn���N���X(Param)
 *    Integer    : �r���o�߂�`����N���X(Progress)
 *    String     : �������ʂ�`����N���X(Result)
 *    
 * @author MRSa
 *
 */
public class MeMoMaFileImportCsvProcess extends AsyncTask<MeMoMaObjectHolder, Integer, String> implements MeMoMaFileSavingProcess.ISavingStatusHolder, MeMoMaFileSavingProcess.IResultReceiver
{	
	private Context parent = null;
	private IResultReceiver receiver = null;
	private ExternalStorageFileUtility fileUtility = null;	
	private String targetFileName = null;
    private String fileSavedResult = "";
	private ProgressDialog importingDialog = null;

	private String backgroundUri = null;
	private String userCheckboxString = null;
	
	/**
	 *   �R���X�g���N�^
	 */
    public MeMoMaFileImportCsvProcess(Context context, ExternalStorageFileUtility utility,  IResultReceiver resultReceiver, String fileName)
    {
    	parent = context;
    	receiver = resultReceiver;
    	fileUtility = utility;
    	targetFileName = fileName;

        //  �v���O���X�_�C�A���O�i�u�f�[�^�C���|�[�g��...�v�j��\������B
    	importingDialog = new ProgressDialog(context);
    	importingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	importingDialog.setMessage(context.getString(R.string.dataImporting));
    	importingDialog.setIndeterminate(true);
    	importingDialog.setCancelable(false);
    	importingDialog.show();

    	//  �ݒ�ǂݏo���p...���炩���߁AUI�X���b�h�œǂ݂����Ă����B   	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
    	backgroundUri = preferences.getString("backgroundUri","");
    	userCheckboxString = preferences.getString("userCheckboxString","");
    }
	
    /**
     *  �񓯊��������{�O�̑O����
     * 
     */
    @Override
    protected void onPreExecute()
    {
    }

    /**
     *    �P���R�[�h���̃f�[�^��ǂݍ��ށB 
     * 
     * @param buf
     * @return
     */
    private String readRecord(BufferedReader buf )
    {
    	String oneRecord = null;
    	try
    	{
    		String oneLine = buf.readLine();
            while (oneLine != null)
            {
            	oneRecord = (oneRecord == null) ? oneLine : oneRecord + oneLine;
            	if (oneRecord.indexOf(",;!<_$") > 0)
            	{
            		// ���R�[�h���������������̂� break ����B
            		break;
            	}
            	// ���̍s��ǂ݂����B
            	oneLine = buf.readLine();
            }
    	}
    	catch (Exception ex)
    	{
            //
    		Log.v(Main.APP_IDENTIFIER, "CSV:readRecord() ex : " + ex.toString());
    		oneRecord = null;
    	}
    	return (oneRecord);
    }

    /**
     *   1���R�[�h���̃f�[�^����؂�
     * 
     * 
     * @param dataLine
     */
    private void parseRecord(String dataLine,  MeMoMaObjectHolder objectHolder)
    {
        int detailIndex = 0;
        int userCheckIndexTrue = 0;
        int userCheckIndexFalse = 0;
        int nextIndex = 0;
        String label = "";
        String detail = "";
        boolean userChecked = false;
        try
        {
            detailIndex = dataLine.indexOf("\",\"");
            if (detailIndex < 0)
            {
                Log.v(Main.APP_IDENTIFIER, "parseRecord() : label wrong : " + dataLine);
            	return;
            }
            label = dataLine.substring(1, detailIndex);
            userCheckIndexTrue = dataLine.indexOf("\",True,", detailIndex);
            userCheckIndexFalse = dataLine.indexOf("\",False,", detailIndex);
            if (userCheckIndexFalse > detailIndex)
            {
                //
                detail = dataLine.substring(detailIndex + 3, userCheckIndexFalse);
            	userChecked = false;
            	nextIndex = userCheckIndexFalse + 8; // 8�́A ",False, �𑫂�����
            }
            else if (userCheckIndexTrue > detailIndex)
            {
                //
                detail = dataLine.substring(detailIndex + 3, userCheckIndexTrue);
            	userChecked = true;
            	nextIndex = userCheckIndexTrue + 7; // 7�́A ",True,  �𑫂�����
            }
            else // if ((userCheckIndexTrue <= detailIndex)&&(userCheckIndexFalse <= detailIndex))
            {
                Log.v(Main.APP_IDENTIFIER, "parseRecord() : detail wrong : " + dataLine);
            	return;            	
            }
            
            //  �c��̃f�[�^��؂�o���B
            String[] datas = (dataLine.substring(nextIndex)).split(",");
            if (datas.length < 6)
            {
            	Log.v(Main.APP_IDENTIFIER, "parseRecord() : data size wrong : " + datas.length);
            	return;
            }
            int drawStyle = Integer.parseInt(datas[0]);
            String paintStyle = datas[1];
            float centerX = Float.parseFloat(datas[2]);
            float centerY = Float.parseFloat(datas[3]);
            float width = Float.parseFloat(datas[4]);
            float height = Float.parseFloat(datas[5]);

            float left = centerX - (width / 2.0f);
            float top = centerY - (height / 2.0f);

            // �I�u�W�F�N�g�̃f�[�^���쐬����
            MeMoMaObjectHolder.PositionObject pos = objectHolder.createPosition(left, top, drawStyle);
            if (pos == null)
            {
                Log.v(Main.APP_IDENTIFIER, "parseRecord() : object create failure.");
            	return;            	
            }
            pos.rect.right = left + width;
            pos.rect.bottom = top + height;
            pos.label = label;
            pos.detail = detail;
            pos.paintStyle = paintStyle;
            pos.userChecked = userChecked;
            Log.v(Main.APP_IDENTIFIER, "OBJECT CREATED: " + label + "(" + left + "," + top + ") [" +drawStyle + "]");
        }
        catch (Exception ex)
        {
        	Log.v(Main.APP_IDENTIFIER, "parseRecord() " + ex.toString());
        }
    	
    }

    
    /**
     *    (CSV�`����)�f�[�^��ǂݍ���Ŋi�[����B
     * 
     * @param fileName
     * @param objectHolder
     * @return
     */
    private String importFromCsvFile(String fileName, MeMoMaObjectHolder objectHolder)
    {
    	String resultMessage = "";
        try
        {
            Log.v(Main.APP_IDENTIFIER, "CSV(import)>> " + fileName);        		
        	BufferedReader buf = new BufferedReader(new FileReader(fileName));
            String dataLine = readRecord(buf);
            while (dataLine != null)
            {
        		if (dataLine.startsWith(";") != true)
        		{
        			// �f�[�^�s�������B���O�ɏo�͂���I
                    parseRecord(dataLine, objectHolder);
        		}
                // ���̃f�[�^�s��ǂݏo��
        		dataLine = readRecord(buf);
            }
        }
        catch (Exception e)
        {
        	resultMessage = " ERR(import)>" + e.toString();
            Log.v(Main.APP_IDENTIFIER, resultMessage);
            e.printStackTrace();
        } 
        return (resultMessage);
    }

    /**
     *  �񓯊�����
     *  �i�o�b�N�O���E���h�Ŏ��s����(���̃��\�b�h�́AUI�X���b�h�ƕʂ̂Ƃ���Ŏ��s����)�j
     * 
     */
    @Override
    protected String doInBackground(MeMoMaObjectHolder... datas)
    {    	
        // �t�@�C�����̐ݒ� ... (�g���q�Ȃ�)
    	String fileName = fileUtility.getGokigenDirectory() + "/exported/" + targetFileName;

    	// �f�[�^��ǂݍ���
        String result = importFromCsvFile(fileName, datas[0]);

        // �f�[�^��ۑ�����
    	MeMoMaFileSavingEngine savingEngine = new MeMoMaFileSavingEngine(fileUtility, backgroundUri, userCheckboxString);
    	String message = savingEngine.saveObjects(datas[0]);

        System.gc();

		return (result + " " + message);
    }

    /**
     *  �񓯊������̐i���󋵂̍X�V
     * 
     */
	@Override
	protected void onProgressUpdate(Integer... values)
	{
        // ����͉������Ȃ�
	}

    /**
     *  �񓯊������̌㏈��
     *  (���ʂ���������)
     */
    @Override
    protected void onPostExecute(String result)
    {
    	try
    	{
            if (receiver != null)
            {
            	receiver.onImportedResult(result + "  " + fileSavedResult);
            }
            fileSavedResult = "";
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "MeMoMaFileImportCsvProcess::onPostExecute() : " + ex.toString());
    	}
    	// �v���O���X�_�C�A���O������
    	importingDialog.dismiss();

    	return;
    }
    
    public void onSavedResult(String detail)
    {
        fileSavedResult = detail;
    }

    public void setSavingStatus(boolean isSaving)
    {
    	
    }

    public boolean getSavingStatus()
    {
        return (false);
    }

    /**
     *    ���ʕ񍐗p�̃C���^�t�F�[�X�i�ϋɓI�Ɏg���\��͂Ȃ�����...�j
     *    
     * @author MRSa
     *
     */
    public interface IResultReceiver
    {
        /**  �ۑ����ʂ̕� **/
        public abstract void onImportedResult(String detail);
    }
}
