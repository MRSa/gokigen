package jp.sourceforge.gokigen.diary;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;

import android.os.Handler;
import android.os.Message;
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
 *  ごきげんグラフ画面の処理クラス
 * 
 * @author MRSa
 *
 */
public class GokigenGraphListener  implements OnClickListener, OnTouchListener, OnKeyListener, ICanvasDrawer
{
    // メニュー
	public static final int MENU_ID_SHOW_DAILY = (Menu.FIRST + 1);   // 日ごと集計
    public static final int MENU_ID_SHOW_WEEKLY = (Menu.FIRST + 2);  // 週ごと集計
    public static final int MENU_ID_SHOW_MONTHLY = (Menu.FIRST + 3); // 月ごと集計
    
    // 集計タイプ
    public static final int REPORTTYPE_DAILY = 10;   // 日ごと集計モード
    public static final int REPORTTYPE_WEEKLY = 11;  // 週ごと集計モード
    public static final int REPORTTYPE_MONTHLY = 12; // 月ごと集計モード
    private int reportType = REPORTTYPE_DAILY;     // 現在の集計タイプ

    // フリックアクションの区別
    private static final int FLICK_NOTHING = 0;               // 不明
    private static final int FLICK_UP = 1;                    // 上方向に
    private static final int FLICK_DOWN = 2;                  // 下方向に
    private static final int FLICK_LEFT= 3;                   // 左方向に
    private static final int FLICK_RIGHT = 4;                 // 右方向に
    //private static final int FLICK_DIAGONAL_LEFT_DOWN = 5;    // 左上から右下へ斜め方向に
    //private static final int FLICK_DIAGONAL_RIGHT_DOWN = 6;   // 右上から左下へ斜め方向に
    //private static final int FLICK_DIAGONAL_LEFT_UP = 7;      // 左下から右上へ斜め方向に
    //private static final int FLICK_DIAGONAL_RIGHT_UP = 8;     // 右下から左上へ斜め方向に

    // フリック検出用ワーク変数
    private float onTouchPosX = 0;
    private float onTouchPosY = 0;
    private boolean flicking = false;
    
    // 表示データの年・月・日
    private int showYear = 0;
    private int showMonth = 0;
    private int showDay = 0;
    
    // 利用するクラス
    private Activity parent = null;                           // 親分のクラス
    private ProgressDialog progressDialog = null;             // 実行中ダイアログ表示クラス

    private GokigenGraphDataHolder gokigenDataHolder = null;  // データ保持クラス
    
    private IGokigenGraphDrawer    currentGraphDrawer = null; // 描画に使用している描画クラス
    private IGokigenGraphDrawer    pieChartDrawer = null;     // 円グラフ描画クラス
    private IGokigenGraphDrawer    lineChartDrawer = null;    // 折れ線グラフ描画クラス
    private IGokigenGraphDrawer    barChartDrawer = null;     // 棒グラフ描画クラス

    /**
     *   コンストラクタ
     * @param arg
     */
    public GokigenGraphListener(Activity arg)
    {
        parent = arg;

        // プログレスダイアログの生成
        progressDialog = new ProgressDialog(parent);

        gokigenDataHolder = new GokigenGraphDataHolder();        
        pieChartDrawer = new GokigenPieChart(parent);
        lineChartDrawer = new GokigenLineChart(parent);
        barChartDrawer = new GokigenBarChart(parent);

        // 実際に描画に使用するクラスを設定する
        currentGraphDrawer = lineChartDrawer;
        currentGraphDrawer.prepare();
    }

    /**
     *  起動時にデータを準備する
     * 
     * @param myIntent
     */
    public void prepareExtraDatas(Intent myIntent)
    {
        try
        {
            // Intentで拾ったデータを読み出す (初期化データ)
            showYear = myIntent.getIntExtra(GokigenGraph.TARGET_YEAR, 2010);
            showMonth = myIntent.getIntExtra(GokigenGraph.TARGET_MONTH, 9);
            showDay = myIntent.getIntExtra(GokigenGraph.TARGET_DAY, 10);

            // Preferenceに記憶されたデータがあればそれを反映させる（こっちが本命データ）
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            showYear = preferences.getInt("graphYear", showYear);
            showMonth = preferences.getInt("graphMonth", showMonth);
            showDay = preferences.getInt("graphDay", showDay);

            // データを構築し、画面の表示を更新する
            prepareGokigenData();
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EXCEPTION :" + ex.getMessage());
        }        
    }
    
