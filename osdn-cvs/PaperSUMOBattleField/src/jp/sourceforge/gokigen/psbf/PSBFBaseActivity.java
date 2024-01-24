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
 *   DemoKitActivity を変更した、ADK(Arduino) との通信を行うActivity(のBaseクラス)
 * 
 * @author MRSa
 *
 */
public class PSBFBaseActivity extends Activity implements Runnable
{
    private static final String TAG = "PaperSumo";
    private static final String ACTION_USB_PERMISSION = "jp.sourceforge.gokigen.psbf.action.USB_PERMISSION";

    /**  送受信メッセージ ID一覧 (ADK <> Android) **/
    private static final int MESSAGE_SWITCH = 1;  // スイッチ状態報告  ADK > Android (Receive)
    public static final byte LED_CONTROL_COMMAND = 2;  // LED制御指示     Android > ADK (Send)
    public static final byte MOTOR_SERVO_COMMAND = 3;  // モータ制御指示 Android > ADK (Send)

    public static final byte MOTOR_A = 0;          //  モータ１
    public static final byte MOTOR_B = 1;          // モータ２
    
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

        /** メインレイアウトの表示 **/
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
            // 通常操作モード
    		setContentView(R.layout.main);
    	}
    	else if (operationMode == OPERATIONMODE_DEMONSTRATION)
    	{
            // デモンストレーションモード
    		setContentView(R.layout.demomain);
    	}
    	else  // if (operationMode == OPERATIONMODE_MANUAL)
    	{
    		// マニュアル操作モード
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
    		// ヌルポ対策... 
    		
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
     *   制御の開始 （拡張点）
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
     *   受信スレッド
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

                /** スイッチデータの入力 **/
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
     *   ADKからデータを入力
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
     *   送信コマンドを抑止するかどうか？
     * 
     * @param status 送信コマンドを送信する(false) / 送信コマンドの送信を停止する(true)
     */
    public void setSendCommandLatch(boolean status)
    {
    	mSendCommandLatched = status;
    }

    /**
     *   送信コマンド抑止状態か応答する
     * 
     * @return
     */
    public boolean getSendCommandStatus()
    {
    	return (mSendCommandLatched);
    }
    
    /**
     *    ADKへメッセージを送信する
     * 
     * @param command  送出コマンド
     * @param target        送信ID (0x00 ～ 0xff)
     * @param value         送信データ (0x00 ～ 0xff)
     */
    public void sendCommand(byte command, byte target, int value)
    {
        if ((mOutputStream == null)||(target == -1)||(sendBuffer != null)||(mSendCommandLatched == true))
        {
        	// メッセージ送信ができない状態...データを捨てて終了する
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

        // スレッドを起こしてメッセージを送信する
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
             // これでどうかな...?
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
     *   アプリ外からのイベント(Broadcast)の受信
     * 
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {
        /**
         *   onReceive() : アクセサリが接続/切断された時の処理
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
     *    受信したメッセージを保持するクラス
     *    
     * @author MRSa
     *
     */
    private class ReceivedMessage 
    {
        private byte id;
        private int state;

        /**
         *    コンストラクタ（受信メッセージ)
         * 
         * @param id              受信メッセージ(1バイト目)
         * @param stateHigh  受信メッセージ(2バイト目)
         * @param stateLow   受信メッセージ(3バイト目)
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
         *  受信メッセージのID (0x00～0xff)
         * @return
         */
        public byte getId()
        {
            return id;
        }

        /**
         *   受信メッセージのデータ (0x0000～0xffff)
         * @return
         */
        public int getState()
        {
            return state;
        }
    }
}
