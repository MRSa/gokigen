package jp.sourceforge.gokigen.psbf;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
/**
 *   �ʏ탂�[�h�œ��쒆�A�Q�[����ԕύX�E�C�x���g�����ɔ�������
 * 
 * @author MRSa
 *
 */
public class SumoBattleEventNormalControl implements SumoGameController.ISumoGameEventReceiver, GokigenSurfaceView.ICanvasDrawer
{
    private PSBFBaseActivity mActivity = null;
    private GokigenSurfaceView view = null;
    private SumoGameController sumoController = null;
    
	private int backgroundColorRed = 0x00;
	private  int backgroundColorGreen = 0x40;
	private  int backgroundColorBlue = 0x00;

	private Paint painter = new Paint();
    
    private boolean gameFinished = false;

    /**
     *   �R���X�g���N�^
     *   
     * @param hostActivity
     */
    public SumoBattleEventNormalControl(PSBFBaseActivity hostActivity, SumoGameController controller)
    {
        mActivity = hostActivity;
        sumoController = controller;

        painter.setAntiAlias(true);
    	painter.setStrokeWidth(2.0f);
        painter.setColor(Color.WHITE);
    	painter.setStyle(Paint.Style.STROKE);
    }

    /**
     *   �]�|���o�I
     */
    public void detectFalldown(int fighterId)
    {
    	if (gameFinished == true)
    	{
    		// ���łɈ�x�]�|�����o�ρB�������Ȃ��B
    		return;
    	}
    	
    	// �Q�[���I����...
    	gameFinished = true;
    	
    	// ���[�^��~����
    	Log.v(PSBFMain.APP_IDENTIFIER, "STOP MOTORs (FALLDOWN NORMAL)");
    	mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_A, 0);

        // �ꉞ�O�̂��߁A�E�F�C�g�����Ă����B
        wait(60);

        mActivity.sendCommand(PSBFBaseActivity.MOTOR_SERVO_COMMAND, PSBFBaseActivity.MOTOR_B, 0);

        // �ꉞ�O�̂��߁A�E�F�C�g�����Ă����B
        wait(60);

    	// ���[�^��������b�`�i���[�^����R�}���h�̑��o���~�j����
    	mActivity.setSendCommandLatch(true);
    	
    	// ��ʍX�V
    	update();
    }

    /**
     *    ������������~����
     *    
     * @param ms
     */
    private void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);  // wait...
        }
        catch (Exception ex)
        {
        	//
        }
    }

    /**
     *    �N���X�̎��s����
     * 
     */
    public void prepare()
    {
    	try
    	{
            final GokigenSurfaceView surfaceView = (GokigenSurfaceView) mActivity.findViewById(R.id.GraphicViewMain);
    		surfaceView.setCanvasDrawer(this);
           	view = surfaceView;
    		update();
    	}
    	catch (Exception ex)
    	{
    		
    	}
    }    
    
    /**
     *   ��ʍX�V�̃g���K�[ (��ʕ\�����X�V����...)
     */
    public void update()
    {
    	try
    	{
    		view.doDraw();
    	}
    	catch (Exception ex)
    	{
    		//
    	}
    }

    /**
     *   �Q�[���X�^�[�g�I
     */
    public void startGame()
    {
    	// ��ʍX�V...
    	update();
    }
    
    /**
     *   �󋵂����Z�b�g
     * 
     */
    public void resetField()
    {
        gameFinished = false;

        // ��ʍX�V
        update();
    }

    /**
     *   ��ʕ`��
     * 
     */
    public void drawOnCanvas(Canvas canvas)
    {
        try
        {
    		// ��ʑS�̂�h��Ԃ�
    		canvas.drawColor(Color.rgb(backgroundColorRed, backgroundColorGreen, backgroundColorBlue));

            // ���[�^�����Ԃ�\������    		
    		drawLatchedStatus(canvas);

    		// �Q�[����Ԃ�\������
    		drawGameStatus(canvas);
    		
        }
        catch (Exception ex)
        {
        	// 
        }
    	
    }

    /**
     *    �����~�����ǂ����̕\�����s��
     * 
     * @param canvas
     */
    private void drawLatchedStatus(Canvas canvas)
    {
		boolean isLatched = mActivity.getSendCommandStatus();
		
		if (isLatched == true)
		{
	        painter.setTextSize(24);
	        painter.setColor(Color.YELLOW);
	        String msg = "���[�^�����~��";
	        canvas.drawText(msg, 10, 20, painter);
			// 
		}
		else
		{
	        painter.setTextSize(24);
	        painter.setColor(Color.WHITE);
	        String msg = " ";
	        canvas.drawText(msg, 10, 20, painter);
			// 			
		}
    }
    
    /**
     *    �Q�[����Ԃ�\������
     * 
     * @param canvas
     */
    private void drawGameStatus(Canvas canvas)
    {
		if (gameFinished == true)
		{
			// �Q�[���I����� �F ���������Ă��邱�Ƃ�\������
	        painter.setTextSize(100);
	        painter.setColor(Color.YELLOW);
	        String msg = "��������I";
	        canvas.drawText(msg, 10, 150, painter);

	        //  ���҂�\������
	        int id = sumoController.getWinner();
	        String winner = " ";
	        if (id == SumoGameController.FIGHTER_A)
	        {
		        painter.setColor(Color.MAGENTA);
	        	winner = "�� (W)";
	        }
	        else if (id == SumoGameController.FIGHTER_B)
	        {
		        painter.setColor(Color.CYAN);
	        	winner = "�� (E)";
	        }
	        canvas.drawText(winner, 100, 300, painter);
		}
		else
		{
			// �Q�[���i�s���̕\��...
	        painter.setTextSize(100);
	        painter.setColor(Color.WHITE);
	        String msg = "�@���I";
	        canvas.drawText(msg, 100, 150, painter);
		}
    }

    /**
     *   ��ʂ��^�b�v���ꂽ�I
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	Log.v(PSBFMain.APP_IDENTIFIER, "SumoBattleDrawer::onTouchEvent()");
    	update();
        return (false);    	
    }
}
