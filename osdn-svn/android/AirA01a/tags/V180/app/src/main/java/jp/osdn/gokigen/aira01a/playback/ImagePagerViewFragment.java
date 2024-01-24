package jp.osdn.gokigen.aira01a.playback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraFileInfo;
import jp.co.olympus.camerakit.OLYCamera.ProgressEvent;
import jp.osdn.gokigen.aira01a.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;


public class ImagePagerViewFragment extends Fragment
{
    private final String TAG = this.toString();
    //private final float IMAGE_RESIZE_FOR_GET_INFORMATION = 640.0f;
    private final String JPEG_SUFFIX = ".jpg";
    private final String RAW_SUFFIX = ".orf";

	private OLYCamera camera = null;
	private List<OLYCameraContentInfoEx> contentList = null;
	private int contentIndex = 0;

	private LayoutInflater layoutInflater = null;
	private ViewPager viewPager = null;
	private LruCache<String, Bitmap> imageCache =null;

	private MyImageDownloader imageDownloader = null;
	private MyMovieDownloader movieDownloader = null;

	public void setCamera(OLYCamera camera) {
		this.camera = camera;
	}
	
	void setContentList(List<OLYCameraContentInfoEx> contentList)
	{
		this.contentList = contentList;
	}
	
	void setContentIndex(int contentIndex) {
		this.contentIndex = contentIndex;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		imageCache = new LruCache<>(9);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		layoutInflater = inflater;
		View view = layoutInflater.inflate(R.layout.fragment_image_pager_view, container, false);
		viewPager = view.findViewById(R.id.viewPager1);
		viewPager.setAdapter(new ImagePagerAdapter());
		viewPager.addOnPageChangeListener(new ImagePageChangeListener());
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		try
		{
			OLYCameraContentInfoEx info  = contentList.get(contentIndex);
			OLYCameraFileInfo file = info.getFileInfo();
			String path = file.getDirectoryPath() + "/" + file.getFilename();

			AppCompatActivity activity = (AppCompatActivity) getActivity();
			if (activity != null)
			{
				ActionBar bar = activity.getSupportActionBar();
				if (bar != null) {
					bar.show();
					bar.setTitle(path);
				}
			}

			String lowerCasePath = path.toLowerCase();
			if (lowerCasePath.endsWith(JPEG_SUFFIX))
            {
                if (info.hasRaw())
                {
                    inflater.inflate(R.menu.image_view_with_raw, menu);
                    MenuItem downloadMenuItem = menu.findItem(R.id.action_download_with_raw);
                    downloadMenuItem.setEnabled(true);
                }
                else
                {
                    inflater.inflate(R.menu.image_view, menu);
                    MenuItem downloadMenuItem = menu.findItem(R.id.action_download);
                    downloadMenuItem.setEnabled(true);
                }
			}
            else
            {
				inflater.inflate(R.menu.movie_view, menu);
				MenuItem downloadMenuItem = menu.findItem(R.id.action_download_movie);
				downloadMenuItem.setEnabled(true);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean doDownload = false;
        boolean getInformation = false;
		float downloadSize = 0;
        String specialSuffix = null;
        if ((item.getItemId() == R.id.action_get_information)||(item.getItemId() == R.id.action_get_information_raw))
        {
            downloadSize = OLYCamera.IMAGE_RESIZE_1024;
            getInformation = true;
            doDownload = true;
        }
        else if ((item.getItemId() == R.id.action_download_original_size)||(item.getItemId() == R.id.action_download_original_size_raw))
        {
			downloadSize = OLYCamera.IMAGE_RESIZE_NONE;
			doDownload = true;
		}
        else if ((item.getItemId() == R.id.action_download_2048x1536)||(item.getItemId() == R.id.action_download_2048x1536_raw))
        {
			downloadSize = OLYCamera.IMAGE_RESIZE_2048;
			doDownload = true;
		}
        else if ((item.getItemId() == R.id.action_download_1920x1440)||(item.getItemId() == R.id.action_download_1920x1440_raw))
        {
			downloadSize = OLYCamera.IMAGE_RESIZE_1920;
			doDownload = true;
		}
        else if ((item.getItemId() == R.id.action_download_1600x1200)||(item.getItemId() == R.id.action_download_1600x1200_raw))
        {
			downloadSize = OLYCamera.IMAGE_RESIZE_1600;
			doDownload = true;
		}
        else if ((item.getItemId() == R.id.action_download_1024x768)||(item.getItemId() == R.id.action_download_1024x768_raw))
        {
			downloadSize = OLYCamera.IMAGE_RESIZE_1024;
			doDownload = true;
		}
        else if (item.getItemId() == R.id.action_download_original_movie)
        {
            downloadSize = OLYCamera.IMAGE_RESIZE_NONE;
            doDownload = true;
        }
        else if (item.getItemId() == R.id.action_download_raw)
        {
            doDownload = true;
            downloadSize = OLYCamera.IMAGE_RESIZE_NONE;
            specialSuffix = RAW_SUFFIX;
		}

		if (doDownload)
		{
			try
			{
				OLYCameraFileInfo file = (contentList.get(contentIndex)).getFileInfo();
				String path = file.getDirectoryPath() + "/" + file.getFilename();
				String lowerCasePath = path.toLowerCase();
				String suffix = (specialSuffix == null) ? lowerCasePath.substring(lowerCasePath.lastIndexOf(".")) : specialSuffix;
				Calendar calendar = Calendar.getInstance();
                String targetFileName = file.getFilename();
                String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime())  + "_" + targetFileName.substring(0, targetFileName.indexOf(".")) + suffix;

				//  ダイアログを表示して保存する
				saveImageWithDialog(filename.toLowerCase(), downloadSize, getInformation);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume()
    {
		super.onResume();
		try
        {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null)
            {
                ActionBar bar = activity.getSupportActionBar();
                if (bar != null)
                {
                    bar.setDisplayShowHomeEnabled(true);
                    bar.show();
                }
            }
            viewPager.setCurrentItem(contentIndex);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}

	@Override
	public void onPause()
	{
		super.onPause();
		try
        {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null)
            {
                ActionBar bar = activity.getSupportActionBar();
                if (bar != null)
                {
                    bar.hide();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG, "onActivityResult() : start");

        //// とりあえず受信する

		Log.v(TAG, "onActivityResult() : end");
	}

	private class ImagePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return contentList.size();
		}

		@Override
		public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
        {
			return view.equals(object);
		}
		
		@Override
		public @NonNull Object instantiateItem(@NonNull ViewGroup container, int position)
        {
			ImageView view = (ImageView)layoutInflater.inflate(R.layout.view_image_page, container, false);
			container.addView(view);
			downloadImage(position, view);
			return view;
		}
		
		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
		{
			container.removeView((ImageView)object);
		}
	}

	private class ImagePageChangeListener implements ViewPager.OnPageChangeListener
	{

		@Override
		public void onPageScrollStateChanged(int state)
		{

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{

		}

		@Override
		public void onPageSelected(int position)
		{
			contentIndex = position;
			try
			{
                OLYCameraFileInfo file = (contentList.get(contentIndex)).getFileInfo();
                String path = file.getDirectoryPath() + "/" + file.getFilename();

                AppCompatActivity activity = (AppCompatActivity)getActivity();
                if (activity != null)
                {
                    ActionBar bar = activity.getSupportActionBar();
                    if (bar != null)
                    {
                        bar.setTitle(path);
                    }
                    activity.getFragmentManager().invalidateOptionsMenu();
                }
			}
			catch (Exception e)
            {
                e.printStackTrace();
            }
		}

	}

	private void downloadImage(int position, final ImageView view)
    {
        try
        {
            OLYCameraFileInfo file = (contentList.get(position)).getFileInfo();
            final String path = file.getDirectoryPath() + "/" + file.getFilename();

            // Get the cached image.
            Bitmap bitmap = imageCache.get(path);
            if (bitmap != null) {
                if (view != null && viewPager.indexOfChild(view) > -1) {
                    view.setImageBitmap(bitmap);
                }
                return;
            }

            // Download the image.
            camera.downloadContentScreennail(path, new OLYCamera.DownloadImageCallback() {
                @Override
                public void onProgress(ProgressEvent e) {
                    // MARK: Do not use to cancel a downloading by progress handler.
                    //       A communication error may occur by the downloading of the next image when
                    //       you cancel the downloading of the image by a progress handler in
                    //       the current version.
                }

                @Override
                public void onCompleted(final byte[] data, final Map<String, Object> metadata) {
                    // Cache the downloaded image.

                    final Bitmap bitmap = createRotatedBitmap(data, metadata);
                    try
                    {
						if (bitmap == null)
                        {
                            // OOM対策...
                            System.gc();
                            return;
                        }
                        if (imageCache != null)
                        {
                            imageCache.put(path, bitmap);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ((bitmap != null) && (view != null) && (viewPager.indexOfChild(view) > -1)) {
                                view.setImageBitmap(bitmap);
                            }
                        }
                    });
                }

                @Override
                public void onErrorOccurred(Exception e)
                {
                    final String message = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            presentMessage("Load failed", message);
                        }
                    });
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}

	/**
	 *   デバイスに画像ファイルをダウンロード（保存）する
	 *
	 * @param filename       ファイル名（カメラ内の）
	 * @param downloadSize   ダウンロードサイズ
     * @param isGetInformationMode 情報取得モードか？
     */
	private void saveImageWithDialog(@NonNull final String filename, float downloadSize, boolean isGetInformationMode)
	{
		if (filename.endsWith(JPEG_SUFFIX))
		{
			// 静止画の取得
			imageDownloader = new MyImageDownloader(filename, downloadSize, isGetInformationMode);
			imageDownloader.startDownload();
		}
		else
		{
			// 動画・RAWファイルの取得
            movieDownloader = new MyMovieDownloader(filename);
            movieDownloader.startDownload();
		}
	}


	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------
	
	private void presentMessage(String title, String message)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title).setMessage(message);
		builder.show();
	}
	
	private void runOnUiThread(Runnable action)
    {
		if (getActivity() == null)
        {
			return;
		}
		getActivity().runOnUiThread(action);
	}
	
	
	private Bitmap createRotatedBitmap(byte[] data, Map<String, Object> metadata)
	{
		Bitmap bitmap;
		try
		{
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			bitmap = null;
			System.gc();
		}
		if (bitmap == null)
		{
			return (null);
		}

		// ビットマップの回転を行う
		int degrees = getRotationDegrees(data, metadata);
		if (degrees != 0)
		{
			Matrix m = new Matrix();
			m.postRotate(degrees);
			try
			{
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				bitmap = null;
				System.gc();
			}
		}
		return (bitmap);
	}
	
	private int getRotationDegrees(byte[] data, Map<String, Object> metadata)
	{
		int degrees;
		int orientation = ExifInterface.ORIENTATION_UNDEFINED;

		try
        {
            if (metadata != null && metadata.containsKey("Orientation"))
            {
                String orientationStr = (String) metadata.get("Orientation");
                if (orientationStr != null)
                {
                    orientation = Integer.parseInt(orientationStr);
                }
            } else {
                // Gets image orientation to display a picture.
                File tempFile = File.createTempFile("temp", null);
                {
                    FileOutputStream outStream = new FileOutputStream(tempFile.getAbsolutePath());
                    outStream.write(data);
                    outStream.close();
                }
                ExifInterface exifInterface = new ExifInterface(tempFile.getAbsolutePath());
                orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                if (!tempFile.delete())
                {
                    Log.v(TAG, "tempFile.delete()");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        switch (orientation)
		{
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                degrees = 0;
                break;
		}
		return degrees;
	}

    /**
     *   静止画のダウンロード（とEXIF情報の取得）
     *
     */
	private class MyImageDownloader implements OLYCamera.DownloadImageCallback
	{
        private boolean isGetInformation;
		private ProgressDialog downloadDialog;
		private String filename;
		private float downloadImageSize;

		/**
		 *   コンストラクタ
		 *
		 * @param filename  ファイル名
		 * @param downloadSize  ダウンロードのサイズ
         * @param isGetInformation  情報を取得するだけかどうか（trueなら情報を取得するだけ)
         */
		MyImageDownloader(final String filename, float downloadSize, boolean isGetInformation)
		{
			this.filename = filename;
			this.downloadImageSize = downloadSize;
            this.downloadDialog = null;
            this.isGetInformation = isGetInformation;
		}

		/**
		 *   静止画のダウンロード開始指示
		 *
		 */
		void startDownload()
		{
			Log.v(TAG, "startDownload() " + filename);
			downloadDialog = new ProgressDialog(getContext());
            if (isGetInformation)
            {
                downloadDialog.setTitle(getString(R.string.dialog_get_information_title));
            }
            else
            {
                downloadDialog.setTitle(getString(R.string.dialog_download_title));
            }
			downloadDialog.setMessage(getString(R.string.dialog_download_message) + " " + filename);
			downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			downloadDialog.setCancelable(false);
			downloadDialog.show();

			// Download the image.
            try
            {
                OLYCameraFileInfo file = (contentList.get(contentIndex)).getFileInfo();
                String path = file.getDirectoryPath() + "/" + file.getFilename();
                camera.downloadImage(path, downloadImageSize, this);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
		}

		/**
		 *   進行中の表示 (進捗バーの更新)
		 *
		 * @param progressEvent 進捗情報
         */
		@Override
		public void onProgress(ProgressEvent progressEvent)
		{
			//
			if (downloadDialog != null)
			{
				int percent = (int)(progressEvent.getProgress() * 100.0f);
				downloadDialog.setProgress(percent);
				//downloadDialog.setCancelable(progressEvent.isCancellable()); // キャンセルできるようにしないほうが良さそうなので
			}
		}

		/**
		 *   ファイル受信終了時の処理
		 *
		 * @param bytes  受信バイト数
         * @param map    ファイルの情報
         */
		@Override
		public void onCompleted(byte[] bytes, Map<String, Object> map)
		{
            if (isGetInformation)
            {
                // Exif情報をダイアログ表示して終わる
                showExifInformation(bytes);
                System.gc();
                return;
            }

			final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + getString(R.string.app_name2) + "/";
			String filepath = new File(directoryPath.toLowerCase(), filename).getPath();

			// ファイルを保存する
			try
			{
				final File directory = new File(directoryPath);
				if (!directory.exists())
				{
					if (!directory.mkdirs())
                    {
                        Log.v(TAG, "directory.mkdirs()");
                    }
				}
				FileOutputStream outputStream = new FileOutputStream(filepath);
				outputStream.write(bytes);
				outputStream.close();
			}
			catch (Exception e)
			{
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (downloadDialog != null)
						{
							downloadDialog.dismiss();
						}
                        presentMessage(getString(R.string.download_control_save_failed), message);
                    }
                });
                // ダウンロード失敗時には、ギャラリーにデータ登録を行わない。
                return;
            }

            boolean hasGps = false;
            float[] latLong = new float[2];
            try
            {
                //
                ExifInterface exif = new ExifInterface(filepath);
                hasGps = exif.getLatLong(latLong);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

			// ギャラリーに受信したファイルを登録する
			try
			{
				long now = System.currentTimeMillis();
				ContentValues values = new ContentValues();
				values.put(Images.Media.MIME_TYPE, "image/jpeg");
				values.put(Images.Media.DATA, filepath);
				values.put(Images.Media.DATE_ADDED, now);
				values.put(Images.Media.DATE_TAKEN, now);
                if ((hasGps)&&(latLong.length >= 2))
                {
                    values.put(MediaStore.Images.Media.LATITUDE, latLong[0]);
                    values.put(MediaStore.Images.Media.LONGITUDE, latLong[1]);
                }
				values.put(Images.Media.DATE_MODIFIED, now);
				values.put(Images.Media.ORIENTATION, getRotationDegrees(bytes, map));

                final Activity activity = getActivity();
                ContentResolver resolver = null;
                if (activity != null)
                {
                    resolver = activity.getContentResolver();
                }
                if (resolver != null)
                {
                    final Uri insertedImage = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadDialog != null) {
                                downloadDialog.dismiss();
                            }
                            try {
                                Snackbar.make(getActivity().findViewById(R.id.fragment1), getString(R.string.download_control_save_success) + " " + filename, Snackbar.LENGTH_SHORT).show();
                                //Toast.makeText(getActivity(), getString(R.string.download_control_save_success) + " " + filename, Toast.LENGTH_SHORT).show();
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                                if (preferences.getBoolean("share_after_receive", false)) {
                                    shareContent(insertedImage);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
			} catch (Exception e) {
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (downloadDialog != null)
						{
							downloadDialog.dismiss();
						}
						presentMessage(getString(R.string.download_control_save_failed), message);
					}
				});
			}
			System.gc();
		}

        /**
         *   エラー発生時の処理
         *
         * @param e エラーの情報
         */
		@Override
		public void onErrorOccurred(Exception e)
		{
			final String message = e.getMessage();
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (downloadDialog != null)
					{
						downloadDialog.dismiss();
					}
                    if (isGetInformation)
                    {
                        presentMessage(getString(R.string.download_control_get_information_failed), message);
                    }
                    else
                    {
                        presentMessage(getString(R.string.download_control_download_failed), message);
                    }
				}
			});
		}

		/**
		 *   共有の呼び出し
		 *
		 * @param pictureUri  画像ファイル名
		 */
		private void shareContent(final Uri pictureUri)
		{
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			try {
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				intent.setType("image/jpeg");
				intent.putExtra(Intent.EXTRA_STREAM, pictureUri);
				Activity activity = getActivity();
				if (activity != null)
				{
                    activity.startActivityForResult(intent, 0);
                }
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Log.v(TAG, " URI : " + pictureUri);
			}
		}

        /**
         *   EXIF情報の表示 (ExifInterface を作って、表示クラスに渡す)
         *
         * @param bytes 画像情報
         */
        private void showExifInformation(byte[] bytes)
        {
            ExifInterface exif = null;
            try
            {
                Calendar calendar = Calendar.getInstance();
                String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime());
                File tempFile = File.createTempFile(filename, null);
                {
                    FileOutputStream outStream = new FileOutputStream(tempFile.getAbsolutePath());
                    outStream.write(bytes);
                    outStream.close();
                }
                exif = new ExifInterface(tempFile.getAbsolutePath());
                if (!tempFile.delete())
                {
                    Log.v(TAG, "tempFile.delete()");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            runOnUiThread(new ExifInfoToShow(exif));
        }

        /**
         *   EXIF情報を表示する処理
         *   （クラス生成時に、表示情報を作り出す）
         */
        private class ExifInfoToShow implements Runnable
        {
            private String message;

            /**
             *   コンストラクタ
             * @param information メッセージ
             */
            ExifInfoToShow(ExifInterface information)
            {
                this.message = formMessage(information);
            }

            /**
             *   Exif情報を表示に適した形式に変更し、情報を表示する
             * @param exifInterface Exif情報
             * @return 表示に適したExif情報
             */
            private String formMessage(ExifInterface exifInterface)
            {
                String msg = "";
                if (exifInterface != null)
                {
                    // 撮影時刻
                    msg = msg + getString(R.string.exif_datetime_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_DATETIME) + "\r\n"; //(string)
                    //msg = msg + "\r\n";

                    // 焦点距離
                    double focalLength = exifInterface.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0.0f);
                    msg = msg + getString(R.string.exif_focal_length_title);
                    msg = msg + " " + String.valueOf(focalLength) + "mm ";
                    msg = msg + "(" + getString(R.string.exif_focal_35mm_equiv_title) + " " + String.valueOf(focalLength * 2.0d) + "mm)" + "\r\n";
                    msg = msg + "\r\n";

                    // カスタムイメージプロセッシング利用の有無
                    //if (exifInterface.getAttributeInt(ExifInterface.TAG_CUSTOM_RENDERED, 0) != 0)
                    //{
                    //	msg = msg + getString(R.string.exif_custom_process_title) + "\r\n";
                    //}

                    // 撮影モード
                    String[] stringArray = getResources().getStringArray(R.array.exif_exposure_program_value);
                    int exposureProgram = exifInterface.getAttributeInt(ExifInterface.TAG_EXPOSURE_PROGRAM, 0);
                    msg = msg + getString(R.string.exif_camera_mode_title);
                    msg = msg + " " + ((stringArray.length > exposureProgram) ? stringArray[exposureProgram] : ("? (" + exposureProgram + ")")) + "\r\n";
                    //msg = msg + "\r\n";

                    // 測光モードの表示
                    String[] meteringStringArray = getResources().getStringArray(R.array.exif_metering_mode_value);
                    int metering = exifInterface.getAttributeInt(ExifInterface.TAG_METERING_MODE, 0);
                    msg = msg + getString(R.string.exif_metering_mode_title);
                    msg = msg + " " + ((meteringStringArray.length > metering) ? meteringStringArray[metering] : ("? (" + metering + ")")) + "\r\n";
                    //msg = msg + "\r\n";


                    // 露光時間
                    msg = msg + getString(R.string.exif_exposure_time_title);
					String expTime =  getExifAttribute(exifInterface, ExifInterface.TAG_EXPOSURE_TIME);
					float val, inv = 0.0f;
					try
					{
						val = Float.parseFloat(expTime);
						if (val < 1.0f)
						{
							inv = 1.0f / val;
						}
						if (inv < 2.0f)  // if (inv < 10.0f)
						{
							inv = 0.0f;
						}
					}
					catch (Exception e)
					{
						//
						e.printStackTrace();
					}
                    //msg = msg + " " + expTime + "s "; //(string)
					if (inv > 0.0f)
					{
						// シャッター速度を分数で表示する
                        int intValue = (int) inv;
                        int modValue = intValue % 10;
                        if ((modValue == 9)||(modValue == 4))
                        {
                            // ちょっと格好が悪いけど...切り上げ
                            intValue++;
                        }
						msg = msg + " 1/" + intValue + " s ";
					}
                    else
                    {
                        // シャッター速度を小数で表示する
                        msg = msg + " " + expTime + "s "; //(string)
                    }
					msg = msg + "\r\n";

                    // 絞り値
                    msg = msg + getString(R.string.exif_aperture_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_F_NUMBER) + "\r\n";  // (string)

                    // ISO感度
                    msg = msg + getString(R.string.exif_iso_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_ISO_SPEED_RATINGS) + "\r\n";  // (string)

                    msg = msg + "\r\n";

                    // カメラの製造元
                    msg = msg + getString(R.string.exif_maker_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_MAKE) + "\r\n";

                    // カメラのモデル名
                    msg = msg + getString(R.string.exif_camera_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_MODEL)+ "\r\n";  // (string)

					String lat = getExifAttribute(exifInterface, ExifInterface.TAG_GPS_LATITUDE);
					if ((lat != null)&&(lat.length() > 0))
					{
                        // 「位置情報あり」と表示
						msg = msg + " \r\n  " + getString(R.string.exif_with_gps) + "\r\n";
					}
                    //msg = msg + getExifAttribute(exifInterface, ExifInterface.TAG_FLASH);      // フラッシュ (int)
                    //msg = msg + getExifAttribute(exifInterface, ExifInterface.TAG_ORIENTATION);  // 画像の向き (int)
                    //msg = msg + getExifAttribute(exifInterface, ExifInterface.TAG_WHITE_BALANCE);  // ホワイトバランス (int)

                    // その他の情報...EXIFタグで取得できたものをログにダンプする
                    msg = msg + ExifInformationDumper.dumpExifInformation(exifInterface, false);
                }
                else
                {
                    msg = getString(R.string.download_control_get_information_failed);
                }
                return (msg);
            }

            private String getExifAttribute(ExifInterface attr, String tag)
            {
                String value = attr.getAttribute(tag);
                if (value == null)
                {
                    value = "";
                }
                return (value);
            }

            @Override
            public void run()
            {
                if (downloadDialog != null)
                {
                    downloadDialog.dismiss();
                }
                presentMessage(getString(R.string.download_control_get_information_title), message);
                System.gc();
            }
        }
    }

