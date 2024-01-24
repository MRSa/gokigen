package jp.osdn.gokigen.aira01b;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import java.util.List;

import jp.osdn.gokigen.aira01b.liveview.LiveViewFragment;
import jp.osdn.gokigen.aira01b.olycameraproperty.OlyCameraPropertyListFragment;
import jp.osdn.gokigen.aira01b.olycamerawrapper.ICameraStatusReceiver;
import jp.osdn.gokigen.aira01b.olycamerawrapper.IOLYCameraObjectProvider;
import jp.osdn.gokigen.aira01b.olycamerawrapper.IOlyCameraCoordinator;
import jp.osdn.gokigen.aira01b.olycamerawrapper.OlyCameraCoordinator;
import jp.osdn.gokigen.aira01b.playback.ImageGridViewFragment;
import jp.osdn.gokigen.aira01b.preference.ICameraPropertyAccessor;
import jp.osdn.gokigen.aira01b.preference.PreferenceFragment;

/**
 *
 *
 */
public class MainActivity extends AppCompatActivity implements ICameraStatusReceiver, IChangeScene
{
    /** OpenCV **/
    static
    {
        System.loadLibrary("opencv_java3");
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private final String TAG = this.toString();
    private IOlyCameraCoordinator olyCameraCoordinator = null;
    private IOLYCameraObjectProvider olyCameraObjectProvider = null;
    private MyInterfaceProvider interfaceFactory = null;

    /**
     *
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

        interfaceFactory = new MyInterfaceProvider(this, this, this);
        OlyCameraCoordinator coordinator = new OlyCameraCoordinator(this, interfaceFactory);
        olyCameraCoordinator = coordinator;
        olyCameraObjectProvider = coordinator;
        interfaceFactory.setPropertyProvider(olyCameraCoordinator.getCameraPropertyProvider());

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
        olyCameraCoordinator.startWatchWifiStatus(this);

        Log.d(TAG, "OpenCV library found inside package. Using it!");
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    /**
     *
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        olyCameraCoordinator.stopWatchWifiStatus(this);
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


    @Override
    public void onStatusNotify(final String message)
    {
        Log.v(TAG, "onStatusNotify() : " + message);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments)
        {
            if ((f != null)&&(f.getClass().toString().contains("ConnectingFragment")))
            {
                final ConnectingFragment target = (ConnectingFragment) f;
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                         target.setInformationText(message);
                    }
                });
                return;
            }
        }
    }

    @Override
    public void onCameraConnected()
    {
        if ((olyCameraCoordinator != null)&&(olyCameraCoordinator.isWatchWifiStatus()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    changeViewToLiveViewFragment();
                }
            });
        }
    }

    /**
     *    カメラとの接続が切れた時 ... ConnectingFragmentに切り替える
     *   (CameraCoordinator.ICameraCallback の実装)
     *
     */
    @Override
    public void onCameraDisconnected()
    {
        if ((olyCameraCoordinator != null)&&(olyCameraCoordinator.isWatchWifiStatus()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    changeViewToConnectingFragment();
                }
            });
        }
    }

    /**
     *    カメラとの接続エラーが発生した時 ... ConnectingFragmentに切り替える
     *   (CameraCoordinator.ICameraCallback の実装)
     *
     * @param message メッセージ
     * @param e  例外
     */
    public void onCameraOccursException(String message, Exception e)
    {
        alertConnectingFailed(message, e);
        onCameraDisconnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == ICameraPropertyAccessor.CHOICE_SPLASHSCREEN)&&(resultCode == RESULT_OK)&&(data != null))
        {
            Uri selectedImage = data.getData();
            Log.v(TAG, "Image File : " + selectedImage.toString());
            //setSplashScreenImageFile(selectedImage);
        }
    }

    /**
     *
     *
     */
    // private void setSplashScreenImageFile(Uri imageUri) { }

    /**
     *   接続リトライのダイアログを出す
     *
     * @param message 表示用の追加メッセージ
     * @param e 例外
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
                        olyCameraCoordinator.connect();
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
    private void changeViewToLiveViewFragment()
    {
        LiveViewFragment fragment = new LiveViewFragment();
        fragment.setInterfaces(olyCameraCoordinator, interfaceFactory);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment1, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     *   OlyCameraPropertyListFragmentに表示を切り替える実処理
     */
    private void changeViewToOlyCameraPropertyListFragment()
    {
        OlyCameraPropertyListFragment fragment = new OlyCameraPropertyListFragment();
        fragment.setInterface(this, interfaceFactory.getPropertyProvider());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment1, fragment);
        // backstackに追加
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     *   PreferenceFragmentに表示を切り替える実処理
     */
    private void changeViewToPreferenceFragment()
    {
        PreferenceFragment fragment = new PreferenceFragment();
        fragment.setInterface(this, interfaceFactory, olyCameraCoordinator.getChangeRunModeExecutor());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment1, fragment);
        // backstackに追加
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     *   ImageGridViewFragmentに表示を切り替える実処理
     *
     */
    private void changeViewToImageGridViewFragment()
    {
        ImageGridViewFragment fragment = new ImageGridViewFragment();
        fragment.setCamera(olyCameraObjectProvider.getOLYCamera());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment1, fragment);
        // backstackに追加
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void changeSceneToCameraPropertyList()
    {
        changeViewToOlyCameraPropertyListFragment();
    }

    @Override
    public void changeSceneToConfiguration()
    {
        changeViewToPreferenceFragment();
    }

    @Override
    public void changeSceneToPlaybackCamera()
    {
        changeViewToImageGridViewFragment();
    }

    @Override
    public void changeSceneToPlaybackPhone()
    {
        // 起動時画面の選択...
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, ICameraPropertyAccessor.CHOICE_SPLASHSCREEN);
    }

    @Override
    public void exitApplication()
    {
        // カメラの電源をOFFにしたうえで、アプリケーションを終了する。
        olyCameraCoordinator.disconnect(true);
        finish();
    }
}