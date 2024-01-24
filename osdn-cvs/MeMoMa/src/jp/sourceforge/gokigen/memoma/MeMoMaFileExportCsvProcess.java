package jp.sourceforge.gokigen.memoma;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
public class MeMoMaFileExportCsvProcess extends AsyncTask<MeMoMaObjectHolder, Integer, String>
{	
	private IResultReceiver receiver = null;
	private ExternalStorageFileUtility fileUtility = null;	
	private String exportedFileName = null;

	ProgressDialog savingDialog = null;
	
	/**
	 *   �R���X�g���N�^
	 */
    public MeMoMaFileExportCsvProcess(Context context, ExternalStorageFileUtility utility,  IResultReceiver resultReceiver)
    {
    	receiver = resultReceiver;
    	fileUtility = utility;

        //  �v���O���X�_�C�A���O�i�u�ۑ���...�v�j��\������B
    	savingDialog = new ProgressDialog(context);
    	savingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	savingDialog.setMessage(context.getString(R.string.dataSaving));
    	savingDialog.setIndeterminate(true);
    	savingDialog.setCancelable(false);
    	savingDialog.show();
    	
    	/** �t�@�C�����o�b�N�A�b�v����f�B���N�g�����쐬���� **/
    	File dir = new File(fileUtility.getGokigenDirectory() + "/exported");
    	dir.mkdir();

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
     *    �f�[�^��(CSV�`����)�ۊǂ���B
     * 
     * @param fileName
     * @param objectHolder
     * @return
     */
    private String exportToCsvFile(String fileName, MeMoMaObjectHolder objectHolder)
    {
    	String resultMessage = "";
        try
        {
        	// �G�N�X�|�[�g����t�@�C���������肷��
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            exportedFileName = fileName + "_" + outFormat.format(calendar.getTime()) + ".csv";
        	FileWriter writer = new FileWriter(new File(exportedFileName));    	
            
        	//  �f�[�^�̃^�C�g�����o��
        	String str = "";
        	str = "; label,detail,userChecked,shape,style,centerX,centerY,width,height,;!<_$ (';!<_$' is a record Separator)\r\n";
            writer.write(str);
        	
        	// �I�u�W�F�N�g�̏o�� �i�ێ����Ă�����̂����ׂĕ\������j
        	Enumeration<Integer> keys = objectHolder.getObjectKeys();
            while (keys.hasMoreElements())
            {
                Integer key = keys.nextElement();
                MeMoMaObjectHolder.PositionObject pos = objectHolder.getPosition(key);

                // TODO:  �i�荞�ݏ���������ꍇ�ɂ́A���̏����ɏ]���Ă��ڂ荞�ޕK�v����B

                str = "";
                str = str + "\"" + pos.label + "\"";
                str = str + ",\"" + pos.detail + "\"";
                if (pos.userChecked == true)
                {
                	str = str + ",True";
                }
                else
                {
                	str = str + ",False";
                }
                str = str + "," + pos.drawStyle;   // �I�u�W�F�N�g�̌`��
                str = str + "," + pos.paintStyle;   // �I�u�W�F�N�g�̓h��Ԃ����
                str = str + "," + (Math.round(pos.rect.centerX() * 100.0f) / 100.0f);
                str = str + "," + (Math.round(pos.rect.centerY() * 100.0f) / 100.0f);
                str = str + "," + (Math.round(pos.rect.width() * 100.0f) / 100.0f);
                str = str + "," + (Math.round(pos.rect.height() * 100.0f) / 100.0f);
                str = str + ",;!<_$\r\n";
                writer.write(str);
            }
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
        	resultMessage = " ERR>" + e.toString();
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
    	String fileName = fileUtility.getGokigenDirectory() + "/exported/" + datas[0].getDataTitle();

    	// �f�[�^��ۊǂ���
        String result = exportToCsvFile(fileName, datas[0]);

        System.gc();

		return (result);
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
            	receiver.onExportedResult(exportedFileName, result);
            }
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "MeMoMaFileExportCsvProcess::onPostExecute() : " + ex.toString());
    	}
    	// �v���O���X�_�C�A���O������
    	savingDialog.dismiss();

    	return;
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
        public abstract void onExportedResult(String exportedFileName, String detail);
    }
}
