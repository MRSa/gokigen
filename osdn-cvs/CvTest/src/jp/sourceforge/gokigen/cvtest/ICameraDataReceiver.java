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
	
    /** �v���r���[�f�[�^�����炤 **/
	public abstract void onPreviewFrame(byte[] arg0, Camera arg1);
	
	/** �f�[�^�̎�M���� **/
	public abstract void prepareToReceive();

}
