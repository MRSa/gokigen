package jp.osdn.gokigen.aira01b.manipulate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import jp.osdn.gokigen.aira01b.R;

import static jp.osdn.gokigen.aira01b.manipulate.IImageManipulatorOperator.MANIPULATE_IMAGE_NONE;

/**
 *
 *
 */
class EffectImageProcessor implements View.OnTouchListener, IManipulateImageOperation
{
    private static final String TAG = EffectImageProcessor.class.getSimpleName();

    private static final int TARGET_WIDTH = 640;
    private static final int TARGET_HEIGHT = 480;

    private static final String JPEG_SUFFIX = ".jpg";

    private final Activity parent;
    private final IManipulateImageHolder imageHolder;
    private final IImageManipulatorOperator imageOperator;

    private int lastManipulateOperation = MANIPULATE_IMAGE_NONE;
    private String lastSavedFilePath = null;

    private float pipX = 0.015f;
    private float pipY = 0.015f;
    //private float pipS = 0.3f;

    /**
     *   コンストラクタ
     *
     */
    EffectImageProcessor(Activity activity, IManipulateImageHolder imageHolder, IImageManipulatorOperator imageOperator)
    {
        this.parent = activity;
        this.imageHolder = imageHolder;
        this.imageOperator = imageOperator;
    }

