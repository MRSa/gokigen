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
 *   出力コントローラクラス ... AndroidからADKを操作するときのクラス
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
     *   ADKに接続されたときに呼ばれるクラス
     */
    protected void onAccesssoryAttached()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
    	int operationMode = Integer.parseInt(preferences.getString("operationMode", "2"));
    	if (operationMode == PSBFBaseActivity.OPERATIONMODE_NORMAL)
    	{
        	//  「Stop Motor」ボタンが押されたときの処理
            final Button emoButton = (Button) mActivity.findViewById(R.id.EMOButton1);
            if (emoButton != null)
            {
                emoButton.setOnClickListener(this);
            }
            //  「Release」ボタンが押されたときの処理
            final Button resetButton = (Button) mActivity.findViewById(R.id.ResetButton);
            if (resetButton != null)
            {
            	resetButton.setOnClickListener(this);
            }

            // ノーマル操作モードはここで折り返す (スライダーを使ったモータの出力制御は行わない。）
     		return;
    	}
    	else if (operationMode == PSBFBaseActivity.OPERATIONMODE_DEMONSTRATION)
    	{
        	//  「Stop Motor」ボタンが押されたときの処理
            final Button emoButton = (Button) mActivity.findViewById(R.id.EMOButton2);
            if (emoButton != null)
            {
                emoButton.setOnClickListener(this);
            }

            //  「Release」ボタンが押されたときの処理
            final Button unlatchButton = (Button) mActivity.findViewById(R.id.UnlatchButton);
            if (unlatchButton != null)
            {
            	unlatchButton.setOnClickListener(this);
            }
    		// デモンストレーションモードはここで折り返す (スライダーを使ったモータの出力制御は行わない。）
    		return;    		
    	}

    	// マニュアル操作モード (PSBFBaseActivity.OPERATIONMODE_MANUAL)
    	
    	// モータを準備する
    	setupMotorController(PSBFBaseActivity.MOTOR_A, R.id.MotorASlider, R.id.MotorAValue);
    	setupMotorController(PSBFBaseActivity.MOTOR_B, R.id.MotorBSlider, R.id.MotorBValue);

    	//  「Stop Motor」ボタンが押されたときの処理
        final Button emoButton = (Button) mActivity.findViewById(R.id.EMOButton);
        if (emoButton != null)
        {
            emoButton.setOnClickListener(this);
        }
    }

    /**
     *    モータ制御部分を準備する処理
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
     *   クリックされたときの処理
     */
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.EMOButton)
        {
        	// モータの動作を止める処理
           SeekBar motorA = (SeekBar) mActivity.findViewById(R.id.MotorASlider);
           SeekBar motorB = (SeekBar) mActivity.findViewById(R.id.MotorBSlider);
           
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
           
       	   // モータ制御コマンド送信を復旧する
       	   mActivity.setSendCommandLatch(false);

       	   // ゲーム状態をリセットする
       	   if (gameController != null)
       	   {
       	       gameController.resetStatus();
       	   }
        }
        else if ((id == R.id.EMOButton1)||(id == R.id.EMOButton2))
        {
        	// モータ停止処理
        	Log.v(PSBFMain.APP_IDENTIFIER, "STOP MOTORs (and latch)");
        	mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_A, 0);
            try
            {
                // 一応念のため、ウェイトを入れておく。
                Thread.sleep(50);  // wait 50ms...
            }
            catch (Exception ex)
            {
            	//
            }
        	mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_B, 0);

        	// モータ制御コマンドの送出を停止する
        	mActivity.setSendCommandLatch(true);
        	
        	// 画面表示を更新する
        	if (gameController != null)
        	{
        		gameController.updateScreen();
        	}
        }
        else if (id == R.id.UnlatchButton)
        {
        	Log.v(PSBFMain.APP_IDENTIFIER, "unlatch");

        	// モータ制御コマンド送出を再開する
        	mActivity.setSendCommandLatch(false);

        	// 画面表示を更新する
        	if (gameController != null)
        	{
        		gameController.updateScreen();
        	}
        }
        else if (id == R.id.ResetButton)
        {
        	Log.v(PSBFMain.APP_IDENTIFIER, "reset");

        	// モータ制御コマンド送出を再開する
        	mActivity.setSendCommandLatch(false);

        	// ゲーム状態をリセットする
        	if (gameController != null)
        	{
        		gameController.resetStatus();
        	}
        }
    }
}