	/**
	 *   動画(とRAWファイル)のダウンロード
	 *
	 */
	private class MyMovieDownloader implements  OLYCamera.DownloadLargeContentCallback
	{
		private ProgressDialog downloadDialog = null;
		private String filename;
		private String filepath = null;
		private FileOutputStream outputStream = null;

		/**
		 *   コンストラクタ
		 *
		 * @param filename ファイル名
		 */
		MyMovieDownloader(final String filename)
		{
			this.filename = filename;
		}

		/**
		 *   ダウンロードの開始
		 *
		 */
		void startDownload()
		{
			Log.v(TAG, "startDownload() " + filename);
			downloadDialog = new ProgressDialog(getContext());
			downloadDialog.setTitle(getString(R.string.dialog_download_file_title));
			downloadDialog.setMessage(getString(R.string.dialog_download_message) + " " + filename);
			downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			downloadDialog.setCancelable(false);
			downloadDialog.show();

			// Download the image.
            try
            {
                OLYCameraContentInfoEx content = contentList.get(contentIndex);
                OLYCameraFileInfo file = content.getFileInfo();
                String targetFileName = file.getFilename();
                if (content.hasRaw())
                {
                    targetFileName = targetFileName.replace(".JPG", ".ORF");
                }
                String path = file.getDirectoryPath() + "/" + targetFileName;
                Log.v(TAG, "downloadLargeContent : " + path);
                camera.downloadLargeContent(path, this);

                final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + getString(R.string.app_name2) + "/";
                filepath = new File(directoryPath.toLowerCase(), filename).getPath();
                try {
                    final File directory = new File(directoryPath);
                    if (!directory.exists())
                    {
                        if (!directory.mkdirs())
                        {
                            Log.v(TAG, "directory.mkdirs()");
                        }
                    }
                    outputStream = new FileOutputStream(filepath);
                } catch (Exception e) {
                    final String message = e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadDialog != null) {
                                downloadDialog.dismiss();
                            }
                            presentMessage(getString(R.string.download_control_save_failed), message);
                        }
                    });
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
		}

		@Override
		public void onProgress(byte[] bytes, ProgressEvent progressEvent)
		{
			if (downloadDialog != null)
			{
				int percent = (int)(progressEvent.getProgress() * 100.0f);
				downloadDialog.setProgress(percent);
				//downloadDialog.setCancelable(progressEvent.isCancellable()); // キャンセルできるようにしないほうが良さそうなので
			}
			try
			{
				if (outputStream != null)
				{
					outputStream.write(bytes);
				}
			}
            catch (Exception e)
			{
                e.printStackTrace();
			}
		}

		@Override
		public void onCompleted()
		{
			try
			{
                final Activity activity = getActivity();
				Uri uriToShare = null;
				if (outputStream != null)
				{
					outputStream.flush();
					outputStream.close();
                    outputStream = null;
				}
                if (!filename.endsWith(RAW_SUFFIX))
                {
                    // ギャラリーに受信したファイルを登録する
                    long now = System.currentTimeMillis();
                    ContentValues values = new ContentValues();
                    values.put(Images.Media.MIME_TYPE, "video/mp4");
                    values.put(Images.Media.DATA, filepath);
                    values.put(Images.Media.DATE_ADDED, now);
                    values.put(Images.Media.DATE_TAKEN, now);
                    values.put(Images.Media.DATE_MODIFIED, now);

                    if (activity != null)
                    {
                        ContentResolver resolver = activity.getContentResolver();
                        uriToShare = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadDialog != null)
                        {
                            downloadDialog.dismiss();
                        }
                        Snackbar.make(activity.findViewById(R.id.fragment1), getString(R.string.download_control_save_success) + " " + filename, Snackbar.LENGTH_SHORT).show();
                        //Toast.makeText(activity, getString(R.string.download_control_save_success) + " " + filename, Toast.LENGTH_SHORT).show();
                        System.gc();
                    }
                });
                final Uri contentUri = uriToShare;
                if (contentUri != null)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                                if ((preferences.getBoolean("share_after_receive", false)))
                                {
                                    shareContent(contentUri);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
			catch (Exception e)
			{
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (downloadDialog != null)
						{
							downloadDialog.dismiss();
						}
						presentMessage(getString(R.string.download_control_save_failed), message);
					}
				});
			}
            System.gc();
		}

		@Override
		public void onErrorOccurred(Exception e)
		{
			final String message = e.getMessage();
            try
            {
                if (outputStream != null)
                {
                    outputStream.flush();
                    outputStream.close();
                    outputStream = null;
                }
            }
            catch (Exception ex)
            {
                e.printStackTrace();
                ex.printStackTrace();
            }
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (downloadDialog != null)
					{
						downloadDialog.dismiss();
					}
					presentMessage(getString(R.string.download_control_download_failed), message);
                    System.gc();
				}
			});
			System.gc();
		}
	}

	/**
	 *   共有の呼び出し
	 *
	 * @param movieFileUri  動画ファイル名
	 */
	private void shareContent(final Uri movieFileUri)
	{
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		try
		{
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setType("video/mp4");
			intent.putExtra(Intent.EXTRA_STREAM, movieFileUri);
			Activity activity = getActivity();
			if (activity != null)
			{
                activity.startActivityForResult(intent, 0);
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

