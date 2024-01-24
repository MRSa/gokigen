package jp.sourceforge.gokigen.diary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  �f�[�^�r���[��ʂ̃��X�i�N���X
 * 
 * @author MRSa
 *
 */
public class DiaryDataViewListener implements OnClickListener, OnTouchListener, OnKeyListener
{
    public static final int MENU_ID_DELETE_FILE    = (Menu.FIRST + 100);
    public static final int MENU_ID_SHOW_MAP       = (Menu.FIRST + 101);
    public static final int MENU_ID_EDIT_TEXT      = (Menu.FIRST + 102);
    public static final int MENU_ID_SHARE_CONTENT  = (Menu.FIRST + 103);
    public static final int MENU_ID_INSERT_PICTURE = (Menu.FIRST + 104);
    public static final int MENU_ID_DATA_HISTORY   = (Menu.FIRST + 105);

    static public final String DIARY_FILE     = "jp.sourceforge.gokigen.diary.diaryFile";

    private final int BUFFER_MARGIN    = 4;

    private Activity parent = null;

    private DiaryDataHandler dataHandler = null;
    private int  iconId = R.drawable.emo_im_wtf;
    private ImageAdjuster imageAdjuster = null;
    private String diaryFileName = null;
    private boolean isRenamedDataFile = false;
    private DiaryDateLineDrawer dateLineDrawer = null;
    
    private Dialog textEditDialog = null;
    private Dialog revisionHistoryDialog = null;
	
	/**
	 *  �R���X�g���N�^
	 */
    public DiaryDataViewListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  ���X�i�N���X�̏���
     * 
     */
    public void prepareListener()
    {
    	// �O�{�^���Ƃ̃����N
        final ImageButton previousButton = (ImageButton) parent.findViewById(R.id.showPreviousDataItem);
        previousButton.setOnClickListener(this);

        // ���{�^���Ƃ̃����N
        final ImageButton nextButton = (ImageButton) parent.findViewById(R.id.showNextDataItem);
        nextButton.setOnClickListener(this);
    }

    /**
     *  ���X�i�N���X�̂��̑��̏���
     * 
     */
    public void prepareOther()
    {
        imageAdjuster = new ImageAdjuster(parent);
        diaryFileName = null;
        isRenamedDataFile = false;    	
    }
    
    /**
     *  ���X�i�N���X�̏I��
     */
    public void finishListener()
    {
    	diaryFileName = null;
    	isRenamedDataFile = false;
    }

    /**
     *   ���j���[�ւ̃A�C�e���ǉ�
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {

    	// ���L���j���[��ǉ�����
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_SHARE_CONTENT, Menu.NONE, parent.getString(R.string.shareContent));
        menuItem.setIcon(android.R.drawable.ic_menu_share);

    	// �ҏW���j���[��ǉ�����
    	menuItem = menu.add(Menu.NONE, MENU_ID_EDIT_TEXT, Menu.NONE, parent.getString(R.string.editText));
        menuItem.setIcon(android.R.drawable.ic_menu_edit);

        // �ʒu�i�n�}�j ���j���[��ǉ�����
        menuItem = menu.add(Menu.NONE, MENU_ID_SHOW_MAP, Menu.NONE, parent.getString(R.string.showPlace));
        menuItem.setIcon(android.R.drawable.ic_menu_mapmode);
        	
        // �폜���j���[��ǉ�����
        menuItem = menu.add(Menu.NONE, MENU_ID_DELETE_FILE, Menu.NONE, parent.getString(R.string.delete));
        menuItem.setIcon(android.R.drawable.ic_menu_delete);

        // �摜�}�����j���[��ǉ�����
    	menuItem = menu.add(Menu.NONE, MENU_ID_INSERT_PICTURE, Menu.NONE, parent.getString(R.string.insertPicture));
        menuItem.setIcon(android.R.drawable.ic_menu_gallery);

        // �q�X�g�����j���[��ǉ�����
    	menuItem = menu.add(Menu.NONE, MENU_ID_DATA_HISTORY, Menu.NONE, parent.getString(R.string.revisionHistory));
        menuItem.setIcon(android.R.drawable.ic_menu_recent_history);

        return (menu);
    }

    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
    	menu.findItem(MENU_ID_SHARE_CONTENT).setVisible(true);
        menu.findItem(MENU_ID_DELETE_FILE).setVisible(true);
        menu.findItem(MENU_ID_SHOW_MAP).setVisible(true);
        menu.findItem(MENU_ID_INSERT_PICTURE).setVisible(true);
        menu.findItem(MENU_ID_EDIT_TEXT).setVisible(true);
        menu.findItem(MENU_ID_DATA_HISTORY).setVisible(true);
        return;
    }

    /**
     *   ���j���[�̃A�C�e�����I�����ꂽ�Ƃ��̏���
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if ((id == MENU_ID_DELETE_FILE)||(id == MENU_ID_EDIT_TEXT)||(id == MENU_ID_DATA_HISTORY))
        {
            // �_�C�A���O��\������
            parent.showDialog(id);
            return (true);
        }
        else if (id == MENU_ID_SHOW_MAP)
        {
            // �I�t���C�����[�h���`�F�b�N���A�I�t���C�����ɂ̓}�b�v��ʂ��J���Ȃ��悤�ɂ���
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            boolean isOffline = preferences.getBoolean("offlineMode", false);
            if (isOffline == true)
            {
                Toast.makeText(parent, parent.getString(R.string.warn_offline), Toast.LENGTH_SHORT).show();
                return (true);
            }

            // �f�[�^�̈ʒu��\������
            showPlace(id);
            return (true);
        }
        else if (id == MENU_ID_SHARE_CONTENT)
        {
            // "���L" ��I�������Ƃ�... Intent�𔭍s����
            shareContent();        	
        	return (true);
        }
        else if (id == MENU_ID_INSERT_PICTURE)
        {
        	// �摜�}����I�������Ƃ�...
        	insertPicture();
        	return (true);
        }
        return (false);
    }

    /**
     *  ����ʂ���߂��Ă����Ƃ��̏���...
     * 
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
                Uri uri = data.getData();
                String fileName = null;
                if (diaryFileName != null)
                {
                	fileName = diaryFileName;
                }
                else
                {
                	fileName = parent.getIntent().getStringExtra(DIARY_FILE);
                }
                //Log.v(Main.APP_IDENTIFIER, "update File: " + fileName);
          	    if (appendRevisedMessage(fileName, null, uri.toString()) == true)
        	    {
        		    // �f�[�^���X�V�ł����I �摜�����C����ʂɔ��f������
                    ImageView picture  = (ImageView) parent.findViewById(R.id.pictureView);

                	// �摜��\������
                    picture.setVisibility(View.VISIBLE);
                    imageAdjuster.setImage(picture, uri.toString());
        	    }
          	    System.gc();
            }
            catch (Exception ex)
            {
                Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.toString() + " " + ex.getMessage());
            }
    		return;
    	}
    	if (requestCode == MENU_ID_SHARE_CONTENT)
    	{
    		System.gc();
    	}
        return;
    }

    /**
     *  �_�C�A���O�𐶐�����
     * @param id
     * @return
     */
    public Dialog onCreateDialog(int id)
    {
        if (id == MENU_ID_DELETE_FILE)
        {
            // �폜���邩�ǂ����m�F����_�C�A���O��\������
            return (createConfirmDeleteDialog());
        }
        if (id == MENU_ID_EDIT_TEXT)
        {
        	// �ҏW�_�C�A���O��\������
            return (createEditTextDialog());
        }
        if (id == MENU_ID_DATA_HISTORY)
        {
        	// �������b�Z�[�W�_�C�A���O��\������
            return (createTextViewDialog());
        }
        return (null);    	
    }

    /**
     *  �_�C�A���O������̏������s��
     * @param id
     * @param dialog
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
        if (dataHandler == null)
        {
            // �ʂ�ۑ΍�...�B
        	return;
        }

        if (id == MENU_ID_DELETE_FILE)
        {
            // �폜���邩�ǂ����m�F����_�C�A���O��\������
        }
        else if (id == MENU_ID_EDIT_TEXT)
        {
        	// �ҏW�_�C�A���O��\������
            prepareEditTextDialog(dialog, dataHandler.getMessageString());
        }
        else if (id == MENU_ID_DATA_HISTORY)
        {
        	// �������b�Z�[�W�_�C�A���O��\������
            prepareTextViewDialog(dialog, dataHandler.getWholeMessageString());
        }
        return;
    }
    
    
    /**
     *   (�{�^����)�N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        String fileName = "";
        int id = v.getId();
        if (id == R.id.showPreviousDataItem)
        {
            // �ЂƂO�̃f�[�^��\������
            fileName = dateLineDrawer.moveToPreviousData();
        }
        else if (id == R.id.showNextDataItem)
        {
            // �ЂƂ��̃f�[�^��\������
            fileName = dateLineDrawer.moveToNextData();
        }
        diaryFileName = fileName;
        
        // �f�[�^���X�V����
        //Log.v(Main.APP_IDENTIFIER, "Next File Name: " + fileName);            
        prepareDiaryDataView(fileName);

    }
    
    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        return (false);
    }

    /**
     *  �L�[���������Ƃ��̏���
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        return (false);
    }

    /**
     *  �I������
     */
    public void shutdown()
    {
    	// �_�C�A���O�����
    	dismissDialogs();
    	if (textEditDialog != null)
    	{
    		textEditDialog.dismiss();
    		textEditDialog = null;
    	}
    	if (revisionHistoryDialog != null)
    	{
    		revisionHistoryDialog.dismiss();
    		revisionHistoryDialog = null;
    	}
    }
    
    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {        
    	// �_�C�A���O�����
    	dismissDialogs();

    	// �\������t�@�C�������i�t���p�X�Łj�擾����
        String fileName = null;
        if (diaryFileName != null)
        {
        	fileName = diaryFileName;
        }
        else
        {
        	fileName = parent.getIntent().getStringExtra(DIARY_FILE);
        }

    	// �n���h���𐶐������Z�b�g����
        dataHandler = new DiaryDataHandler();
        dataHandler.resetDatas();

        // ���t�����̕`�敔�� (����������킹��)
//        if (dateLineDrawer == null)
        {
        	dateLineDrawer = null;
            dateLineDrawer = new DiaryDateLineDrawer();
    	    dateLineDrawer.prepare(fileName);
        }

        // �N���X�̏�������
        prepareDiaryDataView(fileName);
    }

    /**
     *  ��������A�C�R�������߂�
     * @param fileName �\������f�[�^�̃t�@�C����
     * @return �A�C�R���h�c
     */
    private int getIconId(String fileName)
    {
        int start = fileName.indexOf("-diary");
    	if (start <= 0)
    	{
    		return (R.drawable.emo_im_wtf);
    	}
        String gokigenRate = fileName.substring((start + 13), (start + 15));
        return (DecideEmotionIcon.decideEmotionIcon(gokigenRate));
    }    

    /**
     *  ���l�f�[�^�����肷��
     * @param fileName
     * @return
     */
    private int getNumberValue(String fileName)
    {
        // ���l�f�[�^���擾����
        int numberValue = 0;
        try
        {
        	String numData = fileName.substring((fileName.lastIndexOf("_") + 1), fileName.lastIndexOf("."));
            numberValue = Integer.parseInt(numData,10);
        }
        catch (Exception ex)
        {
        	numberValue = 0;
        }
        return (numberValue);
    }

    /**
     *  Activity�̏������s��
     * 
     */
    private void prepareDiaryDataView(String fileName)
    {
        try
        {
            // ���l�f�[�^���擾����
        	int numberValue = getNumberValue(fileName);

            // �A�C�R����Id�����肷��
            iconId  = getIconId(fileName);

            // ��ʉ����̃f�[�^�ʒu�̏��
        	final TextView dateArea = (TextView) parent.findViewById(R.id.dateTimeArea);
        	dateArea.setText(dateLineDrawer.getDateTimeString());

        	// �O���t�����̗̈�
            final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.DateTimeDataView);
            view.setCanvasDrawer(dateLineDrawer);

            // �A�C�R����\������
            final ImageView iconArea = (ImageView) parent.findViewById(R.id.showEmotionIcon);
            iconArea.setImageResource(iconId);

            // ���l��\������
            final TextView numberTitle = (TextView) parent.findViewById(R.id.numberValueTitle);
            final TextView numberArea = (TextView) parent.findViewById(R.id.showNumberValue);
            if (numberValue != 0)
            {
                // ���l���ݒ肳��Ă���ꍇ�͕\������
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
                numberTitle.setVisibility(View.VISIBLE);
                numberArea.setText("" + numberValue + preferences.getString("amountUnit", ""));
            }
            else
            {
                // ���l���ݒ肳��Ă��Ȃ��ꍇ�ɂ͉����\�����Ȃ�
                numberTitle.setVisibility(View.INVISIBLE);
                numberArea.setText("");
            }

            // �f�[�^�t�@�C�����p�[�X���ĕϐ��ɕ�������
            boolean ret = readDataFileContents(fileName, dataHandler);
            if (ret == false)
            {
                // �f�[�^�t�@�C���ǂݏo�����s...
                Log.v(Main.APP_IDENTIFIER, "PARSE ERROR :" + fileName);
                return;
            }

            // �ۑ����Ԃ̐ݒ�
            TextView savedTime = (TextView) parent.findViewById(R.id.dateInfo);
            savedTime.setText(dataHandler.getSavedTimeString());

            // �ʒu���i�̃^�C�g���j�̐ݒ�
            TextView scanedTime = (TextView) parent.findViewById(R.id.locationTitle);
            scanedTime.setText(parent.getString(R.string.savedLocation) + " " + "(" + dataHandler.getScanedTimeString() + ")");

            // �ʒu���̐ݒ�
            TextView location  = (TextView) parent.findViewById(R.id.showLocation);
            location.setText(dataHandler.getLocationString());            

            // ���[�g���̐ݒ�
            RatingBar ratings  = (RatingBar) parent.findViewById(R.id.showRate);
            ratings.setRating(dataHandler.getRatingValue());

            // ���b�Z�[�W�̐ݒ�
            TextView  comment  = (TextView)  parent.findViewById(R.id.showComment);
            comment.setText(dataHandler.getMessageString());

            // �摜�̐ݒ�
            String pictureString = dataHandler.getPictureString();
            ImageView picture  = (ImageView) parent.findViewById(R.id.pictureView);
            if (pictureString != null)
            {
            	// �摜�f�[�^�̕\���G���A��\������
            	picture.setVisibility(View.VISIBLE);

            	// �w�肳�ꂽ�摜�f�[�^��\������
                imageAdjuster.setImage(picture, pictureString);
            }
            else
            {
            	// �摜�f�[�^�̕\���G���A������
            	picture.setVisibility(View.GONE);
            }
            
            // �}�[�J�[�������ĕ`�悷��
            final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.DateTimeDataView);
            surfaceView.doDraw();
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EXCEPTION :" + ex.getMessage());
        }
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
     *  �t�@�C����ǂݏo�� (SAX�p�[�T���g�p����)
     * @param fileName
     * @return
     */
    private boolean readDataFileContents(String fileName, DefaultHandler handler)
    {
        try
        {
              SAXParserFactory spfactory = SAXParserFactory.newInstance();
              SAXParser parser = spfactory.newSAXParser();
              FileInputStream input = new FileInputStream(new File(fileName));
              parser.parse(input, handler);
              
              input.close();
              input = null;
              parser = null;
              spfactory = null;
//            System.gc();
              
              return (true);
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "SAX PARSER Ex:" + ex.getMessage());            
        }
        return (false);
    }
    
    /**
     *  �f�[�^�̈ʒu��\������
     * 
     * @param menuId
     */
    private void showPlace(int menuId)
    {
        Intent locationIntent = new Intent(parent, jp.sourceforge.gokigen.diary.LocationMap.class);
        locationIntent.putExtra(LocationMap.LOCATION_FILE, "");
        locationIntent.putExtra(LocationMap.LOCATION_ISCURRENT, false);
        locationIntent.putExtra(LocationMap.LOCATION_ICONID, iconId);
        locationIntent.putExtra(LocationMap.LOCATION_LATITUDE, dataHandler.getLatitude());
        locationIntent.putExtra(LocationMap.LOCATION_LONGITUDE, dataHandler.getLongitude());
        locationIntent.putExtra(LocationMap.LOCATION_MESSAGE, dataHandler.getLocationString());
        parent.startActivityForResult(locationIntent, menuId);
    }
    
    /**
     *   �f�[�^�����L����I
     * 
     */
    private void shareContent()
    {
    	String message = "";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        try
        {
            int rating = (int) (dataHandler.getRatingValue() * 10);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            int useAA = Integer.parseInt(preferences.getString("useAAtype", "0"));
            int useLocation = Integer.parseInt(preferences.getString("useLocationtype", "0"));
            double  currentLatitude = (dataHandler.getLatitude() / 1E6);
            double  currentLongitude = (dataHandler.getLongitude() / 1E6);
        	message = dataHandler.getMessageString() + " " + message;
        	if (useAA == 1)
        	{
        	    message = message + " " + DecideEmotionIcon.decideEmotionString(rating);
        	}
        	else if (useAA == 2)
        	{
        	    message =  message + " " + DecideEmotionIcon.decideEmotionJapaneseStyleString(rating);        	    
        	}
        	if (useLocation == 1)
        	{
        		// �ʒu��������
        		if ((currentLatitude != 0.0)&&(currentLongitude != 0.0))
        		{
        		    message = message + " " + "Location:" + currentLatitude + "," + currentLongitude;
        		}
        	}
        	else if (useLocation == 2)
        	{
        		// �ʒu��������
        		if ((currentLatitude != 0.0)&&(currentLongitude != 0.0))
        		{
            		message = message + " " + "http://maps.google.co.jp/maps?q=" + currentLatitude + "," + currentLongitude;
        		}
        	}
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, parent.getString(R.string.app_name) + " | " + dataHandler.getSavedTimeString());
            intent.putExtra(Intent.EXTRA_TEXT, message);

            String pictureString = dataHandler.getPictureString();
            if (pictureString != null)
            {
            	try
            	{
                	intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
                	intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, ImageAdjuster.parseUri(pictureString));
                    Log.v(Main.APP_IDENTIFIER, "Attached Pic.:" + pictureString);
            	}
            	catch (Exception ee)
            	{
            		// 
                    Log.v(Main.APP_IDENTIFIER, "attach failure : " + pictureString + "  " + ee.getMessage());
            	}
            }
            parent.startActivityForResult(intent, MENU_ID_SHARE_CONTENT);          	
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(parent, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.v(Main.APP_IDENTIFIER, "xxx : " + e.getMessage() + ", " + message);
        }
    }
    
    /**
     *  ���b�Z�[�W���X�V����
     *
     * @param fileName  �X�V�i�ǋL�j����t�@�C����
     * @param messageToRevise  �X�V���郁�b�Z�[�W
     * @return
     */
    private boolean appendRevisedMessage(String fileName, String messageToRevised, String mediaUriToRevised)
    {
        String oldFileName = null;
    	InputStream is = null;
    	OutputStream os = null;
        try
        {
            // �ҏW�O�̃t�@�C����(���l�[������)�ۑ�����B
        	File targetFile = new File(fileName);
            File readFile = new File(fileName + "~");

            // �f�[�^�t�@�C�����ꊇ�œǂݏo���B(�P���̃f�[�^�́A�����������SkB�Ɨ\�z���Ă��邩��B�B�B�j
            long dataFileSize = targetFile.length();
            targetFile.renameTo(readFile);

			int offset = 0;
            byte[] buffer = new byte[(int) dataFileSize + BUFFER_MARGIN];
            
            is = new FileInputStream(readFile);
			if (is != null)
			{
				offset = 0;
				while (offset < dataFileSize)
				{
			        int size = is.read(buffer, offset, (int) (dataFileSize + BUFFER_MARGIN));
			        if (size <= 0)
			        {
			        	break;
			        }
			        offset = offset + size;
				}
				is.close();
				// offset���ǂݍ��񂾃f�[�^�T�C�Y...
			}

			Calendar calendar = Calendar.getInstance();
            SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // �t�@�C��������������B�i�����ɍ���X�V�������b�Z�[�W������j
			String bufferString = new String(buffer, 0, offset, "UTF-8");
            buffer = null;
            int bottom = bufferString.lastIndexOf("</gokigendiary>");
            bufferString = bufferString.substring(0, bottom);
            bufferString = bufferString + "<update>";
            if (messageToRevised != null)
            {
                bufferString = bufferString + "<revisedmessage>" + messageToRevised + "</revisedmessage>";
            }
            if (mediaUriToRevised != null)
            {
                bufferString = bufferString + "<attachedpicture>" + mediaUriToRevised + "</attachedpicture>";
            }
            bufferString = bufferString + "<revisedtime>" + outFormat.format(calendar.getTime()) + "</revisedtime>";
            bufferString = bufferString + "</update>" + "</gokigendiary>";

            byte [] outputByte = bufferString.getBytes("UTF-8");
            
            
            if (messageToRevised != null)
            {
                dataHandler.setRevisedMessageString(messageToRevised);
            }
            
            if (mediaUriToRevised != null)
            {
                dataHandler.setAttachedPictureString(mediaUriToRevised);
            	
                // �t�@�C������ύX���鏀�� (�摜�Y�t�Ȃ��˓Y�t����ɕς�����ꍇ)
                try
                {
                	oldFileName = diaryFileName;
                    diaryFileName = fileName.replace("N_", "P_");  // �����L
                    diaryFileName = diaryFileName.replace("S_", "Q_");  // ���L
                }
                catch (Exception e)
                {
                	Log.v(Main.APP_IDENTIFIER, ".:." + e.getMessage());
                }
            }

            targetFile = null;
            targetFile = new File(fileName);
            os = new FileOutputStream(targetFile, false);
            os.write(outputByte, 0, outputByte.length);
            os.flush();
            os.close();
            os = null;
            outputByte = null;
            bufferString = null;

            renameFile(oldFileName, diaryFileName);
            
            return (true);
        }
    	catch (Exception ex)
    	{
    		if (is != null)
    		{
    			try
    			{
    			    is.close();
    			}
    			catch (Exception e)
    			{
    				
    			}
    		}

    		if (os != null)
    		{
    			try
    			{
    		        os.close();
    			}
    			catch (Exception e)
    			{
    				
    			}
    		}
    		is = null;
    		os = null;
    	}
    	return (false);
    }

    /**
     *  �f�[�^�t�@�C���̃t�@�C�������X�V(�ύX)����
     * 
     */
    private void renameFile(String oldFileName, String newFileName)
    {
    	try
        {
    		
        	if ((oldFileName != null)&&(isRenamedDataFile == false)&&(oldFileName.matches(newFileName) != true))
        	{
        		// �t�@�C�������C������
        		File newFile = new File(newFileName);
                File oldFile = new File(oldFileName);
                oldFile.renameTo(newFile);
            	Log.v(Main.APP_IDENTIFIER, "RENAME :" + oldFileName + "=>" + newFileName);
            	isRenamedDataFile = true;
        	}
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());        	
        }
        return;
    }

    /**
     *  �_�C�A���O(�Q)�����
     * 
     */
    private void dismissDialogs()
    {
    	// 
    	if (textEditDialog != null)
    	{
    		textEditDialog.dismiss();
    	}
		textEditDialog = null;

		if (revisionHistoryDialog != null)
    	{
    		revisionHistoryDialog.dismiss();
    	}
		revisionHistoryDialog = null;
    }

    /**
     *  ���b�Z�[�W�ҏW�_�C�A���O�̕\��
     * 
     * @param message
     * @return
     */
    private void prepareTextViewDialog(Dialog layout, String message)
    {
        // ���݂̓��̓f�[�^���_�C�A���O�Ɋi�[����
        final TextView  revisionText = (TextView)  layout.findViewById(R.id.revisionText);
        revisionText.setText(message);
    }
    	
    /**
     *  ���b�Z�[�W�ҏW�_�C�A���O�̕\��
     * 
     * @param message
     * @return
     */
    private Dialog createTextViewDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) parent.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.textviewdialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle(parent.getString(R.string.revisionHistory));

        builder.setView(layout);
        builder.setCancelable(true);
        builder.setPositiveButton(parent.getString(R.string.confirmYes), new DialogInterface.OnClickListener()
              {
                   public void onClick(DialogInterface dialog, int id)
                   {
                    	  System.gc();
                    	  dialog.cancel();
                   }
               });