    /**
     *
     *
     */
    @Override
    public void selectEffectType(final IManipulateImageCallback callback)
    {
        // リスト表示用のアラートダイアログ
        AlertDialog.Builder listDialog = new AlertDialog.Builder(parent);
        listDialog.setTitle(parent.getString(R.string.dialog_title_select_image_effect));
        listDialog.setItems(parent.getResources().getStringArray(R.array.array_select_effect_process),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int which)
                    {
                        parent.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                selectEffectImageProcess(which, callback);
                            }
                        });
                    }
                });
        listDialog.create().show();
    }

    /**
     *   画像の保存
     *
     */
    @Override
    public void selectedSaveImage()
    {
        // 保存処理(プログレスダイアログ（「保存中...」）を表示して処理する)
        final ProgressDialog saveDialog = new ProgressDialog(parent);
        saveDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        saveDialog.setMessage(parent.getString(R.string.data_saving));
        saveDialog.setIndeterminate(true);
        saveDialog.setCancelable(false);
        saveDialog.show();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                System.gc();
                saveEffectImageImpl(lastManipulateOperation);
                System.gc();
                saveDialog.dismiss();
            }
        });
        try
        {
            thread.start();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.gc();
        }
    }

    /**
     *   画像の加工処理呼び出し（プレビュー）
     *
     * @param which 選択したアイテム
     */
    private void selectEffectImageProcess(final int which, final IManipulateImageCallback callback)
    {
        // リスト選択時の処理(プログレスダイアログ（「ロード中...」）を表示して処理する)
        final ProgressDialog loadingDialog = new ProgressDialog(parent);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(parent.getString(R.string.data_loading));
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        ImageView targetImageView = imageHolder.getImageTargetImageView();
        targetImageView.setImageResource(android.R.color.transparent); // 表示をクリア
        Thread thread = new Thread(new Runnable() {
            public void run()
            {
                if ((which != IImageManipulatorOperator.PICTURE_IN_PICTURE_S)&&(which != IImageManipulatorOperator.PICTURE_IN_PICTURE_L))
                {
                    // PinP以外は消す...
                    imageOperator.clearPictureInPicture();
                }
                selectEffectImageProcessImpl(which, callback);
                System.gc();
                loadingDialog.dismiss();
            }
        });
        try
        {
            thread.start();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.gc();
        }
    }

    private void selectEffectImageProcessImpl(final int which, final IManipulateImageCallback callback)
    {
        try
        {
            Bitmap bitmap = getEffectImageBitmap(which);
            System.gc();
            if (bitmap != null)
            {
                //  大きさを確認する
                double width = bitmap.getWidth();
                double height = bitmap.getHeight();
                if (width > height)
                {
                    // 幅優位...
                    if (width > TARGET_WIDTH)
                    {
                        // 縮尺を決める
                        double rate = TARGET_WIDTH / width;
                        height = height * rate;
                        width = TARGET_WIDTH;
                    }
                }
                else // if (height > width)
                {
                    // 高さ優位...
                    if (height > TARGET_HEIGHT)
                    {
                        double rate = TARGET_HEIGHT / height;
                        width = width * rate;
                        height = TARGET_HEIGHT;
                    }
                }
                Log.v(TAG, "createScaledBitmap : (" + bitmap.getWidth() + "," + bitmap.getHeight() + ") to ("+ (int) width + "," + (int) height + ")");

                final ImageView targetImageView = imageHolder.getImageTargetImageView();
                final Bitmap targetBitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                bitmap.recycle();
                lastManipulateOperation = which;

                parent.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        targetImageView.setImageBitmap(targetBitmap);
                        callback.manipulateImageResult(which, true);
                        System.gc();
                    }
                });
            }
            else
            {
                parent.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(parent, parent.getString(R.string.effect_process_failure), Toast.LENGTH_SHORT).show();
                        callback.manipulateImageResult(which, false);
                    }
                });
            }
        }
        catch (Exception e)
        {
            lastManipulateOperation = MANIPULATE_IMAGE_NONE;
            e.printStackTrace();
            parent.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(parent, parent.getString(R.string.effect_process_failure), Toast.LENGTH_SHORT).show();
                    callback.manipulateImageResult(which, false);
                }
            });
        }
    }

    private boolean checkBothImageFile()
    {
        String fileName1 = imageHolder.getSourceImage1();
        String fileName2 = imageHolder.getSourceImage2();
        if ((fileName1 == null)||(fileName2 == null))
        {
            parent.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(parent, parent.getString(R.string.warning_please_select_both_image), Toast.LENGTH_SHORT).show();
                }
            });
            return (false);
        }
        return (true);
    }

    /**
     *   ビットマップの保存
     *
     * @param which 画像イメージの選択肢
     */
    private void saveEffectImageImpl(int which)
    {
        Bitmap bitmap = getEffectImageBitmap(which);
        if (bitmap == null)
        {
            parent.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(parent, parent.getString(R.string.effect_process_failure), Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        try
        {
            Calendar calendar = Calendar.getInstance();
            final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + parent.getString(R.string.app_name) + "/";
            String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime()) + JPEG_SUFFIX;
            String filepath = new File(directoryPath.toLowerCase(), filename).getPath();

            final File directory = new File(directoryPath);
            if (!directory.exists())
            {
                if (!directory.mkdirs())
                {
                    Log.v(TAG, "MKDIR FAIL. : " + directoryPath);
                }
            }
            FileOutputStream outputStream = new FileOutputStream(filepath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            long now = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            ContentResolver resolver = parent.getContentResolver();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, filepath);
            values.put(MediaStore.Images.Media.DATE_ADDED, now);
            values.put(MediaStore.Images.Media.DATE_TAKEN, now);
            values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            lastSavedFilePath = filepath;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            lastSavedFilePath = null;
            parent.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(parent, parent.getString(R.string.effected_save_image_failure), Toast.LENGTH_SHORT).show();
                }
            });
        }
        bitmap.recycle();

        // 保存後、PinP位置をリセットする
        pipX = 0.015f;
        pipY = 0.015f;

        System.gc();
        parent.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(parent, parent.getString(R.string.effected_save_image_success), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *   加工後のBitmapイメージを取得する
     *
     * @param which 加工処理コマンド
     * @return Bitmapイメージ
     */
    private Bitmap getEffectImageBitmap(int which)
    {
        Bitmap bitmap = null;
        switch (which)
        {
            case IImageManipulatorOperator.COMBINE_LEFT_RIGHT:
                if (checkBothImageFile())
                {
                    bitmap = imageOperator.processCombineLeftRight(imageHolder.getSourceImage1(), imageHolder.getSourceImage2(), false);
                }
                break;

            case IImageManipulatorOperator.COMBINE_UP_DOWN:
                if (checkBothImageFile())
                {
                    bitmap = imageOperator.processCombineUpDown(imageHolder.getSourceImage1(), imageHolder.getSourceImage2(), false);
                }
                break;

            case IImageManipulatorOperator.COMBINE_LEFT_RIGHT_RESIZED:
                if (checkBothImageFile())
                {
                    bitmap = imageOperator.processCombineLeftRight(imageHolder.getSourceImage1(), imageHolder.getSourceImage2(), true);
                }
                break;

            case IImageManipulatorOperator.COMBINE_UP_DOWN_RESIZED:
                if (checkBothImageFile())
                {
                    bitmap = imageOperator.processCombineUpDown(imageHolder.getSourceImage1(), imageHolder.getSourceImage2(), true);
                }
                break;

            case IImageManipulatorOperator.PICTURE_IN_PICTURE_L:
                if (checkBothImageFile())
                {
                    bitmap = imageOperator.processPictureInPicture(imageHolder.getSourceImage1(), imageHolder.getSourceImage2(), 0.6f, new PointF(pipX, pipY));
                }
                break;
            case IImageManipulatorOperator.PICTURE_IN_PICTURE_S:
                if (checkBothImageFile())
                {
                    bitmap = imageOperator.processPictureInPicture(imageHolder.getSourceImage1(), imageHolder.getSourceImage2(), 0.3f, new PointF(pipX, pipY));
                }
                break;
            default:
                Log.v(TAG, "SELECTED : " + which);
                break;
        }
        return (bitmap);
    }

    /**
     *
     *
     */
    @Override
    public void sharedSaveImage()
    {
        if (lastSavedFilePath == null)
        {
            parent.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(parent, parent.getString(R.string.image_save_first), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(parent);

        // 選択ダイアログのタイトル
        builder.setChooserTitle(R.string.share_chooser_title);

        // タイトル
        builder.setSubject(parent.getString(R.string.app_name));

        // 画像の設定
        builder.setStream(Uri.parse(lastSavedFilePath));

        // 送るデータのタイプ
        builder.setType("image/jpeg");

        Log.v(TAG, "filePath:" + lastSavedFilePath);

        // Shareアプリ一覧のDialogの表示
        builder.startChooser();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (v.getId() != R.id.targetImageView)
        {
            return (false);
        }
        if (event.getPointerCount() > 1)
        {
            Log.v(TAG, "DETECT Multi-Touch : " + event.getPointerCount());
            // マルチタッチの場合...
            //return (scaleGestureDetector.onTouchEvent(event));
        }
        if (((event.getAction() == MotionEvent.ACTION_UP)||(event.getAction() == MotionEvent.ACTION_MOVE))&&
                ((lastManipulateOperation == IImageManipulatorOperator.PICTURE_IN_PICTURE_L)||(lastManipulateOperation == IImageManipulatorOperator.PICTURE_IN_PICTURE_S)))
        {
            // PictureInPictureの時、指が離された または タッチを移動した場合、中の画像を移動させる
            Bitmap bmp = ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap();
            int width = bmp.getWidth();
            int height = bmp.getHeight();

            // 画像内の位置 (0～1換算)
            pipX = event.getX() / v.getWidth();
            pipY = event.getY() / v.getHeight();

            // ビットマップイメージの更新...ただし大きさは変えない
            ((ImageView) v).setImageBitmap(Bitmap.createScaledBitmap(imageOperator.updatePictureInPicture(1.0f, new PointF(pipX, pipY)), width, height, true));
            Log.v(TAG, "onTouch Up (new): <" + pipX + "," + pipY + "> (" + v.getWidth() + "," + v.getHeight() + ") [" + width + "," + height + "]");
        }
        return (true);
    }

/*
    @Override
    public boolean onScale(ScaleGestureDetector detector)
    {
        // 中に表示する画像の大きさを変える
        pipS = pipS * detector.getScaleFactor();
        Log.d(TAG, "onScale : " + detector.getScaleFactor() + " SCALE : " + pipS);

        ImageView view = imageHolder.getImageTargetImageView();
        Bitmap bmp = ((BitmapDrawable) view.getDrawable()).getBitmap();
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        // ビットマップイメージの更新...場所は変えない
        bmp = imageOperator.updatePictureInPicture(detector.getScaleFactor(), new PointF(pipX, pipY));
        view.setImageBitmap(Bitmap.createScaledBitmap(bmp, width, height, true));
        bmp.recycle();
        return (true);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector)
    {
        Log.d(TAG, "onScaleBegin : "+ detector.getScaleFactor());
        return (true);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector)
    {
        Log.d(TAG, "onScaleEnd : "+ detector.getScaleFactor());
    }
*/
}
