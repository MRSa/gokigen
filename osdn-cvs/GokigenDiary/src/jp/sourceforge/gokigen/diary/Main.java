package jp.sourceforge.gokigen.diary;

import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 *  
 * @author MRSa
 *
 */
public class Main extends MapActivity
{
    private MainListener   listener = null;     // �C�x���g�����N���X

    public static final String APP_IDENTIFIER = "Gokigen";
    public static final String APP_BASEDIR = "/Diary";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** ���X�i�N���X�𐶐� **/
        listener = new MainListener((Activity) this);

        /** ��ʂ̏��� **/
        setContentView(R.layout.main);

        /** ���X�i�N���X�̏��� **/       
        listener.prepareListener();        
    }

    /**
     *  �o�H�\�����邩�H
     */
    @Override
    protected boolean isRouteDisplayed()
    {
       return (false);
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
     * 
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
     *  �_�C�A���O�̐���
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
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }   
    }
}
