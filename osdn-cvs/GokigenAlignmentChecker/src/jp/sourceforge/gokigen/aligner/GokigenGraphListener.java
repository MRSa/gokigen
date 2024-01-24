package jp.sourceforge.gokigen.aligner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Camera;
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


/**
 *  ごきげんグラフ画面の処理クラス
 * 
 * @author MRSa
 *
 */
public class GokigenGraphListener  implements OnClickListener, OnTouchListener, OnKeyListener, ICanvasDrawer, ICameraDataReceiver, IBitmapWriterCallback
{
    // カメラ画像のキャプチャ実施    
    private boolean isCapture = false;
    private boolean dataWriting = false;

    // 利用するクラス
    private Activity parent = null;                           // 親分のクラス
    private IGokigenGraphDrawer    currentGraphDrawer = null; // 描画に使用している描画クラス
    private ExternalStorageFileUtility fileUtility = null;    // ファイルを出力するときに使うクラス
    
    private BitmapWriter bmpWriter = null;                    // 画像を出力するクラス
    private String   writingMessage = null;

    /**
     *   コンストラクタ
     * @param arg
     */
    public GokigenGraphListener(Activity arg)
    {
        parent = arg;

        // 実際に描画に使用するクラスを設定する
        currentGraphDrawer = new GokigenScaleDrawer(arg);
        currentGraphDrawer.prepare();

        fileUtility = new ExternalStorageFileUtility(Main.APP_BASEDIR);    

        try
        {
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            String shapeStr = preferences.getString("showShapeType", "0");
            int shapeType = Integer.parseInt(shapeStr);
            Log.v(Main.APP_IDENTIFIER, "showShapeType : " + shapeType);
            currentGraphDrawer.setDrawType(shapeType);    
        }
        catch (Exception ex)
        {
        	// なにもしない
        	Log.v(Main.APP_IDENTIFIER, "ERR>" + ex.toString());
        }
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
            // データを構築し、画面の表示を更新する
            //prepareGokigenData();
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
        view.setOnClickListener(this);
        view.setOnTouchListener(this);
        view.setPixelFormat(PixelFormat.TRANSLUCENT);
        
        // カメラ表示の領域
        final CameraViewer cameraCanvas = (CameraViewer) parent.findViewById(R.id.CameraView);
        cameraCanvas.setOnClickListener(this);
        cameraCanvas.setPreviewCallback(this);
        cameraCanvas.setOnTouchListener(this);
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
       	Log.v(Main.APP_IDENTIFIER, "GokigenGraphListener::onClick()");

       	//int id = v.getId();

       	// データを(再)描画する
       	redrawScreen();
     }
    
    /**
     *  画面の再描画を実行
     * 
     */
    private void redrawScreen()
    {
        // グラフの再描画を指示する
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.doDraw();         
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
        Log.v(Main.APP_IDENTIFIER, "onTouchEvent() :" + action);

        if (action == MotionEvent.ACTION_DOWN)
        {
            // 画面キャプチャ指示
        	isCapture = true;
            return (true);
        }
        return (ret);
    }

    /**
     *  キー入力を拾う
     * 
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	boolean ret = false;
        Log.v(Main.APP_IDENTIFIER, "GokigenGraphListener::onKeyDown() :" + keyCode);

        int action = event.getAction();
        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {
            // 画面キャプチャ指示
        	isCapture = true;        	
        	return (true);
        }
    	return (ret);
    }

    /**
     *  トラックボールが動かされたときの処理
     * 
     * @param event
     * @return
     */
    public boolean onTrackballEvent(MotionEvent event)
    {
        // ここでは何もしない
        return (false);
    }

    /**
     *   触られたときの処理
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // Log.v(Main.APP_IDENTIFIER, "onTouch()");
        return (onTouchEvent(event));
    }

    /**
     *  キーを押したときの操作
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
       	//Log.v(Main.APP_IDENTIFIER, "onKey() : " + keyCode);
       	return (onKeyDown(keyCode, event));
    }

    /**
     *   メニューへのアイテム追加
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {
        return (menu);
    }
    
    /**
     *   メニュー表示前の処理
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
        return;
    }

    /**
     *   メニューのアイテムが選択されたときの処理
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return (false);
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
    		//canvas.drawColor(Color.BLACK);
            currentGraphDrawer.drawOnCanvas(canvas, 0);
    	}
    	catch (Exception ex)
    	{
    		// 例外発生...でもそのときには何もしない
    		Log.v(Main.APP_IDENTIFIER, "drawOnCanvas()" + ex.getMessage());
    	}
    	return;
    }

    /**
     *  画像情報の取得
     * 
     */
    public void onPreviewFrame(byte[] arg0, Camera arg1, int width, int height)
    {
    	if ((isCapture == true)&&((dataWriting == false))&&(arg0 != null))
    	{
            dataWriting = true;
            bmpWriter = null;
    		bmpWriter = new BitmapWriter(parent, fileUtility, this, arg0, width, height);
    		writingMessage = "WRITE";
    		bmpWriter.execute(writingMessage);
    	}
    }

    /**
     *  画像データの書き込み途中...
     */
    public void onProgressUpdate()
    {
    	// 書き込み中表示を行う
    	currentGraphDrawer.setMessage(parent.getString(R.string.capturing));
    	
        // グラフの再描画を指示する
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.doDraw();    	
    }
    
    /**
     *  画像データの書き込み終了...
     */
    public void finishedWrite(boolean result)
    {
    	currentGraphDrawer.setMessage(parent.getString(R.string.captured));
    	
        // グラフの再描画を指示する
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.doDraw();         

        // 書き込み結果を応答する
    	Log.v(Main.APP_IDENTIFIER, "DATA WRITE DONE : " + result);
    	dataWriting = false;
		isCapture = false;
	    parent.finish();
	    System.gc();
        return;
    }
}
