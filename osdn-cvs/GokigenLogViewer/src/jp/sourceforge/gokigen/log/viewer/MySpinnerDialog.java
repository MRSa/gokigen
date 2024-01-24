package jp.sourceforge.gokigen.log.viewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

/**
 *  ������Ə����Ɏ��Ԃ�������ꍇ�Ɏg���A�X�s�i�[�_�C�A���O�̕\���N���X
 *  IExectionTask �ŏ�����n���Ă��炤
 * 
 * @author MRSa
 *
 */
public class MySpinnerDialog
{
    Activity  parent = null;
    ProgressDialog loadingDialog = null;
    IExectionTask targetTask = null;
    
    /**
     *  �R���X�g���N�^
     * 
     * @param context
     */
	public MySpinnerDialog(Activity context)
	{
        loadingDialog = new ProgressDialog(context);
        parent = context;		
	}

	/**
	 *  ���Ԃ̂����鏈�������s����
	 * 
	 */
	public void executeTask(IExectionTask target)
    {

        //  �v���O���X�_�C�A���O�i�u���[�h��...�v�j��\������B
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(target.getSpinnerMessage(parent));
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        // ���s����N���X�Ɖ�ʂ��L������
        targetTask = null;
        targetTask = target;
        
        try
        {
            // �O�����̎��s
        	targetTask.prepareTask(parent);
        }
        catch (Exception ex)
        {
            // �������Ȃ�...
        }
        
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
            		// ���Ԃ̂�����{���������s����
            		targetTask.executeTask();
            	}
                catch (Exception ex)
            	{
                    // �Ȃɂ����Ȃ�
            	}
        		handler.sendEmptyMessage(0);
           }

            /**
             *   ��ʂ̍X�V
             */
            private final Handler handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                	targetTask.finishTask(parent);
                	loadingDialog.dismiss();

                    // �����͏I��...�N���A����
                	targetTask = null;
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
	 *  ���̃N���X�𗘗p���邽�߂̃C���^�t�F�[�X
	 * 
	 * @author MRSa
	 *
	 */
	public interface IExectionTask
	{
	    // �X�s�i�[�ɕ\�����郁�b�Z�[�W�̎擾
	    public abstract String getSpinnerMessage(Activity parent);

	    // �������s�O�̏���
		public abstract void prepareTask(Activity parent);
		
		// ���������s �i���\���Ԃ������鏈���A�����͕ʃX���b�h�Ŏ��s�j
	    public abstract void executeTask();

	    // �����I�����ɉ�ʂ��X�V���鏈��
	    public abstract void finishTask(Activity parent);
	}
}
