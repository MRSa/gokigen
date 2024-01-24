package jp.sourceforge.gokigen.sound.play;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 *    ���C�����X�i�f�B�X�p�b�`��
 *    
 * @author MRSa
 *
 */
public class SoundPlayListener implements OnTouchListener, OnKeyListener
{
/*
	private static final int ANDEXPLORER_OPEN_FILE_REQUEST = 0;
	private static final int ANDEXPLORER_OPEN_FOLDER_REQUEST = 1;
	private static final int ANDEXPLORER_SAVE_FILE_REQUEST = 2;
	private static final int ANDEXPLORER_SAVE_FOLDER_REQUEST = 3;
	private static final int ANDEXPLORER_UNCOMPRESS_REQUEST = 4;
*/
    private static final int OPEN_PREFERENCE = 0;
	private static final int OPEN_FILE_REQUEST = 100;
	
	public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);    // �ݒ��ʂ̕\��
    public static final int MENU_ID_ABOUT_GOKIGEN = (Menu.FIRST + 2);  // �A�v���P�[�V�����̏��\��
    public static final int MENU_ID_SELECT_FILE = (Menu.FIRST + 3);  // ���y�t�@�C��(MP3�t�@�C��)�̑I��

    private Activity parent = null;  // �e��
    private SoundPlayer player = null;
    private SoundVisualizerListener visualizerListener = null;

    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public SoundPlayListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
        // ��ʕ\���p�̃��X�i����
        visualizerListener = new SoundVisualizerListener(parent);

        // ��ʕ`��N���X�̐ݒ�
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        surfaceView.setCanvasDrawer(visualizerListener);
        visualizerListener.setGokigenSurfaceView(surfaceView);

        player = new SoundPlayer(parent, visualizerListener);

    	// PLAY�{�^��
        final Button playButton = (Button) parent.findViewById(R.id.PlayButton);
        playButton.setOnClickListener(player);

    	// PAUSE�{�^��
        final ImageButton pauseButton = (ImageButton) parent.findViewById(R.id.PauseButton);
        pauseButton.setOnClickListener(player);

        // STOP�{�^��
        final ImageButton stopButton = (ImageButton) parent.findViewById(R.id.StopButton);
        stopButton.setOnClickListener(player);    

        // �Đ��t�@�C������\��
        final TextView playFile = (TextView) parent.findViewById(R.id.SoundInfo);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        playFile.setText(preferences.getString("playFileName", ""));
    }

    /**
     *  �I������
     */
    public void finishListener()
    {

    }

    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {

    }


    /**
     *  �I������
     */
    public void shutdown()
    {
        player.finish();
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
        	Log.v(Main.APP_IDENTIFIER, "File Selected : " + fileName);

        	// �t�@�C������Preference�ɋL�^
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("playFileName", fileName);
            editor.commit();

            // �Đ��t�@�C��������ʕ\��
            final TextView playFile = (TextView) parent.findViewById(R.id.SoundInfo);
            playFile.setText(fileName);
       
        }
        else if (requestCode == OPEN_PREFERENCE)
        {
            // �Đ��t�@�C������\��
            final TextView playFile = (TextView) parent.findViewById(R.id.SoundInfo);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            playFile.setText(preferences.getString("playFileName", ""));
        }
    	
    	// ��ʕ\���̏��������s...
    	prepareToStart();
    }

    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // int id = v.getId();
        // int action = event.getAction();

        //Log.v(Main.APP_IDENTIFIER, "SoundPlayListener::onTouch() ");
    	
    	return (false);
    }

    /**
     *  �L�[���������Ƃ��̑���
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        int action = event.getAction();
        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {
        	//
        }

        // Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onKey() ");
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
    		result = true;
    		break;

    	  case MENU_ID_ABOUT_GOKIGEN:
    		showAboutGokigen();
	    	result = true;
	    	break;

    	  case MENU_ID_SELECT_FILE:
    		  selectMusicFileName();
    		  result = true;
    		  break;

    	  default:
    		result = false;
    		break;
    	}
    	return (result);
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
     *  �ݒ��ʂ�\�����鏈��
     */
    private void showPreference()
    {
        try
        {
            // �ݒ��ʂ��Ăяo��
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.sound.play.Preference.class);
            parent.startActivityForResult(prefIntent, OPEN_PREFERENCE);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        }
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
        /*
        if (id == R.id.editTextArea)
        {
    		// �ύX����e�L�X�g��\��
            return (editTextDialog.getDialog());
        }
        */
    	return (null);
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
    	/*
        if (id == R.id.editTextArea)
        {
        	// �ύX����f�[�^��\������
        	prepareInfoMessageEditDialog(dialog);
        	return;
        } 
        */   	
    }
}
/*
    AndExplorer �� Intent�g�p���@���
      �� http://www.lysesoft.com/products/andexplorer/intent.html
*/