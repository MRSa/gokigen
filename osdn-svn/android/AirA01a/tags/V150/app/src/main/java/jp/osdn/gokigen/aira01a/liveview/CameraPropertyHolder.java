package jp.osdn.gokigen.aira01a.liveview;

import android.util.Log;
import android.view.View;

import java.util.List;

import jp.osdn.gokigen.aira01a.takepicture.CameraController;

/**
 *
 *
 */
public class CameraPropertyHolder
{
    private final String TAG = this.toString();
    private CameraController cameraController = null;
    private String propertyName = null;
    private List<String> valueList = null;
    private View targetView = null;

    public CameraPropertyHolder(String name, View view, CameraController controller)
    {
        this.propertyName = name;
        this.targetView = view;
        this.cameraController = controller;
    }

    public void prepare()
    {
        valueList = null;
        valueList = cameraController.getPropertyList(propertyName);
        if (valueList == null || valueList.size() == 0)
        {
            // write warning log
            Log.w(TAG, "WARN: getPropertyList() returns empty. : " + propertyName);
            return;
        }
    }

    public boolean canSetCameraProperty()
    {
        return (cameraController.canSetCameraProperty(propertyName));
    }

    public String getPropertyName()
    {
        return (propertyName);
    }

    public String getCameraPropertyValue()
    {
        return (cameraController.getCameraPropertyValue(propertyName));
    }

    public String getCameraPropertyValueTitle()
    {
        return (cameraController.getCameraPropertyValueTitle(propertyName));
    }

    public List<String> getValueList()
    {
        return (valueList);
    }

    public View getTargetView()
    {
        return (targetView);
    }

}
