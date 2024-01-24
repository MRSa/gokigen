package jp.sfjp.gokigen.okaken;

import java.util.Timer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 *    �����炽�����Q�[���I
 * 
 * @author MRSa
 *
 */
public class MoleGameActivity extends Activity  implements ClockTimer.ITimeoutReceiver, GameInformationProvider.IGameStatusListener, IActivityOpener
{
	public static final int NUMBER_OF_MOLE_COLUMNS = 3;
	public static final int NUMBER_OF_MOLE_ROWS = 4;
	public static final int NUMBER_OF_QUESTIONS = NUMBER_OF_MOLE_COLUMNS * (NUMBER_OF_MOLE_ROWS - 1);
	public static final int NUMBER_OF_MOLE_HOLES = NUMBER_OF_QUESTIONS - 1;

	private ClockTimer myTimer = null;
	private Timer timer = null;
    private static final long duration = 100;   // 100ms
    private MoleGameDrawer drawer = null;
    private QuestionnaireProvider questionProvider = null;
    private GameInformationProvider gameStatusHolder = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** �S��ʕ\���ɂ��� **/
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /** �^�C�g�������� **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.molegame);

        // �Q�[����Ԃ̕ێ��I�u�W�F�N�g�̐���
        questionProvider = new QuestionnaireProvider(this, NUMBER_OF_QUESTIONS);
        gameStatusHolder = new GameInformationProvider(this, duration, questionProvider, this);

        // ��ʕ`��N���X�̐ݒ�
        drawer = new MoleGameDrawer(this, gameStatusHolder, this);
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) findViewById(R.id.MoleGameCanvasView);
        surfaceView.setCanvasDrawer(drawer);  
    }

    /**
     *  ���j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //MenuItem menuItem = menu.add(Menu.NONE, Gokigen.MENU_ID_ABOUT, Menu.NONE, getString(R.string.about_gokigen));
    	//menuItem.setIcon(android.R.drawable.ic_menu_info_details);
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *  ���j���[�A�C�e���̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = false;

        return (result);
    }
    
    /**
     *  ���j���[�\���O�̏���
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        //menu.findItem(Gokigen.MENU_ID_ABOUT).setVisible(true);
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
    	if ((drawer != null)&&(drawer.isGameOverDrawn() == true))
    	{
            // �Q�[���I�[�o�[���̕`�悪�ς�ł���Ƃ��ɂ́A�^�C�}���~�߂Ă݂�B
    		return;
    	}
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
    	return (null);
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
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
    	if ((drawer != null)&&(drawer.isGameOverDrawn() == true))
    	{
                // �Q�[���I�[�o�[���̕`�悪�ς�ł���Ƃ��ɂ̓^�C���A�E�g���������Ă��������Ȃ��B
    		    return;
    	}

    	if (gameStatusHolder != null)
    	{
    		gameStatusHolder.receivedTimeout();    		
    	}

    	// ��ʂ̍ĕ`��w���B�B�B
    	final GokigenSurfaceView surfaceView0 = (GokigenSurfaceView) findViewById(R.id.MoleGameCanvasView);
    	surfaceView0.doDraw();
    }
    
    public  void changedCurrentGameStatus(int status)
    {
    	// ��ʂ̍ĕ`��w���B�B
    	final GokigenSurfaceView surfaceView1 = (GokigenSurfaceView) findViewById(R.id.MoleGameCanvasView);
    	surfaceView1.doDraw();    	
    }

    public  void triggeredGameStatus(int status)
    {
    	// ��ʂ̍ĕ`��w���B�B
    	final GokigenSurfaceView surfaceView1 = (GokigenSurfaceView) findViewById(R.id.MoleGameCanvasView);
    	surfaceView1.doDraw();    	
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
            Intent intent = new Intent(this, jp.sfjp.gokigen.okaken.ResultActivity.class);
            intent.putExtra(Gokigen.APP_INFORMATION_STORAGE, questionProvider);
            startActivity(intent);
        }
        catch (Exception ex)
        {
        	// ��O������...
        }
    }
}
