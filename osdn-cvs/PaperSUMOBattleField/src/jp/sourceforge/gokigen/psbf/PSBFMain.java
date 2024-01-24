package jp.sourceforge.gokigen.psbf;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 *    �����o�L�b�g�̃��C������
 * 
 * @author MRSa
 *
 */
public class PSBFMain extends  PSBFBaseActivity
{
    public static final String APP_IDENTIFIER = "Gokigen";
    public static final String APP_BASEDIR = "/PSBF";

    private MainListener listener = null;  // �C�x���g�����N���X

    private OutputController mOutputController = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** ���X�i�N���X�̐��� **/
        listener = new MainListener(this);
        
        /** ���C�A�E�g�̔��f...����̓x�[�X�N���X�ł�� **/
       // setContentView(R.layout.main);

        /** ���X�i�N���X�̏��� **/       
        listener.prepareListener();        
    }

    /**
     * 
     * 
     */
    @Override
    protected void hideControls()
    {
        super.hideControls();
        mOutputController = null;
    }

    /**
     * 
     * 
     */
    protected void showControls()
    {
        super.showControls();

        mOutputController = new OutputController(this, mInputController);
        mOutputController.accessoryAttached();
        
        listener.prepareControlListeners(mInputController);
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
        boolean ret = listener.onOptionsItemSelected(item);
        if (ret == false)
        {
            ret = super.onOptionsItemSelected(item);
        }
        return (ret);
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
    public void onDestroy()
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
