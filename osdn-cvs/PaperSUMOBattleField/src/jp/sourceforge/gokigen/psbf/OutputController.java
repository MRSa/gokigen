package jp.sourceforge.gokigen.psbf;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 *   �o�̓R���g���[���N���X ... Android����ADK�𑀍삷��Ƃ��̃N���X
 * 
 * @author MRSa
 *
 */
public class OutputController extends IAccessoryController  implements OnClickListener
{
	private PSBFBaseActivity mActivity = null;
    private SumoGameController gameController = null;
    OutputController(PSBFBaseActivity hostActivity, InputController inputController) 
    {
        super(hostActivity);
        
        mActivity = hostActivity;
        gameController = inputController.getGameController();
    }

    /**
     *   ADK�ɐڑ����ꂽ�Ƃ��ɌĂ΂��N���X
     */
    protected void onAccesssoryAttached()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
    	int operationMode = Integer.parseInt(preferences.getString("operationMode", "2"));
    	if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
    	{
        	//  �uStop Motor�v�{�^���������ꂽ�Ƃ��̏���
            final Button emoButton = (Button) mActivity.findViewById(R.id.EMOButton1);
            if (emoButton != null)
            {
                emoButton.setOnClickListener(this);
            }
            //  �uRelease�v�{�^���������ꂽ�Ƃ��̏���
            final Button resetButton = (Button) mActivity.findViewById(R.id.ResetButton);
            if (resetButton != null)
            {
            	resetButton.setOnClickListener(this);
            }

            // �m�[�}�����샂�[�h�͂����Ő܂�Ԃ� (�X���C�_�[���g�������[�^�̏o�͐���͍s��Ȃ��B�j
     		return;
    	}
    	else if (operationMode == PSBFBaseActivity.OPERATIONMODE_DEMONSTRATION)
    	{
        	//  �uStop Motor�v�{�^���������ꂽ�Ƃ��̏���
            final Button emoButton = (Button) mActivity.findViewById(R.id.EMOButton2);
            if (emoButton != null)
            {
                emoButton.setOnClickListener(this);
            }

            //  �uRelease�v�{�^���������ꂽ�Ƃ��̏���
            final Button unlatchButton = (Button) mActivity.findViewById(R.id.UnlatchButton);
            if (unlatchButton != null)
            {
            	unlatchButton.setOnClickListener(this);
            }
    		// �f�����X�g���[�V�������[�h�͂����Ő܂�Ԃ� (�X���C�_�[���g�������[�^�̏o�͐���͍s��Ȃ��B�j
    		return;    		
    	}

    	// �}�j���A�����샂�[�h (PSBFBaseActivity.OPERATIONMODE_MANUAL)
    	
    	// ���[�^����������
    	setupMotorController(PSBFBaseActivity.MOTOR_A, R.id.MotorASlider, R.id.MotorAValue);
    	setupMotorController(PSBFBaseActivity.MOTOR_B, R.id.MotorBSlider, R.id.MotorBValue);

    	//  �uStop Motor�v�{�^���������ꂽ�Ƃ��̏���
        final Button emoButton = (Button) mActivity.findViewById(R.id.EMOButton);
        if (emoButton != null)
        {
            emoButton.setOnClickListener(this);
        }
    }

    /**
     *    ���[�^���䕔�����������鏈��
     *    
     * @param motorId
     * @param viewId
     * @param labelId
     */
    private void setupMotorController(byte motorId, int viewId, int labelId) 
    {
        SliderController motorC = new SliderController(mHostActivity, motorId, getResources());
        motorC.attachToSeekBar((SeekBar) findViewById(viewId), (TextView) findViewById(labelId));
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.EMOButton)
        {
        	// ���[�^�̓�����~�߂鏈��
           SeekBar motorA = (SeekBar) mActivity.findViewById(R.id.MotorASlider);
           SeekBar motorB = (SeekBar) mActivity.findViewById(R.id.MotorBSlider);
           
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
           
       	   // ���[�^����R�}���h���M�𕜋�����
       	   mActivity.setSendCommandLatch(false);

       	   // �Q�[����Ԃ����Z�b�g����
       	   if (gameController != null)
       	   {
       	       gameController.resetStatus();
       	   }
        }
        else if ((id == R.id.EMOButton1)||(id == R.id.EMOButton2))
        {
        	// ���[�^��~����
        	Log.v(PSBFMain.APP_IDENTIFIER, "STOP MOTORs (and latch)");
        	mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_A, 0);
            try
            {
                // �ꉞ�O�̂��߁A�E�F�C�g�����Ă����B
                Thread.sleep(50);  // wait 50ms...
            }
            catch (Exception ex)
            {
            	//
            }
        	mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_B, 0);

        	// ���[�^����R�}���h�̑��o���~����
        	mActivity.setSendCommandLatch(true);
        	
        	// ��ʕ\�����X�V����
        	if (gameController != null)
        	{
        		gameController.updateScreen();
        	}
        }
        else if (id == R.id.UnlatchButton)
        {
        	Log.v(PSBFMain.APP_IDENTIFIER, "unlatch");

        	// ���[�^����R�}���h���o���ĊJ����
        	mActivity.setSendCommandLatch(false);

        	// ��ʕ\�����X�V����
        	if (gameController != null)
        	{
        		gameController.updateScreen();
        	}
        }
        else if (id == R.id.ResetButton)
        {
        	Log.v(PSBFMain.APP_IDENTIFIER, "reset");

        	// ���[�^����R�}���h���o���ĊJ����
        	mActivity.setSendCommandLatch(false);

        	// �Q�[����Ԃ����Z�b�g����
        	if (gameController != null)
        	{
        		gameController.resetStatus();
        	}
        }
    }
}
