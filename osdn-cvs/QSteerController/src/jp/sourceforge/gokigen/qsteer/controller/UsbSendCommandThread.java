package jp.sourceforge.gokigen.qsteer.controller;

import java.io.FileOutputStream;
import android.util.Log;

/**
 *    ADK�փ��b�Z�[�W�𑗐M����X���b�h
 * 
 * @author MRSa
 *
 */
public class UsbSendCommandThread implements Runnable
{
	private byte[] dataToSend = null;
    private FileOutputStream mOutputStream = null;

    /**
     *    �R���X�g���N�^
     * 
     * @param stream     �o�̓X�g���[��
     * @param command �o�̓R�}���h
     */
    public UsbSendCommandThread(FileOutputStream stream, byte[] command)
    {
    	dataToSend = command;
    	mOutputStream = stream;
    }

    /**
     *   ���M���C��
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
