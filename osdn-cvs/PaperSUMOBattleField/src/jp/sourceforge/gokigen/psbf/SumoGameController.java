package jp.sourceforge.gokigen.psbf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

/**
 *   �����o���s���̂��낢��ȃC�x���g���n���h�����O���鏈��
 * 
 * @author MRSa
 *
 */
public class SumoGameController implements SoundPool.OnLoadCompleteListener
{
    private List<SwitchStatus> switches = null;

    private PSBFBaseActivity parent = null;
    
    static final int NOT_FIGHTER = 0;
    static final int FIGHTER_A = 1;
    static final int FIGHTER_B = 2;
    
    static final int KEEP_ALIVE_SWITCH = 15;
    static final int PLAY_SWITCH = 3;
    
    static final int TAIKO_A_1 = 7;
    static final int TAIKO_A_2 = 8;
    static final int TAIKO_A_3 = 9;
    static final int TAIKO_A_4 = 10;
    
    static final int TAIKO_B_1 = 11;
    static final int TAIKO_B_2 = 12;
    static final int TAIKO_B_3 = 13;
    static final int TAIKO_B_4 = 14;

	boolean isFighterAfallDown = false;
	boolean isFighterBfallDown = false;
    
    long fighterAfalldownTime = 0;
    long fighterBfalldownTime = 0;

    long sensorAfalldownLimit = 180;
    long sensorBfalldownLimit = 180;

    // ���s���������̏���
    int  winner = NOT_FIGHTER;
    
    // ���ʉ��ݒ�...    
    private SoundPool  soundEffect = null;
    private boolean    isSoundEffectReady = false;
    private int            seTaikoId = 0;
    
    // �^�C�R���͂����ꂽ���ǂ����̔���
    private int            taikoOnThreshold = 3;

    // �]�|�����o�����Ƃ��ɋ����ė~�����l
    private ISumoGameEventReceiver gameEventReceiver = null;

    // �P�ʎ��Ԃ�����ɑ��ۂ�����������
    private int   playerAhitCount = 0;
    private int   playerBhitCount = 0;

    // ���ۂ�@������
    private int   playerAhitCountTotal = 0;
    private int   playerBhitCountTotal = 0;

    // ���ۂ�@�����Ƃ��Ƀ��[�^�𓮍삳���鎞�̏o�͒l�̉����A����A�ω��X�e�b�v
    private int   taikoMoveLowerA = 0;
    private int   taikoMoveLowerB = 0;
    private int   taikoMoveResolution = 0;
    private int   taikoMoveUpperLimit = 200;
    
