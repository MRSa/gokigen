package jp.sourceforge.gokigen.mr999ctl;

import jp.sourceforge.gokigen.mr999ctl.MainScreenUpdater;
import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

/**
 *  イベント処理クラス。
 * @author MRSa
 *
 */
public class MainScreenListener implements OnClickListener, OnTouchListener
{
    private Activity                parent          = null;  // 親分
    private MainScreenUpdater       updater         = null;  // 子分
    private MR999HardwareController hardControl     = null;  // ハード制御
    private MR999AutoPilot          autoPilotMain   = null;  // 自動実行メイン
    private PreferenceHolder        prefHolder      = null;  // 設定情報保持クラス

    /**
     *  コンストラクタ
     * @param argument 呼び出しもと（Activityクラス）
     */
    public MainScreenListener(Activity argument, PreferenceHolder holder)
    {
        super();
        parent        = argument;
        prefHolder    = holder;
        updater       = new MainScreenUpdater(parent);
        hardControl   = new MR999HardwareController();
        autoPilotMain = new MR999AutoPilot(new ScriptHolder(prefHolder), prefHolder, hardControl);
    }

    /**
     *  がっつりこのクラスにイベントリスナを接続する
     * 
     */
    public void prepareListener()
    {
        final Button gripperOpen  = (Button) parent.findViewById(R.id.Grip_Open);
        final Button gripperClose = (Button) parent.findViewById(R.id.Grip_Close);    
        final Button wristLeft    = (Button) parent.findViewById(R.id.Wrist_Left);    
        final Button wristRight   = (Button) parent.findViewById(R.id.Wrist_Right);    
        final Button elbowUp      = (Button) parent.findViewById(R.id.Elbow_Up);    
        final Button elbowDown    = (Button) parent.findViewById(R.id.Elbow_Down);    
        final Button shoulderUp   = (Button) parent.findViewById(R.id.Shoulder_Up);    
        final Button shoulderDown = (Button) parent.findViewById(R.id.Shoulder_Down);    
        final Button baseLeft     = (Button) parent.findViewById(R.id.Base_Left);    
        final Button baseRight    = (Button) parent.findViewById(R.id.Base_Right);    
        final Button actionStop   = (Button) parent.findViewById(R.id.Stop);    
        final Button showConfig   = (Button) parent.findViewById(R.id.Config);    

        /* クリックしたら動くもの */
        showConfig.setOnClickListener(this);

        /* ボタンを触ったりしたら動くもの */
        gripperOpen.setOnTouchListener(this);
        gripperClose.setOnTouchListener(this);
        wristLeft.setOnTouchListener(this);
        wristRight.setOnTouchListener(this);
        elbowUp.setOnTouchListener(this);
        elbowDown.setOnTouchListener(this);
        shoulderUp.setOnTouchListener(this);
        shoulderDown.setOnTouchListener(this);
        baseLeft.setOnTouchListener(this);
        baseRight.setOnTouchListener(this);
        actionStop.setOnTouchListener(this);
    }

    /**
     *  スタート準備
     */
    public void prepareToStart()
    {
        // ハードウェアの制御準備を行うよう、依頼する
        hardControl.prepare();
       
        // 自動制御実行クラスの処理開始準備
        autoPilotMain.prepare();        
        
        // 画面表示内容の更新を行うよう、画面情報表示クラスに依頼する
        updater.updateScreen();

        // 自動実行の確認
        onActivityResult(0, 0, null);    
    }

    /**
     *  ハードウェアの動作停止
     * 
     */
    public void shutdown()
    {
        // ハードウェアの制御を止めるよう依頼する(それも強制停止)
        hardControl.emergencyOff();

        // 画面表示内容の更新を行うよう、画面情報表示クラスに依頼する
        updater.updateScreen();
        
    }

