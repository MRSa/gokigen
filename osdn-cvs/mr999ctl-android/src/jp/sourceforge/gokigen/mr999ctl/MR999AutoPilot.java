package jp.sourceforge.gokigen.mr999ctl;


/**
 *  MR-999の自動制御をつかさどるクラス
 *  
 * @author MRSa
 *
 */
public class MR999AutoPilot
{
    private ScriptHolder            scriptHolder = null;
    private PreferenceHolder        prefHolder   = null;
    private MR999HardwareController hardControl = null;  // ハード制御
    
    /**
     *  コンストラクタ
     * @param 
     */
    public MR999AutoPilot(ScriptHolder arg0, PreferenceHolder arg1, MR999HardwareController arg2)
    {
        super();

        scriptHolder = arg0;
        prefHolder   = arg1;
        hardControl  = arg2;

        initializeSelf();
    }

    /**
     *  初期化処理 (コンストラクタから呼ばれる処理)
     */
    private void initializeSelf()
    {
        // 今は何もしない
    }

    /**
     *  外部から呼ばれる初期化処理
     */
    public void prepare()
    {
        // スクリプト保持クラスの準備
        scriptHolder.prepare();
    }

    /**
     *  オートパイロット処理の実行開始
     */
    public void startAutoPilot()
    {
        // スクリプトファイルを読み出す
        int nofLines = scriptHolder.readScript(prefHolder.getScriptFileName());
        if (nofLines <= 0)
        {
            // スクリプトの読み出しに失敗...終了する
            return;
        }

        // １行づつ読み出して実行を行う
        while (prefHolder.getAutoPilotMode() == true)
        {
            boolean isStop = false;  // コマンドを実行するかどうかのチェック
            try
            {
                isStop = doExecute(scriptHolder.readNext());
            }
            catch (Exception ex)
            {
                isStop = true;
            }
            if (isStop == true)
            {
                //  コマンド実行を終了させる
                break;
            }        
        }
        System.gc();
        return;
    }
    
    /**
     *   実際にコマンドを実行する
     * @param info  コマンド実行情報
     * @return true: コマンド実行の終了, false: コマンド実行の継続
     */
    private boolean doExecute(ScriptHolder.commandInfo info)
    {
        boolean isFinish = false;
        int operation = info.getOperation();
        int arg1      = info.getTarget();
        int arg2      = info.getValue();
        
        switch (operation)
        {
          case ScriptHolder.commandInfo.OPERATION_JUMP:
            // JUMP  : スクリプト内での無条件ジャンプ
            scriptHolder.setNextScriptLine(arg1);
            break;

          case ScriptHolder.commandInfo.OPERATION_SLEEP:
            // SLEEP : 一時的に動作をとめる
            hardControl.pause(arg1);
            break;

          case ScriptHolder.commandInfo.OPERATION_MOVE:
            // MOVE : 指定時間アームの動作を実行する
            hardControl.move(arg1, arg2);
            break;

          case ScriptHolder.commandInfo.OPERATION_STOP:
            // STOP : 動作を停止させる
            hardControl.stop();
            break;

          case ScriptHolder.commandInfo.OPERATION_NOP:
            // NOP : 何もしない
            break;

          case ScriptHolder.commandInfo.OPERATION_END:
          default:
            // END : 実行終了
            isFinish = true;
            break;
        }
        return (isFinish);
    }
}
