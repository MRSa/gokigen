package jp.sourceforge.gokigen.cvtest;

import android.hardware.Camera;

/**
 *  
 * @author MRSa
 *
 */
public interface ICameraDataReceiver
{
	public abstract void onPreviewStared(int width, int height, int frameWidth, int frameHeight);
	
    /** プレビューデータをもらう **/
	public abstract void onPreviewFrame(byte[] arg0, Camera arg1);
	
	/** データの受信準備 **/
	public abstract void prepareToReceive();

}
