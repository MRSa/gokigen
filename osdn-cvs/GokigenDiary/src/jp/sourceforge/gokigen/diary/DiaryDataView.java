package jp.sourceforge.gokigen.diary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 *  �f�[�^�r���[�N���X�I
 * 
 * @author MRSa
 *
 */
public class DiaryDataView extends Activity
{

    private DiaryDataViewListener listener = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** ���X�i�N���X�𐶐� **/
        listener = new DiaryDataViewListener((Activity) this);

        ///** �S��ʕ\���ɂ��� **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** �^�C�g�������� **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /** ��ʂ̏��� **/
        setContentView(R.layout.diarydataview);

        /** ���X�i�N���X�̏��� **/
        listener.prepareListener();

        /** �N���X�̏������� **/
        listener.prepareOther();
    }
    
    /**
     *  ���j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        listener.onCreateOptionsMenu(menu);
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	listener.onPrepareOptionsMenu(menu);
        return (super.onPrepareOptionsMenu(menu));
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
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
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
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }
    }

    /**
     *  �A�v���I�����̏���
     * 
     */
    @Override
    protected void onDestroy()
    {
//          Log.v(Main.APP_IDENTIFIER, "DiaryDataView::onDestroy()");
    	listener.finishListener();
        super.onDestroy();
    }

    /**
     * 
     */
    @Override
    protected void onStart()
    {
//        Log.v(Main.APP_IDENTIFIER, "DiaryDataView::onStart()");
        super.onStart();
    }

    /**
     * 
     */
    @Override
    protected void onStop()
    {
//        Log.v(Main.APP_IDENTIFIER, "DiaryDataView::onStop()");
        super.onStop();
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
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
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
}
