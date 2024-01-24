package jp.sourceforge.gokigen.qsteer.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class TouchMotionRecognizer implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener
{

    private Activity parent = null;  // �e��
    private GestureDetector gestureDetector = null;
    private ScaleGestureDetector scaleGestureDetector = null;
    private IMoveCommand moveCommand = null;  // 

    private boolean onScaling = false;  // �s���`�C���E�s���`�A�E�g���삵�Ă��邩�ǂ���������
	private boolean onGestureProcessed = false;   // ���������̏������s�Ȃ��Ă��邩�ǂ����������B

	private float tempPosX = Float.MIN_VALUE;
	private float tempPosY = Float.MIN_VALUE;
	private float downPosX = Float.MIN_VALUE;
	private float downPosY = Float.MIN_VALUE;
	
    /**
     * 
     * @param parent
     * @param drawTrigger
     * @param usbAccessory
     */
	public TouchMotionRecognizer(Activity parent, TouchMotionRecognizer.IMoveCommand command)
	{
	    this.parent = parent;
	    this.moveCommand = command;
	    gestureDetector = new GestureDetector(parent, this);
	    scaleGestureDetector = new ScaleGestureDetector(parent, this);
	}

	public void prepareToStart(int width, int height)
    {
    	
    }
	
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean isDraw = false;

        /** �X�P�[���W�F�X�`��(�}���`�^�b�`�̃W�F�X�`��)���E�� **/
        isDraw = scaleGestureDetector.onTouchEvent(event);
    	if ((onScaling == true)||(scaleGestureDetector.isInProgress() == true))
    	{
    		//  �}���`�^�b�`���쒆...
        	Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchEvent() : multi touch...");
    		//return (true);
    	}

        /**  �W�F�X�`���[���E��...   **/
        isDraw = gestureDetector.onTouchEvent(event);
        if (isDraw == true)
        {
        	Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchEvent() : isDraw == true");
        	return (isDraw);
        }

    	int action = event.getAction();
    	
    	
        if (action == MotionEvent.ACTION_UP)
        {
        	// �^�b�`�������ꂽ�Ƃ��̏���...
            isDraw = onTouchUp(event);
            return (isDraw);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (preferences.getBoolean("ContinueTurn", false) == true)
        {
            // �p�����[�^���m�F���āA���[���Ɠ����������郂�[�h���������̏����B�B�B        	
        	isDraw = publishContinueTurnCommand();
        	return (isDraw);
        }        

        //Log.v(Main.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchEvent() : " + action);
        if (action == MotionEvent.ACTION_DOWN)
        {
        	// �^�b�`���ꂽ�Ƃ�
        	isDraw = onTouchDown(event);
        }
        else if (action == MotionEvent.ACTION_MOVE)
        {
        	// �^�b�`���ꂽ�܂ܓ������ꂽ�Ƃ��̏���
            isDraw = onTouchMove(event);
        }
        return (isDraw);
    }

    /**
     *   �^�b�`���ꂽ�^�C�~���O�ł̏���
     * @param event
     * @return
     */
    private boolean onTouchDown(MotionEvent event)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchDown() "  + event.getX()  + "," + event.getY());

        // �^�b�`�ʒu���L������
    	downPosX = event.getX();
    	downPosY = event.getY();

        return (true);
    }

    /**
     *   �^�b�`���ꂽ�^�C�~���O�ł̏���
     * @param event
     * @return
     */
    private boolean onTouchMove(MotionEvent event)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchMove() "  + event.getX()  + "," + event.getY());
        
        // �^�b�`�ʒu���L������
        tempPosX = event.getX();
        tempPosY = event.getY();

        float x = tempPosX - downPosX;
        float y = tempPosY - downPosY;
        
        float absX = Math.abs(x);

        byte cmd1 = 0x00;
        byte cmd2 = 0x00;
 
        if (y > 0)
        {
        	if (absX < 50.0f)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_FWD);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_FWD);
        	}
        	else if (x < 0.0f)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_FWD_LEFT);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_FWD_LEFT);        		
        	}
        	else  // if (x >= 0)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_FWD_RIGHT);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_FWD_RIGHT);        		
        	}
        }
        else
        {
        	if (absX < 50.0f)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_BACK);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_BACK);
        	}
        	else if (x < 0.0f)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_BACK_LEFT);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_BACK_LEFT);        		
        	}
        	else  // if (x >= 0)
        	{
            	cmd1 = (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_BACK_RIGHT);
            	cmd2 = (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_BACK_RIGHT);        		
        	}
        }

        // ����R�}���h�̔��s
        moveCommand.publishMove(IMoveCommand.COMMAND_DOUBLE, cmd1, cmd2, IMoveCommand.LED_ON);
        return (true);
    }

    /**
     *   publishContinueTurnCommand() : BandA�͍�����ABandB�͉E����̃R�}���h�𔭍s��������
     * 
     * @return true
     */
    private boolean publishContinueTurnCommand()
    {
        byte cmd1 =  (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_FWD_LEFT);
        byte cmd2 =  (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_FWD_RIGHT);

        // ����R�}���h�̔��s
        moveCommand.publishMove(IMoveCommand.COMMAND_DOUBLE, cmd1, cmd2, IMoveCommand.LED_ON);
        return (true);
    }
    
    /**
     *   �^�b�`�������ꂽ�^�C�~���O�ł̏���
     * @param event
     * @return
     */
    private boolean onTouchUp(MotionEvent event)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onTouchUp() "  + event.getX()  + "," + event.getY());

        boolean longPress = false;
        if (onGestureProcessed == true)
        {
        	// �����O�^�b�`���������ꍇ...�t���O�𗎂Ƃ�
        	onGestureProcessed = false;
        	longPress = true;
        }

        // �^�b�`�ʒu���I�u�W�F�N�g�摜�̍��W�ɕϊ�����
    	float x = event.getX();
    	float y = event.getY();

    	tempPosX = Float.MIN_VALUE;
    	tempPosY = Float.MIN_VALUE;
    	downPosX = Float.MIN_VALUE;
    	downPosY = Float.MIN_VALUE;

    	// ������~�߂�
    	moveCommand.publishMove(IMoveCommand.COMMAND_DOUBLE, (byte) (IMoveCommand.BAND_A|IMoveCommand.MOVE_STOP), (byte) (IMoveCommand.BAND_B|IMoveCommand.MOVE_STOP), IMoveCommand.LED_OFF);
    	return (true);
    }

    /**
     *    GestureDetector.OnGestureListener �̎���
     */
    public boolean onDown(MotionEvent event)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onDown() "  + event.getX()  + "," + event.getY());    	  
        return (false);    	  
    }

    /**
     *    GestureDetector.OnGestureListener �̎���
     */
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onFling() "  + velocityX  + "," + velocityY);    	  
        return (false);    	  
    }

    /**
     *    GestureDetector.OnGestureListener �̎���
     */
    public void onLongPress(MotionEvent event)
    {
  	    Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onLongPress() "  + event.getX()  + "," + event.getY());   
    }

    /**
     *    GestureDetector.OnGestureListener �̎���
     */
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onScroll() "  + distanceX  + "," + distanceY);    	  
        return (false);    	  
    }

    /**
     *    GestureDetector.OnGestureListener �̎���
     */
    public void onShowPress(MotionEvent event)
    {
       Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onShowPress() "  + event.getX()  + "," + event.getY());    	  
    }

    /**
     *    GestureDetector.OnGestureListener �̎���
     */
    public boolean onSingleTapUp(MotionEvent event)
    {
          Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onSingleTapUp() "  + event.getX()  + "," + event.getY());
          return (false);
    }

    /**
     *   �iScaleGestureDetector.OnScaleGestureListener �̎����j
     * 
     * @param detector
     * @return
     */
    public boolean onScale(ScaleGestureDetector detector)
    {
        float scaleFactor = detector.getScaleFactor();
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onScale() : " + scaleFactor );

        return (false);
    }

    /**
     *   �iScaleGestureDetector.OnScaleGestureListener �̎����j
     *   
     * 
     */
    public  boolean onScaleBegin(ScaleGestureDetector detector)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onScaleBegin() " );
  	    return (true);
    }

    /**
     *   �iScaleGestureDetector.OnScaleGestureListener �̎����j
     *   
     */
    public void	 onScaleEnd(ScaleGestureDetector detector)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "TouchMotionRecognizer::onScaleEnd() " );
  	    onScaling = false;
    }

    /**
     * 
     * 
     * @author MRSa
     *
     */
    public interface IMoveCommand
    {
    	public final byte  LED_OFF = 0x00;
    	public final byte  LED_ON = (byte) 0xff;

    	public final byte COMMAND_SINGLE = 0x01;
    	public final byte COMMAND_DOUBLE = 0x02;
    	public final byte COMMAND_STOP = (byte) 0xff;

    	public final byte BAND_A  = 0x00;
    	public final byte BAND_B  = 0x10;
    	public final byte BAND_C  = 0x20;
    	public final byte BAND_D  = 0x30;
    	public final byte BAND_A2 = 0x40;
    	public final byte BAND_B2 = 0x50;
    	public final byte BAND_C2 = 0x60;
    	public final byte BAND_D2 = 0x70;
    	
    	public final byte MOVE_FWD = 0x01;
    	public final byte MOVE_BACK = 0x02;
    	public final byte MOVE_LEFT = 0x03;
    	public final byte MOVE_RIGHT = 0x04;
    	public final byte MOVE_FWD_DASH = 0x05;
    	public final byte MOVE_FWD_LEFT = 0x06;
    	public final byte MOVE_FWD_RIGHT = 0x07;
    	public final byte MOVE_FWD_LEFT_DASH = 0x08;
    	public final byte MOVE_FWD_RIGHT_DASH = 0x09;
    	public final byte MOVE_BACK_LEFT = 0x0a;
    	public final byte MOVE_BACK_RIGHT = 0x0b;
    	public final byte MOVE_BACK_DASH = 0x0c;
    	public final byte MOVE_BACK_LEFT_DASH = 0x0d;
    	public final byte MOVE_BACK_RIGHT_DASH =0x0e;
    	public final byte MOVE_STOP = 0x0f;
    	public final byte MOVE_MASK = 0x0f;

    	public abstract void publishMove(byte command, byte arg1, byte arg2, byte arg3);
    }
}