    /**
     *   �R���X�g���N�^
     *   
     * @param baseActivity
     */
    public SumoGameController(PSBFBaseActivity baseActivity,  int operationMode)
    {
    	parent = baseActivity;

        // ���ʉ��Đ��N���X�̐ݒ�
        soundEffect = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundEffect.setOnLoadCompleteListener(this);

        // ���ʉ��̏���
        seTaikoId = soundEffect.load(baseActivity, R.raw.taikodrum, 1);   // �^�C�R��@�����̃��[�h

    	switches = new ArrayList<SwitchStatus>();
    	switches.add(new SwitchStatus(baseActivity, KEEP_ALIVE_SWITCH, NOT_FIGHTER, R.id.sensorB4Value,  "sensorB4Upper",  "sensorB4Lower"));  // 15 : keep alive�p...
    	switches.add(new SwitchStatus(baseActivity, PLAY_SWITCH, NOT_FIGHTER, R.id.sensorA4Value,  "sensorA4Upper",  "sensorA4Lower")); // 3 : �Q�[���J�n�X�C�b�`�p...
    	switches.add(new SwitchStatus(baseActivity, 0, FIGHTER_A, R.id.sensorA1Value, "sensorA1Upper",  "sensorA1Lower"));  // 0
    	switches.add(new SwitchStatus(baseActivity, 1, FIGHTER_A, R.id.sensorA2Value, "sensorA2Upper",  "sensorA2Lower"));  // 1
    	switches.add(new SwitchStatus(baseActivity, 2, FIGHTER_A, R.id.sensorA3Value, "sensorA3Upper",  "sensorA3Lower"));  // 2

    	switches.add(new SwitchStatus(baseActivity, 4, FIGHTER_B, R.id.sensorB1Value, "sensorB1Upper",  "sensorB1Lower")); // 4
    	switches.add(new SwitchStatus(baseActivity, 5, FIGHTER_B, R.id.sensorB2Value, "sensorB2Upper",  "sensorB2Lower")); // 5
    	switches.add(new SwitchStatus(baseActivity, 6, FIGHTER_B, R.id.sensorB3Value, "sensorB3Upper",  "sensorB3Lower")); // 6
//    	switches.add(new SwitchStatus(baseActivity, 15, NOT_FIGHTER, R.id.sensorB4Value,  "sensorB4Upper",  "sensorB4Lower"));

    	switches.add(new SwitchStatus(baseActivity, TAIKO_A_1, NOT_FIGHTER, R.id.sensorAT1Value, null, null)); // 7
    	switches.add(new SwitchStatus(baseActivity, TAIKO_A_2, NOT_FIGHTER, R.id.sensorAT2Value, null, null)); // 8
    	switches.add(new SwitchStatus(baseActivity, TAIKO_A_3, NOT_FIGHTER, R.id.sensorAT3Value, null, null)); // 9
    	switches.add(new SwitchStatus(baseActivity, TAIKO_A_4, NOT_FIGHTER, R.id.sensorAT4Value, null, null)); // 10
    	    	
    	switches.add(new SwitchStatus(baseActivity, TAIKO_B_1, NOT_FIGHTER, R.id.sensorBT1Value, null, null)); // 11
    	switches.add(new SwitchStatus(baseActivity, TAIKO_B_2, NOT_FIGHTER, R.id.sensorBT2Value, null, null)); // 12
    	switches.add(new SwitchStatus(baseActivity, TAIKO_B_3, NOT_FIGHTER, R.id.sensorBT3Value, null, null)); // 13
    	switches.add(new SwitchStatus(baseActivity, TAIKO_B_4, NOT_FIGHTER, R.id.sensorBT4Value, null, null)); // 14

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(baseActivity);

        sensorAfalldownLimit = Integer.parseInt(preferences.getString("sensorAfallThreshold", "180"));
        sensorBfalldownLimit = Integer.parseInt(preferences.getString("sensorBfallThreshold", "180"));
        taikoOnThreshold = Integer.parseInt(preferences.getString("taikoOnThreshold", "10"));

        taikoMoveLowerA = Integer.parseInt(preferences.getString("thresholdMoveLowerLimitA", "170"));
        taikoMoveLowerB = Integer.parseInt(preferences.getString("thresholdMoveLowerLimitB", "125"));
        taikoMoveResolution = Integer.parseInt(preferences.getString("movingResolution", "16"));
        taikoMoveUpperLimit =  Integer.parseInt(preferences.getString("thresholdMoveUpperlimitAB", "200"));

        // �]�|���o�N���X�𐶐�����
        if (operationMode == PSBFBaseActivity.OPERATIONMODE_MANUAL)
		{
			// �ʏ푀�샂�[�h
        	gameEventReceiver = new SumoBattleEventManualControl(baseActivity);
		}
		else if (operationMode == PSBFBaseActivity.OPERATIONMODE_DEMONSTRATION)
		{
			// �f�����X�g���[�V�������[�h
			gameEventReceiver = new SumoBattleEventDemonstractionControl(baseActivity);
		}
		else // if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
		{
		    // �ʏ탂�[�h	
			gameEventReceiver = new SumoBattleEventNormalControl(baseActivity, this);
		}    
        resetStatus();
    }

    /**
     *    �N���X�̏���...
     * 
     */
    public void prepare()
    {
    	try
    	{
            if (gameEventReceiver != null)
            {
            	gameEventReceiver.prepare();
            }
    	}
    	catch (Exception ex)
    	{
    		//
    	}
    }
    
    /**
     *   ��ԕω��̒ʒm����M�����I
     * 
     * @param id
     * @param status
     */
    public void changedStatusTrigger(int id, int value, int operationMode, boolean isUpdateScreen)
    {
    	if (id == KEEP_ALIVE_SWITCH)
    	{
            // �����ԕ񍐂̃R�}���h����M
    		playerAhitCountTotal = playerAhitCountTotal + playerAhitCount;
    		playerBhitCountTotal = playerBhitCountTotal + playerBhitCount;
    		
    		if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
    		{
    			// ���ۋ쓮�w���I
    	    	if (value == 0)
    	    	{
    	    		// ����A
    	    		driveTaikoA();
    	    	}
    	    	else
    	    	{
    	    		// ����B
    	    		driveTaikoB();
    	    	}

        		// ��ʕ\���̍X�V�w���I
        		updateScreen();
    		}
    	}

    	// �]�|��Ԃ̌��o�����s
    	isFallDown(id, value, isUpdateScreen);
    }


    /**
     *   ��ʕ\�����X�V����
     * 
     */
    public void updateScreen()
    {
		// ��ʍX�V�w���I
    	if (gameEventReceiver != null)
    	{
    		gameEventReceiver.update();
    	}    	
    }
    
