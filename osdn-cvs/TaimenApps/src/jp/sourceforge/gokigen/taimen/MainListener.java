package jp.sourceforge.gokigen.taimen;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 *   �����܁I �̃��C����ʏ���
 *   
 * @author MRSa
 */
public class MainListener implements OnClickListener, OnTouchListener, OnKeyListener
{
	    public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);    // �ݒ��ʂ̕\��
	    public static final int MENU_ID_ABOUT_GOKIGEN = (Menu.FIRST + 2);  // �A�v���P�[�V�����̏��\��

	    private Activity parent = null;  // �e��
	    private TextEditDialog editTextDialog = null;   // �e�L�X�g�ҏW�p�_�C�A���O
	    private SelectFeatureListener featureListener = null;  // �@�\�I��p�̃��X�i
	    private TaimenCanvasDrawer canvasDrawer = null; // �摜�̕\��
	    private TaimenMultiTouchCanvasDrawer multiDrawer = null;  // �摜�̕\��
			
	    /**
	     *  �R���X�g���N�^
	     * @param argument
	     */
	    public MainListener(Activity argument)
	    {
	        parent = argument;
	        editTextDialog = new TextEditDialog(parent);
	        featureListener = new SelectFeatureListener(parent);
	        canvasDrawer = new TaimenCanvasDrawer();
	        multiDrawer = new TaimenMultiTouchCanvasDrawer();
	    }

	    /**
	     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
	     * 
	     */
	    public void prepareListener()
	    {
	    	// �@�\�I���{�^��
	        final ImageButton selectButton = (ImageButton) parent.findViewById(R.id.SelectButton);
	        selectButton.setOnClickListener(featureListener);

            // ���e�L�X�g
	        final TextView infoText = (TextView) parent.findViewById(R.id.MeMoMaInfo);
	        infoText.setOnClickListener(this);
	        
	        // ��ʕ`��N���X
	        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
	        surfaceView.setCanvasDrawer(canvasDrawer);
	        surfaceView.setOnTouchListener(this);
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
	        // ��ʕ`��N���X
	        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
	        surfaceView.setCanvasDrawer(canvasDrawer);

	    	//  �ǂ������\�����邩���擾����
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
	    	boolean useMultiTouch = preferences.getBoolean("useMultiTouchAPI", false);
	    	if (useMultiTouch == true)
	    	{
		        surfaceView.setCanvasDrawer(multiDrawer);	    		
	    	}
	    	else
	    	{
		        surfaceView.setCanvasDrawer(canvasDrawer);	    		
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

	    }

	    /**
	     *   �N���b�N���ꂽ�Ƃ��̏���
	     */
	    public void onClick(View v)
	    {
	         int id = v.getId();
	    	//Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onClick() ");
	         if (id == R.id.MeMoMaInfo)
	         {
	        	 // �e�L�X�g�ҏW�_�C�A���O��\������
                 showInfoMessageEditDialog();
	         }
	    }

	    /**
	     *   �G��ꂽ�Ƃ��̏���
	     * 
	     */
	    public boolean onTouch(View v, MotionEvent event)
	    {
	        int id = v.getId();
	        // int action = event.getAction();

	        //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onTouch() ");

	        if (id == R.id.GraphicView)
	        {
	        	// ��ʂ��^�b�`�����I
	            ((GokigenSurfaceView) v).onTouchEvent(event);
	            return (true);
	        }
	        
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

	        Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onKey() ");
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
	     *    ���b�Z�[�W�ҏW�_�C�A���O��\������
	     * 
	     */
	    private void showInfoMessageEditDialog()
	    {
	    	parent.showDialog(R.id.editTextArea);        	    	
	    }

	    /**
	     *    ���b�Z�[�W�ҏW�_�C�A���O�̕\������������
	     * 
	     */
	    private void prepareInfoMessageEditDialog(Dialog dialog)
	    {
       		TextView txtArea = (TextView) parent.findViewById(R.id.MeMoMaInfo);
         	editTextDialog.prepare((TextEditDialog.ITextEditResultReceiver) new TextEditReceiver(parent, "MeMoMaInfo", R.id.MeMoMaInfo), 0, "", (String) txtArea.getText());
	    }
	    
	    /**
	     *  �ݒ��ʂ�\�����鏈��
	     */
	    private void showPreference()
	    {
	        try
	        {
	            // �ݒ��ʂ��Ăяo��
	            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.taimen.Preference.class);
	            parent.startActivityForResult(prefIntent, 0);
	        }
	        catch (Exception e)
	        {
	             // ��O����...�Ȃɂ����Ȃ��B
	        	 //updater.showMessage("ERROR", MainUpdater.SHOWMETHOD_DONTCARE);
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
            if (id == R.id.editTextArea)
            {
        		// �ύX����e�L�X�g��\��
                return (editTextDialog.getDialog());
            }
	    	return (null);
	    }

	    /**
	     *  �_�C�A���O�\���̏���
	     * 
	     */
	    public void onPrepareDialog(int id, Dialog dialog)
	    {
            if (id == R.id.editTextArea)
            {
            	// �ύX����f�[�^��\������
            	prepareInfoMessageEditDialog(dialog);
            	return;
            }    	
	    }
}
