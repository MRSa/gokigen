package jp.osdn.gokigen.aira01a;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

/**
 *
 *
 */
public class CameraViewOnTouchClickListenerImpl implements View.OnClickListener, View.OnTouchListener {
    private final String TAG = this.toString();
    private LiveViewFragment parent = null;
    private CameraController cameraController = null;
    private TakePictureControl takePictureControl = null;

    /**
     * コンストラクタ
     */
    public CameraViewOnTouchClickListenerImpl(LiveViewFragment parent, CameraController controller, TakePictureControl takePicture)
    {
        this.parent = parent;
        this.cameraController = controller;
        this.takePictureControl = takePicture;
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
            case R.id.aeModeTextView:
                cameraController.changeAEModeValue();
                break;
            case R.id.whiteBalanceImageView:
                cameraController.changeWhiteBalance();
                break;
            case R.id.settingImageView:
                takePictureControl.abortTakingPictures();
                parent.transToSettingFragment();
                break;
            case R.id.unlockImageView:
                takePictureControl.unlockAutoFocus();
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
            case R.id.aelockImageView:
                // AE LOCK状態をトグルする
                cameraController.toggleAELockStatus();
                break;
            case R.id.focalLengthTextView:
                // 焦点距離情報を更新する
                parent.updateFocalLengthView();
                break;
            case R.id.manualFocusImageView:
                // AF/MF状態をトグルする
                cameraController.toggleManualFocusStatus();
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
                takePictureControl.startTakePicture(point);
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            takePictureControl.finishTakePicture();
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
            //takePictureControl.startTakePicture(TakePictureControl.AutoBracketingType.None);
            takePictureControl.startTakePicture();
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            takePictureControl.finishTakePicture();
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
