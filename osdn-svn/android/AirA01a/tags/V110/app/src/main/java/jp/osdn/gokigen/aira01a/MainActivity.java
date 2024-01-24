package jp.osdn.gokigen.aira01a;

import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import jp.co.olympus.camerakit.OLYCamera;

/**
 *
 *
 */
public class MainActivity extends AppCompatActivity implements CameraCoordinator.ICameraCallback
{
    private final String TAG = this.toString();
    private CameraCoordinator coordinator = null;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 画面全体レイアウトの設定
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if (bar != null)
        {
            bar.hide();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        coordinator = new CameraCoordinator(this, this);
        boolean prepare = coordinator.prepare();

        // ConnectingFragmentを表示する
        changeViewToConnectingFragment();
    }

    /**
     *
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        coordinator.connect();
    }

    /**
     *
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        coordinator.disconnect();
    }

    /**
     *
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();
    }

    /**
     *
     *
     */
    @Override
    public void onStop()
    {
        super.onStop();
    }

    /**
     * 　 カメラとの接続を解除する
     *
     * @param powerOff 真ならカメラの電源オフを伴う
     */
    public void disconnectWithPowerOff(final boolean powerOff)
    {
        if (coordinator != null)
        {
            coordinator.disconnect(powerOff);
        }
    }

    /**
     *   カメラとの接続が確立した時 ... LiveViewFragmentに切り替える
     *   (CameraCoordinator.ICameraCallback の実装)
     *
     * @param myCamera
     */
    public void onCameraConnected(OLYCamera myCamera)
    {
        if ((coordinator != null)&&(coordinator.isConnect()))
        {
            changeViewToLiveViewFragment(myCamera);
        }
    }

    /**
     *    カメラとの接続が切れた時 ... ConnectingFragmentに切り替える
     *   (CameraCoordinator.ICameraCallback の実装)
     *
     */
    public void onCameraDisconnected()
    {
        if ((coordinator != null)&&(coordinator.isConnect()))
        {
            changeViewToConnectingFragment();
        }
    }

    /**
     *    カメラとの接続エラーが発生した時 ... ConnectingFragmentに切り替える
     *   (CameraCoordinator.ICameraCallback の実装)
     *
     * @param message
     * @param e
     */
    public void onCameraOccursException(String message, Exception e)
    {
        alertConnectingFailed(message, e);
        onCameraDisconnected();
    }

    /**
     *   接続リトライのダイアログを出す
     *
     * @param message
     * @param e
     */
    private void alertConnectingFailed(String message, Exception e)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Connect failed")
                .setMessage(e.getMessage() != null ? "<" + message + "> " + e.getMessage() : message + " : Unknown error")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        coordinator.retryConnect();
                    }
                });
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                builder.show();
            }
        });
    }

    /**
     *   ConnectingFragmentに表示を切り替える実処理
     */
    private void changeViewToConnectingFragment()
    {
        ConnectingFragment fragment = new ConnectingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment1, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     *   LiveViewFragmentに表示を切り替える実処理
     */
    private void changeViewToLiveViewFragment(OLYCamera myCamera)
    {
        LiveViewFragment fragment = new LiveViewFragment();
        fragment.setCamera(myCamera);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment1, fragment);
        transaction.commitAllowingStateLoss();
    }
}
