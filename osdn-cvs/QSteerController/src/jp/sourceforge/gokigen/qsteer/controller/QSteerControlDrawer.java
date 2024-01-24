package jp.sourceforge.gokigen.qsteer.controller;

import jp.sourceforge.gokigen.qsteer.controller.TouchMotionRecognizer.IMoveCommand;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 
 * @author MRSa
 *
 */
public class QSteerControlDrawer implements ICanvasDrawer, TouchMotionRecognizer.IMoveCommand
{
	private final int bgColor = 0;   // �������F
	
    private Activity parent = null;  // �e��
    private IRedrawer drawTrigger = null;  // 
    private TouchMotionRecognizer eventHandler = null;
    private UsbAccessoryManagement usbAccessory = null;
    private ImageProcessor imageProcessor = null;
    
    private byte bandACommand = 0x00;   // �Ō�ɔ��s�����R�}���h
    private byte bandBCommand = 0x00;   // �Ō�ɔ��s�����R�}���h 
    
    private boolean autoPilotMode = false;
    private boolean lastPublishedMove = false;
    
    /**
     *     �R���X�g���N�^
     * 
     */
	public QSteerControlDrawer(Activity parent, IRedrawer trigger, UsbAccessoryManagement usbAccessory, ImageProcessor imageProcessor)
	{
	    this.parent = parent;
	    this.drawTrigger =trigger;
	    this.usbAccessory = usbAccessory;
	    this.imageProcessor = imageProcessor;

	    // TouchMotionRecognizer
	    eventHandler = new TouchMotionRecognizer(parent, this);
	}

