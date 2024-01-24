package jp.sourceforge.gokigen.mr999ctl;

import jp.sourceforge.gokigen.mr999ctl.MainScreenListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.Menu;

/**
 *  MR-999CTL : MR-999 (���{�b�g�A�[��) ����A�v���i���C���N���X�j
 * @author MRSa
 *
 */
public class MR999mainctl extends Activity
{
    MainScreenListener listener = null;  // �C�x���g�����N���X
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        /**  �C�x���g����������N���X�𐶐�����   **/
        listener = new MainScreenListener((Activity) this, new PreferenceHolder(this));

        /** �S��ʕ\���ɂ��� **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /** �^�C�g�������� **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        /** ��ʕ\���̃��C�A�E�g�𐶐����� **/
        setContentView(R.layout.main);

        /** �C�x���g���X�i���������� */
        listener.prepareListener();
    
    }

    /**
     *  ���j���[�ɂ��āA����͉������Ȃ�
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return (false);
    }

    /**
     *   ���j���[�ɂ��āA����͉������Ȃ�
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	return (false);
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
