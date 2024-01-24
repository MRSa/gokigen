package jp.osdn.gokigen.aira01b.liveview;

/**
 *
 *
 */
public interface ILiveImageStatusNotify
{
    void toggleFocusAssist();
    void toggleShowGridFrame();

    IMessageDrawer getMessageDrawer();
}
