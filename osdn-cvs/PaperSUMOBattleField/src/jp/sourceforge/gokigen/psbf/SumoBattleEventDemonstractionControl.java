package jp.sourceforge.gokigen.psbf;

import android.util.Log;

/**
 *   デモンストレーションモードで動作中、ゲーム状態変更・イベント発生に伴う処理
 * 
 * @author MRSa
 *
 */
public class SumoBattleEventDemonstractionControl  implements SumoGameController.ISumoGameEventReceiver
{
    PSBFBaseActivity mActivity = null;

    /**
     *   コンストラクタ
     *   
     * @param hostActivity
     */
    public SumoBattleEventDemonstractionControl(PSBFBaseActivity hostActivity)
    {
        mActivity = hostActivity;    	
    }
	
    /**
     *   転倒検出！
     */
    public void detectFalldown(int fighterId)
    {
    	
    	// モータ停止処理
    	Log.v(PSBFMain.APP_IDENTIFIER, "STOP MOTORs (FALLDOWN)");
    	//mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_A, 0);
    	//mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_B, 0);
    }

    /**
     *   ゲームスタート！
     */
    public void startGame()
    {
    	
    	
    }

    /**
     *    クラスの実行準備
     * 
     */
    public void prepare()
    {
    	
    }    

    /**
     *   画面更新のトリガー
     */
    public void update()
    {
    	
    	
    }

    /**
     *   状況をリセット
     * 
     */
    public void resetField()
    {
    	
    }
}
