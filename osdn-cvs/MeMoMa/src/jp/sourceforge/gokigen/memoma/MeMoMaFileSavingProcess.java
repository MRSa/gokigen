package jp.sourceforge.gokigen.memoma;

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
public class MeMoMaFileSavingProcess extends AsyncTask<MeMoMaObjectHolder, Integer, String>
{	
	private IResultReceiver receiver = null;
	private ExternalStorageFileUtility fileUtility = null;
	private ISavingStatusHolder statusHolder = null;
	
	private String backgroundUri = null;
	private String userCheckboxString = null;
	private ProgressDialog savingDialog = null;
	
	/**
	 *   �R���X�g���N�^
	 */
    public MeMoMaFileSavingProcess(Context context, ISavingStatusHolder holder, ExternalStorageFileUtility utility,  IResultReceiver resultReceiver)
    {
    	receiver = resultReceiver;
    	fileUtility = utility;
    	statusHolder = holder;

        //  �v���O���X�_�C�A���O�i�u�ۑ���...�v�j��\������B
    	savingDialog = new ProgressDialog(context);
    	savingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	savingDialog.setMessage(context.getString(R.string.dataSaving));
    	savingDialog.setIndeterminate(true);
    	savingDialog.setCancelable(false);
    	savingDialog.show();

    	//  �ݒ�ǂݏo���p...���炩���߁AUI�X���b�h�œǂ݂����Ă����B
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    	backgroundUri = preferences.getString("backgroundUri","");
    	userCheckboxString = preferences.getString("userCheckboxString","");
    	    	
    	// ���ۊǏ�ԂɃ��Z�b�g����
    	statusHolder.setSavingStatus(false);
    }
	
    /**
     *  �񓯊��������{�O�̑O����
     * 
     */
    @Override
    protected void onPreExecute()
    {
    	// ���ۊǏ�ԂɃ��Z�b�g����
    	statusHolder.setSavingStatus(false);
    }

    /**
     *  �񓯊�����
     *  �i�o�b�N�O���E���h�Ŏ��s����(���̃��\�b�h�́AUI�X���b�h�ƕʂ̂Ƃ���Ŏ��s����)�j
     * 
     */
    @Override
    protected String doInBackground(MeMoMaObjectHolder... datas)
    {
    	// �ۊǒ���Ԃ�ݒ肷��
    	statusHolder.setSavingStatus(true);

    	// �f�[�^�̕ۊǃ��C��
    	MeMoMaFileSavingEngine savingEngine = new MeMoMaFileSavingEngine(fileUtility, backgroundUri, userCheckboxString);
    	String result = savingEngine.saveObjects(datas[0]);

        System.gc();
		
    	// ���ۊǏ�ԂɃ��Z�b�g����
    	statusHolder.setSavingStatus(false);

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
            	receiver.onSavedResult(result);
            }
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "MeMoMaFileSavingProcess::onPostExecute() : " + ex.toString());
    	}
    	// �v���O���X�_�C�A���O������
    	savingDialog.dismiss();

    	// ���ۊǏ�ԂɃZ�b�g����
    	statusHolder.setSavingStatus(false);
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
        public abstract void onSavedResult(String detail);
    }

    /**
     *     �t�@�C���ۑ����{��Ԃ��L������C���^�t�F�[�X�N���X
     *     
     * @author MRSa
     *
     */
    public interface ISavingStatusHolder
    {
    	/**  �ۑ�����Ԃ�ݒ肷�� **/
        public abstract void setSavingStatus(boolean isSaving);
        
        /** �ۑ�����Ԃ��擾���� **/
        public abstract boolean getSavingStatus();
    }

}
