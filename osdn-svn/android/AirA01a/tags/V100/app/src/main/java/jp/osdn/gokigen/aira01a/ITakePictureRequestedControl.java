package jp.osdn.gokigen.aira01a;

import android.app.Activity;
import android.graphics.RectF;

/**
 * Created by MRSa on 2016/05/24.
 */
public interface ITakePictureRequestedControl
{
    boolean getTouchShutterStatus();
    void setShutterImageSelected(boolean isSelected);

    boolean getFocusFrameStatus();
    void setFocusFrameStatus(boolean isShow);
    void hideFocusFrame();
    void showFocusFrame(RectF rect, CameraLiveImageView.FocusFrameStatus status);
    void showFocusFrame(RectF rect, CameraLiveImageView.FocusFrameStatus status, double duration);


    float getIntrinsicContentSizeWidth();
    float getIntrinsicContentSizeHeight();

    void presentMessage(int resId, String message);

    Activity getActivity();
}
