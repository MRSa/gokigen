package jp.sourceforge.gokigen.psbf;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 *   SoundPlayer : 音楽の再生クラス
 * 
 * @author MRSa
 *
 */
public class SoundPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener
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
	 *   サウンドの再生開始
	 */
    public void playSound()
    {
		Log.v(PSBFMain.APP_IDENTIFIER, "SoundPlayer::playSound()");

		//  設定に記録されているデータを画面に反映させる
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
     *   再生準備完了！
     * 
     */
    public void onPrepared(MediaPlayer mp)
    {
    	try
    	{
    		Log.v(PSBFMain.APP_IDENTIFIER, "SoundPlayer::onPrepared()");

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
    		Log.v(PSBFMain.APP_IDENTIFIER, "PLAY START FAILURE : " + ex.toString());
    	}    	
    }
    
    /**
     *   サウンドの一時停止
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
     *    音楽が再生中かどうかを応答する
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
        	Log.v(PSBFMain.APP_IDENTIFIER, "SOUND RESET FAILURE : " + ex.toString());        
        }
    }

    /**
     *   サウンドの再生停止
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
     *    音楽をリピートする！
     * 
     */
    public void onCompletion(MediaPlayer mp)
    {
    	mp.start();
    }
    
    /**
     *    音楽再生時にエラーが発生した場合の処理
     */
    public  boolean onError(MediaPlayer mp, int what, int extra)
    {
    	stopSound();
    	resetSound();
    	//finish();
    	return (false);
    }

    /**
     *   アプリケーションを終了しようとした時の後始末。
     * 
     */
    public void finish()
    {
    	mplayer.release();
    	visualizer.release();
    }
}
