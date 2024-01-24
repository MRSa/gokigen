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
    private Activity parent = null;  // 親分
    private MediaPlayer mplayer = null;
    private SoundVisualizerListener visualizerListener = null;
    private Visualizer visualizer =null;

    /**
	 *  コンストラクタ
	 * @param argument
	 */
	public SoundPlayer(Activity argument, SoundVisualizerListener listener)
	{
	        parent = argument;
	        mplayer = new MediaPlayer();
	        visualizerListener = listener;
	}

    /**
	 *   クリックされたときの処理
	 *
	 */
	public void onClick(View v)
	{
        Log.v(Main.APP_IDENTIFIER, "SoundPlayer::onClick()"); 
	
		     int id = v.getId();

	         if (id == R.id.PlayButton)
	         {
	        	 // 再生開始
	        	 playSound();
	         }
	         else if (id == R.id.PauseButton)
	         {
	        	 // 音楽の再生一時停止/再開
	        	 pauseSound();
	         }
	         else if (id == R.id.StopButton)
	         {
	        	 // 音楽の再生停止
	        	 stopSound();
	         }
	         return;
    }

	/**
	 *   サウンドの再生開始
	 */
    private void playSound()
    {
    	//  設定に記録されているデータを画面に反映させる
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
     *   再生準備完了！
     * 
     */
    public void onPrepared(MediaPlayer mp)
    {
    	try
    	{
    		// 再生開始！
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
     *   サウンドの一時停止
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
     *     音のリセット
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
     *   サウンドの再生停止
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
