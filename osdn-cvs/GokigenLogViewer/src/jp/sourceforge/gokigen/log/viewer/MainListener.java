package jp.sourceforge.gokigen.log.viewer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 *  ��ʑ�����f�B�X�p�b�`����N���X
 *  (Activity�ƕ�����ׂ����ǂ���...�����͕�����ق����Ƃ��Ă��邪...
 * 
 * @author MRSa
 *
 */
public class MainListener implements OnClickListener, OnTouchListener
{
    public final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);
    public final int MENU_ID_ABOUT      = (Menu.FIRST + 3);
    public final int MENU_ID_EXPORT     = (Menu.FIRST + 4);
    public final int MENU_ID_TOP        = (Menu.FIRST + 5);
    public final int MENU_ID_BOTTOM     = (Menu.FIRST + 6);
    public final int MENU_ID_CLEAR      = (Menu.FIRST + 7);

    private Activity parent = null;  // �e��
	private LogViewUpdater viewUpdater = null;  // ���O��\������N���X
	private LogExporter    logExporter = null;  // ���O�������o���N���X
	private LogClearUpdater clearUpdater = null;  // ���O���N���A����N���X

	
	/**
     *  �R���X�g���N�^
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
    	// 
    	viewUpdater = new LogViewUpdater(parent);
    	logExporter = new LogExporter(parent);
    	clearUpdater = new LogClearUpdater(parent);

        // �X�V�{�^���Ƃ̃����N
        final Button refreshButton = (Button) parent.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);    	
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
    	// ���O��ʂ̍X�V
    	refreshLogView();    
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
        int id = v.getId();
        if (id == R.id.refreshButton)
        {
            // �X�V�{�^���������ꂽ�I
        	refreshLogView();
        }
    }

    /**
     *  �\�����郍�O���X�V����
     * 
     */
    private void refreshLogView()
    {
        viewUpdater.refreshLogData();    	
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
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_EXPORT, Menu.NONE, parent.getString(R.string.save));
    	menuItem.setIcon(android.R.drawable.ic_menu_save);

    	menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
    	menuItem.setIcon(android.R.drawable.ic_menu_preferences);

    	menuItem = menu.add(Menu.NONE, MENU_ID_CLEAR, Menu.NONE, parent.getString(R.string.clear));
    	menuItem.setIcon(android.R.drawable.ic_menu_delete);

    	menuItem = menu.add(Menu.NONE, MENU_ID_TOP, Menu.NONE, parent.getString(R.string.top));
    	menuItem.setIcon(R.drawable.ic_menu_back);

    	menuItem = menu.add(Menu.NONE, MENU_ID_BOTTOM, Menu.NONE, parent.getString(R.string.bottom));
    	menuItem.setIcon(R.drawable.ic_menu_forward);

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
    	menu.findItem(MENU_ID_EXPORT).setVisible(true);
    	menu.findItem(MENU_ID_TOP).setVisible(true);
    	menu.findItem(MENU_ID_BOTTOM).setVisible(true);
    	menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
    	menu.findItem(MENU_ID_CLEAR).setVisible(true);
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
    	  case MENU_ID_PREFERENCES:
    		// �ݒ荀�ڂ̐ݒ�
    	    showPreference();
    		result = true;
    		break;

    	  case MENU_ID_EXPORT:
            // �\�����f�[�^�̃t�@�C���o��
    		logExporter.exportLogData(null);
      		result = true;
      		break;

    	  case MENU_ID_ABOUT:
    		// �A�v���̃N���W�b�g�\��
    	    parent.showDialog(R.id.info_about_gokigen);
      		result = true;
      		break;

    	  case MENU_ID_TOP:
    		// ���O�̐擪�Ɉړ�����
    		moveToTop();
    		result = true;
    		break;
      		
    	  case MENU_ID_BOTTOM:
    		// ���O�̖����Ɉړ�����
    		moveToBottom();
      		result = true;
      		break;	

    	  case MENU_ID_CLEAR:
    		// ���O���N���A����
    		clearLog();
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
            Intent prefIntent = new Intent(parent, jp.sourceforge.gokigen.log.viewer.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        }
    }

    /**
     *  ���O�̐擪�֕\�����ړ�������
     * 
     */
    private void moveToTop()
    {
        ListView listView = (ListView) parent.findViewById(R.id.messageListView);
	    listView.setSelection(0);
    }

    /**
     *  ���O�̖����֕\�����ړ�������
     */
    private void moveToBottom()
    {
        ListView listView = (ListView) parent.findViewById(R.id.messageListView);
	    listView.setSelection(listView.getCount() - 1);    	
    }

    /** 
     * ���O���N���A����
     * 
     *
     */
    private void clearLog()
    {
    	clearUpdater.clearLogData();
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
    	if (id == R.id.detail_dialog)
    	{
    		// �f�[�^�̏ڍׂ��擾
    		DetailDialog dialog = new DetailDialog(parent);
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
    	if (id == R.id.detail_dialog)
    	{
    		// �f�[�^�̏ڍׂ��擾
        	EditText view = (EditText) dialog.findViewById(R.id.detailmessage);
        	if (view != null)
        	{
                view.setText(viewUpdater.getDetailData());
            }
    	}
    }
}
