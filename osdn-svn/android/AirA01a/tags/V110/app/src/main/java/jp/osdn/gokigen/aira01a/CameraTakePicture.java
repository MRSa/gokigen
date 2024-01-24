package jp.osdn.gokigen.aira01a;

import android.app.Activity;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.HashMap;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraAutoFocusResult;
import jp.co.olympus.camerakit.OLYCameraKitException;

/**
 *   カメラのAF制御と撮影の制御
 *
 *
 */
public class CameraTakePicture
{
    private OLYCamera camera = null;
    private ITakePictureRequestedControl control = null;

    /**
     *   コンストラクタ
     *
     */
    public CameraTakePicture(OLYCamera camera, ITakePictureRequestedControl control)
    {
        this.camera = camera;
        this.control = control;
    }

    /**
     *
     *
     */
    public void startTakePicture()
    {
        if (camera.isTakingPicture())
        {
            // スチル撮影中なら、何もしない
            return;
        }
        OLYCamera.ActionType actionType = getActionType();
        if (actionType == OLYCamera.ActionType.Movie)
        {
            // ムービー撮影中の場合...
            movieControl();
            return;
        }
        if (camera.isRecordingVideo())
        {
            // ムービー撮影中の時には、何もしない
            return;
        }
        if (actionType == OLYCamera.ActionType.Single)
        {
            // 一枚とる
            camera.takePicture(new HashMap<String, Object>(), new takePictureControl(true, true, R.string.shutter_control_take_failed));
        }
        else if (actionType == OLYCamera.ActionType.Sequential)
        {
            // 連続でとる
            camera.startTakingPicture(null, new takePictureControl(false, true, R.string.shutter_control_take_failed));
        }
    }

    /**
     *
     *
     */
    private void movieControl()
    {
        if (camera.isTakingPicture())
        {
            // スチル撮影中なら、何もしない
            return;
        }
        if (!camera.isRecordingVideo())
        {
            // ムービー撮影の開始
            camera.startRecordingVideo(new HashMap<String, Object>(), new finishMovieControl(true));
        }
        else
        {
            // ムービー撮影の終了
            camera.stopRecordingVideo(new finishMovieControl(false));
        }
    }

    /**
     *
     *
     */
    public void finishTakePicture()
    {
        OLYCamera.ActionType actionType = getActionType();
        if (actionType == OLYCamera.ActionType.Sequential)
        {
            if (camera.isTakingPicture())
            {
                // 撮影中の時には撮影を終わらせる
                camera.stopTakingPicture(new takePictureControl(true, false, R.string.shutter_control_take_failed));
            }
        }
    }

    /**
     *
     *
     * @param point
     */
    public void startTakePicture(PointF point)
    {
        OLYCamera.ActionType actionType = getActionType();
        if (control.getTouchShutterStatus())
        {
            // Touch Shutter mode
            if (actionType == OLYCamera.ActionType.Single)
            {
                takePictureWithPoint(point);
            }
            else if (actionType == OLYCamera.ActionType.Sequential)
            {
                startTakingPictureWithPoint(point);
            }
            else if (actionType == OLYCamera.ActionType.Movie)
            {
                movieControl();
            }
        }
        else
        {
            // Touch AF mode
            if (actionType == OLYCamera.ActionType.Single || actionType == OLYCamera.ActionType.Sequential)
            {
                lockAutoFocus(point);
            }
        }
    }

    /**
     *
     *
     * @param point
     */
    public void takePictureWithPoint(PointF point)
    {
        if (prepareTakePictureWithPoint(point))
        {
            // AF成功時のみ写真を撮影する
            camera.takePicture(new HashMap<String, Object>(), new takePictureControl(true, true, R.string.shutter_control_take_failed));
        }
    }

    /**
     *
     * @param point
     */
    public void startTakingPictureWithPoint(PointF point)
    {
        prepareTakePictureWithPoint(point);
        camera.startTakingPicture(null, new takePictureControl(false, true, R.string.shutter_control_take_failed));
    }

    /**
     *
     *
     * @param point
     */
    private boolean prepareTakePictureWithPoint(PointF point)
    {
        if (camera.isTakingPicture() || camera.isRecordingVideo())
        {
            return (false);
        }

        // Display a provisional focus frame at the touched point.
        final RectF preFocusFrameRect;
        {
            float focusWidth = 0.125f;  // 0.125 is rough estimate.
            float focusHeight = 0.125f;
            float imageWidth = control.getIntrinsicContentSizeWidth();
            float imageHeight = control.getIntrinsicContentSizeHeight();
            if (imageWidth > imageHeight) {
                focusHeight *= (imageWidth / imageHeight);
            } else {
                focusHeight *= (imageHeight / imageWidth);
            }
            preFocusFrameRect = new RectF(point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f,
                    point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f);
        }
        control.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Running);

