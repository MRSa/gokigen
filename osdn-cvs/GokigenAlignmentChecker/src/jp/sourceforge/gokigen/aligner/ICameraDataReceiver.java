package jp.sourceforge.gokigen.aligner;

import android.hardware.Camera;

/**
 *  
 * @author MRSa
 *
 */
public interface ICameraDataReceiver
{
    /** �v���r���[�f�[�^�����炤 **/
	public abstract void onPreviewFrame(byte[] arg0, Camera arg1, int width, int height);

}