    /**
     *    �]�|��Ԃ����Z�b�g
     * 
     */
    public void resetStatus()
    {
    	fighterAfalldownTime = System.currentTimeMillis();
    	fighterBfalldownTime = System.currentTimeMillis();  
    	winner = NOT_FIGHTER;
        if (gameEventReceiver != null)
        {
        	gameEventReceiver.resetField();
        	gameEventReceiver.startGame();
    		gameEventReceiver.update();
        }
    }
    
    /**
     *    �����̗͎m����������
     *    
     * @return
     */
    public int getWinner()
    {
        return (winner);
    }

    /**
     *    ���ʉ��̓ǂݏo�����I�������I
     * @param soundPool
     * @param sampleId
     * @param status
     */
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
    {
    	isSoundEffectReady = true;
    }

    /**
     *   �I�������B�B�B
     */
    public void finishAction()
    {
    	try
    	{
        	if (soundEffect != null)
        	{
                soundEffect.release();
        	}
    	}
    	catch (Exception ex)
    	{
    		// 
    	}
    }

    /**
     *   ���ۂ��@���ꂽ���̏��� �i�����ł́A�������������L�^������x�j
     * 
     * @param id
     */
    protected void hitTaiko(int id)
    {
    	if ((id == TAIKO_A_1)||(id == TAIKO_A_2)||(id == TAIKO_A_3)||(id == TAIKO_A_4))
    	{
              // PLAYER A�̃^�^�R�����́I
    		  playerAhitCount++;
    	}
    	else if ((id == TAIKO_B_1)||(id == TAIKO_B_2)||(id == TAIKO_B_3)||(id == TAIKO_B_4))
    	{
            // PLAYER B�̃^�^�R�����́I
  		    playerBhitCount++;
    	}    	
    }

    /**
     *   �]�|�������ǂ������o����
     * @param fighterA   �͎mA�̏�ԁi�]�|���Ă��邩�ǂ����j
     * @param fighterB   �͎mB�̏�ԁi�]�|���Ă��邩�ǂ����j
     * @return  �]�|�����͎m�iFIGHTER_A or FIGHTER_B or NOT_FIGHTER)
     */
    private void detectFalldown(boolean fighterA, boolean fighterB)
    {
    	int result = NOT_FIGHTER;
    	long falldownTime = System.currentTimeMillis();
    	long falldownMillsA = 0;
    	long falldownMillsB = 0;
    	if (fighterA == false)
        {
        	fighterAfalldownTime = System.currentTimeMillis();
        }
        else
        {
        	falldownMillsA = falldownTime - fighterAfalldownTime;

        	//fighterBfalldownTime = System.currentTimeMillis();
        	//Log.v(PSBFMain.APP_IDENTIFIER, "falldown : A " + falldownMillsA);
        }
        if (fighterB == false)
        {
        	fighterBfalldownTime = System.currentTimeMillis();
        }
        else
        {
        	falldownMillsB =  falldownTime - fighterBfalldownTime;

        	//fighterAfalldownTime = System.currentTimeMillis();
        	//Log.v(PSBFMain.APP_IDENTIFIER, "falldown : B " + falldownMillsB);
        }

        if ((falldownMillsA > sensorAfalldownLimit)&&(fighterA == true))
        {
        	// FIGHTER_A �]�|���o
        	result = FIGHTER_A;

        	Log.v(PSBFMain.APP_IDENTIFIER, "LOSE A : " + falldownMillsA + "(" +sensorAfalldownLimit + ")");
        }
        else if ((falldownMillsB > sensorBfalldownLimit)&&(fighterB == true))
        {
        	// FIGHTER_B �]�|���o
        	result = FIGHTER_B;

        	Log.v(PSBFMain.APP_IDENTIFIER, "LOSE B : " + falldownMillsB + "(" +sensorBfalldownLimit + ")");
        }

        if ((winner == NOT_FIGHTER)&&(result != NOT_FIGHTER))
        {
            // ���҂��L������ �i���ӁF�]�|���茋�ʂƂ͋t�����ҁj
            winner = (result == FIGHTER_A) ? FIGHTER_B : FIGHTER_A;
        
            // �]�|�������Ƃ�ʒm����
            if ((gameEventReceiver != null))
            {
                gameEventReceiver.detectFalldown(result);
            }
        }
        return;
    }

