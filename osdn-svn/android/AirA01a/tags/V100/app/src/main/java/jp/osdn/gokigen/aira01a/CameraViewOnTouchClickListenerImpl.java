package jp.osdn.gokigen.aira01a;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

/**
 *
 * Created by MRSa on 2016/04/29.
 */
public class CameraViewOnTouchClickListenerImpl implements View.OnClickListener, View.OnTouchListener {
    private final String TAG = this.toString();
    private LiveViewFragment parent = null;
    private CameraController cameraController = null;
    private CameraTakePicture cameraTakePicture = null;

    /**
     * コンストラクタ
     */
    public CameraViewOnTouchClickListenerImpl(LiveViewFragment parent, CameraController controller, CameraTakePicture takePicture)
    {
        this.parent = parent;
        this.cameraController = controller;
        this.cameraTakePicture = takePicture;
    }

    /**
     * オブジェクトがクリックされた時の処理(分岐)
     *
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.drivemodeImageView:
                cameraController.changeDriveMode();
                break;
            case R.id.takemodeTextView:
                cameraController.changeTakeMode();
                break;
            case R.id.shutterSpeedTextView:
                cameraController.changeShutterSpeed();
                break;
            case R.id.apertureValueTextView:
                cameraController.changeApertureValue();
                break;
            case R.id.exposureCompensationTextView:
                cameraController.changeExposureCompensation();
                break;
            case R.id.isoSensitivityTextView:
                cameraController.changeIsoSensitivity();
                break;
            case R.id.whiteBalaneImageView:
                cameraController.changeWhiteBalance();
                break;
            case R.id.settingImageView:
                cameraTakePicture.abortTakingPictures();
                parent.transToSettingFragment();
                break;
            case R.id.unlockImageView:
                cameraTakePicture.unlockAutoFocus();
                break;
            case R.id.showPlaybackImageView:
                if (cameraController.changeToPlaybackMode())
                {
                    parent.transToPlaybackFragment();
                }
                break;
            case R.id.zoomInImageView:
                if (!cameraController.isZooming())
                {
                    // ズーム動作中ではない場合、ズームインする
                    cameraController.driveZoomLens(+1);
                }
                break;
            case R.id.zoomOutImageView:
                if (!cameraController.isZooming())
                {
                    // ズーム動作中ではない場合、ズームアウトする
                    cameraController.driveZoomLens(-1);
                }
                break;

            default:
                //  do nothing!
                break;
        }
    }

    /**
     * オブジェクトがタッチされた時の処理(分岐)
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int id = v.getId();
        if (id == R.id.cameraLiveImageView)
        {
            processCameraLiveImageView(event);
            return (true);
        }
        else if (id == R.id.shutterImageView)
        {
            processShutterImageView(event);
            return (true);
        }
        return (false);
    }

    /**
     *
     * @param event
     */
    private void processCameraLiveImageView(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            PointF point = parent.getPointWithEvent(event);
            if (parent.isFocusPointArea(point))
            {
                cameraTakePicture.startTakePicture(point);
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            cameraTakePicture.finishTakePicture();
        }
    }

    /**
     *
     * @param event
     */
    private void processShutterImageView(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            cameraTakePicture.startTakePicture();
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            cameraTakePicture.finishTakePicture();
        }
    }

    // -------------------------------------------------------------------------
    // Camera actions
    // -------------------------------------------------------------------------

    //
    // Touch Shutter mode:
    //   - Tap a subject to focus and automatically release the shutter.
    //
    // Touch AF mode:
    //   - Tap to display a focus frame and focus on the subject in the selected area.
    //   - You can use the image view to choose the position of the focus frame.
    //   - Photographs can be taken by tapping the shutter button.
    //
}
