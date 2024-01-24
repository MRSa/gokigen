package jp.sourceforge.gokigen.psbf;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 *    �}�j���A�����샂�[�h�ŏo�Ă���A�X���C�_�̐�����s���N���X
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
     *    �N���X�F�X���C�h�o�[�𓮂��������̏���...
     * 
     * @author MRSa
     *
     */
    class SliderMoveListener implements SeekBar.OnSeekBarChangeListener
    {
        private final TextView mValueLabel;
        private int mCurrent = 0;

        /**
         *   �R���X�g���N�^
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
         *   �X���C�_�[�𓮂��������ʂ𑗐M����
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
    	 *    �X���C�_�[�𓮍삳���n�߂����̏���
    	 * 
    	 */
    	public void onStartTrackingTouch(SeekBar seekBar)
    	{
    		
    	}

    	/**
    	 *    �X���C�_�[�̓�����~�߂����̏���
    	 */
    	public void onStopTrackingTouch(SeekBar seekBar)
    	{
    		
    	}

    	/**
    	 *    ���݂̃X���C�_�l���擾����
    	 *    
    	 * @return ���݂̃X���C�_�l
    	 */
        public int getCurrentValue()
        {
            return (mCurrent);
        }
    }
}
