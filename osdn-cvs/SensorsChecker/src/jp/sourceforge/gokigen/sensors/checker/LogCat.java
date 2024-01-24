package jp.sourceforge.gokigen.sensors.checker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class LogCat extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ///** 全画面表示にする **/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ///** タイトルを消す **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /** リスナクラスの準備 **/
        //listener.prepareListener();

        /** 画面の準備 **/
        setContentView(R.layout.logcat);
        
        /** クラスの準備する **/
        prepareMyActivity();
    }

    /**
     *  メニューの生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *   メニュー表示前の処理
     * @param menu
     * @return
     */
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return (super.onPrepareOptionsMenu(menu));
    }
    /**
     *  メニューアイテムの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return (super.onOptionsItemSelected(item));
    }

    /**
     *  画面が裏に回ったときの処理
     */
    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     *  画面が表に出てきたときの処理
     */
    @Override
    public void onResume()
    {
        super.onResume();
    }

    /**
     * 
     * 
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    /**
     * 
     */
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    /**
     * 
     */
    @Override
    protected void onStop()
    {
        super.onStop();
    }

    /**
     *  子画面から応答をもらったときの処理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
/**
        try
        {
            // 子画面からもらった情報の応答処理をイベント処理クラスに依頼する
            listener.onActivityResult(requestCode, resultCode, data);
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
            Log.v(Main.APP_IDENTIFIER, "Ex:" + ex.getMessage());
        }   
**/
    }

    /**
     *  ダイアログ表示の準備
     * 
     */
    protected Dialog onCreateDialog(int id)
    {
        return (prepareConfirmDeleteDialog());
    }
    
    /**
     *   ファイルの削除を確認するダイアログ
     * 
     * @return
     */
    private Dialog prepareConfirmDeleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmClose));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.confirmYes), new DialogInterface.OnClickListener()
              {
                   public void onClick(DialogInterface dialog, int id)
                   {
                       LogCat.this.finish();
                   }
               });
        builder.setNegativeButton(getString(R.string.confirmNo), new DialogInterface.OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int id)
                   {
                        dialog.cancel();
                   }
               });
        return (builder.create());
    }

    /**
     *  Activityの準備を行う
     * 
     */
    private void prepareMyActivity()
    {
        //Intent myIntent = getIntent();
        try
        {
            ArrayList<String> commandLine = new ArrayList<String>();
            // コマンドの作成
            commandLine.add( "logcat");
            commandLine.add( "-d");
            commandLine.add( "-v");
            commandLine.add( "time");
            commandLine.add( "-s");
            commandLine.add( "tag:W");
            Process process = Runtime.getRuntime().exec( commandLine.toArray( new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 1024);
            String line = bufferedReader.readLine();
            String log = "";
            while (line != null)
            {
            	log = log + line + "\n";
            }
        }
        catch (Exception ex)
        {
            Log.v("SensorsChecker", "EXCEPTION :" + ex.getMessage());
        }

    }
}
