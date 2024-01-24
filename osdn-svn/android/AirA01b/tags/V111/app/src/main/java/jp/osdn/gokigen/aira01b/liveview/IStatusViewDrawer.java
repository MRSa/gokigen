package  jp.osdn.gokigen.aira01b.liveview;

public interface IStatusViewDrawer
{
    void updateStatusView(String message);
    void updateFocusAssistStatus();
    void updateGridFrameStatus();
}
