package jp.sourceforge.gokigen.qsteer.controller;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;


/**
 *    USBアクセサリ (ADK)の接続・切断送受信管理を行うクラス
 * 
 * @author MRSa
 *
 */
public class UsbAccessoryManagement implements Runnable
{

    private static final String TAG = "UsbAccessoryManagement";
    private static final int MESSAGE_RECEIVE = 1;  // メッセージ受信報告  ADK > Android (Receive)

	private UsbManager mUsbManager = null;
	private UsbAccessory mAccessory = null;
	private PendingIntent mPermissionIntent = null;
	private boolean mPermissionRequestPending = false;

	private Activity parent = null;
	private IAccessoryControl accessoryControl = null;

    private ParcelFileDescriptor mFileDescriptor = null;
    private FileInputStream mInputStream = null;
    private FileOutputStream mOutputStream = null;
		
	/**
	 *    コンストラクタ
	 * 
	 * @param parent 従属するActivity
	 */
	public UsbAccessoryManagement(Activity parent, IAccessoryControl accesoryControl)
	{
		/** 親を覚える **/
        this.parent = parent;

        /** アクセサリ制御クラスを覚える **/
        this.accessoryControl = accesoryControl;

        /** UsbManagerを取得する **/
        mUsbManager = (UsbManager) parent.getSystemService(Context.USB_SERVICE);
	}

	/**
	 *    ADK接続・切断の受信待ち
	 *      （onCreate() で呼ばれることを期待）
	 * 
	 */
	public void prepare()
	{
		mPermissionIntent = PendingIntent.getBroadcast(parent, 0, new Intent(GokigenSymbols.ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(GokigenSymbols.ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		parent.registerReceiver(mUsbReceiver, filter);
	}

	/**
     *   受信スレッド
     * 
     */
    public void run() 
    {
        int ret = 0;
        byte[] buffer = new byte[16384];
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
            Message m = Message.obtain(mHandler, MESSAGE_RECEIVE);
            m.obj = buffer;
            mHandler.sendMessage(m);
        }
        Log.d(TAG, "::: exit usb accessory receiving loop :::");
    }

    /**
     *    ADKへメッセージを送信する
     * 
     * @param command  送出コマンド
     * @param target        送信ID (0x00 ～ 0xff)
     * @param value         送信データ (0x00 ～ 0xff)
     */
    public void sendCommand(byte[] command)
    {
        if (mOutputStream == null)
        {
        	// メッセージ送信ができない状態...データを捨てて終了する
            Log.v(GokigenSymbols.APP_IDENTIFIER, "SEND STREAM IS NONE.");
        	return;
        }

        // スレッドを起こしてメッセージを送信する
        Thread thread = new Thread(new UsbSendCommandThread(mOutputStream, command));
        try
        {
            thread.start();
        }
        catch (Exception ex)
        {
             // これでどうかな...?
        }
    }

	/**
	 *    USBアクセサリのオープン
	 * 
	 * @param accessory
	 */
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
            Log.d(TAG, "usb accessory opened");
            accessoryControl.enableControls(true);
        }
        else
        {
            Log.d(TAG, "usb accessory open fail");
            accessoryControl.enableControls(false);
        }
    }

    /**
     * 
     * 
     */
    private void closeAccessory()
    {
        try
        {
        	accessoryControl.enableControls(false);
            if (mFileDescriptor != null)
            {
                mFileDescriptor.close();
            }
        }
        catch (Exception e)
        {
            //
        }
        finally
        {
            mFileDescriptor = null;
            mAccessory = null;
            accessoryControl.enableControls(false);
        }
    }

    /**
     * 
     * 
     * @param msg
     */
    protected void handleReceivedMessage(Message msg)
    {
        if (accessoryControl != null)
        {
        	accessoryControl.receivedMessage(msg);
        }
        else
        {
            Log.d(TAG, "accessoryControl is null.");
        }
    }
    
    /**
     *   ADKからデータを入力
     * 
     */
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            handleReceivedMessage(msg);
        }
    };

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
            if (GokigenSymbols.ACTION_USB_PERMISSION.equals(action))
            {
                synchronized (this)
                {
                	UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);  /** Android 3.1 **/
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
            	UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);  /** Android 3.1 **/
                if (accessory != null && accessory.equals(mAccessory))
                {
                    closeAccessory();
                }
            }
        }
    };
    
    /**
     *   ADK接続・切断の受信待ちの終了。
     *      （onDestroy() で呼ばれることを期待）
     * 
     */
    public void finish()
    {
        parent.unregisterReceiver(mUsbReceiver);    	
    }

    /**
     *    アクセサリの接続を許すかどうかの確認を行う。
     *    （onResume() で呼ばれることを期待)
     * 
     */
    public void CheckAccessoryPermission()
    {
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
            accessoryControl.enableControls(false);
        }
    }

    /**
     *    インタフェース
     * 
     * @author MRSa
     *
     */
    public interface IAccessoryControl
    {
        public abstract void enableControls(boolean isEnable);
        public abstract void receivedMessage(Message msg);    	
    }
}