    /**
     *   �͎m���]�|�������ǂ������肷�� (���C������)
     * 
     * @param id
     * @param currentValue
     * @param isUpdateScreen
     * @return
     */
    private void isFallDown(int id, int currentValue, boolean isUpdateScreen)
    {
        try
        {
        	//Log.v(PSBFMain.APP_IDENTIFIER, "FALLDOWN CHECK (ID :" + id + ")");
        	isFighterAfallDown = true;
        	isFighterBfallDown = true;
            boolean fallDown = false;
            int fallDownCheckGroup = NOT_FIGHTER;
        	Iterator<SwitchStatus> ite = switches.iterator();
        	while (ite.hasNext() == true)
        	{
                SwitchStatus target = ite.next();
                int index = target.getSwitchId();
                int group = target.getGroupId();
                if (index == id)
                {
                	fallDownCheckGroup = group;
                    fallDown = target.setCurrentValue(currentValue, isUpdateScreen);
                }
                else
                {
                    fallDown = target.getIsSwitchOn();
                }

                if (group == FIGHTER_A)
                {
                	if (fallDown == false)
                	{
                		isFighterAfallDown = false;
                	}
                }
                else if (group == FIGHTER_B)
                {
                	if (fallDown == false)
                	{
                		isFighterBfallDown = false;
                	}                	
                }
        	}
        	if (fallDownCheckGroup != NOT_FIGHTER)
        	{
        		// �`�F�b�N����Z���T���͎m�̃Z���T�̎��ɂ̂ݓ]�|���胍�W�b�N���Ăяo��
            	detectFalldown(isFighterAfallDown, isFighterBfallDown);
        	}
        }
    	catch (Exception ex)
    	{
    		//
    		Log.v(PSBFMain.APP_IDENTIFIER, "EX : " + ex.toString());
    	}
    	return;
    }

    /**
     *   ����A�̋쓮�w��
     * 
     */
    private void driveTaikoA()
    {
		float resolutionValue = playerAhitCount * ((taikoMoveUpperLimit - taikoMoveLowerA) / taikoMoveResolution);
        int moveValue = (int) resolutionValue + taikoMoveLowerA;

		// ����l�̐ݒ�
		if (moveValue >taikoMoveUpperLimit)
		{
			moveValue = taikoMoveUpperLimit;
		}

		// �������͂��߂̏��񂾂��́A�t���p���[�ŋ쓮����
		if ((playerAhitCount != 0)&&(playerAhitCountTotal == playerAhitCount))
		{
			moveValue = taikoMoveUpperLimit;
		}
		//Log.v(PSBFMain.APP_IDENTIFIER, "TAIKO  A:" + playerAhitCount + " (" + playerAhitCountTotal + ")"+ " value : " + moveValue);

		/// ���ۓ���R�}���h�̔��s�I
		parent.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_A, moveValue);

