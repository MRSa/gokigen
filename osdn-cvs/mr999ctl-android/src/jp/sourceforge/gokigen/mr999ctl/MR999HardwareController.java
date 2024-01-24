package jp.sourceforge.gokigen.mr999ctl;

import jp.sourceforge.gokigen.usbiowrapper.LibUsbGokigenWrapper;

/**
 *  ロボットアームの制御を行う人
 *  
 *    5軸の制御
 *    　 - Base     : 左回転 / 右回転
 *       - Shoulder : 上 / 下
 *       - Elbow    : 上 / 下
 *       - Wrist    : 左回転 / 右回転
 *       - Gripper  : 開 / 閉
 *  
 *    制御信号は、ms単位で送出する。
 *       ※ 移動角度とか移動距離といった話は、ここを呼び出す元でmsに変換する。
 *  
 * @author MRSa
 *
 */
public class MR999HardwareController
{
    private static final int PAUSE_LIMIT_DURATION = 99;  // 動作停止時間
    
    private static final int gripperMask     = 0x00000003;
    private static final int wristMask       = 0x00000300;
    private static final int elbowMask       = 0x0000000c;
    private static final int shoulderMask    = 0x000000c0;
    private static final int baseMask        = 0x00000030;

    public static final int GripperOpen      = 0x00000001;
    public static final int GripperClose     = 0x00000002;
    public static final int WristTurnLeft    = 0x00000100;
    public static final int WristTurnRight   = 0x00000200;
    public static final int ElbowUp          = 0x00000008;
    public static final int ElbowDown        = 0x00000004;
    public static final int ShoulderUp       = 0x00000040;
    public static final int ShoulderDown     = 0x00000080;
    public static final int BaseTurnLeft     = 0x00000010;
    public static final int BaseTurnRight    = 0x00000020;
    
    private boolean isReady = false;
    private LibUsbGokigenWrapper accessWrapper = null;    
    
    /**
     *  コンストラクタ
     * @param 
     */
    public MR999HardwareController()
    {
        super();
        initializeSelf();
    }

    /**
     *  初期化処理 (コンストラクタから呼ばれる処理)
     */
    private void initializeSelf()
    {
        accessWrapper = new LibUsbGokigenWrapper();        
    }

    /**
     *  動作準備処理
     */
    public boolean prepare()
    {
        // (必要であれば)ハードウェアの初期化処理を行う        
        isReady = accessWrapper.prepareUsbIo();
        return (isReady);
    }

    /**
     *  緊急停止
     *    ※ prepare()を使って、動作準備をやり直さないと動作しない。
     */
    public void emergencyOff()
    {
        stop();
        isReady = false;
    }
 
    /**
     *   アーム動作停止
     */
    public void stop()
    {
        // 動作停止指示を送出する（ポート０とポート１の両方とも）
        accessWrapper.outputUsbIo(0, 0xff);
        accessWrapper.outputUsbIo(1, 0xff);
        return;        
    }

    /**
     *   アームの動作実行
     * @param controlTarget  動作させる軸
     * @param duration       動作時間 (単位 : ms)
     * @return <code>true</code> 動作成功,  <code>false</code> 動作失敗
     */
    public boolean move(int controlTarget, int duration)
    {
        if (isReady == false)
        {
            // 動作準備されていないときには、動作指示は送らない。
            return (false);
        }

        // 指示されたコマンド（動かすた軸）を解析する
        int gripperCommand  = controlTarget & gripperMask;
        int wristCommand    = controlTarget & wristMask;
        int elbowCommand    = controlTarget & elbowMask;
        int shoulderCommand = controlTarget & shoulderMask;
        int baseCommand     = controlTarget & baseMask;

        int outputValue = 0xff;

        //  ベース
        if (baseCommand == BaseTurnLeft)
        {
            outputValue = outputValue - 16;
        }
        else if (baseCommand == BaseTurnRight)
        {
            outputValue = outputValue - 32;
        }

        // 肩
        if (shoulderCommand == ShoulderUp)
        {
            outputValue = outputValue - 64;
        }
        else if (shoulderCommand == ShoulderDown)
        {
            outputValue = outputValue - 128;
        }

        // ひじ
        if (elbowCommand == ElbowUp)
        {
            outputValue = outputValue - 8;
        }
        else if (elbowCommand == ElbowDown)
        {
            outputValue = outputValue - 4;
        }
        
        // 指（グリッパー）
        if (gripperCommand == GripperOpen)
        {
            outputValue = outputValue - 2;
        }
        else if (gripperCommand == GripperClose)
        {
            outputValue = outputValue - 1;
        }
        
        // ポート０へコマンドを送出する
        accessWrapper.outputUsbIo(0, outputValue);
        
        // 腕 (Wrist) だけは、ポート1へコマンドを送出する
        if (wristCommand != 0)
        {
            outputValue = 0xff;
            if (wristCommand == WristTurnLeft)
            {
                outputValue = outputValue - 1;
            }
            else if (wristCommand == WristTurnRight)
            {
                outputValue = outputValue - 2; 
            }
            accessWrapper.outputUsbIo(1, outputValue);
        }
        
        // 動作指示時間の指定確認
        if (duration == 0)
        {
            //  動作時間が指定されていなかった場合には、
            // 動作指示を送ったまま、ここで終了する。
            return (true);
        }

        // 一時停止をする
        pause(duration);
        
        // 動作停止指示を送る
        stop();
        
        // OK応答する
        return (true);
    }
    
    /**
     *  動作の一時停止
     * @param duration
     */
    public void pause(int duration)
    {
        //  値が制限値よりも小さければ、即時return
        if (duration <= PAUSE_LIMIT_DURATION)
        {
            return;
        }
        
        //////////////////////////////////////////////////////////
        try
        {
            // 指定された時間、待つ。
            java.lang.Thread.sleep(duration);
        }
        catch (Exception ex)
        {
            // 何もしない (割り込み発生...)
        }
        //////////////////////////////////////////////////////////
    }
}
