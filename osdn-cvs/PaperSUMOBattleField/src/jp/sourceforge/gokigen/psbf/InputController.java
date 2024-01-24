package jp.sourceforge.gokigen.psbf;

/**
 *    データ入力を管理するクラス
 * 
 * @author MRSa
 *
 */
public class InputController extends IAccessoryController
{
    private PSBFBaseActivity parent = null;
    private SumoGameController gameController = null;
    private int operationMode = PSBFBaseActivity.OPERATIONMODE_MANUAL;

    /**
     *    コンストラクタ
     *      
     * @param hostActivity
     * @param operationMode
     */
    public InputController(PSBFBaseActivity hostActivity, int operationMode)
    {
        super(hostActivity);
        parent = hostActivity;
        this.operationMode = operationMode;
    }

    /**
     *    ADKと接続した時の処理...
     * 
     */
    protected void onAccesssoryAttached()
    {
    	try
    	{
        	gameController = new SumoGameController(parent, operationMode);
    	}
    	catch (Exception ex)
    	{
    		//
    	}
    }

    /**
     *   終了処理。。。
     */
    public void finishAction()
    {
    	try
    	{
    		gameController.finishAction();
    	}
    	catch (Exception ex)
    	{
    		//
    	}
    }

    /**
     *   スイッチ状態が変わったとき
     * 
     * @param switchIndex
     * @param switchState
     */
    public void switchStateChanged(int switchIndex, int switchState)
    {
    	boolean isRefreshDraw = false;
    	try
    	{
            if (operationMode == PSBFBaseActivity.OPERATIONMODE_MANUAL)
    		{
    			// マニュアル操作モード
    			isRefreshDraw = true;
    		}
    		gameController.changedStatusTrigger(switchIndex, switchState, operationMode, isRefreshDraw);
    	}
    	catch (Exception ex)
    	{
    		//
    	}
    }
    
    /**
     *    ゲームコントローラを取得する
     * 
     * @return
     */
    public SumoGameController getGameController()
    {
        return (gameController);
    }
}
