package jp.sourceforge.gokigen.qsteer.controller;

import java.io.FileOutputStream;
import android.util.Log;

/**
 *    ADKへメッセージを送信するスレッド
 * 
 * @author MRSa
 *
 */
public class UsbSendCommandThread implements Runnable
{
	private byte[] dataToSend = null;
    private FileOutputStream mOutputStream = null;

    /**
     *    コンストラクタ
     * 
     * @param stream     出力ストリーム
     * @param command 出力コマンド
     */
    public UsbSendCommandThread(FileOutputStream stream, byte[] command)
    {
    	dataToSend = command;
    	mOutputStream = stream;
    }

    /**
     *   送信メイン
     * 
     */
    public void run()
    {
    	try
    	{
            mOutputStream.write(dataToSend);
            //Log.v(GokigenSymbols.APP_IDENTIFIER, "SEND :" + dataToSend[0]);
    	}
        catch (Exception ex)
    	{
            Log.v(GokigenSymbols.APP_IDENTIFIER, "SEND FAIL :" + ex.toString());        	//
    	}
    }	
}
