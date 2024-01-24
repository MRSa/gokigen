package jp.sourceforge.gokigen.cvtest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

/**
 *    ���X�i�N���X
 * 
 * @author MRSa
 *
 */
public class MainListener implements OnClickListener, OnTouchListener, QSteerControlDrawer.IRedrawer
{
    public final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);
    public final int MENU_ID_ABOUT = (Menu.FIRST + 2);
    public final int MENU_ID_TEST = (Menu.FIRST + 3);

    private Activity parent = null;  // �e��
    private QSteerControlDrawer canvasDrawer = null;
    private CaptureOverlayDrawer overlayDrawer = null;
    private ImageProcessor imageProcessor = null;
 		
    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
        imageProcessor = new ImageProcessor(this);
        canvasDrawer = new QSteerControlDrawer(argument, this, imageProcessor);
        overlayDrawer = new CaptureOverlayDrawer(argument, imageProcessor);
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
        // �u���s���v�̕\��������
    	parent.setProgressBarIndeterminateVisibility(false);
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
    	// �����ɍ��킹�āA�`��N���X��ύX����
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        surfaceView.setCanvasDrawer(canvasDrawer);
        surfaceView.setTranslucent();

        // �J�����摜�̏�ɏd�˂��킹�ĕ\������`��N���X��ݒ肷��
        final GokigenSurfaceView overlayView = (GokigenSurfaceView) parent.findViewById(R.id.OverlayView);
        overlayView.setCanvasDrawer(overlayDrawer);
        overlayView.setTranslucent();

        // �J�����摜��M���̏����N���X��ݒ�
        final CameraViewer cameraView = (CameraViewer) parent.findViewById(R.id.CameraView);
        cameraView.setPreviewCallback(imageProcessor);
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
    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
    	//menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
    	menuItem.setIcon(android.R.drawable.ic_menu_preferences);

    	menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, parent.getString(R.string.about_gokigen));
    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
    	//menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);

    	menuItem = menu.add(Menu.NONE, MENU_ID_TEST, Menu.NONE, parent.getString(R.string.app_name));
    	//menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
    	menuItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);

    	return (menu);
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
    	menu.findItem(MENU_ID_PREFERENCES).setVisible(false);
    	menu.findItem(MENU_ID_ABOUT).setVisible(true);
    	menu.findItem(MENU_ID_TEST).setVisible(false);
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
    	  case MENU_ID_ABOUT:
    		showAboutGokigen();
    		result = true;
    		break;
    	  case MENU_ID_TEST:
      		showTestScreen();
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
     *  �e�X�g�p��ʂ�\�����鏈��
     */
    private void showTestScreen()
    {
        try
        {
            // �ʂ̉�ʂ��Ăяo��
            //Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.qsteer.drive.Sample1Java.class);
            //parent.startActivityForResult(prefIntent, 0);
        	Log.v(GokigenSymbols.APP_IDENTIFIER, "called showTestScreen() ");
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
            Log.v(GokigenSymbols.APP_IDENTIFIER, "showTestScreen() : " + e.toString());
        }
    }

    /**
     *  �ݒ��ʂ�\�����鏈��
     */
    private void showPreference()
    {
        try
        {
            // �ݒ��ʂ��Ăяo��
            Intent prefIntent = new Intent(parent, jp.sourceforge.gokigen.cvtest.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        }
    }

    public void onSaveInstanceState(Bundle outState)
    {
	    /* �����ŏ�Ԃ�ۑ� */ 
	    //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onSaveInstanceState()");
    }
    
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	/* �����ŏ�Ԃ𕜌� */
	    //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onRestoreInstanceState()");
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
     *   ��ʂ��ĕ`�悷��B
     * 
     */
    public void redraw()
    {
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        surfaceView.doDraw();
        
        final GokigenSurfaceView overlayView = (GokigenSurfaceView) parent.findViewById(R.id.OverlayView);
        overlayView.doDraw();
    }

    /**
     *    USB�A�N�Z�T�����L���E�����ɂȂ����Ƃ��ɌĂяo�����B
     * 
     * @param isEnable
     */
    public void enableControls(boolean isEnable)
    {
    	if (isEnable == true)
    	{
    	    showControls();    		
    	}
    	else
    	{
            parent.setContentView(R.layout.no_device);
    	}    	
    }

    private void showControls()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        boolean isSingleMode = preferences.getBoolean("SingleControl", false);
        if (isSingleMode == true)
        {
        	// �V���O�����샂�[�h
        }
        else
        {
        	// �f���A�����샂�[�h
        }
        parent.setContentView(R.layout.main);
    }
    
    /**
     *    USB�A�N�Z�T�����烁�b�Z�[�W����M�������̏��� 
     * 
     * @param msg
     */
    public void receivedMessage(Message msg)
    {
    	
    }
}
