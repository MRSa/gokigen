package jp.sourceforge.gokigen.diary;

import android.location.Location;

public interface ILocationReceiver
{
    /**  ���P�[�V�������o **/
    public abstract void onLocationChanged(Location location);
}
