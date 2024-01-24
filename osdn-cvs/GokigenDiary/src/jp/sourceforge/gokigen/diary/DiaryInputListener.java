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
 *   日記記録エリア
 * 
 * @author MRSa
 *
 */
public class DiaryInputListener implements OnClickListener, OnTouchListener, OnKeyListener, IDateTimeInputDialogListener
{
    public static final int MENU_ID_READ_BCR   = (Menu.FIRST + 1);
    public static final int MENU_ID_SPECIFY_DATETIME = (Menu.FIRST + 2);

	private Activity parent = null;  // 親分
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
     *  コンストラクタ
     * @param argument
     */
    public DiaryInputListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);

        /** ディレクトリの準備 **/        
        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);

        // 書き込みボタンとのリンク
        final ImageButton writeButton = (ImageButton) parent.findViewById(R.id.DataWriteButton);        
        writeButton.setOnClickListener(this);

        // 共有ボタンとのリンク
        final ImageButton shareButton = (ImageButton) parent.findViewById(R.id.DataShareButton);        
        shareButton.setOnClickListener(this);
        
        // カメラ起動ボタンとのリンク
        final ImageButton cameraButton = (ImageButton) parent.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(this);

        // マイク起動ボタンとのリンク
        final ImageButton micButton = (ImageButton) parent.findViewById(R.id.micButton);

        // 音声認識 (オフラインモードかチェックし、オフライン時にはボタンを表示しない)
        boolean isOffline = preferences.getBoolean("offlineMode", false);        
        PackageManager pm = parent.getPackageManager();
    	List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
    	if ((activities.size() != 0)&&(isOffline != true))
        {
            micButton.setOnClickListener(this);
    	}
    	else
    	{
    		// マイクボタンを消す
    		micButton.setVisibility(View.INVISIBLE);
    	}

    	String picture = preferences.getString("takenPictureFileName", "");
        Log.v(Main.APP_IDENTIFIER, "PictureFile : " + picture);
    	if (picture.length() > 0)
    	{
    		// 写真が撮られていた場合...読み出して表示するs
            final ImageView area = (ImageView) parent.findViewById(R.id.cameraView);
            ImageAdjuster.setImage(parent, area, picture);
    	}
    	
        // カテゴリ選択ボタンとのリンク
        //final Button setCategoryButton = (ImageButton) parent.findViewById(R.id.setCategoryButton);
        //setCategoryButton.setOnClickListener(this);
        
        // 数字の単位を設定する
        final TextView unitArea = (TextView) parent.findViewById(R.id.numberUnitArea);
        unitArea.setText(preferences.getString("amountUnit", ""));

        final ImageView iconArea = (ImageView) parent.findViewById(R.id.emotionIconArea);
        final RatingBar rating = (RatingBar) parent.findViewById(R.id.ratingBar1);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            // レーティングバーの値が変更された...        
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
        /** ファイルユーティリティの削除 **/
        fileUtility = null;
    }

    
    /**
     *  スタート準備
     */
    public void prepareToStart()
    {

    }

    /**
     *  終了準備
     */
    public void shutdown()
    {
    }
    
    /**
     *  他画面から戻ってきたとき...
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

    		// 音声認識した情報を読み出す！
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Iterator<String> iterator = matches.iterator();
            while (iterator.hasNext())
            {
                message = message + " " + iterator.next();
                break;  /////  実は、一個だけでよかった...。（選択肢が並ぶ、とのこと。）
            }

            // 文字列を末尾に追加する
            final EditText diary  = (EditText) parent.findViewById(R.id.descriptionInputArea);
            message = diary.getText().toString() + message;
            diary.setText(message);
        }
     	else if ((requestCode == R.id.cameraButton)&&(resultCode == Activity.RESULT_OK))
        {
            try
            {
            	// 画像ファイルの新しいファイル名を決める
            	long dateTime = Calendar.getInstance().getTime().getTime();
            	String fileName = dateTime + ".jpg";
                pictureFileName = fileUtility.getGokigenDirectory() + "/" + fileUtility.decideFileNameWithDate(fileName);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("takenPictureFileName", pictureFileName);
                editor.commit();
                Log.v(Main.APP_IDENTIFIER, "takenPictureFile : " + pictureFileName);
                
                // ファイルをコピーする (ファイルが存在した場合には上書きする)
                boolean ret = fileUtility.copyFile(pictureFileName, fileUtility.getGokigenDirectory() + temporaryPictureFile);
                if (ret == false)
                {
                	Log.v(Main.APP_IDENTIFIER, "fail copy : " + fileUtility.getGokigenDirectory() + temporaryPictureFile + " => " + pictureFileName);
                }

/**/
                // 現在位置情報を取得
                double  currentLatitude = (preferences.getInt("Latitude",   35000000) / 1E6);
                double  currentLongitude = (preferences.getInt("Longitude", 135000000) / 1E6);

                // ギャラリーに登録する
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
               
                // 本ファイルとして画像登録を行う
                File tempFile = new File(fileUtility.getGokigenDirectory() + temporaryPictureFile);

                // イメージをギャラリー管理下に置く
                //MediaStore.Images.Media.insertImage(parent.getContentResolver(), tempFile.getAbsolutePath(), fileName, fileName);

                // 一時ファイルを削除する
                tempFile.delete();
                tempFile = null;
                
                // キャプチャした画像を画面に表示してみる。
                final ImageView area = (ImageView) parent.findViewById(R.id.cameraView);
                ImageAdjuster.setImage(parent, area, pictureFileName);

                // (日時を設定していない場合は)テキストをクリア...
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
                // 拾った文字列を末尾に追加する
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
        // 反応が遅くなるかもしれないが、、、ガベコレを実施する。
        System.gc();
    }

    /**
     *   クリックされたときの処理
     */
    public void onClick(View v)
    {
        int id = v.getId();
        if ((id == R.id.DataWriteButton)||(id == R.id.DataShareButton))
        {
            // 日記データをファイルに書き込む
            boolean ret = writeDiaryContents(id);

            // 書き込み結果と共に親Activityに応答する
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
            // カメラ起動指示...
            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileUtility.getGokigenDirectory() + temporaryPictureFile)));
            parent.startActivityForResult(intent, id);
        }
        else if (id == R.id.micButton)
        {
        	// マイクで音声認識 (Android 1.6だと、デフォルトロケールしか通用しないらしい)
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
     *   触られたときの処理
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
     *  キーを押す
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
     *   メニューへのアイテム追加
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
     *   メニュー表示前の処理
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
     *   メニューのアイテムが選択されたときの処理
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
     *  ダイアログ表示の準備 (生成時、1回だけ設定する項目)
     * 
     */
    public Dialog onCreateDialog(int id)
    {
    	if (id == R.id.layout_dateTimePicker)
    	{
            // 日時設定ダイアログを生成する。
    		datetimeinputdialog = null;
    		datetimeinputdialog = new DateTimeInputDialog(parent, this);
    		return (datetimeinputdialog.getDialog());
    	}
    	return (null);
    }
    	
    /**
     *  ダイアログ表示の準備 (開くたびに設定する項目)
     * 
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
    	if (id == R.id.layout_dateTimePicker)
    	{
            // 現在時刻を日時設定ダイアログに反映させる
    		setupDialogForSpecifyDateTime(dialog);
    		return;
    	}
    	
    }

    /**
     *  アイコン情報を一時記憶する
     * 
     * @param iconId
     * @param text
     */
    public void setEmotion(int iconId, String text)
    {
        //
    }

    
    /**
     *  記録日時を指定する場合、ダイアログ
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
     *  バーコードの読み取り
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
     *   日記情報をファイルに出力する
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

        // 共有を実行するパターンかどうかを判定する
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
            	// 記録日時指定の場合には、ここでは記録しない。
                outputData = outputData + "";
            }
            else
            {
                outputData = outputData + diary.getText().toString();            	
            }
            outputData = outputData + "</message>";
            outputData = outputData + "<picture>" + pictureFileName + "</picture>";

            // Preference パラメータから位置情報を取得する！
            double  currentLatitude = (preferences.getInt("Latitude",   35000000) / 1E6);
            double  currentLongitude = (preferences.getInt("Longitude", 135000000) / 1E6);
            String  currentLocation = preferences.getString("LocationName", "?????");
            String  locationTime = preferences.getString("LocationTime", "-----");

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
            String date  = dateFormat.format(calendar.getTime());

            // 書き込む文字列を生成する
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

            //  記録するデータをマージする
            contents = contents + "<comments>" + outputData + "</comments>";
            if (useSpecifiedDateTime == true)
            {
            	// 日時指定の場合には、更新した情報のデータとして記録する。
                contents = contents + "<update><revisedmessage>" + diary.getText().toString() + "</revisedmessage>";
                contents = contents + "<revisedtime>" + outFormat.format(calendar.getTime()) + "</revisedtime></update>";
            }
            contents = contents + "</gokigendiary>";

            byte [] outputByte = contents.getBytes("UTF-8");
            
            // ファイルに書き込みする (毎回Open & Closeするよう変更する)
            String addRate = "";
            if (rate < 10)
            {
                addRate = "0";
            }
            
            // ファイル名を決定する
            String attachId = "";
            String fileName = "";
            if (pictureFileName.length() > 0)
            {
            	// 添付あり： 共有あり Q_, 共有なし P_
            	attachId = (isShareData == true) ? "Q_" : "P_";
            }
            else
            {
            	// 添付なし： 共有あり S_, 共有なし N_
            	attachId = (isShareData == true) ? "S_" : "N_";
            }
            if (useSpecifiedDateTime == true)
            {
            	// ファイル名を指定した日付で置き換える...
            	date = getSpecifiedTimeString();
                fileName = fileUtility.decideFileNameWithSpecifiedDate("diary" + date + "_" + addRate + rate + categoryId + attachId + numberValue + ".txt", specifiedYear, specifiedMonth, specifiedDay);
            }
            else
            {
                fileName = fileUtility.decideFileNameWithDate("diary" + date + "_" + addRate + rate + categoryId + attachId + numberValue + ".txt");
            }

            // ファイル名のチェック
            try
            {
                File targetFile = new File(fileName);
                if (targetFile.exists() == true)
                {
                	// 指定したファイルがすでに存在した、、前のファイルを別名にコピーしておく...
                	fileUtility.copyFile(fileName + "~", fileName);
                    targetFile.delete();
                }
                targetFile = null;
            }
            catch (Exception fileEx)
            {
            	// 例外発生...なにもしない
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
            // 書き込みに失敗した...
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

        // 「共有」Intentを起動する。
        if (isShareData == true)
        {
            shareContent();
        }
        return (retValue);
    }

    /**
     *  指定した日時用の文字列を生成する
     * 
     * @return  指定した時間の文字列
     */
    private String getSpecifiedTimeString()
    {
        String time = "";

        // 時の指定
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

    	// 分の指定
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
    	
    	// 秒数の指定 (固定値)
    	time = time + "11";

    	return (time);
    }
        
    /**
     *  指定した日時用の文字列を生成する
     * 
     * @return  指定した時間の文字列
     */
    private String getSpecifiedDateTimeString()
    {
        // 年月日の指定
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

        // 時の指定
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

    	// 分の指定
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
    	
    	// 秒数の指定 (固定値)
    	data = data + "11";

    	return (data);
    }

    /**
     *   データを共有する！
     * 
     */
    public void shareContent()
    {
    	Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        try
        {
        	// 書き込まれているデータを読み出す
            final EditText diary  = (EditText) parent.findViewById(R.id.descriptionInputArea);
            final RatingBar rating1 = (RatingBar) parent.findViewById(R.id.ratingBar1);            
            final EditText numberArea = (EditText) parent.findViewById(R.id.numberInputArea);
            String numberValue = numberArea.getText().toString();
            if (numberValue.length() != 0)
            {
                // 数値が設定されている場合は単位を末尾にくっつける
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
        		// 位置情報を入れる
        		if ((currentLatitude != 0.0)&&(currentLongitude != 0.0))
        		{
        		    message = message + " " + "Location:" + currentLatitude + "," + currentLongitude;
        		}
        	}
        	else if (useLocation == 2)
        	{
        		// 位置情報を入れる (Google Mapへのリンク)
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
     *  日時設定の入力がされた場合の処理...
     * 
     */
    public void inputDateTimeEntered(boolean useCurrent, int year, int month, int day, int hour, int minite)
	{
        // 日時入力を使うかどうかの設定を記憶する
    	useSpecifiedDateTime = (useCurrent == true) ? false : true;
    	
        // 入力した日時を記憶する
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
                // 表示をクリアする
            	info.setText(parent.getString(R.string.blank));

                // データをクリアする
            	specifiedYear = 0;
                specifiedMonth = 0;
                specifiedDay = 0;
                specifiedHour = 0;
                specifiedMinite = 0;
            }
            else
            {
                // 設定した日時を表示する
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
     *  日時入力の設定がキャンセルされたとき...
     * 
     */
    public void inputDateTimeCanceled()
    {
        // 何もしない...
    }
}
