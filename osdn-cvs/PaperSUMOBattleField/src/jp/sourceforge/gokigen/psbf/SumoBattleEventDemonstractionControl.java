package jp.sourceforge.gokigen.psbf;

import android.util.Log;

/**
 *   �f�����X�g���[�V�������[�h�œ��쒆�A�Q�[����ԕύX�E�C�x���g�����ɔ�������
 * 
 * @author MRSa
 *
 */
public class SumoBattleEventDemonstractionControl  implements SumoGameController.ISumoGameEventReceiver
{
    PSBFBaseActivity mActivity = null;

    /**
     *   �R���X�g���N�^
     *   
     * @param hostActivity
     */
    public SumoBattleEventDemonstractionControl(PSBFBaseActivity hostActivity)
    {
        mActivity = hostActivity;    	
    }
	
    /**
     *   �]�|���o�I
     */
    public void detectFalldown(int fighterId)
    {
    	
    	// ���[�^��~����
    	Log.v(PSBFMain.APP_IDENTIFIER, "STOP MOTORs (FALLDOWN)");
    	//mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_A, 0);
    	//mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_B, 0);
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
    	
    }
}
