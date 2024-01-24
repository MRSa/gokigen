package jp.osdn.gokigen.aira01a.takepicture;

import android.graphics.PointF;

import jp.co.olympus.camerakit.OLYCamera;

/**
 *   カメラのAF制御と撮影の制御
 *
 *
 */
public class TakePictureControl
{
    private OLYCamera camera = null;
    private ITakePictureRequestedControl control = null;

    private MovieRecordingControl movieShot = null;
    private SequentialShotControl sequentialShot = null;
    private SingleShotControl singleShot = null;
    private AutoFocusControl autoFocus = null;
    private BracketingShotControl bracketing = null;

    /**
     *   コンストラクタ
     *
     */
    public TakePictureControl(OLYCamera camera, ITakePictureRequestedControl control)
    {
        this.camera = camera;
        this.control = control;
        movieShot = new MovieRecordingControl(camera, control);
        sequentialShot = new SequentialShotControl(camera, control);
        singleShot = new SingleShotControl(camera, control);
        autoFocus = new AutoFocusControl(camera, control);
        bracketing = new BracketingShotControl(camera, control);
    }

    /**
     *  写真撮影を開始する
     *
     */
    public void startTakePicture()
    {
        OLYCamera.ActionType actionType = camera.getActionType();
        if (actionType == OLYCamera.ActionType.Movie)
        {
            // 動画撮影モードの時には、撮影開始・撮影終了を指示する
            movieShot.movieControl();
            return;
        }

        if ((camera.isTakingPicture())||(camera.isRecordingVideo()))
        {
            // スチル or ムービー撮影中の時には、何もしない
            return;
        }

        if (actionType == OLYCamera.ActionType.Single)
        {
            if (control.getAutoBracketingSetting(false) == BracketingShotControl.BRACKET_NONE)
            {
                // 一枚とる
                singleShot.singleShot();
            }
            else
            {
                // オートブラケッティングの実行
                bracketing.startShootBracketing();
            }
        }
        else if (actionType == OLYCamera.ActionType.Sequential)
        {
            // 連続でとる
            sequentialShot.shotControl();
        }
    }

    /**
     *   撮影の終了
     *
     */
    public void finishTakePicture()
    {
        if (OLYCamera.ActionType.Sequential == camera.getActionType())
        {
            // 連続撮影モードの時のみ、撮影終了を指示する
            sequentialShot.shotControl();
        }
    }

    /**
     *
     *
     * @param point フォーカス（焦点を合わせる）ポイント
     */
    public void startTakePicture(PointF point)
    {
        OLYCamera.ActionType actionType = camera.getActionType();
        if (control.getTouchShutterStatus())
        {
            // Touch Shutter mode
            if (actionType == OLYCamera.ActionType.Single)
            {
                takePictureWithPoint(point);
            }
            else if (actionType == OLYCamera.ActionType.Sequential)
            {
                autoFocus.driveAutoFocus(point, false);
                sequentialShot.shotControl();
            }
            else if (actionType == OLYCamera.ActionType.Movie)
            {
                movieShot.movieControl();
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
     *   ポイント指定で撮影する
     *
     * @param point フォーカス（焦点を合わせる）ポイント
     */
    public void takePictureWithPoint(PointF point)
    {
        // LiveViewFragmentから直接呼ばれていた...タッチシャッターモードで顔検出した時の場合...
        if (autoFocus.driveAutoFocus(point, false))
        {
            if (control.getAutoBracketingSetting(false) == BracketingShotControl.BRACKET_NONE)
            {
                // AF成功時のみ一枚写真を撮影する
                singleShot.singleShot();
            }
            else
            {
                // オートブラケッティングの実行
                bracketing.startShootBracketing();
            }
        }
    }

    /**
     *   AF枠をクリアする
     *
     */
    public void resetAutoFocus()
    {
        try
        {
            camera.clearAutoFocusPoint();
            camera.unlockAutoFocus();
            control.setFocusFrameStatus(false);
        }
        catch (Exception ee)
        {
            //
            ee.printStackTrace();
        }
    }

    /**
     *   撮影を中断する
     *
     */
    public void abortTakingPictures()
    {
        if (camera.isTakingPicture())
        {
            // 撮影中の時には撮影を終わらせる
            sequentialShot.shotControl();
        }
        if (camera.isRecordingVideo())
        {
            // ムービー撮影中の場合は終了させる
            movieShot.movieControl();
        }
    }

    /**
     *   オートフォーカスをロックする
     *
     * @param point  ターゲットAF点
     */
    public void lockAutoFocus(PointF point)
    {
        autoFocus.driveAutoFocus(point, true);
    }

    /**
     *   AF-Lを解除する
     *
     */
    public void unlockAutoFocus()
    {
        autoFocus.unlockAutoFocus();
    }
}
