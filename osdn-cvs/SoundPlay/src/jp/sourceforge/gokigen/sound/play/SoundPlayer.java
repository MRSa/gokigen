package jp.sourceforge.gokigen.sound.play;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SoundPlayer implements  OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
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
	 *   �N���b�N���ꂽ�Ƃ��̏���
	 *
	 */
	public void onClick(View v)
	{
        Log.v(Main.APP_IDENTIFIER, "SoundPlayer::onClick()"); 
	
		     int id = v.getId();

	         if (id == R.id.PlayButton)
	         {
	        	 // �Đ��J�n
	        	 playSound();
	         }
	         else if (id == R.id.PauseButton)
	         {
	        	 // ���y�̍Đ��ꎞ��~/�ĊJ
	        	 pauseSound();
	         }
	         else if (id == R.id.StopButton)
	         {
	        	 // ���y�̍Đ���~
	        	 stopSound();
	         }
	         return;
    }

	/**
	 *   �T�E���h�̍Đ��J�n
	 */
    private void playSound()
    {
    	//  �ݒ�ɋL�^����Ă���f�[�^����ʂɔ��f������
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
    	String fileName = preferences.getString("playFileName", "");
        try
        {
        	mplayer.reset();
    	    mplayer.setDataSource(fileName);
    	    mplayer.setOnPreparedListener(this);
    	    mplayer.prepare();
        }
        catch (Exception ex)
        {
        	Log.v(Main.APP_IDENTIFIER, "PLAY START FAILURE '" + fileName + "' : " + ex.toString());
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
    		Log.v(Main.APP_IDENTIFIER, "PLAY START FAILURE : " + ex.toString());
    	}    	
    }
    
    /**
     *   �T�E���h�̈ꎞ��~
     * 
     */
    private void pauseSound()
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
        	Log.v(Main.APP_IDENTIFIER, "PAUSE FAILURE : " + ex.toString());        
        }
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
        	Log.v(Main.APP_IDENTIFIER, "SOUND RESET FAILURE : " + ex.toString());        
        }
    }

    /**
     *   �T�E���h�̍Đ���~
     * 
     */
    private void stopSound()
    {
        try
        {
        	mplayer.stop();
        	visualizer.setEnabled(false);
        	mplayer.reset();
        }
        catch (Exception ex)
        {
        	Log.v(Main.APP_IDENTIFIER, "PLAY STOP FAILURE : " + ex.toString());
        	resetSound();
        }
    }
    
    public  boolean onError(MediaPlayer mp, int what, int extra)
    {
    	stopSound();
    	resetSound();
    	//finish();
    	return (false);
    }

    public void finish()
    {
    	mplayer.release();
    	visualizer.release();
    }
}
