package jp.sourceforge.gokigen.memoma;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 *    ���X�g�`���ŕ\���E�G�N�X�|�[�g
 * 
 * @author MRSa
 *
 */
public class ExtensionActivityListener  implements OnClickListener, MeMoMaFileLoadingProcess.IResultReceiver, MeMoMaFileExportCsvProcess.IResultReceiver, FileSelectionDialog.IResultReceiver, MeMoMaFileImportCsvProcess.IResultReceiver
{
    public final int MENU_ID_EXPORT= (Menu.FIRST + 1);
    public final int MENU_ID_SHARE = (Menu.FIRST + 2);
    public final int MENU_ID_IMPORT = (Menu.FIRST + 3);

    private final String EXTENSION_DIRECTORY = "/exported";
    
    private ExternalStorageFileUtility fileUtility = null;
	private MeMoMaObjectHolder objectHolder = null;
	private FileSelectionDialog fileSelectionDialog = null;
	
	private boolean isShareExportedData = false;

	private List<SymbolListArrayItem> listItems = null;
    
    private Activity parent = null;  // �e��
	
	/**
     *  �R���X�g���N�^
     * @param argument
     */
    public ExtensionActivityListener(Activity argument)
    {
        parent = argument;
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);
        objectHolder = new MeMoMaObjectHolder(parent, new MeMoMaConnectLineHolder());
    }
    /**
     *  �N�����Ƀf�[�^����������
     * 
     * @param myIntent
     */
    public void prepareExtraDatas(Intent myIntent)
    {
        try
        {
            // Intent�ŏE�����f�[�^��ǂݏo�� (�������f�[�^)
        	//fullPath = myIntent.getStringExtra(ExtensionActivity.MEMOMA_EXTENSION_DATA_FULLPATH);
        	objectHolder.setDataTitle(myIntent.getStringExtra(ExtensionActivity.MEMOMA_EXTENSION_DATA_TITLE));

            // Preference�ɋL�����ꂽ�f�[�^������΂�����擾����
            // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
         }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "Exception :" + ex.toString());
        }        
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
        // �t�B���^�ݒ�{�^��
        final ImageButton filterButton = (ImageButton) parent.findViewById(R.id.SetFilterButton);
        filterButton.setOnClickListener(this);

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
		Log.v(Main.APP_IDENTIFIER, "ExtensionActivityListener::prepareToStart() : "  + objectHolder.getDataTitle());

		// �t�@�C�������[�h����I
		// (AsyncTask���g���ăf�[�^��ǂݍ���)
		MeMoMaFileLoadingProcess asyncTask = new MeMoMaFileLoadingProcess(parent, fileUtility, this);
        asyncTask.execute(objectHolder);
    }

    /**
     *    �ڍ׃f�[�^��\������B
     * 
     * @param title
     * @param url
     */
    private void showDetailData(String first, String second, String third)
    {
        Log.v(Main.APP_IDENTIFIER, "SELECTED: " + first + " " + second + " " + third);
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
        // �Ȃɂ����Ȃ�...
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.SetFilterButton)
        {
        	 // �t�B���^�ݒ�{�^���������ꂽ�I
        }
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
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_SHARE, Menu.NONE, parent.getString(R.string.export_csv));
    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
    	menuItem.setIcon(android.R.drawable.ic_menu_share);

    	menuItem = menu.add(Menu.NONE, MENU_ID_EXPORT, Menu.NONE, parent.getString(R.string.shareContent));
    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);   /*  for Android 3.1  */
    	menuItem.setIcon(android.R.drawable.ic_menu_save);

    	menuItem = menu.add(Menu.NONE, MENU_ID_IMPORT, Menu.NONE, parent.getString(R.string.import_csv));
    	menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);   /*  for Android 3.1  */
    	menuItem.setIcon(android.R.drawable.ic_menu_edit);

    	return (menu);
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
    	menu.findItem(MENU_ID_SHARE).setVisible(true);
    	menu.findItem(MENU_ID_EXPORT).setVisible(true);
    	menu.findItem(MENU_ID_IMPORT).setVisible(true);

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
          case MENU_ID_EXPORT:
            // �\�����f�[�^�̃G�N�X�|�[�g
  		    export_as_csv(false);
    		result = true;
    		break;

          case MENU_ID_SHARE:
        	export_as_csv(true);
        	result = true;
        	break;

          case MENU_ID_IMPORT:
        	// �f�[�^�̃C���|�[�g
        	importObjectFromCsv();
        	result = true;
        	break;

    	  default:
    		result = false;
    		break;
    	}
    	return (result);
    }

    /**
     *   CSV�`���Ńf�[�^���C���|�[�g����
     * 
     */
    private void importObjectFromCsv()
    {
    	// �f�[�^�̃C���|�[�g
    	parent.showDialog(R.id.listdialog);    	
    }

    /**
     *   �f�[�^��CSV�`���ŏo�͂���
     * 
     */
    private void export_as_csv(boolean isShare)
    {
    	isShareExportedData = isShare;

    	// AsyncTask���g���ăf�[�^���G�N�X�|�[�g����
    	MeMoMaFileExportCsvProcess asyncTask = new MeMoMaFileExportCsvProcess(parent, fileUtility, this);
        asyncTask.execute(objectHolder);
    }
    
    /**
     *  �_�C�A���O�̐���
     * 
     */
    public Dialog onCreateDialog(int id)
    {
    	if (id == R.id.listdialog)
    	{
    		fileSelectionDialog = new FileSelectionDialog(parent, parent.getString(R.string.dialogtitle_selectcsv), fileUtility, ".csv",  this);
    		return (fileSelectionDialog.getDialog());
    	}

/**
    	if (id == R.id.info_about_gokigen)
    	{
    		CreditDialog dialog = new CreditDialog(parent);
    		return (dialog.getDialog());
    	}
**/
   	    return (null);
    }

    /**
     *    �t�@�C���I���_�C�A���O�̕\������������
     * 
     */
    private void prepareFileSelectionDialog(Dialog dialog)
    {
    	fileSelectionDialog.prepare("", EXTENSION_DIRECTORY);
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
        if (id == R.id.listdialog)
        {
        	// CSV�C���|�[�g�_�C�A���O����������
        	prepareFileSelectionDialog(dialog);
        	return;
        }
    }

    /**
     *    �t�@�C�������[�h����r���̃o�b�N�O���E���h����...
     * 
     */
	public void onLoadingProcess()
	{
		try
		{
	        // ���X�g�ɕ\������A�C�e���𐶐�����
	        listItems = null;
	        listItems = new ArrayList<SymbolListArrayItem>();

	        // TODO: ���炩�̖@���ɏ]���ĕ��בւ�������B

	        Enumeration<Integer> keys = objectHolder.getObjectKeys();
	        while (keys.hasMoreElements())
	        {
	            Integer key = keys.nextElement();
	            MeMoMaObjectHolder.PositionObject pos = objectHolder.getPosition(key);

	            // �A�C�R���̌���
	            int objectStyleIcon = MeMoMaObjectHolder .getObjectDrawStyleIcon(pos.drawStyle);
	            
	            // ���[�U�`�F�b�N�̗L���\��
	            int userCheckedIcon = (pos.userChecked == true) ? R.drawable.btn_checked : R.drawable.btn_notchecked;

	            // TODO: �A�C�e���I�����̏��G���A��(ArrayItem���ɂ�)�p�ӂ��Ă��邪���g�p�B
	            SymbolListArrayItem listItem = new SymbolListArrayItem(userCheckedIcon, pos.label, pos.detail, "", objectStyleIcon);

	            listItems.add(listItem);
	        }
	    } catch (Exception ex)
	    {
	        // ��O����...���O��f��
	    	Log.v(Main.APP_IDENTIFIER, "ExtensionActivityListener::onLoadingProcess() : " + ex.toString());
	    }	
	}

    /**
     *    �t�@�C���̃��[�h���ʂ��󂯎��
     * 
     */
    public void onLoadedResult(String detail)
    {
		Log.v(Main.APP_IDENTIFIER, "ExtensionActivityListener::onLoadedResult() '"  + objectHolder.getDataTitle() +"' : " + detail);

		// �ǂݍ��񂾃t�@�C�������^�C�g���ɐݒ肷��
		parent.setTitle(objectHolder.getDataTitle());
		
		// �I�u�W�F�N�g�ꗗ��\������
		updateObjectList();
		
		// �ǂݍ��݂������Ƃ�`�B����
		//String outputMessage = parent.getString(R.string.load_data) + " " + objectHolder.getDataTitle() + " " + detail;
        //Toast.makeText(parent, outputMessage, Toast.LENGTH_SHORT).show();    	
    }

    /**
     *    �t�@�C���̃G�N�X�|�[�g���ʂ��󂯎��
     * 
     */
    public void onExportedResult(String exportedFileName, String detail)
    {
		Log.v(Main.APP_IDENTIFIER, "ExtensionActivityListener::onExportedResult() '"  + objectHolder.getDataTitle() +"' : " + detail);

		// �G�N�X�|�[�g�������Ƃ�`�B����
		String outputMessage = parent.getString(R.string.export_csv) + " " + objectHolder.getDataTitle() + " " + detail;
        Toast.makeText(parent, outputMessage, Toast.LENGTH_SHORT).show();
        
        if (isShareExportedData == true)
        {
        	// �G�N�X�|�[�g�����t�@�C�������L����
            shareContent(exportedFileName);
        }
    	isShareExportedData = false;
    }
    
    /**
     *    �I�u�W�F�N�g�ꗗ���X�V����
     */
    private void updateObjectList()
    {
    	try
    	{
    		// ���X�g�A�_�v�^�[�𐶐����A�ݒ肷��
            ListView listView = (ListView) parent.findViewById(R.id.ExtensionView);
            ListAdapter adapter = new SymbolListArrayAdapter(parent,  R.layout.listarrayitems, listItems);
            listView.setAdapter(adapter);

            // �A�C�e����I�������Ƃ��̏���
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //@Override
                public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
                {
                    ListView listView = (ListView) parentView;
                    SymbolListArrayItem item = (SymbolListArrayItem) listView.getItemAtPosition(position);

                    /// ���X�g���I�����ꂽ�Ƃ��̏���...�f�[�^���J��
                    showDetailData(item.getTextResource1st(), item.getTextResource2nd(), item.getTextResource3rd());
                }
            });
            System.gc();   // ����Ȃ��i�Q�Ƃ��؂ꂽ�j�N���X����������
    	}
    	catch (Exception ex)
    	{
    		// �������Ȃ��B
    	}
    }
    
    /**
     *    �G�N�X�|�[�g�����t�@�C�������L����
     * 
     * @param fileName
     */
    private void shareContent(String fileName)
    {
    	String message = "";
        try
        {
        	// ���݂̎������擾����
            Calendar calendar = Calendar.getInstance();
    		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date =  outFormat.format(calendar.getTime());

            // ���[���^�C�g��
            String title = parent.getString(R.string.app_name) + " | "+ objectHolder.getDataTitle() + " | " + date;

            // ���[���̖{�����\�z����
            message = message + "Name : " + objectHolder.getDataTitle() + "\n";
            message = message + "exported : " + date + "\n";
            message = message + "number of objects : " + objectHolder.getCount() + "\n";

            // Share Intent�𔭍s����B
            SharedIntentInvoker.shareContent(parent, MENU_ID_SHARE, title, message,  fileName, "text/plain");
        }
        catch (Exception ex)
        {
        	
        }
    }
    
    /**
     *   �t�@�C�����I�����ꂽ�I
     * 
     */
    public void selectedFileName(String fileName)
    {
    	// CSV�t�@�C������I�u�W�F�N�g�����[�h����N���X���Ăяo���B
        Log.v(Main.APP_IDENTIFIER, "ExtensionActivityListener::selectedFileName() : " + fileName);
        MeMoMaFileImportCsvProcess asyncTask = new MeMoMaFileImportCsvProcess(parent, fileUtility, this, fileName);
        asyncTask.execute(objectHolder);
    }

    /**
     *    �C���|�[�g���ʂ̎�M
     * 
     * @param detail
     */
    public void onImportedResult(String detail)
    {
		Log.v(Main.APP_IDENTIFIER, "ExtensionActivityListener::onImportedResult() '"  + objectHolder.getDataTitle() +"' : " + detail);

		// �C���|�[�g�������Ƃ�`�B����
		String outputMessage = parent.getString(R.string.import_csv) + " " + objectHolder.getDataTitle() + " " + detail;
        Toast.makeText(parent, outputMessage, Toast.LENGTH_SHORT).show();

        // �ꗗ�̃��X�g�����Ȃ���
        onLoadingProcess();
        updateObjectList();
    }    
}
