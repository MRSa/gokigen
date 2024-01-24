package jp.sourceforge.gokigen.mr999ctl;

import jp.sourceforge.gokigen.usbiowrapper.LibUsbGokigenWrapper;

/**
 *  ���{�b�g�A�[���̐�����s���l
 *  
 *    5���̐���
 *    �@ - Base     : ����] / �E��]
 *       - Shoulder : �� / ��
 *       - Elbow    : �� / ��
 *       - Wrist    : ����] / �E��]
 *       - Gripper  : �J / ��
 *  
 *    ����M���́Ams�P�ʂő��o����B
 *       �� �ړ��p�x�Ƃ��ړ������Ƃ������b�́A�������Ăяo������ms�ɕϊ�����B
 *  
 * @author MRSa
 *
 */
public class MR999HardwareController
{
    private static final int PAUSE_LIMIT_DURATION = 99;  // �����~����
    
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
     *  �R���X�g���N�^
     * @param 
     */
    public MR999HardwareController()
    {
        super();
        initializeSelf();
    }

    /**
     *  ���������� (�R���X�g���N�^����Ă΂�鏈��)
     */
    private void initializeSelf()
    {
        accessWrapper = new LibUsbGokigenWrapper();        
    }

    /**
     *  ���쏀������
     */
    public boolean prepare()
    {
        // (�K�v�ł����)�n�[�h�E�F�A�̏������������s��        
        isReady = accessWrapper.prepareUsbIo();
        return (isReady);
    }

    /**
     *  �ً}��~
     *    �� prepare()���g���āA���쏀������蒼���Ȃ��Ɠ��삵�Ȃ��B
     */
    public void emergencyOff()
    {
        stop();
        isReady = false;
    }
 
    /**
     *   �A�[�������~
     */
    public void stop()
    {
        // �����~�w���𑗏o����i�|�[�g�O�ƃ|�[�g�P�̗����Ƃ��j
        accessWrapper.outputUsbIo(0, 0xff);
        accessWrapper.outputUsbIo(1, 0xff);
        return;        
    }

    /**
     *   �A�[���̓�����s
     * @param controlTarget  ���삳���鎲
     * @param duration       ���쎞�� (�P�� : ms)
     * @return <code>true</code> ���쐬��,  <code>false</code> ���쎸�s
     */
    public boolean move(int controlTarget, int duration)
    {
        if (isReady == false)
        {
            // ���쏀������Ă��Ȃ��Ƃ��ɂ́A����w���͑���Ȃ��B
            return (false);
        }

        // �w�����ꂽ�R�}���h�i�����������j����͂���
        int gripperCommand  = controlTarget & gripperMask;
        int wristCommand    = controlTarget & wristMask;
        int elbowCommand    = controlTarget & elbowMask;
        int shoulderCommand = controlTarget & shoulderMask;
        int baseCommand     = controlTarget & baseMask;

        int outputValue = 0xff;

        //  �x�[�X
        if (baseCommand == BaseTurnLeft)
        {
            outputValue = outputValue - 16;
        }
        else if (baseCommand == BaseTurnRight)
        {
            outputValue = outputValue - 32;
        }

        // ��
        if (shoulderCommand == ShoulderUp)
        {
            outputValue = outputValue - 64;
        }
        else if (shoulderCommand == ShoulderDown)
        {
            outputValue = outputValue - 128;
        }

        // �Ђ�
        if (elbowCommand == ElbowUp)
        {
            outputValue = outputValue - 8;
        }
        else if (elbowCommand == ElbowDown)
        {
            outputValue = outputValue - 4;
        }
        
        // �w�i�O���b�p�[�j
        if (gripperCommand == GripperOpen)
        {
            outputValue = outputValue - 2;
        }
        else if (gripperCommand == GripperClose)
        {
            outputValue = outputValue - 1;
        }
        
        // �|�[�g�O�փR�}���h�𑗏o����
        accessWrapper.outputUsbIo(0, outputValue);
        
        // �r (Wrist) �����́A�|�[�g1�փR�}���h�𑗏o����
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
        
        // ����w�����Ԃ̎w��m�F
        if (duration == 0)
        {
            //  ���쎞�Ԃ��w�肳��Ă��Ȃ������ꍇ�ɂ́A
            // ����w���𑗂����܂܁A�����ŏI������B
            return (true);
        }

        // �ꎞ��~������
        pause(duration);
        
        // �����~�w���𑗂�
        stop();
        
        // OK��������
        return (true);
    }
    
    /**
     *  ����̈ꎞ��~
     * @param duration
     */
    public void pause(int duration)
    {
        //  �l�������l������������΁A����return
        if (duration <= PAUSE_LIMIT_DURATION)
        {
            return;
        }
        
        //////////////////////////////////////////////////////////
        try
        {
            // �w�肳�ꂽ���ԁA�҂B
            java.lang.Thread.sleep(duration);
        }
        catch (Exception ex)
        {
            // �������Ȃ� (���荞�ݔ���...)
        }
        //////////////////////////////////////////////////////////
    }
}
