package jp.sourceforge.gokigen.diary;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 *   ���L�L�^�G���A
 * 
 * @author MRSa
 *
 */
public class DiaryInputListener implements OnClickListener, OnTouchListener, OnKeyListener, IDateTimeInputDialogListener
{
    public static final int MENU_ID_READ_BCR   = (Menu.FIRST + 1);
    public static final int MENU_ID_SPECIFY_DATETIME = (Menu.FIRST + 2);

	private Activity parent = null;  // �e��
    private ExternalStorageFileUtility fileUtility = null;
    final private String temporaryPictureFile = "/takePic.jpg";
    private String categoryId = "Z";
    
    private DateTimeInputDialog datetimeinputdialog = null;
    
    private boolean useSpecifiedDateTime = false;
    private int specifiedYear = 0;
    private int specifiedMonth = 0;
    private int specifiedDay = 0;
    private int specifiedHour = 0;
    private int specifiedMinite = 0;

    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public DiaryInputListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

        /** �f�B���N�g���̏��� **/        
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);

        // �������݃{�^���Ƃ̃����N
        final ImageButton writeButton = (ImageButton) parent.findViewById(R.id.DataWriteButton);        
        writeButton.setOnClickListener(this);

        // ���L�{�^���Ƃ̃����N
        final ImageButton shareButton = (ImageButton) parent.findViewById(R.id.DataShareButton);        
        shareButton.setOnClickListener(this);
        
        // �J�����N���{�^���Ƃ̃����N
        final ImageButton cameraButton = (ImageButton) parent.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);

        // �}�C�N�N���{�^���Ƃ̃����N
        final ImageButton micButton = (ImageButton) parent.findViewById(R.id.micButton);

        // �����F�� (�I�t���C�����[�h���`�F�b�N���A�I�t���C�����ɂ̓{�^����\�����Ȃ�)
        boolean isOffline = preferences.getBoolean("offlineMode", false);        
        PackageManager pm = parent.getPackageManager();
    	List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
    	if ((activities.size() != 0)&&(isOffline != true))
        {
            micButton.setOnClickListener(this);
    	}
    	else
    	{
    		// �}�C�N�{�^��������
    		micButton.setVisibility(View.INVISIBLE);
    	}

    	String picture = preferences.getString("takenPictureFileName", "");
        Log.v(Main.APP_IDENTIFIER, "PictureFile : " + picture);
    	if (picture.length() > 0)
    	{
    		// �ʐ^���B���Ă����ꍇ...�ǂݏo���ĕ\������s
            final ImageView area = (ImageView) parent.findViewById(R.id.cameraView);
            ImageAdjuster.setImage(parent, area, picture);
    	}
    	
        // �J�e�S���I���{�^���Ƃ̃����N
        //final Button setCategoryButton = (ImageButton) parent.findViewById(R.id.setCategoryButton);
        //setCategoryButton.setOnClickListener(this);
        
        // �����̒P�ʂ�ݒ肷��
        final TextView unitArea = (TextView) parent.findViewById(R.id.numberUnitArea);
        unitArea.setText(preferences.getString("amountUnit", ""));

        final ImageView iconArea = (ImageView) parent.findViewById(R.id.emotionIconArea);
        final RatingBar rating = (RatingBar) parent.findViewById(R.id.ratingBar1);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            // ���[�e�B���O�o�[�̒l���ύX���ꂽ...        
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
            	int id = DecideEmotionIcon.decideEmotionIcon((int)(rating * 10));
                iconArea.setImageResource(id);
            }
        });

    }

    /**
     * 
     */
    public void finishListener()
    {
        /** �t�@�C�����[�e�B���e�B�̍폜 **/
        fileUtility = null;
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        String pictureFileName = "";
       
     	if ((requestCode == R.id.micButton)&&(resultCode == Activity.RESULT_OK))
        {
    		String message = "";

    		// �����F����������ǂݏo���I
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Iterator<String> iterator = matches.iterator();
            while (iterator.hasNext())
            {
                message = message + " " + iterator.next();
                break;  /////  ���́A������ł悩����...�B�i�I���������ԁA�Ƃ̂��ƁB�j
            }

            // ������𖖔��ɒǉ�����
            final EditText diary  = (EditText) parent.findViewById(R.id.descriptionInputArea);
            message = diary.getText().toString() + message;
            diary.setText(message);
        }
     	else if ((requestCode == R.id.cameraButton)&&(resultCode == Activity.RESULT_OK))
        {
            try
            {
            	// �摜�t�@�C���̐V�����t�@�C���������߂�
            	long dateTime = Calendar.getInstance().getTime().getTime();
            	String fileName = dateTime + ".jpg";
                pictureFileName = fileUtility.getGokigenDirectory() + "/" + fileUtility.decideFileNameWithDate(fileName);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("takenPictureFileName", pictureFileName);
                editor.commit();
                Log.v(Main.APP_IDENTIFIER, "takenPictureFile : " + pictureFileName);
                
                // �t�@�C�����R�s�[���� (�t�@�C�������݂����ꍇ�ɂ͏㏑������)
                boolean ret = fileUtility.copyFile(pictureFileName, fileUtility.getGokigenDirectory() + temporaryPictureFile);
                if (ret == false)
                {
                	Log.v(Main.APP_IDENTIFIER, "fail copy : " + fileUtility.getGokigenDirectory() + temporaryPictureFile + " => " + pictureFileName);
                }

/**/
                // ���݈ʒu�����擾
                double  currentLatitude = (preferences.getInt("Latitude",   35000000) / 1E6);
                double  currentLongitude = (preferences.getInt("Longitude", 135000000) / 1E6);

                // �M�������[�ɓo�^����
                ContentValues values = new ContentValues(10);
                ContentResolver contentResolver = parent.getContentResolver();
                values.put(Images.Media.TITLE, fileName);
                values.put(Images.Media.DISPLAY_NAME, fileName);
                values.put(Images.Media.DATE_TAKEN, dateTime);
                values.put(Images.Media.MIME_TYPE, "image/jpeg");
                values.put(Images.Media.LATITUDE, currentLatitude);
                values.put(Images.Media.LONGITUDE, currentLongitude);
                values.put(Images.Media.DATA, pictureFileName);
                contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);

                Log.v(Main.APP_IDENTIFIER, "Picture File :" + pictureFileName + " Latitude:" + currentLatitude + " Longitude:" + currentLongitude);
/**/
               
                // �{�t�@�C���Ƃ��ĉ摜�o�^���s��
                File tempFile = new File(fileUtility.getGokigenDirectory() + temporaryPictureFile);

                // �C���[�W���M�������[�Ǘ����ɒu��
                //MediaStore.Images.Media.insertImage(parent.getContentResolver(), tempFile.getAbsolutePath(), fileName, fileName);

                // �ꎞ�t�@�C�����폜����
                tempFile.delete();
                tempFile = null;
                
                // �L���v�`�������摜����ʂɕ\�����Ă݂�B
                final ImageView area = (ImageView) parent.findViewById(R.id.cameraView);
                ImageAdjuster.setImage(parent, area, pictureFileName);

                // (������ݒ肵�Ă��Ȃ��ꍇ��)�e�L�X�g���N���A...
                final TextView info = (TextView) parent.findViewById(R.id.InformationArea);
                if (useSpecifiedDateTime == false)
                {
                    info.setText(parent.getString(R.string.blank));
                }
            }
            catch (Exception e)
            {
                Log.v(Main.APP_IDENTIFIER, "e:" + e.getMessage() + " " + pictureFileName);
            }
        }
     	else if ((requestCode == MENU_ID_READ_BCR)&&(resultCode == Activity.RESULT_OK))
        {
            try
            {
                // �E����������𖖔��ɒǉ�����
                final EditText diary  = (EditText) parent.findViewById(R.id.descriptionInputArea);
                String contents = diary.getText().toString();
                if (contents.length() > 0)
                {
                	contents = contents + " ";
                }
                contents = contents + data.getStringExtra("SCAN_RESULT");
                diary.setText(contents);
            }
            catch (Exception e)
            {
            	
            }
        }
        // �������x���Ȃ邩������Ȃ����A�A�A�K�x�R�������{����B
        System.gc();
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        int id = v.getId();
        if ((id == R.id.DataWriteButton)||(id == R.id.DataShareButton))
        {
            // ���L�f�[�^���t�@�C���ɏ�������
            boolean ret = writeDiaryContents(id);

            // �������݌��ʂƋ��ɐeActivity�ɉ�������
            Intent resultIntent = new Intent();
            int resultCode = DiaryInput.RESULT_DATA_WRITE_FAILURE;
            if (ret == true)
            {
                resultCode = DiaryInput.RESULT_DATA_WRITE_SUCCESS;
            }
            parent.setResult(resultCode, resultIntent);
            parent.finish();
        }
        else if (id == R.id.cameraButton)
        {
            // �J�����N���w��...
            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileUtility.getGokigenDirectory() + temporaryPictureFile)));
            parent.startActivityForResult(intent, id);
        }
        else if (id == R.id.micButton)
        {
        	// �}�C�N�ŉ����F�� (Android 1.6���ƁA�f�t�H���g���P�[�������ʗp���Ȃ��炵��)
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            String myLocale = preferences.getString("myLocale", "en");

            Log.v(Main.APP_IDENTIFIER, "MyLocale :" + myLocale);
            
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, parent.getString(R.string.speechRecognization));
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, myLocale);
            parent.startActivityForResult(intent, R.id.micButton);
        }
    }

    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // int id = v.getId();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {

        }
        return (false);
    }

    /**
     *  �L�[������
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        int action = event.getAction();
        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {

        }
        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_BACK))
        {

        }
        return (false);
    }
    

    /**
     *   ���j���[�ւ̃A�C�e���ǉ�
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_READ_BCR, Menu.NONE, parent.getString(R.string.readBarcode));
        menuItem.setIcon(R.drawable.ic_menu_show_barcode);

        menuItem = menu.add(Menu.NONE, MENU_ID_SPECIFY_DATETIME, Menu.NONE, parent.getString(R.string.specifyDateTime));
        //menuItem.setIcon(android.R.drawable.ic_menu_my_calendar);
        menuItem.setIcon(android.R.drawable.ic_menu_set_as);
        
        
        //MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
        //menuItem.setIcon(android.R.drawable.ic_menu_preferences);
        
        return (menu);
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(MENU_ID_READ_BCR).setVisible(true);
        menu.findItem(MENU_ID_SPECIFY_DATETIME).setVisible(true);
        //menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
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
          case MENU_ID_READ_BCR:
        	result = true;
        	readBarcode();
        	break;

          case MENU_ID_SPECIFY_DATETIME:
        	result = true;
            parent.showDialog(R.id.layout_dateTimePicker);
        	break;

        	//case MENU_ID_PREFERENCES:
            //  showPreference();
            //  result = true;
            //  break;

          default:
            result = false;
            break;
        }
        return (result);
    }

    /**
     *  �_�C�A���O�\���̏��� (�������A1�񂾂��ݒ肷�鍀��)
     * 
     */
    public Dialog onCreateDialog(int id)
    {
    	if (id == R.id.layout_dateTimePicker)
    	{
            // �����ݒ�_�C�A���O�𐶐�����B
    		datetimeinputdialog = null;
    		datetimeinputdialog = new DateTimeInputDialog(parent, this);
    		return (datetimeinputdialog.getDialog());
    	}
    	return (null);
    }
    	
    /**
     *  �_�C�A���O�\���̏��� (�J�����тɐݒ肷�鍀��)
     * 
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
    	if (id == R.id.layout_dateTimePicker)
    	{
            // ���ݎ���������ݒ�_�C�A���O�ɔ��f������
    		setupDialogForSpecifyDateTime(dialog);
    		return;
    	}
    	
    }

    /**
     *  �A�C�R�������ꎞ�L������
     * 
     * @param iconId
     * @param text
     */
    public void setEmotion(int iconId, String text)
    {
        //
    }

    
    /**
     *  �L�^�������w�肷��ꍇ�A�_�C�A���O
     * 
     */
    private void setupDialogForSpecifyDateTime(Dialog dialog)
    {
    	if (datetimeinputdialog != null)
    	{
    		datetimeinputdialog.setDateAndTime(dialog, -1);
    	}
    	return;
    }
    
    /**
     *  �o�[�R�[�h�̓ǂݎ��
     * 
     */
    private void readBarcode()
    {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        try
        {
            parent.startActivityForResult(intent, MENU_ID_READ_BCR);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(parent, parent.getString(R.string.notFoundBcr), Toast.LENGTH_SHORT).show();
        }
    }
    
    
    /**
     *   ���L�����t�@�C���ɏo�͂���
     * 
     */
    private boolean writeDiaryContents(int btnId)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        boolean retValue = true;
        boolean isShareData = false;
    	String contents = "";
        FileOutputStream oStream = null;
        final EditText diary  = (EditText) parent.findViewById(R.id.descriptionInputArea);
        final RatingBar rating1 = (RatingBar) parent.findViewById(R.id.ratingBar1);        
        final EditText numberArea = (EditText) parent.findViewById(R.id.numberInputArea);

        String pictureFileName = preferences.getString("takenPictureFileName", "");

        // ���L�����s����p�^�[�����ǂ����𔻒肷��
        if (btnId == R.id.DataShareButton)
        {
        	isShareData = true;
        }

        try
        {
            String numberValue = numberArea.getText().toString();
            if (numberValue.length() == 0)
            {
                numberValue = "0";
            }

            int rate = (int) (rating1.getRating() * 10.0);
            String outputData = ""; 
            outputData = outputData + "<rate>" + rate + "</rate>";
            outputData = outputData + "<message>";
            if (useSpecifiedDateTime == true)
            {
            	// �L�^�����w��̏ꍇ�ɂ́A�����ł͋L�^���Ȃ��B
                outputData = outputData + "";
            }
            else
            {
                outputData = outputData + diary.getText().toString();            	
            }
            outputData = outputData + "</message>";
            outputData = outputData + "<picture>" + pictureFileName + "</picture>";

            // Preference �p�����[�^����ʒu�����擾����I
            double  currentLatitude = (preferences.getInt("Latitude",   35000000) / 1E6);
            double  currentLongitude = (preferences.getInt("Longitude", 135000000) / 1E6);
            String  currentLocation = preferences.getString("LocationName", "?????");
            String  locationTime = preferences.getString("LocationTime", "-----");

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
            String date  = dateFormat.format(calendar.getTime());

            // �������ޕ�����𐶐�����
            contents = contents + "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n";
            contents = contents + "<gokigendiary>";
            contents = contents + "<time>";
            if (useSpecifiedDateTime == true)
            {
                contents = contents + getSpecifiedDateTimeString();
            }
            else
            {
                contents = contents + outFormat.format(calendar.getTime());            	
            }
            contents = contents + "</time>";
            contents = contents + "<location>";
            contents = contents + "<latitude>"  + currentLatitude + "</latitude>";
            contents = contents + "<longitude>" +currentLongitude + "</longitude>";
            contents = contents + "<scantime>" + locationTime + "</scantime>";
            contents = contents + "<place>" + currentLocation + "</place>";
            contents = contents + "</location>";

            //  �L�^����f�[�^���}�[�W����
            contents = contents + "<comments>" + outputData + "</comments>";
            if (useSpecifiedDateTime == true)
            {
            	// �����w��̏ꍇ�ɂ́A�X�V�������̃f�[�^�Ƃ��ċL�^����B
                contents = contents + "<update><revisedmessage>" + diary.getText().toString() + "</revisedmessage>";
                contents = contents + "<revisedtime>" + outFormat.format(calendar.getTime()) + "</revisedtime></update>";
            }
            contents = contents + "</gokigendiary>";

            byte [] outputByte = contents.getBytes("UTF-8");
            
            // �t�@�C���ɏ������݂��� (����Open & Close����悤�ύX����)
            String addRate = "";
            if (rate < 10)
            {
                addRate = "0";
            }
            
            // �t�@�C���������肷��
            String attachId = "";
            String fileName = "";
            if (pictureFileName.length() > 0)
            {
            	// �Y�t����F ���L���� Q_, ���L�Ȃ� P_
            	attachId = (isShareData == true) ? "Q_" : "P_";
            }
            else
            {
            	// �Y�t�Ȃ��F ���L���� S_, ���L�Ȃ� N_
            	attachId = (isShareData == true) ? "S_" : "N_";
            }
            if (useSpecifiedDateTime == true)
            {
            	// �t�@�C�������w�肵�����t�Œu��������...
            	date = getSpecifiedTimeString();
                fileName = fileUtility.decideFileNameWithSpecifiedDate("diary" + date + "_" + addRate + rate + categoryId + attachId + numberValue + ".txt", specifiedYear, specifiedMonth, specifiedDay);
            }
            else
            {
                fileName = fileUtility.decideFileNameWithDate("diary" + date + "_" + addRate + rate + categoryId + attachId + numberValue + ".txt");
            }

            // �t�@�C�����̃`�F�b�N
            try
            {
                File targetFile = new File(fileName);
                if (targetFile.exists() == true)
                {
                	// �w�肵���t�@�C�������łɑ��݂����A�A�O�̃t�@�C����ʖ��ɃR�s�[���Ă���...
                	fileUtility.copyFile(fileName + "~", fileName);
                    targetFile.delete();
                }
                targetFile = null;
            }
            catch (Exception fileEx)
            {
            	// ��O����...�Ȃɂ����Ȃ�
                Log.v(Main.APP_IDENTIFIER, "EX outFile : " + fileName);
            }

            oStream = fileUtility.openFileStream(fileName, true);
            if (oStream != null)
            {
                oStream.write(outputByte, 0, outputByte.length);
                oStream.flush();
                oStream.close();
            }
            else
            {
                Log.v(Main.APP_IDENTIFIER, contents);
            }
            oStream = null;
            System.gc();
        }
        catch (Exception ex)
        {
            // �������݂Ɏ��s����...
            if (oStream != null)
            {
                try
                {
                    oStream.close();
                }
                catch (Exception e)
                {
                    //
                }
                oStream = null;
                System.gc();
            }
            retValue = false;
        }

        // �u���L�vIntent���N������B
        if (isShareData == true)
        {
            shareContent();
        }
        return (retValue);
    }

    /**
     *  �w�肵�������p�̕�����𐶐�����
     * 
     * @return  �w�肵�����Ԃ̕�����
     */
    private String getSpecifiedTimeString()
    {
        String time = "";

        // ���̎w��
        if (specifiedHour == 0)
    	{
    		time = "00";
    	}
    	else if (specifiedHour < 10)
    	{
    		time = "0" + specifiedHour;
    	}
    	else
    	{   
    	    time = specifiedHour + "";
    	}

    	// ���̎w��
    	if (specifiedMinite == 0)
    	{
    		time = time + "00";
    	}
    	else if (specifiedMinite < 10)
    	{
    		time = time + "0" + specifiedMinite;
    	}
    	else
    	{
    		time = time + specifiedMinite;
    	}
    	
    	// �b���̎w�� (�Œ�l)
    	time = time + "11";

    	return (time);
    }
        
    /**
     *  �w�肵�������p�̕�����𐶐�����
     * 
     * @return  �w�肵�����Ԃ̕�����
     */
    private String getSpecifiedDateTimeString()
    {
        // �N�����̎w��
    	String data = specifiedYear + "-";
        if (specifiedMonth < 10)
        {
            data = data + "0";
        }
        data = data + specifiedMonth + "-";
        if (specifiedDay < 10)
        {
        	data = data + "0";
        }
        data = data + specifiedDay + " ";

        // ���̎w��
        if (specifiedHour == 0)
    	{
    	    data = data + "00";
    	}
    	else if (specifiedHour < 10)
    	{
    		data = data + "0" + specifiedHour;
    	}
    	else
    	{   
    	    data = data + specifiedHour + "";
    	}
        data = data + ":";

    	// ���̎w��
    	if (specifiedMinite == 0)
    	{
    		data = data + "00";
    	}
    	else if (specifiedMinite < 10)
    	{
    		data = data + "0" + specifiedMinite;
    	}
    	else
    	{
    		data = data + specifiedMinite;
    	}
        data = data + ":";
    	
    	// �b���̎w�� (�Œ�l)
    	data = data + "11";

    	return (data);
    }

    /**
     *   �f�[�^�����L����I
     * 
     */
    public void shareContent()
    {
    	Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        try
        {
        	// �������܂�Ă���f�[�^��ǂݏo��
            final EditText diary  = (EditText) parent.findViewById(R.id.descriptionInputArea);
            final RatingBar rating1 = (RatingBar) parent.findViewById(R.id.ratingBar1);            
            final EditText numberArea = (EditText) parent.findViewById(R.id.numberInputArea);
            String numberValue = numberArea.getText().toString();
            if (numberValue.length() != 0)
            {
                // ���l���ݒ肳��Ă���ꍇ�͒P�ʂ𖖔��ɂ�������
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
                numberValue = numberValue + preferences.getString("amountUnit", "");
            }
            else
            {
            	numberValue = null;
            }

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            double  currentLatitude = (preferences.getInt("Latitude",   35000000) / 1E6);
            double  currentLongitude = (preferences.getInt("Longitude", 135000000) / 1E6);
            int useAA = Integer.parseInt(preferences.getString("useAAtype", "0"));
            int useLocation = Integer.parseInt(preferences.getString("useLocationtype", "0"));
        	int rating = (int) (rating1.getRating() * 10);
            String message =  diary.getText().toString();
            if (numberValue != null)
            {
            	message = message + " (" + numberValue + ")";
            }
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
        		// �ʒu�������� (Google Map�ւ̃����N)
        		if ((currentLatitude != 0.0)&&(currentLongitude != 0.0))
        		{
        		    message = message + " " + "http://maps.google.co.jp/maps?q=" + currentLatitude + "," + currentLongitude;
        		}
        	}
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, parent.getString(R.string.app_name) + " | " +  outFormat.format(calendar.getTime()));
            intent.putExtra(Intent.EXTRA_TEXT, message);

            String pictureFileName = preferences.getString("takenPictureFileName", "");
            Log.v(Main.APP_IDENTIFIER, "PICTURE FILE NAME : " + pictureFileName);
            if (pictureFileName.length() > 0)
            {
            	try
            	{
                	File picFile = new File(pictureFileName);
                	intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
                	intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(picFile));
            	}
            	catch (Exception ee)
            	{
            		// 
                    Log.v(Main.APP_IDENTIFIER, "attach failure : " + pictureFileName + "  " + ee.getMessage());
            	}
            }
            parent.startActivityForResult(intent, 0);          	
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Log.v(Main.APP_IDENTIFIER, "launch intent : " + ex.getMessage());
        }
        catch (Exception e)
        {
            Log.v(Main.APP_IDENTIFIER, "launch intent(xxx) : " + e.getMessage());
        }
    }

    /**
     *  �����ݒ�̓��͂����ꂽ�ꍇ�̏���...
     * 
     */
    public void inputDateTimeEntered(boolean useCurrent, int year, int month, int day, int hour, int minite)
	{
        // �������͂��g�����ǂ����̐ݒ���L������
    	useSpecifiedDateTime = (useCurrent == true) ? false : true;
    	
        // ���͂����������L������
    	specifiedYear = year;
        specifiedMonth = month;
        specifiedDay = day;
        specifiedHour = hour;
        specifiedMinite = minite;

        try
        {
            TextView info = (TextView) parent.findViewById(R.id.InformationArea);
            if (useSpecifiedDateTime == false)
            {
                // �\�����N���A����
            	info.setText(parent.getString(R.string.blank));

                // �f�[�^���N���A����
            	specifiedYear = 0;
                specifiedMonth = 0;
                specifiedDay = 0;
                specifiedHour = 0;
                specifiedMinite = 0;
            }
            else
            {
                // �ݒ肵��������\������
            	String dateTime = parent.getString(R.string.enteredDateTime) + " " + specifiedYear + "-" +
            	                  specifiedMonth + "-" + specifiedDay + "  " + specifiedHour + ":" + specifiedMinite; 
                info.setText(dateTime);
            }
        }
        catch (Exception ex)
        {
        	//
        }
	}
	
    /**
     *  �������͂̐ݒ肪�L�����Z�����ꂽ�Ƃ�...
     * 
     */
    public void inputDateTimeCanceled()
    {
        // �������Ȃ�...
    }
}