		playerAhitCount = 0;
    }
    
    /**
     *    ����B�̋쓮�w��
     * 
     */
    private void driveTaikoB()
    {
		float resolutionValue = playerBhitCount * ((taikoMoveUpperLimit - taikoMoveLowerB) / taikoMoveResolution);
		int moveValue = (int) resolutionValue + taikoMoveLowerB;

		// ����l�̐ݒ�
		if (moveValue >taikoMoveUpperLimit)
		{
			moveValue =taikoMoveUpperLimit;
		}

        // �������͂��߂̏��񂾂��́A�t���p���[�ŋ쓮����
		if ((playerAhitCount != 0)&&(playerAhitCountTotal == playerAhitCount))
		{
			moveValue = taikoMoveUpperLimit;
		}
		//Log.v(PSBFMain.APP_IDENTIFIER, "TAIKO  B:" + playerBhitCount + " (" + playerBhitCountTotal + ")"+ " value : " + moveValue);

		/// ���ۓ���R�}���h�̔��s�I
		parent.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_B, moveValue);
    	
		playerBhitCount = 0;
    }

    /**
     *   ��Ԃ��L�����Ă���N���X
     * 
     * @author MRSa
     *
     */
    private class SwitchStatus
    {
			private Activity mActivity = null;
			private int mSwitchId = -1;
			private int mGroup = 0;
			private int mViewId = -1;
			private int mCurrentValue = 1024;
			private String upperLimit = null;
			private String lowerLimit = null;
			private boolean isSwitchOn = false;
			
			/**
			 *   �R���X�g���N�^
			 *  
			 * @param parent
			 * @param viewId 
			 * @param upperLimitPrefId
			 * @param lowerLimitPrefId
			 */
			public SwitchStatus(Activity parent, int switchId, int groupId, int viewId, String upperLimitPrefId, String lowerLimitPrefId)
		    {
				mActivity = parent;
				mSwitchId = switchId;
				mGroup = groupId;
		    	mViewId = viewId;
		    	upperLimit = upperLimitPrefId;
		    	lowerLimit = lowerLimitPrefId;
		    }

			/**
			 *   �f�[�^�̃O���[�vID
			 * 
			 * @return
			 */
			public int getGroupId()
			{
				return (mGroup);
			}

			/**
			 *   �X�C�b�`��ID����������B
			 * @return
			 */
			public int getSwitchId()
			{
				return (mSwitchId);
			}

			/**
			 *   �X�C�b�`��Ԃ���������
			 *   
			 * @return
			 */
			public boolean getIsSwitchOn()
			{
				return (isSwitchOn);
			}
			
			/**
			 *   �]�|���Ă��邩�ǂ����̔�����s���B
			 * 
			 * @param currentValue
			 * @return
			 */
			public boolean setCurrentValue(int currentValue, boolean isupdateScreen)
			{
				int difference = 0;
//				boolean isFalldown = false;

				difference = currentValue - mCurrentValue;
				mCurrentValue = currentValue;				
				if ((mSwitchId != KEEP_ALIVE_SWITCH)&&(mSwitchId != PLAY_SWITCH)&&(mGroup == NOT_FIGHTER)&&(difference > taikoOnThreshold)&&(isSoundEffectReady == true))
				{
					//Log.v(PSBFMain.APP_IDENTIFIER, "HIT : " + "["  + mSwitchId + "]" + currentValue + "  (" + difference +" > " + taikoOnThreshold + ")");
					if (soundEffect != null)
					{
						// �h���Ƃ�������炷�B
					    soundEffect.play(seTaikoId, 1, 1, 3, 0, 1);
					}
					
					// ���ۂ������������Ƃ��L�^����
					hitTaiko(mSwitchId);
				}
				
				if ((upperLimit == null)||(lowerLimit == null))
	            {
					// �]�|���Ă��Ȃ����Ƃɂ���B
					isSwitchOn = false;
		            if (isupdateScreen == true)
		            {
		            	updateScreen(currentValue, false);
		            }
					return (isSwitchOn);
	            }
				
	        	// �������l�̎擾
	            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mActivity);
	            int offLimitUpper = 0xffff;
	            int offLimitLower = 0x0000;
	            try
	            {
	                offLimitUpper = Integer.parseInt(pref.getString(upperLimit, "1100"));
	                offLimitLower = Integer.parseInt(pref.getString(lowerLimit, "0"));
	            }
	            catch (Exception ex)
	            {
	            	offLimitUpper = 0xffff;
	            	offLimitLower = 0x0000;
	            }
	            if ((offLimitLower <= currentValue)&&(currentValue <= offLimitUpper))
	            {
	            	// �]�|���Ă��Ȃ��I
	            	isSwitchOn = false;
	            }
	            else
	            {
	                // �]�|���Ă���I
	            	isSwitchOn = true;
	            }
	            if (isupdateScreen == true)
	            {
	            	updateScreen(currentValue, isSwitchOn);
	            }
	            return (isSwitchOn);
			}

			/**
			 *    ��ʂɃf�[�^��\������
			 *    
			 * @param currentValue
			 * @param isBold
			 */
			private void updateScreen(int currentValue, boolean isBold)
			{
				try
				{
		            TextView textView = (TextView) mActivity.findViewById(mViewId);
		            if (textView != null)
		            {
		            	if (isBold == true)
		            	{
		            		textView.setTextColor(Color.WHITE);	            		
		            	}
		            	else
		            	{
		            		textView.setTextColor(Color.DKGRAY);
		            	}
		                textView.setText("" + currentValue + " ");
		            }
	                // Log.v(PSBFMain.APP_IDENTIFIER, "ID : " + mSwitchId + " value : " + currentValue);
				}
	            catch (Exception ex)
	            {
	            	// �G���[�����H
	            	Log.v(PSBFMain.APP_IDENTIFIER, "ID : " + mSwitchId + " " + ex.toString());
	            }
			}
		}

    /**
     *    Interface ISumoGameEventReceiver �F �Q�[����Ԃ̒ʒm���s���C���^�t�F�[�X 
     * 
     * @author MRSa
     *
     */
    public interface ISumoGameEventReceiver
    {
        /** �N���X�̏��� **/
    	public abstract void prepare();
    	
    	/** �]�|�������Ƃ�ʒm����  **/
        public abstract void detectFalldown(int fighterId);

        /** �Q�[���̏�Ԃ��N���A���ꂽ�@**/
        public abstract void resetField();

        /** �Q�[�����J�n���ꂽ���Ƃ�ʒm���� **/
        public abstract void startGame();

        /** (��ʍX�V�̃g���K�[) **/
        public abstract void update();
    }

}
