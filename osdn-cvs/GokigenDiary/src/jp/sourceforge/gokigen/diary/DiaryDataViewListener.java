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
 *  データビュー画面のリスナクラス
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
	 *  コンストラクタ
	 */
    public DiaryDataViewListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  リスナクラスの準備
     * 
     */
    public void prepareListener()
    {
    	// 前ボタンとのリンク
        final ImageButton previousButton = (ImageButton) parent.findViewById(R.id.showPreviousDataItem);
        previousButton.setOnClickListener(this);

        // 次ボタンとのリンク
        final ImageButton nextButton = (ImageButton) parent.findViewById(R.id.showNextDataItem);
        nextButton.setOnClickListener(this);
    }

    /**
     *  リスナクラスのその他の準備
     * 
     */
    public void prepareOther()
    {
        imageAdjuster = new ImageAdjuster(parent);
        diaryFileName = null;
        isRenamedDataFile = false;    	
    }
    
    /**
     *  リスナクラスの終了
     */
    public void finishListener()
    {
    	diaryFileName = null;
    	isRenamedDataFile = false;
    }

    /**
     *   メニューへのアイテム追加
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {

    	// 共有メニューを追加する
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_SHARE_CONTENT, Menu.NONE, parent.getString(R.string.shareContent));
        menuItem.setIcon(android.R.drawable.ic_menu_share);

    	// 編集メニューを追加する
    	menuItem = menu.add(Menu.NONE, MENU_ID_EDIT_TEXT, Menu.NONE, parent.getString(R.string.editText));
        menuItem.setIcon(android.R.drawable.ic_menu_edit);

        // 位置（地図） メニューを追加する
        menuItem = menu.add(Menu.NONE, MENU_ID_SHOW_MAP, Menu.NONE, parent.getString(R.string.showPlace));
        menuItem.setIcon(android.R.drawable.ic_menu_mapmode);
        	
        // 削除メニューを追加する
        menuItem = menu.add(Menu.NONE, MENU_ID_DELETE_FILE, Menu.NONE, parent.getString(R.string.delete));
        menuItem.setIcon(android.R.drawable.ic_menu_delete);

        // 画像挿入メニューを追加する
    	menuItem = menu.add(Menu.NONE, MENU_ID_INSERT_PICTURE, Menu.NONE, parent.getString(R.string.insertPicture));
        menuItem.setIcon(android.R.drawable.ic_menu_gallery);

        // ヒストリメニューを追加する
    	menuItem = menu.add(Menu.NONE, MENU_ID_DATA_HISTORY, Menu.NONE, parent.getString(R.string.revisionHistory));
        menuItem.setIcon(android.R.drawable.ic_menu_recent_history);

        return (menu);
    }

    /**
     *   メニュー表示前の処理
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
     *   メニューのアイテムが選択されたときの処理
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if ((id == MENU_ID_DELETE_FILE)||(id == MENU_ID_EDIT_TEXT)||(id == MENU_ID_DATA_HISTORY))
        {
            // ダイアログを表示する
            parent.showDialog(id);
            return (true);
        }
        else if (id == MENU_ID_SHOW_MAP)
        {
            // オフラインモードかチェックし、オフライン時にはマップ画面を開かないようにする
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            boolean isOffline = preferences.getBoolean("offlineMode", false);
            if (isOffline == true)
            {
                Toast.makeText(parent, parent.getString(R.string.warn_offline), Toast.LENGTH_SHORT).show();
                return (true);
            }

            // データの位置を表示する
            showPlace(id);
            return (true);
        }
        else if (id == MENU_ID_SHARE_CONTENT)
        {
            // "共有" を選択したとき... Intentを発行する
            shareContent();        	
        	return (true);
        }
        else if (id == MENU_ID_INSERT_PICTURE)
        {
        	// 画像挿入を選択したとき...
        	insertPicture();
        	return (true);
        }
        return (false);
    }

    /**
     *  他画面から戻ってきたときの処理...
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
        		    // データが更新できた！ 画像をメイン画面に反映させる
                    ImageView picture  = (ImageView) parent.findViewById(R.id.pictureView);

                	// 画像を表示する
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
     *  ダイアログを生成する
     * @param id
     * @return
     */
    public Dialog onCreateDialog(int id)
    {
        if (id == MENU_ID_DELETE_FILE)
        {
            // 削除するかどうか確認するダイアログを表示する
            return (createConfirmDeleteDialog());
        }
        if (id == MENU_ID_EDIT_TEXT)
        {
        	// 編集ダイアログを表示する
            return (createEditTextDialog());
        }
        if (id == MENU_ID_DATA_HISTORY)
        {
        	// 履歴メッセージダイアログを表示する
            return (createTextViewDialog());
        }
        return (null);    	
    }

    /**
     *  ダイアログ生成後の処理を行う
     * @param id
     * @param dialog
     */
    public void onPrepareDialog(int id, Dialog dialog)
    {
        if (dataHandler == null)
        {
            // ぬるぽ対策...。
        	return;
        }

        if (id == MENU_ID_DELETE_FILE)
        {
            // 削除するかどうか確認するダイアログを表示する
        }
        else if (id == MENU_ID_EDIT_TEXT)
        {
        	// 編集ダイアログを表示する
            prepareEditTextDialog(dialog, dataHandler.getMessageString());
        }
        else if (id == MENU_ID_DATA_HISTORY)
        {
        	// 履歴メッセージダイアログを表示する
            prepareTextViewDialog(dialog, dataHandler.getWholeMessageString());
        }
        return;
    }
    
    
    /**
     *   (ボタンが)クリックされたときの処理
     */
    public void onClick(View v)
    {
        String fileName = "";
        int id = v.getId();
        if (id == R.id.showPreviousDataItem)
        {
            // ひとつ前のデータを表示する
            fileName = dateLineDrawer.moveToPreviousData();
        }
        else if (id == R.id.showNextDataItem)
        {
            // ひとつ次のデータを表示する
            fileName = dateLineDrawer.moveToNextData();
        }
        diaryFileName = fileName;
        
        // データを更新する
        //Log.v(Main.APP_IDENTIFIER, "Next File Name: " + fileName);            
        prepareDiaryDataView(fileName);

    }
    
    /**
     *   触られたときの処理
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        return (false);
    }

    /**
     *  キーを押したときの処理
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        return (false);
    }

    /**
     *  終了準備
     */
    public void shutdown()
    {
    	// ダイアログを閉じる
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
     *  スタート準備
     */
    public void prepareToStart()
    {        
    	// ダイアログを閉じる
    	dismissDialogs();

    	// 表示するファイル名を（フルパスで）取得する
        String fileName = null;
        if (diaryFileName != null)
        {
        	fileName = diaryFileName;
        }
        else
        {
        	fileName = parent.getIntent().getStringExtra(DIARY_FILE);
        }

    	// ハンドラを生成しリセットする
        dataHandler = new DiaryDataHandler();
        dataHandler.resetDatas();

        // 日付部分の描画部分 (文字列もあわせて)
//        if (dateLineDrawer == null)
        {
        	dateLineDrawer = null;
            dateLineDrawer = new DiaryDateLineDrawer();
    	    dateLineDrawer.prepare(fileName);
        }

        // クラスの準備する
        prepareDiaryDataView(fileName);
    }

    /**
     *  ごきげんアイコンを決める
     * @param fileName 表示するデータのファイル名
     * @return アイコンＩＤ
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
     *  数値データを決定する
     * @param fileName
     * @return
     */
    private int getNumberValue(String fileName)
    {
        // 数値データを取得する
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
     *  Activityの準備を行う
     * 
     */
    private void prepareDiaryDataView(String fileName)
    {
        try
        {
            // 数値データを取得する
        	int numberValue = getNumberValue(fileName);

            // アイコンのIdを決定する
            iconId  = getIconId(fileName);

            // 画面下部のデータ位置の情報
        	final TextView dateArea = (TextView) parent.findViewById(R.id.dateTimeArea);
        	dateArea.setText(dateLineDrawer.getDateTimeString());

        	// グラフ部分の領域
            final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.DateTimeDataView);
            view.setCanvasDrawer(dateLineDrawer);

            // アイコンを表示する
            final ImageView iconArea = (ImageView) parent.findViewById(R.id.showEmotionIcon);
            iconArea.setImageResource(iconId);

            // 数値を表示する
            final TextView numberTitle = (TextView) parent.findViewById(R.id.numberValueTitle);
            final TextView numberArea = (TextView) parent.findViewById(R.id.showNumberValue);
            if (numberValue != 0)
            {
                // 数値が設定されている場合は表示する
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
                numberTitle.setVisibility(View.VISIBLE);
                numberArea.setText("" + numberValue + preferences.getString("amountUnit", ""));
            }
            else
            {
                // 数値が設定されていない場合には何も表示しない
                numberTitle.setVisibility(View.INVISIBLE);
                numberArea.setText("");
            }

            // データファイルをパースして変数に分解する
            boolean ret = readDataFileContents(fileName, dataHandler);
            if (ret == false)
            {
                // データファイル読み出し失敗...
                Log.v(Main.APP_IDENTIFIER, "PARSE ERROR :" + fileName);
                return;
            }

            // 保存時間の設定
            TextView savedTime = (TextView) parent.findViewById(R.id.dateInfo);
            savedTime.setText(dataHandler.getSavedTimeString());

            // 位置情報（のタイトル）の設定
            TextView scanedTime = (TextView) parent.findViewById(R.id.locationTitle);
            scanedTime.setText(parent.getString(R.string.savedLocation) + " " + "(" + dataHandler.getScanedTimeString() + ")");

            // 位置情報の設定
            TextView location  = (TextView) parent.findViewById(R.id.showLocation);
            location.setText(dataHandler.getLocationString());            

            // レート情報の設定
            RatingBar ratings  = (RatingBar) parent.findViewById(R.id.showRate);
            ratings.setRating(dataHandler.getRatingValue());

            // メッセージの設定
            TextView  comment  = (TextView)  parent.findViewById(R.id.showComment);
            comment.setText(dataHandler.getMessageString());

            // 画像の設定
            String pictureString = dataHandler.getPictureString();
            ImageView picture  = (ImageView) parent.findViewById(R.id.pictureView);
            if (pictureString != null)
            {
            	// 画像データの表示エリアを表示する
            	picture.setVisibility(View.VISIBLE);

            	// 指定された画像データを表示する
                imageAdjuster.setImage(picture, pictureString);
            }
            else
            {
            	// 画像データの表示エリアを消す
            	picture.setVisibility(View.GONE);
            }
            
            // マーカー部分を再描画する
            final GokigenSurfaceView surfaceView = (GokigenSurfaceView) parent.findViewById(R.id.DateTimeDataView);
            surfaceView.doDraw();
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EXCEPTION :" + ex.getMessage());
        }
    }

    /**
     *  画像ファイルの挿入 (データファイルの更新)
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
     *  ファイルを読み出す (SAXパーサを使用する)
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
     *  データの位置を表示する
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
     *   データを共有する！
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
        		// 位置情報を入れる
        		if ((currentLatitude != 0.0)&&(currentLongitude != 0.0))
        		{
        		    message = message + " " + "Location:" + currentLatitude + "," + currentLongitude;
        		}
        	}
        	else if (useLocation == 2)
        	{
        		// 位置情報を入れる
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
     *  メッセージを更新する
     *
     * @param fileName  更新（追記）するファイル名
     * @param messageToRevise  更新するメッセージ
     * @return
     */
    private boolean appendRevisedMessage(String fileName, String messageToRevised, String mediaUriToRevised)
    {
        String oldFileName = null;
    	InputStream is = null;
    	OutputStream os = null;
        try
        {
            // 編集前のファイルを(リネームして)保存する。
        	File targetFile = new File(fileName);
            File readFile = new File(fileName + "~");

            // データファイルを一括で読み出す。(１件のデータは、せいぜい数百kBと予想しているから。。。）
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
				// offsetが読み込んだデータサイズ...
			}

			Calendar calendar = Calendar.getInstance();
            SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // ファイルを書き換える。（末尾に今回更新したメッセージを入れる）
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
            	
                // ファイル名を変更する準備 (画像添付なし⇒添付ありに変わった場合)
                try
                {
                	oldFileName = diaryFileName;
                    diaryFileName = fileName.replace("N_", "P_");  // 未共有
                    diaryFileName = diaryFileName.replace("S_", "Q_");  // 共有
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
     *  データファイルのファイル名を更新(変更)する
     * 
     */
    private void renameFile(String oldFileName, String newFileName)
    {
    	try
        {
    		
        	if ((oldFileName != null)&&(isRenamedDataFile == false)&&(oldFileName.matches(newFileName) != true))
        	{
        		// ファイル名を修正する
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
     *  ダイアログ(群)を閉じる
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
     *  メッセージ編集ダイアログの表示
     * 
     * @param message
     * @return
     */
    private void prepareTextViewDialog(Dialog layout, String message)
    {
        // 現在の入力データをダイアログに格納する
        final TextView  revisionText = (TextView)  layout.findViewById(R.id.revisionText);
        revisionText.setText(message);
    }
    	
    /**
     *  メッセージ編集ダイアログの表示
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
     *  メッセージ編集ダイアログの文字列を設定する
     *     
     * @param layout
     * @param message
     */
    private void prepareEditTextDialog(Dialog layout, String message)
    {
        // 現在の入力データをダイアログに格納する
        final TextView  editComment = (TextView)  layout.findViewById(R.id.editTextArea);
        editComment.setText(message);
    }
    
    /**
     *  メッセージ編集ダイアログの表示
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
                    		  // データが更新できた！ メイン画面に反映させる
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
     *   ファイルの削除を確認するダイアログ
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
