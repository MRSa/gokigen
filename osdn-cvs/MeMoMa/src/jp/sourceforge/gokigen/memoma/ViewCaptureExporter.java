package jp.sourceforge.gokigen.memoma;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

/**
 *  �f�[�^���t�@�C���ɕۑ�����Ƃ��p �A�N�Z�X���b�p (�񓯊����������s)
 *  View�̏����摜�`���ipng�`���j�ŕۑ�����B
 *  �ǂ�View��ۑ�����̂��́AICaptureExporter.getCaptureTargetView()�N���X���g���ċ����Ă��炤�B
 *  
 *  AsyncTask
 *    String       : ���s���ɓn���N���X(Param)           : �t�@�C���������炤
 *    Integer    : �r���o�߂�`����N���X(Progress)   : ����͎g���Ă��Ȃ�
 *    String      : �������ʂ�`����N���X(Result)      : ���ʂ���������B
 *    
 * @author MRSa
 *
 */
public class ViewCaptureExporter extends AsyncTask<String, Integer, String>
{
	private ICaptureExporter receiver = null;
	private ExternalStorageFileUtility fileUtility = null;	
	private String exportedFileName = null;

	private ProgressDialog savingDialog = null;
	
	private Bitmap targetBitmap = null;

	/**
	 *   �R���X�g���N�^
	 */
    public ViewCaptureExporter(Context context, ExternalStorageFileUtility utility,  ICaptureExporter resultReceiver)
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
    	try
    	{
        	targetBitmap = null;
            if (receiver != null)
            {
            	// ��ʂ̃L���v�`�������{����
            	View targetView = receiver.getCaptureTargetView();
            	targetView.setDrawingCacheEnabled(false);
            	targetView.setDrawingCacheEnabled(true);
            	targetBitmap = Bitmap.createBitmap(targetView.getDrawingCache());
            	targetView.setDrawingCacheEnabled(false);
            }
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "ViewCaptureExporter::onPreExecute() : " + ex.toString());
    	}
    }
    
    /**
     *    �r�b�g�}�b�v�f�[�^��(PNG�`����)�ۊǂ���B
     * 
     * @param fileName
     * @param objectHolder
     * @return
     */
    private String exportToFile(String fileName)
    {
    	String resultMessage = "";
        try
        {
        	if (targetBitmap == null)
        	{
        		// �r�b�g�}�b�v�����Ȃ����߁A�����Ő܂�Ԃ��B
        		return ("SCREEN DATA GET FAILURE...");
        	}
        	
        	// �G�N�X�|�[�g����t�@�C���������肷��
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            exportedFileName = fileName + "_" + outFormat.format(calendar.getTime()) + ".png";

            // PNG�`���Ńt�@�C���o�͂��s���B
            OutputStream out = new FileOutputStream(exportedFileName);
            targetBitmap.compress(CompressFormat.PNG, 100, out);
            out.flush();
            out.close();            
        }
        catch (Exception e)
        {
        	resultMessage = " ERR(png)>" + e.toString();
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
    protected String doInBackground(String... datas)
    {
        // �t�@�C�����̐ݒ� ... (�g���q�Ȃ�)
    	String fileName = fileUtility.getGokigenDirectory() + "/exported/" + datas[0];

    	// �f�[�^��ۊǂ���
        String result = exportToFile(fileName);

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
            	receiver.onCaptureExportedResult(exportedFileName, result);
            }
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "ViewCaptureExporter::onPostExecute() : " + ex.toString());
    	}
    	// �v���O���X�_�C�A���O������
    	if (savingDialog != null)
    	{
            savingDialog.dismiss();
    	}
    	return;
    }     
 
    /**
     *    ���ʕ񍐗p�̃C���^�t�F�[�X
     *    
     * @author MRSa
     *
     */
    public interface ICaptureExporter
    {
    	/** �f�[�^���L���v�`������ View���擾���� **/
    	public abstract View getCaptureTargetView();
    	
        /**  �ۑ����ʂ̕� **/
        public abstract void onCaptureExportedResult(String exportedFileName, String detail);
    }
}
