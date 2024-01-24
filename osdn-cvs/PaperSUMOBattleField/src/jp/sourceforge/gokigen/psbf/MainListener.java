package jp.sourceforge.gokigen.psbf;

import java.io.File;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class MainListener implements OnClickListener, OnTouchListener, SoundVisualizerListener.IDataCaptureListener
{
    private static final int OPEN_PREFERENCE = 0;
	private static final int OPEN_FILE_REQUEST = 100;

	public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);
    public static final int MENU_ID_ABOUT_GOKIGEN = (Menu.FIRST + 2);  // �A�v���P�[�V�����̏��\��
    public static final int MENU_ID_SELECT_FILE = (Menu.FIRST + 3);  // ���y�t�@�C��(MP3�t�@�C��)�̑I��
    public static final int MENU_ID_FORCE_DISPLAY = (Menu.FIRST + 4);  // ��ʂ̋����\��
    public static final int MENU_ID_QUIT = (Menu.FIRST + 5); // �A�v���P�[�V�����I��

    private PSBFBaseActivity parent = null;  // �e��
    
    private SoundPlayer player = null;
    private SoundVisualizerListener soundListener = null;
    private boolean motorControlSignal = false;
    private long motorControlCount = 0;

    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public MainListener(PSBFBaseActivity argument)
    {
        parent = argument;
        soundListener = new SoundVisualizerListener(argument);
        player = new SoundPlayer(argument, soundListener);
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
    	
     }

    /**
     *   �R���g���[���p�̃��X�i�ݒ�
     * 
     */
    public void prepareControlListeners(InputController inputController)
    {
        // ��ʕ`��N���X�̐ݒ�
    	try
    	{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        	int operationMode = Integer.parseInt(preferences.getString("operationMode", "2"));
        	if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
        	{
                // �ʏ푀�샂�[�h
                SumoGameController controller = inputController.getGameController();
                if (controller != null)
                {
                	controller.prepare();
                }
        	}
        	else if (operationMode == PSBFBaseActivity.OPERATIONMODE_DEMONSTRATION)
        	{
                // �f�����X�g���[�V�������[�h  
                final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
                surfaceView.setCanvasDrawer(soundListener);
                soundListener.setGokigenSurfaceView(surfaceView);
                soundListener.setDataCaptureListener(this);
        	}
        	else  // if (operationMode == PSBFBaseActivity.OPERATIONMODE_MANUAL)
        	{
        		// �}�j���A�����샂�[�h
        	}
    	}
    	catch (Exception ex)
    	{
    		// ���O�\��
    	    Log.v(PSBFMain.APP_IDENTIFIER, "prepareToStart() EX: " + ex.toString());
    	}
    }

    /**
     *  �I������
     */
    public void finishListener()
    {
    	// �Đ����̎��ɂ́A�����~�߂�B
        if ((player != null)&&(player.isPlaying() == true))
        {
        	player.finish();
        }
    }

    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
    	try
    	{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        	int operationMode = Integer.parseInt(preferences.getString("operationMode", "2"));
        	if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
        	{
                // �ʏ푀�샂�[�h ... ��قǐݒ肷��
        	}
        	else if (operationMode == PSBFBaseActivity.OPERATIONMODE_DEMONSTRATION)
        	{
                // �f�����X�g���[�V�������[�h  
        		prepareGokigenSurfaceView();
                player.playSound();
        	}
        	else  // if (operationMode == PSBFBaseActivity.OPERATIONMODE_MANUAL)
        	{
        		// �}�j���A�����샂�[�h
        	}
    	}
    	catch (Exception ex)
    	{
    		// ���O�\��
    	    Log.v(PSBFMain.APP_IDENTIFIER, "prepareToStart() EX: " + ex.toString());
    	}
    }
    /**
     *   �R���g���[���p�̃��X�i�ݒ�
     * 
     */
    private void prepareGokigenSurfaceView()
    {
        // ��ʕ`��N���X�̐ݒ�
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        if (surfaceView != null)
        {
            surfaceView.setCanvasDrawer(soundListener);
            soundListener.setGokigenSurfaceView(surfaceView);
        }
    }
    
    /**
     *  �I������
     */
    public void shutdown()
    {
        
    }
    
    /**
     *  ����ʂ���߂��Ă����Ƃ�...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == OPEN_FILE_REQUEST)
        {
        	// ���y�t�@�C���̑I��...
        	Uri selectFile = data.getData();   // AndExporler ����擾����B
        	String fileName = selectFile.toString().substring(7);   // "file://" ��؂���B
        	Log.v(PSBFMain.APP_IDENTIFIER, "File Selected : " + fileName);

        	// �t�@�C������Preference�ɋL�^
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("playFileName", fileName);
            editor.commit();

            /**
            // �Đ��t�@�C��������ʕ\��
            final TextView playFile = (TextView) parent.findViewById(R.id.SoundInfo);
            playFile.setText(fileName);
            **/
        }
        else if (requestCode == OPEN_PREFERENCE)
        {
            // �ݒ��ʂ���A���Ă������A
        }

    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        // int id = v.getId();

    }

    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // int id = v.getId();
        // int action = event.getAction();

        return (false);
    }

    /**
     *   ���j���[�ւ̃A�C�e���ǉ�
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
        menuItem.setIcon(android.R.drawable.ic_menu_preferences);
    	menuItem = menu.add(Menu.NONE, MENU_ID_SELECT_FILE, Menu.NONE, parent.getString(R.string.selectFile));
    	menuItem.setIcon(android.R.drawable.ic_menu_view);
        menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT_GOKIGEN, Menu.NONE, parent.getString(R.string.about_gokigen));
        menuItem.setIcon(android.R.drawable.ic_menu_info_details);
        menuItem = menu.add(Menu.NONE, MENU_ID_FORCE_DISPLAY, Menu.NONE, parent.getString(R.string.simulate));
        menuItem = menu.add(Menu.NONE, MENU_ID_QUIT, Menu.NONE, parent.getString(R.string.quit));
        return (menu);
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
        menu.findItem(MENU_ID_ABOUT_GOKIGEN).setVisible(true);
    	menu.findItem(MENU_ID_SELECT_FILE).setVisible(true);
    	menu.findItem(MENU_ID_FORCE_DISPLAY).setVisible(true);
    	menu.findItem(MENU_ID_QUIT).setVisible(true);
        return;
    }

    /**
     *   ���j���[�̃A�C�e�����I�����ꂽ�Ƃ��̏���
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = false;
        switch (item.getItemId())
        {
          case MENU_ID_PREFERENCES:
            showPreference();
            result = false;
            break;

          case MENU_ID_ABOUT_GOKIGEN:
              showAboutGokigen();
                result = true;
                break;

    	  case MENU_ID_SELECT_FILE:
    		  selectMusicFileName();
    		  result = true;
    		  break;
    	  case MENU_ID_FORCE_DISPLAY:
    		  forceDisplay();
    		  result = true;
    		  break;
    	  case MENU_ID_QUIT:
    		  finishApplication();
    		  result = true;
    		  break;
          default:
            result = false;
            break;
        }
        return (result);
    }

    private void finishApplication()
    {
        parent.finish();
        System.exit(0);
    }

    private void forceDisplay()
    {
    	parent.showControls();
    }
    
    
    /**
     *  �ݒ��ʂ�\�����鏈��
     */
    private void showPreference()
    {
        try
        {
            // �ݒ��ʂ��Ăяo��
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.psbf.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        }
    }

    /**
     *   �A�v���̏���\������
     * 
     */
    private void showAboutGokigen()
    {
        // �A�v���̏��(�N���W�b�g)��\������I
        parent.showDialog(R.id.info_about_gokigen);
    }    

    /**
     *  �_�C�A���O�̐���
     * 
     */
    public Dialog onCreateDialog(int id)
    {
        if (id == R.id.info_about_gokigen)
        {
            // �N���W�b�g�_�C�A���O��\��
            CreditDialog dialog = new CreditDialog(parent);
            return (dialog.getDialog());
        }
        return (null);
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
        
    }

    /**
     *    �����t�@�C���̑I��
     * 
     */
    private void selectMusicFileName()
    {
    	try
    	{
        	Intent intent = new Intent();
        	intent.setAction(Intent.ACTION_PICK);
        	Uri startDir = Uri.fromFile(new File("/sdcard"));
        	intent.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.file");
        	intent.putExtra("browser_filter_extension_whitelist", "*.mp3");
        	intent.putExtra("explorer_title", "Open MP3 File...");
        	intent.putExtra("browser_title_background_color", "440000AA");
        	intent.putExtra("browser_title_foreground_color", "FFFFFFFF");
        	intent.putExtra("browser_list_background_color", "66000000");
        	intent.putExtra("browser_list_fontscale", "120%");
        	intent.putExtra("browser_list_layout", "2"); 
        	parent.startActivityForResult(intent, OPEN_FILE_REQUEST);
    	}
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        }
    }

	/** �^�b�`�C�x���g����M�������̏��� **/
    public boolean onTouchEventReceived(MotionEvent event)
    {
    	return (false);
    }

    /** ���y�f�[�^����M�������̏��� **/
    public void onMusicDataReceived(float current, float max)
    {
    	 // ���y�ɂ��킹�āA���[�^�𓮂����I
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
    	int volumeRate = Integer.parseInt(preferences.getString("volumeRate", "10"));    	
    	int volumeOffsetA = Integer.parseInt(preferences.getString("volumeOffsetA", "1"));    	
    	int volumeOffsetB = Integer.parseInt(preferences.getString("volumeOffsetB", "1"));    	
    	long volumeCount = Integer.parseInt(preferences.getString("volumeCount", "1"));    	

    	motorControlCount++;
    	if ((motorControlCount % volumeCount) != 0)
    	{
            // ���[�^����Ԋu�̍��Ԃ������̂ŉ������Ȃ�
    		return;
    	}
    	
    	// ���[�^�[�̏o�́H
    	float motorDriveRate = (volumeRate / 10) * current;
    	int value = (int) motorDriveRate;

    	// �ǂ����̃��[�^�𓮂����H
    	byte command = PSBFBaseActivity.MOTOR_A;
    	String motorType = "A";
    	if (motorControlSignal == true)
    	{
    		command = PSBFBaseActivity.MOTOR_A;
    		motorControlSignal = false;
    		value = value + volumeOffsetA;
    		motorType = "A";
    	}
    	else
    	{
    		command = PSBFBaseActivity.MOTOR_B;
    		motorControlSignal = true;
    		value = value + volumeOffsetB;
    		motorType = "B";
    	}
    	
    	// ADK�փ��N�G�X�g�𑗐M
    	Log.v(PSBFMain.APP_IDENTIFIER, "MOVE MOTOR : " + motorType + " " + value);
        parent.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, command, value);

    }
}
