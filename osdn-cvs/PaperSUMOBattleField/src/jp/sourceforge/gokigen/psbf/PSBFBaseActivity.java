package jp.sourceforge.gokigen.psbf;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

// ANDROID 2.3.4
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 *   DemoKitActivity ��ύX�����AADK(Arduino) �Ƃ̒ʐM���s��Activity(��Base�N���X)
 * 
 * @author MRSa
 *
 */
public class PSBFBaseActivity extends Activity implements Runnable
{
    private static final String TAG = "PaperSumo";
    private static final String ACTION_USB_PERMISSION = "jp.sourceforge.gokigen.psbf.action.USB_PERMISSION";

    /**  ����M���b�Z�[�W ID�ꗗ (ADK <> Android) **/
    private static final int MESSAGE_SWITCH = 1;  // �X�C�b�`��ԕ�  ADK > Android (Receive)
    public static final byte LED_CONTROL_COMMAND = 2;  // LED����w��     Android > ADK (Send)
    public static final byte MOTOR_SERVO_COMMAND = 3;  // ���[�^����w�� Android > ADK (Send)

    public static final byte MOTOR_A = 0;          //  ���[�^�P
    public static final byte MOTOR_B = 1;          // ���[�^�Q
    
    public static final int OPERATIONMODE_NORMAL = 0;
    public static final int OPERATIONMODE_DEMONSTRATION = 1;
    public static final int OPERATIONMODE_MANUAL = 2;
    
    private UsbManager mUsbManager = null;
    private PendingIntent mPermissionIntent = null;
    private boolean mPermissionRequestPending = false;

    private UsbAccessory mAccessory = null;

    protected InputController mInputController = null;

    private ParcelFileDescriptor mFileDescriptor = null;
    private FileInputStream mInputStream = null;
    private FileOutputStream mOutputStream = null;
    
    private boolean mSendCommandLatched = false;
    
    byte[] sendBuffer = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //  ANDROID 2.3.4
        mUsbManager = UsbManager.getInstance(this);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        if (getLastNonConfigurationInstance() != null)
        {
            //  ANDROID 2.3.4
        	mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
            
            openAccessory(mAccessory);
        }

        /** ���C�����C�A�E�g�̕\�� **/
        setContentView(R.layout.main);

        enableControls(false);

