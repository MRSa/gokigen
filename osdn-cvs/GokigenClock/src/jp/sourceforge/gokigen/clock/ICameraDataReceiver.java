package jp.sourceforge.gokigen.clock;

import android.hardware.Camera;

/**
 *  
 * @author MRSa
 *
 */
public interface ICameraDataReceiver
{
    /** �v���r���[�f�[�^�����炤 **/
	public abstract void onPreviewFrame(byte[] arg0, Camera arg1);

}