    /**
     *  他画面から戻ってきたとき...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // 自動制御パラメータのモードを読み直す
        prefHolder.resetAutoPilotMode();

        // 自動制御パラメータがONの場合、、、
        if (prefHolder.getAutoPilotMode() == true)
        {
            updater.setValueToTextView(R.id.infoArea, "MR-999 AUTOPILOT");

            // スレッドを起こして自動制御処理を実行する
            Thread thread = new Thread()
            {
                public void run()
                {
                    // 自動制御を実行する
                    autoPilotMain.startAutoPilot();

                    // 自動制御実行が終了したことを表示する
                    updater.setValueToTextView(R.id.infoArea, "FINISHED AUTOPILOT");

                    // 自動制御実行フラグを落とす
                    prefHolder.clearAutoPilotMode();
                }
            };
            try
            {
                thread.start();
            }
            catch (Exception ex)
            {
                //

            }
        }
        return;
    }

    /**
     *   ボタンが押されたときの処理
     */
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.Config)
        {
            // 動作を停止させる
            hardControl.stop();

            // 自動実行を中止する
            prefHolder.clearAutoPilotMode();
            
            updater.setValueToTextView(R.id.infoArea, "MR-999 CONTROLLER");

            // 表示画面を設定画面へ切り替える
            showPreference();

            return;
        }
        return;
    }

    /**
     *   ボタンが触られたときの処理
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        int id = v.getId();
        int action = event.getAction();

        if (id == R.id.Stop)
        {
            // STOPボタン関係のときには、動作を止める
            // 
            hardControl.stop();

            // オートパイロットモードを停止させる
            prefHolder.clearAutoPilotMode();

            String message = "";
            if (action == MotionEvent.ACTION_DOWN)
            {
                // 表示するメッセージを変更する
                message = "STOP";
            }

            // メッセージを更新する
            updater.setValueToTextView(R.id.infoArea, message);
            return (true);            
        }

        if (prefHolder.getAutoPilotMode() == true)
        {
            // オートパイロットモード時には、ボタン操作を受け付けないようにする
            return (true);
        }        

/*
        if (action == MotionEvent.ACTION_MOVE)
        {
            // ボタン上でポインタを動かした場合には、何もせずに終了させる。
            return (true);
        }
*/
        
        // ボタンを押したとき以外は、動作を止める
        if ((action == MotionEvent.ACTION_UP)||
            (action == MotionEvent.ACTION_OUTSIDE)||
            (action == MotionEvent.ACTION_CANCEL))
        {
            // 動作を止める
            hardControl.stop();

            // メッセージを更新する
            updater.setValueToTextView(R.id.infoArea, "");
            return (true);
        }

        // ボタンIDから動作させる軸のIDへと変換する
        int axis = convertFromButtonIdToAxisId(id);       
        if (axis < 0)
        {
            // 軸の指定が間違っていたので、何も実行せずに終了する
            return (true);
        }

        // 動作指示を送出する
        hardControl.move(axis, 0);
        return (true);
    }

    /**
     *  ボタンIDから軸のIDへ変換する
     * @param buttonId
     * @return 軸のID (負の場合には変換エラー)
     */
    private int convertFromButtonIdToAxisId(int buttonId)
    {
        String message = "MR-999 CONTROLLER";
        int id = -1;
        switch (buttonId)
        {
          case R.id.Grip_Open:
            id = MR999HardwareController.GripperOpen;
            message = "Grip Open";
            break;

          case R.id.Grip_Close:
              id = MR999HardwareController.GripperClose;
            message = "Grip Close";
            break;

          case R.id.Wrist_Left:
              id = MR999HardwareController.WristTurnLeft;
            message = "Wrist Left";
            break;

          case R.id.Wrist_Right:
              id = MR999HardwareController.WristTurnRight;
            message = "Wrist Right";
            break;

          case R.id.Elbow_Up:
              id = MR999HardwareController.ElbowUp;
            message = "Elbow Up";
            break;

          case R.id.Elbow_Down:
              id = MR999HardwareController.ElbowDown;
            message = "Elbow Down";
            break;

          case R.id.Shoulder_Up:
              id = MR999HardwareController.ShoulderUp;
            message = "Shoulder Up";
            break;

          case R.id.Shoulder_Down:
              id = MR999HardwareController.ShoulderDown;
            message = "Shoulder Down";
            break;

          case R.id.Base_Left:
              id = MR999HardwareController.BaseTurnLeft;
            message = "Base Left";
            break;

          case R.id.Base_Right:
              id = MR999HardwareController.BaseTurnRight;
            message = "Base Right";
            break;

          default:
            // ID指定エラー...
            break;
        }

        // メッセージを更新する
        updater.setValueToTextView(R.id.infoArea, message);

        return (id);
    }

    /**
     *  設定画面を表示する処理
     */
    private void showPreference()
    {
        try
        {
            // 設定画面を呼び出す
            Intent prefIntent = new Intent(parent, jp.sourceforge.gokigen.mr999ctl.MR999Preferences.class);
            parent.startActivityForResult(prefIntent, 0);            
        }
        catch (Exception e)
        {
             // 例外発生...なにもしない。
        }
    }
}
