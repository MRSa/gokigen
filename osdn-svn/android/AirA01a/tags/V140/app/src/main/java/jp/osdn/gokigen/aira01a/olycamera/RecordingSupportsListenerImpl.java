package jp.osdn.gokigen.aira01a.olycamera;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraRecordingSupportsListener;
import jp.osdn.gokigen.aira01a.liveview.IStatusViewDrawer;
import jp.osdn.gokigen.aira01a.preview.RecordedImageDrawer;

/**
 *   OLYCameraRecordingSupportsListener の実装
 *   (RecordedImageDrawer用)
 *
 * Created by MRSa on 2016/04/29.
 */
public class RecordingSupportsListenerImpl implements OLYCameraRecordingSupportsListener
{
    private final String TAG = this.toString();
    private RecordedImageDrawer imageDrawer = null;
    private IStatusViewDrawer statusDrawer = null;

    /**
     *   コンストラクタ
     *
     * @param drawer
     */
    public RecordingSupportsListenerImpl(RecordedImageDrawer drawer, IStatusViewDrawer statusDrawer)
    {
        this.imageDrawer = drawer;
        this.statusDrawer = statusDrawer;
    }

    /**
     *
     * @param camera
     */
    @Override
    public void onReadyToReceiveCapturedImagePreview(OLYCamera camera)
    {

    }

    /**
     *
     *
     * @param camera
     * @param data
     * @param metadata
     */
    @Override
    public void onReceiveCapturedImagePreview(OLYCamera camera, byte[] data, Map<String, Object> metadata)
    {
        if (imageDrawer != null)
        {
            imageDrawer.onReceiveCapturedImagePreview(camera, data, metadata);
        }
    }

    /**
     *
     * @param camera
     * @param e
     */
    @Override
    public void onFailToReceiveCapturedImage(OLYCamera camera, Exception e)
    {

    }

    /**
     *
     * @param camera
     */
    @Override
    public void onReadyToReceiveCapturedImage(OLYCamera camera)
    {

    }

    /**
     *
     * @param camera
     * @param data
     * @param metadata
     */
    @Override
    public void onReceiveCapturedImage(OLYCamera camera, byte[] data, Map<String, Object> metadata)
    {

    }

    /**
     *
     *
     * @param camera
     * @param e
     */
    @Override
    public void onFailToReceiveCapturedImagePreview(OLYCamera camera, Exception e)
    {

    }

    /**
     *   ズーム操作が止まった
     *
     * @param camera
     */
    @Override
    public void onStopDrivingZoomLens(OLYCamera camera)
    {
        if (statusDrawer != null)
        {
            statusDrawer.updateFocalLengthView();
        }
    }


}
