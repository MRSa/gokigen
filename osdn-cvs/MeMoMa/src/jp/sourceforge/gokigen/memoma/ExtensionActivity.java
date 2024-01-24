package jp.sourceforge.gokigen.memoma;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 
 * @author MRSa
 *
 */
public class ExtensionActivity extends  Activity
{
	/** �N���R�[�h **/
    public static final String MEMOMA_EXTENSION_LAUNCH_ACTIVITY = "jp.sfjp.gokigen.memoma.extension.activity";

    /** �f�[�^���ʎq(�\�����f�[�^�̕ۑ��t�@�C���ւ̃t���p�X) **/
    public static final String MEMOMA_EXTENSION_DATA_FULLPATH = "jp.sfjp.gokigen.memoma.extension.data.fullpath";
    public static final String MEMOMA_EXTENSION_DATA_TITLE = "jp.sfjp.gokigen.memoma.extension.data.title";

    private ExtensionActivityListener listener = null;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
          super.onCreate(savedInstanceState);

          /** ���X�i�N���X�𐶐����� **/
          listener = new ExtensionActivityListener((Activity) this);

          /** ���C�A�E�g��ݒ肷�� **/
          setContentView(R.layout.extensionview);

          /** ���X�i�N���X�̏��� **/
          listener.prepareExtraDatas(getIntent());
          listener.prepareListener(); 
     }

    /**
     *  ���j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	menu = listener.onCreateOptionsMenu(menu);
    	return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *  ���j���[�A�C�e���̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return (listener.onOptionsItemSelected(item));
    }
    
    /**
     *  ���j���[�\���O�̏���
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	listener.onPrepareOptionsMenu(menu);
    	return (super.onPrepareOptionsMenu(menu));
    }

    /**
     *  ��ʂ����ɉ�����Ƃ��̏���
     */
    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            // ������~�߂�悤�C�x���g�����N���X�Ɏw������
        	listener.shutdown();        	
        }
        catch (Exception ex)
        {
        	// �������Ȃ�
        }
    }
    
    /**
     *  ��ʂ��\�ɏo�Ă����Ƃ��̏���
     */
    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
        	// ���쏀������悤�C�x���g�����N���X�Ɏw������
        	listener.prepareToStart();
        }
        catch (Exception ex)
        {
            // �Ȃɂ����Ȃ�
        }
    }

    /**
     *   �I�����̏���
     * 
     */
    @Override
    protected void onDestroy()
    {
        listener.finishListener();
        super.onDestroy();
    }

    /**
     * 
     */
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    /**
     * 
     */
    @Override
    protected void onStop()
    {
        super.onStop();
    }

    /**
     * 
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
            super.onSaveInstanceState(outState);
    }

    /**
     * 
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	return (listener.onCreateDialog(id));
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
    	listener.onPrepareDialog(id, dialog);
    	return;
    }
    
    /**
     *  �q��ʂ��牞������������Ƃ��̏���
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            // �q��ʂ������������̉����������C�x���g�����N���X�Ɉ˗�����
        	listener.onActivityResult(requestCode, resultCode, data);
        }
        catch (Exception ex)
        {
            // ��O�����������Ƃ��ɂ́A�������Ȃ��B
        }
    }    
}
