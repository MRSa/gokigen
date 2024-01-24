package jp.sfjp.gokigen.okaken;

import java.util.Timer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class OkakenActivity extends Activity implements ClockTimer.ITimeoutReceiver, IActivityOpener, OnClickListener
{
	private ClockTimer myTimer = null;
	private Timer timer = null;
    private static final long duration = 500;   // 500ms
    private MainDrawer drawer = null;
    //private GameInformationProvider gameStatusHolder = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** �S��ʕ\���ɂ��� **/
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /** �^�C�g�������� **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Preference���擾����
        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	// String colorString = preferences.getString("backgroundColor", "0xff004000");

        // ��ʕ\���̃��C�A�E�g��ݒ肷��
        setContentView(R.layout.main);
        
        // ��ʕ`��N���X�̐ݒ�
        drawer = new MainDrawer(this, this);
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) findViewById(R.id.MainView);
        surfaceView.setCanvasDrawer(drawer);

        // �{�^���̐ݒ�
        final Button instructionButton = (Button) findViewById(R.id.InstructionButton);
        instructionButton.setOnClickListener(this);
        //surfaceView.setOnTouchListener(this);
    
    }

    /**
     *  ���j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add(Menu.NONE, Gokigen.MENU_ID_ABOUT, Menu.NONE, getString(R.string.about_gokigen));
    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *  ���j���[�A�C�e���̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = false;
        switch (item.getItemId())
        {
          case Gokigen.MENU_ID_ABOUT:
        	// About���j���[���I�����ꂽ�Ƃ��́A�N���W�b�g�_�C�A���O��\������
        	showDialog(R.id.info_about_gokigen);
            result = true;
            break;

          default:
            result = false;
            break;
        }
        return (result);
    }
    
    /**
     *  ���j���[�\���O�̏���
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(Gokigen.MENU_ID_ABOUT).setVisible(true);
        return (super.onPrepareOptionsMenu(menu));
    }

    /**
     *  ��ʂ����ɉ�����Ƃ��̏���
     */
    @Override
    public void onPause()
    {
        super.onPause();
        stopTimer();

    }
    
    /**
     *  ��ʂ��\�ɏo�Ă����Ƃ��̏���
     */
    @Override
    public void onResume()
    {
        super.onResume();
        startTimer();
    }
    
    /**
     *  �q��ʂ��牞������������Ƃ��̏���
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            // �q��ʂ������������̉�������
        }
        catch (Exception ex)
        {
            // ��O�����������Ƃ��ɂ́A�������Ȃ��B
        }
    } 

    /**
     *  �_�C�A���O�\���i����j�̏���
     * 
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == R.id.info_about_gokigen)
	    {
        	// �N���W�b�g�_�C�A���O��\��
		    CreditDialog dialog = new CreditDialog(this);
		    return (dialog.getDialog());
	    }
    	return (null);
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
        if (id == R.id.info_about_gokigen)
	    {
            // �N���W�b�g�_�C�A���O��\������Ƃ��ɂ͉������Ȃ��B
        	return;
	    }
    	// �_�C�A���O�����X�V����ꍇ�ɂ́A�����ɒǉ�����
    	return;
    }  
    /**
     *   �^�C���A�E�g��M���̏���...
     * 
     */
    public void receivedTimeout()
    {
        // Log.v(Gokigen.APP_IDENTIFIER, "receivedTimeout()");    

    	// ��ʂ̍ĕ`��w���B�B�B�i0.5sec�����H�j
    	final GokigenSurfaceView surfaceView0 = (GokigenSurfaceView) findViewById(R.id.MainView);
    	surfaceView0.doDraw();
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        int id = v.getId();
    	drawer.showInstruction(id);

    	// ��ʂ̍ĕ`��w���B�B�B�i0.5sec�����H�j
    	final GokigenSurfaceView surfaceView1 = (GokigenSurfaceView) findViewById(R.id.MainView);
    	surfaceView1.doDraw();

    }
    
    /**
     *  Activity��؂�ւ���
     * 
     * @param fileName
     */
    public void requestToStartActivity(int id)
    {
        try
        {
            // Activity���N������
            Intent intent = new Intent(this, jp.sfjp.gokigen.okaken.MoleGameActivity.class);
            startActivityForResult(intent, id);
        }
        catch (Exception ex)
        {
        	// ��O������...
        }
    }
    
    /**
     * 
     * 
     */
    private void stopTimer()
    {
        try
        {
            // TODO: ������~�߂�悤�C�x���g�����N���X�Ɏw������
        	if (timer != null)
        	{
        		timer.cancel();
        		timer = null;
        	}
           	myTimer = null;
        }
        catch (Exception ex)
        {
            // �������Ȃ�
        }    	
    }
    
    /**
     * 
     * 
     */
    private void startTimer()
    {
        try
        {
        	if (timer != null)
        	{
        		timer.cancel();
        		timer = null;
        	}
        	timer = new Timer();
        	
        	// �^�C�}�[�^�X�N�̏���
           	myTimer = null;
        	myTimer = new ClockTimer(this);
        	timer.scheduleAtFixedRate(myTimer, duration, duration);
        }
        catch (Exception ex)
        {
            // �Ȃɂ����Ȃ�
        }    	
    }
}