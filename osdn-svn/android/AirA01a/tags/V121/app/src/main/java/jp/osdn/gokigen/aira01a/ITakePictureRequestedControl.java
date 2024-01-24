package jp.osdn.gokigen.aira01a;

import android.app.Activity;
import android.graphics.RectF;

/**
 * Created by MRSa on 2016/05/24.
 */
public interface ITakePictureRequestedControl
{
    boolean getShootingBusyStatus();

    boolean getTouchShutterStatus();

    boolean getFocusFrameStatus();
    void setFocusFrameStatus(boolean isShow);

    void hideFocusFrame(boolean isUiThread);

    void showFocusFrame(RectF rect, CameraLiveImageView.FocusFrameStatus status);
    void showFocusFrame(RectF rect, CameraLiveImageView.FocusFrameStatus status, double duration);

    float getIntrinsicContentSizeWidth();
    float getIntrinsicContentSizeHeight();

    int getAutoBracketingSetting(boolean isCount);

    // 以下の３つは、UIスレッドで実行すること！
    void showOtherInformation(String message);
    void setShutterImageSelected(final boolean isSelected);
    void presentMessage(final int resId, final String message);
}
