package jp.osdn.gokigen.aira01a;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import jp.co.olympus.camerakit.OLYCamera;
import jp.osdn.gokigen.aira01a.connection.CameraConnectCoordinator;
import jp.osdn.gokigen.aira01a.connection.ConnectingFragment;
import jp.osdn.gokigen.aira01a.liveview.LiveViewFragment;

/**
 *
 *
 */
public class MainActivity extends AppCompatActivity implements CameraConnectCoordinator.ICameraCallback
{
    private final String TAG = this.toString();
    private final int REQUEST_NEED_PERMISSIONS = 1010;
    private CameraConnectCoordinator coordinator = null;

    /**
     *
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
            // タイトルバーは表示しない
            bar.hide();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        coordinator = new CameraConnectCoordinator(this, this);

        // 外部メモリアクセス権のオプトイン
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.INTERNET,
                    },
                    REQUEST_NEED_PERMISSIONS);
        }

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
     */
    public void onCameraOccursException(String message, Exception e)
    {
        alertConnectingFailed(message, e);
        onCameraDisconnected();
    }

    /**
     *   致命的なエラーが発生した時... メッセージを表示し、アプリケーションを終了させる
     *
     * @param message ユーザに知らせるメッセージ
     */
    @Override
    public void onCameraConnectFatalError(String message)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_fatal_error))
                .setMessage(message)
                .setPositiveButton(getString(R.string.button_exit_application), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // アプリケーションを終了させる
                        finish();
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
     *   接続リトライのダイアログを出す
     *
     */
    private void alertConnectingFailed(String message, Exception e)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_connect_failed))
                .setMessage(e.getMessage() != null ? "<" + message + "> " + e.getMessage() : message + " : Unknown error")
                .setPositiveButton(getString(R.string.button_retry), new DialogInterface.OnClickListener() {
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
        coordinator.prepare(fragment);

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
