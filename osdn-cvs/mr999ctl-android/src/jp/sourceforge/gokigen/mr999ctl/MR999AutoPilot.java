package jp.sourceforge.gokigen.mr999ctl;


/**
 *  MR-999�̎�������������ǂ�N���X
 *  
 * @author MRSa
 *
 */
public class MR999AutoPilot
{
    private ScriptHolder            scriptHolder = null;
    private PreferenceHolder        prefHolder   = null;
    private MR999HardwareController hardControl = null;  // �n�[�h����
    
    /**
     *  �R���X�g���N�^
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
     *  ���������� (�R���X�g���N�^����Ă΂�鏈��)
     */
    private void initializeSelf()
    {
        // ���͉������Ȃ�
    }

    /**
     *  �O������Ă΂�鏉��������
     */
    public void prepare()
    {
        // �X�N���v�g�ێ��N���X�̏���
        scriptHolder.prepare();
    }

    /**
     *  �I�[�g�p�C���b�g�����̎��s�J�n
     */
    public void startAutoPilot()
    {
        // �X�N���v�g�t�@�C����ǂݏo��
        int nofLines = scriptHolder.readScript(prefHolder.getScriptFileName());
        if (nofLines <= 0)
        {
            // �X�N���v�g�̓ǂݏo���Ɏ��s...�I������
            return;
        }

        // �P�s�Âǂݏo���Ď��s���s��
        while (prefHolder.getAutoPilotMode() == true)
        {
            boolean isStop = false;  // �R�}���h�����s���邩�ǂ����̃`�F�b�N
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
                //  �R�}���h���s���I��������
                break;
            }        
        }
        System.gc();
        return;
    }
    
    /**
     *   ���ۂɃR�}���h�����s����
     * @param info  �R�}���h���s���
     * @return true: �R�}���h���s�̏I��, false: �R�}���h���s�̌p��
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
            // JUMP  : �X�N���v�g���ł̖������W�����v
            scriptHolder.setNextScriptLine(arg1);
            break;

          case ScriptHolder.commandInfo.OPERATION_SLEEP:
            // SLEEP : �ꎞ�I�ɓ�����Ƃ߂�
            hardControl.pause(arg1);
            break;

          case ScriptHolder.commandInfo.OPERATION_MOVE:
            // MOVE : �w�莞�ԃA�[���̓�������s����
            hardControl.move(arg1, arg2);
            break;

          case ScriptHolder.commandInfo.OPERATION_STOP:
            // STOP : ������~������
            hardControl.stop();
            break;

          case ScriptHolder.commandInfo.OPERATION_NOP:
            // NOP : �������Ȃ�
            break;

          case ScriptHolder.commandInfo.OPERATION_END:
          default:
            // END : ���s�I��
            isFinish = true;
            break;
        }
        return (isFinish);
    }
}
