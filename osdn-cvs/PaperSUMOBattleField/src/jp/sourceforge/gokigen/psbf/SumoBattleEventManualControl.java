package jp.sourceforge.gokigen.psbf;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 *    マニュアルモードで動作中、ゲーム状態変更・イベント発生に伴う処理
 * 
 * @author MRSa
 *
 */
public class SumoBattleEventManualControl implements SumoGameController.ISumoGameEventReceiver
{
    PSBFBaseActivity mActivity = null;

	
	/**
	 *   コンストラクタ
	 */
    public SumoBattleEventManualControl(PSBFBaseActivity hostActivity)
    {
        mActivity = hostActivity;
    	
    }

    /**
     *   転倒検出！
     */
    public void detectFalldown(int fighterId)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean isStopMotor = preferences.getBoolean("stopMotorWhenLose", false);

        // モータの動作を止める処理
        SeekBar motorA = (SeekBar) mActivity.findViewById(R.id.MotorASlider);
        SeekBar motorB = (SeekBar) mActivity.findViewById(R.id.MotorBSlider);
        if (isStopMotor == true)
        {        
            // モータの動作を停止する処理（パラメータで停止することが指定されていた場合のみ）
        	motorA.setProgress(0);
            try
            {
                // 一応念のため、ウェイトを入れておく。
                Thread.sleep(50);  // wait 50ms...
            }
            catch (Exception ex)
            {
            	//
            }
            motorB.setProgress(0);
        }

        // 負けた方の力士のランプを転倒させる
        int fighterAImage = R.drawable.indicator_button_capacitive_off_noglow;
		int fighterBImage = R.drawable.indicator_button_capacitive_off_noglow;
        if (fighterId == SumoGameController.FIGHTER_A)
        {
        	// 力士Aが転倒
            fighterAImage = R.drawable.indicator_button_capacitive_on_noglow;
        }
        else if (fighterId == SumoGameController.FIGHTER_B)
        {
        	// 力士Bが転倒
            fighterBImage = R.drawable.indicator_button_capacitive_on_noglow;
        }

        // 転倒状態をイメージ表示する！
    	ImageView imageViewA = (ImageView) mActivity.findViewById(R.id.ButtonA1);
	    imageViewA.setImageDrawable(mActivity.getResources().getDrawable(fighterAImage));
	    ImageView imageViewB = (ImageView) mActivity.findViewById(R.id.ButtonB1);
	    imageViewB.setImageDrawable(mActivity.getResources().getDrawable(fighterBImage));
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
        // 転倒状態をイメージ表示（クリア）する！
    	ImageView imageViewA = (ImageView) mActivity.findViewById(R.id.ButtonA1);
	    imageViewA.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.indicator_button_capacitive_off_noglow));
	    ImageView imageViewB = (ImageView) mActivity.findViewById(R.id.ButtonB1);
	    imageViewB.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.indicator_button_capacitive_off_noglow));
    }
}
