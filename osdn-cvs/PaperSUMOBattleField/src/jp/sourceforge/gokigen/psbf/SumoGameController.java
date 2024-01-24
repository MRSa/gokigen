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
 *   紙相撲実行時のいろいろなイベントをハンドリングする処理
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

    // 勝敗がついた時の勝者
    int  winner = NOT_FIGHTER;
    
    // 効果音設定...    
    private SoundPool  soundEffect = null;
    private boolean    isSoundEffectReady = false;
    private int            seTaikoId = 0;
    
    // タイコ入力がされたかどうかの判定
    private int            taikoOnThreshold = 3;

    // 転倒を検出したときに教えて欲しい人
    private ISumoGameEventReceiver gameEventReceiver = null;

    // 単位時間あたりに太鼓をたたいた回数
    private int   playerAhitCount = 0;
    private int   playerBhitCount = 0;

    // 太鼓を叩いた回数
    private int   playerAhitCountTotal = 0;
    private int   playerBhitCountTotal = 0;

    // 太鼓を叩いたときにモータを動作させる時の出力値の下限、上限、変化ステップ
    private int   taikoMoveLowerA = 0;
    private int   taikoMoveLowerB = 0;
    private int   taikoMoveResolution = 0;
    private int   taikoMoveUpperLimit = 200;
    
    /**
     *   コンストラクタ
     *   
     * @param baseActivity
     */
    public SumoGameController(PSBFBaseActivity baseActivity,  int operationMode)
    {
    	parent = baseActivity;

        // 効果音再生クラスの設定
        soundEffect = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundEffect.setOnLoadCompleteListener(this);

        // 効果音の準備
        seTaikoId = soundEffect.load(baseActivity, R.raw.taikodrum, 1);   // タイコを叩く音のロード

    	switches = new ArrayList<SwitchStatus>();
    	switches.add(new SwitchStatus(baseActivity, KEEP_ALIVE_SWITCH, NOT_FIGHTER, R.id.sensorB4Value,  "sensorB4Upper",  "sensorB4Lower"));  // 15 : keep alive用...
    	switches.add(new SwitchStatus(baseActivity, PLAY_SWITCH, NOT_FIGHTER, R.id.sensorA4Value,  "sensorA4Upper",  "sensorA4Lower")); // 3 : ゲーム開始スイッチ用...
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

        // 転倒検出クラスを生成する
        if (operationMode == PSBFBaseActivity.OPERATIONMODE_MANUAL)
		{
			// 通常操作モード
        	gameEventReceiver = new SumoBattleEventManualControl(baseActivity);
		}
		else if (operationMode == PSBFBaseActivity.OPERATIONMODE_DEMONSTRATION)
		{
			// デモンストレーションモード
			gameEventReceiver = new SumoBattleEventDemonstractionControl(baseActivity);
		}
		else // if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
		{
		    // 通常モード	
			gameEventReceiver = new SumoBattleEventNormalControl(baseActivity, this);
		}    
        resetStatus();
    }

    /**
     *    クラスの準備...
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
     *   状態変化の通知を受信した！
     * 
     * @param id
     * @param status
     */
    public void changedStatusTrigger(int id, int value, int operationMode, boolean isUpdateScreen)
    {
    	if (id == KEEP_ALIVE_SWITCH)
    	{
            // 定期状態報告のコマンドを受信
    		playerAhitCountTotal = playerAhitCountTotal + playerAhitCount;
    		playerBhitCountTotal = playerBhitCountTotal + playerBhitCount;
    		
    		if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
    		{
    			// 太鼓駆動指示！
    	    	if (value == 0)
    	    	{
    	    		// 太鼓A
    	    		driveTaikoA();
    	    	}
    	    	else
    	    	{
    	    		// 太鼓B
    	    		driveTaikoB();
    	    	}

        		// 画面表示の更新指示！
        		updateScreen();
    		}
    	}

    	// 転倒状態の検出を実行
    	isFallDown(id, value, isUpdateScreen);
    }


    /**
     *   画面表示を更新する
     * 
     */
    public void updateScreen()
    {
		// 画面更新指示！
    	if (gameEventReceiver != null)
    	{
    		gameEventReceiver.update();
    	}    	
    }
    
    /**
     *    転倒状態をリセット
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
     *    勝ちの力士を応答する
     *    
     * @return
     */
    public int getWinner()
    {
        return (winner);
    }

    /**
     *    効果音の読み出しが終了した！
     * @param soundPool
     * @param sampleId
     * @param status
     */
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
    {
    	isSoundEffectReady = true;
    }

    /**
     *   終了処理。。。
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
     *   太鼓が叩かれた時の処理 （ここでは、たたいた数を記録する程度）
     * 
     * @param id
     */
    protected void hitTaiko(int id)
    {
    	if ((id == TAIKO_A_1)||(id == TAIKO_A_2)||(id == TAIKO_A_3)||(id == TAIKO_A_4))
    	{
              // PLAYER Aのタタコン入力！
    		  playerAhitCount++;
    	}
    	else if ((id == TAIKO_B_1)||(id == TAIKO_B_2)||(id == TAIKO_B_3)||(id == TAIKO_B_4))
    	{
            // PLAYER Bのタタコン入力！
  		    playerBhitCount++;
    	}    	
    }

    /**
     *   転倒したかどうか検出する
     * @param fighterA   力士Aの状態（転倒しているかどうか）
     * @param fighterB   力士Bの状態（転倒しているかどうか）
     * @return  転倒した力士（FIGHTER_A or FIGHTER_B or NOT_FIGHTER)
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
        	// FIGHTER_A 転倒検出
        	result = FIGHTER_A;

        	Log.v(PSBFMain.APP_IDENTIFIER, "LOSE A : " + falldownMillsA + "(" +sensorAfalldownLimit + ")");
        }
        else if ((falldownMillsB > sensorBfalldownLimit)&&(fighterB == true))
        {
        	// FIGHTER_B 転倒検出
        	result = FIGHTER_B;

        	Log.v(PSBFMain.APP_IDENTIFIER, "LOSE B : " + falldownMillsB + "(" +sensorBfalldownLimit + ")");
        }

        if ((winner == NOT_FIGHTER)&&(result != NOT_FIGHTER))
        {
            // 勝者を記憶する （注意：転倒判定結果とは逆が勝者）
            winner = (result == FIGHTER_A) ? FIGHTER_B : FIGHTER_A;
        
            // 転倒したことを通知する
            if ((gameEventReceiver != null))
            {
                gameEventReceiver.detectFalldown(result);
            }
        }
        return;
    }

    /**
     *   力士が転倒したかどうか判定する (メイン処理)
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
        		// チェックするセンサが力士のセンサの時にのみ転倒判定ロジックを呼び出す
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
     *   太鼓Aの駆動指示
     * 
     */
    private void driveTaikoA()
    {
		float resolutionValue = playerAhitCount * ((taikoMoveUpperLimit - taikoMoveLowerA) / taikoMoveResolution);
        int moveValue = (int) resolutionValue + taikoMoveLowerA;

		// 上限値の設定
		if (moveValue >taikoMoveUpperLimit)
		{
			moveValue = taikoMoveUpperLimit;
		}

		// たたきはじめの初回だけは、フルパワーで駆動する
		if ((playerAhitCount != 0)&&(playerAhitCountTotal == playerAhitCount))
		{
			moveValue = taikoMoveUpperLimit;
		}
		//Log.v(PSBFMain.APP_IDENTIFIER, "TAIKO  A:" + playerAhitCount + " (" + playerAhitCountTotal + ")"+ " value : " + moveValue);

		/// 太鼓動作コマンドの発行！
		parent.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_A, moveValue);

		playerAhitCount = 0;
    }
    
    /**
     *    太鼓Bの駆動指示
     * 
     */
    private void driveTaikoB()
    {
		float resolutionValue = playerBhitCount * ((taikoMoveUpperLimit - taikoMoveLowerB) / taikoMoveResolution);
		int moveValue = (int) resolutionValue + taikoMoveLowerB;

		// 上限値の設定
		if (moveValue >taikoMoveUpperLimit)
		{
			moveValue =taikoMoveUpperLimit;
		}

        // たたきはじめの初回だけは、フルパワーで駆動する
		if ((playerAhitCount != 0)&&(playerAhitCountTotal == playerAhitCount))
		{
			moveValue = taikoMoveUpperLimit;
		}
		//Log.v(PSBFMain.APP_IDENTIFIER, "TAIKO  B:" + playerBhitCount + " (" + playerBhitCountTotal + ")"+ " value : " + moveValue);

		/// 太鼓動作コマンドの発行！
		parent.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_B, moveValue);
    	
		playerBhitCount = 0;
    }

    /**
     *   状態を記憶しているクラス
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
			 *   コンストラクタ
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
			 *   データのグループID
			 * 
			 * @return
			 */
			public int getGroupId()
			{
				return (mGroup);
			}

			/**
			 *   スイッチのIDを応答する。
			 * @return
			 */
			public int getSwitchId()
			{
				return (mSwitchId);
			}

			/**
			 *   スイッチ状態を応答する
			 *   
			 * @return
			 */
			public boolean getIsSwitchOn()
			{
				return (isSwitchOn);
			}
			
			/**
			 *   転倒しているかどうかの判定を行う。
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
						// ドンという音を鳴らす。
					    soundEffect.play(seTaikoId, 1, 1, 3, 0, 1);
					}
					
					// 太鼓をたたいたことを記録する
					hitTaiko(mSwitchId);
				}
				
				if ((upperLimit == null)||(lowerLimit == null))
	            {
					// 転倒していないことにする。
					isSwitchOn = false;
		            if (isupdateScreen == true)
		            {
		            	updateScreen(currentValue, false);
		            }
					return (isSwitchOn);
	            }
				
	        	// しきい値の取得
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
	            	// 転倒していない！
	            	isSwitchOn = false;
	            }
	            else
	            {
	                // 転倒している！
	            	isSwitchOn = true;
	            }
	            if (isupdateScreen == true)
	            {
	            	updateScreen(currentValue, isSwitchOn);
	            }
	            return (isSwitchOn);
			}

			/**
			 *    画面にデータを表示する
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
	            	// エラー発生？
	            	Log.v(PSBFMain.APP_IDENTIFIER, "ID : " + mSwitchId + " " + ex.toString());
	            }
			}
		}

    /**
     *    Interface ISumoGameEventReceiver ： ゲーム状態の通知を行うインタフェース 
     * 
     * @author MRSa
     *
     */
    public interface ISumoGameEventReceiver
    {
        /** クラスの準備 **/
    	public abstract void prepare();
    	
    	/** 転倒したことを通知する  **/
        public abstract void detectFalldown(int fighterId);

        /** ゲームの状態がクリアされた　**/
        public abstract void resetField();

        /** ゲームが開始されたことを通知する **/
        public abstract void startGame();

        /** (画面更新のトリガー) **/
        public abstract void update();
    }

}
