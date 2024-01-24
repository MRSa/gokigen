package jp.sourceforge.gokigen.android.sample;

import android.location.Location;

public interface ILocationReceiver
{
    /**  ロケーション検出 **/
    public abstract void onLocationChanged(Location location);
}
