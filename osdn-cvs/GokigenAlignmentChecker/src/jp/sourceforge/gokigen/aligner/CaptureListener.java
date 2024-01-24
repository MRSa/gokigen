package jp.sourceforge.gokigen.aligner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class CaptureListener implements OnClickListener, OnTouchListener
{
    public static final int MENU_ID_ABOUT = (Menu.FIRST + 2);

    private Activity parent = null;  // �e��
    private GokigenGraphListener graphListener = null;

    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public CaptureListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
        try
        {
            /** �`��N���X�̏��� **/
        	graphListener = new GokigenGraphListener(parent);
        	graphListener.prepareListener();
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
    	graphListener.prepareListener();
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
     *   �L�[�������ꂽ�Ƃ��̏���
     * 
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return (graphListener.onKeyDown(keyCode, event));
    }

    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        // int action = event.getAction();
    	//Log.v(Main.APP_IDENTIFIER, "CaptureListener::onTouchEvent() :" + event.getAction());

        return (false);
    }
    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
    	//Log.v(Main.APP_IDENTIFIER, "onTouch() :" + event.getAction());

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
        return (null);
/*
    	MenuItem menuItem;

        // �N���W�b�g�̕\��
        menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, parent.getString(R.string.about));
    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);

        return (menu);
*/
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
        //menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
    	//menu.findItem(MENU_ID_ABOUT).setVisible(true);
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
         case MENU_ID_ABOUT:
      		// �A�v���̃N���W�b�g�\��
      	    parent.showDialog(R.id.info_about_gokigen);
        	result = true;
        	break;

          default:
            result = false;
            break;
        }
        return (result);
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
