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
 *  �C�x���g�����N���X�B
 * @author MRSa
 *
 */
public class MainScreenListener implements OnClickListener, OnTouchListener
{
    private Activity                parent          = null;  // �e��
    private MainScreenUpdater       updater         = null;  // �q��
    private MR999HardwareController hardControl     = null;  // �n�[�h����
    private MR999AutoPilot          autoPilotMain   = null;  // �������s���C��
    private PreferenceHolder        prefHolder      = null;  // �ݒ���ێ��N���X

    /**
     *  �R���X�g���N�^
     * @param argument �Ăяo�����ƁiActivity�N���X�j
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
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
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

        /* �N���b�N�����瓮������ */
        showConfig.setOnClickListener(this);

        /* �{�^����G�����肵���瓮������ */
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
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
        // �n�[�h�E�F�A�̐��䏀�����s���悤�A�˗�����
        hardControl.prepare();
       
        // ����������s�N���X�̏����J�n����
        autoPilotMain.prepare();        
        
        // ��ʕ\�����e�̍X�V���s���悤�A��ʏ��\���N���X�Ɉ˗�����
        updater.updateScreen();

        // �������s�̊m�F
        onActivityResult(0, 0, null);    
    }

    /**
     *  �n�[�h�E�F�A�̓����~
     * 
     */
    public void shutdown()
    {
        // �n�[�h�E�F�A�̐�����~�߂�悤�˗�����(�����������~)
        hardControl.emergencyOff();

        // ��ʕ\�����e�̍X�V���s���悤�A��ʏ��\���N���X�Ɉ˗�����
        updater.updateScreen();
        
    }

    /**
     *  ����ʂ���߂��Ă����Ƃ�...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // ��������p�����[�^�̃��[�h��ǂݒ���
        prefHolder.resetAutoPilotMode();

        // ��������p�����[�^��ON�̏ꍇ�A�A�A
        if (prefHolder.getAutoPilotMode() == true)
        {
            updater.setValueToTextView(R.id.infoArea, "MR-999 AUTOPILOT");

            // �X���b�h���N�����Ď������䏈�������s����
            Thread thread = new Thread()
            {
                public void run()
                {
                    // ������������s����
                    autoPilotMain.startAutoPilot();

                    // ����������s���I���������Ƃ�\������
                    updater.setValueToTextView(R.id.infoArea, "FINISHED AUTOPILOT");

                    // ����������s�t���O�𗎂Ƃ�
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
     *   �{�^���������ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.Config)
        {
            // ������~������
            hardControl.stop();

            // �������s�𒆎~����
            prefHolder.clearAutoPilotMode();
            
            updater.setValueToTextView(R.id.infoArea, "MR-999 CONTROLLER");

            // �\����ʂ�ݒ��ʂ֐؂�ւ���
            showPreference();

            return;
        }
        return;
    }

    /**
     *   �{�^�����G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        int id = v.getId();
        int action = event.getAction();

        if (id == R.id.Stop)
        {
            // STOP�{�^���֌W�̂Ƃ��ɂ́A������~�߂�
            // 
            hardControl.stop();

            // �I�[�g�p�C���b�g���[�h���~������
            prefHolder.clearAutoPilotMode();

            String message = "";
            if (action == MotionEvent.ACTION_DOWN)
            {
                // �\�����郁�b�Z�[�W��ύX����
                message = "STOP";
            }

            // ���b�Z�[�W���X�V����
            updater.setValueToTextView(R.id.infoArea, message);
            return (true);            
        }

        if (prefHolder.getAutoPilotMode() == true)
        {
            // �I�[�g�p�C���b�g���[�h���ɂ́A�{�^��������󂯕t���Ȃ��悤�ɂ���
            return (true);
        }        

/*
        if (action == MotionEvent.ACTION_MOVE)
        {
            // �{�^����Ń|�C���^�𓮂������ꍇ�ɂ́A���������ɏI��������B
            return (true);
        }
*/
        
        // �{�^�����������Ƃ��ȊO�́A������~�߂�
        if ((action == MotionEvent.ACTION_UP)||
            (action == MotionEvent.ACTION_OUTSIDE)||
            (action == MotionEvent.ACTION_CANCEL))
        {
            // ������~�߂�
            hardControl.stop();

            // ���b�Z�[�W���X�V����
            updater.setValueToTextView(R.id.infoArea, "");
            return (true);
        }

        // �{�^��ID���瓮�삳���鎲��ID�ւƕϊ�����
        int axis = convertFromButtonIdToAxisId(id);       
        if (axis < 0)
        {
            // ���̎w�肪�Ԉ���Ă����̂ŁA�������s�����ɏI������
            return (true);
        }

        // ����w���𑗏o����
        hardControl.move(axis, 0);
        return (true);
    }

    /**
     *  �{�^��ID���玲��ID�֕ϊ�����
     * @param buttonId
     * @return ����ID (���̏ꍇ�ɂ͕ϊ��G���[)
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
            // ID�w��G���[...
            break;
        }

        // ���b�Z�[�W���X�V����
        updater.setValueToTextView(R.id.infoArea, message);

        return (id);
    }

    /**
     *  �ݒ��ʂ�\�����鏈��
     */
    private void showPreference()
    {
        try
        {
            // �ݒ��ʂ��Ăяo��
            Intent prefIntent = new Intent(parent, jp.sourceforge.gokigen.mr999ctl.MR999Preferences.class);
            parent.startActivityForResult(prefIntent, 0);            
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        }
    }
}
