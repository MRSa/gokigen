package jp.sourceforge.gokigen.android.sample;

/**
 *  �W�I�R�[�f�B���O���ʂ���������C���^�t�F�[�X
 * 
 * @author MRSa
 *
 */
public interface IGeocoderResultReceiver
{
	/**
	 *  �W�I�R�[�f�B���O���ʂ�n��
	 * 
	 *   @param resultString �W�I�R�[�f�B���O����
	 **/
    public abstract void receivedResult(MyLocation resultString);
}
