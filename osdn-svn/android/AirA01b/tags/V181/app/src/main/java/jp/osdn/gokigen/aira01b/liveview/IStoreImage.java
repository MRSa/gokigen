package jp.osdn.gokigen.aira01b.liveview;

import android.graphics.Bitmap;
import android.location.Location;

interface IStoreImage
{
    void doStore(final Bitmap target, final Location location);
}
