package jp.osdn.gokigen.aira01a;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;

/**
 *   撮影後に撮影結果を表示するフラグメント
 *   (Olympusのサンプルコード、RecviewFragment から改名した。 描画ロジックは、RecordedImageDrawer に切り離した。)
 *
 *   ※ prepareImageToShow() と onCreate() と onCreateView() の呼び出し順番に注意。
 *
 */
public class CapturedDataViewFragment extends Fragment implements View.OnTouchListener
{
    private final String TAG = this.toString();
    private RecordedImageDrawer drawer = null;
    private ImageView imageView = null;

    /**
     *   作成された時
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");
    }

    /**
     *   Viewを作成する時
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return 作成したview
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_recview, container, false);

        view.findViewById(R.id.view1).setOnTouchListener(this);
        view.findViewById(R.id.view2).setOnTouchListener(this);
        view.findViewById(R.id.view3).setOnTouchListener(this);
        imageView = (ImageView)view.findViewById(R.id.imageView1);
        imageView.setOnTouchListener(this);
        if (drawer == null)
        {
            drawer = new RecordedImageDrawer(imageView);
        }
        else
        {
            drawer.setTargetView(imageView);
        }
        return (view);
    }

    /**
     *   表に現れた時...
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
        drawer.startDrawing();
    }

    /**
     *    裏に隠れた時...
     *
     */
    @Override
    public void onPause()
    {
        super.onPause();
        drawer.stopDrawing();
    }

    /**
     *   viewにタッチされたことを検出 ... 呼び出し元へ戻る
     *   (View.OnTouchListener の実装)
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            getFragmentManager().popBackStack();
            return (true);
        }
        return (false);
    }

    /**
     *   表示する撮影結果画像を(カメラクラスと共に)受け取る
     *
     * @param camera
     * @param data
     * @param metadata
     */
    public void prepareImageToShow(OLYCamera camera, byte[] data, Map<String, Object> metadata)
    {
        Log.v(TAG, "prepareImageToShow()");
        if (drawer == null)
        {
            if (imageView == null)
            {
                Log.v(TAG, "prepareImageToShow() : imageView is NULL.");
            }
            drawer = new RecordedImageDrawer(imageView);
        }
        drawer.setImageArea(camera, getActivity());
        drawer.setImageData(data, metadata);
    }
}
