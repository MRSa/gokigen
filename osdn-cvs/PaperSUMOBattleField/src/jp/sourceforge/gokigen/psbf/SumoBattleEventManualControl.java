package jp.sourceforge.gokigen.psbf;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 *    �}�j���A�����[�h�œ��쒆�A�Q�[����ԕύX�E�C�x���g�����ɔ�������
 * 
 * @author MRSa
 *
 */
public class SumoBattleEventManualControl implements SumoGameController.ISumoGameEventReceiver
{
    PSBFBaseActivity mActivity = null;

	
	/**
	 *   �R���X�g���N�^
	 */
    public SumoBattleEventManualControl(PSBFBaseActivity hostActivity)
    {
        mActivity = hostActivity;
    	
    }

    /**
     *   �]�|���o�I
     */
    public void detectFalldown(int fighterId)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean isStopMotor = preferences.getBoolean("stopMotorWhenLose", false);

        // ���[�^�̓�����~�߂鏈��
        SeekBar motorA = (SeekBar) mActivity.findViewById(R.id.MotorASlider);
        SeekBar motorB = (SeekBar) mActivity.findViewById(R.id.MotorBSlider);
        if (isStopMotor == true)
        {        
            // ���[�^�̓�����~���鏈���i�p�����[�^�Œ�~���邱�Ƃ��w�肳��Ă����ꍇ�̂݁j
        	motorA.setProgress(0);
            try
            {
                // �ꉞ�O�̂��߁A�E�F�C�g�����Ă����B
                Thread.sleep(50);  // wait 50ms...
            }
            catch (Exception ex)
            {
            	//
            }
            motorB.setProgress(0);
        }

        // ���������̗͎m�̃����v��]�|������
        int fighterAImage = R.drawable.indicator_button_capacitive_off_noglow;
		int fighterBImage = R.drawable.indicator_button_capacitive_off_noglow;
        if (fighterId == SumoGameController.FIGHTER_A)
        {
        	// �͎mA���]�|
            fighterAImage = R.drawable.indicator_button_capacitive_on_noglow;
        }
        else if (fighterId == SumoGameController.FIGHTER_B)
        {
        	// �͎mB���]�|
            fighterBImage = R.drawable.indicator_button_capacitive_on_noglow;
        }

        // �]�|��Ԃ��C���[�W�\������I
    	ImageView imageViewA = (ImageView) mActivity.findViewById(R.id.ButtonA1);
	    imageViewA.setImageDrawable(mActivity.getResources().getDrawable(fighterAImage));
	    ImageView imageViewB = (ImageView) mActivity.findViewById(R.id.ButtonB1);
	    imageViewB.setImageDrawable(mActivity.getResources().getDrawable(fighterBImage));
    }

    /**
     *   �Q�[���X�^�[�g�I
     */
    public void startGame()
    {
    	
    	
    }

    /**
     *    �N���X�̎��s����
     * 
     */
    public void prepare()
    {
    	
    }    

    /**
     *   ��ʍX�V�̃g���K�[
     */
    public void update()
    {
    	
    	
    }

    /**
     *   �󋵂����Z�b�g
     * 
     */
    public void resetField()
    {
        // �]�|��Ԃ��C���[�W�\���i�N���A�j����I
    	ImageView imageViewA = (ImageView) mActivity.findViewById(R.id.ButtonA1);
	    imageViewA.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.indicator_button_capacitive_off_noglow));
	    ImageView imageViewB = (ImageView) mActivity.findViewById(R.id.ButtonB1);
	    imageViewB.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.indicator_button_capacitive_off_noglow));
    }
}
