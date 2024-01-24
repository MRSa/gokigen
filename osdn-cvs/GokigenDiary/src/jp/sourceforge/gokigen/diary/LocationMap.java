package jp.sourceforge.gokigen.diary;

import com.google.android.maps.MapActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class LocationMap extends MapActivity
{
    static public final String LOCATION_FILE       = "jp.sourceforge.gokigen.diary.locationCsv";
    static public final String LOCATION_ISCURRENT  = "jp.sourceforge.gokigen.diary.isCurrent";
    static public final String LOCATION_ICONID     = "jp.sourceforge.gokigen.diary.locIcon";
    static public final String LOCATION_LATITUDE = "jp.sourceforge.gokigen.diary.latitude";
    static public final String LOCATION_LONGITUDE = "jp.sourceforge.gokigen.diary.longitude";
    static public final String LOCATION_MESSAGE = "jp.sourceforge.gokigen.diary.locMsg";

    LocationMapListener  listener = null;   // �C�x���g�����N���X

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        listener = new LocationMapListener((Activity) this);

        ///** �S��ʕ\���ɂ��� **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** �^�C�g�������� **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /** ��ʂ̏��� **/
        setContentView(R.layout.locationmap);

        /** �C�x���g���X�i���������� **/
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
     *  �_�C�A���O�\���̏���
     * 
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	return (listener.onCreateDialog(id));
    }
    
    /**
     *   �I��(�폜)���ɌĂ΂�鏈��
     */
    @Override
    protected void onDestroy()
    {
        listener.finishListener();
        super.onDestroy();        
    }
    
}
