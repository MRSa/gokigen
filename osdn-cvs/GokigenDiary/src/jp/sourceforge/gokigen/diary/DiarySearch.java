package jp.sourceforge.gokigen.diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 *  �_�C�A���������
 * 
 * @author MRSa
 *
 */
public class DiarySearch extends Activity
{
	static public final String TARGET_DIR   = "jp.sourceforge.gokigen.diary.SearchDir";
	static public final String TARGET_LABEL = "jp.sourceforge.gokigen.diary.SearchLabel";

	DiarySearchListener listener = null;  // �C�x���g�����N���X

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        listener = new DiarySearchListener((Activity) this);

        ///** �S��ʕ\���ɂ��� **/
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** �^�C�g�������� **/       
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        /** ��ʂ̏��� **/
        setContentView(R.layout.diarysearch);

        /** �C�x���g���X�i���������� **/
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