	/**
	 *   �N�����̏���...
	 */
	public void prepareToStart(int width, int height)
    {
        eventHandler.prepareToStart(width, height);
        
        if (imageProcessor != null)
        {
            imageProcessor.prepareToStart(width, height);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        autoPilotMode = preferences.getBoolean("AutoPilot", false);
    }

	/**
	 * 
	 */
	public void finished()
	{
        if (imageProcessor != null)
        {
            imageProcessor.finished();
        }
	}
	
	/**
	 *    ��ʃT�C�Y���ς�����Ƃ�...
	 */
    public void changedScreenProperty(int format, int width, int height)
    {
        Log.v(GokigenSymbols.APP_IDENTIFIER, "QSteerControlDrawer::changedScreenProperty() " + " f:" + format + " w:" + width + " h:" + height);
    }

    /**
     *   ��ʕ`�惁�C������
     *   �i�`�悷����e�́A�{���\�b�h�ŏ����Ε\���ł���j
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	// Log.v(GokigenSymbols.APP_IDENTIFIER, "QSteerControlDrawer::drawOnCanvas()");
    	try
    	{
    		// ��ʑS�̂��N���A����
    		canvas.drawColor(bgColor, PorterDuff.Mode.CLEAR);

            // �w�i�摜��\��
    		drawBackground(canvas);

    		// �I�u�W�F�N�g��\��
    		drawObjects(canvas);

    		// ���s�����R�}���h��\��
    		drawPublishedCommands(canvas);
    	}
    	catch (Exception ex)
    	{
    		// ��O����...�ł����̂Ƃ��ɂ͉������Ȃ�
    		Log.v(GokigenSymbols.APP_IDENTIFIER, "drawOnCanvas() ex: " + ex.getMessage());
    	}
    }

    /**
     *   �^�b�`�C�x���g�̏���
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
    	return (eventHandler.onTouchEvent(event));
    }

    /**
     *     USB�A�N�Z�T���ɓ���R�}���h�𔭍s����
     * 
     */
    public void publishMove(byte command, byte arg1, byte arg2, byte arg3)
    {
        byte[] message = new byte[4];
        message[0] = command;
        message[1] = arg1;
        message[2] = arg2;
        message[3] = arg3;
        usbAccessory.sendCommand(message);

        bandACommand = (byte) (arg1 & TouchMotionRecognizer.IMoveCommand.MOVE_MASK);
        bandBCommand = (byte) (arg2 & TouchMotionRecognizer.IMoveCommand.MOVE_MASK);

        drawTrigger.redraw();
    }

    /**
     *    �w�i��`�悷��B
     * 
     * @param canvas
     */
    private void drawBackground(Canvas canvas)
    {
    	if (imageProcessor != null)
    	{
    		imageProcessor.drawBackground(canvas);
    	}    	
    }

    /**
     *    �I�u�W�F�N�g��`�悷��B
     * 
     * @param canvas
     */
    private void drawObjects(Canvas canvas)
    {
    	if (imageProcessor != null)
    	{
    		boolean ret = imageProcessor.drawObjects(canvas);

    		////////////////////////////////  �e���|��������  } /////////////////////////////////////
    		if ((autoPilotMode == true)&&(ret == true))
    		{
    			// ����R�}���h�𔭍s����
    	        byte cmd1 =  (byte) (IMoveCommand.BAND_A | IMoveCommand.MOVE_FWD_LEFT);
    	        byte cmd2 =  (byte) (IMoveCommand.BAND_B | IMoveCommand.MOVE_FWD_RIGHT);

    	        // ����R�}���h�̔��s
    	        publishMove(IMoveCommand.COMMAND_DOUBLE, cmd1, cmd2, IMoveCommand.LED_ON);
    			lastPublishedMove = true;
    		}
    		else if ((autoPilotMode == true)&&(ret == false)&&(lastPublishedMove == true))
    		{
    			// �����~�R�}���h�̔��s
    			publishMove(IMoveCommand.COMMAND_STOP, IMoveCommand.BAND_A, IMoveCommand.BAND_B, IMoveCommand.LED_OFF);
    			lastPublishedMove = false;
    		}
    		////////////////////////////////  } �e���|��������  /////////////////////////////////////
    	}
    }

    /**
     *    �R�}���h���s���ʂ�`�悷��
     * 
     * @param canvas
     */
    private void drawPublishedCommands(Canvas canvas)
	{
    	Paint paint = new Paint();

    	paint.setColor(Color.argb(64, 0, 0, 0));
    	canvas.drawRect(10.0f, 40.0f, 90.0f, 100.0f, paint);
    	
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(16.0f);
        paint.setStrokeWidth(1.0f);
        drawCommand(canvas, paint, bandACommand, 20.0f, 60.0f);

        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(16.0f);
        paint.setStrokeWidth(1.0f);
        drawCommand(canvas, paint, bandBCommand, 20.0f, 90.0f);
	}

    /**
     *    �R�}���h���s���ʂ�`�悷��
     * 
     * @param canvas
     * @param command
     * @param offsetX
     * @param offsetY
     */
    private void drawCommand(Canvas canvas, Paint paint, byte command, float offsetX, float offsetY)
    {
    	String message = "";
    	switch (command)
    	{
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD:
          	message = "��";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK:
          	message = "��";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_LEFT:
          	message = "��";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_RIGHT:
          	message = "��";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_DASH:
          	message = "����";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_LEFT:
          	message = "����";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_RIGHT:
          	message = "����";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_LEFT_DASH:
          	message = "������";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_FWD_RIGHT_DASH:
          	message = "������";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_LEFT:
          	message = "����";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_RIGHT:
          	message = "����";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_DASH:
          	message = "����";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_LEFT_DASH:
          	message = "������";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_BACK_RIGHT_DASH:
          	message = "������";
      	    break;
          case TouchMotionRecognizer.IMoveCommand.MOVE_STOP:
          default:
        	message = "STOP";
            break;
    	}
        canvas.drawText(" " + message, offsetX, offsetY, paint);
    }

    /**
     *    ��ʍĕ`��̃g���K
     * 
     * @author MRSa
     */
    public interface IRedrawer
    {	
        public abstract void redraw();
    }
}
