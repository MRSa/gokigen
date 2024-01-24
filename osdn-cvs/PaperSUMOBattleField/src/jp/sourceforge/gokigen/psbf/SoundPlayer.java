package jp.sourceforge.gokigen.psbf;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 *   SoundPlayer : ���y�̍Đ��N���X
 * 
 * @author MRSa
 *
 */
public class SoundPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener
{
    private Activity parent = null;  // �e��
    private MediaPlayer mplayer = null;
    private SoundVisualizerListener visualizerListener = null;
    private Visualizer visualizer =null;

    /**
	 *  �R���X�g���N�^
	 * @param argument
	 */
	public SoundPlayer(Activity argument, SoundVisualizerListener listener)
	{
	        parent = argument;
	        mplayer = new MediaPlayer();
	        visualizerListener = listener;
	}
	
	/**
	 *   �T�E���h�̍Đ��J�n
	 */
    public void playSound()
    {
		Log.v(PSBFMain.APP_IDENTIFIER, "SoundPlayer::playSound()");

		//  �ݒ�ɋL�^����Ă���f�[�^����ʂɔ��f������
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
    	String fileName = preferences.getString("playFileName", "");
        try
        {
        	mplayer.reset();
    	    mplayer.setDataSource(fileName);
    	    mplayer.setOnPreparedListener(this);
    	    mplayer.setOnCompletionListener(this);
    	    mplayer.setOnErrorListener(this);
    	    mplayer.prepare();
        }
        catch (Exception ex)
        {
        	Log.v(PSBFMain.APP_IDENTIFIER, "PLAY START FAILURE '" + fileName + "' : " + ex.toString());
        	ex.printStackTrace();
        	stopSound();
        }
    }
    
    /**
     *   �Đ����������I
     * 
     */
    public void onPrepared(MediaPlayer mp)
    {
    	try
    	{
    		Log.v(PSBFMain.APP_IDENTIFIER, "SoundPlayer::onPrepared()");

    		// �Đ��J�n�I
    	    mp.start();
	        visualizer = new Visualizer(mp.getAudioSessionId());
    	    visualizer.setEnabled(false);
    	    visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
    	    visualizer.setDataCaptureListener(visualizerListener, (Visualizer.getMaxCaptureRate() / 2), false, true);    // FFT
    	    //visualizer.setDataCaptureListener(visualizerListener, (Visualizer.getMaxCaptureRate() / 2), true, false);  // wave form
        	visualizer.setEnabled(true);
    	}
    	catch (Exception ex)
    	{
    		Log.v(PSBFMain.APP_IDENTIFIER, "PLAY START FAILURE : " + ex.toString());
    	}    	
    }
    
    /**
     *   �T�E���h�̈ꎞ��~
     * 
     */
    public void pauseSound()
    {
        try
        {
        	if (mplayer.isPlaying() == true)
        	{
        	    mplayer.pause();
        	}
        	else
        	{
        		mplayer.start();
        	}
        }
        catch (Exception ex)
        {
        	Log.v(PSBFMain.APP_IDENTIFIER, "PAUSE FAILURE : " + ex.toString());        
        }
    }

    /**
     *    ���y���Đ������ǂ�������������
     *    
     * @return
     */
    public boolean isPlaying()
    {
    	if (mplayer == null)
    	{
    		return (false);
    	}
    	if (mplayer.isPlaying() != true)
    	{
    		return (false);
    	}
    	return (true);
    }
    
    
    /**
     *     ���̃��Z�b�g
     * 
     */
    private void resetSound()
    {
        try
        {
        	mplayer.reset();
        	visualizer.setEnabled(false);
        }
        catch (Exception ex)
        {
        	Log.v(PSBFMain.APP_IDENTIFIER, "SOUND RESET FAILURE : " + ex.toString());        
        }
    }

    /**
     *   �T�E���h�̍Đ���~
     * 
     */
    public void stopSound()
    {
        try
        {
        	mplayer.stop();
        	visualizer.setEnabled(false);
        	mplayer.reset();
        }
        catch (Exception ex)
        {
        	Log.v(PSBFMain.APP_IDENTIFIER, "PLAY STOP FAILURE : " + ex.toString());
        	resetSound();
        }
    }

    /**
     *    ���y�����s�[�g����I
     * 
     */
    public void onCompletion(MediaPlayer mp)
    {
    	mp.start();
    }
    
    /**
     *    ���y�Đ����ɃG���[�����������ꍇ�̏���
     */
    public  boolean onError(MediaPlayer mp, int what, int extra)
    {
    	stopSound();
    	resetSound();
    	//finish();
    	return (false);
    }

    /**
     *   �A�v���P�[�V�������I�����悤�Ƃ������̌�n���B
     * 
     */
    public void finish()
    {
    	mplayer.release();
    	visualizer.release();
    }
}