        if (mAccessory != null)
        {
            showControls();
        }
        else
        {
            hideControls();
        }

    }

    protected void hideControls()
    {
        setContentView(R.layout.no_device);
        mInputController = null;
    }

    protected void showControls()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	int operationMode = Integer.parseInt(preferences.getString("operationMode", "2"));
    	if (operationMode == OPERATIONMODE_NORMAL)
    	{
            // �ʏ푀�샂�[�h
    		setContentView(R.layout.main);
    	}
    	else if (operationMode == OPERATIONMODE_DEMONSTRATION)
    	{
            // �f�����X�g���[�V�������[�h
    		setContentView(R.layout.demomain);
    	}
    	else  // if (operationMode == OPERATIONMODE_MANUAL)
    	{
    		// �}�j���A�����샂�[�h
    		setContentView(R.layout.manualmain);
    	}
    	mInputController = null;
        mInputController = new InputController(this, operationMode);
        mInputController.accessoryAttached();
    }

    
    @Override
    public Object onRetainNonConfigurationInstance()
    {
        if (mAccessory != null)
        {
            return mAccessory;
        }
        else
        {
            return super.onRetainNonConfigurationInstance();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

//        Intent intent = getIntent();
        if (mInputStream != null && mOutputStream != null)
        {
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null)
        {
            if (mUsbManager.hasPermission(accessory))
            {
                openAccessory(accessory);
            }
            else
            {
                synchronized (mUsbReceiver) 
                {
                    if (!mPermissionRequestPending)
                    {
                        mUsbManager.requestPermission(accessory, mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        }
        else
        {
            Log.d(TAG, "mAccessory is null");
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        closeAccessory();
    }

    @Override
    public void onDestroy()
    {
    	try
    	{
    	    mInputController.finishAction();
    	}
    	catch (Exception ex)
    	{
    		// �k���|�΍�... 
    		
    	}
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    private void openAccessory(UsbAccessory accessory)
    {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null)
        {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
            Thread thread = new Thread(null, this, TAG);
            thread.start();
            Log.d(TAG, "accessory opened");
            enableControls(true);
        }
        else
        {
            Log.d(TAG, "accessory open fail");
        }
    }

    private void closeAccessory()
    {
        enableControls(false);

        try
        {
            if (mFileDescriptor != null)
            {
                mFileDescriptor.close();
            }
        }
        catch (IOException e)
        {
            //
        }
        finally
        {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }

    /**
     *   ����̊J�n �i�g���_�j
     * 
     * @param enable
     */
    protected void enableControls(boolean enable)
    {
        if (enable)
        {
            showControls();
        }
        else
        {
            hideControls();
        }
    }

    /**
     *   ��M�X���b�h
     * 
     */
    public void run() 
    {
        int ret = 0;
        byte[] buffer = new byte[16384];
        int i;

        while (ret >= 0)
        {
            try
            {
                ret = mInputStream.read(buffer);
            }
            catch (IOException e) 
            {
                break;
            }

            i = 0;
            while (i < ret) 
            {
                int len = ret - i;

                /** �X�C�b�`�f�[�^�̓��� **/
                if (buffer[i] == 0x01)
                {
                    if (len >= 4)
                    {
                        Message m = Message.obtain(mHandler, MESSAGE_SWITCH);
                        m.obj = new ReceivedMessage(buffer[i + 1], buffer[i + 2], buffer[i + 3]);
                        mHandler.sendMessage(m);
                        //Log.v(PSBFMain.APP_IDENTIFIER, "RECEIVE SWITCH : " + buffer[i + 1] + " " + buffer[i + 2] + " " + buffer[i + 3]);
                    }
                    i += 4;
                }
                else
                {
                    Log.d(TAG, "Received unknown msg: " + buffer[i] + "(i:" + i + ", len:" + len + ")");
                    i = len;
                }
            }
        }
    }

    /**
     *   ADK����f�[�^�����
     * 
     */
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
              case MESSAGE_SWITCH:
                 ReceivedMessage o = (ReceivedMessage) msg.obj;
                 handleReceivedMessage(o);
                 break;
            }
        }
    };

    /**
     *   ���M�R�}���h��}�~���邩�ǂ����H
     * 
     * @param status ���M�R�}���h�𑗐M����(false) / ���M�R�}���h�̑��M���~����(true)
     */
    public void setSendCommandLatch(boolean status)
    {
    	mSendCommandLatched = status;
    }

    /**
     *   ���M�R�}���h�}�~��Ԃ���������
     * 
     * @return
     */
    public boolean getSendCommandStatus()
    {
    	return (mSendCommandLatched);
    }
    
    /**
     *    ADK�փ��b�Z�[�W�𑗐M����
     * 
     * @param command  ���o�R�}���h
     * @param target        ���MID (0x00 �` 0xff)
     * @param value         ���M�f�[�^ (0x00 �` 0xff)
     */
    public void sendCommand(byte command, byte target, int value)
    {
        if ((mOutputStream == null)||(target == -1)||(sendBuffer != null)||(mSendCommandLatched == true))
        {
        	// ���b�Z�[�W���M���ł��Ȃ����...�f�[�^���̂ĂďI������
            Log.v(PSBFMain.APP_IDENTIFIER, "SEND COMMAND LATCHED :" + command + " " + target + " " + value);
        	return;
        }

    	sendBuffer = new byte[4];
        if (value > 255)
        {
            value = 255;
        }
        if (value < 0)
        {
            value = 0;
        }

        sendBuffer[0] = command;
        sendBuffer[1] = target;
        sendBuffer[2] = (byte) value;
        sendBuffer[3] = 0;

        // �X���b�h���N�����ă��b�Z�[�W�𑗐M����
        Thread thread = new Thread(new Runnable()
        {  
            public void run()
            {
            	try
            	{
                    mOutputStream.write(sendBuffer);
                    Log.v(PSBFMain.APP_IDENTIFIER, "SEND :" + sendBuffer[0] + " " + sendBuffer[1] + " " + sendBuffer[2] + " " + sendBuffer[3]);
            	}
                catch (Exception ex)
            	{
                	//
            	}
                sendBuffer = null;
            }
        });
        try
        {
            thread.start();
        }
        catch (Exception ex)
        {
             // ����łǂ�����...?
        }
    }

    protected void handleReceivedMessage(ReceivedMessage o)
    {
        if (mInputController != null)
        {
            byte id = o.getId();
            mInputController.switchStateChanged(id, o.getState());
        }
    }

    /**
     *   �A�v���O����̃C�x���g(Broadcast)�̎�M
     * 
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {
        /**
         *   onReceive() : �A�N�Z�T�����ڑ�/�ؒf���ꂽ���̏���
         * 
         */
    	@Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action))
            {
                synchronized (this)
                {
                    UsbAccessory accessory = UsbManager.getAccessory(intent);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                    {
                        openAccessory(accessory);
                    }
                    else
                    {
                        Log.d(TAG, "permission denied for accessory " + accessory);
                    }
                    mPermissionRequestPending = false;
                }
            }
            else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action))
            {
                UsbAccessory accessory = UsbManager.getAccessory(intent);
                if (accessory != null && accessory.equals(mAccessory))
                {
                    closeAccessory();
                }
            }
        }
    };

    /**
     *    ��M�������b�Z�[�W��ێ�����N���X
     *    
     * @author MRSa
     *
     */
    private class ReceivedMessage 
    {
        private byte id;
        private int state;

        /**
         *    �R���X�g���N�^�i��M���b�Z�[�W)
         * 
         * @param id              ��M���b�Z�[�W(1�o�C�g��)
         * @param stateHigh  ��M���b�Z�[�W(2�o�C�g��)
         * @param stateLow   ��M���b�Z�[�W(3�o�C�g��)
         */
        public ReceivedMessage(byte id, byte stateHigh, byte stateLow)
        {
            int high, low;
            high = stateHigh & 0xff;
            low  = stateLow & 0xff;
            this.id = id;
            this.state = high * 256 + low;
        }

        /**
         *  ��M���b�Z�[�W��ID (0x00�`0xff)
         * @return
         */
        public byte getId()
        {
            return id;
        }

        /**
         *   ��M���b�Z�[�W�̃f�[�^ (0x0000�`0xffff)
         * @return
         */
        public int getState()
        {
            return state;
        }
    }
}
