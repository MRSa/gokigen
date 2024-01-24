package jp.sourceforge.gokigen.psbf;

/**
 *    �f�[�^���͂��Ǘ�����N���X
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
     *    �R���X�g���N�^
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
     *    ADK�Ɛڑ��������̏���...
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
     *   �I�������B�B�B
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
     *   �X�C�b�`��Ԃ��ς�����Ƃ�
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
    			// �}�j���A�����샂�[�h
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
     *    �Q�[���R���g���[�����擾����
     * 
     * @return
     */
    public SumoGameController getGameController()
    {
        return (gameController);
    }
}
