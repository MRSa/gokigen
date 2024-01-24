package jp.sourceforge.gokigen.log.viewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

/**
 *  ちょっと処理に時間がかかる場合に使う、スピナーダイアログの表示クラス
 *  IExectionTask で処理を渡してもらう
 * 
 * @author MRSa
 *
 */
public class MySpinnerDialog
{
    Activity  parent = null;
    ProgressDialog loadingDialog = null;
    IExectionTask targetTask = null;
    
    /**
     *  コンストラクタ
     * 
     * @param context
     */
	public MySpinnerDialog(Activity context)
	{
        loadingDialog = new ProgressDialog(context);
        parent = context;		
	}

	/**
	 *  時間のかかる処理を実行する
	 * 
	 */
	public void executeTask(IExectionTask target)
    {

        //  プログレスダイアログ（「ロード中...」）を表示する。
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(target.getSpinnerMessage(parent));
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        // 実行するクラスと画面を記憶する
        targetTask = null;
        targetTask = target;
        
        try
        {
            // 前処理の実行
        	targetTask.prepareTask(parent);
        }
        catch (Exception ex)
        {
            // 何もしない...
        }
        
        /**
         *  ダイアログ表示中の処理
         * 
         */
        Thread thread = new Thread(new Runnable()
        {  
            public void run()
            {
            	try
            	{
            		// 時間のかかる本処理を実行する
            		targetTask.executeTask();
            	}
                catch (Exception ex)
            	{
                    // なにもしない
            	}
        		handler.sendEmptyMessage(0);
           }

            /**
             *   画面の更新
             */
            private final Handler handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                	targetTask.finishTask(parent);
                	loadingDialog.dismiss();

                    // 処理は終了...クリアする
                	targetTask = null;
                }
            };   
        });
        try
        {
            thread.start();
        }
        catch (Exception ex)
        {

        }
    }

	/**
	 *  このクラスを利用するためのインタフェース
	 * 
	 * @author MRSa
	 *
	 */
	public interface IExectionTask
	{
	    // スピナーに表示するメッセージの取得
	    public abstract String getSpinnerMessage(Activity parent);

	    // 処理実行前の準備
		public abstract void prepareTask(Activity parent);
		
		// 処理を実行 （結構時間がかかる処理、ここは別スレッドで実行）
	    public abstract void executeTask();

	    // 処理終了時に画面を更新する処理
	    public abstract void finishTask(Activity parent);
	}
}
