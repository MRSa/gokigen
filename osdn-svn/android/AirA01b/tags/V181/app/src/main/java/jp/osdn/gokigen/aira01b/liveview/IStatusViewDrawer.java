package  jp.osdn.gokigen.aira01b.liveview;

public interface IStatusViewDrawer
{
    void updateStatusView(String message);
    void updateFocusAssistStatus();
    void updateGridFrameStatus();
    void showFavoriteSettingDialog();

    void toggleTimerStatus();

    void toggleGpsTracking();
    void updateGpsTrackingStatus();

    void updateLiveViewMagnifyScale(final boolean isMaxLimit, final float scale);

    IMessageDrawer getMessageDrawer();
}
