package jp.sourceforge.gokigen.aligner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MainListener  implements OnClickListener, OnTouchListener, ICanvasDrawer
{
    public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);
    public static final int MENU_ID_ABOUT = (Menu.FIRST + 2);
    public static final int MENU_ID_CAPTURE = (Menu.FIRST + 3);
    public static final int MENU_ID_UNDO = (Menu.FIRST + 4);
    public static final int MENU_ID_RESET = (Menu.FIRST + 5);
    public static final int MENU_ID_CHECKIN =  (Menu.FIRST + 6);

    private Activity parent = null;  // �e��
    private ImageAdjuster imageSetter = null;
    private IBodyDrawer bodyDrawer = null;

    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
        imageSetter = new ImageAdjuster(parent);
        imageSetter.setIsClearBitmap(false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        String shapeType = preferences.getString("showShapeType", "0");
        int reportType = 0;
        try
        {
        	reportType = Integer.parseInt(shapeType);
        }
        catch (Exception ex)
        {
        	//
        	Log.v(Main.APP_IDENTIFIER, "cannot get ShapeType.");
        }
        if (reportType == 0)
        {
            bodyDrawer = new GokigenSideBodyDrawer(parent, imageSetter);
        }
        else
        {
            bodyDrawer = new GokigenFrontBodyDrawer(parent, imageSetter);
        }
        bodyDrawer.setMessage("");
    
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
        try
        {

        }
        catch (Exception ex)
        {
            // �_�~�[�̃K�x�R��
            System.gc();
        }
    }

    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
    	// �v���t�@�����X����摜�t�@�C����ǂݏo��
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        String filenameToShow = preferences.getString(Main.APP_EXAMINE_FILENAME, "");
        int minLen = Environment.getExternalStorageDirectory().getPath().length();
        if (filenameToShow.length() <= minLen)
        {
            ////////// �\������摜�t�@�C�����Ȃ������Ƃ��B�B�B�L���v�`����ʂ��J�� //////////
        	showCapture();
        	return;
        }

        // �\���f�[�^����ʂɕ\������        
        final ImageView imgView = (ImageView) parent.findViewById(R.id.PictView);
        imageSetter.setImage(imgView, filenameToShow);

        Log.v(Main.APP_IDENTIFIER, "File name to show : " + filenameToShow);
        
        // �̂̌`��\������
        prepareBodyShape();
        return;	 
    }

    /**
     *  �C���[�W�f�[�^��\����ɌĂяo����鏈��
     * 
     */
    private void prepareBodyShape()
    {
        // �O���t(�X�P�[��)�����̗̈�
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.InfoView);
        view.setCanvasDrawer(this);
        view.setOnClickListener(this);
        view.setOnTouchListener(this);
        view.setPixelFormat(PixelFormat.TRANSLUCENT);
        view.bringToFront();
        view.doDraw();
        view.setPixelFormat(PixelFormat.TRANSLUCENT);
    }

    /**
     *  �I������
     */
    public void shutdown()
    {

    }

    /**
     *  �L�����o�X�Ƀf�[�^��`�悷��
     * 
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	//Log.v(Main.APP_IDENTIFIER, "MainListener::drawOnCanvas()");
    	try
    	{
    		// �ĕ`������s
            bodyDrawer.drawOnCanvas(canvas, 0);
    	}
    	catch (Exception ex)
    	{
    		// ��O����...�ł����̂Ƃ��ɂ͉������Ȃ�
    		Log.v(Main.APP_IDENTIFIER, "drawOnCanvas()" + ex.getMessage());
    	}
    	return;
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
     *   �L�[�������ꂽ�Ƃ��̏���
     * 
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return (false);
    }

    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	boolean ret = bodyDrawer.onTouchEvent(event);
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.InfoView);
        view.doDraw();

        return (ret);
    }
    

    /**
     *  �g���b�N�{�[�����������ꂽ�Ƃ��̏���
     * 
     * @param event
     * @return
     */
    public boolean onTrackballEvent(MotionEvent event)
    {
    	//Log.v(Main.APP_IDENTIFIER, "onTrackballEvent()...");
    	boolean ret = bodyDrawer.onTrackballEvent(event);
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.InfoView);
        view.doDraw();
    	return (ret);
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
        MenuItem menuItem;

        // �A���C�����g�`�F�b�N�̎��s
        menuItem = menu.add(Menu.NONE, MENU_ID_CHECKIN, Menu.NONE, parent.getString(R.string.checkin_name));
        menuItem.setIcon(R.drawable.ic_menu_mark);
        
        // �L���v�`����ʂ̕\��
        menuItem = menu.add(Menu.NONE, MENU_ID_CAPTURE, Menu.NONE, parent.getString(R.string.captureScreen_name));
        menuItem.setIcon(android.R.drawable.ic_menu_camera);
        
        // �ʒu�A���h�D
        menuItem = menu.add(Menu.NONE, MENU_ID_UNDO, Menu.NONE, parent.getString(R.string.undo_name));
        menuItem.setIcon(R.drawable.ic_menu_revert);
        
        // �ʒu���Z�b�g
        menuItem = menu.add(Menu.NONE, MENU_ID_RESET, Menu.NONE, parent.getString(R.string.reset_name));
        menuItem.setIcon(R.drawable.ic_menu_refresh);
        
        // �ݒ荀�ڂ̕\��
        menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
        menuItem.setIcon(android.R.drawable.ic_menu_preferences);
        // �N���W�b�g�̕\��
        menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, parent.getString(R.string.about));
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
        menu.findItem(MENU_ID_CHECKIN).setVisible(true);
        menu.findItem(MENU_ID_UNDO).setVisible(true);
        menu.findItem(MENU_ID_CAPTURE).setVisible(true);
        menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
        menu.findItem(MENU_ID_RESET).setVisible(true);
    	menu.findItem(MENU_ID_ABOUT).setVisible(true);
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
          case MENU_ID_CAPTURE:
            // �L���v�`����ʂ̕\��
        	showCapture();
            result = true;
            break;

          case MENU_ID_PREFERENCES:
            // �ݒ荀�ڂ̕\��
        	showPreference();
            result = true;
            break;

          case MENU_ID_ABOUT:
      		// �A�v���̃N���W�b�g�\��
      	    parent.showDialog(R.id.info_about_gokigen);
        	result = true;
        	break;

          case MENU_ID_UNDO:
            // ����̃A���h�D
        	bodyDrawer.undo();
        	refreshView();
            result = true;
            break;

          case MENU_ID_RESET:
            // ����̃��Z�b�g
          	bodyDrawer.reset();
        	refreshView();
            break;

          case MENU_ID_CHECKIN:
        	// �A���C�����g�̃`�F�b�N�����s����
        	doCheckAllignment();
        	break;
            
          default:
            result = false;
            break;
        }
        return (result);
    }

    /**
     *  ��ʂ��ĕ`�悷��
     * 
     */
    private void refreshView()
    {
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.InfoView);
        view.doDraw();
    }
    
    /**
     *  �L���v�`����ʂ�\�����鏈��
     * 
     */
    private void showCapture()
    {
        try
        {
            Intent captureIntent = new Intent(parent,jp.sourceforge.gokigen.aligner.CaptureScreen.class);
            parent.startActivityForResult(captureIntent, R.id.captureInformationArea);
        }
        catch (Exception e)
        {
             // ��O����...
            Log.v(Main.APP_IDENTIFIER, "Launch Fail(Capture) : " + e.getMessage());
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
            Intent prefIntent = new Intent(parent, jp.sourceforge.gokigen.aligner.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
            Log.v(Main.APP_IDENTIFIER, "Launch Fail(Preference) : " + e.getMessage() + " " + e.toString());
        }
    }

    /**
     *   �A���C�����g�̃`�F�b�N�����s����
     * 
     */
    private void doCheckAllignment()
    {
    	bodyDrawer.storePosition();
        return;
    }
    
    /**
     *  �_�C�A���O�̐���
     * 
     */
    public Dialog onCreateDialog(int id)
    {
    	if (id == R.id.info_about_gokigen)
    	{
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
}
