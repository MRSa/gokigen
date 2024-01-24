package jp.osdn.gokigen.aira01a.playback;

import jp.co.olympus.camerakit.OLYCameraFileInfo;

class OLYCameraContentInfoEx
{
    private final OLYCameraFileInfo fileInfo;
    private boolean hasRaw;
    OLYCameraContentInfoEx(OLYCameraFileInfo fileInfo, boolean hasRaw)
    {
        this.fileInfo = fileInfo;
        this.hasRaw = hasRaw;
    }

    void setHasRaw(boolean value)
    {
        hasRaw = value;
    }
    OLYCameraFileInfo getFileInfo()
    {
        return (fileInfo);
    }

    boolean hasRaw()
    {
        return (hasRaw);
    }
}
