package jp.sourceforge.gokigen.android.sample;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class MainListener implements OnClickListener, OnTouchListener
{
    public final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);
    public static final String INTENTINFO_DURATION = "LOCATION_DURATION";
    public static final String INTENTINFO_GPSTYPE  = "LOCATION_GPSTYPE";

    private Activity parent = null;  // �e��
    private MainUpdater updater = null;
		
    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
        updater = new MainUpdater(argument);
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {

    	
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
    	menuItem.setIcon(android.R.drawable.ic_menu_preferences);

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

    	  default:
    		result = false;
    		break;
    	}
    	return (result);
    }

    /**
     *  �ݒ��ʂ�\�����鏈��
     */
    private void showPreference()
    {
        try
        {
            // �ݒ��ʂ��Ăяo��
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.android.sample.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        	 updater.showMessage("ERROR", MainUpdater.SHOWMETHOD_DONTCARE);
        }
    }

    /**
     *  �_�C�A���O�̐���
     * 
     */
    public Dialog onCreateDialog(int id)
    {
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
