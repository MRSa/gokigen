package jp.sourceforge.gokigen.sensors.checker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class LogCat extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ///** �S��ʕ\���ɂ��� **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** �^�C�g�������� **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /** ���X�i�N���X�̏��� **/
        //listener.prepareListener();

        /** ��ʂ̏��� **/
        setContentView(R.layout.logcat);
        
        /** �N���X�̏������� **/
        prepareMyActivity();
    }

    /**
     *  ���j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return (super.onPrepareOptionsMenu(menu));
    }
    /**
     *  ���j���[�A�C�e���̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return (super.onOptionsItemSelected(item));
    }

    /**
     *  ��ʂ����ɉ�����Ƃ��̏���
     */
    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     *  ��ʂ��\�ɏo�Ă����Ƃ��̏���
     */
    @Override
    public void onResume()
    {
        super.onResume();
    }

    /**
     * 
     * 
     */
    @Override
    protected void onDestroy()
    {
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
     *  �q��ʂ��牞������������Ƃ��̏���
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
/**
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
**/
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    protected Dialog onCreateDialog(int id)
    {
        return (prepareConfirmDeleteDialog());
    }
    
    /**
     *   �t�@�C���̍폜���m�F����_�C�A���O
     * 
     * @return
     */
    private Dialog prepareConfirmDeleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmClose));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.confirmYes), new DialogInterface.OnClickListener()
              {
                   public void onClick(DialogInterface dialog, int id)
                   {
                       LogCat.this.finish();
                   }
               });
        builder.setNegativeButton(getString(R.string.confirmNo), new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                        dialog.cancel();
                   }
               });
        return (builder.create());
    }

    /**
     *  Activity�̏������s��
     * 
     */
    private void prepareMyActivity()
    {
        //Intent myIntent = getIntent();
        try
        {
            ArrayList<String> commandLine = new ArrayList<String>();
            // �R�}���h�̍쐬
            commandLine.add( "logcat");
            commandLine.add( "-d");
            commandLine.add( "-v");
            commandLine.add( "time");
            commandLine.add( "-s");
            commandLine.add( "tag:W");
            Process process = Runtime.getRuntime().exec( commandLine.toArray( new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 1024);
            String line = bufferedReader.readLine();
            String log = "";
            while (line != null)
            {
            	log = log + line + "\n";
            }
        }
        catch (Exception ex)
        {
            Log.v("SensorsChecker", "EXCEPTION :" + ex.getMessage());
        }

    }
}
