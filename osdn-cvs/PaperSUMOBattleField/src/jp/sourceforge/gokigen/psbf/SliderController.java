package jp.sourceforge.gokigen.psbf;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 *    マニュアル操作モードで出てくる、スライダの制御を行うクラス
 * 
 * @author MRSa
 *
 */
public class SliderController
{
    private PSBFBaseActivity mActivity = null;
    private final byte mCommandTarget;    

    /**
     * 
     * @param activity
     * @param index
     * @param res
     */
    public SliderController(PSBFBaseActivity activity, byte motorId, Resources res) 
    {
        mActivity = activity;
        mCommandTarget = motorId;
    }

    /**
     * 
     * @param seekBar
     */
    public void attachToSeekBar(SeekBar seekBar, TextView textLabel)
    {
        seekBar.setOnSeekBarChangeListener(new SliderMoveListener(mActivity, seekBar, textLabel));
    }

    /**
     *    クラス：スライドバーを動かした特の処理...
     * 
     * @author MRSa
     *
     */
    class SliderMoveListener implements SeekBar.OnSeekBarChangeListener
    {
        private final TextView mValueLabel;
        private int mCurrent = 0;

        /**
         *   コンストラクタ
         *   
         * @param activity
         * @param target
         * @param label
         */
        public SliderMoveListener(Activity activity, SeekBar target, TextView label)
        {
            mValueLabel = label;
            if (mValueLabel != null)
            {
                mValueLabel.setText("" + mCurrent + " ");
            }
        }

        /**
         *   スライダーを動かした結果を送信する
         * 
         */
    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    	{
    		mCurrent = progress;
            if (mActivity != null)
            {
                mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, mCommandTarget, mCurrent);
            }
            if (mValueLabel != null)
            {
                mValueLabel.setText("" + mCurrent + " ");
            }
            // Log.v("PSBF_MOTOR", "ID:" + mCommandTarget + "  Value: " + mCurrent);
    	}

    	/**
    	 *    スライダーを動作させ始めた時の処理
    	 * 
    	 */
    	public void onStartTrackingTouch(SeekBar seekBar)
    	{
    		
    	}

    	/**
    	 *    スライダーの動作を止めた時の処理
    	 */
    	public void onStopTrackingTouch(SeekBar seekBar)
    	{
    		
    	}

    	/**
    	 *    現在のスライダ値を取得する
    	 *    
    	 * @return 現在のスライダ値
    	 */
        public int getCurrentValue()
        {
            return (mCurrent);
        }
    }
}
