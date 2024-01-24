package jp.sourceforge.gokigen.memoma;


import java.util.Enumeration;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 *  �I�u�W�F�N�g�̈ʒu�𐮗񂷂�N���X (�񓯊����������s)
 *  
 *  AsyncTask
 *    MeMoMaObjectHolder : ���s���ɓn���N���X(Param)
 *    Integer    : �r���o�߂�`����N���X(Progress)
 *    String     : �������ʂ�`����N���X(Result)
 *    
 * @author MRSa
 *
 */
public class ObjectAligner extends AsyncTask<MeMoMaObjectHolder, Integer, String>
{
	ProgressDialog executingDialog = null;
	IAlignCallback  receiver = null;
	/**
	 *   �R���X�g���N�^
	 */
    public ObjectAligner(Context context, IAlignCallback client)
    {
        receiver = client;
    	
    	//  �v���O���X�_�C�A���O�i�u�ۑ���...�v�j��\������B
    	executingDialog = new ProgressDialog(context);
    	executingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	executingDialog.setMessage(context.getString(R.string.dataAligning));
    	executingDialog.setIndeterminate(true);
    	executingDialog.setCancelable(false);
    	executingDialog.show();
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
     *  �񓯊�����
     *  �i�o�b�N�O���E���h�Ŏ��s����(���̃��\�b�h�́AUI�X���b�h�ƕʂ̂Ƃ���Ŏ��s����)�j
     * 
     */
    @Override
    protected String doInBackground(MeMoMaObjectHolder... datas)
    {
    	MeMoMaObjectHolder objectHolder = datas[0];

    	// �I�u�W�F�N�g�̏o�� �i�ێ����Ă�����̂͂��ׂĕ\������j
    	Enumeration<Integer> keys = objectHolder.getObjectKeys();
        while (keys.hasMoreElements())
        {
            Integer key = keys.nextElement();
            MeMoMaObjectHolder.PositionObject pos = objectHolder.getPosition(key);
            
            float newLeft = (float) Math.floor((pos.rect.left + 15.0f)/ 30.0) * 30.0f;
            float newTop = (float) Math.floor((pos.rect.top + 15.0f)/ 30.0) * 30.0f;
            pos.rect.offsetTo(newLeft, newTop);
        }
        System.gc();
		
		return ("");
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
    			// ���וς������Ƃ�ʒm����
    			receiver.objectAligned();
    		}
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "ObjectAligner::onPostExecute() : " + ex.toString());
    	}

    	// �v���O���X�_�C�A���O������
    	executingDialog.dismiss();
        return;
    }     
    
    /**
     *    ���וς������Ƃ�ʒm����
     * 
     * @author MRSa
     *
     */
    public interface IAlignCallback
    {
        public abstract void objectAligned();
    }
}