/*
        builder.setNegativeButton(getString(R.string.confirmNo), new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                        dialog.cancel();
                   }
               });
*/
        revisionHistoryDialog = builder.create();
        return (revisionHistoryDialog);
    }    

    /**
     *  ���b�Z�[�W�ҏW�_�C�A���O�̕������ݒ肷��
     *     
     * @param layout
     * @param message
     */
    private void prepareEditTextDialog(Dialog layout, String message)
    {
        // ���݂̓��̓f�[�^���_�C�A���O�Ɋi�[����
        final TextView  editComment = (TextView)  layout.findViewById(R.id.editTextArea);
        editComment.setText(message);
    }
    
    /**
     *  ���b�Z�[�W�ҏW�_�C�A���O�̕\��
     * 
     * @param message
     * @return
     */
    private Dialog createEditTextDialog()
    {
    	LayoutInflater inflater = (LayoutInflater) parent.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.messagedialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle(parent.getString(R.string.editText));

        final TextView  editComment = (TextView)  layout.findViewById(R.id.editTextArea);
        
        builder.setView(layout);
        builder.setCancelable(true);
        builder.setPositiveButton(parent.getString(R.string.confirmYes), new DialogInterface.OnClickListener()
              {
                   public void onClick(DialogInterface dialog, int id)
                   {
                          String fileName = null;
                          if (diaryFileName != null)
                          {
                        	  fileName = diaryFileName;
                          }
                          else
                          {
                        	  fileName = parent.getIntent().getStringExtra(DIARY_FILE);
                          }
                          Log.v(Main.APP_IDENTIFIER, "update File: " + fileName);

                          if (appendRevisedMessage(fileName, editComment.getText().toString(), null) == true)
                    	  {
                    		  // �f�[�^���X�V�ł����I ���C����ʂɔ��f������
                    	      TextView  comment  = (TextView) parent.findViewById(R.id.showComment);
                              comment.setText(dataHandler.getMessageString());
                    	  }
                    	  System.gc();
                    	  dialog.cancel();
                    	  parent.finish();
                   }
               });
        builder.setNegativeButton(parent.getString(R.string.confirmNo), new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                        dialog.cancel();
                   }
               });
        textEditDialog = builder.create();
        return (textEditDialog);
    }    
    
    /**
     *   �t�@�C���̍폜���m�F����_�C�A���O
     * 
     * @return
     */
    private Dialog createConfirmDeleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setMessage(parent.getString(R.string.confirmDelete));
        builder.setCancelable(false);
        builder.setPositiveButton(parent.getString(R.string.confirmYes), new DialogInterface.OnClickListener()
              {
                   public void onClick(DialogInterface dialog, int id)
                   {
                       String fileName = null;
                       if (diaryFileName != null)
                       {
                     	  fileName = diaryFileName;
                       }
                       else
                       {
                     	  fileName = parent.getIntent().getStringExtra(DIARY_FILE);
                       }
                       Log.v(Main.APP_IDENTIFIER, "Delete File: " + fileName);

                       try
                       {
                           File deleteFile = new File(fileName);
                           deleteFile.delete();
                       }
                       catch (Exception ex)
                       {
                           Log.v(Main.APP_IDENTIFIER, "File Delete Error (" + ex.getMessage() + ")" + fileName);
                       }
                       parent.finish();
                   }
               });

        builder.setNegativeButton(parent.getString(R.string.confirmNo), new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                        dialog.cancel();
                   }
               });
        return (builder.create());
    }
}