        // Set auto-focus point.
        try
        {
            camera.setAutoFocusPoint(point);
        }
        catch (OLYCameraKitException e)
        {
            e.printStackTrace();
            // Lock failed.
            try
            {
                camera.unlockAutoFocus();
            }
            catch (OLYCameraKitException ee)
            {
                //
            }
            control.setFocusFrameStatus(false);
            control.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Failed, 1.0);
            return (false);
        }
        return (true);
    }

    /**
     *
     *
     * @return  アクションタイプ
     */
    public OLYCamera.ActionType getActionType()
    {
        return (camera.getActionType());
    }

    /**
     *
     *
     */
    public void resetAutoFocus()
    {
        try {
            camera.clearAutoFocusPoint();
            camera.unlockAutoFocus();
        }
        catch (OLYCameraKitException ee)
        {
            //
        }
        control.setFocusFrameStatus(false);
    }
    /**
     *
     *
     */
    public void abortTakingPictures()
    {
        if (camera.isTakingPicture())
        {
            // 撮影中の時には撮影を終わらせる
            camera.stopTakingPicture(new takePictureControl(true, false, R.string.shutter_control_take_failed));
        }
        if (camera.isRecordingVideo())
        {
            // ムービー撮影の終了
            camera.stopRecordingVideo(new finishMovieControl(false));
        }
    }

    // focus control
    public void lockAutoFocus(PointF point)
    {
        if (camera.isTakingPicture() || camera.isRecordingVideo())
        {
            return;
        }

        // Display a provisional focus frame at the touched point.
        final RectF preFocusFrameRect;
        {
            float focusWidth = 0.125f;  // 0.125 is rough estimate.
            float focusHeight = 0.125f;
            float imageWidth = control.getIntrinsicContentSizeWidth();
            float imageHeight = control.getIntrinsicContentSizeHeight();
            if (imageWidth > imageHeight) {
                focusHeight *= (imageWidth / imageHeight);
            } else {
                focusHeight *= (imageHeight / imageWidth);
            }
            preFocusFrameRect = new RectF(point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f,
                    point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f);
        }
        control.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Running);

        // Set auto-focus point.
        try {
            camera.setAutoFocusPoint(point);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            // Lock failed.
            try {
                camera.clearAutoFocusPoint();
                camera.unlockAutoFocus();
            } catch (OLYCameraKitException ee) {
            }
            control.setFocusFrameStatus(false);
            control.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Failed, 1.0);
            return;
        }

        // Lock auto-focus.
        camera.lockAutoFocus(new finishFocusControl(preFocusFrameRect));

    }

    /**
     *
     *
     */
    public void unlockAutoFocus()
    {
        if (camera.isTakingPicture() || camera.isRecordingVideo())
        {
            return;
        }

        // Unlock auto-focus.
        try
        {
            camera.unlockAutoFocus();
            camera.clearAutoFocusPoint();
        }
        catch (OLYCameraKitException e)
        {
            e.printStackTrace();
        }
        control.setFocusFrameStatus(false);
        control.hideFocusFrame();
    }

    /**
     *
     *
     * @param action
     */
    private void runOnUiThread(Runnable action)
    {
        Activity activity = control.getActivity();
        if (activity == null)
        {
            return;
        }
        activity.runOnUiThread(action);
    }

    /**
     *   ムービーの制御終了処理
     *
     */
    private class finishMovieControl implements OLYCamera.CompletedCallback
    {
        //private ITakePictureRequestedControl control = null;
        private boolean isShutterImageSelected = false;

        /**
         *   コンストラクタ
         *
         */
        public finishMovieControl(boolean isShutterImage)
        {
            isShutterImageSelected = isShutterImage;
        }

        /**
         *   ムービー終了
         *
         */
        @Override
        public void onCompleted()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    control.setShutterImageSelected(isShutterImageSelected);
                }
            });
        }

        /**
         *   エラー発生
         *
         * @param e
         */
        @Override
        public void onErrorOccurred(OLYCameraKitException e)
        {
            final String message = e.getMessage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    control.presentMessage(R.string.shutter_control_record_failed, message);
                }
            });
        }
    }

    /**
     *
     *
     */
    private class finishFocusControl implements OLYCamera.TakePictureCallback
    {
        private RectF preFocusFrameRect = null;
        public finishFocusControl(final RectF preFocusFrameRect)
        {
            this.preFocusFrameRect = preFocusFrameRect;
        }
        @Override
        public void onProgress(OLYCamera camera, OLYCamera.TakingProgress progress, OLYCameraAutoFocusResult autoFocusResult) {
            if (progress == OLYCamera.TakingProgress.EndFocusing) {
                if (autoFocusResult.getResult().equals("ok") && autoFocusResult.getRect() != null) {
                    // Lock succeed.
                    control.setFocusFrameStatus(true);

                    //focusedSoundPlayer.start();
                    RectF postFocusFrameRect = autoFocusResult.getRect();
                    control.showFocusFrame(postFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Focused);

                } else if (autoFocusResult.getResult().equals("none")) {
                    // Could not lock.
                    try {
                        camera.clearAutoFocusPoint();
                        camera.unlockAutoFocus();
                    } catch (OLYCameraKitException ee) {
                    }
                    control.setFocusFrameStatus(false);
                    control.hideFocusFrame();
                } else {
                    // Lock failed.
                    try {
                        camera.clearAutoFocusPoint();
                        camera.unlockAutoFocus();
                    } catch (OLYCameraKitException ee) {
                    }
                    control.setFocusFrameStatus(false);
                    control.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Failed, 1.0);
                }
            }
        }

        @Override
        public void onCompleted() {
            // No operation.
        }

        /**
         *   エラー発生時
         *
         * @param e
         */
        @Override
        public void onErrorOccurred(Exception e) {
            // Lock failed.
            try {
                camera.clearAutoFocusPoint();
                camera.unlockAutoFocus();
            } catch (OLYCameraKitException ee) {
            }
            control.setFocusFrameStatus(false);
            control.hideFocusFrame();

            final String message = e.getMessage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    control.presentMessage(R.string.shutter_control_af_failed, message);
                }
            });
        }
    }

    /**
     *
     *
     */
    private class takePictureControl implements OLYCamera.TakePictureCallback
    {
        private boolean isCompletedAction = false;
        private boolean isProgressAction = false;
        private int failureMessageResId = 0;

        /**
         *   コンストラクタ
         * @param isCompletedAction    撮影処理終了時に後処理をするか？
         * @param failureMessageResId  撮影失敗時の表示メッセージタイトル（ResId）
         */
        public takePictureControl(boolean isCompletedAction, boolean isProgressAction, int failureMessageResId)
        {
            this.isCompletedAction = isCompletedAction;
            this.isProgressAction = isProgressAction;
            this.failureMessageResId = failureMessageResId;
        }

        /**
         *   動作時
         *
         * @param olyCamera
         * @param takingProgress
         * @param olyCameraAutoFocusResult
         */
        @Override
        public void onProgress(OLYCamera olyCamera, OLYCamera.TakingProgress takingProgress, OLYCameraAutoFocusResult olyCameraAutoFocusResult)
        {
            if (!isProgressAction)
            {
                // do nothing!
                return;
            }
            if (takingProgress == OLYCamera.TakingProgress.EndFocusing)
            {
                if (!control.getFocusFrameStatus())
                {
                    String result = olyCameraAutoFocusResult.getResult();
                    if (result.equals("ok"))
                    {
                        RectF postFocusFrameRect = olyCameraAutoFocusResult.getRect();
                        if (postFocusFrameRect == null)
                        {
                            // フォーカスが合っているはずなのにフォーカスフレームがない異常...
                            return;
                        }
                        control.showFocusFrame(postFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Focused);
                    }
                    else if (result.equals("none"))
                    {
                        control.setFocusFrameStatus(false);
                        control.hideFocusFrame();
                    }
                    else
                    {
                        control.setFocusFrameStatus(false);
                        control.hideFocusFrame();
                    }
                }
            }
            //else if (takingProgress == OLYCamera.TakingProgress.BeginCapturing)
            //{
            //    shutterSoundPlayer.start();
            //}
        }

        /**
         *   処理終了時
         *
         */
        @Override
        public void onCompleted()
        {
            if (!isCompletedAction)
            {
                // do nothing!
                return;
            }

            if (!control.getFocusFrameStatus())
            {
                try
                {
                    camera.clearAutoFocusPoint();
                }
                catch (OLYCameraKitException ee)
                {
                    //
                }
                control.setFocusFrameStatus(false);
                control.hideFocusFrame();
            }
        }

        /**
         *   エラー発生時の処理
         *
         * @param e
         */
        @Override
        public void onErrorOccurred(Exception e)
        {
            if (!control.getFocusFrameStatus())
            {
                try
                {
                    camera.clearAutoFocusPoint();
                }
                catch (OLYCameraKitException ee)
                {

                }
                control.setFocusFrameStatus(false);
                control.hideFocusFrame();
            }
            final String message = e.getMessage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    control.presentMessage(failureMessageResId, message);
                }
            });
        }
    }
}
