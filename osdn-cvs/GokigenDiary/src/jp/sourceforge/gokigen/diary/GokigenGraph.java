package jp.sourceforge.gokigen.diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 *  ��������O���t�̕\�����
 * @author MRSa
 *
 */
public class GokigenGraph extends Activity
{
	static public final String TARGET_YEAR = "jp.sourceforge.gokigen.diary.GraphYear";
	static public final String TARGET_MONTH = "jp.sourceforge.gokigen.diary.GraphMonthl";
	static public final String TARGET_DAY = "jp.sourceforge.gokigen.diary.GraphDay";

	GokigenGraphListener listener = null;
	
	/**
	 *  ����������
	 * 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** ���X�i�N���X�𐶐� **/
        listener = new GokigenGraphListener((Activity) this);

        ///** �S��ʕ\���ɂ��� **/
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** �^�C�g�������� **/       
        //requestWindowFeature(Window.FEATURE_NO_TITLE);


        /** ��ʂ̏��� **/
        setContentView(R.layout.gokigengraph);

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
     *  �I��������
     */
    @Override
    protected void onDestroy()
    {
        listener.finishListener();
        super.onDestroy();
    }

    /**
     *  �J�n����
     */
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    /**
     *  ��~����
     */
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    
    /**
     *  �q��ʂ��牞������������Ƃ��̏���
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
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
