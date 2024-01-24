package jp.osdn.gokigen.aira01a;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraRecordingSupportsListener;

/**
 *
 *
 *
 */
public class CameraRecordingSupportsListenerImpl implements OLYCameraRecordingSupportsListener
{
    private final String TAG = this.toString();
    private LiveViewFragment parent = null;
    /**
     *   コンストラクタ
     *
     */
    public CameraRecordingSupportsListenerImpl(LiveViewFragment parent)
    {
        this.parent = parent;
    }


    @Override
    public void onReadyToReceiveCapturedImagePreview(OLYCamera olyCamera)
    {

    }

    @Override
    public void onReceiveCapturedImagePreview(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map)
    {
        if (olyCamera.getActionType() == OLYCamera.ActionType.Single)
        {
            CapturedDataViewFragment fragment = new CapturedDataViewFragment();
            fragment.prepareImageToShow(olyCamera, bytes, map);
            FragmentTransaction transaction = parent.getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(parent.getId(), fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onFailToReceiveCapturedImagePreview(OLYCamera olyCamera, Exception e)
    {

    }

    @Override
    public void onReadyToReceiveCapturedImage(OLYCamera olyCamera)
    {

    }

    @Override
    public void onReceiveCapturedImage(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map)
    {

    }

    @Override
    public void onFailToReceiveCapturedImage(OLYCamera olyCamera, Exception e)
    {

    }

    @Override
    public void onStopDrivingZoomLens(OLYCamera olyCamera)
    {
        parent.getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                parent.updateFocalLengthView();
            }
        });
    }
}
