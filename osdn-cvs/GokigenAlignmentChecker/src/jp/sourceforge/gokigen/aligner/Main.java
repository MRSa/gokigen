package jp.sourceforge.gokigen.aligner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity
{
    public static final String APP_IDENTIFIER = "Gokigen";
    public static final String APP_BASEDIR = "/Shisei";
    public static final String APP_EXAMINE_FILENAME = "fileNameToCheck";

    private MainListener   listener = null;     // �C�x���g�����N���X
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** ���X�i�N���X�𐶐� **/
        listener = new MainListener((Activity) this);

        /** �S��ʕ\���ɂ��� **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /** �^�C�g�������� **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /** ��ʂ̏��� **/
        setContentView(R.layout.main);

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


    /**
     *  �L�[�������ꂽ�Ƃ��̏���
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        try
        {
        	// �L�[�������ꂽ���Ƃ�ʒm����
        	if (listener.onKeyDown(keyCode, event) == true)
        	{
        		return (true);
        	}
        }
        catch (Exception ex)
        {
            // ��O�����������Ƃ��ɂ́A�������Ȃ��B
        }
        return (super.onKeyDown(keyCode, event));
    }

    /**
     *  ��ʂ��^�b�`���ꂽ�Ƃ��̏���
     * 
     */
    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        try
        {
        	// �G��ꂽ���Ƃ�ʒm����
        	if (listener.onTouchEvent(event) == true)
        	{
        		return (true);
        	}
        }
        catch (Exception ex)
        {
            // ��O�����������Ƃ��ɂ́A�������Ȃ��B
        }
    	return (super.onTouchEvent(event));
    }

    /**
     *  ��ʂ��^�b�`���ꂽ�Ƃ��̏���
     * 
     */
    @Override
    public boolean onTrackballEvent (MotionEvent event)
    {
    	//Log.v(Main.APP_IDENTIFIER, "Main::onTrackballEvent()");
        try
        {
        	// �G��ꂽ���Ƃ�ʒm����
        	if (listener.onTrackballEvent(event) == true)
        	{
        		return (true);
        	}
        }
        catch (Exception ex)
        {
            // ��O�����������Ƃ��ɂ́A�������Ȃ��B
        }
    	return (super.onTouchEvent(event));
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

}