    /**
     *  がっつりこのクラスにイベントリスナを接続する (画面が表にきた時の処理)
     * 
     */
    public void prepareListener()
    {
        // グラフ部分の領域
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.setCanvasDrawer(this);
        
        // 前のデータ表示
        final ImageButton previous = (ImageButton) parent.findViewById(R.id.changePrevious);
        previous.setOnClickListener(this);

        // 次のデータ表示
        final ImageButton next = (ImageButton) parent.findViewById(R.id.changeNext);
        next.setOnClickListener(this);

        // 円グラフ表示        
        final ImageButton graphStyle = (ImageButton) parent.findViewById(R.id.changeGraphStyleButton);
        graphStyle.setOnClickListener(this);

        // 棒グラフ表示
        final ImageButton barChart = (ImageButton) parent.findViewById(R.id.changeBargraphStyleButton);
        barChart.setOnClickListener(this);

        // 折れ線グラフ表示
        final ImageButton lineChart = (ImageButton) parent.findViewById(R.id.changeLinegraphStyleButton);
        lineChart.setOnClickListener(this);
    }

    /**
     *  画面が裏に回ったときの処理
     * 
     */
    public void finishListener()
    {
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
     *  集計用データを読み込む
     * 
     */
    private void prepareGokigenData()
    {
        //  プログレスダイアログ（「Loading...」）を表示する。
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(parent.getString(R.string.dataLoading));
        progressDialog.setCancelable(false);
        progressDialog.show();
    	
        /**
         *  プログレスダイアログ表示中に実施する処理の記述
         *  (secCancelable(false)にしているため、ちゃんとrun()を終わらないと、
         *   ANR以外の終了方法はなくなるので注意。)
         */
        Thread thread = new Thread(new Runnable()
        {  
            public void run()
            {
            	try
            	{
                    // データを取得して準備する
            		gokigenDataHolder.parseGokigenItems(reportType, showYear, showMonth, showDay);
            		handler.sendEmptyMessage(0);
            	}
            	catch (Exception ex)
            	{
            		Log.v(Main.APP_IDENTIFIER, "run() :" + ex.getMessage() + " " + showYear + "/" + showMonth + "/" + showDay);
            	}
            }

            /**
             *   画面の更新(プログレスダイアログ表示終了の処理)
             */
            private final Handler handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                	// ここで、画面への描画指示を出す
                	redrawScreen();
                    progressDialog.dismiss();
                }
            };   
        });
        try
        {
            thread.start();
        }
        catch (Exception ex)
        {
            // 例外処理は何もしない
        }
    }

    /**
     *  他画面から戻ってきたとき...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // 反応が遅くなるかもしれないが、、、ガベコレを実施する。
        System.gc();
    }

    /**
     *   クリックされたときの処理
     */
    public void onClick(View v)
    {
        int id = v.getId();

        int dataMoveCount = 0;
        if (id == R.id.changeGraphStyleButton)
        {
            // 円グラフにする
            currentGraphDrawer = pieChartDrawer;
            currentGraphDrawer.prepare();
        }
        else if (id == R.id.changeLinegraphStyleButton)
        {
            // 折れ線グラフにする
        	currentGraphDrawer = lineChartDrawer;
            currentGraphDrawer.prepare();
        }
        else if (id == R.id.changeBargraphStyleButton)
        {
            // 棒グラフにする
            currentGraphDrawer = barChartDrawer;
            currentGraphDrawer.prepare();
        }
        else if (id == R.id.changeNext)
        {
            // 1つ後ろのデータに表示を変更する
            dataMoveCount = 1;
        }
        else if (id == R.id.changePrevious)
        {
            // 1つ前のデータに表示を変更する
            dataMoveCount = -1;
        }
        
        // データを(再)描画する
        changeViewData(dataMoveCount);
    }
    
    /**
     *  グラフ描画する年月日を一時記憶する
     * 
     * @param year   記憶する年
     * @param month  記憶する月
     * @param day    記憶する日
     */
    private void storeGraphToShow(int year, int month, int day)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("graphYear",  year);
        editor.putInt("graphMonth", month);
        editor.putInt("graphDay",   day);
        editor.commit();        
    }

    /**
     *  ラベルに表示する（年・月・日の）文字列を生成する
     * @return ラベル文字列
     */
    private String getLabelString()
    {
    	String labelString = "";
        switch (reportType)
        {
          case REPORTTYPE_MONTHLY:
            // 月次データ     
            labelString = " " + showYear + "/" + showMonth + " ";
      	    break;

          case REPORTTYPE_WEEKLY:
            // 週ごとデータ
        	
      	    break;

          case REPORTTYPE_DAILY:
          default:
            // 日ごとデータ
            labelString = " " + showYear + "/" + showMonth + "/" + showDay + " ";
        	break;
        }
        return (labelString);
    }
    
    /**
     *  画面の再描画を実行
     * 
     */
    private void redrawScreen()
    {
        // ラベルの再描画
    	final TextView infoArea = (TextView) parent.findViewById(R.id.GokigenInfo);
        infoArea.setText(getLabelString());

        // グラフの再描画を指示する
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.doDraw();         
    }

    /**
     *   表示しているグラフを更新する
     *   
     * @param addValue
     */
    private void changeViewData(int value)
    {
        if (value == 0)
        {
            // 値が指定されていない場合には、画面の再描画のみ実行する
        	redrawScreen();
        	return;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.set(showYear, (showMonth - 1), showDay);
        switch (reportType)
        {
          case REPORTTYPE_MONTHLY:
            // 月次データ     
            calendar.add(Calendar.MONTH, value);
      	    break;

          case REPORTTYPE_WEEKLY:
            // 週ごとデータ
            calendar.add(Calendar.DATE, (value * 7));
      	    break;

          case REPORTTYPE_DAILY:
          default:
            // 日ごとデータ
            calendar.add(Calendar.DATE, value);
        	break;
        }

        // 日付データを取得しなおす
        showYear = calendar.get(Calendar.YEAR);
        showMonth = calendar.get(Calendar.MONTH) + 1;
        showDay = calendar.get(Calendar.DATE);

        // 出力する日を設定する
        storeGraphToShow(showYear, showMonth, showDay);
        
        // ごきげんデータを読み直す...
        prepareGokigenData();
    }

    /**
     *    画面(GokigenSurfaceView)を触られたときの処理
     *    (フリックアクションの検出を行う。)
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean ret = false;
        int action = event.getAction() & 0x000000ff;
        float currentPositionX = event.getX();
        float currentPositionY = event.getY();
        switch (action)
        {
          case MotionEvent.ACTION_DOWN:
            // タッチをはじめた
            onTouchPosX = currentPositionX;
            onTouchPosY = currentPositionY;
            flicking = false;
            ret = true;
            break;

          case MotionEvent.ACTION_UP:
            // タッチが離された...フリック動作の確認と実行
        	doFlickAction(flicking, onTouchPosX, currentPositionX, onTouchPosY, currentPositionY);
            flicking = false;
            ret = true;
            break;

          case MotionEvent.ACTION_CANCEL:
            // タッチがキャンセルされた...フリック動作の確認と実行
        	doFlickAction(flicking, onTouchPosX, currentPositionX, onTouchPosY, currentPositionY);
            flicking = false;
            ret = true;
            break;

          case MotionEvent.ACTION_MOVE:
            // タッチしながら移動      
            flicking = true;
            ret = true;
            break;

          default:
            // 何もしない
            break;
        }
        return (ret);
    }
    
    /**
     *   フリックアクションの種別を判定する
     * @param startX  X軸移動開始位置
     * @param endX    X軸移動終了位置
     * @param startY  Y軸移動開始位置
     * @param endY    Y軸移動終了位置
     * @return        フリックアクションの結果
     */
    private int decideFlickActionType(float startX, float endX, float startY, float endY)
    {
    	int flickType = FLICK_NOTHING;
    	
    	float deltaX = endX - startX;
    	float deltaY = endY - startY;
    	
        if (Math.abs(deltaX) > Math.abs(deltaY))
        {
    	    // X軸側の移動距離が大きい場合 (左右)
        	flickType = (deltaX <= 0) ? FLICK_LEFT: FLICK_RIGHT;
        }
        else
        {
        	// Y軸側の移動距離が大きい場合(上下)
        	flickType = (deltaY <= 0) ? FLICK_UP : FLICK_DOWN;
        }
        return (flickType);
    }

    /**
     *  タッチアクション（フリックアクション）にあわせた処理を実行する
     * 
     * @param isFlick
     * @param prevPosX
     * @param nextPosX
     */
    private void doFlickAction(boolean isFlick, float prevPosX, float nextPosX, float prevPosY, float nextPosY)
    {
        if (isFlick == false)
        {
            // タッチしたまま移動していない場合には、なにもしない
            return;
        }

        // フリックアクションの種別を判定
        int actionType = decideFlickActionType(prevPosX, nextPosX, prevPosY, nextPosY);
        boolean needMove = false;
        int dataMoveCount = 0;
        switch (actionType)
        {
          case FLICK_UP:  // 上方向に
            currentGraphDrawer.actionZoomIn();
          	break;

          case FLICK_DOWN: // 下方向に
          	currentGraphDrawer.actionZoomOut();
          	break;

          case FLICK_LEFT: // 左方向に
        	needMove = currentGraphDrawer.actionShowNextData();
        	if (needMove == true)
        	{
        		// データをひとつ次のものへ移動させる
        		dataMoveCount = 1;
        	}
        	break;

          case FLICK_RIGHT: // 右方向に
        	needMove = currentGraphDrawer.actionShowPreviousData();
        	if (needMove == true)
        	{
        		// データをひとつ前のものへ移動させる
        		dataMoveCount = -1;
        	}
          	break;

          case FLICK_NOTHING:
          default:
            // 未対応の操作...
        	return;
        }
        
        // 表示データを更新する
		changeViewData(dataMoveCount);
        return;
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
            //
        }
        return (false);
    }

    /**
     *  キーを押したときの操作
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        int action = event.getAction();
        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {
        	//
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
        // 日ごと集計
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_SHOW_DAILY, Menu.NONE, parent.getString(R.string.showDaily));
        menuItem.setIcon(android.R.drawable.ic_menu_day);

        // 週ごと集計
        //menuItem = menu.add(Menu.NONE, MENU_ID_SHOW_WEEKLY, Menu.NONE, parent.getString(R.string.showWeekly));
        //menuItem.setIcon(android.R.drawable.ic_menu_week);

        // 月ごと集計
        menuItem = menu.add(Menu.NONE, MENU_ID_SHOW_MONTHLY, Menu.NONE, parent.getString(R.string.showMonthly));
        menuItem.setIcon(android.R.drawable.ic_menu_month);

        return (menu);
    }
    
    /**
     *   メニュー表示前の処理
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(MENU_ID_SHOW_DAILY).setVisible(true);    // 日ごと集計
        //menu.findItem(MENU_ID_SHOW_WEEKLY).setVisible(true);    // 週ごと集計
        menu.findItem(MENU_ID_SHOW_MONTHLY).setVisible(true);  // 月ごと集計
        return;
    }

    /**
     *   メニューのアイテムが選択されたときの処理
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);

        boolean result = false;
        switch (item.getItemId())
        {
          case MENU_ID_SHOW_DAILY:
            // 日ごと集計モード
            reportType = REPORTTYPE_DAILY;
            result = true;
            break;

          case MENU_ID_SHOW_WEEKLY:
            // 週次集計モード
            reportType = REPORTTYPE_WEEKLY;
            result = true;
            break;

          case MENU_ID_SHOW_MONTHLY:
            // 月次集計モード
            reportType = REPORTTYPE_MONTHLY;
            result = true;
            break;

          default:
        	// その他...
            result = false;
            break;
        }
        if (result == true)
        {
            // モードが切り替わった場合、データを読み直して、画面を再描画する
            prepareGokigenData();
            view.doDraw();
        }
        return (result);
    }

    /**
     *  キャンバスにデータを描画する
     * 
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	try
    	{
    		// 画面を塗りつぶしてから再描画を実行
    		canvas.drawColor(Color.BLACK);
            currentGraphDrawer.drawOnCanvas(canvas, reportType, gokigenDataHolder);
    	}
    	catch (Exception ex)
    	{
    		// 例外発生...でもそのときには何もしない
    	}
    	return;
    }
}
