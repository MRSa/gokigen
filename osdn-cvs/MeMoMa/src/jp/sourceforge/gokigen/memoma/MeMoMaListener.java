package jp.sourceforge.gokigen.memoma;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.SeekBar;

/**
 *   �����܁I �̃��C����ʏ���
 *   
 * @author MRSa
 */
public class MeMoMaListener implements OnClickListener, OnTouchListener, OnKeyListener, IObjectSelectionReceiver, ConfirmationDialog.IResultReceiver, ObjectDataInputDialog.IResultReceiver, ItemSelectionDialog.ISelectionItemReceiver, TextEditDialog.ITextEditResultReceiver, ObjectAligner.IAlignCallback, SelectLineShapeDialog.IResultReceiver
{
	    public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);    // �ݒ��ʂ̕\��
	    public static final int MENU_ID_ABOUT_GOKIGEN = (Menu.FIRST + 2);  // �A�v���P�[�V�����̏��\��
	    public static final int MENU_ID_NEW = (Menu.FIRST + 3);                     // �V�K�쐬
	    public static final int MENU_ID_EXTEND= (Menu.FIRST + 4);                   // �g���@�\
	    public static final int MENU_ID_ALIGN = (Menu.FIRST + 5);                     // �I�u�W�F�N�g�̐���
	    public static final int MENU_ID_INSERT_PICTURE = (Menu.FIRST + 6);   // �摜�̎w��
	    public static final int MENU_ID_OPERATION = (Menu.FIRST + 7);           // ����R�}���h
	    public static final int MENU_ID_RENAME = (Menu.FIRST + 8);                // ���l�[��
	    public static final int MENU_ID_CAPTURE = (Menu.FIRST + 9);              // �摜�̃L���v�`��
	    public static final int MENU_ID_SHARE = (Menu.FIRST + 10);              // �摜�̋��L
	    

	    private Activity parent = null;  // �e��
	    private TextEditDialog editTextDialog = null;   // �e�L�X�g�ҏW�p�_�C�A���O
	    private MeMoMaCanvasDrawer objectDrawer = null; // �摜�̕\��
	    private MeMoMaObjectHolder objectHolder = null;  // �I�u�W�F�N�g�̕ێ��N���X
	    private MeMoMaConnectLineHolder lineHolder =null;  // �I�u�W�F�N�g�Ԃ̐ڑ���ԕێ��N���X
	    //private SelectFeatureListener featureListener = null;  // �@�\�I��p�̃��X�i
	    
	    private MeMoMaDataInOutManager dataInOutManager = null;
	    
	    private OperationModeHolder drawModeHolder = null;
	    private LineStyleHolder lineStyleHolder = null;
	    
	    private ConfirmationDialog confirmationDialog = null;
	    
	    private ObjectDataInputDialog objectDataInputDialog = null;
	    
	    private SelectLineShapeDialog lineSelectionDialog = null;
	    
	    private ItemSelectionDialog itemSelectionDialog = null;
	    private ObjectOperationCommandHolder commandHolder = null;
	    
	    private boolean isEditing = false;
	    private Integer  selectedObjectKey = 0;
	    private Integer  objectKeyToDelete = 0;
	    private Integer selectedContextKey = 0;

	    /**
	     *  �R���X�g���N�^
	     * @param argument
	     */
	    public MeMoMaListener(Activity argument, MeMoMaDataInOutManager inoutManager)
	    {
	        parent = argument;
	        dataInOutManager = inoutManager;
	        lineHolder = new MeMoMaConnectLineHolder();
	        objectHolder = new MeMoMaObjectHolder(argument, lineHolder);
	        editTextDialog = new TextEditDialog(parent, R.drawable.icon);
	        //lineHolder = new MeMoMaConnectLineHolder();
	        //featureListener = new SelectFeatureListener(parent);
	        drawModeHolder = new OperationModeHolder(parent);

	        lineStyleHolder = new LineStyleHolder(parent);
	        lineStyleHolder.prepare();

	        // �V�K�쐬���̊m�F�_�C�A���O�ɂ���
	        confirmationDialog = new ConfirmationDialog(argument);	    	
            confirmationDialog.prepare(this, android.R.drawable.ic_dialog_alert, parent.getString(R.string.createnew_title), parent.getString(R.string.createnew_message));	    	

            // �I�u�W�F�N�g�̃f�[�^���̓_�C�A���O�𐶐�
            objectDataInputDialog = new ObjectDataInputDialog(argument, objectHolder);
            objectDataInputDialog.setResultReceiver(this);
            
            // �ڑ����̌`��Ƒ�����I������_�C�A���O�𐶐�
            lineSelectionDialog = new SelectLineShapeDialog(argument, lineStyleHolder);
            lineSelectionDialog.setResultReceiver(this);
            
            // �A�C�e���I���_�C�A���O�𐶐�
            commandHolder = new ObjectOperationCommandHolder(argument);
            itemSelectionDialog = new ItemSelectionDialog(argument);
            itemSelectionDialog.prepare(this,  commandHolder, parent.getString(R.string.object_operation));
            
	        // �`��N���X�̐���
	        objectDrawer = new MeMoMaCanvasDrawer(argument, objectHolder, lineStyleHolder, this);

	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        	String colorString = preferences.getString("backgroundColor", "0xff004000");
        	objectDrawer.setBackgroundColor(colorString);

	    }

	    /**
	     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
	     * 
	     */
	    public void prepareListener()
	    {
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

	        // �\���ʒu���Z�b�g�{�^��
	        final ImageButton homeButton = (ImageButton) parent.findViewById(R.id.HomeButton);
	        homeButton.setOnClickListener(this);

	        // �g���{�^��
	        final ImageButton expandButton = (ImageButton) parent.findViewById(R.id.ExpandButton);
	        expandButton.setOnClickListener(this);

	        // �쐬�{�^��
	        final ImageButton createObjectButton = (ImageButton) parent.findViewById(R.id.CreateObjectButton);
	        createObjectButton.setOnClickListener(this);

	        final ImageButton deleteObjectButton = (ImageButton) parent.findViewById(R.id.DeleteObjectButton);
            deleteObjectButton.setOnClickListener(this);

	        // ���̌`��؂�ւ��{�^��
	        final ImageButton lineStyleButton = (ImageButton) parent.findViewById(R.id.LineStyleButton);
	        lineStyleButton.setOnClickListener(this);

	        // �f�[�^�ۑ��{�^��
	        final ImageButton saveButton = (ImageButton) parent.findViewById(R.id.SaveButton);
	        saveButton.setOnClickListener(this);

	        // ��ʕ`��N���X
	        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
	        surfaceView.setOnTouchListener(this);
	    		    	
	        // �X���C�h�o�[���������ꂽ���̏���
	        final SeekBar seekbar = (SeekBar) parent.findViewById(R.id.ZoomInOut);
	        seekbar.setOnSeekBarChangeListener(objectDrawer);
	        int progress = preferences.getInt("zoomProgress", 50);
	        seekbar.setProgress(progress);

	        // �u���s���v�̕\��������
	    	parent.setProgressBarIndeterminateVisibility(false);
	    	
            //// �N�����Ƀf�[�^��ǂݏo��	    	
	    	prepareMeMoMaInfo();
	    }

	    /**
	     *  �I������
	     */
	    public void finishListener()
	    {
	    	// �I�����ɏ�Ԃ�ۑ�����
            saveData(true);
	    }

	    /**
	     *  �X�^�[�g����
	     */
	    public void prepareToStart()
	    {
	    	//  �ݒ�ɋL�^����Ă���f�[�^����ʂɔ��f������
	    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

	    	// �`��I�u�W�F�N�g�̌`���ݒ肷��
	        //int objectStyle = Integer.parseInt(preferences.getString("drawStyle", "0"));

	        // ���C���̌`����擾���A�ݒ肷��
	    	setLineStyle();

	        // ���샂�[�h����ʂɔ��f������
	        updateButtons(Integer.parseInt(preferences.getString("operationMode", "0")));

	        // �����ɍ��킹�āA�`��N���X��ύX����
	        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
	        surfaceView.setCanvasDrawer(objectDrawer);

	        // �w�i�摜�i�̖��O�j��ݒ肵�Ă���
	        String backgroundString = preferences.getString("backgroundUri", "");
            objectDrawer.setBackgroundUri(backgroundString);
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
	    	if ((requestCode == MENU_ID_INSERT_PICTURE)&&(resultCode == Activity.RESULT_OK))
	    	{
	            try
	            {
	            	// �擾����uri �� preference�ɋL�^����
	    	    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
	                Uri backgroundUri = data.getData();
	                SharedPreferences.Editor editor = preferences.edit();
	                editor.putString("backgroundUri", backgroundUri.toString());
	                editor.commit();
	                
	                // �w�i�摜�C���[�W�̍X�V����
	            	updateBackgroundImage(backgroundUri.toString());
	                
	          	    System.gc();
	            }
	            catch (Exception ex)
	            {
	                Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.toString() + " " + ex.getMessage());
	            }
	            return;
	    	}
	    	else if (requestCode == MENU_ID_PREFERENCES)
            {
	    		// �w�i�F�A�w�i�摜�̐ݒ���s���B
    	    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            	String colorString = preferences.getString("backgroundColor", "0xff004000");
            	objectDrawer.setBackgroundColor(colorString);

            	// �w�i�摜�C���[�W�̍X�V����
            	String backgroundString = preferences.getString("backgroundUri", "");
            	updateBackgroundImage(backgroundString);

            	Log.v(Main.APP_IDENTIFIER, "RETURENED PREFERENCES " + backgroundString);

            }
	    	else if (requestCode == MENU_ID_EXTEND)
	    	{
	    		// ���̑�...���J���Ă���t�@�C����ǂ݂Ȃ���
	            dataInOutManager.loadFile((String) parent.getTitle());
	    	}
	    	else
	    	{
		    	// ��ʕ\���̏��������s...
		    	//prepareToStart();
		    	return;
	    	}
            // ��ʂ̍ĕ`����w������
       	    redrawSurfaceview();
	    }

	    /**
	     *    �w�i�摜�C���[�W�̍X�V����
	     * 
	     */
	    private void updateBackgroundImage(String uri)
	    {
             // �w�i�摜�C���[�W�̍X�V����	    	
            GokigenSurfaceView graphView = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);

            // �r�b�g�}�b�v��ݒ肷��
            objectDrawer.updateBackgroundBitmap(uri, graphView.getWidth(), graphView.getHeight());

            // ��ʂ̍ĕ`��w��
            graphView.doDraw();
	    }
	    
	    
	    /**
	     *   �N���b�N���ꂽ�Ƃ��̏���
	     */
	    public void onClick(View v)
	    {
	         int id = v.getId();

	         //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onClick() " + id);
	         if (id == R.id.MeMoMaInfo)
	         {
	        	 // �e�L�X�g�ҏW�_�C�A���O��\������
                 showInfoMessageEditDialog();
	         }
	         else if (id == R.id.LineStyleButton)
	         {
	        	 // ���C���`���ς���_�C�A���O�ŕύX����悤�ɕύX����
	        	 selectLineShapeDialog();
	         }
	         else if (id == R.id.ExpandButton)
	         {
	        	 // �g�����j���[���Ăяo��
	        	 callExtendMenu();
	         }
	         else if ((id == R.id.DeleteObjectButton)||(id == R.id.CreateObjectButton))
	         {
	        	 // �폜�{�^�� or �쐬�{�^���������ꂽ���̏���
	        	 updateButtons(drawModeHolder.updateOperationMode(id));
	         }
	         else if (id == R.id.HomeButton)
	         {
	        	 /**  �\���ʒu�����Z�b�g���� **/
	        	 // �\���{���ƕ��s�ړ��ɂ��ă��Z�b�g����
	        	 objectDrawer.resetScaleAndLocation((SeekBar) parent.findViewById(R.id.ZoomInOut));
	        	 
	        	 // ��ʂ̍ĕ`����w������
	        	 redrawSurfaceview();
            }
	        else if (id == R.id.SaveButton)
	        {
	        	// �f�[�^�ۑ����w�����ꂽ�I
	        	saveData(true);
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

	        //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onTouch() " + id);

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
            // �V�K�쐬
	    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_NEW, Menu.NONE, parent.getString(R.string.createnew));
	    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
	        menuItem.setIcon(android.R.drawable.ic_menu_add);    // �ۃv���X

	        // �摜�̋��L
	    	menuItem = menu.add(Menu.NONE, MENU_ID_SHARE, Menu.NONE, parent.getString(R.string.shareContent));
	    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
	    	menuItem.setIcon(android.R.drawable.ic_menu_share);

	    	// �摜�̃L���v�`��
	    	menuItem = menu.add(Menu.NONE, MENU_ID_CAPTURE, Menu.NONE, parent.getString(R.string.capture_data));
	    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
	        menuItem.setIcon(android.R.drawable.ic_menu_crop);    // �I�u�W�F�N�g�̃L���v�`��

	        // �I�u�W�F�N�g�̐���
	    	menuItem = menu.add(Menu.NONE, MENU_ID_ALIGN, Menu.NONE, parent.getString(R.string.align_data));
	    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
	        menuItem.setIcon(android.R.drawable.ic_menu_rotate);    // �I�u�W�F�N�g�̐���

	        // �^�C�g���̕ύX
	    	menuItem = menu.add(Menu.NONE, MENU_ID_RENAME, Menu.NONE, parent.getString(R.string.rename_title));
	    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
	        menuItem.setIcon(android.R.drawable.ic_menu_edit);    // �^�C�g���̕ύX

	        // �ǎ��̑I��
	    	menuItem = menu.add(Menu.NONE, MENU_ID_INSERT_PICTURE, Menu.NONE, parent.getString(R.string.background_data));
	    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
	        menuItem.setIcon(android.R.drawable.ic_menu_gallery);    // �ǎ��̑I��

	        // �g�����j���[
	    	menuItem = menu.add(Menu.NONE, MENU_ID_EXTEND, Menu.NONE, parent.getString(R.string.extend_menu));
	    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
	        menuItem.setIcon(android.R.drawable.ic_menu_share);    // �g�����j���[...

	        // �ݒ�
	        menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
	    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
	    	menuItem.setIcon(android.R.drawable.ic_menu_preferences);

            // �N���W�b�g���̕\��
	        menuItem = menu.add(Menu.NONE, MENU_ID_ABOUT_GOKIGEN, Menu.NONE, parent.getString(R.string.about_gokigen));
	    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
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
	    	menu.findItem(MENU_ID_NEW).setVisible(true);
	    	menu.findItem(MENU_ID_SHARE).setVisible(true);
	    	menu.findItem(MENU_ID_CAPTURE).setVisible(true);
	    	menu.findItem(MENU_ID_ALIGN).setVisible(true);
	    	menu.findItem(MENU_ID_RENAME).setVisible(true);
	    	menu.findItem(MENU_ID_INSERT_PICTURE).setVisible(true);
	    	menu.findItem(MENU_ID_EXTEND).setVisible(true);
	    	menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
	    	menu.findItem(MENU_ID_ABOUT_GOKIGEN).setVisible(true);
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

	    	  case MENU_ID_NEW:
	    		createNewScreen();
	    		result = true;
	    		break;

	    	  case MENU_ID_EXTEND:
	    		// �g�����j���[���Ăяo��
	    		callExtendMenu();
	    		result = true;
	    		break;

	    	  case MENU_ID_ALIGN:
                // �I�u�W�F�N�g�̐�����s��
	    		alignData();
                result = true;
		    	break;

	    	  case MENU_ID_RENAME:
	    		// �^�C�g�����̕ύX  (�e�L�X�g�ҏW�_�C�A���O��\������)
	            showInfoMessageEditDialog();
	    		result = true;
	    		break;

	    	  case MENU_ID_INSERT_PICTURE:
	            // �w�i�摜�̐ݒ���s��
		    	insertPicture();
	            result = true;
			    break;

	    	  case MENU_ID_CAPTURE:
	    		// ��ʃL���v�`�����w�����ꂽ�ꍇ...
	    		doCapture(false);
	    		result = true;
	    		break;

	    	  case MENU_ID_SHARE:
	    		// ��ʃL���v�`�������L���w�����ꂽ�ꍇ...
	    		doCapture(true);
	    		result = true;
	    		break;

	    	  case android.R.id.home:
	    		/** �A�C�R���������ꂽ���̏���... **/
		        // �e�L�X�g�ҏW�_�C�A���O��\������
	            showInfoMessageEditDialog();
			    result = true;
			    break;

	    	  default:
	    		result = false;
	    		break;
	    	}
	    	return (result);
	    }

	    /**
	     *  �摜�t�@�C���̑}�� (�f�[�^�t�@�C���̍X�V)
	     * 
	     */
	    private void insertPicture()
	    {
	    	Intent intent = new Intent();
	    	intent.setType("image/*");
	    	intent.setAction(Intent.ACTION_GET_CONTENT);
	        parent.startActivityForResult(intent, MENU_ID_INSERT_PICTURE);
	    }

	    /**
	     *    ��ʃL���v�`���̎��{
	     * 
	     * @param isShare
	     */
	    private void doCapture(boolean isShare)
	    {
	    	// ��ʂ̃X�N���[���V���b�g���Ƃ鏈�������s����
	    	dataInOutManager.doScreenCapture((String) parent.getTitle(), objectHolder, objectDrawer, isShare);

        	// ��ʂ��ĕ`�悷��
            redrawSurfaceview();
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
	     *   �g�����j���[���Ăяo��
	     * 
	     */
	    private void callExtendMenu()
	    {
	    	// ���ݕ\�����̃f�[�^���t�@�C���ɕۑ�����
	    	dataInOutManager.saveFile((String) parent.getTitle(), true);
	    	
	    	// ���ݓǂݍ���ł���t�@�C���̃t�@�C�����𐶐�����
	    	String fullPath = dataInOutManager.getDataFileFullPath((String) parent.getTitle(), ".xml");
	    	
            //  �����Ŋg�����j���[���Ăяo��	    	
	        // (�n���f�[�^������� Intent�Ƃ���)
	        Intent intent = new Intent();
	        
	        intent.setAction(ExtensionActivity.MEMOMA_EXTENSION_LAUNCH_ACTIVITY);
	        intent.putExtra(ExtensionActivity.MEMOMA_EXTENSION_DATA_FULLPATH, fullPath);
	        intent.putExtra(ExtensionActivity.MEMOMA_EXTENSION_DATA_TITLE, (String) parent.getTitle());

	        // �f�[�^�\���pActivity���N������
	        parent.startActivityForResult(intent, MENU_ID_EXTEND);
	    }

	    /**
	     *    �f�[�^�̓ǂݍ��݂��s��
	     * 
	     */
	    private void prepareMeMoMaInfo()
	    {
	    	//  �ݒ�ɋL�^����Ă���f�[�^����ʂ̃^�C�g���ɔ��f������
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
	    	String memomaInfo = preferences.getString("MeMoMaInfo", parent.getString(R.string.app_name));
	    	parent.setTitle(memomaInfo);

	        // �A�N�V�����o�[�ƃt�@�C�����̏���
	        final ActionBar bar = parent.getActionBar();
	        dataInOutManager.prepare(objectHolder, bar, memomaInfo);

            //dataInOutManager.loadFile((String) parent.getTitle());
	    }

	    /**
	     *   �f�[�^�̕ۑ����s��
	     *   
	     *   
	     *   @param forceOverwrite  true�̎��́A�t�@�C�������m�肵�Ă����Ƃ��́i�m�F�����Ɂj�㏑���ۑ��������ōs���B
	     *   
	     */
	    private void saveData(boolean forceOverwrite)
	    {
            dataInOutManager.saveFile((String) parent.getTitle(), forceOverwrite);
	    }

	    /**
	     *   �f�[�^�̐�����s��
	     * 
	     */
	    private void alignData()
	    {
	    	ObjectAligner aligner = new ObjectAligner(parent, this);
	    	aligner.execute(objectHolder);
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
	     *   �V�K�쐬���w�����ꂽ�Ƃ�...�S���N���A���č��Ȃ����ėǂ����m�F����B
	     * 
	     */
	    private void createNewScreen()
	    {
	    	parent.showDialog(R.id.confirmation);
	    }
	    
	    /**
	     *    �ڑ����̐ݒ�_�C�A���O��\������
	     */
	    private void selectLineShapeDialog()
	    {
	    	// �ڑ����̐ݒ�_�C�A���O��\������...
	    	parent.showDialog(R.id.selectline_dialog);
	    }

	    /**
	     *    ���b�Z�[�W�ҏW�_�C�A���O�̕\������������
	     * 
	     */
	    private void prepareInfoMessageEditDialog(Dialog dialog)
	    {
	    	String message = (String) parent.getTitle();
         	editTextDialog.prepare(dialog, this, parent.getString(R.string.dataTitle), message, true);
	    }

	    /**
	     *    ���b�Z�[�W�ҏW�_�C�A���O�̕\������������
	     * 
	     */
	    private void prepareConfirmationDialog(Dialog dialog)
	    {
    		// Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::prepareConfirmationDialog() " );
	    }

	    /**
	     *    �I�u�W�F�N�g���͗p�_�C�A���O�̕\������������
	     * 
	     */
	    private void prepareObjectInputDialog(Dialog dialog)
	    {
    		Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::prepareObjectInputDialog(), key: " + selectedObjectKey);
    		
    		//  �_�C�A���O�̏������s��
    		objectDataInputDialog.prepareObjectInputDialog(dialog, selectedObjectKey);
    		
	    }

	    /**
	     *   �A�C�e���I���_�C�A���O�̕\������������
	     * 
	     * @param dialog
	     */
	    private void prepareItemSelectionDialog(Dialog dialog)
	    {
	    	// �A�C�e���I���_�C�A���O�̕\���ݒ�
	    	// (���I�ύX���B�B�B����͌Œ�Ȃ̂ŉ������Ȃ��j
	    }

	    /**
	     *    �ڑ����I��p�_�C�A���O�̕\������������
	     * 
	     */
	    private void prepareLineSelectionDialog(Dialog dialog)
	    {
    		Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::prepareLineSelectionDialog(), key: " + selectedObjectKey);
    		
    		//  �_�C�A���O�̏������s��
    		lineSelectionDialog.prepareSelectLineShapeDialog(dialog, selectedObjectKey);
	    }

	    /**
	     *  �ݒ��ʂ�\�����鏈��
	     */
	    private void showPreference()
	    {
	        try
	        {
	            // �ݒ��ʂ��Ăяo��
	            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.memoma.Preference.class);
	            parent.startActivityForResult(prefIntent, MENU_ID_PREFERENCES);
	        }
	        catch (Exception e)
	        {
	             // ��O����...�Ȃɂ����Ȃ��B
	        	 //updater.showMessage("ERROR", MainUpdater.SHOWMETHOD_DONTCARE);
	        }
	    }

        /**
         *    �ڑ����̌`��𔽉f������
         * 
         */
	    private void setLineStyle()
	    {
	    	int buttonId = LineStyleHolder.getLineShapeImageId(lineStyleHolder.getLineStyle(), lineStyleHolder.getLineShape());
	        final ImageButton lineStyleObj = (ImageButton) parent.findViewById(R.id.LineStyleButton);
	        lineStyleObj.setImageResource(buttonId);	
	    }

	    /**
	     *    �I�u�W�F�N�g���������ꂽ�I
	     * 
	     */
	    public void objectCreated()
	    {
      	     // �����œ��샂�[�h���ړ����[�h�ɖ߂��B
    		 drawModeHolder.changeOperationMode(OperationModeHolder.OPERATIONMODE_MOVE);
    		 updateButtons(OperationModeHolder.OPERATIONMODE_MOVE);

       	   // ��ʂ��ĕ`�悷��
       	   redrawSurfaceview();
	    }

	    /**
	     *    �󂫗̈悪�^�b�`���ꂽ�I
	     * 
	     */
	    public int touchedVacantArea()
	    {
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
	    	return (Integer.parseInt(preferences.getString("operationMode", "0")));
	    }

	    /**
	     *    �󂫗̈�Ń^�b�`�������ꂽ�I
	     * 
	     */
	    public int touchUppedVacantArea()
	    {
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
	    	return (Integer.parseInt(preferences.getString("operationMode", "0")));
	    }

	    /**
	     *    �I�u�W�F�N�g��{���ɍ폜���ėǂ����m�F������ɁA�I�u�W�F�N�g���폜����B
	     * 
	     * @param key
	     */
	    private void removeObject(Integer key)
	    {
	    	// �{���ɏ����ėǂ����A�m�F������_�C�A���O��\�����āAOK�������ꂽ������B
	    	 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent);
	    	 alertDialogBuilder.setTitle(parent.getString(R.string.deleteconfirm_title));
	    	 alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
	    	 alertDialogBuilder.setMessage(parent.getString(R.string.deleteconfirm_message));

	    	 // �폜����I�u�W�F�N�g�̃L�[���o�����ށB
	    	 objectKeyToDelete = key;

	    	 // OK�{�^���̐���
	    	 alertDialogBuilder.setPositiveButton(parent.getString(R.string.confirmYes), new DialogInterface.OnClickListener()
             {
                  public void onClick(DialogInterface dialog, int id)
                  {
               		    //  �폜���[�h�̎�... �m�F��폜�����ǁA���͊m�F�Ȃ��ō폜���s���B
                		objectHolder.removePosition(objectKeyToDelete);
                		
                		// �폜����I�u�W�F�N�g�ɐڑ�����Ă���������ׂč폜����
                		objectHolder.getConnectLineHolder().removeAllConnection(objectKeyToDelete);
                		
                		// �_�C�A���O�����
                 	   dialog.dismiss();

                 	   // �����œ��샂�[�h���폜���[�h����ړ����[�h�ɖ߂��B
       	    		   drawModeHolder.changeOperationMode(OperationModeHolder.OPERATIONMODE_MOVE);
       	    		   updateButtons(OperationModeHolder.OPERATIONMODE_MOVE);
       	    		

                 	   // ��ʂ��ĕ`�悷��
                 	   redrawSurfaceview();
                  }
              });
	    	 
	    	 // Cancel�{�^���̐���
	    	 alertDialogBuilder.setNegativeButton(parent.getString(R.string.confirmNo), new DialogInterface.OnClickListener()
             {
                 public void onClick(DialogInterface dialog, int id)
                 {
                	 dialog.cancel();
                 }
             });
    		
	         // �_�C�A���O�̓L�����Z���\�ɐݒ肷��
	         alertDialogBuilder.setCancelable(true);

	         // �_�C�A���O��\������
	         AlertDialog alertDialog = alertDialogBuilder.create();
	         alertDialog.show();

	         return;
	    }

	    /**
	     *    �I�u�W�F�N�g�𕡐�����
	     * 
	     * @param key
	     */
	    private void duplicateObject(Integer key)
	    {
	    	// �I�𒆃I�u�W�F�N�g�𕡐�����
	        objectHolder.duplicatePosition(key);

	        // ��ʂ��ĕ`�悷��
      	   redrawSurfaceview();
	    }	    

	    /**
	     *    �I�u�W�F�N�g���g�傷��
	     * 
	     * @param key
	     */
	    private void expandObject(Integer key)
	    {
	    	// �I�𒆃I�u�W�F�N�g���g�傷��
	        objectHolder.expandObjectSize(key);

	        // ��ʂ��ĕ`�悷��
      	   redrawSurfaceview();
	    }	    
	    /**
	     *    �I�u�W�F�N�g���k������
	     * 
	     * @param key
	     */
	    private void shrinkObject(Integer key)
	    {
	    	// �I�𒆃I�u�W�F�N�g���k������
	        objectHolder.shrinkObjectSize(key);

	        // ��ʂ��ĕ`�悷��
      	   redrawSurfaceview();
	    }

	     private void setButtonBorder(ImageButton button, boolean isHighlight)
	      {
	      	try
	      	{
	              BitmapDrawable btnBackgroundShape = (BitmapDrawable)button.getBackground();
	              if (isHighlight == true)
	              {
//	                 	btnBackgroundShape.setColorFilter(Color.rgb(51, 181, 229), Mode.LIGHTEN);
	              	btnBackgroundShape.setColorFilter(Color.BLUE, Mode.LIGHTEN);
	              }
	              else
	              {
	              	btnBackgroundShape.setColorFilter(Color.BLACK, Mode.LIGHTEN);
	              } 
	      	}
	      	catch (Exception ex)
	      	{
	      		// 
	      		Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::setButtonBorder(): " + ex.toString());
	      	}
	      }	     

	     /**
	     *   �{�^�����X�V����
	     * 
	     */
	    private void updateButtons(int mode)
	    {
	        final ImageButton createObjectButton = (ImageButton) parent.findViewById(R.id.CreateObjectButton);
	        final ImageButton deleteObjectButton = (ImageButton) parent.findViewById(R.id.DeleteObjectButton);

	        if (mode == OperationModeHolder.OPERATIONMODE_DELETE)
	        {
	        	setButtonBorder(createObjectButton, false);
	        	setButtonBorder(deleteObjectButton, true);
	        }
	        else if (mode == OperationModeHolder.OPERATIONMODE_CREATE)
	        {
	        	setButtonBorder(createObjectButton, true);
	        	setButtonBorder(deleteObjectButton, false);	    		
	        }
	        else // if (mode == OperationModeHolder.OPERATIONMODE_MOVE)
	    	{
	        	setButtonBorder(createObjectButton, false);
	        	setButtonBorder(deleteObjectButton, false);	    		
	    	}
	    }

	    
	    /**
	     *   �I�u�W�F�N�g���I�����ꂽ�i�������ŁI�j
	     * 
	     */
	    public void objectSelectedContext(Integer key)
	    {
	    	Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::objectSelectedContext(),  key:" + key);
	    	selectedContextKey = key;

	    	// �I�u�W�F�N�g�̃A�C�e���I���_�C�A���O��\������...
	    	parent.showDialog(MENU_ID_OPERATION);

	    }
	    
	    
	    /**
	     *   �I�u�W�F�N�g���I�����ꂽ�I
	     * 
	     */
	    public boolean objectSelected(Integer key)
	    {
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
	    	int operationMode = Integer.parseInt(preferences.getString("operationMode", "0"));
	    	if (operationMode == OperationModeHolder.OPERATIONMODE_DELETE)
	    	{
	    		// �I�u�W�F�N�g���폜����
	    		removeObject(key);
	    		
	    		return (true);
	    	}
	    	//if ((operationMode == OperationModeHolder.OPERATIONMODE_MOVE)||
	    	//		(operationMode == OperationModeHolder.OPERATIONMODE_CREATE))
	    	{
	    		// �I�����ꂽ�I�u�W�F�N�g���L������
		    	selectedObjectKey = key;
		    	Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::objectSelected() key : " + key);

		    	// �I�u�W�F�N�g�̏ڍאݒ�_�C�A���O��\������...
		    	parent.showDialog(R.id.objectinput_dialog);
	    	}
	    	return (true);
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
            if (id == R.id.confirmation)
            {
            	// �m�F���郁�b�Z�[�W��\������
            	return (confirmationDialog.getDialog());
            }
            if (id == R.id.objectinput_dialog)
            {
            	// �I�u�W�F�N�g���͂̃_�C�A���O��\������
            	return (objectDataInputDialog.getDialog());
            }
            if (id == MENU_ID_OPERATION)
            {
            	// �A�C�e���I���_�C�A���O�̏������s��
            	return (itemSelectionDialog.getDialog());
            }
            if (id == R.id.selectline_dialog)
            {
            	// �ڑ����I���_�C�A���O�̏������s��
            	return (lineSelectionDialog.getDialog());
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
            if (id == R.id.confirmation)
            {
            	// �m�F�_�C�A���O��\������B
            	prepareConfirmationDialog(dialog);
            	return;
            }
            if (id == R.id.objectinput_dialog)
            {
            	// �I�u�W�F�N�g���͂̃_�C�A���O��\������
            	prepareObjectInputDialog(dialog);
            }
            if (id == MENU_ID_OPERATION)
            {
            	// �I�u�W�F�N�g����I���̃_�C�A���O��\������
            	prepareItemSelectionDialog(dialog);
            }
            if (id == R.id.selectline_dialog)
            {
            	// �ڑ����I���̃_�C�A���O��\������
            	prepareLineSelectionDialog(dialog);
            }
	    }

        /**
         *    �V�K��ԂɕύX����B
         * 
         */
        public void acceptConfirmation()
        {
            //
        	Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::acceptConfirmation()");
        	
        	// �I�u�W�F�N�g�f�[�^���N���A����B
    	    objectHolder.removeAllPositions();  // �I�u�W�F�N�g�̕ێ��N���X
    	    objectHolder.getConnectLineHolder().removeAllLines();  // �I�u�W�F�N�g�Ԃ̐ڑ���ԕێ��N���X

        	// ��ʂ̔{���ƕ\���ʒu��������Ԃɖ߂�
        	if (objectDrawer != null)
        	{
    	        final SeekBar zoomBar = (SeekBar) parent.findViewById(R.id.ZoomInOut);
        		objectDrawer.resetScaleAndLocation(zoomBar);
        	}

        	/**
        	// �薼�� "����"�ɕύX���A�֌W�����N���A����
        	String newName = parent.getString(R.string.no_name);
        	parent.setTitle(newName);
	    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("MeMoMaInfo", newName);
            editor.commit();
            **/
            
        	// ��ʂ��ĕ`�悷��
            redrawSurfaceview();

            // �t�@�C�����I���_�C�A���O���J��
            showInfoMessageEditDialog();

        }

        /**
         *   ��ʂ��ĕ`�悷��
         * 
         */
        private void redrawSurfaceview()
        {
        	final GokigenSurfaceView surfaceview = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        	surfaceview.doDraw();        	
        }
        
        /**
         *    �s���B�������Ȃ��B
         * 
         */
        public  void rejectConfirmation()
        {
    		Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::rejectConfirmation()");
        }

        /**
         *   �I�u�W�F�N�g�����񂳂ꂽ���̏���
         * 
         */
        public void objectAligned()
        {
            // ��ʂ̍ĕ`����w������
       	    redrawSurfaceview();        	
        }
        
        /**
         *   �I�u�W�F�N�g�ҏW�_�C�A���O������ꂽ���̏���
         * 
         */
        public void finishObjectInput()
        {
            // ��ʂ̍ĕ`����w������
       	    redrawSurfaceview();
        }
        
        /**
         *   �I�u�W�F�N�g�ҏW�_�C�A���O������ꂽ���̏���
         * 
         */
        public void cancelObjectInput()
        {
            // �������Ȃ�	
        }
        
        
        /**
         *   ���ݕҏW�����ǂ�����m��
         * 
         * @return
         */
        public boolean isEditing()
        {
        	return (isEditing);
        }

        /**
         *   ���ݕҏW���̃t���O���X�V����
         * 
         * @param value
         */
        public void setIsEditing(boolean value)
        {
        	isEditing = value;
        }

        /**
         *   �A�C�e�����I�����ꂽ�I
         * 
         */
        public void itemSelected(int index, String itemValue)
        {
            //
        	Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::itemSelected() : " + itemValue + " [" + index + "]");
        	
        	if (index == ObjectOperationCommandHolder.OBJECTOPERATION_DELETE)
        	{
        		// �I�u�W�F�N�g�폜�̊m�F
	    		removeObject(selectedContextKey);
        	}
        	else if (index == ObjectOperationCommandHolder.OBJECTOPERATION_DUPLICATE)
        	{
        		// �I�u�W�F�N�g�̕���
        		duplicateObject(selectedContextKey);        		
        	}
        	else if (index == ObjectOperationCommandHolder.OBJECTOPERATION_SIZEBIGGER)
        	{
        		// �I�u�W�F�N�g�̊g��
        		expandObject(selectedContextKey);
        	}
        	else if (index == ObjectOperationCommandHolder.OBJECTOPERATION_SIZESMALLER)
        	{
        		// �I�u�W�F�N�g�̏k��
        		shrinkObject(selectedContextKey);
        	}
        }

        /**
         *    (���񖢎g�p)
         * 
         */
        public void itemSelectedMulti(String[] items, boolean[] status)
        {
        	
        }
        public void canceledSelection()
        {
        	
        }
        
        public void onSaveInstanceState(Bundle outState)
        {
    	    /* �����ŏ�Ԃ�ۑ� */ 
    	    //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onSaveInstanceState()");
        }
        
        public void onRestoreInstanceState(Bundle savedInstanceState)
        {
        	/* �����ŏ�Ԃ𕜌� */
    	    Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::onRestoreInstanceState()");
        }

        public boolean finishTextEditDialog(String message)
        {
        	if ((message == null)||(message.length() == 0))
        	{
                // �f�[�^�����͂���Ă��Ȃ������̂ŁA�������Ȃ��B
        		return (false);
        	}
        	
        	// ��������L�^
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("MeMoMaInfo", message);
            editor.commit();

            // �^�C�g���ɐݒ�
            parent.setTitle(message);

            // �ۑ��V�[�P���X����x���点�� 
            saveData(true);

            // �t�@�C���I�����X�g�̍X�V
            dataInOutManager.updateFileList(message, parent.getActionBar());

            return (true);
        }
        public boolean cancelTextEditDialog()
        {
            return (false);
        }

        /**
         *    �ڑ���
         * 
         */
        public void finishSelectLineShape(int style, int shape, int thickness)
        {
        	int buttonId = LineStyleHolder.getLineShapeImageId(style, shape);
            final ImageButton lineStyleObj = (ImageButton) parent.findViewById(R.id.LineStyleButton);
            lineStyleObj.setImageResource(buttonId);	
            //Log.v(Main.APP_IDENTIFIER, "MeMoMaListener::finishSelectLineShape() buttonId:" + buttonId);
        }

        /**
         * 
         * 
         */
        public void cancelSelectLineShape()
        {
        	
        }
}
