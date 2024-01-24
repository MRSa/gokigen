package jp.sourceforge.gokigen.memoma;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MeMoMaDataInOutManager implements MeMoMaFileSavingProcess.ISavingStatusHolder, MeMoMaFileSavingProcess.IResultReceiver, MeMoMaFileLoadingProcess.IResultReceiver,  ActionBar.OnNavigationListener, ObjectLayoutCaptureExporter.ICaptureLayoutExporter
{
	private Activity parent = null;
	private MeMoMaObjectHolder objectHolder = null;
	private ExternalStorageFileUtility fileUtility = null;
    private MeMoMaDataFileHolder dataFileHolder = null;
	
	private boolean isSaving = false;	
	private boolean isShareExportedData = false;
	
	/**
	 *    �R���X�g���N�^
	 * 
	 */
	public MeMoMaDataInOutManager(Activity activity)
	{
	    parent = activity;
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);
	}

	/**
	 * 
	 * @param objectHolder
	 * @param lineHolder
	 */
	public void prepare(MeMoMaObjectHolder objectHolder, ActionBar bar, String fileName)
	{
        this.objectHolder = objectHolder;
        //this.lineHolder = lineHolder;
        
    	// �f�[�^�t�@�C���t�H���_���X�V����
        dataFileHolder = new MeMoMaDataFileHolder(parent, android.R.layout.simple_spinner_dropdown_item, fileUtility, ".xml");
        int index = dataFileHolder.updateFileList(fileName, null);

        // �A�N�V�����o�[��ݒ肷��
        prepareActionBar(bar);

        // �^�C�g���̐ݒ��ύX����
        if ((bar != null)&&(index >= 0))
        {
            bar.setSelectedNavigationItem(index);  // ����...
        }
	}

	/**
	 *   �f�[�^�t�@�C���ꗗ���X�V���A�A�N�V�����o�[�ɔ��f������
	 * 
	 * @param fileName
	 */
	public void updateFileList(String titleName, ActionBar bar)
	{
		if (dataFileHolder != null)
		{
			// �f�[�^�t�@�C���ꗗ���X�V����
            int index = dataFileHolder.updateFileList(titleName, null);

            // �^�C�g�����I�u�W�F�N�g�t�H���_�ɋL��������
    		objectHolder.setDataTitle(titleName);

    		// �^�C�g���̐ݒ��ύX����
            if ((bar != null)&&(index >= 0))
            {
                bar.setSelectedNavigationItem(index);  // ����...
            }
		}
	}

    /**
     *   �f�[�^�̕ۑ����s�� (�����̃t�@�C�������݂��Ă����ꍇ�A *.BAK�Ƀ��l�[���i�㏑���j���Ă���ۑ�����)
     *   
     *   
     *   @param forceOverwrite  true�̎��́A�t�@�C�������m�肵�Ă����Ƃ��́i�m�F�����Ɂj�㏑���ۑ��������ōs���B
     *   
     */
	public void saveFile(String dataTitle, boolean forceOverwrite)
	{
		if (objectHolder == null)
		{
			Log.e(Main.APP_IDENTIFIER, "ERR>MeMoMaDataInOutManager::saveFile() : "  + dataTitle);
			return;
		}

		// �^�C�g�����I�u�W�F�N�g�t�H���_�ɋL��������
		objectHolder.setDataTitle(dataTitle);
		Log.v(Main.APP_IDENTIFIER, "MeMoMaDataInOutManager::saveFile() : "  + dataTitle);

		// �����^�Ńt�@�C����ۑ�����B�B�B
		String message = saveFileSynchronous();
		onSavedResult(message);
	}

	/**
	 *    �f�[�^�t�@�C���̃t���p�X����������
	 * 
	 * @param dataTitle
	 * @return
	 */
	public String getDataFileFullPath(String dataTitle, String extension)
	{
		return (fileUtility.getGokigenDirectory() + "/" + dataTitle + extension);
	}
	
	/**  �ۑ�����Ԃ�ݒ肷�� **/
    public void setSavingStatus(boolean isSaving)
    {
    	this.isSaving = isSaving;
    }
    
    /** �ۑ�����Ԃ��擾���� **/
    public boolean getSavingStatus()
    {
    	return (isSaving);
    }

	/**
	 *    �ۑ��I�����̏���
	 */
    public  void onSavedResult(String detail)
    {
        // �ۑ��������Ƃ�`�B����
		String outputMessage = parent.getString(R.string.save_data) + " " + objectHolder.getDataTitle() + " " + detail;
        Toast.makeText(parent, outputMessage, Toast.LENGTH_SHORT).show();    	

		// �t�@�C�����X�g�X�V ... (�����ł�����Ⴀ�AAsyncTask�ɂ��Ă���Ӗ��Ȃ��Ȃ�...)
        dataFileHolder.updateFileList(objectHolder.getDataTitle(), null);
    }

    /**
	 *    �ǂݍ��ݏI�����̏���
	 */
    public  void onLoadedResult(String detail)
    {
        // �ǂݍ��݂������Ƃ�`�B����
		String outputMessage = parent.getString(R.string.load_data) + " " + objectHolder.getDataTitle() + " " + detail;
        Toast.makeText(parent, outputMessage, Toast.LENGTH_SHORT).show();

    	// ��ʂ��ĕ`�悷��
    	final GokigenSurfaceView surfaceview = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
    	surfaceview.doDraw();
    }

    /**
     *    �t�@�C�������[�h����r���̃o�b�N�O���E���h����...
     * 
     */
	public void onLoadingProcess()
	{
        // �������Ȃ�...
	}

    /**
     *    �t�@�C������f�[�^��ǂݍ��ށB
     * 
     * @param dataTitle
     */
    public void loadFile(String dataTitle)
    {
        loadFileWithName(dataTitle);
    }
    
    
    /**
     *   �t�@�C������̃f�[�^�ǂݍ��ݏ���
     * 
     * @param dataTitle
     */
	private void loadFileWithName(String dataTitle)
	{
        if (objectHolder == null)
		{
			Log.e(Main.APP_IDENTIFIER, "ERR>MeMoMaDataInOutManager::loadFile() : "  + dataTitle);
			return;
		}

		// �^�C�g�����I�u�W�F�N�g�t�H���_�ɋL��������
		objectHolder.setDataTitle(dataTitle);
		Log.v(Main.APP_IDENTIFIER, "MeMoMaDataInOutManager::loadFile() : "  + dataTitle);

		// AsyncTask���g���ăf�[�^��ǂݍ���
		MeMoMaFileLoadingProcess asyncTask = new MeMoMaFileLoadingProcess(parent, fileUtility, this);
        asyncTask.execute(objectHolder);
	}

	/**
	 *    �A�N�V�����o�[���X�V����...
	 * 
	 * @param bar
	 */
	private void prepareActionBar(ActionBar bar)
	{
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);  // ���X�g������
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);   // �^�C�g���̕\�����}�X�N����
        bar.setListNavigationCallbacks(dataFileHolder, this);  
	}

	/**
	 *    �t�@�C����ۑ�����...�����^�ŁB
	 * 
	 * @return
	 */
	private String saveFileSynchronous()
	{
		// �����^�Ńt�@�C����ۑ�����B�B�B
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
    	String backgroundUri = preferences.getString("backgroundUri","");
    	String userCheckboxString = preferences.getString("userCheckboxString","");
    	MeMoMaFileSavingEngine saveEngine = new MeMoMaFileSavingEngine(fileUtility, backgroundUri, userCheckboxString);
    	String message = saveEngine.saveObjects(objectHolder);
        return (message);		
	}
	
	
	/**
	 * 
	 * 
	 */
	public boolean onNavigationItemSelected(int itemPosition, long itemId)
	{
		String data = dataFileHolder.getItem(itemPosition);
		Log.v(Main.APP_IDENTIFIER, "onNavigationItemSelected(" + itemPosition + "," + itemId + ") : " + data);

		// �����^�Ō��݂̃t�@�C����ۑ�����B�B�B
		String message = saveFileSynchronous();
		if (message.length() != 0)
		{
            onSavedResult(message);
		}
		
    	// �I�������t�@�C�������^�C�g���ɔ��f���A�܂�Preference�ɂ��L������
        parent.setTitle(data);
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("MeMoMaInfo", data);
        editor.commit();

		// �I�������A�C�e�������[�h����I
        loadFileWithName(data);

		 return (true);
	}

	/**
	 *    �X�N���[���L���v�`�������{����
	 * 
	 */
	public void doScreenCapture(String title, MeMoMaObjectHolder holder, MeMoMaCanvasDrawer drawer, boolean isShare)
	{
		isShareExportedData = isShare;
		
    	// AsyncTask���g���ăf�[�^���G�N�X�|�[�g����
		ObjectLayoutCaptureExporter asyncTask = new ObjectLayoutCaptureExporter(parent, fileUtility, holder, drawer, this);
        asyncTask.execute(title);
	}
	
    /**
     *    �t�@�C���̃G�N�X�|�[�g���ʂ��󂯎��
     * 
     */
	public void onCaptureLayoutExportedResult(String exportedFileName, String detail)
    {
		Log.v(Main.APP_IDENTIFIER, "MeMoMaDataInOutManager::onCaptureExportedResult() '"  + objectHolder.getDataTitle() +"' : " + detail);

		// �G�N�X�|�[�g�������Ƃ�`�B����
		String outputMessage = parent.getString(R.string.capture_data) + " " + objectHolder.getDataTitle() + " " + detail;
        Toast.makeText(parent, outputMessage, Toast.LENGTH_SHORT).show();
        
        if (isShareExportedData == true)
        {
        	// �G�N�X�|�[�g�����t�@�C�������L����
        	shareContent(exportedFileName);
        }
    	isShareExportedData = false;
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
            SharedIntentInvoker.shareContent(parent, MeMoMaListener.MENU_ID_SHARE, title, message,  fileName, "image/png");
        }
        catch (Exception ex)
        {
        	
        }
    }

}
