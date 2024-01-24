package jp.osdn.gokigen.aira01b.liveview.phonecamera;

public interface IPhoneCameraShutter
{
    void onPressedPhoneShutter();
    void onTouchedPreviewArea();
    void onSavedPicture(boolean isSuccess);
}
