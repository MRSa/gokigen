package jp.sfjp.gokigen.okaken;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class ResultActivity extends Activity implements ClockTimer.ITimeoutReceiver, IActivityOpener, OnClickListener
{
	private ClockTimer myTimer = null;
	private Timer timer = null;
    private static final long duration = 500;   // 500ms
    private ResultDrawer drawer = null;

    private IResultProvider resultProvider = null;
	private List<SymbolListArrayItem> listItems = null;
	
	private DetailDialog detailDialog = null;
	private SymbolListArrayItem itemToShowDetail = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** 全画面表示にする **/
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /** タイトルを消す **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Preferenceを取得する
        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	// String colorString = preferences.getString("backgroundColor", "0xff004000");

        // 画面表示のレイアウトを設定する
        setContentView(R.layout.result);

        // Intentから結果データを読み出す
        QuestionnaireProvider questionProvider = null;
        try
        {
        	questionProvider = getIntent().getParcelableExtra(Gokigen.APP_INFORMATION_STORAGE);
        	resultProvider = questionProvider;
        }
        catch (Exception ex)
        {
            Log.v(Gokigen.APP_IDENTIFIER, "EXCEPTION(questionProvider) :" + ex.toString() + "  " + ex.getMessage());
        }

        // 結果データの反映
        deployDataToList();

        // 詳細データ表示用Dialogの作成
        detailDialog = new DetailDialog(this);
        
        // 画面描画クラスの設定
        drawer = new ResultDrawer(this, questionProvider);
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) findViewById(R.id.ResultView);
        surfaceView.setCanvasDrawer(drawer);
    
    }

    /**
     *  メニューの生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add(Menu.NONE, Gokigen.MENU_ID_ABOUT, Menu.NONE, getString(R.string.about_gokigen));
    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);
        menuItem = menu.add(Menu.NONE, Gokigen.MENU_ID_SHARE, Menu.NONE, getString(R.string.share_content));
        menuItem.setIcon(android.R.drawable.ic_menu_share);    // 共有...
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *  メニューアイテムの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = false;
        switch (item.getItemId())
        {
          case Gokigen.MENU_ID_ABOUT:
        	// Aboutメニューが選択されたときは、クレジットダイアログを表示する
        	showDialog(R.id.info_about_gokigen);
            result = true;
            break;

          case Gokigen.MENU_ID_SHARE:
        	//  共有メニューが呼び出された
        	shareContent();
        	result = true;
        	break;
          default:
            result = false;
            break;
        }
        return (result);
    }
    
    /**
     *  メニュー表示前の処理
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(Gokigen.MENU_ID_ABOUT).setVisible(true);
        return (super.onPrepareOptionsMenu(menu));
    }

    /**
     *  画面が裏に回ったときの処理
     */
    @Override
    public void onPause()
    {
        super.onPause();
        stopTimer();

    }
    
    /**
     *  画面が表に出てきたときの処理
     */
    @Override
    public void onResume()
    {
        super.onResume();
        startTimer();
    }
    
    /**
     *  子画面から応答をもらったときの処理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            // 子画面からもらった情報の応答処理
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
        }
    } 

    /**
     *  ダイアログ表示（初回）の準備
     * 
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == R.id.info_about_gokigen)
	    {
        	// クレジットダイアログを表示
		    CreditDialog dialog = new CreditDialog(this);
		    return (dialog.getDialog());
	    }
        else if (id == R.id.detail_dialog)
	    {
        	// 詳細ダイアログを表示
		    return (detailDialog.getDialog());
	    }
    	return (null);
    }

    /**
     *  ダイアログ表示の準備
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
        if (id == R.id.info_about_gokigen)
	    {
            // クレジットダイアログを表示するときには何もしない。
        	return;
	    }
        else if (id == R.id.detail_dialog)
	    {
        	// 詳細ダイアログの表示を更新する
        	detailDialog.onPrepareDialog(dialog, itemToShowDetail);
		    return;
	    }
    	// ダイアログ情報を更新する場合には、ここに追加する
    	return;
    }  
    /**
     *   タイムアウト受信時の処理...
     * 
     */
    public void receivedTimeout()
    {
        // Log.v(Gokigen.APP_IDENTIFIER, "receivedTimeout()");    

    	// 画面の再描画指示。。。（0.5secおき？）
    	final GokigenSurfaceView surfaceView0 = (GokigenSurfaceView) findViewById(R.id.ResultView);
    	surfaceView0.doDraw();
    }

    /**
     *   クリックされたときの処理
     */
    public void onClick(View v)
    {
        //int id = v.getId();

    	// 画面の再描画指示。。。（0.5secおき？）
    	final GokigenSurfaceView surfaceView1 = (GokigenSurfaceView) findViewById(R.id.ResultView);
    	surfaceView1.doDraw();

    }
    
    /**
     *  Activityを切り替える
     * 
     * @param fileName
     */
    public void requestToStartActivity(int id)
    {
        try
        {
            // Activityを起動する
            Intent intent = new Intent(this, jp.sfjp.gokigen.okaken.MoleGameActivity.class);
            startActivityForResult(intent, id);
        }
        catch (Exception ex)
        {
        	// 例外発生時...
        }
    }
    
    /**
     * 
     * 
     */
    private void stopTimer()
    {
        try
        {
            // TODO: 動作を止めるようイベント処理クラスに指示する
        	if (timer != null)
        	{
        		timer.cancel();
        		timer = null;
        	}
           	myTimer = null;
        }
        catch (Exception ex)
        {
            // 何もしない
        }    	
    }
    
    /**
     * 
     * 
     */
    private void startTimer()
    {
        try
        {
        	if (timer != null)
        	{
        		timer.cancel();
        		timer = null;
        	}
        	timer = new Timer();
        	
        	// タイマータスクの準備
           	myTimer = null;
        	myTimer = new ClockTimer(this);
        	timer.scheduleAtFixedRate(myTimer, duration, duration);
        }
        catch (Exception ex)
        {
            // なにもしない
        }    	
    }

    /**
     *    選択したアイテムを表示する
     * 
     * @param item
     */
    private void showDetailData(SymbolListArrayItem item)
    {
    	try
    	{
    		itemToShowDetail = item;
    		showDialog(R.id.detail_dialog);
    	}
    	catch (Exception ex)
    	{
        	// データが取れない...なのでここで折り返す
	    	Log.v(Gokigen.APP_IDENTIFIER, "ResultActivity::showDetailData() : exception " + ex.toString() );    		
    		itemToShowDetail = null;
    	}
    }

    /**
     *    データを一覧に表示する
     * 
     */
	private void deployDataToList()
	{
		try
		{
	        // リストに表示するアイテムを生成する
	        listItems = null;
	        listItems = new ArrayList<SymbolListArrayItem>();
	        
	        if (resultProvider == null)
	        {
	        	// データが取れない...なのでここで折り返す
		    	Log.v(Gokigen.APP_IDENTIFIER, "ResultActivity::deployDataToList() : resultProvider is null." );
	        	return;
	        }

	        // 回答したデータを全て取得してコンテナに入れる
	        int count = resultProvider.getNumberOfAnsweredQuestions();
	        for (int index = 0; index < count; index++)
	        {
	        	 SymbolListArrayItem listItem = resultProvider.getAnsweredInformation(this, index);
	        	 listItems.add(listItem);
	        }

            // リストアダプターを生成し、設定する
            ListView listView = (ListView) findViewById(R.id.ResultListView);
            ListAdapter adapter = new SymbolListArrayAdapter(this,  R.layout.listarrayitems, listItems);
            listView.setAdapter(adapter);

            // リスト内のアイテムを選択したときの処理を登録する
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //@Override
                public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
                {
                    ListView listView = (ListView) parentView;
                    SymbolListArrayItem item = (SymbolListArrayItem) listView.getItemAtPosition(position);

                    /// リストが選択されたときの処理...データを開く
                    showDetailData(item);
                }
            });
            System.gc();   // いらない（参照が切れた）クラスを消したい            

		} catch (Exception ex)
	    {
	        // 例外発生...ログを吐く
	    	Log.v(Gokigen.APP_IDENTIFIER, "ResultActivity::deployDataToList() : " + ex.toString());
	    }
	}
	
	/**
	 *    結果を共有する...
	 * 
	 */
	private void shareContent()
	{
        int score = 0;
		String message = "";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        try
        {
        	if (resultProvider != null)
        	{
        		score = (int) ((float) resultProvider.getScore(0) * 100.0f);
            	if (drawer != null)
            	{
            		message = getString(R.string.app_name) + getString(R.string.share_message) + "\n- - - - -\n";
            		message = message + drawer.analysisResult(resultProvider) + "\n- - - - -\n";
            	}
            	message = message + getString(R.string.app_credit) + "\n\n";
        	}
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " (" + getString(R.string.game_score) +  score + getString(R.string.game_pts) + ")");
            intent.putExtra(Intent.EXTRA_TEXT, message + "  (" +   score + getString(R.string.game_pts) + ")");
/*
            String pictureString = null;
            if (pictureString != null)
            {
            	try
            	{
                	intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
                	intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, ImageAdjuster.parseUri(pictureString));
                    Log.v(Gokigen.APP_IDENTIFIER, "Attached Pic.:" + pictureString);
            	}
            	catch (Exception ee)
            	{
            		// 
                    Log.v(Gokigen.APP_IDENTIFIER, "attach failure : " + pictureString + "  " + ee.getMessage());
            	}
            }
*/
            startActivityForResult(intent, Gokigen.MENU_ID_SHARE);          	
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.v(Gokigen.APP_IDENTIFIER, "xxx : " + e.getMessage() + ", " + message);
        }
	}
}
