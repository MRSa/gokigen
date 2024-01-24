package jp.osdn.gokigen.aira01a.liveview;

import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraLiveViewListener;

/**
 *  OLYCameraLiveViewListener の実装
 *  （LiveViewFragment用）
 *
 */
public class CameraLiveViewListenerImpl implements OLYCameraLiveViewListener
{
    private final String TAG = this.toString();
    private IImageDataReceiver imageView = null;

    /**
     * コンストラクタ
     */
    public CameraLiveViewListenerImpl()
    {
        //
    }

    /**
     * 更新するImageViewを拾う
     *
     * @param target
     */
    public void setCameraLiveImageView(IImageDataReceiver target)
    {
        this.imageView = target;
    }

    /**
     * LiveViewの画像データを更新する
     *
     * @param camera
     * @param data
     * @param metadata
     */
    @Override
    public void onUpdateLiveView(OLYCamera camera, byte[] data, Map<String, Object> metadata)
    {
        if (imageView != null)
        {
            imageView.setImageData(data, metadata);
        }
    }

    /**
     * 　 CameraLiveImageView
     */
    public interface IImageDataReceiver
    {
        public abstract void setImageData(byte[] data, Map<String, Object> metadata);
    }
}
