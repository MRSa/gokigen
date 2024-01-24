package jp.sourceforge.gokigen.psbf;

import android.content.res.Resources;
import android.view.View;

public abstract class IAccessoryController
{
    protected PSBFBaseActivity mHostActivity = null;

    public IAccessoryController(PSBFBaseActivity activity)
    {
        mHostActivity = activity;
    }

    protected View findViewById(int id)
    {
        return mHostActivity.findViewById(id);
    }

    protected Resources getResources()
    {
        return mHostActivity.getResources();
    }

    protected void accessoryAttached()
    {
        onAccesssoryAttached();
    }

    abstract protected void onAccesssoryAttached();
}
