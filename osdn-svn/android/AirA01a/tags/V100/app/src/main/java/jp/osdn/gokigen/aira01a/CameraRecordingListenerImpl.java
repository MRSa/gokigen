package jp.osdn.gokigen.aira01a;

import android.app.Activity;
import android.widget.ImageView;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraAutoFocusResult;
import jp.co.olympus.camerakit.OLYCameraRecordingListener;

/**
 * Created by MRSa on 2016/04/29.
 */
public class CameraRecordingListenerImpl implements OLYCameraRecordingListener
{
    private final String TAG = this.toString();
    private LiveViewFragment parent = null;
    private ImageView imageView = null;

    /**
     *   コンストラクタ
     *
     */
    public CameraRecordingListenerImpl(LiveViewFragment parent)
    {
        this.parent = parent;
    }

    @Override
    public void onStartRecordingVideo(OLYCamera olyCamera)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imageView != null) {
                    imageView.setSelected(true);
                }
            }
        });
    }

    /**
     *   更新するImageViewを拾う
     *
     * @param target
     */
    public void setImageView(ImageView target)
    {
        this.imageView = target;
    }

    @Override
    public void onStopRecordingVideo(OLYCamera olyCamera)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imageView != null) {
                    imageView.setSelected(false);
                }
            }
        });
    }

    @Override
    public void onChangeAutoFocusResult(OLYCamera olyCamera, OLYCameraAutoFocusResult olyCameraAutoFocusResult)
    {
        // do nothing!
    }

    private void runOnUiThread(Runnable action)
    {
        Activity activity = parent.getActivity();
        if (activity == null)
        {
            return;
        }
        activity.runOnUiThread(action);
    }
}
